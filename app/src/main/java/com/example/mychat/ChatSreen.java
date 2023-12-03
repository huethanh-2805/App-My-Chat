package com.example.mychat;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatSreen extends AppCompatActivity {


    private static final int PICK_IMAGE_REQUEST = 1;

    private static final int PICK_FILE_REQUEST_CODE = 1;
    private Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;



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
    ImageButton btn_chooseImage;

    ImageButton btn_file;
    EditText text_send;
    Intent intent;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;
    ImageView btn_more;
    String userReceiverID;

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

        btn_file=findViewById(R.id.btn_file);
        btn_chooseImage=findViewById(R.id.image_btn);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        userReceiverID = intent.getStringExtra("receiverID");



        fUser = FirebaseAuth.getInstance().getCurrentUser();

        btn_chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btn_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile();
            }
        });



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
                                    intent.putExtra("my_id", fUser.getUid()); //Gửi id của mình
                                    intent.putExtra("user_id", userReceiverID); //Gửi id của người chat với mình
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


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            String fileExtension = getFileExtension(filePath);

            if (fileExtension.equals("png") || fileExtension.equals("jpg") ){
                uploadImage();
            }
            else if (fileExtension.equals("pdf") || fileExtension.equals("txt") ){
                uploadFile();
            }
        }
    }

    @SuppressLint("Range")
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String getFileExtension(Uri uri) {
        // Lấy đuôi file từ URI
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String fileExtension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        Toast.makeText(this, fileExtension, Toast.LENGTH_SHORT).show();
        return fileExtension;
    }

    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Lưu hình ảnh vào Firebase Storage
            StorageReference ref = storageReference.child("images/" + System.currentTimeMillis() + ".jpg");

            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            sendImage(fUser.getUid(), userReceiverID, imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(ChatSreen.this, "Upload failed", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFile() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            // Tạo tên duy nhất cho tập tin
            String fileName = System.currentTimeMillis() + "_" + fUser.getUid();

            // Tạo đường dẫn lưu trữ trên Firebase Storage
            StorageReference fileRef = storage.getReference().child("files/" + fileName);

            // Upload tập tin lên Firebase Storage
            UploadTask uploadTask = fileRef.putFile(filePath);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                        // Lấy đường dẫn của tập tin đã upload
                        progressDialog.dismiss();
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String fileUrl = uri.toString();
                            sendFile(fUser.getUid(), userReceiverID, fileUrl);

//                            // Lưu đường dẫn vào Firestore
//                            Map<String, Object> dataToSave = new HashMap<>();
//                            dataToSave.put("file_url", fileUrl);
//
//                            firestore.collection("files")
//                                    .add(dataToSave)
//                                    .addOnSuccessListener(documentReference -> {
//                                        // Thành công
//                                        Toast.makeText(this, "Upload thành công", Toast.LENGTH_SHORT).show();
//                                    })
//                                    .addOnFailureListener(e -> {
//                                        // Lỗi khi lưu vào Firestore
//                                        Toast.makeText(this, "Lỗi khi lưu vào Firestore", Toast.LENGTH_SHORT).show();
//                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Lỗi khi upload tập tin lên Firebase Storage
                        Toast.makeText(this, "Lỗi khi upload tập tin", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void sendImage(String sender, String receiver, String message) {
        CollectionReference usersCollection = db.collection("messages");

        HashMap<String, Object> messageData = new HashMap<>();
        Timestamp timestamp = Timestamp.now();
        messageData.put("sender", sender);
        messageData.put("receiver", receiver);
        messageData.put("message", message);
        messageData.put("timestamp", timestamp);
        messageData.put("type", "image");

        usersCollection.add(messageData);
    }

    private void sendFile(String sender, String receiver, String message) {
        CollectionReference usersCollection = db.collection("messages");

        HashMap<String, Object> messageData = new HashMap<>();
        Timestamp timestamp = Timestamp.now();
        messageData.put("sender", sender);
        messageData.put("receiver", receiver);
        messageData.put("message", message);
        messageData.put("timestamp", timestamp);
        messageData.put("title", getFileNameFromUri(filePath));
        messageData.put("type", getFileExtension(filePath));

        usersCollection.add(messageData);
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
        messageData.put("sender_delete", "");
        messageData.put("receiver_delete", "");
        messageData.put("message", message);
        messageData.put("timestamp", timestamp);
        messageData.put("type", "text");

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
//                                if ((message.getReceiver().equals(myid) && message.getSender().equals(userid))
//                                        || (message.getReceiver().equals(userid) && message.getSender().equals(myid))
//                                        || (message.getReceiver().equals(myid) && message.getSender().equals(""))
//                                        || (message.getSender().equals(myid) && message.getReceiver().equals(""))) {
//                                    if (!message.getAppearStatus()) {
//                                        mMessage.add(message);
//                                        message.setAppeared();
//                                    }
//                                }

                                if ((message.getReceiver().equals(myid) && message.getSender().equals(userid))) {
                                    if (!message.getAppearStatus()) {
                                        mMessage.add(message);
                                        message.setAppeared();
                                    }
                                }

                                if ((message.getReceiver().equals(userid) && message.getSender().equals(myid))) {
                                    if (!message.getAppearStatus()) {
                                        mMessage.add(message);
                                        message.setAppeared();
                                    }
                                }

                                if ((message.getReceiver().equals(myid) && message.getSender().equals(""))) {
                                    if (!message.getAppearStatus() && d.getString("sender_delete").equals(userid)) {
                                        mMessage.add(message);
                                        message.setAppeared();
                                    }
                                }

                                if ((message.getSender().equals(myid) && message.getReceiver().equals(""))) {
                                    if (!message.getAppearStatus() && d.getString("receiver_delete").equals(userid)) {
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