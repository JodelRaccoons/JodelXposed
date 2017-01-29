package com.jodelXposed.hooks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * Created by Admin on 07.01.2017.
 */

@SuppressWarnings("ResourceType")
public class FastScrollDownPostDetail {

    public FastScrollDownPostDetail(final XC_LoadPackage.LoadPackageParam lpparam) {

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

                            if (lastVisibleItemPosition > splitList) {
                                callMethod(recyclerView, "smoothScrollToPosition", 0);
                            } else if (lastVisibleItemPosition <= splitList) {
                                callMethod(recyclerView, "smoothScrollToPosition", itemCount);
                            }
                        }
                    });

                }
            });
    }
}
