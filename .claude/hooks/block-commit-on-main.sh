#!/usr/bin/env bash
# PreToolUse hook (Bash): refuse a git commit while the current branch is main.
set -u

input=$(cat)
printf '%s' "$input" | grep -q -E '\bgit\b[^"]*\bcommit\b' || exit 0

cd "${CLAUDE_PROJECT_DIR:-$(pwd)}" || exit 0
if [ "$(git branch --show-current 2>/dev/null)" = "main" ]; then
  echo "Never commit to main. Create a branch named <type>/<short-description> first." >&2
  exit 2
fi
exit 0
