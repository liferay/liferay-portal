# XML Element Text

Flags a parse that a branch adds whose `Document` is used only to read the texts of child elements of its root, as in `SAXReaderUtil.read(xml)` followed by `getRootElement()` and `elementText("name")`. The code reads the texts with `XMLUtil.getElementText(xml, "name")` instead, which needs no parser, because every `SAXReaderUtil.read` call builds a new parser, and building one takes class loading locks that queue request threads under load.

## Source Formatter Candidate

This validation stands in for a Source Formatter check. Source Formatter catches the same code at format time and in CI without a pr-check run and rewrites it, so the check is the intended home of this rule, and this validation is the guard until it lands. When the check lands in `modules/util/source-formatter`, delete this file and its entry in `SKILL.md`.

- Check: `JavaXMLElementTextCheck`, which rewrites each such parse into `XMLUtil.getElementText` calls and removes the `Document` and `Element` variables

## Match

`\.java$ &! /test/|/testIntegration/`

## Command

This check is static and needs no build. It reads each changed Java file the branch keeps that imports `com.liferay.portal.kernel.xml.Document` and does not mention `DocumentException`, drops comments and string literals, and reports each `Document <name> = <reader>.read(<xml>);` that starts a line the branch adds, where `<reader>` is `SAXReaderUtil`, `UnsecureSAXReaderUtil`, `SAXReaderUtil.getSAXReader()`, `saxReader`, or `_saxReader`. A parse is reported only when all of the following hold, which are the conditions under which the check rewrites the code:

- `<xml>` is a variable that the file declares as a `String`, and the block that declares the document does not assign it again. A parse with a second argument is not reported, because that argument validates the document or names a schema.
- In the rest of that block, the document is used only as `Element <root> = <name>.getRootElement();`.
- Each such root element is used at least once, and only for `elementText` or `element(...).getText()` with a string literal. A root element that is walked, read for attributes, read with `elementTextTrim`, or passed on is not reported.

A file that mentions `DocumentException` is left out, because removing the parse could leave a `catch` block that no longer compiles. The **Match** leaves out tests under the `test` and `testIntegration` directories, which parse XML to check that it is well formed. Each finding prints as `<path>:<line>: parses XML only to read the texts of child elements of its root`.

