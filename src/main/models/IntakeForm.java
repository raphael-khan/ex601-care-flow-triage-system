package main.models;

import java.time.LocalDateTime;

public class IntakeForm {

    private final String intakeId;
    private String gender;
    private String insuranceName;
    private String insuranceNumber;
    private String reasonForVisit;
    private IntakeStatus submissionStatus;
    private LocalDateTime submittedAt;

    public IntakeForm(
            String intakeId,
            String gender,
            String insuranceName,
            String insuranceNumber,
            String reasonForVisit) {

        this.intakeId = intakeId;
        this.gender = gender;
        this.insuranceName = insuranceName;
        this.insuranceNumber = insuranceNumber;
        this.reasonForVisit = reasonForVisit;
        this.submissionStatus = IntakeStatus.DRAFT;
    }

    public boolean updateDetails(
            String gender,
            String insuranceName,
            String insuranceNumber,
            String reasonForVisit) {

        if (submissionStatus != IntakeStatus.DRAFT) {
            return false;
        }

        this.gender = gender;
        this.insuranceName = insuranceName;
        this.insuranceNumber = insuranceNumber;
        this.reasonForVisit = reasonForVisit;

        return true;
    }

    public boolean isValid() {
        if (gender == null || gender.isBlank()) {
            return false;
        }

        if (insuranceName == null || insuranceName.isBlank()) {
            return false;
        }

        if (insuranceNumber == null || insuranceNumber.isBlank()) {
            return false;
        }

        if (reasonForVisit == null || reasonForVisit.isBlank()) {
            return false;
        }

        return true;
    }

    public boolean submit() {
        if (submissionStatus != IntakeStatus.DRAFT) {
            return false;
        }

        if (!isValid()) {
            return false;
        }

        submissionStatus = IntakeStatus.SUBMITTED;
        submittedAt = LocalDateTime.now();

        return true;
    }

    public String getIntakeId() {
        return intakeId;
    }

    public String getGender() {
        return gender;
    }

    public String getInsuranceName() {
        return insuranceName;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public String getReasonForVisit() {
        return reasonForVisit;
    }

    public IntakeStatus getSubmissionStatus() {
        return submissionStatus;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
}
