package com.jodelXposed.hooks.location

import android.location.Location
import com.jodelXposed.hooks.helper.Log
import com.jodelXposed.utils.Options
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage

class LocationSpoof(lpparam: XC_LoadPackage.LoadPackageParam) {
    init {
        try {
            findAndHookMethod(Options.hooks.Class_LocationChangeListener, lpparam.classLoader, "onLocationChanged", Location::class.java, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    if (Options.location.active) {
                        val l = param.args[0] as Location
                        l.latitude = Options.location.lat
                        l.longitude = Options.location.lng
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Log.dlog("!!!!!!!!!! Failed loading LocationSpoof hook !!!!!!!!!!")
        }
    }
}
