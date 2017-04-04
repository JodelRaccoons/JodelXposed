package com.jodelXposed.hooks.debug;

import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;

import dalvik.system.DexFile;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by Admin on 04.02.2017.
 */

public class Logging {

    private static final String TAG = "Logging";

    public Logging(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod("com.rubylight.android.config.rest.Config", lpparam.classLoader, "getBoolean", String.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.d(TAG, "rubylight.Config#getBoolean: " + param.args[0]);
                if (param.args[0].equals("crashlytics.verbose.enabled")) {
                    param.setResult(true);
                } else if (param.args[0].equals("security.hmac.enabled")) {
                    param.setResult(false);
                }
            }
        });


        findAndHookMethod(ThreadGroup.class, "uncaughtException", Thread.class, Throwable.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                ((Throwable) param.args[1]).printStackTrace();
            }
        });
        findAndHookMethod("com.crashlytics.android.Crashlytics", lpparam.classLoader, "logException", Throwable.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                ((Throwable) param.args[0]).printStackTrace();
                return null;
            }
        });
    }

    public static void listAllMethods(Class clazz) {
        for (Method m : clazz.getDeclaredMethods()) {
            StringBuilder params = new StringBuilder();
            for (Class c : m.getParameterTypes()) {
                params.append("\r\n ").append(c.getName());
            }
            Log.d(TAG, "ListMethods:" +
                "\r\n Name: " + m.getName() +
                "\r\n Params: " + params.toString() +
                "\r\n Return type: " + m.getReturnType());
        }
    }

    public static void logAllClassesInPackage(XC_LoadPackage.LoadPackageParam lpparam, String pkg) throws IOException {
        DexFile dexFile = new DexFile(lpparam.appInfo.sourceDir);
        Enumeration<String> entries = dexFile.entries();
        ArrayList<Class> classes = new ArrayList<>();
        while (entries.hasMoreElements()) {
            String clazz = entries.nextElement();
            if (clazz.contains(pkg)) {
                classes.add(XposedHelpers.findClass(clazz, lpparam.classLoader));
            }
        }
        logAllMethods(classes.toArray(new Class[0]));
    }

    public static void logAllMethods(Class... clazzes) {
        for (Class clazz : clazzes)
            logAllMethods(clazz);
    }

    public static void logAllMethods(Class clazz) {
        if (!clazz.getName().isEmpty()) {
            XposedBridge.hookAllConstructors(clazz, new XCLoggingHook());
            for (Method m : clazz.getDeclaredMethods()) {
                if (!Modifier.isAbstract(m.getModifiers())) {
                    try {
                        Log.v(TAG, "Logging " + clazz.getName());
                        XposedBridge.hookMethod(m, new XCLoggingHook());
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }

    }


    private static class XCLoggingHook extends XC_MethodHook {

        private XCLoggingHook() {
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {

            java.lang.reflect.Method method = (Method) param.method;

//            Method

//            Method method = (Method) param.method;

            String declaringClass = param.method.getDeclaringClass().getName();

            String methodModifier = Modifier.toString(param.method.getModifiers());

            String methodName = param.method.getName();

            StringBuilder parameterTypes = new StringBuilder();
            if (method.getParameterTypes() != null && method.getParameterTypes().length != 0) {
                parameterTypes.append("(");
                for (Class c : method.getParameterTypes()) {
                    parameterTypes.append(c.getSimpleName());
                    parameterTypes.append(", ");
                }
                parameterTypes.deleteCharAt(parameterTypes.lastIndexOf(", "));
                parameterTypes.append(")");
            }

            String clazzAndMethod = declaringClass + "#" + methodName + "::" + parameterTypes.toString();

            StackTraceElement callerClass = new Exception().getStackTrace()[3];

            String caller = "\r\nCalled from: " + callerClass.getClassName() + "#" + callerClass.getMethodName();

            String result = param.getResult() != null ? "\r\nWith Result: " + String.valueOf(param.getResult()) : "";

            StringBuilder args = new StringBuilder("\r\nUsing arguments: ");

            if (param.args != null && method.getParameterTypes() != null && method.getParameterTypes().length != 0)

                for (int i = 0; i <= param.args.length; i++) {
                    args.append("\r\n[ ");
                    args.append(method.getParameterTypes()[i].getSimpleName());
                    args.append(" => ");
                    args.append(String.valueOf(param.args[i]));
                    args.append(" ]");
                }
//                for (Object arg : param.args){
//                    if (arg != null){
//                        args.append("\r\n[ ");
//                        args.append(arg.getClass().getSimpleName());
//                        args.append(" => ");
//                        args.append(String.valueOf(arg));
//                        args.append(" ]");
//                    } else {
//                        args.append("\r\n[ ");
//                        args.append("NULL");
//                        args.append(" => ");
//                        args.append("NULL");
//                        args.append(" ]");
//                    }
//
//                }

            Log.d(TAG, clazzAndMethod + caller + result + (param.args != null ? args.toString() : ""));
        }
    }
}
