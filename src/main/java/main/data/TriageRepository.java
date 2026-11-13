package main.data;

import main.models.Patient;
import main.models.TriageAssessment;

import java.util.List;

public interface TriageRepository {
    void save(TriageAssessment assessment);
    /** Patients whose intake form is submitted and who have not been reviewed yet (R1). */
    List<Patient> findPatientsAwaitingReview();
}
