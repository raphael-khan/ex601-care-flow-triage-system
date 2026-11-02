package main.controllers;

import main.models.IntakeForm;
import main.models.Patient;
import main.models.TriageAssessment;
import main.models.UnauthorizedNurseException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Part 3: branch coverage of TriageController.reviewIntakeForm (5 decision points, C(G) = 6).
 * One test per path in the minimum branch-coverage set. Every test checks the result
 * (returned assessment or thrown exception) AND the resulting state.
 * Node numbers refer to the CFG in the A2 write-up.
 */
class ReviewIntakeFormBranchTest {

    private static void assertNothingChanged(TriageFixture f, Patient p) {
        assertNull(p.getAssessment());
        assertTrue(f.queue.getWaitingPatients().isEmpty());
        assertTrue(f.repository.getSavedAssessments().isEmpty());
    }

    // covers Path 1: 1 -> 2 -> 12   (D1 True: nurse not authorized)
    @Test
    void path1_unauthorizedNurse() {
        TriageFixture f = new TriageFixture();
        Patient p = f.patient("Pat", "fever");

        UnauthorizedNurseException e = assertThrows(UnauthorizedNurseException.class,
                () -> f.controller.reviewIntakeForm(f.unauthorized, p, 3));

        assertTrue(e.getMessage().contains("999"));
        assertNothingChanged(f, p);
    }

    // covers Path 2: 1 -> 3 -> 4 -> 12   (D1 False, D2 True: required information missing)
    @Test
    void path2_missingRequiredInformation() {
        TriageFixture f = new TriageFixture();
        Patient p = f.patient("Pat", " ");

        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> f.controller.reviewIntakeForm(f.nurseA, p, 3));

        assertTrue(e.getMessage().contains("missing required information"));
        assertNothingChanged(f, p);
    }

    // covers Path 3: 1 -> 3 -> 5 -> 6 -> 12   (D2 False, D3 True: complete form never submitted)
    @Test
    void path3_formNotSubmitted() {
        TriageFixture f = new TriageFixture();
        Patient p = f.draftPatient("Pat");

        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> f.controller.reviewIntakeForm(f.nurseA, p, 3));

        assertTrue(e.getMessage().contains("not been submitted"));
        assertNothingChanged(f, p);
        assertEquals(List.of(), f.controller.getPatientsAwaitingReview());
    }

    // covers Path 4: 1 -> 3 -> 5 -> 7 -> 8 -> 12   (D3 False, D4 True: already reviewed)
    @Test
    void path4_alreadyReviewed() {
        TriageFixture f = new TriageFixture();
        Patient p = f.patient("Pat", "fever");
        TriageAssessment first = f.controller.reviewIntakeForm(f.nurseA, p, 2);

        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> f.controller.reviewIntakeForm(f.nurseB, p, 5));

        assertTrue(e.getMessage().contains("already been reviewed"));
        assertSame(first, p.getAssessment());
        assertEquals(2, first.getPriorityLevel());
        assertSame(f.nurseA, first.getAssignedBy());
        assertEquals(1, f.queue.getWaitingPatients().size());
        assertEquals(1, f.repository.getSavedAssessments().size());
    }

    // covers Path 5: 1 -> 3 -> 5 -> 7 -> 9 -> 10 -> 12   (D4 False, D5 True: level out of range)
    @Test
    void path5_levelOutOfRange() {
        TriageFixture f = new TriageFixture();
        Patient p = f.patient("Pat", "fever");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> f.controller.reviewIntakeForm(f.nurseA, p, 6));

        assertTrue(e.getMessage().contains("6"));
        assertNothingChanged(f, p);
    }

    // covers Path 6: 1 -> 3 -> 5 -> 7 -> 9 -> 11 -> 12   (D5 False: every check passes)
    @Test
    void path6_validReview() {
        TriageFixture f = new TriageFixture();
        Patient p = f.patient("Pat", "fever");

        TriageAssessment a = f.controller.reviewIntakeForm(f.nurseA, p, 5);

        assertEquals(5, a.getPriorityLevel());
        assertSame(f.nurseA, a.getAssignedBy());
        assertEquals(TriageFixture.NOW_LOCAL, a.getAssignedAt());
        assertSame(p, a.getPatient());
        assertSame(a, p.getAssessment());
        assertTrue(p.isReviewed());
        assertEquals(List.of(p), f.queue.getWaitingPatients());
        assertEquals(List.of(a), f.repository.getSavedAssessments());
        assertTrue(f.controller.getPatientsAwaitingReview().isEmpty());
    }
}
