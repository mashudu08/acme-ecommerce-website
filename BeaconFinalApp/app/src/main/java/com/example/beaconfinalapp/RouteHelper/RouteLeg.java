package com.example.beaconfinalapp.RouteHelper;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RouteLeg {
    private double distance;
    private String strDistance;
    private double duration;
    private LatLng startLocation;
    private LatLng endLocation;
    private String startAddress;
    private String endAddress;
    private String trafficSpeedEntry; //TODO: Include speed limit parameter in url construct
    //TODO: Include optional in-between destinations


    List<RouteStep> steps;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getStrDistance() {
        return strDistance;
    }

    public void setStrDistance(String strDistance) {
        this.strDistance = strDistance;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public LatLng getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LatLng startLocation) {
        this.startLocation = startLocation;
    }

    public LatLng getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LatLng endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public String getTrafficSpeedEntry() {
        return trafficSpeedEntry;
    }

    public void setTrafficSpeedEntry(String trafficSpeedEntry) {
        this.trafficSpeedEntry = trafficSpeedEntry;
    }


    public List<RouteStep> getSteps() {
        return steps;
    }

    public void setSteps(List<RouteStep> steps) {
        this.steps = steps;
    }
}
