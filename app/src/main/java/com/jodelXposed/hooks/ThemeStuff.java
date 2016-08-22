package com.jodelXposed.hooks;

import com.jodelXposed.models.Theme;
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
                    Theme t = Options.getInstance().getThemeObject();
                    xlog("Color input: " + color);
                    if (color.equalsIgnoreCase("#FF9908")) newColor = t.orange;
                    else if (color.equalsIgnoreCase("#FFBA00")) newColor = t.yellow;
                    else if (color.equalsIgnoreCase("#DD5F5F")) newColor = t.red;
                    else if (color.equalsIgnoreCase("#06A3CB")) newColor = t.blue;
                    else if (color.equalsIgnoreCase("#8ABDB0")) newColor = t.bluegrayish;
                    else if (color.equalsIgnoreCase("#9EC41C")) newColor = t.green;
                    else newColor = color;
                    param.setResult(newColor);
                }
            }
        });
    }
}
