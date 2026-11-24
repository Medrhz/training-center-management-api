# Training Center Management & Enrollment API

**Document:** Software Requirements Specification (SRS)  
**Version:** 1.0 — Initial MVP Baseline  
**Project type:** Internal training-center management REST API  
**Scope:** Requirements and business behavior; technology and UML design are documented separately.

> **Requirement status legend**
> - **Agreed:** Discussed and selected for this educational project.
> - **Proposed:** Mentor's explicit recommendation; needs confirmation before it is treated as a binding business rule.
> - **Future:** Deliberately deferred to a later learning phase.
>

## 1. Business Context and Problem Statement

A training center offers training courses, schedules course sessions, manages students, and tracks course enrollment
requests. Currently, scattered or manual processes make it difficult to identify active registrations, prevent
duplicates, respect course capacity, and preserve enrollment history.

The proposed system is an internal backend API that centralizes these activities and enforces explicit business rules.
It is an educational, realistic **modular monolith** project, not a complete training-center ERP.

## 2. Business Objectives

| ID    | Business objective                                                         |
|-------|----------------------------------------------------------------------------|
| BO-01 | Centralize student and student-profile information.                        |
| BO-02 | Maintain an organized catalog of training courses and scheduled sessions.  |
| BO-03 | Manage enrollment requests, confirmations, and cancellations consistently. |
| BO-04 | Prevent invalid registrations and over-capacity confirmations.             |
| BO-05 | Preserve enrollment history for operational follow-up and later analysis.  |

## 3. Scope and Actors

**Primary actor — Training Center Staff (Agreed).** Staff manage students, courses, sessions, and enrollments through
the API. `Admin` and `Training Coordinator` may be distinct roles in a future security phase, but that distinction is
not required for this MVP.

**Students and professors:** These are not direct system actors in the initial internal-management scope. Student data
exists in the system; a direct student portal and professor portal are outside the MVP.

**Security note:** Authentication, JWT, and role-based authorization have **not** been implemented at this stage. The
initial API must not be described as production-secure or publicly exposed without appropriate controls.

## 4. Functional Requirements

### 4.1 Student Management

| ID    | Requirement                                             | Status                         |
|-------|---------------------------------------------------------|--------------------------------|
| FR-01 | Register a student.                                     | Agreed                         |
| FR-02 | View student details.                                   | Agreed                         |
| FR-03 | Update student information.                             | Agreed                         |
| FR-04 | List and search students.                               | Agreed                         |
| FR-05 | Activate an inactive student.                           | Agreed                         |
| FR-06 | Deactivate an active student.                           | Agreed                         |
| FR-07 | View and update additional student-profile information. | Agreed (exact fields proposed) |

**Initial data proposal:** student ID, full name, email, phone, status; profile may include address and additional
contact details. Field-level validation and whether the profile is stored separately will be decided during detailed
design.

### 4.2 Course Catalog

| ID    | Requirement                        | Status |
|-------|------------------------------------|--------|
| FR-08 | Create a training course.          | Agreed |
| FR-09 | View course details.               | Agreed |
| FR-10 | Update course information.         | Agreed |
| FR-11 | Activate a course.                 | Agreed |
| FR-12 | Archive a course.                  | Agreed |
| FR-13 | List and search courses.           | Agreed |
| FR-14 | Define and update course capacity. | Agreed |

**Course Lifecycle — Agreed**

A newly created course starts with DRAFT status.

- DRAFT: The course is not available for enrollment.
- ACTIVE: The course accepts eligible enrollment requests.
- ARCHIVED: The course no longer accepts enrollment requests.

ARCHIVED is a terminal status. An archived course cannot
be reactivated.

If the training center wants to offer the same training
again, it creates a new course with a new identity.

The archived course and its enrollment history are preserved.
Historical enrollments are not copied into the new course.

**Initial data proposal:** Course ID, title, description, capacity, and status.

### 4.3 Course Sessions

| ID    | Requirement                                | Status |
|-------|--------------------------------------------|--------|
| FR-15 | Schedule a session for an existing course. | Agreed |
| FR-16 | Reschedule or update a session.            | Agreed |
| FR-17 | View sessions belonging to a course.       | Agreed |

**Agreed MVP scope:** Each course session has a date,
start time, and end time.

Sessions belong to a course. Enrollment is into the
course, not into individual sessions.

Teacher assignments, room management, and scheduling
conflict detection are outside the MVP.

### 4.4 Enrollment Management

