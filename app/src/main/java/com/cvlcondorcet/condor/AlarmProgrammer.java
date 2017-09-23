package com.cvlcondorcet.condor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Quentin DE MUYNCK on 23/09/2017.
 */

public class AlarmProgrammer {

    static void setAlarm(Context ctx, String id, Date startEvent) {
        AlarmManager manager = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startEvent);
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 20);
        calendar.roll(Calendar.DAY_OF_MONTH, false);
        Intent newIntent = new Intent(ctx, AlarmReceiver.class);
        newIntent.putExtra("id", id);
        final int _id = (int) System.currentTimeMillis();
        PendingIntent intent = PendingIntent.getBroadcast(ctx, _id, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
       // manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intent);
        manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intent);
        Log.i("ALarm", calendar.toString());
        Log.i("ALARM", Calendar.getInstance().getTime().toString());
      //  manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5*1000, intent);
        Log.i("ALARM", "Alarm has been set");
    }

    static void scheduleAllAlarms(Context ctx) {
        Database db = new Database(ctx);
        db.open();
        ArrayList<Event> list = db.getEvents();
        db.close();
        for (Event event : list) {
            setAlarm(ctx, event.getId(), event.getDateBeginDate());
        }
        Log.i("SCHEDULED", "ALL ALARMS");
    }
}
