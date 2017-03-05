package com.jodelXposed.hooks;

import com.jodelXposed.utils.Log;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

/**
 * Created by Admin on 11.02.2017.
 */

public class JodelErrorTracing {

    public JodelErrorTracing(final XC_LoadPackage.LoadPackageParam lpparam) {


        findAndHookMethod(
            "retrofit.RestAdapter.Builder",
            lpparam.classLoader,
            "setLogLevel",
            "retrofit.RestAdapter.LogLevel",
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object fullLogging = Enum.valueOf((Class<Enum>) findClass("retrofit.RestAdapter.LogLevel", lpparam.classLoader), "FULL");
                    setObjectField(param.thisObject, "logLevel", fullLogging);
                }
            });


        findAndHookMethod(
            "com.rubylight.android.config.rest.Config",
            lpparam.classLoader,
            "getBoolean",
            String.class,
            boolean.class,
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    String query = (String) param.args[0];
                    Log.vlog("Query: " + query);
                    if (query.equals("crashlytics.verbose.enabled")) {
                        param.setResult(true);
                    }
                }
            }
        );


//        try {
//            Logging.logAllClassesInPackage(lpparam,"com.jodelapp.jodelandroidv3.events");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //Print any network error
        findAndHookMethod("com.jodelapp.jodelandroidv3.utilities.errorhandling.rx.ErrorResolutionSubscriber", lpparam.classLoader, "onError", Throwable.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                /*
                * response: retrofit.client.Response@9e94456
                *
                * successType: class com.jodelapp.jodelandroidv3.api.model.SendPostResponse
                * */

                Object retrofitError = param.args[0];
                for (Field f : retrofitError.getClass().getDeclaredFields()) {
                    f.setAccessible(true);
                    try {
                        Object object = f.get(retrofitError);

                        for (Field subf : object.getClass().getDeclaredFields()) {
                            subf.setAccessible(true);
                            Log.dlog(f.getName() + ": " + String.valueOf(f.get(object)));
                        }
                    } catch (Exception e) {
                        Log.dlog(f.getName() + ": " + String.valueOf(f.get(retrofitError)));
                    }
                }
//                ((Throwable) param.args[0]).printStackTrace();
            }
        });

//    public void a(int i, String str, ErrorResolverView errorResolverView) {

//        findAndHookMethod(
//            "com.jodelapp.jodelandroidv3.utilities.errorhandling.ErrorResolutionByCase",
//            lpparam.classLoader,
//            "a",
//            int.class,
//            String.class,
//            "com.jodelapp.jodelandroidv3.view.ErrorResolverView",
//            new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    Thread.dumpStack();
//                }
//            }
//        );
    }

}
