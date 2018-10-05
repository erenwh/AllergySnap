package com.example.brhee.allergysnap;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    Context context;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference profileUser;

    private Button signout_button;

    private TextView fullname, username;

    private String currentUserID;

    private CircleImageView userProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();

        profileUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        fullname = (TextView) findViewById(R.id.profile_full_name);
        username = (TextView) findViewById(R.id.profile_username);

        // This will cause the program to crash if a user is not found in the database.
        //userProfileImage = (CircleImageView) findViewById(R.id.profile_picture);

        profileUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //String myFirstName = dataSnapshot.child("firstname").getValue().toString();
                    //String myLastName = dataSnapshot.child("lastname").getValue().toString();
                    String myUsername = dataSnapshot.child("username").getValue().toString();
                    //String myProfileImage = dataSnapshot.child("profilepicture").getValue().toString();

                    //Picasso.get().load(myProfileImage).placeholder(R.drawable.ic_profile_default).into(userProfileImage);

                    //fullname.setText(myFirstName + " " + myLastName);
                    username.setText(myUsername);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        signout_button = (Button) findViewById(R.id.profile_signout_button);

        signout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));

            }
        });

    }

    private void InitializeFields() {

    }
}
