package com.cvlcondorcet.condor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

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

    DBOpenHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        Log.i("UPGRADE", "WANT TO UPDATE DB3");
    }

    /**
     * Creates the Database, by SQL instructions.
     * @param db    the {@link SQLiteDatabase} object
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("UPGRADE", "WANT TO UPDATE DB2");
        db.execSQL(POSTS_TABLE);
        db.execSQL(PROFS_TABLE);
        db.execSQL(GEN_TABLE);
    }

    /**
     * If database is too old (when upgrading the app), destroys previous tables and recreates it.
     * @param db    the {@link SQLiteDatabase} object
     * @param oldVersion    current version
     * @param newVersion    new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("UPGRADE", "WANT TO UPDATE DB");
        db.execSQL("DROP TABLE IF EXISTS " + Posts.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Profs.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + General.TABLE_NAME);
        onCreate(db);
    }

    private static class Constants implements BaseColumns {
        static final String DATABASE_NAME = "database.db";
        static final int DATABASE_VERSION = 18;
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
}
