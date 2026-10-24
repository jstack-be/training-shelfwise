# Shelfwise back-end — instructions for Claude

Loaded when working under `shelfwise-code/backend`. The root `CLAUDE.md` still applies.

Checkstyle and `ArchitectureTest` enforce the mechanical rules below. Violations that existed before are frozen in
`checkstyle-suppressions.xml` and `src/test/resources/archunit-store`; never add to them to make a check pass.

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
- Every feature package uses the same sub-packages: `entity`, `repository`, `service`, `helper`, `web`.
- Passwords are hashed with BCrypt.

## REST API conventions

- Base paths: `/api/auth`, `/api/catalogue`, `/api/staff/catalogue`, `/api/staff/members`, `/api/desk`,
  `/api/portal`, `/api/admin`, `/api/reports`, `/api/system`, `/api/training`, `/api/demo`, `/api/provider/payments`.
  Staff endpoints live under `/api/staff`, `/api/desk`, `/api/admin` and `/api/reports`; member endpoints under
  `/api/portal`.
- Authentication is a bearer token from `POST /api/auth/login` (`{ "username": …, "password": … }`), returned as
  `{ "token": …, "profile": … }`.
- A refused business action returns **HTTP 422** with an `ApiError`:
  `{ "code": "LOAN_LIMIT_REACHED", "ruleId": "BR-MEM-02", "message": "…", "details": [] }`.
- Unknown resources 404, missing permissions 403, conflicts 409.
- Error codes are UPPER_SNAKE_CASE and describe the reason, not the endpoint: `MEMBER_BLOCKED`,
  `MEMBERSHIP_EXPIRED`, `LOAN_LIMIT_REACHED`, `MAX_RENEWALS_REACHED`, `RENEWAL_RESERVATIONS_WAITING`,
  `POSSIBLE_DUPLICATE`.
- Dates are ISO strings (`2026-03-16`); amounts have two decimals. Null fields are left out of responses.
- Branch scoping happens on the server: librarians only see their own branch in R-01 and R-02.
