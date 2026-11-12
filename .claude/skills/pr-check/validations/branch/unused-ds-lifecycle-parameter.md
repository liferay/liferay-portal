# Unused DS Lifecycle Parameter

Flags a parameter that the body of a Declarative Services (DS) lifecycle method never reads, in a method that the branch adds or changes. A lifecycle method is a method or a constructor annotated `@Activate`, `@Modified`, or `@Deactivate`. Service Component Runtime passes only the parameters a lifecycle method declares, so an unused `BundleContext`, `ComponentContext`, `Map<String, Object> properties`, or deactivation reason is dead weight that invites the next reader to look for a use that is not there. A constructor parameter annotated `@Reference` is an injected service, not a lifecycle argument, so it is exempt.

## Source Formatter Candidate

This validation stands in for a Source Formatter check. Source Formatter catches the same code at format time and in CI without a pr-check run, so the check is the intended home of this rule, and this validation is the guard until it lands. When the check lands in `modules/util/source-formatter`, delete this file and its entry in `SKILL.md`.

- Check: `UnusedParameterCheck`
- Source Formatter pull request: https://github.com/ling-alan-huang/liferay-portal/pull/4420
- Ticket: LPD-114888

## Match

`\.java$ &! ^modules/third-party/`

## Command

This check is static and needs no build. It walks each changed Java file that mentions `@Activate`, `@Modified`, or `@Deactivate`, skips generated files, and splits the file into tokens with comments, string literals, and character literals left out. It then skips the annotations and modifiers that follow each lifecycle annotation, up to the opening parenthesis of the parameter list. An annotated field, which reaches `;`, `=`, `{`, or `}` before any parenthesis, is not a method and is skipped, and so is a method without a body. Each parameter not annotated `@Reference` is reported when no token of the body equals its name, other than a member name that follows `.`.

A finding is reported only when the branch adds or changes a line between the lifecycle annotation and the closing brace of the body. A generated file carries the `@generated` Javadoc marker and is excluded before the scan. The match ignores case, so the `@Generated` annotation on REST Builder output is covered too. The check uses `command grep` so each file and its added lines are searched by the system grep rather than the shell's `grep` wrapper, which some environments route to another tool with its own defaults. Each finding prints as `<path>:<line>: "<method>" never reads its "<parameter>" parameter, remove it`.

