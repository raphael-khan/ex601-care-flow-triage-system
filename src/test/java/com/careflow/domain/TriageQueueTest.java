package com.careflow.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TriageQueueTest {

    @Test
    void waitingEntriesAreOrderedByPriorityThenArrivalTime() {
        LocalDateTime base = LocalDateTime.of(2026, 10, 28, 10, 0);
        QueueEntry low = entry(1, 4, base.minusMinutes(20), QueueStatus.WAITING);
        QueueEntry urgentLater = entry(2, 1, base.minusMinutes(5), QueueStatus.WAITING);
        QueueEntry urgentEarlier = entry(3, 1, base.minusMinutes(10), QueueStatus.WAITING);
        QueueEntry evaluated = entry(4, 1, base.minusMinutes(30), QueueStatus.EVALUATED);

        List<QueueEntry> ordered = new TriageQueue(List.of(low, urgentLater, urgentEarlier, evaluated))
                .getWaitingEntriesInPriorityOrder();

        assertEquals(List.of(3L, 2L, 1L),
                ordered.stream().map(QueueEntry::getQueueEntryId).toList());
    }

    private QueueEntry entry(long id, int priority, LocalDateTime entered, QueueStatus status) {
        Patient patient = new Patient(id, "Patient " + id);
        TriageAssessment assessment = new TriageAssessment(priority, entered.minusMinutes(5), "assessment");
        return new QueueEntry(id, patient, assessment, entered, status);
    }
}