| ID    | Requirement                                                                                                         | Status |
|-------|---------------------------------------------------------------------------------------------------------------------|--------|
| FR-18 | Request enrollment of a student into a course.                                                                      | Agreed |
| FR-19 | Confirm an eligible pending enrollment.                                                                             | Agreed |
| FR-20 | Cancel an enrollment.                                                                                               | Agreed |
| FR-21 | Automatically promote the oldest eligible pending enrollment when cancelling a confirmed enrollment frees capacity. | Agreed |
| FR-22 | View enrollment details.                                                                                            | Agreed |
| FR-23 | View a student's enrollment history, including cancellations and re-enrollments.                                    | Agreed |
| FR-24 | View the enrollments of a course.                                                                                   | Agreed |
| FR-25 | View confirmed and available capacity for a course.                                                                 | Agreed |
| FR-26 | List, filter, and sort enrollments.                                                                                 | Agreed |

## 5. Business Rules

The rules in this section are **business decisions**, not Java classes, JPA annotations, database schemas, or API
endpoints.

### 5.1 Student Rules

| ID    | Rule                                                                         | Status |  
|-------|------------------------------------------------------------------------------|--------|
| BR-01 | A student must exist before an enrollment request can be made for them.      | Agreed |
| BR-02 | Only `ACTIVE` students can request new enrollments.                          | Agreed |
| BR-03 | Only `ACTIVE` students are eligible for enrollment confirmation.             | Agreed |
| BR-04 | Deactivating a student cancels that student's `PENDING` enrollments.         | Agreed |
| BR-05 | Deactivating a student does not cancel their `CONFIRMED` enrollments.        | Agreed |
| BR-06 | Reactivating a student does not automatically restore cancelled enrollments. | Agreed |

`INACTIVE` means the student's administrative status blocks new requests and confirmation; it does **not** mean the
student's record or previously confirmed registrations are deleted.

### 5.2 Course Rules

| ID    | Rule                                                                                              | Status |
|-------|---------------------------------------------------------------------------------------------------|--------|
| BR-07 | A course must exist before an enrollment request can reference it.                                | Agreed |
| BR-08 | Only `ACTIVE` courses accept new enrollment requests.                                             | Agreed |
| BR-09 | Only `ACTIVE` courses allow confirmation of pending enrollments.                                  | Agreed |
| BR-10 | Course capacity must be greater than zero.                                                        | Agreed |
| BR-11 | Capacity cannot be reduced below the current confirmed enrollment count.                          | Agreed |
| BR-12 | `ARCHIVED` courses cannot accept new enrollment requests.                                         | Agreed |
| BR-13 | Archiving a course preserves its previously confirmed enrollments and historical records.         | Agreed |
| BR-14 | Archiving a course cancels its pending enrollment requests.                                       | Agreed |
| BR-32 | A newly created course starts in DRAFT status.                                                    | Agreed |
| BR-33 | ARCHIVED is terminal. Offering the training again requires a new course with a distinct identity. | Agreed |

### 5.3 Enrollment and Capacity Rules

| ID    | Rule                                                                                                             | Status |
|-------|------------------------------------------------------------------------------------------------------------------|--------|
| BR-15 | Each enrollment references an existing student and an existing course.                                           | Agreed |
| BR-16 | Every new enrollment request starts in `PENDING`.                                                                | Agreed |
| BR-17 | `PENDING` enrollments do not reserve seats.                                                                      | Agreed |
| BR-18 | Only `CONFIRMED` enrollments consume seats.                                                                      | Agreed |
| BR-19 | The confirmed enrollment count must not exceed course capacity.                                                  | Agreed |
| BR-20 | A student may have at most one active (`PENDING` or `CONFIRMED`) enrollment for a given course.                  | Agreed |
| BR-21 | Cancelled enrollments remain in history.                                                                         | Agreed |
| BR-22 | A `CANCELLED` enrollment is terminal and cannot be reactivated.                                                  | Agreed |
| BR-23 | After cancellation, a student may submit a **new** enrollment request for the same course if currently eligible. | Agreed |
| BR-24 | A `PENDING` enrollment may transition to `CONFIRMED` only if all confirmation conditions are satisfied.          | Agreed |
| BR-25 | A `PENDING` or `CONFIRMED` enrollment may transition to `CANCELLED`.                                             | Agreed |

**Important clarification:** A new `PENDING` request may be submitted for a course that is **already full**, provided
the student and course are eligible and no active duplicate exists. Capacity is required **at confirmation**, not at
initial request creation.

### 5.4 FIFO and Automatic Promotion

