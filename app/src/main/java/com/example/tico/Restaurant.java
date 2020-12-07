package com.example.tico;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Pair;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.collect.ArrayListMultimap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    int score;
    Map<String, Object> info;
    int authScore;
    int totalScore;
    List<String> reviews;
    List<Integer> reviewScores;
    String restaurantId;
    Double lat;
    Double lng;




    public Restaurant(String name, String language, String id, String photoURL, String detailURL, String distanceURL, int cuisineFlagRes, Double lat, Double lng) {
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
        this.score = 0;

        this.reviewScores = new ArrayList<>();
        this.reviews = new ArrayList<>();
        this.lat = lat;
        this.lng = lng;
    }

    public void setInfo(String restaurantId, Map<String, Object> info) {
        this.restaurantId = restaurantId;
        this.info = info;
        if (info.containsKey("authScore")){
            this.authScore = Integer.parseInt((String) info.get("authScore"));
        } else {
            this.authScore = 0;
        }
        if (info.containsKey("totalScore")){
            this.totalScore = Integer.parseInt((String) info.get("totalScore"));
        } else {
            this.totalScore = 0;
        }
    }

    public void addReview(int score, String reviewText) {
        this.reviews.add(reviewText);
        this.reviewScores.add(score);
    }

    public void refreshScore() {
        int authScore = this.authScore;
        int total = this.totalScore;
        if (this.reviewScores != null) {
            for (int reviewScore : this.reviewScores) {
                total++;
                authScore += reviewScore;
            }
        }
        if (total > 0) {
            this.score = authScore * 100 / total;
        } else {
            this.score = 0;
        }
    }

    public void rateAuthentic() {
        this.authScore++;
        this.totalScore++;
        refreshScore();
    }

    public void rateInauthentic() {
        this.totalScore++;
        refreshScore();
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


    public int getScore() {
        refreshScore();
        return this.score;
    }
    public int getTotalScore() { return this.totalScore + this.reviewScores.size(); }
    public String getRestaurantId() { return this.restaurantId; }
    public String getAuthScoreString() { return Integer.toString(this.authScore); }
    public String getTotalScoreString() { return Integer.toString(this.totalScore); }


}
