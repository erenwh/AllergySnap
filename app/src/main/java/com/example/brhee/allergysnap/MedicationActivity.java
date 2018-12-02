package com.example.brhee.allergysnap;

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
//        medSearch.addTextChangedListener(new TextWatcher() {
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
                                Medication newMed = new Medication(medFilter, Integer.parseInt(err.getElementsByTagName("rxnormId").item(0).getTextContent()));
                                if (userObj.medications == null) {
                                    userObj.medications = new ArrayList<>();
                                }
                                userObj.medications.add(newMed);
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(userObj);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MedicationActivity.this, "Added " + medFilter + " successfully!", Toast.LENGTH_LONG).show();
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
                                }
                            });
                        }
                    } catch (Exception e) {
                        //Log.d(TAG, e.getMessage());
                        System.out.println(e.getMessage());
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
