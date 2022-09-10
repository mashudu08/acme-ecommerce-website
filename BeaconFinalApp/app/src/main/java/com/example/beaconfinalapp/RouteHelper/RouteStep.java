package com.example.beaconfinalapp.RouteHelper;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RouteStep {
    private double distance;
    private double duration;
    private LatLng startLocation;
    private LatLng endLocation;
    private List<LatLng> stepPolyline;

    private String travelMode;

    private String maneuver;

    private String htmlInstruction;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
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

    public List<LatLng> getStepPolyline() {
        return stepPolyline;
    }

    public void setStepPolyline(List<LatLng> stepPolyline) {
        this.stepPolyline = stepPolyline;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    public String getManeuver() {
        return maneuver;
    }

    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }

    public String getHtmlInstruction() {
        return htmlInstruction;
    }

    public void setHtmlInstruction(String htmlInstruction) {
        this.htmlInstruction = htmlInstruction;
    }
}
