---

allowed-tools: [Bash, Glob, Grep, Read, Skill, Write]
argument-hint: '<routine name | Testray build ID>'
description: Triage every failure in a team's Testray build, give each one a side (test, product, environment, another team, already tracked, or unknown) backed by evidence, and route it to the skill that fixes it. Use when the user asks to triage a build, go through a team's failures, or decide whether a failure is a test or a product problem.
name: test-triage

---

# Triage a Test Build

Piloted by the Content Management team on `ci:test:content-management` (LPD-110398).

This skill decides **who owns each failure**. It never edits test or product code. Fixing happens in `test-fix` (test side, or a product regression with a suspect commit window) and `bug-fix` (a filed product Bug). The side decided here, with its evidence, travels with the failure into the fixing skill, so a product change is never used to make a test pass and a test is never bent to hide a product bug.

## Preconditions

- `gh` is authenticated.

- Jira access works as described in `.claude/rules/jira.md`.

- Reading CI screenshots and logs needs Google Cloud credentials (see [`references/ci-artifacts.md`](references/ci-artifacts.md)).

- `${TESTRAY_CLIENT_ID}` and `${TESTRAY_CLIENT_SECRET}` are set (see [`../test-fix/references/testray.md`](../test-fix/references/testray.md)).

## Input

- **Routine name**, for example `[master] ci:test:<team>`. Triage its newest complete build.

- **Testray build ID**. Triage that build within its routine. When the script reports the build is incomplete, triage the build it names instead and say so in the report.

When nothing is supplied, ask the user for the team routine. Team routines are named `[master] ci:test:<name>`, where the name usually follows the team but not always its `.github/CODEOWNERS` handle (for example `dev-tools` for `@liferay-devtools`). List them with the `/o/c/routines` endpoint, the filter `contains(name,'[master] ci:test:') and r_routineToProjects_c_projectId eq '35392'`, and `pageSize=200`, and ask the user to pick one.

Ask the user once, up front, whether any suites or components are excluded right now (for example, a suite another engineer is migrating to Playwright). Record the answer and skip those failures without triaging them; list them and their count in the report.

## Expected Output

A triage table with one row per failure, or per cluster that shares a proven cause:

| Test | Pattern | Side | Evidence | Next Step |
| --- | --- | --- | --- | --- |

- **Evidence** is the specific thing that decided the side: a screenshot, a log line, a local before and after, a commit. A side without evidence is `Unknown`.

- **Next Step** is a skill to invoke, a ticket to comment on, or the question that blocks the verdict.

- **Pattern** is the cross build result from **Characterize the Build** (for example `20/20`).

- **Side** is one of `Test`, `Product`, `Environment`, `Other team`, `Already tracked`, or `Unknown`, as defined in **Decide the Side**, or `Not dug` for a failure **Characterize the Build** leaves for a later build or for the `Top Level Build` row (see **Expand Aggregate Rows**).

Below the table, a **Needs Your Decision** list: every failure this run could not settle, and every ticket for this routine carrying the `test-triage-review` label (JQL `labels = test-triage-review AND text ~ "\"<routine name>\""`, then keep only tickets whose text has the exact routine name not followed by `-`, because Jira also matches the phrase inside a longer routine name such as `search-remote`), each with a link and one line on what is undecided. A failure on this list is never routed or skipped automatically; it stays on the list in every later report until a person removes the label. After the label is removed, the next build treats the failure like any other: an assigned or in progress ticket makes it `Already tracked`, and otherwise it is triaged and routed again.

Every ticket this skill files or labels names the routine in its text, and the fixing skills copy the routine name from `--evidence` into their outcome comments, so the query above finds them.

Write the table and the list to a file the user names (default `test-triage-<build>.md` in a directory outside the repository) and show the counts per side in the reply.

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

