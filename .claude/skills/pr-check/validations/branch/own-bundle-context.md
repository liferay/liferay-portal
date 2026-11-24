# Own Bundle Context

Flags `SystemBundleUtil.getBundleContext()` that a branch adds to module code which has a `BundleContext` of its own. The system bundle's context hides which bundle registers a service or opens a tracker, so a registration outlives the bundle that made it and a lookup is charged to the framework instead of its caller. A component uses the `BundleContext` its `@Activate` method receives, a static utility or a plain object uses `FrameworkUtil.getBundle(<Class>.class)` and its `BundleContext`, and an integration test uses `BundleContextTestUtil.getBundleContext(<Class>.class)`.

## Source Formatter Candidate

This validation stands in for a Source Formatter check. Source Formatter catches the same code at format time and in CI without a pr-check run, so the check is the intended home of this rule, and this validation is the guard until it lands. When the check lands in `modules/util/source-formatter`, delete this file and its entry in `SKILL.md`. The check lists the same files in its `allowedFileNames` property.

- Check: `JavaSystemBundleContextCheck`

## Match

`^modules/.*\.java$ &! /src/test/|^modules/apps/portal-cache/portal-cache-test/src/testIntegration/java/com/liferay/portal/cache/internal/test/Cluster(GeneralTest|PluginCacheReplicationTest)\.java$|^modules/apps/portal-scheduler/portal-scheduler-test/src/testIntegration/java/com/liferay/portal/scheduler/internal/test/ClusterSchedulerEngineTest\.java$|^modules/apps/portal/portal-license-test/src/testIntegration/java/com/liferay/portal/license/test/BaseLicenseTestCase\.java$|^modules/apps/portal-search/portal-search-test-util/src/main/java/com/liferay/portal/search/test/util/(BaseSearchResultUtilTestCase|background/task/BaseReindexSingleIndexerBackgroundTaskExecutorTestCase|expando/BaseExpandoTestCase|mappings/LiferayFieldQueryFactoryFixture)\.java$|^modules/core/jaxws-osgi-bridge/src/main/java/com/liferay/jaxws/osgi/bridge/Provider\.java$`

## Command

This check is static and needs no build. It reads each changed Java file under `modules` that the branch keeps and reports each `SystemBundleUtil.getBundleContext()` on a line the branch adds, outside comments and string literals. The **Match** leaves out unit tests under `src/test`, which run without an OSGi framework, so the system bundle's context is the only one they have, and the files below, which keep the system bundle's context on purpose:

- `modules/apps/portal-cache/portal-cache-test/src/testIntegration/java/com/liferay/portal/cache/internal/test/ClusterGeneralTest.java`, `ClusterPluginCacheReplicationTest.java` beside it, and `modules/apps/portal-scheduler/portal-scheduler-test/src/testIntegration/java/com/liferay/portal/scheduler/internal/test/ClusterSchedulerEngineTest.java`, whose code also runs in a second portal JVM where the test bundle is not installed.
- `modules/apps/portal/portal-license-test/src/testIntegration/java/com/liferay/portal/license/test/BaseLicenseTestCase.java`, which resets the lifecycle action of the system bundle.
- `BaseExpandoTestCase.java`, `BaseSearchResultUtilTestCase.java`, `LiferayFieldQueryFactoryFixture.java`, and `BaseReindexSingleIndexerBackgroundTaskExecutorTestCase.java` under `modules/apps/portal-search/portal-search-test-util`, which unit tests also extend.
- `modules/core/jaxws-osgi-bridge/src/main/java/com/liferay/jaxws/osgi/bridge/Provider.java`, which the app server loads outside any bundle.

Each finding prints as `<path>:<line>: uses SystemBundleUtil.getBundleContext()`.

```bash
ADDED='
	/^@@/ {
		size = split(substr($3, 2), range, ",")

		count = (size > 1) ? range[2] + 0 : 1

		for (i = 0; i < count; i++) {
			print range[1] + i
		}
	}
'

SCAN='
	function removeCommentsAndLiterals(line,    c, i, j, size, text) {
		if (!block && !comment && (line !~ /["\/\047]/)) {
			return line
		}

		size = length(line)
		text = ""

		for (i = 1; i <= size; i++) {
			if (comment) {
				j = index(substr(line, i), "*/")

				if (j == 0) {
					return text
				}

				comment = 0
				i += j
			}
			else if (block) {
				j = index(substr(line, i), "\"\"\"")

				if (j == 0) {
					return text
				}

				block = 0
				i += j + 1
				text = text " "
			}
			else if (substr(line, i, 2) == "//") {
				return text
			}
			else if (substr(line, i, 2) == "/*") {
				comment = 1
				i++
			}
			else if (substr(line, i, 3) == "\"\"\"") {
				block = 1
				i += 2
			}
			else {
				c = substr(line, i, 1)

				if ((c == "\"") || (c == "\047")) {
					for (j = i + 1; (j <= size) && (substr(line, j, 1) != c); j++) {
						if (substr(line, j, 1) == "\\") {
							j++
						}
					}

					c = " "
					i = j
				}

				text = text c
			}
		}

		return text
	}

	{
		if (removeCommentsAndLiterals($0) ~ /SystemBundleUtil[[:space:]]*\.[[:space:]]*getBundleContext[[:space:]]*\(/) {
			print FNR
		}
	}
'

FINDINGS=$(
	bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}" | while IFS= read -r FILE
	do
		[[ -f ${FILE} ]] || continue

		command grep --fixed-strings --quiet 'getBundleContext' "${FILE}" || continue

		LINES=$(git diff --unified=0 "${MERGE_BASE}...HEAD" -- "${FILE}" | awk "${ADDED}")

		[[ -z ${LINES} ]] && continue

		awk "${SCAN}" "${FILE}" | while IFS= read -r LINE
		do
			if command grep --fixed-strings --line-regexp --quiet "${LINE}" <<< "${LINES}"
			then
				echo "${FILE}:${LINE}: uses SystemBundleUtil.getBundleContext()"
			fi
		done
	done
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind, so a clean branch and a branch with findings can both exit 0.

Run the command from `${REPO_ROOT}`, with the branch checked out and the working tree clean. The scan reads the files of the working tree and compares their paths with the diff, which names every path from the repository root, so from any directory below it, no path matches and the check passes without reporting anything.

Use `while read` rather than a `for` over an unquoted substitution, so a path holding whitespace stays one file rather than splitting into several.

When `${FINDINGS}` is empty, the branch adds no use of the system bundle's context to module code, so the validation passes.

Otherwise, read the code each line names. A line is not a finding when the code runs where no bundle of its own exists, such as a class that a unit test outside `src/test` also loads without an OSGi framework, or code that runs in a second portal JVM. Every other line is a finding, and the validation fails. Report the message below to the developer with one row for each finding and return it as the failure note.

```markdown
**Module code uses the system bundle's context.** Use the bundle's own `BundleContext` instead of `SystemBundleUtil.getBundleContext()`: the `BundleContext` of the `@Activate` method in a component, `FrameworkUtil.getBundle(<Class>.class)` and its `BundleContext` in a static utility or a plain object, and `BundleContextTestUtil.getBundleContext(<Class>.class)` in an integration test. For additional questions, use #t-core-infrastructure on Slack.

| File | Line | Fix |
| --- | --- | --- |
| <file> | <line> | <the replacement for this code> |
```

## Time Estimate

~1-5 sec for the command (static, no build), plus reading each line it prints.