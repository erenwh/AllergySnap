package com.example.brhee.allergysnap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.medialablk.easytoast.EasyToast;

public class ResetPasswordActivity extends AppCompatActivity {

    private Button ResetPasswordButton;
    private EditText ResetPasswordEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        ResetPasswordButton = (Button) findViewById(R.id.reset_password_button);
        ResetPasswordEmail = (EditText) findViewById(R.id.reset_password_email);

        ResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userEmail = ResetPasswordEmail.getText().toString();

                if (TextUtils.isEmpty(userEmail)) {
                    //Toast.makeText(ResetPasswordActivity.this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                    EasyToast.custom(ResetPasswordActivity.this, "Please enter a valid email address.", R.drawable.ic_profile_detail_email, getResources().getColor(R.color.colorAlert), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);

                }
                else {
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                //Toast.makeText(ResetPasswordActivity.this, "Please check your email for further instructions.", Toast.LENGTH_SHORT).show();
                                EasyToast.custom(ResetPasswordActivity.this, "Please check your email for further instructions.", R.drawable.ic_profile_detail_email, getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);
                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                            }
                            else {
                                String message = task.getException().getMessage();
                                //Toast.makeText(ResetPasswordActivity.this, "Error Occured:" + message, Toast.LENGTH_SHORT).show();
                                EasyToast.custom(ResetPasswordActivity.this, "Error Occured:" + message, R.drawable.ic_profile_default, getResources().getColor(R.color.colorAlert), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);
                            }
                        }
                    });
                }

            }
        });
    }
}
