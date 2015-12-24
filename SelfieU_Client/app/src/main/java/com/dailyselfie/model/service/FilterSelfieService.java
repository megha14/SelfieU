package com.dailyselfie.model.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.dailyselfie.model.content.SelfieContract;
import com.dailyselfie.model.mediator.SelfieDataMediator;

import java.util.ArrayList;

/**
 * Created by Megha on 11/15/2015.
 */
public class FilterSelfieService extends IntentService {

    public static final String ACTION_FILTER_SERVICE_RESPONSE =
            "com.dailyselfie.services.FilterSelfieService.RESPONSE";

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
    private NotificationCompat.Builder mBuilder;

    /**
     * Constructor for FilterSelfieService.
     */
    public FilterSelfieService() {
        super("FilterSelfieService");
    }

    /**
     * Constructor for FilterSelfieService.
     *
     * @param name
     */
    public FilterSelfieService(String name) {
        super("FilterSelfieService");
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
                                    long selfieId, ArrayList<String> filters) {
        return new Intent(context,
                FilterSelfieService.class)
                .putExtra(SelfieContract.SelfieTable._ID, selfieId).putStringArrayListExtra("Filters", filters);
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
                new SelfieDataMediator(getApplicationContext());

        // Check if Selfie Filter is successful.
        finishNotification(mSelfieMediator.applyFilters(
                getApplicationContext(),
                intent.getLongExtra(SelfieContract.SelfieTable._ID, 0), intent.getStringArrayListExtra("Filters")));

        // Send the Broadcast to SelfiePlayerActivity that the Selfie
        // Filter is completed.
        sendBroadcast();
    }

    /**
     * Send the Broadcast to Activity that the Selfie Filter is
     * completed.
     */
    private void sendBroadcast() {
        // Use a LocalBroadcastManager to restrict the scope of this
        // Intent to the SelfieClient application.
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(ACTION_FILTER_SERVICE_RESPONSE)
                        .addCategory(Intent.CATEGORY_DEFAULT));
    }

    /**
     * Finish the Notification after the Selfie is Filtered.
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
                .setContentTitle("Selfie Filter")
                .setContentText("Filter in progress")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setTicker("Filtering selfie")
                .setProgress(0,
                        0,
                        true);

        // Build and issue the notification.
        mNotifyManager.notify(NOTIFICATION_ID,
                mBuilder.build());
    }

}
