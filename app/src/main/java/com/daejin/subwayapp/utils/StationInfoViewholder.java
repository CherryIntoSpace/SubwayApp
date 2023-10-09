package com.daejin.subwayapp.utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daejin.subwayapp.R;

public class StationInfoViewholder extends RecyclerView.ViewHolder {

    ImageView iv_searchIcon;
    TextView tv_scode;
    TextView tv_sname;
    TextView tv_linenum;

    public StationInfoViewholder(@NonNull View itemView) {
        super(itemView);
        iv_searchIcon = itemView.findViewById(R.id.iv_searchIcon);
        tv_scode = itemView.findViewById(R.id.tv_scode);
        tv_sname = itemView.findViewById(R.id.tv_sname);
        tv_linenum = itemView.findViewById(R.id.tv_linenum);
    }

    void onBind(StationList stationList) {
        iv_searchIcon.setImageResource(stationList.getSearch_Icon());
        tv_scode.setText(stationList.getStation_code());
        tv_sname.setText(stationList.getStation_name());
        tv_linenum.setText(stationList.getLine_number());
    }

    public TextView getCodeView() {
        return tv_scode;
    }

    public TextView getLineView() {
        return tv_linenum;
    }
}
