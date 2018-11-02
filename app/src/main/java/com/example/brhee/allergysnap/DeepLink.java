package com.example.brhee.allergysnap;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class DeepLink extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = DeepLink.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_link);

        // Button click listener
        findViewById(R.id.buttonOk).setOnClickListener(this);
    }
    @Override
    protected void onStart() {
        super.onStart();

        // Check for link in intent
        if (getIntent() != null && getIntent().getData() != null) {
            Uri data = getIntent().getData();

            Log.d(TAG, "data:" + data);
            ((TextView) findViewById(R.id.deepLinkText))
                    .setText(getString(R.string.deep_link_fmt, data.toString()));
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonOk) {
            finish();
        }
    }
}
