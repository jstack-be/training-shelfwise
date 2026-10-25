# Exercise 8 – A review agent (analysts)

**20 min** · Have an agent review a bad change, with read access only.

**Different from the developers:** you break a document instead of code.

1. **Break a rule.** Open `shelfwise-analysis/02-functional/FA-06-fines-and-payments.md` in a text editor yourself. In §3.8, example 1: change “Within grace period” to “1 × €0.25” and the fine from €0.00 to €0.25. Save it; don’t commit.
2. **Write the reviewer.** Ask Claude: “Create a read-only agent .claude/agents/doc-reviewer.md that checks changed analysis documents against the business rules in summary.md §16 and the examples in Appendix A.”
3. **Run it.** New session: “Review my uncommitted changes with the doc-reviewer agent.”
4. **Headless (optional).** In a terminal: `claude -p "Review my uncommitted changes with the doc-reviewer agent" --allowedTools "Bash(git diff:*)"`

**Done when** the review names BR-FIN-03 and AC-FIN-01 without you saying what to look for. Then undo your change.

<details>
<summary><b>Solution</b></summary>

- **What a good review says:** example 1 now breaks **BR-FIN-03** (grace period): a book returned on the first chargeable day costs nothing. **AC-FIN-01** in summary.md expects €0.00.
- **Why read-only:** a reviewer that can edit fixes things without telling you. With read access only, you stay in charge of the change.

**Take-away:** a review agent is only as good as the rules, IDs and examples you give it.
</details>
