package com.jodelXposed.charliekelly.activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.jodelXposed.R;
import com.jodelXposed.charliekelly.asynctasks.GeocoderAsync;
import com.jodelXposed.krokofant.utils.Settings;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import static com.jodelXposed.krokofant.utils.Log.xlog;

public class SettingsActivity extends Activity implements View.OnClickListener, GeocoderAsync.OnGeoListener{

    private int PLACEPICKER_REQUEST = 0;
    private Settings mSettings = Settings.getInstance();
    private CheckBox chkIsActive;
    private Button btnSelectPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.chkIsActive = (CheckBox) findViewById(R.id.chk_is_active);
        this.btnSelectPosition = ((Button)findViewById(R.id.btn_select_position));

        this.btnSelectPosition.setOnClickListener(this);
        this.chkIsActive.setOnClickListener(this);

        try{
            mSettings.load();
        } catch (JSONException | IOException e){
            xlog(e.getMessage());
        }
        this.chkIsActive.setChecked(mSettings.isActive());
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();

        if(viewID == this.btnSelectPosition.getId())
            this.pickLocation();


        if(viewID == this.chkIsActive.getId()){
            try{
                mSettings.setActive(chkIsActive.isChecked());
                mSettings.setDoResetAuthenticated(true);
                mSettings.save();

                Toast.makeText(this, "Saved settings to file\n" + mSettings.toJson(), Toast.LENGTH_LONG).show();

            } catch (JSONException | IOException e){
                xlog(e.getMessage());
            }
        }
    }


    /**
     * Open a Place picker, zoomed in on coordinates from settings
     */
    private void pickLocation(){
        //Start place picker with these coordinates in the center
        double lat = mSettings.getLat();
        double lng = mSettings.getLng();

        xlog(String.format("Loaded latlng from Settings:Lat: %s, Lng: %s", lat, lng));

        PlacePicker.IntentBuilder i = new PlacePicker.IntentBuilder();
        i.setLatLngBounds(new LatLngBounds(
                new LatLng( (lat-0.20), (lng-0.20) ),
                new LatLng( (lat+0.20), (lng+0.20))
        ));


        try{
            xlog("Opening maps app");
            //Intent in = i.build((Activity)context);
            startActivityForResult(i.build(this), PLACEPICKER_REQUEST);
        }
        catch(Exception ex){
            xlog("Error opening maps:");
            xlog(ex.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        xlog("onActivityResult requestcode: " + requestCode);

        if(resultCode != RESULT_OK) {
            xlog("Error, resultCode: " + String.valueOf(resultCode));
            return;
        }

        if(requestCode == PLACEPICKER_REQUEST){
            xlog("Recieved data from placepicker activity");

            if(data == null){
                xlog("data was null");
                return;
            }

            Place place = PlacePicker.getPlace(this, data);
            this.setSettingsFromPlace(place);
        }
    }

    /**
     * Extract location properties from place object and insert to the Settings-object, then save settings to file
     * @param place
     */
    private void setSettingsFromPlace(Place place) {
        LatLng latlng = place.getLatLng();
        double lat = latlng.latitude;
        double lng = latlng.longitude;

        mSettings.setLat(lat);
        mSettings.setLng(lng);

        //Get location data on seperate thread, then call onGeoFinished
        new GeocoderAsync(lat, lng, this, this).execute();
    }

    @Override
    public void onGeoFinished(List<Address> addresses) {
        if(addresses == null){
            Toast.makeText(this, "No addresses nearby, unable to set location", Toast.LENGTH_SHORT).show();
            return;
        }

        int length = addresses.size();
        boolean save = false;

        //Find an adress that has all needed indexes
        for(int i = 0; i < length; i++){
            Address a = addresses.get(i);
            String locality = a.getLocality();
            String country = a.getCountryName();
            String countryCode = a.getCountryCode();

            if(locality == null){
                xlog("Locality was null");
                continue;
            }

            if(country == null){
                xlog("Country was null");
                continue;
            }

            if(countryCode == null){
                xlog("CountryCode was null");
                continue;
            }

            mSettings.setCity(locality);
            mSettings.setCountry(country);
            mSettings.setCountryCode(countryCode);
            mSettings.setDoResetAuthenticated(true);
            save = true;
            break;
        }

        //If we found an adress with all indexes, save to file
        if(save){
            try {
                mSettings.save();
                Toast.makeText(getApplicationContext(), "Location saved", Toast.LENGTH_SHORT).show();
            } catch (JSONException | IOException e) {
                xlog(e.getLocalizedMessage());
            }

        }else{
            Toast.makeText(getApplicationContext(), "Could not save location, try again", Toast.LENGTH_SHORT).show();
        }
    }
}
