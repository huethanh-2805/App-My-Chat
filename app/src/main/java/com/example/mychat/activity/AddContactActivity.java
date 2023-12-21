package com.example.mychat.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mychat.R;
import com.example.mychat.adapter.ChatAdapter;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddContactActivity extends Activity implements View.OnClickListener {
    ImageView imgAdd;
    EditText editText;
    ListView listView;

    ImageView back;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    FirebaseFirestore db;
    CollectionReference cref;
    //
    List<User> user = new ArrayList<>(); //tên người liên hệ
    private ChatAdapter adapter;
    Context context;
    MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        context=getApplicationContext();
        imgAdd = (ImageView) findViewById(R.id.add);
        imgAdd.setOnClickListener(this);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listView);
        onClickListView();
        editText = (EditText) findViewById(R.id.editText);
        searchUserAdd();
    }



    private void searchUserAdd() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                imgAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkContactExists();
            }
        });
    }


    private int selectedPosition = -1; // Biến để lưu trữ vị trí của item được chọn trước đó
    private User userAdd;
    private void onClickListView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setBackgroundResource(R.color.lightblue);
                userAdd=(User)parent.getItemAtPosition(position);
                showDialogUserInfo(view);
            }

        });
    }

    private void showDialogUserInfo(View view) {
        AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        myBuilder.setView(dialogView);

        ImageView avt = dialogView.findViewById(R.id.avt);
//        Picasso.get().load(userAdd.getImg()).into(avt);
        avt.setImageResource(R.drawable.ic_avt);
        TextView username = dialogView.findViewById(R.id.username);
        username.setText(userAdd.getName());
        TextView email = dialogView.findViewById(R.id.email);
        email.setText(userAdd.getEmail());
        TextView bio = dialogView.findViewById(R.id.bio);
        bio.setText(userAdd.getString());

        myBuilder.setTitle("Add contact")
                .setIcon(R.drawable.ic_add_friend)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.setBackgroundResource(R.color.transparent);

                    }
                })
                .setNegativeButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        view.setBackgroundResource(R.color.transparent);

                        saveUserContact(userAdd.getEmail(),userAdd.getName());
                        sendMessage(auth.getUid(),userAdd.getUid()," ");
                    }});
        AlertDialog alertDialog = myBuilder.create();
        alertDialog.show();
    }
    protected void checkContactExists() {
        user.clear();
        final String emailToCheck = editText.getText().toString().trim();
        db = FirebaseFirestore.getInstance();
        cref = db.collection("users");
        cref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot d : task.getResult()) {
                        String email = d.getString("email");
                        if (!emailToCheck.isEmpty() && email.contains(emailToCheck)) {
                            String username = d.getString("username");
                            user.add(new User(username, "...", d.getString("avatarUrl"), email,d.getId(),Timestamp.now()));
                        }
                        adapter = new ChatAdapter(AddContactActivity.this, R.layout.array_adapter, user);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "fail for", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
//        if (v.getId()==R.id.add){
//            saveUserContact(userAdd.getEmail(),userAdd.getName());
////            saveUserChat(userAdd.getUid(),auth.getCurrentUser().getUid(),"");
//            sendMessage(auth.getCurrentUser().getUid(),userAdd.getUid(),"123");
//        }
        if(v.getId()==R.id.back) {
            finish();
        }
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
    }

