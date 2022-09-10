package com.example.beaconfinalapp.LocationHelpers;

import java.io.Serializable;

public class LocationModel implements Serializable {
    private String address;

    private String name;
    private boolean isOpen;
    private LocationGeometry geometry;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public LocationGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(LocationGeometry geometry) {
        this.geometry = geometry;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
