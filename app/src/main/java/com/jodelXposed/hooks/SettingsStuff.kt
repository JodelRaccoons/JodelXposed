package com.jodelXposed.hooks

import android.content.Intent

import com.jodelXposed.utils.Options

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

import com.jodelXposed.utils.Utils.getNewIntent
import com.jodelXposed.utils.Utils.getSystemContext
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getObjectField

class SettingsStuff(lpparam: XC_LoadPackage.LoadPackageParam, classLoader: ClassLoader = lpparam.classLoader) {

    init {
        /*
         * Add JodelXposed entries in ListView
         * Seamless integration #1
         */
        findAndHookMethod(Options.hooks.Class_MyMenuPresenter, classLoader, Options.hooks.Method_Settings_AddEntriesMethod, object : XC_MethodHook() {
            @Suppress("UNCHECKED_CAST")
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam) {
                (param.result as MutableList<Any>).add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedLocation", "JX Change location"))
                (param.result as MutableList<Any>).add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedGeneralSettings", "JX Settings"))
            }
        })

        /*
         * Add JodelXposed entries in ListView - Handle clicks on Items
         * Seamless integration #2
         */
        findAndHookMethod(Options.hooks.Class_MyMenuPresenter, classLoader, Options.hooks.Method_Settings_HandleClickEventsMethod, "com.jodelapp.jodelandroidv3.view.MyMenuItem", object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                val selected = getObjectField(param!!.args[0], "name") as String

                if (selected.equals("xposedLocation", ignoreCase = true))
                    getSystemContext().startActivity(getNewIntent("utils.Picker").addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("choice", 1))
                else if (selected.equals("xposedGeneralSettings", ignoreCase = true))
                    getSystemContext().startActivity(getNewIntent("JXPreferenceActivity").addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
            }
        })
    }


}
