package com.careflow.service;

import com.careflow.domain.Clinician;
import com.careflow.domain.Evaluation;
import com.careflow.domain.QueueEntry;
import com.careflow.domain.QueueStatus;
import com.careflow.repository.EvaluationRepository;
import com.careflow.repository.TriageQueueRepository;

import java.time.Clock;
import java.time.LocalDateTime;

public class ClinicianService {
    private final TriageQueueRepository queueRepository;
    private final EvaluationRepository evaluationRepository;
    private final Clock clock;

    public ClinicianService(TriageQueueRepository queueRepository,
                            EvaluationRepository evaluationRepository,
                            Clock clock) {
        this.queueRepository = queueRepository;
        this.evaluationRepository = evaluationRepository;
        this.clock = clock;
    }

    public EvaluationCompletionResult completeEvaluation(long queueEntryId,
                                                         Clinician clinician,
                                                         String outcome,
                                                         QueueStatus resultingStatus) {
        if (clinician == null || !clinician.canEvaluatePatients()) {
            return EvaluationCompletionResult.UNAUTHORIZED;
        }

        QueueEntry entry = queueRepository.findEntryById(queueEntryId);
        if (entry == null) {
            return EvaluationCompletionResult.ENTRY_NOT_FOUND;
        }

        if (!entry.isWaiting()) {
            return EvaluationCompletionResult.INVALID_STATE;
        }

        if (outcome == null || outcome.isBlank()) {
            return EvaluationCompletionResult.INVALID_OUTCOME;
        }

        if (resultingStatus != QueueStatus.EVALUATED) {
            return EvaluationCompletionResult.INVALID_RESULTING_STATUS;
        }

        entry.markEvaluated();
        Evaluation evaluation = new Evaluation(
                entry.getQueueEntryId(),
                clinician.getClinicianId(),
                entry.getPatient().getPatientId(),
                LocalDateTime.now(clock),
                outcome,
                resultingStatus);
        evaluationRepository.save(evaluation);
        queueRepository.save(entry);
        return EvaluationCompletionResult.SUCCESS;
    }
}
