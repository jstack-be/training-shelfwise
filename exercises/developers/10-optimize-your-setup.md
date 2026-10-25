# Exercise 10 – Optimize your setup

**40 min** · Improve your setup, and build a feature along the way to test each change.

1. **Look at your setup.** Go through CLAUDE.md, your skills, subagents and hooks. What is missing, unclear or never used?
2. **Improve it.** Sharpen, move or add: a procedure to a skill, research to a subagent, a must-do check to a hook. Keep CLAUDE.md lean.
3. **Test it on a feature.** Build something while you optimize, for example CR-01 in summary.md Appendix C (3 renewals for books). Does your setup kick in by itself?
4. **Compare.** Less steering, less context, fewer fixes? Keep what helps; remove what never loads.

**Done when** you can show one change to your setup, and what it improved in the build.

<details>
<summary><b>Solution</b></summary>

**There is no single answer. These are the moves that usually pay off.**

- **CLAUDE.md:** only what every task needs. Point to sources instead of copying them; move the rest to a skill or a nested CLAUDE.md.
- **Skills:** one procedure each, with a description in the words a user types. Remove skills that never load.
- **Subagents:** for read-heavy detours such as research and review: read-only tools, a short report back. Building stays in the main session.
- **Hooks:** for what must always happen: tests before Claude stops, no commit on main. Instructions ask; hooks enforce.

**Take-away:** optimize from real mistakes, one change at a time, and test each change on real work.

A reference setup on the results branch: https://github.com/jstack-be/training-shelfwise/tree/results/.claude
</details>
