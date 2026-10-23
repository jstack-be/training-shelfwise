# Shelfwise — instructions for Claude

Shelfwise is the new lending system of Northwick Public Library (NPL). It replaces LibTrack 4. This repository holds
the analysis, the consolidated specification and the implementation of increments 1 and 2.

## Read this first

- `summary.md` is the **single source of truth** for functional behaviour of the code. Where the analysis pack and
  `summary.md` differ, `summary.md` prevails. Appendix C of `summary.md` is out of scope.
- For questions about the analysis pack itself (`shelfwise-analysis/`), use the `analysis-researcher` agent. Never
  pick a value yourself; unresolved conflicts go to the product owner, Hannah Mercer.
- Never invent behaviour. If something is not specified, ask. If you are told to continue anyway, choose the simplest
  reasonable behaviour and record it in `shelfwise-code/ASSUMPTIONS.md`.
- Use the IDs (`F-…`, `BR-…`, `AC-…`) in tests, commit messages and pull request descriptions.

## Skills and agents

Load the skill before you start that kind of work, not afterwards.

| Work | Use |
|---|---|
| Explain or verify an overdue fine | skill `explain-fine` |
| Back-end feature, endpoint or migration | skill `backend-feature` |
| Any screen or component | skill `frontend-folio` |
| Writing or fixing tests | skill `writing-tests` |
| Adding a material type | skill `material-type` |
| Business date, daily processing, outbox, seed scenarios | skill `training-mode` |
| The review before you report a task as done | skill `spec-review` |
| A question about the analysis pack | agent `analysis-researcher` |

## Repository layout

```
summary.md                     Consolidated functional specification (v1.0, 2026-10-13)
shelfwise-analysis/            Analysis pack v1.3
  01-business/ 02-functional/ 03-technical/ 04-decisions/ 05-meetings/ 06-reference/
shelfwise-code/
  compose.yaml                 db + backend + frontend
  ASSUMPTIONS.md               Choices the specification leaves open
  README.md                    Run instructions and the AC traceability table
  backend/src/main/java/be/npl/shelfwise/
    admin/ businessdate/ calendar/ catalogue/ common/ config/ finance/ lending/ member/
    notification/ payment/ portal/ processing/ report/ reservation/ security/ seed/ training/
  backend/src/main/resources/db/migration/    Flyway migrations
  backend/src/test/java/be/npl/shelfwise/
    unit/                      Pure calculations (A.1, A.3, A.7) and member rules
    support/                   ApiClient, Holidays2026, Rules, TestFixtures
    *IT.java                   Integration tests against PostgreSQL
  frontend/src/app/
    core/ store/ helpers/ ui/ shared/ pages/ layout/ training-kit/
  frontend/src/styles/         Folio design tokens, Material theme, base styles
```

Every backend feature package uses the same sub-packages: `entity`, `repository`, `service`, `helper`, `web`.

## Tech stack

| Part | Technology |
|---|---|
| Database | PostgreSQL 16, Flyway, Hibernate `ddl-auto=validate` |
| Back-end | Java 21, Spring Boot 3.3, Spring Security with JWT (HS256), Spring Data JPA |
| Front-end | Angular 20 standalone components, Angular Material, signals |
| Tests | JUnit 5, AssertJ, Testcontainers, Jasmine/Karma |
| Runtime | Docker Compose |

## Commands

```bash
# Run everything (front-end on :4200, back-end on :8080, PostgreSQL on :5432)
cd shelfwise-code && docker compose up --build

# Unit tests only (fast, no Docker needed) — run after every change to a Java file
cd shelfwise-code/backend && mvn test -Dtest='*Test'

# All back-end tests (unit + integration; integration tests need Docker for Testcontainers)
cd shelfwise-code/backend && mvn test

# One test class
cd shelfwise-code/backend && mvn test -Dtest=FineCalculatorTest

# Front-end unit tests and build
cd shelfwise-code/frontend && npx ng test --watch=false
cd shelfwise-code/frontend && npx ng build
```

Accounts (password `shelfwise`): `admin`, `manager.cen`, `librarian.cen`, `librarian.riv`. Members log in with card
numbers `NPL000001` to `NPL000019`.

## Java coding conventions

- Constructor injection only; injected fields are `private final`. Never `@Autowired` on a field in production code.
- Java records for DTOs, request bodies and value objects. No Lombok in new code.
- Prefer `Optional` over returning `null` from repository and service methods.
- `var` only when the type is obvious from the right-hand side.
- Keep methods short; extract a private method beyond about 30 lines.
- Meaningful names, no abbreviations except well-known ones (`id`, `dto`, `url`).
- Javadoc for every public class and for public methods whose behaviour is not obvious.
- `java.time` only (`LocalDate`, `Instant`). Never `java.util.Date` or `Calendar`.
- `BigDecimal` for money. Never `double` or `float` for amounts.
- Never catch `Exception` or `Throwable`; catch the most specific exception.
- Log with SLF4J placeholders, never string concatenation.
- 4 spaces, line length 120, no wildcard imports.
- Validate input at the boundary with Bean Validation (`@Valid`, `@NotNull`, `@NotBlank`).

