---

allowed-tools: [Agent, Bash, Edit, Glob, Grep, Read, Skill, Write]
description: Check that a PR is ready to be sent for review.
name: pr-check

---

# PR Check

Run premerge checks against the current branch. The skill iterates through the validations under `validations`, runs each one whose trigger matches the diff, and reports PASS or FAIL. Integration tests, Playwright tests, and Poshi tests are out of scope. Use the `test-plan` skill when their coverage is needed.

## Preconditions

- **On a feature branch.** When `HEAD` is `master` or detached, exit with a one-line message.

- **Working tree clean.** `git status --porcelain` must return empty. When dirty, abort and ask the developer to commit first.

- **Rebased on the latest `master`.** Resolve the master remote. Prefer `upstream`, otherwise the remote whose URL points at `liferay/liferay-portal` (check `git remote --verbose`). When none resolves, compare `git merge-base HEAD master` to `git rev-parse master`. Abort and tell the developer to rebase when the two differ, and warn that the branch was not checked against a remote. Otherwise run these steps.

	1. `git fetch <remote> master`.

	1. Fast forward local `master` to the fetched tip. When `master` is checked out in another worktree, fast forward it there with `git -C <worktree> merge --ff-only <remote>/master`. Otherwise update it in place with `git fetch <remote> master:master`, which also creates `master` when it does not exist. Both are fast forward only. When the command fails (because `master` has diverged or its worktree is not clean), warn the developer and stop the run.

	1. `git rebase <remote>/master`. On a clean rebase, continue against the rebased branch. On conflict, list the unmerged files (`git diff --diff-filter=U --name-only`) and ask the developer who should resolve the conflicts. When the developer asks you to resolve them, fix the conflicts, `git add` the files, and run `git rebase --continue`. In every other case (the developer resolves them, the conflicts cannot be resolved, or the rebase fails otherwise) run `git rebase --abort` and stop the run.

- **Diff baseline is local `master`.** After the rebase, the three-dot diff against local `master` is the baseline.

- **Diff is nonempty.** When the three-dot diff produces no files, exit with a one-line message — no validation produces useful signal on a clean branch.

## Input

### Diff

```bash
git diff --name-status "$(git merge-base HEAD master)...HEAD"
```

## Expected Output

**`PASS`** or **`FAIL`**, followed by a **Results Summary** table the `pr` and `pr-check-publish` skills reuse to record what was tested on the GitHub PR.

The procedure runs in two passes over the validations, in the order below. The order is dependency-driven: drift first (later validations see the regenerated tree), then formatting, then build, then tests.

1. [Instance Wrapper Build](validations/instance-wrapper-build.md)

1. [REST Builder](validations/rest-builder.md)

1. [Service Builder](validations/service-builder.md)

1. [Go Generate](validations/go-generate.md)

1. [Source Format](validations/source-format.md)

1. [Go Source Format](validations/go-source-format.md)

1. [Module Registration](validations/module-registration.md)

1. [Portlet Title](validations/portlet-title.md)

1. [Transaction Usage](validations/transaction-usage.md)

1. [Full Portal Build](validations/full-portal-build.md)

1. [Per-Module Compile](validations/per-module-compile.md)

1. [Integration Test Compile](validations/integration-test-compile.md)

1. [Cross-Module Compile](validations/cross-module-compile.md)

1. [Baseline](validations/baseline.md)

1. [JSP Compile](validations/jsp-compile.md)

1. [Theme Build](validations/theme-build.md)

1. [Workspace Build](validations/workspace-build.md)

1. [Poshi Syntax](validations/poshi-syntax.md)

1. [Structural Smoke](validations/structural-smoke.md)

1. [Java Unit Tests](validations/java-unit-test.md)

1. [PQL Validation](validations/pql-validation.md)

1. [JavaScript Unit Tests](validations/javascript-unit-test.md)

Process each validation in a subagent.

### Pass 1: Estimate

Read every validation file under `.claude/skills/pr-check/validations` in a single parallel batch — one Read tool call per file, all in the same tool-use turn. From each file, take the regex inside its `## Match` section.

In your next turn, compose a single bash script that:

- computes the diff: `git diff --name-only "$(git merge-base HEAD master)...HEAD"`
- for each validation, tests its regex against the diff and prints the validation name when it fires (a leading `!` in the regex inverts: fire when any diff path does *not* match the rest)
- ` &! ` in the regex splits it into an include side and an exclude side. The validation fires when a diff path matches the include side but not the exclude side.
- runs as a single Bash tool invocation

From the script's output, sum the matched validations' `## Time Estimate` values for the cumulative total. The matching is mechanical; consult each file's prose `## Trigger` only when a result needs human-judgment context (e.g., Service Builder output-only catch-up).

When the total exceeds 20 minutes, surface the breakdown and ask the developer whether to trim a validation or proceed.

### Pass 2: Execute

For each matched validation, spawn one subagent. **Pass it only the `## Command` and `## Autocommit` sections of the validation file, not the full file.** Record PASS or FAIL. Do not halt on FAIL — continue so the developer sees the full picture. When a command directs the subagent to return a failure note on FAIL, capture that note alongside the FAIL result.

When the validation's **Command** is a build (gradle, ant, npm, jest), bound the output:

```bash
<command> 2>&1 | tail --lines=100
```

Decide PASS/FAIL from the build tool's success markers in the captured output (`BUILD SUCCESSFUL` / `BUILD FAILED`, `Tests: N passed, M failed`, etc.). Apply only to build commands. Leave inert commands like `git status --porcelain` and `git diff --quiet` untouched.

When all validations pass, report `PASS`. When any fail, report `FAIL` and surface the failed validations.

## Results Summary

After the two passes complete, emit a Results Summary block. It is the canonical record of what was tested, embedded verbatim by the `pr` skill into the PR description and reused by the `pr-check-publish` skill when recording a run on an existing PR.

Capture the tested commit with `git rev-parse HEAD` **after** Pass 2 completes, so the SHA reflects the tree that was actually exercised — including any autocommits the validations made, such as the `<TICKET> SF` source-format commit. This is the commit the `pr` skill pushes as the PR head and the commit the webhook binds the `pr-check` status to, so a reviewer can tell whether the current head is the one that was tested.

The block is the overall state and tested SHA, followed by a table with one row per **matched** validation — the validations that actually ran, in the execution order above. Validations whose `## Match` regex did not fire are omitted rather than listed as skipped, so the table reflects only what the diff exercised.

```markdown
**pr-check: PASS** — tested on `<head-SHA>`

| Validation | Result |
| --- | --- |
| Source Format | PASS |
| Full Portal Build | PASS |
| Java Unit Tests | PASS |
```

The overall state is `PASS` only when every row is `PASS`; any `FAIL` row makes it `FAIL`.

When a failed validation returned a failure note, append it below the table, separated by a blank line. The note is part of the Results Summary, so it travels verbatim into the PR description through the `pr` skill and into any comment the `pr-check-publish` skill posts.