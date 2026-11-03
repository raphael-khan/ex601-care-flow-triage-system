package com.careflow.repository;

import com.careflow.domain.Evaluation;

import java.util.ArrayList;
import java.util.List;

public class InMemoryEvaluationRepository implements EvaluationRepository {
    private final List<Evaluation> evaluations = new ArrayList<>();

    @Override
    public void save(Evaluation evaluation) {
        evaluations.add(evaluation);
    }

    @Override
    public int count() {
        return evaluations.size();
    }
}
