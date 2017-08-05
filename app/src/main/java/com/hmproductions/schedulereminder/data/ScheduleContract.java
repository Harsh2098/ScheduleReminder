package com.hmproductions.schedulereminder.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by harsh on 7/31/17.
 *
 * ScheduleContract holds the column fields in the 'schedule' table.
 */

public class ScheduleContract {

    static final String CONTENT_AUTHORITY = "com.hmproductions.schedulereminder";
    static final String PATH_CAPTION = "schedule";

    private static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_CAPTION);

    public class ScheduleEntry implements BaseColumns
    {
        static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_DAY = "weekday";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_PRIORITY = "priority";
        static final String TABLE_NAME = "schedule";
    }
}
