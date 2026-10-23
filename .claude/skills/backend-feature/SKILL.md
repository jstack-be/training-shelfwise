---
name: backend-feature
description: Recipe for back-end work in the Spring Boot application — adding or changing an endpoint, a service, a helper, an entity or a Flyway migration. Use when implementing or changing a feature in shelfwise-code/backend, when the application fails to start after a schema change, or when a query needs an index or a fetch join.
---

# Back-end feature work

Follow `CLAUDE.md` for the coding conventions and the architecture rules; this skill is the order of work.

## A new or changed endpoint

1. **Helper first.** Every calculation (due date, fine, hold period, category) goes in `<feature>/helper` as a
   `final` class with a private constructor and only `static` methods — no Spring, no database. Unit test it.
2. **Service.** One public method per use case, `@Transactional` when it writes. Read other features through their
   service, never through their repository. Parameters come from `RulesProperties`, from `MemberCategory` /
   `MaterialType`, or from the database. The business date comes from `BusinessDateService`.
3. **Refusals.** Throw `BusinessRuleException(code, ruleId, message)` with an UPPER_SNAKE_CASE code and a `BR-`/`F-`
   id. `ApiExceptionHandler` turns it into HTTP 422 with an `ApiError`.
4. **DTOs.** Nested records in `<feature>/web/<Feature>Dtos.java`.
5. **Controller.** Thin: `@Valid` on the request body, one service call, map to a DTO. Put it under the right base
   path (`/api/desk`, `/api/portal`, `/api/staff`, `/api/admin`, `/api/reports`).
6. **Security.** Check `SecurityConfig` and the role annotations: staff endpoints are not reachable by members, and
   branch scoping happens on the server.
7. **Both channels.** A rule that applies at the desk and in the portal must be enforced in both services, with the
   same code and ruleId.
8. **Front-end.** Add the call to the matching `src/app/core/api/*.api.ts` and the types to `core/models.ts`.
9. **Tests.** See the `writing-tests` skill — a unit test for the helper, an integration test per channel.

## A Flyway migration

- New file in `backend/src/main/resources/db/migration`, named `V<next>__<snake_case_description>.sql`.
- **Never change a migration that is already in git.** Flyway compares checksums; editing `V1__baseline.sql` breaks
  every existing database. Always add a new migration.
- `bigserial` primary keys, `numeric(8,2)` money, `date` business dates, `timestamptz` timestamps.
- Foreign keys and `not null` explicit. Check constraints named `<table>_<column>_check`.
- Hibernate runs with `ddl-auto=validate`: every column in the migration must match the entity and vice versa, or
  the application does not start.
- No seed data in migrations — seed data is loaded by `SeedOnStartup`.
- Run the integration tests afterwards: they start from an empty PostgreSQL and run every migration.

## Performance

- Avoid N+1 queries: `JOIN FETCH` or an entity graph when loans are loaded with their copies and titles.
- Add an index for every column a report filters or sorts on, in the same migration.

## When the application does not start

Read the `backend` container logs first; the cause is almost always one of:

| Message | Cause |
|---|---|
| `Schema-validation: missing column` | Entity field without a migration, or a type mismatch |
| `Validate failed: migration checksum mismatch` | A migration already in git was edited — revert it and add a new one |
| `FlywayException: Found non-empty schema` | Stale volume: `docker compose down -v`, then `up --build` |

```bash
cd shelfwise-code/backend && mvn clean install -DskipTests
cd shelfwise-code && docker compose up --build
```
