package com.cvlcondorcet.condor;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Quentin DE MUYNCK on 19/09/2017.
 */

public class Event {
    private String name, desc, place, picture, id;
    private Date begin, end;

    public Event(String id, String name, String desc, String place, String picture, String begin, String end) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.place = place;
        this.picture = picture;
        try {
            this.begin = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(begin);
            this.end = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(end);
        } catch (ParseException e) {e.printStackTrace(); }
    }

    public String getId() {return id;}
    public String getName() { return name; }
    public String getDesc() { return desc; }
    public String getPlace() { return place; }
    public String getPicture() { return picture; }
    public String getDateBegin() { return new SimpleDateFormat("dd/MM").format(begin);}
    public String getDateEnd() { return new SimpleDateFormat("dd/MM").format(end); }
    public String getHourBegin() { return new SimpleDateFormat("hh:mm").format(begin); }
    public String getHourEnd() { return new SimpleDateFormat("hh:mm").format(end);}
}
