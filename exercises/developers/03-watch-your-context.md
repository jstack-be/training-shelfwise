# Exercise 3 – Watch your context

**20 min** · Build a small page and watch what fills the context window.

1. **Number 1: the start.** New session, run `/context`. Note the total and what Memory files (CLAUDE.md) take.
2. **Number 2: build it.** Type: “Add a detail page for a title: click a search result and show title, authors, year, ISBN and availability per branch.” Then run `/context`.
3. **Compact.** Run `/compact`. Ask: “Which files did you change, and why?” Does it still know?
4. **Number 3: clear.** Run `/clear`, ask the same question, then `/context`. What did it forget? Why isn’t it back at zero?

**Done when** you have three numbers and know what filled the context. Then `git stash -u` to start clean.

<details>
<summary><b>Solution</b></summary>

**Building fills the context much faster than asking. CLAUDE.md is there from the start.**

- **Number 1:** before you ask anything, the system prompt, tools and memory files are loaded. CLAUDE.md on main alone is about 30,000 characters: 7–8k tokens.
- **Number 2:** every file it read (routes, API, components), every edit and every build stays in the context. Building costs far more than asking.
- **The feature:** only front-end work: a route, a page and a link from the search result. The API already exists: `GET /api/catalogue/titles/{id}`.
- **/compact vs /clear:** after `/compact` it still roughly knows which files it changed. After `/clear` it doesn’t: `git diff` is the only reliable answer.

**Take-away:** use `/compact` to continue the same task and `/clear` to start a new one. A slim CLAUDE.md lowers number 1.

Check: `frontend/src/app/core/api/catalogue.api.ts`. Slim CLAUDE.md on results: https://github.com/jstack-be/training-shelfwise/blob/results/CLAUDE.md
</details>
