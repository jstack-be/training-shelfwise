# Exercise 7 – Extend the harness

**25 min** · Rules you parked in exercise 5 become a subagent, permissions and a hook.

1. **A subagent.** Turn the parked “Questions about the analysis pack” into `.claude/agents/analysis-researcher.md`, with only Read, Grep and Glob.
2. **Permissions.** In `.claude/settings.json`: allow tests and read-only git, ask before `git push`, deny Read on `.env` files.
3. **A hook.** A PreToolUse hook on Bash that blocks `git commit` on main. With exit code 2, Claude sees why it was blocked.
4. **Prove it.** Ask “What is the grace period, and which document decides?” without naming an agent. Ask it to push. Commit on main.

**Done when** all three kick in by themselves, without you mentioning them in the prompt.

> `.claude/settings.json` already contains a deny rule for the `exercises/` folder. Keep it, and add your rules next to it.

<details>
<summary><b>Solution</b></summary>

**On results: two scoped agents, one settings.json, three hooks.**

- **Agents:** analysis-researcher (Read, Grep, Glob, on Sonnet) answers analysis questions. spec-reviewer checks a change before it is called done.
- **Permissions:** allow: tests, builds and read-only git (`./mvnw test`, `npx ng test`, `git diff` …). ask: `git push`. deny: `Read(./**/.env)`, `Read(./**/.env.*)`.
- **Hooks:** before a Bash command: `block-commit-on-main.sh`. When Claude stops: unit tests if Java changed, ESLint if TypeScript changed.
- **Why no builder agent:** an agent that builds, commits and pushes on its own hides the process you need to see. Building stays in the main session.

Beyond the harness: ArchUnit, Checkstyle and ESLint enforce the coding rules in the build itself.

Results branch: https://github.com/jstack-be/training-shelfwise/tree/results/.claude
</details>
