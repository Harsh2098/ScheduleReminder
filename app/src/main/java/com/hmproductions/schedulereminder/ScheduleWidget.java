package com.hmproductions.schedulereminder;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.hmproductions.schedulereminder.ui.DayActivity;

import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */
public class ScheduleWidget extends AppWidgetProvider {

    private static String[] weekdays = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        String currentWeekday = weekdays[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        views.setTextViewText(R.id.widget_weekday_textView, currentWeekday);

        // Setting adapter to widget GridView.
        views.setRemoteAdapter(R.id.schedule_widget_gridView, new Intent(context, GridWidgetService.class));
        views.setEmptyView(R.id.schedule_widget_gridView, R.id.empty_view_textView);

        PendingIntent appPendingIntent = PendingIntent.getActivity(
                context,
                0,
                new Intent(context, DayActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        views.setPendingIntentTemplate(R.id.schedule_widget_gridView, appPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.schedule_widget_gridView);

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

