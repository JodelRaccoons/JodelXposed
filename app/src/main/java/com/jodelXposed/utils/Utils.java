package com.jodelXposed.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.jodelXposed.hooks.LayoutHooks;

import java.io.File;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class Utils {
    private static final String JXFolderPath = Environment.getExternalStorageDirectory() + File.separator + "JodelXposed";
    public static final String OldSettingsPath = Environment.getExternalStorageDirectory() + File.separator + ".jodel-settings-v2";
    public static Activity snackbarUtilActivity;

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
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                snackbarUtilActivity = (Activity) param.thisObject;
            }
        });
    }

    public static void makeSnackbarWithNoCtx(XC_LoadPackage.LoadPackageParam lpparam, String message) {
        makeSnackbarWithNoCtx(lpparam, message, 0);
    }

    public static void makeSnackbarWithNoCtx(XC_LoadPackage.LoadPackageParam lpparam, String message, int length) {
        if (snackbarUtilActivity != null) {
            try {
                Class TSnackbar = XposedHelpers.findClass("com.androidadvance.topsnackbar.TSnackbar", lpparam.classLoader);
                Object contentView = snackbarUtilActivity.findViewById(android.R.id.content);
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
                snackbarview.setBackgroundColor(snackbarUtilActivity.getResources().getColor(snackbarUtilActivity.getResources().getIdentifier("background_floating_material_light", "color", "com.tellm.android.app")));
                TextView snackbarTextView = (TextView) snackbarview.findViewById(snackbarUtilActivity.getResources().getIdentifier("snackbar_text", "id", "com.tellm.android.app"));
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
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, snackbarUtilActivity.getResources().getDisplayMetrics());
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
