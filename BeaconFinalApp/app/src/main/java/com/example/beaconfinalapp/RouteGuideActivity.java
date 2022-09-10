package com.example.beaconfinalapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.beaconfinalapp.JSONParsers.RouteParser;
import com.example.beaconfinalapp.LocationHelpers.LocationModel;
import com.example.beaconfinalapp.Network_Helper.NetworkUtil;
import com.example.beaconfinalapp.R;
import com.example.beaconfinalapp.RouteHelper.RouteLeg;
import com.example.beaconfinalapp.RouteHelper.RouteModel;
import com.example.beaconfinalapp.RouteHelper.RouteStep;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RouteGuideActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationModel model;
    LocationModel[] models;
    private static final String TAG = "RouteActivity";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location userLocation;
    List<LatLng> polyLinePoints = null;
    boolean locationPermissionGranted;

    //Views
    Button btnGetRoute, btnStartGuidance;
    RelativeLayout navigationMenu;
    TextView txtDestination, txtDrivingEta, txtDistance;


    // The entry point to the Fused Location Provider.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_route_guide);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation(fusedLocationProviderClient);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnGetRoute = findViewById(R.id.btnGetDirections);
        navigationMenu = findViewById(R.id.navigationMenu);
        txtDrivingEta = findViewById(R.id.txtDrivingEta);
        txtDestination = findViewById(R.id.txtDestination);
        btnStartGuidance = findViewById(R.id.btnStartGuidance);
        txtDistance = findViewById(R.id.distance);

        btnGetRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMap();
            }
        });

        btnStartGuidance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNavigation(new LatLng(model.getGeometry().getLocation().getLatitude(),model.getGeometry().getLocation().getLongitude()));
            }
        });

    }

    //#region Google override methods
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        model = (LocationModel) getIntent().getSerializableExtra("LocationModel");
        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));

        final LatLng location = new LatLng(model.getGeometry().getLocation().getLatitude(), model.getGeometry().getLocation().getLongitude());

        mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(model.getName())
                .draggable(true));


        CameraPosition _cameraPosition = new CameraPosition.Builder()
                .target(location)
                .zoom(17)
                .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(_cameraPosition));

        //Turn on the my location layer and related control on the map
        updateLocationUI();
    }


    //#endregion

    //===============================================================

    //#region Helper methods

    private RouteModel getRoute() {

        //Make the api request and capture the response string
        String response = getNetworkResponse();

        //Parse the JSON response
        RouteParser parser = new RouteParser(response);

        //Get the route model list object
        RouteModel route = parser.getRoute();

        return route;
    }

    //Make the API request
    private String getNetworkResponse() {

        LatLng dest = new LatLng(model.getGeometry().getLocation().getLatitude()
                , model.getGeometry().getLocation().getLongitude());

        NetworkUtil util = new NetworkUtil(createUrlString(dest));

        return util.getResponse();
    }

    //Create the request url
    private String createUrlString(LatLng dest) {

        LatLng currentLocation = new LatLng(userLocation.getLatitude()
                , userLocation.getLongitude());


        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + currentLocation.latitude + "," + currentLocation.longitude + "&destination="
                + dest.latitude + "," + dest.longitude + "&key=AIzaSyDPRrKswq4mqoobbJsPzvP5TSHtlRw2rWM";

        return url;
    }

    //Extract the polyline points from the route model
    private List<LatLng> getPolylines(List<RouteLeg> legs) {

        List<RouteStep> steps = new ArrayList<RouteStep>();

        //Extract each route step
        for (RouteLeg leg : legs) {
            for (RouteStep step : leg.getSteps()) {
                steps.add(step);
            }
        }


        //List of coordinates for each step
        List<LatLng> stepCoordinates = new ArrayList<LatLng>();


        //Get the coordinates of each step
        for (RouteStep step : steps) {
            //Cycle through the polyline points in each step
            for (LatLng point : step.getStepPolyline()) {
                stepCoordinates.add(point);
            }
        }

        return stepCoordinates;
    }

    private void getLocation(FusedLocationProviderClient client) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    userLocation = location;
                    locationPermissionGranted = true;
                }

                else{
                    locationPermissionGranted = true;
                }
            }
        });
    }

    private void updateMap(){

        RouteModel route = getRoute();

        List<LatLng> polylines = getPolylines(route.getRouteLegs());
        setViews(route.getRouteLegs());

        Log.i(TAG, "Polyline points are: "+ polylines);
        final LatLng location = new LatLng(model.getGeometry().getLocation().getLatitude(),
                model.getGeometry().getLocation().getLongitude());
        //Add poly lines to the map
        Polyline polyline = this.mMap.addPolyline(new PolylineOptions()
                .addAll(polylines)
                .width(25)
                .color(Color.rgb(255,140,0))
                .clickable(true)
                .visible(true)
                .jointType(1)
                .geodesic(true));
        mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(model.getName())
                .draggable(true));
        CameraPosition _cameraPosition = new CameraPosition.Builder()
                .target(location)
                .zoom(15)
                .build();
        polyline.setTag("A");
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(_cameraPosition));
    }

    @SuppressLint("MissingPermission")
    private void updateLocationUI(){
        if(mMap == null){
            //Log this entry point
            Log.i(TAG, "MAP IS NULL");
            return;
        }
        try{
            if (locationPermissionGranted){
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
            else{
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                userLocation = null;
            }
        }catch (SecurityException e)  {
            Log.e(TAG, e.getMessage());
        }
    }


    //Set view Displays
    private void setViews(List<RouteLeg> routeLegs){
        //Change visibility of views
        btnGetRoute.setVisibility(View.GONE);
        navigationMenu.setVisibility(View.VISIBLE);

        //Set view text
        txtDestination.setText("To: "+model.getName());

        Double eta = 0.0;
        Double distance = 0.0;
        for(RouteLeg leg: routeLegs){
            eta += leg.getDuration();
            distance += leg.getDistance();
        }

        String etaText = String.valueOf(Math.round(eta/60));

        txtDrivingEta.setText(etaText+"min");
        txtDistance.setText(String.valueOf("Distance:"+(distance/1000)+"km"));
    }


    private void startNavigation(LatLng dest){
        //Set the end destination
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q="+dest.latitude+","+dest.longitude));
        intent.setPackage("com.google.android.apps.maps");

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
            Log.d(TAG,"Navigation started");
        }
        else{
            Log.d(TAG,"Navigation could not start");
        }
    }


    //#endregion
}