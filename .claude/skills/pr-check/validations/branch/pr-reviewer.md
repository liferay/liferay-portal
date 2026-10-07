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

(cd "${REPO_ROOT}" && pr-reviewer/review.sh --json "${MERGE_BASE}..HEAD" > "${LOG_RERUN}" 2>&1)
```

Report **PASS** when the rerun exits `0`. Report **FAIL** when it still finds violations, and return them as the note below. Do not start a second round of fixes, since a violation the first round did not resolve needs the developer's judgment, and a loop of automated rounds can chase a false positive indefinitely.

```markdown
**The pr-reviewer rules still flag this branch.** Apply each fix below, or leave the code as it is when a violation is a false positive and say so in the PR description.

| File | Line | Rule | Violation | Fix |
| --- | --- | --- | --- | --- |
| <file> | <line> | <rule> | <message> | <fix> |
```

Report **FAIL** with the same note when the reviewer found violations and none could be applied.

## Autocommit

When `git status --porcelain` is nonempty after the fixes, stage the tracked modifications with `git add --update` and create a commit titled `<TICKET> Apply pr-reviewer rules`.

## Notes

Run **after** the drift validations, so the reviewer sees the regenerated tree, and **before** Source Format, Workspace Source Format, and Go Source Format, so that the formatters format the fixes. That holds in `liferay-portal-ee` too, which has no `portal-impl` to run `format-source-current-branch` from.

The reviewer calls the model once per rule group, in parallel, and every group reads the rules that apply to it in full, so the cost is mostly fixed per run rather than per line.

## Time Estimate

~1-3 min for each review, and it reviews twice when it applies fixes.