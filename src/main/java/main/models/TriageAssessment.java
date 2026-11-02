package main.models;

import java.time.LocalDateTime;

public class TriageAssessment {
    private final Patient patient;
    private Nurse assignedBy;
    private int priorityLevel;
    private LocalDateTime assignedAt;

    public TriageAssessment(Patient patient, Nurse assignedBy, int priorityLevel, LocalDateTime assignedAt) {
        this.patient = patient;
        this.assignedBy = assignedBy;
        this.priorityLevel = priorityLevel;
        this.assignedAt = assignedAt;
    }

    /** A change also records who made it and when (fix for defect D1). */
    public void assignPriority(int priorityLevel, Nurse changedBy, LocalDateTime changedAt) {
        this.priorityLevel = priorityLevel;
        this.assignedBy = changedBy;
        this.assignedAt = changedAt;
    }

    public Patient getPatient() { return patient; }
    public Nurse getAssignedBy() { return assignedBy; }
    public int getPriorityLevel() { return priorityLevel; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
}
