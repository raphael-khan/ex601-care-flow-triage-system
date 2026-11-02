package main.data;

import main.models.IntakeStatus;
import main.models.Patient;
import main.models.TriageAssessment;

import java.util.ArrayList;
import java.util.List;

/** In-memory stand-in for the database until CP2. */
public class InMemoryTriageRepository implements TriageRepository {
    private final List<Patient> patients = new ArrayList<>();
    private final List<TriageAssessment> assessments = new ArrayList<>();

    public void addPatient(Patient patient) { patients.add(patient); }

    @Override
    public void save(TriageAssessment assessment) { assessments.add(assessment); }

    @Override
    public List<Patient> findPatientsAwaitingReview() {
        return patients.stream()
                .filter(p -> p.getIntakeForm().getSubmissionStatus() == IntakeStatus.SUBMITTED)
                .filter(p -> !p.isReviewed())
                .toList();
    }

    public List<TriageAssessment> getSavedAssessments() { return List.copyOf(assessments); }
}
