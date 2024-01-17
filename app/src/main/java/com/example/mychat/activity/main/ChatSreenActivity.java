package com.example.mychat.activity.main;



import static android.content.ContentValues.TAG;
import android.app.AlertDialog;
import android.app.ProgressDialog;


import android.annotation.SuppressLint;

import android.content.BroadcastReceiver;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import android.provider.OpenableColumns;

import android.util.Log;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.activity.ForwardActivity;
import com.example.mychat.activity.ConversationInformationActivity;
import com.example.mychat.others.MyChat;
import com.example.mychat.R;
import com.example.mychat.others.ScreenshotDetector;
import com.example.mychat.others.ThemeHelper;
import com.example.mychat.adapter.MessageUserAdapter;
import com.example.mychat.adapter.MessageGroupAdapter;
import com.example.mychat.object.Message;
import com.example.mychat.iface.OnItemClickListener;
import com.example.mychat.service.MessageNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatSreenActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private static final int PICK_FILE_REQUEST_CODE = 1;
    private static final int PICK_VIDEO_REQUEST = 1;
    private Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;



    LinearLayout barLayout;
    RelativeLayout bottomBar;
    RelativeLayout layoutChatScreen;

    ImageView profile_image;

    ImageView btn_back;
    TextView username;

    MessageUserAdapter messageUserAdapter;
    MessageGroupAdapter messageGroupAdapter;

    List<Message> mMessage=new ArrayList<>();

    RecyclerView recyclerView;

    FirebaseUser fUser;
    DatabaseReference reference;

    ImageButton btn_send;
    ImageButton btn_chooseImage;

    ImageButton btn_chooseVideo;
    ImageButton btn_file;
    EditText text_send;
    Intent intent;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;
    ImageView btn_more;
    String userReceiverID;
    private boolean check1=false;
    private boolean check2=false;

    boolean isGroup;
    TextView txtStatus;
    List<String> id=new ArrayList<>();
    List<String> img=new ArrayList<>();

    TextView show_message;

    CollectionReference cref;
    private List<Message> unsentMessages = new ArrayList<>(); // Danh sách tạm tin nhắn chưa gửi
    private boolean isNetworkConnected;

    private ScreenshotDetector screenshotDetector;
    private static final int REQUEST_CODE_CONVERSATION_INFO = 1001; // Chọn một số bất kỳ


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

        txtStatus=findViewById(R.id.txtStatus);

        btn_more = findViewById(R.id.more);
        barLayout=findViewById(R.id.bar_layout);

        btn_file=findViewById(R.id.btn_file);
        btn_chooseImage=findViewById(R.id.image_btn);
        btn_chooseVideo=findViewById(R.id.btn_video);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        userReceiverID = intent.getStringExtra("receiverID");
        isGroup=intent.getBooleanExtra("group",false);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        setOnClickListener();
        getInfo();
        setThemeBasedOnSelectedTheme();
//        getStatus();
//        Intent serviceIntent = new Intent(this, MessageNotification.class);
//        serviceIntent.putExtra("otherUser", userReceiverID);
//        startService(serviceIntent);


        // Đăng ký BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);

        Intent serviceIntent = new Intent(this, MessageNotification.class);
        serviceIntent.putExtra("otherUser", userReceiverID);
        startService(serviceIntent);
        screenshotDetector = new ScreenshotDetector(this, fUser.getUid(), userReceiverID);
