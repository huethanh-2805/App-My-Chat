package com.example.mychat.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mychat.R;
import com.example.mychat.adapter.GridViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class MediaGridActivity extends AppCompatActivity {
    Context context;
    private RecyclerView recyclerView;
    private GridViewAdapter gridViewAdapter;
    private ImageView btn_back;

    Intent intent;

    String name;
    String myID;
    String userID;
    boolean isGroup;

    ArrayList<String> uriList;
    ArrayList<String> typeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_grid);
        //
        btn_back = findViewById(R.id.back);
        recyclerView = findViewById(R.id.recyclerView);
        //
        context = getApplicationContext();
        intent = getIntent();
        name = intent.getStringExtra("user_name");
        myID = intent.getStringExtra("my_id");
        userID = intent.getStringExtra("user_id");
        isGroup = intent.getBooleanExtra("isGroup",false);
        //set up grid 3 cột
        GridLayoutManager gridManager = new GridLayoutManager(MediaGridActivity.this, 3);
        recyclerView.setLayoutManager(gridManager);
        //Toast.makeText(context,myID + " " + userID, Toast.LENGTH_SHORT).show();
        //if (!isGroup) Toast.makeText(context,"NOT GROUP", Toast.LENGTH_SHORT).show();
        //
        showMediaGridView(myID,userID);
        //
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    protected void showMediaGridView(String myID, String userID) {//userID can be group id
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //get MESSAGE collection
        CollectionReference ref = db.collection("messages");
        //
        Query query;
        if (isGroup) query = ref.whereIn("receiver", Arrays.asList(myID, userID))
                .orderBy("timestamp", Query.Direction.DESCENDING);
        else query = ref.whereIn("receiver", Arrays.asList(myID, userID))
                .whereIn("sender", Arrays.asList(myID, userID))
                .orderBy("timestamp", Query.Direction.DESCENDING);
        Task<QuerySnapshot> task = query.get();
        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    uriList = new ArrayList<String>();
                    typeList = new ArrayList<String>();
                    for (QueryDocumentSnapshot d : querySnapshot) {
                        String type = d.getString("type");
                        if (type.equals("image") || type.equals("video")) {
                            String link = d.getString("message");
                            uriList.add(link);
                            typeList.add(type);
                        }
                    }
                    //
                    gridViewAdapter = new GridViewAdapter(MediaGridActivity.this, uriList, typeList);
                    recyclerView.setAdapter(gridViewAdapter);
                } else {
                    Toast.makeText(context,"fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}