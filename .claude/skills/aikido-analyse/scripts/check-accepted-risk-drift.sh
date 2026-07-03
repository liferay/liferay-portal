#!/usr/bin/env bash
# Compare the project's .aikido-ignore config against a fresh
# Aikido issues export, and report which configured groups are actually
# ignored in Aikido vs. which are still open (never applied, or reopened).
#
# This is read-only: it never calls the Aikido API and never mutates
# anything. It exists so aikido-analyse can automatically flag drift every
# run, while the actual ignore call (bulk-ignore-groups.sh) stays a manual,
# reviewed step.
#
# Usage:
#   check-accepted-risk-drift.sh <export.json> [<config-file>]
#
# <export.json>  Output of the issues/export API call (see SKILL.md step 2).
# <config-file>  Defaults to .aikido-ignore at the repo root.
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/../../../.." && pwd)"

EXPORT_FILE="${1:-}"
CONFIG_FILE="${2:-$REPO_ROOT/.aikido-ignore}"

if [[ -z "$EXPORT_FILE" || ! -f "$EXPORT_FILE" ]]; then
  echo "ERROR: export JSON file required and must exist: ${EXPORT_FILE:-<none>}" >&2
  echo "Usage: $0 <export.json> [<config-file>]" >&2
  exit 1
fi

if [[ ! -f "$CONFIG_FILE" ]]; then
  echo "No $CONFIG_FILE found — nothing configured as accepted risk, nothing to check."
  exit 0
fi

STILL_OPEN=()
CONFIRMED_IGNORED=()
NOT_IN_EXPORT=()

while IFS= read -r line; do
  [[ -z "$line" || "$line" == \#* ]] && continue
  gid="$(awk '{print $1}' <<<"$line")"
  [[ -z "$gid" ]] && continue

  MATCH_COUNT=$(jq --argjson g "$gid" '[.[] | select(.group_id == $g)] | length' "$EXPORT_FILE")
  if [[ "$MATCH_COUNT" -eq 0 ]]; then
    NOT_IN_EXPORT+=("$gid")
    continue
  fi

  OPEN_COUNT=$(jq --argjson g "$gid" '[.[] | select(.group_id == $g and .status == "open")] | length' "$EXPORT_FILE")
  if [[ "$OPEN_COUNT" -gt 0 ]]; then
    STILL_OPEN+=("$gid ($OPEN_COUNT open)")
  else
    CONFIRMED_IGNORED+=("$gid")
  fi
done < "$CONFIG_FILE"

echo "==> Accepted-risk drift check against: $EXPORT_FILE"
echo "==> Config: $CONFIG_FILE"
echo

if [[ ${#CONFIRMED_IGNORED[@]} -gt 0 ]]; then
  echo "Confirmed ignored (no action needed): ${CONFIRMED_IGNORED[*]}"
fi

if [[ ${#NOT_IN_EXPORT[@]} -gt 0 ]]; then
  echo "Not present in this export (check group id is still correct): ${NOT_IN_EXPORT[*]}"
fi

if [[ ${#STILL_OPEN[@]} -gt 0 ]]; then
  echo
  echo "DRIFT: configured as accepted risk but still open in Aikido:"
  for entry in "${STILL_OPEN[@]}"; do
    echo "  - $entry"
  done
  echo
  echo "Run scripts/bulk-ignore-groups.sh to print the apply command(s) for these."
  exit 2
fi

echo
echo "No drift: every configured group is already ignored."
