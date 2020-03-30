package com.godknows.covid19;

public class Tracking {
    private String email,lat,lng,uid;

    public Tracking(){

    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public String getLng() {
        return lng;
    }

    public String getLat() {
        return lat;
    }

    public String getUid() {
        return uid;
    }

    public Tracking(String email, String uid, String lat, String lng) {
        this.email = email;
        this.lat = lat;
        this.lng = lng;
        this.uid = uid;
    }
}
