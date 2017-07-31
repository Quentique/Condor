package com.cvlcondorcet.condor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Quentin DE MUYNCK on 12/07/2017.
 */

public class Database {

    private SQLiteDatabase database;
    private DBOpenHelper helper;

    public Database(Context context) {
        helper = new DBOpenHelper(context);
    }

    public void open() throws SQLException { database = helper.getWritableDatabase(); }
    public void close() {
        database.close();
    }

    public ArrayList updateGen(JSONArray array) {
        ArrayList toBeDownloaded = new ArrayList();
        boolean logo = false;
        boolean cover = false;
        boolean canteen = false;
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject element = array.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(DBOpenHelper.General.COLUMN_ID, element.getInt("id"));
                values.put(DBOpenHelper.General.COLUMN_NAME, element.getString("name"));
                values.put(DBOpenHelper.General.COLUMN_VALUE, element.getString("value"));
                String value = element.getString("value");
                switch(element.getString("name")) {
                    case "logo_updated":
                        if (element.getString("value") != timestamp("logo_updated")) { logo = true; }
                        break;
                    case "logo":
                        if (logo) { toBeDownloaded.add(element.getString("value")); }
                        break;
                    case "cover_updated":
                        if (element.getString("value") != timestamp("cover_updated")) { cover= true; }
                        break;
                    case "cover":
                        if (cover) toBeDownloaded.add(element.getString("value"));
                        break;
                    case "canteen_updated":
                        if (element.getString("value") != timestamp("canteen_updated")) { canteen = true; }
                        break;
                    case "canteen":
                        if (canteen) toBeDownloaded.add(element.getString("value"));
                }
                database.replace(DBOpenHelper.General.TABLE_NAME, null, values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return toBeDownloaded;
    }

    public void updatePosts(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject element = array.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(DBOpenHelper.Posts.COLUMN_ID, element.getInt("id"));
                values.put(DBOpenHelper.Posts.COLUMN_NAME, element.getString("name"));
                values.put(DBOpenHelper.Posts.COLUMN_CONTENT, element.getString("content"));
                values.put(DBOpenHelper.Posts.COLUMN_DATE, element.getString("date"));
                Log.i("DEBUG", element.getString("state"));
                if (element.getString("state").contains("deleted")) {
                    values.put(DBOpenHelper.Posts.COLUMN_STATE, 1);
                } else { values.put(DBOpenHelper.Posts.COLUMN_STATE, 0); }
                values.put(DBOpenHelper.Posts.COLUMN_CAT, element.getString("categories"));
                values.put(DBOpenHelper.Posts.COLUMN_PIC, element.getString("picture"));
                database.replace(DBOpenHelper.Posts.TABLE_NAME, null, values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateProfs(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject element = array.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(DBOpenHelper.Profs.COLUMN_ID, element.getInt("id"));
                values.put(DBOpenHelper.Profs.COLUMN_NAME, element.getString("name"));
                values.put(DBOpenHelper.Profs.COLUMN_BEGIN, element.getString("begin_date"));
                values.put(DBOpenHelper.Profs.COLUMN_END, element.getString("end_date"));
                values.put(DBOpenHelper.Profs.COLUMN_DELETED, element.getString("deleted"));
                database.replace(DBOpenHelper.Profs.TABLE_NAME, null, values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String timestamp(String name) {
        String result;
        Cursor cursor = database.query(DBOpenHelper.General.TABLE_NAME, new String[] {DBOpenHelper.General.COLUMN_VALUE}, DBOpenHelper.General.COLUMN_NAME + " = ?", new String[] {name}, null, null, null);

        if (cursor != null && cursor.getCount()>0) {
            try {
                cursor.moveToFirst();
                Log.i("DEBUGONCE", cursor.getString(cursor.getColumnIndex(DBOpenHelper.General.COLUMN_VALUE)));
                return cursor.getString(cursor.getColumnIndex(DBOpenHelper.General.COLUMN_VALUE));
            } finally {
                cursor.close();
            }

        } else { return ""; }
    }

    public void beginSync() {
        Cursor cursor = database.query(DBOpenHelper.General.TABLE_NAME, new String[]{DBOpenHelper.General.COLUMN_ID}, DBOpenHelper.General.COLUMN_NAME + " = 'last_sync'", null, null, null, null);
        String id ="";

        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            id = cursor.getString(cursor.getColumnIndex(DBOpenHelper.General.COLUMN_ID));
        }

        if (id.isEmpty()) {
            database.execSQL("INSERT OR REPLACE INTO " + DBOpenHelper.General.TABLE_NAME + " (" + DBOpenHelper.General.COLUMN_NAME + ", " + DBOpenHelper.General.COLUMN_VALUE + ")" + "VALUES ('last_sync', datetime('now', 'localtime'))");
        } else {
            database.execSQL("INSERT OR REPLACE INTO " + DBOpenHelper.General.TABLE_NAME + " (" + DBOpenHelper.General.COLUMN_ID + ", " + DBOpenHelper.General.COLUMN_NAME + ", " + DBOpenHelper.General.COLUMN_VALUE + ")" + "VALUES (" + id + ",'last_sync', datetime('now', 'localtime'))");
        }
    }

    public ArrayList<TeachersAbsence> getTeachersAbsence() {
        ArrayList<TeachersAbsence> results = new ArrayList<>();
        Cursor cursor = database.query(DBOpenHelper.Profs.TABLE_NAME,
                        new String[] {DBOpenHelper.Profs.COLUMN_NAME, DBOpenHelper.Profs.COLUMN_BEGIN, DBOpenHelper.Profs.COLUMN_END},
                        DBOpenHelper.Profs.COLUMN_DELETED + " != 1",
                        null, null, null, null);

        if (cursor != null & cursor.getCount()>0) {
            try {
                while(cursor.moveToNext()) {
                    TeachersAbsence absence = new TeachersAbsence(
                                    cursor.getString(cursor.getColumnIndex(DBOpenHelper.Profs.COLUMN_NAME)),
                                    cursor.getString(cursor.getColumnIndex(DBOpenHelper.Profs.COLUMN_BEGIN)),
                                    cursor.getString(cursor.getColumnIndex(DBOpenHelper.Profs.COLUMN_END)));
                    results.add(absence);
                }
            } finally {
                cursor.close();
            }
        }
        return results;
    }

    public ArrayList<Post> getPosts() {
        ArrayList<Post> results = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT _id, name, substr(content, 0, 101), date, picture, categories FROM posts WHERE deleted != 1", null);

        if (cursor != null && cursor.getCount()>0) {
            try {
                while(cursor.moveToNext()) {
                    Post post = new Post(
                            cursor.getString(cursor.getColumnIndex(DBOpenHelper.Posts.COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndex(DBOpenHelper.Posts.COLUMN_NAME)),
                            cursor.getString(cursor.getColumnIndex("substr(content, 0, 101)")),
                            cursor.getString(cursor.getColumnIndex(DBOpenHelper.Posts.COLUMN_PIC)),
                            cursor.getString(cursor.getColumnIndex(DBOpenHelper.Posts.COLUMN_DATE)),
                            cursor.getString(cursor.getColumnIndex(DBOpenHelper.Posts.COLUMN_CAT)));
                    results.add(post);
                }
            } finally {
                cursor.close();
            }
        }
        return results;
    }

    public ArrayList<String> getCategories() {
        ArrayList<String> results = new ArrayList<>();
        Cursor cursor = database.query(DBOpenHelper.General.TABLE_NAME, new String[] {DBOpenHelper.General.COLUMN_VALUE}, DBOpenHelper.General.COLUMN_NAME + " = ?", new String[] {"categories"}, null, null, null);
        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            results = parseCategories(cursor.getString(cursor.getColumnIndex(DBOpenHelper.General.COLUMN_VALUE)));
        }
        return results;
    }

    public Post getPost(String id) {
        Cursor cursor = database.query(DBOpenHelper.Posts.TABLE_NAME,
                new String[] {DBOpenHelper.Posts.COLUMN_NAME, DBOpenHelper.Posts.COLUMN_CONTENT, DBOpenHelper.Posts.COLUMN_DATE, DBOpenHelper.Posts.COLUMN_PIC, DBOpenHelper.Posts.COLUMN_CAT},
                DBOpenHelper.Posts.COLUMN_ID + " = " + id,
                null, null, null, null);
        if (cursor != null & cursor.getCount()>0) {
            try {
                cursor.moveToFirst();
                Post post = new Post(id,
                        cursor.getString(cursor.getColumnIndex(DBOpenHelper.Posts.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(DBOpenHelper.Posts.COLUMN_CONTENT)),
                        cursor.getString(cursor.getColumnIndex(DBOpenHelper.Posts.COLUMN_PIC)),
                        cursor.getString(cursor.getColumnIndex(DBOpenHelper.Posts.COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndex(DBOpenHelper.Posts.COLUMN_CAT)));
                return post;
            } finally {
                cursor.close();
            }
        } else {
            return null;
        }
    }

    public static ArrayList<String> parseCategories(String categories) {
        JSONArray array;
        ArrayList<String> results = new ArrayList<>();
        try {
            array = new JSONArray(categories);
            for (int i = 0 ; i < array.length() ; i++) {
                results.add(array.getString(i));
            }
        } catch (JSONException e) { e.printStackTrace(); }

        return results;
    }
}

