package com.example.shivambhardwaj.shitube.Models;

public class CreatersModel {
    String Name, UserName, profileImage;
    long joinedOn;
    int subscribersCount;
    String Admin;

    public CreatersModel() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }


    public long getJoinedOn() {
        return joinedOn;
    }

    public void setJoinedOn(long joinedOn) {
        this.joinedOn = joinedOn;
    }

    public int getSubscribersCount() {
        return subscribersCount;
    }

    public void setSubscribersCount(int subscribersCount) {
        this.subscribersCount = subscribersCount;
    }

    public String getAdmin() {
        return Admin;
    }

    public void setAdmin(String admin) {
        Admin = admin;
    }
}
