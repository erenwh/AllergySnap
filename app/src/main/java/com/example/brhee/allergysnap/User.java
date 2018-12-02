package com.example.brhee.allergysnap;

import java.util.ArrayList;
import java.util.Collections;

public class User {
    protected String username, email, DOB, fName, lName;
    protected boolean hasPFP = false;
    protected String uri;
    protected ArrayList<Allergy> allergies;
    protected ArrayList<Medication> medications;
    protected ArrayList<Integer> scans;

    public User() {
        medications = new ArrayList<>();
        allergies = new ArrayList<>();
        scans = new ArrayList<Integer>(Collections.nCopies(3, 0));
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        medications = new ArrayList<>();
    }


}
