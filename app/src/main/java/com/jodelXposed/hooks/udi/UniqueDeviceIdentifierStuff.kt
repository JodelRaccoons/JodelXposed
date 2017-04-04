package com.jodelXposed.hooks.udi

import com.jodelXposed.hooks.helper.Log.dlog
import com.jodelXposed.utils.Options
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class UniqueDeviceIdentifierStuff(lpparam: LoadPackageParam) {
    init {
        try {
            findAndHookMethod(Options.hooks.Class_UniqueDeviceIdentifier, lpparam.classLoader, Options.hooks.Method_UDI_GetUdiMethod, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    if (Options.udi.active) {
                        val realUDI = param.result as String
                        if (Options.udi.udi.isEmpty()) {
                            Options.udi.udi = realUDI
                            dlog("Backing up UDI")
                            Options.save()
                            return
                        }

                        dlog("UDI spoof = ${Options.udi.udi}")
                        param.result = Options.udi.udi
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            dlog("!!!!!!!!!! Failed loading UniqueDeviceIdentifier hook !!!!!!!!!!")
        }
    }
}
