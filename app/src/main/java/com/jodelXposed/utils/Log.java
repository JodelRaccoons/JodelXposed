package com.jodelXposed.utils;

import de.robv.android.xposed.XposedBridge;

public class Log {
    public static void xlog(String log) {
        try {
            Class.forName("de.robv.android.xposed.XposedBridge", false, Log.class.getClassLoader());
            XposedBridge.log("JodelXposed: " + log);
        } catch (ClassNotFoundException e) {
            android.util.Log.i("JodelXposed", log);
        }
    }
}
