package com.jodelXposed.hooks;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Color;
import android.support.v7.widget.AppCompatImageButton;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jodelXposed.App;
import com.jodelXposed.R;
import com.jodelXposed.utils.Utils;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

import static android.view.View.GONE;
import static android.widget.ImageView.ScaleType.CENTER_CROP;
import static android.widget.ImageView.ScaleType.FIT_CENTER;
import static android.widget.ImageView.ScaleType.FIT_XY;
import static com.jodelXposed.utils.Utils.dpToPx;
import static com.jodelXposed.utils.Utils.getSystemContext;

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
        try {
            JodelResIDs.drawable_gallery_chooser = XResources.getFakeResId(modRes, R.drawable.ic_icon_gallery);
            resparam.res.setReplacement(JodelResIDs.drawable_gallery_chooser, modRes.fwd(R.drawable.ic_icon_gallery));

            JodelResIDs.ic_color_chooser = XResources.getFakeResId(modRes, R.drawable.ic_color_palette);
            resparam.res.setReplacement(JodelResIDs.ic_color_chooser, modRes.fwd(R.drawable.ic_color_palette));

            JodelResIDs.ic_launcher = XResources.getFakeResId(modRes, R.drawable.ic_launcher);
            resparam.res.setReplacement(JodelResIDs.ic_launcher, modRes.fwd(R.drawable.ic_launcher));

            JodelResIDs.ic_jx_icon = XResources.getFakeResId(modRes, R.drawable.ewok);
            resparam.res.setReplacement(JodelResIDs.ic_jx_icon, modRes.fwd(R.drawable.ewok));

            JodelResIDs.ic_toggle_scale = XResources.getFakeResId(modRes, R.drawable.ic_toggle_scale);
            resparam.res.setReplacement(JodelResIDs.ic_toggle_scale, modRes.fwd(R.drawable.ic_toggle_scale));

            JodelResIDs.ic_scroll = XResources.getFakeResId(modRes, R.drawable.ic_scroll);
            resparam.res.setReplacement(JodelResIDs.ic_scroll, modRes.fwd(R.drawable.ic_scroll));

            JodelResIDs.ic_edit = XResources.getFakeResId(modRes, R.drawable.ic_mode_edit_black_24dp);
            resparam.res.setReplacement(JodelResIDs.ic_edit, modRes.fwd(R.drawable.ic_mode_edit_black_24dp));

            JodelResIDs.ic_map_location = XResources.getFakeResId(modRes, R.drawable.ic_map_location);
            resparam.res.setReplacement(JodelResIDs.ic_map_location, modRes.fwd(R.drawable.ic_map_location));

            JodelResIDs.ic_information = XResources.getFakeResId(modRes, R.drawable.ic_information);
            resparam.res.setReplacement(JodelResIDs.ic_information, modRes.fwd(R.drawable.ic_information));
        } catch (Exception e) {
            if (e instanceof Resources.NotFoundException) {
                Toast.makeText(getSystemContext(), "Please reboot your device in order to use JodelXposed", Toast.LENGTH_LONG).show();
            } else {
                e.printStackTrace();
                Toast.makeText(getSystemContext(), "Something weired happened. Please open a issue on GitHub", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void fragment_create_post() {
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
                    AppCompatImageButton galleryButton = new AppCompatImageButton(liparam.view.getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(20, 20, 20, 20);
                    params.gravity = Gravity.RIGHT;
                    galleryButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    galleryButton.setLayoutParams(params);
                    XposedHelpers.callMethod(galleryButton, "setImageDrawable", liparam.res.getDrawable(JodelResIDs.drawable_gallery_chooser));
                    XposedHelpers.callMethod(galleryButton, "setLayoutParams", layoutParams);

                    //set tag for later usage, see ColorAndGalleryPicker.class
                    galleryButton.setTag("gallery_button");

                    //apply layout changes
                    galleryButton.requestLayout();

                    //Inflate view for color chooser and so on, see above for details
                    AppCompatImageButton colorChooserButton = new AppCompatImageButton(liparam.view.getContext());
                    colorChooserButton.setLayoutParams(params);
                    colorChooserButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
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
    }

    public void fragment_photo_edit() {
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

    public void fragment_post_detail() {
        resparam.res.hookLayout(App.Companion.getPACKAGE_NAME(), "layout", "fragment_post_detail", new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                Context ctx = liparam.view.getContext();
                ViewGroup parent = (ViewGroup) liparam.view.findViewById(liparam.res.getIdentifier("post_detail_container", "id", App.Companion.getPACKAGE_NAME()));

                View shadow_above_reply = liparam.view.findViewById(liparam.res.getIdentifier("shadow_above_reply", "id", App.Companion.getPACKAGE_NAME()));

                //Add new linear layout
                RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                rlParams.addRule(RelativeLayout.FOCUSABLES_TOUCH_MODE, RelativeLayout.TRUE);


                LinearLayout newLl = new LinearLayout(ctx);
                newLl.setId(XResources.getFakeResId("tv_fast_scroll_down"));
                newLl.setOrientation(LinearLayout.HORIZONTAL);
                newLl.setWeightSum(6);
                newLl.setLayoutParams(rlParams);

                //Define new LL below the post recyclerview
                RelativeLayout.LayoutParams paramsitemRefresh = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsitemRefresh.addRule(RelativeLayout.ABOVE, newLl.getId());
                View itemRefresh = liparam.view.findViewById(liparam.res.getIdentifier("itemRefresh", "id", "com.tellm.android.app"));
                itemRefresh.setLayoutParams(paramsitemRefresh);


                //find reply button and apply new layoutparams
                View reply_button = liparam.view.findViewById(liparam.res.getIdentifier("create_reply_button", "id", App.Companion.getPACKAGE_NAME()));
                reply_button.measure(0, 0);
                LinearLayout.LayoutParams rlParamsReplyButton = new LinearLayout.LayoutParams(Utils.getDisplayWidth() - dpToPx(80), dpToPx(70));

                reply_button.setLayoutParams(rlParamsReplyButton);
                //remove reply button from current parent
                ((RelativeLayout) reply_button.getParent()).removeView(reply_button);


                //Add a new placeholder view between reply and scroll down button
                LinearLayout.LayoutParams rlParamsPlaceholder = new LinearLayout.LayoutParams(3, ViewGroup.LayoutParams.MATCH_PARENT);
                View placeholder = new View(ctx);
                placeholder.setBackgroundColor(Color.LTGRAY);
                placeholder.setLayoutParams(rlParamsPlaceholder);


                //Add new fast scroll down button
                LinearLayout.LayoutParams rlParamsFastScrollDown = new LinearLayout.LayoutParams(dpToPx(80), ViewGroup.LayoutParams.MATCH_PARENT);
                rlParamsFastScrollDown.gravity = Gravity.CENTER;
                ImageView fast_scroll_down = new ImageView(ctx);
                fast_scroll_down.setTag("tag_fast_scroll_down");
                fast_scroll_down.setVisibility(GONE);
                fast_scroll_down.setPadding(0, Utils.dpToPx(20) - 2, 0, Utils.dpToPx(20) - 2);
                fast_scroll_down.setBackgroundColor(Color.WHITE);
                fast_scroll_down.setLayoutParams(rlParamsFastScrollDown);
                //set image in view
                XposedHelpers.callMethod(fast_scroll_down, "setImageDrawable", liparam.res.getDrawable(JodelResIDs.ic_scroll));

                //add all views to its new LL parent
                RelativeLayout.LayoutParams shadowParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 12);
                shadowParams.addRule(RelativeLayout.ABOVE, newLl.getId());
                shadow_above_reply.setLayoutParams(shadowParams);
                newLl.addView(reply_button);
                newLl.addView(placeholder);
                newLl.addView(fast_scroll_down);

                //add LL to parent RL
                parent.addView(newLl);
            }
        });
    }

    public void hook() {
        try {
            fragment_create_post();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            fragment_post_detail();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            fragment_photo_edit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class JodelResIDs {
        public static int drawable_gallery_chooser;
        public static int ic_color_chooser;
        public static int layout_appcompatimageview;
        public static int ic_jx_icon;
        public static int layout_color_picker;
        public static int ic_toggle_scale;
        public static int ic_scroll;
        public static int ic_edit;
        public static int ic_map_location;
        public static int ic_information;
        public static int ic_launcher;
    }
}
