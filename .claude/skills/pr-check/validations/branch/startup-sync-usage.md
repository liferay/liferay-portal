# Startup Sync Usage

Flags changes to deferred startup work in hand written Java: an added or removed `DependencyManagerSyncUtil`, `InitialRequestSyncUtil`, or `InitialRequestPortalInstanceLifecycleListener` usage, a change inside the body of a callable or task registered with either of them, and a change to the `@Deactivate` method of a class that registers one. Registered work runs after the component that registered it has returned from activation, when `DependencyManagerSyncUtil.sync()` finishes startup or on the first request, so a mistake in it breaks the startup order or hides a component restarting during startup. A change that repeats a known mistake fails for everyone, and Shuyang Zhou and the Core Infrastructure team must approve every other change.

## Match

`\.java$`

## Command

This check is static and needs no build. It walks each changed Java file, skips generated files, and reports three kinds of findings in the rest. It uses `command grep` so the diff is filtered by the system grep rather than the shell's `grep` wrapper, which some environments route to another tool with its own defaults.

- An added or removed line that names `DependencyManagerSyncUtil`, `InitialRequestSyncUtil`, or `InitialRequestPortalInstanceLifecycleListener`, whose subclasses run their work for each company through `InitialRequestSyncUtil`. This covers a new, moved, or removed usage, along with its imports and test references. Each prints as `<path>:<diff line>`, with the leading `+` or `-` of the diff line.
- A changed line inside the body of a registered callable or task. The scan finds each `registerSyncCallable(` and `registerSyncFutureTask(` call of either utility in the file before and after the branch, and follows its parentheses to the end of the call, so the whole of an inline lambda, an anonymous class, or a wrapping `FutureTask` or `CompanyInheritableThreadLocalCallable` counts as the body. Each hunk that adds a line inside a body prints as `<path>:<line>: changes the body registered at line <line>`.
- A changed line inside a `@Deactivate` method of a file that registers a callable or task, which the scan follows by its braces. Each hunk that adds a line inside one prints as `<path>:<line>: changes the deactivate method at line <line>`.

The first `<line>` in either finding is the first line of the hunk, which can sit above the body or the method. A hunk whose added lines fall outside every body and `@Deactivate` method, but whose removed lines fall inside one, prints the same way, with `, both counted before the branch` at the end, since its line numbers count the file before the branch. A registration that passes a variable or a method reference names its body elsewhere in the file, so a branch that changes that file prints `<path>: changes the file of the registration at line <line>, which names its body elsewhere` once.

A generated file carries the `@generated` Javadoc marker that Service Builder and the other code generators stamp on every file they produce (for example every `*LocalService` interface). The scan matches it case-insensitively, so the `@Generated` annotation on REST Builder output is covered too. That annotation is machine written boilerplate, not hand written startup code, so any file carrying it is excluded before the scan. A file the branch deletes is read from the merge base.

Anchor that match to a Javadoc tag or an annotation rather than searching the whole file for the bare word, or a class that only mentions `@generated` in prose drops out of the scan entirely.

