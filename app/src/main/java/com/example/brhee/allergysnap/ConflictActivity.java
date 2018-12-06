package com.example.brhee.allergysnap;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;

public class ConflictActivity extends AppCompatActivity {

    private static final String TAG = "ConflictActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseUser user;
    private User userObj;
    private String userID;
    private TextView disclaimer;
    private ProgressBar progressBar;
    private long one;

    private FirebaseAuth.AuthStateListener mAuthListener;

    ArrayList<MedicationConflict> conflictList;

    public BottomNavigationView navigation;
    private ActionBar toolbar;

    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conflict);
        Log.d(TAG, "onCreate: Started");

        // navigation bar
        toolbar = getSupportActionBar();
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_conflicts);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();
        progressBar = findViewById(R.id.progressBar);


        if (user != null) {
            userID = user.getUid();
            myRef = mFirebaseDatabase.getReference("Users/"+userID);
        }
        if (userID == null) {
            startActivity(new Intent(ConflictActivity.this, LoginActivity.class));
        }
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user != null) {
                    // User is signed in
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // get User info (updates when user info changes)
                            userObj = dataSnapshot.getValue(User.class);
                            getConflicts();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        };

        disclaimer = findViewById(R.id.disclaimer);
        disclaimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConflictActivity.this);
                builder.setMessage(R.string.disclaimer)
                        .setTitle("DISCLAIMER");

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }

    private void progressBarVisible() {
        one = System.currentTimeMillis();
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "progressBarVisible: Visible -" + one);
    }

    private void progressBarInvisible() {
        progressBar.setVisibility(View.GONE);
        long two = System.currentTimeMillis();
        Log.d(TAG, "progressBarVisible: Visible -" + two);
        Log.d(TAG, "progressBarInvisible: Time of progressBar: " + String.valueOf(((two - one)/1000F)));
    }

    private void getConflicts() {
        progressBarVisible();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<MedicationConflict> res = new ArrayList<>();
                // Get user Medications IDs
                final ArrayList<Integer> myMeds = new ArrayList<>();
                
                final ArrayList<String> myMedsNames = new ArrayList<>();
                final ArrayList<String> myAllergies = new ArrayList<>();
                if (userObj.allergies != null) {
                    for (Allergy a :
                            userObj.allergies) {
                        myAllergies.add(a.name);
                    }
                }
                if (userObj.medications != null) {
                    for (Medication m :
                            userObj.medications) {
                        myMeds.add(m.id);
                        myMedsNames.add(m.name);
                    }
                }
                final StringBuilder urlLink = new StringBuilder("https://rxnav.nlm.nih.gov/REST/interaction/list.json?rxcuis=");
                for (int i :
                        myMeds) {
                    urlLink.append(i).append("+");
                }
                urlLink.setLength(urlLink.length() - 1);
                System.out.println(urlLink);


                // Send to API

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

                ArrayList<String> drugs;
                String description;
                String severity;
                String source;

                if (obj != null) {
                    try {
                        // Get fullInteractionTypeGroup
                        JSONArray fullInteractionTypeGroup = obj.getJSONArray("fullInteractionTypeGroup");
                        for (int i = 0; i < fullInteractionTypeGroup.length(); i++) {
                            JSONObject fullInteractionTypeObj = fullInteractionTypeGroup.getJSONObject(i);
                            JSONArray fullInteractionTypeArr = fullInteractionTypeObj.getJSONArray("fullInteractionType");
                            description = "";
                            severity = "";
                            source = fullInteractionTypeObj.getString("sourceName");
                            for (int j = 0; j < fullInteractionTypeArr.length(); j++) {
                                JSONObject fullInteractionType = fullInteractionTypeArr.getJSONObject(j);
                                JSONArray interactionPair = fullInteractionType.getJSONArray("interactionPair");
                                drugs = new ArrayList<>();
                                for (int k = 0; k < interactionPair.length(); k++) {
                                    // Add description
                                    JSONObject currPair = interactionPair.getJSONObject(k);
                                    description = currPair.getString("description");
                                    System.out.println(description);

                                    // Add Severity
                                    severity = currPair.getString("severity");
                                    System.out.println(severity);

                                    // Add drug name
                                    JSONArray interactionConcept = currPair.getJSONArray("interactionConcept");
                                    for (int l = 0; l < interactionConcept.length(); l++) {
                                        JSONObject currDrug = interactionConcept.getJSONObject(l);
                                        JSONObject sourceConceptItem = currDrug.getJSONObject("sourceConceptItem");
                                        String drugName = sourceConceptItem.getString("name");
                                        if (!drugs.contains(drugName)) {
                                            drugs.add(drugName);
                                        }
                                        System.out.println(drugName);
                                    }
                                    MedicationConflict mc = new MedicationConflict(drugs, description, severity, source);

                                    res.add(mc);
                                }

                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                conflictList = res;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ListView mListView = (ListView) findViewById(R.id.listView);
                        if (conflictList != null) {
                            ConflictListAdapter adapter = new ConflictListAdapter
                                    (ConflictActivity.this,
                                            R.layout.adapter_view_layout,
                                            conflictList);
                            mListView.setAdapter(adapter);
                        } else {
                            mListView.setEmptyView(findViewById(R.id.emptyElement));
                        }
                        progressBarInvisible();
                    }
                });

                if (userObj.medications2 == null) {
                    userObj.medications2 = userObj.medications;
                }
                if (userObj.medConflictList == null) {
                    userObj.medConflictList = new ArrayList<>();
                    userObj.medConflictList.addAll(conflictList);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(userObj);
                }
                if (userObj.medications2 != userObj.medications) {
                    //update
                    userObj.medConflictList.clear();
                    userObj.medConflictList.addAll(conflictList);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(userObj);

                    userObj.medications2 = userObj.medications;
                }

            }
        }).start();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_medications:
                    startActivity(new Intent(ConflictActivity.this, MedicationActivity.class));
                    return true;
                case R.id.navigation_allergies:
                    startActivity(new Intent(ConflictActivity.this, AllergyActivity.class));
                    return true;
                case R.id.navigation_main:
                    startActivity(new Intent(ConflictActivity.this, MainActivity.class));
                    return true;
                case R.id.navigation_conflicts:
                    //startActivity(new Intent(ConflictActivity.this, ConflictActivity.class));
                    return true;
                case R.id.navigation_profile:
                    startActivity(new Intent(ConflictActivity.this, ProfileActivity.class));
                    return true;
            }
            return false;
        }
    };

    // Changes button back
    @Override
    protected void onResume() {
        super.onResume();
        navigation.getMenu().getItem(4).setChecked(true);
    }

}

