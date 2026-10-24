# Shelfwise front-end — instructions for Claude

Loaded when working under `shelfwise-code/frontend`. The root `CLAUDE.md` still applies.

ESLint (`npm run lint`) enforces the mechanical rules below. Violations that existed before are frozen in
`eslint-suppressions.json`; never add to it to make lint pass.

## Front-end conventions

- Standalone components only, `ChangeDetectionStrategy.OnPush` everywhere, `input()`/`output()` and signals. No
  `@Input()`/`@Output()` decorators in new code.
- State lives in module-level signal stores in `src/app/store`. Components read signals and call store functions.
- API calls are plain functions in `src/app/core/api/*.api.ts` using `apiGet`, `apiPost`, `apiPut` from `core/http.ts`.
  Never inject `HttpClient` in a component.
- Shared types in `src/app/core/models.ts` mirror the back-end DTOs. Helpers in `src/app/helpers` are pure and have a
  spec next to them.
- Amounts use the `money` pipe; business rule errors use `shared/error-message.ts`.
- Styling, tokens, primitives and UI copy: see the `frontend-folio` skill. Never a hex value or a pixel spacing in a
  component.
