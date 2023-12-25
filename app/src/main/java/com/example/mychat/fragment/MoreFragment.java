package com.example.mychat.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mychat.activity.main.LoginActivity;
import com.example.mychat.activity.AppearanceActivity;
import com.example.mychat.activity.ChangeProfileActivity;
import com.example.mychat.custom.CustomListMore;
import com.example.mychat.R;
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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class MoreFragment extends Fragment  {
    String[] items = new String[]{"Account", "Chats", "Appearance", "Notification", "Privacy", "Data Usage", "Help", "Invite Your Friends", "Sign out"};

    Integer[] icons = {R.drawable.ic_avt, R.drawable.ic_chats, R.drawable.ic_apperance, R.drawable.ic_noti, R.drawable.ic_privacy, R.drawable.ic_data, R.drawable.ic_help, R.drawable.ic_invite, R.drawable.ic_sign_out};
    ListView listView;
    TextView txtUserName;
    TextView txtEmail;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    final FirebaseUser user = auth.getCurrentUser();


     CircleImageView profileAvt;


    private Button btnSignOut;
    private GoogleSignInClient signInClient;


    //    final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String UsernameCurrentUser;
    String EmailCurrentUser;

    String ImageURCurrentUser;



    //LinearLayout btn_setProfile;
    Context context;
    MainFragment mainFragment;
    ChatFragment chatFragment;

    public static MoreFragment newInstance(String strArg) {
        MoreFragment fragment = new MoreFragment();
        Bundle args = new Bundle();
        args.putString("MoreActivity", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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

        LinearLayout layout_more = (LinearLayout) inflater.inflate(R.layout.activity_more, null);

        txtUserName = layout_more.findViewById(R.id.txtName);
        txtEmail = layout_more.findViewById(R.id.txtEmail);
        profileAvt=layout_more.findViewById(R.id.profileAvt);

        //btn_setProfile=layout_more.findViewById(R.id.btn_setProfile);


        listView = layout_more.findViewById(R.id.listView);
        CustomListMore adapter = new CustomListMore(context, R.layout.custom_listview_more, items, icons);
        listView.setAdapter(adapter);

        // lấy thông tin user hiện thị
        getInfoUser();

//        btn_setProfile.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(context, ChangeProfileActivity.class);
//                startActivity(intent);
//            }
//
//        });


        //Click on Item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent= new Intent(context, ChangeProfileActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        mainFragment.changeFragment(new ChatFragment());
                        break;
                    case 2:
                        Intent intent2 = new Intent(context, AppearanceActivity.class);
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
                    case 8:
                        signOut();
                        startActivity(new Intent(context, LoginActivity.class));
                        mainFragment.finish();
                        break;
                }
            }
        });

        return layout_more;
    }


    //        setContentView(R.layout.activity_more);


    private void getInfoUser() {
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Lấy giá trị của trường "username" và "email" từ document
                            UsernameCurrentUser = documentSnapshot.getString("username");
                            EmailCurrentUser = documentSnapshot.getString("email");
                            ImageURCurrentUser=documentSnapshot.getString("avatarUrl");

                            txtEmail.setText(EmailCurrentUser);
                            txtUserName.setText(UsernameCurrentUser);

                            if (ImageURCurrentUser!=null){
                                Picasso.get().load(ImageURCurrentUser).into(profileAvt);
                            }

                        } else {
                            Toast.makeText(context, "Document không tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Fail read field in database", Toast.LENGTH_SHORT).show();
                    }


                });
    }





    private void signOut() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(context, signInOptions);
        signInClient.signOut()
                .addOnCompleteListener((Activity) context, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            auth.signOut();
                        }
//                        Toast.makeText(getApplicationContext(),"signout",Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
