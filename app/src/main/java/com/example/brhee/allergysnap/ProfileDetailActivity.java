package com.example.brhee.allergysnap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editFirstName, editLastName, editEmail, editPassword, editPasswordConfirm, editDOB;
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
        //username

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

    }

    private void updateUser() {
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String passwordConfirm = editPasswordConfirm.getText().toString().trim();
        String DOB = editDOB.getText().toString().trim();

        //String username
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
                break;
        }
    }
}
