---
name: explain-fine
description: Explain an overdue fine with a day-by-day breakdown and check it against the specification. Use when someone asks why a member was charged a fine, how much a late return will cost, or wants a fine explained to a member in plain language.
---

# Explain an overdue fine

## Steps

1. Collect the inputs: member category on the date of calculation, material type, due date, return date (for an
   accrued fine: the business date) and the replacement cost of the copy. Take them from the loan or charge; ask
   when one is missing. Do not guess the category from the member's age today.
2. Count the chargeable days with the script, which lists every day with its status:

   ```bash
   python3 .claude/skills/explain-fine/scripts/chargeable_days.py <due-date> <return-date>
   ```

3. Apply the order of calculation of `summary.md` §10.6 (BR-FIN-01 to BR-FIN-06), with the rates of §10.2.
4. Compare the result with the closest example in Appendix A.3 of `summary.md`. If your result and the code disagree,
   say so and do not adjust either one.
5. Write the explanation with the template for the audience (member or staff).

## Gotchas

- `shelfwise-analysis/06-reference/fine-tariffs.csv` is a LibTrack 4 export with old rates and a 2-day grace period.
  It does not apply to Shelfwise. Use the rates of `summary.md` §10.2.
- Grace period: with exactly 1 chargeable day there is no fine. From 2 chargeable days on, **all** days are charged,
  including the first.
- SENIOR members use the ADULT rate with 50% discount. Round once, after the discount and before the maximum.
- A YOUTH member pays €0.00 on BOOK and MAGAZINE. Still show the chargeable days, so the member sees why.
- An accrued fine is not a charge yet. Say "so far" and mention that the amount grows until the copy is returned.

## Template for a member

> You returned *<title>* on <weekday, date>. It was due on <weekday, date>.
> Between those dates the library was open on <n> days: <list>. Sundays and public holidays (<list>) don't count.
> <n> days × €<rate> = €<amount>. <Senior discount / maximum, if any.>
> Your fine is **€<fine>**.

No rule IDs, no internal terms such as "chargeable day".

## Template for staff

Same breakdown as a table (date, open or closed, counted), followed by each calculation step with its BR-ID and a
reference to the matching AC example.
