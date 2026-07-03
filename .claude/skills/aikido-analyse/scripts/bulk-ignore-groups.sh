#!/usr/bin/env bash
# Print — or, with --apply, apply — the Aikido bulk-ignore for a set of issue
# groups. An issue group already collapses every per-file instance of the same
# CVE/rule+package (e.g. one pinned Spring version flagged in 100+ pom.xml
# files) into one id, so one call per group clears the whole set instead of one
# call per finding.
#
# Default is print-only: it never calls the Aikido API, it prints the curl
# command(s) for a human to review and run. Bulk-ignoring mutates a shared
# dashboard other people look at, so a human reviews the exact group ids and
# reason below, then copies the printed curl command(s) to run manually.
#
# Pass --apply to perform the PUT calls directly instead of printing them
# (idempotent: re-ignoring an already-ignored group is a no-op). Use this once
# the group ids and reasons below have been reviewed.
#
# Usage:
#   bulk-ignore-groups.sh [--apply] --reason "<text>" <group_id> [<group_id> ...]
#   bulk-ignore-groups.sh [--apply] --config <path>
#   bulk-ignore-groups.sh [--apply]            # reads .aikido-ignore
#                                               # at the repo root, if present
#
# Config file format (default: .aikido-ignore at repo root),
# one accepted-risk group per line, blank lines and #-comments ignored:
#   <group_id><whitespace><reason text, may contain spaces>
# Example:
#   20662660  accepted platform risk - pinned by Liferay ROOT Spring 4.1.9, see SECURITY_REVIEW-3.md
#
# Keeping the accepted-risk group list in a project-root config file (rather
# than only passed on the command line) means it's version-controlled,
# reviewable in a diff, and reusable run to run — not re-typed or re-derived
# each time this skill is used.
#
# Required env: AIKIDO_CLIENT_ID, AIKIDO_CLIENT_SECRET (OAuth client-credentials;
# used to mint the bearer token for the printed — or, with --apply, executed —
# curl call).
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/../../../.." && pwd)"
DEFAULT_CONFIG="$REPO_ROOT/.aikido-ignore"

REASON=""
CONFIG_FILE=""
APPLY=false
GROUP_IDS=()

usage() {
  sed -n '/^# /p' "$0" | sed 's/^# //'
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --reason) REASON="$2"; shift 2 ;;
    --config) CONFIG_FILE="$2"; shift 2 ;;
    --apply)  APPLY=true; shift ;;
    -h|--help) usage; exit 0 ;;
    *) GROUP_IDS+=("$1"); shift ;;
  esac
done

# Pairs of (group_id, reason) to emit. When --reason is given with explicit
# group ids on the CLI, every id shares that one reason (original behavior).
# Otherwise fall back to a config file, where each line carries its own
# reason.
PAIRS=()

if [[ ${#GROUP_IDS[@]} -gt 0 ]]; then
  if [[ -z "$REASON" ]]; then
    echo "ERROR: --reason is required when passing group ids directly." >&2
    exit 1
  fi
  for gid in "${GROUP_IDS[@]}"; do
    PAIRS+=("$gid"$'\t'"$REASON")
  done
else
  [[ -z "$CONFIG_FILE" ]] && CONFIG_FILE="$DEFAULT_CONFIG"
  if [[ ! -f "$CONFIG_FILE" ]]; then
    echo "ERROR: no group ids given and config file not found: $CONFIG_FILE" >&2
    usage >&2
    exit 1
  fi
  while IFS= read -r line; do
    [[ -z "$line" || "$line" == \#* ]] && continue
    gid="$(awk '{print $1}' <<<"$line")"
    reason="$(awk '{$1=""; sub(/^ /,""); print}' <<<"$line")"
    if [[ -z "$gid" || -z "$reason" ]]; then
      echo "ERROR: malformed line in $CONFIG_FILE: $line" >&2
      exit 1
    fi
    PAIRS+=("$gid"$'\t'"$reason")
  done < "$CONFIG_FILE"
  echo "==> Using config: $CONFIG_FILE"
fi

if [[ ${#PAIRS[@]} -eq 0 ]]; then
  echo "ERROR: no group ids to ignore (config file empty?)." >&2
  exit 1
fi

if [[ -z "${AIKIDO_CLIENT_ID:-}" || -z "${AIKIDO_CLIENT_SECRET:-}" ]]; then
  echo "ERROR: AIKIDO_CLIENT_ID / AIKIDO_CLIENT_SECRET not set." >&2
  exit 1
fi

echo "==> Group ids and reasons:"
for pair in "${PAIRS[@]}"; do
  echo "    ${pair}"
done
echo

if [[ "$APPLY" == true ]]; then
  echo "==> --apply: pushing ignore status to Aikido (one PUT per group)..."
  TOKEN=$(curl -sS -X POST https://app.aikido.dev/api/oauth/token \
    -u "$AIKIDO_CLIENT_ID:$AIKIDO_CLIENT_SECRET" -d grant_type=client_credentials | jq -r .access_token)
  if [[ -z "$TOKEN" || "$TOKEN" == "null" ]]; then
    echo "ERROR: could not obtain an Aikido access token." >&2
    exit 2
  fi
  rc=0
  for pair in "${PAIRS[@]}"; do
    gid="${pair%%$'\t'*}"
    reason="${pair#*$'\t'}"
    code=$(curl -sS -o /dev/null -w "%{http_code}" -X PUT \
      -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
      -d "$(jq -n --arg r "$reason" '{reason: $r}')" \
      "https://app.aikido.dev/api/public/v1/issues/groups/$gid/ignore")
    echo "    group $gid -> HTTP $code"
    [[ "$code" == 2* ]] || rc=1
  done
  exit "$rc"
fi

echo "==> Review the group ids and reasons above, then run the following"
echo "    manually (one block per group) to actually apply the ignore"
echo "    (or re-run this command with --apply):"
echo

for pair in "${PAIRS[@]}"; do
  gid="${pair%%$'\t'*}"
  reason="${pair#*$'\t'}"
  cat <<EOF
TOKEN=\$(curl -sS -X POST https://app.aikido.dev/api/oauth/token \\
  -u "\$AIKIDO_CLIENT_ID:\$AIKIDO_CLIENT_SECRET" -d grant_type=client_credentials | jq -r .access_token)
curl -sS -X PUT -H "Authorization: Bearer \$TOKEN" -H "Content-Type: application/json" \\
  -d '$(jq -n --arg r "$reason" '{reason: $r}')' \\
  "https://app.aikido.dev/api/public/v1/issues/groups/$gid/ignore"

EOF
done
