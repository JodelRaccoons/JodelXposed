package com.jodelXposed.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class Utils {
    public static String SettingsPath = Environment.getExternalStorageDirectory() + "/.jodel-settings-v2";

    public static Context getSystemContext() {
        Object activityThread = callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread");
        return (Context) callMethod(activityThread, "getSystemContext");
    }

    public static Activity getActivity(XC_MethodHook.MethodHookParam param) {
        return (Activity) callMethod(param.thisObject, "getActivity");
    }

    public static void makeSnackbar(XC_MethodHook.MethodHookParam param, String message) {
        try {
            XposedHelpers.findMethodBestMatch(Class.forName("com.androidadvance.topsnackbar.TSnackbar"), "show")
                .invoke(
                    XposedHelpers.callStaticMethod(
                        Class.forName("com.androidadvance.topsnackbar.TSnackbar"),
                        "a",
                        new Class[]{View.class, CharSequence.class, int.class},
                        Utils
                            .getActivity(param)
                            .findViewById(
                                Utils.getIdentifierById(param, "mainCoordinatorLayout"))
                        , message, 1));
        } catch (Exception e) {
            Log.xlog("Could not create snackbar", e);
        }
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
            add("#FF795548"); //Yellow
            add("#FFDD5F5F"); //Red
            add("#FF06A3CB"); //Blue
            add("#FF8ABDB0"); //Bluegrayish
            add("#FF9EC41C"); //Green
        }};
    }
}
