---

allowed-tools: [Bash, Glob, Grep, Read, Skill, Write]
argument-hint: '<routine name | Testray build ID>'
description: Triage every failure in a team's Testray build, rank it by its history across recent builds, map it to existing tickets and pull requests, cluster failures that share a cause, and route each one to `test-fix` or to the CI team. Use when the user asks to triage a build or go through a team's failures.
name: test-triage

---

# Triage a Test Build

Piloted by the Content Management team on `ci:test:content-management` (LPD-110398).

This skill works across a whole build: it decides **which failures to dig into and in what order**, makes sure no failure gets a second fix, and hands each one to `test-fix`. It never edits test or product code, and it does not decide whether a fix belongs in the test or the product; `test-fix` decides that from the commit that broke the test and its documented intent.

## Preconditions

- `gh` is authenticated.

- Jira access works as described in `.claude/rules/jira.md`.

- Reading CI screenshots and logs needs Google Cloud credentials (see [`../test-fix/references/ci-artifacts.md`](../test-fix/references/ci-artifacts.md)).

- `${TESTRAY_CLIENT_ID}` and `${TESTRAY_CLIENT_SECRET}` are set (see [`../test-fix/references/testray.md`](../test-fix/references/testray.md)).

## Input

- **Routine name**, for example `[master] ci:test:<team>`. Triage its newest complete build.

- **Testray build ID**. Triage that build within its routine. When the script reports the build is incomplete, triage the build it names instead and say so in the report.

When nothing is supplied, ask the user for the team routine. Team routines are named `[master] ci:test:<name>`, where the name usually follows the team but not always its `.github/CODEOWNERS` handle (for example `dev-tools` for `@liferay-devtools`). List them with the `/o/c/routines` endpoint, the filter `contains(name,'[master] ci:test:') and r_routineToProjects_c_projectId eq '35392'`, and `pageSize=200`, and ask the user to pick one.

Ask the user once, up front, whether any suites or components are excluded right now (for example, a suite another engineer is migrating to Playwright). Record the answer and skip those failures without triaging them; list them and their count in the report.

## Expected Output

A triage table with one row per failure, or per cluster that shares a proven cause:

| Test | Pattern | Route | Evidence | Result |
| --- | --- | --- | --- | --- |

- **Evidence** is the specific thing that decided the route or the cluster: a screenshot, a log line, a ticket, a pull request.

- **Pattern** is the cross build result from **Characterize the Build** (for example `20/20`).

- **Result** is what came back: the `test-fix` verdict with its Task and pull request, the LRCI ticket, or the ticket or pull request that already covers the failure.

- **Route** is one of `test-fix`, `Environment`, `Already tracked`, or `Not dug`.

Below the table, a **Needs Your Decision** list with a link and one line on what is undecided for each of these:

- Every failure whose `test-fix` run ended `Unresolved`, other than one that names an open pull request.

- Every `No fix needed` on a `DETERMINISTIC`, `NEAR-DETERMINISTIC`, or `NEW-DETERMINISTIC` failure, noting that the local pass contradicts CI and that the environment is the first thing to check.

- Every failure whose open `claude-test-fix` Task carries a `test-fix` handover summary as its latest comment, found in **Map to Existing Work**. The Task stays in **In Progress** until a person picks it up, so it reappears in every later report until then.

Write the table and the list to a file the user names (default `test-triage-<build>.md` in a directory outside the repository) and show the counts per route in the reply.

## Workflow

### Characterize the Build

Run the bundled script from the repository root. It profiles the routine's recent complete builds and classifies each failure in the triaged build by how many of those builds it also failed in:

```bash
python3 "$(git rev-parse --show-toplevel)/.claude/skills/test-triage/scripts/characterize.py" \
	--builds 20 \
	--routine "<routine name>" \
	--tsv "<directory outside the repository>/characterize-<build>.tsv"
```

Pass `--build <id>` instead of `--routine` to triage a specific build, and `--project <id>` for a routine outside the master project.

Rank the work by this pattern, never by Testray's own flake category or by a single build's status. Testray's category is wrong in both directions: deterministic breaks get labelled as known flakes, and the reverse. The statuses, in priority order:

- `DETERMINISTIC` and `NEAR-DETERMINISTIC` failures have a real, reproducible cause. Triage them first.

- `NEW-DETERMINISTIC` failures have failed in every build since they started (at least two), or in at least the last three builds. They are the newest regressions and have the cleanest commit window, so triage them with the deterministic ones.

