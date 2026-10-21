# CacheField Usage

Catches `@CacheField` usage in hand written code that repeats a known mistake. A `@CacheField` value is computed once and shared through the entity cache with every reader of the entity, so a value computed from the wrong data, left stale, changed in place, or computed from unsaved state reaches every other request.

## Match

`\.(ftl|java|jsp|jspf)$|/service\.xml$ &! /src/test/|/src/testIntegration/|^modules/util/portal-tools-service-builder-test`

## Command

This check is static and needs no build. It reports candidate lines, and every candidate is then read in its file and checked against the known mistakes below. It uses `command grep` so the diff is filtered by the system grep rather than the shell's `grep` wrapper, which some environments route to another tool with its own defaults.

The scan first reads the `@CacheField` declarations in the hand written model `*Impl.java` files of the tree, leaving out test sources and the Service Builder test bed modules, whose cache fields only exercise the generator. From each declaration it builds the accessor names that Service Builder generates: the `methodName` attribute when the annotation sets one, and otherwise the field name without its leading underscore and with its first letter in upper case, so `_fooBar` gives `FooBar`. A getter takes `get` and a setter takes `set`, and the scan accepts both prefixes on every name. A field whose type is a primitive, a boxed primitive, or `String` gives no accessor names, since its value cannot be changed in place. An empty list falls back to a pattern no source line matches. It also collects the source setters, which are the setters a declaring `*Impl.java` overrides to reset one of its cache fields, since a call to one writes a column a cache field is computed from. A write to a source column of a primitive, boxed primitive, or `String` field is still found through these, but the field's own setter is left out of them.

It then walks each changed file and skips generated files. It numbers each added line of the branch diff, and each removed line that calls `scratchClone()`, by its line in the file, and keeps these candidates:

- Every added line in a file that declares a `@CacheField`, which covers the annotation itself, the lazy getters, the resets, and the source setters.
- An added line in any other file that names a cache field, as `CacheField` or `cacheField`, which covers the annotation and Service Builder's cache field generation in `ServiceBuilder.java` and its templates.
- An added line in any other file that calls a cache field accessor or a source setter.
- An added `change-tracking-enabled="true"` line in a `service.xml`, since publishing a publication then writes the rows of that entity in production.
- An added or removed `scratchClone()` call, since a removed one lets a changed instance publish its cache fields, and an added one has no other purpose than to stop that.

A generated file carries the `@generated` Javadoc marker that Service Builder and the other code generators stamp on every file they produce (for example every `*LocalService` interface). The scan matches it case-insensitively, so the `@Generated` annotation on REST Builder output is covered too. That annotation is machine written boilerplate, not hand written `@CacheField` code, so any file carrying it is excluded before the scan. FreeMarker templates are the exception: Service Builder's templates carry the marker for the files they generate but are themselves hand written, so they are scanned.

Anchor that match to a Javadoc tag or an annotation rather than searching the whole file for the bare word, or a class that only mentions `@generated` in prose drops out of the scan entirely.

Each candidate prints as `<path>:<line>:<added line>`, with `(removed) ` before the text of a removed line.

