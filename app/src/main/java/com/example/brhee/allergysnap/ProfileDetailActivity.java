package com.example.brhee.allergysnap;

import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editFirstName, editLastName, editEmail, editPassword, editPasswordConfirm, editDOB, editUsername;
    private ProgressBar progressBar;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private String userID;
    private User userObj;

    boolean emailInUse = false;;
    boolean usernameInUse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        editFirstName = findViewById(R.id.first_name_text);
        editLastName = findViewById(R.id.last_name_text);
        editEmail = findViewById(R.id.email_text);
        //editEmail.setEnabled(false);
        editPassword = findViewById(R.id.password_text);
        editPasswordConfirm = findViewById(R.id.password_conf_text);
        editDOB = findViewById(R.id.dob_text);
        editUsername = findViewById(R.id.username_text);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("Users");
        final FirebaseUser user = mAuth.getCurrentUser();
//        userObj = new User(user.getDisplayName(), user.getEmail());

        if (user != null) {
            userID = user.getUid();
        }
        Query userData = myRef;
        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userObj = dataSnapshot.child(userID).getValue(User.class);
                    setValues();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // ...
//                if(userObj != null) {
//                    setValues(dataSnapshot);
//                } else {
//                    //userObj = new User(user.);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // ...
//            }
//        });

        //editUsername.setText(user.getDisplayName());
        //editEmail.setText(user.getEmail());


        findViewById(R.id.deactivate_button).setOnClickListener(this);
        findViewById(R.id.submit_button).setOnClickListener(this);

    }



    private void setValues() {

        Log.d("EditProfileActivity", "Extracted data for User" + userID);

        editEmail.setText(userObj.email);
        editUsername.setText(userObj.username);

        if(userObj.fName != null) {
            editFirstName.setText(userObj.fName);
        }

        if(userObj.lName != null) {
            editLastName.setText(userObj.lName);
        }

        if(userObj.DOB != null) {
            editDOB.setText(userObj.DOB);
        }


        progressBar.setVisibility(View.GONE);

    }

    private void setEmailValidity(boolean email) {
        emailInUse = email;
    }

    private void setUsernameValidity(boolean username) {
        usernameInUse = username;
    }

    private void updateValues() {
        emailInUse = false;
        usernameInUse = false;
        final String firstName = editFirstName.getText().toString().trim();
        final String lastName = editLastName.getText().toString().trim();
        final String email = editEmail.getText().toString().trim();
        final String password = editPassword.getText().toString().trim();
        String passwordConfirm = editPasswordConfirm.getText().toString().trim();
        final String DOB = editDOB.getText().toString().trim();
        final String username = editUsername.getText().toString().trim();

        //Validate inputs
        if (!firstName.isEmpty() && lastName.isEmpty()) {
            editLastName.setError("Last name missing");
            editLastName.requestFocus();
            return;
        }

        if (firstName.isEmpty() && !lastName.isEmpty()) {
            editLastName.setError("First name missing");
            editLastName.requestFocus();
            return;
        }

        if (!DOB.isEmpty()) {
            if (!DOB.matches("\\d{2}-\\d{2}-\\d{4}")) {
                editDOB.setError("Date should be in format dd-mm-yyyy");
                editDOB.requestFocus();
                return;
            }
        }

        if (!password.isEmpty() && password.length() < 6) {
            editPassword.setError(getString(R.string.input_error_password_length));
            editPassword.requestFocus();
            return;
        }

        if (passwordConfirm.isEmpty() && !password.isEmpty()) {
            editPasswordConfirm.setError(getString(R.string.input_error_confirm_password));
            editPasswordConfirm.requestFocus();
            return;
        }

        if (!password.equals(passwordConfirm)) {
            editPasswordConfirm.setError(getString(R.string.input_error_match_password));
            editPasswordConfirm.requestFocus();
            return;
        }

        //Check for email in Database (NOT FUNCTIONING IN ORDER DUE TO FIREBASE THREADING)
        if (!email.equals(userObj.email)) {
            Query emailCheck = FirebaseDatabase.getInstance().getReference("Users").orderByChild("email").equalTo(email);
            emailCheck.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        setEmailValidity(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            if (emailInUse) {
                editEmail.setError("Email already in use");
                editEmail.requestFocus();
                return;
            }
        }

        //Check for username in Database (NOT FUNCTIONING IN ORDER DUE TO FIREBASE THREADING)
        if (!username.equals(userObj.username)) {
            Query usernameCheck = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").equalTo(username);
            usernameCheck.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        setUsernameValidity(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if (usernameInUse) {
                editUsername.setError("Username already in use");
                editUsername.requestFocus();
                return;
            }
        }






        Query usernameCheck = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").equalTo(username);
        usernameCheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    editUsername.setError("Username already in use");
                    editUsername.requestFocus();
                    return;
                } else {
                    //Update User in Database and Auth
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (!email.equals(userObj.email)) {
                        if (user != null) {
                            if(!user.updateEmail(email).isSuccessful()) {
                                //Checks if email is in use
                                editEmail.setError("Email already in use");
                                editEmail.requestFocus();
                                progressBar.setVisibility(View.GONE);
                                return;
                            }
                        }
                    }
                    progressBar.setVisibility(View.VISIBLE);

                    userObj.email = email;
                    userObj.username = username;


                    if (!password.isEmpty()) {
                        if (user != null) {
                            user.updatePassword(password);
                        }
                    }

                    //Set values to user object
                    if (!firstName.isEmpty() && !lastName.isEmpty()) {
                        userObj.fName = firstName;
                        userObj.lName = lastName;
                    }
                    if (!DOB.isEmpty()) {
                        userObj.DOB = DOB;
                    }

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(userObj).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileDetailActivity.this, "Updated Information Successfully", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ProfileDetailActivity.this, "Update Failure: Try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



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
                updateValues();
                break;
            case R.id.deactivate_button:
                deactivate();
                break;
        }
    }
}
