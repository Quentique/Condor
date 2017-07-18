package com.cvlcondorcet.condor;

/**
 * Created by Quentin DE MUYNCK on 18/07/2017.
 */

public class Post {
    private String name, content, id, picture, date;

    public Post(String id, String name, String content, String picture, String date) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.picture = picture;
        this.date = date;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getContent() { return content; }
    public String getPicture() { return picture; }
    public String getDate() { return date; }
}
