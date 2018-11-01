package com.example.brhee.allergysnap;

public class Medication {
    protected String name, info;
    protected int id;

    public Medication(String name) {
        this.name = name;
    }

    public Medication(String name, String info) {
        this.name = name;
        this.info = info;
    }

    public Medication(String name, String info, int id) {
        this.name = name;
        this.info = info;
        this.id = id;
    }
}
