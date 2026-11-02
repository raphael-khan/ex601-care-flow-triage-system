package com.careflow.service;

import com.careflow.domain.*;
import com.careflow.repository.EvaluationRepository;
import com.careflow.repository.InMemoryTriageQueueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * White-box tests for ClinicianService.completeEvaluation.
 * CFG: N1 authorization, N2 UNAUTHORIZED, N3 entry lookup, N4 ENTRY_NOT_FOUND, N5 WAITING check,
 * N6 INVALID_STATE, N7 outcome check, N8 INVALID_OUTCOME, N9 resulting-status check,
 * N10 INVALID_RESULTING_STATUS, N11 success block, N12 exit.
 */
class ClinicianServiceTest {
    private static final LocalDateTime FIXED_NOW = LocalDateTime.of(2026, 10, 28, 16, 0);

    private InMemoryTriageQueueRepository queueRepository;
    private RecordingEvaluationRepository evaluationRepository;
    private ClinicianService service;
    private Clinician authorizedClinician;

    @BeforeEach
    void setUp() {
        queueRepository = new InMemoryTriageQueueRepository();
        evaluationRepository = new RecordingEvaluationRepository();
        Clock clock = Clock.fixed(Instant.parse("2026-10-28T16:00:00Z"), ZoneOffset.UTC);
        service = new ClinicianService(queueRepository, evaluationRepository, clock);
        authorizedClinician = new Clinician(10L, "Dr. Test", true);
    }

    // Covers Path 1: N1 -> N2 -> N12 (unauthorized clinician)
    @Test
    void rejectsUnauthorizedClinicianWithoutChangingState() {
        QueueEntry entry = waitingEntry(1L);
        queueRepository.add(entry);
        Clinician unauthorized = new Clinician(11L, "Unauthorized", false);

        EvaluationCompletionResult result = service.completeEvaluation(
                1L, unauthorized, "Stable", QueueStatus.EVALUATED);

        assertEquals(EvaluationCompletionResult.UNAUTHORIZED, result);
        assertEquals(QueueStatus.WAITING, entry.getQueueStatus());
        assertEquals(0, evaluationRepository.count());
    }

    // Supplements Path 1: null clinician (first operand of the N1 compound condition)
    @Test
    void rejectsNullClinicianWithoutChangingState() {
        QueueEntry entry = waitingEntry(1L);
        queueRepository.add(entry);

        EvaluationCompletionResult result = service.completeEvaluation(
                1L, null, "Stable", QueueStatus.EVALUATED);

        assertEquals(EvaluationCompletionResult.UNAUTHORIZED, result);
        assertEquals(QueueStatus.WAITING, entry.getQueueStatus());
        assertEquals(0, evaluationRepository.count());
    }

    // Covers Path 2: N1 -> N3 -> N4 -> N12 (queue entry not found)
    @Test
    void rejectsUnknownEntryWithoutCreatingEvaluation() {
        QueueEntry other = waitingEntry(1L);
        queueRepository.add(other);

        EvaluationCompletionResult result = service.completeEvaluation(
                999L, authorizedClinician, "Stable", QueueStatus.EVALUATED);

        assertEquals(EvaluationCompletionResult.ENTRY_NOT_FOUND, result);
        assertEquals(0, evaluationRepository.count());
        assertEquals(QueueStatus.WAITING, other.getQueueStatus());
    }

    // Covers Path 3: N1 -> N3 -> N5 -> N6 -> N12 (entry not WAITING)
    @Test
    void rejectsEntryThatIsNotWaiting() {
        QueueEntry entry = waitingEntry(1L);
        entry.markEvaluated();
        queueRepository.add(entry);

        EvaluationCompletionResult result = service.completeEvaluation(
                1L, authorizedClinician, "Stable", QueueStatus.EVALUATED);

        assertEquals(EvaluationCompletionResult.INVALID_STATE, result);
        assertEquals(QueueStatus.EVALUATED, entry.getQueueStatus());
        assertEquals(0, evaluationRepository.count());
    }

