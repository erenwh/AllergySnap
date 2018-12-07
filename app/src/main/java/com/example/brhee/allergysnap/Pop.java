package com.example.brhee.allergysnap;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.widget.TextView;

import java.util.ArrayList;

public class Pop extends AppCompatActivity {

    public TextView conflictView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);

        conflictView = (TextView)findViewById(R.id.conflict_result);
        conflictView.setMovementMethod(new ScrollingMovementMethod());

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*1), (int)(height*0.1));

        Bundle bundle = getIntent().getExtras();
        int size = bundle.getInt("size");

        String conflictText = "";

        for (int x = 0; x < size; x++) {
            conflictText += bundle.getString("allergy" + x);
            conflictText += " allergy!\n";
        }
        conflictView.setText(conflictText);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                finish();
//            }
//        }, 10000);
    }
}
