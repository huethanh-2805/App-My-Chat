package com.example.mychat;



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
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;



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


    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btnLogin){
            progressBar.setVisibility(View.VISIBLE);

            final FirebaseAuth auth=FirebaseAuth.getInstance();
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
                                startActivity(new Intent(LoginActivity.this,MoreActivity.class));
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
    }

    @NonNull
    @Override
    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {
        progressBar.setVisibility(View.INVISIBLE);
        return super.getOnBackInvokedDispatcher();
    }
}
