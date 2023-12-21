package com.example.mychat.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.mychat.activity.main.ChatSreenActivity;
import com.example.mychat.adapter.NewGroupAdapter;
import com.example.mychat.fragment.MainFragment;
import com.example.mychat.object.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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

public class AddNewMember extends Activity implements View.OnClickListener {
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

    Intent intent;
    DocumentReference docRef;
    MainFragment mainFragment;

    String groupID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_member);
        context=getApplicationContext();
        imgAdd = (ImageView) findViewById(R.id.add);
        imgAdd.setOnClickListener(this);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        editText = (EditText) findViewById(R.id.editText);
//        editText.requestFocus()

        intent = getIntent();
        groupID = intent.getStringExtra("groupId");
        getListOfMember();
        searchUserAdd();
        listView = (ListView) findViewById(R.id.listView);
    }
    void getListOfMember(){
        db = FirebaseFirestore.getInstance();
        docRef = db.collection("groups").document(groupID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.contains("member")) {
                            List<DocumentReference> memberReferences = (List<DocumentReference>) document.get("member");

                            if (memberReferences != null && !memberReferences.isEmpty()) {
                                List<Task<DocumentSnapshot>> memberTasks = new ArrayList<>();
                                for (DocumentReference memberRef : memberReferences) {
                                    Task<DocumentSnapshot> memberTask = memberRef.get();
                                    memberTasks.add(memberTask);
                                }
                                Tasks.whenAllComplete(memberTasks)
                                        .addOnCompleteListener(task1 -> {
                                            List<String> memberList=new ArrayList<>();
                                            String creatorId = document.getString("creator");
                                            String memberId;
                                            for (Task<DocumentSnapshot> memberTask : memberTasks) {
                                                if (memberTask.isSuccessful()) {
                                                    DocumentSnapshot memberSnapshot = memberTask.getResult();
                                                    if (memberSnapshot.exists()) {
                                                        memberId = memberSnapshot.getId();
                                                        memberList.add(memberId);
                                                    }
                                                }
                                            }

                                            getContactExists(memberList);
                                            searchUserAdd();
                                        });
                            }
                        }
                    } else {
                        Toast.makeText(AddNewMember.this, "Group document not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddNewMember.this, "Error fetching group document: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    };


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
    protected void getContactExists(List<String> listMemberId) {
//        if (listMemberId.size()==0){
//            Toast.makeText(AddNewMember.this, "djmflf", Toast.LENGTH_SHORT).show();
//        }
//        else{
//            Toast.makeText(AddNewMember.this, listMemberId.get(listMemberId.size()-1), Toast.LENGTH_SHORT).show();
//        }
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
                                                Boolean isExisted=false;
                                                for (String memberId: listMemberId){
                                                    if (memberId.compareTo(contactDocument.getId())==0)
                                                    {
                                                        isExisted=true;
                                                    }
                                                }
                                                if (!isExisted){
                                                    String email = contactDocument.getString("email");
                                                    String username = contactDocument.getString("username");
                                                    User u=new User(username, "...", contactDocument.getString("avatarUrl"), email,contactDocument.getId(),Timestamp.now());
                                                    user.add(u);
                                                    userAdapter.add(user.get(user.size()-1));
                                                    adapter = new NewGroupAdapter(AddNewMember.this, R.layout.adapter_new_group,userAdapter);
                                                    listView.setAdapter(adapter);
                                                    adapter.notifyDataSetChanged();
                                                }
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
                StringBuilder message = new StringBuilder("Bạn muốn thêm ");
                for (User u : us) {
                    message.append(u.getName()).append(" , ");
                }
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
                myBuilder.setIcon(R.drawable.ic_noti)
                        .setTitle("New Member")
                        .setMessage(message)
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                saveMember();
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

    private void saveMember() {
        FirebaseFirestore ff=FirebaseFirestore.getInstance();
        CollectionReference contactCollection = ff.collection("groups");
        DocumentReference groupDocument = contactCollection.document(groupID);

        List<DocumentReference> userReferences = new ArrayList<>();
        for (User user : us) {
            String uid = user.getUid();
            // Tạo DocumentReference từ uid của mỗi người dùng và thêm vào danh sách
            DocumentReference userReference = ff.collection("users").document(uid);
            userReferences.add(userReference);
        }
        // Đọc mảng hiện tại từ tài liệu
        groupDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        List<DocumentReference> members = (List<DocumentReference>) documentSnapshot.get("member");
                        if (members == null) {
                            members = new ArrayList<>();
                        }
                        for (DocumentReference user: userReferences){
                            if (!members.contains(ff.collection("user").document(user.getId()))){
                                members.add(user);
                            }
                        }
                        Map<String, Object> data = new HashMap<>();
                        data.put("member", members);
                        // Sử dụng merge() để chỉ cập nhật trường 'groups' mà không thay đổi các trường khác
                        groupDocument.set(data, SetOptions.merge())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        for (User u:us) {
                                             saveGroupContact(groupID,u.getUid());
                                        }
                                        showNiceDialogBox();
                                    }
                                });
                    }
                }
            }
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

    public void showNiceDialogBox(){
        String message="Add member successfully!";

        AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
        myBuilder.setIcon(R.drawable.ic_noti)
                .setTitle("Add member")
                .setMessage(message)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent(AddNewMember.this, ChatSreenActivity.class);
                        intent.putExtra("receiverID",groupID);
                        AddNewMember.this.startActivity(intent);
                        intent.putExtra("group", true);
                        startActivity(intent);
                    }
                })
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