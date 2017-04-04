package com.jodelXposed

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Handler
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.jodelXposed.hooks.LayoutHooks
import com.jodelXposed.hooks.helper.Activity.getSys
import com.jodelXposed.hooks.helper.Log.dlog
import com.jodelXposed.hooks.helper.Log.xlog
import com.jodelXposed.hookupdate.HookUpdater
import com.jodelXposed.models.HookValues
import com.jodelXposed.utils.Hooks
import com.jodelXposed.utils.Options
import com.jodelXposed.utils.TSnackbar
import com.jodelXposed.utils.Utils.getNewIntent
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.io.IOException

class App : IXposedHookLoadPackage, IXposedHookZygoteInit, IXposedHookInitPackageResources {

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {
        if (resparam?.packageName != PACKAGE_NAME)
            return
        Companion.resparam = resparam
        val layoutHooks = LayoutHooks(resparam, MODULE_PATH)
        layoutHooks.addResources()
        layoutHooks.hook()
    }


    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName != "com.tellm.android.app")
            return

        App.Companion.lpparam = lpparam

        val pkgInfo: PackageInfo = getSys().packageManager.getPackageInfo(lpparam.packageName, 0)


        xlog("\n----------\n" +
                "Starting JodelXposed\n" +
                "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})\n" +
                "JodelTarget ${BuildConfig.JODEL_VERSION_NAME} (${BuildConfig.JODEL_VERSION_CODE})\n" +
                "JodelLocal ${pkgInfo.versionName} (${pkgInfo.versionCode})\n" +
                "----------")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions()
        }

        Options.load()


        if (BuildConfig.JODEL_VERSION_CODE == pkgInfo.versionCode) {
            dlog("Loading shipped hooks.json")
            var shippedHooks = HookValues()
            try {
                val jxContext = getSys().createPackageContext("com.jodelXposed", Context.CONTEXT_IGNORE_SECURITY)
                val ins = jxContext.assets.open("${pkgInfo.versionCode}/hooks.json")
                shippedHooks = Gson().fromJson(ins.reader().readText(), HookValues::class.java)
            } catch(ex: JsonSyntaxException) {
                xlog("Hooks json syntax error", ex)
            } catch (ex: IOException) {
                xlog("Could not read asset", ex)
            }

            if (Options.hooks.version >= shippedHooks.version) {
                dlog("Using local hooks.json")
            } else {
                dlog("Saving shipped hooks to local")
                Options.hooks = shippedHooks
                Options.save()
            }
        }

        xlog("#### Locating classes ####")
        JClasses(lpparam)

        xlog("#### Updating hooks ####")
        HookUpdater().updateHooks(Options.hooks, pkgInfo.versionCode)

        xlog("#### Loading hooks ####")
        if (Options.hooks.versionCode == pkgInfo.versionCode)
            Hooks(lpparam).hook()
        else
            TSnackbar.make(lpparam, "Please update your Jodel version!")
    }

    private fun checkPermissions() {
        Handler().postDelayed({ getSys().startActivity(getNewIntent("Picker").addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)) }, 3000)
    }

    @Throws(Throwable::class)
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        MODULE_PATH = startupParam.modulePath
    }

    companion object {
        var MODULE_PATH: String? = null
        val PACKAGE_NAME: String = "com.tellm.android.app"
        var lpparam: LoadPackageParam? = null
        var resparam: XC_InitPackageResources.InitPackageResourcesParam? = null
    }
}
