package com.jodelXposed.hooks

import com.jodelXposed.utils.Options

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

import com.jodelXposed.utils.Log.xlog
import de.robv.android.xposed.XposedHelpers.findAndHookMethod

class UniqueDeviceIdentifierStuff(lpparam: LoadPackageParam) {
    init {
        findAndHookMethod(Options.hooks.Class_UniqueDeviceIdentifier, lpparam.classLoader, Options.hooks.Method_UDI_GetUdiMethod, object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                val realUDI = param!!.result as String
                if (Options.udi.udi.length == 0) {
                    Options.udi.udi = realUDI
                    xlog("Backing up UDI")
                    Options.save()
                }
                if (Options.udi.active) {
                    if (Options.udi.udi == realUDI || Options.udi.udi.length == 0) {
                        xlog("Using real UDI")
                    } else {
                        val spoofUDI = Options.udi.udi
                        xlog("UDI spoof = " + spoofUDI)
                        param.result = spoofUDI
                    }
                }
            }
        })
    }
}
