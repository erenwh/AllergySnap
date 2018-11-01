package com.example.brhee.allergysnap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

public class ResultActivity extends AppCompatActivity {

    TextView barcodeResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        barcodeResult = (TextView)findViewById(R.id.barcode_result);

        Bundle bundle = getIntent().getExtras();
        barcodeResult.setText(bundle.getString("barcode_value"));

    }

    public void scanBarcode(View v) {
        Intent intent = new Intent(this, Camera2.class);
        startActivityForResult(intent, 0);
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra("barcode");
                    barcodeResult.setText("Barcode value: " + barcode.displayValue);
                }
                else {
                    barcodeResult.setText("No barcode found!");
                }
            }

        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }*/

}
