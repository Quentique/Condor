package com.cvlcondorcet.condor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.File;

/**
 * Receives the topic message from the web & shows notification to advise user of new content
 * @author Quentin DE MUYNCK
 */

public class NotifListener extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message) {
        String messageT = message.getFrom();
        String what = message.getData().get("what");
       // Log.d("TEST", "From: " + messageT);
//        Toast.makeText(this, "Message received", Toast.LENGTH_LONG).show();
        if (messageT.startsWith("/topics/condor")) {
            if (what.equals("sync")) {
                if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("sync_when_notified", false)){
                    Intent servicee = new Intent(this, Sync.class);
                    startService(servicee);
                } else {
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Notification.Builder noti;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel chanell = new NotificationChannel("channel1", "Condor", NotificationManager.IMPORTANCE_DEFAULT);
                        manager.createNotificationChannel(chanell);
                        noti = new Notification.Builder(this, "channel1");
                    } else {
                        noti = new Notification.Builder(this);
                    }
                    Intent newIntent = new Intent(this, MainActivity.class);
                    newIntent.putExtra("fragment", "sync");
                    newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent intent = PendingIntent.getActivity(this, 1, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    noti.setContentTitle(getString(R.string.new_content))
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentText(getString(R.string.click_sync))
                            .setContentIntent(intent);
                    noti.setAutoCancel(true);
                    manager.notify(1, noti.build());
                }                // Log.i("TEST", "NOTIFICATION RECEIVED");
            } else if (what.startsWith("delete")) {
                String table;
                switch (what) {
                    case "deletePosts":
                        table = DBOpenHelper.Posts.TABLE_NAME;
                        break;
                    case "deleteEvents":
                        table = DBOpenHelper.Events.TABLE_NAME;
                        break;
                    case "deleteMaps":
                        table = DBOpenHelper.Maps.TABLE_NAME;
                        break;
                    default:
                        table = "0";
                        break;
                }
                Database db = new Database(getApplicationContext());
                db.open();
                db.deleteTable(table);
                db.close();
            } else if (what.equals("purge")) {
                Database db = new Database(getApplicationContext());
                db.open();
                db.deleteAllTables();
                db.close();
                deleteDir(new File(getApplicationInfo().dataDir));
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if(!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }
}
