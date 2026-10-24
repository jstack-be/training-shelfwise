---
name: commit-and-pr
description: Shelfwise commit, branch and pull request conventions — Conventional Commits with IDs, one logical change per commit, branch naming and the pull request description template. Use before committing, creating a branch or opening a pull request.
---

# Commits, branches and pull requests

## Branch

Never commit to `main` (a hook blocks it). Work on a branch named `<type>/<short-description>`.

## Commits

- Conventional Commits, naming the IDs (`F-…`, `BR-…`, `AC-…`, `CR-…`):
  `feat(lending): three renewals for books (CR-01, BR-LOA-03, AC-LEN-06)`.
  Types: `feat`, `fix`, `test`, `refactor`, `docs`, `chore`.
- One logical change per commit; never mix refactoring and behaviour changes.

## Pull request description

Name the same IDs as in the commit messages.

```markdown
## What
One paragraph: what changes for members or staff.

## Why
Change request or feature ID, with a link to the section in summary.md.

## Rules and examples
| ID | Change | Test |
|---|---|---|

## Assumptions
Choices not covered by the specification (also added to ASSUMPTIONS.md).

## How to verify
Seed scenario, role and business date to use, and what you should see.

## Checklist
- [ ] Unit and integration tests pass
- [ ] Front-end tests pass
- [ ] Spec compliance review done
- [ ] ASSUMPTIONS.md and README traceability updated
```
