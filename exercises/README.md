# Exercises

The exercises of the *Agentic coding* course, with a solution under each one.

- [developers/](developers/): the exercises as they are on the slides.
- [analysts/](analysts/): the same exercises for participants who don’t write code. They use Claude Code in this repo, but don’t build the app or run tests. Where an exercise is the same, the file says so and links to the developer version.

| # | Exercise | Developers | Analysts |
|---|---|---|---|
| 1 | Explore Shelfwise | [open](developers/01-explore-shelfwise.md) | [same](analysts/01-explore-shelfwise.md) |
| 2 | Two models | [open](developers/02-two-models.md) | [check instead of test](analysts/02-two-models.md) |
| 3 | Watch your context | [open](developers/03-watch-your-context.md) | [write a guide instead of a page](analysts/03-watch-your-context.md) |
| 4 | Plan first | [open](developers/04-plan-first.md) | [plan test cases instead of code](analysts/04-plan-first.md) |
| 5 | Slim down CLAUDE.md | [open](developers/05-slim-down-claude-md.md) | [same](analysts/05-slim-down-claude-md.md) |
| 6 | A skill for Shelfwise | [open](developers/06-a-skill-for-shelfwise.md) | [same](analysts/06-a-skill-for-shelfwise.md) |
| 7 | Extend the harness | [open](developers/07-extend-the-harness.md) | [one different permission](analysts/07-extend-the-harness.md) |
| 8 | Optimize your setup | [open](developers/08-optimize-your-setup.md) | [research, spec and plan, without building](analysts/08-optimize-your-setup.md) |

All exercises start on the `main` branch. The `results` branch shows the end result.

## Claude can’t read this folder

The solutions are in these files, so Claude must not find them while you do the exercises. `.claude/settings.json` has a deny rule for it:

```json
"deny": ["Read(/exercises/**)"]
```

Keep that rule when you edit `settings.json`. A command that doesn’t name a file, like `grep -r … .`, still reads this folder. Only the sandbox closes that gap.

Read the exercises on GitHub or in your editor.
