package com.cvlcondorcet.condor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.File;
import java.util.Map;

/**
 * Receives the topic message from the web & shows notification to advise user of new content
 * @author Quentin DE MUYNCK
 */

public class NotifListener extends FirebaseMessagingService {
    /**
     * Handling message when app's in the foreground
     * @param message the received message
     */
    @Override
    public void onMessageReceived(RemoteMessage message) {
        String messageT = message.getFrom();
        String what = message.getData().get("what");
        if (messageT != null && messageT.startsWith("/topics/condor54")) {
            if (what != null) {
                if (what.equals("sync")) {
                    if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("sync_when_notified", false)) {
                        Intent servicee = new Intent(this, Sync.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(servicee);
                        } else {
                            startService(servicee);
                        }
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
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addNextIntentWithParentStack(newIntent);
                        PendingIntent intent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        noti.setContentTitle(getString(R.string.new_content))
                                .setSmallIcon(R.drawable.ic_launcher_material)
                                .setContentText(getString(R.string.click_sync))
                                .setContentIntent(intent);
                        noti.setAutoCancel(true);
                        manager.notify(1, noti.build());
                    }
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
            } else {
                if (message.getNotification() != null) {
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Notification.Builder noti;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel chanell = new NotificationChannel("channel1", "Condor", NotificationManager.IMPORTANCE_DEFAULT);
                        manager.createNotificationChannel(chanell);
                        noti = new Notification.Builder(this, "channel1");
                    } else {
                        noti = new Notification.Builder(this);
                    }

                    noti.setContentTitle(message.getNotification().getTitle()).setContentText(message.getNotification().getBody()).setSmallIcon(R.drawable.ic_launcher_material);
                    Intent newIntent;
                    if (message.getData().containsKey("posts")) {
                        newIntent = new Intent(this, PostViewerActivity.class);
                        newIntent.putExtra("id", message.getData().get("posts"));
                    } else if (message.getData().containsKey("events")) {
                        newIntent = new Intent(this, EventViewerActivity.class);
                        newIntent.putExtra("id", message.getData().get("events"));
                    } else {
                        newIntent = new Intent(this, MainActivity.class);
                        for (Map.Entry<String, String> entry : message.getData().entrySet()) {
                            newIntent.putExtra(entry.getKey(), entry.getValue());
                        }
                    }
                    newIntent.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                    stackBuilder.addNextIntentWithParentStack(newIntent);
                    PendingIntent intent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    noti.setContentIntent(intent);
                    noti.setAutoCancel(true);
                    manager.notify(5, noti.build());
                }
            }
        }
    }

    private static boolean deleteDir(File dir) {
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
