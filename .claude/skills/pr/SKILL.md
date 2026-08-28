---

allowed-tools: [Bash, Glob, Grep, Read, Skill]
argument-hint: "[optional target-org/repo, message hint, or --skip-pr-check]"
description: Create a GitHub PR for the current branch. Use when the user asks to create a PR, send a PR, or invokes /pr.
name: pr

---

# Create a Pull Request

Create a GitHub PR for the current branch, transition the linked Jira tickets to review, and record the PR URL on those tickets.

## Preconditions

- At least one commit adds tests. When none do, ask the user for a rationale and refuse to proceed without one. The only exceptions are PRs with no code changes (e.g., language key updates or markdown changes).

- The current branch is a development branch, not `master` or any other protected branch.

- The `pr-check` skill must pass. Skip only when `${ARGUMENTS}` contains `--skip-pr-check`. A skip requires a reason: take it from the text following the flag when present, otherwise prompt the user for one. The reason is recorded in the **PR Check** section, which is written for a skip rather than omitted.

## Input

### Branch

The current Git branch must contain the commits ready to ship.

### Jira Tickets

A branch may span more than one ticket. Resolve the **ticket set** — every ticket the PR touches.

The ticket key follows the pattern `LPD-12345`, `LCD-12345`, `LRCI-1234`, and similar forms (uppercase letters, hyphen, digits).

Collect every distinct ticket key from the subjects of the branch's commits relative to `master`. Each subject is prefixed with its ticket (`LPD-12345 <subject>`); extract every distinct key, in commit order (oldest first). When no commit carries a ticket, prompt the user for one.

### Target Repository

The target repository defaults to `<fork-owner>/liferay-portal`. When `${ARGUMENTS}` names a different `org/repo`, use that; when it matches an alias below, expand the alias; otherwise, ask the user to choose `<fork-owner>` from one of the team forks:

- `liferay-ac`
- `liferay-appsec`
- `liferay-bpm`
- `liferay-commerce`
- `liferay-content-management`
- `liferay-core-infra`
- `liferay-database-infra`
- `liferay-devtools`
- `liferay-frontend`
- `liferay-headless`
- `liferay-page-management`
- `liferay-platform-experience`
- `liferay-search`
- `liferay-site-management`

The following short aliases resolve to a target repository:

- `brian` → `brianchandotcom/liferay-portal`

The PR head is `<github-username>:<branch-name>` (the GitHub username is read from the user's `origin` remote URL — e.g., `git@github.com:brianchandotcom/liferay-portal.git` yields `brianchandotcom`), and the base is `master`.

## Expected Output

### Pushed Branch

Push the current branch to the user's remote when it has not been pushed yet or when new local commits exist.

### Pull Request

The title is concise (under 72 characters) and prefixed with the key of the first ticket in the set:

```
LPD-83847 Fix OutOfMemoryError during batch engine import
```

The body follows this format, with one `browse` link per ticket in the set:

```markdown
https://liferay.atlassian.net/browse/TICKET-ID-1
https://liferay.atlassian.net/browse/TICKET-ID-2

## What Is Being Fixed

Explain the problem or bug that motivated the change — what was going wrong or what was missing.

## How It Is Being Fixed

Explain the approach taken across all commits. Describe the key changes and the reasoning behind the approach. Write in plain prose rather than bullet points.

## Why Are There No Tests?

This optional section is only included when the commits do not add any tests. It should contain the rationale provided by the user.

## PR Check

<pr-check Results Summary>

<!-- pr-check {"result": "<state>", "sha": "<tested-SHA>"} -->
```

The **PR Check** section is the Results Summary block emitted by the `pr-check` skill that ran as the precondition — the overall state line, the tested SHA, the table of validations, and every note appended below it, since any row can carry a note, a `PASS` included — pasted verbatim, followed by a hidden marker. The marker is an HTML comment, invisible in rendered Markdown, whose payload is a JSON object of the form `<!-- pr-check {"result": "<state>", "sha": "<tested-SHA>"} -->`, where `<state>` is `success` when the overall state is `PASS` and `failure` when it is `FAIL`, so a run carrying `NOT VERIFIED` rows records `success`, and `<tested-SHA>` is the full 40-character SHA from the Results Summary. The webhook reads the `result` and `sha` fields to apply the `pr-check` commit status to that SHA and the `pr-check - <state>` label to the PR, so this skill records no status or label itself.

When pr-check was skipped via `--skip-pr-check`, still write the section, but as a skip rather than a run. Under the same `## PR Check` heading, the body is a single `**pr-check: SKIPPED** — <reason>` line with no table, and the marker payload adds a `reason` field alongside `result` (set to `skipped`) and the PR head SHA. The JSON object keys are alphabetical (`reason`, `result`, `sha`).

```markdown
**pr-check: SKIPPED** — <reason>

<!-- pr-check {"reason": "<reason>", "result": "skipped", "sha": "<head-SHA>"} -->
```

The webhook applies the status and label when it processes the `pull_request` event for the newly opened PR, so no publish step is needed at creation. Use the `pr-check-publish` skill only to record a later pr-check run on an existing PR.

Use a direct, to-the-point style. Avoid being verbose. Present the proposed title and body to the user before submitting, and proceed once they approve.

Create the pull request with `--body-file`, or with `--body` from a quoted-heredoc variable; either keeps the marker's literal `!` off the command line, where it could otherwise trigger history expansion and corrupt the marker. Use `mktemp` for the file so it stays out of the working tree, and remove it afterward.

```bash
body_file=$(mktemp)

gh pr create \
	--base master \
	--body-file "${body_file}" \
	--head <github-username>:<branch-name> \
	--repo <target-org/repo> \
	--title "<title>"

rm "${body_file}"
```

### Transitioned Jira Tickets

Apply the steps below to **every ticket in the ticket set**, recording the outcome per ticket and continuing on failure.

For each ticket, fetch it (issue type, status, subtasks) and resolve the **target ticket** — the one whose status reflects active work and on which the PR URL is recorded:

| Ticket Type | Target |
| --- | --- |
| Bug (`10004`) | The bug itself |
| Task (`10002`) | Its Technical Task (`10153`) subtask |
| Technical Task (`10153`) | Itself |

When the target is not already in an in-progress status, transition it first:

| Target Type | Destination | Transition ID |
| --- | --- | --- |
| Bug | In Progress | `61` |
| Technical Task | In Progress | `41` |

Then transition it to review:

| Target Type | Destination | Transition ID |
| --- | --- | --- |
| Bug | In Review | `71` |
| Technical Task | In Peer Review | `31` |

When the review transition fails (for example, because the ticket is already in a later status), still proceed to record the PR URL.

Set the **Git Pull Request** field (`customfield_10201`) on the target ticket to the new PR URL.

### Existing Pull Request

This handling applies per target ticket, while recording the PR URL above.

When the **Git Pull Request** field already holds one or more PR URLs, ask the user whether the new PR **supersedes** the existing one or is **added** alongside it.

When the user chooses **supersede**:

1. Overwrite **Git Pull Request** with the new PR URL, dropping the previous value.

1. Add a comment on the previous PR, linking to the new one (for example, `Superseded by <new-pr-url>.`).

1. Close the previous PR when possible. When the user lacks permission to close it directly, add a `ci:close` comment instead, so the CI bot closes it.

When the user chooses **add**:

1. Append the new PR URL to the existing value, separating each URL with a comma and a space.

### Summary

Report back to the user with:

- Each ticket in the set, with its resulting status and link.
- The PR URL.