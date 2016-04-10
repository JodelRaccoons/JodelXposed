package com.jodelXposed;

import com.jodelXposed.krokofant.hooks.JodelHooks;
import com.jodelXposed.krokofant.utils.Settings;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static com.jodelXposed.krokofant.utils.Log.xlog;

public class App implements IXposedHookLoadPackage {

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.tellm.android.app"))
            return;

        if (lpparam.packageName.equals("com.tellm.android.app")) {
            xlog("Loading settings");
            Settings settings = Settings.getInstance();
            settings.load();

            xlog("Loading hooks");
            new JodelHooks().hook(lpparam);
        }
    }
}
