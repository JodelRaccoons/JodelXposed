package com.jodelXposed.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.util.TypedValue;

import java.lang.reflect.Field;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getSurroundingThis;

/**
 * Created by Admin on 06.01.2017.
 */

@SuppressWarnings({"WeakerAccess", "unused", "unchecked"})
public class XposedUtilHelpers {
    private final static String TAG = XposedUtilHelpers.class.getSimpleName();
    private static XC_LoadPackage.LoadPackageParam lpparam;

    public static void setup(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedUtilHelpers.lpparam = lpparam;
    }

    public static int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getSystemContext().getResources().getDisplayMetrics());
    }

    public static Context getSystemContext() {
        if (lpparam == null) {
            return getActivityFromActivityThread();
        }
        return (Context) callMethod(callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread"), "getSystemContext");
    }

    public static Context createApplicationContextByPackageName(String packageName) {
        if (lpparam == null) {
            throw new IllegalStateException("Please instantiate XposedUtils first using the XposedUtils.Builder class. \r\n " +
                "Otherwise you also can only instantiate the helpers using the static method XposedUtilHelpers.setup(Lpparam)");
        }
        if (packageName == null) {
            packageName = lpparam.packageName;
        }
        try {
            return getSystemContext().createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "Coudn't find Application you are trying to create a context for. Returning the system context.");
        }
        return getSystemContext();
    }

    public static Context createApplicationContextFromHookedApp() {
        return createApplicationContextByPackageName(null);
    }

    @Deprecated
    public static Activity getActivityByParam(XC_MethodHook.MethodHookParam param) {
        Object currentObject = param.thisObject;
        Activity ac;
        try {
            ac = (Activity) callMethod(param.thisObject, "getActivity");
            while (ac == null) {
                currentObject = getSurroundingThis(currentObject);
                ac = (Activity) callMethod(currentObject, "getActivity");
            }
        } catch (Throwable th) {
            throw new RuntimeException("Cant get a activity from here!");
        }
        return ac;
    }

    public static Activity getActivityFromActivityThread() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);

            Map<Object, Object> activities = (Map<Object, Object>) activitiesField.get(activityThread);
            if (activities == null)
                return null;

            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity) activityField.get(activityRecord);
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
            Log.e(TAG, "CANNOT FIND CURRENT ACTIVITY!");
        }
        return null;
    }
}
