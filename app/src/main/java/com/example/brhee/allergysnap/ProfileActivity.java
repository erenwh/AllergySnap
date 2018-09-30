package com.example.brhee.allergysnap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView name, username;

    private CircleImageView userProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = (TextView) findViewById(R.id.profile_full_name);
        username = (TextView) findViewById(R.id.profile_username);
        userProfileImage = (CircleImageView) findViewById(R.id.profile_picture);

    }
}
