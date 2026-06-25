#!/usr/bin/env bash
# Run Aikido local scanner locally via Docker.
#
# Usage:
#   bin/aikido-scan.sh [--base-commit <sha>] [--repo-name <name>] [--no-fail] [--full]
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
#   --no-fail            Report findings but always exit 0
#   --full               Run a full baseline scan (not PR-gated); required
#                        before the first PR-gated scan on a new repository
#
# Required env:
#   AIKIDO_SECRET_KEY  — API key from app.aikido.dev/settings/integrations
#                        (also accepted as AIKIDO_LOCAL_SCANNER_TOKEN)
#
# Examples:
#   AIKIDO_SECRET_KEY=xxx bin/aikido-scan.sh --full   # first time / new repo
#   AIKIDO_SECRET_KEY=xxx bin/aikido-scan.sh
#   AIKIDO_SECRET_KEY=xxx bin/aikido-scan.sh --base-commit d8caace6
#   AIKIDO_SECRET_KEY=xxx bin/aikido-scan.sh --repo-name my-repo
#   AIKIDO_SECRET_KEY=xxx bin/aikido-scan.sh --no-fail
set -euo pipefail

SCANNER_IMAGE="aikidosecurity/local-scanner:v1.0.139"
REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"

BASE_COMMIT=""
REPO_NAME=""
FAIL_ON="critical"
FULL_SCAN=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    --base-commit) BASE_COMMIT="$2"; shift 2 ;;
    --repo-name)   REPO_NAME="$2";   shift 2 ;;
    --no-fail)     FAIL_ON="";       shift   ;;
    --full)        FULL_SCAN=true;   shift   ;;
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
  echo "  AIKIDO_SECRET_KEY=<key> bin/aikido-scan.sh" >&2
  exit 1
fi

if ! command -v docker &>/dev/null; then
  echo "ERROR: docker not found on PATH." >&2
  exit 1
fi

BRANCH="$(git -C "$REPO_ROOT" rev-parse --abbrev-ref HEAD)"
HEAD_SHA="$(git -C "$REPO_ROOT" rev-parse HEAD)"

SCAN_ARGS=(
  --apikey "$API_KEY"
  --repositoryname "$REPO_NAME"
  --branchname "$BRANCH"
  --disable-artifact-scanning
  --exclude .m2
  --exclude target
  --exclude graphify-out
  --exclude .ai
)

if [[ "$FULL_SCAN" == true ]]; then
  echo "==> Aikido full baseline scan  repo=$REPO_NAME  branch=$BRANCH"
  echo "    head=$HEAD_SHA"
else
  if [[ -z "$BASE_COMMIT" ]]; then
    BASE_COMMIT="$(git -C "$REPO_ROOT" merge-base HEAD master 2>/dev/null \
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

[[ -n "$FAIL_ON" ]] && SCAN_ARGS+=(--fail-on "$FAIL_ON")

docker run --rm \
  --entrypoint "" \
  -e TRIVY_TIMEOUT="${TRIVY_TIMEOUT:-90m}" \
  -e TRIVY_OFFLINE_SCAN="${TRIVY_OFFLINE_SCAN:-true}" \
  -v "$REPO_ROOT:/scan:ro" \
  "$SCANNER_IMAGE" \
  aikido-local-scanner scan /scan "${SCAN_ARGS[@]}"
