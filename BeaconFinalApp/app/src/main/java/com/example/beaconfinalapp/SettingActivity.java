package com.example.beaconfinalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {


    Button btnLogout;
    //Initialize firebase

    //Firebase variable
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        firebaseAuth = FirebaseAuth.getInstance();

        btnLogout = findViewById(R.id.btnGLogout);


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, AccessActivity.class);
                intent.putExtra("signedup", true);
                startActivity(intent);
            }
        });
    }
}