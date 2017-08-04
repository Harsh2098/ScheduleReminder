package com.hmproductions.schedulereminder.ui;

import android.app.AlarmManager;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity
        extends AppCompatActivity
        implements ScheduleRecyclerAdapter.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private static final String RINGER_SWITCH_KEY = "ringer-switch-key";
    public static final String WEEKDAY_KEY = "weekday-key";
    private static final int LOADER_ID = 101;

    RecyclerView schedule_recyclerView;
    ImageView ringerImageView;
    Switch ringer_switch;
    AlarmManager alarmManager;

    private ScheduleRecyclerAdapter mAdapter;
    private boolean ringerSwitchMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        schedule_recyclerView = (RecyclerView)findViewById(R.id.weekday_recyclerView);
        ringer_switch = (Switch)findViewById(R.id.ringer_switch);
        ringerImageView = (ImageView)findViewById(R.id.ringer_imageView);

        setupPreferences();
        RingerSwitchListener();

        schedule_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        schedule_recyclerView.setHasFixedSize(true);

        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    private void setupPreferences() {

        SharedPreferences prefrences = PreferenceManager.getDefaultSharedPreferences(this);
        ringerSwitchMode = prefrences.getBoolean(RINGER_SWITCH_KEY, false);

        if(ringerSwitchMode)
            ringerImageView.setImageResource(R.mipmap.volume_on_icon);
        else
            ringerImageView.setImageResource(R.mipmap.volume_off_icon);
    }

    private void savePreferences() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(RINGER_SWITCH_KEY, ringerSwitchMode);
        editor.apply();
    }

    private void RingerSwitchListener() {

        ringer_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ringerSwitchMode = isChecked;

                // Setting proper image view
                if(ringerSwitchMode)
                    ringerImageView.setImageResource(R.mipmap.volume_on_icon);
                else
                    ringerImageView.setImageResource(R.mipmap.volume_off_icon);
            }
        });
    }

    @Override
    public void onClickListener(int position) {

        Intent intent = new Intent(MainActivity.this, DayActivity.class);
        intent.putExtra(WEEKDAY_KEY, position);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ScheduleContract.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        List<Schedule> list = new ArrayList<>();

        if(cursor != null) {

            cursor.moveToFirst();
            while (cursor.moveToNext()) {

                list.add(new Schedule(
                        cursor.getString(cursor.getColumnIndexOrThrow(ScheduleEntry.COLUMN_DAY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ScheduleEntry.COLUMN_NAME))
                ));
            }

            mAdapter = new ScheduleRecyclerAdapter(list, this, this);
            schedule_recyclerView.setAdapter(mAdapter);
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
