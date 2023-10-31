package com.example.mychat;

import static android.content.ContentValues.TAG;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
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
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity implements View.OnClickListener {
    EditText edtEmail;
    EditText edtPass;
    CheckBox checkRemember;
    TextView btnForgot;
    Button btnLogin;
    LinearLayout btnLogWithFacebook;
    LinearLayout btnLogWithGoogle;
    TextView btnToSignUp;
    ProgressBar progressBar;

    ActionCodeSettings actionCodeSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,String> test=new HashMap<>();

        actionCodeSettings=
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://www.example.com/finishSignUp?cartId=1234")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setIOSBundleId("com.example.ios")
                        .setAndroidPackageName(
                                "com.example.android",
                                true, /* installIfNotAvailable */
                                "12"    /* minimumVersion */)
                        .build();
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btnLogin){
            progressBar.setVisibility(View.VISIBLE);

            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.sendSignInLinkToEmail(edtEmail.getText().toString(), actionCodeSettings)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                            }
                        }
                    });
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LoginActivity.this, MoreActivity.class);
                    startActivity(intent);
                    finish();
                }
            },2000);
        }
    }
}
