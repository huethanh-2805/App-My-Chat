package com.example.mychat.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mychat.activity.main.ChatSreenActivity;
import com.example.mychat.R;
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

public class MessageNotification extends Service {
    //public static boolean notificationOn;
    //public static boolean isRunning = true;
    FirebaseFirestore db;
    FirebaseAuth auth;
    CollectionReference ref;
    //
    String otherUser;
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
        //Toast.makeText(this, "Message Notification Service Destroyed", Toast.LENGTH_LONG).show();
    }
    //
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(getApplicationContext(),"SERVICE STARTED", Toast.LENGTH_LONG).show();
        otherUser = intent.getStringExtra("otherUser");
        auth = FirebaseAuth.getInstance();
        String currentUser = auth.getCurrentUser().getUid().toString();
        db = FirebaseFirestore.getInstance();
        ref = db.collection("notification");
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                for (DocumentSnapshot newMessage : querySnapshot.getDocuments()) {
                    if (newMessage.exists()) {
                        //Toast.makeText(getApplicationContext(),"CHANGE CAUGHT", Toast.LENGTH_SHORT).show();
                        //chỉ xử lý những bộ được thêm vào
                        DocumentSnapshot docSnap = newMessage;
                        String receiver = docSnap.getString("receiver");
                        if (receiver != null) {
                            if (receiver.equals(currentUser)) { //lấy những bộ mà người dùng là receiver
                                String idNotification = docSnap.getId();
                                String sender = docSnap.getString("sender");
                                DocumentReference notifyDoc = db.collection("notification").document(idNotification);
                                DocumentReference userDoc = db.collection("users").document(sender);
                                userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot userSnapshot = task.getResult();
                                            if (userSnapshot.exists()) {
                                                String sendername = userSnapshot.getString("username");
                                                if (sender.equals(otherUser))
                                                    Notify(sendername, sender);
                                                //Toast.makeText(getApplicationContext(),"TIN NHẮN MỚI TỪ " + sendername, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                                notifyDoc.delete();
                            }
                        }
                    }
                }
            }
        });
        return START_STICKY;
    }
    //
    private void Notify(String noti, String sender) {
//        Toast.makeText(getApplicationContext(),"AOOOO " + noti, Toast.LENGTH_SHORT).show();

        String channelId = "channel_id";
        String channelName = "MyChat_Channel";
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        //
        notificationManager.createNotificationChannel(notificationChannel);
        // intent dưới dạng chờ để launch khi được tap vào
        Intent intent = new Intent(this, ChatSreenActivity.class);
        intent.putExtra("receiverID", sender);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        //build thông báo
//        Toast.makeText(getApplicationContext(),"TIN NHẮN MỚI TỪ " + noti, Toast.LENGTH_SHORT).show();
        //
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_noti)
                .setContentTitle("MyChat")
                .setContentText("Tin nhắn mới từ " + noti)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        // Hiện thông báo
        notificationManager.notify(0, builder.build());
        //
    }
}