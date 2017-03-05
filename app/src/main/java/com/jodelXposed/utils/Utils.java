package com.jodelXposed.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.jodelXposed.hooks.LayoutHooks;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import git.unbrick.xposedhelpers.XposedUtilHelpers;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;

public class Utils {
    public static final String OldSettingsPath = Environment.getExternalStorageDirectory() + File.separator + ".jodel-settings-v2";
    private static final String JXFolderPath = Environment.getExternalStorageDirectory() + File.separator + "JodelXposed";
    public static Activity mainActivity;

    public static Context getSystemContext() {
        Object activityThread = callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread");
        return (Context) callMethod(activityThread, "getSystemContext");
    }

    public static Activity getActivity(XC_MethodHook.MethodHookParam param) {
        return (Activity) callMethod(param.thisObject, "getActivity");
    }

    public static String getJXFolder() {
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

    public static void makeSnackbar(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param, String message) {
        try {
            Activity activity = getActivity(param);
            Class TSnackbar = XposedHelpers.findClass("com.androidadvance.topsnackbar.TSnackbar", lpparam.classLoader);
            Object contentView = activity.findViewById(android.R.id.content);
            Object subContentView = XposedHelpers.callMethod(contentView, "getChildAt", 0);
            Object snackbar = XposedHelpers.callStaticMethod(TSnackbar, "a", subContentView, message, -1);
            View snackbarview = (View) XposedHelpers.callMethod(snackbar, "getView");
            snackbarview.setBackgroundColor(activity.getResources().getColor(activity.getResources().getIdentifier("background_floating_material_light", "color", "com.tellm.android.app")));
            TextView snackbarTextView = (TextView) snackbarview.findViewById(activity.getResources().getIdentifier("snackbar_text", "id", "com.tellm.android.app"));
            snackbarTextView.setTextColor(Color.BLACK);
            snackbarTextView.setGravity(Gravity.CENTER);
            XposedHelpers.callMethod(snackbar, "ay", LayoutHooks.JodelResIDs.ic_jx_icon, 256);
            XposedHelpers.callMethod(snackbar, "show");
        } catch (Exception e) {
            Log.xlog("Could not create snackbar", e);
            Toast.makeText(getActivity(param), message, Toast.LENGTH_LONG).show();
        }
    }


    public static void getFirstJodelFragmentActivity(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.MainActivity", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @SuppressLint({"UnsafeDynamicallyLoadedCode", "SdCardPath"})
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                mainActivity = (Activity) param.thisObject;
                System.load("/data/data/com.jodelXposed/lib/libxposedjnihook.so");
            }
        });
    }

    public static void makeSnackbarWithNoCtx(XC_LoadPackage.LoadPackageParam lpparam, String message) {
        makeSnackbarWithNoCtx(lpparam, message, 0);
    }

    public static void makeSnackbarWithNoCtx(XC_LoadPackage.LoadPackageParam lpparam, String message, int length) {
        if (mainActivity != null) {
            try {
                Class TSnackbar = XposedHelpers.findClass("com.androidadvance.topsnackbar.TSnackbar", lpparam.classLoader);
                Object contentView = mainActivity.findViewById(android.R.id.content);
                Object subContentView = XposedHelpers.callMethod(contentView, "getChildAt", 0);
                Object snackbar = null;
                switch (length) {
                    case 0:
                        snackbar = XposedHelpers.callStaticMethod(TSnackbar, "a", subContentView, message, 0);
                        break;
                    case -1:
                        snackbar = XposedHelpers.callStaticMethod(TSnackbar, "a", subContentView, message, -1);
                        break;
                    case -2:
                        snackbar = XposedHelpers.callStaticMethod(TSnackbar, "a", subContentView, message, -2);
                        break;
                }
                View snackbarview = (View) XposedHelpers.callMethod(snackbar, "getView");
                snackbarview.setBackgroundColor(mainActivity.getResources().getColor(mainActivity.getResources().getIdentifier("background_floating_material_light", "color", "com.tellm.android.app")));
                TextView snackbarTextView = (TextView) snackbarview.findViewById(mainActivity.getResources().getIdentifier("snackbar_text", "id", "com.tellm.android.app"));
                snackbarTextView.setTextColor(Color.BLACK);
                snackbarTextView.setGravity(Gravity.CENTER);
                XposedHelpers.callMethod(snackbar, "ay", LayoutHooks.JodelResIDs.ic_jx_icon, 256);
                XposedHelpers.callMethod(snackbar, "show");
            } catch (Exception e) {
                Log.xlog("Could not create snackbar", e);
                Toast.makeText(Utils.getSystemContext(), message, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(Utils.getSystemContext(), "Activity is null.", Toast.LENGTH_SHORT).show();
        }
    }

    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = mainActivity.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(int px) {
        DisplayMetrics displayMetrics = getSystemContext().getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static void updateFeedAndLocation(XC_LoadPackage.LoadPackageParam lpparam, double lat, double lng) {
//        try {
//            if (mainActivity != null) {
//                Object locationManager = XposedHelpers.findFirstFieldByExactType(mainActivity.getClass(), XposedHelpers.findClass("com.jodelapp.jodelandroidv3.usecases.LocationManager", lpparam.classLoader)).get(mainActivity);
//                callMethod(locationManager, "EC");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        Class JodelApp = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.JodelApp", lpparam.classLoader);
        Class AppComponentInterface = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.api.AppComponent", lpparam.classLoader);
        Class OttoEventBus = XposedHelpers.findClass("com.squareup.otto.Bus", lpparam.classLoader);
        Class AddressUpdateEvent = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.events.AddressUpdateEvent", lpparam.classLoader);
        Class UpdateMyMenuEvent = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.events.UpdateMyMenuEvent", lpparam.classLoader);
        Class LocationManagerInterface = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.usecases.LocationManager", lpparam.classLoader);
        Method methodGetJodelApp = XposedHelpers.findMethodsByExactParameters(JodelApp, JodelApp, Context.class)[0];
        Field appComponentField = XposedHelpers.findFirstFieldByExactType(JodelApp, AppComponentInterface);
        Object jodelAppInstance = XposedHelpers.callStaticMethod(JodelApp, methodGetJodelApp.getName(), XposedUtilHelpers.getActivityFromActivityThread().getApplicationContext());
        Method methodGetEventBus = XposedHelpers.findMethodsByExactParameters(AppComponentInterface, OttoEventBus)[0];
        Method methodGetLocationManager = XposedHelpers.findMethodsByExactParameters(AppComponentInterface, LocationManagerInterface)[0];

        Object appComponentInstance = XposedHelpers.getObjectField(jodelAppInstance, appComponentField.getName());
        Object OttoEventBusInstance = XposedHelpers.callMethod(appComponentInstance, methodGetEventBus.getName());
        Object locationManagerInstance = XposedHelpers.callMethod(appComponentInstance, methodGetLocationManager.getName());


        Address address = new Address(Locale.getDefault());
        address.setLatitude(lat);
        address.setLongitude(lng);

        Location location = new Location("Xposed");
        location.setLatitude(lat);
        location.setLongitude(lng);

        XposedHelpers.callMethod(locationManagerInstance, "i", location);

        Object updateMyMenuEvent = newInstance(UpdateMyMenuEvent);
        setAdditionalInstanceField(updateMyMenuEvent, "xposed", true);

        callMethod(OttoEventBusInstance, Options.INSTANCE.getHooks().Method_Otto_Append_Bus_Event, updateMyMenuEvent);

//        Object addressUpdateEventInstance = XposedHelpers.newInstance(AddressUpdateEvent,address, location);

//        XposedHelpers.callMethod(OttoEventBusInstance,Options.INSTANCE.getHooks().Method_Otto_Append_Bus_Event, addressUpdateEventInstance);

//        Class JodelApp = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.JodelApp", lpparam.classLoader);
//
//        Location location = new Location("Jodel");
//        location.setLatitude(lat);
//        location.setLongitude(lng);
//
//        Activity activity = XposedUtilHelpers.getActivityFromActivityThread();
//        Class AppComponent = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.api.AppComponent", lpparam.classLoader);
//
//
//        Method[] methods = findMethodsByExactParameters(JodelApp, AppComponent);
//
//        Object JodelAppObject = callStaticMethod(JodelApp, "Y", activity.getApplicationContext());
//        Object AppComponentObject = callMethod(JodelAppObject, methods[0].getName());
//        final Object Bus = callMethod(AppComponentObject, "getBus");
//
//        Object LocationManager = callMethod(AppComponentObject, "getLocationManager");
//
//        callMethod(LocationManager, "j", location);
//        callMethod(LocationManager, "h", location);
//        callMethod(LocationManager, "i", location);
//
//        Class UpdateMyMenuEvent = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.events.UpdateMyMenuEvent", lpparam.classLoader);
//        final Class FeedUpdateEvent = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.events.FeedUpdateEvent", lpparam.classLoader);
//
//        Object updateEvent = newInstance(UpdateMyMenuEvent);
//        setAdditionalInstanceField(updateEvent, "locationchange", true);
//
//        callMethod(Bus, Options.INSTANCE.getHooks().Method_Otto_Append_Bus_Event, updateEvent);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                callMethod(Bus,
//                    Options.INSTANCE.getHooks().Method_Otto_Append_Bus_Event,
//                    XposedHelpers.newInstance(FeedUpdateEvent));
//            }
//        }, 500);
    }

    public static int getDisplayHeight() {
        return Utils.getSystemContext().getResources().getDisplayMetrics().heightPixels;
    }

    public static int getDisplayWidth() {
        return Utils.getSystemContext().getResources().getDisplayMetrics().widthPixels;
    }

    public static Display getDefaultDisplay() {
        return ((WindowManager) Utils.getSystemContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    public static int getIdentifierById(XC_MethodHook.MethodHookParam param, String id) {
        return getActivity(param).getResources().getIdentifier(id, "id", "com.tellm.android.app");
    }

    public static Object getEventBus(XC_LoadPackage.LoadPackageParam lpparam) {
        Class JodelAppClass = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.JodelApp", lpparam.classLoader);
        Class AppComponentClass = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.api.AppComponent", lpparam.classLoader);
        Object JodelAppInstance = null;
        Object AppComponentInstance = null;
        for (Method m : JodelAppClass.getMethods()) {
            if (m.getReturnType().equals(JodelAppClass)) {
                JodelAppInstance = XposedHelpers.callStaticMethod(JodelAppClass, m.getName(), XposedUtilHelpers.getActivityFromActivityThread().getApplicationContext());
            }
        }

        for (Method m : JodelAppClass.getMethods()) {
            if (m.getReturnType().equals(AppComponentClass) && JodelAppInstance != null) {
                AppComponentInstance = XposedHelpers.callMethod(JodelAppInstance, m.getName());
            }
        }

        if (AppComponentInstance != null) {
            return callMethod(AppComponentInstance, "getBus");
        } else {
            return null;
        }
    }

    public static Intent getNewIntent(String path) {
        Intent launchIntent = new Intent(Intent.ACTION_MAIN);
        launchIntent.setComponent(new ComponentName("com.jodelXposed", "com.jodelXposed." + path));
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return launchIntent;
    }

    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                throw new PackageManager.NameNotFoundException();
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
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
