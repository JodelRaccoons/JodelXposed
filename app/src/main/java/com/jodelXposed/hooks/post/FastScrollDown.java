package com.jodelXposed.hooks.post;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jodelXposed.hooks.helper.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * Created by Admin on 07.01.2017.
 */

@SuppressWarnings("ResourceType")
public class FastScrollDown {

    public FastScrollDown(final XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            findAndHookMethod("com.jodelapp.jodelandroidv3.features.postdetail.PostDetailPresenter", lpparam.classLoader, "a", "com.jodelapp.jodelandroidv3.api.model.PostThreadPage", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Class PresenterView = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.features.postdetail.PostDetailContract.View", lpparam.classLoader);
                    Class PostDetailFragment = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.features.postdetail.PostDetailFragment", lpparam.classLoader);
                    Object mPostDetailFragment = XposedHelpers.findFirstFieldByExactType(param.thisObject.getClass(), PresenterView).get(param.thisObject);
                    View view = (View) XposedHelpers.findFirstFieldByExactType(PostDetailFragment, View.class).get(mPostDetailFragment);
                    view.findViewWithTag("tag_fast_scroll_down").setVisibility(View.VISIBLE);
                }
            });


            findAndHookMethod(
                "com.jodelapp.jodelandroidv3.features.postdetail.PostDetailFragment",
                lpparam.classLoader,
                "onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        View view = (View) param.getResult();
                        ImageView ivScrollDown = (ImageView) view.findViewWithTag("tag_fast_scroll_down");

                        ivScrollDown.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Object recyclerView = getObjectField(param.thisObject, "recyclerView");
                                Object rvAdapter = callMethod(recyclerView, "getAdapter");
                                Object rvLayoutManager = callMethod(recyclerView, "getLayoutManager");
                                int itemCount = (int) callMethod(rvAdapter, "getItemCount");
                                int lastVisibleItemPosition = (int) callMethod(rvLayoutManager, "gV");
                                int splitList = Math.round(itemCount / 2);

                                Class presenter = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.features.postdetail.PostDetailContract.Presenter", lpparam.classLoader);
                                Class postDetailFragment = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.features.postdetail.PostDetailFragment", lpparam.classLoader);
                                Object presenterInstance = null;
                                try {
                                    presenterInstance = XposedHelpers.findFirstFieldByExactType(postDetailFragment, presenter).get(param.thisObject);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }

                                if (lastVisibleItemPosition > splitList) {
                                    callMethod(presenterInstance, "Lk");
                                } else if (lastVisibleItemPosition <= splitList) {
                                    callMethod(presenterInstance, "Ll");
                                }
                            }
                        });

                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
            Log.dlog("!!!!!!!!!! Failed loading FastScrollDown hook !!!!!!!!!!");
        }
    }
}
