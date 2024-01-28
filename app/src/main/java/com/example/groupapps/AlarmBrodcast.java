package com.example.groupapps;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmBrodcast extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;
    private static MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String text = bundle.getString("event");
        String date = bundle.getString("date") + " " + bundle.getString("time");

        if (intent.getAction() != null && intent.getAction().equals("TURN_OFF_ACTION")) {
            // Stop the alarm sound and cancel the notification
            stopAlarmAndNotification(context);
            return;
        }

        // Check if the notification is already active
        if (isNotificationActive(context, NOTIFICATION_ID)) {
            // Notification is already active,
        } else {
            // Play alarm sound
            mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }

        // Click on Notification
        Intent intent1 = new Intent(context, NotificationMessage.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.putExtra("message", text);

        // Turn Off Action
        Intent turnOffIntent = new Intent(context, AlarmBrodcast.class);
        turnOffIntent.setAction("TURN_OFF_ACTION");
        turnOffIntent.putExtra("event", text);
        turnOffIntent.putExtra("date", bundle.getString("date"));
        turnOffIntent.putExtra("time", bundle.getString("time"));
        PendingIntent turnOffPendingIntent = PendingIntent.getBroadcast(context, 0, turnOffIntent, 0);

        // Notification Builder
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent1, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "notify_001");

        // Set all the properties for the notification
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
        contentView.setImageViewResource(R.id.icon, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.message, text);
        contentView.setTextViewText(R.id.date, date);

        // Set turn off action on the "Turn Off" button
        contentView.setOnClickPendingIntent(R.id.flashButton, turnOffPendingIntent);

        mBuilder.setSmallIcon(R.drawable.alaram);
        mBuilder.setAutoCancel(true);
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setOnlyAlertOnce(false);
        mBuilder.setContent(contentView);
        mBuilder.setContentIntent(pendingIntent);

        // Create a notification channel after API level 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "notify_001";
            NotificationChannel channel = new NotificationChannel(channelId, "channel name", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        Notification notification = mBuilder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void stopAlarmAndNotification(Context context) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Cancel the notification
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID);
    }

    private boolean isNotificationActive(Context context, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            StatusBarNotification[] activeNotifications = new StatusBarNotification[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                activeNotifications = notificationManager.getActiveNotifications();
            }
            for (StatusBarNotification notification : activeNotifications) {
                if (notification.getId() == notificationId) {
                    return true;
                }
            }
        }
        return false;
    }

}