| ID    | Rule                                                                                                                                   | Status |
|-------|----------------------------------------------------------------------------------------------------------------------------------------|--------|
| BR-26 | Eligible pending requests take priority in ascending order of request creation time (FIFO).                                            | Agreed |
| BR-27 | An ineligible older request must not block an eligible later request.                                                                  | Agreed |
| BR-28 | Cancelling a `CONFIRMED` enrollment automatically triggers consideration of the oldest eligible pending request for the released seat. | Agreed |
| BR-29 | Cancelling a `PENDING` enrollment does not release any reserved seat because pending requests do not reserve capacity.                 | Agreed |
| BR-30 | Equal request timestamps are resolved with deterministic tie-breaking (e.g., request ID).                                              | Agreed |
| BR-31 | Automatic confirmation must satisfy current student eligibility, course status, duplicate rules, and capacity.                         | Agreed |
| BR-34 | Manual confirmation must reject a later eligible request when an older eligible pending request exists for the same course.            | Agreed |s

**Manual Confirmation — Agreed**

Staff may manually confirm a pending enrollment only when
that enrollment is the oldest eligible pending request
for the course.

If an older eligible pending request exists, the
confirmation is rejected.

Ineligible requests do not block later eligible requests.

No manual FIFO override is supported in the MVP.

## 6. Enrollment State Model

| Current state | Business action    | Next state  | Allowed?                                                 |
|---------------|--------------------|-------------|----------------------------------------------------------|
| New request   | Request enrollment | `PENDING`   | Yes, if eligible                                         |
| `PENDING`     | Confirm            | `CONFIRMED` | Yes, if eligible, capacity available, and FIFO respected |
| `PENDING`     | Cancel             | `CANCELLED` | Yes                                                      |
| `CONFIRMED`   | Cancel             | `CANCELLED` | Yes                                                      |
| `CANCELLED`   | Confirm            | —           | No                                                       |
| `CANCELLED`   | Reactivate         | —           | No                                                       |
| `CONFIRMED`   | Confirm again      | —           | No (no transition needed)                                |

## 7. Domain Invariants

- **INV-01 — Capacity:** `0 <= confirmedEnrollmentCount <= course.capacity`.
- **INV-02 — No active duplicates:** For each `(student, course)` pair, there is at most one enrollment whose status is
  `PENDING` or `CONFIRMED`.
- **INV-03 — Terminal cancellation:** An enrollment in `CANCELLED` cannot move to another state.
- **INV-04 — Eligibility for confirmation:** A transition to `CONFIRMED` requires an `ACTIVE` student and an `ACTIVE`
  course.

These are requirements on valid domain state; their ownership and technical enforcement will be decided during domain,
persistence, and transaction design.

## 8. Non-Functional Requirements

| ID     | Quality requirement        | Verification direction                                                                     |
|--------|----------------------------|--------------------------------------------------------------------------------------------|
| NFR-01 | Data integrity             | Invalid references and conflicting records are prevented or rejected.                      |
| NFR-02 | Consistency                | State transitions cannot leave contradictory business data.                                |
| NFR-03 | Transactional behavior     | Cancellation and required automatic promotion behave as one consistent business operation. |
| NFR-04 | Clear error handling       | Invalid input, missing records, conflicts, and unexpected failures are distinguishable.    |
| NFR-05 | Maintainability            | Modules and business responsibilities remain understandable.                               |
| NFR-06 | Testability                | Business rules can be verified with meaningful automated tests.                            |
| NFR-07 | Efficient queries          | Avoid demonstrable N+1 queries and unnecessary data fetching.                              |
| NFR-08 | Bounded listings           | Listing endpoints offer pagination, sorting, and appropriate filters.                      |
| NFR-09 | History retention          | Cancelled enrollment records remain queryable.                                             |
| NFR-10 | Concurrent capacity safety | Two overlapping confirmation requests cannot successfully exceed available capacity.       |

No numerical performance targets have been specified yet. A transaction annotation alone does not guarantee NFR-10; the
required consistency mechanism will be chosen during design.

## 9. Out of Scope — MVP

- Direct student self-service portal or professor portal.
- Login, Spring Security, JWT, roles/permissions (future dedicated learning phase).
- Payment gateway, invoices, full accounting, or tuition collection.
- Attendance tracking, grading, exams, certificate generation.
- Professor payroll, room booking, complex session scheduling.
- Email/SMS notifications, Redis, Kafka, RabbitMQ, microservices.
- Course fees and course tags as mandatory MVP features; add only if supported by a justified business requirement.

## 10. Acceptance Criteria — Initial Critical Scenarios

### AC-01 — Request enrollment

**Given** an `ACTIVE` student and an `ACTIVE` course with no active enrollment for that pair,  
**when** staff submit an enrollment request,  
**then** a new `PENDING` enrollment is recorded, even when no seat is currently free.

### AC-02 — Reject duplicate active enrollment

**Given** a student already has a `PENDING` or `CONFIRMED` enrollment in a course,  
**when** another request for that course is submitted,  
**then** the request is rejected without creating a duplicate.

