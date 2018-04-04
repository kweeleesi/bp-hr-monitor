package com.example.hp.heartrytcare.service;

import android.util.Log;

import com.example.hp.heartrytcare.helper.Constants;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseInstanceImpl extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseInstanceImpl";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(Constants.FIREBASE_UID, refreshedToken);
    }

    private void sendRegistrationToServer(String uID, String refreshedToken) {

    }
}
