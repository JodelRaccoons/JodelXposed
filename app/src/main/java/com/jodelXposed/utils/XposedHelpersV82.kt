package com.jodelXposed.utils

import de.robv.android.xposed.XposedHelpers.*
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method


/*
 * See the following for reference
 * https://github.com/rovo89/XposedBridge/blob/art/app/src/main/java/de/robv/android/xposed/XposedHelpers.java
 * http://api.xposed.info/reference/de/robv/android/xposed/XposedHelpers.html
 */


fun findClassIfExists(className: String, classLoader: ClassLoader): Class<*>? {
    try {
        return findClass(className, classLoader)
    } catch (e: ClassNotFoundError) {
        return null
    }
}

fun findConstructorExactIfExists(clazz: Class<*>, vararg parameterTypes: Any): Constructor<*>? {
    try {
        return findConstructorExact(clazz, parameterTypes)
    } catch (e: ClassNotFoundError) {
        return null
    } catch (e: NoSuchMethodError) {
        return null
    }
}

fun findConstructorExactIfExists(className: String, classLoader: ClassLoader, vararg parameterTypes: Any): Constructor<*>? {
    try {
        return findConstructorExact(className, classLoader, parameterTypes)
    } catch (e: ClassNotFoundError) {
        return null
    } catch (e: NoSuchMethodError) {
        return null
    }
}

fun findFieldIfExists(clazz: Class<*>, fieldName: String): Field? {
    try {
        return findField(clazz, fieldName)
    } catch (e: NoSuchFieldError) {
        return null
    }
}

fun findMethodExactIfExists(clazz: Class<*>, methodName: String, vararg parameterTypes: Any): Method? {
    try {
        return findMethodExact(clazz, methodName, parameterTypes)
    } catch (e: ClassNotFoundError) {
        return null
    } catch (e: NoSuchMethodError) {
        return null
    }
}

fun findMethodExactIfExists(className: String, classLoader: ClassLoader, methodName: String, vararg parameterTypes: Any): Method? {
    try {
        return findMethodExact(className, classLoader, methodName, parameterTypes)
    } catch (e: ClassNotFoundError) {
        return null
    } catch (e: NoSuchMethodError) {
        return null
    }
}
