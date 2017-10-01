package com.cvlcondorcet.condor;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

/**
 * Registers token & subscribe to the topic to receive new-content notification
 * @author Quentin DE MUYNCK
 * @see MainActivity
 */

public class InstanceIDService extends FirebaseInstanceIdService {
    public void onTokenRefresh() {
        /*Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);*/
        try {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d("TEST", "Refreshed token: " + refreshedToken);
            // TODO: Implement this method to send any registration to your app's servers.
            subscribeTopic(refreshedToken);
        } catch(IOException e) {}
    }
    private void subscribeTopic(String token) throws IOException {
        FirebaseMessaging.getInstance().subscribeToTopic("condor541951236");

        Log.i("TEST", "SUBSCRIBED TOPIC");
    }
}
