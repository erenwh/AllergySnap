package com.example.brhee.allergysnap;

public class Medication extends UserItem {

    public Medication() { }

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

    public Medication(String name, int id) {
        this.id = id;
        this.name = name;
    }
}
