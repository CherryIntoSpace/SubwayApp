package com.daejin.subwayapp.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.list.StationTime;

public class StationScheduleViewholder extends RecyclerView.ViewHolder {

    TextView tv_time;
    TextView tv_sStation;
    TextView tv_eStation;

    public StationScheduleViewholder(@NonNull View itemView) {
        super(itemView);
        tv_time = itemView.findViewById(R.id.tv_time);
        tv_sStation = itemView.findViewById(R.id.tv_sStation);
        tv_eStation = itemView.findViewById(R.id.tv_eStation);
    }

    public void onBind(StationTime stationTime){
        tv_time.setText(stationTime.getArrive_Time());
        tv_sStation.setText(stationTime.getStart_Station());
        tv_eStation.setText(stationTime.getEnd_Station());
    }
}
