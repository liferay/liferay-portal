# BeanParamUtil Getter

Flags a `BeanParamUtil` call that the branch adds without a getter: a call to a `*Silent` method, a call with three arguments, or a call whose fourth argument is not a getter. `BeanParamUtil` takes the getter of the bean as a method reference, such as `BeanParamUtil.getLong(folder, request, "folderId", BookmarksFolder::getFolderId)`, so the compiler checks the property and no reflective lookup runs on each call.

## Source Formatter Candidate

This validation stands in for a Source Formatter check. Source Formatter catches the same code at format time and in CI without a pr-check run, so the check is the intended home of this rule, and this validation is the guard until it lands. When the check lands in `modules/util/source-formatter`, delete this file and its entry in `SKILL.md`.

- Check: `BeanParamUtilGetterCheck`
- Source Formatter pull request: https://github.com/ling-alan-huang/liferay-portal/pull/4419
- Ticket: LPD-114632

## Match

`\.(java|jsp|jspf)$ &! ^modules/third-party/`

## Command

This check is static and needs no build. It walks each changed file that mentions `BeanParamUtil.`, skips generated files and `BeanParamUtil.java` itself, and reads every `BeanParamUtil.getBoolean`, `getDouble`, `getInteger`, `getLong`, and `getString` call, with or without the `Silent` suffix, to its closing parenthesis, counting the arguments at the top level and skipping string and character literals. A call is reported when its method ends in `Silent`, when it has fewer than four arguments, or when its fourth argument holds neither `::` nor `->` and is neither a name that ends in `Function` or `function` nor a call to a method whose name does. The finding is reported at the line where the call starts, and only when the branch adds that line. The check uses `command grep` so each file and its added lines are searched by the system grep rather than the shell's `grep` wrapper, which some environments route to another tool with its own defaults. Each finding prints as `<path>:<line>: calls "BeanParamUtil.<method>" without a getter, pass the getter of the bean as a method reference`.

A generated file carries the `@generated` Javadoc marker that Service Builder and the other code generators stamp on every file they produce. The match ignores case, so it also covers the `@Generated` annotation on REST Builder output. Any file that carries either marker is excluded before the call scan.

```bash
REFLECTIVE_CALLS='
	{
		text = text $0 "\n"
	}

	END {
		line = 1
		n = length(text)

		for (i = 1; i <= n; i++) {
			ch = substr(text, i, 1)

			if (ch == "\n") {
				line++

				continue
			}

			if (substr(text, i, 14) != "BeanParamUtil.") {
				continue
			}

			if ((i > 1) && (substr(text, i - 1, 1) ~ /[[:alnum:]_$.]/)) {
				continue
			}

			rest = substr(text, i + 14, 40)

			if (!match(rest, /^get(Boolean|Double|Integer|Long|String)(Silent)?\(/)) {
				continue
			}

			method = substr(rest, 1, RLENGTH - 1)
			start = line
			count = 1
			depth = 0
			argument = ""
			fourth = ""

			for (j = i + 14 + RLENGTH; j <= n; j++) {
				c = substr(text, j, 1)

				if (c == "\n") {
					line++
				}

				if ((c == "\"") || (c == "\047")) {
					argument = argument c

					for (j++; (j <= n) && (substr(text, j, 1) != c); j++) {
						if (substr(text, j, 1) == "\\") {
							argument = argument substr(text, j, 1)
							j++
						}

						argument = argument substr(text, j, 1)
					}

					argument = argument c

					continue
				}

				if ((c == "(") || (c == "{") || (c == "[")) {
					depth++
				}
				else if ((c == ")") || (c == "}") || (c == "]")) {
					if (depth == 0) {
						break
					}

					depth--
				}
				else if ((c == ",") && (depth == 0)) {
					if (count == 4) {
						fourth = argument
					}

					count++
					argument = ""

					continue
				}

				argument = argument c
			}

			if (count == 4) {
				fourth = argument
			}

			i = j

			if ((method !~ /Silent$/) && (count >= 4) && ((fourth ~ /->|::/) || (fourth ~ /^[[:space:]]*([[:alnum:]_$]+[[:space:]]*\.[[:space:]]*)*[[:alnum:]_$]*[Ff]unction[[:space:]]*(\(.*\))?[[:space:]]*$/))) {
				continue
			}

			print start "\t" method
		}
	}
'

FINDINGS=$(
	bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}" | while IFS= read -r FILE
	do
		[[ -f ${FILE} ]] || continue

		[[ ${FILE} == */com/liferay/portal/kernel/bean/BeanParamUtil.java ]] && continue

		command grep --fixed-strings --quiet --regexp='BeanParamUtil.' "${FILE}" || continue

		if command grep \
			--extended-regexp \
			--ignore-case \
			--quiet \
			--regexp='^[[:space:]]*(\*[[:space:]]*@generated|@generated\()' \
			"${FILE}"
		then
			continue
		fi

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

		awk "${REFLECTIVE_CALLS}" "${FILE}" | while IFS=$'\t' read -r LINE METHOD
		do
			if command grep --fixed-strings --line-regexp --quiet "${LINE}" <<< "${ADDED}"
			then
				echo "${FILE}:${LINE}: calls \"BeanParamUtil.${METHOD}\" without a getter, pass the getter of the bean as a method reference"
			fi
		done
	done
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind, so it reports on the diff's last file rather than on the diff.

Run the loop from `${REPO_ROOT}`. The changed paths are relative to the repository root, so from any directory below it, every file test fails and the check passes without scanning anything.

Use `while read` rather than a `for` over an unquoted substitution, so a path holding whitespace stays one file rather than splitting into several.

When `${FINDINGS}` is empty, the branch adds no `BeanParamUtil` call without a getter, so the validation passes.

Otherwise, the validation fails. Report the message below to the developer with one row for each finding, giving the path and the line as the finding prints them, and return it as the failure note.

```markdown
**A `BeanParamUtil` call reads the bean without a getter.** Each line below calls a `BeanParamUtil` method that read the property by name through reflection. Those methods were removed, so the call no longer compiles. Pass the getter of the bean as a method reference after the parameter name, such as `BookmarksFolder::getFolderId`. When the bean has no such getter, read the request parameter with `ParamUtil` instead.

| File | Line |
| --- | --- |
| <file> | <line> |
```

## Time Estimate

~10-30 sec (static, no build).