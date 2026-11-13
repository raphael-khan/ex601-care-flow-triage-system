package main.controllers;

import main.data.TriageRepository;
import main.models.IntakeForm;
import main.models.IntakeStatus;
import main.models.Nurse;
import main.models.Patient;
import main.models.PriorityLevel;
import main.models.TriageAssessment;
import main.models.TriageQueue;
import main.models.UnauthorizedNurseException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

public class TriageController {
    private final TriageQueue queue;
    private final TriageRepository repository;
    private final Clock clock;

    public TriageController(TriageQueue queue, TriageRepository repository, Clock clock) {
        this.queue = queue;
        this.repository = repository;
        this.clock = clock;
    }

    /** R1: patients with a submitted intake form who still need review. */
    public List<Patient> getPatientsAwaitingReview() {
        return repository.findPatientsAwaitingReview();
    }

    /** R2: review a patient's intake form and assign a priority. */
    public TriageAssessment reviewIntakeForm(Nurse nurse, Patient patient, int priorityLevel) {
        IntakeForm form = patient.getIntakeForm();
        if (!nurse.isAuthorized()) {
            throw new UnauthorizedNurseException(nurse);
        }
        if (!form.isValid()) {
            throw new IllegalStateException("Intake form is missing required information");
        }
        if (form.getSubmissionStatus() != IntakeStatus.SUBMITTED) {
            throw new IllegalStateException("Intake form has not been submitted");
        }
        if (patient.isReviewed()) { // fix for D2: one assessment per patient; use changePriority
            throw new IllegalStateException("Patient has already been reviewed");
        }
        if (!PriorityLevel.isValid(priorityLevel)) {
            throw new IllegalArgumentException("Priority must be 1-5, was " + priorityLevel);
        }
        TriageAssessment assessment = nurse.recordAssessment(patient, priorityLevel, now());
        repository.save(assessment);
        queue.addToQueue(patient);
        return assessment;
    }

    /**
     * R3: change an existing priority.
     * Returns true if the priority changed, false if the new level equals the current one (fix for D3).
     */
    public boolean changePriority(Nurse nurse, TriageAssessment assessment, int newPriorityLevel) {
        if (!nurse.isAuthorized()) {
            throw new UnauthorizedNurseException(nurse);
        }
        if (!PriorityLevel.isValid(newPriorityLevel)) {
            throw new IllegalArgumentException("Priority must be 1-5, was " + newPriorityLevel);
        }
        int oldLevel = assessment.getPriorityLevel();
        if (newPriorityLevel == oldLevel) {
            return false;
        }
        assessment.assignPriority(newPriorityLevel, nurse, now());
        queue.logPriorityChange(assessment, oldLevel);
        queue.sortByPriority();
        return true;
    }

    private LocalDateTime now() {
        return LocalDateTime.now(clock);
    }
}
