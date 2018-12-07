package com.example.brhee.allergysnap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.medialablk.easytoast.EasyToast;

import java.util.ArrayList;
import java.util.Arrays;


public class AllergyActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    //Firebase variable
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseUser user;
    private User userObj;
    private String userID;
    private ProgressBar progressbar;
    private int idcounter;

    // search allergy variable
    ListView    list;
    ListViewAdapter adapter;
    SearchView editsearch;

    // my Allergy List variable
    String[]    allergyNameList;
    ArrayList<Allergy> allergyNameList_user = new ArrayList<Allergy>();
    ListView    listV_myAllergyList;
    ListViewAdapterMyAllergyList myAllergyAdapter;

    public BottomNavigationView navigation;
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergy);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        progressbar = findViewById(R.id.progressBar);
        progressbar.setVisibility(View.VISIBLE);
        Button addBtn = (Button) findViewById(R.id.btnAddToAllergyList_User);

        // navigation bar
        toolbar = getSupportActionBar();
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_allergies);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        // check with allergy database
        // Generate allergysnap allergy data
        allergyNameList = new String[]{"egg", "fish", "shellfish", "fruit", "garlic", "hot peppers", "oats", "meat", "milk", "peanut", "rice", "sesame", "soy", "sulfites", "tartrazine", "tree nut", "wheat", "tetracycline", "dilantin", "tegretol", "penicillin", "cephalosporins", "sulfonamides", "pollen", "cat", "dog", "insect sting", "mold", "perfume", "cosmetics", "semen", "latex", "water", "house dust mite", "nickel", "gold", "chromium", "cobalt", "formaldehyde", "fungicide", "dimethylaminopropylamine", "latex", "paraphenylenediamine", "glyceryl monothioglycolate", "toluenesulfonamide formaldehyde"};
        Arrays.sort(allergyNameList);
        for (int i = 0; i < allergyNameList.length; i++) {
            Allergy allergy = new Allergy(allergyNameList[i]);
            // Bind all strings into an array
            allergyNameList_user.add(allergy);
        }

        // Take input from User : locate the EditText in activity_allergy.xml
        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(AllergyActivity.this);
        final CharSequence query = editsearch.getQuery();

        // users own allergy list (copy allergysnap allerylist and append users added allergylist)
        addBtn.setOnClickListener(new View.OnClickListener() {
//            final String queryString = query.toString();
            @Override
            public void onClick(View v) {
                System.out.println("adding allergy: " + query.toString());
                if (query.toString().isEmpty()){
                    AlertDialog.Builder adbuilder1 = new AlertDialog.Builder(AllergyActivity.this);
                    adbuilder1.setMessage("please type at least one allergy")
                            .setTitle("Empty Text");
                    AlertDialog dialog1 = adbuilder1.create();
                    dialog1.show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Are you sure you want to ADD " + query.toString() + " to your allergy list?").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                                boolean duplicate = false;
                                if(userObj.allergies != null){
                                    for(Allergy alle: userObj.allergies){
                                        if(alle.getName().equalsIgnoreCase(query.toString())){
                                            AlertDialog.Builder adbuilder = new AlertDialog.Builder(AllergyActivity.this);
                                            adbuilder.setMessage("Allergy already added")
                                                    .setTitle("Duplicate Allergy");
                                            AlertDialog dialog1 = adbuilder.create();
                                            dialog1.show();
//                                            editsearch.setQuery("", false);
                                            duplicate = true;
                                        }
                                    }
                                }
                                // add to firebase
                                if (!duplicate){
                                    if (userObj.allergies == null){
                                        userObj.allergies = new ArrayList<>();
                                    }
                                    else{
                                        idcounter = userObj.allergies.size()-1;
//                                        Allergy allergy = new Allergy(query.toString());
                                        Allergy allergy = new Allergy(query.toString(), ++idcounter);
                                        allergy.timeAdded = System.currentTimeMillis();
                                        allergyNameList_user.add(allergy);
                                        userObj.allergies.add(allergy);
                                        FirebaseDatabase.getInstance().getReference("Users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(userObj);

                                        //Toast.makeText(AllergyActivity.this, "Successfully Added " + query.toString() + " to your allergy list", Toast.LENGTH_LONG).show();
                                        EasyToast.custom(AllergyActivity.this, "Successfully added " + query.toString() + " to your allergy list.", R.drawable.ic_allergies_24dp, getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);

                                    }
                                }
                        }
                    }).setNegativeButton("Cancel", null);
                    AlertDialog alert = builder.create();
                    alert.show();
//                    editsearch.setQuery("", false);
                }
            }
        });

        // setting listview
        // ********* search allergy ********//
        //  ******** ALLERGY SEARCH *************** //
        list = (ListView) findViewById(R.id.listview_search);
        //  ******** MY ALLERGY LIST *************** //
        listV_myAllergyList = (ListView) findViewById(R.id.listview_myAllergyList);


        // connect to firebase and pull user's information
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("Users");
        user = mAuth.getCurrentUser();
        if (user != null){
            userID = user.getUid();
            final Query userData = myRef;
            userData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        userObj = dataSnapshot.child(userID).getValue(User.class);
                        if (userObj != null){
                            // my allergy list
                            myAllergyAdapter = new ListViewAdapterMyAllergyList(AllergyActivity.this, R.layout.listview_allergy_myallergy, userObj.allergies, userObj);
                            listV_myAllergyList.setAdapter(myAllergyAdapter);
                            // show the allergy search list & pass results to ListViewAdapter Class

                            //adapter = new ListViewAdapter(AllergyActivity.this, arrayList, userObj);
                            adapter = new ListViewAdapter(AllergyActivity.this, allergyNameList_user, userObj);
                            list.setAdapter(adapter);
                        }
                        progressbar.setVisibility(View.INVISIBLE);
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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_medications:
                    startActivity(new Intent(AllergyActivity.this, MedicationActivity.class));
                    return true;
                case R.id.navigation_allergies:
                    //startActivity(new Intent(AllergyActivity.this, AllergyActivity.class));
                    return true;
                case R.id.navigation_main:
                    startActivity(new Intent(AllergyActivity.this, MainActivity.class));
                    return true;
                case R.id.navigation_conflicts:
                    startActivity(new Intent(AllergyActivity.this, ConflictActivity.class));
                    return true;
                case R.id.navigation_profile:
                    startActivity(new Intent(AllergyActivity.this, ProfileActivity.class));
                    return true;
            }
            return false;
        }
    };

    // Changes button back
    @Override
    protected void onResume() {
        super.onResume();
        navigation.getMenu().getItem(3).setChecked(true);
    }
}
