package com.example.emergency.common;

public class MyLocation {

    private double lat, log;
    private String address, area , city, state;

    public MyLocation(String address, String area, String city, String state) {
        this.address = address;
        this.area = area;
        this.city = city;
        this.state = state;
    }

    public MyLocation(double lat, double log) {
        this.lat = lat;
        this.log = log;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLog() {
        return log;
    }

    public void setLog(double log) {
        this.log = log;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
