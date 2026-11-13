package com.careflow.domain;

import java.util.Comparator;
import java.util.List;

public class TriageQueue {
    private final List<QueueEntry> entries;

    public TriageQueue(List<QueueEntry> entries) {
        this.entries = List.copyOf(entries);
    }

    public List<QueueEntry> getWaitingEntriesInPriorityOrder() {
        return entries.stream()
                .filter(QueueEntry::isWaiting)
                .sorted(Comparator.comparingInt(QueueEntry::getPriority)
                        .thenComparing(QueueEntry::getEnteredQueueAt))
                .toList();
    }
}
