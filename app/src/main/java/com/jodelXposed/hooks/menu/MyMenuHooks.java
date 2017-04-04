package com.jodelXposed.hooks.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jodelXposed.JClasses;
import com.jodelXposed.hooks.helper.EventBus;
import com.jodelXposed.hooks.helper.Log;
import com.jodelXposed.models.HookValues;
import com.jodelXposed.models.Location;
import com.jodelXposed.models.UDI;
import com.jodelXposed.utils.Options;
import com.jodelXposed.utils.TSnackbar;
import com.jodelXposed.utils.Utils;
import com.mypopsy.maps.StaticMap;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.jodelXposed.hooks.helper.Activity.getMain;
import static com.jodelXposed.hooks.helper.Activity.getSys;
import static com.jodelXposed.utils.Utils.getNewIntent;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;

/**
 * Created by Admin on 03.04.2017.
 */

public class MyMenuHooks {

    private View myMenuFragmentView;
    private Object viewPagerReference;
    private Object myMenuFragmentInstance;


    public MyMenuHooks(XC_LoadPackage.LoadPackageParam lpparam) {

        try {
            changeInitialPageNumber();

            hookAdapterCount();

            addXposedFragment();

            hookViewPager(lpparam);

            updateMapOnEvent();

            onViewCreatedHook(lpparam);

            addSwitchToEntry(lpparam);

            myMenuEntriesHandler(lpparam);

            handleUpdateMyMenuEvent();

            handleMyMenuClicks(lpparam);
        } catch (Exception e) {
            e.printStackTrace();
            Log.dlog("!!!!!!!!!! Failed loading MyMenu hook !!!!!!!!!!");
        }
    }


