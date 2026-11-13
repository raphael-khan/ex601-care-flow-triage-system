package main.models;

public class UnauthorizedNurseException extends RuntimeException {
    public UnauthorizedNurseException(Nurse nurse) {
        super("Nurse " + nurse.getNurseId() + " is not authorized to set triage priority");
    }
}
