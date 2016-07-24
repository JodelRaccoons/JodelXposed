package com.jodelXposed;

import android.content.pm.PackageInfo;
import com.jodelXposed.krokofant.hooks.JodelHooks;
import com.jodelXposed.krokofant.utils.Settings;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static com.jodelXposed.krokofant.utils.Log.xlog;
import static com.jodelXposed.krokofant.utils.Utils.getSystemContext;

public class App implements IXposedHookLoadPackage {

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.tellm.android.app"))
            return;

        if (lpparam.packageName.equals("com.tellm.android.app")) {
            PackageInfo pkgInfo = getSystemContext().getPackageManager().getPackageInfo(lpparam.packageName, 0);
            xlog(String.format("----------%n" +
                    "Starting JodelXposed%n" +
                    "Version %s (%d)%n" +
                    "JodelTarget %s (%d)%n" +
                    "JodelLocal %s (%d)%n" +
                    "----------%n",
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE,
                BuildConfig.JODEL_VERSION_NAME,
                BuildConfig.JODEL_VERSION_CODE,
                pkgInfo.versionName,
                pkgInfo.versionCode
            ));

            xlog("Loading settings");
            Settings settings = Settings.getInstance();
            settings.load();

            xlog("Loading hooks");
            new JodelHooks().hook(lpparam);
        }
    }
}
