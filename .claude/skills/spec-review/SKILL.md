---
name: spec-review
description: The specification compliance review that every Shelfwise change goes through before it is reported as done — checks each touched business rule against summary.md in both channels, with its code, ruleId and test. Use after finishing an implementation, before reporting a task as done, before opening a pull request, or when asked whether a change follows the specification.
---

# Specification compliance review

Run this after the tests pass and before you report a task as done — also for a small change.

## Steps

1. Read §16 (business rules index) and §17 (features and increments) of `summary.md` completely.
2. Read the sections of `summary.md` and the FA documents in `shelfwise-analysis/02-functional/` that belong to
   every feature you touched. Where the two disagree, `summary.md` prevails.
3. Read every changed file **in full**, not only the diff — a correct diff can still leave a half-applied rule.
4. For every business rule the change touches, check:
   - it is enforced in every channel where it applies (desk **and** portal);
   - the refusal returns the right `code` and `ruleId` with HTTP 422;
   - a test exists whose name starts with the AC-ID.
5. Check the recurring mistakes:
   - no business parameter hard-coded (it belongs in `RulesProperties`, an enum, or the database);
   - no `LocalDate.now()` in domain code (use `BusinessDateService`);
   - no money in `double`/`float`, no rounding outside `Money`;
   - no entity of another feature read through its repository;
   - no behaviour invented — anything the specification leaves open is in `shelfwise-code/ASSUMPTIONS.md`.
6. Confirm the traceability table in `shelfwise-code/README.md` lists the AC-IDs you added.

## Report

Always report the result as this table, and never report a task as done while a row is "missing":

| Rule ID | Where it is enforced | Test that proves it | Status |
|---|---|---|---|
| BR-… | `…Service#method` (desk + portal) | `…IT#test` (AC-…) | OK / missing / unsure |

Name every "unsure" row explicitly in your summary, with the question to ask the product owner. Do not resolve a
specification conflict yourself.
