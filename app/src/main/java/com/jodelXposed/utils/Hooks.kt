package com.jodelXposed.utils

import com.jodelXposed.hooks.*
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class Hooks(private val lpparam: LoadPackageParam) {

    fun hook() {
//        try {
//            Log.xlog("#### Loading BetaStuff hooks ####")
//            BetaStuff(lpparam)
//            Log.xlog("#### BetaStuff hooks loaded! ####")
//        } catch (e: Throwable) {
//            e.printStackTrace()
//            Log.xlog("!!!! FAILED loading BetaStuff hooks! !!!!\n\n")
//        }

        try {
            Log.xlog("#### Loading ImageStuff hooks ####")
            ImageStuff(lpparam)
            Log.xlog("#### ImageStuff hooks loaded! ####")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading ImageStuff hooks! !!!!\n" + "\n")
        }

        try {
            Log.xlog("#### Loading LocationStuff hooks ####")
            LocationStuff(lpparam)
            Log.xlog("#### LocationStuff hooks loaded! ####")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading LocationStuff hooks! !!!!\n" + "\n")
        }

        try {
            Log.xlog("#### Loading PostStuff hooks ####")
            PostStuff(lpparam)
            Log.xlog("#### PostStuff hooks loaded! ####")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading PostStuff hooks! !!!!\n" + "\n")
        }

        try {
            Log.xlog("#### Loading SettingsStuff hooks ####")
            SettingsStuff(lpparam)
            Log.xlog("#### SettingsStuff hooks loaded! ####")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading SettingsStuff hooks! !!!!\n\n")
        }

        try {
            Log.xlog("#### Loading UniqueDeviceIdentifierStuff hooks ####")
            UniqueDeviceIdentifierStuff(lpparam)
            Log.xlog("#### UniqueDeviceIdentifierStuff hooks loaded! ####")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading UniqueDeviceIdentifierStuff hooks! !!!!\n\n")
        }

        try {
            Log.xlog("#### Loading SavePost hooks ####")
            SavePost(lpparam)
            Log.xlog("#### Loading SavePost hooks loaded! ####")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading SavePost hooks! !!!!\n\n")
        }
    }
}
