# Claude Code setup for Shelfwise

| File | Loaded | Purpose |
|---|---|---|
| `../CLAUDE.md` | every session | The rules that apply to every task: conventions, architecture, the business values a change can trip over, and a pointer to the right skill. |
| `skills/*/SKILL.md` | on demand | One procedure each. Claude reads the description and loads the body only when the work matches. |
| `agents/*.md` | on demand | Work that runs in its own context and reports back a summary. |
| `settings.json` | every session | Permissions: tests, builds and read-only git are pre-approved; `.env` files and `git push` are denied. |

`settings.local.json` is personal and not committed.

## Why the split

Everything in `CLAUDE.md` costs context in **every** session, so it only holds what is always relevant. A long
procedure that is needed now and then — explaining a fine, adding a material type, driving the business date — is a
skill: the one-line description stays in context, the body is read when it is actually needed.

An agent is for work whose *intermediate* output is not worth keeping: reading half the analysis pack to answer one
question (`analysis-researcher`), or implementing a scoped change (`senior-developer`). You see the conclusion, not
the files it read.

## Rules of thumb when you extend this

- A skill description says **when** to use it, in the third person, with the words someone would actually type.
- One skill, one job. Two skills that explain the same rule will drift apart — that is how the old `fines` skill
  ended up quoting LibTrack 4 rates.
- Never put a business value in two places. `summary.md` is the source of truth; `CLAUDE.md` repeats only the
  handful of values that are needed to not make a mistake, and everything else is a reference to a section.
- An agent that commits, pushes or "improves things it notices along the way" will do exactly that, unsupervised.
  Keep agents scoped and read-only where you can.
