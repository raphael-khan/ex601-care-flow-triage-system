package com.careflow.repository;

import com.careflow.domain.Evaluation;

public interface EvaluationRepository {
    void save(Evaluation evaluation);
    int count();
}
