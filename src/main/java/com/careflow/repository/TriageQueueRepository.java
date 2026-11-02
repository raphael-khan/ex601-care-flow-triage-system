package com.careflow.repository;

import com.careflow.domain.QueueEntry;
import com.careflow.domain.TriageQueue;

public interface TriageQueueRepository {
    TriageQueue loadCurrentQueue();
    QueueEntry findEntryById(long queueEntryId);
    void save(QueueEntry entry);
}
