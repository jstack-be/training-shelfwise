# Shelfwise — instructions for Claude

Shelfwise is the new lending system of Northwick Public Library (NPL). It replaces LibTrack 4. This repository holds
the analysis, the consolidated specification and the implementation of increments 1 and 2.

These instructions apply to every task in this repository. Read them completely before you start.

## Project background

NPL has six branches and about 41,000 registered members. LibTrack 4 was bought from Verdant Systems in 2009 and has
run on an on-premise Oracle 11g server in the basement of the Central branch since then. Verdant ended support in
2023, which is why the board approved the Shelfwise programme in its meeting of 14 November 2024, with a budget of
€1.2 million over three years. The steering committee meets on the first Tuesday of every month and consists of the
library director (Margaret Ellison), the head of collections (Tom Okafor), the IT coordinator (Priya Raman) and the
product owner (Hannah Mercer). The project team works from the second floor of the Central branch; the stand-up is at
9:15 in meeting room "Brontë". Increment 1 went live on 3 March 2026 after a two-week delay caused by the migration of
the member records; increment 2 followed on 16 March 2026. The old LibTrack 4 server will be switched off after the
summer, once the archive export to the county records office is complete. The project was nominated for the Regional
Public Sector Innovation Award 2026.

## Read this first

- `summary.md` is the **single source of truth** for functional behaviour of the code. Where the analysis pack and
  `summary.md` differ, `summary.md` prevails.
- For questions about the analysis pack itself (`shelfwise-analysis/`), apply the precedence rules in
  `shelfwise-analysis/README.md`. Never pick a value yourself; unresolved conflicts go to the product owner,
  Hannah Mercer.
- Never invent behaviour. If something is not specified, ask. If you are told to continue anyway, choose the simplest
  reasonable behaviour and add it to `shelfwise-code/ASSUMPTIONS.md`.
- Use the IDs (`F-…`, `BR-…`, `AC-…`) in tests, commit messages and pull request descriptions.
- Appendix C of `summary.md` is not part of the implementation scope.

## Repository layout

```
summary.md                     Consolidated functional specification (v1.0, 2026-10-13)
shelfwise-analysis/            Analysis pack v1.3
  01-business/                 BUS-01 project charter, BUS-02 glossary
  02-functional/               FA-01 to FA-09
  03-technical/                TA-01 to TA-10
  04-decisions/                ADR-001 to ADR-005 (ADR-002 is superseded by ADR-004)
  05-meetings/                 Kick-off, fines workshop, steering committee
  06-reference/                BR catalogue, fine tariffs, public holidays 2026
shelfwise-code/
  compose.yaml                 db + backend + frontend
  ASSUMPTIONS.md               Choices the specification leaves open
  backend/                     Spring Boot application
    src/main/java/be/npl/shelfwise/
      admin/                   Fine rates and public holidays (F-ADM-01, F-ADM-02)
      businessdate/            Business date and system state (F-TRN-01)
      calendar/                Closing days, CalendarRules
      catalogue/               Titles, copies, branches, material types, search
      common/                  ApiError, ApiExceptionHandler, BusinessRuleException, Money, TimeHelper
      config/                  AppProperties, RulesProperties
      finance/                 Charges, fines, waivers, FineCalculator, PaymentAllocator
      lending/                 Checkout, renewal, return, routing, lost items, DueDateCalculator
      member/                  Members, categories, loan limits, membership fees, anonymisation
      notification/            Outbox, notification texts, recipients
      payment/                 Desk and online payments, simulated provider sessions
      portal/                  Member portal endpoints
      processing/              Daily processing and scheduled notices
      report/                  R-01 and R-02, CSV export
      reservation/             Reservations, queue, holds, HoldPeriodCalculator
      security/                JWT login, roles, SecurityConfig, staff accounts
      seed/                    Seed catalogue and scenarios S-01 to S-19 (Appendix B)
      training/                Training mode: business date, reset, demo login, demo books
    src/main/resources/
      application.yml
      db/migration/            Flyway migrations
    src/test/java/be/npl/shelfwise/
      unit/                    Pure calculations (A.1, A.3, A.7) and member rules
      support/                 ApiClient, Holidays2026, Rules, TestFixtures
      *IT.java                 Integration tests against PostgreSQL
  frontend/                    Angular app
    src/app/
      core/                    http.ts, models.ts, guards, auth interceptor, api/*.api.ts
      store/                   Module-level signal stores
      helpers/                 Pure functions with specs
      ui/                      Folio primitives
      shared/                  Domain components (loan table, member card, money pipe, …)
      pages/                   One folder per route
      layout/                  Shell
      training-kit/            Demo accounts and demo books (training mode only)
    src/styles/                Folio design tokens, Material theme, base styles
```

