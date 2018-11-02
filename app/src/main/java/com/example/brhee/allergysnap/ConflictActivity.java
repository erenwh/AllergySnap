package com.example.brhee.allergysnap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

    ArrayList<MedicationConflict> conflictList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conflict);
        Log.d(TAG, "onCreate: Started");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("Users");
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
                            ArrayList<MedicationConflict> conflictList = getConflicts();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        ListView mListView = (ListView) findViewById(R.id.listView);



        /*ArrayList<MedicationConflict> conflictList = new ArrayList<>();

        ArrayList<String> drug1 = new ArrayList<>();
        drug1.add("a");
        drug1.add("b");
        String des = "des";
        String sev = "DEADLY";

        MedicationConflict mc = new MedicationConflict(drug1, des, sev);

        ArrayList<String> drug2 = new ArrayList<>();
        drug2.add("b");
        drug2.add("c");
        String des2 = "des";
        String sev2 = "DEADLY";

        MedicationConflict mc2 = new MedicationConflict(drug2, des2, sev2);

        conflictList.add(mc);
        conflictList.add(mc2);*/
        if (conflictList != null) {
            ConflictListAdapter adapter = new ConflictListAdapter(this, R.layout.adapter_view_layout, conflictList);
            mListView.setAdapter(adapter);
        }
        else {
            mListView.setEmptyView(findViewById(R.id.emptyElement));
        }
    }

    private ArrayList<MedicationConflict> getConflicts() {
        ArrayList<MedicationConflict> res = new ArrayList<>();
        // Get user Medications IDs
        final ArrayList<Integer> myMeds= new ArrayList<>();
        if (userObj.medications != null) {
            for (Medication m :
                    userObj.medications) {
                myMeds.add(m.id);
            }
        }
        final StringBuilder urlLink = new StringBuilder("https://rxnav.nlm.nih.gov/REST/interaction/list.json?rxcuis=");
        for (int i :
                myMeds) {
            urlLink.append(i).append("+");
        }

        // Send to API
        final StringBuffer buffer = new StringBuffer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                urlLink.setLength(urlLink.length() - 1);
                System.out.println(urlLink);
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
            }
        }).start();

        // Extract info from JSON
        try {
            JSONObject obj = new JSONObject(buffer.toString());
            for (int i = 0; i < obj.length(); i++)
            {
                try {
                    JSONObject oneObject = obj.getJSONObject(i);
                    // Pulling items from the array

                } catch (JSONException e) {
                    // Oops
                }
            }
        } catch (Throwable t) {
        }

        // Construct Array
        return res;
    }
}
