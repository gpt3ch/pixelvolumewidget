package com.gpt3ch.volfloatt;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class VolumeControlService extends Service {

    private static final String CHANNEL_ID = "VolumeControlChannel";
    private static final int NOTIFICATION_ID = 1; // Use a constant for the ID
    private AudioManager audioManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("VolControlService", "onCreate called");
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("VolControlService", "onStartCommand called with action: " + (intent != null ? intent.getAction() : "null"));

        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            switch (action) {
                case "ACTION_VOLUME_UP":
                    adjustVolume(AudioManager.ADJUST_RAISE);
                    break;
                case "ACTION_VOLUME_DOWN":
                    adjustVolume(AudioManager.ADJUST_LOWER);
                    break;
                case "ACTION_SHOW_VOLUME_UI":
                    // Adjust volume by zero with the FLAG_SHOW_UI flag
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
                    break;
            }
        }

        Notification notification = buildNotification();
        startForeground(NOTIFICATION_ID, notification);
        Log.d("VolControlService", "startForeground called");

        return START_STICKY;
    }

    private void adjustVolume(int direction) {
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, AudioManager.FLAG_SHOW_UI);
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Volume Control Service",
                NotificationManager.IMPORTANCE_LOW
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(serviceChannel);
            Log.d("VolControlService", "Notification channel created: " + CHANNEL_ID + " with importance: " + serviceChannel.getImportance());
        } else {
            Log.e("VolControlService", "NotificationManager is null in createNotificationChannel");
        }
    }

    private Notification buildNotification() {
        Intent showVolumeIntent = new Intent(this, VolumeControlService.class);
        showVolumeIntent.setAction("ACTION_SHOW_VOLUME_UI");
        PendingIntent pendingIntent =
                PendingIntent.getService(this, 0, showVolumeIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle("Volume Control Running")
                .setContentText("Tap to adjust volume")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent) // Set our custom action intent
                .setOngoing(true);

        // Add action buttons for volume control (these remain the same)
        Intent volumeUpIntent = new Intent(this, VolumeControlService.class);
        volumeUpIntent.setAction("ACTION_VOLUME_UP");
        PendingIntent volumeUpPendingIntent =
                PendingIntent.getService(this, 0, volumeUpIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.addAction(android.R.drawable.ic_media_play, "Up", volumeUpPendingIntent);

        Intent volumeDownIntent = new Intent(this, VolumeControlService.class);
        volumeDownIntent.setAction("ACTION_VOLUME_DOWN");
        PendingIntent volumeDownPendingIntent =
                PendingIntent.getService(this, 0, volumeDownIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.addAction(android.R.drawable.ic_media_play, "Down", volumeDownPendingIntent);

        Notification notification = builder.build();
        return notification;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}