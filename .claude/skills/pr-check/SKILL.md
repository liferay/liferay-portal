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

- computes the diff: `git diff --name-only --no-renames "$(git merge-base HEAD master)...HEAD"`, since a detected rename collapses to its new path alone and hides the old one from every regex
- for each validation, tests its regex against the diff and prints the validation name when it fires (a leading `!` in the regex inverts: fire when any diff path does *not* match the rest)
- ` &! ` in the regex splits it into an include side and an exclude side. The validation fires when a diff path matches the include side but not the exclude side.
- runs as a single Bash tool invocation

From the script's output, sum the matched validations' `## Time Estimate` values for the cumulative total. The matching is mechanical; consult each file's prose `## Trigger` only when a result needs human-judgment context (e.g., Service Builder output-only catch-up).

When the total exceeds 20 minutes, surface the breakdown and ask the developer whether to trim a validation or proceed.

### Pass 2: Execute

The rules below divide in two. Dispatch, ordering, the shared setup, handoffs, the ledger, and the overall state belong to this runner. Reading a log, judging a result, and reporting a note belong to the subagent, which never sees this document and is told only what it needs.

For each matched validation, spawn one subagent. **Pass it only the `## Command` and `## Autocommit` sections of the validation file, not the full file.** A validation with no `## Autocommit` section makes no commit, so say so rather than leaving the subagent to infer it from an absence. That says nothing about the working tree, since a validation without one can still build and leave output behind. Record `PASS`, `FAIL`, or `NOT VERIFIED`, and capture any note the command directs it to return. Do not halt on a failure, so the developer sees the full picture.

A validation reports **`NOT VERIFIED`** when it ran and established nothing about the branch, such as an empty work set, a compile with no source, or a change with no counterpart to exercise. It does not block, and it carries a reason naming what went unexamined, one line in the table with whatever detail the validation asks for beneath it. Reserve `FAIL` for a validation that found a real defect.

A `NOT VERIFIED` run does not autocommit, since a run that established nothing has produced nothing worth recording and the tree it would stage may hold a half finished setup. A `FAIL` run still autocommits where its **Autocommit** section says to, because a formatter's repairs are worth keeping even when an unfixable violation blocks the branch, and so does a `PASS` run. Tell the subagent this when you dispatch it, since its **Autocommit** section reads as unconditional on its own.

Run a validation that autocommits with **nothing else that writes to the working tree** in flight, since `git add --all` cannot tell its own repair from one another validation made seconds earlier and commits the wrong work under its title. A validation that only reads is safe alongside anything, provided it reads a commit it pinned at the start rather than the working tree or the index. A concurrent validation moves the tree when it writes and the index when it stages, so only a pinned commit holds still for the whole run. Whether a validation reads or writes can depend on the diff, since **Module Registration** only reports when its markers are all removals and builds when one is added, so treat it as a writer unless its own text rules the writing branch out for the diff at hand. Keep tree writers off each other too, since several share build output such as `modules/build/node`.

Run `ant compile install-portal-snapshots` once before the first validation that declares it, rather than letting each launch the same build into the same `${REPO_ROOT}/.m2`. Tell every later subagent that it is satisfied, since a subagent sees only its own **Command** and would otherwise run it again.

A validation may hand off to another, as **Per-Module Compile** does when its deploy set grows past the point where one full build is cheaper. Run the validation it names, give the table that validation's row and result, and mark the one that handed off `NOT VERIFIED`. Pass 1 selects on regexes alone and cannot see a set Pass 2 derives, so a handoff is the only way those branches run.

An autocommit can change the diff, so recompute the ledger after a validation whose commit may add a path Pass 1 never saw, as Baseline's `packageinfo` and `bnd.bnd` repairs do, and dispatch whatever newly fires. Skip it after a validation that can only touch paths the branch already changed, such as a formatter running in current branch mode, since its commit cannot widen the diff.

Give the subagent everything the validations use and none of them define. That is `${REPO_ROOT}`, the ticket their **Autocommit** sections write into a commit title as `<TICKET>`, and the result its own verdict implies for committing, since the rule above lives here and the subagent never reads this document:

```bash
REPO_ROOT=$(git rev-parse --show-toplevel)
```

