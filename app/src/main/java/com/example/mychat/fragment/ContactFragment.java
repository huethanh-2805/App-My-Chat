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
import androidx.fragment.app.Fragment;

import com.example.mychat.activity.AddContactActivity;
import com.example.mychat.activity.main.ChatSreenActivity;
import com.example.mychat.adapter.ChatAdapter;
import com.example.mychat.R;
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
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import nl.joery.animatedbottombar.AnimatedBottomBar;


public class ContactFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    ImageView imgNewContact;
    SearchView searchView;
    ListView listView;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    //
    FirebaseFirestore db;
    CollectionReference cref;
    Query query;
    //
    List<User> user=new ArrayList<>(); //tên người liên hệ
    String emailCurrentUser;
    SharedPreferences sharedPreferences;
    private ChatAdapter adapter;
    AnimatedBottomBar bottomBar;

    Context context;
    MainFragment mainFragment;
    TextView txtStatus;
    public static ContactFragment newInstance(String strArg) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString("ContactActivity", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        LinearLayout layout_contact = (LinearLayout) inflater.inflate(R.layout.activity_contact, null);

        searchView=layout_contact.findViewById(R.id.searchView);

        listView = (ListView)layout_contact.findViewById(R.id.listView);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(this);

        imgNewContact = (ImageView) layout_contact.findViewById(R.id.imgAdd);
        imgNewContact.setOnClickListener(this);
        txtStatus = layout_contact.findViewById(R.id.txtStatus);
        getListUserFromDatabase();
        searchUserWithUserName();
        return layout_contact;
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.imgAdd) {
            startActivity(new Intent(context, AddContactActivity.class));
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

    private void getListUserFromDatabase() {

        user = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        //get CONTACT collection
        cref = db.collection("contact");
        DocumentReference doc = cref.document(auth.getCurrentUser().getUid());
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) { //get EVERY documents within the collection
                        //get the userContact array
                        List<DocumentReference> docUser = (List<DocumentReference>) documentSnapshot.get("userContact");
                        List<DocumentReference> docGroup = (List<DocumentReference>) documentSnapshot.get("groups");
                        if (docGroup!=null) {
                            docUser.addAll(docGroup);
                        }
                        //get the total
                        final int totalUsers = docUser.size();
                        final int[] counter = {0};
                        for (DocumentReference d : docUser) { //get every user contact from array
                            d.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot userSnapshot = task.getResult();
                                        if (userSnapshot.exists()) {
                                            String username = userSnapshot.getString("username");
                                            String email = userSnapshot.getString("email");
                                            String uid=userSnapshot.getId();
                                            User u=new User(username, "abc", userSnapshot.getString("avatarUrl"), email,uid, Timestamp.now());
                                            if (email.equals(" ")) {
                                                u.setIsGroup();
                                            }
                                            user.add(u);
                                        }
                                    }

                                    counter[0]++;
                                    // Kiểm tra nếu tất cả các cuộc gọi đã hoàn thành
                                    if (counter[0] == totalUsers) {
                                            // Tất cả các cuộc gọi đã hoàn thành, cập nhật adapter ở đây
                                            adapter = new ChatAdapter(context, R.layout.array_adapter, user);
                                            listView.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "fail for", Toast.LENGTH_SHORT).show();
                                    counter[0]++;
                                }
                            });
                        }

                    }

                } else {
                    Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
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
    @Override
    public void onResume() {
        super.onResume();
        user.clear();
        if (adapter!=null) {
            adapter.notifyDataSetChanged();
            getListUserFromDatabase();

        }

        // Đăng ký BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
        context.registerReceiver(networkChangeReceiver, intentFilter);
    }

    //Lắng nghe sự thay đổi của mạng
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

}


