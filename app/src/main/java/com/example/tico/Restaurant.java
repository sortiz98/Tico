package com.example.tico;

import java.io.Serializable;

public class Restaurant implements Serializable {
    String name;
    String formattedAddress;
    String photoURL;
    String iconURL;
    String detailURL;
    String id;
    boolean open;

    public Restaurant(String name, String id, String photoURL, String detailURL) {
        this.name = name;
        this.id = id;
        this.photoURL = photoURL;
        this.detailURL = detailURL;
    }

    public Restaurant(String name, String formattedAddress, String photoURL, String iconURL, String id, boolean open) {
        this.name = name;
        this.formattedAddress = formattedAddress;
        this.photoURL = photoURL;
        this.iconURL = iconURL;
        this.id = id;
        this.open = open;
    }

    public String getName() { return this.name; }
    public String getAddress() {return this.formattedAddress; }
    public String getPhotoURL() {return this.photoURL; }
    public String getIconURL() {return this.iconURL; }
    public String getID() {return this.id; }
    public boolean open() {return this.open; }
}
