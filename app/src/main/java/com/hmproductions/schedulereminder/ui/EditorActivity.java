package com.hmproductions.schedulereminder.ui;

import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hmproductions.schedulereminder.R;
import com.hmproductions.schedulereminder.ScheduleWidget;
import com.hmproductions.schedulereminder.data.ScheduleContract;
import com.hmproductions.schedulereminder.data.ScheduleContract.ScheduleEntry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER_ID = 103;
    private Uri mCurrentUri;
    private Spinner mTaskSpinner;
    private Switch mTaskSwitch;
    private EditText task_editText;
    private Button timeButton;

    private String name, time, mCurrentWeekday;

    private TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            StringBuilder builder = new StringBuilder();

            if(hourOfDay%12 == 0)
                builder.append("12");
            else
                builder.append(hourOfDay%12);
            builder.append(":");

            if(minute<10) builder.append("0");
            builder.append(minute);

            if(hourOfDay>12)
                builder.append(" PM");
            else
                builder.append(" AM");

            time = builder.toString();
            timeButton.setText(time);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        if(getIntent().getData() != null) {
            setTitle("Edit a Task");
            mCurrentUri = getIntent().getData();
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        }
        else
            setTitle("Add a new Task");

        /* Getting weekday to update or insert with appropriate weekday */
        mCurrentWeekday = getIntent().getStringExtra(MainActivity.WEEKDAY_KEY);

        BindViews();
        SetupSwitch();
        SetupTasksSpinner();
        SetupTimePickerButtonListener();

        task_editText.setEnabled(false);
    }

    private void BindViews() {
        mTaskSpinner = (Spinner)findViewById(R.id.spinner);
        timeButton = (Button)findViewById(R.id.time_button);
        mTaskSwitch = (Switch)findViewById(R.id.task_switch);
        task_editText = (EditText)findViewById(R.id.task_editText);
    }

    private void SetupSwitch() {

        mTaskSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mTaskSpinner.setEnabled(false);
                    task_editText.setEnabled(true);
                } else {
                    mTaskSpinner.setEnabled(true);
                    task_editText.setEnabled(false);
                }
            }
        });
    }

    private void SetupTasksSpinner() {
        mTaskSpinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(EditorActivity.this, R.array.array_tasks, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mTaskSpinner.setAdapter(adapter);

        mTaskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                name = getResources().getStringArray(R.array.array_tasks)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void SetupTimePickerButtonListener()
    {
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hourOfDay, minute;

                if(timeButton.getText().toString().equals("CURRENT TIME")) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                    minute = calendar.get(Calendar.MINUTE);
                } else {

                    String timeString = timeButton.getText().toString();
                    hourOfDay = Integer.parseInt(timeString.substring(0,2));
                    minute = Integer.parseInt(timeString.substring(3,5));
                }
                TimePickerDialog timePicker = new TimePickerDialog(EditorActivity.this, timeListener, hourOfDay, minute, false);
                timePicker.show();
            }
        });
    }

    private boolean insertData() {

        if(mTaskSwitch.isChecked()) {
            if(task_editText.getText().toString().isEmpty() || task_editText.getText().toString().equals("")) {
                Toast.makeText(this, "Please enter task name", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                name = task_editText.getText().toString();
            }
        }

        if(timeButton.getText().equals("CURRENT TIME")) {
            SimpleDateFormat formatter = new SimpleDateFormat("h:mm a", Locale.US);
            time = formatter.format(new Date(System.currentTimeMillis()));
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(ScheduleEntry.COLUMN_DAY, mCurrentWeekday);
        contentValues.put(ScheduleEntry.COLUMN_NAME, name);
        contentValues.put(ScheduleEntry.COLUMN_TIME, time);

        if(mCurrentUri == null)
            getContentResolver().insert(ScheduleContract.CONTENT_URI, contentValues);

        else
            getContentResolver().update(mCurrentUri, contentValues, null, null);

        // Update all live widgets
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, ScheduleWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.schedule_widget_gridView);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (getIntent() == null) {
            menu.findItem(R.id.delete_action).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.done_action :
                if(insertData())
                    NavUtils.navigateUpFromSameTask(this);
                break;

            case R.id.delete_action :
                getContentResolver().delete(mCurrentUri, null, null);
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, mCurrentUri, null, null ,null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();
        task_editText.setText(cursor.getString(cursor.getColumnIndexOrThrow(ScheduleEntry.COLUMN_NAME)));
        timeButton.setText(cursor.getString(cursor.getColumnIndexOrThrow(ScheduleEntry.COLUMN_TIME)));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
