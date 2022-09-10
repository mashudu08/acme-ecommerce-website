package com.example.beaconfinalapp.JSONParsers;

import android.util.Log;

import com.example.beaconfinalapp.RouteHelper.RouteLeg;
import com.example.beaconfinalapp.RouteHelper.RouteModel;
import com.example.beaconfinalapp.RouteHelper.RouteStep;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RouteParser implements iJSONParser{
    //This class is used to get the request from the Network util class
    //It will parse the JSON data and return a directions object where it was instantiated
    //It requires search parameters in order to construct the request
    //It requires an application context in rode3r to pass it to the network util

    //Route Parser TAG
    private static final String TAG = "RouteParser";

    //This list holds Route Models that have been parsed
    RouteModel route;

    //Response string
    String response;

    public RouteParser(String response){
        this.response = response;
        try{
            parseJson();
        }catch (Exception e){
            Log.e(TAG, "COULD NOT PARSE JSON (ROUTE)");
        }
    }

    //JSON PARSER INTERFACE OVERRIDE
    @Override
    public void parseJson() {

        //Route model to be returned
        RouteModel model = new RouteModel();

        //Add the various attribute to the location model
        //Attempt to parse JSON String

        try{
            JSONObject root = new JSONObject(response);

            //Set the route legs
            model.setRouteLegs(getRouteLegs(root.getJSONArray("routes")));
            setRouteModel(model);

        }catch (JSONException e){
            Log.e(TAG, "COULD NOT PARSE JSON");
        }
    }

    //#region helper methods to help parse JSON

    //Extract the route legs
    private List<RouteLeg> getRouteLegs(JSONArray root){

        List<RouteLeg> routeLegs = new ArrayList<RouteLeg>();

        //Attempt to extract the legs
        try{
            for(int i = 0; i < root.length(); i++){

                JSONObject routeObject = root.getJSONObject(i);
                JSONArray legArray = routeObject.getJSONArray("legs");
                for(int x = 0; x < legArray.length(); x++){
                    RouteLeg leg = new RouteLeg();
                    JSONObject jsonObject = legArray.getJSONObject(x);

                    //get distance
                    leg.setDistance(jsonObject.getJSONObject("distance").getDouble("value"));

                    //Get duration
                    leg.setDuration(jsonObject.getJSONObject("duration").getDouble("value"));

                    //Get end location
                    leg.setEndAddress(jsonObject.getString("end_address"));

                    //Get start address
                    leg.setStartAddress(jsonObject.getString("start_location"));

                    //Get start location
                    leg.setStartLocation(new LatLng(jsonObject.getJSONObject("start_location").getDouble("lat")
                            ,jsonObject.getJSONObject("start_location").getDouble("lng")));

                    //Get end location
                    leg.setEndLocation(new LatLng(jsonObject.getJSONObject("end_location").getDouble("lat")
                            ,jsonObject.getJSONObject("end_location").getDouble("lng")));

                    //Get the route steps
                    leg.setSteps(getSteps(jsonObject.getJSONArray("steps")));
                    routeLegs.add(leg);
                }

            }
        }catch (JSONException e){
            Log.e(TAG, "COULD NOT EXTRACT ROUTE LEGS");
        }

        return routeLegs;
    }

    //Extract the step from the route leg
    private List<RouteStep> getSteps(JSONArray jsonArray) {
        List<RouteStep> routeSteps = new ArrayList<RouteStep>();

        for (int i = 0; i < jsonArray.length(); i++) {

            RouteStep step = new RouteStep();

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //Get the distance
                step.setDistance(jsonObject.getJSONObject("distance").getDouble("value"));

                //Get the duration
                step.setDuration(jsonObject.getJSONObject("duration").getDouble("value"));

                //Get the end location
                step.setEndLocation(new LatLng(jsonObject.getJSONObject("end_location").getDouble("lat")
                        , jsonObject.getJSONObject("end_location").getDouble("lng")));


                //Get the start location
                step.setStartLocation(new LatLng(jsonObject.getJSONObject("start_location").getDouble("lat")
                        , jsonObject.getJSONObject("end_location").getDouble("lng")));

                //Get the polyline value
                //Extract value from json string response then convert to latlng value
                List<LatLng> stepPolylines = PolyUtil.decode((jsonObject.getJSONObject("polyline").getString("points")));

                step.setStepPolyline(stepPolylines);

                routeSteps.add(step);

            } catch (JSONException e) {
                Log.e(TAG, "COULD NOT EXTRACT ROUTE STEP");
            }

        }

        return routeSteps;
    }

    private void setRouteModel(RouteModel route){
        this.route = route;
    }

    //Return the route list
    public RouteModel getRoute(){
        return this.route;
    }

    //#endregion
}
