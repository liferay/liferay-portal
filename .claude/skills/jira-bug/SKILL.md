---

allowed-tools: Bash(curl *), Bash(git *), Glob, Grep, Read
argument-hint: "[commit hash or description]"
description: Create a Jira bug ticket in the LPD project through the REST API. Use when the user asks to create or file a Jira bug or LPD ticket.
name: jira-bug

---

# Create a Jira Bug in LPD

Create a bug ticket in the LPD Jira project through the REST API.

## Input

When `${ARGUMENTS}` is a commit hash, inspect the commit with `git show` to understand the fix and infer the bug it addresses. When `${ARGUMENTS}` is a free-form description, use it directly.

## Gather Information

Request any missing details from the user:

- **Summary** — short title describing the bug.
- **Steps to Reproduce** — clear, minimal steps.
- **Expected Behavior** — what should have happened.
- **Actual Behavior** — what happened instead.

## Required Fields

The LPD project requires the following fields. Apply these defaults unless the user specifies otherwise:

- **Affects Version**: `Master` (ID: `16660`).
- **Component**: Infer from the code area. Fetch the LPD project components and select the one whose name matches the relevant area or keyword.
- **Cross Cutting Properties** (`customfield_10979`): `None` (ID: `14468`). Send the value as an array: `[{"id": "14468"}]`. Jira rejects a bare object and responds with `Specify the value for Cross Cutting Properties in an array`.

## Create the Ticket

Create the issue in the LPD project with the gathered summary, description, and required fields. Author the description in Atlassian Document Format (ADF) with the following sections, in order: Description, Steps to Reproduce, Expected Behavior, Actual Behavior. Append a Fix section when a commit is referenced.

## Output

The ticket key and the browse URL: `https://liferay.atlassian.net/browse/<KEY>`.