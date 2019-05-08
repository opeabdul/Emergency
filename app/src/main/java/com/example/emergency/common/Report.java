package com.example.emergency.common;



public class Report {

    private String emergencyType, emergencyStatus, emergencyTimeStamp;

    public Report(String emergencyType, String emergencyStatus, String emergencyTimeStamp) {
        this.emergencyType = emergencyType;
        this.emergencyStatus = emergencyStatus;
        this.emergencyTimeStamp = emergencyTimeStamp;
    }

    public String getEmergencyType() {
        return emergencyType;
    }

    public void setEmergencyType(String emergencyType) {
        this.emergencyType = emergencyType;
    }

    public String getEmergencyStatus() {
        return emergencyStatus;
    }

    public void setEmergencyStatus(String emergencyStatus) {
        this.emergencyStatus = emergencyStatus;
    }

    public String getEmergencyTimeStamp() {
        return emergencyTimeStamp;
    }

    public void setEmergencyTimeStamp(String emergencyTimeStamp) {
        this.emergencyTimeStamp = emergencyTimeStamp;
    }

}
