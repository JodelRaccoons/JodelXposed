package com.jodelXposed.hooks.imageblur;

import android.graphics.Bitmap;

import com.jodelXposed.hooks.helper.Log;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findMethodsByExactParameters;

/**
 * Created by Admin on 26.01.2017.
 */

public class RemoveBlurFromImages {
    public RemoveBlurFromImages(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Class<?> JodelImageHelper = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.utilities.JodelImageHelper", lpparam.classLoader);
            Method[] methods = findMethodsByExactParameters(JodelImageHelper, Bitmap.class, Bitmap.class);
            findAndHookMethod("com.jodelapp.jodelandroidv3.utilities.JodelImageHelper", lpparam.classLoader, methods[0].getName(), Bitmap.class, new XC_MethodReplacement() {
                @Override
                protected Bitmap replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return (Bitmap) param.args[0];
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.dlog("!!!!!!!!!! Failed loading RemoveBlurFromImages hook !!!!!!!!!!");
        }
    }
}
