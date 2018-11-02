package com.example.brhee.allergysnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AllergyActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    //Firebase variable
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseUser user;
    private User userObj;
    private String userID;

    // search allergy variable
    ListView    list;
    ListViewAdapter adapter;
    SearchView editsearch;
    String[]    allergyNameList;
    ArrayList<Allergy> arrayList = new ArrayList<Allergy>();

    // my Allergy List variable
    ListView    listV_myAllergyList;
    ListViewAdapterMyAllergyList myAllergyAdapter;
    String[] myAllergyNameList;
    ArrayList<Allergy> myAllergyArraylist = new ArrayList<Allergy>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergy);

        // check with allergy database
        // Generate sample data
        allergyNameList = new String[]{"egg", "fish", "fruit", "garlic", "hot peppers", "oats", "meat", "milk", "peanut", "rice", "sesame", "soy", "sulfites", "tartrazine", "tree nut", "wheat", "tetracycline", "dilantin", "tegretol", "penicillin", "cephalosporins", "sulfonamides", "pollen", "cat", "dog", "insect sting", "mold", "perfume", "cosmetics", "semen", "latex", "water", "house dust mite", "nickel", "gold", "chromium", "cobalt", "formaldehyde", "fungicide", "dimethylaminopropylamine", "latex", "paraphenylenediamine", "glyceryl monothioglycolate", "toluenesulfonamide formaldehyde"};
        for (int i = 0; i < allergyNameList.length; i++) {
            Allergy allergy = new Allergy(allergyNameList[i]);
            // Bind all strings into an array
            arrayList.add(allergy);
        }

        // setting listview
        // ********* search allergy ********//
        list = (ListView) findViewById(R.id.listview_search);
        //  ******** showing my allergy list *************** //
        listV_myAllergyList = (ListView) findViewById(R.id.listview_myAllergyList);

        // connect to firebase
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("Users");
        user = mAuth.getCurrentUser();
        if (user != null){
            userID = user.getUid();
            final Query userData = myRef;
            userData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        userObj = dataSnapshot.child(userID).getValue(User.class);
                        if (userObj != null){
                            myAllergyAdapter = new ListViewAdapterMyAllergyList(AllergyActivity.this, R.layout.listview_allergy_myallergy, userObj.allergies, userObj);
                            listV_myAllergyList.setAdapter(myAllergyAdapter);
                            // pass results to ListViewAdapter Class
                            adapter = new ListViewAdapter(AllergyActivity.this, R.layout.listview_allergy_search, arrayList, userObj.allergies, userObj);
                            list.setAdapter(adapter);
                            // Take input from User : locate the EditText in activity_allergy.xml
                            editsearch = (SearchView) findViewById(R.id.search);
                            editsearch.setOnQueryTextListener(AllergyActivity.this);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            startActivity(new Intent(AllergyActivity.this, LoginActivity.class));
        }


        // if valid allergy, add to myAllergyList, else toast "invalid allergy"

        // add to my allergylist

        // delete the allergy from my



        // display the my allergy list
//        myAllergyAdapter = new ListViewAdapterMyAllergyList(this, myAllergyArraylist);
//        listV_myAllergyList.setAdapter(myAllergyAdapter);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text);
        return false;
    }
}
