package com.cvlcondorcet.condor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

/**
 * Created by Quentin DE MUYNCK on 23/09/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        Log.i("BROADCAST", "RECEIVE INTENT");
        if (intent.getExtras().containsKey("id") && !intent.getStringExtra("id").equals("")) {
            Database db = new Database(ctx);
            db.open();
            Event e = db.getEvent(intent.getStringExtra("id"));
            db.close();

            NotificationManager manager = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
            Notification.Builder noti;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel chanell = new NotificationChannel("channel1", "Condor", 1);
                manager.createNotificationChannel(chanell);
                noti = new Notification.Builder(ctx, "channel1");
            } else {
                noti = new Notification.Builder(ctx);
            }
            Intent newIntent = new Intent(ctx, EventViewerActivity.class);
            newIntent.putExtra("id", e.getId());
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 1, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            noti.setContentTitle(e.getName())
                    .setContentText(ctx.getResources().getString(R.string.tomorrow) + e.getHourBegin())
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent);
            if (Build.VERSION.SDK_INT >= 21) { noti.setVisibility(VISIBILITY_PUBLIC); }
            manager.notify(1, noti.build());
            Log.i("NOTIF", "NOtifcation displayed");
        }
    }
}
