package main.models;

import java.util.Comparator;

public class HighestPriorityFirstStrategy implements PriorityOrderingStrategy {
    @Override
    public Comparator<Patient> comparator() {
        return Comparator.<Patient>comparingInt(p -> p.getAssessment().getPriorityLevel()).reversed();
    }
}
