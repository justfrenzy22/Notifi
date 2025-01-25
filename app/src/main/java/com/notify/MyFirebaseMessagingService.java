package com.notify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New token: " + token);

        sendTokenToServer(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "Message received from: " + remoteMessage);

        if (remoteMessage.getNotification() != null) {

            Boolean isAppForeground = ((MyApplication) getApplication()).IsAppForeground();

            Log.d(TAG, "is App Foreground: " + isAppForeground);

            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            handleNotification(title, body);
        }

        if (!remoteMessage.getData().isEmpty()) {
            Log.d(TAG, "Data Payload: " + remoteMessage.getData());
        }
    }

    private void handleNotification(String title, String body) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "custom_channel_id";
        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm);

        NotificationChannel channel = new NotificationChannel(
                channelId,
                "Custom Channel",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Channel for custom sound notifications");
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        channel.setSound(soundUri, audioAttributes);
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.launcher_icon_round)
                .setAutoCancel(true)
                .setSound(soundUri);

        manager.notify(1, builder.build());
    }

    private NotificationCompat.Builder getNotificationBuilder(String title, String body) {
        String channelId = "custom_channel_id";
        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm);


        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.launcher_icon_round)
                .setAutoCancel(true)
                .setSound(soundUri);
    }

    private void createNotificationChannel (String title, String body) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "custom_channel_id";
            Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm);

            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Custom Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for custom sound notifications");
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(soundUri, audioAttributes);
            manager.createNotificationChannel(channel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.mipmap.launcher_icon_round)
                    .setAutoCancel(true)
                    .setSound(soundUri);

            manager.notify(1, builder.build());
        }
    }

    private void showCustomNotification(String title, String body) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "custom_channel_id";
        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm);

        NotificationChannel channel = new NotificationChannel(
                channelId,
                "Custom Channel",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Channel for custom sound notifications");
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        channel.setSound(soundUri, audioAttributes);
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.launcher_icon_round)
                .setAutoCancel(true)
                .setSound(soundUri);

        manager.notify(0, builder.build());
    }

//    private void showNotification(String title, String body, Boolean isForeground) {
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        String channelId = "default";
//        String channelName = "Default Channel";
//
//        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                    .build();
//
//            NotificationChannel channel = new NotificationChannel(
//                    channelId,
//                    channelName,
//                    NotificationManager.IMPORTANCE_HIGH
//            );
//            channel.setDescription("Channel for custom notifications");
//            channel.setSound(soundUri, audioAttributes);
//            channel.enableVibration(true);
//            manager.createNotificationChannel(channel);
//        }
////
////
////        NotificationChannel channel = new NotificationChannel(
////                channelId,
////                channelName,
////                NotificationManager.IMPORTANCE_HIGH
////        );
////        channel.setDescription("Channel for alarm notifications");
//
//        // Make the notification silent by disabling vibration and sound
////        channel.setSound(null, null);  // Disable the default sound
////        channel.enableVibration(false);  // Disable vibration
//
//        // Create the notification channel if it doesn't exist
////        manager.createNotificationChannel(channel);
//
//        // Build the notification
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setSmallIcon(R.mipmap.launcher_icon_round)
//                .setAutoCancel(true)
//                .setSound(soundUri)
//                .setPriority(NotificationCompat.PRIORITY_HIGH);
////        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
////                .setContentTitle(title)
////                .setContentText(body)
////                .setSmallIcon(R.mipmap.launcher_icon)
////                .setAutoCancel(true)
////                .setSilent(true); // Ensures that no sound or vibration plays with the notification
//
//        // Send the notification
//        manager.notify(0, builder.build());
//
//        // Manually play the sound and vibration after the notification
////        playSound(this, soundUri.toString());
////        vibrate(this);
//    }

    private void sendTokenToServer(String token) {
        Log.d(TAG, "Token sent to server: " + token);
    }

    // Method to manually play sound
    private void playSound(Context context, String soundUri) {
        Uri rawPathUri = Uri.parse(soundUri);
        Ringtone r = RingtoneManager.getRingtone(context, rawPathUri);
        r.play();
    }

    // Method to manually trigger vibration
    private void vibrate(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            // Deprecated in API 26
            v.vibrate(500);
        }
    }
}