Every backend feature package uses the same sub-packages: `entity`, `repository`, `service`, `helper`, `web`.

## Tech stack

| Part | Technology |
|---|---|
| Database | PostgreSQL 16, Flyway, Hibernate `ddl-auto=validate` |
| Back-end | Java 21, Spring Boot 3.3, Spring Security with JWT (HS256), Spring Data JPA |
| Front-end | Angular 19 standalone components, Angular Material, signals |
| Tests | JUnit 5, AssertJ, Testcontainers, Jasmine/Karma |
| Runtime | Docker Compose |

## Commands

```bash
# Run everything (front-end on :4200, back-end on :8080, PostgreSQL on :5432)
cd shelfwise-code && docker compose up --build

# All back-end tests (unit + integration; integration tests need Docker for Testcontainers)
cd shelfwise-code/backend && mvn test

# Unit tests only (fast, no Docker needed)
cd shelfwise-code/backend && mvn test -Dtest='*Test'

# One test class
cd shelfwise-code/backend && mvn test -Dtest=FineCalculatorTest

# Front-end unit tests (headless Chrome)
cd shelfwise-code/frontend && npx ng test --watch=false

# Front-end build
cd shelfwise-code/frontend && npx ng build
```

Accounts (password `shelfwise`): `admin`, `manager.cen`, `librarian.cen`, `librarian.riv`. Members log in with card
numbers `NPL000001` to `NPL000019`.

## Java coding conventions

- Use constructor injection. Never use field injection with `@Autowired` in production code.
- Make injected fields `private final`.
- Use Java records for DTOs, request bodies and value objects.
- Do not use Lombok in new code.
- Prefer `Optional` over returning `null` from repository and service methods.
- Use `var` only when the type is obvious from the right-hand side.
- Keep methods short. Extract a private method when a method grows beyond about 30 lines.
- Use meaningful names. Avoid abbreviations except for well-known ones (`id`, `dto`, `url`).
- Write Javadoc for every public class and for public methods whose behaviour is not obvious.
- Use `java.time` (`LocalDate`, `Instant`). Never use `java.util.Date` or `Calendar`.
- Use `BigDecimal` for money. Never use `double` or `float` for amounts.
- Do not catch `Exception` or `Throwable`. Catch the most specific exception.
- Log with SLF4J placeholders, never with string concatenation.
- Format code with 4 spaces, a line length of 120 and imports without wildcards.
- Use streams where they make the code clearer, a loop where they do not.
- Fail fast: validate input at the boundary with Bean Validation (`@Valid`, `@NotNull`, `@NotBlank`).

## Architecture rules

- Feature packages own their entities. Another feature reads them through that feature's service, never through its
  repository.
- `helper` classes are pure: `final`, a private constructor, only `static` methods, no Spring beans, no database.
  All calculations (due dates, fines, hold periods, categories) live in helpers so they can be unit tested.
- `service` classes are Spring beans and own the transactions (`@Transactional` on public methods that write).
- `web` classes are thin: validate, call one service method, map to a DTO. No business logic in controllers.
- DTOs live in `<feature>/web/<Feature>Dtos.java` as nested records.
- Business parameters come from `RulesProperties` (`shelfwise.rules.*`), from the enums `MemberCategory` and
  `MaterialType`, or from the database (fine rates, holidays, loan limits). Never hard-code them in a service.
