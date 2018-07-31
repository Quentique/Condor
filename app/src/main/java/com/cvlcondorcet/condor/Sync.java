package com.cvlcondorcet.condor;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Background service, used for syncing internal database of the application.
 *
 * @author Quentin DE MUYNCK
 *
 */

public class Sync extends IntentService {

    public final static String broadcast_URI = "com.cvlcondorcet.condor.broadcast.progress";

    private static final String base_URL = "***REMOVED***";
    private static final String uploads = "***REMOVED***";
    private static final String check_URL = "***REMOVED***";
    private static final String GEN_URL = "***REMOVED***";
    private static final String POSTS_URL = "***REMOVED***";
    private static final String PROFS_URL = "***REMOVED***";
    private static final String MAPS_URL = "***REMOVED***";
    private static final String EVENTS_URL = "***REMOVED***";
    private static final String KEY = "?q=***REMOVED***";

    public static String rssURL;

    private boolean networkError = false;

    private final Database db = new Database(this);
    private final Handler handler = new Handler();
    private NotificationManager manager;
    private Notification.Builder noti;

    private Intent intent;

    private int progress;
    private String progressMessage;

    private final Runnable sendProgress = new Runnable() {
        @Override
        public void run() {
            displayProgress();
            handler.postDelayed(this, 2000);
        }
    };

    /**
     * Default constructor.
     */
    public Sync() {
        super("Sync");
        Log.i("EEEE", "SERVICE CONSTRUCTED");
    }

    /**
     * Creating service, making it operational, affecting variables values.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(broadcast_URI);
        Log.i("EEEE", "SERVICE CONSTRUCTED");
    }

    /**
     * Starting service, setting broadcaster up.
     * @param intent    the intent containing the broadcaster Uri
     * @param startId   useless id
     */
    @Override
    public void onStart(Intent intent, int startId) {
        handler.removeCallbacks(sendProgress);
        handler.postDelayed(sendProgress, 500);
        Log.i("EEEE", "SERVICE CONSTRUCTED");
        super.onStart(intent, startId);
    }

    /**
     * Destroying service, removing priorities and callbacks.
     */
    @Override
    public void onDestroy() {
        handler.removeCallbacks(sendProgress);
        stopForeground(true);
        super.onDestroy();

    }

