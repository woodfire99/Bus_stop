package com.example.project.alarm;

public class Alarm {
    private int stationID, busNO;

    public Alarm(int stationID, int busNO){
        this.busNO = busNO;
        this.stationID = stationID;
    }

    public int getStationID() {
        return stationID;
    }

    public int getBusNO() {
        return busNO;
    }
}
