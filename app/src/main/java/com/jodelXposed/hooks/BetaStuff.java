package com.jodelXposed.hooks;

import com.jodelXposed.models.Hookvalues;
import com.jodelXposed.utils.Options;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class BetaStuff {

    public BetaStuff(XC_LoadPackage.LoadPackageParam lpparam) {
        Hookvalues hooks = Options.getInstance().getHooks();

        // Unlock experiments (features that are available on some devices like post pining or searching for hashtags)
        findAndHookMethod(hooks.Class_Storage, lpparam.classLoader, hooks.BetaHook_UnlockFeatures, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(true);
            }
        });

    }
}
