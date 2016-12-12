package com.jodelXposed.hooks;

import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Color;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jodelXposed.App;
import com.jodelXposed.R;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

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
    }

    public void hook() {

        resparam.res.hookLayout(App.Companion.getPACKAGE_NAME(), "layout", "fragment_create_post", new XC_LayoutInflated() {
                @Override
                public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                    /*
                    * child 0: cameraButton
                    * child 1: galleryPicker
                    * */

                    RelativeLayout llParent = (RelativeLayout) liparam.view.findViewById(liparam.res.getIdentifier("cameraButton", "id", "com.tellm.android.app")).getParent();

                    //type-casting from AppCompatImageView to AppCompatImageView wont work so we have to call the methods manually -.-
                    LayoutInflater.from(liparam.view.getContext()).inflate(JodelResIDs.layout_appcompatimageview, llParent, true);
                    XposedHelpers.callMethod(llParent.getChildAt(2), "setImageDrawable", liparam.res.getDrawable(JodelResIDs.drawable_gallery_chooser));

                    //due to wrap content, we have to measure the cameraButton and apply the measurements to the galleryButton
                    llParent.getChildAt(0).measure(0, 0);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(llParent.getChildAt(0).getMeasuredWidth() + 2, llParent.getChildAt(0).getMeasuredHeight() + 2);
                    layoutParams.addRule(RelativeLayout.LEFT_OF, llParent.getChildAt(0).getId());
                    XposedHelpers.callMethod(llParent.getChildAt(2), "setLayoutParams", layoutParams);

                    //set tag for later usage, see ImageStuff.class
                    llParent.getChildAt(2).setTag("gallery_button");

                    //apply layout changes
                    llParent.getChildAt(2).requestLayout();


                    View appCompatImageView = LayoutInflater.from(liparam.view.getContext()).inflate(JodelResIDs.layout_appcompatimageview, null, false);
                    XposedHelpers.callMethod(appCompatImageView, "setImageDrawable", liparam.res.getDrawable(JodelResIDs.ic_color_chooser));
                    RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(llParent.getChildAt(0).getMeasuredWidth() + 2, llParent.getChildAt(0).getMeasuredHeight() + 2);
                    layoutParams2.addRule(RelativeLayout.BELOW, llParent.getChildAt(0).getId());
                    layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    XposedHelpers.callMethod(appCompatImageView, "setLayoutParams", layoutParams2);
                    appCompatImageView.requestLayout();
                    appCompatImageView.setTag("color_chooser");

                    llParent.addView(appCompatImageView, 0);
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
