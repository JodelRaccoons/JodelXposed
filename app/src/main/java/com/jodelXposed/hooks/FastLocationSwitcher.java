package com.jodelXposed.hooks;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jodelXposed.App;
import com.jodelXposed.utils.Options;
import com.jodelXposed.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.view.View.GONE;
import static com.jodelXposed.utils.Utils.dpToPx;
import static com.jodelXposed.utils.Utils.getNewIntent;
import static com.jodelXposed.utils.Utils.getSystemContext;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findMethodsByExactParameters;

/**
 * Created by Admin on 26.01.2017.
 */

@SuppressWarnings("ResourceType")
public class FastLocationSwitcher {

    public FastLocationSwitcher(final XC_LoadPackage.LoadPackageParam lpparam) {
        final int[] viewIdBackup = new int[1];

        Class MainActivity = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.view.MainActivity", lpparam.classLoader);

        Method[] methods = findMethodsByExactParameters(MainActivity, void.class, View.class);

        findAndHookMethod(MainActivity, methods[0].getName(), View.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (Options.INSTANCE.getLocation().getOverrideHometown()) {
                    final Activity activity = (Activity) param.thisObject;
                    int id = activity.getResources().getIdentifier("feed_tab", "id", App.Companion.getPACKAGE_NAME());

                    final AlertDialog dialog = new AlertDialog.Builder(activity).create();

                    View view = (View) param.args[0];
                    if (view.getId() == id) {
                        viewIdBackup[0] = view.getId();

                        //Prevent initial pressing
                        //noinspection ResourceType
                        view.setId(123456);

                        LinearLayout rootLL = new LinearLayout(activity);
                        rootLL.setOrientation(LinearLayout.VERTICAL);

                        RelativeLayout headerParent = new RelativeLayout(activity);
                        headerParent.setLayoutTransition(new LayoutTransition());
                        headerParent.setId(123454);
                        headerParent.setGravity(Gravity.CENTER);
                        headerParent.setBackgroundColor(Color.parseColor("#FF9908"));


                        //**********************HEADER VIEW*****************************

                        final RelativeLayout headerView = new RelativeLayout(activity);


                        LinearLayout.LayoutParams headerLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        headerView.setPadding(0, dpToPx(24), 0, dpToPx(24));

                        headerView.setLayoutParams(headerLayoutParams);

                        headerView.setBackgroundColor(Color.parseColor("#FF9908"));


                        ImageView imageView = new ImageView(activity);
                        imageView.setId(123455);
                        RelativeLayout.LayoutParams ivLayoutParams = new RelativeLayout.LayoutParams(dpToPx(64), dpToPx(64));
                        ivLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                        imageView.setLayoutParams(ivLayoutParams);
                        imageView.setImageDrawable(activity.getResources().getDrawable(LayoutHooks.JodelResIDs.ic_map_location));

                        TextView headerTextView = new TextView(activity);
                        RelativeLayout.LayoutParams tvLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        tvLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                        tvLayoutParams.addRule(RelativeLayout.BELOW, 123455);
                        tvLayoutParams.setMargins(0, dpToPx(12), 0, 0);
                        headerTextView.setPadding(0, dpToPx(12), 0, 0);
                        headerTextView.setText("LOCATION SWITCH");
                        headerTextView.setLayoutParams(tvLayoutParams);

                        headerView.addView(imageView);
                        headerView.addView(headerTextView);

                        headerParent.addView(headerView);

                        //****************HELPER VIEW****************************


                        final LinearLayout helpLinearLayout = new LinearLayout(activity);
                        helpLinearLayout.setOrientation(LinearLayout.VERTICAL);
                        helpLinearLayout.setBackgroundColor(Color.parseColor("#FF9908"));
                        RelativeLayout.LayoutParams helpLinearLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        helpLinearLayoutParams.setMargins(dpToPx(18), dpToPx(18), dpToPx(18), dpToPx(18));
                        helpLinearLayoutParams.addRule(RelativeLayout.BELOW, 123454);
                        helpLinearLayout.setLayoutParams(helpLinearLayoutParams);


                        ImageView helpImageView = new ImageView(activity);
                        LinearLayout.LayoutParams helpImageViewLayoutParams = new LinearLayout.LayoutParams(dpToPx(36), dpToPx(36));
                        helpImageViewLayoutParams.setMargins(0, 0, 0, dpToPx(12));
                        helpImageViewLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                        helpImageView.setLayoutParams(helpImageViewLayoutParams);
                        helpImageView.setImageDrawable(activity.getResources().getDrawable(LayoutHooks.JodelResIDs.ic_information));


                        final TextView helpDetail = new TextView(activity);
                        LinearLayout.LayoutParams helpLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        helpLayoutParams.setMargins(dpToPx(12), 0, dpToPx(12), dpToPx(12));
                        helpDetail.setTypeface(Typeface.SANS_SERIF);
                        helpDetail.setText(
                            "To switch to a city, just select it by clicking it. " +
                                "If you want to change the cities in here, long press the one you want to change and select a new one in the location Picker.");

                        helpLinearLayout.addView(helpImageView);
                        helpLinearLayout.addView(helpDetail);

                        helpLinearLayout.setVisibility(GONE);

                        headerParent.addView(helpLinearLayout);

                        headerParent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (headerView.getVisibility() == GONE) {
                                    headerView.setVisibility(View.VISIBLE);
                                    helpLinearLayout.setVisibility(View.GONE);
                                } else {
                                    headerView.setVisibility(GONE);
                                    helpLinearLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                        rootLL.addView(headerParent);


                        for (int i = 1; i <= 4; i++) {
                            LinearLayout.LayoutParams subLLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            subLLP.gravity = Gravity.CENTER;
                            subLLP.setMargins(0, dpToPx(12), 0, dpToPx(12));

                            TextView textView = new TextView(activity);
                            textView.setGravity(Gravity.CENTER);

                            Object receiver = Options.INSTANCE.getLocation();
                            Method m = Options.INSTANCE.getLocation().getClass().getMethod("getNamefastChange" + i);
                            String text = String.valueOf(m.invoke(receiver)).isEmpty() ? "Long press to set" : String.valueOf(m.invoke(receiver));
                            textView.setText(text);

                            final int finalI = i;
                            textView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    Toast.makeText(activity, "Starting location picker...", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                    getSystemContext()
                                        .startActivity(getNewIntent("utils.Picker")
                                            .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                            .putExtra("choice", 2).putExtra("fastChange", finalI));
                                    return true;
                                }
                            });

                            final int finalI1 = i;
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Object receiver = Options.INSTANCE.getLocation();
                                    try {
                                        Method m = Options.INSTANCE.getLocation().getClass().getMethod("getLatfastChange" + finalI1);
                                        Method m2 = Options.INSTANCE.getLocation().getClass().getMethod("getLngfastChange" + finalI1);

                                        Options.INSTANCE.getLocation().setLat((Double) m.invoke(receiver));
                                        Options.INSTANCE.getLocation().setLng((Double) m2.invoke(receiver));
                                        Options.INSTANCE.save();

                                        dialog.dismiss();

                                        Utils.updateFeedAndLocation(lpparam, Options.INSTANCE.getLocation().getLat(), Options.INSTANCE.getLocation().getLng());
                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            textView.setLayoutParams(subLLP);
                            rootLL.addView(textView);

                            if (i != 4) {
                                View divider = new View(activity);
                                LinearLayout.LayoutParams dividerLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);
                                divider.setBackgroundColor(Color.LTGRAY);
                                divider.setLayoutParams(dividerLayoutParams);
                                rootLL.addView(divider);
                            }
                        }

                        dialog.setView(rootLL);
                        dialog.show();
                    }
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (Options.INSTANCE.getLocation().getOverrideHometown()) {
                    ((View) param.args[0]).setId(viewIdBackup[0]);
                }
            }
        });
    }
}
