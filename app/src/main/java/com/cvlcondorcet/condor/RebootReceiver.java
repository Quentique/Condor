package com.cvlcondorcet.condor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Schedules all alarms again after device reboots
 * @author Quentin DE MUYNCK
 * @see AlarmProgrammer
 */

public class RebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Event.format = context.getString(R.string.date_format);
            Event.format2 = context.getString(R.string.hour_format);
            AlarmProgrammer.scheduleAllAlarms(context);
        }
    }
}
