package com.jodelXposed

import android.app.AndroidAppHelper
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Handler
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.jodelXposed.hooks.LayoutHooks
import com.jodelXposed.models.HookValues
import com.jodelXposed.retrofit.JodelXposedAPI
import com.jodelXposed.retrofit.RetrofitProvider
import com.jodelXposed.utils.Hooks
import com.jodelXposed.utils.Log.dlog
import com.jodelXposed.utils.Log.xlog
import com.jodelXposed.utils.Options
import com.jodelXposed.utils.Utils
import com.jodelXposed.utils.Utils.getNewIntent
import com.jodelXposed.utils.Utils.getSystemContext
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import es.dmoral.prefs.Prefs
import git.unbrick.xposedhelpers.XposedUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class App : IXposedHookLoadPackage, IXposedHookZygoteInit, IXposedHookInitPackageResources {

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {
        if (resparam?.packageName != PACKAGE_NAME)
            return
        val layoutHooks = LayoutHooks(resparam, MODULE_PATH)
        layoutHooks.addResources()
        layoutHooks.hook()
    }


    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName != "com.tellm.android.app")
            return

        App.Companion.lpparam = lpparam

        val pkgInfo: PackageInfo = getSystemContext().packageManager.getPackageInfo(lpparam.packageName, 0)

        XposedUtils.Builder()
                .withBaseUrl("http://spectre-app.de:8080")
                .withLoadPackageParam(lpparam)
                .disableAnalytics(true)
                .build()


        xlog("----------\n" +
                "Starting JodelXposed\n" +
                "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})\n" +
                "JodelTarget ${BuildConfig.JODEL_VERSION_NAME} (${BuildConfig.JODEL_VERSION_CODE})\n" +
                "JodelLocal ${pkgInfo.versionName} (${pkgInfo.versionCode})\n" +
                "----------")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions()
        }

        try {
            Options.load()
        } catch (e: Exception) {
            xlog("Options cannot be loaded", e)
        }

        if (BuildConfig.JODEL_VERSION_CODE == pkgInfo.versionCode) {
            dlog("Loading shipped hooks.json")
            var shippedHooks = HookValues()
            try {
                val jxContext = getSystemContext().createPackageContext(
                        "com.jodelXposed", Context.CONTEXT_IGNORE_SECURITY)
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

        // Check for hook updates
        updateHooks(Options.hooks, pkgInfo.versionCode)

        xlog("#### Loading hooks ####")
        Hooks(lpparam).hook()

    }

    private fun checkPermissions() {
        Handler().postDelayed({ getSystemContext().startActivity(getNewIntent("utils.Picker").addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)) }, 3000)
    }


    private fun updateHooks(oldHooks: HookValues, installedVersionCode: Int, api: JodelXposedAPI = RetrofitProvider.JXAPI) {

        api.getHooks(installedVersionCode).enqueue(object : Callback<HookValues> {
            override fun onResponse(call: Call<HookValues>, response: Response<HookValues>) {
                try {
                    val repoHooks = response.body()
                    if (repoHooks.versionCode > oldHooks.versionCode || (repoHooks.versionCode == oldHooks.versionCode && repoHooks.version > oldHooks.version)) {
                        dlog("Replacing local hooks with repo hooks")
                        Options.hooks = repoHooks
                        Options.save()
                        Prefs.with(Utils.snackbarUtilActivity).writeBoolean("displayJXchangelog", true)
                        Toast.makeText(AndroidAppHelper.currentApplication(), "Updated hooks, please force restart Jodel", Toast.LENGTH_LONG).show()

                    } else {
                        dlog("Repo hooks are of the same or older version. Not updating.")
                    }
                } catch (e: Exception) {
                    xlog("Your Jodel version is not supported by JodelXposed yet")
                    Toast.makeText(AndroidAppHelper.currentApplication(), "Your Jodel version isnt supported by JodelXposed yet.", Toast.LENGTH_LONG).show()
                }

            }

            override fun onFailure(call: Call<HookValues>, t: Throwable) {
                xlog("Failed fetching new hooks", t)
                Toast.makeText(AndroidAppHelper.currentApplication(), "Failed updating hooks, " + t.message + " !", Toast.LENGTH_LONG).show()
            }
        })
    }

    @Throws(Throwable::class)
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        MODULE_PATH = startupParam.modulePath
    }

    companion object {
        var MODULE_PATH: String? = null
        val PACKAGE_NAME: String = "com.tellm.android.app"
        var lpparam: LoadPackageParam? = null
    }
}
