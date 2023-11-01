package com.example.mychat;



import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText edtEmail;
    private EditText edtPass;
    private CheckBox checkRemember;
    private TextView btnForgot;
    private Button btnLogin;
    private LinearLayout btnLogWithFacebook;
    private LinearLayout btnLogWithGoogle;
    private TextView btnToSignUp;
    private ProgressBar progressBar;

    private GoogleSignInClient signInClient;
    private final int REQUEST_CODE=100;

//    CallbackManager mCallbackManager;
    FirebaseAuth auth;
    FirebaseUser user;

    private boolean logWithFacebook=false;
    private boolean logWithGoogle=false;
    LoginButton loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth=FirebaseAuth.getInstance();

//        AccessToken accessToken=AccessToken.getCurrentAccessToken();
//        if (accessToken!=null && !accessToken.isExpired()){
//            startActivity(new Intent(LoginActivity.this,MoreActivity.class));
//        }
//        AppEventsLogger.activateApp(this);
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(getApplication());

        loginButton = (LoginButton) findViewById(R.id.login_button);

        edtEmail=findViewById(R.id.edtEmail);
        edtPass=findViewById(R.id.edtPass);

        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        btnLogin=findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        btnForgot=findViewById(R.id.btnForgot);
        btnForgot.setOnClickListener(this);

        checkRemember=findViewById(R.id.checkRemember);

        btnLogWithFacebook=findViewById(R.id.btnFacebook);
        btnLogWithFacebook.setOnClickListener(this);

        btnLogWithGoogle=findViewById(R.id.btnGoogle);
        btnLogWithGoogle.setOnClickListener(this);

        btnToSignUp=findViewById(R.id.btnSignUp);
        btnToSignUp.setOnClickListener(this);

//        setUpLoginWithFacebook();
        setUpLoginWithGoogle();
    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//        user=auth.getCurrentUser();
//        if(user!=null){
//            startActivity(new Intent(LoginActivity.this,MoreActivity.class));
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
//        if (logWithFacebook==true){
//            mCallbackManager.onActivityResult(requestCode, resultCode, data);
//        }
        // request Google sign in

        if (logWithGoogle==true){
            if (requestCode==REQUEST_CODE){
                Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
                try{
                    GoogleSignInAccount account=task.getResult(ApiException.class);
                    //make method for SIgninFireBase
                    handleGoogleAccessToken(account);
                }catch (ApiException e) {
                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(),""+task.getResult(),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void handleGoogleAccessToken(GoogleSignInAccount account) {
        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
//                    Toast.makeText(getApplicationContext(), "Google signin success", Toast.LENGTH_SHORT).show();
                    user = auth.getCurrentUser();
                    if (user != null) {
                        startActivity(new Intent(LoginActivity.this, MoreActivity.class));
                        saveUser(user.getDisplayName(), user.getEmail());
                        finish();
                    }
                } else {
//                    Toast.makeText(getApplicationContext(),"Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    private void handleFacebookAccessToken(AccessToken token) {
//        if (token.getCurrentAccessToken() != null) {
//            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
//                    new GraphRequest.GraphJSONObjectCallback() {
//                        @Override
//                        public void onCompleted(final JSONObject me, GraphResponse response) {
//                            if (me != null) {
//                                Log.i("Login: ", me.optString("name"));
//                                Log.i("ID: ", me.optString("id"));
//
//                                Toast.makeText(LoginActivity.this, "Name: " + me.optString("name"), Toast.LENGTH_SHORT).show();
//                                Toast.makeText(LoginActivity.this, "ID: " + me.optString("id"), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//
//            Bundle parameters = new Bundle();
//            parameters.putString("fields", "id,name,link");
//            request.setParameters(parameters);
//            request.executeAsync();
//        }
//    }
//        Log.d(TAG, "handleFacebookAccessToken:" + token);
//
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        auth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            user = auth.getCurrentUser();
//                            if (user!=null){
//                                startActivity(new Intent(LoginActivity.this,MoreActivity.class));
//                                saveUser(user.getDisplayName(),user.getEmail());
//                                finish();
//                            }
//
////
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Toast.makeText(LoginActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
////                            updateUI(null);
//                        }
//                    }
//                });



    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btnLogin){
            signInNormal();
        }

        if (view.getId()==R.id.btnSignUp) {
            progressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(intent);
//                    finish();
                }
            },500);
        }
        if (view.getId()==R.id.btnGoogle) {
            logWithGoogle=true;
            signInGoogle();

        }
//        if (view.getId()==R.id.btnFacebook){
//            logWithFacebook=true;
//            loginButton.performClick();
//        }
    }
    private void signInGoogle() {
        Intent intent  =signInClient.getSignInIntent();
        startActivityForResult(intent,REQUEST_CODE);
    }


    private void signInNormal() {
        progressBar.setVisibility(View.VISIBLE);

        auth=FirebaseAuth.getInstance();
        String email=edtEmail.getText().toString();
        String password=edtPass.getText().toString();
        if ((email.length()==0)||(password.length()==0)) {
            Toast.makeText(getApplicationContext(),"Enter email & password",Toast.LENGTH_SHORT).show();
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        if (auth.getCurrentUser().isEmailVerified()){
                            startActivity(new Intent(LoginActivity.this,ChatActivity.class));
                            finish();
                        }
                        else {
                            Toast.makeText(LoginActivity.this,"Please verify your email." ,Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    }
                    else {
                        Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                });
            }
        },1500);
    }
    private void setUpLoginWithGoogle() {
        // configure Google sign in
        GoogleSignInOptions signInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the Options Specified by SignInOptions
        signInClient=GoogleSignIn.getClient(getApplicationContext(),signInOptions);
    }

//    private static final String EMAIL = "email";
//    private void setUpLoginWithFacebook() {
//        mCallbackManager = CallbackManager.Factory.create();
//
////        loginButton.setPermissions("email"/*, "public_profile"*/);
//        loginButton.setPermissions(Arrays.asList(EMAIL));
//        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.d(TAG, "facebook:onSuccess:" + loginResult);
//                handleFacebookAccessToken(loginResult.getAccessToken());
//            }
//            @Override
//            public void onCancel() {
//                Log.d(TAG, "facebook:onCancel");
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.d(TAG, "facebook:onError", error);
//            }
//        });
//    }

    private void saveUser(String username,String email) {
        if (user!=null){
            String uid = user.getUid();
//            Toast.makeText(getApplicationContext(),uid,Toast.LENGTH_SHORT).show();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            CollectionReference usersCollection = db.collection("users");
            DocumentReference userDocument = usersCollection.document(uid);

            Map<String, Object> userData = new HashMap<>();
            userData.put("username",username);
            userData.put("email",email);

            userDocument.set(userData, SetOptions.merge()) // Sử dụng SetOptions.merge() để cập nhật thông tin người dùng mà không ghi đè dữ liệu hiện có.
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Document updated with ID: " + uid);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error updating document", e);
                    });
        }
    }

    @NonNull
    @Override
    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {
        progressBar.setVisibility(View.INVISIBLE);
        return super.getOnBackInvokedDispatcher();
    }
}
