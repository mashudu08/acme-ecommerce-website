package com.example.beaconfinalapp.LocationHelpers;

import java.io.Serializable;

public class LocationViewport implements Serializable {

    private Northeast northeast;
    private Southwest southwest;


    public LocationViewport(double neLat, double neLong, double seLat, double seLong) {


        this.northeast = new Northeast(neLat, neLong) ;
        this.southwest = new Southwest(seLat, seLong);
    }

    public Northeast getNortheast() {
        return northeast;
    }

    public Southwest getSouthwest() {
        return southwest;
    }


    public class Northeast implements Serializable{

        double latitude;
        double longitude;

        public Northeast(double latitude, double longitude) {
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

    private class Southwest implements Serializable{
        double latitude;
        double longitude;

        public Southwest(double latitude, double longitude) {
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
}
