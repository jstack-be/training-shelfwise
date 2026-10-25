# Exercise 10 – Optimize your setup (analysts)

**40 min** · Improve your setup, and test each change on an analysis task.

**Same as the developers, with a different test task.**

1. **Look at your setup.** Go through CLAUDE.md, your skills, subagents and hooks. What is missing, unclear or never used?
2. **Improve it.** Sharpen, move or add: a procedure to a skill, research to a subagent, a must-do check to a hook. Keep CLAUDE.md lean.
3. **Test it on an impact analysis.** Type: “Write the impact analysis for CR-01 in summary.md Appendix C (3 renewals for books). Which rules, acceptance examples, analysis documents and screens change, and what is still open?” Does your setup kick in by itself?
4. **Compare.** Less steering, less context, fewer corrections? Keep what helps; remove what never loads.

**Done when** you can show one change to your setup, and what it improved in the analysis.

<details>
<summary><b>Solution</b></summary>

What a good impact analysis of CR-01 contains:

- **BR-LOA-03** gets a value per material type: BOOK 3, AUDIOVISUAL and MAGAZINE 2. The parameter in **summary.md §4** (“Maximum renewals per loan: 2”) changes the same way.
- **AC-LEN-06** (third renewal refused) no longer holds for books.
- The portal shows the **number of renewals left** per loan (§6.6): for books that becomes up to 3.
- **FA-04 §4** in the analysis pack still says 2.

See the developer solution for the setup moves that usually pay off.
</details>
