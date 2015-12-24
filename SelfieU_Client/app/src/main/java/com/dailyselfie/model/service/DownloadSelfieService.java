package com.dailyselfie.model.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.LocalBroadcastManager;

import com.dailyselfie.model.content.SelfieContract;
import com.dailyselfie.model.mediator.SelfieDataMediator;


/**
 * Intent Service that runs in background and downloads the Selfie with a
 * given Id.  After the operation, it broadcasts the Intent to send
 * the result of the download to the SelfiePlayerActivity.
 */
public class DownloadSelfieService extends IntentService {
    /**
     * Custom Action that will be used to send Broadcast to the
     * SelfieListActivity.
     */
    public static final String ACTION_DOWNLOAD_SERVICE_RESPONSE =
            "com.dailyselfie.services.DownloadSelfieService.RESPONSE";

    /**
     * It is used by Notification Manager to send Notifications.
     */
    private static final int NOTIFICATION_ID = 2;

    /**
     * SelfieDataMediator mediates the communication between Selfie
     * Service and local storage in the Android device.
     */
    private SelfieDataMediator mSelfieMediator;

    /**
     * Manages the Notification displayed in System UI.
     */
    private NotificationManager mNotifyManager;

    /**
     * Builder used to build the Notification.
     */
    private Builder mBuilder;

    /**
     * Constructor for DownloadSelfieService.
     */
    public DownloadSelfieService() {
        super("DownloadSelfieService");
    }

    /**
     * Constructor for DownloadSelfieService.
     *
     * @param name
     */
    public DownloadSelfieService(String name) {
        super("DownloadSelfieService");
    }

    /**
     * Factory method that makes the explicit intent another Activity
     * uses to call this Service.
     *
     * @param context
     * @param selfieId
     * @return
     */
    public static Intent makeIntent(Context context,
                                    long selfieId) {
        return new Intent(context,
                DownloadSelfieService.class)
                .putExtra(SelfieContract.SelfieTable._ID, selfieId);
    }

    /**
     * Hook method that is invoked on the worker thread with a request
     * to process. Only one Intent is processed at a time, but the
     * processing happens on a worker thread that runs independently
     * from other application logic.
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Starts the Notification to show the progress of selfie download.
        startNotification();

        // Create SelfieDataMediator that will mediate the communication
        // between Server and Android Storage.
        mSelfieMediator =
                new SelfieDataMediator(this);

        // Check if Selfie Download is successful.
        finishNotification(mSelfieMediator.downloadSelfie(
                getApplicationContext(),
                intent.getLongExtra(SelfieContract.SelfieTable._ID, 0)));

        // Send the Broadcast to SelfiePlayerActivity that the Selfie
        // Download is completed.
        sendBroadcast();
    }

    /**
     * Send the Broadcast to Activity that the Selfie Download is
     * completed.
     */
    private void sendBroadcast() {
        // Use a LocalBroadcastManager to restrict the scope of this
        // Intent to the SelfieClient application.
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(ACTION_DOWNLOAD_SERVICE_RESPONSE)
                        .addCategory(Intent.CATEGORY_DEFAULT));
    }

    /**
     * Finish the Notification after the Selfie is Downloaded.
     *
     * @param status
     */
    private void finishNotification(String status) {
        // When the loop is finished, updates the notification.
        mBuilder.setContentTitle(status)
                // Removes the progress bar.
                .setProgress(0,
                        0,
                        false)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentText("")
                .setTicker(status);

        // Build the Notification with the given
        // Notification Id.
        mNotifyManager.notify(NOTIFICATION_ID,
                mBuilder.build());
    }

    /**
     * Starts the Notification to show the progress of selfie download.
     */
    private void startNotification() {
        // Gets access to the Android Notification Service.
        mNotifyManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);


        // Create the Notification and set a progress indicator for an
        // operation of indeterminate length.
        mBuilder = new NotificationCompat
                .Builder(this)
                .setContentTitle("Selfie Download")
                .setContentText("Download in progress")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setTicker("Downloading selfie")
                .setProgress(0,
                        0,
                        true);

        // Build and issue the notification.
        mNotifyManager.notify(NOTIFICATION_ID,
                mBuilder.build());
    }

}
