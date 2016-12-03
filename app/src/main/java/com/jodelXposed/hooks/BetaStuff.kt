package com.jodelXposed.hooks

import com.jodelXposed.utils.Log
import com.jodelXposed.utils.Options
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import de.robv.android.xposed.XposedHelpers.findAndHookMethod

class BetaStuff(lpparam: LoadPackageParam) {
    init {
        // Unlock experiments (features that are available on some devices like post pining or searching for hashtags)
        findAndHookMethod(Options.hooks.Class_Storage, lpparam.classLoader, Options.hooks.Method_BetaHook_UnlockFeatures, String::class.java, object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: MethodHookParam) {
                val requested: String = param.args[0] as String
                Log.vlog("Requested feature: $requested")
                Log.vlog("Default enabled: ${param.result}")
                for (feature in Options.hooks.Array_FeaturesEnabled) {
                    with(feature) {
                        if (startsWith("!") && substring(1) == requested) {
                            param.result = false
                            Log.vlog("Feature disabled")
                        } else if (param.result == false && equals(requested)) {
                            param.result = true
                            Log.vlog("Feature enabled")
                        }
                    }
                }
            }
        })
    }
}