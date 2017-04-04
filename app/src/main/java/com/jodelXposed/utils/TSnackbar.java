package com.jodelXposed.utils;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jodelXposed.hooks.LayoutHooks;
import com.jodelXposed.hooks.helper.Log;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.jodelXposed.hooks.helper.Activity.getMain;
import static com.jodelXposed.hooks.helper.Activity.getSys;

/**
 * Created by Admin on 04.04.2017.
 */

public class TSnackbar {

    public static void make(XC_LoadPackage.LoadPackageParam lpparam, String message) {
        make(lpparam, message, 0);
    }

    public static void make(XC_LoadPackage.LoadPackageParam lpparam, String message, int length) {
        if (getMain() != null) {
            try {
                Class TSnackbar = XposedHelpers.findClass("com.androidadvance.topsnackbar.TSnackbar", lpparam.classLoader);
                Object contentView = getMain().findViewById(android.R.id.content);
                Object subContentView = XposedHelpers.callMethod(contentView, "getChildAt", 0);
                Object snackbar = null;
                switch (length) {
                    case 0:
                        snackbar = XposedHelpers.callStaticMethod(TSnackbar, "a", subContentView, message, 0);
                        break;
                    case -1:
                        snackbar = XposedHelpers.callStaticMethod(TSnackbar, "a", subContentView, message, -1);
                        break;
                    case -2:
                        snackbar = XposedHelpers.callStaticMethod(TSnackbar, "a", subContentView, message, -2);
                        break;
                }
                View snackbarview = (View) XposedHelpers.callMethod(snackbar, "getView");
                snackbarview.setBackgroundColor(getMain().getResources().getColor(getMain().getResources().getIdentifier("background_floating_material_light", "color", "com.tellm.android.app")));
                TextView snackbarTextView = (TextView) snackbarview.findViewById(getMain().getResources().getIdentifier("snackbar_text", "id", "com.tellm.android.app"));
                snackbarTextView.setTextColor(Color.BLACK);
                snackbarTextView.setGravity(Gravity.CENTER);
                XposedHelpers.callMethod(snackbar, "ay", LayoutHooks.JodelResIDs.ic_jx_icon, 256);
                XposedHelpers.callMethod(snackbar, "show");
            } catch (Exception e) {
                Log.xlog("Could not create snackbar", e);
                Toast.makeText(getSys(), message, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getSys(), message, Toast.LENGTH_LONG).show();
            Toast.makeText(getSys(), "Activity is null.", Toast.LENGTH_SHORT).show();
        }
    }
}
