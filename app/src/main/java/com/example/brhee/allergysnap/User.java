package com.example.brhee.allergysnap;

public class User {
    protected String username, email, DOB, fName, lName;
    protected boolean validName = false;
    protected boolean hasPFP = false;

    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User(String username, String email, String DOB, String fName, String lName) {
        this.username = username;
        this.email = email;
        this.DOB = DOB;
        this.fName = fName;
        this.lName = lName;
    }
}
