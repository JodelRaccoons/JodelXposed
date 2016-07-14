package com.jodelXposed.krokofant.utils;

import android.os.Environment;


import com.jodelXposed.BackgroundOperations;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.jodelXposed.krokofant.utils.Log.xlog;

public class Settings {
    public static String settingsPath = Environment.getExternalStorageDirectory() + "/.jodel-settings";

    public static Settings getInstance() {
        return singleton;
    }

    public static class DefaultSettings {
        public static final String city = "Heard and McDonald Islands";
        public static final String country = "Heard and McDonald Islands";
        public static final String countryCode = "AU";
        public static final double lat = -53.076499;
        public static final double lng = 73.37357;
        public static final boolean active = true;
        public static final String uid = "";
    }

    //private static final int VERSION = 1;
    private static final Settings singleton = new Settings();

    private double lat;
    private double lng;
    private String city;
    private String country;
    private String countryCode;
    private String uid;

    //If the module is active
    private boolean active;

    //If the settings is loaded
    private boolean isLoaded;


    private Settings() {
        this.isLoaded = false;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean b) {
        this.active = b;
    }

    /**
     * Load settings file. If file doesn't exist, it creates a new file with default values
     *
     * @throws JSONException
     * @throws IOException
     */
    public void load() throws JSONException, IOException {
        File file = new File(settingsPath);

        if (!file.exists()) {
            this.createDefaultFile(file);
        }

        String json = FileUtils.readFileToString(file, Charsets.UTF_8);
        xlog("Loaded file: " + json);

        try {
            JSONObject jsonObject = new JSONObject(json);

            this.city = jsonObject.getString("city");
            this.country = jsonObject.getString("country");
            this.countryCode = jsonObject.getString("countryCode");
            this.lat = jsonObject.getDouble("lat");
            this.lng = jsonObject.getDouble("lng");
            this.active = jsonObject.getBoolean("active");
            this.uid = jsonObject.getString("uid");

            this.isLoaded = true;

        } catch (Exception e) {
            xlog("Some indexes was not found, recreating file");

            this.createDefaultFile(file);
            this.load();
        }


    }


    /**
     * Save this object to the settings file
     *
     * @throws IOException
     * @throws JSONException
     */
    public void save() throws IOException, JSONException {
        JSONObject toFile = this.toJson();
        if (toFile == null) {
            xlog("could not convert settings object to JSON");
            return;
        }

        writeToFile(toFile.toString());
    }

    /**
     * Write string to passed file
     *
     * @param file
     * @param string
     */
    private void writeToFile(File file, String string) {
        //File file = new File(settingsPath);
        try {
            xlog(String.format("Writing %s to file", string));
            BackgroundOperations.currentlocation = string;

            FileOutputStream output = FileUtils.openOutputStream(file);
            output.write(string.getBytes());
            output.close();
        } catch (IOException e) {
            xlog("Could not write to file");
            xlog(e.getMessage());
        }
    }

    /**
     * Open file and write to it
     *
     * @param string
     */
    private void writeToFile(String string) {
        File file = new File(settingsPath);
        writeToFile(file, string);
    }


    /**
     * Create a default file
     *
     * @param file
     */
    public void createDefaultFile(File file) {
        xlog("Creating settings file");
        this.city = DefaultSettings.city;
        this.country = DefaultSettings.country;
        this.countryCode = DefaultSettings.countryCode;
        this.lat = DefaultSettings.lat;
        this.lng = DefaultSettings.lng;
        this.active = DefaultSettings.active;
        this.uid = DefaultSettings.uid;
        this.writeToFile(file, this.toJson().toString());
    }

    /**
     * Reset location
     */
    public void resetLocation() {
        try {
            this.city = DefaultSettings.city;
            this.country = DefaultSettings.country;
            this.countryCode = DefaultSettings.countryCode;
            this.lat = DefaultSettings.lat;
            this.lng = DefaultSettings.lng;
            this.save();
        } catch (IOException | JSONException e) {
            xlog("Failed to reset location");
        }
    }

    /**
     * Return this object as a json object
     *
     * @return null if failed
     */
    public JSONObject toJson() {
        try {
            JSONObject toFile = new JSONObject();
            toFile.put("active", this.active);
            toFile.put("city", this.city);
            toFile.put("country", this.country);
            toFile.put("countryCode", this.countryCode);
            toFile.put("lat", this.lat);
            toFile.put("lng", this.lng);
            toFile.put("uid", this.uid);

            return toFile;
        } catch (JSONException e) {
            return null;
        }
    }
}
