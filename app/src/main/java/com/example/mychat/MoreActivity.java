package com.example.mychat;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Text;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

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
                            String username = documentSnapshot.getString("username");
                            String email = documentSnapshot.getString("email");

                            txtEmail.setText(email);
                            txtUserName.setText(username);
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
                        // ...
//                        Toast.makeText(getApplicationContext(),"signout",Toast.LENGTH_SHORT).show();
                    }
                });



    }

}
