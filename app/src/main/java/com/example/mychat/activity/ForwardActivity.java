package com.example.mychat.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mychat.R;
import com.example.mychat.adapter.NewGroupAdapter;
import com.example.mychat.fragment.MainFragment;
import com.example.mychat.object.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ForwardActivity extends Activity implements View.OnClickListener {
    ImageView imgSend;
    EditText editText;
    ListView listView;


    ImageView back;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    FirebaseFirestore db;
    DocumentReference dref;
    //
    List<User> user = new ArrayList<>(); //tên người liên hệ
    List<User> userAdapter = new ArrayList<>();
    private NewGroupAdapter adapter;
    Context context;
    MainFragment mainFragment;

    String mess = " ";
    String type=" ";
    String title=" ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward);
        context = getApplicationContext();
        imgSend = (ImageView) findViewById(R.id.imgSend);
        imgSend.setOnClickListener(this);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        editText = (EditText) findViewById(R.id.editText);
//        editText.requestFocus();
        searchUserAdd();
        getContactExists();
        listView = (ListView) findViewById(R.id.listView);
        Intent intent = getIntent();

        // Kiểm tra xem Intent có dữ liệu không
        if (intent != null) {
            // Lấy Bundle từ Intent
            Bundle bundle = intent.getExtras();

            // Kiểm tra xem Bundle có dữ liệu không
            if (bundle != null) {
                // Lấy giá trị từ Bundle bằng key
                mess = bundle.getString("messages");
                type=bundle.getString("type");
                title=bundle.getString("title");
            }
        }
    }


    private void searchUserAdd() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchMailExist();
            }
        });
    }

    protected void searchMailExist() {
        userAdapter.clear();
        final String emailToCheck = editText.getText().toString().trim();
        for (User u : user) {
            if (u.getEmail().contains(emailToCheck)) {
                userAdapter.add(u);
            }
        }
        adapter.notifyDataSetChanged();
    }

    protected void getContactExists() {
        userAdapter.clear();
        db = FirebaseFirestore.getInstance();
        dref = db.collection("contact").document(auth.getCurrentUser().getUid());
        dref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Tài liệu tồn tại, đọc dữ liệu từ trường userContact
                        List<DocumentReference> userContacts = (List<DocumentReference>) document.get("userContact");
                        if (userContacts != null) {
                            for (DocumentReference contactRef : userContacts) {
                                contactRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot contactDocument = task.getResult();
                                            if (contactDocument.exists()) {
                                                // Xử lý dữ liệu từ tài liệu được tham chiếu tại đây
                                                // Ví dụ: Lấy thông tin từ tài liệu contactDocument
                                                String email = contactDocument.getString("email");
                                                String username = contactDocument.getString("username");
                                                User u = new User(username, "...", contactDocument.getString("avatarUrl"), email, contactDocument.getId(), Timestamp.now());
                                                user.add(u);
                                                userAdapter.add(user.get(user.size() - 1));
                                                adapter = new NewGroupAdapter(ForwardActivity.this, R.layout.adapter_new_group, userAdapter);
                                                listView.setAdapter(adapter);
                                                adapter.notifyDataSetChanged();
                                            } else {
                                                // Tài liệu không tồn tại
                                            }
                                        } else {
                                            // Đã xảy ra lỗi khi lấy tài liệu tham chiếu
                                        }
                                    }
                                });
                            }

                        } else {
                            // Trường userContact không tồn tại hoặc là null
                        }
                    } else {
                        // Tài liệu không tồn tại
                    }
                } else {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "fail for", Toast.LENGTH_SHORT).show();
            }
        });
    }

    List<User> us = new ArrayList<>();

    private void findUserAdd() {
        us.clear();
        for (User u : user) {
            if (u.isChecked()) {
                us.add(u);
                u.setChecked(false);
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.back) {
            finish();
        }
        if (v.getId() == R.id.imgSend) {
            findUserAdd();
            if (us.size() != 0) {
                StringBuilder message = new StringBuilder("Bạn muốn tạo chuyển tiếp tin nhắn với ");
                for (User u : us) {
                    message.append(u.getName()).append(" , ");
                }

                AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
                myBuilder.setIcon(R.drawable.ic_noti)
                        .setTitle("Forward Messages")
                        .setMessage(message)
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                saveGroup();
                                for (User u : us) {
                                    sendMessage(auth.getCurrentUser().getUid(), u.getUid(), mess,type,title);
                                }
                                showNiceDialogBox();
                            }
                        })
                        .show();
            } else {
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
                myBuilder.setIcon(R.drawable.ic_noti)
                        .setTitle("Forward Messages")
                        .setMessage("Vui lòng chọn người dùng để chuyển tiếp")
                        .setPositiveButton("Close", null)
                        .show();
            }
        }
    }


    private void sendMessage(String sender, String receiver, String message,String type,String title) {
        CollectionReference usersCollection = db.collection("messages");

        HashMap<String, Object> messageData = new HashMap<>();
        Timestamp timestamp = Timestamp.now();
        //
        messageData.put("sender", sender);
        messageData.put("receiver", receiver);
        messageData.put("sender_delete", "");
        messageData.put("receiver_delete", "");
        messageData.put("message", message);
        messageData.put("timestamp", timestamp);
        messageData.put("type", type);
        if (type.equals("file") || type.equals("pdf") || type.equals("txt") || type.equals("docx")) {
            messageData.put("title", title);
        }
        usersCollection.add(messageData);
    }

    public void showNiceDialogBox() {
        String message = "Forward messages successfully!";
        AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
        myBuilder.setIcon(R.drawable.ic_noti)
                .setTitle("Forward Messages")
                .setMessage(message)
                .setPositiveButton("Close", null)
                .show();
    }

}