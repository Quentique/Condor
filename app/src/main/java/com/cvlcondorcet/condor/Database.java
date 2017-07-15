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

    public void open() throws SQLException {
        database = helper.getWritableDatabase();
        Log.i("WARNING", database.getPath());
    }

    public void close() {
        database.close();
    }

    public ArrayList updateGen(JSONArray array) {
        ArrayList toBeDownloaded = new ArrayList();
        boolean logo = false;
        boolean cover = false;
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
                }
                database.replace(DBOpenHelper.General.TABLE_NAME, null, values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return toBeDownloaded;
    }

    public void updatePosts(JSONArray array) {
        for (int i = 0; i < array.length(); i++){
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
                database.replace(DBOpenHelper.Posts.TABLE_NAME, null, values);
            } catch (JSONException e)
            {
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
            cursor.moveToFirst();
            Log.i("DEBUGONCE", cursor.getString(cursor.getColumnIndex(DBOpenHelper.General.COLUMN_VALUE)));
            return cursor.getString(cursor.getColumnIndex(DBOpenHelper.General.COLUMN_VALUE));

        } else { return ""; }
    }

    public void beginSync() {
       database.execSQL("INSERT OR REPLACE INTO " + DBOpenHelper.General.TABLE_NAME + " (" + DBOpenHelper.General.COLUMN_NAME + ", " + DBOpenHelper.General.COLUMN_VALUE + ")" + "VALUES ('last_sync', datetime('now', 'localtime'))");
       /* ContentValues values = new ContentValues();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        values.put(DBOpenHelper.General.COLUMN_VALUE, date);
        database.update(DBOpenHelper.General.TABLE_NAME, values, "name = 'timestamp'", null);*/
    }


}

