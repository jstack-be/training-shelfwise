#!/usr/bin/env bash
# Stop hook: when front-end TypeScript changed, run ESLint before Claude may finish.
# Blocks once (exit 2) on failure; a second stop attempt in the same turn is let through to avoid a loop.
set -u

input=$(cat)
root="${CLAUDE_PROJECT_DIR:-$(pwd)}"
frontend="$root/shelfwise-code/frontend"
cd "$root" || exit 0

[ -n "$(git status --porcelain -- 'shelfwise-code/frontend/src/*.ts' 'shelfwise-code/frontend/eslint*')" ] || exit 0

if printf '%s' "$input" | grep -q '"stop_hook_active"[[:space:]]*:[[:space:]]*true'; then
  exit 0
fi

if [ ! -x "$frontend/node_modules/.bin/eslint" ]; then
  echo "frontend-lint-on-stop: run npm ci in shelfwise-code/frontend; ESLint not run" >&2
  exit 0
fi

if output=$(cd "$frontend" && ./node_modules/.bin/eslint src 2>&1); then
  exit 0
fi

{
  echo "ESLint fails (cd shelfwise-code/frontend && npm run lint). Fix it before you finish:"
  printf '%s\n' "$output" | head -40
} >&2
exit 2
