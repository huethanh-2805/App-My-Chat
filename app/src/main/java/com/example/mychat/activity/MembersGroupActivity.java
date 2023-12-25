package com.example.mychat.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mychat.R;
import com.example.mychat.custom.CustomListMembers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MembersGroupActivity extends AppCompatActivity {
    ImageView btn_back;
    Intent intent;
    ListView listView;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_group);

        btn_back = findViewById(R.id.btn_back);
        intent = getIntent();
        String groupID = intent.getStringExtra("groupID");
        listView = (ListView) findViewById(R.id.listView);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("groups").document(groupID);
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

                                // Xử lý khi tất cả công việc đã hoàn thành
                                Tasks.whenAllComplete(memberTasks)
                                        .addOnCompleteListener(task1 -> {
                                            List<String> memberUsernames = new ArrayList<>();
                                            List<String> memberAvatars = new ArrayList<>();
                                            List<String> memberRoles = new ArrayList<>();
                                            String creatorId = document.getString("creator");

                                            for (Task<DocumentSnapshot> memberTask : memberTasks) {
                                                if (memberTask.isSuccessful()) {
                                                    DocumentSnapshot memberSnapshot = memberTask.getResult();
                                                    if (memberSnapshot.exists()) {
                                                        String username = memberSnapshot.getString("username");
                                                        String avatarUrl = memberSnapshot.getString("avatarUrl");
                                                        memberUsernames.add(username);
                                                        memberAvatars.add(avatarUrl);

                                                        // Determine the role of the member based on some condition
                                                        String memberId = memberSnapshot.getId();
                                                        String role = memberId.equals(creatorId) ? "Group creator" : "Member";
                                                        memberRoles.add(role);
                                                    }
                                                }
                                            }

                                            // Chuyển List<String> thành String[]
                                            String[] memberUsernamesArray = memberUsernames.toArray(new String[0]);
                                            String[] memberAvatarsArray = memberAvatars.toArray(new String[0]);
                                            String[] memberRolesArray = memberRoles.toArray(new String[0]);

                                            CustomListMembers adapter = new CustomListMembers(MembersGroupActivity.this, R.layout.activity_custom_list_members, memberUsernamesArray, memberRolesArray, memberAvatarsArray);
                                            listView.setAdapter(adapter);
                                        });
                            } else {
                                Toast.makeText(MembersGroupActivity.this, "No members in the group", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MembersGroupActivity.this, "Member not found in group document", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MembersGroupActivity.this, "Group document not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MembersGroupActivity.this, "Error fetching group document: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}