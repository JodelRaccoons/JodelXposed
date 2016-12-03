package com.jodelXposed.hooks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView

import com.jodelXposed.App
import com.jodelXposed.models.HookValues
import com.jodelXposed.utils.Log
import com.jodelXposed.utils.Options
import com.jodelXposed.utils.Utils

import java.util.ArrayList
import java.util.HashMap

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import es.dmoral.prefs.Prefs

import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField

class PostStuff(lpparam: XC_LoadPackage.LoadPackageParam) {

    init {
        val hooks = Options.hooks

        /*
         * Track posts #1P
         * Set additional data on the TimeView of each Post to track the
         * user_handle / poster
         * Apply darker shade to OP's posts in a thread
         */
        findAndHookMethod(hooks.Class_PostDetailRecyclerAdapter, lpparam.classLoader, hooks.Method_PostStuff_TrackPostsMethod, "com.jodelapp.jodelandroidv3.view.adapter.PostDetailRecyclerAdapter\$PostViewHolder", Int::class.javaPrimitiveType, object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                val textView = getObjectField(param!!.args[0], "created")
                val posts = getObjectField(param.thisObject, "posts") as List<*>
                val ids = HashMap<String, String>(posts.size)

                Log.vlog("Postsize: " + posts.size)

                for (post in posts) {
                    val user_handle = getObjectField(post, "userHandle") as String
                    if (!ids.containsKey(user_handle)) {
                        ids.put(user_handle, ids.size.toString())
                    }
                    setAdditionalInstanceField(post, "updateExtraPost", ids[user_handle])
                    Log.vlog("User handle: " + user_handle + " Id: " + ids[user_handle])
                }

                try {
                    val i = param.args[1] as Int
                    val id = getAdditionalInstanceField(posts[i], "updateExtraPost") as String
                    setAdditionalInstanceField(textView, "updateExtraView", id)
                } catch (ignored: IndexOutOfBoundsException) {
                    //In case you reached the last available post (found on Mt. Everest)
                }

            }
        })

        /*
         * Track posts #2
         * Use the additional data from the TimeView to insert the poster ID
         * next to the regular TimeView text
         */
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.TimeView", lpparam.classLoader, "update", object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                val id = getAdditionalInstanceField(param!!.thisObject, "updateExtraView") as String
                if (id != null) {
                    callMethod(param.thisObject, "append", " #" + id)
                }
            }
        })

        val StickyPost = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.api.model.StickyPost", lpparam.classLoader)
        findAndHookMethod("com.jodelapp.jodelandroidv3.api.model.GetPostsComboResponse", lpparam.classLoader, "getStickies", object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                if (Prefs.with(Utils.snackbarUtilActivity).readBoolean("displayJXchangelog", true)) {
                    var post: MutableList<*>? = param!!.result as List<*>
                    if (post == null)
                        post = ArrayList()

                    val message = Options.hooks.updateMessage
                    val type = "info"
                    val postid = Options.hooks.versionCode.toString()
                    val color = "595959"
                    val locationName = "JodelXposed"

                    val stickyPost = XposedHelpers.newInstance(StickyPost, message, type, postid, color, locationName, null, null, null)
                    post!!.add(0, stickyPost)

                    //TODO set boolean to false when StickyPost is removed by user
                    //TODO set boolean to true when hooks are updated
                }
            }
        })

        //Enable pasting in PostEditText
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.CreateTextPostFragment", lpparam.classLoader, "onCreateView", LayoutInflater::class.java, ViewGroup::class.java, Bundle::class.java, object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                val rootView = param!!.result as View
                (rootView.findViewById(rootView.resources.getIdentifier("scrollContainer", "id", App.PACKAGE_NAME)) as ScrollView).getChildAt(0).isClickable = true
            }
        })
    }
}
