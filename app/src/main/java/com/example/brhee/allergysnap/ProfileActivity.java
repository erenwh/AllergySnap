package com.example.brhee.allergysnap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import javax.xml.datatype.Duration;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {


    private Button signout_button;
    private TextView extra, username, ingredients, barcodes, qrcodes;
    private ListView profileFeed;

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
    private ArrayList<UserItem> items = new ArrayList<>();

    private CircleImageView userProfileImage;

    // Firebase Invite
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private static final int REQUEST_INVITE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
            myRef = mFirebaseDatabase.getReference("Users/"+userID);
        }
        if (userID == null) {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        }


        username = findViewById(R.id.username);
        extra = findViewById(R.id.extra);
        ingredients = findViewById(R.id.ingredient_scans);
        barcodes = findViewById(R.id.barcode_scans);
        qrcodes = findViewById(R.id.qr_scans);
        userProfileImage = (CircleImageView) findViewById(R.id.profile_picture);
        profileFeed = findViewById(R.id.prof_feed);



        // Firebase Invite
        // Invite button click listener
        findViewById(R.id.inviteButton).setOnClickListener(this);

        // Check for App Invite invitations and launch deep-link activity if possible.
        // Requires that an Activity is registered in AndroidManifest.xml to handle
        // deep-link URLs.
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData data) {
                        if (data == null) {
                            Log.d(TAG, "getInvitation: no data");
                            return;
                        }

                        // Get the deep link
                        Uri deepLink = data.getLink();

                        // Extract invite
                        FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(data);
                        if (invite != null) {
                            String invitationId = invite.getInvitationId();
                        }

                        // Handle the deep link
                        // [START_EXCLUDE]
                        Log.d(TAG, "deepLink:" + deepLink);
                        if (deepLink != null) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setPackage(getPackageName());
                            intent.setData(deepLink);

                            startActivity(intent);
                        }
                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });

        // profileDetail Nav Btn
        // cameraNavBtn
        ImageView ProfileDetailNavBtn = (ImageView) findViewById(R.id.profile_picture);
        ProfileDetailNavBtn.setOnClickListener(this);
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

                            // add all the medications and allergies to arraylist and sort them
                            items.clear();
                            items.addAll(userObj.medications);
                            items.addAll(userObj.allergies);
                            Collections.sort(items);
                            Log.d(TAG, "items sorted");

                            // set Text values
                            setValues();

                            // display profile feed
                            ProfileFeedAdapter adapter = new ProfileFeedAdapter(ProfileActivity.this, R.layout.adapter_profile_feed, items);
                            profileFeed.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

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

    private void showMessage(String msg) {
        ViewGroup container = findViewById(R.id.snackbarLayout);
        Snackbar.make(container, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        showMessage(getString(R.string.google_play_services_error));
    }

    /**
     * User has clicked the 'Invite' button, launch the invitation UI with the proper
     * title, message, and deep link
     */
    // [START on_invite_clicked]
    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }
    // [END on_invite_clicked]

    // [START on_activity_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // [START_EXCLUDE]
                //showMessage(getString(R.string.send_failed));
                // [END_EXCLUDE]
            }
        }
    }
    // [END on_activity_result]

    


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

        if (userObj != null) {
            ingredients.setText(userObj.scans.get(0).toString());
            barcodes.setText(userObj.scans.get(1).toString());
            qrcodes.setText(userObj.scans.get(2).toString());
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
            case R.id.inviteButton:
                onInviteClicked();
                break;
        }
    }

}
