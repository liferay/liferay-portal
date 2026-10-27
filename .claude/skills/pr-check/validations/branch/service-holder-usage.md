# Service Holder Usage

Catches hand written production Java that gets or releases a service so that the service's use count no longer follows its owner's lifetime: a service released in the call that got it, a release without a get, or a get that is never released. The first happens with `ungetService` in the method that got the service, `SystemBundleUtil.callService`, `ServiceReferenceMapperFactory.create`, a tracker that lives for one method, or a `ServiceLatch`. A use count that drops to 0 too early can dispose the provider, with everything it references, and the next get activates the provider again, while a use count that never drops keeps a provider alive that its owner no longer needs.

## Match

`\.java$ &! -test-util/|/src/test/|/src/testIntegration/|^[^/]+/test/|^portal-test`

## Command

This check is static and needs no build. It walks each changed Java file, skips generated files, and reports each added line of the branch diff that gets or releases a service, opens a tracker, or names a latch. It uses `command grep` so the diff is filtered by the system grep rather than the shell's `grep` wrapper, which some environments route to another tool with its own defaults.

A generated file carries the `@generated` Javadoc marker that Service Builder and the other code generators stamp on every file they produce (for example every `*LocalService` interface). The scan matches it case-insensitively, so the `@Generated` annotation on REST Builder output is covered too. That annotation is machine written boilerplate, not hand written service code, so any file carrying it is excluded before the scan.

Anchor that match to a Javadoc tag or an annotation rather than searching the whole file for the bare word, or a class that only mentions `@generated` in prose drops out of the scan entirely.

The scan numbers each added line with its line number in the file and keeps the lines that contain any of these:

- `ServiceLatch`, which covers `SystemBundleUtil.newServiceLatch`.
- `ServiceReferenceMapperFactory.create(` or `ServiceReferenceMapperFactory.createFromFunction(`, which wrap a mapper that gets the service, reads it, and releases it before the mapper returns.
- `ServiceTrackerFactory.open(`, `ServiceTrackerListFactory.open(`, `ServiceTrackerMapFactory.open`, `new ServiceTracker<`, or `new ServiceTracker(`, which open a tracker in code.
- `SystemBundleUtil.callService(`, which gets a service, calls it, and releases it.
- `bundleContext.getService(` on any receiver whose name ends in `bundleContext` or `BundleContext`, which gets a service from a `BundleContext`.
- `getServiceObjects(`.
- `serviceObjects.getService(` on any receiver whose name ends in `serviceObjects` or `ServiceObjects`, which gets a service from a `ServiceObjects` or a `ComponentServiceObjects`.
- `ungetService(`, the release of a get from a `BundleContext`, `ServiceObjects`, or `ComponentServiceObjects`.

A removed line is never a candidate. A bare `getService(` is one only when its receiver is a `BundleContext` or a `ServiceObjects` as matched above, since `ServiceTrackerMap.getService(key)` and `ServiceTracker.getService()` own no use count.

Each candidate prints as `<path>:<line>:<added line>`.

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

		CHANGED_LINES=$(
			git diff --unified=0 "${MERGE_BASE}...HEAD" -- "${FILE}" \
				| awk -v file="${FILE}" '
					/^@@ / {
						split($3, range, ",")

						line = substr(range[1], 2) + 0

						hunk = 1

						next
					}

					hunk && /^\+/ {
						print file ":" line ":" substr($0, 2)

						line++
					}
				'
		)

		command grep \
			--extended-regexp \
			--regexp='ServiceLatch' \
			--regexp='ServiceReferenceMapperFactory\.create(FromFunction)?\(' \
			--regexp='ServiceTrackerFactory\.open\(' \
			--regexp='ServiceTrackerListFactory\.open\(' \
			--regexp='ServiceTrackerMapFactory\.open' \
			--regexp='SystemBundleUtil\.callService\(' \
			--regexp='[Bb]undleContext\.getService\(' \
			--regexp='[Ss]erviceObjects\.getService\(' \
			--regexp='getServiceObjects\(' \
			--regexp='new[[:space:]]+ServiceTracker[<(]' \
			--regexp='ungetService\(' \
			<<< "${CHANGED_LINES}"
	done
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind, so it reports on the diff's last Java file rather than on the diff. A clean branch can end on a `command grep` that matched nothing and exits 1. A branch with a candidate in an early file that ends on a clean one also exits 1. `continue` returns 0, so a diff whose last Java file is generated exits 0 whatever preceded it. Both statuses occur on both verdicts.

Run the loop from `${REPO_ROOT}`. The changed paths are relative to the repository root, so from any directory below it, every file test fails and the check passes without scanning anything.

Use `while read` rather than a `for` over an unquoted substitution, so a path holding whitespace stays one file rather than splitting into several.

When `${FINDINGS}` is empty, the branch adds no service get, release, tracker, or latch, so the validation passes.

Otherwise, each printed line is a candidate, not a finding. Read it in its file, with the whole method around it, and check it against the known mistakes below. A candidate is not a finding when it only imports, renames, reformats, or moves existing code. A service held for its owner's lifetime is not a mistake either: one got in `addingService` and released in `removedService`, one got in `@Activate` and released in `@Deactivate`, and a tracker opened in `@Activate` and closed in `@Deactivate`.

1. **A service released in the call that got it.** The branch gets a service from a `BundleContext`, `ServiceObjects`, or `ComponentServiceObjects` and releases it before the method that got it returns, in a `finally` block or not, or calls a helper that gets, calls, and releases, such as `SystemBundleUtil.callService`. This covers getting a new instance of a service with prototype scope for each call or request.

1. **A tracker or latch for one call.** The branch opens a `ServiceTracker`, `ServiceTrackerMap`, or `ServiceTrackerList` and closes it, or drops it, before the method that opened it returns, so every service it got is released when the call ends. Waiting for a service with a `ServiceLatch` is the same mistake, since the latch closes its trackers, and releases every service it got, as soon as the last one arrives.

1. **A service got to read a value.** The branch gets a service in a tracker callback, such as a `ServiceTrackerCustomizer` method or a `ServiceReferenceMapper`, only to read a value from it, such as a key, name, type, or flag, and releases it in the same callback. `ServiceReferenceMapperFactory.create` and `ServiceReferenceMapperFactory.createFromFunction` do this for the mapper they wrap, so a mapper built with either one gets the service just to read the value it emits.

1. **A release without a get.** The branch releases a service with `ungetService` on a path that never got it, such as a tracker callback or mapper that only read a property from the reference, so the release drops a use count another holder owns.

1. **A get that is never released.** The branch gets a service from a `BundleContext`, `ServiceObjects`, or `ComponentServiceObjects` and no path releases it while its owner lives on, such as a get on every call or request, or a service a tracker callback rejects and never releases.

When any candidate repeats a known mistake, the validation fails. Report the message below to the developer with one row for each mistake, giving the path and the line as the candidate prints them, and return it as the failure note.

```markdown
**Service holder usage repeats a known mistake.** Fix each line below before requesting review. For additional questions, use #t-core-infrastructure on Slack.

| File | Line | Mistake |
| --- | --- | --- |
| <file> | <line> | <mistake> |
```

When no candidate repeats a known mistake, the validation passes.

## Time Estimate

~10-30 sec (static, no build).