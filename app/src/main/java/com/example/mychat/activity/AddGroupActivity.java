package com.example.mychat.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddGroupActivity extends Activity implements View.OnClickListener {
    ImageView imgAdd;
    EditText editText;
    ListView listView;


    ImageView back;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    FirebaseFirestore db;
    DocumentReference dref;
    //
    List<User> user = new ArrayList<>(); //tên người liên hệ
    List<User> userAdapter=new ArrayList<>();
    private NewGroupAdapter adapter;
    Context context;
    MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        context=getApplicationContext();
        imgAdd = (ImageView) findViewById(R.id.add);
        imgAdd.setOnClickListener(this);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        editText = (EditText) findViewById(R.id.editText);
//        editText.requestFocus();
        searchUserAdd();
        getContactExists();
        listView = (ListView) findViewById(R.id.listView);
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

    private void showBtnAdd() {
        for (User u: user) {
            if (u.isChecked()){
                imgAdd.setVisibility(View.VISIBLE);
                return;
            }
        }
        imgAdd.setVisibility(View.GONE);
    }


//    private void onClickListView() {
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                view.setBackgroundResource(R.color.lightblue);
//            }
//        });
//    }
//    private void showDialogUserInfo(View view) {
//        AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
//        myBuilder.setView(dialogView);
//
//        ImageView avt = dialogView.findViewById(R.id.avt);
////        Picasso.get().load(userAdd.getImg()).into(avt);
//        avt.setImageResource(R.drawable.ic_avt);
//        TextView username = dialogView.findViewById(R.id.username);
//        username.setText(userAdd.getName());
//        TextView email = dialogView.findViewById(R.id.email);
//        email.setText(userAdd.getEmail());
//        TextView bio = dialogView.findViewById(R.id.bio);
//        bio.setText(userAdd.getString());
//
//        myBuilder.setTitle("Add contact")
//                .setIcon(R.drawable.ic_add_friend)
//                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        view.setBackgroundResource(R.color.transparent);
//
//                    }
//                })
//                .setNegativeButton("Add", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        view.setBackgroundResource(R.color.transparent);
//
//                        saveUserContact(userAdd.getEmail(),userAdd.getName());
//                        sendMessage(auth.getUid(),userAdd.getUid()," ");
//                    }});
//        AlertDialog alertDialog = myBuilder.create();
//        alertDialog.show();
//    }

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
                                                adapter = new NewGroupAdapter(AddGroupActivity.this, R.layout.adapter_new_group,userAdapter);
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
        if (v.getId()==R.id.add) {
            findUserAdd();
            if (us.size() != 0) {
                StringBuilder message = new StringBuilder("Bạn muốn tạo nhóm với ");
                for (User u : us) {
                    message.append(u.getName()).append(" , ");
                }
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
                myBuilder.setIcon(R.drawable.ic_noti)
                        .setTitle("New Group")
                        .setMessage(message)
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                saveGroup();
                            }})
                        .show();
            }
            else {
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
                myBuilder.setIcon(R.drawable.ic_noti)
                        .setTitle("New Group")
                        .setMessage("Vui lòng chọn người dùng để tạo nhóm")
                        .setPositiveButton("Close", null)
                        .show();
            }
        }
    }

    private void saveGroup() {
        FirebaseFirestore ff=FirebaseFirestore.getInstance();
        CollectionReference groupCollection = ff.collection("groups");
        List<DocumentReference> userReferences = new ArrayList<>();

        StringBuilder n=new StringBuilder("Nhóm của ");

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
        groupInfo.put("avatarUrl","https://firebasestorage.googleapis.com/v0/b/mychat-7f8c6.appspot.com/o/images%2Fic_avt.png?alt=media&token=bdee67b4-9273-4176-a185-24806ef76ced");

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
        String message="Create group successfully!";

        AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
        myBuilder.setIcon(R.drawable.ic_noti)
                .setTitle("Create Group")
                .setMessage(message)
                .setPositiveButton("Close", null)
                .show();
    }
}