package com.jodelXposed.utils

import com.jodelXposed.hooks.*
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class Hooks(private val lpparam: LoadPackageParam) {

    fun hook() {

        try {
            Log.xlog("#### Acquiring... JodelActivity ####")
            Utils.getFirstJodelFragmentActivity(lpparam)
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED acquiring JodelActivity ! !!!!\n" + "\n")
        }

        try {
            Log.xlog("#### Loading UnlockExperiments hooks ####")
            UnlockExperiments(lpparam)
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading UnlockExperiments hooks! !!!!\n\n")
        }

        try {
            Log.xlog("#### Loading ColorAndGalleryPicker hooks ####")
            ColorAndGalleryPicker(lpparam)
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading ColorAndGalleryPicker hooks! !!!!\n" + "\n")
        }

        try {
            Log.xlog("#### Loading LocationStuff hooks ####")
            LocationStuff(lpparam)
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading LocationStuff hooks! !!!!\n" + "\n")
        }

        try {
            Log.xlog("#### Loading PostStuff hooks ####")
            PostStuff(lpparam)
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading PostStuff hooks! !!!!\n" + "\n")
        }

        try {
            Log.xlog("#### Loading SettingsStuff hooks ####")
            SettingsStuff(lpparam)
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading SettingsStuff hooks! !!!!\n\n")
        }

        try {
            Log.xlog("#### Loading UniqueDeviceIdentifierStuff hooks ####")
            UniqueDeviceIdentifierStuff(lpparam)
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading UniqueDeviceIdentifierStuff hooks! !!!!\n\n")
        }

        try {
            Log.xlog("#### Loading SavePost hooks ####")
            SavePost(lpparam)
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading SavePost hooks! !!!!\n\n")
        }

        try {
            Log.xlog("#### Loading FastScrollDown hooks ####")
            FastScrollDown(lpparam)
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading FastScrollDown hooks! !!!!\n\n")
        }

        try {
            Log.xlog("#### Loading JodelMenu hooks ####")
            JodelMenu(lpparam)
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading JodelMenu hooks! !!!!\n\n")
        }

        try {
            Log.xlog("#### Loading FastLocationSwitcher hooks ####")
            FastLocationSwitcher(lpparam)
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading FastLocationSwitcher hooks! !!!!\n\n")
        }

        try {
            Log.xlog("#### Loading RemoveBlurFromImages hooks ####")
            RemoveBlurFromImages(lpparam)
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading RemoveBlurFromImages hooks! !!!!\n\n")
        }

        try {
            Log.xlog("#### Loading DebugHooks hooks ####")
            DebugHooks(lpparam)
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.xlog("!!!! FAILED loading DebugHooks hooks! !!!!\n\n")
        }
    }
}