```bash
CACHE_FIELD_FILES=$(
	git grep \
		--files-with-matches \
		--fixed-strings \
		'@CacheField' \
		-- \
		'*Impl.java' \
		':!*/src/test/*' \
		':!*/src/testIntegration/*' \
		':!modules/util/portal-tools-service-builder-test*'
)

ACCESSORS=$(
	echo "${CACHE_FIELD_FILES}" | while IFS= read -r FILE
	do
		[[ -n ${FILE} ]] || continue

		awk '
			/@CacheField/ {
				annotation = ""
				inside = 1
			}

			inside {
				annotation = annotation " " $0
			}

			inside && /;[[:space:]]*$/ {
				declaration = $0

				sub(/[[:space:]]*(=.*)?;[[:space:]]*$/, "", declaration)

				count = split(declaration, words, /[[:space:]]+/)

				inside = 0

				if (words[count - 1] ~ /^(boolean|byte|char|double|float|int|long|short|Boolean|Byte|Character|Double|Float|Integer|Long|Short|String)$/) {
					next
				}

				name = words[count]

				sub(/^_/, "", name)

				name = toupper(substr(name, 1, 1)) substr(name, 2)

				if (match(annotation, /methodName[[:space:]]*=[[:space:]]*"[^"]+"/)) {
					name = substr(annotation, RSTART, RLENGTH)

					sub(/^[^"]*"/, "", name)
					sub(/"$/, "", name)
				}

				print name
			}
		' "${FILE}"
	done | sort --unique | paste -d '|' -s -
)

SOURCE_SETTERS=$(
	echo "${CACHE_FIELD_FILES}" | while IFS= read -r FILE
	do
		[[ -n ${FILE} ]] || continue

		awk '
			FNR == NR {
				if (/@CacheField/) {
					annotation = ""
					inside = 1
				}

				if (inside) {
					annotation = annotation " " $0
				}

				if (inside && /;[[:space:]]*$/) {
					declaration = $0

					sub(/[[:space:]]*(=.*)?;[[:space:]]*$/, "", declaration)

					count = split(declaration, words, /[[:space:]]+/)

					fields[words[count]] = 1

					if (words[count - 1] ~ /^(boolean|byte|char|double|float|int|long|short|Boolean|Byte|Character|Double|Float|Integer|Long|Short|String)$/) {
						name = words[count]

						sub(/^_/, "", name)

						name = toupper(substr(name, 1, 1)) substr(name, 2)

						if (match(annotation, /methodName[[:space:]]*=[[:space:]]*"[^"]+"/)) {
							name = substr(annotation, RSTART, RLENGTH)

							sub(/^[^"]*"/, "", name)
							sub(/"$/, "", name)
						}

						ownSetters["set" name] = 1
					}

					inside = 0
				}

				next
			}

			/^\t(public|protected|private)[^=;]*\(/ {
				method = $0

				sub(/\(.*/, "", method)
				sub(/.*[[:space:]]/, "", method)
			}

			/^\t}/ {
				method = ""
			}

			method ~ /^set/ && !(method in ownSetters) {
				for (field in fields) {
					if (index($0, field " = ") > 0) {
						print method
					}
				}
			}
		' "${FILE}" "${FILE}"
	done | sort --unique | paste -d '|' -s -
)

FINDINGS=$(
	bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}" | while IFS= read -r FILE
	do
		[[ -f ${FILE} ]] || continue

		if [[ ${FILE} != *.ftl ]] &&
			command grep \
				--extended-regexp \
				--ignore-case \
				--regexp='^[[:space:]]*(\*[[:space:]]*@generated|@generated\()' \
				"${FILE}" > /dev/null
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

						next
					}

					hunk && /^-/ && /scratchClone\(/ {
						print file ":" line ":(removed) " substr($0, 2)
					}
				'
		)

		[[ -n ${CHANGED_LINES} ]] || continue

		if command grep --fixed-strings --quiet '@CacheField' "${FILE}"
		then
			echo "${CHANGED_LINES}"

			continue
		fi

		echo "${CHANGED_LINES}" \
			| command grep \
				--extended-regexp \
				--regexp='[Cc]ache[Ff]ield' \
				--regexp='change-tracking-enabled="true"' \
				--regexp='scratchClone\(' \
				--regexp="(\.|::)(get|set)(${ACCESSORS:-[^[:print:]]})([^[:alnum:]_]|$)" \
				--regexp="(\.|::)(${SOURCE_SETTERS:-[^[:print:]]})([^[:alnum:]_]|$)"
	done
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind, so it reports on the diff's last file rather than on the diff. A clean branch ends on a `command grep` that matched nothing and exits 1. A branch with a candidate in an early file that ends on a clean one also exits 1. `continue` returns 0, so a diff whose last file is generated exits 0 whatever preceded it. Both statuses occur on both verdicts.

Run the whole block from `${REPO_ROOT}`. The changed paths and the `git grep` over the declarations are relative to the repository root, so from any directory below it, every file test fails and the check passes without scanning anything.

Use `while read` rather than a `for` over an unquoted substitution, so a path holding whitespace stays one file rather than splitting into several.

When `${FINDINGS}` is empty, the branch touches no `@CacheField` usage, so the validation passes.

Otherwise, each printed line is a candidate, not a finding. Read it in its file and check it against the known mistakes below. A candidate is not a finding when it only imports, renames, reformats, or moves existing code, when it sits in a declaring file and has nothing to do with its cache fields, or when it calls a method on another type that shares a name with a cache field accessor or a source setter, such as `getDDMForm()` on a form instance or `setSettings()` on another model. In a declaring file, read the cache field getters and the setters of the columns they read, not only the added lines, since a mistake often lies in what the branch left out. For a call, follow the instance back to where the method got it, and when the candidate returns the object a cache field getter returns, read the callers of its method.

1. **A value read from outside the row.** The branch adds a cache field, or changes its getter, so that its value depends on anything other than the entity's own columns or data that cannot change for the entity's lifetime. That covers what the getter reads and what the code it calls reads:
	- another table or entity, through a `*LocalService`, `*LocalServiceUtil`, persistence or finder call, or the getters of another model, such as the fragment entry of a link, the virtual host rows of a company or layout set, or the user groups of a user;
	- a registry, a service tracker, or a `Snapshot` whose service answers from such data;
	- configuration, properties, portlet preferences, or company or group settings such as the enabled languages;
	- the calling thread or request: `CompanyThreadLocal`, `LocaleThreadLocal`, `PrincipalThreadLocal`, `PermissionThreadLocal`, `ServiceContextThreadLocal`, `LocaleUtil.getDefault()`, a language ID lookup that validates against the company's languages such as `LocaleUtil.fromLanguageId(languageId)`, a deserializer that validates against them, or `new LocalizedValue()` taking the thread's default locale.

	Setting `permanent = true` on a field whose getter reads such data is the same mistake.

1. **A source change that does not reset the field.** The branch leaves a cache field set when a column its getter reads changes. A setter of that column, overridden in the `*Impl.java` or generated, does not reset the field, such as a `setDefinition` that resets the form but not the fields map built from it. The same goes for a new write path that bypasses the resetting setter, such as turning on change tracking for the entity or for a table its getter reads, since publishing a publication then writes production rows without resetting the field, and for `permanent = true` on a field whose source setters do not all reset it.

1. **A shared value changed in place.** The branch changes the object a cache field getter returns, or passes or returns it to code that does: a setter call on it, a `put`, `remove`, `add`, `addAll`, or `clear` on a map, list, set, or JSON object reached through it, or a write into an array it returns, such as escaping the text values of the editable values a link returns, or writing a URL into a configuration a structure item read from its link. The object is shared with every reader through the entity cache, so the change reaches every one of them.

1. **A value published from a modified instance.** The branch changes a model instance it got from persistence, the entity cache, or a service, does not save it, and then reads one of its cache field getters, or hands it to code that does, such as rendering, processing, validation, a display context, or DTO conversion. Examples are a link given its fragment entry's new configuration and then rendered, a model listener reading the cache field of the instance it is about to save, and a structure given a template's definition and then read for its form. The lazy getter publishes its value into the shared entity cache entry of the same primary key and MVCC version, so a value computed from unsaved state reaches every reader and stays after a rollback. A `clone()` keeps the primary key and MVCC version, so a modified clone publishes the same way. An instance from `scratchClone()`, and a new instance that was never fetched, are not this mistake, but removing a `scratchClone()` call from code that then changes the instance is. In Service Builder's cache field generation, a change that makes a lazy getter publish where it did not before, or widens when it publishes, such as dropping the MVCC version check, is this mistake for every caller at once.

1. **A scratch copy with no cache field to protect.** The branch adds a `scratchClone()` call whose copy is not a changed, unsaved instance that has its cache field getters read, directly or by the code it is handed to. A scratch copy exists only to keep such an instance from publishing its cache fields, so any other use is this mistake.

When any candidate repeats a known mistake, the validation fails. Report the message below to the developer with one row for each mistake, giving the path and the line as the candidate prints them, and return it as the failure note.

```markdown
**`@CacheField` usage repeats a known mistake.** Fix each line below before requesting review. For additional questions, use #t-core-infrastructure on Slack.

| File | Line | Mistake |
| --- | --- | --- |
| <file> | <line> | <mistake> |
```

When no candidate repeats a known mistake, the validation passes.

## Time Estimate

~10-30 sec (static, no build).