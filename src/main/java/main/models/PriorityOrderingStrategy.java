package main.models;

import java.util.Comparator;

/** ADR-001: the sorting rule lives behind this interface. */
public interface PriorityOrderingStrategy {
    Comparator<Patient> comparator();
}