    /**
     * Making synchronization, building notification, checking if the server is okay, then
     * downloading JSON data and passing them to {@link Database Database} for handling.
     * @param i not used
     */
    @Override
    public void onHandleIntent(Intent i)
    {
        String act = "none";
        if (i.getExtras() != null && i.getExtras().containsKey("from")) {
            act = i.getStringExtra("from");
        }
        progressMessage = "Syncing...";
        //Log.i("NOTI", "DDDDDONE");
        CharSequence tickerText = getString(R.string.sync_notif_name);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chanell = new NotificationChannel("channel1", "Coucou", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(chanell);
            noti = new Notification.Builder(this, "channel1");
        } else {
            noti = new Notification.Builder(this);
        }
        Intent newIntent = new Intent(this, MainActivity.class);
        newIntent.putExtra("fragment", "sync");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(newIntent);
        // PendingIntent intent = PendingIntent.getActivity(this, 1, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent intent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        noti.setContentTitle(getResources().getString(R.string.sync))
                .setContentText(tickerText)
                .setSmallIcon(R.drawable.ic_launcher_material)
                .setOngoing(true)
                .setContentIntent(intent)
                .setTicker(getString(R.string.sync_start_ticker));
        if (Build.VERSION.SDK_INT >= 21) { noti.setVisibility(Notification.VISIBILITY_PUBLIC); }

        noti.setProgress(0, 0, true);
       // Log.i("NOTI", "DONE");

        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        Bundle params = new Bundle();
        startForeground(5, noti.build());

        String continueSync = serverState();
        if (continueSync.contains("200")) {
            networkError = false;
            params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "success");
        } else {
            params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "fail");
            progress = -1;
            progressMessage = continueSync;
           // Log.i("TESTSYNC", progressMessage);
            displayProgress();
            handler.removeCallbacks(sendProgress);
            networkError = true;
            displayProgress();
        }
        analytics.logEvent("performed_sync", params);
        if (!networkError) {
            try {
                db.open();
                db.initialiseSync();
               // Log.i("SYNC", "GENERAL SYNC");
                JSONArray maps = get(MAPS_URL);
                db.updateMaps(maps);
                JSONArray gen = get(GEN_URL);

                ArrayList liste;
                liste = db.updateGen(gen);
                changeProgress(20, "Downloading files...");
                for (int j = 0; j < liste.size(); j++) {
                    progress += 20 / liste.size();
                    changeProgress(progress, "Downloading file " + j + "/" + liste.size());
                    downloadFile(liste.get(j).toString());
                   // Log.i("SYNC", "DOWNLOADING FILE");
                }
                changeProgress(50, "News....");
                rssURL = db.timestamp("website") + "feed";
                JSONArray posts = get(POSTS_URL);
               // Log.i("SYNC", "POSTS SYNC");
                db.updatePosts(posts);
                changeProgress(70, "Events....");
                JSONArray events = get(EVENTS_URL);
                db.updateEvents(events);
               /* JSONArray profs = get(PROFS_URL);
               // Log.i("SYNC", "PROFS SYNC");*/
                changeProgress(90, "Ending sync...");
              //  db.updateProfs(profs);
                db.beginSync();
              //  Log.i("SYNC", "END SYNC");
                progressMessage = "Sync ended.";
                progress = 100;
                noti.setProgress(100, 100, false);
                noti.setContentText(getString(R.string.sync_end));
                manager.notify(5, noti.build());
                Log.i("SYNC", "ABOUT TO CALL CONFIGURATION");

                if (db.endingSync() && act.equals("activity")) {
                    Log.i("SYNC", "CALLED CONFIGURATION");
                    Intent restart = new Intent(this, MainActivity.class);
                    restart.putExtra("fragment", "nav");
                    startActivity(restart);
                }
                db.close();
            } catch(SQLException e){
                progress = -2;
                Crashlytics.logException(e);
            }

            noti.setOngoing(false);
            noti.setAutoCancel(true);
            noti.setTicker(getString(R.string.end_sync_ticker));
            manager.notify(2, noti.build());
            //manager.cancel(1);
            handler.removeCallbacks(sendProgress);
            displayProgress();
            if (Build.VERSION.SDK_INT >= 26) {
                noti.setTimeoutAfter(20000);
            } else {
                manager.cancel(2);
            }
        }
        if(db != null && db.isOpen()) {
            db.close();
        }
        stopSelf();
    }

    /**
     * Retrieving JSON data from server and translating it into {@link JSONArray JSONArray}.
     * @param content   the url used to download
     * @return          {@link JSONArray JSONArray} containing the requested values
     */
    private JSONArray get(String content) {
        StringBuilder answer = new StringBuilder();
        URL url = null;
        JSONArray tab = new JSONArray();
        try {

            try {
                String machin;
                switch (content) {
                    case GEN_URL:
                        machin = db.timestamp("timestamp");
                        break;
                    case MAPS_URL:
                        machin = db.timestamp("maps_change");
                        break;
                    default:
                        machin = db.timestamp("last_sync");
                        break;
                }
                machin = machin.replaceAll("\\s", "T");
                Log.i("CONDOR", machin);
                url = new URL(base_URL + content + KEY + "&timestamp=" + machin);
                Log.i("TEST", url.toString());
            } catch (MalformedURLException e) {
                Crashlytics.logException(e);
            }

            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
              //  Log.i("NETWORK ERROR", hello);
            } catch (IOException e) {
                Crashlytics.logException(e);
                progress = -1;
                progressMessage = "An network error has occurred while syncing. Please try again later.";
                handler.post(sendProgress);
                stopForeground(true);
                networkError = true;
            }
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    answer.append(inputLine);
                in.close();
            } catch (IOException e) {
                Crashlytics.logException(e);
            } finally {
                connection.disconnect();
            }
           // Log.i("E", answer);
            try {
                tab = new JSONArray(answer.toString());
            } catch (JSONException e) {
                Crashlytics.logException(e);
            }
        } catch (NullPointerException ignored) {
            Crashlytics.logException(ignored);
        }
        return tab;
    }

    /**
     * Downloads the file given in parameter.
     * @param file  the file name that must be downloaded
     */
    private void downloadFile(String file) {
        try {
            URL url = new URL(base_URL + uploads + file);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.connect();

            InputStream input = new BufferedInputStream(connection.getInputStream());
            FileOutputStream output = openFileOutput(file, MODE_PRIVATE);

            byte data[] = new byte[1024];
            int count;

            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
          //  Log.i("EBUG", "File downloaded " + file);
        } catch (IOException e) {
            Crashlytics.logException(e);
        }
    }

    /**
     * Retrieving the server state, in order to know if sync can continue.
     * @return  server response to check file
     */
    private String serverState() {
        URL url = null;
        StringBuilder answer = new StringBuilder();
        try{
            url = new URL(base_URL + check_URL + KEY);
        } catch (MalformedURLException e) {
            Crashlytics.logException(e);
        }
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(20000);
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    answer.append(inputLine);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
                progress = -1;
                handler.post(sendProgress);
                stopForeground(true);
                networkError = true;
                answer = new StringBuilder("Server is unreachable, please try again later.");
            } finally {
                connection.disconnect();
            }
        } catch (ConnectException e) {
            progress = -1;
            handler.post(sendProgress);
            stopForeground(true);
            networkError = true;
            answer = new StringBuilder("Server is unreachable, please try again later.");
        } catch (IOException e) {
            progress = -1;
            handler.post(sendProgress);
            stopForeground(true);
            networkError = true;
            Crashlytics.logException(e);
            answer = new StringBuilder("I/O Error");
        }
        return answer.toString();
    }

    /**
     * Displaying progress in the service notification.
     * @param newProgress   the new progress given by the service
     */
    private void changeProgress(int newProgress, String newProgressMessage) {
        progress = newProgress;
        progressMessage = newProgressMessage;
        noti.setProgress(100, newProgress, false);
        manager.notify(5, noti.build());
    }

    /**
     * Sending progress through broadcaster
     */
    private void displayProgress() {
        intent.putExtra("progress", progress);
        intent.putExtra("progressMessage", progressMessage);
        sendBroadcast(intent);
        if (progress == -1) {
            noti.setContentTitle(getResources().getString(R.string.error))
                    .setContentText(Jsoup.parse(progressMessage).text())
                    .setSmallIcon(R.drawable.ic_launcher_material)
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .setProgress(0, 0, false);
            manager.notify(10, noti.build());
        }
    }
}
