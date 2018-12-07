package com.example.brhee.allergysnap;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.medialablk.easytoast.EasyToast;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editFirstName, editLastName, editEmail, editPassword, editPasswordConfirm, editDOB, editUsername;
    private CircleImageView profilePicture;
    private ProgressBar progressBar;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private StorageReference mStorage;
    private String userID;
    private User userObj;
    private Uri uri;
    private FirebaseUser user;

    private boolean usernameChanged;
    private boolean emailChanged;
    private boolean pictureChanged;

    boolean emailInUse = false;;
    boolean usernameInUse = false;

    FirebaseAuth mFirebaseAuth;
//    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onStart() {
        super.onStart();
//        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        // // signoutBtn navigate to signin page
        // Button signOutBtn = findViewById(R.id.profile_detail_logout_button);
        // signOutBtn.setOnClickListener(this);
        // mFirebaseAuth = FirebaseAuth.getInstance();
        // mAuthListener = new FirebaseAuth.AuthStateListener() {
        //     @Override
        //     public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        //         if (firebaseAuth.getCurrentUser() == null){
        //             startActivity(new Intent(ProfileDetailActivity.this, LoginActivity.class));
        //         }
        //     }
        // };

        editFirstName = findViewById(R.id.first_name_text);
        editLastName = findViewById(R.id.last_name_text);
        editEmail = findViewById(R.id.email_text);
        //editEmail.setEnabled(false);
        editPassword = findViewById(R.id.password_text);
        editPasswordConfirm = findViewById(R.id.password_conf_text);
        editDOB = findViewById(R.id.dob_text);
        editUsername = findViewById(R.id.username_text);
        progressBar = findViewById(R.id.progressBar);
        profilePicture = findViewById(R.id.profile_picture);

        int color = getResources().getColor(R.color.DarkGray);
        ColorFilter cf = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
        profilePicture.setColorFilter(cf);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("Users");
        user = mAuth.getCurrentUser();
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

        findViewById(R.id.deactivate_button).setOnClickListener(this);
        findViewById(R.id.submit_button).setOnClickListener(this);
        findViewById(R.id.change_photo).setOnClickListener(this);

    }



    private void setValues() {
        progressBar.setVisibility(View.VISIBLE);

        if (userObj != null) {
            editEmail.setText(userObj.email);
            editUsername.setText(userObj.username);

            if (userObj.fName != null) {
                editFirstName.setText(userObj.fName);
            }

            if (userObj.lName != null) {
                editLastName.setText(userObj.lName);
            }

            if (userObj.DOB != null) {
                editDOB.setText(userObj.DOB);
            }

            if (userObj.hasPFP && userObj.uri != null) {
                Picasso.get().load(userObj.uri).into(profilePicture);
                profilePicture.setColorFilter(null);
            }
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
        usernameChanged = false;
        emailChanged = false;
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

        if (username.isEmpty()) {
            editUsername.setError(getString(R.string.input_error_name));
            editUsername.requestFocus();
            return;
        }

        if (username.length() < 3) {
            editUsername.setError("Username must be at least 3 characters");
            editUsername.requestFocus();
            return;
        }

        if (username.length() > 15) {
            editUsername.setError("Username cannot be more than 15 characters");
            editUsername.requestFocus();
            return;
        }

        if (!username.matches("[A-Za-z0-9_]+")) {
            editUsername.setError("Username may only contain letters, numbers and underscore");
            editUsername.requestFocus();
            return;
        }

        if (!DOB.isEmpty()) {
            if (!DOB.matches("\\d{2}-\\d{2}-\\d{4}")) {
                editDOB.setError("Date should be in format dd-mm-yyyy");
                editDOB.requestFocus();
                return;
            }
        }

        if (!userObj.username.equalsIgnoreCase(username))
            usernameChanged = true;

        if(!userObj.email.equals(email)) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editEmail.setError(getString(R.string.input_error_email_invalid));
                editEmail.requestFocus();
                return;
            }
            emailChanged = true;
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
//        if (!email.equals(userObj.email)) {
//            Query emailCheck = FirebaseDatabase.getInstance().getReference("Users").orderByChild("email").equalTo(email);
//            emailCheck.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        setEmailValidity(true);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//            if (emailInUse) {
//                editEmail.setError("Email already in use");
//                editEmail.requestFocus();
//                return;
//            }
//        }

        //Check for username in Database (NOT FUNCTIONING IN ORDER DUE TO FIREBASE THREADING)
//        if (!username.equals(userObj.username)) {
//            Query usernameCheck = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").equalTo(username);
//            usernameCheck.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        setUsernameValidity(true);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//            if (usernameInUse) {
//                editUsername.setError("Username already in use");
//                editUsername.requestFocus();
//                return;
//            }
//        }






        Query usernameCheck = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").equalTo(username);
        usernameCheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && usernameChanged) {
                    editUsername.setError("Username already in use");
                    editUsername.requestFocus();
                    return;
                } else {
                    //Update User in Database and Auth
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                    if (!email.equals(userObj.email)) {
//                        if (user != null) {
//                            if(user.updateEmail(email).isSuccessful()) {
//                                //Checks if email is in use
//                                editEmail.setError("Email already in use");
//                                editEmail.requestFocus();
//                                progressBar.setVisibility(View.GONE);
//                                return;
//                            }
//                        }
//                    }
                    Query emailCheck = FirebaseDatabase.getInstance().getReference("Users").orderByChild("email").equalTo(email);
                    emailCheck.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && emailInUse) {
                                editEmail.setError("Email already in use");
                                editEmail.requestFocus();
                                return;
                            } else {
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                progressBar.setVisibility(View.VISIBLE);

                                userObj.email = email;
                                userObj.username = username;


                                if (!password.isEmpty()) {
                                    if (user != null) {
                                        user.updatePassword(password);
                                    }
                                }

                                if(emailChanged) {
                                    user.updateEmail(email)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                    }
                                                }
                                            });
                                }

                                //Set values to user object
                                if (!firstName.isEmpty() && !lastName.isEmpty()) {
                                    userObj.fName = firstName;
                                    userObj.lName = lastName;
                                }
                                if (!DOB.isEmpty()) {
                                    userObj.DOB = DOB;
                                }

