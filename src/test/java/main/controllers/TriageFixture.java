package main.controllers;

import main.data.InMemoryTriageRepository;
import main.models.HighestPriorityFirstStrategy;
import main.models.IntakeForm;
import main.models.Nurse;
import main.models.Patient;
import main.models.TriageAssessment;
import main.models.TriageQueue;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/** Shared setup with a controllable clock, so every timestamp in an expected value is known in advance. */
class TriageFixture {
    static final Instant NOW = Instant.parse("2026-11-02T09:00:00Z");
    static final LocalDateTime NOW_LOCAL = LocalDateTime.ofInstant(NOW, ZoneOffset.UTC);

    final InMemoryTriageRepository repository = new InMemoryTriageRepository();
    final TriageQueue queue = new TriageQueue(new HighestPriorityFirstStrategy());
    final MutableClock clock = new MutableClock(NOW);
    final TriageController controller = new TriageController(queue, repository, clock);

    final Nurse nurseA = new Nurse("Ana", 101, true);
    final Nurse nurseB = new Nurse("Ben", 102, true);
    final Nurse unauthorized = new Nurse("Uma", 999, false);

    /** A patient whose intake form was filled in with these values and then submitted (if it could be). */
    Patient patient(String name, String reasonForVisit) {
        IntakeForm form = new IntakeForm("INT-" + name, "F", "Aetna", "A-123", reasonForVisit);
        form.submit();
        return register(name, form);
    }

    /** A patient whose complete form was saved as a draft and never submitted. */
    Patient draftPatient(String name) {
        return register(name, new IntakeForm("INT-" + name, "F", "Aetna", "A-123", "fever"));
    }

    Patient register(String name, IntakeForm form) {
        Patient p = new Patient(name, LocalDate.of(1990, 1, 1), form);
        repository.addPatient(p);
        return p;
    }

    TriageAssessment reviewed(String name, int level) {
        return controller.reviewIntakeForm(nurseA, patient(name, "chest pain"), level);
    }
}
