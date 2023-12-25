package com.example.mychat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

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
    List<User> userAdapter=new ArrayList<>();
    private AdapterNewGroup adapter;
    Context context;
    MainFragment mainFragment;

    String mess=" ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward);
        context=getApplicationContext();
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
                                                User u=new User(username, "...", contactDocument.getString("avatarUrl"), email,contactDocument.getId(),Timestamp.now());
                                                user.add(u);
                                                userAdapter.add(user.get(user.size()-1));
                                                adapter = new AdapterNewGroup(ForwardActivity.this, R.layout.adapter_new_group,userAdapter);
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
                }
                else {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "fail for", Toast.LENGTH_SHORT).show();
            }
        });
    }

    List<User> us=new ArrayList<>();
    private void findUserAdd() {
        us.clear();
        for (User u:user) {
            if (u.isChecked()) {
                us.add(u);
            }
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.back) {
            finish();
        }
        if (v.getId()==R.id.imgSend) {
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
                                for (User u: user) {
                                    sendMessage(auth.getCurrentUser().getUid(),u.getUid(),mess);
                                }
                                showNiceDialogBox();
                            }})
                        .show();
            }
            else {
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
                myBuilder.setIcon(R.drawable.ic_noti)
                        .setTitle("Forward Messages")
                        .setMessage("Vui lòng chọn người dùng để chuyển tiếp")
                        .setPositiveButton("Close", null)
                        .show();
            }
        }
    }

    private void saveGroup() {
        FirebaseFirestore ff=FirebaseFirestore.getInstance();
        CollectionReference groupCollection = ff.collection("groups");

        List<DocumentReference> userReferences = new ArrayList<>();
        StringBuilder n=new StringBuilder("Nhóm của "+auth.getCurrentUser().getDisplayName()+" ");

        for (User user : us) {
            String uid = user.getUid();
            // Tạo DocumentReference từ uid của mỗi người dùng và thêm vào danh sách
            DocumentReference userReference = ff.collection("users").document(uid);
            userReferences.add(userReference);
            n.append(", ").append(user.getName());
        }
        userReferences.add(ff.collection("users").document(auth.getCurrentUser().getUid()));

        String name=n.toString();
        HashMap<String, Object> groupInfo = new HashMap<>();
        Timestamp timestamp = Timestamp.now();
        groupInfo.put("username",name);
        groupInfo.put("email"," ");
        groupInfo.put("creator", auth.getCurrentUser().getUid() );
        groupInfo.put("member", userReferences);
        groupInfo.put("timestamp",timestamp);
        groupInfo.put("avatarUrl","https://firebasestorage.googleapis.com/v0/b/mychat-7f8c6.appspot.com/o/images%2F1701658755851.jpg?alt=media&token=c256c427-9a37-4c12-bafe-73b78fe6d3a4");

        groupCollection.add(groupInfo)
                .addOnSuccessListener(documentReference -> {
                    String documentUid = documentReference.getId(); // Lấy uid của document vừa thêm
                    sendMessage(auth.getUid(),documentUid," ");
                    saveGroupContact(documentUid,auth.getCurrentUser().getUid()); // Sử dụng uid này nếu cần thiết
                    for (User u:us) {
                        saveGroupContact(documentUid,u.getUid());
                    }
                    showNiceDialogBox();
                })
                .addOnFailureListener(e -> {
                    // Thêm không thành công
                    // Xử lý lỗi ở đây
                });
    }
    private void saveGroupContact(String id,String uid) {
        FirebaseFirestore ff=FirebaseFirestore.getInstance();
        CollectionReference contactCollection = ff.collection("contact");
        DocumentReference userDocument = contactCollection.document(uid);
        // Đọc mảng hiện tại từ tài liệu
        userDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        List<DocumentReference> groups = (List<DocumentReference>) documentSnapshot.get("groups");

                        if (groups == null) {
                            groups = new ArrayList<>();
                        }

                        if (!groups.contains(ff.collection("groups").document(id))) {
                            groups.add(ff.collection("groups").document(id));
                            Map<String, Object> data = new HashMap<>();
                            data.put("groups", groups);
                            // Sử dụng merge() để chỉ cập nhật trường 'groups' mà không thay đổi các trường khác
                            userDocument.set(data, SetOptions.merge())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            // Xử lý khi hoàn thành (nếu cần)

                                        }
                                    });
                        }
                    }
                }
            }
        });
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
    }
    public void showNiceDialogBox(){
        String message="Forward messages successfully!";

        AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
        myBuilder.setIcon(R.drawable.ic_noti)
                .setTitle("Forward Messages")
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
                                                            showNiceDialogBox();
//                                                            saveUser(userAdd.getUid());

                                                        }
                                                    });
                                        }
                                        else{
                                            showNiceDialogBox();
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
