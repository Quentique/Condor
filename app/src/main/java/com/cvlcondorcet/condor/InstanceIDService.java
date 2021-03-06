package com.cvlcondorcet.condor;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * Subscribe to the topic to receive new-content notification
 * @author Quentin DE MUYNCK
 * @see MainActivity
 */

public class InstanceIDService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
            subscribeTopic(token);
    }
    private void subscribeTopic(String token) {
       FirebaseMessaging.getInstance().subscribeToTopic(MainActivity.TOPIC_ID);
    }
}
