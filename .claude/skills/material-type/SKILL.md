---
name: material-type
description: Complete checklist for adding a new material type (for example BOARD_GAME or E_READER) to Shelfwise, across the enum, the database, the seed data, the reports and the front-end. Use whenever a new kind of lendable material, its loan period, its fine rates or its replacement cost has to be added.
---

# Adding a new material type

A material type touches the whole stack. Work through this list completely and in this order; stop and ask when a
value is not specified rather than inventing one.

## Before you start

Ask the product owner (or the person requesting it) for: the loan period in days, the default replacement cost, the
ADULT and YOUTH daily fine rates, and whether the type has its own loan limit or counts towards the general one.
Record every answer, and every choice the specification leaves open, in `shelfwise-code/ASSUMPTIONS.md`.

## Back-end

1. Add the value to `catalogue/entity/MaterialType.java` with its loan period and default replacement cost.
2. Add a Flyway migration (`V<next>__<snake_case>.sql`) that extends the check constraint on `title.material_type`.
   Never edit an existing migration — see the `backend-feature` skill.
3. In the same migration, insert the `fine_rate` rows for ADULT and YOUTH. SENIOR gets no row: it uses the ADULT
   rate with the 50% discount.
4. Check `MemberCategory` and `LoanLimit` for a per-material limit, and whether it counts towards the general loan
   limit.
5. Add at least two titles of the new type to `SeedCatalogueData`, so it is visible in training mode.
6. Update `ReportService` so R-01 and R-02 group the new type correctly.

## Front-end

7. Add the value to `MaterialType` in `src/app/core/models.ts`.
8. Add a label to `MATERIAL_LABEL` in `src/app/helpers/labels.ts` (never show the raw enum value).
9. Check the catalogue filters in `pages/catalogue` and the fine rate screen in `pages/admin`.

## Proving it

10. Add tests for the loan period, the due date on a closing day, and the fine at the new rates, with their AC-IDs
    (see the `writing-tests` skill). Update the traceability table in `shelfwise-code/README.md`.
11. Run `mvn test` and `npx ng test --watch=false`.
12. Run the `spec-review` skill.
13. Update documentation derived from `summary.md` only after the product owner has approved the new type.
