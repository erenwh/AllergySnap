package com.example.brhee.allergysnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView name, username;
    private CircleImageView userProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = (TextView) findViewById(R.id.profile_full_name);
        username = (TextView) findViewById(R.id.profile_username);
        userProfileImage = (CircleImageView) findViewById(R.id.profile_picture);

        // profileDetail Nav Btn
        // cameraNavBtn
        ImageView ProfileDetailNavBtn = (ImageView) findViewById(R.id.profile_picture);
        ProfileDetailNavBtn.setOnClickListener(this);
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
