package com.example.brhee.allergysnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileDetailActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        // signoutBtn navigate to signin page
        Button signOutBtn = findViewById(R.id.profile_detail_logout_button);
        signOutBtn.setOnClickListener(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(ProfileDetailActivity.this, LoginActivity.class));
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            // signOut btn
            case R.id.profile_detail_logout_button:
                mFirebaseAuth.signOut();
//                startActivity(new Intent(ProfileDetailActivity.this, LoginActivity.class));
                break;
        }
    }
}
