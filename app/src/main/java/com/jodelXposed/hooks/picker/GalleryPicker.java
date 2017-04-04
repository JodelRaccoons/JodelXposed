package com.jodelXposed.hooks.picker;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.FileObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jodelXposed.hooks.helper.Log;
import com.jodelXposed.utils.Options;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.os.FileObserver.CLOSE_WRITE;
import static com.jodelXposed.hooks.helper.Activity.getSys;
import static com.jodelXposed.utils.Bitmap.loadBitmap;
import static com.jodelXposed.utils.Utils.getJXSharedImage;
import static com.jodelXposed.utils.Utils.getNewIntent;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * Created by Admin on 04.04.2017.
 */

public class GalleryPicker {
    public GalleryPicker(final XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            findAndHookMethod("com.jodelapp.jodelandroidv3.view.CreateTextPostFragment", lpparam.classLoader, "onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws IllegalAccessException {
                    setupGalleryPicker(param, lpparam);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.dlog("!!!!!!!!!! Failed loading GalleryPicker hook !!!!!!!!!!");
        }
    }

    private void setupGalleryPicker(final XC_MethodHook.MethodHookParam param, final XC_LoadPackage.LoadPackageParam lpparam) {
        final FileObserver imageFileObserver = new FileObserver(getJXSharedImage(), CLOSE_WRITE) {
            @Override
            public void onEvent(int i, String s) {
                Log.dlog("File Observer issued, loading image");
                this.stopWatching();
                Log.dlog("Image loading, FileObserver stopped!");

                Object eventBus = getObjectField(param.thisObject, "bus");

                Class PictureTakenEvent = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.events.PictureTakenEvent", lpparam.classLoader);
                Bitmap bitmap = loadBitmap();
                XposedHelpers.setAdditionalInstanceField(bitmap, "color", XposedHelpers.getObjectField(param.thisObject, findColorField(param)));
                Object pictureTakenEvent = XposedHelpers.newInstance(PictureTakenEvent, bitmap);

                callMethod(eventBus, Options.INSTANCE.getHooks().Method_Otto_Append_Bus_Event, pictureTakenEvent);
            }
        };

        (((View) param.getResult()).findViewWithTag("gallery_button"))
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageFileObserver.startWatching();
                    getSys().startActivity(getNewIntent("utils.Picker").putExtra("choice", 3));
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
}
