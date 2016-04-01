package com.jodelXposed.charliekelly.asynctasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.jodelXposed.krokofant.utils.Log.xlog;

/**
 * Created by charliekelly2 on 23/01/2016.
 */
public class GeocoderAsync extends AsyncTask<Void, Void, List<Address>> {
    public interface OnGeoListener{
        public void onGeoFinished(List<Address> addresses);
    }

    private OnGeoListener mListener;
    private Context mContext;
    private double mLat;
    private double mLng;

    public GeocoderAsync(double lat, double lng, OnGeoListener listener, Context context) {
        this.mListener = listener;
        this.mContext = context;
        this.mLat = lat;
        this.mLng = lng;
    }

    @Override
    protected List<Address> doInBackground(Void... params) {

        try {
            Geocoder geocoder = new Geocoder(this.mContext, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(mLat, mLng, 5);

            return addresses;
        } catch(IOException e) {
            xlog("Exception");
            xlog(e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Address> addresses) {
        super.onPostExecute(addresses);
        this.mListener.onGeoFinished(addresses);
    }
}