//                                if (pictureChanged) {
//                                    userObj.hasPFP = true;
//                                }

                                if (pictureChanged) {
                                    mStorage = FirebaseStorage.getInstance().getReference();
                                    final StorageReference photoRef = mStorage.child("Profile Pictures").child(userID.toString());
                                    photoRef.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                        @Override
                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                            if (!task.isSuccessful()) {
                                                throw task.getException();
                                            }
                                            return photoRef.getDownloadUrl();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                userObj.uri = task.getResult().toString();
                                                userObj.hasPFP = true;
                                                pictureChanged = false;
                                                FirebaseDatabase.getInstance().getReference("Users")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .setValue(userObj).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        progressBar.setVisibility(View.GONE);
                                                        if (task.isSuccessful()) {
                                                            //Toast.makeText(ProfileDetailActivity.this, "Updated Information Successfully", Toast.LENGTH_LONG).show();
                                                            EasyToast.custom(ProfileDetailActivity.this, "Updated Information Successfully", R.drawable.ic_profile_default, getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);

                                                        } else {
                                                            //Toast.makeText(ProfileDetailActivity.this, "Update Failure: Try again", Toast.LENGTH_LONG).show();
                                                            EasyToast.custom(ProfileDetailActivity.this, "Updated Failure: Try again", R.drawable.ic_profile_default, getResources().getColor(R.color.colorAlert), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);
                                                        }
                                                    }
                                                });


                                            } else {
                                                //Toast.makeText(ProfileDetailActivity.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                EasyToast.custom(ProfileDetailActivity.this, "Updated Failure: " + task.getException().getMessage(), R.drawable.ic_profile_default, getResources().getColor(R.color.colorAlert), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);

                                            }
                                        }
                                    });
                                    //UploadTask uploadTask = photoRef.putFile(uri);
//                                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                                        @Override
//                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                                            if (!task.isSuccessful()) {
//                                                throw task.getException();
//                                            }
//
//                                            // Continue with the task to get the download URL
//                                            return photoRef.getDownloadUrl();
//                                        }
//                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Uri> task) {
//                                            if (task.isSuccessful()) {
//                                                userObj.uri = photoRef;
//                                                userObj.hasPFP = true;
//                                            } else {
//                                                // Handle failures
//                                                // ...
//                                            }
//                                        }
//                                    });

                                } else {

                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(userObj).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressBar.setVisibility(View.GONE);
                                            if (task.isSuccessful()) {
                                                //Toast.makeText(ProfileDetailActivity.this, "Updated Information Successfully", Toast.LENGTH_LONG).show();
                                                EasyToast.custom(ProfileDetailActivity.this, "Updated Information Successfully", R.drawable.ic_profile_default, getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);

                                            } else {
                                                //Toast.makeText(ProfileDetailActivity.this, "Update Failure: Try again", Toast.LENGTH_LONG).show();
                                                EasyToast.custom(ProfileDetailActivity.this, "Update Failure: Try again", R.drawable.ic_profile_default, getResources().getColor(R.color.colorAlert), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);
                                            }
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

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
        builder.setMessage("Are you sure you want to deactivate your account?").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final DatabaseReference usernameRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

                if (user != null) {
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                usernameRef.removeValue();
                                if (userObj.hasPFP) {
                                    StorageReference pPath = FirebaseStorage.getInstance().getReferenceFromUrl(userObj.uri);
                                    pPath.delete();
                                }
                                //Toast.makeText(getApplicationContext(), "Account Deactivated", Toast.LENGTH_LONG).show();
                                EasyToast.custom(getApplicationContext(), "Account Deactivated", R.drawable.ic_profile_default, getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);
                                startActivity(new Intent(ProfileDetailActivity.this, LoginActivity.class));
                            }
                            else {
                                String message = task.getException().getMessage();
                                EasyToast.custom(getApplicationContext(), "Error Occurred: " + message, R.drawable.ic_profile_default, getResources().getColor(R.color.colorAlert), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);
                                //Toast.makeText(getApplicationContext(), "Error Occurred: " + message, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        }).setNegativeButton("Cancel", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void changePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK) {
//            Uri uri = data.getData();
//            mStorage = FirebaseStorage.getInstance().getReference();
//            StorageReference photoRef = mStorage.child("Profile Pictures").child(userID.toString() + "_pfp");
//            photoRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                    user.g
//                   // user
//                }
//            });
            pictureChanged = true;
            uri = data.getData();
            Picasso.get().load(uri).into(profilePicture);
        } else {
            pictureChanged = false;
            uri = null;
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));

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
            case R.id.change_photo:
                changePhoto();
                break;
            // signOut btn
            // case R.id.profile_detail_logout_button:
            //     mFirebaseAuth.signOut();
//                startActivity(new Intent(ProfileDetailActivity.this, LoginActivity.class));

        }
    }
}
