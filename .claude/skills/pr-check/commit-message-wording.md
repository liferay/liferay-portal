# Commit Message Wording

The **Commit message wording** precondition in [SKILL.md](SKILL.md) follows these rules. It flags a commit message on the branch that reveals a vulnerability the change fixes, and notes one that names a protection the change adds. The message of a security fix reads like any other change to the same code, and the details stay in the private ticket, because a commit message stays in Git history for good and is public once the branch is pushed. The check is advisory. Nothing it finds reaches the Results Summary, so the published result never marks a security fix.

## Check

Read the message of every commit the branch itself adds, title and body, leaving out merges and the commits a merge brings in:

```bash
git log --first-parent --format="commit %h%n%B" --no-merges --no-show-signature --reverse "$(git merge-base HEAD "${BASE_REF}")..HEAD"
```

When the command fails, warn the developer that the commit messages went unchecked, and continue.

Read each message as a whole, since a neutral title can carry a body that is not. A message is flagged when any part of it describes:

- **How to exploit the defect**: steps, a request, a parameter value, a payload, a bypass, or code that reproduces it.
- **That it fixes a vulnerability**: a label such as security issue, vulnerability, exploit, or attack, even with no other detail, since the label alone marks the commit as a security fix.
- **What could reach it**: a path, endpoint, service, portlet, or role, stated as what could reach or call the defect.
- **What it allowed**: what someone could read, change, run, or become, including a class of vulnerability named together with where it was.

A message is not flagged, but noted, when it names a protection the change adds or an access rule it sets, without the details above: a permission or access check, a restriction or scope, escaping or sanitizing, input validation, or who may now do what, such as guests, roles, owners, or administrators. Such wording does not explain the defect, but it tells a reader where to look when the change fixes a vulnerability, and only the developer knows whether it does.

A message is neither flagged nor noted when it:

- Fixes an ordinary defect, or describes a change by the code it touches (its classes, methods, and calls) without naming a protection or an access rule.
- Names a product feature by its name only, such as the Security Settings tab or the Permissions screen.

These examples are fictional:

| Message | Result | Reason |
| --- | --- | --- |
| `LPD-12345 Add the Security Settings tab to the widget admin` | No | Names a product feature only |
| `LPD-12345 Add permission check to WidgetService` | Note | Names a protection |
| `LPD-12345 Allow guests to view public widgets` | Note | Says who may now do what |
| `LPD-12345 Fix a security issue in the widget preview` | Flag | That it fixes a vulnerability |
| `LPD-12345 Fix guests reading any widget by passing a crafted widget ID` | Flag | How to exploit it, and what could reach it |
| `LPD-12345 Validate the widget ID`, with the body `Calling WidgetService.getWidget with a negative ID returned the first widget of any company.` | Flag, the body | How to exploit it, and what it allowed |

For each flagged or noted text, suggest a wording that the rules above would neither flag nor note:

- Avoid a stock verb, such as update or change, and never call a change in behavior a refactor.
- Drop a flagged body sentence, or keep only what the change does.
- Keep a title under 72 characters, with the ticket prefix, in sentence case, and with no trailing period.
- Leave out each word of the `git.commit.vulnerability.keywords` list in `${REPO_ROOT}/source-formatter.properties`, since Source Format rejects a commit message that holds one.
- Name the classes and methods that changed, read from `git show <SHA>`, rather than the protection the change adds.

## Report

When nothing is flagged or noted, continue without a word. Otherwise, show the developer the block below, and ask whether to stop the run to reword or to continue. A reword costs nothing here, since no validation has run yet. Never offer to reword a commit that is already pushed, since its message is already public. Give the SHA of each commit as the log prints it, and quote only the flagged or noted text rather than the whole message. The block is shown to the developer alone, so never copy any of it into the Results Summary, the PR description, or a comment:

```markdown
**These commit messages may describe more than the change.** A flagged text says how a defect could be exploited, what could reach it, or what it allowed, or labels the change as a vulnerability fix. A noted text names a protection the change adds or who may now do what, which tells a reader where to look when the change fixes a vulnerability. A commit message is public once the branch is pushed. Reword each one before you push, or keep it when you judge it safe. This check never blocks the branch, so the choice is yours.

| Commit | Kind | Text | Reason | Suggested Wording |
| --- | --- | --- | --- | --- |
| <SHA> | <Flag or Note> | <text> | <reason> | <suggested wording> |
```