package com.careflow.repository;

import com.careflow.domain.QueueEntry;
import com.careflow.domain.TriageQueue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class InMemoryTriageQueueRepository implements TriageQueueRepository {
    private final Map<Long, QueueEntry> entries = new LinkedHashMap<>();

    public void add(QueueEntry entry) {
        entries.put(entry.getQueueEntryId(), entry);
    }

    @Override
    public TriageQueue loadCurrentQueue() {
        return new TriageQueue(new ArrayList<>(entries.values()));
    }

    @Override
    public QueueEntry findEntryById(long queueEntryId) {
        return entries.get(queueEntryId);
    }

    @Override
    public void save(QueueEntry entry) {
        entries.put(entry.getQueueEntryId(), entry);
    }
}