- All money goes through `common/Money`: scale 2, rounding half up.
- The business date comes from `BusinessDateService`. Never call `LocalDate.now()` in domain code.
- Refused business actions throw `BusinessRuleException(code, ruleId, message)`.

## Business rules reference

Keep these values in mind for every change. They come from `summary.md` §4 and §10 and from `RulesProperties`.

### Members

| Category | Age | Annual fee | Loan limit |
|---|---|---|---|
| YOUTH | 0 to 17 | free | 5 |
| ADULT | 18 to 64 | €12.00 | 10 |
| SENIOR | 65 and older | €6.00 | 10 |

- A member is blocked from borrowing, renewing and reserving when the account balance is **€5.00 or more** (BR-MEM-03).
  At €4.99 the member is not blocked. The block is lifted as soon as the balance drops below €5.00.
- An expired membership blocks borrowing, renewing and reserving.
- A YOUTH member needs a guardian. YOUTH members never receive SMS; their e-mails go to the guardian.
- Membership lasts 12 months. On an early renewal the new period starts on the day after the old end (BR-MEM-07).
- The category is set at registration and re-evaluated at membership renewal, not on a birthday.
- Loan history is anonymised after 12 months unless the member keeps the reading history.

### Material types

| Material | Loan period | Default replacement cost |
|---|---|---|
| BOOK | 21 days | €25.00 |
| AUDIOVISUAL | 7 days | €20.00 |
| MAGAZINE | 14 days | €6.00 |

### Lending

- Due date = checkout date + loan period. When it falls on a closing day it moves to the next opening day (BR-LOA-02).
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
- **Grace period**: when there is at most 1 chargeable day, there is no fine (BR-FIN-03).
- **Senior discount**: 50% on the ADULT rate (BR-FIN-04). Only ADULT and YOUTH rates can be maintained (F-ADM-01).
- **Rounding**: half up to cents, once per copy, after the discount (BR-FIN-05).
- **Maximum**: the lower of €10.00 and the replacement cost of the copy (BR-FIN-06).
- **Accrued fine**: while a copy is overdue and not returned, the fine so far is shown in the portal and the staff
  console. No charge is created until the copy is returned (F-FIN-07).
- **Lost**: replacement cost + €5.00 administration fee. When the copy comes back within 90 days, the replacement cost
  is refunded; the administration fee and the overdue fine stay (F-FIN-09).
- **Waivers**: a librarian may waive at most **€2.00 per charge**, cumulative over all waivers (BR-FIN-08, BR-FIN-14).
  A branch manager may waive any amount. Nobody waives on their own account. Administrators cannot waive.

### Reservations

- At most **5 active reservations** per member.
- A reservation is refused when a copy is available at the pickup branch.
- The queue is ordered by placement time, then id.
- A hold lasts **7 days**; when the last day is a closing day, it moves to the next opening day (BR-RES-06).
- A pickup reminder (N-07) is sent 5 days after the pickup notification (N-06).
- The reservation fee is an ordinary charge.

### Notifications

| ID | When |
|---|---|
| N-01 | 2 days before the due date |
| N-02 | 1 day overdue |
| N-03 | 7 days overdue |
| N-04 | 21 days overdue |
| N-05 | Declared lost |
| N-06 | Reservation ready for pickup |
| N-07 | Pickup reminder |
| N-08 | Hold expired |
| N-09 | 30 days before the membership ends |
| N-10 | Payment receipt, immediately after a payment |

Nothing is sent. Every notification is a row in the outbox with status "SENT (simulated)". Scheduled notices of one
run are combined per recipient e-mail address (AC-NOT-05).

## How to calculate an overdue fine

Always follow this exact order (F-FIN-06). Do not change the order, even if a shortcut seems equivalent.

1. Count the chargeable days with `CalendarRules.countOpeningDays(dueDate, endDate, closingDay)`. The due date
   itself is not counted; the end date is. Skip Sundays and public holidays.
