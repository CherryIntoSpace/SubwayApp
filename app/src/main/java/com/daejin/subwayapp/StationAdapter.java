package com.daejin.subwayapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder> {
    private ArrayList<StationList> sList = new ArrayList<>();

    public interface onItemClickListener {
        void onItemClicked(int pos, String data);
    }

    private onItemClickListener itemClickListener;

    public void setOnItemClickListener(onItemClickListener listener) {
        itemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_searchIcon;
        TextView tv_scode;
        TextView tv_sname;
        TextView tv_linenum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_searchIcon = itemView.findViewById(R.id.iv_searchIcon);
            tv_scode = itemView.findViewById(R.id.tv_scode);
            tv_sname = itemView.findViewById(R.id.tv_sname);
            tv_linenum = itemView.findViewById(R.id.tv_linenum);
        }

        public TextView getCodeView() {
            return tv_scode;
        }

        void onBind(StationList stationList) {
            iv_searchIcon.setImageResource(stationList.getSearch_Icon());
            tv_scode.setText(stationList.getStation_code());
            tv_sname.setText(stationList.getStation_name());
            tv_linenum.setText(stationList.getLine_number());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.station_list, parent, false);
        StationAdapter.ViewHolder viewHolder = new StationAdapter.ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = "";
                int pos = viewHolder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    data = viewHolder.getCodeView().getText().toString();
                }
                itemClickListener.onItemClicked(pos, data);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(sList.get(position));
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
