package com.example.gcm.gcmexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private BroadcastReceiver broadcastReceiver;
    private boolean receiverRegistered;
    private ProgressBar progressBar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /**
         When a push notification with 'notification' field is sent, and the application is in
         background, after the notification is tapped, the custom data sent will be received
         here as extras.
         If the notification does not contain the 'notification' field, the data will be received
         in the onMessageReceived method.
         See table in: https://firebase.google.com/docs/notifications/android/console-audience
         */
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        textView = (TextView) findViewById(R.id.main_message);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                hideProgress(context);
            }
        };

        registerReceiver();

        String currentToken = FirebaseInstanceId.getInstance().getToken();
        if (currentToken != null) {
            Log.d(TAG, "Current token is: " + currentToken);
            hideProgress(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        receiverRegistered = false;
        super.onPause();
    }

    private void hideProgress(Context context) {
        progressBar.setVisibility(ProgressBar.GONE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean tokenSent = sharedPreferences
                .getBoolean(MyInstanceIDListenerService.SENT_TOKEN_TO_SERVER, false);
        if (tokenSent) {
            textView.setText(getString(R.string.gcm_registered));
        } else {
            textView.setText(getString(R.string.gcm_error_registering));
        }
    }

    private void registerReceiver() {
        if (receiverRegistered) return;
        IntentFilter intent = new IntentFilter(MyInstanceIDListenerService.REGISTRATION_COMPLETE);
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(broadcastReceiver, intent);
        receiverRegistered = true;
    }
}
