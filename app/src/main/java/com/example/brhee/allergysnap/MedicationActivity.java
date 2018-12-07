package com.example.brhee.allergysnap;

import java.net.*;
import java.util.*;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.common.StringUtils;
import com.medialablk.easytoast.EasyToast;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;

public class MedicationActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText medSearch;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseUser user;
    private User userObj;
    private String userID;
    private ListView userMeds;
    private ProgressBar progressbar;

    private ActionBar toolbar;
    public BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        // navigation bar
        toolbar = getSupportActionBar();
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_medications);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        progressbar = findViewById(R.id.progressBar);
        progressbar.setVisibility(View.VISIBLE);
        medSearch = findViewById(R.id.med_search);
        userMeds = findViewById(R.id.user_meds);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("Users");
        findViewById(R.id.search_sub).setOnClickListener(this);
        user = mAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
            Query userData = myRef;
            userData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userObj = dataSnapshot.child(userID).getValue(User.class);
                        if (userObj != null) {
                            MedicationAdapter adapter = new MedicationAdapter(MedicationActivity.this, R.layout.adapter_medications, userObj.medications, userObj);
                            userMeds.setAdapter(adapter);
                        }
                        progressbar.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        //findViewById(R.id.test_button).setOnClickListener(this);
//        medSearch.addTextChangedListener(new TextWatcher() {e
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });



    }

    public void search(String med) {
        if (med.trim().length() > 0) {
            final String medicine = med;
            progressbar.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Do network action in this function
                    try {
                        String urlLink = "https://rxnav.nlm.nih.gov/REST/rxcui?name=" + medicine;
                        URL url = new URL(urlLink);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        int responseCode = con.getResponseCode();
                        System.out.println("Response Code : " + responseCode);
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                                .parse(new InputSource(new StringReader(response.toString())));
                        NodeList errNodes = doc.getElementsByTagName("idGroup");
                        Element err = (Element) errNodes.item(0);
                        if (err.getElementsByTagName("rxnormId").getLength() > 0) {
                            boolean duplicate = false;
                            // success
                            if (userObj.medications != null) {
                                for (Medication med : userObj.medications) {
                                    if (med.name.equalsIgnoreCase(err.getElementsByTagName("name").item(0).getTextContent())) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MedicationActivity.this);
                                                builder.setMessage("Medication already added")
                                                        .setTitle("Duplicate Medication");

                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                                progressbar.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                        duplicate = true;
                                    }
                                }
                            }
                            if (!duplicate) {
                                char[] chars = err.getElementsByTagName("name").item(0).getTextContent().toLowerCase().toCharArray();
                                boolean found = false;
                                for (int i = 0; i < chars.length; i++) {
                                    if (!found && Character.isLetter(chars[i])) {
                                        chars[i] = Character.toUpperCase(chars[i]);
                                        found = true;
                                    } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
                                        found = false;
                                    }
                                }
                                final String medFilter = String.valueOf(chars);
                                int rxId = Integer.parseInt(err.getElementsByTagName("rxnormId").item(0).getTextContent());

                                Medication newMed = new Medication(medFilter, rxId);
                                if (userObj.medications == null) {
                                    userObj.medications = new ArrayList<>();
                                }

                                // add time
                                newMed.timeAdded = System.currentTimeMillis();

                                // Use Bing API to get image url for medicine
                                String subscriptionKey = "a29f39d77de74657927b964a761a4073";
                                String host = "https://api.cognitive.microsoft.com";
                                String imgPath = "/bing/v7.0/images/search";
                                // construct the search request URL (in the form of endpoint + query string)
                                URL urlImg = new URL(host + imgPath + "?q=" + URLEncoder.encode(medFilter, "UTF-8") + "&imageType=transparent");
                                HttpsURLConnection connection = (HttpsURLConnection) urlImg.openConnection();
                                connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);
                                // receive JSON body
                                InputStream stream = connection.getInputStream();
                                String responseInfo = new Scanner(stream).useDelimiter("\\A").next();
                                // construct result object for return
                                stream.close();
                                JsonParser parser = new JsonParser();
                                JsonObject json = parser.parse(responseInfo).getAsJsonObject();
                                JsonArray results = json.getAsJsonArray("value");
                                JsonObject first_result = (JsonObject)results.get(0);
                                String resultURL = first_result.get("contentUrl").getAsString();
                                newMed.url = resultURL;

                                // strip google search text for medicine info
                                String urlInfo = "https://www.google.com/search?q=" + medFilter;
                                org.jsoup.nodes.Document docInfo = Jsoup.connect(urlInfo).get();
                                Elements temp = docInfo.select("div[class=K9xsvf Uva9vc kno-fb-ctx]");
                                if (temp.size() == 0) {
                                    newMed.info = "Information N/A.";
                                } else {
                                    newMed.info = temp.get(0).getElementsByTag("span").first().text();
                                }

                                // add medication to medications list and push to firebase
                                userObj.medications.add(newMed);
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(userObj);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        progressbar.setVisibility(View.INVISIBLE);
                                        //Toast.makeText(MedicationActivity.this, "Added " + medFilter + " successfully!", Toast.LENGTH_LONG).show();
                                        EasyToast.custom(MedicationActivity.this, "Added " + medFilter + " successfully!", R.drawable.ic_medications_24dp, getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.background_light), Toast.LENGTH_LONG);
                                        medSearch.setText("");
                                    }
                                });


                            }
                        } else {
                            // error
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MedicationActivity.this);
                                    builder.setMessage("Medication not found")
                                            .setTitle("Invalid search");

                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                    progressbar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    } catch (Exception e) {
                        //Log.d(TAG, e.getMessage());
                        System.out.println(e.getMessage());
                        progressbar.setVisibility(View.INVISIBLE);
                    }
                }
            }).start();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_sub:
                if (medSearch.getText().toString().trim().length() > 0) {
                    search(medSearch.getText().toString().trim());
                }
                break;
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_medications:
                    //startActivity(new Intent(MedicationActivity.this, MedicationActivity.class));
                    return true;
                case R.id.navigation_allergies:
                    startActivity(new Intent(MedicationActivity.this, AllergyActivity.class));
                    return true;
                case R.id.navigation_main:
                    startActivity(new Intent(MedicationActivity.this, MainActivity.class));
                    return true;
                case R.id.navigation_conflicts:
                    startActivity(new Intent(MedicationActivity.this, ConflictActivity.class));
                    return true;
                case R.id.navigation_profile:
                    startActivity(new Intent(MedicationActivity.this, ProfileActivity.class));
                    return true;
            }
            return false;
        }
    };

    // Changes button back
    @Override
    protected void onResume() {
        super.onResume();
        navigation.getMenu().getItem(1).setChecked(true);
    }
}
