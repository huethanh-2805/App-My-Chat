package com.example.mychat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatSreen extends AppCompatActivity {
    LinearLayout barLayout;
    RelativeLayout bottomBar;
    RelativeLayout layoutChatScreen;
    ImageView profile_image;

    ImageView btn_back;
    TextView username;

    MessageAdapter messageAdapter;
    List<Message> mMessage;

    RecyclerView recyclerView;

    FirebaseUser fUser;
    DatabaseReference reference;

    ImageButton btn_send;
    EditText text_send;
    Intent intent;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;
    ImageView btn_more;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Đặt theme trước khi gọi setContentView
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_sreen);

        applyNightMode();
        bottomBar=findViewById(R.id.bottom_bar);
        layoutChatScreen=findViewById(R.id.layoutChatScreen);
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        btn_back = findViewById(R.id.back);
        btn_more = findViewById(R.id.more);
        barLayout=findViewById(R.id.bar_layout);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        String userReceiverID = intent.getStringExtra("receiverID");

//        final User[] oUser = new User[1];


        fUser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(fUser.getUid(), userReceiverID, msg);
                } else {
                    Toast.makeText(ChatSreen.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        db.collection("users").document(userReceiverID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Lấy giá trị của trường "username" và "email" từ document
                            String name = documentSnapshot.getString("username");
                            btn_more.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ChatSreen.this, ConversationInformation.class);
                                    intent.putExtra("user_name",name);
                                    ChatSreen.this.startActivity(intent);
                                }
                            });
                            username.setText(name);
                            readMessages(fUser.getUid(), userReceiverID, "https://static.vecteezy.com/system/resources/previews/002/002/257/non_2x/beautiful-woman-avatar-character-icon-free-vector.jpg");
                        } else {
                            Toast.makeText(getApplicationContext(), "Document không tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Fail read field in database", Toast.LENGTH_SHORT).show();
                    }
                });


        setThemeBasedOnSelectedTheme();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setThemeBasedOnSelectedTheme();
    }

    private void sendMessage(String sender, String receiver, String message) {
        CollectionReference usersCollection = db.collection("messages");


        HashMap<String, Object> messageData = new HashMap<>();
        Timestamp timestamp = Timestamp.now();
        messageData.put("sender", sender);
        messageData.put("receiver", receiver);
        messageData.put("message", message);
        messageData.put("timestamp", timestamp);

        usersCollection.add(messageData);
    }

    private void readMessages(final String myid, final String userid, final String imageurl) {
        mMessage = new ArrayList<>();
        CollectionReference chatsCollection = db.collection("messages");

        chatsCollection.orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Xử lý lỗi nếu có
                            return;
                        }
                        if (queryDocumentSnapshots != null) {
                            mMessage.clear();
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Message message = d.toObject(Message.class);
                                if ((message.getReceiver().equals(myid) && message.getSender().equals(userid))
                                        || (message.getReceiver().equals(userid) && message.getSender().equals(myid))) {
                                    if (!message.getAppearStatus()) {
                                        mMessage.add(message);
                                        message.setAppeared();
                                    }
                                }
                            }
                            messageAdapter = new MessageAdapter(ChatSreen.this, mMessage, imageurl);
                            recyclerView.setAdapter(messageAdapter);
                        } else {
                            Toast.makeText(ChatSreen.this, "Không tìm thấy dữ liệu trong cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void applyNightMode() {
        sharedPreferences=MyChat.getSharedPreferences();
        boolean nightMode=sharedPreferences.getBoolean("night",false);
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @SuppressLint("ResourceAsColor")
    private void setThemeBasedOnSelectedTheme() {
        int selectedTheme = ThemeHelper.getSelectedTheme(this);
        switch (selectedTheme) {
            case 0:
                setTheme(R.style.AppTheme_Dark1);
                layoutChatScreen.setBackgroundResource(R.color.Light1);
                barLayout.setBackgroundResource(R.color.lightblue);
                break;
            case 1:
//                setTheme(R.style.AppTheme_Dark2);
                layoutChatScreen.setBackgroundResource(R.color.lightblue);
                barLayout.setBackgroundResource(R.color.Light2);
                break;
            case 2:
//                setTheme(R.style.AppTheme_Dark3);
                layoutChatScreen.setBackgroundResource(R.color.green);
                barLayout.setBackgroundResource(R.color.Light3);
                break;
            case 3:
                layoutChatScreen.setBackgroundResource(R.color.Dark1);
                barLayout.setBackgroundResource(R.color.red);

                break;
            case 4:
                layoutChatScreen.setBackgroundResource(R.color.Dark2);
                barLayout.setBackgroundResource(R.color.Dark1);

                break;
            case 5:
                layoutChatScreen.setBackgroundResource(R.color.Dark3);
                barLayout.setBackgroundResource(R.color.pink);

                break;
            case 6:
                layoutChatScreen.setBackgroundResource(R.drawable.theme3d1);
                barLayout.setBackgroundResource(R.color.pink);

                break;
            case 7:
                layoutChatScreen.setBackgroundResource(R.drawable.theme_love3d);
                barLayout.setBackgroundResource(R.color.pink);

                break;
            case 8:
                layoutChatScreen.setBackgroundResource(R.drawable.theme_blackheart);
                barLayout.setBackgroundResource(R.color.lightblack);

                break;
            case 9:
                layoutChatScreen.setBackgroundResource(R.drawable.theme_socola);
                barLayout.setBackgroundResource(R.color.brown);

                break;
            case 10:
                layoutChatScreen.setBackgroundResource(R.drawable.theme_cocacola);
                barLayout.setBackgroundResource(R.color.lightblack);

                break;
            case 11:
                layoutChatScreen.setBackgroundResource(R.drawable.theme_mochi);
                barLayout.setBackgroundResource(R.color.brown);

                break;
            default:
                // Nếu giá trị không hợp lệ, sử dụng theme mặc định
                setTheme(R.style.Base_Theme_MyChat);
                break;
        }
    }
}