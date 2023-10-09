package com.daejin.subwayapp.utils;

public class StationTime {
    private String arrive_Time;
    private String start_Station;
    private String end_Station;

    public String getArrive_Time() {
        return arrive_Time;
    }

    public void setArrive_Time(String arrive_Time) {
        this.arrive_Time = arrive_Time;
    }

    public String getStart_Station() {
        return start_Station;
    }

    public void setStart_Station(String start_Station) {
        this.start_Station = start_Station;
    }

    public String getEnd_Station() {
        return end_Station;
    }

    public void setEnd_Station(String end_Station) {
        this.end_Station = end_Station;
    }

    public StationTime(String arrive_Time, String start_Station, String end_Station){
        this.arrive_Time = arrive_Time;
        this.start_Station = start_Station;
        this.end_Station = end_Station;
    }

    public StationTime(){
        this.arrive_Time = arrive_Time;
        this.start_Station = start_Station;
        this.end_Station = end_Station;
    }
}
