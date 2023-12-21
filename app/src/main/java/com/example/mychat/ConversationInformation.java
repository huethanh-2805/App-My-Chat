package com.example.mychat;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationInformation extends BaseActivity {
    String[] items = new String[]{"Change theme", "Media", "Block", "Delete chat"};
    String[] themes = new String[]{"Light Blue", "Nice blue", "Nice green", "Nice Fire", "Nice orange", "Nice pink", "Loso", "Love", "Black heart", "Sweet Chocolate", "Cocacola", "Mochi mochi"};
    Integer[] icons = {R.drawable.ic_theme, R.drawable.ic_picture, R.drawable.ic_block, R.drawable.ic_delete};
    Integer[] colors = {R.drawable.ic_light1, R.drawable.ic_light2, R.drawable.ic_light3, R.drawable.ic_dark1, R.drawable.ic_dark2, R.drawable.ic_dark3, R.drawable.theme3d1, R.drawable.theme_love3d, R.drawable.theme_blackheart, R.drawable.theme_socola, R.drawable.theme_cocacola, R.drawable.theme_mochi};
    ListView listView;
    ListView listViewThemes;
    TextView txtUserName;
    ImageView btn_back;
    ImageView profile_image;
    Intent intent;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean isUserBlocked; //Biến để kiểm tra user có bị lock hay không
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
        boolean isGroup = intent.getBooleanExtra("isGroup", false);

        //Set avatar của receiver
        profile_image = findViewById(R.id.profile_image);
        String avatar = intent.getStringExtra("avatarUrl");
        if (avatar!=null) {
            Picasso.get().load(avatar).into(profile_image);
        }

        txtUserName.setText(name);

        listView = findViewById(R.id.listView);
        checkUserBlocked(myID, userID);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showThemeSelectionDialog();
                        break;
                    case 1:
                        showMediaGrid(name, myID, userID, isGroup);
                        break;
                    case 2:
                        showBlockConfirmationDialog(myID, userID, name);
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
                finish();
            }
        });

    }

    private void checkUserBlocked(String myID, String userID){
        CollectionReference contactCollection = db.collection("contact");
        DocumentReference contactDocument = contactCollection.document(myID);
        contactDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        List<DocumentReference> blockList = (List<DocumentReference>) documentSnapshot.get("block");
                        if (blockList != null) {
                            DocumentReference userToCheck = db.collection("users").document(userID);
                            isUserBlocked = blockList.contains(userToCheck);
                        } else {
                            isUserBlocked = false;
                        }

                        if(isUserBlocked){
                            //Toast.makeText(getApplicationContext(), "isBlock", Toast.LENGTH_SHORT).show();
                            items[2] = "Unblock";
                        }
                        else {
                            //Toast.makeText(getApplicationContext(), "isNotBlock", Toast.LENGTH_SHORT).show();
                            items[2] = "Block";
                        }
                        CustomListMore adapter = new CustomListMore(ConversationInformation.this, R.layout.custom_listview_more, items, icons);
                        listView.setAdapter(adapter);

                    }
                } else {
                    Log.d("TAG", "Lỗi khi lấy dữ liệu: ", task.getException());
                }
            }
        });
    }

    //Hiển thị thông báo hỏi lại có chắc chắn muốn block hay không
    private void showBlockConfirmationDialog(String myId, String userId, String name){
        AlertDialog.Builder builder = new AlertDialog.Builder(ConversationInformation.this);

        String title;
        String message;
        String positiveButtonLabel;

        if (isUserBlocked) {
            title = "Unblock " + name + "?";
            message = "You will start receiving messages from " + name + "'s MyChat account.";
            positiveButtonLabel = "UNBLOCK";
        } else {
            title = "Block " + name + "?";
            message = "Your MyChat account won't receive messages from " + name + "'s MyChat account.";
            positiveButtonLabel = "BLOCK";
        }

        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(positiveButtonLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (isUserBlocked) {
                            unblockUser(myId, userId);
                        } else {
                            blockUser(myId, userId);
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void blockUser(String myId, String userId){
        CollectionReference contactCollection = db.collection("contact");
        // Truy vấn tài liệu contact có id bằng myId
        DocumentReference contactDocument = contactCollection.document(myId);

        // Lấy danh sách người liên hệ từ tài liệu contact
        contactDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        List<DocumentReference> blockAdds = (List<DocumentReference>) documentSnapshot.get("block");
                        // Kiểm tra xem danh sách block có tồn tại không
                        if (blockAdds == null) {
                            blockAdds = new ArrayList<>(); // Nếu không tồn tại, tạo mới danh sách
                        }

                        // Tạo một DocumentReference mới dựa trên userId
                        DocumentReference userBlock = db.collection("users").document(userId);

                        if (!blockAdds.contains(userBlock)) {
                            blockAdds.add(userBlock);
                            Map<String, Object> data = new HashMap<>();
                            data.put("block", blockAdds);

                            contactDocument.set(data, SetOptions.merge())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            showNiceDialogBox(true);
                                            checkUserBlocked(myId, userId);
                                        }
                                    });
                        }
                        else {
                            showNiceDialogBox(false);
                        }
                    }
                } else {
                    Log.d("TAG", "Lỗi khi lấy dữ liệu: ", task.getException());
                }
            }
        });
    }


    public void showNiceDialogBox(boolean check){
        String message;
        if(check) {
            message="Block successfully!";
        }
        else {
            message="User is already in the block list!";
        }
        AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
        myBuilder.setIcon(R.drawable.ic_noti)
                .setTitle("Block user")
                .setMessage(message)
                .setPositiveButton("Close", null)
                .show();
    }

    private void unblockUser(String myId, String userId){
        CollectionReference contactCollection = db.collection("contact");
        DocumentReference contactDocument = contactCollection.document(myId); // Truy vấn tài liệu contact có id bằng myId

        contactDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        List<DocumentReference> blockLists = (List<DocumentReference>) documentSnapshot.get("block");
                        DocumentReference userBlock = db.collection("users").document(userId);

                        if (blockLists != null && blockLists.contains(userBlock)) {
                            blockLists.remove(userBlock);
                            Map<String, Object> data = new HashMap<>();
                            data.put("block", blockLists);

                            contactDocument.set(data, SetOptions.merge())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            // Handle completion if needed
                                            showNiceDialogBoxUnblock(true);
                                            checkUserBlocked(myId, userId);
                                        }
                                    });
                        }
                        else {
                            showNiceDialogBoxUnblock(false);
                        }
                    }
                } else {
                    Log.d("TAG", "Lỗi khi lấy dữ liệu: ", task.getException());
                }
            }
        });
    }

    public void showNiceDialogBoxUnblock(boolean check){
        String message;
        if(check) {
            message="Unlock successfully!";
        }
        else {
            message="User is not already in the block list!";
        }
        AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
        myBuilder.setIcon(R.drawable.ic_noti)
                .setTitle("Unblock user")
                .setMessage(message)
                .setPositiveButton("Close", null)
                .show();
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
                    Map<String, Object> data = new HashMap<>();
                    data.put("sender", "");
                    data.put("sender_delete", myId);

                    db.collection("messages").document(messageId)
                            .update(data)
                            .addOnSuccessListener(aVoid -> {

                            })
                            .addOnFailureListener(e -> {

                            });
                }

                for (QueryDocumentSnapshot document : task2.getResult()) {
                    String messageId = document.getId();
                    Map<String, Object> data = new HashMap<>();
                    data.put("receiver", "");
                    data.put("receiver_delete", myId);

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

    private void showThemeSelectionDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose a Theme");

            // Set the adapter directly on the AlertDialog.Builder
            builder.setAdapter(new CustomListTheme(this, R.layout.custom_list_themes, themes, colors), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    applyTheme(which);
                    dialog.dismiss();  // Dismiss the dialog after a theme is
                    finish();
                }
            });

            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error showing dialog", Toast.LENGTH_SHORT).show();
        }
    }

    private void applyTheme(int themeIndex) {
        ThemeHelper.saveSelectedTheme(this, themeIndex);
        recreate(); // Recreate the activity to apply the new theme
    }

    private void showMediaGrid(String username, String myId, String userId, boolean isGroup){
        Intent intent = new Intent(this, MediaGridActivity.class);
        intent.putExtra("user_name",username);
        intent.putExtra("my_id", myId);
        intent.putExtra("user_id", userId);
        intent.putExtra("isGroup", isGroup);
        ConversationInformation.this.startActivity(intent);
    }

}