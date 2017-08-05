package com.hmproductions.schedulereminder.data;

/**
 * Created by harsh on 7/31/17.
 *
 * Schedule holds weekday and title of the task.
 */

public class Schedule {

    private String mDay, mName, mTime;

    public Schedule(String day, String name, String time) {
        mDay = day;
        mName = name;
        mTime = time;
    }

    public String getDay() {
        return mDay;
    }

    public String getName() {
        return mName;
    }

    public String getTime() {
        return mTime;
    }
}
