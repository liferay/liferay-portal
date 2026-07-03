---
name: aikido-scan
description: Use when running an Aikido local security scan for this project, including pre-scan generated-output cleanup and scanner credential checks.
---

# aikido-scan

Use this skill to run a clean Aikido local scan of the current repository, before drawing any
conclusions from Aikido findings. This skill is portable — it makes no assumptions about the
repo it runs in and can be copied as-is (`.claude/skills/aikido-scan/`) into any project.

The scan scripts live in `scripts/` alongside this file (not in the repo's top-level `bin/`) —
they are considered part of this skill, not standalone repo tooling. Always invoke them by the
paths below.

Repo name and repo root are auto-detected (git remote / script location) — nothing to configure
for a new project. Two optional files at the *target repo's root* let a project extend the
generic defaults without editing the skill itself:

- `.aikido-exclude` — extra `--exclude` glob patterns for the scanner, one per line
  (`#` comments allowed). Use for repo-specific noisy paths beyond the built-in generic excludes
  (`.gradle`, `.m2`, `.ai`, `graphify-out`, `target`, `**/classes/**`, `**/tmp/**`).
- `.aikido-clean` — extra glob patterns (relative to repo root) for the pre-scan cleanup
  step, beyond the built-in generic ones (`**/target`, `**/assembly/overlays`, `**/build/stage`).

Neither file is required; both are absent by default in a fresh project.

## Precondition

`AIKIDO_SECRET_KEY` (or `AIKIDO_LOCAL_SCANNER_TOKEN`) and, for the analyse skill's export step,
`AIKIDO_CLIENT_ID` / `AIKIDO_CLIENT_SECRET` must already be set in the operating system
environment (not only in an `.env` file). Do not try to `source` a dotfile — if a variable is
missing, stop and tell the user which one, rather than guessing or reading `.env`.

```bash
[[ -n "${AIKIDO_SECRET_KEY:-${AIKIDO_LOCAL_SCANNER_TOKEN:-}}" ]] || echo "MISSING: AIKIDO_SECRET_KEY"
```

## Workflow

### 1. Check workspace state

```bash
git status --short
```

Do not delete or revert user changes. Only generated/build output is a cleanup candidate.

### 2. Preview scanner-noise cleanup (dry run)

```bash
.claude/skills/aikido-scan/scripts/pre-aikido-scan.sh
```

Review the listed paths. This only ever targets generated `target/**` trees, assembly overlays,
and build `stage/` dirs (plus anything listed in an optional `.aikido-clean`) — never
tracked files or source POMs.

### 3. Apply cleanup

Only after the user has reviewed the preview output:

```bash
.claude/skills/aikido-scan/scripts/pre-aikido-scan.sh --apply
```

### 4. Run the scan

```bash
.claude/skills/aikido-scan/scripts/aikido-scan.sh --no-fail
```

Use `--no-fail` while triaging so the run reports findings without blocking. Drop `--no-fail`
only for a final gate check the user explicitly wants enforced. Add `--full` only for a first
baseline scan on a brand-new Aikido-registered repo/branch (see script header).

If Aikido still reports generated overlays or fixtures after this, do not widen excludes
blindly — hand off to `aikido-analyse` to confirm what's noise vs. real before adding a pattern
to `.aikido-exclude`.

## What Not To Do

- Don't add broad `--exclude` patterns (or edit the scripts directly) to make a scan pass before
  triaging what they hide — use the project's `.aikido-exclude` file instead.
- Don't run `--apply` cleanup without showing the preview first.
- Don't fall back to reading `.env` for credentials — they're expected in the OS environment.
- Don't hardcode repo-specific paths back into `scripts/aikido-scan.sh` or
  `scripts/pre-aikido-scan.sh` — that breaks portability. Use the optional project-root config
  files instead.
