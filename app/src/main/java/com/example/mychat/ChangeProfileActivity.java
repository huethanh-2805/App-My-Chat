package com.example.mychat;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
    private EditText displayNameEditText;
    private Uri filePath;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String currentDisplayName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        btn_back = findViewById(R.id.back);
        imageView = findViewById(R.id.imageView);
        displayNameEditText = findViewById(R.id.changeUsername);
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