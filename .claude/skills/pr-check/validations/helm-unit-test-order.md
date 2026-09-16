# Helm Unit Test Order

## Trigger

A `helm unittest` suite changed: a `*_test.yaml` file under a chart's `tests` directory in `cloud/helm`.

Cases under `tests` sort alphabetically by their `it` description in case sensitive ASCII order, which is Rule 48 in the `format-source` skill and rule 202 in `pr-reviewer/rules`. Nothing enforces it: `helm unittest` runs the cases in whatever order it finds them, and the source formatter has no check for this shape, so an unsorted suite passes every other gate.

The suites themselves are not run here. `ci-test-cloud-helm-chart.yaml` already runs `cloud/scripts/tests/run_helm_tests.sh` on every push touching `cloud/helm/**` or `cloud/scripts/tests/**`, and that coverage is enough — this validation reads the files and runs no chart.

## Match

`^cloud/helm/[^/]+/tests/.*_test\.yaml$`

## Command

Check every changed suite:

```bash
(cd "${REPO_ROOT}" && for file in $(git diff --name-only "$(git merge-base HEAD master)...HEAD" -- 'cloud/helm/*/tests/*_test.yaml')
do
	descriptions=$(command grep '^        it: ' "${file}" | sed 's/^        it: //')

	if [[ ${descriptions} != "$(echo "${descriptions}" | LC_ALL=C sort)" ]]
	then
		echo "UNSORTED ${file}"
	fi
done)
```

Every `UNSORTED` line is a FAIL. Report the file and the descriptions that sit out of order, and say that sorting the `tests` entries is the fix.

No output is a PASS. When the diff changed no suite at all, report **NOT VERIFIED** — the regex fired on a path the command then filtered out.

The `grep` pattern is the eight space indentation a case's keys carry in these files, which is the only place `it` appears in the `helm unittest` schema. A suite that indents differently reads as empty here and passes without being checked, so say so rather than reporting a clean result when the pattern matched nothing in a file the diff changed.

## Time Estimate

~1 sec. The check reads the changed files and runs nothing.