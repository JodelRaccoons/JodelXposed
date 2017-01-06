package com.jodelXposed.hooks;

import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Color;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jodelXposed.App;
import com.jodelXposed.R;

import java.util.List;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

import static android.widget.ImageView.ScaleType.CENTER;
import static android.widget.ImageView.ScaleType.CENTER_CROP;
import static android.widget.ImageView.ScaleType.FIT_CENTER;
import static android.widget.ImageView.ScaleType.FIT_XY;
import static com.jodelXposed.utils.Utils.dpToPx;

/**
 * Created by Admin on 21.11.2016.
 */

@SuppressWarnings("deprecation")
public class LayoutHooks {

    private XC_InitPackageResources.InitPackageResourcesParam resparam;
    private String MODULE_PATH;

    public LayoutHooks(XC_InitPackageResources.InitPackageResourcesParam resparam, String MODULE_PATH) {
        this.resparam = resparam;
        this.MODULE_PATH = MODULE_PATH;
    }

    public void addResources() {
        XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, resparam.res);

        //Add svg
        JodelResIDs.drawable_gallery_chooser = XResources.getFakeResId(modRes, R.drawable.ic_icon_gallery);
        resparam.res.setReplacement(JodelResIDs.drawable_gallery_chooser, modRes.fwd(R.drawable.ic_icon_gallery));

        JodelResIDs.ic_color_chooser = XResources.getFakeResId(modRes, R.drawable.ic_color_palette);
        resparam.res.setReplacement(JodelResIDs.ic_color_chooser, modRes.fwd(R.drawable.ic_color_palette));

        JodelResIDs.ic_jx_icon = XResources.getFakeResId(modRes, R.mipmap.ic_launcher);
        resparam.res.setReplacement(JodelResIDs.ic_jx_icon, modRes.fwd(R.mipmap.ic_launcher));

        JodelResIDs.ic_toggle_scale = XResources.getFakeResId(modRes, R.drawable.ic_toggle_scale);
        resparam.res.setReplacement(JodelResIDs.ic_toggle_scale, modRes.fwd(R.drawable.ic_toggle_scale));


        //Add layout
        JodelResIDs.layout_appcompatimageview = XResources.getFakeResId(modRes, R.layout.image_view_gallery_chooser);
        resparam.res.setReplacement(JodelResIDs.layout_appcompatimageview, modRes.fwd(R.layout.image_view_gallery_chooser));

        JodelResIDs.layout_color_picker = XResources.getFakeResId(modRes, R.layout.color_picker_layout);
        resparam.res.setReplacement(JodelResIDs.layout_color_picker, modRes.fwd(R.layout.color_picker_layout));

