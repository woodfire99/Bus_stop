package com.example.project.bus;

public class BusStation {
    int ID;
    String stationName, si_gun_gu, eup_myeon_dong;
    double longitude, latitude;

    public BusStation(){

    }

    public BusStation(int id, String name){
        this.ID = id;
        this.stationName = name;
    }

    public String toName(){
        return this.getStationName() + "-" + this.getID();
    }

    public void setSi_gun_gu(String si_gun_gu) {
        this.si_gun_gu = si_gun_gu;
    }

    public void setEup_myeon_dong(String eup_myeon_dong) {
        this.eup_myeon_dong = eup_myeon_dong;
    }

    public String getSi_gun_gu() {
        return si_gun_gu;
    }

    public String getEup_myeon_dong() {
        return eup_myeon_dong;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getID() {
        return ID;
    }

    public String getStationName() {
        return stationName;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
