# Generated Workspace File

`workspaces/refresh_other_workspaces.sh` copies `liferay-sample-workspace` over every other workspace, so a change to a copied file outside the sample is silently reverted on the next refresh. Such a file may change only through a refresh, and an edit to it belongs in the sample workspace.

## Match

`.`

## Command

Skip this validation for `liferay-sample-workspace`, the source the other workspaces are regenerated from.

Build a regex from the `--exclude` patterns of the refresh script at `${SOURCE_SHA}`, rather than keeping a copy of them here, and keep the changed files it does not match. An `rsync` pattern without a slash matches a path component at any depth, which is why the regex matches after any `/` and not only at the start of the path:

```bash
EXCLUDES_REGEX=$(
	git show "${SOURCE_SHA}:workspaces/refresh_other_workspaces.sh" \
		| sed -n 's/^[[:space:]]*--exclude[[:space:]]\{1,\}\([^[:space:]\\]*\).*/\1/p' \
		| sed 's/\./\\./g; s/\*/[^\/]*/g' \
		| paste -d '|' -s -
)
```

Report **NOT VERIFIED** when `${EXCLUDES_REGEX}` is empty, naming the unreadable script, since an empty regex would mark every file as regenerated.

For every changed path of the workspace that does not match `(^|/)(${EXCLUDES_REGEX})(/|$)`, read the mode and blob that `${SOURCE_SHA}` records for the branch's copy and for the sample workspace's:

```bash
COPY=$(git -C "${BUILD_ROOT}" ls-tree "${SOURCE_SHA}" -- "<path>" | cut --fields=1)
SAMPLE=$(git -C "${BUILD_ROOT}" ls-tree "${SOURCE_SHA}" -- "../liferay-sample-workspace/<path>" | cut --fields=1)
```

Compare what git records rather than the bytes of the two files. For a symlink, git stores the path the link points to, while reading the path on disk follows the link to its target's contents, so a byte comparison never matches a symlink, although the refresh copies it as a link and leaves it unchanged. The mode also tells a symlink from a regular file that holds the same text.

Read the sample from `${SOURCE_SHA}` and never from a local branch. A local branch goes stale silently, and the comparison then reports a file as diverged because the sample moved on rather than because the branch changed anything.

Report PASS for a path whose `${SAMPLE}` is not empty and equals `${COPY}`, which is what a refresh produces. Report FAIL for a path whose two differ, and for a path with an empty `${SAMPLE}`, which the sample does not contain, since the refresh deletes it. Name each path, and say that the next refresh reverts it and that the edit belongs in `liferay-sample-workspace`.

## Time Estimate

~10 sec.