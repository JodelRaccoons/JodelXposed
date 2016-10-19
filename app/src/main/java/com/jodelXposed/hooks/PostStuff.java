package com.jodelXposed.hooks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.jodelXposed.models.Hookvalues;
import com.jodelXposed.utils.Log;
import com.jodelXposed.utils.Options;

import java.util.HashMap;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.jodelXposed.utils.Utils.getActivity;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;

public class PostStuff {

    public PostStuff(XC_LoadPackage.LoadPackageParam lpparam) {
        final Hookvalues hooks = Options.getInstance().getHooks();

        /*
         * Track posts #1
         * Set additional data on the TimeView of each Post to track the
         * user_handle / poster
         * Apply darker shade to OP's posts in a thread
         */
        findAndHookMethod(hooks.Class_PostDetailRecyclerAdapter, lpparam.classLoader, hooks.PostStuff_TrackPostsMethod, "com.jodelapp.jodelandroidv3.view.adapter.PostDetailRecyclerAdapter$PostViewHolder", int.class, new XC_MethodHook() {
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
                if (id != null){
                    callMethod(param.thisObject, "append", " #" + id);
                }
            }
        });


        findAndHookMethod("com.jodelapp.jodelandroidv3.view.PostDetailFragment", lpparam.classLoader, "onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                final SharedPreferences sharedPref = getActivity(param).getPreferences(Context.MODE_PRIVATE);

                final String postID = (String) XposedHelpers.getObjectField(param.thisObject,"postId");

                final Switch sw = (Switch) ((View)param.getResult()).findViewWithTag("sw_gcm_notification");
                sw.setVisibility(View.VISIBLE);
                sw.setChecked(!Options.getInstance().getBetaObject().getNotificationList().contains(postID));

                int color = 0;
                try {
                    color = Color.parseColor((String)getObjectField(param.thisObject,"ayf"));
                    switchColor(sw,sw.isChecked(),color);
                    final int finalColor = color;
                    sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (!sharedPref.getBoolean("displayedNotificationExplanation", false)){
                                showFeatureExplanation(sharedPref,param);
                            }
                            switchColor(sw,b, finalColor);
                            if (b){
                                Options.getInstance().getBetaObject().getNotificationList().remove(postID);
                            }else{
                                Options.getInstance().getBetaObject().getNotificationList().add(postID);
                            }
                            Options.getInstance().save();
                        }
                    });
                }catch(IllegalArgumentException ignored){}
            }
        });

    }

    private void showFeatureExplanation(final SharedPreferences editor, XC_MethodHook.MethodHookParam param) {
        new AlertDialog.Builder(getActivity(param))
            .setTitle("You discovered a new Feature!")
            .setMessage("You discovered a new JodelXposed feature, the disabling of notifications in single threads. So now you have the possibility to mute specific threads which get annoying.")
            .setPositiveButton("Okay, dont display again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    editor.edit().putBoolean("displayedNotificationExplanation", true).apply();
                    dialogInterface.dismiss();
                }
            })
            .setCancelable(false)
            .show();
    }

    private void switchColor(Switch sw, boolean checked, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            sw.getThumbDrawable().setColorFilter(checked ? color : Color.GRAY, PorterDuff.Mode.MULTIPLY);
            sw.getTrackDrawable().setColorFilter(!checked ? color : Color.GRAY, PorterDuff.Mode.MULTIPLY);
        }

    }
}
