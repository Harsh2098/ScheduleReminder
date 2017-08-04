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
 * Created by harsh on 8/4/17.
 *
 * Adapter to bind views for each day.
 */

public class DayRecyclerAdapter extends RecyclerView.Adapter<DayRecyclerAdapter.DayViewHolder> {

    private List<Schedule> mData = new ArrayList<>();
    private Context mContext;

    private DayListClickListener mListener;

    public interface DayListClickListener {
        void onDayClick(int position);
    }

    public DayRecyclerAdapter(Context context, List<Schedule> list, DayListClickListener listener) {

        mContext = context;
        mData = list;
        mListener = listener;
    }

    @Override
    public DayRecyclerAdapter.DayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new DayViewHolder(LayoutInflater.from(mContext).inflate(R.layout.day_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(DayRecyclerAdapter.DayViewHolder holder, int position) {

        if(mData.size() > 0) {
            holder.title_textView.setText(mData.get(position).getName());
        }
    }

    @Override
    public int getItemCount() {
        if(mData == null) return 0;
        else return mData.size();
    }

    public void swapData(List<Schedule> data) {

        mData = data;
        notifyDataSetChanged();
    }

    class DayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title_textView;

        DayViewHolder(View itemView) {
            super(itemView);

            title_textView = (TextView)itemView.findViewById(R.id.title_textView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onDayClick(getAdapterPosition());
        }
    }
}
