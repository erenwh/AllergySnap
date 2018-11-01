package com.example.brhee.allergysnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.vision.barcode.Barcode;
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

    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    TextView barcodeResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barcodeResult = (TextView)findViewById(R.id.barcode_result);

        // navigation bar
        toolbar = getSupportActionBar();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // signinNavBtn
        //InitFirebaseAuth();
        final Button btn = findViewById(R.id.RedirectToSignInBtn);
        btn.setOnClickListener(this);

        final Button btn2= findViewById(R.id.barcode_scan);
        btn2.setOnClickListener(this);

//        Button cam2btn = findViewById(R.id.cam2);
//        cam2btn.setOnClickListener(this);
      
        //ImageView cameraBtn = (ImageView) findViewById(R.id.cameraBtn);
        //cameraBtn.setOnClickListener(this);

    }

    public void scanBarcode(View v) {
        Log.e("here", "herereer");
        Intent intent = new Intent(MainActivity.this, Camera2.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra("barcode");

                    Bundle bundle = new Bundle();
                    Intent i = new Intent(this, ResultActivity.class);
                    bundle.putString("barcode_value", barcode.displayValue);
                    i.putExtras(bundle);
                    startActivity(i);


                    //barcodeResult.setText("Barcode value: " + barcode.displayValue);
                }
                else {
                    //barcodeResult.setText("No barcode found!");
                    Bundle bundle = new Bundle();
                    Intent i = new Intent(this, ResultActivity.class);
                    bundle.putString("barcode_value", "No barcode found!");
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }

        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Button btn = findViewById(R.id.RedirectToSignInBtn);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            System.out.println("USER LOGGED IN");
            System.out.println(user);
            btn.setVisibility(View.GONE);
        } else {
            // No user is signed in
            System.out.println("NO USER LOGGED IN");
            System.out.println(user);
            btn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Button btn = findViewById(R.id.RedirectToSignInBtn);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            System.out.println("USER LOGGED IN");
            System.out.println(user);
            btn.setVisibility(View.GONE);
        } else {
            // No user is signed in
            System.out.println("NO USER LOGGED IN");
            System.out.println(user);
            btn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Button btn = findViewById(R.id.RedirectToSignInBtn);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            System.out.println("USER LOGGED IN");
            System.out.println(user);
            btn.setVisibility(View.GONE);
        } else {
            // No user is signed in
            System.out.println("NO USER LOGGED IN");
            System.out.println(user);
            btn.setVisibility(View.VISIBLE);
        }
    }


//    public void openCameraActivity(View view){
//        Intent startCameraActivity= new Intent(MainActivity.this, CameraActivity.class);
//        startActivity(startCameraActivity);
//    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //openCameraActivity
            //case R.id.cameraBtn:
             //   System.out.println("cameraBtn works");
               // startActivity(new Intent(MainActivity.this,  Camera2.class));
                //startActivityForResult(new Intent(MainActivity.this,  Camera2.class), 0);
                //break;
            //login btn
            case R.id.RedirectToSignInBtn:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.barcode_scan:
                startActivity(new Intent(MainActivity.this, ResultActivity.class));
                break;
            // profile detail btn

//            case R.id.cam2:
//                startActivity(new Intent(MainActivity.this,  Camera2.class));

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