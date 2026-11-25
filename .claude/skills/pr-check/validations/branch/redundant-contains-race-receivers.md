# Redundant Contains on Race Receivers

Flags an added `if` statement in hand written Java that checks a key or an element on a receiver other threads change, and then reads, adds, or removes the same key or element with a second call. The value can disappear from a `ServiceTrackerMap` or a concurrent map or set between the two calls, or another thread can add the element. A single call that reports presence through its result has no such window.

## Source Formatter Candidate

This validation stands in for a Source Formatter check. Source Formatter catches the same code at format time and in CI without a pr-check run, so the check is the intended home of this rule, and this validation is the guard until it lands. When the check lands in `modules/util/source-formatter`, delete this file and its entry in `SKILL.md`.

- Check: `JavaRedundantContainsCheck`
- Source Formatter pull request: https://github.com/ling-alan-huang/liferay-portal/pull/4433
- Ticket: LPD-117495

## Match

`\.java$`

## Command

This check is static and needs no build. It walks each changed Java file, skips generated files, collects the names the file declares or assigns as a race receiver, and prints each added line that checks a key or an element on one of those names. It uses `command grep` so each file is searched by the system grep rather than the shell's `grep` wrapper, which some environments route to another tool with its own defaults.

A race receiver is a name declared with a type that starts with `ServiceTrackerMap`, `ScopedServiceTrackerMap`, `Concurrent`, or `CopyOnWrite`, or a name assigned from `Collections.synchronized*`, `new Concurrent*`, `new CopyOnWrite*`, or `ConcurrentHashMap.newKeySet()`.

A generated file carries the `@generated` Javadoc marker that Service Builder and the other code generators stamp on every file they produce. The scan matches it case insensitively, so the `@Generated` annotation on REST Builder output is covered too, and any file carrying it is excluded before the scan. Anchor that match to a Javadoc tag or an annotation rather than searching the whole file for the bare word, or a class that only mentions `@generated` in prose drops out of the scan entirely.

```bash
FINDINGS=$(
	bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}" | while IFS= read -r FILE
	do
		[[ -f ${FILE} ]] || continue

		if command grep \
			--extended-regexp \
			--ignore-case \
			--quiet \
			--regexp='^[[:space:]]*(\*[[:space:]]*@generated|@generated\()' \
			"${FILE}"
		then
			continue
		fi

		NAMES=$(
			{
				tr '\n' ' ' < "${FILE}" \
					| command grep \
						--extended-regexp \
						--only-matching \
						--regexp='((Scoped)?ServiceTrackerMap|Concurrent[[:alnum:]_]*|CopyOnWrite[[:alnum:]_]*)(<[^;=(){}]*>)?[[:space:]]+[[:alnum:]_]+[[:space:]]*[;=]' \
					| sed -E 's/[[:space:]]*[;=]$//; s/.*[^[:alnum:]_]([[:alnum:]_]+)$/\1/'

				tr '\n' ' ' < "${FILE}" \
					| command grep \
						--extended-regexp \
						--only-matching \
						--regexp='[[:alnum:]_]+[[:space:]]*=[[:space:]]*(Collections[[:space:]]*\.[[:space:]]*(<[^;]*>[[:space:]]*)?synchronized|new[[:space:]]+(Concurrent|CopyOnWrite)|ConcurrentHashMap[[:space:]]*\.[[:space:]]*(<[^;]*>[[:space:]]*)?newKeySet)' \
					| sed -E 's/^([[:alnum:]_]+).*/\1/'
			} | sort --unique | paste -d '|' -s -
		)

		[[ -n ${NAMES} ]] || continue

		ADDED=$(
			git diff --unified=0 "${MERGE_BASE}...HEAD" -- "${FILE}" \
				| awk '
					/^@@/ {
						size = split(substr($3, 2), range, ",")

						count = (size > 1) ? range[2] : 1

						for (i = 0; i < count; i++) {
							print range[1] + i
						}
					}
				'
		)

		command grep \
			--extended-regexp \
			--line-number \
			--regexp="(^|[^[:alnum:]_.])(this\.)?(${NAMES})\.(containsKey|contains)\(" \
			--regexp="(^|[^[:alnum:]_.])(this\.)?(${NAMES})\.(get|getService)\(.*\)[[:space:]]*[!=]=[[:space:]]*null" \
			"${FILE}" \
			| while IFS=: read -r LINE TEXT
			do
				if command grep --fixed-strings --line-regexp --quiet "${LINE}" <<< "${ADDED}"
				then
					echo "${FILE}:${LINE}:${TEXT}"
				fi
			done
	done
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind.

Run the loop from `${REPO_ROOT}`. The changed paths are relative to the repository root, so from any directory below it, every file test fails and the check passes without scanning anything.

When `${FINDINGS}` is empty, the branch adds no check on a race receiver, so the validation passes.

Otherwise, each printed line is a candidate, not a finding. Read the file around the line to classify it. The receiver and the key must be the same in both calls, ignoring whitespace and a leading `this.`, and nothing between the two calls may change the receiver (`put*`, `remove*`, `clear`, `compute*`, `merge`, `replace*`, `add*`, `retainAll`, or an assignment) or assign the key. A candidate is a finding when it has one of these shapes:

- `if (x.containsKey(k))` or `if (x.get(k) != null)`, alone or joined to other conditions with `&&`, followed by `x.get(k)`, `x.getOrDefault(k, ...)`, or `x.getService(k)` anywhere in the `if` body or later in the same `&&` condition.
- A guard: `if (!x.containsKey(k))` or `if (x.get(k) == null)`, alone or as one of several `||` conditions, whose body ends in `return`, `continue`, `break`, or `throw`, with no `else`, followed later in the same block by one of the reads above.
- `if (!x.containsKey(k))` whose first statement is `x.put(k, ...)`.
- On a set: `if (!x.contains(e))` whose first statement is `x.add(e)`, `if (x.contains(e))` whose first statement is `x.remove(e)`, or a guard on `x.contains(e)` followed directly by `x.add(e);`, or on `!x.contains(e)` followed directly by `x.remove(e);`.
- On a list or a queue: `if (x.contains(e))` whose first statement is `x.remove(e)`.

Any other shape is not a finding, and neither is an `add` that comes after other work, because folding the check into that `add` would mark the element before the work runs.

Report every finding as a FAIL and return the block below as the failure note.

```markdown
**A key or an element is checked and then read or changed with a second call on a receiver that other threads change.** The value can disappear, or another thread can add the element, between the two calls. Use the single call that reports presence: a `get` or `getService` with a null check, `putIfAbsent` or `computeIfAbsent`, or the boolean result of `add` or `remove`. For additional questions, use #t-core-infrastructure on Slack.

| File | Line | Receiver | Calls |
| --- | --- | --- | --- |
| <file> | <line> | <receiver> | <check> + <operation> |
```

## Time Estimate

~10-30 sec (static, no build).