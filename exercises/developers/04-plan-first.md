# Exercise 4 – Plan first

**25 min** · One feature with a hidden catch. Plan it before any code is written.

1. **Plan with a subagent.** Shift+Tab to plan mode, then type: “Plan the book-drop return from summary.md §8.5 (AC-LEN-11). Make sure to use a Plan subagent.”
2. **Read the plan.** Which places get the book-drop date, and which keep the check-in day? Does it mention the fine? If not, ask before you approve.
3. **Build it.** Approve the plan and let it build, with tests. Don’t steer during the build.
4. **Check the result.** A book-drop return checked in on Mon 16 March gets return date Sat 14, and no fine. Checked in on Tue 7 April, after Easter: Sat 4 April.

**Done when** the plan named the fine before any code was written, and the tests are green.

<details>
<summary><b>Solution</b></summary>

**Two lines in `returnCopy` use the check-in day. An agent easily changes only one.**

- **What should be there:** a book-drop checkbox on the Returns page. The return date becomes the last opening day before check-in: Mon 16 March → Sat 14.
- **The trap:** `returnCopy` sets the return date and calculates the fine on two separate lines, both with `today`. Change only the first, and the fine still counts Monday.
- **A new test that catches it:** no existing test covers this. Due Thu 12 March, dropped Saturday, checked in Mon 16: return date Sat 14, fine €0.50. With the trap: €0.75.
- **Why AC-LEN-11 misses it:** in AC-LEN-11 the book isn’t late either way, so the fine is €0.00 with or without the trap. Only a late book shows the mistake.

**Take-away:** plan when a change looks small but the data flows far. A Plan subagent drafts it; you check it.

Check: summary.md §8.5 and AC-LEN-11. The code: `LendingService.returnCopy` on GitHub.
</details>
