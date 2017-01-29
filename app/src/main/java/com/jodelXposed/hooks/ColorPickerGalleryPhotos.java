package com.jodelXposed.hooks;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.FileObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jodelXposed.utils.Log;
import com.jodelXposed.utils.Options;
import com.jodelXposed.utils.Utils;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.os.FileObserver.CLOSE_WRITE;
import static com.jodelXposed.utils.Bitmap.loadBitmap;
import static com.jodelXposed.utils.Utils.getActivity;
import static com.jodelXposed.utils.Utils.getJXSharedImage;
import static com.jodelXposed.utils.Utils.getNewIntent;
import static com.jodelXposed.utils.Utils.getSystemContext;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class ColorPickerGalleryPhotos {
    public static boolean imageShared = false;

    /**
     * Add features on ImageView - load custom stored image, adjust ScaleType
     * Remove blur effect
     */
    public ColorPickerGalleryPhotos(final XC_LoadPackage.LoadPackageParam lpparam) {

        findAndHookMethod("com.jodelapp.jodelandroidv3.view.CreateTextPostFragment", lpparam.classLoader, "onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws IllegalAccessException {


                final FileObserver imageFileObserver = new FileObserver(getJXSharedImage(), CLOSE_WRITE) {
                    @Override
                    public void onEvent(int i, String s) {
                        Log.dlog("File Observer issued, loading image");
                        this.stopWatching();
                        Log.dlog("Image loading, FileObserver stopped!");

                        Object eventBus = getObjectField(param.thisObject, "bus");

                        Class PictureTakenEvent = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.events.PictureTakenEvent", lpparam.classLoader);
                        Object pictureTakenEvent = XposedHelpers.newInstance(PictureTakenEvent, loadBitmap());

                        callMethod(eventBus, Options.INSTANCE.getHooks().Method_Otto_Append_Bus_Event, pictureTakenEvent);
                    }
                };

                final Activity activity = getActivity(param);

                String colorField = null;
                for (Field f : param.thisObject.getClass().getDeclaredFields()) {
                    f.setAccessible(true);
                    if (f.getType().getName().equals(String.class.getName()) && f.isAccessible()) {
                        String field = (String) f.get(param.thisObject);
                        if (field != null && field.contains("#")) {
                            colorField = f.getName();
                            break;
                        }
                    }
                }
                final String finalColorField = colorField;

                (((View) param.getResult()).findViewWithTag("gallery_button"))
                    .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        imageFileObserver.startWatching();
                        getSystemContext().startActivity(getNewIntent("utils.Picker").putExtra("choice", 3));
                    }
                });

                final View create_post_layout = ((View) param.getResult()).findViewById(activity.getResources().getIdentifier("create_post_layout", "id", "com.tellm.android.app"));

                (((View) param.getResult()).findViewWithTag("color_chooser"))
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            View dialoglayout = activity.getLayoutInflater().inflate(LayoutHooks.JodelResIDs.layout_color_picker, null);

                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Pick your desired color");
                            builder.setView(dialoglayout);

                            AlertDialog alertDialog = builder.create();

                            ColorPickerOnClickListener colorPickerOnClickListener = new ColorPickerOnClickListener(create_post_layout, param, finalColorField, alertDialog);

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
        });
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
