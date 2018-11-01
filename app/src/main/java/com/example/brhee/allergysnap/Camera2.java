package com.example.brhee.allergysnap;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import android.Manifest;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiDetector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class Camera2 extends AppCompatActivity {

    private SurfaceView sv;
    private TextView tv;

    private static final int requestPermissionID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera2);
        sv = (SurfaceView)findViewById(R.id.sv_barcode);
        tv = (TextView) findViewById(R.id.tv_barcode);
        startCameraSource();
    }

    // Old CameraSource for Barcode only
    /*
    private void createCameraSource() {

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();

        final CameraSource cs = new CameraSource.Builder(this, barcodeDetector).setAutoFocusEnabled(true).setRequestedPreviewSize(1600, 1024).build();

        sv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(Camera2.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                try {
                    cs.start(sv.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cs.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("barcode", barcodes.valueAt(0)); // Gets the latest barcode from the array
                    setResult(CommonStatusCodes.SUCCESS, intent);
                    finish();
                }
            }
        });
    }*/

    private void startCameraSource() {


        // Create the BarcodeDetector
        final BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();

        // Create the TextRecognizer
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        // Create MultiDetector
        MultiDetector multiDetector = new MultiDetector.Builder().add(barcodeDetector).add(textRecognizer).build();

        // Create CameraSource
        final CameraSource cameraSource = new CameraSource.Builder(this, multiDetector).setAutoFocusEnabled(true).setRequestedPreviewSize(1600, 1024).build();;

        if (!multiDetector.isOperational()) {
            Log.d("cam2activity","Detector dependencies not loaded yet");
        } else {

            /**
             * Add call back to SurfaceView and check if camera permission is granted.
             * If permission is granted we can start our cameraSource and pass it to surfaceView
             */

            sv.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(Camera2.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    requestPermissionID);
                            return;
                        }
                        cameraSource.start(sv.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                /**
                 * Release resources for cameraSource
                 */

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });

            //Set the TextRecognizer's Processor.
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                /**
                 * Detect all the text from camera using TextBlock and the values into a stringBuilder
                 * which will then be set to the textView.
                 * */

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0 ){

                        tv.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i=0;i<items.size();i++){
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                tv.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }

        /**
          * Callback for Barcode Scanner
          */
        sv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(Camera2.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                try {
                    cameraSource.start(sv.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("barcode", barcodes.valueAt(0)); // Gets the latest barcode from the array
                    setResult(CommonStatusCodes.SUCCESS, intent);
                    finish();
                }
            }
        });
    }

}
