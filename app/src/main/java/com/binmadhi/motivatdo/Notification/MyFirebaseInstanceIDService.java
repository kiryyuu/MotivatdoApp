package com.binmadhi.motivatdo.Notification;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;


/**
 * @author ton1n8o - antoniocarlos.dev@gmail.com on 6/13/16.
 */
public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    public static final String TOKEN_BROADCAST="myfcmtokenbroadcast";
    private static String TAG = "TOKEN";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        getApplicationContext().sendBroadcast(new Intent(TOKEN_BROADCAST));
      //  storeToken(s);
        Log.e(TAG, "onNewToken: "+s );
    }
/*
    private void storeToken(String refreshedToken) {
        prefs = new PreferencesManager(this);
        currentUser = prefs.getCurrentUser();
        currentUser.setToken(refreshedToken);
        Log.e(TAG, "storeToken: "+refreshedToken );
      }
*/
}
