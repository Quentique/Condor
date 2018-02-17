package com.cvlcondorcet.condor;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class representing an event
 * @author Quentin DE MUYNCK
 * @see RecyclerViewAdapterEvents#events
 */

class Event {
    private String name, desc, place, picture, id;
    private Date begin, end;
    public static String format, format2;

    public Event(String id, String name, String desc, String place, String picture, String begin, String end) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.place = place;
        this.picture = picture;
        try {
            this.begin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(begin);
            this.end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end);
        } catch (ParseException e) {e.printStackTrace(); }
    }

    public String getId() {return id;}
    public String getName() { return name; }
    public String getDesc() { return desc; }
    public String getPlace() { return place; }
    public String getPicture() { return picture; }
    public String getDateBegin() { return new SimpleDateFormat(format).format(begin);}
    public Date getDateBeginDate() { return begin; }
    public String getDateEnd() { return new SimpleDateFormat(format).format(end); }
    public String getHourBegin() { return new SimpleDateFormat(format2).format(begin); }
    public String getHourEnd() { return new SimpleDateFormat(format2).format(end);}
}