    private void handleMyMenuClicks(final XC_LoadPackage.LoadPackageParam lpparam) {

        findAndHookMethod(Options.INSTANCE.getHooks().Class_MyMenuPresenter, lpparam.classLoader, Options.INSTANCE.getHooks().Method_Settings_HandleClickEventsMethod, "com.jodelapp.jodelandroidv3.view.MyMenuItem", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String selected = (String) getObjectField(param.args[0], "name");

                if (selected.equalsIgnoreCase("xposedLocation")) {
                    Toast.makeText(getSys(), "Starting location picker...", Toast.LENGTH_LONG).show();
                    getSys().startActivity(getNewIntent("utils.Picker").addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("choice", 1));
                } else if (selected.equalsIgnoreCase("xposedLink")) {
                    //setCurrentItem(int,boolean)
                    callMethod(viewPagerReference, "e", 4, true);
                } else if (selected.equals("xposedDateTimeChoice")) {
                    Dialog.showDateTimeDialog(lpparam);
                } else if (selected.equals("xposedHashtags")) {
                    Dialog.showHashtagDialog(lpparam);
                }
            }
        });
    }

    /*
    * if(additionalinstancefield is set) -> return a different list
    * */
    private void myMenuEntriesHandler(final XC_LoadPackage.LoadPackageParam lpparam) {
        String methodName = XposedHelpers.findMethodsByExactParameters(JClasses.MyMenuPresenter, List.class)[0].getName();

        XposedHelpers.findAndHookMethod(JClasses.MyMenuPresenter, methodName, new XC_MethodHook() {
            @SuppressWarnings("unchecked")
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (getAdditionalInstanceField(param.thisObject, "xposed") != null) {
                    ArrayList xposedOptionsItems = new ArrayList();
                    xposedOptionsItems.add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedLocationSpoofing", "Location spoofing"));
                    xposedOptionsItems.add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedFastChange", "Override hometown"));
                    xposedOptionsItems.add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedLocation", "JX Change location"));
                    xposedOptionsItems.add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedDateTimeChoice", "Time formatting"));
                    xposedOptionsItems.add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedHashtags", "Search for hashtags"));
                    param.setResult(xposedOptionsItems);
                } else {
                    ArrayList arrayList = (ArrayList) param.getResult();
                    arrayList.add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedLink", "Xposed ♥"));
                }
            }
        });
    }


    private void handleUpdateMyMenuEvent() {
        findAndHookMethod(JClasses.MyMenuFragment, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if ((myMenuFragmentInstance != null) && (myMenuFragmentInstance == param.thisObject)) {
                    Activity activity = getMain();

                    ImageView ivMap = (ImageView) myMenuFragmentView.findViewWithTag("iv_map");

                    Location location = Options.INSTANCE.getLocation();

                    double lat = location.getLat();
                    double lng = location.getLng();
                    StaticMap map = new StaticMap()
                        .size(Utils.getDisplayWidth() / 2, Utils.dpToPx(230) / 2)
                        .zoom(12)
                        .marker(StaticMap.Marker.Style.RED.toBuilder().label('L').build(),
                            new StaticMap.GeoPoint(lat, lng));

                    Picasso.with(activity).load(map.toURL().toString()).fit().into(ivMap);
                }
            }
        });
    }

    //Handle instantiation of new myMenuFragment
    private void onViewCreatedHook(final XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod(JClasses.MyMenuFragment, "onViewCreated", View.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if ((myMenuFragmentInstance != null) && (myMenuFragmentInstance == param.thisObject)) {
                    Class MyMenuContractPresenter = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.features.mymenu.MyMenuContract.Presenter", lpparam.classLoader);
                    String presenterField = XposedHelpers.findFirstFieldByExactType(JClasses.MyMenuFragment, MyMenuContractPresenter).getName();
                    Object MyMenuPresenterInstance = getObjectField(param.thisObject, presenterField);
                    setAdditionalInstanceField(MyMenuPresenterInstance, "xposed", true);
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if ((myMenuFragmentInstance != null) && (myMenuFragmentInstance == param.thisObject)) {
                    Activity activity = getMain();
                    myMenuFragmentView = (View) param.args[0];
                    View view = myMenuFragmentView;
                    TextView textView = (TextView) view.findViewById(android.R.id.text1);
                    textView.setText("Xposed");

                    ((LinearLayout) textView.getParent()).setOrientation(LinearLayout.VERTICAL);

                    ImageView imageView = new ImageView(activity);
                    imageView.setTag("iv_map");
                    LinearLayout.LayoutParams ivlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dpToPx(230));
                    imageView.setLayoutParams(ivlp);

                    ((ViewGroup) textView.getParent()).addView(imageView, ((ViewGroup) textView.getParent()).getChildCount());

                    Location location = Options.INSTANCE.getLocation();

                    StaticMap map = new StaticMap()
                        .size(Utils.getDisplayWidth() / 2, Utils.dpToPx(230) / 2)
                        .zoom(12)
                        .marker(StaticMap.Marker.Style.RED.toBuilder().label('L').build(),
                            new StaticMap.GeoPoint(location.getLat(), location.getLng()));

                    Picasso.with(activity).load(map.toURL().toString()).fit().into(imageView);
                }
            }
        });
    }


    private void addSwitchToEntry(final XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod(JClasses.MyMenuAdapter, "getView", int.class, View.class, ViewGroup.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object myMenuItem = callMethod(param.thisObject, "getItem", (int) param.args[0]);
                String name = (String) getObjectField(myMenuItem, "name");
                if (name.equals("xposedLocationSpoofing") || name.equals("xposedFastChange") || name.equals("xposedUdiSwitch")) {
                    final Activity activity = getMain();
                    LinearLayout view = (LinearLayout) param.getResult();

                    for (int i = 0; i <= view.getChildCount(); i++) {
                        if (view.getChildAt(i).getClass().equals(ImageButton.class)) {
                            view.removeView(view.getChildAt(i));
                        }
                    }

                    final Switch sw = new Switch(activity);
                    final LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    llp.setMargins(0, 0, Utils.dpToPx(11), 0);
                    sw.setLayoutParams(llp);
                    switch (name) {
                        case "xposedLocationSpoofing":
                            sw.setChecked(Options.INSTANCE.getLocation().getActive());
                            sw.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Options.INSTANCE.getLocation().setActive(sw.isChecked());
                                    Options.INSTANCE.save();
                                }
                            });

                            break;
                        case "xposedFastChange":
                            sw.setChecked(Options.INSTANCE.getLocation().getOverrideHometown());
                            sw.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Options.INSTANCE.getLocation().setOverrideHometown(sw.isChecked());
                                    Options.INSTANCE.save();
                                }
                            });
                            break;
                        case "xposedUdiSwitch":
                            sw.setChecked(Options.INSTANCE.getUdi().getActive());
                            sw.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Options options = Options.INSTANCE;
                                    UDI udi = options.getUdi();
                                    HookValues hooks = options.getHooks();
                                    if (udi.getOriginalUdi().isEmpty()
                                        || !udi.getOriginalUdi().equals(callMethod(Utils.getUniqueDeviceIdentifier(), hooks.Method_UDI_GetUdiMethod))) {
                                        udi.setOriginalUdi((String) callMethod(Utils.getUniqueDeviceIdentifier(), hooks.Method_UDI_GetUdiMethod));
                                    }
                                    if (sw.isChecked()) {
                                        if (udi.getUdi().isEmpty()) {
                                            TSnackbar.make(lpparam, "Please set a UDI first!");
                                            sw.setChecked(false);
                                        } else
                                            EventBus.post(newInstance(EventBus.Events.UserSyncRequestEvent, udi.getUdi()));
                                    } else {
                                        EventBus.post(newInstance(EventBus.Events.UserSyncRequestEvent, udi.getOriginalUdi()));
                                    }
                                    udi.setActive(sw.isChecked());
                                    Options.INSTANCE.save();
                                }
                            });
                            break;
                    }


                    view.addView(sw, view.getChildCount());
                }
            }
        });
    }

    /*
    * Prevent instantiation of fourth tab item by returning to the layout a adaptercount of 3
    * */
    private void hookAdapterCount() {
        XposedHelpers.findAndHookMethod(JClasses.SectionsPagerAdapter, "getCount", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (new Exception().getStackTrace()[3].getClassName().contains("SlidingTabLayout")) {
                    int count = (int) param.getResult();
                    param.setResult(count - 1);
                }
            }
        });
    }


    /*
    * Update static map
    * */
    private void updateMapOnEvent() {
        findAndHookMethod(JClasses.MyMenuPresenter, "handle", "com.jodelapp.jodelandroidv3.events.UpdateMyMenuEvent", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object updateMyMenuEvent = param.args[0];
                if (getAdditionalInstanceField(updateMyMenuEvent, "xposed") != null) {
                    Activity activity = getMain();

                    ImageView imageView = (ImageView) myMenuFragmentView.findViewWithTag("iv_map");

                    Location location = Options.INSTANCE.getLocation();

                    StaticMap map = new StaticMap()
                        .size(Utils.getDisplayWidth() / 2, Utils.dpToPx(230) / 2)
                        .zoom(12)
                        .marker(StaticMap.Marker.Style.RED.toBuilder().label('L').build(),
                            new StaticMap.GeoPoint(location.getLat(), location.getLng()));

                    Picasso.with(activity).load(map.toURL().toString()).fit().into(imageView);

                }
            }
        });
    }

    /*
    * Create a new instance of the MyMenuFragment and add it to the fragment list
    * */
    private void addXposedFragment() {
        XposedBridge.hookAllConstructors(JClasses.SectionsPagerAdapter, new XC_MethodHook() {
            @SuppressWarnings("unchecked")
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (myMenuFragmentInstance == null) {
                    String fragmentList = XposedHelpers.findFirstFieldByExactType(JClasses.SectionsPagerAdapter, List.class).getName();
                    myMenuFragmentInstance = XposedHelpers.newInstance(JClasses.MyMenuFragment);
                    setAdditionalInstanceField(myMenuFragmentInstance, "xposed", true);
                    ((List) getObjectField(param.thisObject, fragmentList)).add(myMenuFragmentInstance);
                }
            }
        });
    }


    /*
    * Higher the offscreen page limit to prevent the destroying of the feed -> buggy layout
    * */
    private void hookViewPager(final XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(JClasses.MainActivity, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (viewPagerReference == null) {
                    Class ViewPager = XposedHelpers.findClass("android.support.v4.view.ViewPager", lpparam.classLoader);
                    String viewPagerFieldName = XposedHelpers.findFirstFieldByExactType(JClasses.MainActivity, ViewPager).getName();
                    viewPagerReference = getObjectField(param.thisObject, viewPagerFieldName);
                    XposedHelpers.callMethod(viewPagerReference, "setOffscreenPageLimit", 3);
                }
            }
        });
    }


    private void changeInitialPageNumber() {
        String method = XposedHelpers.findMethodsByExactParameters(JClasses.MainActivity, int.class)[0].getName();
        findAndHookMethod(JClasses.MainActivity, method, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(1);
            }
        });
    }
}
