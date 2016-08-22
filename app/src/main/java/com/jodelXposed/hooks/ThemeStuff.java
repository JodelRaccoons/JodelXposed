package com.jodelXposed.hooks;

import com.jodelXposed.utils.Options;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.jodelXposed.utils.Log.xlog;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class ThemeStuff {

    public ThemeStuff(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod("com.jodelapp.jodelandroidv3.utilities.Util", lpparam.classLoader, "dq", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if(Options.getInstance().getThemeObject().isActive()) {
                    String color = (String) param.getResult();
                    String newColor;
                    xlog("Color input: " + color);
                    if (color.equalsIgnoreCase("#FF9908")) newColor = "#2E271F";
                    else if (color.equalsIgnoreCase("#FFBA00")) newColor = "#3C3326";
                    else if (color.equalsIgnoreCase("#DD5F5F")) newColor = "#544836";
                    else if (color.equalsIgnoreCase("#06A3CB")) newColor = "#968873";
                    else if (color.equalsIgnoreCase("#8ABDB0")) newColor = "#A1481C";
                    else if (color.equalsIgnoreCase("#9EC41C")) newColor = "#A1481C";
                    else newColor = color;
                    param.setResult(newColor);
                }
            }
        });
    }
}