## Architecture rules

- Feature packages own their entities. Another feature reads them through that feature's service, never through its
  repository.
- `helper` classes are pure: `final`, private constructor, only `static` methods, no Spring beans, no database. All
  calculations (due dates, fines, hold periods, categories) live in helpers so they can be unit tested.
- `service` classes are Spring beans and own the transactions (`@Transactional` on public methods that write).
- `web` classes are thin: validate, call one service method, map to a DTO. No business logic in controllers.
- DTOs live in `<feature>/web/<Feature>Dtos.java` as nested records.
- Business parameters come from `RulesProperties` (`shelfwise.rules.*`), from the enums `MemberCategory` and
  `MaterialType`, or from the database (fine rates, holidays, loan limits). Never hard-code them in a service.
- All money goes through `common/Money`: scale 2, rounding half up.
- The business date comes from `BusinessDateService`. Never call `LocalDate.now()` in domain code.
- Refused business actions throw `BusinessRuleException(code, ruleId, message)`.

## Business rules reference

From `summary.md` §4 and §10 and from `RulesProperties`.

### Members

| Category | Age | Annual fee | Loan limit |
|---|---|---|---|
| YOUTH | 0 to 17 | free | 5 |
| ADULT | 18 to 64 | €12.00 | 10 |
| SENIOR | 65 and older | €6.00 | 10 |

- Blocked from borrowing, renewing and reserving at a balance of **€5.00 or more** (BR-MEM-03). At €4.99 not blocked;
  the block lifts as soon as the balance drops below €5.00.
- An expired membership blocks borrowing, renewing and reserving.
- A YOUTH member needs a guardian, never receives SMS, and e-mails go to the guardian.
- Membership lasts 12 months. On an early renewal the new period starts the day after the old end (BR-MEM-07).
- The category is set at registration and re-evaluated at membership renewal, not on a birthday.
- Loan history is anonymised after 12 months unless the member keeps the reading history.

### Material types

| Material | Loan period | Default replacement cost |
|---|---|---|
| BOOK | 21 days | €25.00 |
| AUDIOVISUAL | 7 days | €20.00 |
| MAGAZINE | 14 days | €6.00 |

### Lending

- Due date = checkout date + loan period; when it falls on a closing day it moves to the next opening day (BR-LOA-02).
- Closing days are Sundays and the public holidays in the `public_holiday` table.
- A renewal starts from the current due date, not from the renewal date (BR-LOA-04).
- At most **2 renewals** per loan. A renewal is refused when another member has reserved the title.
- A loan that is 42 days overdue is declared lost (F-FIN-08).

### Fines

| Material | ADULT | YOUTH | SENIOR |
|---|---|---|---|
| BOOK | €0.25 per day | €0.00 | ADULT rate −50% |
| AUDIOVISUAL | €1.00 per day | €0.50 per day | ADULT rate −50% |
| MAGAZINE | €0.10 per day | €0.00 | ADULT rate −50% |

- Only **chargeable days** count: opening days after the due date up to and including the return date (BR-FIN-01).
- **Grace period**: at most 1 chargeable day means no fine (BR-FIN-03).
- **Rounding**: half up to cents, once per copy, after the senior discount (BR-FIN-05).
- **Maximum**: the lower of €10.00 and the replacement cost of the copy (BR-FIN-06).
- **Accrued fine**: shown while the copy is overdue and not returned; no charge until it is returned (F-FIN-07).
- **Lost**: replacement cost + €5.00 administration fee; back within 90 days refunds the replacement cost only
  (F-FIN-09).
- **Waivers**: a librarian waives at most **€2.00 per charge**, cumulative (BR-FIN-08, BR-FIN-14). A branch manager
  waives any amount. Nobody waives on their own account. Administrators cannot waive.
- The exact order of calculation is in the `explain-fine` skill. Never take a shortcut through it.

### Reservations

- At most **5 active reservations** per member; refused when a copy is available at the pickup branch.
- The queue is ordered by placement time, then id.
- A hold lasts **7 days**; when the last day is a closing day it moves to the next opening day (BR-RES-06).
- A pickup reminder (N-07) is sent 5 days after the pickup notification (N-06). The reservation fee is an ordinary
  charge.

### Notifications

| ID | When | ID | When |
|---|---|---|---|
| N-01 | 2 days before the due date | N-06 | Reservation ready for pickup |
| N-02 | 1 day overdue | N-07 | Pickup reminder |
| N-03 | 7 days overdue | N-08 | Hold expired |
| N-04 | 21 days overdue | N-09 | 30 days before the membership ends |
| N-05 | Declared lost | N-10 | Payment receipt, immediately after a payment |

Nothing is sent. Every notification is a row in the outbox with status "SENT (simulated)". Scheduled notices of one
run are combined per recipient e-mail address (AC-NOT-05).

## REST API conventions

