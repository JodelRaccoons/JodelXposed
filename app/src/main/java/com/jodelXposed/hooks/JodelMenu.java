package com.jodelXposed.hooks;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.jodelXposed.JClasses;
import com.jodelXposed.models.HookValues;
import com.jodelXposed.models.Location;
import com.jodelXposed.models.UDI;
import com.jodelXposed.utils.EventBus;
import com.jodelXposed.utils.Options;
import com.jodelXposed.utils.Utils;
import com.jodelXposed.utils.XposedUtilHelpers;
import com.mypopsy.maps.StaticMap;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.jodelXposed.utils.Log.dlog;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;

/**
 * Created by Admin on 09.01.2017.
 */

@SuppressWarnings("unchecked")
public class JodelMenu {

    public static Object viewPagerReference;
    private final Object[] myMenuFragmentInstance;
    private final View[] myMenuFragmentView;

    public JodelMenu(final XC_LoadPackage.LoadPackageParam lpparam) {
        myMenuFragmentInstance = new Object[]{null};
        myMenuFragmentView = new View[]{null};


        for (Method m : JClasses.MainActivity.getDeclaredMethods()) {
            if (m.getReturnType().equals(int.class) && m.getParameterTypes().length == 0) {
                changeInitialPageNumber(m);
                break;
            }
        }

        //Higher the offscreen page limit to prevent the destroying of the feed -> buggy layout
        for (final Field f : JClasses.MainActivity.getDeclaredFields()) {
            if (f.toGenericString().contains("ViewPager")) {
                XposedHelpers.findAndHookMethod(JClasses.MainActivity, "onCreate", Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        viewPagerReference = getObjectField(param.thisObject, f.getName());
                        XposedHelpers.callMethod(viewPagerReference, "setOffscreenPageLimit", 3);
                    }
                });
            }
        }


        //Prevent instantiation of fourth tab item by returning to the layout a adaptercount of 3
        XposedHelpers.findAndHookMethod(JClasses.SectionsPagerAdapter, "getCount", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (new Exception().getStackTrace()[3].getClassName().contains("SlidingTabLayout")) {
                    int count = (int) param.getResult();
                    param.setResult(count - 1);
                }
            }
        });

        //Create a new instance of the MyMenuFragment and add it to the fragment list
        XposedBridge.hookAllConstructors(JClasses.SectionsPagerAdapter, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                for (Field f : JClasses.SectionsPagerAdapter.getFields()) {
                    if (f.toGenericString().contains("Fragment")) {
                        dlog("Found fragmentlist at: " + f.getName());
                    }
                }

                myMenuFragmentInstance[0] = XposedHelpers.newInstance(JClasses.MyMenuFragment);

                for (Field f : param.thisObject.getClass().getDeclaredFields()) {
                    if (f.getType().toString().contains("java.util.List")) {
                        //noinspection unchecked
                        ((List) getObjectField(param.thisObject, f.getName())).add(myMenuFragmentInstance[0]);
                    }
                }
            }
        });


        updateMapOnChange();

        //Check if the current hooked fragment is our newly created fragment -> if yes, set additional instance field
        for (Field f : JClasses.MyMenuFragment.getDeclaredFields()) {
            if (f.toGenericString().contains("Presenter")) {
                onViewCreatedHook(f);
            }
        }

        getViewHook(lpparam);

        myMenuEntriesHandler(lpparam);

        handleUpdateMyMenuEvent();
    }

    //Override the default page number to make the feed the initial page
    private void changeInitialPageNumber(Method m) {
        findAndHookMethod(JClasses.MainActivity, m.getName(), new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(1);
            }
        });
    }

    //Update static map
    private void updateMapOnChange() {
        findAndHookMethod(JClasses.MyMenuPresenter, "handle", "com.jodelapp.jodelandroidv3.events.UpdateMyMenuEvent", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object updateMyMenuEvent = param.args[0];
                if (getAdditionalInstanceField(updateMyMenuEvent, "locationchange") != null) {
                    Activity activity = XposedUtilHelpers.getActivityFromActivityThread();

                    ImageView imageView = (ImageView) myMenuFragmentView[0].findViewWithTag("iv_map");

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

    //New list in new fragment
    private void myMenuEntriesHandler(final XC_LoadPackage.LoadPackageParam lpparam) {
        //if(additionalinstancefield is set) -> return a different list
        XposedHelpers.findAndHookMethod(JClasses.MyMenuPresenter, Options.INSTANCE.getHooks().Method_Settings_AddEntriesMethod, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (getAdditionalInstanceField(param.thisObject, "xposed") != null) {
                    ArrayList xposedOptionsItems = new ArrayList();
                    xposedOptionsItems.add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedLocationSpoofing", "Location spoofing"));
                    xposedOptionsItems.add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedFastChange", "Override hometown"));
                    xposedOptionsItems.add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedLocation", "JX Change location"));
//                    xposedOptionsItems.add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedUdiSwitch", "UDI Spoofing"));
//                    xposedOptionsItems.add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedChangeUdi", "Change UDI"));
//                    xposedOptionsItems.add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedReportBug", "JX Report a bug"));
                    param.setResult(xposedOptionsItems);
                } else {
                    ArrayList arrayList = (ArrayList) param.getResult();
                    arrayList.add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedLink", "Xposed ♥"));
                }
            }
        });
    }

    //Change some MyMenuItems to a switch
    private void getViewHook(final XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod(JClasses.MyMenuAdapter, "getView", int.class, View.class, ViewGroup.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object myMenuItem = callMethod(param.thisObject, "getItem", (int) param.args[0]);
                String name = (String) getObjectField(myMenuItem, "name");
                if (name.equals("xposedLocationSpoofing") || name.equals("xposedFastChange") || name.equals("xposedUdiSwitch")) {
                    final Activity activity = XposedUtilHelpers.getActivityFromActivityThread();
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
                                            Utils.makeSnackbarWithNoCtx(lpparam, "Please set a UDI first!");
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

    private void handleUpdateMyMenuEvent() {
        findAndHookMethod(JClasses.MyMenuPresenter, "handle", "com.jodelapp.jodelandroidv3.events.UpdateMyMenuEvent", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (getAdditionalInstanceField(param.args[0], "xposed") != null) {
                    Activity activity = XposedUtilHelpers.getActivityFromActivityThread();

                    ImageView ivMap = (ImageView) myMenuFragmentView[0].findViewWithTag("iv_map");

                    Location location = Options.INSTANCE.getLocation();
                    StaticMap map = new StaticMap()
                        .size(Utils.getDisplayWidth() / 2, Utils.dpToPx(230) / 2)
                        .zoom(12)
                        .marker(StaticMap.Marker.Style.RED.toBuilder().label('L').build(),
                            new StaticMap.GeoPoint(location.getLat(), location.getLng()));

                    Picasso.with(activity).load(map.toURL().toString()).fit().into(ivMap);
                }
            }
        });
    }

    //Handle instantiation of new myMenuFragment
    private void onViewCreatedHook(final Field f) {
        findAndHookMethod(JClasses.MyMenuFragment, "onViewCreated", View.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if ((myMenuFragmentInstance[0] != null) && (myMenuFragmentInstance[0] == param.thisObject)) {

                    Object MyMenuPresenterInstance = getObjectField(param.thisObject, f.getName());
                    setAdditionalInstanceField(MyMenuPresenterInstance, "xposed", true);
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if ((myMenuFragmentInstance[0] != null) && (myMenuFragmentInstance[0] == param.thisObject)) {
                    Activity activity = XposedUtilHelpers.getActivityFromActivityThread();
                    myMenuFragmentView[0] = (View) param.args[0];
                    View view = myMenuFragmentView[0];
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
}
