package com.jodelXposed.hooks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.jodelXposed.App;
import com.jodelXposed.models.HookValues;
import com.jodelXposed.utils.Log;
import com.jodelXposed.utils.Options;
import com.jodelXposed.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import es.dmoral.prefs.Prefs;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;

public class PostStuff {

    public PostStuff(XC_LoadPackage.LoadPackageParam lpparam) {
        final HookValues hooks = Options.INSTANCE.getHooks();

        /*
         * Track posts #1P
         * Set additional data on the TimeView of each Post to track the
         * user_handle / poster
         * Apply darker shade to OP's posts in a thread
         */
        findAndHookMethod(hooks.Class_PostDetailRecyclerAdapter, lpparam.classLoader, hooks.Method_PostStuff_TrackPostsMethod, "com.jodelapp.jodelandroidv3.view.adapter.PostDetailRecyclerAdapter$PostViewHolder", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object textView = getObjectField(param.args[0], "created");
                List posts = (List) getObjectField(param.thisObject, "posts");
                HashMap<String, String> ids = new HashMap<>(posts.size());

                Log.dlog("Postsize: " + posts.size());

                for (Object post : posts) {
                    String user_handle = (String) getObjectField(post, "userHandle");
                    if (!ids.containsKey(user_handle)) {
                        ids.put(user_handle, String.valueOf(ids.size()));
                    }
                    setAdditionalInstanceField(post, "updateExtraPost", ids.get(user_handle));
                    Log.dlog("User handle: " + user_handle + " Id: " + ids.get(user_handle));
                }

                try {
                    int i = (int) param.args[1];
                    String id = (String) getAdditionalInstanceField(posts.get(i), "updateExtraPost");
                    setAdditionalInstanceField(textView, "updateExtraView", id);
                } catch (IndexOutOfBoundsException ignored) {
                    //In case you reached the last available post (found on Mt. Everest)
                }
            }
        });

        /*
         * Track posts #2
         * Use the additional data from the TimeView to insert the poster ID
         * next to the regular TimeView text
         */
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.TimeView", lpparam.classLoader, "update", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String id = (String) getAdditionalInstanceField(param.thisObject, "updateExtraView");
                if (id != null) {
                    callMethod(param.thisObject, "append", " #" + id);
                }
            }
        });

        final Class StickyPost = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.api.model.StickyPost", lpparam.classLoader);
        findAndHookMethod("com.jodelapp.jodelandroidv3.api.model.GetPostsComboResponse", lpparam.classLoader, "getStickies", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (Prefs.with(Utils.snackbarUtilActivity).readBoolean("displayJXchangelog", true)) {
                    List post = (List) param.getResult();
                    if (post == null)
                        post = new ArrayList();

                    String message = Options.INSTANCE.getHooks().updateMessage;
                    String type = "info";
                    String postid = String.valueOf(Options.INSTANCE.getHooks().versionCode);
                    String color = "595959";
                    String locationName = "JodelXposed";

                    Object stickyPost = XposedHelpers.newInstance(StickyPost, message, type, postid, color, locationName, null, null, null);
                    post.add(0, stickyPost);

                    //TODO set boolean to false when StickyPost is removed by user
                    //TODO set boolean to true when hooks are updated
                }
            }
        });

        //Enable pasting in PostEditText
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.CreateTextPostFragment", lpparam.classLoader, "onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                View rootView = (View) param.getResult();
                ((ScrollView) rootView.findViewById(rootView.getResources().getIdentifier("scrollContainer", "id", App.Companion.getPACKAGE_NAME()))).getChildAt(0).setClickable(true);
            }
        });
    }
}
