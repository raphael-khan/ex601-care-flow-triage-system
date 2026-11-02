package com.careflow.domain;

import java.time.LocalDateTime;

public class TriageAssessment {
    public static final int HIGHEST_PRIORITY = 1;
    public static final int LOWEST_PRIORITY = 5;

    private final int priorityLevel;
    private final LocalDateTime assessmentTime;
    private final String assessmentNotes;

    public TriageAssessment(int priorityLevel, LocalDateTime assessmentTime, String assessmentNotes) {
        if (priorityLevel < HIGHEST_PRIORITY || priorityLevel > LOWEST_PRIORITY) {
            throw new IllegalArgumentException("priorityLevel must be between 1 and 5");
        }
        this.priorityLevel = priorityLevel;
        this.assessmentTime = assessmentTime;
        this.assessmentNotes = assessmentNotes;
    }

    public int getPriorityLevel() { return priorityLevel; }
    public LocalDateTime getAssessmentTime() { return assessmentTime; }
    public String getAssessmentNotes() { return assessmentNotes; }
}
