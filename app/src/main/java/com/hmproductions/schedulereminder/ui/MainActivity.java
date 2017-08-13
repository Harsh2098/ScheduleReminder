package com.hmproductions.schedulereminder.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.hmproductions.schedulereminder.R;
import com.hmproductions.schedulereminder.adapters.ScheduleRecyclerAdapter;
import com.hmproductions.schedulereminder.data.Schedule;
import com.hmproductions.schedulereminder.data.ScheduleContract;
import com.hmproductions.schedulereminder.data.ScheduleContract.ScheduleEntry;
import com.hmproductions.schedulereminder.utils.AlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity
        extends AppCompatActivity
        implements ScheduleRecyclerAdapter.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private static final String RINGER_SWITCH_KEY = "ringer-switch-key";
    private static final String NOTIFICATION_SWITCH_KEY = "notification-switch-key";
    public static final String WEEKDAY_KEY = "weekday-key";
    private static final int LOADER_ID = 101;
    private static final int RC_7AM_BROADCAST = 1000;

    RecyclerView schedule_recyclerView;
    ImageView ringerImageView, notificationImageView;
    Switch ringer_switch, notification_switch;
    AlarmManager alarmManager;

    private ScheduleRecyclerAdapter mAdapter;
    private boolean ringerSwitchMode, notificationSwitchMode;
    private PendingIntent mAlarmPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        schedule_recyclerView = (RecyclerView)findViewById(R.id.weekday_recyclerView);
        ringer_switch = (Switch)findViewById(R.id.ringer_switch);
        ringerImageView = (ImageView)findViewById(R.id.ringer_imageView);
        notification_switch = (Switch)findViewById(R.id.notification_switch);
        notificationImageView = (ImageView)findViewById(R.id.notification_imageView);
        mAlarmPendingIntent = PendingIntent.getBroadcast(
                MainActivity.this,
                RC_7AM_BROADCAST,
                new Intent(MainActivity.this, AlarmReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        setupPreferences();
        RingerSwitchListener();
        NotificationSwitchListener();

        mAdapter = new ScheduleRecyclerAdapter(null, this, this);

        schedule_recyclerView.setAdapter(mAdapter);
        schedule_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        schedule_recyclerView.setHasFixedSize(true);

        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    private void setupPreferences() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ringerSwitchMode = preferences.getBoolean(RINGER_SWITCH_KEY, false);
        notificationSwitchMode = preferences.getBoolean(NOTIFICATION_SWITCH_KEY, false);

        if(!ringerSwitchMode) {
            ringerImageView.setImageResource(R.mipmap.volume_on_icon);
            ringer_switch.setChecked(false);
        }
        else {
            ringerImageView.setImageResource(R.mipmap.volume_off_icon);
            ringer_switch.setChecked(true);
        }

        if(!notificationSwitchMode) {
            notificationImageView.setImageResource(R.mipmap.notification_off_icon);
            notification_switch.setChecked(false);
        }
        else {
            notificationImageView.setImageResource(R.mipmap.notification_icon);
            notification_switch.setChecked(true);
            Set7AMAlarm();
        }
    }

    private void savePreferences() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(RINGER_SWITCH_KEY, ringerSwitchMode);
        editor.putBoolean(NOTIFICATION_SWITCH_KEY, notificationSwitchMode);

        editor.apply();
    }

    private void RingerSwitchListener() {

        ringer_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ringerSwitchMode = isChecked;

                // Setting proper image view
                if(ringerSwitchMode)
                    ringerImageView.setImageResource(R.mipmap.volume_off_icon);
                else
                    ringerImageView.setImageResource(R.mipmap.volume_on_icon);
            }
        });
    }

    private void NotificationSwitchListener() {

        notification_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    notificationSwitchMode = true;
                    Set7AMAlarm();
                } else {
                    alarmManager.cancel(mAlarmPendingIntent);
                    notificationSwitchMode = false;
                }
            }
        });
    }

    private void Set7AMAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, mAlarmPendingIntent);
    }

    @Override
    public void onClickListener(int position) {

        Intent intent = new Intent(MainActivity.this, DayActivity.class);
        intent.putExtra(WEEKDAY_KEY, position);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ScheduleContract.CONTENT_URI, null, null, null, ScheduleEntry.COLUMN_TIME);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        List<Schedule> list = new ArrayList<>();

        if(cursor != null) {

            while (cursor.moveToNext()) {

                list.add(new Schedule(
                        cursor.getLong(cursor.getColumnIndexOrThrow(ScheduleEntry.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ScheduleEntry.COLUMN_DAY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ScheduleEntry.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ScheduleEntry.COLUMN_TIME))
                ));
            }

            mAdapter.swapData(list);
        }

        else
            Toast.makeText(this,"Database empty", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // #YOLO
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }
}
