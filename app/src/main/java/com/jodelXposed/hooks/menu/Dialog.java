package com.jodelXposed.hooks.menu;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jodelXposed.hooks.LayoutHooks;
import com.jodelXposed.hooks.helper.EventBus;
import com.jodelXposed.utils.Utils;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import es.dmoral.prefs.Prefs;

import static android.app.AlertDialog.Builder;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.widget.ListPopupWindow.WRAP_CONTENT;
import static com.jodelXposed.hooks.helper.Activity.getMain;
import static com.jodelXposed.utils.Utils.dpToPx;
import static de.robv.android.xposed.XposedHelpers.newInstance;

/**
 * Created by Admin on 03.04.2017.
 */

@SuppressWarnings("ResourceType")
public class Dialog {

    public static void showHashtagDialog(final XC_LoadPackage.LoadPackageParam lpparam) {
        final Activity activity = getMain();

        AlertDialog dialog = new Builder(activity).create();
        LinearLayout rootLL = new LinearLayout(activity);
        rootLL.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout headerParent = new RelativeLayout(activity);
        headerParent.setLayoutTransition(new LayoutTransition());
        headerParent.setId(123454);
        headerParent.setGravity(Gravity.CENTER);
        headerParent.setBackgroundColor(Color.parseColor("#FF9908"));


        //**********************HEADER VIEW*****************************

        RelativeLayout headerView = new RelativeLayout(activity);


        LinearLayout.LayoutParams headerLayoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headerView.setPadding(0, dpToPx(12), 0, dpToPx(24));

        headerView.setLayoutParams(headerLayoutParams);

        headerView.setBackgroundColor(Color.parseColor("#FF9908"));


        ImageView imageView = new ImageView(activity);
        imageView.setId(123455);
        RelativeLayout.LayoutParams ivLayoutParams = new RelativeLayout.LayoutParams(dpToPx(64), dpToPx(64));
        ivLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        ivLayoutParams.bottomMargin = Utils.dpToPx(12);
        imageView.setLayoutParams(ivLayoutParams);
        imageView.setImageDrawable(activity.getResources().getDrawable(LayoutHooks.JodelResIDs.ic_hashtag));
        imageView.setPadding(0, 0, 0, Utils.dpToPx(12));

        TextView headerTextView = new TextView(activity);
        RelativeLayout.LayoutParams tvLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        tvLayoutParams.addRule(RelativeLayout.BELOW, 123455);
        tvLayoutParams.setMargins(0, dpToPx(12), 0, 0);
        headerTextView.setPadding(0, dpToPx(12), 0, 0);
        headerTextView.setText("SEARCH FOR YOUR \r\nHASHTAG");
        headerTextView.setGravity(Gravity.CENTER);
        headerTextView.setLayoutParams(tvLayoutParams);

        headerView.addView(imageView);
        headerView.addView(headerTextView);

        headerParent.addView(headerView);

        //****************HELPER VIEW****************************

        rootLL.addView(headerParent);


        final EditText editText = new EditText(activity);
        editText.setMaxLines(1);
        editText.setHint("#jhj");

        LinearLayout searchLinearLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams searchllp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        searchllp.setMargins(Utils.dpToPx(14), Utils.dpToPx(8), Utils.dpToPx(8), Utils.dpToPx(14));
        searchLinearLayout.setLayoutParams(searchllp);

        LinearLayout.LayoutParams editTextLP = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        editText.setLayoutParams(editTextLP);

        searchLinearLayout.addView(editText);
        rootLL.addView(searchLinearLayout);


        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EventBus.post(newInstance(EventBus.Events.HideKeyboardEvent));

                Class FeedFragment = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.features.feed.FeedFragment", lpparam.classLoader);

                Object mFeedFragment = XposedHelpers.callStaticMethod(FeedFragment, "HM");

                Bundle bundle = new Bundle();
                bundle.putString("tag", "hashtag");
                bundle.putString("hashtag", editText.getText().toString().replace("#", ""));
                XposedHelpers.callMethod(mFeedFragment, "setArguments", bundle);

                Object fragmentManager = XposedHelpers.callMethod(activity, "getSupportFragmentManager");
                Object fragmentTransaction = XposedHelpers.callMethod(fragmentManager, "aP");
                Object postDetailContainerId = activity.getResources().getIdentifier("post_detail_container", "id", "com.tellm.android.app");
                fragmentTransaction = XposedHelpers.callMethod(fragmentTransaction, "b", postDetailContainerId, mFeedFragment, "hashtagFeed");
                fragmentTransaction = XposedHelpers.callMethod(fragmentTransaction, "d", "hashtagFeed");
                XposedHelpers.callMethod(fragmentTransaction, "commit");
            }
        });

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EventBus.post(newInstance(EventBus.Events.HideKeyboardEvent));
                dialog.dismiss();
            }
        });

        dialog.setView(rootLL);
        dialog.show();

        EventBus.post(newInstance(EventBus.Events.ShowKeyboardEvent, editText));

    }

    public static void showDateTimeDialog(final XC_LoadPackage.LoadPackageParam lpparam) {
        final Activity activity = getMain();
        String[] arr = new String[]{"HH:MM:SS", "Jodel + HH:MM:SS", "YYYY:MM:DD HH:MM:SS", "Jodel default"};

        final AlertDialog dialog = new Builder(activity).create();
        LinearLayout rootLL = new LinearLayout(activity);
        rootLL.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout headerParent = new RelativeLayout(activity);
        headerParent.setLayoutTransition(new LayoutTransition());
        headerParent.setId(123454);
        headerParent.setGravity(Gravity.CENTER);
        headerParent.setBackgroundColor(Color.parseColor("#FF9908"));


        //**********************HEADER VIEW*****************************

        RelativeLayout headerView = new RelativeLayout(activity);


        LinearLayout.LayoutParams headerLayoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headerView.setPadding(0, dpToPx(12), 0, dpToPx(24));

        headerView.setLayoutParams(headerLayoutParams);

        headerView.setBackgroundColor(Color.parseColor("#FF9908"));


        ImageView imageView = new ImageView(activity);
        imageView.setId(123455);
        RelativeLayout.LayoutParams ivLayoutParams = new RelativeLayout.LayoutParams(dpToPx(64), dpToPx(64));
        ivLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        ivLayoutParams.bottomMargin = Utils.dpToPx(12);
        imageView.setLayoutParams(ivLayoutParams);
        imageView.setImageDrawable(activity.getResources().getDrawable(LayoutHooks.JodelResIDs.ic_circular_clock));

        TextView headerTextView = new TextView(activity);
        RelativeLayout.LayoutParams tvLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        tvLayoutParams.addRule(RelativeLayout.BELOW, 123455);
        tvLayoutParams.setMargins(0, dpToPx(24), 0, 0);
        headerTextView.setPadding(0, dpToPx(12), 0, 0);
        headerTextView.setText("CHOOSE YOUR\r\nTIME FORMATTING");
        headerTextView.setGravity(Gravity.CENTER);
        headerTextView.setLayoutParams(tvLayoutParams);

        headerView.addView(imageView);
        headerView.addView(headerTextView);

        headerParent.addView(headerView);

        //****************HELPER VIEW****************************

        rootLL.addView(headerParent);


        for (int i = 0; i < arr.length; i++) {
            LinearLayout.LayoutParams subLLP = new LinearLayout.LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            subLLP.gravity = Gravity.CENTER;

            TextView textView = new TextView(activity);
            textView.setGravity(Gravity.CENTER);
            textView.setText(arr[i]);
            textView.setPadding(0, dpToPx(12), 0, dpToPx(12));

            if (Prefs.with(activity).readInt("timeFormat") == i + 1)
                textView.setBackgroundColor(Color.parseColor("#D3D3D3"));

            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Prefs.with(activity).writeInt("timeFormat", finalI + 1);
                    dialog.dismiss();
                    com.jodelXposed.utils.TSnackbar.make(lpparam, "Refresh your feed to apply changes!");
                }
            });
            textView.setLayoutParams(subLLP);
            rootLL.addView(textView);

            if (i != 4) {
                View divider = new View(activity);
                LinearLayout.LayoutParams dividerLayoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, 3);
                divider.setBackgroundColor(Color.LTGRAY);
                divider.setLayoutParams(dividerLayoutParams);
                rootLL.addView(divider);
            }
        }

        dialog.setView(rootLL);
        dialog.show();

    }

}
