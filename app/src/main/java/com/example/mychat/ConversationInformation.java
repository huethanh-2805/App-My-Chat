package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ConversationInformation extends AppCompatActivity {
    String[] items = new String[]{"Change theme", "Media", "Block", "Delete chat"};

    Integer[] icons = {R.drawable.ic_theme, R.drawable.ic_picture, R.drawable.ic_block, R.drawable.ic_delete};
    ListView listView;
    TextView txtUserName;
    ImageView btn_back;
    Intent intent;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_information);

        txtUserName = findViewById(R.id.username);
        btn_back = findViewById(R.id.back);
        intent = getIntent();
        String name = intent.getStringExtra("user_name");
        txtUserName.setText(name);

        listView = findViewById(R.id.listView);
        CustomListMore adapter = new CustomListMore(this, R.layout.custom_listview_more, items, icons);
        listView.setAdapter(adapter);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConversationInformation.this, ChatSreen.class);
                ConversationInformation.this.startActivity(intent);
            }
        });



    }

}