# PrintWriter Wrapper

Flags hand written portal runtime Java, outside the build and command line tools in `modules/sdk` and `modules/util`, that wraps a writer or a stream in a new `PrintWriter` or `UnsyncPrintWriter` only to print to it. A `PrintWriter` swallows every `IOException`, so a failed write goes unnoticed, it adds an allocation, and when it is constructed with `autoFlush` set to `true` it flushes the wrapped writer on every `println`. Such code writes to the wrapped `Writer` or to the response writer directly, or, for an `OutputStream`, uses an `OutputStreamWriter` with an explicit charset.

## Source Formatter Candidate

This validation stands in for a Source Formatter check. Source Formatter catches the same code at format time and in CI without a pr-check run, so the check is the intended home of this rule, and this validation is the guard until it lands. When the check lands in `modules/util/source-formatter`, delete this file and its entry in `SKILL.md`.

- Check: `PrintWriterWrapperCheck`
- Source Formatter pull request: https://github.com/ling-alan-huang/liferay-portal/pull/4413
- Ticket: LPD-113525

## Match

`\.java$ &! -test-util/|/src/test/|/src/testIntegration/|^[^/]+/test/|^portal-test|^modules/(sdk|third-party|util)/`

## Command

This check is static and needs no build. It walks each changed Java file that constructs a `PrintWriter` or an `UnsyncPrintWriter`, skips generated files, and reports each `new PrintWriter(` or `new UnsyncPrintWriter(` on a line the branch adds whose writer only prints. The scan splits the file into tokens with comments, string literals, and character literals left out, then judges each construction in one of three ways:

- Assigned to a variable declared as `Writer`: always reported, since no `PrintWriter` method is reachable through that variable.
- Assigned to a local variable, a field, or a try-with-resources resource: reported when every use of the variable in the block that declares it is a call to `append`, `close`, `flush`, `print`, `println`, or `write`, an assignment to the variable, or a comparison with `null`.
- Passed as an argument to an unqualified method call: reported when every method of the same name and argument count in the file is `private` and uses the matching parameter only in those calls.

A writer that is passed to any other method, returned, assigned to another variable, put into a map, or used to call `checkError`, `format`, or `printf` is not reported. The check uses `command grep` so each file and its added lines are searched by the system grep rather than the shell's `grep` wrapper, which some environments route to another tool with its own defaults. Each finding prints as `<path>:<line>: wraps a writer in a "PrintWriter" only to print`, where `<line>` is the line of `new`.

A generated file carries the `@generated` Javadoc marker that Service Builder and the other code generators stamp on every file they produce (for example every `*LocalService` interface). The scan ignores case in that match, so the `@Generated` annotation on REST Builder output is covered too. That annotation is machine written boilerplate, not hand written writer code, so any file carrying it is excluded before the `PrintWriter` scan.

Anchor that match to a Javadoc tag or an annotation rather than searching the whole file for the bare word, or a class that only mentions `@generated` in prose drops out of the scan entirely.

