package com.jodelXposed;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by Admin on 08.03.2017.
 */

public class JClasses {
    public static Class SectionsPagerAdapter;
    public static Class MyMenuPresenter;
    public static Class MyMenuPresenterInterface;
    public static Class MainActivity;
    public static Class MyMenuFragment;
    public static Class MyMenuAdapter;
    public static Class SlidingTabLayout;
    public static Class JodelApp;
    public static Class AppComponentInterface;
    public static Class OttoEventBus;
    public static Class AddressUpdateEvent;
    public static Class UpdateMyMenuEvent;
    public static Class LocationManagerInterface;

    public JClasses(XC_LoadPackage.LoadPackageParam lpparam) {
        SectionsPagerAdapter = findClass("com.jodelapp.jodelandroidv3.view.adapter.SectionsPagerAdapter", lpparam.classLoader);
        MyMenuPresenter = findClass("com.jodelapp.jodelandroidv3.features.mymenu.MyMenuPresenter", lpparam.classLoader);
        MyMenuPresenterInterface = findClass("com.jodelapp.jodelandroidv3.features.mymenu.MyMenuContract.Presenter", lpparam.classLoader);
        MainActivity = findClass("com.jodelapp.jodelandroidv3.view.MainActivity", lpparam.classLoader);
        MyMenuFragment = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.features.mymenu.MyMenuFragment", lpparam.classLoader);
        MyMenuAdapter = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.features.mymenu.MyMenuAdapter", lpparam.classLoader);
        SlidingTabLayout = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.view.SlidingTabLayout", lpparam.classLoader);
        JodelApp = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.JodelApp", lpparam.classLoader);
        AppComponentInterface = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.api.AppComponent", lpparam.classLoader);
        OttoEventBus = XposedHelpers.findClass("com.squareup.otto.Bus", lpparam.classLoader);
        AddressUpdateEvent = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.events.AddressUpdateEvent", lpparam.classLoader);
        UpdateMyMenuEvent = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.events.UpdateMyMenuEvent", lpparam.classLoader);
        LocationManagerInterface = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.usecases.LocationManager", lpparam.classLoader);
    }
}
