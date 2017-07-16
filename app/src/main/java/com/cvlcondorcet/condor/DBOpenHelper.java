package com.cvlcondorcet.condor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Quentin DE MUYNCK on 12/07/2017.
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String POSTS_TABLE = "CREATE TABLE " + Posts.TABLE_NAME + "(" +
            Posts.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Posts.COLUMN_NAME + " TEXT, " +
            Posts.COLUMN_CONTENT + " TEXT, " +
            Posts.COLUMN_STATE  + " NUMERIC, " +
            Posts.COLUMN_DATE + " TEXT, " +
            Posts.COLUMN_CAT + " TEXT)";

    private static final String GEN_TABLE = "CREATE TABLE " + General.TABLE_NAME + "(" +
            General.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            General.COLUMN_NAME + " TEXT, " +
            General.COLUMN_VALUE + " TEXT)";

    private static final String PROFS_TABLE = "CREATE TABLE " + Profs.TABLE_NAME + "(" +
            Profs.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Profs.COLUMN_NAME + " TEXT, " +
            Profs.COLUMN_BEGIN + " TEXT, " +
            Profs.COLUMN_END + " TEXT, " +
            Profs.COLUMN_DELETED + " TEXT)";

    public DBOpenHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(POSTS_TABLE);
        db.execSQL(PROFS_TABLE);
        db.execSQL(GEN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Posts.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Profs.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + General.TABLE_NAME);
        onCreate(db);
    }

    public static class Constants implements BaseColumns {
        public static final String DATABASE_NAME = "database.db";
        public static final int DATABASE_VERSION = 5;
    }

    public static class Posts implements BaseColumns {
        public static final String TABLE_NAME = "posts";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_STATE = "deleted";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_CAT = "categories";
    }

    public static class General implements BaseColumns {
        public static final String TABLE_NAME = "gen";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_VALUE = "value";
    }

    public static class Profs implements BaseColumns {
        public static final String TABLE_NAME = "profs";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BEGIN = "date_begin";
        public static final String COLUMN_END = "date_end";
        public static final String COLUMN_DELETED = "deleted";
    }
}
