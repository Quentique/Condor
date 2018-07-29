package com.cvlcondorcet.condor;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * Registers token & subscribe to the topic to receive new-content notification
 * @author Quentin DE MUYNCK
 * @see MainActivity
 */

public class InstanceIDService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
           // Log.d("TEST", "Refreshed token: " + refreshedToken);
            subscribeTopic(token);
    }
    private void subscribeTopic(String token) {
       FirebaseMessaging.getInstance().subscribeToTopic("condor541951236");

        //Log.i("TEST", "SUBSCRIBED TOPIC");
    }
}
