# Exercise 6 – A skill for Shelfwise

**25 min** · Turn the parked fine section into a skill, and prove it loads.

1. **Move it.** Move “How to calculate an overdue fine” from `parked.md` to `.claude/skills/explain-fine/SKILL.md`. `/skill-creator` can help.
2. **Write the description.** Say when to use it, in the words a user types: “why was I charged”, “what will a late return cost”.
3. **Add a gotcha.** `fine-tariffs.csv` has old LibTrack 4 rates and a 2-day grace period. Tell the skill never to use it.
4. **Prove it loads.** New session: “Hugo Hale (S-08) asks why his fine is €1.50.” Does the skill load? Ask something unrelated: it shouldn’t.

**Done when** the skill loads for the fine question, not for the unrelated one, and explains €1.50 day by day.

<details>
<summary><b>Solution</b></summary>

**The explain-fine skill on results: one job, a sharp description and a script.**

- **Description:** “Use when someone asks why a member was charged a fine, how much a late return will cost …”: the words a user types.
- **Steps:** collect the inputs, count the days with the script, apply BR-FIN-01 to 06 in order, compare with A.3, use the member or staff template.
- **Script:** `scripts/chargeable_days.py` lists every day as open or closed. The script counts, so the model doesn’t have to count Sundays itself.
- **Gotchas:** `fine-tariffs.csv` holds old LibTrack 4 rates. From 2 chargeable days on, all days count. SENIOR pays the ADULT rate −50%, rounded once.

Hugo Hale (S-08): due Mon 2 March, 12 chargeable days by 16 March. 12 × €0.25 = €3.00, minus 50% = €1.50.

The other six skills follow the same pattern. Results branch: https://github.com/jstack-be/training-shelfwise/tree/results/.claude/skills/explain-fine
</details>