        //Replace google_play_services_version
        int resID = resparam.res.getIdentifier("google_play_services_version","integer","com.tellm.android.app");
        Log.d("Jodel","RESID: "+resID);
        resparam.res.setReplacement(resID,modRes.fwd(R.integer.google_play_services_version));

    }

    public void hook() {

        resparam.res.hookLayout(App.Companion.getPACKAGE_NAME(), "layout", "fragment_create_post", new XC_LayoutInflated() {
                @Override
                public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {

                    //Parent relative layout
                    RelativeLayout relativeLayoutParent = (RelativeLayout) liparam.view.findViewById(liparam.res.getIdentifier("cameraButton", "id", "com.tellm.android.app")).getParent();

                    //free the cameraButtonView from its parent
                    View cameraButton = liparam.view.findViewById(liparam.res.getIdentifier("cameraButton", "id", "com.tellm.android.app"));
                    relativeLayoutParent.removeView(cameraButton);
                    cameraButton.measure(0, 0);

                    //Create new layoutparams for the color chooser and gallery picker
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(cameraButton.getMeasuredWidth() + 2, cameraButton.getMeasuredHeight() + 2);

                    //Create a new LinearLayout
                    LinearLayout llNew = new LinearLayout(liparam.view.getContext());
                    llNew.setOrientation(LinearLayout.HORIZONTAL);
                    llNew.setGravity(Gravity.CENTER_HORIZONTAL);
                    LinearLayout.LayoutParams llNewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    llNew.setLayoutParams(llNewParams);

                    //type-casting from AppCompatImageView to AppCompatImageView wont work so we have to call the methods manually -.-
                    //Inflate the new gallery button, set image and apply layout params
                    View galleryButton = LayoutInflater.from(liparam.view.getContext()).inflate(JodelResIDs.layout_appcompatimageview, null, false);
                    XposedHelpers.callMethod(galleryButton, "setImageDrawable", liparam.res.getDrawable(JodelResIDs.drawable_gallery_chooser));
                    XposedHelpers.callMethod(galleryButton, "setLayoutParams", layoutParams);

                    //set tag for later usage, see ImageStuff.class
                    galleryButton.setTag("gallery_button");

                    //apply layout changes
                    galleryButton.requestLayout();

                    //Inflate view for color chooser and so on, see above for details
                    View colorChooserButton = LayoutInflater.from(liparam.view.getContext()).inflate(JodelResIDs.layout_appcompatimageview, null, false);
                    XposedHelpers.callMethod(colorChooserButton, "setImageDrawable", liparam.res.getDrawable(JodelResIDs.ic_color_chooser));
                    XposedHelpers.callMethod(colorChooserButton, "setLayoutParams", layoutParams);
                    colorChooserButton.setTag("color_chooser");
                    colorChooserButton.requestLayout();

                    llNew.addView(galleryButton);
                    llNew.addView(cameraButton);
                    llNew.addView(colorChooserButton);

                    llNew.requestLayout();

                    relativeLayoutParent.addView(llNew, 0);
                }
            }
        );

        resparam.res.hookLayout(App.Companion.getPACKAGE_NAME(), "layout", "fragment_photo_edit", new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                final ImageView imageView = (ImageView) liparam.view.findViewById(liparam.view.getResources().getIdentifier("image_edit_preview", "id", "com.tellm.android.app"));
                ImageButton geoFilterButton = (ImageButton) liparam.view.findViewById(liparam.view.getResources().getIdentifier("geo_filter_switch", "id", "com.tellm.android.app"));

                //Enable some hidden features
                geoFilterButton.setVisibility(View.VISIBLE);

                geoFilterButton.measure(0, 0);

                AppCompatImageButton toggle_scale_button = new AppCompatImageButton(liparam.view.getContext());
                toggle_scale_button.setImageDrawable(liparam.view.getResources().getDrawable(JodelResIDs.ic_toggle_scale));

                //TODO geofilter button and scale type button are offset

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(geoFilterButton.getMeasuredWidth() / 2, geoFilterButton.getMeasuredHeight() / 2);
                params.addRule(RelativeLayout.BELOW, liparam.view.getResources().getIdentifier("geo_filter_switch", "id", "com.tellm.android.app"));
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.setMargins(0, 0, dpToPx(17), dpToPx(34));

                toggle_scale_button.setLayoutParams(params);

                ((RelativeLayout) liparam.view).addView(toggle_scale_button);

                liparam.view.setBackgroundColor(Color.BLACK);

                toggle_scale_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (imageView.getScaleType()) {
                            case CENTER_CROP:
                                imageView.setScaleType(FIT_CENTER);
                                break;
                            case FIT_CENTER:
                                imageView.setScaleType(FIT_XY);
                                break;
                            case FIT_XY:
                                imageView.setScaleType(CENTER_CROP);
                                break;
                        }
                    }
                });
            }
        });

    }

    public static class JodelResIDs {
        public static int drawable_gallery_chooser;
        public static int ic_color_chooser;
        public static int layout_appcompatimageview;
        public static int ic_jx_icon;
        public static int layout_color_picker;
        public static int ic_toggle_scale;
    }
}
