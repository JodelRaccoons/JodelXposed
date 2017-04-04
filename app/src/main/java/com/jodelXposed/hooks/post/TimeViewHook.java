package com.jodelXposed.hooks.post;

import com.jodelXposed.hooks.helper.Log;

import java.sql.Timestamp;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import es.dmoral.prefs.Prefs;

import static com.jodelXposed.hooks.helper.Activity.getMain;

/**
 * Created by Admin on 04.04.2017.
 */

public class TimeViewHook {
    public TimeViewHook(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            XposedHelpers.findAndHookMethod("com.jodelapp.jodelandroidv3.view.TimeView", lpparam.classLoader, "a", "org.joda.time.DateTime", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    String jodelDate = (String) param.getResult();

                    Object dateTimeObject = param.args[0];
                    if (dateTimeObject != null) {
                        long dateTimeMillis = (long) XposedHelpers.callMethod(dateTimeObject, "getMillis");
                        String dateTimeString = new Timestamp(dateTimeMillis).toString().replace("-", ".");

                        switch (Prefs.with(getMain()).readInt("timeFormat", 4)) {
                            case 1:
                                dateTimeString = dateTimeString.substring(dateTimeString.indexOf(" ") + 1, dateTimeString.length() - 4);
                                break;
                            case 2:
                                dateTimeString = jodelDate + " | " + dateTimeString.substring(dateTimeString.indexOf(" ") + 1, dateTimeString.length() - 4);
                                break;
                            case 3:
                                dateTimeString = dateTimeString.substring(0, dateTimeString.length() - 4);
                                break;
                            case 4:
                                dateTimeString = jodelDate;
                                break;
                            default:
                                dateTimeString = jodelDate + " | " + dateTimeString.substring(dateTimeString.indexOf(" ") + 1, dateTimeString.length() - 4);
                                break;
                        }

                        param.setResult(dateTimeString);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.dlog("!!!!!!!!!! Failed loading TimeView hook !!!!!!!!!!");
        }
    }
}
