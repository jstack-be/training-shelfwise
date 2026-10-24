# Shelfwise — instructions for Claude

Shelfwise is the new lending system of Northwick Public Library (NPL), replacing LibTrack 4.

## Read this first

- `summary.md` is the **single source of truth** for functional behaviour of the code. Where the analysis pack and
  `summary.md` differ, `summary.md` prevails. Appendix C of `summary.md` is out of scope.
- For questions about the analysis pack itself (`shelfwise-analysis/`), use the `analysis-researcher` agent. When
  documents conflict, never choose a value yourself; unresolved conflicts go to the product owner, Hannah Mercer.
- Never invent behaviour. If something is not specified, ask. If you are told to continue anyway, choose the simplest
  reasonable behaviour and record it in `shelfwise-code/ASSUMPTIONS.md`.
- Load the matching skill before you start a kind of work, not afterwards.

## Business rules

`summary.md` §4 and §10 hold the business rules; the parameters are in `RulesProperties`, `MemberCategory`
and `MaterialType`. Read them there; never rely on a remembered value.

## Before you finish a task

Also for small changes:

1. Run the full back-end suite (`cd shelfwise-code/backend && ./mvnw test`), and the front-end tests when you changed
   the front-end. A Stop hook already runs the unit tests when Java files changed; fix what it reports.
2. Have the `spec-reviewer` agent review the change and report its table.

## Commits and pull requests

Before committing, creating a branch or opening a pull request, load the `commit-and-pr` skill.

## Security

- Never print or copy `JWT_SECRET` or any other secret. Never commit secrets; the JWT secret in `compose.yaml` is a
  development value.
- Never use production data. Seed and test data only.
- Do not call external URLs from the application or the tests. The payment provider is simulated
  (`/api/provider/payments`).
- Never log a password or a token.

## Glossary

| Term | Meaning |
|---|---|
| Member | A registered library user. Never "patron" or "customer". |
| Title / Copy | A work in the catalogue / a physical item of a title, with a barcode. |
| Branch | One of the six libraries: CEN, RIV, EAS, MIL, HAR, WES. |
| Loan / Renewal | A copy checked out to a member / extending its due date. |
| Reservation / Hold | A request for a title / a copy set aside at the pickup branch. |
| Charge | An amount a member owes: overdue fine, replacement cost, administration fee, reservation fee. |
| Waiver | Cancelling all or part of a charge by staff. |
| Business date | The date the system works with; the real date in production, adjustable in training mode. |
| Daily processing | The nightly run that sends notices, expires holds and declares loans lost. |
