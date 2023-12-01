package com.example.mychat;

import static com.google.firebase.firestore.DocumentChange.Type.ADDED;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class MessageNotification extends Service {
    //public static boolean notificationOn;
    //public static boolean isRunning = true;
    FirebaseFirestore db;
    FirebaseAuth auth;
    CollectionReference ref;
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
        notificationManager = NotificationManagerCompat.from(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Message Notification Service Destroyed", Toast.LENGTH_LONG).show();
        //
        Intent serviceIntent = new Intent(this, MessageNotification.class);
        startService(serviceIntent);
    }
    //
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(),"SERVICE STARTED", Toast.LENGTH_LONG).show();
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
                for (DocumentChange newMessage : querySnapshot.getDocumentChanges()) {
                    if (newMessage.getType() == ADDED) {
                        Toast.makeText(getApplicationContext(),"CHANGE CAUGHT", Toast.LENGTH_SHORT).show();
                        //chỉ xử lý những bộ được thêm vào
                        DocumentSnapshot documentSnapshot = newMessage.getDocument();
                        String receiver = documentSnapshot.getString("receiver");
                        if (receiver.equals(currentUser)) { //lấy những bộ mà người dùng là receiver
                            String idNotification = documentSnapshot.getId();
                            String sender = documentSnapshot.getString("sender");
                            DocumentReference notifyDoc = db.collection("notification").document(idNotification);
                            notifyDoc.delete();
                            DocumentReference userDoc = db.collection("users").document(sender);
                            userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot userSnapshot = task.getResult();
                                        if (userSnapshot.exists()) {
                                            String sendername = userSnapshot.getString("username");
                                            Toast.makeText(getApplicationContext(),"TIN NHẮN MỚI TỪ " + sendername, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
    //
}