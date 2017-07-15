package com.cvlcondorcet.condor;

import android.app.IntentService;
import android.content.Intent;
import android.database.SQLException;
import android.util.Log;
import android.widget.Toast;

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

/**
 * Created by Quentin DE MUYNCK on 12/07/2017.
 */

public class Sync extends IntentService {

    private static String base_URL = "http://10.0.2.2:81/";
    private static String GEN_URL = "read.php";
    private static String POSTS_URL = "posts.php";
    private static String PROFS_URL = "profs.php";

    private Database db = new Database(this);

    public Sync() {
        super("Sync");
    }

    @Override
    public void onHandleIntent(Intent i)
    {
        try {
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JSONArray gen = get(GEN_URL);
        ArrayList liste;
        liste = db.updateGen(gen);
        for (int j = 0 ; j < liste.size() ; j++ ) {
            downloadFile(liste.get(j).toString());
        }

        JSONArray posts = get(POSTS_URL);
        db.updatePosts(posts);

        JSONArray profs = get(PROFS_URL);
        db.updateProfs(profs);
        db.beginSync();
        db.close();
        /*int icon = R.mipmap.ic_launcher;
        // Le premier titre affiché
        CharSequence tickerText = "Titre de la notification";
        // Daté de maintenant
        long when = System.currentTimeMillis();

        Notification.Builder noti = new Notification.Builder(this)
                .setContentTitle("New mail from ")
                .setContentText(tickerText)
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager manager;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel chanell = new NotificationChannel("channel1", "Coucou", 1);
        manager.createNotificationChannel(chanell);

        noti.setChannelId("channel1");
        manager.notify(001, noti.build());
       // startForeground(1, noti);

        Log.i("HELLO", "PITE");*/
        Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_LONG).show();
        stopSelf();
    }

    private JSONArray get(String content) {
        String answer = "";
        URL url = null;
        JSONArray tab = new JSONArray();
        try{
            String machin = "";
            if (content == GEN_URL) {machin = db.timestamp("timestamp"); } else { machin = db.timestamp("last_sync"); }
            url = new URL(base_URL + content + "?timestamp=" + machin);
        }catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
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
        try {
            tab = new JSONArray(answer);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return tab;
    }
    private boolean downloadFile(String file) {
        try {
            URL url = new URL(base_URL + file);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.connect();
            // this will be useful so that you can show a typical 0-100% progress bar
            int fileLength = connection.getContentLength();

            // download the file
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
