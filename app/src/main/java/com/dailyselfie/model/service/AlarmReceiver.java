package com.dailyselfie.model.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dailyselfie.R;
import com.dailyselfie.view.ui.Dashboard;

public class AlarmReceiver extends BroadcastReceiver {

    private static final int MY_NOTIFICATION_ID = 1;
    private Intent mNotificationIntent;
    private PendingIntent mPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {

        mNotificationIntent = new Intent(context, Dashboard.class);
        mPendingIntent = PendingIntent.getActivity(context, 0, mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build notification
        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setTicker("Time for another selfie")
                .setSmallIcon(R.drawable.ic_action_camera)
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("Time for another selfie")
                .setContentIntent(mPendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND);

        // Get NotificationManager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MY_NOTIFICATION_ID, notificationBuilder.build());
    }
}
