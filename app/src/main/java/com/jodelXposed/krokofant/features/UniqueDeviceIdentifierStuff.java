package com.jodelXposed.krokofant.features;

import com.jodelXposed.krokofant.utils.Settings;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.json.JSONException;

import java.io.IOException;

import static com.jodelXposed.krokofant.utils.Log.xlog;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class UniqueDeviceIdentifierStuff {
    private static class UDI {
        static String GetUID = "Ar";
    }

    /**
     * Spoof UID
     */
    public UniqueDeviceIdentifierStuff(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod("com.jodelapp.jodelandroidv3.utilities.UniqueDeviceIdentifier", lpparam.classLoader, UDI.GetUID, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                xlog("UDI = " + param.getResult());
                try {
                    Settings settings = Settings.getInstance();
                    if (!settings.isLoaded())
                        settings.load();
                    if (settings.getUid().length() == 0) {
                        settings.setUid((String) param.getResult());
                        settings.save();
                    } else if (settings.getUid().equals(param.getResult())) {
                        xlog("UDI not spoofed");
                    } else {
                        xlog("UDI spoof = " + settings.getUid());
                        param.setResult(settings.getUid());
                    }
                } catch (JSONException | IOException e) {
                    xlog("Error: " + e.getLocalizedMessage());
                }
            }
        });
    }
}
