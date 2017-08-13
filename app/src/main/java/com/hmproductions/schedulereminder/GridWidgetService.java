package com.hmproductions.schedulereminder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.hmproductions.schedulereminder.data.Schedule;
import com.hmproductions.schedulereminder.data.ScheduleContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Harsh Mahajan on 13/8/2017.
 *
 * Grid Widget Service works like an adapter for schedule gridView in the widget.
 */

public class GridWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<Schedule> mData = new ArrayList<>();

    private String[] weekdays = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    GridRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        mData.clear();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        String currentWeekday = weekdays[calendar.get(Calendar.DAY_OF_WEEK) - 1];

        Cursor cursor = mContext.getContentResolver().query(ScheduleContract.CONTENT_URI, null, null, null, ScheduleContract.ScheduleEntry.COLUMN_TIME);

        if(cursor != null) {

            while (cursor.moveToNext()) {
                Schedule schedule = new Schedule(
                        cursor.getLong(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_DAY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_TIME))
                );

                /* Add to the list iff today's weekday is equal to schedule's weekday */
                if(schedule.getDay().equalsIgnoreCase(currentWeekday)) {
                    mData.add(schedule);
                }
            }
            cursor.close();
        }
    }

    @Override
    public void onDestroy() {
        mData.clear();
    }

    @Override
    public int getCount() {
        if(mData == null) return 0;
        return mData.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if(mData == null || mData.size() == 0) return null;

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        Log.v(":::",mData.get(position).getName());
        remoteViews.setTextViewText(R.id.title_textView, mData.get(position).getName());
        remoteViews.setTextViewText(R.id.time_textView, mData.get(position).getTime());
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}