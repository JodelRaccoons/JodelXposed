package com.jodelXposed.utils

import de.robv.android.xposed.XposedBridge

object Log {
    @JvmStatic
    val TAG: String = "JodelXposed"

    @JvmStatic
    fun xlog(msg: String?) {
        try {
            Class.forName("de.robv.android.xposed.XposedBridge", false, Log::class.java.classLoader)
            XposedBridge.log("$TAG: $msg")
        } catch (e: ClassNotFoundException) {
            android.util.Log.e(TAG, "Tried to output to Xposed log: $msg")
        }
    }

    @JvmStatic
    fun xlog(t: Throwable?) {
        try {
            Class.forName("de.robv.android.xposed.XposedBridge", false, Log::class.java.classLoader)
            XposedBridge.log(t)
        } catch (e: ClassNotFoundException) {
            android.util.Log.e(TAG, "Tried to output to Xposed log", t)
        }
    }

    @JvmStatic
    fun xlog(msg: String?, t: Throwable?) {
        xlog(msg)
        xlog(t)
    }

    @JvmStatic
    fun dlog(msg: String?, t: Throwable? = null) {
        if (t == null)
            android.util.Log.d(TAG, msg)
        else
            android.util.Log.d(TAG, msg, t)
    }

    @JvmStatic
    fun dlog(msg: String?) = dlog(msg, null)
}
