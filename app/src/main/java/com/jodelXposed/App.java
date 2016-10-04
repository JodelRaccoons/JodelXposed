package com.jodelXposed;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;

import com.jodelXposed.utils.Hooks;
import com.jodelXposed.utils.Options;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static com.jodelXposed.utils.Log.dlog;
import static com.jodelXposed.utils.Log.xlog;
import static com.jodelXposed.utils.Utils.getSystemContext;

public class App implements IXposedHookLoadPackage {


    @SuppressLint("DefaultLocale")
    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.tellm.android.app"))
            return;

        if (lpparam.packageName.equals("com.tellm.android.app")) {

            PackageInfo pkgInfo = getSystemContext().getPackageManager().getPackageInfo(lpparam.packageName, 0);

            try {
                dlog(String.format("----------%n" +
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
            }catch(Exception e){
                e.printStackTrace();
                xlog("Information cannot be gathered");
            }
            try {
                Options.getInstance();
                Options.getInstance().getHooks().versionCode = pkgInfo.versionCode;
                Options.getInstance().save();
            }catch (Exception e){
                e.printStackTrace();
                xlog("Options cannot be loaded");
            }

            Hooks hooks = new Hooks(lpparam);

            dlog("#### Loading hooks ####");
            hooks.hook();

        }
    }

}