A failure that is not dug this run (an `INTERMITTENT-*` failure with no deterministic neighbour, or a `NEW-OR-SPORADIC` one that shares no signature with a failure being dug) gets the side `Not dug` and the next step "watch the next build". No ticket is filed for it. A failure whose ticket carries `test-triage-review` goes on the **Needs Your Decision** list instead of `Not dug`.

### Expand Aggregate Rows

Some rows are not single tests:

- **Playwright shard rows** (names like `playwright-js-tomcat101-postgresql163/21/0`) stand for every spec in that shard. Read the shard console ([`references/ci-artifacts.md`](references/ci-artifacts.md)): its summary (`N passed`, `N failed`, `N flaky`) and its numbered failure list. A failing spec that also has its own row in the build is triaged there; triage only the failing specs that have no row of their own. List specs that only flaked (passed on retry); they are not failures yet, but they are the next ones.

- **`PortalLogAssertorTest`** rows fail on server log content. Group them by the logged signature, not by the row, and find the test that produced each log line.

- **`Top Level Build`** has an empty error and fails whenever any other row fails. Give it the side `Not dug` with the next step "fails with the other rows" and file nothing. When it is the only failing row, read its console and decide the side from that.

### Map to Existing Work

Before any digging, find what already covers each failure, so that no failure gets a second fix.

1. Search Jira for tickets that name the test (class, method, or spec title) anywhere in their text, comments included (JQL `text ~ "\"<name>\""`, quoted as a phrase because Jira otherwise matches each word on its own). Open each hit and confirm it names this test before counting it.

1. Search open pull requests that change the test file or any helper it calls (macros, page objects, fixtures). Search the fork of the team that owns the test, the fork of each team that owns a helper, and `brianchandotcom/liferay-portal`. A team's fork is `<CODEOWNERS handle without @>/liferay-portal`. When `.github/CODEOWNERS` has no entry for a file (Poshi testcases and macros under `portal-web/test/functional`), search the fork of every team handle in `.github/CODEOWNERS`. Skip a team that has no fork of the repository. Filter on the changed files; `--search` only matches titles and descriptions and misses them:

	```bash
	gh pr list \
		--jq '.[] | select(any(.files[]; .path | endswith("/<file basename>"))) | .url' \
		--json changedFiles,files,url \
		--limit 200 \
		--repo <fork> \
		--state open
	```

	`--json files` returns at most 100 files per pull request. For a pull request whose `changedFiles` is over 100, list its files with `gh api repos/<fork>/pulls/<number>/files --jq '.[].filename' --paginate`. Pull requests opened from personal forks against other repositories are not found this way.

1. For a resolved ticket, decide coverage the way `test-fix` does in **Claim the Failure**: find its fix commit and test `git merge-base --is-ancestor <fixSha> <buildSha>`. When the fix was already in the failing build, the ticket does not cover this failure.

A failure is `Already tracked` when any of these covers it:

- A resolved ticket whose fix commit is not yet in the failing build, or, when it has no fix commit, that was resolved on or after the failure date (step 3).

- An open pull request changes the code for it.

- An open ticket for it is assigned or in progress.

- An open ticket in `LRCI` or owned by another team names it.

Two exceptions, both of which put the failure on the **Needs Your Decision** list instead:

- A ticket for it, open or closed, carries the `test-triage-review` label. A fixing skill or an earlier triage run could not settle it, and a person decides what happens next.

- An open ticket for it links a pull request that is merged and already in the failing build. The fix did not cover this failure; add the `test-triage-review` label to that ticket and comment the routine name on it.

An open, unassigned product Bug in the triaging team's own component does not make a failure tracked; the failure keeps its side and goes to **Route**. When the side is `Test`, `Environment`, or `Other team`, comment the evidence on that Bug and, when it is labelled `test-triage-filed`, close it (for `Other team`, link it to the owner's ticket first), as **Close the Loop** does after a handback. Another team's open ticket always counts as tracked; a closed one is judged by step 3.

### Read the CI Evidence First

