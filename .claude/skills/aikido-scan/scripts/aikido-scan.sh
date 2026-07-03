#!/usr/bin/env bash
# Run Aikido local scanner locally via Docker.
#
# Usage:
#   .claude/skills/aikido-scan/scripts/aikido-scan.sh [--base-commit <sha>] [--repo-name <name>] [--no-fail] [--full] [--force-create-repository-for-branch]
#
# Runs a PR-gating scan of the current branch against the main baseline
# already registered in Aikido. Base commit defaults to the merge-base of
# HEAD and master/main. Repo name is derived from the git remote URL
# (last path component, .git suffix stripped); falls back to the directory
# basename. Use --repo-name to override when the remote name differs from
# the Aikido-registered name.
#
# Aikido requires at least one full (non-gated) scan before PR-gated scans
# will work. Use --full to run a baseline scan first.
#
# Options:
#   --base-commit <sha>  Override the auto-detected merge-base commit
#   --repo-name <name>   Override the auto-detected repository name
#   --no-fail            Report findings but always exit 0 (still runs with
#                        --fail-on internally; PR gating mode requires it)
#   --full               Run a full baseline scan (not PR-gated); required
#                        before the first PR-gated scan on a new repository
#   --force-create-repository-for-branch
#                        Create a separate Aikido repo for a long-lived branch
#
# Required env:
#   AIKIDO_SECRET_KEY  — API key from app.aikido.dev/settings/integrations
#                        (also accepted as AIKIDO_LOCAL_SCANNER_TOKEN)
#
# This token is separate from the Aikido public REST API, which uses OAuth2
# client-credentials (AIKIDO_CLIENT_ID / AIKIDO_CLIENT_SECRET from
# app.aikido.dev/settings/integrations/public-api) instead:
#   TOKEN=$(curl -s -X POST https://app.aikido.dev/api/oauth/token \
#     -u "$AIKIDO_CLIENT_ID:$AIKIDO_CLIENT_SECRET" -d grant_type=client_credentials \
#     | jq -r .access_token)
#   curl -H "Authorization: Bearer $TOKEN" \
#     https://app.aikido.dev/api/public/v1/repositories/code
# Token expires in 3600s; re-request per session, don't cache to disk.
#
# Examples:
#   AIKIDO_SECRET_KEY=xxx .claude/skills/aikido-scan/scripts/aikido-scan.sh --full   # first time / new repo
#   AIKIDO_SECRET_KEY=xxx .claude/skills/aikido-scan/scripts/aikido-scan.sh
#   AIKIDO_SECRET_KEY=xxx .claude/skills/aikido-scan/scripts/aikido-scan.sh --base-commit d8caace6
#   AIKIDO_SECRET_KEY=xxx .claude/skills/aikido-scan/scripts/aikido-scan.sh --repo-name my-repo
#   AIKIDO_SECRET_KEY=xxx .claude/skills/aikido-scan/scripts/aikido-scan.sh --no-fail
#   AIKIDO_SECRET_KEY=xxx .claude/skills/aikido-scan/scripts/aikido-scan.sh --full --force-create-repository-for-branch
#   AIKIDO_SECRET_KEY=xxx .claude/skills/aikido-scan/scripts/aikido-scan.sh --full --no-fail --force-create-repository-for-branch
set -euo pipefail

SCANNER_IMAGE="aikidosecurity/local-scanner:v1.0.139"
REPO_ROOT="$(cd "$(dirname "$0")/../../../.." && pwd)"

BASE_COMMIT=""
REPO_NAME=""
FAIL_ON="critical"
NO_FAIL=false
FULL_SCAN=false
FORCE_CREATE_REPOSITORY_FOR_BRANCH=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    --base-commit) BASE_COMMIT="$2"; shift 2 ;;
    --repo-name)   REPO_NAME="$2";   shift 2 ;;
    --no-fail)     NO_FAIL=true;     shift   ;;
    --full)        FULL_SCAN=true;   shift   ;;
    --force-create-repository-for-branch)
                   FORCE_CREATE_REPOSITORY_FOR_BRANCH=true; shift ;;
    -h|--help)
      sed -n '/^# /p' "$0" | sed 's/^# //'
      exit 0 ;;
    *) echo "Unknown option: $1" >&2; exit 1 ;;
  esac
