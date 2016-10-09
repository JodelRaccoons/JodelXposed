package com.jodelXposed;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.jodelXposed.retrofit.RetrofitProvider;
import com.jodelXposed.retrofit.VersionResponse;
import com.jodelXposed.utils.Hooks;
import com.jodelXposed.utils.Options;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jodelXposed.utils.Log.dlog;
import static com.jodelXposed.utils.Log.xlog;
import static com.jodelXposed.utils.Utils.getSystemContext;

public class App implements IXposedHookLoadPackage {


    @SuppressLint("DefaultLocale")
    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.tellm.android.app"))
            return;

        if (lpparam.packageName.equals("com.tellm.android.app")) {

            final PackageInfo pkgInfo = getSystemContext().getPackageManager().getPackageInfo(lpparam.packageName, 0);

            try {
                dlog(String.format("----------%n" +
                        "Starting JodelXposed%n" +
                        "Version %s (%d)%n" +
                        "JodelTarget %s (%d)%n" +
                        "JodelLocal %s (%d)%n" +
                        "----------%n",
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE,
                    BuildConfig.JODEL_VERSION_NAME,
                    BuildConfig.JODEL_VERSION_CODE,
                    pkgInfo.versionName,
                    pkgInfo.versionCode
                ));
            }catch(Exception e){
                e.printStackTrace();
                xlog("Information cannot be gathered");
            }
            try {
                Options.getInstance();
                Options.getInstance().getHooks().versionCode = pkgInfo.versionCode;
                Options.getInstance().save();
            }catch (Exception e){
                e.printStackTrace();
                xlog("Options cannot be loaded");
            }

            Hooks hooks = new Hooks(lpparam);

            dlog("#### Loading hooks ####");
            hooks.hook();

            RetrofitProvider.getJodelXposedService().latestVersion().enqueue(new Callback<VersionResponse>() {
                @Override
                public void onResponse(Call<VersionResponse> call, Response<VersionResponse> response) {
                    Log.d("ServerVersionCode: "," "+response.body().getVersioncode());
                    if (!(response.body().getVersioncode() == pkgInfo.versionCode)){

                    }
                }

                @Override
                public void onFailure(Call<VersionResponse> call, Throwable t) {

                }
            });

        }
    }

}
