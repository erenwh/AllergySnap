package com.example.brhee.allergysnap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActionBar toolbar;

    public String ingredients = "";
    public String product_name = "";
    public String barcode_number = "";

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
    TextView barcodeIngredients;
    TextView barcodeName;
    TextView qrResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barcodeResult = (TextView)findViewById(R.id.barcode_result);
        barcodeIngredients = (TextView)findViewById(R.id.barcode_ingredients);
        barcodeName = (TextView)findViewById(R.id.barcode_name);
        qrResult = (TextView)findViewById(R.id.qr_result);

        // navigation bar
        toolbar = getSupportActionBar();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // signinNavBtn
        //InitFirebaseAuth();
        final Button btn = findViewById(R.id.RedirectToSignInBtn);
        btn.setOnClickListener(this);

//        Button cam2btn = findViewById(R.id.cam2);
//        cam2btn.setOnClickListener(this);
      
        //ImageView cameraBtn = (ImageView) findViewById(R.id.cameraBtn);
        //cameraBtn.setOnClickListener(this);

    }


    public void scanBarcode(View v) {
        if (barcodeIngredients != null) {
            barcodeIngredients.setText("");
        }
        if (barcodeName != null) {
            barcodeName.setText("");
        }
        if (barcodeResult != null) {
            barcodeResult.setText("");
        }
        if (qrResult != null) {
            qrResult.setText("");
        }
        Intent intent = new Intent(MainActivity.this, Camera2.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra("barcode");

                    //If the Barcode is a number
                    if(barcode.valueFormat == 5) {
                        new JsonTask().execute("https://api.barcodelookup.com/v2/products?barcode=" + barcode.displayValue + "&formatted=y&key=n1fr3ogpc7kikr2wrgsyivbxnj43mh");
                    }
                    if (barcode.valueFormat == 8) {
                        Bundle bundle = new Bundle();
                        Intent i = new Intent(this, ResultActivity.class);
                        bundle.putString("qr_result", barcode.displayValue);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                }
                else {
                    Bundle bundle = new Bundle();
                    Intent i = new Intent(this, ResultActivity.class);
                    bundle.putString("barcode_number", "No barcode found!");
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }

        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private class JsonTask extends AsyncTask<String, String, String> {

        public ProgressDialog pd = new ProgressDialog(MainActivity.this);;

        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                String str = "";
                String data2 = "";

                while (null != (str= br.readLine())) {
                    data2 +=str;
                }

                Gson g = new Gson();

                ResultActivity.Sample.RootObject value = g.fromJson(data2, ResultActivity.Sample.RootObject.class);

                barcode_number = value.products[0].barcode_number;

                product_name = value.products[0].product_name;

                ingredients = value.products[0].ingredients;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            Bundle bundle = new Bundle();
            Intent i = new Intent(getApplicationContext(), ResultActivity.class);
            bundle.putString("ingredients", ingredients);
            bundle.putString("product_name", product_name);
            bundle.putString("barcode_number", barcode_number);
            i.putExtras(bundle);
            startActivity(i);
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //login btn
            case R.id.RedirectToSignInBtn:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
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