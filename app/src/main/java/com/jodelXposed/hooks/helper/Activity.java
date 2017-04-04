package com.jodelXposed.hooks.helper;

import android.content.Context;
import android.os.Bundle;

import com.jodelXposed.utils.Utils;

import java.lang.reflect.Field;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by Admin on 04.04.2017.
 */

public class Activity {

    private static android.app.Activity mainActivity;
    private static android.app.Activity activityThreadActivity;
    private static Context sysContext;


    public static void setup(XC_LoadPackage.LoadPackageParam lpparam) {
        Utils.nothing(lpparam);
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.MainActivity", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                mainActivity = (android.app.Activity) param.thisObject;
            }
        });
    }

    public static android.app.Activity getMain() {
        if (mainActivity == null) {
            throw new RuntimeException("No mainActivity passed!");
        }

        return mainActivity;
    }

    public static android.app.Activity getAT() {
        if (activityThreadActivity == null) {
            activityThreadActivity = getActivityFromActivityThread();
        }
        return activityThreadActivity;
    }

    public static Context getSys() {
        if (sysContext == null) {
            sysContext = (Context) callMethod(callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread"), "getSystemContext");
        }
        return sysContext;
    }


    private static android.app.Activity getActivityFromActivityThread() {
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
                    return (android.app.Activity) activityField.get(activityRecord);
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
            android.util.Log.e("ACTIVITY", "CANNOT FIND CURRENT ACTIVITY!");
        }
        return null;
    }
}
