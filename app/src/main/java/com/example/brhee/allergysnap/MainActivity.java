package com.example.brhee.allergysnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView cameraBtn = (ImageView) findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(this);

    }

    public void openCameraActivity(View view){
        Intent startCameraActivity= new Intent(MainActivity.this, CameraActivity.class);
        startActivity(startCameraActivity);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //openCameraActivity
            case R.id.cameraBtn:
                System.out.println("cameraBtn works");
                openCameraActivity(v);
                break;
        }
    }
}
