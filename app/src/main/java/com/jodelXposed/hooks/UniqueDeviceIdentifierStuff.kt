package com.jodelXposed.hooks

import com.jodelXposed.utils.Options

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

import com.jodelXposed.utils.Log.dlog
import de.robv.android.xposed.XposedHelpers.findAndHookMethod

class UniqueDeviceIdentifierStuff(lpparam: LoadPackageParam) {
    init {
        findAndHookMethod(Options.hooks.Class_UniqueDeviceIdentifier, lpparam.classLoader, Options.hooks.Method_UDI_GetUdiMethod, object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam) {
                val realUDI = param.result as String
                if (Options.udi.udi.length == 0) {
                    Options.udi.udi = realUDI
                    dlog("Backing up UDI")
                    Options.save()
                    return
                }

                dlog("UDI spoof = ${Options.udi.udi}")
                param.result = Options.udi.udi
            }
        })
    }
}
