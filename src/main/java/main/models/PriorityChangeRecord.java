package main.models;

import java.time.LocalDateTime;

/** One audit entry for a priority change (invariant: changes must be recorded). */
public record PriorityChangeRecord(Patient patient, int oldLevel, int newLevel,
                                   Nurse changedBy, LocalDateTime changedAt) { }
