package com.hmproductions.schedulereminder.ui;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hmproductions.schedulereminder.R;
import com.hmproductions.schedulereminder.adapters.DayRecyclerAdapter;
import com.hmproductions.schedulereminder.data.Schedule;
import com.hmproductions.schedulereminder.data.ScheduleContract;

import java.util.ArrayList;
import java.util.List;

public class DayActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, DayRecyclerAdapter.DayListClickListener{

    private static final int LOADER_ID = 101;
    private String[] weekdays = new String[] {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday","Weekday"};
    private String currentWeekday;

    private DayRecyclerAdapter mAdapter;
    RecyclerView day_recyclerView;
    private FloatingActionButton add_floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentWeekday = weekdays[getIntent().getIntExtra(MainActivity.WEEKDAY_KEY, 7)];
        setTitle(currentWeekday);

        add_floatingActionButton = (FloatingActionButton)findViewById(R.id.add_fab);
        day_recyclerView = (RecyclerView)findViewById(R.id.day_recycler_View);
        mAdapter = new DayRecyclerAdapter(this, null, this);

        day_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        day_recyclerView.setAdapter(mAdapter);
        day_recyclerView.setHasFixedSize(false);

        AddFabClickListener();

        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    private void AddFabClickListener()
    {
        add_floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(DayActivity.this);

                final EditText nameInput = new EditText(DayActivity.this);
                nameInput.setMaxLines(1);
                nameInput.setHint("Eg. Digital Electronics");

                builder
                        .setTitle("New item Name")
                        .setView(nameInput)
                        .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if(!(nameInput.getText().toString().equals("") || nameInput.getText().toString().isEmpty())) {
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put(ScheduleContract.ScheduleEntry.COLUMN_DAY, currentWeekday);
                                    contentValues.put(ScheduleContract.ScheduleEntry.COLUMN_NAME, nameInput.getText().toString());
                                    getContentResolver().insert(ScheduleContract.CONTENT_URI, contentValues);
                                    getSupportLoaderManager().restartLoader(LOADER_ID, null, DayActivity.this);
                                }
                                else {
                                    Toast.makeText(DayActivity.this, "Text field was empty", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ScheduleContract.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        List<Schedule> list = new ArrayList<>();

        if(cursor != null) {

            while (cursor.moveToNext()) {
                if(cursor.getString(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_DAY)).equals(currentWeekday)) {

                    list.add(new Schedule (
                            currentWeekday,
                            cursor.getString(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_NAME))));
                }
            }

            mAdapter.swapData(list);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onDayClick(int position) {
        // TODO : Implement method to update value
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(this);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