```bash
UNUSED_PARAMETERS='
	function add(t) {
		n++
		tok[n] = t
		ln[n] = NR
	}

	{
		line = $0
		token = ""

		for (c = 1; c <= length(line); c++) {
			ch = substr(line, c, 1)
			two = substr(line, c, 2)

			if (comment) {
				if (two == "*/") {
					comment = 0
					c++
				}

				continue
			}

			if (textBlock) {
				if (substr(line, c, 3) == "\"\"\"") {
					textBlock = 0
					c += 2
				}

				continue
			}

			if (ch ~ /[[:alnum:]_$]/) {
				token = token ch

				continue
			}

			if (token != "") {
				add(token)

				token = ""
			}

			if (two == "//") {
				break
			}

			if (two == "/*") {
				comment = 1
				c++
			}
			else if (substr(line, c, 3) == "\"\"\"") {
				textBlock = 1
				c += 2
			}
			else if ((ch == "\"") || (ch == "\047")) {
				for (c++; (c <= length(line)) && (substr(line, c, 1) != ch); c++) {
					if (substr(line, c, 1) == "\\") {
						c++
					}
				}

				add("literal")
			}
			else if (ch !~ /[[:space:]]/) {
				add(ch)
			}
		}

		if (token != "") {
			add(token)
		}
	}

	END {
		depth = 0

		for (k = 1; k <= n; k++) {
			if (tok[k] ~ /^[([{]$/) {
				stack[++depth] = k
			}
			else if ((tok[k] ~ /^[])}]$/) && (depth > 0)) {
				pair[k] = stack[depth]
				pair[stack[depth]] = k
				depth--
			}
		}

		for (k = 1; k < n; k++) {
			if ((tok[k] != "@") || (tok[k + 1] !~ /^(Activate|Deactivate|Modified)$/)) {
				continue
			}

			for (j = k + 2; (j <= n) && (tok[j] == "@"); ) {
				for (j++; tok[j + 1] == "."; j += 2) {
				}

				j++

				if ((tok[j] == "(") && pair[j]) {
					j = pair[j] + 1
				}
			}

			for (; (j <= n) && (tok[j] !~ /^[(;={}]$/); j++) {
			}

			if ((tok[j] != "(") || !pair[j] || seen[j]) {
				continue
			}

			seen[j] = 1
			last = pair[j]

			for (b = last + 1; (b <= n) && (tok[b] !~ /^[;{}]$/); b++) {
			}

			if ((tok[b] != "{") || !pair[b]) {
				continue
			}

			start = j + 1
			nesting = 0

			for (p = j + 1; p <= last; p++) {
				if ((tok[p] ~ /^[(<]$/) && (p < last)) {
					nesting++
				}
				else if ((tok[p] ~ /^[)>]$/) && (p < last)) {
					nesting--
				}

				if (((tok[p] != ",") || nesting) && (p < last)) {
					continue
				}

				if (p > start) {
					reference = 0

					for (q = start; q < p; q++) {
						if ((tok[q] == "@") && (tok[q + 1] == "Reference")) {
							reference = 1
						}
					}

					name = tok[p - 1]
					used = 0

					for (q = b + 1; q < pair[b]; q++) {
						if ((tok[q] == name) && (tok[q - 1] != ".")) {
							used = 1

							break
						}
					}

					if (!reference && !used) {
						print ln[k] "\t" ln[pair[b]] "\t" ln[p - 1] "\t" tok[j - 1] "\t" name
					}
				}

				start = p + 1
			}
		}
	}
'

FINDINGS=$(
	bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}" | while IFS= read -r FILE
	do
		[[ -f ${FILE} ]] || continue

		command grep --extended-regexp --quiet --regexp='@(Activate|Deactivate|Modified)' "${FILE}" || continue

		command grep --ignore-case --quiet --regexp='@generated' "${FILE}" && continue

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

		awk "${UNUSED_PARAMETERS}" "${FILE}" | while IFS=$'\t' read -r START END LINE METHOD PARAMETER
		do
			for ((CHANGED_LINE = START; CHANGED_LINE <= END; CHANGED_LINE++))
			do
				if command grep --fixed-strings --line-regexp --quiet "${CHANGED_LINE}" <<< "${ADDED}"
				then
					echo "${FILE}:${LINE}: \"${METHOD}\" never reads its \"${PARAMETER}\" parameter, remove it"

					break
				fi
			done
		done
	done
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind, so it reports on the diff's last file rather than on the diff.

Run the loop from `${REPO_ROOT}`. The changed paths are relative to the repository root, so from any directory below it, every file test fails and the check passes without scanning anything.

Use `while read` rather than a `for` over an unquoted substitution, so a path holding whitespace stays one file rather than splitting into several.

When `${FINDINGS}` is empty, every lifecycle method that the branch adds or changes reads each of its parameters, so the validation passes.

Otherwise, the validation fails. Report the message below to the developer with one row for each finding, giving the path, the line, the method, and the parameter as the finding prints them, and return it as the failure note.

```markdown
**A DS lifecycle method declares a parameter it never reads.** Service Component Runtime passes only the parameters a lifecycle method declares, so remove each parameter below, along with its import when nothing else uses it. Remove the whole lifecycle method when it is left with no parameters and an empty body.

| File | Line | Method | Parameter |
| --- | --- | --- | --- |
| <file> | <line> | <method> | <parameter> |
```

## Time Estimate

~10-30 sec (static, no build).