package com.jodelXposed.utils;

import com.jodelXposed.hooks.BetaStuff;
import com.jodelXposed.hooks.ImageStuff;
import com.jodelXposed.hooks.LocationStuff;
import com.jodelXposed.hooks.PostStuff;
import com.jodelXposed.hooks.SettingsStuff;
import com.jodelXposed.hooks.ThemeStuff;
import com.jodelXposed.hooks.UniqueDeviceIdentifierStuff;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hooks {

    private XC_LoadPackage.LoadPackageParam loadPackageParam;

    public Hooks(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        this.loadPackageParam = loadPackageParam;
    }

    public void hook() {
        XC_LoadPackage.LoadPackageParam lpparam = loadPackageParam;

        try {
            Log.dlog("#### Loading BetaStuff hooks ####");
            new BetaStuff(lpparam);
            Log.dlog("#### BetaStuff hooks loaded! ####");
        } catch (Throwable e) {
            e.printStackTrace();
            Log.dlog("!!!! FAILED loading BetaStuff hooks! !!!!\n\n");
        }

        try {
            Log.dlog("#### Loading ImageStuff hooks ####");
            new ImageStuff(lpparam);
            Log.dlog("#### ImageStuff hooks loaded! ####");
        } catch (Throwable e) {
            e.printStackTrace();
            Log.dlog("!!!! FAILED loading ImageStuff hooks! !!!!\n" +
                "\n");
        }

        try {
            Log.dlog("#### Loading LocationStuff hooks ####");
            new LocationStuff(lpparam);
            Log.dlog("#### LocationStuff hooks loaded! ####");
        } catch (Throwable e) {
            e.printStackTrace();
            Log.dlog("!!!! FAILED loading LocationStuff hooks! !!!!\n" +
                "\n");
        }

        try {
            Log.dlog("#### Loading PostStuff hooks ####");
            new PostStuff(lpparam);
            Log.dlog("#### PostStuff hooks loaded! ####");
        } catch (Throwable e) {
            e.printStackTrace();
            Log.dlog("!!!! FAILED loading PostStuff hooks! !!!!\n" +
                "\n");
        }

        try {
            Log.dlog("#### Loading SettingsStuff hooks ####");
            new SettingsStuff(lpparam);
            Log.dlog("#### SettingsStuff hooks loaded! ####");
        } catch (Throwable e) {
            e.printStackTrace();
            Log.dlog("!!!! FAILED loading SettingsStuff hooks! !!!!\n\n");
        }

        try {
            Log.dlog("#### Loading UniqueDeviceIdentifierStuff hooks ####");
            new UniqueDeviceIdentifierStuff(lpparam);
            Log.dlog("#### UniqueDeviceIdentifierStuff hooks loaded! ####");
        } catch (Throwable e) {
            e.printStackTrace();
            Log.dlog("!!!! FAILED loading UniqueDeviceIdentifierStuff hooks! !!!!\n\n");
        }

        try {
            Log.dlog("#### Loading ThemeStuff hooks ####");
            new ThemeStuff(lpparam);
            Log.dlog("#### ThemeStuff hooks loaded! ####");
        } catch (Throwable e) {
            e.printStackTrace();
            Log.dlog("!!!! FAILED loading ThemeStuff hooks! !!!!\n\n");
        }
    }
}
