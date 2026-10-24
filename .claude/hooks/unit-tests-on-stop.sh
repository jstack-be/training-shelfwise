#!/usr/bin/env bash
# Stop hook: when Java files in the back-end changed, run the unit tests before Claude may finish.
# Blocks once (exit 2) on failure; a second stop attempt in the same turn is let through to avoid a loop.
set -u

input=$(cat)
root="${CLAUDE_PROJECT_DIR:-$(pwd)}"
backend="$root/shelfwise-code/backend"
cd "$root" || exit 0

[ -n "$(git status --porcelain -- 'shelfwise-code/backend/*.java')" ] || exit 0

if printf '%s' "$input" | grep -q '"stop_hook_active"[[:space:]]*:[[:space:]]*true'; then
  exit 0
fi

if [ ! -x "$backend/mvnw" ]; then
  echo "unit-tests-on-stop: shelfwise-code/backend/mvnw not found, unit tests not run" >&2
  exit 0
fi

# Skip the run when the Java sources are unchanged since the last green run.
state="$(git rev-parse --git-dir)/claude-unit-tests.pass"
hash=$( { git diff HEAD -- 'shelfwise-code/backend/*.java'
          git ls-files -o --exclude-standard -z -- 'shelfwise-code/backend/*.java' | xargs -0 cat 2>/dev/null
        } | shasum | cut -d' ' -f1 )
[ -f "$state" ] && [ "$(cat "$state")" = "$hash" ] && exit 0

log=$(mktemp)
trap 'rm -f "$log"' EXIT
if (cd "$backend" && ./mvnw -q test -Dtest='*Test') >"$log" 2>&1; then
  echo "$hash" >"$state"
  exit 0
fi

{
  echo "Checkstyle, architecture or unit tests fail (cd shelfwise-code/backend && ./mvnw test -Dtest='*Test')."
  echo "Fix them before you finish:"
  grep -E '\[ERROR\]|FAIL|expected|Exception' "$log" | grep -v -E 'To see the full stack trace|Re-run Maven|Help 1|Please refer|For more information|^\[ERROR\] *$' | head -40
} >&2
exit 2