done

if [[ -z "$REPO_NAME" ]]; then
  REPO_NAME="$(git -C "$REPO_ROOT" remote get-url origin 2>/dev/null \
    | sed 's|.*[/:]||; s|\.git$||')" || true
  REPO_NAME="${REPO_NAME:-$(basename "$REPO_ROOT")}"
fi

API_KEY="${AIKIDO_SECRET_KEY:-${AIKIDO_LOCAL_SCANNER_TOKEN:-}}"
if [[ -z "$API_KEY" ]]; then
  echo "ERROR: AIKIDO_SECRET_KEY is not set." >&2
  echo "  Export it or prefix the command:" >&2
  echo "  AIKIDO_SECRET_KEY=<key> .claude/skills/aikido-scan/scripts/aikido-scan.sh" >&2
  exit 1
fi

if ! command -v docker &>/dev/null; then
  echo "ERROR: docker not found on PATH." >&2
  exit 1
fi

BRANCH="$(git -C "$REPO_ROOT" rev-parse --abbrev-ref HEAD)"
HEAD_SHA="$(git -C "$REPO_ROOT" rev-parse HEAD)"

# Generic build-tool cache/output excludes, safe for any repo.
SCAN_ARGS=(
  --apikey "$API_KEY"
  --repositoryname "$REPO_NAME"
  --branchname "$BRANCH"
  --disable-artifact-scanning
  --exclude .gradle
  --exclude .m2
  --exclude .ai
  --exclude graphify-out
  --exclude target
  --exclude '**/classes/**'
  --exclude '**/tmp/**'
)

# Project-specific extra excludes, one pattern per line, in an optional
# .aikido-exclude file at the repo root. Keeps this script portable
# across repos without editing it.
EXCLUDES_FILE="$REPO_ROOT/.aikido-exclude"
if [[ -f "$EXCLUDES_FILE" ]]; then
  while IFS= read -r pattern; do
    [[ -z "$pattern" || "$pattern" == \#* ]] && continue
    SCAN_ARGS+=(--exclude "$pattern")
  done < "$EXCLUDES_FILE"
fi

if [[ "$FULL_SCAN" == true ]]; then
  echo "==> Aikido full baseline scan  repo=$REPO_NAME  branch=$BRANCH"
  echo "    head=$HEAD_SHA"
else
  if [[ -z "$BASE_COMMIT" ]]; then
    BASE_COMMIT="$(git -C "$REPO_ROOT" merge-base HEAD develop 2>/dev/null \
      || git -C "$REPO_ROOT" merge-base HEAD master 2>/dev/null \
      || git -C "$REPO_ROOT" merge-base HEAD main 2>/dev/null \
      || git -C "$REPO_ROOT" rev-parse HEAD~1)"
    echo "==> base commit auto-detected: $BASE_COMMIT"
  fi
  SCAN_ARGS+=(
    --gating-mode pr
    --base-commit-id "$BASE_COMMIT"
    --head-commit-id "$HEAD_SHA"
  )
  echo "==> Aikido PR scan  repo=$REPO_NAME  branch=$BRANCH"
  echo "    base=$BASE_COMMIT"
  echo "    head=$HEAD_SHA"
fi

SCAN_ARGS+=(--fail-on "$FAIL_ON")
[[ "$FORCE_CREATE_REPOSITORY_FOR_BRANCH" == true ]] && \
  SCAN_ARGS+=(--force-create-repository-for-branch)

# --fail-on is always passed above: the scanner requires it in PR gating mode
# regardless of --no-fail. --no-fail instead controls whether this script's
# own exit code reflects the gate result, so it can report findings without
# blocking (e.g. during triage) while still surfacing all scanner output.
set +e
docker run --rm \
  --entrypoint "" \
  -e TRIVY_TIMEOUT="${TRIVY_TIMEOUT:-90m}" \
  -e TRIVY_OFFLINE_SCAN="${TRIVY_OFFLINE_SCAN:-true}" \
  -v "$REPO_ROOT:/scan:ro" \
  "$SCANNER_IMAGE" \
  aikido-local-scanner scan /scan "${SCAN_ARGS[@]}"
SCAN_STATUS=$?
set -e

if [[ "$NO_FAIL" == true ]]; then
  exit 0
fi
exit "$SCAN_STATUS"
