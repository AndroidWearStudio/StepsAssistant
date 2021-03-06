package com.sm.stepsassistant;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.text.NumberFormat;

public class NotificationReceiver extends BroadcastReceiver {
    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean showNotes = prefs.getBoolean(StartListenerService.SHOW_STEP_CARD,true);
        if (showNotes) {
            int notificationId = 1;
            Intent openIntent = new Intent(context, MyWearActivity.class);
            PendingIntent openToday = PendingIntent.getActivity(context, 0, openIntent, 0);
            NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender()
                    .setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.notify_background))
                    .setCustomSizePreset(Notification.WearableExtender.SIZE_LARGE);

            int numberOfSteps = StartListenerService.calculateSteps(context);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(NumberFormat.getInstance().format(numberOfSteps) + " steps")
                    .setContentText(StartListenerService.calculateTime(context))
                    .addAction(R.drawable.ic_action_full_screen, "Open", openToday)
                    .extend(wearableExtender);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(notificationId, notificationBuilder.build());
            Log.d("OUTPUT", "Notification Updated!");
        }
    }
}
