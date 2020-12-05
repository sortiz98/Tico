package com.example.tico;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Restaurant implements Serializable {
    String name; // name of the restaurant
    String language; // current user language
    String formattedAddress; // address
    String photoURL; // url for the first photo
    String detailURL;
    String distanceURL;
    String id; // place id
    double distance; // distance from user's location to the restaurant
    double time; // time from user's location to the restaurant
    boolean openNow; // whether the restaurant is open
    int cuisineFlagRes;

    public Restaurant(String name, String language, String id, String photoURL, String detailURL, String distanceURL, int  cuisineFlagRes) {
        this.name = name;
        this.language = language;
        this.formattedAddress = "";
        this.photoURL = photoURL;
        this.detailURL = detailURL;
        this.distanceURL = distanceURL;
        this.id = id;
        this.distance = 0.0;
        time = 0.0;
        this.openNow = false;
        this.cuisineFlagRes = cuisineFlagRes;
    }


    /** A list of getter methods */
    public String getName() {return this.name; }
    public String getLanguage() {return this.language; }
    public String getAddress() {return this.formattedAddress; }
    public String getPhotoURL() {return this.photoURL; }
    public String getDetailURL() {return this.detailURL; }
    public String getDistanceURL() {return this.distanceURL;}
    public String getID() {return this.id; }
    public boolean open() {return this.openNow; }
    public double getDistance() {
        return this.distance;
    }
    public double getTime() {return this.time; }

    public int getCuisineFlagRes() {
        return this.cuisineFlagRes;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setTime(double time) {this.time = time;}
}
