package com.daejin.subwayapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.viewholders.StationScheduleViewholder;
import com.daejin.subwayapp.list.StationTime;

import java.util.ArrayList;

public class TimeAdapter extends RecyclerView.Adapter {
    private ArrayList<StationTime> slist = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.station_schedule, parent, false);
        StationScheduleViewholder viewholder = new StationScheduleViewholder(view);

        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StationScheduleViewholder stationScheduleViewholder = (StationScheduleViewholder) holder;
        stationScheduleViewholder.onBind(slist.get(position));
    }

    @Override
    public int getItemCount() {
        return slist.size();
    }

    public void setsList(ArrayList<StationTime> list) {
        this.slist = list;
        notifyDataSetChanged();
    }
}
