package com.jodelXposed.hooks.post;

import com.jodelXposed.hooks.helper.Log;
import com.jodelXposed.utils.Options;

import java.util.HashMap;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;

/**
 * Created by Admin on 03.04.2017.
 */

public class TrackPosts {

    public TrackPosts(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            trackPosts1(lpparam);
            trackPosts2(lpparam);
        } catch (Exception e) {
            e.printStackTrace();
            Log.dlog("!!!!!!!!!! Failed loading TrackPosts hook !!!!!!!!!!");
        }
    }

    /**
     * Track posts #1P
     * Set additional data on the TimeView of each Post to track the
     * user_handle / poster
     * Apply darker shade to OP's posts in a thread
     */
    private void trackPosts1(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod(
            Options.INSTANCE.getHooks().Class_PostDetailRecyclerAdapter,
            lpparam.classLoader,
            Options.INSTANCE.getHooks().Method_PostStuff_TrackPostsMethod,
            "com.jodelapp.jodelandroidv3.features.postdetail.PostDetailRecyclerAdapter.PostViewHolder",
            int.class,
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object postList = getObjectField(param.thisObject, "posts");
                    Object post = callMethod(postList, "get", (int) param.args[1]);

                    Log.dlog("PostId: " + getObjectField(post, "postId"));

                    Object textView = getObjectField(param.args[0], "created");
                    List posts = (List) getObjectField(param.thisObject, "posts");
                    HashMap<String, String> ids = new HashMap<>(posts.size());

                    for (Object p : posts) {
                        String user_handle = (String) getObjectField(p, "userHandle");
                        if (!ids.containsKey(user_handle)) {
                            ids.put(user_handle, String.valueOf(ids.size()));
                        }
                        setAdditionalInstanceField(post, "updateExtraPost", ids.get(user_handle));
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
    }

    /**
     * Track posts #2
     * Use the additional data from the TimeView to insert the poster ID
     * next to the regular TimeView text
     */
    private void trackPosts2(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod(
            "com.jodelapp.jodelandroidv3.view.TimeView",
            lpparam.classLoader,
            "update",
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    String id = (String) getAdditionalInstanceField(param.thisObject, "updateExtraView");
                    if (id != null)
                        callMethod(param.thisObject, "append", " #" + id);
                }
            });
    }
}
