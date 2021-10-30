package vn.edu.tdc.zuke_customer.FCM;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import vn.edu.tdc.zuke_customer.R;
import vn.edu.tdc.zuke_customer.ZukeApplication;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if(notification == null)
            return;
        String title = notification.getTitle();
        String content = notification.getBody();
        sendNotification(title,content);
    }

    private void sendNotification(String title, String content) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ZukeApplication.CHANEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.app);
        Notification notification = notificationBuilder.build();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(manager!= null){
            manager.notify(1,notification);
        }
    }
}
