package main.models;

import java.time.LocalDate;

public class Patient {
    private final String name;
    private final LocalDate birthday;
    private final IntakeForm intakeForm;      // Patient 1 -> 1 IntakeForm (A1 Part 2)
    private TriageAssessment assessment;      // Patient 1 -> 0..1 TriageAssessment (A1 Part 2)

    public Patient(String name, LocalDate birthday, IntakeForm intakeForm) {
        this.name = name;
        this.birthday = birthday;
        this.intakeForm = intakeForm;
    }

    public String getName() { return name; }
    public LocalDate getBirthday() { return birthday; }
    public IntakeForm getIntakeForm() { return intakeForm; }
    public TriageAssessment getAssessment() { return assessment; }

    /** A patient has been reviewed once they have an assessment. */
    public boolean isReviewed() { return assessment != null; }

    void setAssessment(TriageAssessment assessment) { this.assessment = assessment; }
}
