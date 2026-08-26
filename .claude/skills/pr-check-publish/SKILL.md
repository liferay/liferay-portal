---

allowed-tools: [Bash]
argument-hint: "<pr-url>"
description: Comment a pr-check run on an existing GitHub PR with a marker the webhook parses to apply the commit status and label. Use after rerunning pr-check on an open PR.
name: pr-check-publish

---

# Publish pr-check Results

Post the `pr-check` Results Summary as a comment on an existing GitHub PR, ending the comment with a hidden marker. The webhook parses that marker and applies the `pr-check` commit status to the tested SHA and the `pr-check - <state>` label to the PR — this skill never writes the status or label itself, so it works the same whether or not the user has write access on the target repository.

Newly created PRs do not need this skill: the `pr` skill writes the same Results Summary and marker into the PR description at creation, and the webhook reads it from there. Reach for this skill only to record a later run — for example after pushing more commits and rerunning `pr-check` on an open PR — where the description no longer reflects the current head.

## Input

### Pull Request

`${ARGUMENTS}` carries a PR URL of the form `https://github.com/<target-org>/liferay-portal/pull/<number>`. When missing or malformed, abort and ask the user for the URL.

### Results Summary

Use the **Results Summary** block emitted by the `pr-check` run in the current session — the overall state line, the tested SHA, the table of validations, and every note appended below it. Any row can carry a note, a `PASS` included, so carry them all verbatim. When no Results Summary is available (pr-check has not run this session), abort and ask the user to run `pr-check` first; this skill records a run, it does not perform one.

## Expected Output

### Posted Comment

Post a fresh comment on each run rather than editing a prior one, so the PR keeps a chronological record. The comment body is the Results Summary verbatim, a blank line, then the marker — an HTML comment, invisible in rendered Markdown, whose payload is a JSON object of the form `<!-- pr-check {"result": "<state>", "sha": "<tested-SHA>"} -->`, where `<state>` is `success` when the overall state is `PASS` and `failure` when it is `FAIL`, so a run carrying `NOT VERIFIED` rows records `success`, and `<tested-SHA>` is the full 40-character SHA from the Results Summary:

```markdown
**pr-check: PASS** — tested on `<tested-SHA>`

| Validation | Result |
| --- | --- |
| Source Format | PASS |
| Full Portal Build | PASS |
| Java Unit Tests | PASS |

<!-- pr-check {"result": "success", "sha": "<tested-SHA>"} -->
```

Post the body with `--body-file`, or with `--body` from a quoted-heredoc variable; either keeps the marker's literal `!` off the command line, where it could otherwise trigger history expansion and corrupt the marker. Use `mktemp` for the file so it stays out of the working tree, and remove it afterward.

```bash
comment_file=$(mktemp)

gh pr comment \
	--body-file "${comment_file}" \
	"<pr-url>"

rm "${comment_file}"
```

When the comment fails to post, surface the error — without it the webhook has nothing to parse, so the status and label will not appear.

### Summary

Report back to the user with:

- The comment URL and the tested SHA the marker records.
- That the `pr-check` commit status and `pr-check - <state>` label are applied by the webhook once it processes the comment, and so may lag the comment by a moment.