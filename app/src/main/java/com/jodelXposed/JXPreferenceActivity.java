package com.jodelXposed;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.jodelXposed.models.Location;
import com.jodelXposed.models.UDI;
import com.jodelXposed.utils.Options;
import com.mypopsy.maps.StaticMap;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * Created by Admin on 14.10.2016.
 */

@SuppressWarnings("deprecation")
public class JXPreferenceActivity extends AppCompatPreferenceActivity implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    final Options options = Options.getInstance();
    Location location = options.getLocationObject();
    UDI udi = options.getUDIObject();

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.layout_jx_prefs);
        StaticMap map = new StaticMap().marker(location.getLat(),location.getLng()).size(1280, 480);
        ImageView ivMap = (ImageView) findViewById(R.id.ivMap);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        toolbar.setTitleTextColor(Color.BLACK);
        try {
            Picasso.with(this).load(String.valueOf(map.toURL().toURI())).placeholder(R.drawable.progress_animation).into(ivMap);
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }


        addPreferencesFromResource(R.xml.jx_prefs);
        findPreference("switch_location").setOnPreferenceChangeListener(this);
        findPreference("switch_udi").setOnPreferenceChangeListener(this);
        findPreference("change_udi").setOnPreferenceChangeListener(this);

        ((SwitchPreference)findPreference("switch_location")).setChecked(location.isActive());
        ((SwitchPreference)findPreference("switch_udi")).setChecked(udi.isActive());
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()){
            case "change_udi":
                editUDIDialog();
                break;
            default:
                break;
        }
        return true;
    }

    private void editUDIDialog() {
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()){
            case "switch_location":
                location.setActive(((SwitchPreference)preference).isChecked());
                options.save();
                break;
            case "switch_udi":
                udi.setActive(((SwitchPreference)preference).isChecked());
                options.save();
                break;
            default:
                break;
        }
        return true;
    }
}
