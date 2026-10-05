# Helm Unit Test Order

Checks that the cases in a `helm unittest` suite sort alphabetically by their `it` description, as rule 209 in `pr-reviewer/rules` requires. Nothing else enforces that order. The suites themselves run in `ci-test-cloud-helm-chart.yaml`, so this validation only reads the files.

## Match

`^cloud/helm/[^/]+/tests/.*_test\.yaml$`

## Command

Check every changed suite:

```bash
(cd "${REPO_ROOT}" && bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}" | while IFS= read -r file
do
	[[ -f ${file} ]] || continue

	descriptions=$(command grep '^        it: ' "${file}" | sed 's/^        it: //')

	if [[ ${descriptions} != "$(echo "${descriptions}" | LC_ALL=C sort)" ]]
	then
		echo "UNSORTED ${file}"
	fi
done)
```

Every `UNSORTED` line is a FAIL. Report the file and the descriptions that sit out of order, and say that sorting the `tests` entries is the fix.

No output is a PASS. When the diff changed no suite at all, report **NOT APPLICABLE** — the regex fired on a path the command then filtered out.

The `grep` pattern is the eight space indentation a case's keys carry in these files, which is the only place `it` appears in the `helm unittest` schema. A suite that indents differently reads as empty here and passes without being checked, so say so rather than reporting a clean result when the pattern matched nothing in a file the diff changed.

## Time Estimate

~1 sec. The check reads the changed files and runs nothing.