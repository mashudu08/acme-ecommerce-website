package com.example.beaconfinalapp.Models;

public class UserModel {

    private boolean isImperial;
    private String name;
    private String homeAddress;

    public boolean isImperial() {
        return isImperial;
    }

    public void setImperial(boolean imperial) {
        isImperial = imperial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }
}
