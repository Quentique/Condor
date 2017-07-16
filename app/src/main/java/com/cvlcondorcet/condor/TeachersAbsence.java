package com.cvlcondorcet.condor;

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

        multipleDays = (beginning.startsWith(end.substring(0,9))) ? false : true;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBeginning(String beginning) {
        this.beginning = beginning;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getName() {
        return this.name;
    }

    public String getBeginning() { return beginning; }
    public String getEnd() { return end; }
}
