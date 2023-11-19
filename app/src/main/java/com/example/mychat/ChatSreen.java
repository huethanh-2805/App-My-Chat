package com.example.mychat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatSreen extends AppCompatActivity {

    ImageView profile_image;

    ImageView btn_back;
    TextView username;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    FirebaseUser fUser;
    DatabaseReference reference;

    ImageButton btn_send;
    EditText text_send;
    Intent intent;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_sreen);

        profile_image=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        btn_send=findViewById(R.id.btn_send);
        text_send=findViewById(R.id.text_send);
        btn_back=findViewById(R.id.back);

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent=getIntent();
        String userN=intent.getStringExtra("userid");
        username.setText(userN);
        final String userid=(String)"1F9VYzj8BCRi8GiAI0zBoAZIu4u1";
        final User[] oUser = new User[1];


        fUser= FirebaseAuth.getInstance().getCurrentUser();


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=text_send.getText().toString();
                if (!msg.equals("")){
                    sendMessage(fUser.getUid(),userid,msg);
                    readMessages(fUser.getUid(), userid, "https://static.vecteezy.com/system/resources/previews/002/002/257/non_2x/beautiful-woman-avatar-character-icon-free-vector.jpg");

                }else{
                    Toast.makeText(ChatSreen.this,"You can't send empty message",Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChatSreen.this,ContactActivity.class);
                ChatSreen.this.startActivity(intent);
            }
        });



        db.collection("users").document(userid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            oUser[0] = documentSnapshot.toObject(User.class);
                            // Lấy giá trị của trường "username" và "email" từ document
                            String name = documentSnapshot.getString("username");
                            Toast.makeText(ChatSreen.this,name,Toast.LENGTH_SHORT).show();
                            username.setText(name);
                            readMessages(fUser.getUid(), userid, "https://static.vecteezy.com/system/resources/previews/002/002/257/non_2x/beautiful-woman-avatar-character-icon-free-vector.jpg");
                        } else {
                            Toast.makeText(getApplicationContext(),"Document không tồn tại",Toast.LENGTH_SHORT).show();
                        }

                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Fail read field in database",Toast.LENGTH_SHORT).show();
                    }
                });

    }



    private void sendMessage(String sender, String receiver, String message){
        CollectionReference usersCollection = db.collection("chats");

        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("timestamp", FieldValue.serverTimestamp());

        usersCollection.add(hashMap);
    }

    private void readMessages(final String myid, final String userid, final String imageurl) {
        mchat = new ArrayList<>();
        CollectionReference chatsCollection = db.collection("chats");

        chatsCollection.orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            mchat.clear();
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Chat chat = d.toObject(Chat.class);
                                Toast.makeText(ChatSreen.this, chat.getMessage(), Toast.LENGTH_SHORT).show();
                                if ((chat.getReceiver().equals(myid) && chat.getSender().equals(userid))
                                        || (chat.getReceiver().equals(userid) && chat.getSender().equals(myid))) {
                                    if (chat.getAppearStatus()==false) {
                                        mchat.add(chat);
                                        chat.setAppeared();
                                    }
                                }
                            }
                            messageAdapter = new MessageAdapter(ChatSreen.this, mchat, imageurl);
                            recyclerView.setAdapter(messageAdapter);

                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(ChatSreen.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // if we do not get any data or any error we are displaying
                        // a toast message that we do not get any data
                        Toast.makeText(ChatSreen.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}