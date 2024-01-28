package com.example.groupapps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

public class StopReceiver extends BroadcastReceiver {
    private static final String TAG = "StopReceiver";
    private static MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        Log.d(TAG, "onReceive called");

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Cancel the notification
        NotificationManagerCompat.from(context).cancel(1);
    }
}
