package com.example.mychat.others;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ScreenshotDetector extends ContentObserver {
    private static final Uri SCREENSHOT_CONTENT_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    //private static final String[] SCREENSHOT_PROJECTION = {MediaStore.Images.Media.DATA};
    //private static final String SCREENSHOT_SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC";

    private Context context;
    String sender, receiver;

    FirebaseFirestore db;

    int count;

    public ScreenshotDetector(Context context, String sender, String receiver) {
        super(new Handler());
        this.context = context;
        this.sender = sender;
        this.receiver = receiver;
        count = 0;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        if (count%3 != 0) {
            count++;
            return;
        }
        if (uri != null && uri.toString().matches(SCREENSHOT_CONTENT_URI.toString() + "/[0-9]+")) {
            Toast.makeText(context,"screenshot taken",Toast.LENGTH_SHORT).show();
            //
            screenshotNotification();
            //
            count++;
        }
    }

    public void start() {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.registerContentObserver(SCREENSHOT_CONTENT_URI, true, this);
        count = 0;
        //
    }

    public void stop() {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.unregisterContentObserver(this);
    }
    private void screenshotNotification() {
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(sender).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    //
                    String username = documentSnapshot.getString("username");
                    //
                    CollectionReference usersCollection = db.collection("messages");
                    //
                    HashMap<String, Object> messageData = new HashMap<>();
                    Timestamp timestamp = Timestamp.now();
                    messageData.put("sender", sender);
                    messageData.put("receiver", receiver);
                    messageData.put("message", "["  + username + " took a screenshot]");
                    messageData.put("timestamp", timestamp);
                    messageData.put("type", "screenshot");
                    usersCollection.add(messageData);
                }
            }
        });
    }
}