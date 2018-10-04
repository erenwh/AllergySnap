package com.example.brhee.allergysnap;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editFirstName, editLastName, editEmail, editPassword, editPasswordConfirm, editDOB, editUsername;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        editFirstName = findViewById(R.id.first_name_text);
        editLastName = findViewById(R.id.last_name_text);
        editEmail = findViewById(R.id.email_text);
        editPassword = findViewById(R.id.password_text);
        editPasswordConfirm = findViewById(R.id.password_conf_text);
        editDOB = findViewById(R.id.dob_text);
        editUsername = findViewById(R.id.username_text);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        findViewById(R.id.deactivate_button).setOnClickListener(this);
        findViewById(R.id.submit_button).setOnClickListener(this);

    }

    private void updateUser() {
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String passwordConfirm = editPasswordConfirm.getText().toString().trim();
        String DOB = editDOB.getText().toString().trim();
        String username = editUsername.getText().toString().trim();

    }

    private void deactivate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileDetailActivity.this);
        builder.setMessage("Are you sure you want to Deactivate your account?").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Account Deactivated", Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(ProfileDetailActivity.this, LoginActivity.class));

                            }
                            else {
                                String message = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(), "Error Occurred: " + message, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        }).setNegativeButton("Cancel", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_button:

                break;
            case R.id.deactivate_button:
                //TODO: Implement deleting account
                //NOTE: may want to make button red to indicate deleting
                //ALSO: Add another dialog box when it is clicked so it isn't directly deleted
                deactivate();
                break;
        }
    }
}
