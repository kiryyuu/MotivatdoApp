package com.binmadhi.motivatdo.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.binmadhi.motivatdo.Activities.MainActivity;
import com.binmadhi.motivatdo.R;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "TAG";

    // PreferencesManager pref;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        //  pref=new PreferencesManager(this);
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData().get("data").toString());
            sendNotification(remoteMessage.getData().get("message").toString(),
                    remoteMessage.getData().get("data").toString());
            //   pref.setLang(remoteMessage.getData().get("message"));
            Log.e(TAG, "onMessageReceived: Notification" + remoteMessage.getData().get("data").toString());
        }
    }


    private void sendNotification(String message, String title) {
        Log.e(TAG, "sendNotification: " + title);
        int requestID = (int) System.currentTimeMillis();
        Intent intent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message).setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        Notification notification = notificationBuilder.build();

        notificationManager.notify(requestID, notification);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.e(TAG, "onDeletedMessages: Messages are deleted ");
    }

}
