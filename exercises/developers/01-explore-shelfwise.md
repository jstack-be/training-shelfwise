# Exercise 1 – Explore Shelfwise

**15 min** · Clone the repo (branch `main`), open Claude Code in it, ask three questions.

1. **Ask what it is.** Type: “What is Shelfwise? Explain it in three sentences.”
2. **Ask about a rule.** Type: “How much does it cost to reserve a book?”
3. **Ask where it lives.** Type: “In which file is this rule enforced?”
4. **Watch it work.** While it answers, look at what it does. Which files does it search or open?

**Done when** you know the answer, which file enforces it, and which files the agent opened.

<details>
<summary><b>Solution</b></summary>

**Reserving costs €0.50 for adults and seniors. Youth members reserve for free.**

- **What it is:** Shelfwise lends books, audiovisual items and magazines in six branches, with fines, reservations and a member portal.
- **The rule:** a reservation costs €0.50 for ADULT and SENIOR members and nothing for YOUTH (summary.md §9.2). It isn’t refunded if you cancel.
- **Where it lives:** the amounts are in `MemberCategory`; `ReservationService` charges the fee. You don’t need to read the code: the agent found it.
- **How it worked:** CLAUDE.md doesn’t give the amount, so the agent had to search the project and open files. That loop makes it an agent.

**Take-away:** you ask in plain language; the agent searches and reads the project for you.

Check: summary.md §9.2 and S-12 (Leo Lane, 5 fees of €0.50). Results branch: https://github.com/jstack-be/training-shelfwise/tree/results
</details>
