package com.example.tico;

import java.io.Serializable;

public class Restaurant implements Serializable {
    String name;
    String language;
    String formattedAddress;
    String photoURL;
    String iconURL;
    String detailURL;
    String distanceURL;
    String id;
    double distance; // distance from user's location to the restaurant
    double time;
    boolean openNow;

    public Restaurant(String name, String language, String id, String photoURL, String detailURL, String distanceURL) {
        this.name = name;
        this.language = language;
        this.id = id;
        this.photoURL = photoURL;
        this.detailURL = detailURL;
        this.distanceURL = distanceURL;
    }


    /** A list of getter methods */
    public String getName() { return this.name; }
    public String getLanguage() { return this.language; }
    public String getAddress() {return this.formattedAddress; }
    public String getPhotoURL() {return this.photoURL; }
    public String getDetailURL() {return this.detailURL; }
    public String getDistanceURL() {return this.distanceURL;}
    public String getIconURL() {return this.iconURL; }
    public String getID() {return this.id; }
    public boolean open() {return this.openNow; }
    public double getDistance() {return this.distance; }
    public double getTime() {return this.time; }
}