```bash
RANGES='
	/(DependencyManagerSyncUtil|InitialRequestSyncUtil)\.registerSync(Callable|FutureTask)\(/ && !inside {
		depth = 0
		inline = 0
		inside = 1
		start = NR
		text = substr($0, index($0, ".registerSync"))
	}

	inside {
		if (NR != start) {
			text = $0
		}

		gsub(/"([^"\\]|\\.)*"/, "", text)

		if (text ~ /->|\{/) {
			inline = 1
		}

		depth += gsub(/\(/, "", text) - gsub(/\)/, "", text)

		if (depth <= 0) {
			print start, NR, inline

			inside = 0
			registrations++
		}
	}

	/^[[:space:]]*@Deactivate/ {
		braces = 0
		deactivate = NR
		opened = 0
	}

	deactivate {
		text = $0

		gsub(/"([^"\\]|\\.)*"/, "", text)

		count = gsub(/\{/, "", text)

		if (count > 0) {
			opened = 1
		}

		braces += count - gsub(/\}/, "", text)

		if (opened && (braces <= 0)) {
			deactivates[++size] = deactivate " " NR " 2"

			deactivate = 0
		}
	}

	END {
		if (registrations > 0) {
			for (i = 1; i <= size; i++) {
				print deactivates[i]
			}
		}
	}
'

HUNKS='
	function find(ranges, first, count,    i, list, pair, size) {
		if (count == 0) {
			return ""
		}

		size = split(ranges, list, ";")

		for (i = 1; i <= size; i++) {
			split(list[i], pair, " ")

			if ((pair[3] > 0) && (first <= pair[2]) && ((first + count - 1) >= pair[1])) {
				return list[i]
			}
		}

		return ""
	}

	function indirect(ranges,    i, list, pair, size) {
		size = split(ranges, list, ";")

		for (i = 1; i <= size; i++) {
			split(list[i], pair, " ")

			if (pair[3] == 0) {
				return pair[1]
			}
		}

		return 0
	}

	function report(line, range, suffix,    pair) {
		split(range, pair, " ")

		if (pair[3] == 1) {
			print file ":" line ": changes the body registered at line " pair[1] suffix
		}
		else {
			print file ":" line ": changes the deactivate method at line " pair[1] suffix
		}
	}

	{
		split(substr($2, 2), removed, ",")
		split(substr($3, 2), added, ",")

		if (!(2 in removed)) {
			removed[2] = 1
		}

		if (!(2 in added)) {
			added[2] = 1
		}

		range = find(head, added[1], added[2])

		if (range != "") {
			report(added[1], range, "")
		}
		else {
			range = find(base, removed[1], removed[2])

			if (range != "") {
				report(removed[1], range, ", both counted before the branch")
			}
		}
	}

	END {
		start = indirect(head)

		if ((NR > 0) && (start > 0)) {
			print file ": changes the file of the registration at line " start ", which names its body elsewhere"
		}
	}
'

FINDINGS=$(
	bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}" | while IFS= read -r FILE
	do
		if [[ -f ${FILE} ]]
		then
			SOURCE=$(cat "${FILE}")
		else
			SOURCE=$(git show "${MERGE_BASE}:${FILE}")
		fi

		if command grep \
			--extended-regexp \
			--ignore-case \
			--quiet \
			--regexp='^[[:space:]]*(\*[[:space:]]*@generated|@generated\()' \
			<<< "${SOURCE}"
		then
			continue
		fi

		git diff "${MERGE_BASE}...HEAD" -- "${FILE}" \
			| command grep --extended-regexp '^[+-]' \
			| command grep --extended-regexp --invert-match '^(\+\+\+|---) ' \
			| command grep \
				--extended-regexp \
				--label="${FILE}" \
				--regexp='(DependencyManagerSyncUtil|InitialRequestPortalInstanceLifecycleListener|InitialRequestSyncUtil)' \
				--with-filename

		BASE_RANGES=$(git show "${MERGE_BASE}:${FILE}" 2> /dev/null | awk "${RANGES}" | paste -d ';' -s -)
		HEAD_RANGES=""

		if [[ -f ${FILE} ]]
		then
			HEAD_RANGES=$(awk "${RANGES}" "${FILE}" | paste -d ';' -s -)
		fi

		if [[ -z ${BASE_RANGES}${HEAD_RANGES} ]]
		then
			continue
		fi

		git diff --unified=0 "${MERGE_BASE}...HEAD" -- "${FILE}" \
			| command grep '^@@' \
			| awk \
				-v base="${BASE_RANGES}" \
				-v file="${FILE}" \
				-v head="${HEAD_RANGES}" \
				"${HUNKS}"
	done
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind, so it reports on the diff's last Java file rather than on the diff. A clean branch can end on a `command grep` that matched nothing and exits 1, or on a `continue` that returns 0, and so can a branch with findings in an earlier file. Both statuses occur on both verdicts.

Run the loop from `${REPO_ROOT}`. The changed paths are relative to the repository root, so from any directory below it, every file test fails, the diff of every file comes back empty, and the check passes without finding anything.

Use `while read` rather than a `for` over an unquoted substitution, so a path holding whitespace stays one file rather than splitting into several.

When `${FINDINGS}` is empty, the branch changes no deferred startup work, so the validation passes.

Otherwise, read each finding in its file and check it against the known mistakes below. This step applies to everyone, including the logins on the whitelist. An import, or a line that only renames, reformats, or moves existing code, is not a mistake.

1. **A guard against deactivation.** The branch makes registered work, or the `@Deactivate` method of the class that registers it, skip, end early, or tolerate the deactivation of the component before the work has run, such as a flag that `@Deactivate` sets and the work reads, a check in `@Deactivate` for a field that only the work sets, a check of the `BundleContext` or the bundle state, a lock or `synchronized` block that the work shares with `@Deactivate`, or a catch of `IllegalStateException` around the work. Startup finishes at the end of `DependencyManagerSyncUtil.sync()`, and no component should be deactivated before then, so a guard only hides a component that restarts during startup.

When any finding repeats a known mistake, the validation fails for everyone and the whitelist below does not apply. Report the message below to the developer with one row for each mistake, giving the path as the finding prints it and the line number in that file, and return it as the failure note.

```markdown
**Startup sync usage repeats a known mistake.** Fix each line below before requesting review. For additional questions, use #t-core-infrastructure on Slack.

| File | Line | Mistake |
| --- | --- | --- |
| <file> | <line> | <mistake> |
```

Otherwise, resolve the GitHub login of the person running pr-check with `gh api user --jq '.login'` and check it against the whitelist below. The validation passes only when that login is on the whitelist, and fails for everyone else. It also fails when the login cannot be resolved, for example when `gh` is not authenticated, so an unknown initiator never passes. Add one login per line as the roster changes.

```
shuyangzhou
```

When the validation fails, report the message below to the developer and return it as the failure note.

```markdown
**Startup sync usage requires Core Infrastructure review.** Work deferred with `DependencyManagerSyncUtil`, `InitialRequestSyncUtil`, or `InitialRequestPortalInstanceLifecycleListener` runs outside the activation of the component that registers it and decides the startup order, so every change to it requires additional review. After team review is complete, please send this pull request to @liferay-core-infra and request review from @shuyangzhou. For additional questions, use #t-core-infrastructure on Slack.
```

## Time Estimate

~10-30 sec (static, no build).