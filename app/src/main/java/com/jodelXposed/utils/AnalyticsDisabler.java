package com.jodelXposed.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.findClassIfExists;
import static de.robv.android.xposed.XposedHelpers.findMethodBestMatch;

/**
 * Created by Admin on 06.01.2017.
 */

public class AnalyticsDisabler {

    private final String TAG = AnalyticsDisabler.class.getSimpleName();
    private ArrayList<String> suppressTags = new ArrayList<String>() {{
        add("com.amplitude.api.AmplitudeClient");
        add("RLT/Tracker");
        add("RLT/Analytics");
        add("Fabric");
        add("GoogleSignatureVerifier");
    }};


    public AnalyticsDisabler(XC_LoadPackage.LoadPackageParam lpparam) {
        suppressLoggingCalls();

        StringBuilder analyticsFrameworks = new StringBuilder("Found ");

        if (findClassIfExists("com.crashlytics.android.core.CrashlyticsCore.Builder", lpparam.classLoader) != null) {
            analyticsFrameworks.append("Crashlytics, ");
            try {
                findAndHookMethod("com.crashlytics.android.core.CrashlyticsCore.Builder", lpparam.classLoader, "build", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        callMethod(param.thisObject, "disabled", true);
                    }
                });
            } catch (Throwable ignored) {
                Log.e(TAG, "Failed disabling Crashlytics, something went wrong :(");
            }
        }


        if (findClassIfExists("com.crashlytics.android.ndk.JniNativeApi", lpparam.classLoader) != null) {
            analyticsFrameworks.append("CrashlyticsJniNativeApi, ");
            try {
                findAndHookMethod("com.crashlytics.android.ndk.JniNativeApi", lpparam.classLoader, "initialize", String.class, AssetManager.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return false;
                    }
                });
            } catch (Throwable ignored) {
                Log.e(TAG, "Failed disabling CrashlyticsJniNativeApi, something went wrong :(");
            }
        }

        if (findClassIfExists("com.rubylight.android.statistics.impl.TrackerImpl", lpparam.classLoader) != null) {
            analyticsFrameworks.append("RubylightAnalytics, ");
            try {
                Class TrackerImpl = findClass("com.rubylight.android.statistics.impl.TrackerImpl", lpparam.classLoader);
                Class Tracker = findClass("com.rubylight.android.statistics.Tracker", lpparam.classLoader);

                for (Method m : Tracker.getDeclaredMethods()) {
                    Method mTrackImpl = findMethodBestMatch(TrackerImpl, m.getName(), m.getParameterTypes()[0]);
                    XposedBridge.hookMethod(mTrackImpl, new XCEmptyReplacement());
                }

                findAndHookMethod(TrackerImpl, "q", Map.class, new XCEmptyReplacement());
                findAndHookMethod(TrackerImpl, "r", Map.class, new XCEmptyReplacement());

            } catch (Throwable ignored) {
                Log.e(TAG, "Failed disabling RubylightAnalytics, something went wrong :(");
            }
        }

        if (findClassIfExists("com.facebook.internal.Logger", lpparam.classLoader) != null) {
            analyticsFrameworks.append("FacebookLogger, ");
            try {
                findAndHookMethod("com.facebook.internal.Logger", lpparam.classLoader, "log", "com.facebook.LoggingBehavior", int.class, String.class, String.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return null;
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Failed disabling Facebook internal logger, something went wrong :(");
            }
        }

        if (findClassIfExists("com.facebook.appevents.AppEventsLogger", lpparam.classLoader) != null) {
            analyticsFrameworks.append("FacebookAppeventsLogger, ");
            try {
                findAndHookMethod("com.facebook.appevents.AppEventsLogger", lpparam.classLoader, "logEvent", Context.class, "com.facebook.appevents.AppEventsLogger$AppEvent", "com.facebook.appevents.AppEventsLogger$AccessTokenAppIdPair", new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return null;
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Failed disabling Facebook appevents logger, something went wrong :(");
            }
        }

        Class AmplitudeClient = findClassIfExists("com.amplitude.api.AmplitudeClient", lpparam.classLoader);
        Class Amplitude = findClassIfExists("com.amplitude.api.Amplitude", lpparam.classLoader);
        if (Amplitude != null) {
            analyticsFrameworks.append("AmplitudeLogger, ");
            try {
                findAndHookMethod(AmplitudeClient, "c", Context.class, String.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.args[0] = null;
                        param.args[1] = null;
                    }
                });
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
                Log.e(TAG, "Failed disabling Amplitude logger, something went wrong :(");
            }
        }

        analyticsFrameworks.append("disabling them! Your data belongs to you!");
        Log.d(TAG, analyticsFrameworks.toString());
    }

    private void suppressLoggingCalls() {
        findAndHookMethod(Log.class, "e", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (suppressTags.contains(param.args[0].toString())) {
                    param.setResult(0);
                }
            }
        });

        findAndHookMethod(Log.class, "w", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (suppressTags.contains(param.args[0].toString())) {
                    param.setResult(0);
                }
            }
        });

        findAndHookMethod(Log.class, "i", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (suppressTags.contains(param.args[0].toString())) {
                    param.setResult(0);
                }
            }
        });
        findAndHookMethod(Log.class, "d", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (suppressTags.contains(param.args[0].toString())) {
                    param.setResult(0);
                }
            }
        });
        findAndHookMethod(Log.class, "v", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (suppressTags.contains(param.args[0].toString())) {
                    param.setResult(0);
                }
            }
        });
        findAndHookMethod(Log.class, "wtf", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (suppressTags.contains(param.args[0].toString())) {
                    param.setResult(0);
                }
            }
        });

    }

    public void suppress(String tag) {
        if (!suppressTags.contains(tag))
            suppressTags.add(tag);
    }
}
