package main.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntakeFormTest {
    private IntakeForm form(
            String gender,
            String insuranceName,
            String insuranceNumber,
            String reasonForVisit) {

        return new IntakeForm(
                "INT-001",
                gender,
                insuranceName,
                insuranceNumber,
                reasonForVisit);
    }

    private void assertDraftUnchanged(
            IntakeForm form,
            String gender,
            String insuranceName,
            String insuranceNumber,
            String reasonForVisit) {

        assertEquals("INT-001", form.getIntakeId());
        assertEquals(gender, form.getGender());
        assertEquals(insuranceName, form.getInsuranceName());
        assertEquals(insuranceNumber, form.getInsuranceNumber());
        assertEquals(reasonForVisit, form.getReasonForVisit());
        assertEquals(IntakeStatus.DRAFT, form.getSubmissionStatus());
        assertNull(form.getSubmittedAt());
    }

    // Path 1: N1 -> N2 -> N10 -> N12
    @Test
    void nullGenderIsInvalid() {
        IntakeForm form = form(null, "Aetna", "12345", "Headache");

        assertFalse(form.isValid());
        assertDraftUnchanged(form, null, "Aetna", "12345", "Headache");
    }

    // Path 2: N1 -> N2 -> N3 -> N10 -> N12
    @Test
    void blankGenderIsInvalid() {
        IntakeForm form = form("   ", "Aetna", "12345", "Headache");

        assertFalse(form.isValid());
        assertDraftUnchanged(form, "   ", "Aetna", "12345", "Headache");
    }

    // Path 3: N1 -> N2 -> N3 -> N4 -> N10 -> N12
    @Test
    void nullInsuranceNameIsInvalid() {
        IntakeForm form = form("Male", null, "12345", "Headache");

        assertFalse(form.isValid());
        assertDraftUnchanged(form, "Male", null, "12345", "Headache");
    }

    // Path 4: N1 -> N2 -> N3 -> N4 -> N5 -> N10 -> N12
    @Test
    void blankInsuranceNameIsInvalid() {
        IntakeForm form = form("Male", "   ", "12345", "Headache");

        assertFalse(form.isValid());
        assertDraftUnchanged(form, "Male", "   ", "12345", "Headache");
    }

    // Path 5: N1 -> N2 -> N3 -> N4 -> N5 -> N6 -> N10 -> N12
    @Test
    void nullInsuranceNumberIsInvalid() {
        IntakeForm form = form("Male", "Aetna", null, "Headache");

        assertFalse(form.isValid());
        assertDraftUnchanged(form, "Male", "Aetna", null, "Headache");
    }

    // Path 6: N1 -> N2 -> N3 -> N4 -> N5 -> N6 -> N7 -> N10 -> N12
    @Test
    void blankInsuranceNumberIsInvalid() {
        IntakeForm form = form("Male", "Aetna", "   ", "Headache");

        assertFalse(form.isValid());
        assertDraftUnchanged(form, "Male", "Aetna", "   ", "Headache");
    }

    // Path 7: N1 -> N2 -> N3 -> N4 -> N5 -> N6 -> N7 -> N8 -> N10 -> N12
    @Test
    void nullReasonForVisitIsInvalid() {
        IntakeForm form = form("Male", "Aetna", "12345", null);

        assertFalse(form.isValid());
        assertDraftUnchanged(form, "Male", "Aetna", "12345", null);
    }

    // Path 8: N1 -> N2 -> N3 -> N4 -> N5 -> N6 -> N7 -> N8 -> N9 -> N10 -> N12
    @Test
    void blankReasonForVisitIsInvalid() {
        IntakeForm form = form("Male", "Aetna", "12345", "   ");

        assertFalse(form.isValid());
        assertDraftUnchanged(form, "Male", "Aetna", "12345", "   ");
    }

    // Path 9: N1 -> N2 -> N3 -> N4 -> N5 -> N6 -> N7 -> N8 -> N9 -> N11 -> N12
    @Test
    void allRequiredFieldsValid() {
        IntakeForm form = form("Male", "Aetna", "12345", "Headache");

        assertTrue(form.isValid());
        assertDraftUnchanged(form, "Male", "Aetna", "12345", "Headache");
    }
}