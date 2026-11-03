package com.careflow.domain;

import java.time.LocalDateTime;

public class Evaluation {
    private final long queueEntryId;
    private final long clinicianId;
    private final long patientId;
    private final LocalDateTime evaluatedAt;
    private final String outcome;
    private final QueueStatus resultingStatus;

    public Evaluation(long queueEntryId, long clinicianId, long patientId,
                      LocalDateTime evaluatedAt, String outcome, QueueStatus resultingStatus) {
        this.queueEntryId = queueEntryId;
        this.clinicianId = clinicianId;
        this.patientId = patientId;
        this.evaluatedAt = evaluatedAt;
        this.outcome = outcome;
        this.resultingStatus = resultingStatus;
    }

    public long getQueueEntryId() { return queueEntryId; }
    public long getClinicianId() { return clinicianId; }
    public long getPatientId() { return patientId; }
    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
    public String getOutcome() { return outcome; }
    public QueueStatus getResultingStatus() { return resultingStatus; }
}