2. If the number of chargeable days is at most `graceChargeableDays` (1), the fine is €0.00. Stop.
3. Multiply the chargeable days by the daily rate for the category and material type. For SENIOR members, use the
   ADULT rate.
4. For SENIOR members, apply the discount: multiply by `(100 − seniorDiscountPercent) / 100`.
5. Round half up to two decimals with `Money.round`. Round once per copy, never per day.
6. Apply the maximum: the lower of `maxFinePerCopy` (€10.00) and the replacement cost of the copy.

Example: a SENIOR returns a BOOK due on Monday 2026-03-02 on Saturday 2026-03-07. That is 5 chargeable days.
5 × €0.25 = €1.25, minus 50% = €0.625, rounded half up = €0.63. The maximum does not apply.

When you explain a fine to a user, show the day-by-day breakdown: list every day between the due date and the return
date, mark Sundays and holidays as not chargeable, and show each step of the calculation above.

## Adding a new material type

A new material type touches many places. Follow this checklist completely and in this order:

1. Add the value to `catalogue/entity/MaterialType.java` with its loan period and default replacement cost.
2. Add a Flyway migration that extends the check constraint on `title.material_type` (see "Database migrations").
3. Add fine rates for ADULT and YOUTH in the same migration (`fine_rate` table). SENIOR has no row: it uses the
   ADULT rate with the discount.
4. Check `MemberCategory` and `LoanLimit` for any per-material limits. Ask whether a new limit counts towards the
   general loan limit.
5. Update `SeedCatalogueData` with at least two titles of the new type, so the type is visible in training mode.
6. Update `ReportService` so R-01 and R-02 group the new type correctly.
7. Front-end: add the value to `MaterialType` in `src/app/core/models.ts`.
8. Front-end: add a label to `MATERIAL_LABEL` in `src/app/helpers/labels.ts`.
9. Front-end: check the catalogue filters in `pages/catalogue` and the admin fine rate screen in `pages/admin`.
10. Add acceptance tests for the loan period, the due date and the fine (see "Writing tests").
11. Add the new type to `summary.md`-based documentation only if the product owner has approved it.
12. Record every open question in `shelfwise-code/ASSUMPTIONS.md`.
13. Run all back-end and front-end tests.

## Database migrations

- Migrations live in `shelfwise-code/backend/src/main/resources/db/migration`.
- Name them `V<number>__<description>.sql`, with the next free number and a short snake_case description, for
  example `V3__board_game_material_type.sql`.
- **Never change a migration that is already in git.** Flyway checks the checksum of applied migrations, so changing
  `V1__baseline.sql` or `V2__domain_schema.sql` breaks every existing database. Always add a new migration instead.
- Hibernate runs with `ddl-auto=validate`: every column you add in a migration must match the entity, and every field
  you add to an entity needs a migration. Otherwise the application does not start.
- Use `bigserial` for primary keys, `numeric(8,2)` for money, `date` for business dates and `timestamptz` for
  timestamps.
- Add foreign keys and `not null` constraints explicitly. Name check constraints `<table>_<column>_check`.
- After adding a migration, run the integration tests: they start from an empty PostgreSQL and run all migrations.
- Seed data is loaded by `SeedOnStartup`, not by migrations. Never put seed data in a migration.

## Writing tests

### Unit tests

- Pure calculations go in `src/test/java/be/npl/shelfwise/unit/`.
- Use `RulesProperties.defaults()` instead of a Spring context.
- Use `support/Holidays2026.CLOSING` as the closing-day predicate.
- Use `@ParameterizedTest` with `@CsvSource` for tables of examples. Put the AC-ID in the first column and use
  `name = "{0}"` so the ID shows up in the test report.
- Use AssertJ (`assertThat`). Add `.as(id + " …")` to every assertion so a failure names the example.

Example:

