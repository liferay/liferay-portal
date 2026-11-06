# Empty Service Properties

Flags a `registerService` call that passes an empty `Hashtable` or `HashMapDictionary` as the service properties, built inline with `new`, or held by a local variable that is declared with one and passed on unchanged. OSGi documents `null` as "no properties", so the empty dictionary only allocates and suggests properties that are not there. Such code passes `null` instead.

## Source Formatter Candidate

This validation stands in for a Source Formatter check. Source Formatter catches the same code at format time and in CI without a pr-check run, so the check is the intended home of this rule, and this validation is the guard until it lands. When the check lands in `modules/util/source-formatter`, delete this file and its entry in `SKILL.md`.

- Check: `EmptyServicePropertiesCheck`
- Source Formatter pull request: https://github.com/ling-alan-huang/liferay-portal/pull/4415
- Ticket: LPD-114249

## Match

`\.java$ &! ^modules/third-party/`

## Command

This check is static and needs no build. It walks each changed Java file that calls `registerService` and mentions `Hashtable` or `HashMapDictionary`, skips generated files, and reports each qualified `registerService(` call, such as `bundleContext.registerService(`, whose properties argument is empty on a line the branch adds. The scan splits the file into tokens with comments, string literals, and character literals left out, and takes the last argument of the call, skipping the commas inside nested parentheses and type arguments. That argument is reported in one of two ways:

- It is `new Hashtable<...>()`, `new java.util.Hashtable<...>()`, or `new HashMapDictionary<...>()`, with or without type arguments: reported at the line of the argument. `HashMapDictionaryBuilder` has no `create()` without arguments, so every dictionary it builds starts from a map or a `put`, and it is not matched.
- It is a variable name, and the nearest declaration of that name in a block enclosing the call is initialized with one of those constructions, and the name appears nowhere else in the declaring block: reported at the line of the argument when the branch adds either that line or the line of the declaration.

An unqualified `registerService(` call is a helper method of the class rather than an OSGi registration, and the helper may require a nonnull dictionary, so it is not reported. A variable that is filled, read, or passed anywhere else in its block is not reported either. The check uses `command grep` so each file and its added lines are searched by the system grep rather than the shell's `grep` wrapper, which some environments route to another tool with its own defaults. Each finding prints as `<path>:<line>: registers a service with an empty "Dictionary", pass "null"`.

A generated file carries the `@generated` Javadoc marker that Service Builder and the other code generators stamp on every file they produce (for example every `*LocalService` interface). The scan ignores case in that match, so the `@Generated` annotation on REST Builder output is covered too. That annotation is machine written boilerplate, not hand written registration code, so any file carrying it is excluded before the registration scan.

Anchor that match to a Javadoc tag or an annotation rather than searching the whole file for the bare word, or a class that only mentions `@generated` in prose drops out of the scan entirely.

