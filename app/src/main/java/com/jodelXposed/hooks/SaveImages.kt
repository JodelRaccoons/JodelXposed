package com.jodelXposed.hooks

import android.app.AndroidAppHelper
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import com.jodelXposed.utils.Log.dlog
import com.jodelXposed.utils.Log.xlog
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.callbacks.XC_LoadPackage
import com.jodelXposed.utils.Bitmap as BitmapJX

class SaveImages(loadPackageParam: XC_LoadPackage.LoadPackageParam, classLoader: ClassLoader = loadPackageParam.classLoader) {
    init {
        fun afterHookHandler(param: XC_MethodHook.MethodHookParam) {
            val post = getObjectField(param.thisObject, "aDh")
            val context = AndroidAppHelper.currentApplication()

            var postImageUrl = getObjectField(post, "imageUrl")
            if (postImageUrl !is String) {
                dlog("Post has no imageUrl")
                return
            }
            postImageUrl = if (postImageUrl.startsWith("//")) "https:${postImageUrl}" else postImageUrl
            xlog("Saving image from ${postImageUrl}")

            Picasso.with(context).load(postImageUrl).into(object : Target {
                override fun onBitmapFailed(errorDrawable: Drawable?) {
                    xlog("Failed to load image")
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    dlog("Preparing to load image")
                }

                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                    dlog("Loaded image")
                    // TODO: Save to dedicated folder
                    BitmapJX.saveBitmap(bitmap)
                }
            })
        }

        XposedHelpers.findAndHookMethod(
                "com.jodelapp.jodelandroidv3.view.gesture.JodelGestureListener",
                classLoader,
                "onDoubleTap",
                MotionEvent::class.java,
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) = afterHookHandler(param)
                })
    }
}