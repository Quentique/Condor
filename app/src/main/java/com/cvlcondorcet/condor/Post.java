package com.cvlcondorcet.condor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Quentin DE MUYNCK on 18/07/2017.
 */

public class Post implements Comparable<Post> {
    private String name, content, id, picture, date, formatedDate, formatedCategories;
    private ArrayList<String> categories;

    public Post(String id, String name, String content, String picture, String date, String categories) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.picture = picture;
        this.date = date;
        this.formatedDate = formatDate(date, "yyyy-MM-dd hh:mm:ss", "dd/MM/yyyy");
        this.categories = Database.parseCategories(categories);
        this.formatedCategories = formatCategories();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getContent() { return content; }
    public String getPicture() { return picture; }
    public String getDate() { return date; }
    public String getFormatedDate() { return formatedDate; }
    public ArrayList<String> getCategories() { return categories;}
    public String getFormatedCategories() { return formatedCategories; }

    @Override
    public int compareTo(Post postToCompare) {
        Date newDate = getDateObject(postToCompare.getDate(), "yyyy-MM-dd hh:mm:ss");
        Date actualDate = getDateObject(getDate(), "yyyy-MM-dd hh:mm:ss");
        return actualDate.compareTo(newDate);
    }

    public String formatDate(String toParse, String oldFormat, String toFormat) {
        try {
            SimpleDateFormat format1 = new SimpleDateFormat(oldFormat);
            Date pdate = format1.parse(toParse);
            SimpleDateFormat format2 = new SimpleDateFormat(toFormat);
            return format2.format(pdate);
        } catch (ParseException e ) { e.printStackTrace(); return ""; }
    }

    public Date getDateObject(String obj, String format) {
        try {
            return new SimpleDateFormat(format).parse(obj);
        } catch (ParseException e) { e.printStackTrace(); return null; }
    }

    public String formatCategories() {
        String result = "";
        for (String item : categories) {
            result += item + ", ";
        }
        return result.substring(0, result.length()-2);
    }
}
