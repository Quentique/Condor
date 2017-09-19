package com.cvlcondorcet.condor;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Class representing and gathering information about a single post.
 * @author Quentin DE MUYNCK
 * @see Database#getPost(String)
 * @see Database#getPosts()
 * @see RecyclerViewAdapterPosts
 * @see PostsLoader#loadInBackground()
 */

class Post implements Comparable<Post> {
    private final String name, content, picture, date, formatedDate, formatedCategories;
    private String link, id;
    private final ArrayList<String> categories;

    public Post(String id, String name, String content, String picture, String date, String categories) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.picture = picture;
        this.date = date;
        this.formatedDate = formatDate(date, "yyyy-MM-dd hh:mm:ss", "dd/MM/yyyy");
        this.categories = Database.parseCategories(categories);
        this.formatedCategories = formatCategories();
        this.link = "";
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getContent() { return content; }
    public String getPicture() { return picture; }
    private String getDate() { return date; }
    public String getFormatedDate() { return formatedDate; }
    public ArrayList<String> getCategories() { return categories;}
    public String getFormatedCategories() { return formatedCategories; }
    public String getLink() { return link; }

    public void setLink(String link) { this.link = link; }
    public void setId(String id) { this.id = id; }

    /**
     * Compares the post by date (to classifies them by descending date).
     * @param postToCompare the post compared with this one
     * @return  int which represents which one is older than the other one
     */
    @Override
    public int compareTo(@NonNull Post postToCompare) {
        Date newDate = getDateObject(postToCompare.getDate());
        Date actualDate = getDateObject(getDate());
        try {
            return actualDate.compareTo(newDate);
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * Static method to format a string date into another format.
     * @param toParse   date in String format
     * @param oldFormat format of the given date
     * @param toFormat  desired format
     * @return  the desired formatted date in string or "" if error occured
     */
    public static String formatDate(String toParse, String oldFormat, String toFormat) {
        try {
            SimpleDateFormat format1 = new SimpleDateFormat(oldFormat, Locale.US);
            Date pdate = format1.parse(toParse);
            SimpleDateFormat format2 = new SimpleDateFormat(toFormat, Locale.getDefault());
            return format2.format(pdate);
        } catch (ParseException e ) { e.printStackTrace(); return ""; }
    }

    /**
     * Parses a date and return the date format.
     * @param obj   date in string
     * @return  date object
     * @see Post#compareTo(Post)
     */
    public static Date getDateObject(String obj) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(obj);
        } catch (ParseException e) { e.printStackTrace(); return null; }
    }

    /**
     * Displays categories into a single string, cut by commas.
     * @return  categories in String format
     */
    private String formatCategories() {
        String result = "";
        for (String item : categories) {
            result += item + ", ";
        }
        try {
            return result.substring(0, result.length()-2);
        } catch (Exception e ) {
            return "";
        }
    }
}
