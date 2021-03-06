package com.cvlcondorcet.condor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;

import java.util.Calendar;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Receives alarm broadcasts of the application and displays the appropriate notification
 * @author Quentin DE MUYNCK
 * @see Database#getEvent(String)
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {

        Event.format = ctx.getResources().getString(R.string.date_format);
        Event.format2 = ctx.getResources().getString(R.string.hour_format);

        if (intent.getExtras().containsKey("id") && !intent.getStringExtra("id").equals("")) {
            Database db = new Database(ctx);
            db.open();
            Event e = db.getEvent(intent.getStringExtra("id"));
            db.close();
            if (e != null) {
                String content;
                Calendar cal = Calendar.getInstance();
                cal.setTime(e.getDateBeginDate());
                cal.add(Calendar.DAY_OF_MONTH, -1);
                boolean toContinue = false;
                content = "";
                if (cal.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                    content = ctx.getResources().getString(R.string.tomorrow) + e.getHourBegin();
                    toContinue = true;
                }
                cal.add(Calendar.DAY_OF_MONTH, 1);
                if (cal.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                    content = ctx.getResources().getString(R.string.today) + e.getHourBegin();
                    toContinue = true;
                }
                cal.add(Calendar.DAY_OF_MONTH, -7);
                if (cal.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                    content = ctx.getResources().getString(R.string.in_a_week) + e.getDateBegin();
                    toContinue = true;
                }
                if (toContinue) {
                    NotificationManager manager = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
                    Notification.Builder noti;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel chanell = new NotificationChannel("channel1", "Condor", NotificationManager.IMPORTANCE_DEFAULT);
                        manager.createNotificationChannel(chanell);
                        noti = new Notification.Builder(ctx, "channel1");
                    } else {
                        noti = new Notification.Builder(ctx);
                    }

                    Intent newIntent = new Intent(ctx, EventViewerActivity.class);
                    newIntent.putExtra("id", e.getId());
                    newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 1, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    noti.setContentTitle(Html.fromHtml(e.getName()))
                            .setContentText(content)
                            .setSmallIcon(R.drawable.ic_launcher_material)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);
                    if (Build.VERSION.SDK_INT >= 21) {
                        noti.setVisibility(Notification.VISIBILITY_PUBLIC);
                    }
                    final int _id = Integer.decode(e.getId()) + 1025638;
                    manager.notify(_id, noti.build());
                }
            }
        }
    }
}
