package com.jodelXposed.models;

public class Location {
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = Float.parseFloat(String.valueOf(lat));
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = Float.parseFloat(String.valueOf(lng));
    }

    boolean active = false;
    public float lat = 0;
    public float lng = 0;
}