For every failure still open, read the failure screenshot and the console before forming a theory from the error text ([`references/ci-artifacts.md`](references/ci-artifacts.md)). The error names the step that noticed the problem, which is often not the step that caused it. [`references/reading-failures.md`](references/reading-failures.md) lists the patterns that mislead most often.

Group failures into clusters only after the evidence agrees. A shared locator or a shared error message is not a shared cause.

### Check the Run Context

Before reproducing, record how CI ran the test. A local run that differs in any of these can report the opposite of CI:

- Feature flags the test or its class sets, and company level settings that override `portal-ext.properties`.

- For upgrade tests, the data archive and its source version.

- The database, app server, and browser version from the batch name and the test's properties.

- Whether the test ran inside a fresh virtual instance (Poshi `setUp` calling `TestCase.setUpPortalInstance()` with `test.liferay.virtual.instance` enabled).

### Reproduce Locally

Follow [`references/local-environment.md`](references/local-environment.md) to make the local bundle match the run context, then run the test.

- **Different failure**: treat it as a local problem until proven otherwise. Clean the state earlier runs left behind and rerun before drawing any conclusion.

- **Passes locally**: compare against the run context before calling it a flake. A virtual instance, a flag state, or data left over from another run explains a local pass more often than flakiness does.

- **Same failure**: the diagnosis can be tested. Continue.

A control run proves the diagnosis. Only a run with the change applied proves a fix. A probe meant to show the failure must actually fail before its "after" result counts.

### Decide the Side

Decide by the behaviour, not by where the assertion fails. A test failing against product code does not make the product wrong.

1. Find what changed. Search history for the failing locator, macro, page object, or fixture (`git log -S '<string>'`) and for product commits between the last pass and the first failure. Check test side changes first; a test helper that changed under the test is a common cause.

1. Treat the test's expected values as the specification unless the commit that changed the behaviour documents the contract change (its subject, its Jira ticket, or its pull request body). This is the rule `test-fix` applies.

1. For tests that exercise the runtime (Java Integration, Playwright, Poshi), reproduce the user facing behaviour without the test, by hand or with a minimal probe. A `Product` side needs this: a person doing the same thing sees the defect.

1. Name the side:

	- `Test`: the test or its helpers are wrong, out of date, or racing the UI; the product behaves as specified.

	- `Product`: the product is wrong and the behaviour reproduces without the test.

	- `Environment`: CI infrastructure, data archives, or test runner configuration.

	- `Other team`: the defective code belongs to another team's component. The test stays in the suite that runs it; the bug goes to the owner, with the failing tests as its reproduction.

	- `Already tracked`: an open ticket or pull request covers it (see **Map to Existing Work**).

	- `Unknown`: the evidence does not settle it yet. Write down what would.

### Route

Pass the side and its evidence along with every handoff: write the failure's row, its evidence, the routine name, and the owning team's `.github/CODEOWNERS` handle (ask the user when the routine does not map to one) to a file in the same directory as the triage table (outside the repository), and pass it as `--evidence`. What comes back is handled in **Close the Loop**, within this run.

When **Map to Existing Work** found an open Task for the failure that nobody is working on (for example one an earlier triage run filed), reuse it. For `test-fix`, pass it as `--task`; for `Already tracked`, comment the covering ticket or pull request URL on it and close it as `Won't Do`; for any other route except `Unknown`, link it to the new ticket with the **Fix** link type and close it as `Won't Do`. For `Unknown`, use it as the Task in **Close the Loop**.

- `Test` → invoke `test-fix` as `<caseResultId> --evidence <file> --side Test`. When the test has no recorded last pass, say so in the evidence file; `test-fix` then works from that evidence instead of a commit window.

- `Product` with a recorded last pass and first failure, and no Bug filed yet → invoke `test-fix` as `<caseResultId> --evidence <file> --side Product`; its `Bug in portal` path fixes regressions inside that window.

- `Product` with no such window, or already filed as a Bug → file it with `jira-bug` when it is not filed yet and add the `test-triage-filed` label, then invoke `bug-fix` as `<Bug key> --evidence <file>`.

