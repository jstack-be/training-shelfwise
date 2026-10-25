# Exercise 5 – Slim down CLAUDE.md

**20 min** · The CLAUDE.md on main has 539 lines, and every session pays for them.

1. **Measure.** New session, run `/context`. How many tokens does CLAUDE.md take?
2. **Decide what stays.** Keep only what every task needs and Claude can’t find in the code itself. Fix what is wrong, like the Angular version.
3. **Park the rest.** Move everything else to a new file, for example `parked.md`. Exercises 6 and 7 give it a proper home. Delete what nobody needs.
4. **Test the effect.** New session: `/context`, then ask where the fine is calculated. Still a good answer?

**Done when** CLAUDE.md fits on one screen and the rest sits in `parked.md`, ready for exercises 6 and 7.

<details>
<summary><b>Solution</b></summary>

**Keep what every task needs. Park what is needed sometimes. Delete the rest.**

- **Keep:** Read this first, Before you finish, Security and Glossary, plus a pointer to the business rules in summary.md §4 and §10.
- **Park:** fine calculation, material type, tests, training mode, commits, analysis questions, spec review, Java and Angular rules: needed sometimes, not always.
- **Delete:** project background, repository layout, tech stack and value tables. Claude finds them in the code or in summary.md, and copies go stale.
- **Where it ends up:** on results, the parked parts became seven skills, two agents, hooks and a CLAUDE.md per folder. You build part of that in exercises 6 and 7.

**Take-away:** CLAUDE.md is paid in every session. Keep only what every task needs; the rest waits in `parked.md`.

Results branch: https://github.com/jstack-be/training-shelfwise/blob/results/CLAUDE.md
</details>
