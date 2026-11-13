package com.careflow.domain;

import java.time.LocalDateTime;

public class QueueEntry {
    private final long queueEntryId;
    private final Patient patient;
    private final TriageAssessment currentAssessment;
    private final LocalDateTime enteredQueueAt;
    private QueueStatus queueStatus;

    public QueueEntry(long queueEntryId, Patient patient, TriageAssessment currentAssessment,
                      LocalDateTime enteredQueueAt, QueueStatus queueStatus) {
        this.queueEntryId = queueEntryId;
        this.patient = patient;
        this.currentAssessment = currentAssessment;
        this.enteredQueueAt = enteredQueueAt;
        this.queueStatus = queueStatus;
    }

    public long getQueueEntryId() { return queueEntryId; }
    public Patient getPatient() { return patient; }
    public TriageAssessment getCurrentAssessment() { return currentAssessment; }
    public LocalDateTime getEnteredQueueAt() { return enteredQueueAt; }
    public QueueStatus getQueueStatus() { return queueStatus; }
    public boolean isWaiting() { return queueStatus == QueueStatus.WAITING; }
    public int getPriority() { return currentAssessment.getPriorityLevel(); }

    public void markEvaluated() {
        if (!isWaiting()) {
            throw new IllegalStateException("Only waiting entries can be evaluated");
        }
        queueStatus = QueueStatus.EVALUATED;
    }
}
