# Empty SafeCloseable

Flags an empty lambda, `() -> {}`, that the branch adds where it stands for a `SafeCloseable`: returned from a method declared to return `SafeCloseable`, or used to initialize or assign a variable or field declared as `SafeCloseable`. `SafeCloseable.EMPTY` is the shared instance that does nothing, so such code uses it instead of writing another empty lambda.

## Source Formatter Candidate

This validation stands in for a Source Formatter check. Source Formatter catches the same code at format time and in CI without a pr-check run, so the check is the intended home of this rule, and this validation is the guard until it lands. When the check lands in `modules/util/source-formatter`, delete this file and its entry in `SKILL.md`.

- Check: `EmptySafeCloseableCheck`
- Source Formatter pull request: https://github.com/ling-alan-huang/liferay-portal/pull/4417
- Ticket: LPD-114367

## Match

`\.java$ &! ^modules/third-party/`

## Command

This check is static and needs no build. It walks each changed Java file that mentions `SafeCloseable`, skips generated files and `SafeCloseable.java`, which declares `SafeCloseable.EMPTY` itself, and reports each empty lambda with no parameters on a line the branch adds whose target is a `SafeCloseable`. The scan splits the file into tokens with comments, string literals, and character literals left out, so a lambda whose body holds only a comment counts as empty. A lambda counts when the token before it is `return`, `=`, `?`, or `:`, and its statement is one of these:

- A `return` statement whose innermost enclosing method declares the return type `SafeCloseable`. A `return` inside a lambda body belongs to that lambda, and a constructor has no return type, so neither is reported.
- A statement whose first `=` follows a name declared with the type `SafeCloseable`, either in the same declaration (a local variable or a field) or anywhere else in the file (an assignment to such a variable or field).

The lambda must not sit inside parentheses that open after the statement starts, so an empty lambda passed as a method argument is not reported: without type information, the parameter type of the called method is unknown. The finding is reported at the line of the lambda's opening parenthesis. The check uses `command grep` so each file and its added lines are searched by the system grep rather than the shell's `grep` wrapper, which some environments route to another tool with its own defaults. Each finding prints as `<path>:<line>: creates an empty "SafeCloseable", use "SafeCloseable.EMPTY"`.

A generated file carries the `@generated` Javadoc marker that Service Builder and the other code generators stamp on every file they produce. The scan ignores case in that match, so the `@Generated` annotation on REST Builder output is covered too. That annotation is machine written boilerplate, not hand written code, so any file carrying it is excluded before the lambda scan.

```bash
EMPTY_LAMBDAS='
	function add(t) {
		n++
		tok[n] = t
		ln[n] = NR
	}

	function declared(name,    j) {
		for (j = 2; j <= n; j++) {
			if ((tok[j] == name) && (tok[j - 1] == "SafeCloseable")) {
				return 1
			}
		}

		return 0
	}

	function returnsSafeCloseable(i,    b, k, open) {
		for (b = enclosing[i]; b; b = enclosing[b]) {
			if (tok[b - 1] == "->") {
				return 0
			}

			for (k = b - 1; (k > 0) && (tok[k] ~ /^([[:alnum:]_$]+|[.,])$/); k--) {
			}

			if ((tok[k] != ")") || !pair[k]) {
				continue
			}

			open = pair[k]

			if (tok[open - 1] ~ /^(catch|for|if|switch|synchronized|try|while)$/) {
				continue
			}

			return tok[open - 2] == "SafeCloseable"
		}

		return 0
	}

	function statementStart(i,    k) {
		for (k = i - 1; k > 0; k--) {
			if ((tok[k] == ")") || (tok[k] == "]")) {
				k = pair[k]
			}
			else if ((tok[k] == ";") || (tok[k] == "{") || (tok[k] == "}") || (tok[k] == "(")) {
				return k + 1
			}
		}

		return 1
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
			else if ((tok[k] ~ /^[])}]$/) && (depth > 0)) {
				pair[k] = stack[depth]
				pair[stack[depth]] = k
				depth--

				if (tok[k] == "}") {
					braceDepth--
				}
			}
		}

		for (i = 2; i + 4 <= n; i++) {
			if ((tok[i] != "(") || (tok[i + 1] != ")") || (tok[i + 2] != "->") || (tok[i + 3] != "{") || (tok[i + 4] != "}")) {
				continue
			}

			if (tok[i - 1] !~ /^(:|=|\?|return)$/) {
				continue
			}

			start = statementStart(i)

			if (tok[start] == "return") {
				if (returnsSafeCloseable(i)) {
					print ln[i]
				}

				continue
			}

			for (k = start; (k < i) && (tok[k] != "="); k++) {
			}

			if ((k < i) && ((tok[k - 2] == "SafeCloseable") || declared(tok[k - 1]))) {
				print ln[i]
			}
		}
	}
'

FINDINGS=$(
	bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}" | while IFS= read -r FILE
	do
		[[ -f ${FILE} ]] || continue

		[[ ${FILE} == */com/liferay/petra/lang/SafeCloseable.java ]] && continue

		command grep --fixed-strings --quiet --regexp='SafeCloseable' "${FILE}" || continue

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

		awk "${EMPTY_LAMBDAS}" "${FILE}" | while read -r LINE
		do
			if command grep --fixed-strings --line-regexp --quiet "${LINE}" <<< "${ADDED}"
			then
				echo "${FILE}:${LINE}: creates an empty \"SafeCloseable\", use \"SafeCloseable.EMPTY\""
			fi
		done
	done
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind, so it reports on the diff's last Java file rather than on the diff.

Run the loop from `${REPO_ROOT}`. The changed paths are relative to the repository root, so from any directory below it, every file test fails and the check passes without scanning anything.

Use `while read` rather than a `for` over an unquoted substitution, so a path holding whitespace stays one file rather than splitting into several.

When `${FINDINGS}` is empty, the branch adds no empty lambda that stands for a `SafeCloseable`, so the validation passes.

Otherwise, the validation fails. Report the message below to the developer with one row for each finding, giving the path and the line as the finding prints them, and return it as the failure note.

```markdown
**An empty lambda stands for a `SafeCloseable`.** Each line below returns or stores `() -> {}` where a `SafeCloseable` is expected. `SafeCloseable.EMPTY` is the shared instance that does nothing. Use it instead.

| File | Line |
| --- | --- |
| <file> | <line> |
```

## Time Estimate

~10-30 sec (static, no build).