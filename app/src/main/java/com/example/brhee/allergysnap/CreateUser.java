package com.example.brhee.allergysnap;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class CreateUser extends AppCompatActivity implements View.OnClickListener {


    private EditText editEmail, editUsername, editPassword, editPasswordConfirm;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    boolean emailInUse = false;;
    boolean usernameInUse = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        editEmail = findViewById(R.id.edit_email);
        editUsername = findViewById(R.id.edit_username);
        editPassword = findViewById(R.id.edit_password);
        editPasswordConfirm = findViewById(R.id.edit_password_confirm);

        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.register_button).setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            //handle the already login user
        }
    }

    //Set UserDisplay Name
    private void updateUsername()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!= null)
        {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(editUsername.getText().toString().trim())
                    //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))  // here you can set image link also.
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TESTING", "User profile updated.");
                            }
                        }
                    });
        }
    }

    private void setEmailValidity(boolean email) {
        emailInUse = email;
    }

    private void setUsernameValidity(boolean username) {
        usernameInUse = username;
    }

    private void registerUser() {
        emailInUse = false;
        usernameInUse = false;
        final String username = editUsername.getText().toString().trim();
        final String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String passwordConfirm = editPasswordConfirm.getText().toString().trim();

        if (username.isEmpty()) {
            editUsername.setError(getString(R.string.input_error_name));
            editUsername.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editEmail.setError(getString(R.string.input_error_email));
            editEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError(getString(R.string.input_error_email_invalid));
            editEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editPassword.setError(getString(R.string.input_error_password));
            editPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editPassword.setError(getString(R.string.input_error_password_length));
            editPassword.requestFocus();
            return;
        }

        if (passwordConfirm.isEmpty()) {
            editPasswordConfirm.setError(getString(R.string.input_error_confirm_password));
            editPasswordConfirm.requestFocus();
            return;
        }

        if (!password.equals(passwordConfirm)) {
            editPasswordConfirm.setError(getString(R.string.input_error_match_password));
            editPasswordConfirm.requestFocus();
            return;
        }

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

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            updateUsername();
                            User user = new User(username, email);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if(task.isSuccessful()) {
                                        Toast.makeText(CreateUser.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(CreateUser.this, getString(R.string.registration_failure), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(CreateUser.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                registerUser();
                break;
        }
    }
}
