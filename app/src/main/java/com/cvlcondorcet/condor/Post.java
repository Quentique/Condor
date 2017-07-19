package com.cvlcondorcet.condor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Quentin DE MUYNCK on 18/07/2017.
 */

public class Post {
    private String name, content, id, picture, date, formatedDate;

    public Post(String id, String name, String content, String picture, String date) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.picture = picture;
        this.date = date;
        this.formatedDate = formatDate();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getContent() { return content; }
    public String getPicture() { return picture; }
    public String getDate() { return date; }
    public String getFormatedDate() { return formatedDate; }

    public String formatDate() {
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date pdate = format1.parse(date);
            SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
            return format2.format(pdate);
        } catch (ParseException e ) { e.printStackTrace(); return ""; }
    }
}
