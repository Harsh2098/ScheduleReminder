package com.hmproductions.schedulereminder.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hmproductions.schedulereminder.R;
import com.hmproductions.schedulereminder.data.Schedule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harsh on 7/31/17.
 *
 * ScheduleRecyclerAdapter to display list of to-do with weekday
 */

public class ScheduleRecyclerAdapter extends RecyclerView.Adapter<ScheduleRecyclerAdapter.ScheduleViewHolder> {

    private static final int WEEKDAYS = 7;
    private String[] weekdays = new String[] {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday","Weekday"};

    private List<Schedule> mData = new ArrayList<>();
    private Context mContext;
    private OnItemClickListener mClickListener;

    public interface OnItemClickListener {
        void onClickListener(int position);
    }

    public ScheduleRecyclerAdapter(List<Schedule> data, Context context, OnItemClickListener listener) {
        mData = data;
        mContext = context;
        mClickListener = listener;
    }

    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View myView = LayoutInflater.from(mContext).inflate(R.layout.schedule_list_item, parent, false);
        return new ScheduleViewHolder(myView);
    }

    @Override
    public void onBindViewHolder(ScheduleViewHolder holder, int position) {

        holder.day_textView.setText(weekdays[position]);

        /*
        *
        * !!!!!!!!!!!!!!!!!!!!  mData.size() > position   !!!!!!!!!!!!!!!!!!!!
        *
        *                       */
        if(mData.size() > position) {
            String tasks = getScheduleList(mData.get(position).getDay());
            holder.name_textView.setText(tasks);
        }
    }

    private String getScheduleList(String weekday) {

        StringBuilder stringBuilder = new StringBuilder();

        for(Schedule schedule : mData) {

            if(schedule.getDay().equals(weekday)) {
                stringBuilder.append(schedule.getName()).append("\n");
            }
        }

        String tasks = stringBuilder.toString();

        Log.v(":::", stringBuilder.toString());

        if(tasks.isEmpty() || tasks.equals(""))
            return "No items to display.";

        return tasks;
    }

    @Override
    public int getItemCount() {
        return WEEKDAYS;
    }

    public void swapData(List<Schedule> data) {

        mData = data;
        notifyDataSetChanged();
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView day_textView;
        TextView name_textView;

        ScheduleViewHolder(View itemView) {
            super(itemView);

            day_textView = (TextView)itemView.findViewById(R.id.weekday_textView);
            name_textView = (TextView)itemView.findViewById(R.id.name_textView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickListener.onClickListener(getAdapterPosition());
        }
    }
}
