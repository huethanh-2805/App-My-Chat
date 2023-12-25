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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mychat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends Activity implements View.OnClickListener {

    private Button btnSignUp;
    private EditText edtEmail;
    private EditText edtPass;
    private EditText edtUserName;
    private CheckBox checkAgree;
    private TextView btnLogIn;
    private ProgressBar progressBar;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        progressBar=findViewById(R.id.progressBar);
        checkAgree=findViewById(R.id.checkAgree);

        btnSignUp=findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);


        edtEmail=findViewById(R.id.edtEmail);
        edtPass=findViewById(R.id.edtPass);
        edtUserName=findViewById(R.id.edtUserName);


        btnLogIn=findViewById(R.id.btnLogIn);
        btnLogIn.setOnClickListener(this);



    }
    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btnSignUp) {
            progressBar.setVisibility(View.VISIBLE);
            String email=edtEmail.getText().toString();
            String password=edtPass.getText().toString();
            String username=edtUserName.getText().toString();
            if ((email.length()==0)||(password.length()==0)) {
                Toast.makeText(getApplicationContext(),"Enter email & password",Toast.LENGTH_SHORT).show();
                return;
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final FirebaseAuth auth=FirebaseAuth.getInstance();
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                       if (task.isSuccessful()){

                           auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(SignupActivity.this,"User register successfully. Please verify your email.",Toast.LENGTH_SHORT).show();
                                        saveUser(username,email);
                                        edtEmail.setText("");
                                        edtPass.setText("");
                                        edtUserName.setText("");
                                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
//                                        Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);

                                    }
                               }
                           });
                       }
                       else {
                           Toast.makeText(SignupActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                           progressBar.setVisibility(View.INVISIBLE);

                       }
                    });
                }
            },3000);
        }

        if (view.getId()==R.id.btnLogIn) {
            progressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            },500);
        }
    }

    private void saveUser(String username,String email) {
        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            String uid = user.getUid();
//            Toast.makeText(getApplicationContext(),uid,Toast.LENGTH_SHORT).show();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            CollectionReference usersCollection = db.collection("users");
            DocumentReference userDocument = usersCollection.document(uid);

            Map<String, Object> userData = new HashMap<>();
            userData.put("username", username);
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

}