package com.example.mychat.activity.main;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.mychat.activity.ForgotPasswordActivity;
import com.example.mychat.fragment.MainFragment;
import com.example.mychat.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText edtEmail;
    private EditText edtPass;
    private CheckBox checkRemember;
    private TextView btnForgot;
    private Button btnLogin;

    private LinearLayout btnLogWithGoogle;
    private TextView btnToSignUp;
    private ProgressBar progressBar;
    private GoogleSignInClient signInClient;
    private final int REQUEST_CODE = 100;
    FirebaseAuth auth;
    FirebaseUser user;

    private boolean logWithGoogle = false;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth=FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        btnForgot = findViewById(R.id.btnForgot);
        btnForgot.setOnClickListener(this);


        btnLogWithGoogle = findViewById(R.id.btnGoogle);
        btnLogWithGoogle.setOnClickListener(this);

        btnToSignUp = findViewById(R.id.btnSignUp);
        btnToSignUp.setOnClickListener(this);

        setUpLoginWithGoogle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // request Google sign in
        if (logWithGoogle) {
            if (requestCode == REQUEST_CODE) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    //make method for SIgninFireBase
                    handleGoogleAccessToken(account);
                } catch (ApiException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"fail 1",Toast.LENGTH_SHORT).show();
                }
            }
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

    private void handleGoogleAccessToken(GoogleSignInAccount account) {
        progressBar.setVisibility(View.VISIBLE);
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Google signin success", Toast.LENGTH_SHORT).show();
                    user = auth.getCurrentUser();
                    if (user != null) {
                        startActivity(new Intent(LoginActivity.this, MainFragment.class));
                        saveUser(user.getDisplayName(), user.getEmail());
                        progressBar.setVisibility(View.INVISIBLE);
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnLogin) {
            signInNormal();
            progressBar.setVisibility(View.INVISIBLE);
        }

        if (view.getId() == R.id.btnSignUp) {
            progressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(intent);
                    progressBar.setVisibility(View.INVISIBLE);
//                    finish();
                }
            }, 500);
        }
        if (view.getId() == R.id.btnGoogle) {
            logWithGoogle = true;
            progressBar.setVisibility(View.VISIBLE);
            signInGoogle();
        }
        if (view.getId() == R.id.btnForgot) {
            progressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                    startActivity(intent);
                    progressBar.setVisibility(View.INVISIBLE);

//                    finish();
                }
            }, 500);
        }

    }

    private void signInGoogle() {
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, REQUEST_CODE);
    }


    private void signInNormal() {
        progressBar.setVisibility(View.VISIBLE);

        auth = FirebaseAuth.getInstance();
        String email = edtEmail.getText().toString();
        String password = edtPass.getText().toString();
        if ((email.length() == 0) || (password.length() == 0)) {
            Toast.makeText(getApplicationContext(), "Enter email & password", Toast.LENGTH_SHORT).show();
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (auth.getCurrentUser().isEmailVerified()) {
                            startActivity(new Intent(LoginActivity.this, MainFragment.class));
                            progressBar.setVisibility(View.INVISIBLE);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Please verify your email.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    } else {
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                });
            }
        }, 1500);
    }

    private void setUpLoginWithGoogle() {
        // configure Google sign in
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the Options Specified by SignInOptions
        signInClient = GoogleSignIn.getClient(getApplicationContext(), signInOptions);
    }

    private void saveUser(String username, String email) {
        if (user != null) {
            String uid = user.getUid();
//            Toast.makeText(getApplicationContext(),uid,Toast.LENGTH_SHORT).show();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            CollectionReference usersCollection = db.collection("users");
            DocumentReference userDocument = usersCollection.document(uid);

            Map<String, Object> userData = new HashMap<>();
            userData.put("username", username);
            userData.put("email", email);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

}


