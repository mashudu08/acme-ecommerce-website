package com.example.beaconfinalapp.LocationHelpers;

import java.io.Serializable;

public class PlaceLocation implements Serializable {

    double latitude;
    double longitude;

    public PlaceLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }


    public double getLongitude() {
        return longitude;
    }
}
