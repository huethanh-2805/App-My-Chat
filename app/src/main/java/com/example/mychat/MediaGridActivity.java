package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class MediaGridActivity extends AppCompatActivity {
    Context context;
    private GridView gridView;
    private GridViewAdapter gridViewAdapter;
    private ImageView btn_back;

    Intent intent;

    String name;
    String myID;
    String userID;
    boolean isGroup;

    ArrayList<String> grid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_grid);
        //
        btn_back = findViewById(R.id.back);
        gridView = findViewById(R.id.gridView);
        //
        context = getApplicationContext();
        intent = getIntent();
        name = intent.getStringExtra("user_name");
        myID = intent.getStringExtra("my_id");
        userID = intent.getStringExtra("user_id");
        isGroup = intent.getBooleanExtra("isGroup",false);
        //
        //Toast.makeText(context,myID + " " + userID, Toast.LENGTH_SHORT).show();
        //if (!isGroup) Toast.makeText(context,"NOT GROUP", Toast.LENGTH_SHORT).show();
        //
        grid = new ArrayList<String>();
        //
        showMediaGridView(myID,userID);
    }
    protected void showMediaGridView(String myID, String userID) {//userID can be group id
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //
        String string = "https://firebasestorage.googleapis.com/v0/b/mychat-7f8c6.appspot.com/o/images%2F1701674431709.jpg?alt=media&token=b9305495-0958-41ab-a107-ae5500e7367c";
        grid.add(string);
        gridViewAdapter = new GridViewAdapter(MediaGridActivity.this, grid);
        gridView.setAdapter(gridViewAdapter);

        if(!isGroup) return;
        //get MESSAGE collection
        CollectionReference ref = db.collection("messages");
        //
        Query query = ref.whereIn("receiver", Arrays.asList(myID, userID))
                .whereIn("sender", Arrays.asList(myID, userID))
                .orderBy("timestamp", Query.Direction.DESCENDING);
        //
        Task<QuerySnapshot> task = query.get();
        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    for (QueryDocumentSnapshot d : querySnapshot) {
                        String type = d.getString("type");
                        String link = d.getString("message");
                        if (type.equals("video") || type.equals("image")) {
                            //Toast.makeText(context,link, Toast.LENGTH_SHORT).show();
                            grid.add(link);
                        }
                    }
                    gridViewAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(context,"fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}