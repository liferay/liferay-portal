---

allowed-tools: [Bash, Edit, Glob, Grep, Read, Skill, Write]
argument-hint: '<LPD bug key or URL> [--evidence <file>] [--take-over]'
description: Fix a filed product Bug whose failing tests have no single suspect commit window, proving the user facing behaviour is fixed before trusting the tests. Use when the user asks to fix a product bug found by failing tests, or when the `test-triage` skill routes a failure here.
name: bug-fix

---

# Fix a Product Bug Found by Tests

Piloted by the Content Management team (LPD-110398).

Use this skill for a Bug that `test-triage` routes here, or for any other filed Bug that failing tests reproduce when `test-fix` does not apply (there is no window between a last pass and a first failure to search, or the failure has always been there).

The risk this skill guards against is a change that makes the tests pass without fixing what a user sees. Every step below is ordered so the behaviour is proven first and the tests are checked last.

## Preconditions

- The Bug exists in Jira and names at least one failing test or a reproduction.

- The bundle used here is not shared with test side work in progress (see [`../test-triage/references/local-environment.md`](../test-triage/references/local-environment.md)).

## Expected Output

One outcome, posted as a comment on the Bug and returned to the caller. Every outcome except a `Fixed` that leaves no named test failing also adds the `test-triage-review` label to the Bug through the Jira REST API, so it reaches a person even if the caller stops before acting on it. Copy the routine name from the `--evidence` file, when there is one, into the outcome comment; without it, ask the user for the routine name, or say in the comment that none applies:

- `Blocked on owner`: an open pull request already fixes the Bug, or the code belongs to another team and the user declined to take it. Verify an open pull request first and comment the result on it; a failed verification still ends here, with the failure in the comment. When the user declined and the owner has no pull request or ticket, find or file the owning team's ticket and link it to the Bug. The comment on the Bug names the owner's pull request or ticket.

- `Fixed`: the root cause, the before and after evidence, the regression test, and the pull request URL.

- `Handed back`: the behaviour did not reproduce without the test, or a test had to change to pass. The comment starts with `Handed back to test-triage: <reason>` and carries the evidence, the Bug stays open, and the failure returns to `test-triage`.

- `Unresolved`: the behaviour reproduces but no fix converged. The comment lists the hypotheses, the attempts and what each changed, and the most plausible remaining lead; the Bug stays open.

## Rules

- **Add a regression test** at the lowest level that shows the defect (unit, then integration, then Playwright). Poshi is not used for new tests.

- **Do not change an existing test's assertions or expected values.** A test that has to change to pass means the side was wrong; stop and send the failure back to `test-triage`.

- **Do not fix another team's code while their fix is in review.** Verify their fix instead and report the result on their pull request.

## Workflow

### Check Ownership and Fixes in Flight

1. Read the Bug (description, comments, links, component, and assignee) and the `--evidence` file when `test-triage` passed one.

1. Search for work already under way: the Bug key in commit subjects on the base branch, in open pull requests across the team forks (`gh search prs <KEY> --state open`), and in linked tickets. A `claude-test-fix` Task that `test-triage` linked to the Bug is not work under way. When an open pull request already fixes the Bug, verify it, comment the result on it, and stop with `Blocked on owner`.

1. Find the owner of the code at fault with `.github/CODEOWNERS`. When it belongs to another team and nobody has picked it up, post the evidence on the Bug for them before writing a fix, and ask the user whether to continue. Skip the question when `--take-over` is passed: `test-triage` already asked, and the user agreed to take the work.

### Reproduce the Behaviour Without the Test

Reproduce what a user would see, by hand or with a minimal probe, on a bundle that matches the conditions the tests ran under (database, virtual instance, upgrade archive, feature flags). Write down the steps and the observed result. When it does not reproduce without the test, stop: the side is not proven, and the failure goes back to `test-triage`.

Record the "before" evidence (screenshot, log excerpt, HTTP response). The fix is judged against it.

### Find the Root Cause

Trace from the observed behaviour to the code: the request that fails, the exception in the server log, the component that renders nothing. Read the code at the current head of the branch, not from memory, and confirm every claim about what breaks against that code.

When a direct call reproduces the problem more simply than the UI (a REST or JSON web service request, a unit level call), use it. It shortens every later iteration.

### Fix and Prove the Behaviour

1. Invoke the `start-work` skill on the Bug so the branch and commits carry its key. Doing this only now keeps a Bug that is handed back earlier unassigned.

1. Make the smallest product change that addresses the root cause. Follow the code style rules in `pr-reviewer/rules` and the deploy steps in the project `CLAUDE.md`.

1. Deploy it and confirm the deployed artifact contains it.

1. Repeat the reproduction from **Reproduce the Behaviour Without the Test** exactly. The behaviour must be fixed here, before any test runs.

1. Add the regression test and show it fails without the fix and passes with it.

1. Run every test named on the Bug. Tests that still fail are either a second defect (fixing step N exposes step N+1) or a different cause; return them to `test-triage` in the outcome comment (or, without a caller, list them there for the user) rather than widening the fix.

### Restore and Report

Every outcome passes through this step, including a hand back.

1. When the run ends without a commit, discard the uncommitted product changes (`git restore` on the touched files) and redeploy each module they touched from the clean tree, so the next run starts from the code under test.

1. Restore the bundle's `portal-ext.properties` and data to how they were before the fix was deployed, or leave the fix on its own bundle that nothing else uses.

1. For `Fixed`, invoke `format-source`, commit with the Bug key, and invoke the `pr` skill targeting the owning team from `.github/CODEOWNERS`.

1. Comment on the Bug with the outcome and the content listed for it in **Expected Output**.