package com.example.brhee.allergysnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActionBar toolbar;

    // Static vars
    public static final String ANONYMOUS = "anonymous";

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private String mUsername;
    private String mPhotoUrl;

    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // navigation bar
        toolbar = getSupportActionBar();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // signinNavBtn
        //InitFirebaseAuth();
        Button btn = findViewById(R.id.RedirectToSignInBtn);
        btn.setOnClickListener(this);

        // cameraNavBtn
        ImageView cameraBtn = (ImageView) findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(this);
    }

    public void openCameraActivity(View view){
        Intent startCameraActivity= new Intent(MainActivity.this, CameraActivity.class);
        startActivity(startCameraActivity);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //openCameraActivity
            case R.id.cameraBtn:
                System.out.println("cameraBtn works");
                openCameraActivity(v);
                break;
            //login btn
            case R.id.RedirectToSignInBtn:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
//            // profile detail btn
//            case R.id.TempRedirctToPDA:
//                startActivity(new Intent(MainActivity.this, ProfileDetailActivity.class));
//                break;
//            // signOut btn
//            case R.id.SignOutBtn:
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                break;
        }
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.SighOut:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    /*
    private void InitFirebaseAuth() {
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }
    }*/

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_medications:
                    startActivity(new Intent(MainActivity.this, MedicationActivity.class));
                    return true;
                case R.id.navigation_allergies:
                    startActivity(new Intent(MainActivity.this, AllergyActivity.class));
                    return true;
                case R.id.navigation_conflicts:
                    startActivity(new Intent(MainActivity.this, ConflictActivity.class));
                    return true;
                case R.id.navigation_profile:
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    return true;
            }
            return false;
        }
    };
}