# Exercise 8 – A review agent

**20 min** · Have an agent review a bad change, with read access only.

1. **Break a rule.** New branch. In `FineCalculator`, change `<=` into `<` in the grace check (BR-FIN-03). Commit it.
2. **Write the reviewer.** Turn the spec compliance review from “Before you finish” into `.claude/agents/spec-reviewer.md`.
3. **Run it headless.** `claude -p "Review this branch with the spec-reviewer agent" --allowedTools "Bash(git diff:*)"`
4. **In CI (optional).** With a fork: have the agent turn that command into a GitHub Actions workflow on `pull_request`.

**Done when** the review names BR-FIN-03, FineCalculator and AC-FIN-01 without you saying what to look for.

<details>
<summary><b>Solution</b></summary>

**One agent, one fixed task, read access only.**

- **What a good review says:** BR-FIN-03 is broken in `FineCalculator`: 1 chargeable day now costs €0.25. AC-FIN-01 expects €0.00 and fails.
- **Why it finds it:** spec-reviewer reads §16 of summary.md and every changed file in full, and checks each touched rule against its AC test.
- **Why read-only:** a reviewer that can edit fixes things without telling you. With only read access and `git diff`, you stay in charge of the change.
- **In CI:** the same command in a GitHub Actions job on `pull_request`. The agent comments; a human decides on the merge.

**Take-away:** a review agent is only as good as the rules, IDs and tests you give it.

Results branch: https://github.com/jstack-be/training-shelfwise/blob/results/.claude/agents/spec-reviewer.md
</details>
