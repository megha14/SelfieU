package com.dailyselfie.view.ui;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.dailyselfie.R;
import com.dailyselfie.model.service.AlarmReceiver;

import java.util.Calendar;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

public class AlarmActivity extends AppCompatActivity {

    static final int DIALOG_ID = 0;
    private static final String LOG_TAG = AlarmActivity.class.getSimpleName();
    private static final int RQS_1 = 0;
    int year_x, month_x, day_x, hours_x, minutes_x;
    private Toolbar toolbar;
    private Button addDate;
    private Button addTime;
    private CheckBox repeat;
    private ToggleButton setAlarm;
    private EditText addDateText;
    private EditText addTimeText;
    private Boolean mChecked = false;
    public PendingIntent pendingIntent;
    public AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        initializeUI();
    }

    private void initializeUI(){

        toolbar = (Toolbar) findViewById(R.id.alarmbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Set Reminder");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addDate = (Button) findViewById(R.id.addDate);
        addTime = (Button) findViewById(R.id.addTime);
        repeat = (CheckBox) findViewById(R.id.repeatAlarm);
        setAlarm = (ToggleButton) findViewById(R.id.setAlarm);
        addDateText = (EditText) findViewById(R.id.addDateText);
        addTimeText = (EditText) findViewById(R.id.addTimeText);

        loadPreferences();

        final Calendar calendar = getInstance();
        final int RQS_1 = 1;

        addDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "addDate");
                new DatePickerDialog(AlarmActivity.this, onDateSetListener, calendar.get(YEAR),
                        calendar.get(MONTH), calendar.get(DAY_OF_MONTH)).show();

            }
        });

        addTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "addTime");
                new TimePickerDialog(AlarmActivity.this, onTimeSetListener, calendar.get(HOUR), calendar.get(MINUTE), true).show();
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    mChecked = true;
                }
            }
        });

        setAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(LOG_TAG, "Button on");
                    setAlarm(calendar);
                }
            }
        });

    }
    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear;
            day_x = dayOfMonth;
            String msg = year_x + "/" + month_x + "/" + day_x;
            Log.d(LOG_TAG, msg);
            addDateText.setText(msg);

        }
    };

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hours_x = hourOfDay;
            minutes_x = minute;
            String msg = hourOfDay + " : " + minute;
            Log.d(LOG_TAG, msg);
            addTimeText.setText(msg);
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm, menu);
        return true;
    }

    private void setAlarm(Calendar targetCal) {

        savePreferences();
        Log.d(LOG_TAG, "setAlarm " + targetCal.getTime());
        targetCal.clear();
        targetCal.set(year_x, month_x, day_x, hours_x, minutes_x);
        Log.d(LOG_TAG, "setAlarm " + targetCal.getTime());
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getBaseContext(), RQS_1, intent, 0);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (mChecked) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), 5 * 60 * 1000, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        }
    }

    private void savePreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("Login",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Button", setAlarm.isChecked());
        editor.putString("DateText", addDateText.getText().toString());
        editor.putString("TimeText",addTimeText.getText().toString());
        editor.commit();
    }

    private void loadPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        addDateText.setText(sharedPreferences.getString("DateText", ""));
        addTimeText.setText(sharedPreferences.getString("TimeText", ""));
        setAlarm.setChecked(sharedPreferences.getBoolean("Button", false));

    }

    public void cancelAlarm(){
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(this, intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
           setContentView(R.layout.activity_alarm_land);
            initializeUI();
        }else{
            setContentView(R.layout.activity_alarm);
            initializeUI();
        }
    }

}