//    private void saveUserChat(String user1,String user2,String message) {
//        CollectionReference chatsCollection = db.collection("chats");
//
//        Query query = chatsCollection
//                .whereEqualTo("user1", user1)
//                .whereEqualTo("user2", user2);
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    if (task.getResult().size() == 0) {
//                                // Nếu không tìm thấy tài liệu thỏa mãn điều kiện user1 = u1 và user2 = u2,
//                                // thực hiện truy vấn ngược lại user1 = u2 và user2 = u1
//                                Query reverseQuery = chatsCollection
//                                        .whereEqualTo("user1", user2)
//                                        .whereEqualTo("user2", user1);
//
//                                reverseQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                    @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> reverseTask) {
//                                if (reverseTask.isSuccessful() && reverseTask.getResult().size() == 0) {
//                                    HashMap<String, Object> chatData = new HashMap<>();
//                                    chatData.put("user1", user1);
//                                    chatData.put("user2", user2);
//                                    chatData.put("lastMessage", message);
//                                    chatsCollection.add(chatData);
//                                }
//                            }
//                        });
//                    }
//                } else {
//                    // Xử lý khi truy vấn không thành công
//                }
//            }
//        });
//    }


    public void showNiceDialogBox(boolean check){
        String message;
        if(check) {
            message="Add contact successfully!";
        }
        else {
            message="This contact was added before!";
        }
        AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
        myBuilder.setIcon(R.drawable.ic_noti)
                .setTitle("Add contact")
                .setMessage(message)
                .setPositiveButton("Close", null)
                .show();
    }

    private void saveUser(String userUid) {
        FirebaseFirestore ff=FirebaseFirestore.getInstance();

        CollectionReference contactCollection = ff.collection("contact");
        DocumentReference userDocument = contactCollection.document(userUid);

        // Đọc mảng hiện tại từ tài liệu
        userDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (!documentSnapshot.exists()) {
                        // Đọc danh sách hiện tại từ tài liệu
//                        contactCollection.add(new HashMap<>());
                        Map<String, Object> emptyData = new HashMap<>();
                        userDocument.set(emptyData)
                                .addOnSuccessListener(aVoid -> {
                                    List<DocumentReference> emptyArray = new ArrayList<>();
                                    userDocument.update("block",emptyArray)
                                            .addOnSuccessListener(aVoid1 -> {
                                                // Mảng đã được tạo thành công
                                            })
                                            .addOnFailureListener(e -> {
                                                // Xử lý khi không thể tạo mảng
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    // Xử lý khi không thể tạo document
                                });
                    }

                    List<DocumentReference> userAdds = (List<DocumentReference>) documentSnapshot.get("userContact");

                    if (userAdds==null) {
                        userAdds= new ArrayList<>();
                        userAdds.add(ff.collection("users").document(auth.getCurrentUser().getUid().toString()));

                        // Cập nhật tài liệu với danh sách mới
                        Map<String, Object> data = new HashMap<>();
                        data.put("userContact", userAdds);

                        userDocument.update(data)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // Xử lý khi hoàn thành (nếu cần)
//                                                            showNiceDialogBox(true);
                                    }
                                });
                    }
                    // Thêm mới DocumentReference vào danh sách nếu chưa có
                    if (!userAdds.contains(ff.collection("users").document(auth.getCurrentUser().getUid().toString()))) {
                        userAdds.add(ff.collection("users").document(auth.getCurrentUser().getUid().toString()));

                        // Cập nhật tài liệu với danh sách mới
                        Map<String, Object> data = new HashMap<>();
                        data.put("userContact", userAdds);

                        userDocument.set(data, SetOptions.merge())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // Xử lý khi hoàn thành (nếu cần)
//                                                            showNiceDialogBox(true);

                                    }
                                });
                    }
                }
            }
        });
    }
    private void saveUserContact(String email, String name) {
        final String[] uid=new String[1];
        FirebaseFirestore ff=FirebaseFirestore.getInstance();
        CollectionReference userCollection = ff.collection("users");
        userCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        if(Objects.equals(document.getString("email"), email) && Objects.equals(document.getString("username"), name)){
                            uid[0] =document.getId();
                            break;
                        }
                    }
                    if (uid[0]!=null) {
                        CollectionReference contactCollection = ff.collection("contact");
                        DocumentReference userDocument = contactCollection.document(auth.getCurrentUser().getUid().toString());

                        // Đọc mảng hiện tại từ tài liệu
                        userDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        // Đọc danh sách hiện tại từ tài liệu
                                        List<DocumentReference> userAdds = (List<DocumentReference>) documentSnapshot.get("userContact");

                                        // Thêm mới DocumentReference vào danh sách nếu chưa có
                                        if (!userAdds.contains(ff.collection("users").document(uid[0]))) {
                                            userAdds.add(ff.collection("users").document(uid[0]));

                                            // Cập nhật tài liệu với danh sách mới
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("userContact", userAdds);

                                            userDocument.set(data, SetOptions.merge())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            // Xử lý khi hoàn thành (nếu cần)
                                                            showNiceDialogBox(true);
                                                            saveUser(userAdd.getUid());

                                                        }
                                                    });
                                        }
                                        else{
                                            showNiceDialogBox(false);
                                        }
                                    }
                                } else {
                                    Log.d("TAG", "Lỗi khi lấy dữ liệu: ", task.getException());
                                }
                            }
                        });
                    }
                } else {
                    Log.d("TAG", "Lỗi khi lấy dữ liệu: ", task.getException());
                }
            }
        });
    }
}