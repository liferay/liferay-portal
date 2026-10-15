# ConcurrentMapperHashMap Usage

Flags new `ConcurrentMapperHashMap` usage in hand written Java, such as `ConcurrentIdentityHashMap`, `ConcurrentReferenceKeyHashMap`, or `ConcurrentReferenceValueHashMap`. Improper `ConcurrentMapperHashMap` usage has heavy performance and memory costs, so a usage that repeats a known mistake fails for everyone, and Shuyang Zhou and the Core Infrastructure team must approve every other new case.

## Match

`\.java$`

## Command

This check is static and needs no build. It walks each changed Java file, skips generated files, and reports any newly added `ConcurrentMapperHashMap` usage in the rest. It uses `command grep` so the diff is filtered by the system grep rather than the shell's `grep` wrapper, which some environments route to another tool with its own defaults.

A generated file carries the `@generated` Javadoc marker that Service Builder and the other code generators stamp on every file they produce (for example every `*LocalService` interface). The scan matches it case-insensitively, so the `@Generated` annotation on REST Builder output is covered too. That annotation is machine-written boilerplate, not hand-written `ConcurrentMapperHashMap` code, so any file carrying it is excluded before the `ConcurrentMapperHashMap` scan.

Anchor that match to a Javadoc tag or an annotation rather than searching the whole file for the bare word, or a class that only mentions `@generated` in prose drops out of the scan entirely.

A `ConcurrentReferenceKeyHashMap` also registers with `FinalizeManager` on every write, so the scan reports each added `compute`, `computeIfAbsent`, or `merge` call on a name the file assigns a `ConcurrentReferenceKeyHashMap` to, even when the line does not name the class. When the file assigns none, that pattern falls back to the class name, which the first pattern already matches. Each finding starts with the path of its file.

```bash
FINDINGS=$(
	bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}" | while IFS= read -r FILE
	do
		[[ -f ${FILE} ]] || continue

		if command grep \
			--extended-regexp \
			--ignore-case \
			--regexp='^[[:space:]]*(\*[[:space:]]*@generated|@generated\()' \
			"${FILE}" > /dev/null
		then
			continue
		fi

		NAMES=$(
			tr '\n' ' ' < "${FILE}" \
				| command grep \
					--extended-regexp \
					--only-matching \
					--regexp='[[:alnum:]_]+[[:space:]]*=[[:space:]]*new[[:space:]]+ConcurrentReferenceKeyHashMap' \
				| command grep --extended-regexp --only-matching '^[[:alnum:]_]+' \
				| paste -d '|' -s -
		)

		git diff "${MERGE_BASE}...HEAD" -- "${FILE}" \
			| command grep --extended-regexp '^\+' \
			| command grep --extended-regexp --invert-match '^\+\+\+' \
			| command grep \
				--extended-regexp \
				--label="${FILE}" \
				--regexp='Concurrent(Identity|Mapper|ReferenceKey|ReferenceValue)HashMap' \
				--regexp="(^|[^[:alnum:]_])(${NAMES:-ConcurrentReferenceKeyHashMap})\.(compute|computeIfAbsent|merge)\(" \
				--with-filename
	done
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind, so it reports on the diff's last Java file rather than on the diff. A clean branch ends on a `command grep` that matched nothing and exits 1. A branch that adds `ConcurrentMapperHashMap` usage in an early file and ends on a clean one also exits 1. `continue` returns 0, so a diff whose last Java file is generated exits 0 whatever preceded it. Both statuses occur on both verdicts.

Run the loop from `${REPO_ROOT}`. The changed paths are relative to the repository root, so from any directory below it, every file test fails and the check passes without scanning anything.

Use `while read` rather than a `for` over an unquoted substitution, so a path holding whitespace stays one file rather than splitting into several.

When `${FINDINGS}` is empty, the branch adds no new `ConcurrentMapperHashMap` usage, so the validation passes.

Otherwise, read each finding in its file and check it against the known mistakes below. This step applies to everyone, including the logins on the whitelist. An import, or a line that only names a type, is not a mistake. Neither is a line that only renames, reformats, or moves existing code. When the branch newly constructs a `ConcurrentReferenceKeyHashMap`, also check the existing `compute`, `computeIfAbsent`, and `merge` calls on it, including those in files the branch does not change.

1. **A write on every access.** The branch calls `compute`, `computeIfAbsent`, or `merge` on a `ConcurrentReferenceKeyHashMap` without first getting `null` from `get` on the same map. Like `put` and `putIfAbsent`, these calls register the key with `FinalizeManager` every time, even when the key is present. Read with `get` first, and build the value and `putIfAbsent` it only on a miss.

1. **A key with nothing else holding it.** The branch keys a `ConcurrentReferenceKeyHashMap` or a `ConcurrentIdentityHashMap` by `String` or by a boxed primitive such as `Integer` or `Long`. A literal, an interned string, or a cached box is never collected. A key built for each call is held only by the map, so a weak reference drops it at the next garbage collection and a soft reference turns the map into an unbounded cache. Use a `ConcurrentHashMap` over a bounded key set, or keep the value on the object that owns the key.

1. **A value that holds its key.** The branch stores a value in a `ConcurrentReferenceKeyHashMap` that strongly references its key, such as a dom4j `QName` and its name, or a class loader whose parent is the key. The key is never collected, so the map is a plain map that pays for references. Use a `ConcurrentHashMap`, or keep the value on the object that owns the key.

1. **A mutable value changed in place.** The branch changes a value that is not thread safe, such as a `HashMap`, an `ArrayList`, or an array, after storing it in the map, so a reader can see it half written. Store an immutable or thread safe value.

When any finding repeats a known mistake, the validation fails for everyone and the whitelist below does not apply. Report the message below to the developer with one row for each mistake, giving the path as the finding prints it and the line number in that file, and return it as the failure note.

```markdown
**ConcurrentMapperHashMap usage repeats a known mistake.** Fix each line below before requesting review. For additional questions, use #t-core-infrastructure on Slack.

| File | Line | Mistake | Fix |
| --- | --- | --- | --- |
| <file> | <line> | <mistake> | <fix> |
```

Otherwise, resolve the GitHub login of the person running pr-check with `gh api user --jq '.login'` and check it against the whitelist below. The validation passes only when that login is on the whitelist, and fails for everyone else. It also fails when the login cannot be resolved, for example when `gh` is not authenticated, so an unknown initiator never passes. Add one login per line as the roster changes.

```
shuyangzhou
```

When the validation fails, report the message below to the developer and return it as the failure note.

```markdown
**ConcurrentMapperHashMap usage requires Core Infrastructure review.** New `ConcurrentMapperHashMap` code (`ConcurrentReferenceKeyHashMap`, `ConcurrentReferenceValueHashMap`, etc.) has heavy performance and memory implications and requires additional review. After team review is complete, please send this pull request to @liferay-core-infra and request review from @shuyangzhou. For additional questions, use #t-core-infrastructure on Slack.
```

## Time Estimate

~10-30 sec (static, no build).