package com.example.brhee.allergysnap;

import java.util.ArrayList;

public class User {
    protected String username, email, DOB, fName, lName;
    protected boolean hasPFP = false;
    protected String uri;
    protected ArrayList<Allergy> allergies;


    public User() {
        this.allergies = new ArrayList<>();
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }


}
