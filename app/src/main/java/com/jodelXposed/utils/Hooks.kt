package com.jodelXposed.utils

import com.jodelXposed.hooks.*
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class Hooks(private val lpparam: LoadPackageParam) {

    fun hook() {

        try {
            Log.xlog("#### Acquiring... JodelActivity ####")
            Utils.getFirstJodelFragmentActivity(lpparam)
            Log.xlog("#### Acquired JodelActivity! ####")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED acquiring JodelActivity ! !!!!\n" + "\n")
        }

        try {
            Log.xlog("#### Loading UnlockExperiments hooks ####")
            UnlockExperiments(lpparam)
            Log.xlog("#### UnlockExperiments hooks loaded! ####")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading UnlockExperiments hooks! !!!!\n\n")
        }

        try {
            Log.xlog("#### Loading ColorAndGalleryPicker hooks ####")
            ColorAndGalleryPicker(lpparam)
            Log.xlog("#### ColorAndGalleryPicker hooks loaded! ####")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading ColorAndGalleryPicker hooks! !!!!\n" + "\n")
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

        try {
            Log.xlog("#### Loading FastScrollDown hooks ####")
            FastScrollDown(lpparam)
            Log.xlog("#### Loading FastScrollDown hooks loaded! ####")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading FastScrollDown hooks! !!!!\n\n")
        }

        try {
            Log.xlog("#### Loading JodelMenu hooks ####")
            JodelMenu(lpparam)
            Log.xlog("#### Loading JodelMenu hooks loaded! ####")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading JodelMenu hooks! !!!!\n\n")
        }

        try {
            Log.xlog("#### Loading FastLocationSwitcher hooks ####")
            FastLocationSwitcher(lpparam)
            Log.xlog("#### Loading FastLocationSwitcher hooks loaded! ####")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading FastLocationSwitcher hooks! !!!!\n\n")
        }

        try {
            Log.xlog("#### Loading RemoveBlurFromImages hooks ####")
            RemoveBlurFromImages(lpparam)
            Log.xlog("#### Loading RemoveBlurFromImages hooks loaded! ####")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading RemoveBlurFromImages hooks! !!!!\n\n")
        }

//        try {
//            Log.xlog("#### Loading JodelErrorTracing hooks ####")
//            JodelErrorTracing(lpparam)
//            Log.xlog("#### Loading JodelErrorTracing hooks loaded! ####")
//        } catch (e: Throwable) {
//            e.printStackTrace()
//            Log.xlog("!!!! FAILED loading JodelErrorTracing hooks! !!!!\n\n")
//        }
    }
}
