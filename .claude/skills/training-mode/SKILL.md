---
name: training-mode
description: Drive the running Shelfwise stack in training mode — move the business date, run daily processing, read the notification outbox, reset to the seed data and pick a seed scenario (S-01 to S-19). Use when checking a change end to end, when a rule depends on time (due dates, overdue notices, holds, lost items), or when a demo needs a specific member situation.
---

# Training mode and the business date

The seed data describes **Monday 2026-03-16**. With `SHELFWISE_TRAINING_MODE=true` (the default in `compose.yaml`)
an administrator can move the business date, so anything time-dependent can be tested without waiting.

Domain code never calls `LocalDate.now()`; it asks `BusinessDateService`. That is what makes this work.

## Cookbook

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
- Moving the date to X runs the daily processing through X−1; the notices of X appear after the run for X.
- Nothing is e-mailed: every notification is a row in the outbox with status "SENT (simulated)".

## Seed scenarios

Members `NPL000001` to `NPL000019` are the scenarios S-01 to S-19 of Appendix B; each one covers a specific case,
for example S-05 is blocked at exactly €5.00, S-08 is a SENIOR with an accrued fine of €1.50, S-13 is a YOUTH with
accrued fines on a BOOK (€0.00) and an AUDIOVISUAL (€6.00). The full list is in
`backend/src/main/java/be/npl/shelfwise/seed/SeedScenarios.java` — read it there instead of guessing a member.

Staff accounts (password `shelfwise`): `admin`, `manager.cen`, `librarian.cen`, `librarian.riv`.

## Checking a change end to end

1. `cd shelfwise-code && docker compose up --build`
2. Reset to the seed data.
3. Log in with the role that uses the feature (member in the portal, librarian or manager at the desk).
4. Perform the action in the UI or through the API.
5. Move the business date when the rule depends on time, then run daily processing.
6. Compare the result — screen, charges, outbox — with the acceptance examples in Appendix A of `summary.md`.
