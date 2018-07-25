package com.cvlcondorcet.condor;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * Class for handling database operations.
 * @author Quentin DE MUYNCK
 */

class Database {

    private SQLiteDatabase database;
    private final DBOpenHelper helper;
    private final Context ctx;
    private ArrayList<Integer> posts, events;
    private boolean canteen, cvl, maps;

    /**
     * Default constructor.
     * @param context Context
     */
    Database(Context context) {
        helper = new DBOpenHelper(context);
        ctx = context;
    }

    boolean isOpen() {
        return database != null && database.isOpen();
    }

    void initialiseSync() {
        Log.i("SYNC", "Initalising function called");
        SharedPreferences pref = ctx.getSharedPreferences("notifications", 0);
        canteen = pref.getBoolean("canteen", false);
        maps = pref.getBoolean("maps", false);
        cvl = pref.getBoolean("cvl", false);
            events = parsePrefNot("events", ctx);
            Log.i("SYNC", events.toString());
            posts = parsePrefNot("posts", ctx);
            Log.i("SYNC", posts.toString());
    }

    void open() throws SQLException { database = helper.getWritableDatabase(); }
    void close() {
        database.close();
    }

    /**
     * Updates data in database from {@link JSONArray JSONArray} got from sync.
     * @param array updated data
     * @return  files that must be downloaded
     * @see Sync#get(String)
     * @see Sync#downloadFile(String)
     */
    @SuppressWarnings("unchecked")
    ArrayList updateGen(JSONArray array) {
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
                switch(element.getString("name")) {
                    case "logo_updated":
                        if (!element.getString("value").equals(timestamp("logo_updated"))) { logo = true; }
                        break;
                    case "logo":
                        if (logo) { toBeDownloaded.add(element.getString("value")); File file = new File(ctx.getApplicationContext().getFilesDir().toString()+"/"+timestamp("logo")); //noinspection ResultOfMethodCallIgnored
                            file.delete();  }
                        break;
                    case "cover_updated":
                        if (!element.getString("value").equals(timestamp("cover_updated"))) { cover= true; }
                        break;
                    case "cover":
                        if (cover) { toBeDownloaded.add(element.getString("value")); File file = new File(ctx.getApplicationContext().getFilesDir().toString()+"/"+timestamp("cover")); //noinspection ResultOfMethodCallIgnored
                            file.delete(); }
                        break;
                    case "canteen_updated":
                        if (!element.getString("value").equals(timestamp("canteen_updated"))) { canteen = true; }
                        break;
                    case "canteen":
                        if (canteen) { toBeDownloaded.add(element.getString("value")); File file = new File(ctx.getApplicationContext().getFilesDir().toString()+"/"+timestamp("canteen")); //noinspection ResultOfMethodCallIgnored
                            file.delete(); }
                        break;
                    case "cvl_updated":
                        if (!element.getString("value").equals(timestamp("cvl_updated"))) { cvl = true; }
                        break;
                    case "cvl":
                        if (cvl) {
                            toBeDownloaded.add(element.getString("value"));
                            File file = new File(ctx.getApplicationContext().getFilesDir().toString() + "/" + timestamp("cvl"));
                            file.delete();
                        }
                        break;
                }
                database.replace(DBOpenHelper.General.TABLE_NAME, null, values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return toBeDownloaded;
    }

    /**
     * Updates posts from data got from sync.
     * @param array Updated data
     *              @see Sync#get(String)
     */
    void updatePosts(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject element = array.getJSONObject(i);
                ContentValues values = new ContentValues();
                if (!posts.contains(element.getInt("id")))
                    posts.add(element.getInt("id"));
                values.put(DBOpenHelper.Posts.COLUMN_ID, element.getInt("id"));
                values.put(DBOpenHelper.Posts.COLUMN_NAME, element.getString("name"));
                values.put(DBOpenHelper.Posts.COLUMN_CONTENT, element.getString("content"));
                values.put(DBOpenHelper.Posts.COLUMN_DATE, element.getString("date"));
               // Log.i("DEBUG", element.getString("state"));
                if (element.getString("state").contains("deleted")) {
                    values.put(DBOpenHelper.Posts.COLUMN_STATE, 1);
                    posts.remove(Integer.valueOf(element.getInt("id")));
                } else { values.put(DBOpenHelper.Posts.COLUMN_STATE, 0); }
                values.put(DBOpenHelper.Posts.COLUMN_CAT, element.getString("categories"));
                values.put(DBOpenHelper.Posts.COLUMN_PIC, element.getString("picture"));
                database.replace(DBOpenHelper.Posts.TABLE_NAME, null, values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

// --Commented out by Inspection START (21/07/2018 11:44):
//    /**
//     * Updates teacher absences from data got from sync.
//     * @param array Updated data
//     *              @see Sync#get(String)
//     */
//    void updateProfs(JSONArray array) {
//        for (int i = 0; i < array.length(); i++) {
//            try {
//                JSONObject element = array.getJSONObject(i);
//                ContentValues values = new ContentValues();
//                values.put(DBOpenHelper.Profs.COLUMN_ID, element.getInt("id"));
//                values.put(DBOpenHelper.Profs.COLUMN_TITLE, element.getString("title"));
//                values.put(DBOpenHelper.Profs.COLUMN_NAME, element.getString("name"));
//                values.put(DBOpenHelper.Profs.COLUMN_BEGIN, element.getString("begin_date"));
//                values.put(DBOpenHelper.Profs.COLUMN_END, element.getString("end_date"));
//                values.put(DBOpenHelper.Profs.COLUMN_DELETED, element.getString("deleted"));
//                database.replace(DBOpenHelper.Profs.TABLE_NAME, null, values);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        database.delete(DBOpenHelper.Profs.TABLE_NAME, DBOpenHelper.Profs.COLUMN_END + " < CURRENT_TIMESTAMP", null);
//    }
// --Commented out by Inspection STOP (21/07/2018 11:44)

    /**
     * Updates maps table, empty it and refill it with new datas.
     * @param array Updated data
     *              @see Sync#get(String)
     */
    void updateMaps(JSONArray array) {
        if (array.length() != 0) {
            database.delete(DBOpenHelper.Maps.TABLE_NAME, null, null);
            for (int i = 0 ; i < array.length() ; i++) {
                try {
                    JSONObject element = array.getJSONObject(i);
                    ContentValues values = new ContentValues();
                    values.put(DBOpenHelper.Maps.COLUMN_ID, element.getInt("id"));
                    values.put(DBOpenHelper.Maps.COLUMN_NAME, element.getString("name"));
                    values.put(DBOpenHelper.Maps.COLUMN_DPNAME, element.getString("display_name"));
                    values.put(DBOpenHelper.Maps.COLUMN_DESC, element.getString("description"));
                    values.put(DBOpenHelper.Maps.COLUMN_FILE, element.getString("map"));
                    values.put(DBOpenHelper.Maps.COLUMN_POS, element.getString("pos"));
                    values.put(DBOpenHelper.Maps.COLUMN_MARK, element.getString("mark"));
                    database.replace(DBOpenHelper.Maps.TABLE_NAME, null, values);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            maps=true;
        }
    }

    void updateEvents(JSONArray array) {
        for (int i = 0 ; i < array.length() ; i++) {
            try {
                JSONObject element = array.getJSONObject(i);
                ContentValues values = new ContentValues();
                if(!events.contains(element.getInt("id")))
                    events.add(element.getInt("id"));
                values.put(DBOpenHelper.Events.COLUMN_ID, element.getInt("id"));
                values.put(DBOpenHelper.Events.COLUMN_NAME , element.getString("name"));
                values.put(DBOpenHelper.Events.COLUMN_DESC, element.getString("description"));
                values.put(DBOpenHelper.Events.COLUMN_START, element.getString("start"));
                values.put(DBOpenHelper.Events.COLUMN_END, element.getString("end"));
                values.put(DBOpenHelper.Events.COLUMN_PLACE, element.getString("place"));
                values.put(DBOpenHelper.Events.COLUMN_STATE, element.getString("state"));
                if (element.getString("state").equals("deleted"))
                    events.remove(Integer.valueOf(element.getInt("id")));
                values.put(DBOpenHelper.Events.COLUMN_PICT, element.getString("picture"));
                database.replace(DBOpenHelper.Events.TABLE_NAME, null, values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        database.delete(DBOpenHelper.Events.TABLE_NAME, DBOpenHelper.Events.COLUMN_STATE + " = 'deleted'", null);
       // database.delete(DBOpenHelper.Events.TABLE_NAME, DBOpenHelper.Events.COLUMN_END + " < CURRENT_TIMESTAMP", null);
        AlarmProgrammer.scheduleAllAlarms(ctx);
    }

    /**
     * Retrieves a value from {@link DBOpenHelper#GEN_TABLE}
     * @param name  the key of value
     * @return  the value corresponding to the key
     * @see DBOpenHelper#GEN_TABLE
     */
    String timestamp(String name) {
        Cursor cursor = database.query(DBOpenHelper.General.TABLE_NAME, new String[] {DBOpenHelper.General.COLUMN_VALUE}, DBOpenHelper.General.COLUMN_NAME + " = ?", new String[] {name}, null, null, null);

        if (cursor != null && cursor.getCount()>0) {
            try {
                cursor.moveToFirst();
               // Log.i("DEBUGONCE", cursor.getString(cursor.getColumnIndex(DBOpenHelper.General.COLUMN_VALUE)));
                return cursor.getString(cursor.getColumnIndex(DBOpenHelper.General.COLUMN_VALUE));
            } finally {
                cursor.close();
            }
        } else { return ""; }
    }

    /**
     * Saves current date & time into the db for further synchronizations (avoid useless syncs).
     */
    void beginSync() {
        Cursor cursor = database.query(DBOpenHelper.General.TABLE_NAME, new String[]{DBOpenHelper.General.COLUMN_ID}, DBOpenHelper.General.COLUMN_NAME + " = 'last_sync'", null, null, null, null);
        String id ="";

        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            id = cursor.getString(cursor.getColumnIndex(DBOpenHelper.General.COLUMN_ID));
        }

        if (id.isEmpty()) {
            database.execSQL("INSERT OR REPLACE INTO " + DBOpenHelper.General.TABLE_NAME + " (" + DBOpenHelper.General.COLUMN_NAME + ", " + DBOpenHelper.General.COLUMN_VALUE + ")" + " VALUES ('last_sync', datetime('now', 'localtime'))");
        } else {
            database.execSQL("INSERT OR REPLACE INTO " + DBOpenHelper.General.TABLE_NAME + " (" + DBOpenHelper.General.COLUMN_ID + ", " + DBOpenHelper.General.COLUMN_NAME + ", " + DBOpenHelper.General.COLUMN_VALUE + ")" + " VALUES (" + id + ",'last_sync', datetime('now', 'localtime'))");
        }
        cursor.close();
        Log.i("DB", "INSERT OR REPLACE INTO " + DBOpenHelper.General.TABLE_NAME + " (" + DBOpenHelper.General.COLUMN_ID + ", " + DBOpenHelper.General.COLUMN_NAME + ", " + DBOpenHelper.General.COLUMN_VALUE + ")" + " VALUES (" + id + ",'last_sync', datetime('now', 'localtime'))");
    }

    void deleteTable(String table) {
        if (!table.equals("0")) {
            database.delete(table, null, null);
        }
    }

    void deleteAllTables() {
        database.delete(DBOpenHelper.Maps.TABLE_NAME, null, null);
        database.delete(DBOpenHelper.Events.TABLE_NAME, null, null);
        database.delete(DBOpenHelper.Posts.TABLE_NAME, null, null);
        database.delete(DBOpenHelper.General.TABLE_NAME, null, null);
    }

    /**
     * Retrieves all rows from teachers absences tables
     * @return  {@link ArrayList Array} containing all rows
     * @see DBOpenHelper#PROFS_TABLE
     */
    ArrayList<TeachersAbsence> getTeachersAbsence() {
        ArrayList<TeachersAbsence> results = new ArrayList<>();
        Cursor cursor = database.query(DBOpenHelper.Profs.TABLE_NAME,
                        new String[] {DBOpenHelper.Profs.COLUMN_TITLE, DBOpenHelper.Profs.COLUMN_NAME, DBOpenHelper.Profs.COLUMN_BEGIN, DBOpenHelper.Profs.COLUMN_END},
                        DBOpenHelper.Profs.COLUMN_DELETED + " != 1 AND " + DBOpenHelper.Profs.COLUMN_END + " > CURRENT_TIMESTAMP",
                        null, null, null, DBOpenHelper.Profs.COLUMN_BEGIN);
        try {
            if (cursor != null && cursor.getCount() > 0) {
                try {
                    while (cursor.moveToNext()) {
                        TeachersAbsence absence = new TeachersAbsence(
                                cursor.getString(cursor.getColumnIndex(DBOpenHelper.Profs.COLUMN_TITLE)),
                                cursor.getString(cursor.getColumnIndex(DBOpenHelper.Profs.COLUMN_NAME)),
                                cursor.getString(cursor.getColumnIndex(DBOpenHelper.Profs.COLUMN_BEGIN)),
                                cursor.getString(cursor.getColumnIndex(DBOpenHelper.Profs.COLUMN_END)));
                        results.add(absence);
                    }
                } finally {
                    cursor.close();
                }
            }
        } catch (NullPointerException ignored) {}
        return results;
    }

    /**
     * Retrieves all posts.
     * @return {@link ArrayList Array} containing all posts.
     * @see DBOpenHelper#POSTS_TABLE
     */
    ArrayList<Post> getPosts() {
        ArrayList<Post> results = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT _id, name, substr(content, 0, 101), date, picture, categories FROM posts WHERE deleted != 1 LIMIT 100", null);

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

    /**
     * Gets categories from {@link DBOpenHelper#GEN_TABLE} and parsing it (JSON format).
     * @return  {@link ArrayList Array} containing all categories
     */
    ArrayList<String> getCategories() {
        ArrayList<String> results = new ArrayList<>();
        Cursor cursor = database.query(DBOpenHelper.General.TABLE_NAME, new String[] {DBOpenHelper.General.COLUMN_VALUE}, DBOpenHelper.General.COLUMN_NAME + " = ?", new String[] {"categories"}, null, null, null);
        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            try {
                JSONObject jObject = new JSONObject(cursor.getString(cursor.getColumnIndex(DBOpenHelper.General.COLUMN_VALUE)));
                Iterator<?> keys = jObject.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    if (jObject.get(key) instanceof String) {
                        results.add(jObject.getString(key));
                    }
                }
            } catch (JSONException ignored) {
            }
        }
        try {
            cursor.close();
        } catch (NullPointerException ignored) {}
        return results;
    }

    ArrayList<Event> getEvents() {
        ArrayList<Event> toReturn = new ArrayList<>();
        Cursor cursor = database.query(DBOpenHelper.Events.TABLE_NAME, new String[] { DBOpenHelper.Events.COLUMN_ID, DBOpenHelper.Events.COLUMN_NAME, DBOpenHelper.Events.COLUMN_DESC, DBOpenHelper.Events.COLUMN_START, DBOpenHelper.Events.COLUMN_END, DBOpenHelper.Events.COLUMN_PICT, DBOpenHelper.Events.COLUMN_PLACE}, DBOpenHelper.Events.COLUMN_STATE + " = ?", new String[]{"published"}, null, null, DBOpenHelper.Events.COLUMN_START+" DESC");
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Event event = new Event(cursor.getString(cursor.getColumnIndex(DBOpenHelper.Events.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(DBOpenHelper.Events.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(DBOpenHelper.Events.COLUMN_DESC)),
                        cursor.getString(cursor.getColumnIndex(DBOpenHelper.Events.COLUMN_PLACE)),
                        cursor.getString(cursor.getColumnIndex(DBOpenHelper.Events.COLUMN_PICT)),
                        cursor.getString(cursor.getColumnIndex(DBOpenHelper.Events.COLUMN_START)),
                        cursor.getString(cursor.getColumnIndex(DBOpenHelper.Events.COLUMN_END)));
                toReturn.add(event);
                //Log.i("EVENT", event.getPlace());
            }
        }
        try {
            cursor.close();
        } catch (NullPointerException ignored) {}
        //Log.i("START", String.valueOf(toReturn.size()));
        return toReturn;
    }

    @SuppressLint("Recycle")
    CursorAdapter getSuggestions() {
        Cursor cursor;
        cursor = database.query(DBOpenHelper.Maps.TABLE_NAME, new String[]{DBOpenHelper.Maps.COLUMN_ID, DBOpenHelper.Maps.COLUMN_DPNAME, DBOpenHelper.Maps.COLUMN_NAME}, null, null, null, null, null);
        return new SimpleCursorAdapter(ctx, android.R.layout.simple_list_item_1, cursor, new String[]{DBOpenHelper.Maps.COLUMN_DPNAME}, new int[]{android.R.id.text1}, 0);
    }

    /**
     * Retrieves post datas for displaying in {@link PostsFragment}
     * @param id    the corresponding id in database for the post
     * @return  a Post object, containing main information and short description
     * @see Post
     * @see PostsLoader#loadInBackground()
     * @see RecyclerViewAdapterPosts#onBindViewHolder(RecyclerView.ViewHolder, int)
     */
    Post getPost(String id) {
        Cursor cursor = database.query(DBOpenHelper.Posts.TABLE_NAME,
                new String[] {DBOpenHelper.Posts.COLUMN_NAME, DBOpenHelper.Posts.COLUMN_CONTENT, DBOpenHelper.Posts.COLUMN_DATE, DBOpenHelper.Posts.COLUMN_PIC, DBOpenHelper.Posts.COLUMN_CAT},
                DBOpenHelper.Posts.COLUMN_ID + " = " + id,
                null, null, null, null);
        try {
            if (cursor != null && cursor.getCount() > 0) {
                try {
                    cursor.moveToFirst();
                    return new Post(id,
                            cursor.getString(cursor.getColumnIndex(DBOpenHelper.Posts.COLUMN_NAME)),
                            cursor.getString(cursor.getColumnIndex(DBOpenHelper.Posts.COLUMN_CONTENT)),
                            cursor.getString(cursor.getColumnIndex(DBOpenHelper.Posts.COLUMN_PIC)),
                            cursor.getString(cursor.getColumnIndex(DBOpenHelper.Posts.COLUMN_DATE)),
                            cursor.getString(cursor.getColumnIndex(DBOpenHelper.Posts.COLUMN_CAT)));
                } finally {
                    cursor.close();
                }
            } else {
                return null;
            }
        } catch (NullPointerException e ){
            return null;
        }
    }

    Event getEvent(String id) {
        Cursor cursor = database.query(DBOpenHelper.Events.TABLE_NAME,
                new String[]{DBOpenHelper.Events.COLUMN_ID, DBOpenHelper.Events.COLUMN_NAME, DBOpenHelper.Events.COLUMN_DESC, DBOpenHelper.Events.COLUMN_PLACE, DBOpenHelper.Events.COLUMN_START, DBOpenHelper.Events.COLUMN_END, DBOpenHelper.Events.COLUMN_PICT},
                DBOpenHelper.Events.COLUMN_ID + " = " + id,
                null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                return new Event(
                        cursor.getString(cursor.getColumnIndex(DBOpenHelper.Events.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(DBOpenHelper.Events.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(DBOpenHelper.Events.COLUMN_DESC)),
                        cursor.getString(cursor.getColumnIndex(DBOpenHelper.Events.COLUMN_PLACE)),
                        cursor.getString(cursor.getColumnIndex(DBOpenHelper.Events.COLUMN_PICT)),
                        cursor.getString(cursor.getColumnIndex(DBOpenHelper.Events.COLUMN_START)),
                        cursor.getString(cursor.getColumnIndex(DBOpenHelper.Events.COLUMN_END)));
            } finally {
                cursor.close();
            }

        } else {
            return null;
        }
    }

    Cursor getPlace(long id) {
        Cursor cursor = database.query(DBOpenHelper.Maps.TABLE_NAME,
                new String[] {DBOpenHelper.Maps.COLUMN_DPNAME, DBOpenHelper.Maps.COLUMN_FILE, DBOpenHelper.Maps.COLUMN_DESC, DBOpenHelper.Maps.COLUMN_POS, DBOpenHelper.Maps.COLUMN_MARK},
                DBOpenHelper.Maps.COLUMN_ID + " = " + String.valueOf(id),
                null, null, null, null);
       // Log.i("DB", String.valueOf(id));
       // Log.i("DB", String.valueOf(cursor.getCount()));
            if (cursor != null && cursor.getCount() > 0) {
                return cursor;
            } else {
                return null;
            }
    }

    Cursor getQuery(String table, String[] columns, String clause) {
        return database.query(table, columns, clause, null, null, null, null);
    }

    long getPlaceId(String name) {
       Cursor cursor = database.query(DBOpenHelper.Maps.TABLE_NAME, new String[]{DBOpenHelper.Maps.COLUMN_ID, DBOpenHelper.Maps.COLUMN_NAME},  DBOpenHelper.Maps.COLUMN_NAME + " = \""+ name+"\"", null, null, null, null);
        if (cursor != null && cursor.getCount() >0 ) {
            cursor.moveToFirst();
            try {
                return cursor.getLong(0);
            } finally {
                cursor.close();
            }
        } else {
            return 0L;
        }
    }

    /**
     * Static method that parses a JSON String and returning it into an ArrayList
     * @param categories    the JSON String
     * @return  the corresponding ArrayList
     *
     */
    static ArrayList<String> parseCategories(String categories) {
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

    static ArrayList<Integer> parsePrefNot(String key, Context ctx) {
        ArrayList<Integer> toReturn;
        Gson gson = new Gson();
        Type founder = new TypeToken<ArrayList<Integer>>(){}.getType();
        SharedPreferences pref = ctx.getSharedPreferences("notifications",0);
        toReturn = gson.fromJson(pref.getString(key, ""), founder);
        if (toReturn == null) toReturn = new ArrayList<>();
        Log.i("DATABASE", "ACTION DEMANDED : "+toReturn.toString());
        return toReturn;
    }

    static void updatePrefValue(String key, ArrayList<Integer> array, Context ctx) {
        Gson gson = new Gson();
        SharedPreferences pref = ctx.getSharedPreferences("notifications",0);
        SharedPreferences.Editor editor=  pref.edit();
        editor.putString(key, gson.toJson(array));
        //Log.i("TESTTTT", String.valueOf(pref.getInt(key,0)-1));
        editor.putInt(key+"_count", array.size());
        editor.apply();
    }
    boolean endingSync() {
        Log.i("SYNC", "Ending sync function called");
        if (events.size() == 0 && posts.size() == 0 && !maps & !cvl & !canteen) {

            return false;
        } else {
            Gson gson = new Gson();
            SharedPreferences pref = ctx.getSharedPreferences("notifications", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("posts_count", posts.size());
            editor.putInt("events_count", events.size());
            editor.putBoolean("cvl", cvl);
            editor.putBoolean("maps", maps);
            editor.putBoolean("canteen", canteen);
            editor.putString("events", gson.toJson(events));
            editor.putString("posts", gson.toJson(posts));
            Log.i("SYNC", gson.toJson(events));
            editor.apply();
            String content = "";
            if (posts.size() > 0){
                content += "- <b>"+String.valueOf(posts.size())+"</b> "+ctx.getString(R.string.new_posts)+"<br/>";
            }
            if (events.size() > 0) {
                content += "- <strong>"+String.valueOf(events.size())+"</strong> "+ctx.getString(R.string.new_events)+"<br/>";
            }
            if (canteen) {
                content += "- "+ ctx.getString(R.string.new_canteen) + "<br/>";
            }
            if (maps) {
                content += "- " +ctx.getString(R.string.new_maps) +"<br/>";
            }
            if (cvl) {
                content +="- "+ctx.getString(R.string.new_cvl)+"<br/>";
            }
            content = content.substring(0, content.length()-5);
            NotificationManager manager = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
            Notification.Builder noti;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel chanell = new NotificationChannel("channel1", "Coucou", NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(chanell);
                noti = new Notification.Builder(ctx, "channel1");
            } else {
                noti = new Notification.Builder(ctx);
            }
            noti.setContentTitle(ctx.getString(R.string.new_content_toread))
                    .setAutoCancel(true)
                    .setContentText(ctx.getString(R.string.drop_to_see))
                    .setShowWhen(true)
                    .setSmallIcon(R.drawable.ic_launcher_material)
                    .setStyle(new Notification.BigTextStyle().bigText(Html.fromHtml(content)));
            noti.setAutoCancel(true);

            Intent newIntent = new Intent(ctx, SplashActivity.class);
            android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(ctx);
            stackBuilder.addNextIntentWithParentStack(newIntent);
            PendingIntent intent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            noti.setContentIntent(intent);
            manager.notify(3, noti.build());
            if (pref.getBoolean("maps", false)){
                Log.i("DATA", "WORKEDDDDD");
            } else {
                Log.i("DATA", "NOT WORKEDDDDD");
            }
            return true;
        }
    }
}

