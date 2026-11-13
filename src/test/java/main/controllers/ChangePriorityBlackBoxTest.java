package main.controllers;

import main.models.PriorityChangeRecord;
import main.models.PriorityLevel;
import main.models.TriageAssessment;
import main.models.UnauthorizedNurseException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Part 2, Behavior B: change an existing priority.
 * Basis: R3, I1 (CRITICAL on top), I2 (authorized only), I3 (every change is recorded:
 * who, old level, new level, when), scale 1-5.
 */
class ChangePriorityBlackBoxTest {

    // T8 (P1): a different valid level is applied and recorded with the CHANGING nurse and time
    @Test
    void changeIsAppliedAndRecorded() {
        TriageFixture f = new TriageFixture();
        TriageAssessment a = f.reviewed("Pat", 2);           // assessed by nurseA at 09:00
        f.clock.advance(Duration.ofMinutes(15));

        assertTrue(f.controller.changePriority(f.nurseB, a, 4));  // changed by nurseB at 09:15

        assertEquals(4, a.getPriorityLevel());
        List<PriorityChangeRecord> log = f.queue.getAuditLog();
        assertEquals(1, log.size());
        PriorityChangeRecord r = log.get(0);
        assertEquals(2, r.oldLevel());
        assertEquals(4, r.newLevel());
        assertSame(f.nurseB, r.changedBy());
        assertEquals(TriageFixture.NOW_LOCAL.plusMinutes(15), r.changedAt());
    }

    // T9 (P2, I1, boundary 4 -> 5): reaching CRITICAL moves the patient to the top
    @Test
    void changeToCriticalMovesToTop() {
        TriageFixture f = new TriageFixture();
        f.reviewed("Other4", 4);
        TriageAssessment a = f.reviewed("Pat", 3);

        assertTrue(f.controller.changePriority(f.nurseA, a, PriorityLevel.CRITICAL));

        assertSame(a.getPatient(), f.queue.getWaitingPatients().get(0));
    }

    // T10 (boundary 5 -> 4): dropping out of CRITICAL lets an existing CRITICAL patient lead
    @Test
    void changeFromCriticalDropsBelowCritical() {
        TriageFixture f = new TriageFixture();
        TriageAssessment a = f.reviewed("Pat", 5);
        TriageAssessment other = f.reviewed("Other5", 5);

        assertTrue(f.controller.changePriority(f.nurseA, a, 4));

        assertSame(other.getPatient(), f.queue.getWaitingPatients().get(0));
        assertSame(a.getPatient(), f.queue.getWaitingPatients().get(1));
    }

    // T11 (P3, B1/B4): out-of-range new levels are rejected; nothing changes or is logged
    @ParameterizedTest(name = "T11 new level {0} is rejected")
    @ValueSource(ints = {0, 6})
    void invalidNewLevelIsRejected(int level) {
        TriageFixture f = new TriageFixture();
        TriageAssessment a = f.reviewed("Pat", 3);

        assertThrows(IllegalArgumentException.class,
                () -> f.controller.changePriority(f.nurseA, a, level));

        assertEquals(3, a.getPriorityLevel());
        assertTrue(f.queue.getAuditLog().isEmpty());
    }

    // T12 (P4, I2): an unauthorized nurse cannot change priority
    @Test
    void unauthorizedChangeIsRejected() {
        TriageFixture f = new TriageFixture();
        TriageAssessment a = f.reviewed("Pat", 3);

        assertThrows(UnauthorizedNurseException.class,
                () -> f.controller.changePriority(f.unauthorized, a, 5));

        assertEquals(3, a.getPriorityLevel());
        assertTrue(f.queue.getAuditLog().isEmpty());
    }

    // T13 (P5): "changing" to the same level is not a change, so the audit trail stays clean
    @Test
    void sameLevelIsNotRecordedAsChange() {
        TriageFixture f = new TriageFixture();
        TriageAssessment a = f.reviewed("Pat", 3);

        assertFalse(f.controller.changePriority(f.nurseB, a, 3));

        assertEquals(3, a.getPriorityLevel());
        assertTrue(f.queue.getAuditLog().isEmpty());
    }
}
