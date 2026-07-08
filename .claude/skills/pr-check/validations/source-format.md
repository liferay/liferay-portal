# Source Format

## Trigger

Always.

## Match

`.`

## Command

Run the SDK setup, then run the source formatter in `current-branch` mode:

```bash
(cd "${REPO_ROOT}" && ant setup-sdk)
(cd "${REPO_ROOT}/portal-impl" && ANT_OPTS="-Xmx2560m" ant format-source-current-branch)
```

## Autocommit

When `git status --porcelain` is nonempty after the formatter (fixable subset applied to the working tree), stage all changes (`git add --all`) and create a commit titled `<TICKET> SF`.

When the commit fails, record the failure and continue to the next validation.

Unfixable violations exit nonzero and fail this validation; surface them from the formatter output in the final report. When both fixable and unfixable violations occur in one run, the autocommit captures the fixable subset; the unfixable subset still blocks the run.

## Notes

Run **after** all drift validations so the formatter sees the regenerated tree.

## Time Estimate

~2-4 min, scaling with the number of changed files.