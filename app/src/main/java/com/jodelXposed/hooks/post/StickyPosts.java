package com.jodelXposed.hooks.post;

import android.view.View;

import com.jodelXposed.hooks.helper.Log;
import com.jodelXposed.utils.Options;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import es.dmoral.prefs.Prefs;

import static com.jodelXposed.hooks.helper.Activity.getMain;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * Created by Admin on 03.04.2017.
 */

public class StickyPosts {

    private final Class stickyPostClass;

    public StickyPosts(XC_LoadPackage.LoadPackageParam lpparam) {
        stickyPostClass = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.api.model.StickyPost", lpparam.classLoader);

        try {
            stickyPosts1(lpparam);
        } catch (Exception e) {
            e.printStackTrace();
            Log.dlog("!!!!!!!!!! Failed loading stickyPosts1 hook !!!!!!!!!!");
        }

        try {
            stickyPosts2(lpparam);
        } catch (Exception e) {
            e.printStackTrace();
            Log.dlog("!!!!!!!!!! Failed loading stickyPosts2 hook !!!!!!!!!!");
        }
    }

    private void stickyPosts1(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod(
            "com.jodelapp.jodelandroidv3.api.model.GetPostsComboResponse",
            lpparam.classLoader,
            "getStickies",
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (Prefs.with(getMain()).readBoolean("displayJXchangelog", true)) {
                        ArrayList posts = (ArrayList) param.getResult();

                        String message = Options.INSTANCE.getHooks().updateMessage;
                        String type = "info";
                        String postid = String.valueOf(Options.INSTANCE.getHooks().versionCode);
                        String color = "595959";
                        String locationName = "JodelXposed";

                        Object stickyPost = XposedHelpers.newInstance(stickyPostClass, message, type, postid, color, locationName, null, null, null);
                        posts.add(0, stickyPost);
                    }
                }
            });
    }

    private void stickyPosts2(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod("com.jodelapp.jodelandroidv3.features.feed.FeedRecyclerAdapter",
            lpparam.classLoader,
            "a",
            "com.jodelapp.jodelandroidv3.features.feed.FeedRecyclerAdapter.StickyViewHolder",
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    Object stickyViewHolder = param.args[0];
                    Object closeButton = getObjectField(stickyViewHolder, "closeButton");
                    Field stickyPostListField = null;
                    for (Field f : param.thisObject.getClass().getDeclaredFields()) {
                        if (f.getType().toString().contains("List")) {
                            stickyPostListField = f;
                            break;
                        }
                    }
                    Object firstStickyPost = ((List) getObjectField(param.thisObject, stickyPostListField.getName())).get(0);
                    if (firstStickyPost != null) {
                        String locationName = (String) getObjectField(firstStickyPost, "locationName");
                        if (locationName.equalsIgnoreCase("JodelXposed")) {
                            final Field finalStickyPostListField = stickyPostListField;
                            callMethod(closeButton, "setOnClickListener", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Prefs.with(getMain()).writeBoolean("displayJXchangelog", false);
                                    try {
                                        callMethod(getObjectField(param.thisObject, finalStickyPostListField.getName()), "remove", 0);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        callMethod(param.thisObject, "bN", 0);
                                    } catch (Exception e) {
                                        callMethod(param.thisObject, "notifyDataSetChanged");
                                    }
                                }
                            });
                        }
                    }
                }
            });
    }
}