```bash
REGISTRATIONS='
	function add(t) {
		n++
		tok[n] = t
		ln[n] = NR
	}

	function empty(from, to,    depth, k) {
		k = from

		if (tok[k] != "new") {
			return 0
		}

		k++

		if ((tok[k] == "java") && (tok[k + 1] == ".") && (tok[k + 2] == "util") && (tok[k + 3] == ".")) {
			k += 4
		}

		if (tok[k] !~ /^(HashMapDictionary|Hashtable)$/) {
			return 0
		}

		k++

		if (tok[k] == "<") {
			depth = 0

			for (; k <= to; k++) {
				if (tok[k] == "<") {
					depth++
				}
				else if (tok[k] == ">") {
					depth--

					if (depth == 0) {
						break
					}
				}
			}

			k++
		}

		return (tok[k] == "(") && (tok[k + 1] == ")") && (k + 1 == to)
	}

	function generic(k,    depth) {
		if ((tok[k - 1] != ".") && (tok[k - 1] !~ /^[A-Z]/)) {
			return 0
		}

		depth = 0

		for (; k <= n; k++) {
			if (tok[k] == "<") {
				depth++
			}
			else if (tok[k] == ">") {
				depth--

				if (depth == 0) {
					return k
				}
			}
			else if (tok[k] !~ /^([[:alnum:]_$]+|[,?.&]|\[|\])$/) {
				return 0
			}
		}

		return 0
	}

	function variable(i, name,    best, block, count, end, j) {
		for (j = i - 1; j > 0; j--) {
			if ((tok[j] == name) && (tok[j + 1] == "=") && ((tok[j - 1] ~ /^[A-Z][[:alnum:]_$]*$/) || (tok[j - 1] == ">")) && (enclosing[j] < i) && (pair[enclosing[j]] > i)) {
				best = j

				break
			}
		}

		if (!best) {
			return 0
		}

		for (end = best + 2; (end <= n) && (tok[end] != ";"); end++) {
			if (tok[end] ~ /^[([{]$/) {
				end = pair[end]
			}
		}

		if (!empty(best + 2, end - 1)) {
			return 0
		}

		block = enclosing[best]
		count = 0

		for (j = block + 1; j < pair[block]; j++) {
			if ((tok[j] == name) && (tok[j - 1] != ".")) {
				count++
			}
		}

		if (count != 2) {
			return 0
		}

		return best
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
			else if (two ~ /^([!=<>+*\/&|^%-]=|->|::|&&|\|\|)$/) {
				add(two)
				c++
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
			enclosing[k] = braces[braceDepth]

			if (tok[k] ~ /^[([{]$/) {
				stack[++depth] = k

				if (tok[k] == "{") {
					braces[++braceDepth] = k
				}
			}
			else if ((tok[k] ~ /^[)}\]]$/) && (depth > 0)) {
				pair[k] = stack[depth]
				pair[stack[depth]] = k
				depth--

				if (tok[k] == "}") {
					braceDepth--
				}
			}
		}

		for (i = 1; i <= n; i++) {
			if ((tok[i] != "registerService") || (tok[i + 1] != "(") || (tok[i - 1] != ".")) {
				continue
			}

			close1 = pair[i + 1]
			last = 0

			for (k = i + 2; k < close1; k++) {
				if (tok[k] ~ /^[([{]$/) {
					k = pair[k]
				}
				else if (tok[k] == "<") {
					skip = generic(k)

					if (skip) {
						k = skip
					}
				}
				else if (tok[k] == ",") {
					last = k
				}
			}

			if (!last) {
				continue
			}

			if (empty(last + 1, close1 - 1)) {
				print ln[last + 1], ln[last + 1]
			}
			else if ((last + 2 == close1) && (tok[last + 1] ~ /^[[:lower:]_$][[:alnum:]_$]*$/)) {
				declaration = variable(i, tok[last + 1])

				if (declaration) {
					print ln[last + 1], ln[declaration]
				}
			}
		}
	}
'

FINDINGS=$(
	bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}" | while IFS= read -r FILE
	do
		[[ -f ${FILE} ]] || continue

		command grep --fixed-strings --quiet --regexp='registerService' "${FILE}" || continue

		command grep \
			--extended-regexp \
			--quiet \
			--regexp='HashMapDictionary|Hashtable' \
			"${FILE}" || continue

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

		awk "${REGISTRATIONS}" "${FILE}" | while read -r LINE DECLARATION_LINE
		do
			if command grep --fixed-strings --line-regexp --quiet "${LINE}" <<< "${ADDED}" ||
				command grep --fixed-strings --line-regexp --quiet "${DECLARATION_LINE}" <<< "${ADDED}"
			then
				echo "${FILE}:${LINE}: registers a service with an empty \"Dictionary\", pass \"null\""
			fi
		done
	done
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind, so it reports on the diff's last Java file rather than on the diff.

Run the loop from `${REPO_ROOT}`. The changed paths are relative to the repository root, so from any directory below it, every file test fails and the check passes without scanning anything.

Use `while read` rather than a `for` over an unquoted substitution, so a path holding whitespace stays one file rather than splitting into several.

When `${FINDINGS}` is empty, the branch adds no registration with empty service properties, so the validation passes.

Otherwise, the validation fails. Report the message below to the developer with one row for each finding, giving the path and the line as the finding prints them, and return it as the failure note.

```markdown
**A service is registered with empty properties.** Each call below passes an empty `Hashtable` or `HashMapDictionary` as the service properties of `registerService`. OSGi documents `null` as "no properties", so the empty dictionary only allocates and suggests properties that are not there. Pass `null` instead.

| File | Line |
| --- | --- |
| <file> | <line> |
```

## Time Estimate

~10-30 sec (static, no build).