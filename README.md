# CareFlow

**Healthcare Patient Intake & Triage Queue Management System**

CareFlow is a healthcare workflow application designed to coordinate patient intake, nurse-led triage, priority-based waiting queues, and clinician evaluations. It aims to help clinical staff identify urgent patients promptly, keep waiting patients informed, and maintain a traceable record of important workflow changes.

Developed as a group course project for **EX601 – Software Engineering Fundamentals**, Boston University, Fall 2026 (Group 9).

> **Project status:** Checkpoint 1 — requirements and unified design baseline. Implementation is in progress. The technology stack below is proposed in the checkpoint submission and requires team confirmation.

## Key Features

The planned minimum viable product (MVP) supports three role-specific workflows:

### Patient
- Create and submit a patient intake form with required personal and visit information.
- Validate intake details and prevent patient edits after successful submission.
- View queue position and estimated waiting time after triage placement (planned enhancement).

### Nurse
- Review submitted intake forms awaiting triage.
- Assign an initial clinical priority after reviewing a valid intake form.
- Update clinical priority when necessary, with changes recorded for traceability.
- Place eligible patients into a priority-aware queue.

### Clinician
- View waiting patients ordered by current triage priority.
- Review a selected patient's current triage assessment.
- Record evaluation outcomes and transition evaluated patients out of the waiting queue.

CareFlow supports clinical workflows; **it does not automate medical diagnosis or make clinical triage decisions**. Authorized healthcare professionals retain responsibility for clinical judgment.

## Technology Stack

The following is the **proposed stack from Checkpoint 1**, not a statement that every component has already been implemented.

| Layer / Purpose | Technology |
| --- | --- |
| Backend | Java 21, Spring Boot 3 |
| Web / Presentation | Spring MVC, Thymeleaf, Bootstrap 5 |
| Database | PostgreSQL |
| Build and execution | Apache Maven, Spring Boot Maven Plugin |
| Testing | JUnit 5, Mockito |
| External APIs | None required for the MVP |
| Runtime AI / LLM | None |

## Architecture

CareFlow follows a **three-layer architecture**:

1. **Presentation / Application:** Patient, triage, and clinician views and controllers coordinate user requests.
2. **Domain:** Entities and business rules govern intake validation, clinical priority, queue ordering, and evaluation transitions.
3. **Data:** Repository adapters manage persistence while keeping storage concerns outside the domain model.

Key domain concepts include `Patient`, `IntakeForm`, `Nurse`, `TriageAssessment`, `QueueEntry`, `TriageQueue`, `Clinician`, and `Evaluation`.

The design uses a single authoritative `TriageAssessment` for current clinical priority, a `PriorityOrderingStrategy` to support configurable ordering policies, and repository interfaces to decouple application logic from PostgreSQL. An in-memory queue repository is also included in the design for isolated testing.

## Getting Started

These instructions apply **once the proposed Spring Boot / Maven project structure is implemented and committed**.

### Prerequisites

- Java Development Kit (JDK) 21
- Apache Maven (or the Maven wrapper, if included in the repository)
- PostgreSQL for database-backed execution
- Git

### Clone the repository

```bash
git clone https://github.com/raphael-khan/ex601-care-flow-triage-system.git
cd ex601-care-flow-triage-system
