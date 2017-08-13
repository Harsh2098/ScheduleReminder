package com.hmproductions.schedulereminder.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.hmproductions.schedulereminder.data.ScheduleContract.ScheduleEntry;

/**
 * Created by harsh on 7/31/17.
 * 
 * Schedule Content Provider
 */

public class ScheduleProvider extends ContentProvider {

    static UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    ScheduleDbHelper mDatabaseHelper;
    SQLiteDatabase mDatabase;

    private static final int URI_WITHOUT_PATH = 100;
    private static final int URI_WITH_PATH = 101;

    static {
        mUriMatcher.addURI(ScheduleContract.CONTENT_AUTHORITY, ScheduleContract.PATH_CAPTION, URI_WITHOUT_PATH);
        mUriMatcher.addURI(ScheduleContract.CONTENT_AUTHORITY, ScheduleContract.PATH_CAPTION + "/#", URI_WITH_PATH);
    }

    @Override
    public boolean onCreate() {

        mDatabaseHelper = new ScheduleDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        mDatabase = mDatabaseHelper.getReadableDatabase();
        Cursor cursor;

        switch (mUriMatcher.match(uri))
        {
            case URI_WITHOUT_PATH :
                cursor = mDatabase.query(ScheduleEntry.TABLE_NAME, projection, selection, selectionArgs, null,null,sortOrder);
                break;

            case URI_WITH_PATH :
                selection = ScheduleEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = mDatabase.query(ScheduleEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default : throw new IllegalArgumentException("Cannot serve URI request at this moment.");
        }

        if(getContext() != null)
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        mDatabase = mDatabaseHelper.getWritableDatabase();

        long colID;
        switch (mUriMatcher.match(uri)) {
            case URI_WITHOUT_PATH:
                colID = mDatabase.insert(ScheduleEntry.TABLE_NAME, null, values);
                if (colID == -1)
                    Toast.makeText(getContext(), "Failed to insert", Toast.LENGTH_SHORT).show();
                else {
                    if(getContext() != null)
                        getContext().getContentResolver().notifyChange(uri, null);
                }
                break;

            default:
                throw new IllegalArgumentException("Cannot serve URI request at this moment.");
        }

        return ContentUris.withAppendedId(uri, colID);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        mDatabase = mDatabaseHelper.getWritableDatabase();
        int noOfRowsDeleted = 0;

        switch (mUriMatcher.match(uri)) {

            case URI_WITH_PATH:

                selection = ScheduleEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                noOfRowsDeleted = mDatabase.delete(ScheduleEntry.TABLE_NAME,selection, selectionArgs);

                if(getContext() != null)
                    getContext().getContentResolver().notifyChange(uri, null);
                break;

            case URI_WITHOUT_PATH:

                noOfRowsDeleted = mDatabase.delete(ScheduleEntry.TABLE_NAME, null, null);

                if(getContext() != null)
                    getContext().getContentResolver().notifyChange(uri, null);
                break;
        }

        return noOfRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int noOfRowsUpdated = 0;
        mDatabase = mDatabaseHelper.getWritableDatabase();

        switch (mUriMatcher.match(uri)) {

            case URI_WITH_PATH:

                selection = ScheduleEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                noOfRowsUpdated = mDatabase.update(ScheduleEntry.TABLE_NAME, values, selection, selectionArgs);

                if(noOfRowsUpdated>0 && getContext() != null)
                    getContext().getContentResolver().notifyChange(uri, null);
        }

        return noOfRowsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
