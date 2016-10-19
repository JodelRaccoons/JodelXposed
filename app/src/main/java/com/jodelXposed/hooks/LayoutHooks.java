package com.jodelXposed.hooks;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

public class LayoutHooks {
    public LayoutHooks(XC_InitPackageResources.InitPackageResourcesParam resparam) {

        /*
        * Hook the Toolbar of the PostDetailView to add a Switch for disabling notifications
         */
        resparam.res.hookLayout(resparam.res.getIdentifier("toolbar_subfeed","layout","com.tellm.android.app"), new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(XC_LayoutInflated.LayoutInflatedParam liparam) throws Throwable {
                Context ctx = AndroidAppHelper.currentApplication().getApplicationContext();

                //Instantiate a switch with tag for later usage
                Switch sw = new Switch(ctx);
                sw.setVisibility(View.GONE);
                sw.setTag("sw_gcm_notification");


                //add it to a LL and RL for proper visibility
                LinearLayout ll = new LinearLayout(ctx);
                ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(sw);


                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                params.addRule(RelativeLayout.ALIGN_BOTTOM);

                RelativeLayout rl = new RelativeLayout(ctx);
                rl.setLayoutParams(params);
                rl.addView(ll);

                //add it to the existing view
                // liparam.view -> LinearLayout
                ((RelativeLayout)((ViewGroup)((LinearLayout)liparam.view).getChildAt(0)).getChildAt(0)).addView(rl);
            }
        });

    }
}
