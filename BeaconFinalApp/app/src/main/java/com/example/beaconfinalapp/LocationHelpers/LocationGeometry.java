package com.example.beaconfinalapp.LocationHelpers;

import java.io.Serializable;

public class LocationGeometry implements Serializable {


    public LocationGeometry(PlaceLocation location, LocationViewport viewport) {
        this.location = location;
        this.viewport = viewport;
    }

    private PlaceLocation location;
    private LocationViewport viewport;

    public PlaceLocation getLocation() {
        return location;
    }

    public LocationViewport getViewport() {
        return viewport;
    }
}
