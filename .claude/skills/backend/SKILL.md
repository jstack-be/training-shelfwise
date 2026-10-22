---
name: backend
description: Backend helper for Shelfwise.
---

# Backend

Use this skill for all backend work. Read `docs/backend-conventions.md` first and follow it.

## New endpoint

1. Add a method to the controller in `<feature>/web`.
2. Add request and response records to `<Feature>Dtos.java`.
3. Put the logic in the service. Use `@Transactional` when you write to the database.
4. Throw `BusinessRuleException` for refused actions so the API returns 422.
5. Add the endpoint to the matching `*.api.ts` file in the front-end.
6. Add an integration test.

## Migrations

Add a new file in `db/migration` with the next version number. Do not change existing migrations.

## Tests

Run `mvn test` after every change. Integration tests need Docker.

## Performance

- Avoid N+1 queries: use `JOIN FETCH` or an entity graph when you load loans with their copies and titles.
- Add an index for every column you filter on in a report.

## When the application does not start

Run `mvn clean install -DskipTests`, then `docker compose up --build` again. Check the logs of the `backend`
container for Flyway or Hibernate validation errors.

## Code review

Before you finish, check that the code is clean, readable and follows the conventions.
