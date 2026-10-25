# Exercise 2 – Two models

**15 min** · Give a fast and a strong model the same task, and compare what they do.

1. **The task.** Type: “Write a unit test for each worked example in FA-04 §3.1, and run only those tests.”
2. **Round 1: Haiku.** Type `/model` and choose Haiku. Give the task. Note which test fails and what Haiku does about it.
3. **Round 2: Opus.** Put round 1 aside with `git stash -u`, then `/clear`. Type `/model`, choose Opus, and give exactly the same task.
4. **Who got it right?** Is the code wrong, or the example? Check summary.md §8.3. Which model found the answer without changing the test?

**Done when** you know which test fails, the correct due date, and which model got it right.

<details>
<summary><b>Solution</b></summary>

**Worked example 4 in FA-04 is wrong. The code is right.**

- **The failing example:** FA-04 §3.1, example 4, says a book checked out on Mon 4 May is due Mon 25 May. But 25 May is Whit Monday, a public holiday.
- **The right answer:** the due date moves to the next opening day: Tue 26 May (BR-LOA-02). That is what `DueDateCalculator` returns.
- **The other five:** examples 1, 2, 3, 5 and 6 are right. A holiday inside the loan period doesn’t move the due date: only the last day counts.
- **Wrong fixes:** changing the expected date to 25 May, or moving the due date for every holiday in the period. Both make the test green and the system wrong.

**Take-away:** never let a model make a test pass by changing what it expects. Report the doc error to the PO.

Check: summary.md §8.3 and A.1, public-holidays-2026.csv. Results branch: https://github.com/jstack-be/training-shelfwise/tree/results
</details>
