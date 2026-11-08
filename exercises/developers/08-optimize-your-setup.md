# Exercise 8 – Optimize your setup

**75 min** · Build online pre-registration (summary.md §6.2) step by step, and improve your setup along the way.

1. **Look at your setup.** Go through CLAUDE.md, your skills, subagents and hooks. What is missing, unclear or never used?
2. **Build the feature** on your own branch (`git switch -c feature/pre-registration`). Research → Interview → Plan → Build → Review. Each step writes a file: research.md, SPEC.md, PLAN.md, then one slice at a time.
3. **Improve it.** After each step: what went wrong or needed steering? Fix the cause in a skill, subagent, hook or CLAUDE.md, not only in the code.
4. **Compare.** Less steering, less context, fewer fixes? Keep what helps; remove what never loads.

**Done when** slice 1 is green and reviewed against SPEC.md, and you can show one setup change and what it improved.

**Starting prompts** (improve them as you go):

1. **Research** (new session):
   > Research how members are registered at the desk, how the portal and its permissions work, and how daily processing runs. Use separate subagents in parallel per area. Facts with file paths only, no solutions. Write it to docs/pre-registration/research.md.
2. **Interview:**
   > Read @docs/pre-registration/research.md and summary.md §6.2. Interview me until you could write the spec for online pre-registration. Dig into the hard parts and skip what the research already answers. Then write docs/pre-registration/SPEC.md.
3. **Plan** (new session, plan mode):
   > Read @docs/pre-registration/SPEC.md and @docs/pre-registration/research.md. Write a plan in two parts: first the approach, the patterns to follow with file paths, and the open questions; then vertical slices, each a thin path through database, API and UI that can be demoed and tested on its own. Once I approve, save it as docs/pre-registration/PLAN.md.
4. **Build** (`/clear` per slice):
   > Implement slice 1 of @docs/pre-registration/PLAN.md test-first. Run the tests, show me the output, tick the slice off and stop.
5. **Review** (new session):
   > Use a subagent to review the diff of this branch against @docs/pre-registration/SPEC.md. Report only gaps that affect correctness, skip style.

<details>
<summary><b>Solution</b></summary>

**The feature: questions the interview should raise** (summary.md doesn’t answer them):

- Is the category set on the pre-registration date or on the completion date at the desk? (BR-MEM-01 says “registration date”.)
- Are the 30 days calendar days or opening days? Daily processing runs every calendar day (§5.2).
- How does a YOUTH pre-register online: who is the guardian, and how is that entered?
- Does the F-MEM-02 duplicate check also run online, and what does the visitor see without leaking personal data?
- Who completes it at the desk (LIBRARIAN, BRANCH_MANAGER), and what happens when the identity doesn’t match?

**A good first slice:** a visitor submits the form, the pre-registration is stored and shows up in a list at the desk. No completion, fee or expiry yet.

**Classic mistakes:**

- a horizontal plan (all database first, then all API);
- filling in the open questions silently instead of asking;
- the membership fee as a charge on the account, while BR-MEM-04 says membership fees are not charges.

**Setup changes that usually pay off:**

- **CLAUDE.md:** only what every task needs. Point to sources instead of copying them.
- **Skills:** one procedure each, with a description in the words a user types. Remove skills that never load.
- **Subagents:** for read-heavy detours such as research and review, with read-only tools. Building stays in the main session.
- **Hooks:** for what must always happen, such as tests before Claude stops. Instructions ask; hooks enforce.

**Take-away:** optimize from real mistakes, one change at a time, and test each change on real work.

A reference setup on the results branch: https://github.com/jstack-be/training-shelfwise/tree/results/.claude
</details>