//        getStatus();
    }

    private void getInfo() {
        if (!isGroup) {
            getInfoUser();
        }
        else {
            getInfoGroup();
        }
    }

    private void setOnClickListener() {
        btn_chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        btn_chooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openVideoChooser();
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
                checkSenderIsBlock(fUser.getUid(), userReceiverID);
                //handleSendMessage();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getInfoUser() {
        db.collection("users").document(userReceiverID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Lấy giá trị của trường "username" và "email" từ document
                            String name = documentSnapshot.getString("username");
                            String avatar=documentSnapshot.getString("avatarUrl");
                            btn_more.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ChatSreenActivity.this, ConversationInformationActivity.class);
                                    intent.putExtra("user_name",name);
                                    intent.putExtra("my_id", fUser.getUid()); //Gửi id của mình
                                    intent.putExtra("user_id", userReceiverID); //Gửi id của người chat với mình
                                    intent.putExtra("avatarUrl", avatar); //Gửi avatar


                                    //Gửi check group
                                    if(isGroup){
                                        intent.putExtra("check_group", true);
                                    } else {
                                        intent.putExtra("check_group", false);
                                    }
//                                    ChatSreenActivity.this.startActivity(intent);
                                    startActivityForResult(intent, REQUEST_CODE_CONVERSATION_INFO);

                                }
                            });
                            username.setText(name);
                            if (avatar!=null) {
                                Picasso.get().load(avatar).into(profile_image);
                            }
                            readMessagesUser(fUser.getUid(), userReceiverID, avatar);
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
    }
    private interface MemberInfoCallback {
        void onMemberInfoReceived();
    }
    private void getInfoGroup() {
        id.clear();
        img.clear();
        db.collection("groups").document(userReceiverID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Lấy giá trị của trường "username" và "email" từ document
                            String name = documentSnapshot.getString("username");
                            String avatar=documentSnapshot.getString("avatarUrl");
                            List<DocumentReference> members=(List<DocumentReference>)documentSnapshot.get("member");
                            getUidAndImgMember(members, new MemberInfoCallback() {
                                @Override
                                public void onMemberInfoReceived() {
                                    btn_more.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(ChatSreenActivity.this, ConversationInformationActivity.class);
                                            intent.putExtra("user_name", name);
                                            intent.putExtra("my_id", fUser.getUid());
                                            intent.putExtra("user_id", userReceiverID);
                                            intent.putExtra("avatarUrl", avatar);
                                            intent.putExtra("check_group",true);
                                            //ChatSreen.this.startActivity(intent);
                                            startActivityForResult(intent, REQUEST_CODE_CONVERSATION_INFO);
                                        }
                                    });
                                    username.setText(name);
                                    if (avatar != null) {
                                        Picasso.get().load(avatar).into(profile_image);
                                    }
                                    readMessagesGroup(fUser.getUid(), userReceiverID);
                                }
                            });
                            username.setText(name);
                            if (avatar!=null) {
                                Picasso.get().load(avatar).into(profile_image);
                            }
                            readMessagesGroup(fUser.getUid(),userReceiverID);
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
    }

    private void getUidAndImgMember(List<DocumentReference> members, MemberInfoCallback callback) {
        // Đếm số lượng yêu cầu
        AtomicInteger count = new AtomicInteger(0);
        int size = members.size();

        for (DocumentReference d : members) {
            d.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot userSnapshot = task.getResult();
                        if (userSnapshot.exists()) {
                            String uid = userSnapshot.getId();
                            String image = userSnapshot.getString("avatarUrl");
                            id.add(uid);
                            img.add(image);
                        }
                    }
                    // Kiểm tra nếu đã hoàn thành việc lấy thông tin cho tất cả thành viên
                    if (count.incrementAndGet() == size) {
                        callback.onMemberInfoReceived(); // Gọi callback khi hoàn thành
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Xử lý khi có lỗi
                    // Kiểm tra nếu đã hoàn thành việc lấy thông tin cho tất cả thành viên
                    if (count.incrementAndGet() == size) {
                        callback.onMemberInfoReceived(); // Gọi callback khi hoàn thành
                    }
                }
            });

            setThemeBasedOnSelectedTheme();


            // Đăng ký BroadcastReceiver
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
            registerReceiver(networkChangeReceiver, intentFilter);
            //
            Intent serviceIntent = new Intent(this, MessageNotification.class);
            serviceIntent.putExtra("otherUser", userReceiverID);
            startService(serviceIntent);
            //
        }
    }

    //Cập nhật liên túc trạng thái mạng
    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            isNetworkConnected = isNetworkConnected();
            if (isNetworkConnected) {
                if(isGroup){
                    txtStatus.setVisibility(View.GONE);
                }
                DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(userReceiverID);

                docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                        if(error!=null){
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }
                        if(value!=null&&value.exists()){
                            String data=value.getString("status");
                            if(Objects.equals(data, "1")){
                                txtStatus.setText("Online");
                                txtStatus.setVisibility(View.VISIBLE);
                            }
                            else{
                                txtStatus.setText("Offline");
                                txtStatus.setVisibility(View.VISIBLE);
                            }

                        }
                        else{
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
            } else {
                txtStatus.setText("No internet connection...");
                txtStatus.setVisibility(View.VISIBLE);
            }
        }
    }

    //Kiểm tra kết nối mạng
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void openVideoChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
    }

    private void chooseImage() {
        checkSenderIsBlock(fUser.getUid(), userReceiverID);
        if (check1) {
            showSenderIsBlockDialogBox();
            text_send.setText("");
        } else if (check2) {
            showReceiverIsBlockDialogBox();
            text_send.setText("");
        } else if (!check1 && !check2){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
        }

    }

    private void chooseFile() {
        checkSenderIsBlock(fUser.getUid(), userReceiverID);
        if (check1) {
            showSenderIsBlockDialogBox();
            text_send.setText("");
        } else if (check2) {
            showReceiverIsBlockDialogBox();
            text_send.setText("");
        } else if (!check1 && !check2){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            String fileExtension = getFileExtension(filePath);

            checkSenderIsBlock(fUser.getUid(), userReceiverID);
            if (check1) {
                showSenderIsBlockDialogBox();
                text_send.setText("");
            } else if (check2) {
                showReceiverIsBlockDialogBox();
                text_send.setText("");
            } else if (!check1 && !check2){
                if (fileExtension.equals("png") || fileExtension.equals("jpg") ){
                    uploadImage();
                }
                else if (fileExtension.equals("pdf") || fileExtension.equals("txt") || fileExtension.equals("docx")){
                    uploadFile();
                }
                else if (fileExtension.equals("mp4")){
                    uploadVideoToStorage();
                }
            }
        }

        if (requestCode == REQUEST_CODE_CONVERSATION_INFO && resultCode == RESULT_OK) {
            // Xử lý dữ liệu trả về từ ConversationInformation
            if (data != null) {
                String updatedUsername = data.getStringExtra("updatedUsername");
                if (updatedUsername != null) {
                    // Cập nhật username ở đây
                    username.setText(updatedUsername);
                }
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

    private void uploadVideoToStorage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference storageRef = storage.getReference().child("videos/" + System.currentTimeMillis() + ".mp4");
            storageRef.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Video uploaded successfully
                        progressDialog.dismiss();
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String videoUrl = uri.toString();
                            sendVideo(fUser.getUid(), userReceiverID, videoUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle unsuccessful uploads
                        Toast.makeText(ChatSreenActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
        else {
            Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show();
        }
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
                    .addOnFailureListener(e -> Toast.makeText(ChatSreenActivity.this, "Upload failed", Toast.LENGTH_SHORT).show());
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
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Lỗi khi upload tập tin lên Firebase Storage
                        Toast.makeText(this, "Lỗi khi upload tập tin", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void sendVideo(String sender, String receiver, String message) {
        CollectionReference usersCollection = db.collection("messages");

        HashMap<String, Object> messageData = new HashMap<>();
        Timestamp timestamp = Timestamp.now();
        messageData.put("sender", sender);
        messageData.put("receiver", receiver);
        messageData.put("message", message);
        messageData.put("timestamp", timestamp);
        messageData.put("type", "video");

        usersCollection.add(messageData);
        //add notification
        sendNotification(sender,receiver,isGroup);
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
        //add notification
        sendNotification(sender,receiver,isGroup);
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

        //add notification
        sendNotification(sender,receiver,isGroup);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setThemeBasedOnSelectedTheme();

        //
//        Intent serviceIntent = new Intent(this, MessageNotification.class);
//        serviceIntent.putExtra("otherUser", userReceiverID);
//        startService(serviceIntent);
        //
        screenshotDetector.start();

    }


    private void sendMessage(String sender, String receiver, String message) {
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
        messageData.put("type", "text");

        usersCollection.add(messageData);
        //add notification
        sendNotification(sender,receiver,isGroup);
    }

    private void handleSendMessage() {
        if (check1) {
            showSenderIsBlockDialogBox();
            text_send.setText("");
        } else if (check2) {
            showReceiverIsBlockDialogBox();
            text_send.setText("");
        } else if (!check1 && !check2){
            String msg = text_send.getText().toString();
            if (!msg.equals("")) {
                sendMessage(fUser.getUid(), userReceiverID, msg);
            } else {
                Toast.makeText(ChatSreenActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
            }
            text_send.setText("");
        }

    }
    private void checkSenderIsBlock(String sender, String receiver) {
        // Kiểm tra trong collection "contact"
        checkContactBlock(sender, receiver);
    }

    private void checkContactBlock(String sender, String receiver) {
        CollectionReference contactCollection = db.collection("contact");
        DocumentReference contactDocument = contactCollection.document(receiver);

        contactDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        // Document tồn tại trong "contact"
                        List<DocumentReference> block = (List<DocumentReference>) documentSnapshot.get("block");
                        if (block != null) {
                            DocumentReference userBlock = db.collection("users").document(sender);
                            if (block.contains(userBlock)) {
                                // Người gửi bị chặn
                                check1 = true;
                            }
                            else {
                                check1=false;
                            }
                            checkSenderIsBlockReceiver(sender, receiver);
                            return;
                        }
                    }
                    // Kiểm tra trong collection "groups"
                    checkGroupBlock(sender, receiver);
                } else {
                    Log.d("TAG", "Lỗi khi lấy dữ liệu từ collection 'contact': ", task.getException());
                }
            }
        });
    }

    private void checkGroupBlock(String sender, String receiver) {
        CollectionReference groupCollection = db.collection("groups");
        DocumentReference groupDocument = groupCollection.document(receiver);

        groupDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        // Document tồn tại trong "groups"
                        List<DocumentReference> block = (List<DocumentReference>) documentSnapshot.get("block");
                        if (block != null) {
                            DocumentReference userBlock = db.collection("users").document(sender);
                            if (block.contains(userBlock)) {
                                // Người gửi bị chặn
                                check1 = true;
                            }
                            else {
                                check1=false;
                            }
                            checkSenderIsBlockReceiver(sender, receiver);
                            return;

                        }
                    }
                    check1=false;
                    checkSenderIsBlockReceiver(sender, receiver);
                    // Document không tồn tại trong "groups" hoặc không có chặn
                    // Ở đây, bạn có thể thêm xử lý phù hợp nếu cần thiết
                } else {
                    Log.d("TAG", "Lỗi khi lấy dữ liệu từ collection 'groups': ", task.getException());
                }
            }
        });
    }

    //Kiểm tra sender đã chặn receiver thì 2 người không thể nhắn tin cho nhau được
    private void checkSenderIsBlockReceiver(String sender, String receiver){
        CollectionReference contactCollection = db.collection("contact");
        DocumentReference contactDocument = contactCollection.document(sender);  // Truy vấn tài liệu contact có id bằng sender

        contactDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        List<DocumentReference> block = (List<DocumentReference>) documentSnapshot.get("block");
                        if (block == null) {
                            check2 = false;
                        }

                        DocumentReference userBlock = db.collection("users").document(receiver);
                        if (!block.contains(userBlock)) {
                            check2 = false;
                        }
                        else {
                            check2 = true;
                        }

                        handleSendMessage();
                    }
                } else {
                    Log.d("TAG", "Lỗi khi lấy dữ liệu: ", task.getException());
                }
            }
        });
    }

    //Hiển thị thông báo sender có bị receiver block
    public void showSenderIsBlockDialogBox(){
        String message;
        message="You cannot send the message because it has been blocked.";

        AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
        myBuilder.setIcon(R.drawable.ic_noti)
                .setMessage(message)
                .setPositiveButton("Close", null)
                .show();
    }

    //Hiển thị thông báo sender đã chặn receiver
    public void showReceiverIsBlockDialogBox(){
        String message;
        message="You've blocked this contact so you can't send them a message.";

        AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
        myBuilder.setIcon(R.drawable.ic_noti)
                .setMessage(message)
                .setPositiveButton("Close", null)
                .show();
    }
    private void readMessagesUser(final String myid, final String userid, final String imageurl) {
        mMessage.clear();
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

                                if ((message.getReceiver().equals(myid) && message.getSender().equals(userid))) {
                                    if (!message.getAppearStatus()) {
                                        if (!message.getMessage().equals(" ")) {
                                            mMessage.add(message);
                                            message.setAppeared();
                                        }
                                    }
                                }

                                if ((message.getReceiver().equals(userid) && message.getSender().equals(myid))) {
                                    if (!message.getAppearStatus()) {
                                        if (!message.getMessage().equals(" ")) {
                                            mMessage.add(message);
                                            message.setAppeared();
                                        }
                                    }
                                }

                                if ((message.getReceiver().equals(myid) && message.getSender().equals(""))) {
                                    if (!message.getAppearStatus() && d.getString("sender_delete").equals(userid)) {
                                        if (!message.getMessage().equals(" ")) {
                                            mMessage.add(message);
                                            message.setAppeared();
                                        }
                                    }
                                }

                                if ((message.getSender().equals(myid) && message.getReceiver().equals(""))) {
                                    if (!message.getAppearStatus() && d.getString("receiver_delete").equals(userid)) {
                                        if (!message.getMessage().equals(" ")) {
                                            mMessage.add(message);
                                            message.setAppeared();
                                        }
                                    }
                                }
                            }
                            messageUserAdapter = new MessageUserAdapter(ChatSreenActivity.this, mMessage, imageurl);
                            messageUserAdapter.setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(Message mess) {
                                    showDeleteDialog(mess);
                                }

                                @Override
                                public void onItemClickForward(Message mess, String type,String title) {
                                    showForwardDialog(mess);
                                }
                            });
                            recyclerView.setAdapter(messageUserAdapter);

                        } else {
                            Toast.makeText(ChatSreenActivity.this, "Không tìm thấy dữ liệu trong cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void readMessagesGroup(final String myid, final String groupid) {
        mMessage.clear();
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

                                assert message != null;
                                if (message.getReceiver().equals(groupid) && id.contains(message.getSender())) {
                                    if (!message.getAppearStatus()) {
                                        if (!message.getMessage().equals(" ")) {
                                            mMessage.add(message);
                                            message.setAppeared();
                                        }
                                    }
                                }
                            }
                            messageGroupAdapter = new MessageGroupAdapter(ChatSreenActivity.this, mMessage);
                            recyclerView.setAdapter(messageGroupAdapter);
                        } else {
                            Toast.makeText(ChatSreenActivity.this, "Không tìm thấy dữ liệu trong cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String messageID;

    private void findDocumentMessageId(Message mess) {
        FirebaseFirestore firestore =FirebaseFirestore.getInstance();
        CollectionReference cre = firestore.collection("messages");

        cre.whereEqualTo("message", mess.getMessage())
                .whereEqualTo("receiver", mess.getReceiver())
                .whereEqualTo("sender", mess.getSender())
                .whereEqualTo("timestamp", (Timestamp)mess.getTimestamp())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot d : task.getResult()) {
                                // Xử lý tài liệu ở đây
                                 messageID = d.getId();
                                if (messageID!=null) {
                                    Toast.makeText(getApplicationContext(), messageID, Toast.LENGTH_SHORT).show();
                                    deleteMessage(messageID);

                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_SHORT).show();

                                }
                                // Bạn có thể sử dụng messageID theo cách cần thiết
                            }
                        } else {
                            // Xử lý lỗi
                            Log.w(TAG, "Lỗi khi lấy tài liệu.", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"fail",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteMessage(String messageId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference messagesRef = db.collection("messages");

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("message", "Nội dung đã bị gỡ");
        updates.put("type","text");
        // Gọi hàm để xóa tin nhắn
        messagesRef.document(messageId)
                .update(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(ChatSreen.this, "Tin nhắn đã được xóa", Toast.LENGTH_SHORT).show();

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xóa tin nhắn thất bại
                        Toast.makeText(ChatSreenActivity.this, "Không thể gỡ tin nhắn", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showForwardDialog(final Message message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận chuyển tiếp tin nhắn");
        builder.setMessage("Bạn có chắc chắn muốn chuyển tiếp tin nhắn này?");
        builder.setPositiveButton("Chuyển tiếp", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Gọi hàm để xóa tin nhắn
//                findDocumentMessageId(message);
                Intent intent1=new Intent(getApplicationContext(), ForwardActivity.class);
                intent1.putExtra("messages", message.getMessage());
                intent1.putExtra("type",message.getType());
                intent1.putExtra("title",message.getTitle());
                startActivity(intent1);

            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Đóng Dialog nếu người dùng chọn Hủy
                dialog.dismiss();
            }
        });

        // Hiển thị Dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showDeleteDialog(final Message message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận gỡ tin nhắn");
        builder.setMessage("Bạn có chắc chắn muốn gỡ tin nhắn này?");
        builder.setPositiveButton("Gỡ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Gọi hàm để xóa tin nhắn
                findDocumentMessageId(message);
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Đóng Dialog nếu người dùng chọn Hủy
                dialog.dismiss();
            }
        });

        // Hiển thị Dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void applyNightMode() {
        sharedPreferences= MyChat.getSharedPreferences();
        boolean nightMode=sharedPreferences.getBoolean("night",false);
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    // Hàm để xóa tin nhắn từ Firestore và cập nhật RecyclerView

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

    @Override
    protected void onPause() {
        super.onPause();
//        Intent serviceIntent = new Intent(this, MessageNotification.class);
//        serviceIntent.putExtra("otherUser", "");
//        startService(serviceIntent);
        //
        screenshotDetector.stop();
    }

    protected void sendNotification(String sender, String receiver, boolean isGroup) {
        if (!isGroup) {
            CollectionReference notificationCollection = db.collection("notification");
            //
            HashMap<String, Object> notification = new HashMap<>();
            Timestamp timestamp = Timestamp.now();
            //
            notification.put("sender", sender);
            notification.put("receiver", receiver);
            notification.put("isGroup", "false");
            notification.put("timestamp", timestamp);
            //
            notificationCollection.add(notification);
        }
        else {//isGroup, receiver is group id BUT IN NOTIFICATION GROUP BECOME SENDER
            DocumentReference docRef = db.collection("groups").document(receiver);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot groupSnapshot = task.getResult();
                        if (groupSnapshot.exists()) {
                            ArrayList<DocumentReference> memberReferences = (ArrayList<DocumentReference>) groupSnapshot.get("member");

                            // Tạo một danh sách để chứa các ID của thành viên
                            ArrayList<String> memberIds = new ArrayList<>();

                            // Lặp qua các tham chiếu và lấy ID
                            for (DocumentReference memberRef : memberReferences) {
                                // Lấy ID từ tham chiếu
                                String memberId = memberRef.getId();
                                memberIds.add(memberId);
                            }

                            // Lặp qua danh sách ID và thực hiện các thao tác cần thiết
                            for (String memberId : memberIds) {
                                if (!memberId.equals(sender)) {
                                    CollectionReference notificationCollection = db.collection("notification");

                                    // Tạo thông báo
                                    HashMap<String, Object> notification = new HashMap<>();
                                    Timestamp timestamp = Timestamp.now();

                                    notification.put("sender", receiver); // xem như group là người gửi
                                    notification.put("receiver", memberId);
                                    notification.put("isGroup", "true");
                                    notification.put("timestamp", timestamp);

                                    // Thêm thông báo vào collection
                                    notificationCollection.add(notification);
                                }
                            }
                        }
                    } else {
                        Log.d("TAG", "Lỗi khi lấy dữ liệu: ", task.getException());
                    }
                }
            });

        }
    }
}