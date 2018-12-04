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
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ActionBar toolbar;

    public String ingredients = "";
    public String product_name = "";
    public String barcode_number = "";

    // Static vars
    public static final String ANONYMOUS = "anonymous";

    TextView barcodeResult;
    TextView barcodeIngredients;
    TextView barcodeName;
    TextView qrResult;

    private FirebaseAuth mAuth;
    private String userID;
    private User userObj;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barcodeResult = (TextView)findViewById(R.id.barcode_result);
        barcodeIngredients = (TextView)findViewById(R.id.barcode_ingredients);
        barcodeName = (TextView)findViewById(R.id.barcode_name);
        qrResult = (TextView)findViewById(R.id.qr_result);

        barcode_number = "";
        product_name = "";
        ingredients = "";

        // navigation bar
        toolbar = getSupportActionBar();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("Users");
        user = mAuth.getCurrentUser();
//        userObj = new User(user.getDisplayName(), user.getEmail());

        if (user != null) {
            userID = user.getUid();
        }
        Query userData = myRef;
        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userObj = dataSnapshot.child(userID).getValue(User.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // signinNavBtn
        //InitFirebaseAuth();
        /*final Button btn = findViewById(R.id.RedirectToSignInBtn);
        btn.setOnClickListener(this);*/

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
                    barcode_number = "";
                    product_name = "";
                    ingredients = "";
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
                    //If the Barcode is a number
                    if(barcode.valueFormat == 5) {
                        // Increment barcode scanning - from main screen
                        userObj.scans.set(1, userObj.scans.get(1) + 1);
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(userObj);
                        new JsonTask().execute("https://api.barcodelookup.com/v2/products?barcode=" + barcode.displayValue + "&formatted=y&key=jjgszqhu4fhqqa6369sd9elzn13omy");
                    }
                    else if (barcode.valueFormat == 8) {
                        // Increment qr scanning - from main screen
                        userObj.scans.set(2, userObj.scans.get(2) + 1);
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(userObj);
                        Bundle bundle = new Bundle();
                        Intent i = new Intent(this, ResultActivity.class);
                        bundle.putString("qr_result", barcode.displayValue);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                    else {
                        Bundle bundle = new Bundle();
                        Intent i = new Intent(this, ResultActivity.class);
                        bundle.putString("barcode_number", "No barcode found!");
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

            barcode_number = "";
            product_name = "";
            ingredients = "";
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

                barcode_number = "";
                product_name = "";
                ingredients = "";
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