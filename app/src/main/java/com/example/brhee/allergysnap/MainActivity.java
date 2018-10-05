package com.example.brhee.allergysnap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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

        //InitFirebaseAuth();
        Button btn = findViewById(R.id.RedirectToSignInBtn);
        btn.setOnClickListener(this);

        // TODO: DELETE
        Button tempBtn = findViewById(R.id.TempRedirctToPDA);
        tempBtn.setOnClickListener(this);

        Button cam2btn = findViewById(R.id.cam2);
        cam2btn.setOnClickListener(this);

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
            // profile detail btn
            case R.id.TempRedirctToPDA:
                startActivity(new Intent(MainActivity.this, ProfileDetailActivity.class));
                break;

            case R.id.cam2:
                startActivity(new Intent(MainActivity.this,  Camera2.class));

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
}
