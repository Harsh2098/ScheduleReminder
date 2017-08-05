package com.hmproductions.schedulereminder.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.hmproductions.schedulereminder.R;
import com.hmproductions.schedulereminder.data.Schedule;
import com.hmproductions.schedulereminder.data.ScheduleContract;
import com.hmproductions.schedulereminder.data.ScheduleContract.ScheduleEntry;
import com.hmproductions.schedulereminder.ui.MainActivity;

import java.util.Calendar;

/**
 * Created by Harsh Mahajan on 5/8/2017.
 *
 * This alarm triggers at 7 AM everyday and issues notifications about tasks to be completed on that day.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private static final int RC_NOTIFICATION_CLICK = 101;
    private static final int NOTIFICATION_ID = 4;
    private String[] weekdays = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void onReceive(final Context context, Intent intent) {

        AsyncTask<Void, Void, Void> mBackgroundTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                String currentWeekday = weekdays[calendar.get(Calendar.DAY_OF_WEEK) - 1];

                Cursor cursor = context.getContentResolver().query(ScheduleContract.CONTENT_URI, null, null, null, ScheduleEntry.COLUMN_TIME);

                if(cursor != null) {

                    while (cursor.moveToNext()) {
                        Schedule schedule = new Schedule(
                                cursor.getLong(cursor.getColumnIndexOrThrow(ScheduleEntry.COLUMN_ID)),
                                cursor.getString(cursor.getColumnIndexOrThrow(ScheduleEntry.COLUMN_DAY)),
                                cursor.getString(cursor.getColumnIndexOrThrow(ScheduleEntry.COLUMN_NAME)),
                                cursor.getString(cursor.getColumnIndexOrThrow(ScheduleEntry.COLUMN_TIME))
                        );

                        if(schedule.getDay().equalsIgnoreCase(currentWeekday)) {
                            stringBuilder.append(schedule.getName()).append("\n");
                        }
                    }
                    cursor.close();
                }

                if(!stringBuilder.toString().isEmpty() && !stringBuilder.toString().equals("")) {

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                    builder
                            .setContentIntent(contentIntent(context))
                            .setContentTitle("Today's Tasks")
                            .setSmallIcon(R.mipmap.ic_notification_icon)
                            .setContentText(stringBuilder.toString())
                            .setAutoCancel(true);

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                }
                return null;
            }
        };

        mBackgroundTask.execute();
    }

    private PendingIntent contentIntent(Context context) {

        Intent intent = new Intent(context, MainActivity.class);

        return PendingIntent.getActivity(
                context,
                RC_NOTIFICATION_CLICK,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }
}
