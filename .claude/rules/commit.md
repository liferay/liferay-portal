# Commit

Follow these rules before committing any change to the repository. These rules apply to all commits.

## Format Source

Before composing the commit, always invoke the `format-source` skill to ensure all changes follow Liferay's coding standards. After it completes, any edits the formatter applied are part of what gets committed.

## Extract the Jira Ticket

The ticket ID follows the pattern `LPD-12345`, `LCD-12345`, `LRCI-1234`, and similar forms (uppercase letters, hyphen, digits). Resolve the ticket in this order:

1. **Branch Name** — extract the ticket from the current branch (e.g., branch `LPD-83847` yields ticket `LPD-83847`).

1. **Recent Commits** — when the branch name lacks a ticket, scan the last five commit messages for a ticket prefix.

1. **User Argument** — when the user supplies a ticket ID, prefer that value.

1. **Fallback** — when no ticket surfaces, prompt the user for one.

## Compose the Commit Message

Read the diff, understand the problem it solves, then compose the message. The reader already has the diff. The message adds only what the diff cannot show.

Never describe a security concern in the title or the body. Do not mention vulnerabilities, exploits, attack vectors, or missing permission checks, and do not explain how the old code could be abused.

### Title

Format: `<TICKET> <Summary of the problem solved>`

- Lead with the Jira ticket ID.
- Follow with a concise summary of the problem solved or the behavior changed, as a user or caller sees it. Do not name the mechanism (classes, methods, selectors) unless the mechanism is the point of the change.
- Use sentence case (capitalize the first word only).
- Omit a trailing period.
- Keep the full line under 72 characters, including the ticket prefix.
- When a companion Jira ticket also applies (e.g., a related subtask), append it after the first: `LPD-12345 LPD-67890 Summary`.

Examples:

- `LCD-50509 Grant ArgoCD permission to the correct namespace`
- `LPD-83357 Add validation to prevent folder changes for CMS object definitions`
- `LPD-83630 Fix typo`
- `LPD-84627 Prevent dispatch trigger loss when the Analytics admin user is missing`

### Body

Omit the body by default. Most commits are fully explained by the title plus the diff.

Add a body only when it carries something a reader cannot get from the diff:

- ASCII diagrams — a flow, a state machine, a race, or a before/after data shape.
- Behavior changes a reviewer would not expect from the title.
- Links — related tickets, commits or pull requests, upstream bugs, specifications, documentation.
- Non-obvious constraints, or alternatives that were considered and rejected, and why.
- Reproduction steps or the observed failure, when the title cannot hold them.

Never restate the diff. Do not narrate which files, classes, methods, selectors, or assertions changed, and do not walk through how a test works.

When a body is warranted:

- Do not wrap lines. Diagrams and code blocks keep their own line breaks.
- Keep it as brief as possible: one short sentence, a few short bullets, or one diagram.
- Separate it from the title with a blank line.
- Write a single point as one plain sentence. Use bullets only when describing several things, one point per bullet, rather than a paragraph of prose.

When the user provides a hint or description, incorporate it into the message.

### Example

Bad — the body retells the diff:

```
LPD-116783 Show each calendar's own color in Manage Calendars

The Color column on the Manage Calendars list rendered every row with whichever calendar's color ended up last in the CSS cascade, because each iteration of the search container emitted an <aui:style> block redefining the shared .calendar-portlet-color-box selector. Target the background-color at a per-row class derived from the calendar id so each <span> binds to its own style rule and keeps the CSP-safe form from LPD-53713.
```

Good — the title states the problem, and the body keeps only the context the diff hides:

```
LPD-116783 Show each calendar's own color in Manage Calendars

Keeps the CSP-safe <aui:style> form from LPD-53713 rather than going back to an inline style.
```