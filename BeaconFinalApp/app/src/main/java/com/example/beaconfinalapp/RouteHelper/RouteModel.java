package com.example.beaconfinalapp.RouteHelper;

import com.google.android.gms.maps.model.Polyline;

import java.util.List;

public class RouteModel {

    private  RouteBounds bounds; //Container (NE, SW) lat and Long
    private List<RouteLeg> routeLegs;
    private Polyline polyLineOverView;
    private List<RouteWarning> warnings;
    private String routeSummar;

    public RouteBounds getBounds() {
        return bounds;
    }

    public void setBounds(RouteBounds bounds) {
        this.bounds = bounds;
    }

    public List<RouteLeg> getRouteLegs() {
        return routeLegs;
    }

    public void setRouteLegs(List<RouteLeg> routeLegs) {
        this.routeLegs = routeLegs;
    }

    public Polyline getPolyLineOverView() {
        return polyLineOverView;
    }

    public void setPolyLineOverView(Polyline polyLineOverView) {
        this.polyLineOverView = polyLineOverView;
    }

    public List<RouteWarning> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<RouteWarning> warnings) {
        this.warnings = warnings;
    }

    public String getRouteSummar() {
        return routeSummar;
    }

    public void setRouteSummar(String routeSummar) {
        this.routeSummar = routeSummar;
    }

}
