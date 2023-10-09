package com.daejin.subwayapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daejin.subwayapp.R;

import java.util.ArrayList;

public class StationAdapter extends RecyclerView.Adapter {
    private ArrayList<StationList> sList = new ArrayList<>();

    public interface onItemClickListener {
        void onItemClicked(int pos, String code, String num);
    }

    private onItemClickListener itemClickListener;

    public void setOnItemClickListener(onItemClickListener listener) {
        itemClickListener = listener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.station_list, parent, false);
        StationInfoViewholder viewHolder = new StationInfoViewholder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = "";
                String num = "";
                int pos = viewHolder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    code = viewHolder.getCodeView().getText().toString();
                    num = viewHolder.getLineView().getText().toString();
                }
                itemClickListener.onItemClicked(pos, code, num);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StationInfoViewholder stationInfoViewholder = (StationInfoViewholder) holder;
        stationInfoViewholder.onBind(sList.get(position));
    }

    @Override
    public int getItemCount() {
        return sList.size();
    }

    public void setsList(ArrayList<StationList> list) {
        this.sList = list;
        notifyDataSetChanged();
    }
}
