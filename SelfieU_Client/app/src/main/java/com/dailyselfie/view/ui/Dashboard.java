package com.dailyselfie.view.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dailyselfie.R;
import com.dailyselfie.model.content.AsyncSelfieHandler;
import com.dailyselfie.model.content.SelfieResolver;
import com.dailyselfie.model.mediator.SelfieDataMediator;
import com.dailyselfie.model.mediator.webdata.SelfieRecord;
import com.dailyselfie.model.service.AlarmReceiver;
import com.dailyselfie.model.service.DownloadSelfieService;
import com.dailyselfie.model.service.FilterSelfieService;
import com.dailyselfie.model.service.UploadSelfieService;
import com.dailyselfie.utils.SelfieStorageUtils;
import com.dailyselfie.utils.Utils;
import com.dailyselfie.view.Adapters.ItemTouchHelperCallback;
import com.dailyselfie.view.Adapters.SelfieRecordAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity implements SelfieRecordAdapter.SelfieRecordViewHolder.ClickListener {

    private static final String LOG_TAG = Dashboard.class.getSimpleName();

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_CAPTURE_PERMISSION = 1;
    public SelfieDataMediator mSelfieMediator;
    public ArrayList<String> newList = new ArrayList<String>();

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private RecyclerView mRecyclerView;

    private RecyclerView.LayoutManager mLayoutManager;
    private SelfieRecordAdapter selfieRecordAdapter;

    private ResultReceiver mResultReceiver;
    private Uri mRecordSelfieUri;

    private SelfieRecord newRecord;

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;

    private List<SelfieRecord> recordList;

    public SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        mResultReceiver = new ResultReceiver();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Dashboard");


        new SelfieListOps().execute();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        selfieRecordAdapter = new SelfieRecordAdapter(this, this);
        mRecyclerView.setAdapter(selfieRecordAdapter);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(1000);
        defaultItemAnimator.setRemoveDuration(1000);
        defaultItemAnimator.setMoveDuration(1000);
        defaultItemAnimator.setChangeDuration(1000);
        mRecyclerView.setItemAnimator(defaultItemAnimator);

        ItemTouchHelper.Callback callback =
                new ItemTouchHelperCallback(selfieRecordAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.setLayoutManager(mLayoutManager);
        } else {
            mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_refresh:
                // User chose the "Refresh" action, mark the current item
                // as a refresh...
                List<SelfieRecord> selfieRecords = selfieRecordAdapter.getSelfieList();
                for (SelfieRecord selfieRecord : selfieRecords) {
                    File file = new File(selfieRecord.getPath());
                    if (!(file.exists())) {
                        new SelfieListOps().downloadSelfie(selfieRecord.getId());
                        selfieRecordAdapter.notifySelfieChanged(selfieRecord);
                    }
                }
                return true;

            case R.id.action_alarm:
                Intent intent = new Intent(this, AlarmActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_logout:
                applyLogoutDialog();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void applyLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);

        builder.setTitle("Are you sure?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Log.d(LOG_TAG, sharedPreferences.getString("User", ""));
                Intent intent1 = new Intent(Dashboard.this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, intent1, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);

                Intent intent = new Intent(Dashboard.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        applyLogoutDialog();
    }

    private void applyFilterDialog(final List<Integer> selectedItems) {
        // Build an AlertDialog

        AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);

        // String array for alert dialog multi choice items
        final String[] filters = this.getResources().getStringArray(R.array.filterList);
        final ArrayList<String> checkedList = new ArrayList<String>();
        builder.setMultiChoiceItems(filters, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                if (isChecked) {
                    checkedList.add(filters[which]);
                } else if (checkedList.contains(filters[which])) {
                    checkedList.remove(filters[which]);
                }
            }
        }).setTitle("Apply Filter").setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (String item : checkedList)
                    newList.add(item);
                List<SelfieRecord> selfieRecordList = selfieRecordAdapter.getSelectedRecords(selectedItems);
                for (SelfieRecord selfieRecord : selfieRecordList) {
                    Utils.showToast(Dashboard.this, "Applying filter to " + selfieRecord.getName());
                    new SelfieListOps().applyFilter(selfieRecord.getId(), newList);
                }
                selfieRecordAdapter.notifyDataSetChanged();
                Log.d(LOG_TAG, newList.toString());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();


    }

    //Take Picture using Android's Camera API
    private void dispatchTakePictureIntent() {

       // Ensure that there's a camera activity to handle the intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            mRecordSelfieUri = SelfieStorageUtils.getRecordedSelfieUri(getApplicationContext());
            Log.d(LOG_TAG, String.valueOf(mRecordSelfieUri));
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    mRecordSelfieUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "ActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, String.valueOf(mRecordSelfieUri));
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Upload the Selfie.

            new UploadSelfieOps().uploadSelfie(mRecordSelfieUri);

        } else
            Utils.showToast(this, "Could not get selfie to upload");
    }


    /**
     * Hook method that is called when user resumes activity
     * from paused state, onPause().
     */
    @Override
    protected void onResume() {
        // Call up to the superclass.
        super.onResume();

        // Register BroadcastReceiver that receives result from
        // UploadSelfieService when a selfie upload completes.
        registerReceiver();
    }

    /**
     * Register a BroadcastReceiver that receives a result from the
     * UploadSelfieService when a selfie upload completes.
     */
    private void registerReceiver() {

        // Create an Intent filter that handles Intents from the
        // UploadSelfieService.
        IntentFilter uploadIntentFilter = new IntentFilter(
                UploadSelfieService.ACTION_UPLOAD_SERVICE_RESPONSE);
        uploadIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Register the BroadcastReceiver.

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mResultReceiver, uploadIntentFilter);

        // Create an Intent filter that handles Intents from the
        // DownloadSelfieService.
        IntentFilter downloadIntentFilter = new IntentFilter(
                DownloadSelfieService.ACTION_DOWNLOAD_SERVICE_RESPONSE);
        downloadIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Register the BroadcastReceiver.

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mResultReceiver, downloadIntentFilter);

        // Create an Intent filter that handles Intents from the
        // FilterSelfieService.
        IntentFilter FilterIntentFilter = new IntentFilter(
                FilterSelfieService.ACTION_FILTER_SERVICE_RESPONSE);
        FilterIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Register the BroadcastReceiver.

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mResultReceiver, FilterIntentFilter);


    }

    /**
     * Hook method that gives a final chance to release resources and
     * stop spawned threads.  onDestroy() may not always be
     * called-when system kills hosting process.
     */
    @Override
    protected void onPause() {
        // Call onPause() in superclass.
        super.onPause();

        // Unregister BroadcastReceiver.
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mResultReceiver);
    }

    /**
     * Finishes this Activity.
     */
    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }


    @Override
    public void onItemClicked(int position) {
        Log.d(LOG_TAG, "on item clicked");
        if (actionMode != null) {
            toggleSelection(position);
        } else {
            newRecord = selfieRecordAdapter.getItem(position);
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, newRecord.getPath());
            startActivity(intent);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        Log.d(LOG_TAG, "On long item click");
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);

        return true;
    }

    @Override
    public void setButtonClicked(int position) {
        selfieRecordAdapter.deleteFromLocal(position);
    }

    private void toggleSelection(int position) {
        selfieRecordAdapter.toggleSelection(position);
        int count = selfieRecordAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }




    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.selected_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete_selected:
                    // TODO: actually remove items
                    Log.d(LOG_TAG, "menu_remove");
                    List<SelfieRecord> selfieRecords = selfieRecordAdapter.getSelectedRecords(selfieRecordAdapter.getSelectedItems());
                    for (SelfieRecord selfieRecord : selfieRecords) {
                        Log.d(LOG_TAG, "deleting");
                        new SelfieListOps().delete(selfieRecord.getId());
                    }
                    selfieRecordAdapter.removeItems(selfieRecordAdapter.getSelectedItems());
                    mode.finish();
                    return true;

                case R.id.action_filter:
                    applyFilterDialog(selfieRecordAdapter.getSelectedItems());
                    mode.finish();
                    return true;

                case R.id.action_download:
                    List<SelfieRecord> selfieRecords1 = selfieRecordAdapter.getSelectedRecords(selfieRecordAdapter.getSelectedItems());
                    for (SelfieRecord selfieRecord : selfieRecords1) {
                        new SelfieListOps().downloadSelfie(selfieRecord.getId());
                        selfieRecordAdapter.notifySelfieChanged(selfieRecord);
                    }
                    mode.finish();
                    return true;

                default:

                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            selfieRecordAdapter.clearSelection();
            actionMode = null;
        }
    }

    /**
     * The Broadcast Receiver that registers itself to receive result
     * from UploadSelfieService.
     */
    private class ResultReceiver
            extends BroadcastReceiver {
        /**
         * Hook method that's dispatched when the UploadService has
         * uploaded the Selfie.
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            // Starts an AsyncTask to get fresh Selfie list from the Selfie Service.
            String action = intent.getAction();
            Log.d(LOG_TAG, "Receiver " + action);
            if (action.equals("com.dailyselfie.services.UploadSelfieService.RESPONSE")){
                new UploadSelfieOps().execute();

            }else
                new SelfieListOps().execute();
        }
    }


    // AsyncTask to do upload, download, and apply filter in background.
    private class SelfieListOps extends AsyncTask<Void, Void, List<SelfieRecord>> {

        SelfieListOps() {
            mSelfieMediator = new SelfieDataMediator(Dashboard.this);
        }


        /**
         * Retrieve the List of Selfies by help of SelfieDataMediator via a
         * synchronous two-way method call, which runs in a background
         * thread to avoid blocking the UI thread.
         */
        @Override
        public List<SelfieRecord> doInBackground(Void... params) {

            return mSelfieMediator.getSelfieList();
        }

        @Override
        public void onPostExecute(List<SelfieRecord> selfies) {
            // add new selfies to the selfie provider asynchronously
            Log.d(LOG_TAG, String.valueOf(selfies));

            new AsyncSelfieHandler(getApplicationContext().getContentResolver()).insert(selfies);

            recordList = new SelfieResolver(getApplicationContext().getContentResolver()).queryAll(sharedPreferences.getString("User", ""));
            Log.d(LOG_TAG, "************    " + recordList.toString());
            // update selfie list
            displaySelfieList(selfies);
        }

        public void displaySelfieList(List<SelfieRecord> selfies) {
            if (selfies != null) {
                // Update the adapter with the List of Selfies.

                selfieRecordAdapter.setSelfies(recordList);
                selfieRecordAdapter.notifyDataSetChanged();

            } else {
                Utils.showToast(getApplicationContext(),
                        "Please connect to the Selfie Service");

            }
        }



        /**
         * Start a service that applies list of filters to selfie having its ID
         */
        public void applyFilter(long selfieId, ArrayList<String> filters) {
            Context appContext = getApplicationContext();
            appContext.startService(FilterSelfieService.makeIntent(appContext,
                    selfieId, filters));
        }

        public void delete(final long selfieId) {
            Log.d(LOG_TAG, "in delete " + selfieId);
            final Context appContext = getApplicationContext();
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    mSelfieMediator.delete(appContext, selfieId);
                    return null;
                }
            }.execute();
        }

        public void downloadSelfie(long selfieId) {


            Context appContext = getApplicationContext();
            appContext.startService(DownloadSelfieService.makeIntent(appContext,
                    selfieId));
        }
    }

    private class UploadSelfieOps extends AsyncTask<Void, Void, SelfieRecord>{

        /**
         * Start a service that Uploads the Selfie having its Uri.
         *
         * @param selfieUri
         */
        public void uploadSelfie(Uri selfieUri) {
            // Sends an Intent command to the UploadSelfieService.
            Context appContext = getApplicationContext();
            appContext.startService(UploadSelfieService.makeIntent(appContext,
                    selfieUri));
        }

        @Override
        public SelfieRecord doInBackground(Void... params) {

            SelfieRecord newRecord = new SelfieResolver(getApplicationContext().getContentResolver()).queryLastInserted();
            return newRecord;
        }

        @Override
        public void onPostExecute(SelfieRecord selfies) {

            selfieRecordAdapter.add(selfies);
            selfieRecordAdapter.notifyDataSetChanged();
        }
    }



}
