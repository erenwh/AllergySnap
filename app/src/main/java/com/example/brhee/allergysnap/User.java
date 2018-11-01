package com.example.brhee.allergysnap;

import android.net.Uri;

import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class User {
    protected String username, email, DOB, fName, lName;
    protected boolean hasPFP = false;
    protected String uri;
    protected ArrayList<Medication> medications;

    public User() { }


    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }


}
