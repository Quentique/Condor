package com.cvlcondorcet.condor;

import android.app.IntentService;
import android.content.Intent;

import java.io.IOException;

/**
 * Created by Quentin DE MUYNCK on 23/09/2017.
 */

public class RegistrationIntentService extends IntentService {

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {

    }
    private void subscribeTopic(String token) throws IOException {

    }
}
