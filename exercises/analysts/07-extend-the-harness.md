# Exercise 7 – Extend the harness (analysts)

**25 min** · Rules you parked in exercise 5 become a subagent, permissions and a hook.

**Same as the developers, with one different permission.** Let Claude write the files; you check and test them.

1. **A subagent.** Turn the parked “Questions about the analysis pack” into `.claude/agents/analysis-researcher.md`, with only Read, Grep and Glob.
2. **Permissions.** In `.claude/settings.json`: deny `Edit(/shelfwise-analysis/**)` (the analysis pack is the source: never change it), deny Read on `.env` files, ask before `git push`.
3. **A hook.** Ask Claude: “Add a PreToolUse hook on Bash that blocks git commit on main, with exit code 2.”
4. **Prove it.** Ask “What is the grace period, and which document decides?” without naming an agent. Ask Claude to change a value in FA-06. Ask it to commit on main.

**Done when** all three kick in by themselves, without you mentioning them in the prompt.

> `.claude/settings.json` already contains a deny rule for the `exercises/` folder. Keep it, and add your rules next to it.

<details>
<summary><b>Solution</b></summary>

- **Subagent:** the grace period question goes to analysis-researcher by itself. Answer: **1 opening day** (BR-FIN-03). The fines workshop (D2) changed it from the 2 days in BUS-02.
- **Permissions:** changing FA-06 is blocked. Pushing asks first.
- **Hook:** the commit on main is blocked, and Claude says why.

See the developer solution for the full setup on the results branch.
</details>
