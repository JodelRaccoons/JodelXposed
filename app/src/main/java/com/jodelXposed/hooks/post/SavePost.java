package com.jodelXposed.hooks.post;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.jodelXposed.hooks.helper.Log;
import com.jodelXposed.utils.Options;
import com.jodelXposed.utils.TSnackbar;
import com.jodelXposed.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.jodelXposed.hooks.helper.Log.vlog;
import static com.jodelXposed.hooks.helper.Log.xlog;
import static com.jodelXposed.utils.Bitmap.saveBitmap;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * Created by Admin on 04.04.2017.
 */

public class SavePost {

    public SavePost(final XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            XposedHelpers.findAndHookMethod(
                Options.INSTANCE.getHooks().Class_JodelGestureListener,
                lpparam.classLoader,
                "onDoubleTap",
                MotionEvent.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object post = getObjectField(param.thisObject, Options.INSTANCE.getHooks().Field_JodelGestureListener_Post);
                        Application context = AndroidAppHelper.currentApplication();

                        Object postImageUrl = getObjectField(post, "imageUrl");
                        if (!(postImageUrl instanceof String)) {
                            vlog("Post has no imageUrl");
                            vlog("Copying post message");
                            String postMessage = (String) getObjectField(post, "message");

                            ClipboardManager clipboard = (ClipboardManager) AndroidAppHelper.currentApplication().getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboard.setPrimaryClip(ClipData.newPlainText("JodelPost", postMessage));

                            TSnackbar.make(lpparam, "Copied to clipboard!", -1);

                            return;
                        }
                        String imageUrl = ((String) postImageUrl).startsWith("//") ? "https:${postImageUrl}" : (String) postImageUrl;
                        final String filename = new File(imageUrl).getName();
                        xlog("Saving image from ${imageUrl}");


                        Picasso.with(context).load(imageUrl).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                vlog("Loaded image");
                                // DONE: Save to dedicated folder
                                // DONE images saving failes at first try when jodel is opened, second try is needed
                                // DONE: start media scanner after saving to make the image visible in the gallery
                                saveBitmap(bitmap, Utils.getSaveImagesFolder() + File.separator + filename);

                                TSnackbar.make(lpparam, "Saved image!", -1);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                xlog("Failed to load image");

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                vlog("Preparing to load image");

                            }
                        });
                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
            Log.dlog("!!!!!!!!!! Failed loading SavePost hook !!!!!!!!!!");
        }
    }
}