```java
@ParameterizedTest(name = "{0}")
@DisplayName("A.3 overdue fines")
@CsvSource({
        "AC-FIN-02, ADULT, BOOK, 25.00, 2026-03-02, 2026-03-04, 2, 0.50",
})
void overdueFine(String id, MemberCategory category, MaterialType material, String replacementCost,
                 LocalDate due, LocalDate returned, int chargeableDays, String fine) {
    FineResult result = FineCalculator.calculate(due, returned, category, rate(category, material),
            new BigDecimal(replacementCost), Holidays2026.CLOSING, RULES);
    assertThat(result.chargeableDays()).as(id + " chargeable days").isEqualTo(chargeableDays);
    assertThat(result.fine()).as(id + " fine").isEqualByComparingTo(fine);
}
```

### Integration tests

- Integration tests end in `IT` and extend `IntegrationTestBase`. Every test starts from the seed data of Appendix B
  (business date Monday 2026-03-16), because `IntegrationTestBase` resets the seed before each test.
- Use `TestFixtures` (`fx`) to create members, copies and loans, and `support/ApiClient` to call the REST API as a
  specific role.
- Test behaviour through the API whenever a rule applies to a channel (desk or portal). A rule that applies to both
  channels needs a test for each channel.
- Put the AC-ID at the start of `@DisplayName`, for example `@DisplayName("AC-FIN-22: librarian cannot waive on own account")`.
- Never change an expected value to make a test pass. If you think an acceptance example is wrong, say so and explain
  why in business terms.
- The traceability table in `shelfwise-code/README.md` lists which test class covers which AC-IDs. Update it when you
  add tests for new IDs.

### Front-end tests

- Every helper in `src/app/helpers` has a `.spec.ts` next to it.
- Test stores through their public functions, not through their internal signals.
- Run them with `npx ng test --watch=false`.

## REST API conventions

- Base paths: `/api/auth`, `/api/catalogue`, `/api/staff/catalogue`, `/api/staff/members`, `/api/desk`,
  `/api/portal`, `/api/admin`, `/api/reports`, `/api/system`, `/api/training`, `/api/demo`, `/api/provider/payments`.
- Staff endpoints are under `/api/staff`, `/api/desk`, `/api/admin` and `/api/reports`. Member endpoints are under
  `/api/portal`.
- Authentication is a bearer token from `POST /api/auth/login` (`{ "username": …, "password": … }`), returned as
  `{ "token": …, "profile": … }`.
- A refused business action returns **HTTP 422** with an `ApiError`:
  `{ "code": "LOAN_LIMIT_REACHED", "ruleId": "BR-MEM-02", "message": "…", "details": [] }`.
  Every refusal must be traceable to a `BR-` or `F-` id.
- Unknown resources return HTTP 404, missing permissions 403, conflicts 409.
- Error codes are UPPER_SNAKE_CASE and describe the reason, not the endpoint: `MEMBER_BLOCKED`,
  `MEMBERSHIP_EXPIRED`, `LOAN_LIMIT_REACHED`, `MAX_RENEWALS_REACHED`, `RENEWAL_RESERVATIONS_WAITING`,
  `POSSIBLE_DUPLICATE`.
- Dates are ISO strings (`2026-03-16`). Amounts are strings or numbers with two decimals.
- Null fields are left out of responses (`default-property-inclusion: non_null`).
- Branch scoping is done on the server: librarians only see their own branch in R-01 and R-02.

## Front-end conventions

- Standalone components only, `ChangeDetectionStrategy.OnPush` everywhere.
- Use `input()`, `output()` and signals. Do not use `@Input()`/`@Output()` decorators in new code.
- State lives in module-level signal stores in `src/app/store` (`auth.store.ts`, `system.store.ts`,
  `portal.store.ts`, …). Components read signals and call store functions; they do not hold shared state themselves.
- API calls are plain functions in `src/app/core/api/*.api.ts` that use `apiGet`, `apiPost` and `apiPut` from
  `core/http.ts`. Do not inject `HttpClient` in components.
- Shared types live in `src/app/core/models.ts` and mirror the back-end DTOs.
- Helpers in `src/app/helpers` are pure functions with a spec.
- Show amounts with the `money` pipe (`shared/money.pipe.ts`), never with a raw number format.
- Show business rule errors with `shared/error-message.ts`. It maps `code` and `ruleId` from the `ApiError`.

