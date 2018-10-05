package com.example.brhee.allergysnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {


    private Button signout_button;
    private TextView extra, username;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    // Google login
    private GoogleSignInClient mGoogleSignInClient;
    // Google instance variables
    private GoogleApiClient mGoogleApiClient;

    private String userID;
    private User userObj;

    private CircleImageView userProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("Users");
        final FirebaseUser user = mAuth.getCurrentUser();

        username = (TextView) findViewById(R.id.username);
        extra = (TextView) findViewById(R.id.extra);
        userProfileImage = (CircleImageView) findViewById(R.id.profile_picture);

        if (user != null) {
            userID = user.getUid();
        }
        if (userID == null) {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        }

        // profileDetail Nav Btn
        // cameraNavBtn
        ImageView ProfileDetailNavBtn = (ImageView) findViewById(R.id.profile_picture);
        ProfileDetailNavBtn.setOnClickListener(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Query userData = myRef;
                    userData.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                userObj = dataSnapshot.child(userID).getValue(User.class);
                                setValues();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    // User is signed out
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                }
            }
        };


        /*
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();

        profileUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        fullname = (TextView) findViewById(R.id.profile_full_name);
        username = (TextView) findViewById(R.id.profile_username);

        userProfileImage = (CircleImageView) findViewById(R.id.profile_picture);

        profileUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String myFirstName = dataSnapshot.child("fName").getValue().toString();
                    String myLastName = dataSnapshot.child("lName").getValue().toString();
                    String myUsername = dataSnapshot.child("username").getValue().toString();
                    String myProfileImage = dataSnapshot.child("profilepicture").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.ic_profile_default).into(userProfileImage);

                    if (myFirstName.equals("") && myLastName.equals("")) {
                        fullname.setText("Profile Name");
                    }
                    else {
                        fullname.setText(myFirstName + " " + myLastName);
                    }
                    username.setText(myUsername);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


        signout_button = (Button) findViewById(R.id.profile_signout_button);

        signout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // firebase + google signout
                // firebase logout
                mAuth.signOut();
                // facebook logout
                LoginManager.getInstance().logOut();
                // google logout
                //TODO
                // twitter logout
                //TODO
                finish();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });



    }
    private void setValues() {
//        if (userObj.username != null) {
//            username.setText(userObj.username);
//        }
//
//        if(userObj.fName != null || userObj.lName != null ) {
//            if (userObj.fName == null) {
//                fullname.setText(" " + userObj.lName);
//            }
//            else if (userObj.lName == null) {
//                fullname.setText(userObj.fName+ " ");
//            }
//            else {
//                fullname.setText(userObj.fName + " " + userObj.lName);
//            }
//        }
//        else {
//            fullname.setText("Profile Name");
//        }
        if (userObj.hasPFP && userObj.uri != null) {
            Picasso.get().load(userObj.uri).into(userProfileImage);
        }
        username.setText(userObj.username);
        if (userObj.fName == null) {
            extra.setText(userObj.email);
        } else {
            String fullName = userObj.fName + " " + userObj.lName;
            extra.setText(fullName);
        }



    }

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
    public void onClick(View v) {
        switch(v.getId()){
            // profile detail nav btn
            case R.id.profile_picture:
                startActivity(new Intent(ProfileActivity.this, ProfileDetailActivity.class));
                break;
        }
    }

}
