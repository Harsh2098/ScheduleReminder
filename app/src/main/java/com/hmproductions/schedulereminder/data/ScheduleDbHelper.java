package com.hmproductions.schedulereminder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hmproductions.schedulereminder.data.ScheduleContract.ScheduleEntry;

/**
 * Created by harsh on 7/31/17.
 *
 * ScheduleDbHelper creates and deletes 'schedule' table when required.
 */

class ScheduleDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 4;

    private static final String DATABASE_NAME = "schedule";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + ScheduleEntry.TABLE_NAME + " (" +
                    ScheduleEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ScheduleEntry.COLUMN_DAY + " TEXT NOT NULL," +
                    ScheduleEntry.COLUMN_NAME + " TEXT NOT NULL," +
                    ScheduleEntry.COLUMN_TIME + " TEXT " +
                    ScheduleEntry.COLUMN_PRIORITY + " TEXT) ";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + ScheduleEntry.TABLE_NAME;


    ScheduleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }
}
