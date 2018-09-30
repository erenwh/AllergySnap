package com.example.brhee.allergysnap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class ResetPasswordActivity extends AppCompatActivity {

    private Button ResetPasswordButton;
    private EditText ResetPasswordEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ResetPasswordButton = (Button) findViewById(R.id.reset_password_button);
        ResetPasswordEmail = (EditText) findViewById(R.id.reset_password_email);
    }
}
