package com.example.brhee.allergysnap;

public class Medication {
    protected String name, info;
    protected int id;

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

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Medication){
            Medication p = (Medication) o;
            return this.name.equals(p.getName());
        } else
            return false;
    }
}
