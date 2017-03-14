package com.jodelXposed.hooks;

import android.app.Application;
import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Admin on 02.03.2017.
 */

public class DebugHooks {

    private static String TAG = DebugHooks.class.getSimpleName();

    public DebugHooks(final XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("com.jodelapp.jodelandroidv3.utilities.UniqueDeviceIdentifier",
            lpparam.classLoader,
            "a",
            Application.class,
            "com.jodelapp.jodelandroidv3.model.Storage",
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.e("UDI", String.valueOf(param.getResult()));

                }
            });


        XposedHelpers.findAndHookMethod(
            "com.jodelapp.jodelandroidv3.features.feed.FeedRecyclerAdapter",
            lpparam.classLoader,
            "a",
            "com.jodelapp.jodelandroidv3.features.feed.FeedRecyclerAdapter.PostViewHolder",
            int.class,
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object postViewHolder = param.args[0];
                    Object post = XposedHelpers.callMethod(XposedHelpers.callMethod(param.thisObject, "getPosts"), "get", (int) param.args[1]);

                    Object bottomRightText = XposedHelpers.getObjectField(postViewHolder, "bottomRightText");

                    boolean fromHome = (boolean) XposedHelpers.getObjectField(post, "fromHome");

                    float distance = (float) XposedHelpers.getObjectField(post, "distance");
                    if (!fromHome) {
                        XposedHelpers.callMethod(bottomRightText, "setText", distance + " km");
                    }

                }
            });
    }
}
