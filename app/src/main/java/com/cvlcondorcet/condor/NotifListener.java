package com.cvlcondorcet.condor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Quentin DE MUYNCK on 23/09/2017.
 */

public class NotifListener extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message) {
        String messageT = message.getFrom();
        Log.d("TEST", "From: " + messageT);
        //Log.d("TEST", "Message: " + message);

        if (messageT.startsWith("/topics/condor")) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification.Builder noti;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel chanell = new NotificationChannel("channel1", "Coucou", 1);
                manager.createNotificationChannel(chanell);
                noti = new Notification.Builder(this, "channel1");
            } else {
                noti = new Notification.Builder(this);
            }
            Intent newIntent = new Intent(this, MainActivity.class);
            newIntent.putExtra("fragment", "sync");
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent intent = PendingIntent.getActivity(this, 1, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            noti.setContentTitle("New Content")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setOngoing(true)
                    .setContentIntent(intent);
            manager.notify(1, noti.build());
            Log.i("TEST", "NOTIFICATION RECEIVED");
        }
    }
}
