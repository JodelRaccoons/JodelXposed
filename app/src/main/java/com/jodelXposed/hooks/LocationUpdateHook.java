package com.jodelXposed.hooks;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Admin on 23.02.2017.
 */

public class LocationUpdateHook {

    public LocationUpdateHook(XC_LoadPackage.LoadPackageParam lpparam) {
//        XposedHelpers.findAndHookMethod("com.jodelapp.jodelandroidv3.features.feed.FeedFragment", lpparam.classLoader, "a", "com.jodelapp.jodelandroidv3.events.AddressUpdateEvent", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        final Object swipeRefreshLayout = XposedHelpers.getObjectField(param.thisObject,"refreshLayout");
//                        callMethod(swipeRefreshLayout, "post", new Runnable() {
//                            @Override
//                            public void run() {
//                                callMethod(swipeRefreshLayout,"setRefreshing",true);
//                                callMethod(param.thisObject,"cP");
//                            }
//                        });
//                    }
//                },500);
//            }
//        });

//        XposedHelpers.findAndHookMethod("com.jodelapp.jodelandroidv3.features.feed.FeedFragment", lpparam.classLoader, "cP", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
//                Thread.dumpStack();
//            }
//        });
    }
}
