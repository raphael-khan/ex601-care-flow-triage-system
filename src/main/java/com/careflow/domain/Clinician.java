package com.careflow.domain;

public class Clinician {
    private final long clinicianId;
    private final String name;
    private final boolean evaluationAuthorized;

    public Clinician(long clinicianId, String name, boolean evaluationAuthorized) {
        this.clinicianId = clinicianId;
        this.name = name;
        this.evaluationAuthorized = evaluationAuthorized;
    }

    public long getClinicianId() { return clinicianId; }
    public String getName() { return name; }
    public boolean canEvaluatePatients() { return evaluationAuthorized; }
}
