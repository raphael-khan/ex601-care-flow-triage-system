package com.careflow.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TriageAssessmentTest {
    private final LocalDateTime now = LocalDateTime.of(2026, 10, 28, 12, 0);

    @Test
    void priorityBelowLowerBoundaryIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new TriageAssessment(0, now, "invalid"));
    }

    @Test
    void priorityAtLowerBoundaryIsAccepted() {
        assertEquals(1, new TriageAssessment(1, now, "urgent").getPriorityLevel());
    }

    @Test
    void priorityJustAboveLowerBoundaryIsAccepted() {
        assertEquals(2, new TriageAssessment(2, now, "high").getPriorityLevel());
    }

    @Test
    void priorityJustBelowUpperBoundaryIsAccepted() {
        assertEquals(4, new TriageAssessment(4, now, "low").getPriorityLevel());
    }

    @Test
    void priorityAtUpperBoundaryIsAccepted() {
        assertEquals(5, new TriageAssessment(5, now, "lowest").getPriorityLevel());
    }

    @Test
    void priorityAboveUpperBoundaryIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new TriageAssessment(6, now, "invalid"));
    }
}