- Base paths: `/api/auth`, `/api/catalogue`, `/api/staff/catalogue`, `/api/staff/members`, `/api/desk`,
  `/api/portal`, `/api/admin`, `/api/reports`, `/api/system`, `/api/training`, `/api/demo`, `/api/provider/payments`.
  Staff endpoints live under `/api/staff`, `/api/desk`, `/api/admin` and `/api/reports`; member endpoints under
  `/api/portal`.
- Authentication is a bearer token from `POST /api/auth/login` (`{ "username": …, "password": … }`), returned as
  `{ "token": …, "profile": … }`.
- A refused business action returns **HTTP 422** with an `ApiError`:
  `{ "code": "LOAN_LIMIT_REACHED", "ruleId": "BR-MEM-02", "message": "…", "details": [] }`. Every refusal must be
  traceable to a `BR-` or `F-` id.
- Unknown resources 404, missing permissions 403, conflicts 409.
- Error codes are UPPER_SNAKE_CASE and describe the reason, not the endpoint: `MEMBER_BLOCKED`,
  `MEMBERSHIP_EXPIRED`, `LOAN_LIMIT_REACHED`, `MAX_RENEWALS_REACHED`, `RENEWAL_RESERVATIONS_WAITING`,
  `POSSIBLE_DUPLICATE`.
- Dates are ISO strings (`2026-03-16`); amounts have two decimals. Null fields are left out of responses.
- Branch scoping happens on the server: librarians only see their own branch in R-01 and R-02.

## Front-end conventions

- Standalone components only, `ChangeDetectionStrategy.OnPush` everywhere, `input()`/`output()` and signals. No
  `@Input()`/`@Output()` decorators in new code.
- State lives in module-level signal stores in `src/app/store`. Components read signals and call store functions.
- API calls are plain functions in `src/app/core/api/*.api.ts` using `apiGet`, `apiPost`, `apiPut` from `core/http.ts`.
  Never inject `HttpClient` in a component.
- Shared types in `src/app/core/models.ts` mirror the back-end DTOs. Helpers in `src/app/helpers` are pure and have a
  spec next to them.
- Amounts use the `money` pipe; business rule errors use `shared/error-message.ts`.
- Styling, tokens, primitives and UI copy: see the `frontend-folio` skill. Never a hex value or a pixel spacing in a
  component.

## Before you finish a task

Also for small changes:

1. Run `mvn test -Dtest='*Test'` after every change to a Java file. Do not continue while a unit test fails.
2. Run the full back-end suite, and the front-end tests when you changed the front-end.
3. Run the `spec-review` skill and report its table.
4. Update `shelfwise-code/ASSUMPTIONS.md` for every choice the specification leaves open.
5. Update the traceability table in `shelfwise-code/README.md` when you added tests for new AC-IDs.

## Commits and pull requests

- Conventional Commits, naming the IDs:
  `feat(lending): three renewals for books (CR-01, BR-LOA-03, AC-LEN-06)`.
  Types: `feat`, `fix`, `test`, `refactor`, `docs`, `chore`.
- One logical change per commit; never mix refactoring and behaviour changes.
- Never commit to `main`. Work on a branch named `<type>/<short-description>`.
- Commit or push only when the user asks for it.
- Pull request description:

  ```markdown
  ## What
  One paragraph: what changes for members or staff.

  ## Why
  Change request or feature ID, with a link to the section in summary.md.

  ## Rules and examples
  | ID | Change | Test |
  |---|---|---|

  ## Assumptions
  Choices not covered by the specification (also added to ASSUMPTIONS.md).

  ## How to verify
  Seed scenario, role and business date to use, and what you should see.

  ## Checklist
  - [ ] Unit and integration tests pass
  - [ ] Front-end tests pass
  - [ ] Spec compliance review done
  - [ ] ASSUMPTIONS.md and README traceability updated
  ```

## Security

- Never read, print or copy `.env` files, `JWT_SECRET` or any other secret. Never commit secrets; the JWT secret in
  `compose.yaml` is a development value.
- Never use production data. Seed and test data only.
- Do not call external URLs from the application or the tests. The payment provider is simulated
  (`/api/provider/payments`).
- Passwords are hashed with BCrypt. Never log a password, a token, or a card number together with a password.

## Glossary

| Term | Meaning |
|---|---|
| Member | A registered library user. Never "patron" or "customer". |
| Title / Copy | A work in the catalogue / a physical item of a title, with a barcode. |
| Branch | One of the six libraries: CEN, RIV, EAS, MIL, HAR, WES. |
| Loan / Renewal | A copy checked out to a member / extending its due date. |
| Reservation / Hold | A request for a title / a copy set aside at the pickup branch. |
| Chargeable day | An opening day after the due date, up to and including the return date. |
| Grace period | No fine when there is at most 1 chargeable day. |
| Accrued fine | The fine so far on a loan that is overdue and not yet returned. |
| Charge | An amount a member owes: overdue fine, replacement cost, administration fee, reservation fee. |
| Waiver | Cancelling all or part of a charge by staff. |
| Business date | The date the system works with; the real date in production, adjustable in training mode. |
| Daily processing | The nightly run that sends notices, expires holds and declares loans lost. |
