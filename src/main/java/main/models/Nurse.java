package main.models;

import java.time.LocalDateTime;

public class Nurse {
    private final String name;
    private final int nurseId;
    private final boolean authorized;

    public Nurse(String name, int nurseId, boolean authorized) {
        this.name = name;
        this.nurseId = nurseId;
        this.authorized = authorized;
    }

    /** Creator (GRASP): the nurse creates the assessment they perform. */
    public TriageAssessment recordAssessment(Patient patient, int priorityLevel, LocalDateTime at) {
        TriageAssessment assessment = new TriageAssessment(patient, this, priorityLevel, at);
        patient.setAssessment(assessment);
        return assessment;
    }

    public String getName() { return name; }
    public int getNurseId() { return nurseId; }
    public boolean isAuthorized() { return authorized; }
}
