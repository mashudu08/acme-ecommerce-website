package com.example.beaconfinalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.beaconfinalapp.JSONParsers.LocationParser;
import com.example.beaconfinalapp.LocationHelpers.LocationModel;
import com.example.beaconfinalapp.LocationHelpers.PlaceLocation;
import com.example.beaconfinalapp.Network_Helper.NetworkUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnPoiClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        OnPoiClickListener, OnMapClickListener, OnRequestPermissionsResultCallback{


    //#region Activity Views

    //Views
    Button btnTestMe;
    FloatingActionButton btnSearch;
    RelativeLayout btnExplore, btnNavigate, btnSetings;

    //Response test variable
    String _response;

    //#endregion


    //#region Activity Constants
    static final String TAG = "MapsActivity: ";
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    LocationModel modelToPass = new LocationModel();
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    //#endregion

    //#region google likely places arrays

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] likelyPlaceNames;
    private String[] likelyPlaceAddresses;
    private List[] likelyPlaceAttributions;
    private LatLng[] likelyPlaceLatLngs;

    //#endregion
    //TODO: Create the current places fragment and inflate it
    //TODO: Call the show current places method to use it in the above fragment


    //#region Google maps variable
    private GoogleMap mMap;
    private int DEFAULT_ZOOM = 15;
    PlacesClient placesClient;
    private CameraPosition cameraPosition;
    //#endregion

    //==============================================================================

    //#region Location variables

    //Fused location provider client
    FusedLocationProviderClient fusedLocationProviderClient;

    //Is the permission granted
    boolean locationPermissionGranted;

    //Location permission code
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    //User's last known location
    private Location lastKnownLocation;

    //Default location (Fallback) TODO: Change this to varsity college
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    //#endregion

    //==============================================================================

    //On create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_maps);


        //Construct a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);

        //Construct a fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //#region View inits

        btnSearch = findViewById(R.id.fabSearch);
        btnExplore = findViewById(R.id.btnExplore);
        btnSetings = findViewById(R.id.btnSettings);
        btnNavigate = findViewById(R.id.btnSearchNavigate);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSearch();
            }
        });


        btnSetings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSearch();
            }
        });

        btnExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, ExploreActivity.class);
                startActivity(intent);
            }
        });
        //#endregion
    }

    //==============================================================================

    //#region google maps callbacks

    @SuppressLint("MissingPermission")
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));

        //TODO: Use the user's current location
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, DEFAULT_ZOOM));

        this.mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(@NonNull Marker marker) {
                //Inflate the layouts for info window, title and snippet
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map),false);

                TextView title = infoWindow.findViewById(R.id.infoTitle);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.infoSnippet);
                snippet.setText(marker.getSnippet());
                return infoWindow;
            }
        });

        this.mMap.setOnMapClickListener(this::onMapClick);


        //Turn on the my location layer and related control on the map
        updateLocationUI();

        //Get the current device location
        getDeviceLocation();

    }

    //When a user clicks on a place direct the map to that location
    @Override
    public void onPoiClick(PointOfInterest poi) {
        Toast.makeText(this, "Clicked: " +
                        poi.name + "\nPlace ID:" + poi.placeId +
                        "\nLatitude:" + poi.latLng.latitude +
                        " Longitude:" + poi.latLng.longitude,
                Toast.LENGTH_SHORT).show();
    }

    //Use when the user the user click on a specific point on the map
    @Override
    public void onMapClick(@NonNull LatLng latLng) {

        //Clear current map marker
        mMap.clear();

        String title = "Latitude: " + String.valueOf(latLng.latitude) + ", Longitude: "+String.valueOf(latLng.longitude);

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title(title));

        //Move the map's camera to the location

        CameraPosition _cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(17)
                .tilt(30)
                .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(_cameraPosition));
    }

    //Request permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }


    //#endregion

    //===============================================================================


    //#region helper methods

    //Get the users permission if not granted already
    private void getLocationPermission(){

        //Check if the location permission has been granted
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationPermissionGranted = true;
        }
        //Request the permission if it is not granted
        else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }

    //Update location ui
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
                lastKnownLocation = null;
                getLocationPermission();
            }
        }catch (SecurityException e)  {
            Log.e(TAG, e.getMessage());
        }
    }

    //Get the user's last known location
    @SuppressLint("MissingPermission")
    private void getDeviceLocation(){

        try{
            if(locationPermissionGranted){
               Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful()){
                            //Set the camera position to the user's current location
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM)
                                );
                            }
                            else{
                                Log.d(TAG, "Current location is null... Switching to defaults");
                                Log.e(TAG, "ERROR IN GET DEVICE LOCATION");
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            }
                        }
                    }
                });
            }
        }catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    @SuppressLint("MissingPermission")
    private void showCurrentPlace(){
        //Return if the map is null
        if(mMap == null){
            Log.d(TAG, "MAP IS NULL AT SHOW CURRENT PLACE");
            return;
        }

        //Get likely places if location has been granted
        if(locationPermissionGranted){
            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                    Place.Field.LAT_LNG);

            FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

            Task<FindCurrentPlaceResponse> placeResult =
                    placesClient.findCurrentPlace(request);
            placeResult.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                @Override
                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                    if(task.isSuccessful() && task.getResult() != null){
                        FindCurrentPlaceResponse likelyPlaces = task.getResult();

                        int count;
                        if(likelyPlaces.getPlaceLikelihoods().size() < M_MAX_ENTRIES){
                            count = likelyPlaces.getPlaceLikelihoods().size();
                        }
                        else{
                            count = M_MAX_ENTRIES;
                        }

                        int i = 0;
                        likelyPlaceNames = new String[count];
                        likelyPlaceAddresses = new String[count];
                        likelyPlaceAttributions = new List[count];
                        likelyPlaceLatLngs = new LatLng[count];

                        for(PlaceLikelihood placeLikelihood: likelyPlaces.getPlaceLikelihoods()){
                            //Build a place of likely places to show to the user
                            likelyPlaceNames[i] = placeLikelihood.getPlace().getName();
                            likelyPlaceAddresses[i] = placeLikelihood.getPlace().getAddress();
                            likelyPlaceAttributions[i] = placeLikelihood.getPlace().getAttributions();
                            likelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                            i++;
                            if(i > (count - 1)){
                                break;
                            }
                        }
                        MapsActivity.this.openPlacesDialog();
                    }
                    else{
                        Log.e(TAG, "ERROR GETTING CURRENT PLACES");
                    }
                }
            });
        }
        else{
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(defaultLocation)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    //Open the places dialog ad let the user select a place
    private void openPlacesDialog() {

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LatLng markerLatLng = likelyPlaceLatLngs[i];
                String markerSnippet = likelyPlaceAddresses[i];
                if(likelyPlaceAttributions[i] != null){
                    markerSnippet = markerSnippet + "\n" + likelyPlaceAttributions[i];
                }

                //Remove the current marker
                mMap.clear();

                //Interact with the map with an info window
                mMap.addMarker(new MarkerOptions()
                .title(likelyPlaceNames[i])
                .position(markerLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .snippet(markerSnippet)).showInfoWindow();

                //Move the map's camera to the location

                CameraPosition _cameraPosition = new CameraPosition.Builder()
                        .target(markerLatLng)
                        .zoom(17)
                        .tilt(30)
                        .build();

                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(_cameraPosition));
            }
        };

        //Display the dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(likelyPlaceNames, listener)
                .show();

    }

    private void getDirections(String search){

        //Sleep thread here if response not being generated

        String response = getNetworkResponse(search);
        LocationParser parser = new LocationParser(response);
        List<LocationModel> list = parser.getLocationModels();

        for(LocationModel model: list){
            modelToPass = model;
            Log.i(TAG, "MODEL: "+model.getName() + " LOCATION IS: "+model.getGeometry().getLocation().getLatitude());
        }

        Intent intent = new Intent(MapsActivity.this, RouteGuideActivity.class);
        intent.putExtra("LocationModel", (Serializable) modelToPass);
        startActivity(intent);

        if(_response != null){
        //    Log.i(TAG, "RESPONSE: "+_response);
        }
        else{
            Log.i(TAG, "RESPONSE: IS NULL");
        }

    }

    //Method to get json response from the network util
    private String getNetworkResponse(String search){
        NetworkUtil util = new NetworkUtil(this.createUrlString(search));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return util.getResponse();
    }

    //This method will create the url string that will be used by the network util object
    private String createUrlString(String searchParams) {

        searchParams.toLowerCase();
        String url = "";
        String params = "";
        String args[] = searchParams.split(" ");

        for (int i = 0; i < args.length; i++) {
            if (i == args.length - 1) {
                params += args[i];
            } else {
                params += args[i] + "%20";
            }

        }

        url = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input="
                + params + "&inputtype=textquery&fields=photos,formatted_address,name,rating,opening_hours,geometry&key=AIzaSyDPRrKswq4mqoobbJsPzvP5TSHtlRw2rWM";

        return url;
    }

    //Search menu method
    private void launchSearch(){
        int AUTOCOMPLETE_REQUEST_CODE = 1;

        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        // Start the autocomplete intent.
        //TODO: Add region filter after completion of project
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    //On search result override
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                getDirections(place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
                System.out.println("Place not returned");
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //#endregion

    //TODO: CREATE CUSTOM INFO WINDOWS

}