- `INTERMITTENT-*` failures are worth a look when they sit next to a deterministic cluster (same class, same helper, same page). A flaky neighbour of a known break is often the same break.

- `NEW-OR-SPORADIC` failures need a second build before they earn a dig, unless they share a signature with something new.

A failure that is not dug this run (an `INTERMITTENT-*` failure with no deterministic neighbour, or a `NEW-OR-SPORADIC` one that shares no signature with a failure being dug) gets the route `Not dug` and the result "watch the next build". No ticket is filed for it.

### Expand Aggregate Rows

Some rows are not single tests:

- **Playwright shard rows** (names like `playwright-js-tomcat101-postgresql163/21/0`) stand for every spec in that shard. Read the shard console ([`../test-fix/references/ci-artifacts.md`](../test-fix/references/ci-artifacts.md)): its summary (`N passed`, `N failed`, `N flaky`) and its numbered failure list. A failing spec that also has its own row in the build is triaged there; triage only the failing specs that have no row of their own. List specs that only flaked (passed on retry); they are not failures yet, but they are the next ones.

- **`PortalLogAssertorTest`** rows fail on server log content. Group them by the logged signature, not by the row, and find the test that produced each log line.

- **`Top Level Build`** has an empty error and fails whenever any other row fails. Give it the route `Not dug` with the result "fails with the other rows" and file nothing. When it is the only failing row, read its console and route it from that.

### Map to Existing Work

Before any digging, find what already covers each failure, so that no failure gets a second fix.

1. Search Jira for tickets that name the test (class, method, or spec title) anywhere in their text, comments included (JQL `text ~ "\"<name>\""`, quoted as a phrase because Jira otherwise matches each word on its own). Open each hit and confirm it names this test before counting it.

1. Search open pull requests that change the test file or any helper it calls (macros, page objects, fixtures), the way `test-fix` does in **Claim the Failure**.

1. For a resolved ticket, decide coverage the way `test-fix` does in **Claim the Failure**: find its fix commit and test `git merge-base --is-ancestor <fixSha> <buildSha>`. When the fix was already in the failing build, the ticket does not cover this failure.

A failure is `Already tracked` when any of these covers it:

- A resolved ticket whose fix commit is not yet in the failing build, or, when it has no fix commit, that was resolved on or after the failure date (step 3).

- An open pull request changes the code for it.

- An open ticket for it is assigned or in progress, other than a `claude-test-fix` Task whose latest comment is a `test-fix` handover summary. That Task is waiting for a person; put the failure on the **Needs Your Decision** list instead.

- An open ticket in `LRCI` or owned by another team names it.

When the record is incomplete, add the test name and build to the covering ticket. An open, unassigned Bug in the triaging team's own component does not make a failure tracked; name it in the evidence file so `test-fix` reuses it instead of filing another.

### Read the CI Evidence and Cluster

For every failure still open, read the failure screenshot and the console before forming a theory from the error text ([`../test-fix/references/ci-artifacts.md`](../test-fix/references/ci-artifacts.md)). The error names the step that noticed the problem, which is often not the step that caused it. [`../test-fix/references/reading-failures.md`](../test-fix/references/reading-failures.md) lists the patterns that mislead most often.

Group failures into clusters only after the evidence agrees. A shared locator or a shared error message is not a shared cause. Within a cluster, a test that fails every build and one that fails a third of the time usually have different causes.

### Route

Route a failure to `Environment` only when the CI evidence alone settles it, without a local run: the job never ran (every test `UNTESTED`), whole projects are missing from a green build, or the console shows an infrastructure error (agent, Docker, database, or data archive) before the test began. Find or file a ticket in the CI project (`LRCI`) through the Jira REST API (`.claude/rules/jira.md`) with the evidence, naming the routine. Do not change code.

Route every other failure to `test-fix`, one run per cluster, starting with the most deterministic member. Write the failure's row, the CI evidence read above, the other tests in its cluster, the routine name, the owning team's `.github/CODEOWNERS` handle (ask the user when the routine does not map to one), and any Bug found in **Map to Existing Work** to a file in the same directory as the triage table (outside the repository), then invoke the `test-fix` skill as `<caseResultId> --evidence <file>`.

When the verdict is `Bug in portal` or `Outdated test`, rerun the other members of the cluster against the fixed bundle. Those that still fail are a different cause: route them on their own. When the verdict is `Unresolved` naming an open pull request, the failure is `Already tracked` by that pull request.

### Report

Write the triage table and reply with the counts per route, the three failures that affect the most tests, and the **Needs Your Decision** list.