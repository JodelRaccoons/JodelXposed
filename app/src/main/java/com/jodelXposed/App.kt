package com.jodelXposed

import android.annotation.SuppressLint
import android.app.AndroidAppHelper
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Handler
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

import com.jodelXposed.models.HookValues
import com.jodelXposed.retrofit.RetrofitProvider
import com.jodelXposed.utils.Hooks
import com.jodelXposed.utils.Options

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.jodelXposed.utils.Log.xlog
import com.jodelXposed.utils.Log.dlog
import com.jodelXposed.utils.Utils.getNewIntent
import com.jodelXposed.utils.Utils.getSystemContext
import java.io.File
import java.io.IOException

class App : IXposedHookLoadPackage, IXposedHookZygoteInit {


    @SuppressLint("DefaultLocale")
    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName != "com.tellm.android.app")
            return

        if (lpparam.packageName == "com.tellm.android.app") {

            val pkgInfo: PackageInfo = getSystemContext().packageManager.getPackageInfo(lpparam.packageName, 0)


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
                dlog("Loading local hooks.json")
                try {
                    val jxContext = getSystemContext().createPackageContext(
                            "com.jodelXposed", Context.CONTEXT_IGNORE_SECURITY)
                    val ins = jxContext.assets.open("${pkgInfo.versionCode}/hooks.json")
                    Options.hooks = Gson().fromJson(ins.reader().readText(), HookValues::class.java)
                    Options.save()
                } catch(ex: JsonSyntaxException) {
                    xlog("Hooks json syntax error", ex)
                } catch (ex: IOException) {
                    xlog("Could not read asset", ex)
                }
            } else if (pkgInfo.versionCode != Options.hooks.versionCode) {
                updateHooks(pkgInfo.versionCode)
            }

            xlog("#### Loading hooks ####")
            Hooks(lpparam).hook()

        }
    }

    private fun checkPermissions() {
        Handler().postDelayed({ getSystemContext().startActivity(getNewIntent("utils.Picker").addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)) }, 3000)
    }


    private fun updateHooks(versionCode: Int) {

        RetrofitProvider.getJodelXposedService().getHooks(versionCode).enqueue(object : Callback<HookValues> {
            override fun onResponse(call: Call<HookValues>, response: Response<HookValues>) {
                try {
                    Options.hooks = response.body()
                    //Success updating hooks, lets update the local version code

                    Toast.makeText(getSystemContext(), Options.hooks.updateMessage + " Please soft-reboot your device!", Toast.LENGTH_LONG).show()

                    Options.save()
                } catch (e: Exception) {
                    xlog("Your Jodel version is not supported by JodelXposed yet")
                    Toast.makeText(getSystemContext(), "Your Jodel version isnt supported by JodelXposed yet.", Toast.LENGTH_LONG).show()
                }

            }

            override fun onFailure(call: Call<HookValues>, t: Throwable) {
                xlog("Failed fethcing new hooks", t)
                Toast.makeText(getSystemContext(), "Failed updating hooks, " + t.message + " !", Toast.LENGTH_LONG).show()
            }
        })
    }

    @Throws(Throwable::class)
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        MODULE_PATH = startupParam.modulePath
    }

    companion object {
        var MODULE_PATH: String? = null
    }
}
