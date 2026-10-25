# Exercise 2 – Two models (analysts)

**15 min** · Give a fast and a strong model the same check, and compare what they find.

**Different from the developers:** you check the examples against the specification instead of writing tests.

1. **The task.** Type: “Check each worked example in FA-04 §3.1 against the rules and the calendar in summary.md. Which ones are right and which are wrong?”
2. **Round 1: Haiku.** Type `/model` and choose Haiku. Give the task. Note which examples it calls wrong.
3. **Round 2: Opus.** Type `/clear`, then `/model`, choose Opus, and give exactly the same task.
4. **Who got it right?** Compare both answers with summary.md §5.1 (calendar) and §8.3 (due date).

**Done when** you know which example is wrong, the correct due date, and which model got it right.

<details>
<summary><b>Solution</b></summary>

**Worked example 4 in FA-04 is wrong.**

- Example 4 says a book checked out on Mon 4 May is due Mon 25 May. But 25 May 2026 is **Whit Monday**, a public holiday (summary.md §5.1).
- The due date moves to the next opening day: **Tue 26 May** (BR-LOA-02).
- Examples 1, 2, 3, 5 and 6 are right. A holiday *inside* the loan period doesn’t move the due date: only the last day counts. A weaker model often misses example 4, or wrongly “corrects” example 3 (Labour Day inside the period).

**Take-away:** don’t trust an example because it is in a document. Report the error to the PO.
</details>