```bash
WRAPPERS='
	function add(t) {
		n++
		tok[n] = t
		ln[n] = NR
	}

	function argument(i,    body, count, end, found, index1, k, m, name, open, parameterEnd, position) {
		for (k = i - 1; k > 0; k--) {
			if (tok[k] ~ /^[)}\]]$/) {
				k = pair[k]
			}
			else if (tok[k] == "(") {
				open = k

				break
			}
			else if (tok[k] ~ /^[;{]$/) {
				return
			}
		}

		if (!open) {
			return
		}

		name = tok[open - 1]

		if ((name !~ /^[[:alpha:]_$][[:alnum:]_$]*$/) || (tok[open - 2] ~ /^(\.|new|::)$/)) {
			return
		}

		index1 = arguments(open, i)
		count = arguments(open, pair[open])

		for (m = 1; m <= n; m++) {
			if ((tok[m] != name) || (tok[m + 1] != "(") || (tok[m - 1] !~ /^([[:alnum:]_$]+|>|\])$/) || (tok[m - 1] ~ /^(case|else|new|return|throw)$/)) {
				continue
			}

			parameterEnd = pair[m + 1]

			for (k = parameterEnd + 1; (k <= n) && (tok[k] !~ /^[{;]$/); k++) {
			}

			if (tok[k] != "{") {
				continue
			}

			body = k

			if (arguments(m + 1, parameterEnd) != count) {
				continue
			}

			if (index1 < count) {
				end = comma[index1 + 1]
			}
			else {
				end = parameterEnd
			}

			for (position = end - 1; (tok[position] == "[") || (tok[position] == "]"); position--) {
			}

			for (k = m - 1; (k > 0) && (tok[k] !~ /^[;{}]$/) && (tok[k] != "private"); k--) {
			}

			if ((tok[k] != "private") || !printOnly(tok[position], body + 1, pair[body] - 1, 0)) {
				return
			}

			found = 1
		}

		if (found) {
			print ln[i]
		}
	}

	function arguments(open, end,    count, k, skip) {
		count = 0

		for (k = open + 1; k < end; k++) {
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
				count++
				comma[count] = k
			}
		}

		return count
	}

	function declared(k) {
		return ((tok[k - 1] ~ /^[A-Z][[:alnum:]_$]*$/) || (tok[k - 1] == ">") || (tok[k - 1] == "]")) && (tok[k + 1] ~ /^([=;,):])$/)
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

	function printOnly(name, from, to, declaration,    k, next1, previous) {
		for (k = from; k <= to; k++) {
			if ((tok[k] != name) || (k == declaration) || (tok[k + 1] == "(") || declared(k)) {
				continue
			}

			previous = tok[k - 1]

			if (previous == ".") {
				if (tok[k - 2] != "this") {
					continue
				}

				previous = tok[k - 3]
			}

			next1 = tok[k + 1]

			if ((next1 == ".") && (tok[k + 2] ~ /^(append|close|flush|print|println|write)$/) && (tok[k + 3] == "(")) {
				continue
			}

			if ((next1 == "=") && (previous ~ /^[;{}]$/)) {
				continue
			}

			if ((next1 ~ /^[!=]=$/) && (tok[k + 2] == "null")) {
				continue
			}

			if ((previous ~ /^[!=]=$/) && (tok[k - 2] == "null")) {
				continue
			}

			return 0
		}

		return 1
	}

	function variable(i,    best, j, name, open, type) {
		name = tok[i - 2]

		if (tok[i - 3] == ".") {
			if (tok[i - 4] != "this") {
				return
			}
		}
		else if (declared(i - 2)) {
			best = i - 2
		}

		if (!best) {
			for (j = 1; j <= n; j++) {
				if ((tok[j] == name) && declared(j) && (enclosing[j] < i) && (pair[enclosing[j]] > i) && (!best || (enclosing[j] > enclosing[best]))) {
					best = j
				}
			}
		}

		if (!best) {
			return
		}

		type = tok[best - 1]
		open = enclosing[best]

		if ((type == "Writer") || printOnly(name, open + 1, pair[open] - 1, best)) {
			print ln[i]
		}
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
			if ((tok[i] != "new") || (tok[i + 1] !~ /^(Unsync)?PrintWriter$/) || (tok[i + 2] != "(")) {
				continue
			}

			if (tok[i - 1] == "=") {
				variable(i)
			}
			else if ((tok[i - 1] == "(") || (tok[i - 1] == ",")) {
				argument(i)
			}
		}
	}
'

FINDINGS=$(
	bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}" | while IFS= read -r FILE
	do
		[[ -f ${FILE} ]] || continue

		command grep \
			--extended-regexp \
			--quiet \
			--regexp='new[[:space:]]+(Unsync)?PrintWriter\(' \
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

		awk "${WRAPPERS}" "${FILE}" | while IFS= read -r LINE
		do
			if command grep --fixed-strings --line-regexp --quiet "${LINE}" <<< "${ADDED}"
			then
				echo "${FILE}:${LINE}: wraps a writer in a \"PrintWriter\" only to print"
			fi
		done
	done
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind, so it reports on the diff's last Java file rather than on the diff. Each iteration ends on a `continue` or on the loop over the lines the `awk` scan prints, and every pass of that loop ends on an `if` that prints a finding or not and exits 0 either way, so a clean branch and a branch with findings both exit 0.

Run the loop from `${REPO_ROOT}`. The changed paths are relative to the repository root, so from any directory below it, every file test fails and the check passes without scanning anything.

Use `while read` rather than a `for` over an unquoted substitution, so a path holding whitespace stays one file rather than splitting into several.

When `${FINDINGS}` is empty, the branch adds no `PrintWriter` that only prints, so the validation passes.

Otherwise, the validation fails. Report the message below to the developer with one row for each finding, giving the path and the line as the finding prints them, and return it as the failure note.

```markdown
**A `PrintWriter` wraps a writer only to print to it.** Each construction below wraps a writer or a stream in a `PrintWriter` or an `UnsyncPrintWriter` that is only used to print, so it swallows every `IOException`, adds an allocation, and, when constructed with `autoFlush` set to `true`, flushes on every `println`. Write to the wrapped `Writer` or the response writer directly, or use an `OutputStreamWriter` with an explicit charset for a stream.

| File | Line |
| --- | --- |
| <file> | <line> |
```

## Time Estimate

~10-30 sec (static, no build).