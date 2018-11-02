package com.example.brhee.allergysnap;

import java.util.ArrayList;

public class MedicationConflict {
    public ArrayList<String> conflictDrugs;
    public String description;
    public String severity;

    public MedicationConflict(ArrayList<String> conflictDrugs, String description, String severity) {

        this.conflictDrugs = conflictDrugs;
        this.description = description;
        this.severity = severity;
    }

    public MedicationConflict(ArrayList<String> conflictDrugs, String description) {

        this.conflictDrugs = conflictDrugs;
        this.description = description;
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
