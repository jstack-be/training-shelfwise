# Fine rules

---
name: fines
description: Explains fines and fine calculations for members and staff.
---

Use this skill when a fine has to be calculated or explained.

## Rates

The daily rates per member category and material type are in
`shelfwise-analysis/06-reference/fine-tariffs.csv`. Read the file and use the rate for the category and material.

## Calculation

1. Count the days between the due date and the return date, without Sundays and public holidays.
2. Apply the grace period.
3. Multiply by the daily rate.
4. Apply the senior discount.
5. Round to cents.
6. Apply the maximum of €10.00.

## Explaining

Explain the fine in plain language and show how you got to the amount.
