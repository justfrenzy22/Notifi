package com.notify;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

public class FCMHelper {

    private static String fcmToken = "";

    public static void fetchFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("FCMHelper", "Fetching FCM registration token failed", task.getException());
                return;
            }
            fcmToken = task.getResult();
            Log.d("FCMHelper", "FCM Token: " + fcmToken);
        });
    }
    static String getFCMToken() {
        Log.d("FCMHelper", "FCM Token in getFCMToken: " + fcmToken);
        return fcmToken;
    }

}
