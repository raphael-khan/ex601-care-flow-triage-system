package main.models;

/** Triage priority scale from the A1 design: 1 (lowest) to 5 (CRITICAL). */
public final class PriorityLevel {
    public static final int MIN = 1;
    public static final int MAX = 5;
    public static final int CRITICAL = 5;

    private PriorityLevel() { }

    public static boolean isValid(int level) {
        return level >= MIN && level <= MAX;
    }
}
