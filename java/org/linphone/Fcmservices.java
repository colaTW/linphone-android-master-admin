package org.linphone;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class Fcmservices extends FirebaseMessagingService {
    public Fcmservices() {}

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            Log.i("MyFirebaseService", "title " + remoteMessage.getNotification().getTitle());
            Log.i("MyFirebaseService", "body " + remoteMessage.getNotification().getBody());
            sentnot(remoteMessage);
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.i("MyFirebaseService", "token " + s);
    }

    public void sentnot(RemoteMessage remoteMessage) {
        final int notifyID = 18; // 通知的識別號碼
        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        final Notification notification =
                new Notification.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .build(); // 建立通知
        notificationManager.notify(notifyID, notification);
    }
}