Resolve `<TICKET>` from the branch name the way [commit.md](../../rules/commit.md) does, which is the ticket pattern of uppercase letters, hyphen, and digits rather than the whole branch name, so `LRCI-8065-rules` and `LRCI-8065-fixture-pr1f` both give `LRCI-8065`. A subagent that is not given it commits under the literal string.

Tell it how to commit as well, since no validation says. The title is the whole message, with no body and no attribution footer of any kind, which is the repository's convention for a generated commit.

When the validation's **Command** is a build (gradle, ant, npm, jest), keep the whole log and bound only what is displayed:

```bash
LOG_CHECK=$(mktemp)
LOG_SETUP=$(mktemp)

<setup command> > "${LOG_SETUP}" 2>&1
<check command> > "${LOG_CHECK}" 2>&1

tail --lines=100 "${LOG_CHECK}"
```

Give each build its own log. A single binding reused across two builds means the second overwrites the first, and the evidence that setup succeeded is gone by the time you need it. A **Command** with one build needs only one.

Judge from each full log rather than from the tail. A source formatter prints its violations in the middle of a run and its stack trace at the end, so the last hundred lines carry the failure and not the reason for it. Search every log the run produced for the build tool's markers (`BUILD SUCCESSFUL`, `BUILD FAILED`, `Tests:`, `Test Suites:`) and for whatever the validation says its finding looks like. Apply this to build commands only, and leave inert commands like `git status --porcelain` untouched.

When a **Command** runs more than one build, keep a log per build and judge each on its own, since a setup step and the check it precedes fail for different reasons. The setup is the earlier build the later one depends on, and a validation that runs two checks rather than a setup and a check should say so.

A failing setup is positive evidence the run could not proceed, so report `NOT VERIFIED` naming it rather than a verdict on a check that never ran, and judge the check itself only once its setup succeeded. That is the case the rule below is about, even though its examples are all external.

A log can be far larger than you can read. Search it for the markers above rather than reading it through, and quote the lines you found. The displayed tail is for the developer, never the basis of a verdict.

When a command ran and exited nonzero, that status alone does not separate a validation that found something from one that could not run. Report `NOT VERIFIED` there only on positive evidence that the run could not proceed, such as a failed download, a registry timeout, or a process killed for memory. Without that evidence report `FAIL`, since a subagent is given the Command and Autocommit sections alone and often cannot tell a finding from an infrastructure error by its wording, and defaulting to `FAIL` leaves a real defect blocking rather than passing it through as unverified.

That governs a nonzero exit and nothing else. A validation that names its own `NOT VERIFIED` case reports it whatever any command exited.

## Results Summary

After the two passes complete, emit a Results Summary block. It is the canonical record of what was tested, embedded verbatim by the `pr` skill into the PR description and reused by the `pr-check-publish` skill when recording a run on an existing PR.

Capture the tested commit with `git rev-parse HEAD` **after** Pass 2 completes, so the SHA reflects the tree that was actually exercised — including any autocommits the validations made, such as the `<TICKET> SF` source-format commit. This is the commit the `pr` skill pushes as the PR head and the commit the webhook binds the `pr-check` status to, so a reviewer can tell whether the current head is the one that was tested.

The block is the overall state and tested SHA, followed by a table with one row per **matched** validation — the validations that actually ran, in the execution order above. Validations whose `## Match` regex did not fire are omitted rather than listed as skipped, so the table reflects only what the diff exercised.

```markdown
**pr-check: PASS** — tested on `<head-SHA>`

| Validation | Result |
| --- | --- |
| Source Format | PASS |
| Module Registration | NOT VERIFIED |
| Java Unit Tests | PASS |

Module Registration verified nothing. The diff removes `.lfrbuild-ci` from `apps:blogs:blogs-api`, which drops the module from CI's deploy pass and breaks no build, so whether CI still needs it is the developer's judgment.
```

The overall state is `FAIL` when any row is `FAIL`, and `PASS` otherwise. A `NOT VERIFIED` row leaves the overall state alone, and the marker the `pr-check-publish` skill writes still records `success`, since the webhook accepts only `failure`, `skipped`, and `success` and silently discards anything else.

Every row whose validation returned a note appends it below the table, separated by a blank line. A `FAIL` and a `NOT VERIFIED` always carry one, and a `PASS` can too, as **Module Registration** does when a diff pairs an addition it verified with a removal it can only report. The notes travel verbatim into the PR description through the `pr` skill and into any comment the `pr-check-publish` skill posts.