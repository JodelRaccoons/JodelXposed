package com.jodelXposed.hooks.post;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.jodelXposed.App;
import com.jodelXposed.hooks.helper.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by Admin on 03.04.2017.
 */

public class EnablePasting {

    /**
     * Enable pasting in PostEditText
     */
    public EnablePasting(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            findAndHookMethod(
                "com.jodelapp.jodelandroidv3.view.CreateTextPostFragment",
                lpparam.classLoader,
                "onCreateView",
                LayoutInflater.class,
                ViewGroup.class,
                Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        View rootView = (View) param.getResult();
                        View scrollContainer = ((ScrollView) rootView.findViewById(rootView.getResources().getIdentifier("scrollContainer", "id", App.Companion.getPACKAGE_NAME()))).getChildAt(0);
                        scrollContainer.setClickable(true);
                        scrollContainer.setLongClickable(true);
                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
            Log.dlog("!!!!!!!!!! Failed loading EnablePasting hook !!!!!!!!!!");
        }
    }
}
