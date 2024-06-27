package com.example.shivambhardwaj.shitube.Models;

public class UsersModel {
    private String UserName ,email,profile;

    public UsersModel(String userName,  String email) {
        UserName = userName;
        this.email = email;
    }

    public UsersModel() {
    }


    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
