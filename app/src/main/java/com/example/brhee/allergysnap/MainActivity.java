package com.example.brhee.allergysnap;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gigamole.library.PulseView;
import com.google.android.gms.common.api.CommonStatusCodes;
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
import com.medialablk.easytoast.EasyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

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
    public BottomNavigationView navigation;

    double longitude;
    double latitude;
    String tree_desc;
    String tree_count;
    String grass_desc;
    String grass_count;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static final String CHANNEL_1_ID = "channel1";
    private NotificationManager manager;
    private DatabaseReference hanRef;
    private User userObj_han;
    boolean pollenTF = false;
    boolean sent = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Location
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    0);
        }

        assert lm != null;
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                //Toast.makeText(MainActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
                //EasyToast.custom(MainActivity.this, "Latitude: " + latitude + "\nLongitude: " + longitude, getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (location != null) {
            Log.e("TAG", "GPS is on");
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            //Toast.makeText(MainActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
            //EasyToast.custom(MainActivity.this, "Latitude: " + latitude + "\nLongitude: " + longitude, getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);
        }
        else{
            //This is what you need:
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        }

        GetPollenData();


        createNotificationChannels();


        System.out.println("long: " + longitude + ", latitude: " + latitude);
        //longitude: -86.90946872, latitude: 40.4232417
        //uk https://api.breezometer.com/pollen/v1/current-conditions?lat=52.2053&lon=-0.1218&key=4a06240afef94732b6b524fd3a78d2e9
        //aus https://api.breezometer.com/pollen/v1/current-conditions?lat=33.8688&lon=151.2093&key=4a06240afef94732b6b524fd3a78d2e9


        barcodeResult = (TextView) findViewById(R.id.barcode_result);
        barcodeIngredients = (TextView) findViewById(R.id.barcode_ingredients);
        barcodeName = (TextView) findViewById(R.id.barcode_name);
        qrResult = (TextView) findViewById(R.id.qr_result);

        barcode_number = "";
        product_name = "";
        ingredients = "";

        // navigation bar
        toolbar = getSupportActionBar();
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        final PulseView pulseView = (PulseView) findViewById(R.id.pv);
        pulseView.startPulse();

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
                    if (userObj !=  null) {
                        if (userObj.allergies != null) {
                            for (Allergy a :
                                    userObj.allergies) {
                                if (a.name.equals("pollen")) pollenTF = true;
                            }
                        }
                        if (pollenTF) {
                            findViewById(R.id.pollen_layout).setVisibility(View.VISIBLE);
                        }
                        if (tree_count == null || tree_count.equals("null"))
                            tree_count = "0";
                        if (!sent && pollenTF) {
                            Notification noti = new NotificationCompat.Builder(MainActivity.this, CHANNEL_1_ID).
                                    setSmallIcon(R.drawable.ic_pollen)
                                    .setContentTitle("Pollen Alert")
                                    .setContentText("You have Pollen Allergy, pollen count has reached " + tree_count)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .build();
                            manager.notify(1, noti);
                            sent = true;
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("Pollen Alert");
            manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(channel1);
        }

    }

    private void GetPollenData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // form url
                longitude = -0.1218;
                latitude = 52.2053;
                String APIkey = "4a06240afef94732b6b524fd3a78d2e9";
                final StringBuilder urlLink = new StringBuilder("https://api.breezometer.com/pollen/v1/current-conditions?");
                urlLink.append("lat=").append(latitude).append("&").append("lon=").append(longitude).append("&").append("key=").append(APIkey);
                System.out.println(urlLink);

                // send to api
                final StringBuffer buffer = new StringBuffer();
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(urlLink.toString());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();


                    InputStream stream = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));


                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                        Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                    }
                    System.out.println(buffer.toString());

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

                // Extract info from JSON
                JSONObject obj = null;
                try {
                    obj = new JSONObject(buffer.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (obj != null) {
                    try {
                        // Get Data object
                        JSONObject data = obj.getJSONObject("data");
                        JSONObject pollens = data.getJSONObject("pollens");
                        JSONObject tree = pollens.getJSONObject("tree");
                        tree_desc = tree.getString("description");
                        tree_count = tree.getString("count");
                        if (tree_count.equals("null")) {
                            tree_count = "0";
                        }
                        JSONObject grass = pollens.getJSONObject("grass");
                        grass_desc = grass.getString("description");
                        grass_count = grass.getString("count");
                        if (grass_count.equals("null")) {
                            grass_count = "0";
                        }
                        System.out.println(tree_desc + tree_count + grass_desc + grass_count);

                        /*TextView t1 = (TextView) findViewById(R.id.tree_desc_text);
                        t1.setText(tree_desc);
                        TextView t2 = findViewById(R.id.tree_count_text);
                        t2.setText(tree_count);
                        TextView t3 = findViewById(R.id.grass_desc_text);
                        t3.setText(grass_desc);
                        TextView t4 = findViewById(R.id.grass_count_text);
                        t4.setText(grass_count);*/




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        TextView t1 = findViewById(R.id.tree_desc_text);
                        t1.setText(tree_desc);
                        TextView t2 = findViewById(R.id.tree_count_text);
                        t2.setText(tree_count);
                        TextView t3 = findViewById(R.id.grass_desc_text);
                        t3.setText(grass_desc);
                        TextView t4 = findViewById(R.id.grass_count_text);
                        t4.setText(grass_count);
                    }
                });

            }
        }).start();

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
                if (data == null) {
                    startActivity(new Intent (MainActivity.this , MainActivity.class));
                }
                else if (data != null) {
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
                        if (userObj != null) {
                            userObj.scans.set(1, userObj.scans.get(1) + 1);
                        }
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(userObj);

                        barcode_number = barcode.displayValue;
                        new JsonTask().execute("https://api.nutritionix.com/v1_1/item?upc=" + barcode.displayValue + "&appId=b50adcc6&appKey=5c4bb39799462d82436788bc1311f47e\n\n\n");
                        //new JsonTask().execute("https://api.barcodelookup.com/v2/products?barcode=" + barcode.displayValue + "&formatted=y&key=jjgszqhu4fhqqa6369sd9elzn13omy");
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

            //barcode_number = "";
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

                Log.d("data2 = ", data2);

                Gson g = new Gson();

                ResultActivity.Sample value = g.fromJson(data2, ResultActivity.Sample.class);

                //barcode_number = "";
                product_name = "";
                ingredients = "";

                product_name = value.item_name;
                ingredients = value.nf_ingredient_statement;

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
                case R.id.navigation_main:
                    //startActivity(new Intent(MainActivity.this, MainActivity.class));
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

    // Changes button back
    @Override
    protected void onResume() {
        super.onResume();
        navigation.getMenu().getItem(2).setChecked(true);
        //if (ResultActivity.getInstance() != null) ResultActivity.getInstance().finish();
    }
}