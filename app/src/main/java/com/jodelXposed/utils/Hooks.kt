package com.jodelXposed.utils

import com.jodelXposed.hooks.AnalyticsDisabler
import com.jodelXposed.hooks.debug.DebugHooks
import com.jodelXposed.hooks.experiments.UnlockExperiments
import com.jodelXposed.hooks.helper.Activity
import com.jodelXposed.hooks.helper.Log
import com.jodelXposed.hooks.imageblur.RemoveBlurFromImages
import com.jodelXposed.hooks.location.FastLocationSwitcher
import com.jodelXposed.hooks.location.LocationSpoof
import com.jodelXposed.hooks.menu.MyMenuHooks
import com.jodelXposed.hooks.picker.ColorPicker
import com.jodelXposed.hooks.picker.GalleryPicker
import com.jodelXposed.hooks.post.*
import com.jodelXposed.hooks.udi.UniqueDeviceIdentifierStuff
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class Hooks(private val lpparam: LoadPackageParam) {

    fun hook() {

        Log.xlog("#### Setting up XposedUtilHelers ####")
        Activity.setup(lpparam)

        Log.xlog("#### Disabling Analytics ####")
        AnalyticsDisabler(lpparam)

        Log.xlog("#### Loading ColorAndGalleryPicker hooks ####")
        ColorPicker(lpparam)
        GalleryPicker(lpparam)

        Log.xlog("#### Loading UnlockExperiments hooks ####")
        UnlockExperiments(lpparam)

        Log.xlog("#### Loading LocationSpoof hooks ####")
        LocationSpoof(lpparam)
        FastLocationSwitcher(lpparam)

        Log.xlog("#### Loading Post hooks ####")
        StickyPosts(lpparam)
        EnablePasting(lpparam)
        TrackPosts(lpparam)
        SavePost(lpparam)
        TimeViewHook(lpparam)
        DistanceHook(lpparam)
        RemoveBlurFromImages(lpparam)
        FastScrollDown(lpparam)

        Log.xlog("#### Loading UniqueDeviceIdentifierStuff hooks ####")
        UniqueDeviceIdentifierStuff(lpparam)

        Log.xlog("#### Loading MyMenuHooks hooks ####")
        MyMenuHooks(lpparam)

        Log.xlog("#### Loading DebugHooks hooks ####")
        DebugHooks(lpparam)
    }
}
