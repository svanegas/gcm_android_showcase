package com.example.gcm.gcmexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    private static final String TAG = MyInstanceIDListenerService.class.getSimpleName();
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            passTokenToServer(refreshedToken);
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();
        } catch (IOException e) {
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
            e.printStackTrace();
        }
        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void passTokenToServer(String token) throws IOException {
        // TODO: Send the token to the server.
    }
}
