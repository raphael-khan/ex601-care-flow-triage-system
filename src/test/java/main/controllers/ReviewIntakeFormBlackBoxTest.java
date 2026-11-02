package main.controllers;

import main.models.IntakeForm;
import main.models.Patient;
import main.models.PriorityLevel;
import main.models.TriageAssessment;
import main.models.UnauthorizedNurseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Part 2, Behavior A: assign a priority when a nurse reviews an intake form.
 * Basis: R1, R2, I1, I2, I4. Expected values come from those rules, not from the code.
 */
class ReviewIntakeFormBlackBoxTest {

    private static void assertNothingChanged(TriageFixture f, Patient p) {
        assertNull(p.getAssessment());
        assertTrue(f.queue.getWaitingPatients().isEmpty());
        assertTrue(f.repository.getSavedAssessments().isEmpty());
    }

    // T1 (P1, B2/B3/B5/B6): valid levels at and just inside both boundaries are accepted
    @ParameterizedTest(name = "T1 level {0} is accepted")
    @ValueSource(ints = {1, 2, 4, 5})
    void validLevelIsAssigned(int level) {
        TriageFixture f = new TriageFixture();
        Patient p = f.patient("Pat", "fever");

        TriageAssessment a = f.controller.reviewIntakeForm(f.nurseA, p, level);

        assertEquals(level, a.getPriorityLevel());
        assertSame(f.nurseA, a.getAssignedBy());
        assertEquals(TriageFixture.NOW_LOCAL, a.getAssignedAt());
        assertSame(a, p.getAssessment());
        assertTrue(f.controller.getPatientsAwaitingReview().isEmpty());   // R1
        assertEquals(List.of(p), f.queue.getWaitingPatients());
        assertEquals(List.of(a), f.repository.getSavedAssessments());
    }

    // T2 (P2/P3, B1/B4): just outside each boundary is rejected and nothing changes
    @ParameterizedTest(name = "T2 level {0} is rejected")
    @ValueSource(ints = {0, 6, -1})
    void invalidLevelIsRejected(int level) {
        TriageFixture f = new TriageFixture();
        Patient p = f.patient("Pat", "fever");

        assertThrows(IllegalArgumentException.class,
                () -> f.controller.reviewIntakeForm(f.nurseA, p, level));

        assertNothingChanged(f, p);
    }

    // T3 (P4, I4, B7/B8): a missing, empty, or blank reason for visit is missing required information
    @ParameterizedTest(name = "T3 reason \"{0}\" is rejected")
    @NullSource
    @ValueSource(strings = {"", "   "})
    void incompleteFormIsRejected(String reason) {
        TriageFixture f = new TriageFixture();
        Patient p = f.patient("Pat", reason);

        assertThrows(IllegalStateException.class,
                () -> f.controller.reviewIntakeForm(f.nurseA, p, 3));

        assertNothingChanged(f, p);
    }

    // T3b (P4, I4): any other required field missing (here the insurance number) is rejected too
    @Test
    void missingInsuranceNumberIsRejected() {
        TriageFixture f = new TriageFixture();
        Patient p = f.register("Pat", new IntakeForm("INT-Pat", "F", "Aetna", null, "fever"));

        assertThrows(IllegalStateException.class,
                () -> f.controller.reviewIntakeForm(f.nurseA, p, 3));

        assertNothingChanged(f, p);
    }

    // T4 (P1, B9): a single non-blank character is enough to count as present
    @Test
    void oneCharacterReasonIsAccepted() {
        TriageFixture f = new TriageFixture();
        Patient p = f.patient("Pat", "x");

        TriageAssessment a = f.controller.reviewIntakeForm(f.nurseA, p, 3);

        assertEquals(3, a.getPriorityLevel());
        assertSame(a, p.getAssessment());
    }

    // T4b (P5, I4): a complete form that was never submitted cannot be triaged
    @Test
    void unsubmittedFormIsRejected() {
        TriageFixture f = new TriageFixture();
        Patient p = f.draftPatient("Pat");

        assertThrows(IllegalStateException.class,
                () -> f.controller.reviewIntakeForm(f.nurseA, p, 3));

        assertNothingChanged(f, p);
    }

    // T5 (P6, I2): an unauthorized nurse cannot assign priority
    @Test
    void unauthorizedNurseIsRejected() {
        TriageFixture f = new TriageFixture();
        Patient p = f.patient("Pat", "fever");

        assertThrows(UnauthorizedNurseException.class,
                () -> f.controller.reviewIntakeForm(f.unauthorized, p, 3));

        assertNothingChanged(f, p);
    }

    // T6 (P7, R2): a patient is reviewed once; changes go through changePriority
    @Test
    void secondReviewIsRejected() {
        TriageFixture f = new TriageFixture();
        Patient p = f.patient("Pat", "fever");
        TriageAssessment first = f.controller.reviewIntakeForm(f.nurseA, p, 2);

        assertThrows(IllegalStateException.class,
                () -> f.controller.reviewIntakeForm(f.nurseB, p, 5));

        assertSame(first, p.getAssessment());
        assertEquals(2, first.getPriorityLevel());
        assertEquals(1, f.queue.getWaitingPatients().size());           // not queued twice
        assertEquals(1, f.repository.getSavedAssessments().size());
    }

    // T6b (R1): only submitted, unreviewed patients are listed; reviewed and draft ones are not
    @Test
    void awaitingReviewListShowsOnlySubmittedUnreviewedPatients() {
        TriageFixture f = new TriageFixture();
        Patient first = f.patient("First", "fever");
        Patient second = f.patient("Second", "cough");
        f.draftPatient("Draft");

        f.controller.reviewIntakeForm(f.nurseA, first, 3);

        assertEquals(List.of(second), f.controller.getPatientsAwaitingReview());
    }

    // T7 (I1): a CRITICAL patient reviewed after others goes to the top
    @Test
    void criticalReviewGoesToTop() {
        TriageFixture f = new TriageFixture();
        f.reviewed("Low", 1);
        f.reviewed("Mid", 4);
        TriageAssessment crit = f.reviewed("Crit", PriorityLevel.CRITICAL);

        assertSame(crit.getPatient(), f.queue.getWaitingPatients().get(0));
    }
}
