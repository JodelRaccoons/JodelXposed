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

import com.jodelXposed.models.Location;
import com.jodelXposed.utils.Options;
import com.jodelXposed.utils.Utils;
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
import git.unbrick.xposedhelpers.XposedUtilHelpers;

import static com.jodelXposed.utils.Log.dlog;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;

/**
 * Created by Admin on 09.01.2017.
 */

@SuppressWarnings("unchecked")
public class JodelMenu {

    public static Object viewPagerReference;
    private final Class sectionsPagerAdapter;
    private final Class myMenuPresenter;
    private final Class myMenuPresenterInterface;
    private final Class mainActivity;
    private final Class myMenuFragment;
    private final Class myMenuAdapter;
    private final Class slidingTabLayout;
    private final Object[] myMenuFragmentInstance;
    private final View[] myMenuFragmentView;

    public JodelMenu(final XC_LoadPackage.LoadPackageParam lpparam) {

        sectionsPagerAdapter = findClass("com.jodelapp.jodelandroidv3.view.adapter.SectionsPagerAdapter", lpparam.classLoader);
        myMenuPresenter = findClass("com.jodelapp.jodelandroidv3.features.mymenu.MyMenuPresenter", lpparam.classLoader);
        myMenuPresenterInterface = findClass("com.jodelapp.jodelandroidv3.features.mymenu.MyMenuContract.Presenter", lpparam.classLoader);
        mainActivity = findClass("com.jodelapp.jodelandroidv3.view.MainActivity", lpparam.classLoader);
        myMenuFragment = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.features.mymenu.MyMenuFragment", lpparam.classLoader);
        myMenuAdapter = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.features.mymenu.MyMenuAdapter", lpparam.classLoader);
        slidingTabLayout = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.view.SlidingTabLayout", lpparam.classLoader);

        myMenuFragmentInstance = new Object[]{null};
        myMenuFragmentView = new View[]{null};


        for (Method m : mainActivity.getDeclaredMethods()) {
            if (m.getReturnType().equals(int.class) && m.getParameterTypes().length == 0) {
                changeInitialPageNumber(m);
                break;
            }
        }

        //Higher the offscreen page limit to prevent the destroying of the feed -> buggy layout
        for (final Field f : mainActivity.getDeclaredFields()) {
            if (f.toGenericString().contains("ViewPager")) {
                XposedHelpers.findAndHookMethod(mainActivity, "onCreate", Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        viewPagerReference = getObjectField(param.thisObject, f.getName());
                        XposedHelpers.callMethod(viewPagerReference, "setOffscreenPageLimit", 3);
                    }
                });
            }
        }


        //Prevent instantiation of fourth tab item by returning to the layout a adaptercount of 3
        XposedHelpers.findAndHookMethod(sectionsPagerAdapter, "getCount", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (new Exception().getStackTrace()[3].getClassName().contains("SlidingTabLayout")) {
                    int count = (int) param.getResult();
                    param.setResult(count - 1);
                }
            }
        });

        //Create a new instance of the MyMenuFragment and add it to the fragment list
        XposedBridge.hookAllConstructors(sectionsPagerAdapter, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                for (Field f : sectionsPagerAdapter.getFields()) {
                    if (f.toGenericString().contains("Fragment")) {
                        dlog("Found fragmentlist at: " + f.getName());
                    }
                }

                myMenuFragmentInstance[0] = XposedHelpers.newInstance(myMenuFragment);

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
        for (Field f : myMenuFragment.getDeclaredFields()) {
            if (f.toGenericString().contains("Presenter")) {
                onViewCreatedHook(f);
            }
        }

        getViewHook(lpparam);

        myMenuEntriesHandler(lpparam);
    }

    //Override the default page number to make the feed the initial page
    private void changeInitialPageNumber(Method m) {
        findAndHookMethod(mainActivity, m.getName(), new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(1);
            }
        });
    }

    //Update static map
    private void updateMapOnChange() {
        findAndHookMethod(myMenuPresenter, "handle", "com.jodelapp.jodelandroidv3.events.UpdateMyMenuEvent", new XC_MethodHook() {
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
        XposedHelpers.findAndHookMethod(myMenuPresenter, Options.INSTANCE.getHooks().Method_Settings_AddEntriesMethod, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (getAdditionalInstanceField(param.thisObject, "xposed") != null) {
                    ArrayList xposedOptionsItems = new ArrayList();
                    xposedOptionsItems.add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedLocationSpoofing", "Location spoofing"));
                    xposedOptionsItems.add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedFastChange", "Override hometown"));
                    xposedOptionsItems.add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedLocation", "JX Change location"));
                    xposedOptionsItems.add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedReportBug", "JX Report a bug"));
                    param.setResult(xposedOptionsItems);
                } else {
                    ArrayList arrayList = (ArrayList) param.getResult();
                    arrayList.add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedLink", "Xposed ♥"));
                }
            }
        });
    }

    //Change some MyMenuItems to a switch
    private void getViewHook(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod(myMenuAdapter, "getView", int.class, View.class, ViewGroup.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object myMenuItem = callMethod(param.thisObject, "getItem", (int) param.args[0]);
                String name = (String) getObjectField(myMenuItem, "name");
                if (name.equals("xposedLocationSpoofing") || name.equals("xposedFastChange")) {
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
                    if (name.equals("xposedLocationSpoofing")) {
                        sw.setChecked(Options.INSTANCE.getLocation().getActive());
                        sw.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Options.INSTANCE.getLocation().setActive(sw.isChecked());
                                Options.INSTANCE.save();
                            }
                        });

                    } else if (name.equals("xposedFastChange")) {
                        sw.setChecked(Options.INSTANCE.getLocation().getOverrideHometown());
                        sw.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Options.INSTANCE.getLocation().setOverrideHometown(sw.isChecked());
                                Options.INSTANCE.save();
                            }
                        });
                    }


                    view.addView(sw, view.getChildCount());
                }
            }
        });

        //Print any network error
        findAndHookMethod("com.jodelapp.jodelandroidv3.utilities.errorhandling.rx.ErrorResolutionSubscriber", lpparam.classLoader, "onError", Throwable.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                ((Throwable) param.args[0]).printStackTrace();
            }
        });
    }

    //Handle instantiation of new myMenuFragment
    private void onViewCreatedHook(final Field f) {
        findAndHookMethod(myMenuFragment, "onViewCreated", View.class, Bundle.class, new XC_MethodHook() {
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