### AC-03 — Capacity at confirmation

**Given** a course has reached confirmed capacity,  
**when** confirmation is attempted,  
**then** the operation is rejected or remains pending according to the future confirmation contract, and confirmed
capacity is never exceeded.

### AC-04 — Automatic promotion after cancellation

**Given** a full course and eligible pending requests,  
**when** a `CONFIRMED` enrollment is cancelled,  
**then** the cancellation is recorded, the oldest eligible `PENDING` enrollment becomes `CONFIRMED`, and confirmed count
does not exceed capacity.

### AC-05 — Skip ineligible pending request

**Given** an earlier pending request is ineligible and a later request is eligible,  
**when** a seat is released,  
**then** the earlier request is not confirmed and the oldest eligible request can be promoted.

### AC-06 — Student deactivation

**Given** a student has one `CONFIRMED` and one `PENDING` enrollment,  
**when** staff deactivate that student,  
**then** the student becomes `INACTIVE`, the pending enrollment becomes `CANCELLED`, and the confirmed enrollment
remains confirmed.

### AC-07 — Re-enrollment after cancellation

**Given** a student has a `CANCELLED` historical enrollment and is currently eligible,  
**when** staff request enrollment again in that course,  
**then** a separate, new `PENDING` enrollment is created; the cancelled record is unchanged.

### AC-08 — No implicit resurrection

**Given** an enrollment is `CANCELLED`,  
**when** staff attempt to confirm that same record,  
**then** the operation is rejected.

### AC-09 — Concurrency protection

**Given** one remaining seat and two concurrent requests to confirm eligible enrollments,  
**when** both requests execute,  
**then** at most one additional enrollment becomes confirmed.

### AC-10 — Course Capacity Update

**Given** an ACTIVE course with capacity 20
And 18 CONFIRMED enrollments

**When** staff update capacity to 15

**Then** the operation is rejected
And the course capacity remains 20
And all existing enrollments remain unchanged.

**When** staff update capacity to 18 or 25

**Then** the operation is accepted.

### AC-11 — Manual Confirmation Respects FIFO

**Given** a course with one available seat
And two eligible PENDING enrollments
And Youssef submitted his request before Sara

**When** staff attempt to confirm Sara's enrollment

**Then** the operation is rejected
And Sara remains PENDING
And the available seat remains unchanged.

**When** staff confirm Youssef's enrollment

**Then** Youssef becomes CONFIRMED
And the available seat count becomes zero.

### AC-12 — Course Archiving

**Given** an ACTIVE course with:

- 15 CONFIRMED enrollments
- 5 PENDING enrollments

**When** staff archive the course

**Then** the course status becomes ARCHIVED
And the 15 CONFIRMED enrollments remain unchanged
And the 5 PENDING enrollments become CANCELLED
And all enrollment history is preserved.

**When** staff attempt to reactivate the archived course

**Then** the operation is rejected.

## 11. Open Decisions and Explicit Assumptions

### Confirmed Decisions

- New courses start in `DRAFT` status.
- `ARCHIVED` is a terminal course status.
- Archived courses cannot be reactivated.
- Reoffering training requires creating a new course with a distinct identity.
- Course capacity cannot be reduced below the current number of confirmed enrollments.
- Manual enrollment confirmation must respect FIFO priority.
- Manually confirming a later eligible request is rejected when an older eligible request exists.
- Archiving a course preserves CONFIRMED enrollments and
  cancels PENDING enrollments.
- Course capacity must be a positive integer.
- Course sessions use date, start time, and end time.
- Teacher assignments and scheduling conflicts are
  outside the MVP.
- Pending enrollments with identical creation timestamps
  are ordered by enrollment ID.

### Additional Details for Design Phase

- Exact student/profile fields, mandatory vs optional values, and uniqueness requirements (including email).
- Exact course fields and their validation requirements.
- Rules for updating already-started or past sessions.
- Whether a cancellation must include a reason and who records it.
- How staff obtain API access during this pre-security educational phase.
- How non-functional requirements, especially concurrency and transaction consistency, are verified.

No unapproved proposal should be silently treated as a confirmed business requirement.

## 12. Traceability and Development Principle

Every meaningful implementation should be traceable through:

`Business Objective -> Functional Requirement -> Business Rule -> UML Design -> Implementation -> Test`

**Project principle:** Technical feasibility is not business justification. Do not introduce features solely because
they are easy to code or demonstrate a framework annotation.

**Next design deliverable (after requirements review):** UML Use Case Diagram. No Spring Boot feature implementation
should start until the requirements baseline and essential UML design have been reviewed.
