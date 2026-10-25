# Exercise 3 – Watch your context (analysts)

**20 min** · Write a document and watch what fills the context window.

**Different from the developers:** you write a guide instead of building a page. The `/context`, `/compact` and `/clear` steps are the same.

1. **Number 1: the start.** New session, run `/context`. Note the total and what Memory files (CLAUDE.md) take.
2. **Number 2: write it.** Type: “Write desk-guide.md: a one-page quick reference for librarians on returns and fines, based on summary.md §8.5–8.7 and §10. Check every statement against FA-04 and FA-06.” Then run `/context`.
3. **Compact.** Run `/compact`. Ask: “Which documents did you use, and which statements did you correct?” Does it still know?
4. **Number 3: clear.** Run `/clear`, ask the same question, then `/context`. What did it forget? Why isn’t it back at zero?

**Done when** you have three numbers and know what filled the context. Then delete `desk-guide.md`.

<details>
<summary><b>Solution</b></summary>

- **Number 1:** before you ask anything, the system prompt, tools and CLAUDE.md are loaded. CLAUDE.md on main alone is 7–8k tokens.
- **Number 2:** every document it read stays in the context. Reading and checking several documents fills it fast.
- **/compact vs /clear:** after `/compact` it still roughly knows which documents it used. After `/clear` it doesn’t: only the file it wrote remains.

**Take-away:** use `/compact` to continue the same task and `/clear` to start a new one. Put results in a file, not only in the chat.
</details>
