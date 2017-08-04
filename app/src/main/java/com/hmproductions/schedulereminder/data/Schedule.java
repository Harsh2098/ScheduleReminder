package com.hmproductions.schedulereminder.data;

/**
 * Created by harsh on 7/31/17.
 *
 * Schedule holds weekday and title of the task.
 */

public class Schedule {

    private String mDay, mName;

    public Schedule(String day, String name) {
        mDay = day;
        mName = name;
    }

    public String getDay() {
        return mDay;
    }

    public String getName() {
        return mName;
    }

}
