package com.cvlcondorcet.condor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Quentin DE MUYNCK on 23/09/2017.
 */

public class RebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            AlarmProgrammer.scheduleAllAlarms(context);
        }
    }
}
