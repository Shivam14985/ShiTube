package com.example.shivambhardwaj.shitube.Services;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.shivambhardwaj.shitube.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseCloudMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        getFirebaseMessage(message.getNotification().getTitle(), message.getNotification().getBody());
    }

    private void getFirebaseMessage(String title, String body) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notify")
                .setSmallIcon(R.drawable.youtube)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(101, builder.build());
    }
}
