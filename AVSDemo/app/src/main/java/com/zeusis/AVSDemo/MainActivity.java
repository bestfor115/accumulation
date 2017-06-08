package com.zeusis.AVSDemo;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.amazon.alexa.api.AlexaServices;
import com.amazon.alexa.api.AlexaServicesConnection;
import com.amazon.alexa.api.AlexaState;
import com.amazon.alexa.api.AlexaStateListener;
/**  * This sample activity intends to show the minimum amount of code required to get a working client
 *  * of AlexaServices which can recognize user speech, and display Alexa state through a single
 *  * button. It also assumes that the application is given permission to use the microphone manually
 *  * through Android Settings.  */
public class MainActivity extends AppCompatActivity  implements AlexaServicesConnection.ConnectionListener, AlexaStateListener {
    /**
     * The text for the button when it is ready to recognize.
     */
    private static final String RECOGNIZE_TEXT = "LISTEN";
    /**
     * The key name representing the logged in status of the user
     */
    private static final String LOGGED_IN = "loggedIn";
    /**
     * The action associated with the intent passed to the service. Actions are required for  * intent extras to be persisted.
     */
    private static final String LOGIN_ACTION = "LOGIN";
    /**
     * Simply a value to track asking for permission to record.
     */
    private static final int REQUEST_MICROPHONE = 1;
    /**
     * The AlexaServicesConnection to use when calling AlexaServices APIs.
     */
    private AlexaServicesConnection alexaServicesConnection;
    /**
     * The button to interact with Alexa.
     */
    private Button recognizeButton;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Always create a single alexaServicesConnection during start up of the application.
        alexaServicesConnection = new AlexaServicesConnection(this);
        // Create a user interface for the user to use.
        RelativeLayout layout = new RelativeLayout(this);
        recognizeButton = new Button(this);
        recognizeButton.setText(RECOGNIZE_TEXT);
        // Start recognizing user speech when clicked.
        recognizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!checkPermission(Manifest.permission.RECORD_AUDIO)) {
                    // Ensure we have permission to record for the sample
                    requestPermissions();
                } else {
                    // Start recognizing user speech
                    AlexaServices.Recognize.start(alexaServicesConnection);
                }
            }
        });
        RelativeLayout.LayoutParams centeringParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        centeringParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        layout.addView(recognizeButton, centeringParams);
        this.setContentView(layout);
    }

    /**
     * Always connect the AlexaServicesConnection in onStart. *
     * Version 1.0|19May AmazonConfidential-- s o License Terms
     * Amazon Alexa Voice Service Android Library Quick Start Guide
     * Ensure all AlexaServicesConnection.ConnectionListeners are registered before  * connecting. Listeners must be registered to know when AlexaServices APIs can be called,  * as the AlexaServicesConnection is not guaranteed to be connected immediately  * after AlexaServicesConnection#connect(PendingIntent) is called.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (!checkPermission(Manifest.permission.RECORD_AUDIO)) {
            requestPermissions();
        }
        Intent successCallback = new Intent(this, MainActivity.class);
        successCallback.setAction(LOGIN_ACTION);
        successCallback.putExtra(LOGGED_IN, true);
        Intent failureCallback = new Intent(this, MainActivity.class);
        failureCallback.setAction(LOGIN_ACTION);
        failureCallback.putExtra(LOGGED_IN, false);
        PendingIntent loggedInCallback = PendingIntent.getActivity(this, 1, successCallback, 0);
        PendingIntent loggedOutCallback = PendingIntent.getActivity(this, 2, failureCallback, 0);
        alexaServicesConnection.registerListener(this);
        alexaServicesConnection.connect(loggedInCallback, loggedOutCallback);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        boolean isLoggedIn = intent.getBooleanExtra(LOGGED_IN, false);
    }

    /**
     * Always disconnect the AlexaServicesConnection in onStop.
     */
    @Override
    protected void onStop() {
        super.onStop();
        alexaServicesConnection.disconnect();
        alexaServicesConnection.deregisterListener(this);
    }

    /**
     * The AlexaServicesConnection is connected, and AlexaServices APIs can now be used. This is the  * best place to register listeners, such as the AlexaStateListener for AlexaState changes,  * which is most important interface to implement as an AVS Android Library client.
     */
    @Override
    public void onConnected() {
        AlexaServices.Recognize.registerListener(alexaServicesConnection, this);
    }

    /**
     * The AlexaServicesConnection is disconnecting. This is the ideal place to deregister listeners.
     */
    @Override
    public void onDisconnected() {
        AlexaServices.Recognize.deregisterListener(alexaServicesConnection, this);
    }

    /**
     * The AlexaState has changed to a new value, most likely due to having a dialog with a user.  * Each state has a specific requirement on what actions the user is capable of taking. The  * implementation here is the simplest correct implementation, but does not take advantage  * of all possible interactions (such as allowing the user to 'barge in' on Alexa when the state  * is AlexaState.SPEAKING). For a full description of behaviors visit:  *  * https://developer.amazon.com/public/solutions/alexa/alexa-voice- service/content/alexa-voice-service-ux-design-guidelines#understand
     */
    @Override
    public void onAlexaStateChanged(final AlexaState alexaState) {
        if (alexaState == AlexaState.IDLE) {
            recognizeButton.setText(RECOGNIZE_TEXT);
            recognizeButton.setEnabled(true);
        } else {
            recognizeButton.setText(alexaState.toString());
            recognizeButton.setEnabled(false);
        }
    }

    /// Helpers for getting recording permissions
    private boolean checkPermission(final String permission) {
        return ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_MICROPHONE);
    }
}