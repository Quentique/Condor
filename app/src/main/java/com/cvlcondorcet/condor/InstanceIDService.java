package com.cvlcondorcet.condor;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Registers token & subscribe to the topic to receive new-content notification
 * @author Quentin DE MUYNCK
 * @see MainActivity
 */

public class InstanceIDService extends FirebaseInstanceIdService {
    public void onTokenRefresh() {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
           // Log.d("TEST", "Refreshed token: " + refreshedToken);
            subscribeTopic(refreshedToken);
    }
    private void subscribeTopic(String token) {
       FirebaseMessaging.getInstance().subscribeToTopic("condor641951236");

        //Log.i("TEST", "SUBSCRIBED TOPIC");
    }
}
