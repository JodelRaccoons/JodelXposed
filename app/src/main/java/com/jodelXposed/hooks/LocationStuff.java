package com.jodelXposed.hooks;

import android.location.Location;
import com.jodelXposed.utils.Options;
import com.jodelXposed.utils.RequestReplacer;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.jodelXposed.utils.Log.xlog;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

public class LocationStuff {
    public LocationStuff(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod("com.jodelapp.jodelandroidv3.location.LocationManager", lpparam.classLoader, "onLocationChanged", Location.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Location l = (Location) param.args[0];
                l.setLatitude(Options.getInstance().getLocationObject().getLat());
                l.setLongitude(Options.getInstance().getLocationObject().getLng());
            }
        });
    }
}
