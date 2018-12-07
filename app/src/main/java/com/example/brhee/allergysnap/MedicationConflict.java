package com.example.brhee.allergysnap;

import android.media.Image;

import java.util.ArrayList;

public class MedicationConflict {
    public ArrayList<String> conflictDrugs;
    public String description;
    public String severity;
    public String source;
    public boolean isAllergy;

    public MedicationConflict() {

    }

    public MedicationConflict(ArrayList<String> conflictDrugs, String description, String severity, String source) {
        this.conflictDrugs = conflictDrugs;
        this.description = description;
        this.severity = severity;
        this.source = source;
    }

    public MedicationConflict(ArrayList<String> conflictDrugs, String description, String source) {

        this.conflictDrugs = conflictDrugs;
        this.description = description;
        this.source = source;
    }

    public ArrayList<String> getConflictDrugs() {
        return conflictDrugs;
    }

    public void setConflictDrugs(ArrayList<String> conflictDrugs) {
        this.conflictDrugs = conflictDrugs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
