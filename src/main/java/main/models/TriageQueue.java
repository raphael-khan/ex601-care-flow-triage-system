package main.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TriageQueue {
    private final List<Patient> waitingPatients = new ArrayList<>();
    private final List<PriorityChangeRecord> auditLog = new ArrayList<>();
    private final PriorityOrderingStrategy ordering;

    public TriageQueue(PriorityOrderingStrategy ordering) {
        this.ordering = ordering;
    }

    public void addToQueue(Patient patient) {
        waitingPatients.add(patient);
        sortByPriority();
    }

    public void sortByPriority() {
        waitingPatients.sort(ordering.comparator());
    }

    /** The assessment already carries the changing nurse and time (see D1 fix). */
    public void logPriorityChange(TriageAssessment assessment, int oldPriorityLevel) {
        auditLog.add(new PriorityChangeRecord(assessment.getPatient(), oldPriorityLevel,
                assessment.getPriorityLevel(), assessment.getAssignedBy(), assessment.getAssignedAt()));
    }

    public List<Patient> getWaitingPatients() { return Collections.unmodifiableList(waitingPatients); }
    public List<PriorityChangeRecord> getAuditLog() { return Collections.unmodifiableList(auditLog); }
}
