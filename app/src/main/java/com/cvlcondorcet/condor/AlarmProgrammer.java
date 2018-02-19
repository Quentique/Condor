package com.cvlcondorcet.condor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Programs the different alarms, which order displaying notification for events
 * @author Quentin DE MUYNCK
 * @see AlarmReceiver
 */

public class AlarmProgrammer {

    /**
     * Programs ONE alarm of ONE event
     * @param ctx   Context
     * @param id    the id of the event {@link Event#id}
     * @param startEvent    the beginning of the event {@link Event#begin}
     */
    static void setAlarm(Context ctx, String id, Date startEvent) {
        AlarmManager manager = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startEvent);

        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 5);
        calendar.roll(Calendar.DAY_OF_MONTH, false);
        Intent newIntent = new Intent(ctx, AlarmReceiver.class);
        newIntent.putExtra("id", id);
        PendingIntent intent = PendingIntent.getBroadcast(ctx, Integer.parseInt(id), newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intent);
    }

    /**
     * Retrieves and schedules all events again
     * @param ctx   Context
     * @see Database#getEvents()
     */
    static void scheduleAllAlarms(Context ctx) {

        Intent deleteIntent = new Intent(ctx, AlarmReceiver.class);

        AlarmManager manager = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);

        Database db = new Database(ctx);
        db.open();
        ArrayList<Event> list = db.getEvents();
        db.close();
        for (Event event : list) {
            PendingIntent intent = PendingIntent.getService(ctx, Integer.parseInt(event.getId()), deleteIntent, 0);
            try {
                manager.cancel(intent);
            } catch (Exception e) {}
            setAlarm(ctx, event.getId(), event.getDateBeginDate());
        }
    }
}
