package com.cvlcondorcet.condor;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * // NO MORE USED //
 * Class representing a teacher absence
 */

class TeachersAbsence {
    private String name, title, date;
    private boolean multipleDays;
    private boolean morning;
    private boolean afternoon;
    private String beginning;
    private String end;

    public TeachersAbsence(String g_title, String g_name, String g_date_beginning, String g_date_end) {
        this.title = g_title;
        this.name = g_name;
        this.beginning = g_date_beginning;
        this.end = g_date_end;
        this.date = null;
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

    String getTitle() { return this.title; }
    String getName() {
        return this.name;
    }
    String getBeginning() { return beginning; }
    String getEnd() { return end; }
    String getDate() { return date; }
    boolean getMultipleDays() { return multipleDays; }
    boolean getMorning() { return morning; }
    boolean getAfternoon() { return afternoon; }

    /**
     * Determines what kind of absence it is according to given date information, then
     * formats date in string, according to which kind of absence it is and locale settings.
     */
    private void analyseDate() {
        try {
            multipleDays = !beginning.startsWith(end.substring(0, 10));
        }catch (Exception e ) { multipleDays = false; }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try {
            Date begin = format.parse(beginning);
            Date ended = format.parse(end);
            String finalFormat;
            Log.i("LOCALE", MainActivity.locale);
            switch (MainActivity.locale) {
                case "fra":
                    finalFormat = "dd/MM";
                    break;
                case "eng":
                    finalFormat = "dd/MM";
                    break;
                case "deu":
                    finalFormat = "dd.MM.";
                    break;
                default:
                    finalFormat="dd/MM";
                    break;
            }
            Log.i("e", beginning.substring(11, 13));
            if (beginning.substring(11, 13).contains("13")) { morning = false; afternoon = true; Log.i("e", "12 HOURS");}
            else if (end.substring(11, 13).contains("23") || end.substring(11, 13).contains("18")) { morning = true; afternoon = true; Log.i("e", "23 HOURS");}
            else if (beginning.substring(11, 13).contains("00") || beginning.contains("08") && end.substring(11, 13).contains("13")){ morning = true; afternoon = false; Log.i("e", "ELSE HOURS");}
            else {
                Log.i("E", "NEW HOURS");
                date = new SimpleDateFormat(finalFormat).format(begin);
                switch (MainActivity.locale) {
                    case "fra":
                        finalFormat = "HH 'h' mm";
                        break;
                    case "eng":
                        finalFormat = "h:mm a";
                        break;
                    case "deu":
                        finalFormat = "H.mm";
                        break;
                    default:
                        finalFormat = "HH:mm";
                        break;
                }
            }

            SimpleDateFormat format2 = new SimpleDateFormat(finalFormat);
            beginning = format2.format(begin);
            end = format2.format(ended);
        } catch (ParseException e) {}
    }
}
