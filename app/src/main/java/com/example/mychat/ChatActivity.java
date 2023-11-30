package com.example.mychat;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;



import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.joery.animatedbottombar.AnimatedBottomBar;


public class ChatActivity extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    ImageView imgNewChat;
    SearchView searchView;
    ListView listView;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    //
    FirebaseFirestore db;
    CollectionReference cref;
    Query query;
    //
    List<User> user=new ArrayList<>();//new ArrayList<>(); //tên người liên hệ
    String emailCurrentUser;
    SharedPreferences sharedPreferences;
    private MyArrayAdapter adapter;
    AnimatedBottomBar bottomBar;

    Context context;
    MainFragment mainFragment;
    public static ChatActivity newInstance(String strArg) {
        ChatActivity fragment = new ChatActivity();
        Bundle args = new Bundle();
        args.putString("ChatActivity", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat);
        try {
            context = getActivity(); // use this reference to invoke main callbacks
            mainFragment = (MainFragment) getActivity();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainFragment must implement callbacks");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LinearLayout layout_chat = (LinearLayout) inflater.inflate(R.layout.activity_chat, null);

        searchView=layout_chat.findViewById(R.id.searchView);

        listView = (ListView)layout_chat.findViewById(R.id.listView);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(this);

        imgNewChat = (ImageView) layout_chat.findViewById(R.id.imgNewChat);

//        getListUserFromDatabase();


        return layout_chat;
    }

    @Override
    public void onResume() {
        super.onResume();
        getListUserFromDatabase();
        searchUserWithUserName();
    }

    @Override
    public void onClick(View view) {

    }

    private void searchUserWithUserName() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter!=null){
                    adapter.getFilter().filter(newText);

                }
                return true;
            }
        });
    }

    private void getListUserFromDatabase() {
        user = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        //get MESSAGE collection
        cref = db.collection("messages");
        //get user's id
        String currentUser = auth.getCurrentUser().getUid().toString();
        //
        Query user2contact = cref.whereEqualTo("sender", currentUser).orderBy("timestamp", Query.Direction.DESCENDING);
        Query contact2user = cref.whereEqualTo("receiver", currentUser).orderBy("timestamp", Query.Direction.DESCENDING);


        //Toast.makeText(context,auth.getCurrentUser().getUid().toString(), Toast.LENGTH_SHORT).show();
        Task<QuerySnapshot> u2cTask = user2contact.get();
        Task<QuerySnapshot> c2uTask = contact2user.get();
        u2cTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Xử lý kết quả thành công
                } else {
                    // Xử lý trường hợp task thất bại
                    Exception e = task.getException();
                    if (e != null) {
                        Toast.makeText(getActivity(),"fail 1",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        c2uTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Xử lý kết quả thành công
                } else {
                    // Xử lý trường hợp task thất bại
                    Exception e = task.getException();
                    if (e != null) {
                        Toast.makeText(getActivity(),"fail 2",Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
        Task<List<QuerySnapshot>> mergedTask = Tasks.whenAllSuccess(u2cTask, c2uTask);
        mergedTask.addOnCompleteListener(new OnCompleteListener<List<QuerySnapshot>>() {
            @Override
            public void onComplete(Task<List<QuerySnapshot>> task) {
                if (task.isSuccessful()) {
                    List<QuerySnapshot> querySnapshots = task.getResult();
                    List<DocumentSnapshot> mergedDocuments = new ArrayList<>();
                    //
                    for (QuerySnapshot querySnapshot : querySnapshots) {
                        mergedDocuments.addAll(querySnapshot.getDocuments());
                    }
                    // Get latest message from EACH contact
                    List<DocumentSnapshot> result = new ArrayList<>();
                    //
                    for (DocumentSnapshot document : mergedDocuments) {
                        //get contact
                        String contact = document.getString("receiver");
                        if (contact.equals(currentUser)){
                            if(document.getString("sender").equals("")){
                                contact = document.getString("sender_delete");
                            }
                            else {
                                contact = document.getString("sender");
                            }
                        }

                        if(document.getString("receiver").equals("")){
                            contact = document.getString("receiver_delete");
                        }
                        //
                        Timestamp timestamp = document.getTimestamp("timestamp");
                        //
                        boolean duplicated = false;
                        int duplicatedIndex = 0;
                        //
                        String contactCheck;
                        Timestamp timestampCheck = null;
                        //
                        for (DocumentSnapshot check : result) {
                            contactCheck = check.getString("receiver");
                            if (contactCheck.equals(currentUser)){
                                if(check.getString("sender").equals("")){
                                    contactCheck = check.getString("sender_delete");
                                }
                                else {
                                    contactCheck = check.getString("sender");
                                }
                            }

                            if(check.getString("receiver").equals("")){
                                contactCheck = check.getString("receiver_delete");
                            }
                            timestampCheck = check.getTimestamp("timestamp");
                            //
                            if (contact.equals(contactCheck)) {
                                duplicated = true;
                                break;
                            }
                            duplicatedIndex++;
                        }
                        if (duplicated) {
                            if (timestamp.compareTo(timestampCheck) > 0) {
                                result.set(duplicatedIndex, document);
                            }
                        } else result.add(document);
                    }

                    //SAU KHI CÓ ĐƯỢC RESULT
                    final int totalUsers = result.size();
                    final int[] counter = {0};
                    CollectionReference userRef = db.collection("users");
                    for (DocumentSnapshot d : result) {
                        String latestMessage = d.getString("message");
                        //
                        String contact = d.getString("receiver");
                        if (contact.equals(currentUser)){
                            if(d.getString("sender").equals("")){
                                contact = d.getString("sender_delete");
                            }
                            else {
                                contact = d.getString("sender");
                            }
                        }

                        if(d.getString("receiver").equals("")){
                            contact = d.getString("receiver_delete");
                        }
                        //lấy ref của contact
                        DocumentReference userDoc = userRef.document(contact);
                        //lấy những thông tin cần thiết của contact
                        String[] uid = new String[1];
                        String[] email = new String[1];
                        String[] username = new String[1];
                        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot userSnapshot = task.getResult();
                                    if (userSnapshot.exists()) {
                                        uid[0] = userSnapshot.getId();
                                        username[0] = userSnapshot.getString("username");
                                        email[0] = userSnapshot.getString("email");

                                        user.add(new User(username[0], latestMessage, R.drawable.ic_avt, latestMessage, uid[0]));

                                        adapter = new MyArrayAdapter(context, R.layout.array_adapter, user);
                                        listView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                        //user.add(new User("username[0]",latestMessage, R.drawable.ic_avt, latestMessage,"uid[0]"));
                        counter[0]++;
                        // Kiểm tra nếu tất cả các cuộc gọi đã hoàn thành
                        if (counter[0] == totalUsers) {
                            // Tất cả các cuộc gọi đã hoàn thành, cập nhật adapter ở đây
                            adapter = new MyArrayAdapter(context, R.layout.array_adapter, user);
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        User item=(User)adapterView.getItemAtPosition(i);
        Intent intent=new Intent(context, ChatSreen.class);
        intent.putExtra("receiverID",item.getUid());
        startActivity(intent);
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


}