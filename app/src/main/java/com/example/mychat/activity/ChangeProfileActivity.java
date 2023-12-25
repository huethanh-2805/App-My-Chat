package com.example.mychat.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.example.mychat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChangeProfileActivity extends Activity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private CircleImageView imageView;
    private ImageView btn_back;
    private TextView displayNameEditText;
    private TextView displayEmailEditText;
    private TextView displayPasswordEditText;
    private Uri filePath;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String currentDisplayName;
    private String currentDisplayEmail;
    private String currentDisplayPassword;
    private ImageView btn_editName;
    private ImageView btn_editPassword;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        btn_back = findViewById(R.id.back);
        imageView = findViewById(R.id.imageView);
        displayNameEditText = findViewById(R.id.changeUsername);
        displayEmailEditText = findViewById(R.id.changeEmail);
        displayPasswordEditText = findViewById(R.id.changePassword);
        btn_editName = findViewById(R.id.btn_editName);
        btn_editPassword = findViewById(R.id.btn_editPassword);
        Button chooseButton = findViewById(R.id.chooseButton);
        Button uploadButton = findViewById(R.id.uploadButton);

        auth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        firestore = FirebaseFirestore.getInstance();

        DocumentReference db = firestore.collection("users").document(auth.getCurrentUser().getUid());

        db.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Lấy giá trị của trường cụ thể từ tài liệu
                    currentDisplayName = documentSnapshot.getString("username");
                    displayNameEditText.setText(currentDisplayName);
                    currentDisplayEmail = documentSnapshot.getString("email");
                    displayEmailEditText.setText(currentDisplayEmail);
                    Picasso.get().load(documentSnapshot.getString("avatarUrl")).into(imageView);


                } else {
                    // Tài liệu không tồn tại
                    Toast.makeText(ChangeProfileActivity.this, "Not existed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Xử lý khi có lỗi xảy ra
                Toast.makeText(ChangeProfileActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

        btn_editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditName(currentDisplayName);
            }
        });

        btn_editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });


        //Toast.makeText(ChangeProfileActivity.this, currentDisplayName, Toast.LENGTH_SHORT).show();

        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
                changeDisplayName();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showEditName(String currentName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangeProfileActivity.this);

        // Tạo LinearLayout để chứa TextInputLayout và EditText
        LinearLayout layout = new LinearLayout(ChangeProfileActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Tạo TextView để làm title
        TextView titleTextView = new TextView(ChangeProfileActivity.this);
        titleTextView.setText("Change Username");
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        titleTextView.setTextColor(ContextCompat.getColor(ChangeProfileActivity.this, R.color.blue));
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setPadding(0, 20, 0, 20); // Điều chỉnh padding nếu cần
        layout.addView(titleTextView);


        TextInputLayout textInputLayout = new TextInputLayout(ChangeProfileActivity.this);
        final EditText input = new EditText(ChangeProfileActivity.this);
        input.setText(currentName);

        textInputLayout.setHint("Enter new username");
        textInputLayout.addView(input);

        layout.addView(textInputLayout);
        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newUsername = input.getText().toString().trim();

                if (!TextUtils.isEmpty(newUsername)) {
                    displayNameEditText.setText(newUsername);
                } else {
                    Toast.makeText(ChangeProfileActivity.this, "Please enter a new username", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangeProfileActivity.this);

        // Tạo LinearLayout để chứa các EditText
        LinearLayout layout = new LinearLayout(ChangeProfileActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Tạo TextView để làm title
        TextView titleTextView = new TextView(ChangeProfileActivity.this);
        titleTextView.setText("Change Password");
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        titleTextView.setTextColor(ContextCompat.getColor(ChangeProfileActivity.this, R.color.blue));
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setPadding(0, 20, 0, 20); // Điều chỉnh padding nếu cần
        layout.addView(titleTextView);

        TextInputLayout currentPasswordLayout = new TextInputLayout(ChangeProfileActivity.this);
        final EditText inputCurrentPassword = new EditText(ChangeProfileActivity.this);
        inputCurrentPassword.setHint("Enter current password");
        inputCurrentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        currentPasswordLayout.addView(inputCurrentPassword);
        layout.addView(currentPasswordLayout);

        TextInputLayout newPasswordLayout = new TextInputLayout(ChangeProfileActivity.this);
        final EditText inputNewPassword = new EditText(ChangeProfileActivity.this);
        inputNewPassword.setHint("Enter new password");
        inputNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPasswordLayout.addView(inputNewPassword);
        layout.addView(newPasswordLayout);

        TextInputLayout reNewPasswordLayout = new TextInputLayout(ChangeProfileActivity.this);
        final EditText inputReNewPassword = new EditText(ChangeProfileActivity.this);
        inputReNewPassword.setHint("Re-enter new password");
        inputReNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        reNewPasswordLayout.addView(inputReNewPassword);
        layout.addView(reNewPasswordLayout);

        builder.setView(layout);

        builder.setPositiveButton("Change Password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String oldPassword = inputCurrentPassword.getText().toString().trim();
                String newPassword = inputNewPassword.getText().toString().trim();
                String confirmPassword = inputReNewPassword.getText().toString().trim();

                // Kiểm tra xác nhận mật khẩu mới
                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(ChangeProfileActivity.this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Thực hiện đổi mật khẩu
                changePassword(oldPassword, newPassword);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Hàm đổi mật khẩu
    private void changePassword(String oldPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Đổi mật khẩu
                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ChangeProfileActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ChangeProfileActivity.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ChangeProfileActivity.this, "Authentication failed. Check your old password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void changeDisplayName() {
        String newDisplayName = displayNameEditText.getText().toString().trim();

        if (!newDisplayName.isEmpty()) {
            // Lấy UID của người dùng hiện tại
            String userId = auth.getCurrentUser().getUid();

            // Cập nhật trường "displayName" trong Firestore
            DocumentReference userRef = firestore.collection("users").document(userId);
            userRef
                    .update("username", newDisplayName)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Cập nhật tên người dùng trong FirebaseAuth
                                auth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder()
                                        .setDisplayName(newDisplayName)
                                        .build());

                                Toast.makeText(ChangeProfileActivity.this, "Display name updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ChangeProfileActivity.this, "Failed to update display name", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Please enter a new display name", Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Lưu hình ảnh vào Firebase Storage
            StorageReference ref = storageReference.child("images/" + System.currentTimeMillis() + ".jpg");

            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            saveImageUrlToFirestore(imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(ChangeProfileActivity.this, "Upload failed", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
        }

    private void saveImageUrlToFirestore(String imageUrl) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Lưu đường dẫn hình ảnh vào Firestore
        DocumentReference userRef = firestore.collection("users").document(userId);
        userRef
                .update("avatarUrl", imageUrl)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangeProfileActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChangeProfileActivity.this, "Failed to save image URL", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}