package com.careflow.domain;

public class Patient {
    private final long patientId;
    private final String name;

    public Patient(long patientId, String name) {
        this.patientId = patientId;
        this.name = name;
    }

    public long getPatientId() { return patientId; }
    public String getName() { return name; }
}
