package com.example.brhee.allergysnap;

import android.nfc.Tag;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
    private ArrayList<String> medications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

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
                            MedicationAdapter adapter = new MedicationAdapter(MedicationActivity.this, R.layout.adapter_medications, userObj.medications);
                            userMeds.setAdapter(adapter);
                        }
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
        final String medicine = med;
        new Thread(new Runnable(){
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
                    Element err = (Element)errNodes.item(0);
                    if (err.getElementsByTagName("rxnormId").getLength() > 0) {
                        boolean duplicate = false;
                        // success
                        if (userObj.medications != null) {
                            for (Medication med : userObj.medications) {
                                if (med.name.equals(err.getElementsByTagName("name").item(0).getTextContent())) {
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
                            Medication newMed = new Medication(err.getElementsByTagName("name").item(0).getTextContent(), Integer.parseInt(err.getElementsByTagName("rxnormId").item(0).getTextContent()));
                            if (userObj.medications == null) {
                                userObj.medications = new ArrayList<>();
                            }
                            userObj.medications.add(newMed);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(userObj);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_sub:
                if (medSearch.getText().toString().trim().length() > 3) {
                    search(medSearch.getText().toString().trim());
                }
                break;
        }
    }
}
