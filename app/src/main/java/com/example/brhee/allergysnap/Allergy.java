package com.example.brhee.allergysnap;

/*
    Allergy Class include information written below:
    1. name : allergy name
    2. id : allergy id
    3. allergyType : allegyType (enum : Food, Drug, Environmental, Contact)
    4. info : additional information of allergy
 */


public class Allergy extends UserItem {
    protected AllergyType allergyType;


    public Allergy() {
    }

    public Allergy(String name) {
        this.name = name;
    }

    public Allergy(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public Allergy(String name, int id, AllergyType allergyType) {
        this.name = name;
        this.id = id;
        this.allergyType = allergyType;
    }

    public Allergy(String name, int id, AllergyType allergyType, String info) {
        this.name = name;
        this.id = id;
        this.allergyType = allergyType;
        this.info = info;
    }

    public Allergy(String name, AllergyType allergyType) {
        this.name = name;
        this.allergyType = allergyType;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Allergy){
            Allergy al = (Allergy) o;
            return this.name.equals(al.getName());
        } else{
            return false;
        }
    }
}
