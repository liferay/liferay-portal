# Source Format

## Trigger

Always.

## Match

`.`

## Command

Run the SDK setup, then run the source formatter in `current-branch` mode:

```bash
(cd "${REPO_ROOT}" && ant setup-sdk)
(cd "${REPO_ROOT}/portal-impl" && ANT_OPTS="-Xmx2560m" ant format-source-current-branch -Dvalidate.commit.messages=true)
```

A nonzero `ant setup-sdk` is an environment failure rather than a formatting one, so report **NOT VERIFIED** and stop rather than formatting a tree that is not set up. That applies to `setup-sdk` alone. A nonzero formatter is a finding, and never read it as an environment failure, however much it looks like one: an unfixable violation surfaces as `Unable to execute Gradle task: :portalYarnFormatCurrentBranch` on the last line or two, which is indistinguishable from a broken toolchain and is why the violations have to be read from the middle of the log. The accompanying `finished with non-zero exit value 1` line sits further up, inside the stack trace, and names the node binary by its full path rather than as `command node`, so search it as a substring or not at all.

Two formatters run here and they fail differently, at opposite ends of the log. Search for both, and do not read a clean Java side as a pass, since a run where every `SourceCheck` violation was fixed and committed can still be failing on the yarn side:

```bash
command grep --fixed-strings 'SourceCheck:' "${LOG}"
command grep --fixed-strings 'ERROR: Dependency' "${LOG}"
```

Neither pattern is anchored, because Ant prefixes the yarn output with `[exec]` and indents it, so a search for `^ERROR:` matches nothing at all. The Java violations arrive in the first dozen lines, the yarn ones about three fifths of the way down, and the terminal Gradle error on the last line or two, so a tail of any reasonable size shows the failure while hiding what caused it.

The yarn side does not always run. `portal-impl/build.xml` sets `skip.node.task` when no changed file matches the frontend regex in `build.properties`, which covers `js`, `json`, `jsp`, `jspf`, `scss`, `ts`, and `tsx`, so a diff of none of those skips `downloadNode`, `yarnInstall` and the yarn formatter entirely. An empty yarn search on such a diff means the formatter never ran rather than that it ran clean, and neither is a failure. Say which of the two happened.

`-Dvalidate.commit.messages=true` is passed explicitly because `format-source-current-branch` never sets it and only the `format-source` target does, so the commit message rules CI enforces go unchecked without it. The flag produces no output of its own, and a clean check and a check that never ran print alike, so there is nothing in a normal run to confirm from. When it matters, rerun under `ant -v`, which echoes the `SourceFormatter` argv and shows `validate.commit.messages=true` among it.

## Autocommit

When `git status --porcelain` is nonempty after the formatter (fixable subset applied to the working tree), stage the tracked modifications with `git add --update` and create a commit titled `<TICKET> SF`.

Use `--update` rather than `--all`. The formatter edits files that already exist, so `--update` covers everything it does, while `--all` also sweeps in whatever else is untracked in the tree at that moment. The run reaches `ant setup-sdk`, `downloadNode`, and `yarnInstall` on the way, and those stay out of the commit only because `.gitignore` happens to cover them. List the staged files against the paths the formatter named before committing, and report any it did not.

When the commit fails, record the failure and continue to the next validation.

Unfixable violations exit nonzero and fail this validation; surface them from the formatter output in the final report. When both fixable and unfixable violations occur in one run, the autocommit captures the fixable subset; the unfixable subset still blocks the run.

## Notes

Run **after** all drift validations so the formatter sees the regenerated tree.

## Time Estimate

~2-4 min. It scales with the number of changed files.