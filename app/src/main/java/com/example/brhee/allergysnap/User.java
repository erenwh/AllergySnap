package com.example.brhee.allergysnap;

public class User {
    protected String username, email, DOB, fName, lName;
    protected boolean hasPFP = false;
    public User() { }


    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }


}
