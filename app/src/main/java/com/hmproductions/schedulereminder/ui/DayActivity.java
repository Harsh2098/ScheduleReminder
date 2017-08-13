package com.hmproductions.schedulereminder.ui;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.hmproductions.schedulereminder.R;
import com.hmproductions.schedulereminder.adapters.DayRecyclerAdapter;
import com.hmproductions.schedulereminder.data.Schedule;
import com.hmproductions.schedulereminder.data.ScheduleContract;

import java.util.ArrayList;
import java.util.List;

public class DayActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, DayRecyclerAdapter.DayListClickListener {

    private static final int LOADER_ID = 101;
    private String[] weekdays = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday", "Weekday"};
    private String currentWeekday;

    private DayRecyclerAdapter mAdapter;
    RecyclerView day_recyclerView;
    private FloatingActionButton add_floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentWeekday = weekdays[getIntent().getIntExtra(MainActivity.WEEKDAY_KEY, 7)];
        setTitle(currentWeekday);

        add_floatingActionButton = (FloatingActionButton) findViewById(R.id.add_fab);
        day_recyclerView = (RecyclerView) findViewById(R.id.day_recycler_View);
        mAdapter = new DayRecyclerAdapter(this, null, this);

        day_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        day_recyclerView.setAdapter(mAdapter);
        day_recyclerView.setHasFixedSize(false);

        AddFabClickListener();

        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    private void AddFabClickListener() {
        add_floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DayActivity.this, EditorActivity.class);
                intent.putExtra(MainActivity.WEEKDAY_KEY, currentWeekday);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ScheduleContract.CONTENT_URI, null, null, null, ScheduleContract.ScheduleEntry.COLUMN_TIME);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        List<Schedule> list = new ArrayList<>();

        if (cursor != null) {

            while (cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_DAY)).equals(currentWeekday)) {

                    list.add(new Schedule(
                            cursor.getLong(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_ID)),
                            currentWeekday,
                            cursor.getString(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_TIME)))
                    );
                }
            }

            mAdapter.swapData(list);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapData(null);
    }

    @Override
    public void onDayClick(long id) {
        Intent intent = new Intent(DayActivity.this, EditorActivity.class);
        intent.putExtra(MainActivity.WEEKDAY_KEY, currentWeekday);
        intent.setData(ContentUris.withAppendedId(ScheduleContract.CONTENT_URI, id));
        Log.v(":::", String.valueOf(intent.getData()));
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
