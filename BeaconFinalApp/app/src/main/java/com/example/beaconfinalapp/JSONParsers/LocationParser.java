package com.example.beaconfinalapp.JSONParsers;

import android.content.Context;
import android.util.Log;

import com.example.beaconfinalapp.LocationHelpers.LocationGeometry;
import com.example.beaconfinalapp.LocationHelpers.LocationModel;
import com.example.beaconfinalapp.LocationHelpers.LocationViewport;
import com.example.beaconfinalapp.LocationHelpers.PlaceLocation;
import com.example.beaconfinalapp.Network_Helper.NetworkUtil;
import com.example.beaconfinalapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocationParser implements iJSONParser{
    //This class is used to get the request from the Network util class
    //It will parse the JSON data and return a directions object where it was instantiated
    //It requires search parameters in order to construct the request
    //It requires an application context in rode3r to pass it to the network util


    //Location Parser TAG
    private static final String TAG = "LocationParser";

    //This list holds Location Models that have been parsed
    private List<LocationModel> locationModels = new ArrayList<LocationModel>();

    String response;

    public LocationParser(String response) {
        this.response = response;
        try{
            parseJson();
        }catch (Exception e){
            Log.e(TAG, "COULD NOT PARSE JSON (LOCATION)");
        }


    }


    //#region helper methods

    //this method will be used to create the object used to by the application
    // to process and display information about the searched place
    @Override
    public void parseJson() {

        //PlaceModel to be returned
        LocationModel model = new LocationModel();

        //Add the various attributes to the LocationModel
        //Attempt to parse the JSON String
        try {
            JSONObject root = new JSONObject(response);
            JSONArray jsonArray = root.getJSONArray("candidates");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                model.setName(jsonObject.getString("name"));
                model.setAddress(jsonObject.getString("formatted_address"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Attempt to process the Location and Viewport latitude and longitude from the geometry object
        try {
            JSONObject root = new JSONObject(response);
            JSONArray jsonArray = root.getJSONArray("candidates");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject geometry = jsonObject.getJSONObject("geometry");

                //Set the geometry of the Location Model
                model.setGeometry(new LocationGeometry(getPlaceLocation(geometry), getLocationViewport(geometry)));

                //Add this parsed object to the list to be return
                this.setLocationModel(model);
            }

        } catch (JSONException e) {
            Log.i(TAG, "Error processing location and viewport");
        }

    }

    //This method is used parse the Location geometry
    private PlaceLocation getPlaceLocation(JSONObject geometry) {

        PlaceLocation location = null;

        try {
            location = new PlaceLocation(geometry.getJSONObject("location").getDouble("lat"),
                    geometry.getJSONObject("location").getDouble("lng"));
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing the Places location");
        }

        return location;
    }

    //This method is used to parse the location viewport
    private LocationViewport getLocationViewport(JSONObject geometry) {

        LocationViewport viewport = null;

        try {
            JSONObject view = geometry.getJSONObject("viewport");
            JSONObject northwest = view.getJSONObject("northeast");
            JSONObject southwest = view.getJSONObject("southwest");
            viewport = new LocationViewport(
                    northwest.getDouble("lat"),
                    northwest.getDouble("lng"),
                    southwest.getDouble("lat"),
                    southwest.getDouble("lng")
            );
        } catch (JSONException e) {
            Log.d(TAG, "Error parsing locationviewport");
        }

        return viewport;
    }

    //Setter for setting this objects LocationModel
    private void setLocationModel(LocationModel model) {
        locationModels.add(model);
    }

    //Used for retrieving location models
    public List<LocationModel> getLocationModels() {
        return this.locationModels;
    }

//#endregion
}
