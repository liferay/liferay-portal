---
name: aikido-analyse
description: Use when exporting, grouping, and triaging Aikido findings for this project after an Aikido scan, including accepted-risk drift checks.
---

# aikido-analyse

Use this skill to fetch and triage Aikido findings for the current repository after an
`aikido-scan` run. This skill is portable — it derives the repo name from `git remote`, looks up
the Aikido repository id live every run (matching branch too, since one org can register several
repos under the same name), and keeps the generic safe-fix discipline below. Project-specific
accepted-risk rules (which major upgrades are deferred, which version lines are "safe") are NOT
embedded here — if the project has its own security-review doc (e.g. `SECURITY_REVIEW*.md` at
repo root), read it first and defer to its accepted-risk list and version-line guidance over the
generic defaults below.

`scripts/bulk-ignore-groups.sh` prints (never runs) the Aikido API call to bulk-ignore an issue
group — the mechanism for clearing an entire repeated-finding CVE (see step 7) in one reviewed
action instead of one-ignore-per-file. The list of accepted-risk group ids itself is a
project-root config file, `.aikido-ignore` (absent by default, like
`aikido-scan`'s `.aikido-exclude`/`.aikido-clean`) — so the decisions are
version-controlled and reviewable in a diff, not re-typed on the command line each time.

## Precondition

`AIKIDO_CLIENT_ID` and `AIKIDO_CLIENT_SECRET` must already be set in the operating system
environment. These are the public-API OAuth2 client-credentials, separate from
`AIKIDO_SECRET_KEY` used by `aikido-scan`. Do not source `.env` — if either variable is missing,
stop and tell the user which one.

```bash
[[ -n "${AIKIDO_CLIENT_ID:-}" && -n "${AIKIDO_CLIENT_SECRET:-}" ]] || echo "MISSING: AIKIDO_CLIENT_ID / AIKIDO_CLIENT_SECRET"
```

## Workflow

### 1. Look up the repository id

Derive the repo name the same way `aikido-scan` does (git remote, last path component, `.git`
suffix stripped; falls back to the directory basename), then look up its Aikido id — don't
hardcode it, ids differ per project/checkout.

**Match by name AND current branch, not name alone.** An Aikido org can have multiple
repositories registered under the same name tracking different branches (e.g. one for `main`,
one per long-lived feature branch created via `--force-create-repository-for-branch`) — matching
by name only can silently pick the wrong one:

```bash
REPO_NAME="$(git remote get-url origin 2>/dev/null | sed 's|.*[/:]||; s|\.git$||')"
REPO_NAME="${REPO_NAME:-$(basename "$(git rev-parse --show-toplevel)")}"
BRANCH="$(git rev-parse --abbrev-ref HEAD)"

TOKEN=$(curl -sS -X POST https://app.aikido.dev/api/oauth/token \
  -u "$AIKIDO_CLIENT_ID:$AIKIDO_CLIENT_SECRET" -d grant_type=client_credentials | jq -r .access_token)

REPO_ID=$(curl -sS -H "Authorization: Bearer $TOKEN" \
  https://app.aikido.dev/api/public/v1/repositories/code | \
  jq -r --arg n "$REPO_NAME" --arg b "$BRANCH" '[.[] | select(.name==$n and .branch==$b)] | .[0].id')
```

If `REPO_ID` comes back empty, list all repos matching the name and ask the user which one is
correct rather than guessing:

```bash
curl -sS -H "Authorization: Bearer $TOKEN" \
  https://app.aikido.dev/api/public/v1/repositories/code | jq --arg n "$REPO_NAME" '.[] | select(.name==$n)'
```

Token expires in 3600s. Re-request per session; never cache it to disk.

### 2. Export issues

```bash
curl -sS -H "Authorization: Bearer $TOKEN" \
  "https://app.aikido.dev/api/public/v1/issues/export?repository_id=$REPO_ID" \
  > "/tmp/aikido-${REPO_NAME}-$(git rev-parse --abbrev-ref HEAD | tr / -).json"
```

Remember: this export gives repository-wide issue records, not a feature-branch UI's
introduced/solved diff counters. Treat counts as latest API-visible state, not a scan-page
replacement.

### 3. Check accepted-risk drift automatically

Always run this next, unconditionally — it's read-only (never calls the Aikido API, never
mutates anything), so there's no reason to skip it:

```bash
.claude/skills/aikido-analyse/scripts/check-accepted-risk-drift.sh \
  "/tmp/aikido-${REPO_NAME}-$(git rev-parse --abbrev-ref HEAD | tr / -).json"
```

It compares every group id in `.aikido-ignore` against this export and reports:
- confirmed ignored (no action needed)
- still open (exit code 2) — configured as accepted risk but the ignore was never applied, or an
  ignore expired/got reopened

If it reports drift, tell the user and point them at `bulk-ignore-groups.sh` (step 7) — do not
call the Aikido API yourself to "fix" it; applying the ignore stays a manual, reviewed step every
time, not something this skill automates away.

### 4. Summarize by severity, file, and group

```bash
jq -r 'map(select(.status=="open")) | group_by(.severity)
  | map({severity: .[0].severity, count: length}) | .[]' "/tmp/aikido-${REPO_NAME}-"*.json

jq -r 'map(select(.status=="open")) | group_by(.affected_file)
  | map({file: .[0].affected_file, count: length}) | sort_by(-.count) | .[:50][]
  | "\(.count)\t\(.file)"' "/tmp/aikido-${REPO_NAME}-"*.json
```

