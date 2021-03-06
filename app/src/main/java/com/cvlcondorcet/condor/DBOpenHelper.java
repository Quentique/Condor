package com.cvlcondorcet.condor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Contains table & column names, manages creation and upgrade of the database.
 * @author Quentin DE MUYNCK
 * @see Database#open()
 * @see Database
 */

class DBOpenHelper extends SQLiteOpenHelper {

    private static final String POSTS_TABLE = "CREATE TABLE " + Posts.TABLE_NAME + "(" +
            Posts.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Posts.COLUMN_NAME + " TEXT, " +
            Posts.COLUMN_CONTENT + " TEXT, " +
            Posts.COLUMN_STATE  + " NUMERIC, " +
            Posts.COLUMN_DATE + " TEXT, " +
            Posts.COLUMN_CAT + " TEXT, " +
            Posts.COLUMN_PIC + " TEXT)";

    private static final String GEN_TABLE = "CREATE TABLE " + General.TABLE_NAME + "(" +
            General.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            General.COLUMN_NAME + " TEXT, " +
            General.COLUMN_VALUE + " TEXT)";

    private static final String PROFS_TABLE = "CREATE TABLE " + Profs.TABLE_NAME + "(" +
            Profs.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Profs.COLUMN_TITLE + " TEXT, " +
            Profs.COLUMN_NAME + " TEXT, " +
            Profs.COLUMN_BEGIN + " TEXT, " +
            Profs.COLUMN_END + " TEXT, " +
            Profs.COLUMN_DELETED + " TEXT)";

    private static final String MAPS_TABLE = "CREATE TABLE " + Maps.TABLE_NAME + "(" +
            Maps.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Maps.COLUMN_NAME + " TEXT, " +
            Maps.COLUMN_DPNAME + " TEXT, " +
            Maps.COLUMN_DESC + " TEXT, " +
            Maps.COLUMN_FILE + " TEXT, " +
            Maps.COLUMN_POS + " TEXT, " +
            Maps.COLUMN_MARK + " TEXT)";

    private static final String EVENTS_TABLE = "CREATE TABLE " + Events.TABLE_NAME + "(" +
            Events.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Events.COLUMN_NAME + " TEXT, " +
            Events.COLUMN_DESC + " TEXT, " +
            Events.COLUMN_START + " TEXT, " +
            Events.COLUMN_END + " TEXT, "+
            Events.COLUMN_PLACE + " TEXT, " +
            Events.COLUMN_STATE + " TEXT, " +
            Events.COLUMN_PICT + " TEXT)";

    DBOpenHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    /**
     * Creates the Database, by SQL instructions.
     * @param db    the {@link SQLiteDatabase} object
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(POSTS_TABLE);
        db.execSQL(PROFS_TABLE);
        db.execSQL(GEN_TABLE);
        db.execSQL(MAPS_TABLE);
        db.execSQL(EVENTS_TABLE);
    }

    /**
     * If database is too old (when upgrading the app), destroys previous tables and recreates it.
     * @param db    the {@link SQLiteDatabase} object
     * @param oldVersion    current version
     * @param newVersion    new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Posts.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Profs.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + General.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Maps.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Events.TABLE_NAME);
        onCreate(db);
    }

    private static class Constants implements BaseColumns {
        static final String DATABASE_NAME = "database.db";
        static final int DATABASE_VERSION = 34;
    }

    static class Posts implements BaseColumns {
        static final String TABLE_NAME = "posts";
        static final String COLUMN_ID = "_id";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_CONTENT = "content";
        static final String COLUMN_STATE = "deleted";
        static final String COLUMN_DATE = "date";
        static final String COLUMN_CAT = "categories";
        static final String COLUMN_PIC = "picture";
    }

    static class General implements BaseColumns {
        static final String TABLE_NAME = "gen";
        static final String COLUMN_ID = "_id";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_VALUE = "value";
    }

    static class Profs implements BaseColumns {
        static final String TABLE_NAME = "profs";
        static final String COLUMN_ID = "_id";
        static final String COLUMN_TITLE = "title";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_BEGIN = "date_begin";
        static final String COLUMN_END = "date_end";
        static final String COLUMN_DELETED = "deleted";
    }

    static class Maps implements BaseColumns {
        static final String TABLE_NAME = "maps";
        static final String COLUMN_ID = "_id";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_DPNAME = "display_name";
        static final String COLUMN_DESC = "description";
        static final String COLUMN_FILE = "file";
        static final String COLUMN_POS = "pos";
        static final String COLUMN_MARK = "mark";
    }

    static class Events implements BaseColumns {
        static final String TABLE_NAME = "events";
        static final String COLUMN_ID = "_id";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_DESC = "description";
        static final String COLUMN_START = "start";
        static final String COLUMN_END = "end";
        static final String COLUMN_PLACE = "place";
        static final String COLUMN_STATE = "state";
        static final String COLUMN_PICT = "picture";
    }
}
