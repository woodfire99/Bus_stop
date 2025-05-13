package com.example.project.bus;

import android.graphics.Color;

public class Bus {
    String destination; // 종점
    int extimeMin; // 남은 예상시간
    int routeCD; // 노선 버스 ID
    int routeNO; // 노선 버스 번호
    int msgNum; // 메세지 번호
    int color;

    private int setColor(){
        if(isRed()){
            return Color.rgb(255, 102, 102);
        }
        else if(isYellow()){
            return Color.rgb(255,255,153);
        }
        else{
            return Color.rgb(102,102,204);
        }
    }

    private boolean isRed(){
        int[] redList = {30300100, 30300104, 30300002, 30300128, 93000077}; // 급행버스
        for(int i=0; i< redList.length; i++){
            if(routeCD == redList[i])return true;
        }
        return false;
    }

    private boolean isYellow(){
        int[] yellowList = {30300001, 30300003, 30300004}; // 유성-마을버스
        for(int i=0; i< yellowList.length; i++){
            if(routeCD == yellowList[i])return true;
        }
        return false;
    }

    // constructor
    public Bus(String des, int extime, int cd, int no, int msgNum){
        this.destination = des;
        this.extimeMin = extime;
        this.routeCD = cd;
        this.routeNO = no;
        this.msgNum = msgNum;
        this.color = setColor();
    }

    // getter
    public int getColor(){
        return color;
    }

    public String getDestination() {
        return destination;
    }

    public int getExtimeMin() {
        return extimeMin;
    }

    public int getRouteCD() {
        return routeCD;
    }

    public int getRouteNO() {
        return routeNO;
    }

    public int getMsgNum() {
        return msgNum;
    }
}