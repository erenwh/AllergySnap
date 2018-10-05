package com.example.brhee.allergysnap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener {

    // Consts
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    // View elements
    private EditText mEmailField;
    private EditText mPasswordField;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;

    // Track user login state
    FirebaseAuth.AuthStateListener mAuthListenr;

    // Google instance variables
    private GoogleApiClient mGoogleApiClient;

    // Google login
    private GoogleSignInClient mGoogleSignInClient;

    // User Info
    private String FName;
    private String LName;
    private String Email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // views
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);

        // Buttons
        findViewById(R.id.FirebaseSighInBtn).setOnClickListener(this);
        findViewById(R.id.sign_up_button).setOnClickListener(this);
        findViewById(R.id.forgot_password_button).setOnClickListener(this);

        // initialize google login btn
        ImageView googleBtn = (ImageView) findViewById(R.id.googleSignIn);
        googleBtn.setOnClickListener(this);

        //TODO: twitter social login
        ImageView twitterBtn = (ImageView) findViewById(R.id.twitterSignIn);
        twitterBtn.setOnClickListener(this);


        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        // user's login state
        mAuthListenr = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // if user is logined in
                if (firebaseAuth.getCurrentUser() != null){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        };

        // google SignInOption
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestIdToken("685757815116-t224e659fctfu3lonsruuc8is0jd27ht.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.FirebaseSighInBtn:
                FirebaseSignin(mEmailField.getText().toString(), mPasswordField.getText().toString());
                System.out.println("FirebaseSignin HIT");
                break;

            case R.id.sign_up_button:
                startActivity(new Intent(LoginActivity.this, CreateUser.class));
                break;

            case R.id.forgot_password_button:
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
                break;
            case R.id.googleSignIn:
                System.out.println("google signin Btn HIT");
                googleSignIn();
//                finish();
//                startActivity(getIntent());
                break;
//            case R.id.fbSignIn:
//                System.out.println("facebook signin Btn HIT");
////                googleSignIn();
//                break;
            case R.id.twitterSignIn:
                System.out.println("twitter signin Btn HIT");
//                googleSignIn();
                break;


        }
    }

    private void FirebaseSignin(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        // showProgressDialog();
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
//                            updateUI(user);
                            System.out.println("LOGIN SUCCESS!");
                            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(myIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            System.out.println("LOGIN FAILED!");
                        }
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    @Override
    public void onStart() {
        super.onStart();
        // check user's login state
        mFirebaseAuth.addAuthStateListener(mAuthListenr);

//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
//        updateUI(currentUser);


        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        updateUI(account);
    }

//
//    private void updateUI(FirebaseUser user) {
//        System.out.println("TODO UI UPDATE");
//
//    }




    // GOOGLE SignIn
    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        System.out.println("--------insdie of google sign in ()------");
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("---we insdie onactivityResult");
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                System.out.println("we inside of google signin");
                GoogleSignInAccount acct = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(acct);
                // Signed in successfully, show authenticated UI.
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                System.out.println("signin google failing -------------T.T");
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
//                            updateUI(user);

                            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                            if (acct != null) {
                                String personName = acct.getDisplayName();
                                String personGivenName = acct.getGivenName();
                                String personFamilyName = acct.getFamilyName();
                                String personEmail = acct.getEmail();
                                String personId = acct.getId();
                                Uri personPhoto = acct.getPhotoUrl();

                                System.out.println("firstname: " + personName + " lastname: " + personFamilyName + " email: + " + personEmail);
                                Toast.makeText(LoginActivity.this, "Name : " + personName + "UserId : " + personId, Toast.LENGTH_SHORT).show();

                                // Create random generator
                                Random generator = new Random();
                                int randomNumber = generator.nextInt(555555) + generator.nextInt(555545);

                                // Generate username
                                String userName = personName + personFamilyName + randomNumber;
                                System.out.println(userName);

                                //add to firebase database
                                User user = new User(userName, personEmail);
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(LoginActivity.this, getString(R.string.registration_failure), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

//    private void updateUI(FirebaseUser user) {
//                System.out.println("google sign in UI UPDATE");
//        // get user information
//        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
//        if (acct != null) {
//            String personName = acct.getDisplayName();
//            String personGivenName = acct.getGivenName();
//            String personFamilyName = acct.getFamilyName();
//            String personEmail = acct.getEmail();
//            String personId = acct.getId();
//            Uri personPhoto = acct.getPhotoUrl();
//
//            Toast.makeText(this, "Name : " + personName + "UserId : " + personId, Toast.LENGTH_SHORT).show();
//        }
//    }

    private void updateUI(FirebaseUser user) {
        System.out.println("TODO UI UPDATE");
    }


}