- `Environment` → find or file a ticket in the CI project (`LRCI`) through the Jira REST API (`.claude/rules/jira.md`) with the evidence. Do not change code.

- `Other team` → file a Bug for the owning team (add the `test-triage-filed` label) or comment on their existing ticket, with the failing test names, the build, and the evidence. When their fix is in review, verify it instead of writing another, comment the result on their pull request, and make that pull request the **Next Step**. When nobody has picked the bug up, ask the user whether to take it; when they agree, invoke `bug-fix` as `<Bug key> --evidence <file> --take-over`. When they decline, **Next Step** is the owner's ticket.

- `Already tracked` → add the test name and build to the existing ticket when they are missing. Do not open another fix.

- `Unknown` → handled in **Close the Loop**.

### Close the Loop

Settle every result within this run. Nothing is resumed automatically on a later build: anything this run cannot settle carries the `test-triage-review` label and goes on the **Needs Your Decision** list. Add and remove labels through the Jira REST API (`.claude/rules/jira.md`) with `PUT /rest/api/3/issue/<KEY>` and the body `{"update": {"labels": [{"add": "<label>"}]}}` (`"remove"` to remove one).

- **Success** (`Outdated test`, `Bug in portal`, or `Fixed`): record the pull request in **Next Step**. When a `Fixed` result names tests that still fail, triage them as new failures in this run (a `Product` side files a new Bug rather than reusing the returned one); the returned Bug and its pull request do not count as covering them in **Map to Existing Work**, and the `test-triage-review` label `bug-fix` added to the Bug does not put them on the list in this run. Once every one of them is routed and its route succeeds, remove the label from the Bug as the last step; otherwise leave it and put the Bug on the list.

- **Handed back** (a `bug-fix` outcome `Handed back`, or a `test-fix` `Unresolved` whose Task comment starts with `Handed back to test-triage:`): decide the side again from both sets of evidence, without keeping the old side by default, and route it once more in this run. When there is a Task (the one the handback came with, or the `claude-test-fix` Task linked to the Bug that came back), pass it as `--task` to `test-fix`; for `Already tracked`, comment the covering ticket or pull request URL on it and close it as `Won't Do`; for any other route, link it to the new ticket with the **Fix** link type and close it as `Won't Do` naming the new route. When the new side is `Test`, `Environment`, or `Other team`, comment the evidence on every open Bug that names the failure, and close the ones labelled `test-triage-filed` (for `Other team`, link each one to the owner's ticket first). When that second route succeeds, remove the `test-triage-review` label the fixing skill added, as the last step; a route that invokes no fixing skill succeeds once its ticket is filed or commented. When it is handed back again, or the new side is `Unknown`, leave the label and put the failure on the list.

- **`Unresolved` naming an open pull request** (from the duplicate check in `test-fix`, which creates no Task): mark the failure `Already tracked` with that pull request. After a handback, this counts as a successful second route: remove the `test-triage-review` label from the Task.

- **Any other result** (`Blocked on owner`, a blocked case, `No fix needed`, `Unresolved`): put the failure on the **Needs Your Decision** list with the result and its ticket. The fixing skill has already labelled the ticket `test-triage-review`. When the result names no ticket (for example, `test-fix` stopped before **Claim the Failure** created one), find or file a Task naming the test with the result, add the label, and put the failure on the list, as for `Unknown`. For `No fix needed` on a `DETERMINISTIC`, `NEAR-DETERMINISTIC`, or `NEW-DETERMINISTIC` failure, note that the local pass contradicts CI and that `Environment` is the first thing to check.

- **`Unknown` from this skill** (the side could not be decided): find or file a Task naming the test with what was tried and what would settle it, add the `test-triage-review` label, and put the failure on the list.

### Report

Write the triage table and reply with the counts per side, the three failures that affect the most tests, and anything that needs the user's decision.