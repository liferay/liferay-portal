#!/usr/bin/env bash
# Prepare the workspace for an Aikido scan by removing generated scanner noise.
#
# Default mode is preview-only. Pass --apply to delete generated/untracked output.
# This script never reverts tracked files and never removes source POMs.
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/../../../.." && pwd)"
APPLY=false

usage() {
  sed -n '/^# /p' "$0" | sed 's/^# //'
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --apply)
      APPLY=true
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
done

MODE="preview"
CLEAN_FLAGS="-fdXn"

if [[ "$APPLY" == true ]]; then
  MODE="apply"
  CLEAN_FLAGS="-fdX"
fi

echo "==> Pre-Aikido cleanup mode: $MODE"
echo "==> Repository: $REPO_ROOT"
echo
echo "==> Current workspace status"
git -C "$REPO_ROOT" status --short
echo

shopt -s globstar nullglob

# Generic build/assembly noise patterns, safe for any Maven/Gradle-style reactor.
# Project-specific extras (e.g. unusual output dirs) can be added, one glob per
# line, in an optional .aikido-clean file at the repo root.
GENERATED_PATHS=(
  "$REPO_ROOT"/**/target
  "$REPO_ROOT"/**/assembly/overlays
  "$REPO_ROOT"/**/build/stage
)

EXTRA_PATHS_FILE="$REPO_ROOT/.aikido-clean"
if [[ -f "$EXTRA_PATHS_FILE" ]]; then
  while IFS= read -r pattern; do
    [[ -z "$pattern" || "$pattern" == \#* ]] && continue
    for expanded in $REPO_ROOT/$pattern; do
      [[ -e "$expanded" ]] && GENERATED_PATHS+=("$expanded")
    done
  done < "$EXTRA_PATHS_FILE"
fi

if [[ ${#GENERATED_PATHS[@]} -eq 0 ]]; then
  echo "No generated scan-noise paths found."
else
  RELATIVE_PATHS=()
  for path in "${GENERATED_PATHS[@]}"; do
    RELATIVE_PATHS+=("${path#$REPO_ROOT/}")
  done

  echo "==> Generated scan-noise paths"
  git -C "$REPO_ROOT" clean $CLEAN_FLAGS -- "${RELATIVE_PATHS[@]}"
  echo
fi

if [[ "$APPLY" != true ]]; then
  echo "Preview only. Re-run with --apply to remove the listed generated files."
fi
