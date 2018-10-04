package com.example.brhee.allergysnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ProfileDetailActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        // signoutBtn navigate to signin page
        Button signOutBtn = findViewById(R.id.profile_detail_logout_button);
        signOutBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            // signOut btn
            case R.id.profile_detail_logout_button:
                startActivity(new Intent(ProfileDetailActivity.this, LoginActivity.class));
                break;
        }
    }
}
