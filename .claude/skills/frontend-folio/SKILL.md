---
name: frontend-folio
description: Folio design system and UI copy rules for the Angular front-end — tokens, primitives, status colours, layout and the words on screen. Use when building or changing any screen, component, dialog, table or empty state in shelfwise-code/frontend, or when choosing colours, spacing, labels or error text.
---

# Folio — the house style of Shelfwise

Folio is the house style. Every screen uses it; nothing is styled ad hoc.

## Tokens (`src/styles/_tokens.scss`)

Colours, spacing, radius, shadows and fonts come from the tokens only. **Never a hex value or a pixel spacing in a
component.**

| Group | Tokens |
|---|---|
| Surfaces | `--sw-color-paper`, `--sw-color-surface`, `--sw-color-sunken` |
| Text | `--sw-color-ink`, `--sw-color-muted` |
| Lines | `--sw-color-line`, `--sw-color-line-strong` |
| Accent | `--sw-color-accent`, `--sw-color-accent-strong`, `--sw-color-accent-soft` |
| Status | `--sw-color-success`, `--sw-color-warning`, `--sw-color-danger`, `--sw-color-info`, `--sw-color-neutral`, each with a `-soft` variant for backgrounds |
| Spacing | `--sw-space-1` (4px) to `--sw-space-7` (48px) |
| Radius | `--sw-radius-sm`, `--sw-radius-md`, `--sw-radius-lg`, `--sw-radius-pill` |
| Shadows | `--sw-shadow-card`, `--sw-shadow-float` |
| Fonts | `--sw-font-body` (Inter), `--sw-font-display` (Fraunces, headings only), `--sw-font-mono` (card numbers, barcodes) |

## Primitives (`src/app/ui`)

Use these before building something new: `page-header`, `section-card`, `stat-tile`, `status-pill`, `empty-state`,
`icon`. Domain components (loan table, member card, money pipe, business date chip, …) live in `src/app/shared`.

- Status colours always go through `status-pill` with a `Tone` from `helpers/tones.ts`: `success`, `warning`,
  `danger`, `info`, `neutral`. A blocked member is `danger`, an accrued fine is `warning`, an available copy is
  `success`.
- One primary action per screen; secondary actions are Material stroked buttons.
- Every list has an `empty-state` that says what the user can do next.
- The business date chip (`shared/business-date-chip.ts`) stays visible in the header (F-TRN-01).
- Amounts use the `money` pipe, never a raw number format. Business rule errors use `shared/error-message.ts`, which
  maps `code` and `ruleId` from the `ApiError`.

## Component rules

Standalone components, `ChangeDetectionStrategy.OnPush`, `input()`/`output()` and signals. Shared state lives in a
store in `src/app/store`; a component never holds it. API calls go through `core/api/*.api.ts`.

## UI copy

- English, sentence case, no exclamation marks.
- Say "member", never "patron" or "customer" (BUS-02).
- Enum labels come from `helpers/labels.ts`. Never show a raw value such as `ON_LOAN`.
- Dates as `16 Mar 2026`, amounts as `€1.50`.
- An error says what to do next: "Pay the open balance below €5.00 to borrow again", not "Member blocked".

## Checking a screen

`npx ng test --watch=false` and `npx ng build`, then look at the screen in the running stack (see the
`training-mode` skill to put the data in the state you need).