```bash
ADDED='
	/^@@/ {
		size = split(substr($3, 2), range, ",")

		count = (size > 1) ? range[2] + 0 : 1

		for (i = 0; i < count; i++) {
			print range[1] + i
		}
	}
'

SCAN='
	function argumentsCount(open, closing,    c, count, i, level) {
		count = 1
		level = 0

		for (i = open + 1; i < closing; i++) {
			c = substr(all, i, 1)

			if ((c == "(") || (c == "[") || (c == "{")) {
				level++
			}
			else if ((c == ")") || (c == "]") || (c == "}")) {
				level--
			}
			else if ((c == ",") && (level == 0)) {
				count++
			}
		}

		return count
	}

	function blockEnd(position,    c, i, level) {
		level = 0

		for (i = position; i <= length(all); i++) {
			c = substr(all, i, 1)

			if (c == "{") {
				level++
			}
			else if ((c == "}") && (level-- == 0)) {
				return i
			}
		}

		return length(all) + 1
	}

	function closingIndex(open,    c, i, level) {
		level = 0

		for (i = open + 1; i <= length(all); i++) {
			c = substr(all, i, 1)

			if (c == "(") {
				level++
			}
			else if ((c == ")") && (level-- == 0)) {
				return i
			}
		}

		return 0
	}

	function countUses(text, name, reads,    count, offset, rest, tail, x) {
		count = 0
		offset = 0
		rest = text

		while ((x = index(rest, name)) > 0) {
			if (isWord(text, offset + x, length(name))) {
				tail = substr(text, offset + x + length(name), 200)

				if (!reads || (tail ~ /^[[:space:]]*\.[[:space:]]*elementText[[:space:]]*\([[:space:]]*\)/) || (tail ~ /^[[:space:]]*\.[[:space:]]*element[[:space:]]*\([[:space:]]*\)[[:space:]]*\.[[:space:]]*getText[[:space:]]*\([[:space:]]*\)/)) {
					count++
				}
			}

			offset += x
			rest = substr(rest, x + 1)
		}

		return count
	}

	function getRow(position,    text) {
		text = substr(all, 1, position)

		return 1 + gsub(/\n/, "", text)
	}

	function isElementTextsOnly(scope, name,    declaration, reads, rest, root, roots) {
		roots = 0
		rest = scope

		while (match(rest, "(^|[^A-Za-z0-9_$.])Element[[:space:]]+[A-Za-z_$][A-Za-z0-9_$]*[[:space:]]*=[[:space:]]*" name "[[:space:]]*\\.[[:space:]]*getRootElement[[:space:]]*\\([[:space:]]*\\)[[:space:]]*;")) {
			declaration = substr(rest, RSTART, RLENGTH)
			rest = substr(rest, RSTART + RLENGTH)

			root = declaration

			sub(/^[^A-Za-z0-9_$]*Element[[:space:]]+/, "", root)
			sub(/[[:space:]]*=.*$/, "", root)

			reads = countUses(scope, root, 1)

			if ((reads == 0) || (reads != (countUses(scope, root, 0) - 1))) {
				return 0
			}

			roots++
		}

		return (roots > 0) && (roots == countUses(scope, name, 0))
	}


	function isWord(text, position, size) {
		return (substr(text, position - 1, 1) !~ /[A-Za-z0-9_$.]/) && (substr(text, position + size, 1) !~ /[A-Za-z0-9_$]/)
	}

	function removeCommentsAndLiterals(line,    c, i, j, size, text) {
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
	{
		all = all removeCommentsAndLiterals($0) "\n"
	}

	END {
		offset = 0
		rest = all

		while (match(rest, /(^|[^A-Za-z0-9_$.])Document[[:space:]]+[A-Za-z_$][A-Za-z0-9_$]*[[:space:]]*=[[:space:]]*((Unsecure)?SAXReaderUtil([[:space:]]*\.[[:space:]]*getSAXReader[[:space:]]*\([[:space:]]*\))?|_?saxReader)[[:space:]]*\.[[:space:]]*read[[:space:]]*\(/)) {
			start = offset + RSTART
			open = offset + RSTART + RLENGTH - 1
			name = substr(rest, RSTART, RLENGTH)
			offset += RSTART + RLENGTH - 1
			rest = substr(rest, RSTART + RLENGTH)

			if (substr(all, start, 1) != "D") {
				start++
			}

			sub(/^[^A-Za-z0-9_$]*Document[[:space:]]+/, "", name)
			sub(/[[:space:]]*=.*$/, "", name)

			closing = closingIndex(open)

			if ((closing == 0) || (argumentsCount(open, closing) != 1) || (substr(all, closing + 1, 2) != ";\n")) {
				continue
			}

			argument = substr(all, open + 1, closing - open - 1)

			gsub(/^[[:space:]]+|[[:space:]]+$/, "", argument)

			prefix = substr(all, 1, start - 1)

			sub(/^.*\n/, "", prefix)

			if ((prefix !~ /^[[:space:]]*$/) || (argument !~ /^[A-Za-z_$][A-Za-z0-9_$]*$/) || (all !~ ("(^|[^A-Za-z0-9_$.])String[[:space:]]+" argument "[[:space:]]*[,:;=)]"))) {
				continue
			}

			scope = substr(all, closing + 2, blockEnd(closing + 2) - closing - 2)

			if ((scope !~ ("(^|[^A-Za-z0-9_$.])" argument "[[:space:]]*[+]?=[^=]")) && isElementTextsOnly(scope, name)) {
				print FILENAME ":" getRow(start) ": parses XML only to read the texts of child elements of its root"
			}
		}
	}
'

FINDINGS=$(
	bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}" | while IFS= read -r FILE
	do
		[[ -f ${FILE} ]] || continue

		command grep --fixed-strings --quiet 'import com.liferay.portal.kernel.xml.Document;' "${FILE}" || continue

		command grep --fixed-strings --quiet 'DocumentException' "${FILE}" && continue

		LINES=$(git diff --unified=0 "${MERGE_BASE}...HEAD" -- "${FILE}" | awk "${ADDED}")

		[[ -z ${LINES} ]] && continue

		awk "${SCAN}" "${FILE}" | while IFS= read -r FINDING
		do
			LINE=${FINDING#"${FILE}":}
			LINE=${LINE%%:*}

			if command grep --fixed-strings --line-regexp --quiet "${LINE}" <<< "${LINES}"
			then
				echo "${FINDING}"
			fi
		done
	done
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind, so a clean branch and a branch with findings can both exit 0.

Run the command from `${REPO_ROOT}`, with the branch checked out and the working tree clean. The scan reads the files of the working tree and compares their paths with the diff, which names every path from the repository root, so from any directory below it, no path matches and the check passes without reporting anything.

Use `while read` rather than a `for` over an unquoted substitution, so a path holding whitespace stays one file rather than splitting into several.

An empty `${FINDINGS}` means that the branch adds no parse that only reads the texts of child elements of its root, and the validation passes.

Otherwise, read the code that each line names. A line is not a finding when the XML can hold text that `XMLUtil.getElementText` reads differently from the parser: an element with attributes, a CDATA section, a reference other than `&amp;`, `&lt;`, and `&gt;`, or an element of the same name that comes before the child the code reads. Every other line is a finding, and the validation fails. Report the message below to the developer with one row for each finding and return it as the failure note.

```markdown
**XML is parsed only to read the texts of child elements of its root.** Read each text with `XMLUtil.getElementText(xml, elementName)` instead of parsing the XML, and drop the `Document` and `Element` variables. Every parse builds a new parser, which takes class loading locks that queue request threads under load. For additional questions, use #t-core-infrastructure on Slack.

| File | Line | Fix |
| --- | --- | --- |
| <file> | <line> | <the XMLUtil.getElementText call for each text that this code reads> |
```

## Time Estimate

~1-5 sec for the command (static, no build), plus reading each line it prints.