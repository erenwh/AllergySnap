package com.example.brhee.allergysnap;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class _spash extends AppCompatActivity {

    ImageView imageView;
    Animation pulse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__spash);

        imageView = (ImageView) findViewById(R.id.splashImage);
        pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        imageView.startAnimation(pulse);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms

                // check if any user cache their sign in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    // User is signed in
                    System.out.println("USER LOGGED IN");
                    // redirect to main page
                    startActivity(new Intent(_spash.this, MainActivity.class));
                } else {
                    // No user is signed in
                    System.out.println("NO USER LOGGED IN");
                    // redirect to sign in
                    startActivity(new Intent(_spash.this, LoginActivity.class));
                }
            }
        }, 3000);
    }

    /*@Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // finish the splash activity so it can't be returned to
                _spash.this.finish();
                // create an Intent that will start the second activity
                Intent mainIntent = new Intent(_spash.this, MainActivity.class);
                _spash.this.startActivity(mainIntent);
            }
        }, 5000); // 5000 milliseconds
    }*/
}
