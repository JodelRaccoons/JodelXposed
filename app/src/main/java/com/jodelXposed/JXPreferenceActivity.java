package com.jodelXposed;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.SyncStateContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jodelXposed.models.Location;
import com.jodelXposed.utils.AppCompatPreferenceActivity;
import com.jodelXposed.utils.Options;
import com.jodelXposed.utils.Picker;

/**
 * Created by Admin on 14.10.2016.
 */

@SuppressWarnings("deprecation")
public class JXPreferenceActivity extends AppCompatPreferenceActivity implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener, OnMapReadyCallback {

    final Options options = Options.INSTANCE;

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.layout_jx_prefs);
        setupToolbar();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        addPreferencesFromResource(R.xml.jx_prefs);
        findPreference("switch_location").setOnPreferenceChangeListener(this);
        findPreference("change_location").setOnPreferenceClickListener(this);

        ((SwitchPreference)findPreference("switch_location")).setChecked(options.getLocation().getActive());
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_back_button);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()){
            case "change_location":
                startActivity(new Intent(this, Picker.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("choice",1));
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()){
            case "switch_location":
                if(newValue instanceof Boolean){
                    options.getLocation().setActive((Boolean)newValue);
                }
                options.save();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Location location = options.getLocation();
        LatLng mLatLng = new LatLng(location.getLat(), location.getLng());
        googleMap.addMarker(new MarkerOptions()
            .position(mLatLng)
            .title("Spoofed location"));
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng,12));

    }
}
