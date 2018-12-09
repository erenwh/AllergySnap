package com.example.brhee.allergysnap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
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
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.medialablk.easytoast.EasyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
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

    // // Track user login state
    // FirebaseAuth.AuthStateListener mAuthListenr;

    // Google instance variables
    private GoogleApiClient mGoogleApiClient;
    // Google login
    private GoogleSignInClient mGoogleSignInClient;

    // Facebook login
    private CallbackManager mCallbackManager;

    // User Info
    private String FName;
    private String LName;
    private String Email;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        int PERMISSION_ALL = 2;
        String[] PERMISSIONS = {
                //android.Manifest.permission.READ_CONTACTS,
                //android.Manifest.permission.WRITE_CONTACTS,
                //android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                //android.Manifest.permission.READ_SMS,
                android.Manifest.permission.CAMERA,
                //Manifest.permission.ACCESS_FINE_LOCATION
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        // views
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);

        // Buttons
        findViewById(R.id.FirebaseSighInBtn).setOnClickListener(this);

        //findViewById(R.id.login_forgot_password_button).setOnClickListener(this);

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

//        // user's login state
//        mAuthListenr = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                // if user is logined in
//                if (firebaseAuth.getCurrentUser() != null){
//                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                }
//            }
//        };

        // google SignInOption
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestIdToken("685757815116-t224e659fctfu3lonsruuc8is0jd27ht.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        ImageButton FacebookLoginButton = findViewById(R.id.facebook_button);
        FacebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("email", "public_profile", "user_photos"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());

                        createFacebookFirebaseUser(loginResult);

                        System.out.println("LOGIN SUCCESS!");
                        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(myIntent);
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                        // ...
                    }
                });
            }
        });

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void createFacebookFirebaseUser(com.facebook.login.LoginResult loginResult) {
        // Facebook Email address
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        Log.v("LoginActivity Response ", response.toString());

                        try {
                            FName = object.getString("first_name");
                            LName = object.getString("last_name");
                            Email = object.getString("email");

                            //System.out.println("FirstName:"+ FName + "LastName:"+LName + "Email:"+Email);

                            // Create random generator
                            Random generator = new Random();
                            int randomNumber = generator.nextInt(555555) + generator.nextInt(555554);

                            // Generate username
                            String username = FName + LName + randomNumber;
                            System.out.println(username);

                            User user = new User(username, Email);
                            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful()) {
                                            //Toast.makeText(LoginActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                            EasyToast.custom(LoginActivity.this, getString(R.string.registration_success), R.drawable.ic_profile_default, getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);

                                        } else {
                                            //Toast.makeText(LoginActivity.this, getString(R.string.registration_failure), Toast.LENGTH_LONG).show();
                                            EasyToast.custom(LoginActivity.this, getString(R.string.registration_failure), R.drawable.ic_profile_default, getResources().getColor(R.color.colorAlert), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);
                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name, first_name, last_name,email,gender, birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }



    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            updateUI(firebaseUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            EasyToast.custom(LoginActivity.this, "Authentication failed.", R.drawable.ic_profile_default, getResources().getColor(R.color.colorAlert), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);
                        }

                        // ...
                    }
                });
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.FirebaseSighInBtn:
                FirebaseSignin(mEmailField.getText().toString(), mPasswordField.getText().toString());
                System.out.println("FirebaseSignin HIT");
                break;
            case R.id.forgot_password_button:
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
                break;
            case R.id.sign_up_button:
                startActivity(new Intent(LoginActivity.this, CreateUser.class));
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
                            EasyToast.custom(LoginActivity.this, "Authentication Failed.", R.drawable.ic_profile_default, getResources().getColor(R.color.colorAlert), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);
                            //Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
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
//        mFirebaseAuth.addAuthStateListener(mAuthListenr);

//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
//        updateUI(currentUser);


        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        updateUI(account);
    }

    // GOOGLE SignIn
    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        System.out.println("--------insdie of google sign in ()------");
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // FACEBOOK SignIn
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // GOOGLE SignIn
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount acct = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(acct);
                // Signed in successfully, show authenticated UI.
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
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

//                                System.out.println("firstname: " + personName + " lastname: " + personFamilyName + " email: + " + personEmail);
                                //Toast.makeText(LoginActivity.this, "Name : " + personName + "UserId : " + personId, Toast.LENGTH_SHORT).show();

                                // Create random generator
                                Random generator = new Random();
                                int randomNumber = generator.nextInt(555555) + generator.nextInt(555545);

                                // Generate username
                                String userName = (personName + personFamilyName + randomNumber).replace(" ", "");
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
                                            EasyToast.custom(LoginActivity.this, getString(R.string.registration_success), R.drawable.ic_profile_default, getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);
                                            //Toast.makeText(LoginActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        } else {
                                            EasyToast.custom(LoginActivity.this, getString(R.string.registration_failure), R.drawable.ic_profile_default, getResources().getColor(R.color.colorAlert), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);
                                            //Toast.makeText(LoginActivity.this, getString(R.string.registration_failure), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            EasyToast.custom(LoginActivity.this, "Authentication Failed.", R.drawable.ic_profile_default, getResources().getColor(R.color.colorAlert), getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);
                            //Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
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
