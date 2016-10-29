package com.jodelXposed.hooks

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.jodelXposed.utils.Log.xlog
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import com.jodelXposed.utils.Bitmap as BitmapJX

class SaveImages(loadPackageParam: XC_LoadPackage.LoadPackageParam, classLoader: ClassLoader = loadPackageParam.classLoader) {
    init {
        fun afterHookHandler(param: XC_MethodHook.MethodHookParam) {
            xlog("Hooking weird lambda!")
            val dialog = param.args[1] as Dialog // MaterialDialog
            val post = param.args[2]
            val customView: LinearLayout // post_prompt_dialog.xml
            val customViewContainer = XposedHelpers.getObjectField(dialog, "Pk") as FrameLayout

            xlog("customViewContainer childcount: ${customViewContainer.childCount}")

            if (customViewContainer.childCount != 1)
                return

            customView = customViewContainer.getChildAt(0) as LinearLayout

            val textView = TextView(dialog.context)
            textView.text = "Download this image..?"
            textView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)

            var postImageUrl = XposedHelpers.getObjectField(post, "imageUrl") as String
            postImageUrl = if (postImageUrl.startsWith("//")) "https:${postImageUrl}" else postImageUrl

            val layout = XposedHelpers.newInstance(
                    XposedHelpers.findClass("com.balysv.materialripple.MaterialRippleLayout", classLoader),
                    dialog.context
            ) as FrameLayout
            layout.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)

            textView.setOnClickListener({
                xlog("Saving image from ${postImageUrl}")

                Picasso.with(dialog.context).load(postImageUrl).into(object : Target {
                    override fun onBitmapFailed(errorDrawable: Drawable?) {
                        xlog("Failed to load image")
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        xlog("Preparing to load image")
                    }

                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                        xlog("Loaded image")
                        // TODO: Save to dedicated folder
                        BitmapJX.saveBitmap(bitmap)
                    }
                })
            })

            xlog("Customview children: ${customView.childCount}")

            layout.addView(textView)
            customView.addView(layout, 2)
        }

        XposedHelpers.findAndHookMethod(
                "com.jodelapp.jodelandroidv3.view.adapter.PostDetailRecyclerAdapter\$\$Lambda\$1",
                classLoader,
                "a",
                "com.jodelapp.jodelandroidv3.view.adapter.PostDetailRecyclerAdapter",
                "com.afollestad.materialdialogs.MaterialDialog",
                "com.jodelapp.jodelandroidv3.api.model.Post",
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) = afterHookHandler(param)
                })
    }
}