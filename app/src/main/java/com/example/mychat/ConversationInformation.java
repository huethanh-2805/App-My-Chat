package com.example.mychat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ConversationInformation extends AppCompatActivity {
    String[] items = new String[]{"Change theme", "Media", "Block", "Delete chat"};

    Integer[] icons = {R.drawable.ic_theme, R.drawable.ic_picture, R.drawable.ic_block, R.drawable.ic_delete};
    ListView listView;
    TextView txtUserName;
    ImageView btn_back;
    Intent intent;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_information);

        txtUserName = findViewById(R.id.username);
        btn_back = findViewById(R.id.back);
        intent = getIntent();
        String name = intent.getStringExtra("user_name");
        String myID = intent.getStringExtra("my_id");    //Nhận id của mình
        String userID = intent.getStringExtra("user_id");//Nhận id của người chat với mình
        txtUserName.setText(name);

        listView = findViewById(R.id.listView);
        CustomListMore adapter = new CustomListMore(this, R.layout.custom_listview_more, items, icons);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        showDeleteConfirmationDialog(myID, userID);
                        break;
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConversationInformation.this, ChatSreen.class);
                ConversationInformation.this.startActivity(intent);
            }
        });

    }

    //Hiển thị thông báo hỏi lại có chắc chắn muốn xóa hội thoại hay không
    private void showDeleteConfirmationDialog(String myId, String userId){
        AlertDialog.Builder builder = new AlertDialog.Builder(ConversationInformation.this);
        builder.setMessage("You cannot undo once you delete this copy of the conversation.")
                .setTitle("Are you sure you want to delete this entire chat?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteChat(myId, userId);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Người dùng không muốn xóa, đóng dialog
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteChat(String myId, String userId){
        CollectionReference messagesCollection = db.collection("messages");

        // Tìm tất cả tin nhắn có sender = myId và receiver = userId
        Query message1 = messagesCollection
                .whereEqualTo("sender", myId)
                .whereEqualTo("receiver", userId);

        // Tìm tất cả tin nhắn có sender = userId và receiver = myId
        Query message2 = messagesCollection
                .whereEqualTo("sender", userId)
                .whereEqualTo("receiver", myId);

        // Kết hợp kết quả của hai truy vấn
        Task<QuerySnapshot> task1 = message1.get();
        Task<QuerySnapshot> task2 = message2.get();

        Tasks.whenAllComplete(task1, task2).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task1.getResult()) {
                    String messageId = document.getId();
                    String senderID = document.getString("sender");
                    String receiverID = document.getString("receiver");

                    Map<String, Object> data = new HashMap<>();

                    if (senderID.equals(myId)) {
                        data.put("sender", "");
                    } else if (receiverID.equals(myId)) {
                        data.put("receiver", "");
                    }

                    db.collection("messages").document(messageId)
                            .update(data)
                            .addOnSuccessListener(aVoid -> {

                            })
                            .addOnFailureListener(e -> {

                            });
                }

                for (QueryDocumentSnapshot document : task2.getResult()) {
                    String messageId = document.getId();
                    String senderID = document.getString("sender");
                    String receiverID = document.getString("receiver");

                    Map<String, Object> data = new HashMap<>();

                    if (senderID.equals(myId)) {
                        data.put("sender", "");
                    } else if (receiverID.equals(myId)) {
                        data.put("receiver", "");
                    }

                    db.collection("messages").document(messageId)
                            .update(data)
                            .addOnSuccessListener(aVoid -> {

                            })
                            .addOnFailureListener(e -> {

                            });
                }
            } else {
                Exception e = task.getException();
            }
        });
    }

}