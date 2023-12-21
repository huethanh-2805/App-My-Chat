package com.example.mychat.activity.main;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {
    private DocumentReference documentReference;
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        documentReference=database.collection("users").document(firebaseAuth.getCurrentUser().getUid().toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        documentReference.update("status","0");
    }

    @Override
    protected void onResume() {
        super.onResume();
        documentReference.update("status","1");
    }
}


