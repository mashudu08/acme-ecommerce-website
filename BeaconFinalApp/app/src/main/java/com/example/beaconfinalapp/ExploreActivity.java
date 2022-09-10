package com.example.beaconfinalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.beaconfinalapp.Adapters.PlacesAdapter;
import com.example.beaconfinalapp.PlacesHelper.PlaceHelper;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.PlusCode;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {

    private static final String TAG = "ExploreActivity";


    PlacesClient placesClient;
    Place[] likelihoods;
    ArrayList<PlaceHelper> placeList = new ArrayList<PlaceHelper>();
    String[] placeArray;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);

        GetPlaces();

        recyclerView = findViewById(R.id.recycleView);

        PlacesAdapter placesAdapter = new PlacesAdapter(placeList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(placesAdapter);

    }

    //Get likely places
    private void GetPlaces(){

        List<Place.Field> placeFields = Collections.singletonList(Place.Field.NAME);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        @SuppressLint("MissingPermission") Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
        placeResponse.addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                FindCurrentPlaceResponse response = task.getResult();
                int size = response.getPlaceLikelihoods().size();
                int index = 0;
                likelihoods = new Place[size];
                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    likelihoods[index] = placeLikelihood.getPlace();
                   index++;

                }

                setList(likelihoods);
                try{
                    for(String place: placeArray){
                        Log.i(TAG, "PLACE FOUND: "+place);
                    }
                }catch (Exception e){
                    Log.d(TAG, "ERROR GETTING PLACES");
                    likelihoods[0] = new Place() {
                        @Nullable
                        @Override
                        public String getAddress() {
                            return "NO ADDRESS FOUND";
                        }

                        @Nullable
                        @Override
                        public AddressComponents getAddressComponents() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public BusinessStatus getBusinessStatus() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public List<String> getAttributions() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public String getId() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public LatLng getLatLng() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public String getName() {
                            return "NO PLACE FOUND";
                        }

                        @Nullable
                        @Override
                        public OpeningHours getOpeningHours() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public String getPhoneNumber() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public List<PhotoMetadata> getPhotoMetadatas() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public PlusCode getPlusCode() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public Integer getPriceLevel() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public Double getRating() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public List<Type> getTypes() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public Integer getUserRatingsTotal() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public Integer getUtcOffsetMinutes() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public LatLngBounds getViewport() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public Uri getWebsiteUri() {
                            return null;
                        }

                        @Override
                        public int describeContents() {
                            return 0;
                        }

                        @Override
                        public void writeToParcel(Parcel parcel, int i) {

                        }
                    };
                    setList(likelihoods);
                }


            } else {
                Exception exception = task.getException();
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                }
            }
        });
    }

    private void setList(Place[] places){

        try{
            for(Place place: places){
                PlaceHelper helper = new PlaceHelper(place.getName(),place.getAddress());
                placeList.add(helper);
            }
        }catch (Exception e){
            Log.d(TAG, "ERROR IN ARRAY");
        }

    }

    private  String[] getPlaceArray(){
        return this.placeArray;
    }
}