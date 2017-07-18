package com.cvlcondorcet.condor;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Quentin DE MUYNCK on 16/07/2017.
 */

public class TeachersAbsence {
    private String name;
    private boolean multipleDays;
    private boolean morning;
    private boolean afternoon;
    private String beginning;
    private String end;

    public TeachersAbsence(String g_name, String g_date_beginning, String g_date_end) {
        this.name = g_name;
        this.beginning = g_date_beginning;
        this.end = g_date_end;
        analyseDate();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String beginning, String end) {
        this.beginning = beginning;
        this.end = end;
        analyseDate();
    }

    public String getName() {
        return this.name;
    }
    public String getBeginning() { return beginning; }
    public String getEnd() { return end; }
    public boolean getMultipleDays() { return multipleDays; }
    public boolean getMorning() { return morning; }
    public boolean getAfternoon() { return afternoon; }

    private void analyseDate() {
        try {
            multipleDays = (beginning.startsWith(end.substring(0, 10))) ? false : true;
        }catch (Exception e ) { multipleDays = false; }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date begin = format.parse(beginning);
            Date ended = format.parse(end);
            Log.i("e", beginning.substring(11, 13));
            if (beginning.substring(11, 13).contains("12")) { morning = false; afternoon = true; Log.i("e", "12 HOURS");}
            else if (end.substring(11, 13).contains("23")) { morning = true; afternoon = true; Log.i("e", "23 HOURS");}
            else { morning = true; afternoon = false; Log.i("e", "ELSE HOURS");}
            SimpleDateFormat format2 = new SimpleDateFormat("dd/MM");
            beginning = format2.format(begin);
            end = format2.format(ended);
        } catch (ParseException e) {}
    }
}
