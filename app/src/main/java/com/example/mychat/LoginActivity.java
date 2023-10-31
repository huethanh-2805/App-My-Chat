package com.example.mychat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LoginActivity extends Activity implements View.OnClickListener {
    EditText edtEmail;
    EditText edtPass;
    CheckBox checkRemember;
    TextView btnForgot;
    Button btnLogin;
    LinearLayout btnLogWithFacebook;
    LinearLayout btnLogWithGoogle;
    TextView btnToSignUp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtEmail=findViewById(R.id.edtEmail);
        edtPass=findViewById(R.id.edtPass);
        btnLogin=findViewById(R.id.btnLogin);
        btnForgot=findViewById(R.id.btnForgot);
        checkRemember=findViewById(R.id.checkRemember);
        btnLogWithFacebook=findViewById(R.id.btnFacebook);
        btnLogWithGoogle=findViewById(R.id.btnGoogle);
    }
    @NonNull
    @Override
    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {
        return super.getOnBackInvokedDispatcher();
    }

    @Override
    public void onClick(View view) {

    }
}
