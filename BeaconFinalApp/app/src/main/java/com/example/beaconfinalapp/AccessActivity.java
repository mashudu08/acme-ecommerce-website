package com.example.beaconfinalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.beaconfinalapp.Fragments.LoginFrag;
import com.example.beaconfinalapp.Fragments.SignUpFrag;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccessActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    boolean signUp;
    private static final String TAG = "AccessActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);

//        try{
//            signUp = (boolean)getIntent().getSerializableExtra("signedup");
//            if(signUp){
//                LoadSignUp(true);
//            }
//
//        }catch (Exception e){
//
//        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //Check if user is already signed in
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser != null){
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
            }else{
                LoadSignUp(true);
            }


        } else {
            System.out.println("User is Null");

            LoadSignUp(false);
        }
    }

    private void LoadSignUp(boolean isSignUp){

        if(!isSignUp){
            Loadfragment(new SignUpFrag());
        }else{
            Loadfragment(new LoginFrag());
        }

    }

    private void Loadfragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

}