# Redundant Assert Message

Flags an `org.junit.Assert` call whose message only repeats an asserted value, such as `Assert.assertNull(String.valueOf(throwable), throwable)`. JUnit already prints the asserted values when the assertion fails, so the message makes the call longer and tells the reader nothing, and the call drops it, as in `Assert.assertNull(throwable)`.

## Source Formatter Candidate

This validation stands in for a Source Formatter check. Source Formatter catches the same code at format time and in CI without a pr-check run, so the check is the intended home of this rule, and this validation is the guard until it lands. When the check lands in `modules/util/source-formatter`, delete this file and its entry in `SKILL.md`.

- Check: `JavaRedundantAssertMessageCheck`
- Source Formatter pull request: https://github.com/ling-alan-huang/liferay-portal/pull/4389
- Ticket: LPD-113243

## Match

`\.java$`

## Command

This check is static and needs no build. It walks each changed Java file that imports `org.junit.Assert`, skips generated files, and reports each `Assert.assertEquals`, `Assert.assertNotEquals`, `Assert.assertNull`, or `Assert.assertSame` call whose message starts on a line the branch adds and only repeats an asserted value. It uses `command grep` so each file and its added lines are searched by the system grep rather than the shell's `grep` wrapper, which some environments route to another tool with its own defaults. It applies the rules of the check:

- The call names `Assert`, lies outside comments and literals, and passes a message and the asserted values only: two arguments for `assertNull` and three for the others, so a call with a delta is left alone.
- The message is `String.valueOf(<value>)` or `<value>.toString()`, where `<value>` equals an asserted argument, ignoring whitespace, and has no side effects: a variable or an array element with a literal or variable index, either one optionally followed by getters (`get*()` other than `getAnd*()`, `is*()`, or `size()`). A bare `get()` counts only on a variable declared as an `AtomicBoolean`, `AtomicInteger`, `AtomicLong`, `AtomicReference`, `Optional`, `OptionalDouble`, `OptionalInt`, `OptionalLong`, or `ThreadLocal`.
- A `String.valueOf` message is kept when the file declares `<value>` as a `char[]`, since that message prints the characters while JUnit prints the identity of the array.
- An `assertEquals` call is reported only when one compared value is a number literal or a variable whose type is not a `String`. Two unequal strings throw a `ComparisonFailure` that shortens them, which leaves the message as the only place the full value appears. Every declaration of the name in the method must agree on one type, which must be a primitive, a `java.lang` type other than `String`, `CharSequence`, `Comparable`, `Object`, or `Serializable`, or a type the file imports, declares, or finds beside it in its package. A field is looked up only for a name that starts with `_` or is in constant case, and a name that is also an untyped lambda parameter has no known type.

The method of a call is the run of lines between two blank lines at class level, which is one member in formatted code. Each finding prints as `<path>:<line>: the message repeats the asserted value`, where `<line>` is the line the message starts on.

A generated file carries the `@generated` Javadoc marker that Service Builder and the other code generators stamp on every file they produce (for example every `*LocalService` interface). The scan ignores case in that match, so the `@Generated` annotation on REST Builder output is covered too. That annotation is machine written boilerplate, not hand written assert code, so any file carrying it is excluded before the assert scan.

Anchor that match to a Javadoc tag or an annotation rather than searching the whole file for the bare word, or a class that only mentions `@generated` in prose drops out of the scan entirely.