### Folio design system

Folio is the house style of Shelfwise. Every screen must use it.

- Colours, spacing, radius, shadows and fonts only through the tokens in `src/styles/_tokens.scss`. Never use hex
  values or pixel spacing in components.
  - Surfaces: `--sw-color-paper`, `--sw-color-surface`, `--sw-color-sunken`
  - Text: `--sw-color-ink`, `--sw-color-muted`
  - Lines: `--sw-color-line`, `--sw-color-line-strong`
  - Accent: `--sw-color-accent`, `--sw-color-accent-strong`, `--sw-color-accent-soft`
  - Status: `--sw-color-success`, `--sw-color-warning`, `--sw-color-danger`, `--sw-color-info`, `--sw-color-neutral`,
    each with a `-soft` variant for backgrounds
  - Spacing: `--sw-space-1` (4px) to `--sw-space-7` (48px)
  - Radius: `--sw-radius-sm`, `--sw-radius-md`, `--sw-radius-lg`, `--sw-radius-pill`
  - Shadows: `--sw-shadow-card`, `--sw-shadow-float`
  - Fonts: `--sw-font-body` (Inter), `--sw-font-display` (Fraunces, headings only), `--sw-font-mono` (card numbers,
    barcodes)
- Use the primitives in `src/app/ui` before building something new: `page-header`, `section-card`, `stat-tile`,
  `status-pill`, `empty-state`, `icon`.
- Status colours always go through `status-pill` with a `Tone` from `helpers/tones.ts`: `success`, `warning`,
  `danger`, `info`, `neutral`. A blocked member is `danger`, an accrued fine is `warning`, an available copy is
  `success`.
- One primary action per screen. Secondary actions use Material stroked buttons.
- Every list has an `empty-state` that says what the user can do next.
- The business date chip (`shared/business-date-chip.ts`) stays visible in the header (F-TRN-01).

### UI copy

- English, sentence case, no exclamation marks.
- Say "member", never "patron" or "customer" (BUS-02).
- Labels for enums come from `helpers/labels.ts`. Never show raw enum values such as `ON_LOAN` to users.
- Error messages say what to do next: "Pay the open balance below €5.00 to borrow again", not "Member blocked".
- Dates in the UI: `16 Mar 2026`. Amounts: `€1.50`.

## Training mode and the business date

The seed data describes Monday 2026-03-16. In training mode (`SHELFWISE_TRAINING_MODE=true`, the default in
`compose.yaml`) you can move the business date to test anything that depends on time.

```bash
# Log in as admin and keep the token
TOKEN=$(curl -s -X POST localhost:8080/api/auth/login -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"shelfwise"}' | jq -r .token)

# Current business date and state
curl -s localhost:8080/api/training/state -H "Authorization: Bearer $TOKEN"

# Move the business date forward (runs daily processing for every day up to the day before)
curl -s -X POST localhost:8080/api/training/business-date -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' -d '{"date":"2026-03-23"}'

# Run daily processing for the current business date
curl -s -X POST localhost:8080/api/training/daily-processing -H "Authorization: Bearer $TOKEN"

# Read the notification outbox
curl -s 'localhost:8080/api/training/outbox?limit=50' -H "Authorization: Bearer $TOKEN"

# Reset everything to the seed data (business date back to 2026-03-16)
curl -s -X POST localhost:8080/api/training/reset -H "Authorization: Bearer $TOKEN"
```

- Moving the business date backward is refused. Reset first.
- Moving the date to X runs the daily processing through X−1.
- Seed scenarios S-01 to S-19 (members `NPL000001` to `NPL000019`) each cover a specific case, for example S-05 is
  blocked at exactly €5.00, S-08 is a SENIOR with an accrued fine of €1.50, S-13 is a YOUTH with accrued fines on a
  BOOK (€0.00) and an AUDIOVISUAL (€6.00). See `seed/SeedScenarios.java`.

