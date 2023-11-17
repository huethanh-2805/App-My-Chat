package com.example.mychat;

import android.app.Activity;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MoreActivity extends Activity implements View.OnClickListener {
    String[] items = new String[]{"Account", "Chats", "Apperance", "Notification", "Privacy", "Data Usage", "Help", "Invite Your Friends"};

    Integer[] icons = {R.drawable.ic_avt, R.drawable.ic_chats, R.drawable.ic_apperance, R.drawable.ic_noti, R.drawable.ic_privacy, R.drawable.ic_data, R.drawable.ic_help, R.drawable.ic_invite};
    ListView listView;
    TextView txtUserName;
    TextView txtEmail;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseAuth auth = FirebaseAuth.getInstance();
    final FirebaseUser user=auth.getCurrentUser();

    private Button btnSignOut;
    private GoogleSignInClient signInClient;


//    final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

     String UsernameCurrentUser;
     String EmailCurrentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        Toast.makeText(getApplicationContext(),auth.getCurrentUser().getDisplayName().toString(),Toast.LENGTH_SHORT).show();

        btnSignOut=findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(this);


        txtUserName=findViewById(R.id.txtName);
        txtEmail=findViewById(R.id.txtEmail);

        listView = findViewById(R.id.listView);
        CustomListMore adapter = new CustomListMore(this, R.layout.custom_listview_more, items, icons);
        listView.setAdapter(adapter);

        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Lấy giá trị của trường "username" và "email" từ document
                            UsernameCurrentUser = documentSnapshot.getString("username");
                            EmailCurrentUser = documentSnapshot.getString("email");

                            txtEmail.setText(EmailCurrentUser);
                            txtUserName.setText(UsernameCurrentUser);
                        } else {
                            Toast.makeText(getApplicationContext(),"Document không tồn tại",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Fail read field in database",Toast.LENGTH_SHORT).show();
                    }
                });

        //Click on Item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:

                        break;
                    case 1:

                        break;
                    case 2:
                        Intent intent2 = new Intent(MoreActivity.this, AppearanceActivity.class);
                        startActivity(intent2);
                        break;
                    case 3:

                        break;
                    case 4:

                        break;
                    case 5:

                        break;
                    case 6:

                        break;
                    case 7:

                        break;
                }
            }
        });


    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btnSignOut){
            signOut();
            startActivity(new Intent(MoreActivity.this,LoginActivity.class));
            finish();
        }
    }

    private void signOut() {
        GoogleSignInOptions signInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient= GoogleSignIn.getClient(getApplicationContext(),signInOptions);
        signInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        auth.signOut();
//                        Toast.makeText(getApplicationContext(),"signout",Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
