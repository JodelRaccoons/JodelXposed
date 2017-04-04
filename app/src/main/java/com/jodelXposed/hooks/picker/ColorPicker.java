package com.jodelXposed.hooks.picker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jodelXposed.hooks.helper.Log;
import com.jodelXposed.utils.Utils;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;
import static com.jodelXposed.hooks.helper.Activity.getMain;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by Admin on 04.04.2017.
 */

public class ColorPicker {

    public ColorPicker(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            findAndHookMethod("com.jodelapp.jodelandroidv3.view.CreateTextPostFragment", lpparam.classLoader, "onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws IllegalAccessException {
                    setupColorPicker(param);
                }
            });
            passColorToCamera(lpparam);
        } catch (Exception e) {
            e.printStackTrace();
            Log.dlog("!!!!!!!!!! Failed loading ColorPicker hook !!!!!!!!!!");
        }
    }

    private void passColorToCamera(final XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod("com.jodelapp.jodelandroidv3.features.photoedit.PhotoEditFragment", lpparam.classLoader, "onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class PhotoEditFragment = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.features.photoedit.PhotoEditFragment", lpparam.classLoader);
                Field colorField = XposedHelpers.findFirstFieldByExactType(PhotoEditFragment, String.class);
                Field bitmapField = XposedHelpers.findFirstFieldByExactType(PhotoEditFragment, Bitmap.class);
                bitmapField.setAccessible(true);
                Bitmap bitmap = (Bitmap) bitmapField.get(param.thisObject);
                String color = (String) XposedHelpers.getAdditionalInstanceField(bitmap, "color");
                XposedHelpers.setObjectField(param.thisObject, colorField.getName(), color);
            }
        });
    }

    private String findColorField(XC_MethodHook.MethodHookParam param) {
        String colorField = null;
        for (Field f : param.thisObject.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if (f.getType().getName().equals(String.class.getName()) && f.isAccessible()) {
                String field = null;
                try {
                    field = (String) f.get(param.thisObject);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (field != null && field.contains("#")) {
                    colorField = f.getName();
                    break;
                }
            }
        }
        return colorField;
    }

    private void setupColorPicker(final XC_MethodHook.MethodHookParam param) throws IllegalAccessException {
        final Activity activity = getMain();

        final View create_post_layout = ((View) param.getResult()).findViewById(activity.getResources().getIdentifier("create_post_layout", "id", "com.tellm.android.app"));

        (((View) param.getResult()).findViewWithTag("color_chooser"))
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    View dialoglayout = getColorPickerView();

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Pick your desired color");
                    builder.setView(dialoglayout);

                    AlertDialog alertDialog = builder.create();

                    ColorPickerOnClickListener colorPickerOnClickListener = new ColorPickerOnClickListener(create_post_layout, param, findColorField(param), alertDialog);

                    dialoglayout.findViewWithTag("cp_orange").setOnClickListener(colorPickerOnClickListener);
                    dialoglayout.findViewWithTag("cp_yellow").setOnClickListener(colorPickerOnClickListener);
                    dialoglayout.findViewWithTag("cp_red").setOnClickListener(colorPickerOnClickListener);
                    dialoglayout.findViewWithTag("cp_blue").setOnClickListener(colorPickerOnClickListener);
                    dialoglayout.findViewWithTag("cp_bluegrayish").setOnClickListener(colorPickerOnClickListener);
                    dialoglayout.findViewWithTag("cp_green").setOnClickListener(colorPickerOnClickListener);

                    alertDialog.show();
                }
            });
    }

    private View getColorPickerView() {
        Context ctx = getMain();

        LinearLayout.LayoutParams colorLayoutParams = new LinearLayout.LayoutParams(Utils.dpToPx(70), Utils.dpToPx(70));
        colorLayoutParams.setMargins(Utils.dpToPx(20), Utils.dpToPx(20), Utils.dpToPx(20), Utils.dpToPx(20));

        LinearLayout rootLayout = new LinearLayout(ctx);
        rootLayout.setOrientation(VERTICAL);
        LinearLayout.LayoutParams rootLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        rootLayout.setLayoutParams(rootLayoutParams);

        LinearLayout firstRow = new LinearLayout(ctx);
        firstRow.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams firstRowLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        firstRowLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        firstRow.setLayoutParams(firstRowLayoutParams);

        ImageView orange = new ImageView(ctx);
        orange.setTag("cp_orange");
        orange.setBackgroundColor(Color.parseColor("#FFFF9908"));
        orange.setLayoutParams(colorLayoutParams);
        firstRow.addView(orange);

        ImageView yellow = new ImageView(ctx);
        yellow.setTag("cp_yellow");
        yellow.setBackgroundColor(Color.parseColor("#FFFFBA00"));
        yellow.setLayoutParams(colorLayoutParams);
        firstRow.addView(yellow);

        ImageView red = new ImageView(ctx);
        red.setTag("cp_red");
        red.setBackgroundColor(Color.parseColor("#FFDD5F5F"));
        red.setLayoutParams(colorLayoutParams);
        firstRow.addView(red);
        rootLayout.addView(firstRow);

        LinearLayout secondRow = new LinearLayout(ctx);
        secondRow.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams secondRowLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        secondRowLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        secondRow.setLayoutParams(secondRowLayoutParams);

        ImageView blue = new ImageView(ctx);
        blue.setTag("cp_blue");
        blue.setBackgroundColor(Color.parseColor("#FF06A3CB"));
        blue.setLayoutParams(colorLayoutParams);
        secondRow.addView(blue);

        ImageView bluegrayish = new ImageView(ctx);
        bluegrayish.setTag("cp_bluegrayish");
        bluegrayish.setBackgroundColor(Color.parseColor("#FF8ABDB0"));
        bluegrayish.setLayoutParams(colorLayoutParams);
        secondRow.addView(bluegrayish);

        ImageView green = new ImageView(ctx);
        green.setTag("cp_green");
        green.setBackgroundColor(Color.parseColor("#FF9EC41C"));
        green.setLayoutParams(colorLayoutParams);
        secondRow.addView(green);
        rootLayout.addView(secondRow);

        return rootLayout;
    }

    private class ColorPickerOnClickListener implements View.OnClickListener {
        private View create_post_layout;
        private XC_MethodHook.MethodHookParam param;
        private String finalColorField;
        private AlertDialog alertDialog;

        ColorPickerOnClickListener(View view, XC_MethodHook.MethodHookParam param, String string, AlertDialog alertDialog) {
            this.create_post_layout = view;
            this.param = param;
            this.finalColorField = string;
            this.alertDialog = alertDialog;
        }

        @Override
        public void onClick(View v) {
            final String tag = (String) v.getTag();
            switch (tag) {
                case "cp_orange":
                    create_post_layout.setBackgroundColor(Color.parseColor(Utils.Colors.Colors.get(0)));
                    XposedHelpers.setObjectField(param.thisObject, finalColorField, Utils.Colors.Colors.get(0));
                    break;
                case "cp_yellow":
                    create_post_layout.setBackgroundColor(Color.parseColor(Utils.Colors.Colors.get(1)));
                    XposedHelpers.setObjectField(param.thisObject, finalColorField, Utils.Colors.Colors.get(1));
                    break;
                case "cp_red":
                    create_post_layout.setBackgroundColor(Color.parseColor(Utils.Colors.Colors.get(2)));
                    XposedHelpers.setObjectField(param.thisObject, finalColorField, Utils.Colors.Colors.get(2));
                    break;
                case "cp_blue":
                    create_post_layout.setBackgroundColor(Color.parseColor(Utils.Colors.Colors.get(3)));
                    XposedHelpers.setObjectField(param.thisObject, finalColorField, Utils.Colors.Colors.get(3));
                    break;
                case "cp_bluegrayish":
                    create_post_layout.setBackgroundColor(Color.parseColor(Utils.Colors.Colors.get(4)));
                    XposedHelpers.setObjectField(param.thisObject, finalColorField, Utils.Colors.Colors.get(4));
                    break;
                case "cp_green":
                    create_post_layout.setBackgroundColor(Color.parseColor(Utils.Colors.Colors.get(5)));
                    XposedHelpers.setObjectField(param.thisObject, finalColorField, Utils.Colors.Colors.get(5));
                    break;
            }
            alertDialog.dismiss();
        }
    }
}
