package com.jodelXposed.utils;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.jodelXposed.JClasses;
import com.jodelXposed.hooks.helper.EventBus;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.jodelXposed.hooks.helper.Activity.getMain;
import static com.jodelXposed.hooks.helper.Activity.getSys;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Utils {
    public static final String OldSettingsPath = Environment.getExternalStorageDirectory() + File.separator + ".jodel-settings-v2";
    private static final String JXFolderPath = Environment.getExternalStorageDirectory() + File.separator + "JodelXposed";

    private static String getJXFolder() {
        File folder = new File(JXFolderPath);
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folder.getAbsolutePath();
    }

    public static String getJXSettingsFile() {
        return getJXFolder() + File.separator + "jodel-settings-v2.json";
    }

    public static String getJXSharedImage() {
        return getJXFolder() + File.separator + ".jodel-input.jpg";
    }

    public static String getSaveImagesFolder() {
        File folder = new File(getJXFolder() + File.separator + "SavedImages");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder.getAbsolutePath();
    }

    public static void nothing(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod("com.jodelapp.jodelandroidv3.JodelApp", lpparam.classLoader, "onCreate", new XC_MethodHook() {
            @SuppressLint({"UnsafeDynamicallyLoadedCode", "SdCardPath"})
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                System.load("/data/data/com.jodelXposed/lib/libnotthedroidsyouaresearchingfor.so");
            }
        });
    }


    public static void updateFeedAndLocation(double lat, double lng) {
        Object locationManagerInstance = getLocationManager();

        Address address = new Address(Locale.getDefault());
        address.setLatitude(lat);
        address.setLongitude(lng);

        Location location = new Location("Xposed");
        location.setLatitude(lat);
        location.setLongitude(lng);

        XposedHelpers.callMethod(locationManagerInstance, "i", location);

        Object updateMyMenuEvent = newInstance(JClasses.UpdateMyMenuEvent);
        setAdditionalInstanceField(updateMyMenuEvent, "xposed", true);
        setAdditionalInstanceField(updateMyMenuEvent, "lat", lat);
        setAdditionalInstanceField(updateMyMenuEvent, "lng", lng);

        EventBus.post(updateMyMenuEvent);
    }


    public static Object getLocationManager() {
        Method methodGetJodelApp = XposedHelpers.findMethodsByExactParameters(JClasses.JodelApp, JClasses.JodelApp, Context.class)[0];
        Field appComponentField = XposedHelpers.findFirstFieldByExactType(JClasses.JodelApp, JClasses.AppComponentInterface);
        Object jodelAppInstance = XposedHelpers.callStaticMethod(JClasses.JodelApp, methodGetJodelApp.getName(), getMain().getApplicationContext());
        Method methodGetLocationManager = XposedHelpers.findMethodsByExactParameters(JClasses.AppComponentInterface, JClasses.LocationManagerInterface)[0];

        Object appComponentInstance = XposedHelpers.getObjectField(jodelAppInstance, appComponentField.getName());
        return XposedHelpers.callMethod(appComponentInstance, methodGetLocationManager.getName());
    }

    public static Object getEventBus() {
        Method methodGetJodelApp = XposedHelpers.findMethodsByExactParameters(JClasses.JodelApp, JClasses.JodelApp, Context.class)[0];
        Field appComponentField = XposedHelpers.findFirstFieldByExactType(JClasses.JodelApp, JClasses.AppComponentInterface);
        Object jodelAppInstance = XposedHelpers.callStaticMethod(JClasses.JodelApp, methodGetJodelApp.getName(), getMain().getApplicationContext());
        Method methodGetEventBus = XposedHelpers.findMethodsByExactParameters(JClasses.AppComponentInterface, JClasses.OttoEventBus)[0];
        Object appComponentInstance = XposedHelpers.getObjectField(jodelAppInstance, appComponentField.getName());
        return XposedHelpers.callMethod(appComponentInstance, methodGetEventBus.getName());
    }

    public static Object getUniqueDeviceIdentifier() {
        Method methodGetJodelApp = XposedHelpers.findMethodsByExactParameters(JClasses.JodelApp, JClasses.JodelApp, Context.class)[0];
        Field appComponentField = XposedHelpers.findFirstFieldByExactType(JClasses.JodelApp, JClasses.AppComponentInterface);
        Object jodelAppInstance = XposedHelpers.callStaticMethod(JClasses.JodelApp, methodGetJodelApp.getName(), getMain().getApplicationContext());

        Method methodGetUniqueDeviceIdentifier = XposedHelpers.findMethodsByExactParameters(JClasses.AppComponentInterface, JClasses.UniqueDeviceIdentifier)[0];
        Object appComponentInstance = XposedHelpers.getObjectField(jodelAppInstance, appComponentField.getName());
        return XposedHelpers.callMethod(appComponentInstance, methodGetUniqueDeviceIdentifier.getName());
    }

    public static int getDisplayHeight() {
        return getSys().getResources().getDisplayMetrics().heightPixels;
    }

    public static int getDisplayWidth() {
        return getSys().getResources().getDisplayMetrics().widthPixels;
    }

    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getMain().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(int px) {
        DisplayMetrics displayMetrics = getSys().getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    public static Intent getNewIntent(String path) {
        Intent launchIntent = new Intent(Intent.ACTION_MAIN);
        launchIntent.setComponent(new ComponentName("com.jodelXposed", "com.jodelXposed." + path));
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return launchIntent;
    }

    /**
     * These are the only accepted Colors by the Jodel Server, credits to pydel by rolsdorph
     */
    public static class Colors {
        public static ArrayList<String> Colors = new ArrayList<String>() {{
            add("#FFFF9908"); //Orange
            add("#FFFFBA00"); //Yellow
            add("#FFDD5F5F"); //Red
            add("#FF06A3CB"); //Blue
            add("#FF8ABDB0"); //Bluegrayish
            add("#FF9EC41C"); //Green
        }};
    }
}
