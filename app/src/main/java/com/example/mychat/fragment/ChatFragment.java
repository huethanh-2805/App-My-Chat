package com.example.mychat.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.mychat.activity.AddGroupActivity;
import com.example.mychat.activity.main.ChatSreenActivity;
import com.example.mychat.adapter.ChatAdapter;
import com.example.mychat.others.MyChat;
import com.example.mychat.R;
import com.example.mychat.object.User;
import com.google.android.gms.tasks.OnCompleteListener;
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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import nl.joery.animatedbottombar.AnimatedBottomBar;


public class ChatFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    ImageView imgNewGroup;
    SearchView searchView;
    ListView listView;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    //
    FirebaseFirestore db;
    CollectionReference cref;
    //
    List<User> user=new ArrayList<>();
    List<String> group=new ArrayList<>();
    SharedPreferences sharedPreferences;
    private ChatAdapter adapter;

    Context context;
    MainFragment mainFragment;
    TextView txtStatus;
    public static ChatFragment newInstance(String strArg) {
        ChatFragment fragment = new ChatFragment();
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
        txtStatus=layout_chat.findViewById(R.id.txtStatus);

        imgNewGroup = (ImageView) layout_chat.findViewById(R.id.imgNewGroup);
        imgNewGroup.setOnClickListener(this);
//      getListUserFromDatabase();

        return layout_chat;
    }



    @Override
    public void onResume() {
        super.onResume();
        getListGroup();
        searchUserWithUserName();

        // Đăng ký BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        NetworkChangeReceiver receiver = new NetworkChangeReceiver();
        context.registerReceiver(receiver, intentFilter);
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkConnected(context)) {
                txtStatus.setVisibility(View.GONE);
            } else {
                txtStatus.setText("No internet connection...");
                txtStatus.setVisibility(View.VISIBLE);
            }
        }

        //Kiểm tra kết nối mang
        private boolean isNetworkConnected(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
    }
    @Override
    public void onClick(View view) {
        if (view.getId()==imgNewGroup.getId()) {
            startActivity(new Intent(context, AddGroupActivity.class));
        }
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
//    private void getListGroup() {
//        CollectionReference cGroup = FirebaseFirestore.getInstance().collection("groups");
//        AtomicInteger tasksCompleted = new AtomicInteger(0);
//        AtomicInteger totalTasks = new AtomicInteger(0); // Số lượng tác vụ cần hoàn thành
//        cGroup.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    List<DocumentReference> members = (List<DocumentReference>) document.get("member");
//                    if (members != null) {
//                        totalTasks.addAndGet(members.size()); // Cập nhật tổng số lượng tác vụ cần hoàn thành
//
//                        for (DocumentReference member : members) {
//                            member.get().addOnCompleteListener(memberTask -> {
//                                if (memberTask.isSuccessful()) {
//                                    DocumentSnapshot documentSnapshot = memberTask.getResult();
//                                    if (documentSnapshot.exists()) {
//                                        if (documentSnapshot.getId().equals(auth.getCurrentUser().getUid())) {
//                                            group.add(document.getId());
//                                        }
//                                    }
//                                } else {
//                                    // Xử lý lỗi khi không thể lấy DocumentReference
//                                }
//
//                                // Tăng số lượng tác vụ đã hoàn thành
//                                int count = tasksCompleted.incrementAndGet();
//
//                                // Kiểm tra xem đã hoàn thành tất cả các tác vụ chưa
//                                if (count == totalTasks.get()) {
//                                    // Nếu đã hoàn thành tất cả, thực hiện công việc tiếp theo ở đây
//                                    getListUserFromDatabase();
//                                }
//                            });
//                        }
//                    }
//                }
//            }
//            else {
//                getListUserFromDatabase();
//            }
//        });
//    }
    private void getListGroup() {
        CollectionReference cGroup = FirebaseFirestore.getInstance().collection("groups");

        cGroup.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AtomicInteger tasksCompleted = new AtomicInteger(0);
                AtomicInteger totalTasks = new AtomicInteger(0); // Số lượng tác vụ cần hoàn thành

                if (task.getResult().isEmpty()) {
                    // Nếu không có tài liệu trong cGroup, gọi getListUserFromDatabase ngay lập tức
                    getListUserFromDatabase();
                    return;
                }

                for (QueryDocumentSnapshot document : task.getResult()) {
                    List<DocumentReference> members = (List<DocumentReference>) document.get("member");

                    if (members != null && !members.isEmpty()) {
                        totalTasks.addAndGet(members.size());

                        for (DocumentReference member : members) {
                            member.get().addOnCompleteListener(memberTask -> {
                                if (memberTask.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = memberTask.getResult();
                                    if (documentSnapshot.exists() && documentSnapshot.getId().equals(auth.getCurrentUser().getUid())) {
                                        group.add(document.getId());
                                    }
                                } else {
                                    // Xử lý lỗi khi không thể lấy DocumentReference
                                }

                                int count = tasksCompleted.incrementAndGet();

                                if (count == totalTasks.get()) {
                                    // Nếu đã hoàn thành tất cả, thực hiện công việc tiếp theo ở đây
                                    getListUserFromDatabase();
                                }
                            });
                        }
                    } else {
                        // Nếu members rỗng, cũng tăng số lượng tác vụ đã hoàn thành
                        int count = tasksCompleted.incrementAndGet();

                        if (count == totalTasks.get()) {
                            // Nếu đã hoàn thành tất cả, thực hiện công việc tiếp theo ở đây
                            getListUserFromDatabase();
                        }
                    }
                }
            } else {
                // Xử lý lỗi khi không thể lấy dữ liệu từ cGroup
            }
        });
    }


    private void getListUserFromDatabase() {
        user.clear();
//        user = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        //get MESSAGE collection
        cref = db.collection("messages");
        //get user's id
        String currentUser = auth.getCurrentUser().getUid().toString();
        //
        Query user2contact = cref.whereEqualTo("sender", currentUser).orderBy("timestamp", Query.Direction.DESCENDING);
        Query contact2user = cref.whereEqualTo("receiver", currentUser).orderBy("timestamp", Query.Direction.DESCENDING);

        Task<QuerySnapshot> g2uTask=null;
        if (group.size()!=0){
            Query group2user = cref.whereIn("receiver", group).orderBy("timestamp", Query.Direction.DESCENDING);

            g2uTask = group2user.get();
            g2uTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Có dữ liệu trả về từ truy vấn
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                // Xử lý dữ liệu ở đây
                            }
                        } else {
                            // Không có dữ liệu trả về từ truy vấn
                        }
                    } else {
                        // Xử lý khi truy vấn không thành công
                    }
                }
            });
        }
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

        Task<List<QuerySnapshot>> mergedTask=null;
        if (g2uTask!=null) {
            mergedTask = Tasks.whenAllSuccess(u2cTask, c2uTask, g2uTask);
        }
        else {
            mergedTask = Tasks.whenAllSuccess(u2cTask, c2uTask);
        }
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
//                    sortDescendingDocument(result);


                    final int[] counter = {0};
                    CollectionReference userRef = db.collection("users");
                    CollectionReference groupRef = db.collection("groups");

                    for (DocumentSnapshot d : result) {
                        String latestMessage = d.getString("message");
                        String type = d.getString("type");
                        if (!type.equals("text")) latestMessage = "[FILE ĐÍNH KÈM]";
                        if (type.equals("screenshot")) latestMessage = "[CHỤP MÀN HÌNH]";
                        if (type.equals("image")) latestMessage = "[ẢNH]";
                        if (type.equals("video")) latestMessage = "[VIDEO]";
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
                        else {
                            if (group.contains(contact) && d.getString("sender").equals(currentUser)) {
                                if (!latestMessage.equals(" ")) {
                                    latestMessage = "Bạn: " + latestMessage;
                                }
                            }
                        }

                        if(d.getString("receiver").equals("")){
                            contact = d.getString("receiver_delete");
                        }
                        //lấy ref của contact
                        DocumentReference userDoc = userRef.document(contact);
                        DocumentReference groupDoc = groupRef.document(contact);

                        //lấy những thông tin cần thiết của contact
                        String[] uid = new String[1];
                        String[] email = new String[1];
                        String[] username = new String[1];
                        //cắt chuỗi nếu quá dài
                        if (latestMessage.length() > 48) {
                            latestMessage = latestMessage.substring(0,45) + "...";
                        }
                        String[] latest = {latestMessage};

                        if (group.contains(contact)) {
                            groupDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot groupSnapshot = task.getResult();
                                        if (groupSnapshot.exists()) {
                                            uid[0] = groupSnapshot.getId();
                                            username[0] = groupSnapshot.getString("username");
                                            email[0] = groupSnapshot.getString("email");
                                            Timestamp timestamp = (Timestamp) groupSnapshot.get("timestamp");
                                            User u=new User(username[0], latest[0], groupSnapshot.getString("avatarUrl"), latest[0], uid[0],timestamp);
                                            if (email[0].equals(" ")) {
                                                u.setIsGroup();
                                            }
                                            user.add(u);
                                            adapter = new ChatAdapter(context, R.layout.array_adapter, user);
                                            listView.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                            counter[0]++;
                        }

                        else {
                            userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot userSnapshot = task.getResult();
                                        if (userSnapshot.exists()) {
                                            uid[0] = userSnapshot.getId();
                                            username[0] = userSnapshot.getString("username");
                                            email[0] = userSnapshot.getString("email");
                                            Timestamp timestamp = (Timestamp) userSnapshot.get("timestamp");
                                            user.add(new User(username[0], latest[0], userSnapshot.getString("avatarUrl"), latest[0], uid[0], timestamp));
//                                        if (user.size()>1){
//                                            Collections.sort(user,Comparator.comparing(User::getTimestamp,Comparator.reverseOrder()));
//
//                                        }
                                            adapter = new ChatAdapter(context, R.layout.array_adapter, user);
                                            listView.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                            counter[0]++;
                        }
                        //user.add(new User("username[0]",latestMessage, R.drawable.ic_avt, latestMessage,"uid[0]"));
                        // Kiểm tra nếu tất cả các cuộc gọi đã hoàn thành
                        if (counter[0] == totalUsers) {
//                            sortDescendingTime(user);
                            // Tất cả các cuộc gọi đã hoàn thành, cập nhật adapter ở đây
//                            sortDescendingUser(user);
                            adapter = new ChatAdapter(context, R.layout.array_adapter, user);
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
        Intent intent=new Intent(context, ChatSreenActivity.class);
        intent.putExtra("receiverID",item.getUid());
        if (item.isGroup()) {
            intent.putExtra("group", true);
        }
        else {
            intent.putExtra("group", false);
        }
        startActivity(intent);
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

    //Lắng nghe sự thay đổi của mạng
}