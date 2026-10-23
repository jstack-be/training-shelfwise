---
name: writing-tests
description: Conventions for Shelfwise tests — unit tests for pure calculations, Testcontainers integration tests against the API, and Angular specs. Use when adding or changing a test, when an acceptance example (AC-ID) needs coverage, or when a test fails and the expected value is in question.
---

# Writing tests

Every acceptance example of Appendix A has a test whose name starts with its AC-ID, so `grep -r "AC-FIN-15"` finds
it. Update the traceability table in `shelfwise-code/README.md` when you cover new IDs.

## Unit tests (`src/test/java/be/npl/shelfwise/unit/`)

For pure calculations and member rules. No Spring context: use `RulesProperties.defaults()` and
`support/Holidays2026.CLOSING` as the closing-day predicate.

Use `@ParameterizedTest` with `@CsvSource` for tables of examples, the AC-ID in the first column, `name = "{0}"`, and
`.as(id + " …")` on every AssertJ assertion so a failure names the example.

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

Run them after every change to a Java file: `mvn test -Dtest='*Test'`.

## Integration tests (`*IT.java`)

- Extend `IntegrationTestBase`, which resets the seed of Appendix B (business date Monday 2026-03-16) before each
  test, so every test starts from a known situation.
- Build data with `TestFixtures` (`fx`) and call the API through `support/ApiClient` as the role that uses the
  feature — not the service directly.
- A rule that applies to the desk and to the portal needs a test for each channel.
- Assert the refusal, not only the status: `code`, `ruleId` and HTTP 422.
- `@DisplayName` starts with the AC-ID: `@DisplayName("AC-FIN-22: librarian cannot waive on own account")`.
- They need Docker (Testcontainers). Run the full suite with `mvn test`.

## Front-end specs

- Every helper in `src/app/helpers` has a `.spec.ts` next to it.
- Test stores through their public functions, never through their internal signals.
- `npx ng test --watch=false`.

## Rules

- **Never change an expected value to make a test pass.** If an acceptance example looks wrong, say so and explain
  why in business terms; the product owner decides.
- A new rule without a failing test first is a rule you cannot prove. Write the test, see it fail, then implement.
- Do not add a test that asserts behaviour the specification does not describe — ask, or record the choice in
  `shelfwise-code/ASSUMPTIONS.md`.
