package com.jodelXposed.hooks

import android.app.AndroidAppHelper
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import com.jodelXposed.utils.Log.dlog
import com.jodelXposed.utils.Log.xlog
import com.jodelXposed.utils.Options
import com.jodelXposed.utils.Utils
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.callbacks.XC_LoadPackage
import com.jodelXposed.utils.Bitmap as BitmapJX

class SavePost(loadPackageParam: XC_LoadPackage.LoadPackageParam, classLoader: ClassLoader = loadPackageParam.classLoader) {
    init {
        fun afterHookHandler(param: XC_MethodHook.MethodHookParam) {
            val post = getObjectField(param.thisObject, Options.hooks.Field_JodelGestureListener_Post)
            val context = AndroidAppHelper.currentApplication()

            var postImageUrl = getObjectField(post, "imageUrl")
            if (postImageUrl !is String) {
                dlog("Post has no imageUrl")
                dlog("Copying post message")
                val postMessage = getObjectField(post, "message") as String

                val clipboard = AndroidAppHelper.currentApplication().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("JodelPost", postMessage)
                clipboard.primaryClip = clip

                Utils.makeSnackbarWithNoCtx(loadPackageParam, "Copied to clipboard!")

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
                    // DONE: Save to dedicated folder
                    //TODO images saving failes at first try when jodel is opened, second try is needed
                    //TODO start media scanner after saving to make the image visible in the gallery
                    BitmapJX.saveBitmap(bitmap, Utils.getSaveImagesPath())

                    Utils.makeSnackbarWithNoCtx(loadPackageParam, "Saved image!")
                }
            })
        }

        XposedHelpers.findAndHookMethod(
                Options.hooks.Class_JodelGestureListener,
                classLoader,
                "onDoubleTap",
                MotionEvent::class.java,
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) = afterHookHandler(param)
                })
    }
}