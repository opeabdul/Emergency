package com.example.emergency.common;

public class User {
    private String name, phoneNumber,image, occupation, age;
    private MyLocation location;

    public User(String name, String phoneNumber, MyLocation location, String image, String occupation, String age) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.image = image;
        this.occupation = occupation;
        this.age = age;
    }

    public User(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public MyLocation getLocation() {
        return location;
    }

    public void setLocation(MyLocation location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
