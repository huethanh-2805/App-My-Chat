package com.example.mychat.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mychat.R;
import com.example.mychat.activity.main.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ForgotPasswordActivity extends Activity implements View.OnClickListener{
    FirebaseAuth auth;
    private Button btnResetPassword;
    private TextView txtSendEmailReset;
    private TextView txtEmailNotExist;
    private EditText edtEmail;
    private ProgressBar progressBar;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReference;
    List<String> listEmail=new ArrayList<String>();
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        btnResetPassword=(Button)findViewById(R.id.btnResetPassword);
        btnResetPassword.setOnClickListener(this);

        txtSendEmailReset=findViewById(R.id.txtSendEmailReset);

        txtEmailNotExist=findViewById(R.id.txtEmailNotExist);

        edtEmail=findViewById(R.id.edtEmail);
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getListEmailUser();
                String enteredEmail = edtEmail.getText().toString().trim();
                if (!TextUtils.isEmpty(enteredEmail)) {
                    if (listEmail.contains(enteredEmail)){
                        txtEmailNotExist.setVisibility(View.GONE);
                        btnResetPassword.setEnabled(true);
                        btnResetPassword.getBackground().setAlpha(255);

                    } else {
                        txtEmailNotExist.setVisibility(View.VISIBLE);
                        btnResetPassword.setEnabled(false);
                        btnResetPassword.getBackground().setAlpha(50);

                    }
                }
            }
        });
        progressBar=findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btnResetPassword) {
            String email=edtEmail.getText().toString();

            if (email.length()==0) {
                Toast.makeText(getApplicationContext(),"Please fill email",Toast.LENGTH_SHORT).show();
            }
            else {
                auth = FirebaseAuth.getInstance();

                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                            auth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                txtSendEmailReset.setVisibility(View.VISIBLE);
//                                          Toast.makeText(getApplicationContext(), "Email reset password have sent.", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                                                progressBar.setVisibility(View.INVISIBLE);
                                                finish();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Email does not exist .", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.INVISIBLE);

                                            }
                                        }
                                    });
                    }
                },1000);

            }
        }
    }

    private void getListEmailUser(){
        collectionReference=db.collection("users");
        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String emailUser = document.getString("email");
                                listEmail.add(emailUser);
                            }
                        } else {
                            Log.d("TAG", "Lỗi khi lấy dữ liệu: ", task.getException());
                        }
                    }
                });
    }


}