**A raw open-issue count is misleading in a multi-module reactor.** The same dependency version
pinned in a parent POM gets reported once per module `pom.xml` that resolves it, so one CVE on
one shared dependency can show up as hundreds of "issues." Aikido already collapses this: every
export record carries a `group_id`, and all per-file instances of the same CVE/rule+package share
one. Triage by group, not by raw issue count — the group is the real unit of decision:

```bash
jq -r 'map(select(.status=="open"))
  | group_by(.group_id)
  | map({group_id: .[0].group_id, package: .[0].affected_package, cve: (.[0].cve_id // .[0].rule),
         version: .[0].installed_version, severity: .[0].severity, count: length})
  | sort_by(-.count)' "/tmp/aikido-${REPO_NAME}-"*.json
```

A package with many open records but few distinct `group_id`s (e.g. one dependency pinned across
50 modules) is a single triage decision, not 50. A package spread across many distinct `group_id`s
at the same version is usually many independent CVEs against that one version — each still gets
patched together by bumping the version once, but reviewed as separate CVEs for severity.

### 5. Triage every open group into one bucket

| Bucket | Signal | Action |
|---|---|---|
| Generated output | path under `target/**`, `**/assembly/overlays/**`, `**/build/stage/**`, or another pattern the project's `.aikido-clean`/`.aikido-exclude` already covers | Fix `aikido-scan` excludes, rescan — don't remediate the dependency |
| Test fixture / sample binary | path under `**/test/resources/**`, sample/autodeployed archives | Ignore in Aikido: justification "test fixture / not shipped to production" + review date |
| Accepted platform risk | dependency version is pinned by a surrounding platform the project can't move independently (check the project's security-review doc for the current list) | Bulk-ignore the whole group (see step 7) — don't remediate |
| Real source-controlled dependency, patchable | finding points at a manifest (`pom.xml`, `build.gradle`, `package.json`, ...) version actually resolved in the build, and isn't platform-pinned | Keep open; safe-fix candidate |

Never chase a finding inside generated output before confirming the source dependency that
produced it still exists in a real manifest. Never bulk-ignore a group just because it's large —
large-and-platform-pinned gets ignored, large-and-patchable gets fixed once (the version bump
clears every module's instance in the group on the next scan).

### 6. Apply safe-fix discipline for real dependency findings

Generic default (override with the project's own security-review doc if one exists):

- Same major/minor line only. Don't propose a major-version jump as the first move for any
  finding — check whether the project has explicit deferred/accepted-risk platform decisions
  first (e.g. a framework pinned by a surrounding platform that can't move independently).
- Patch-line bump only (e.g. `3.2` → `3.2.2`, not `3.2` → `4.x`).
- One dependency family per commit/PR — don't collapse multiple upgrades into one change.
- After each patch, run the smallest affected build/test scope, then re-scan with `aikido-scan`.

### 7. Record deferrals — bulk-ignore by group, not by individual issue

Anything the project has explicitly accepted as platform risk (check its security-review doc for
the current list) gets an Aikido ignore with justification, owner, and review date — not silently
left open. Ignore the **group**, not each individual issue in it — clicking through (or scripting)
one ignore per file is the wrong unit of work when one `group_id` already covers all of them.

`scripts/bulk-ignore-groups.sh` in this skill prints the exact command(s) for a human to review
and run — it never calls the Aikido API itself, on purpose: this mutates a shared dashboard other
people look at, so applying it is always a deliberate, reviewed, manual step, never something this
skill does on its own.

**Keep the accepted-risk group list in `.aikido-ignore` at the repo root**, not on
the command line — one group per line, `<group_id> <reason>` (reason may contain spaces, `#`
comments and blank lines ignored). This makes each accepted-risk decision a reviewable, diffable,
version-controlled line, and means re-running this skill later (e.g. after a rescan) doesn't
require re-deriving or re-typing the same group ids and justifications:

```bash
.claude/skills/aikido-analyse/scripts/bulk-ignore-groups.sh
```

reads that file by default. To ignore one-off groups not worth adding to the file, pass them
directly instead:

```bash
.claude/skills/aikido-analyse/scripts/bulk-ignore-groups.sh \
  --reason "accepted platform risk - <specific reason, e.g. pinned by <platform> <version>, see <doc>" \
  <group_id> [<group_id> ...]
```

Either way, review the printed group ids, package/version, and reason against step 4's group
summary and the project's accepted-risk list before running the printed curl command(s). When
adding a new group to `.aikido-ignore`, cross-check it against step 4's output
first — never add a group id there without having actually seen its package/version/CVE.

## What Not To Do

- Don't recommend a major-version upgrade to "fully fix" a CVE without checking whether the
  project has already deferred it as accepted platform risk.
- Don't treat raw open-issue counts as the success metric — bucket by `group_id`, then judge.
- Don't ignore a finding without a justification, owner, and review date.
- Don't ignore issues one at a time when they share a `group_id` — use
  `scripts/bulk-ignore-groups.sh` and the group-level Aikido API instead.
- Don't add `--apply`-style auto-execution to `bulk-ignore-groups.sh` — it stays print-only so a
  human always reviews the exact groups and reason before anything is mutated on the dashboard.
- Don't skip confirming the dependency is still source-controlled before proposing a patch.
- Don't hardcode a repository id or name into this file — always derive/look it up per run
  (matching branch, not just name — see step 1) so the skill stays portable across projects.
