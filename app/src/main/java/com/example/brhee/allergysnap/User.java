package com.example.brhee.allergysnap;

public class User {
    protected String username, email, DOB, fName, lName;
    protected boolean validName = false;
    protected boolean hasPFP = false;
    protected boolean validDOB = false;

    public User() { }


    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }


}
