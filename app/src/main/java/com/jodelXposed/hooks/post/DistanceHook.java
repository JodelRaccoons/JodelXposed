package com.jodelXposed.hooks.post;

import com.jodelXposed.hooks.helper.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Admin on 04.04.2017.
 */

public class DistanceHook {
    public DistanceHook(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            XposedHelpers.findAndHookMethod(
                "com.jodelapp.jodelandroidv3.features.feed.FeedRecyclerAdapter",
                lpparam.classLoader,
                "a",
                "com.jodelapp.jodelandroidv3.features.feed.FeedRecyclerAdapter.PostViewHolder",
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            Object postViewHolder = param.args[0];
                            Object post = XposedHelpers.callMethod(XposedHelpers.callMethod(param.thisObject, "getPosts"), "get", (int) param.args[1]);
                            Object dateTime = XposedHelpers.getObjectField(post, "createdAt");
                            String dateTimeString = dateTime.toString();
                            dateTimeString = dateTimeString.replace("-", " ");

                            Object bottomRightText = XposedHelpers.getObjectField(postViewHolder, "cornerText");
                            Object timeView = XposedHelpers.getObjectField(postViewHolder, "created");

                            XposedHelpers.callMethod(timeView, "setText", dateTimeString);

                            int distance = Math.round((float) XposedHelpers.getObjectField(post, "distance"));

                            if (!(boolean) XposedHelpers.getObjectField(post, "fromHome")) {
                                XposedHelpers.callMethod(bottomRightText, "setText", distance + " km");
                            }
                        } catch (Exception egnored) {
                        }

                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
            Log.dlog("!!!!!!!!!! Failed loading Distance hook !!!!!!!!!!");
        }
    }
}
