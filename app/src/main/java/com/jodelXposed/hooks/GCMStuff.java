package com.jodelXposed.hooks;

import android.os.Bundle;

import com.jodelXposed.utils.Options;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class GCMStuff {

    private boolean originalstate;

    public GCMStuff(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("com.jodelapp.jodelandroidv3.data.gcm.MyGcmListenerService", lpparam.classLoader, "a", String.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Bundle bundle = (Bundle) param.args[1];
                String bundle_id = bundle.getString("subject_id");
                Object userSettings = XposedHelpers.getObjectField(param.thisObject,"aqz");

                if (Options.getInstance().getBetaObject().getNotificationList().contains(bundle_id)){
                    originalstate = (boolean) XposedHelpers.callMethod(userSettings,"isNotificationsEnabled");
                    XposedHelpers.callMethod(userSettings,"setNotificationsEnabled",false);
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object userSettings = XposedHelpers.getObjectField(param.thisObject,"aqz");
                XposedHelpers.callMethod(userSettings,"setNotificationsEnabled",originalstate);
            }
        });
    }
}
