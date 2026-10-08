# PR Reviewer

Reviews the branch diff against the rules in `pr-reviewer/rules`, which hold the conventions the source formatter does not enforce, and applies the fixes the reviewer returns. The reviewer drops generated files, frontend sources, generated locale files, and lock files itself, and exits at once when nothing is left, so a diff of only those costs one command.

## Match

`.`

## Command

Review the branch diff against the rules in `pr-reviewer/rules`:

```bash
LOG=$(mktemp)
LOG_ERROR=$(mktemp)

(cd "${REPO_ROOT}" && pr-reviewer/review.sh --json "${MERGE_BASE}..HEAD" > "${LOG}" 2> "${LOG_ERROR}")
```

The exit status is the verdict. `0` is a clean diff, so report **PASS** and quote the `reviewed_files` count. `2` means the reviewer could not run, most often because the `claude` CLI is not on the PATH, so report **NOT VERIFIED** and quote `${LOG_ERROR}`. `1` means violations, and the log holds them as JSON, one object per violation with `file`, `line`, `rule`, `message`, and `fix`.

Apply the fixes. For each violation, read the rule file `pr-reviewer/rules/<rule>-*.md` and the code around the line, then make the change the `fix` describes. A `fix` that starts with `Verify:` is one the reviewer was not sure about, so confirm it against the rule and the surrounding code first, and leave the code alone when the rule does not apply. Skip a `fix` that SourceFormatter would revert; the reviewer marks those with `SourceFormatter is authoritative`. Never edit a file that carries an `@generated` marker.

When at least one fix was applied, commit as the **Autocommit** section says, then review once more:

```bash
LOG_RERUN=$(mktemp)
LOG_RERUN_ERROR=$(mktemp)

(cd "${REPO_ROOT}" && pr-reviewer/review.sh --json "${MERGE_BASE}..HEAD" > "${LOG_RERUN}" 2> "${LOG_RERUN_ERROR}")
```

Report **PASS** when the rerun exits `0`. Report **NOT VERIFIED** when it exits `2`, quote `${LOG_RERUN_ERROR}`, and say in the note that the fixes in the `<TICKET> Apply pr-reviewer rules` commit were not reviewed again. Keep that commit, since each fix in it was checked against its rule before it was applied. When the rerun exits `1`, judge each violation it flags the way the first round does, by reading the rule file and the code around the line, but apply no fix. A violation you already dismissed in the same file under the same rule stays dismissed. Report **FAIL** when at least one violation still applies, and return those violations as the note below. Report **PASS** when you dismissed every one. Do not start a second round of fixes, since a violation the first round did not resolve needs the developer's judgment, and a loop of automated rounds can chase a false positive indefinitely.

```markdown
**The pr-reviewer rules still flag this branch.** Apply each fix below, or leave the code as it is when a violation is a false positive and say so in the PR description.

| File | Line | Rule | Violation | Fix |
| --- | --- | --- | --- | --- |
| <file> | <line> | <rule> | <message> | <fix> |
```

When no fix was applied, skip the rerun. Report **FAIL** with the same note when at least one violation applies and could not be fixed, and report **PASS** when you dismissed every violation as a false positive.

Whenever you dismissed a violation, return the dismissed violations as the note below, with a **PASS** as well as a **FAIL**, so that the developer can confirm each one:

```markdown
**The pr-check judged these pr-reviewer violations to be false positives.** Confirm each one, and say so in the PR description.

| File | Line | Rule | Violation | Reason |
| --- | --- | --- | --- | --- |
| <file> | <line> | <rule> | <message> | <why the rule does not apply> |
```

## Autocommit

When `git status --porcelain` is nonempty after the fixes, stage the tracked modifications with `git add --update` and create a commit titled `<TICKET> Apply pr-reviewer rules`.

## Notes

Run **after** the drift validations, so the reviewer sees the regenerated tree, and **before** Source Format, Workspace Source Format, and Go Source Format, so that the formatters format the fixes. That holds in `liferay-portal-ee` too, which has no `portal-impl` to run `format-source-current-branch` from.

The reviewer calls the model once per rule group, in parallel, and every group reads the rules that apply to it in full, so the cost is mostly fixed per run rather than per line.

## Time Estimate

~1-3 min for each review, and it reviews twice when it applies fixes.