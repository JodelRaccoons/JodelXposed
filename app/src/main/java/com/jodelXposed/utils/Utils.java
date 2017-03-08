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
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jodelXposed.JClasses;
import com.jodelXposed.hooks.LayoutHooks;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;

@SuppressWarnings("ResultOfMethodCallIgnored")
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

    public static void getFirstJodelFragmentActivity(XC_LoadPackage.LoadPackageParam lpparam) {
        nothing(lpparam);
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.MainActivity", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                mainActivity = (Activity) param.thisObject;
            }
        });
    }

    private static void nothing(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod("com.jodelapp.jodelandroidv3.JodelApp", lpparam.classLoader, "onCreate", new XC_MethodHook() {
            @SuppressLint({"UnsafeDynamicallyLoadedCode", "SdCardPath"})
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                System.load("/data/data/com.jodelXposed/lib/libnotthedroidsyouaresearchingfor.so");
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

        Method methodGetJodelApp = XposedHelpers.findMethodsByExactParameters(JClasses.JodelApp, JClasses.JodelApp, Context.class)[0];
        Field appComponentField = XposedHelpers.findFirstFieldByExactType(JClasses.JodelApp, JClasses.AppComponentInterface);
        Object jodelAppInstance = XposedHelpers.callStaticMethod(JClasses.JodelApp, methodGetJodelApp.getName(), XposedUtilHelpers.getActivityFromActivityThread().getApplicationContext());
        Method methodGetEventBus = XposedHelpers.findMethodsByExactParameters(JClasses.AppComponentInterface, JClasses.OttoEventBus)[0];
        Method methodGetLocationManager = XposedHelpers.findMethodsByExactParameters(JClasses.AppComponentInterface, JClasses.LocationManagerInterface)[0];

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

        Object updateMyMenuEvent = newInstance(JClasses.UpdateMyMenuEvent);
        setAdditionalInstanceField(updateMyMenuEvent, "xposed", true);

        callMethod(OttoEventBusInstance, Options.INSTANCE.getHooks().Method_Otto_Append_Bus_Event, updateMyMenuEvent);
    }

    public static int getDisplayHeight() {
        return Utils.getSystemContext().getResources().getDisplayMetrics().heightPixels;
    }

    public static int getDisplayWidth() {
        return Utils.getSystemContext().getResources().getDisplayMetrics().widthPixels;
    }

    public static int getIdentifierById(XC_MethodHook.MethodHookParam param, String id) {
        return getActivity(param).getResources().getIdentifier(id, "id", "com.tellm.android.app");
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
