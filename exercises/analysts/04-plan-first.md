# Exercise 4 – Plan first (analysts)

**25 min** · One feature with a hidden catch. Plan the test cases before anything is written.

**Different from the developers:** you plan acceptance test cases instead of code.

1. **Plan with a subagent.** Shift+Tab to plan mode, then type: “Plan the acceptance test cases for the book-drop return from summary.md §8.5 (AC-LEN-11), and write them to book-drop-tests.md. Make sure to use a Plan subagent.”
2. **Read the plan.** Is there a case with a book that is returned *late* through the book drop? If not, ask for one before you approve.
3. **Approve it.** Let it write `book-drop-tests.md`. Don’t steer.
4. **Check the result.** Compare the cases with the solution below.

**Done when** the plan has a late book-drop case with the right fine, before you approved it.

<details>
<summary><b>Solution</b></summary>

**The return date *and* the fine use the book-drop date, not the check-in day.**

- **Book drop:** the return date is the last opening day before check-in. Checked in Mon 16 March → return date **Sat 14 March**. Checked in Tue 7 April, after Easter → **Sat 4 April** (Sunday 5 and Easter Monday 6 are closed).
- **The case that matters:** a BOOK due Thu 12 March, dropped after closing on Saturday, checked in Mon 16 as a book-drop return. Return date Sat 14: 2 chargeable days (Fri 13, Sat 14), fine **€0.50**. Wrong answer: €0.75 (fine counted up to Monday).
- **Why AC-LEN-11 misses it:** in AC-LEN-11 the book isn’t late, so the fine is €0.00 either way. Only a late book shows the mistake.

**Take-away:** plan when a change looks small but touches more than one rule. A Plan subagent drafts it; you check it.
</details>