    // Covers Path 4: N1 -> N3 -> N5 -> N7 -> N8 -> N12 (blank outcome)
    @Test
    void rejectsBlankOutcomeWithoutChangingQueueState() {
        QueueEntry entry = waitingEntry(1L);
        queueRepository.add(entry);

        EvaluationCompletionResult result = service.completeEvaluation(
                1L, authorizedClinician, "   ", QueueStatus.EVALUATED);

        assertEquals(EvaluationCompletionResult.INVALID_OUTCOME, result);
        assertEquals(QueueStatus.WAITING, entry.getQueueStatus());
        assertEquals(0, evaluationRepository.count());
    }

    // Supplements Path 4: null outcome (first operand of the N7 compound condition)
    @Test
    void rejectsNullOutcomeWithoutChangingQueueState() {
        QueueEntry entry = waitingEntry(1L);
        queueRepository.add(entry);

        EvaluationCompletionResult result = service.completeEvaluation(
                1L, authorizedClinician, null, QueueStatus.EVALUATED);

        assertEquals(EvaluationCompletionResult.INVALID_OUTCOME, result);
        assertEquals(QueueStatus.WAITING, entry.getQueueStatus());
        assertEquals(0, evaluationRepository.count());
    }

    // Covers Path 5: N1 -> N3 -> N5 -> N7 -> N9 -> N10 -> N12 (resulting status not EVALUATED)
    @Test
    void rejectsNonEvaluatedResultingStatus() {
        QueueEntry entry = waitingEntry(1L);
        queueRepository.add(entry);

        EvaluationCompletionResult result = service.completeEvaluation(
                1L, authorizedClinician, "Stable", QueueStatus.WAITING);

        assertEquals(EvaluationCompletionResult.INVALID_RESULTING_STATUS, result);
        assertEquals(QueueStatus.WAITING, entry.getQueueStatus());
        assertEquals(0, evaluationRepository.count());
    }

    // Supplements Path 5: null resulting status
    @Test
    void rejectsNullResultingStatus() {
        QueueEntry entry = waitingEntry(1L);
        queueRepository.add(entry);

        EvaluationCompletionResult result = service.completeEvaluation(
                1L, authorizedClinician, "Stable", null);

        assertEquals(EvaluationCompletionResult.INVALID_RESULTING_STATUS, result);
        assertEquals(QueueStatus.WAITING, entry.getQueueStatus());
        assertEquals(0, evaluationRepository.count());
    }

    // Covers Path 6: N1 -> N3 -> N5 -> N7 -> N9 -> N11 -> N12 (success)
    @Test
    void completesEvaluationAndPersistsResult() {
        QueueEntry entry = waitingEntry(1L);
        queueRepository.add(entry);

        EvaluationCompletionResult result = service.completeEvaluation(
                1L, authorizedClinician, "Stable for discharge", QueueStatus.EVALUATED);

        assertEquals(EvaluationCompletionResult.SUCCESS, result);
        assertEquals(QueueStatus.EVALUATED, entry.getQueueStatus());
        assertSame(entry, queueRepository.findEntryById(1L));
        assertEquals(1, evaluationRepository.count());

        Evaluation saved = evaluationRepository.saved.get(0);
        assertEquals(1L, saved.getQueueEntryId());
        assertEquals(10L, saved.getClinicianId());
        assertEquals(entry.getPatient().getPatientId(), saved.getPatientId());
        assertEquals(FIXED_NOW, saved.getEvaluatedAt());
        assertEquals("Stable for discharge", saved.getOutcome());
        assertEquals(QueueStatus.EVALUATED, saved.getResultingStatus());
    }

    private QueueEntry waitingEntry(long id) {
        LocalDateTime entered = LocalDateTime.of(2026, 10, 28, 11, 0);
        Patient patient = new Patient(id, "Patient " + id);
        TriageAssessment assessment = new TriageAssessment(2, entered.minusMinutes(10), "High priority");
        return new QueueEntry(id, patient, assessment, entered, QueueStatus.WAITING);
    }

    /** Test double that keeps the saved Evaluation objects so their fields can be asserted. */
    private static class RecordingEvaluationRepository implements EvaluationRepository {
        private final List<Evaluation> saved = new ArrayList<>();

        @Override
        public void save(Evaluation evaluation) {
            saved.add(evaluation);
        }

        @Override
        public int count() {
            return saved.size();
        }
    }
}