```bash
ASSERTS='
	function balanced(text) {
		return (gsub(/</, "<", text) == gsub(/>/, ">", text)) && (gsub(/\[/, "[", text) == gsub(/\]/, "]", text))
	}

	function check(    angle, c, count, depth, getter, i, k, line, message, method, name, p, rest, start, text, type, value) {
		p = 0
		rest = scope

		while (match(rest, /Assert\.assert(Equals|NotEquals|Null|Same)\(/)) {
			start = p + RSTART
			p += RSTART + RLENGTH - 1
			rest = substr(rest, RSTART + RLENGTH)

			if (substr(scope, start - 1, 1) ~ /[[:alnum:]_.]/) {
				continue
			}

			angle = 0
			arguments[1] = ""
			count = 1
			depth = 0
			line = 0
			method = substr(scope, start + 7, p - start - 7)

			for (i = p + 1; i <= length(scope); i++) {
				c = substr(scope, i, 1)

				if ((c == "(") || (c == "{")) {
					depth++
				}
				else if ((c == ")") || (c == "}")) {
					if (depth-- == 0) {
						break
					}
				}
				else if (c == "<") {
					angle++
				}
				else if ((c == ">") && (substr(scope, i - 1, 1) != "-")) {
					angle--
				}
				else if ((c == ",") && (angle == 0) && (depth == 0)) {
					arguments[++count] = ""

					continue
				}

				if ((count == 1) && (line == 0) && (c ~ /[^[:space:]]/)) {
					line = i
				}

				arguments[count] = arguments[count] c
			}

			if (count != ((method == "assertNull") ? 2 : 3)) {
				continue
			}

			for (k = 1; k <= count; k++) {
				gsub(/^[[:space:]]+|[[:space:]]+$/, "", arguments[k])
			}

			message = arguments[1]

			gsub(/[[:space:]]/, "", message)

			if (message ~ /\.toString\(\)$/) {
				value = substr(message, 1, length(message) - 11)
			}
			else if (message ~ /^String\.valueOf\(.*\)$/) {
				value = substr(message, 16, length(message) - 16)
			}
			else {
				continue
			}

			if (!match(value, /^[A-Za-z_$][A-Za-z0-9_$]*(\[([0-9]+|[A-Za-z_$][A-Za-z0-9_$]*)\])?/)) {
				continue
			}

			name = substr(value, 1, RLENGTH)
			text = substr(value, RLENGTH + 1)

			if (substr(text, 1, 6) == ".get()") {
				if (name ~ /\[/) {
					continue
				}

				type = resolve(name)

				sub(/<.*/, "", type)

				if (type !~ /^(AtomicBoolean|AtomicInteger|AtomicLong|AtomicReference|Optional|OptionalDouble|OptionalInt|OptionalLong|ThreadLocal)$/) {
					continue
				}

				text = substr(text, 7)
			}

			while (match(text, /^\.[A-Za-z0-9_]+\(\)/)) {
				getter = substr(text, 2, RLENGTH - 3)

				if (((getter !~ /^get./) || (getter ~ /^getAnd[A-Z]/)) && (getter !~ /^is[A-Z]/) && (getter != "size")) {
					break
				}

				text = substr(text, RLENGTH + 1)
			}

			if (text != "") {
				continue
			}

			for (k = 2; k <= count; k++) {
				text = arguments[k]

				gsub(/[[:space:]]/, "", text)

				if (text == value) {
					break
				}
			}

			if (k > count) {
				continue
			}

			if ((message ~ /^String\.valueOf\(/) && variable(value) && (match(all, "(^|[^[:alnum:]_])char[[:space:]]*(\\[[[:space:]]*\\]|\\.\\.\\.)[[:space:]]*" value "([^[:alnum:]_$]|$)") || match(all, "(^|[^[:alnum:]_])char[[:space:]]+" value "[[:space:]]*\\["))) {
				continue
			}

			if ((method == "assertEquals") && !nonstring(arguments[2]) && !nonstring(arguments[3])) {
				continue
			}

			text = substr(scope, 1, line)

			print first + gsub(/\n/, "", text)
		}
	}

	function code(line,    c, i, j, size, text) {
		if (!block && !comment && (line !~ /["\/\047]/)) {
			return line
		}

		size = length(line)
		text = ""

		for (i = 1; i <= size; i++) {
			if (comment) {
				j = index(substr(line, i), "*/")

				if (j == 0) {
					return text
				}

				comment = 0
				i += j
			}
			else if (block) {
				j = index(substr(line, i), "\"\"\"")

				if (j == 0) {
					return text
				}

				block = 0
				i += j + 1
				text = text " "
			}
			else if (substr(line, i, 2) == "//") {
				return text
			}
			else if (substr(line, i, 2) == "/*") {
				comment = 1
				i++
			}
			else if (substr(line, i, 3) == "\"\"\"") {
				block = 1
				i += 2
			}
			else {
				c = substr(line, i, 1)

				if ((c == "\"") || (c == "\047")) {
					for (j = i + 1; (j <= size) && (substr(line, j, 1) != c); j++) {
						if (substr(line, j, 1) == "\\") {
							j++
						}
					}

					c = " "
					i = j
				}

				text = text c
			}
		}

		return text
	}

	function declared(text, name,    i, p, rest, result, type) {
		found = 0
		p = 0
		rest = text
		result = ""

		while (match(rest, "[]>[:alnum:]_$][[:space:]]+" name "[[:space:]]*[;=),:&|]")) {
			p += RSTART
			rest = substr(rest, RSTART + 1)

			if (((substr(text, p, 1) == "]") && (substr(text, p - 1, 1) != "[")) || ((substr(text, p, 1) == ">") && (substr(text, p - 1, 1) !~ /[[:alnum:]_$>?]/))) {
				continue
			}

			type = ""

			for (i = p; i >= 1; i--) {
				if ((substr(text, i, 1) !~ /[[:alnum:]_$]/) && (substr(text, i + 1, 1) ~ /[[:alpha:]_$]/) && balanced(substr(text, i + 1, p - i))) {
					type = substr(text, i + 1, p - i)

					gsub(/[[:space:]]/, "", type)

					break
				}
			}

			if (type ~ /^(assert|break|case|continue|do|else|new|return|throw|yield)$/) {
				continue
			}

			if (found++ == 0) {
				result = type
			}
			else if (type != result) {
				return ""
			}
		}

		return result
	}

	function nonstring(value,    path, type) {
		if (value ~ /^[0-9]+$/) {
			return 1
		}

		if (!variable(value)) {
			return 0
		}

		type = resolve(value)

		if ((type == "") || (type ~ /\]$/)) {
			return 0
		}

		if (type ~ /^[a-z]/) {
			return type ~ /^(boolean|byte|char|double|float|int|long|short)$/
		}

		sub(/<.*/, "", type)

		if (type ~ /^(CharSequence|Comparable|Object|Serializable|String)$/) {
			return 0
		}

		if ((type ~ /^(Boolean|Byte|Character|Class|Double|Float|Integer|Long|Number|Short|Throwable)$/) || index(imports, "." type ";") || match(all, "(^|[^[:alnum:]_])(class|enum|interface|record)[[:space:]]+" type "([^[:alnum:]_]|$)")) {
			return 1
		}

		path = directory "/" type ".java"
		type = (getline value < path) >= 0

		close(path)

		return type
	}

	function resolve(name,    type) {
		if (match(scope, "(^|[^[:alnum:]_$.])" name "[[:space:]]*->") || match(scope, "\\([[:space:]]*([[:alnum:]_$]+[[:space:]]*,[[:space:]]*)*" name "[[:space:]]*(,[[:space:]]*[[:alnum:]_$]+[[:space:]]*)*\\)[[:space:]]*->")) {
			return ""
		}

		type = declared(scope, name)

		if ((found == 0) && (name ~ /^(_|[A-Z][A-Z0-9_]*$)/)) {
			type = declared(fields, name)
		}

		return type
	}

	function variable(name) {
		return (name ~ /^[_a-zA-Z][_a-zA-Z0-9]*$/) && (name !~ /^(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|false|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|null|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|true|try|void|volatile|while)$/)
	}

	/^import / {
		imports = imports substr($0, 7)
	}

	{
		text = code($0)
		all = all "\n" text
		lines[NR] = text
		starts[NR] = depth
		depth += gsub(/\{/, "", text) - gsub(/\}/, "", text)

		if (starts[NR] == 1) {
			fields = fields "\n" lines[NR]
		}
	}

	END {
		if (!index(imports, " org.junit.Assert;")) {
			exit
		}

		directory = FILENAME
		first = 1

		sub(/\/[^\/]*$/, "", directory)

		for (i = 1; i <= NR + 1; i++) {
			if ((i > NR) || ((starts[i] <= 1) && (lines[i] !~ /[^[:space:]]/))) {
				if (scope ~ /Assert\./) {
					check()
				}

				first = i + 1
				scope = ""
			}
			else if (i == first) {
				scope = lines[i]
			}
			else {
				scope = scope "\n" lines[i]
			}
		}
	}
'

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

		awk "${ASSERTS}" "${FILE}" | while IFS= read -r LINE
		do
			if command grep --fixed-strings --line-regexp --quiet "${LINE}" <<< "${ADDED}"
			then
				echo "${FILE}:${LINE}: the message repeats the asserted value"
			fi
		done
	done
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind, so it reports on the diff's last Java file rather than on the diff. Each iteration ends on a `continue` or on the loop over the lines the `awk` scan prints, and every pass of that loop ends on an `if` that prints a finding or not and exits 0 either way, so a clean branch and a branch with findings both exit 0.

Run the loop from `${REPO_ROOT}`. The changed paths are relative to the repository root, so from any directory below it, every file test fails and the check passes without scanning anything.

Use `while read` rather than a `for` over an unquoted substitution, so a path holding whitespace stays one file rather than splitting into several.

When `${FINDINGS}` is empty, the branch adds no assert message that repeats an asserted value, so the validation passes.

Otherwise, the validation fails. Report the message below to the developer with one row for each finding, giving the path and the line as the finding prints them, and return it as the failure note.

```markdown
**An assert message repeats the asserted value.** JUnit already prints the asserted values when an assertion fails, so each message below tells the reader nothing. Drop the message, as in `Assert.assertNull(throwable)`.

| File | Line |
| --- | --- |
| <file> | <line> |
```

## Time Estimate

~10-30 sec (static, no build).