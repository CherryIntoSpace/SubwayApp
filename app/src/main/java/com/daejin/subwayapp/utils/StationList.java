package com.daejin.subwayapp.utils;

import com.daejin.subwayapp.R;

public class StationList {
    private int search_Icon;
    private String station_code;
    private String station_name;
    private String line_number;

    public int getSearch_Icon() {
        return R.drawable.baseline_search_24;
    }

    public void setSearch_Icon() {
        this.search_Icon = R.drawable.baseline_search_24;
    }

    public String getStation_code() {
        return station_code;
    }

    public void setStation_code(String station_code) {
        this.station_code = station_code;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public String getLine_number() {
        return line_number;
    }

    public void setLine_number(String line_number) {
        this.line_number = line_number;
    }

    public StationList(int search_Icon, String station_code, String station_name, String line_number) {
        this.search_Icon = R.drawable.baseline_search_24;
        this.station_code = station_code;
        this.station_name = station_name;
        this.line_number = line_number;
    }

    public StationList() {
        this.search_Icon = R.drawable.baseline_search_24;
        this.station_code = station_code;
        this.station_name = station_name;
        this.line_number = line_number;
    }
}
