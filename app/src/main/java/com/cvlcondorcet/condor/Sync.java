package com.cvlcondorcet.condor;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.SQLException;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

/**
 * Created by Quentin DE MUYNCK on 12/07/2017.
 */

public class Sync extends IntentService {

    public  static String broadcast_URI = "com.cvlcondorcet.condor.broadcast.progress";

    private static String base_URL = "http://10.0.2.2:81/condor/";
    private static String uploads = "***REMOVED***";
    private static String check_URL = "***REMOVED***";
    private static String GEN_URL = "***REMOVED***";
    private static String POSTS_URL = "***REMOVED***";
    private static String PROFS_URL = "***REMOVED***";
    private static String KEY = "?q=***REMOVED***";

    public static String rssURL;

    private boolean networkError = false;

    private Database db = new Database(this);
    private final Handler handler = new Handler();
    private NotificationManager manager;
    private Notification.Builder noti;

    Intent intent;

    private int progress;
    private String progressMessage;

    private Runnable sendProgress = new Runnable() {
        @Override
        public void run() {
            displayProgress();
            handler.postDelayed(this, 2000);
        }
    };

    public Sync() {
        super("Sync");
        Log.i("EEEE", "SERVICE CONSTRUCTED");
    }

   @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(broadcast_URI);
        Log.i("EEEE", "SERVICE CONSTRUCTED");
    }
    @Override
    public void onStart(Intent intent, int startId) {
        handler.removeCallbacks(sendProgress);
        handler.postDelayed(sendProgress, 500);
        Log.i("EEEE", "SERVICE CONSTRUCTED");
        super.onStart(intent, startId);
    }
    @Override
    public void onDestroy() {
        handler.removeCallbacks(sendProgress);
        stopForeground(true);
        super.onDestroy();

    }
    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void onHandleIntent(Intent i)
    {
        progressMessage = "Syncing...";
        Log.i("NOTI", "DDDDDONE");
        int icon = R.mipmap.ic_launcher;
        CharSequence tickerText = getString(R.string.sync_notif_name);
        long when = System.currentTimeMillis();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chanell = new NotificationChannel("channel1", "Coucou", 1);
            manager.createNotificationChannel(chanell);
            noti = new Notification.Builder(this, "channel1");
        } else {
            noti = new Notification.Builder(this);
        }
        noti.setContentTitle("Synchronization")
                .setContentText(tickerText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setTicker(getString(R.string.sync_start_ticker));
        if (Build.VERSION.SDK_INT >= 21) { noti.setVisibility(VISIBILITY_PUBLIC); }

        noti.setProgress(0, 0, true);
        Log.i("NOTI", "DONE");

        startForeground(1, noti.build());
        try {
            db.open();
            rssURL = db.timestamp("website") + "feed";
        } catch( SQLException e) { stopSelf(); return; }

            String continueSync = serverState();
            if (continueSync.contains("200")) {
                networkError = false;
            } else {
                progress = -1;
                progressMessage = continueSync;
                displayProgress();
                handler.removeCallbacks(sendProgress);
                networkError = true;
                displayProgress();
            }
            if (!networkError) {
                try {
                JSONArray gen = get(GEN_URL);
                Log.i("SYNC", "GENERAL SYNC");
                ArrayList liste;
                liste = db.updateGen(gen);
                progress = 20;
                progressMessage = "Downloading files...";
                changeProgress(20);
                for (int j = 0; j < liste.size(); j++) {
                    progress += 20 / liste.size();
                    changeProgress(progress);
                    downloadFile(liste.get(j).toString());
                    Log.i("SYNC", "DOWNLOADING FILE");
                }
                    progressMessage = "News...";
                    JSONArray posts = get(POSTS_URL);
                Log.i("SYNC", "POSTS SYNC");
                db.updatePosts(posts);
                progress = 60;
                    progressMessage = "Teacher absences...";
                    changeProgress(progress);
                JSONArray profs = get(PROFS_URL);
                Log.i("SYNC", "PROFS SYNC");
                progress = 80;
                    progressMessage = "Ending sync...";
                changeProgress(progress);
                db.updateProfs(profs);
                db.beginSync();
                Log.i("SYNC", "END SYNC");
                    progressMessage = "Sync ended.";
                    progress = 100;
                noti.setProgress(100, 100, false);
                noti.setContentText(getString(R.string.sync_end));
                manager.notify(1, noti.build());
                db.close();
            } catch(SQLException e){
                progress = -2;
                e.printStackTrace();
            }

            if (Build.VERSION.SDK_INT >= 26) {
                noti.setTimeoutAfter(20000);
            }
            noti.setOngoing(false);
            noti.setAutoCancel(true);
            noti.setTicker(getString(R.string.end_sync_ticker));
            manager.notify(2, noti.build());
            //manager.cancel(1);
                handler.removeCallbacks(sendProgress);
                displayProgress();
        }

        stopSelf();
    }

    private JSONArray get(String content) {
        String answer = "";
        URL url = null;
        JSONArray tab = new JSONArray();

        try{
            String machin;
            if (content == GEN_URL) {machin = db.timestamp("timestamp"); } else { machin = db.timestamp("last_sync"); }
            url = new URL(base_URL + content + KEY + "&timestamp=" + machin);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection connection = null;
        String hello ="";
        try {
            connection = (HttpURLConnection) url.openConnection();
             hello = connection.getResponseMessage();
            Log.i("NETWORK ERROR", hello);
        } catch (IOException e) {
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
                answer += inputLine;
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        Log.i("E", answer);
        try {
            tab = new JSONArray(answer);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tab;
    }
    private boolean downloadFile(String file) {
        try {
            URL url = new URL(base_URL + uploads + file);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.connect();

            InputStream input = new BufferedInputStream(connection.getInputStream());
            FileOutputStream output = openFileOutput(file, MODE_PRIVATE);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
            Log.i("EBUG", "File downloaded " + file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private String serverState() {
        URL url = null;
        String answer = "";
        try{
            url = new URL(base_URL + check_URL + KEY);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection connection = null;
        String hello ="";
        try {
            connection = (HttpURLConnection) url.openConnection();
            hello = connection.getResponseMessage();
        } catch (IOException e) {
            progress = -1;
            handler.post(sendProgress);
            stopForeground(true);
            networkError = true;
            e.printStackTrace();
            answer = "505";
        }
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                answer += inputLine;
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return answer;
    }

    private void changeProgress(int newProgress) {
        noti.setProgress(100, newProgress, false);
        manager.notify(1, noti.build());
    }

   private void displayProgress() {
        intent.putExtra("progress", progress);
        intent.putExtra("progressMessage", progressMessage);
        sendBroadcast(intent);
    }
}
