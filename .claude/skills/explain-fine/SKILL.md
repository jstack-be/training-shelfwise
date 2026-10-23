---
name: explain-fine
description: Explain or verify an overdue fine with a day-by-day breakdown, following the order of calculation of BR-FIN-01 to BR-FIN-06. Use when someone asks why a member was charged a fine, how much a late return will cost, whether an accrued fine is right, or wants a fine explained to a member in plain language.
---

# Explain an overdue fine

## Steps

1. Collect the inputs: member category on the date of calculation, material type, due date, return date (for an
   accrued fine: the business date) and the replacement cost of the copy. Take them from the loan or the charge; ask
   when one is missing. Do not derive the category from the member's age today — it is set at registration and
   re-evaluated at membership renewal.
2. Count the chargeable days with the script, which lists every day with its status:

   ```bash
   python3 .claude/skills/explain-fine/scripts/chargeable_days.py <due-date> <return-date>
   ```

3. Apply the order of calculation below. Do not change the order, even if a shortcut seems equivalent.
4. Compare the result with the closest example in Appendix A.3 of `summary.md`. If your result and the code
   disagree, say so and do not adjust either one.
5. Write the explanation with the template for the audience (member or staff).

## Order of calculation (F-FIN-06, summary.md §10.6)

1. Count the chargeable days: `CalendarRules.countOpeningDays(dueDate, endDate, closingDay)`. The due date itself
   does not count, the end date does. Sundays and public holidays are skipped.
2. At most `graceChargeableDays` (1) chargeable days → the fine is €0.00. Stop (BR-FIN-03).
3. Multiply the chargeable days by the daily rate for the category and material type (summary.md §10.2). SENIOR uses
   the ADULT rate.
4. SENIOR: multiply by `(100 − seniorDiscountPercent) / 100`, so −50% (BR-FIN-04).
5. Round half up to two decimals with `Money.round` — once per copy, never per day (BR-FIN-05).
6. Apply the maximum: the lower of `maxFinePerCopy` (€10.00) and the replacement cost of the copy (BR-FIN-06).

Worked example: a SENIOR returns a BOOK due Monday 2026-03-02 on Saturday 2026-03-07 → 5 chargeable days;
5 × €0.25 = €1.25; −50% = €0.625; rounded half up = €0.63; the maximum does not apply.

## Rates (summary.md §10.2)

| Material | ADULT | YOUTH | SENIOR |
|---|---|---|---|
| BOOK | €0.25 per day | €0.00 | ADULT rate −50% |
| AUDIOVISUAL | €1.00 per day | €0.50 per day | ADULT rate −50% |
| MAGAZINE | €0.10 per day | €0.00 | ADULT rate −50% |

Only the ADULT and YOUTH rates are maintained in the `fine_rate` table (F-ADM-01); SENIOR is derived.

## Gotchas

- `shelfwise-analysis/06-reference/fine-tariffs.csv` is a LibTrack 4 export with old rates (BOOK €0.20) and a 2-day
  grace period. It does **not** apply to Shelfwise. Always use the rates above.
- Grace period: with exactly 1 chargeable day there is no fine. From 2 chargeable days on, **all** days are charged,
  including the first.
- A YOUTH member pays €0.00 on BOOK and MAGAZINE. Still show the chargeable days, so the member sees why.
- An accrued fine is not a charge yet (F-FIN-07). Say "so far" and mention that it grows until the copy is returned.
- A lost item (42 days overdue, F-FIN-08) is replacement cost + €5.00 administration fee on top of the overdue fine.

## Template for a member

> You returned *<title>* on <weekday, date>. It was due on <weekday, date>.
> Between those dates the library was open on <n> days: <list>. Sundays and public holidays (<list>) don't count.
> <n> days × €<rate> = €<amount>. <Senior discount / maximum, if any.>
> Your fine is **€<fine>**.

No rule IDs, no internal terms such as "chargeable day".

## Template for staff

The same breakdown as a table (date, open or closed, counted), followed by each calculation step with its BR-ID and
a reference to the matching AC example in Appendix A.3.
