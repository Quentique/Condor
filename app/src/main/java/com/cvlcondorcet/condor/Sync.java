package com.cvlcondorcet.condor;

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

    private static String base_URL = "http://10.0.2.2:81/";
    private static String GEN_URL = "read.php";
    private static String POSTS_URL = "posts.php";
    private static String PROFS_URL = "profs.php";

    private Database db = new Database(this);
    private final Handler handler = new Handler();

    public Sync() {
        super("Sync");
    }

    @Override
    public void onHandleIntent(Intent i)
    {
        int icon = R.mipmap.ic_launcher;
        CharSequence tickerText = getString(R.string.sync_notif_name);
        long when = System.currentTimeMillis();
        NotificationManager manager;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder noti;
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

        startForeground(1, noti.build());

        try {
            db.open();
                noti.setProgress(100, 5, false);
                manager.notify(1, noti.build());

            JSONArray gen = get(GEN_URL);
            Log.i("SYNC", "GENERAL SYNC");
            ArrayList liste;
            liste = db.updateGen(gen);
            for (int j = 0 ; j < liste.size() ; j++ ) {
                downloadFile(liste.get(j).toString());
                Log.i("SYNC", "DOWNLOADING FILE");
            }
                noti.setProgress(100, 30, false);
                manager.notify(1, noti.build());
            JSONArray posts = get(POSTS_URL);
            Log.i("SYNC", "POSTS SYNC");
            db.updatePosts(posts);
                noti.setProgress(100, 75, false);
                manager.notify(1, noti.build());
            JSONArray profs = get(PROFS_URL);
            Log.i("SYNC", "PROFS SYNC");
            db.updateProfs(profs);
            db.beginSync();
            Log.i("SYNC", "END SYNC");
                noti.setProgress(100, 100, false);
                noti.setContentText(getString(R.string.sync_end));
                manager.notify(1, noti.build());
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        if (Build.VERSION.SDK_INT >= 26) { noti.setTimeoutAfter(20000); }
        noti.setOngoing(false);
        noti.setAutoCancel(true);
        noti.setTicker(getString(R.string.end_sync_ticker));
        manager.notify(2, noti.build());
        //manager.cancel(1);
        stopSelf();
    }

    private JSONArray get(String content) {
        String answer = "";
        URL url = null;
        JSONArray tab = new JSONArray();

        try{
            String machin;
            if (content == GEN_URL) {machin = db.timestamp("timestamp"); } else { machin = db.timestamp("last_sync"); }
            url = new URL(base_URL + content + "?timestamp=" + machin);
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
            e.printStackTrace();
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
            URL url = new URL(base_URL + file);
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
}
