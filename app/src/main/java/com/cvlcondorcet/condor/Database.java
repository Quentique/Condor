package com.cvlcondorcet.condor;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    }

    public void close() {
        database.close();
    }

    public void updateGen(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject element = array.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(DBOpenHelper.General.COLUMN_ID, element.getInt("id"));
                values.put(DBOpenHelper.General.COLUMN_NAME, element.getString("name"));
                values.put(DBOpenHelper.General.COLUMN_VALUE, element.getString("value"));
                database.replace(DBOpenHelper.General.TABLE_NAME, null, values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

