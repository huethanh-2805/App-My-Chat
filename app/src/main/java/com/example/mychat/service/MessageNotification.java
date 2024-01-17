
package com.example.mychat.service;

import static android.content.ContentValues.TAG;
import static com.google.firebase.firestore.DocumentChange.Type.ADDED;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mychat.R;
import com.example.mychat.activity.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MessageNotification extends Service {
    //public static boolean notificationOn;
    //public static boolean isRunning = true;
    FirebaseFirestore db;
    FirebaseAuth auth;
    CollectionReference ref;

    String currentUser;
    //
    boolean isGroup;
    private NotificationManagerCompat notificationManager;
    public MessageNotification() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    //
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser().getUid().toString();
        db = FirebaseFirestore.getInstance();
        ref = db.collection("notification");
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                db = FirebaseFirestore.getInstance();
                ArrayList<String> readList = new ArrayList<String>();
                for (DocumentSnapshot newMessage : querySnapshot.getDocuments()) {
                    if (newMessage.exists()) {
                        //chỉ xử lý những bộ được thêm vào
                        String receiver = newMessage.getString("receiver");
                        if (receiver != null) {
                            if (receiver.equals(currentUser)) { //lấy những bộ mà người dùng là receiver
                                //lấy id bộ thông báo để xóa sau khi đọc thông báo xong
                                String idNotification = newMessage.getId();
                                readList.add(idNotification);
                                //lấy thông tin người gửi, check xem tin nhắn có phải từ nhóm hay không
                                String sender = newMessage.getString("sender");
                                String groupCheck = newMessage.getString("isGroup");
                                //
                                isGroup = false;
                                assert groupCheck != null;
                                if (groupCheck.equals("true")) isGroup = true;
                                DocumentReference userDoc;
                                if (isGroup) userDoc = db.collection("groups").document(sender);
                                else userDoc = db.collection("users").document(sender);
                                userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot userSnapshot = task.getResult();
                                            if (userSnapshot.exists()) {
                                                String sendername = userSnapshot.getString("username");
                                                Notify(sendername, sender);
                                            }
                                        }
                                    }
                                });

                            }
                        }
                    }
                }
                //xóa các bộ
                for (String docID : readList) {
                    DocumentReference notifyDoc = db.collection("notification").document(docID);
                    notifyDoc.delete();
                }
                readList.clear();
            }
        });
        return START_STICKY;
    }
    //
    private void checkToSend(String from, String sender) {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(currentUser);
        //check if offline, send notification
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    return;
                }
                if(value!=null&&value.exists()){
                    String data=value.getString("status");
                    if(Objects.equals(data, "0")){
                        Notify(from,sender);
                    }
                }
                else{
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }
    private void Notify(String noti, String sender) {
        String channelId = "channel_id";
        String channelName = "MyChat_Channel";
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        //
        notificationManager.createNotificationChannel(notificationChannel);
        // intent dưới dạng chờ để launch khi được tap vào
        Intent intent = new Intent(this, MainActivity.class);
        //
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //
        int notificationID = (int) System.currentTimeMillis();
        //
        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, notificationID, intent, PendingIntent.FLAG_MUTABLE);
        //
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_noti)
                .setContentTitle("MyChat")
                .setContentText("Tin nhắn mới từ " + noti)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        // Hiện thông báo
        notificationManager.notify(notificationID, builder.build());
    }
    //
}