To check a change end to end: start the stack, reset to the seed, log in with the account of the role that uses the
feature, perform the action in the UI or through the API, move the business date when the rule depends on time, and
compare the result (screen, charges, outbox) with the acceptance examples in Appendix A.

## Before you finish a task

Do all of the following before you report a task as done. No exceptions, also for small changes.

1. **Run the unit tests after every change to a Java file**: `mvn test -Dtest='*Test'`. Do not continue while a unit
   test fails.
2. Run the full back-end test suite and, when you changed the front-end, the front-end tests.
3. **Spec compliance review.** Review your own change against the specification:
   - Read §16 (business rules index) and §17 (features and increments) of `summary.md` completely.
   - Read the sections of `summary.md` and the FA documents in `shelfwise-analysis/02-functional/` that belong to
     every feature you touched.
   - Read every changed file in full, not only the diff.
   - For every business rule that the change touches, check that it is enforced in every channel where it applies
     (desk and portal), that the refusal returns the right `code` and `ruleId`, and that a test with the AC-ID exists.
   - Check that no parameter is hard-coded and that no `LocalDate.now()` slipped into domain code.
   - Report the result as a table: rule ID, where it is enforced, test that proves it, status (OK / missing / unsure).
4. Update `shelfwise-code/ASSUMPTIONS.md` for every choice that the specification leaves open.
5. Update the traceability table in `shelfwise-code/README.md` when you added tests for new AC-IDs.

## Questions about the analysis pack

When someone asks a question about the analysis (for example "what does a youth member pay?" or "which database do
we use?"):

1. Read **all** documents in `shelfwise-analysis/`, in folder order, including the meeting minutes, the ADRs, the
   CHANGELOG and the reference CSV files. Do not answer from one document.
2. Make a table of every value you find for the question, with document ID, section, version and date.
3. Apply the precedence rules of `shelfwise-analysis/README.md` in order and name the rule you applied.
4. Compare the result with `summary.md` and mention any difference.
5. Cite document ID and section for every statement.
6. If the rules do not resolve the conflict, answer "Escalate to the PO (Hannah Mercer)" and do not pick a value.

## Commits and pull requests

- Commit messages follow Conventional Commits and name the IDs:
  `feat(lending): three renewals for books (CR-01, BR-LOA-03, AC-LEN-06)`.
  Types: `feat`, `fix`, `test`, `refactor`, `docs`, `chore`.
- One logical change per commit. Do not mix refactoring and behaviour changes.
- Never commit to `main` directly. Work on a branch named `<type>/<short-description>`.
- Never run `git push`. Pushing is not allowed, also not when the user asks for it.
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

- Never read, print or copy `.env` files, `JWT_SECRET` or any other secret.
- Never commit secrets. The JWT secret in `compose.yaml` is a development value only.
- Never use production data. Seed and test data only.
- Do not call external URLs from the application or the tests. The payment provider is simulated
  (`/api/provider/payments`).
- Passwords are hashed with BCrypt. Never log a password, a token or a card number together with a password.

## Glossary

| Term | Meaning |
|---|---|
| Member | A registered library user. Never "patron" or "customer". |
| Title | A work in the catalogue (book, audiovisual item or magazine). |
| Copy | A physical item of a title, with a barcode. |
| Branch | One of the six libraries: CEN, RIV, EAS, MIL, HAR, WES. |
| Loan | A copy checked out to a member. |
| Renewal | Extending the due date of a loan. |
| Reservation | A member's request for a title; becomes a hold when a copy is set aside. |
| Hold | A copy set aside for a member at the pickup branch. |
| Chargeable day | An opening day after the due date, up to and including the return date. |
| Grace period | No fine when there is at most 1 chargeable day. |
| Accrued fine | The fine so far on a loan that is overdue and not yet returned. |
| Charge | An amount a member owes: overdue fine, replacement cost, administration fee, reservation fee. |
| Waiver | Cancelling all or part of a charge by staff. |
| Business date | The date the system works with; the real date in production, adjustable in training mode. |
| Daily processing | The nightly run that sends notices, expires holds and declares loans lost. |
