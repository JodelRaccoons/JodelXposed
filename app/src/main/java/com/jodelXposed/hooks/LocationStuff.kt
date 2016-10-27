package com.jodelXposed.hooks

import android.location.Location
import com.jodelXposed.models.Hookvalues

import com.jodelXposed.utils.Options

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage

import de.robv.android.xposed.XposedHelpers.findAndHookMethod

class LocationStuff(lpparam: XC_LoadPackage.LoadPackageParam) {
    init {
        findAndHookMethod(Options.hooks.Class_LocationChangeListener, lpparam.classLoader, "onLocationChanged", Location::class.java, object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: XC_MethodHook.MethodHookParam) {
                if (Options.location.active) {
                    val l = param.args[0] as Location
                    l.latitude = Options.location.lat
                    l.longitude = Options.location.lng
                }
            }
        })
    }
}
