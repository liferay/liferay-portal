# TransactionTestUtil Usage

Keeps each persistence read that tests make through `TransactionTestUtil.invoke` in one place. When a service method is removed because only tests called it, the tests read the persistence themselves, as in `TransactionTestUtil.invoke(AccountEntryUtil.getPersistence(), accountEntryPersistence -> accountEntryPersistence.findByC_S(companyId, status))`. Such a call site is identified by its persistence and method, here `AccountEntryUtil.findByC_S`, and two rules apply to it:

- A call site inside a test util method needs shared usage, which means two or more classes call that method. A test util method that no class calls is dead code and is removed. A test util method that one test class calls is moved into that test.
- A call site in a test class stays there only while no other test class makes the same call. When two or more test classes call the same persistence and method, the call moves into one test util method that all of them call.

A third rule places a test util method by the entity whose persistence it reads. A method of a class named `*TestUtil` that reads `<Entity>Util.getPersistence()` belongs in `<Entity>TestUtil`, not in the test util of another entity or of an area: a method that reads `ObjectDefinitionSettingUtil.getPersistence()` belongs in `ObjectDefinitionSettingTestUtil`, not in `ObjectDefinitionTestUtil`. The rule applies to the read in any shape, with or without `TransactionTestUtil`, and only to a class named `*TestUtil`.

A test util class is a class in a `-test-util` module, a class in `portal-test`, or a class named `*TestUtil`. Every other class that calls `TransactionTestUtil.invoke` counts as a test class.

The validation judges the branch, not the repository. A repeated call is reported only when the branch changes one of the test classes that make it, a test util method is reported only when the branch changes a line of that method or takes one of its callers away, and a method that reads the persistence of another entity is reported only when the branch changes a line of that method. An offender that already exists on the base and that the branch leaves alone does not fail it.

The scan reads one call shape, `TransactionTestUtil.invoke(<Name>.getPersistence(), <parameter> -> <parameter>.<method>(`, and identifies a persistence by the simple name of its `*Util` class. A call in any other shape, such as one that passes a persistence held in a field, a lambda with a block body, or a `TransactionConfig`, cannot be identified, so the scan lists it as a candidate for judgment when the branch changes it rather than passing it silently. A test util method whose own class names it again, through an overload or a call from another method, is listed the same way, since its callers cannot be counted mechanically. The scan does not report a test class that repeats a call a test util method already wraps, and it counts only the callers that name the test util class, so a caller that reaches the method through a static import or a subclass is not counted.

## Source Formatter Candidate

This validation stands in for a Source Formatter check. Source Formatter catches the same code at format time and in CI without a pr-check run, so the check is the intended home of this rule, and this validation is the guard until it lands. When the check lands in `modules/util/source-formatter`, delete this file and its entry in `SKILL.md`.

- Check: `JavaTransactionTestUtilCheck`
- Source Formatter pull request: https://github.com/ling-alan-huang/liferay-portal/pull/4432
- Ticket: LPD-117183

## Match

`(-test[^/]*/|/test/|/testIntegration/|^portal-test/).*\.java$|TestUtil\.java$`

The match takes test code only, which is any Java file under a module whose name holds `-test`, a `test` or `testIntegration` directory, or `portal-test`, and any `*TestUtil` class wherever it sits.

## Command

This check is static and needs no build. It lists every `TransactionTestUtil.invoke` call in the Java files of the repository, since a call the branch adds can repeat one in a file the branch never touched, and then reports four kinds of lines, only for what the branch changes. A call is identified by the persistence and the method it reads, and it sits either in a test util class, which is a class under a `-test-util` module or `portal-test` or one named `*TestUtil`, or in a test class, which is every other class. It also reads every `*TestUtil` class the branch changes for `<Entity>Util.getPersistence()`, in any shape. It uses `command grep` so the callers are counted by the system grep rather than the shell's `grep` wrapper, which some environments route to another tool with its own defaults.

- A call that two or more test classes make to the same persistence and method, when the branch changes at least one of those test classes. Each changed test class prints as `<path>:<line>: repeats <persistence>.<method>, which is also called at <path>:<line>`, with every other test class that makes the call at the end.
- A call inside a test util method that fewer than two other classes call, when the branch changes a line of that method or leaves it with fewer callers than it had at the merge base. It prints as `<path>:<line>: <class>.<method> wraps <persistence>.<method> and no other class calls it` or `<path>:<line>: <class>.<method> wraps <persistence>.<method> and only <path> calls it`.
- A method of a `*TestUtil` class that reads `<Entity>Util.getPersistence()` while the class is not named `<Entity>TestUtil`, when the branch changes a line of that method. It prints as `<path>:<line>: <class>.<method> reads <Entity>Util.getPersistence() and belongs in <Entity>TestUtil`, where `<line>` is the line of the read. A read in a field initializer or a nested class is not reported, the same as in the Source Formatter check.
- A candidate for judgment, which prints with `candidate,` after the line number: a call in a shape the scan cannot read, a call in a test util class outside a method the scan can name, or a test util method that its own class names again.

A caller of a test util method is another Java file that names the class with its package, or sits in that package, and calls the method on the class or passes it as a method reference. The method of a call site is the run of lines between two blank lines at class level, which is one member in formatted code. The scan drops comments and string literals before it reads a file, so a brace or a call inside one is never counted.

The callers at the merge base are counted only in the files whose diff adds or removes a line that names the test util class or the method, so the base is never searched as a whole.

```bash
SOURCE='
	function findMember(line,    header, k, text) {
		enclosing = "?"
		first = line
		internal = 0

		while ((first > 1) && (depths[first - 2] > 0) && ((depths[first - 1] > 1) || (lines[first - 1] ~ /[^[:space:]]/))) {
			first--
		}

		last = line

		while ((last < NR) && (depths[last] > 1)) {
			last++
		}

		if (depths[line - 1] > 1) {
			header = ""

			for (k = first; k <= line; k++) {
				header = header " " lines[k]

				if (depths[k] > 1) {
					break
				}
			}

			sub(/\{.*/, "", header)

			gsub(/@[A-Za-z0-9_$.]+(\([^()]*\))?/, " ", header)

			if ((header !~ /=|(^|[^[:alnum:]_$])(class|enum|interface|record)[[:space:]]/) && match(header, /[A-Za-z_$][A-Za-z0-9_$]*[[:space:]]*\(/)) {
				enclosing = substr(header, RSTART, RLENGTH - 1)

				gsub(/[[:space:]]/, "", enclosing)

				text = all

				internal = gsub("(^|[^[:alnum:]_$.])" enclosing "[[:space:]]*\\(", "", text) - 1
			}
		}
	}

	function getRow(position,    text) {
		text = substr(all, 1, position)

		return 1 + gsub(/\n/, "", text)
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
		text = removeCommentsAndLiterals($0)
		all = all text "\n"
		lines[NR] = text
		depth += gsub(/\{/, "", text) - gsub(/\}/, "", text)
		depths[NR] = depth
	}
'

CALL_SITES="${SOURCE}"'
	END {
		p = 0
		rest = all

		while (match(rest, /TransactionTestUtil[[:space:]]*\.[[:space:]]*invoke[[:space:]]*\(/)) {
			start = p + RSTART
			p += RSTART + RLENGTH - 1
			rest = substr(rest, RSTART + RLENGTH)

			if (substr(all, start - 1, 1) ~ /[[:alnum:]_$]/) {
				continue
			}

			level = 0

			for (i = p + 1; i <= length(all); i++) {
				c = substr(all, i, 1)

				if (c == "(") {
					level++
				}
				else if ((c == ")") && (level-- == 0)) {
					break
				}
			}

			finder = "?"
			line = getRow(start)
			persistence = "?"
			text = substr(all, p + 1, i - p - 1)

			if (match(text, /^[[:space:]]*[A-Za-z_$][A-Za-z0-9_$]*[[:space:]]*\.[[:space:]]*getPersistence[[:space:]]*\([[:space:]]*\)[[:space:]]*,[[:space:]]*[A-Za-z_$][A-Za-z0-9_$]*[[:space:]]*->[[:space:]]*[A-Za-z_$][A-Za-z0-9_$]*[[:space:]]*\.[[:space:]]*[A-Za-z_$][A-Za-z0-9_$]*[[:space:]]*\(/)) {
				text = substr(text, 1, RLENGTH - 1)

				gsub(/[[:space:]]/, "", text)

				split(text, sides, "->")
				split(sides[2], call, ".")

				if (substr(sides[1], index(sides[1], ",") + 1) == call[1]) {
					finder = call[2]
					persistence = substr(sides[1], 1, index(sides[1], ".") - 1)
				}
			}

			findMember(line)

			print FILENAME "\t" line "\t" getRow(i) "\t" persistence "\t" finder "\t" enclosing "\t" first "\t" last "\t" internal
		}
	}
'

CALLS='
	{
		text = text $0 "\n"
	}

	END {
		exit !(((index(text, package "." name) > 0) || (index(text, "package " package ";") > 0)) && match(text, "(^|[^[:alnum:]_$])" name "[[:space:]]*(\\.[[:space:]]*" method "[[:space:]]*\\(|::[[:space:]]*" method "([^[:alnum:]_$]|$))"))
	}
'

DUPLICATES='
	BEGIN {
		FS = "\t"
	}

	NR == FNR {
		changed[$0] = 1

		next
	}

	(NF == 9) && ($4 != "?") && ($1 !~ /-test-util\/|^portal-test\/|TestUtil\.java$/) {
		key = $4 "." $5

		if (!((key, $1) in sites)) {
			classes[key] = classes[key] "\n" $1
			counts[key]++
			sites[key, $1] = $2
		}
	}

	END {
		for (key in counts) {
			if (counts[key] < 2) {
				continue
			}

			size = split(substr(classes[key], 2), list, "\n")

			for (i = 1; i <= size; i++) {
				if (!(list[i] in changed)) {
					continue
				}

				others = ""

				for (j = 1; j <= size; j++) {
					if (j != i) {
						others = others ", " list[j] ":" sites[key, list[j]]
					}
				}

				print list[i] ":" sites[key, list[i]] ": repeats " key ", which is also called at " substr(others, 3)
			}
		}
	}
'

LOOKUPS="${SOURCE}"'
	END {
		p = 0
		rest = all

		while (match(rest, /[A-Za-z_$][A-Za-z0-9_$]*Util[[:space:]]*\.[[:space:]]*getPersistence[[:space:]]*\([[:space:]]*\)/)) {
			entity = substr(rest, RSTART, RLENGTH)
			line = getRow(p + RSTART)
			p += RSTART + RLENGTH - 1
			rest = substr(rest, RSTART + RLENGTH)

			sub(/Util[[:space:]]*\.[^.]*$/, "", entity)

			findMember(line)

			print FILENAME "\t" line "\t" entity "\t" enclosing "\t" first "\t" last
		}
	}
'

TOUCHED='
	/^@@/ {
		size = split(substr($3, 2), range, ",")

		count = (size > 1) ? range[2] + 0 : 1
		start = range[1] + 0

		if (count == 0) {
			if ((start >= first) && (start < last)) {
				touched = 1
			}
		}
		else if ((start <= last) && ((start + count - 1) >= first)) {
			touched = 1
		}
	}

	END {
		exit !touched
	}
'

CHANGED=$(bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}")

READS=$(
	printf '%s\n' "${CHANGED}" | while IFS= read -r FILE
	do
		[[ ${FILE} == *TestUtil.java && -f ${FILE} ]] || continue

		awk "${LOOKUPS}" "${FILE}"
	done
)

SITES=$(
	git grep --files-with-matches --fixed-strings --word-regexp 'TransactionTestUtil' -- '*.java' | while IFS= read -r FILE
	do
		[[ ${FILE} == */TransactionTestUtil.java ]] && continue

		awk "${CALL_SITES}" "${FILE}"
	done
)

FINDINGS=$(
	{
		awk "${DUPLICATES}" <(printf '%s\n' "${CHANGED}") <(printf '%s\n' "${SITES}")

		while IFS=$'\t' read -r FILE LINE CLOSE PERSISTENCE FINDER ENCLOSING FIRST LAST INTERNAL
		do
			[[ -z ${FILE} ]] && continue

			if [[ ${PERSISTENCE} == "?" ]]
			then
				if git diff --unified=0 "${MERGE_BASE}...HEAD" -- "${FILE}" | awk -v first="${LINE}" -v last="${CLOSE}" "${TOUCHED}"
				then
					echo "${FILE}:${LINE}: candidate, calls TransactionTestUtil.invoke in a shape the scan cannot read"
				fi

				continue
			fi

			[[ ${FILE} == *-test-util/* || ${FILE} == portal-test/* || ${FILE} == *TestUtil.java ]] || continue

			if [[ ${ENCLOSING} == "?" ]]
			then
				if git diff --unified=0 "${MERGE_BASE}...HEAD" -- "${FILE}" | awk -v first="${LINE}" -v last="${CLOSE}" "${TOUCHED}"
				then
					echo "${FILE}:${LINE}: candidate, calls ${PERSISTENCE}.${FINDER} outside a method the scan can name"
				fi

				continue
			fi

			CLASS=$(basename "${FILE}" .java)
			FORMER=""
			TOUCHES=false

			if git diff --unified=0 "${MERGE_BASE}...HEAD" -- "${FILE}" | awk -v first="${FIRST}" -v last="${LAST}" "${TOUCHED}"
			then
				TOUCHES=true
			else
				FORMER=$(git diff --name-only --no-renames -G"(^|[^[:alnum:]_\$])(${CLASS}[[:space:]]*(\\.|::)|${ENCLOSING}[[:space:]]*\\()" "${MERGE_BASE}...HEAD" -- '*.java')

				[[ -z ${FORMER} ]] && continue
			fi

			PACKAGE=$(sed -e 's/^package \(.*\);.*$/\1/p' -n "${FILE}")

			CALLERS=$(
				git grep --files-with-matches --fixed-strings --word-regexp "${CLASS}" -- '*.java' | while IFS= read -r CALLER
				do
					if [[ ${CALLER} != "${FILE}" ]] &&
						awk -v method="${ENCLOSING}" -v name="${CLASS}" -v package="${PACKAGE}" "${CALLS}" "${CALLER}"
					then
						echo "${CALLER}"
					fi
				done
			)

			COUNT=$(command grep --count --regexp='.' <<< "${CALLERS}")

			[ ${COUNT} -ge 2 ] && continue

			if [[ ${TOUCHES} == false ]]
			then
				AFTER=0
				BEFORE=0

				while IFS= read -r CALLER
				do
					[[ ${CALLER} == "${FILE}" ]] && continue

					if [[ -f ${CALLER} ]] &&
						awk -v method="${ENCLOSING}" -v name="${CLASS}" -v package="${PACKAGE}" "${CALLS}" "${CALLER}"
					then
						((++AFTER))
					fi

					if git show "${MERGE_BASE}:${CALLER}" 2> /dev/null | awk -v method="${ENCLOSING}" -v name="${CLASS}" -v package="${PACKAGE}" "${CALLS}"
					then
						((++BEFORE))
					fi
				done <<< "${FORMER}"

				[ ${BEFORE} -gt ${AFTER} ] || continue
			fi

			if [ ${COUNT} -eq 0 ]
			then
				USAGE="no other class calls it"
			else
				USAGE="only ${CALLERS} calls it"
			fi

			if [ ${INTERNAL} -gt 0 ]
			then
				echo "${FILE}:${LINE}: candidate, ${CLASS}.${ENCLOSING} wraps ${PERSISTENCE}.${FINDER}, ${USAGE}, and ${CLASS} names the method again"
			else
				echo "${FILE}:${LINE}: ${CLASS}.${ENCLOSING} wraps ${PERSISTENCE}.${FINDER} and ${USAGE}"
			fi
		done <<< "${SITES}"

		while IFS=$'\t' read -r FILE LINE ENTITY ENCLOSING FIRST LAST
		do
			[[ -z ${FILE} || ${ENCLOSING} == "?" ]] && continue

			CLASS=$(basename "${FILE}" .java)

			[[ ${CLASS} == "${ENTITY}TestUtil" ]] && continue

			if git diff --unified=0 "${MERGE_BASE}...HEAD" -- "${FILE}" | awk -v first="${FIRST}" -v last="${LAST}" "${TOUCHED}"
			then
				echo "${FILE}:${LINE}: ${CLASS}.${ENCLOSING} reads ${ENTITY}Util.getPersistence() and belongs in ${ENTITY}TestUtil"
			fi
		done <<< "${READS}"
	} | sort --unique
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind, so a clean branch and a branch with findings can both exit 0.

Run the command from `${REPO_ROOT}`, with the branch checked out and the working tree clean. The scan reads the files of the working tree and compares their paths with the diff, which names every path from the repository root, so from any directory below it, no path matches and the check passes without reporting anything.

Use `while read` rather than a `for` over an unquoted substitution, so a path holding whitespace stays one file rather than splitting into several.

When `${FINDINGS}` is empty, the branch adds no repeated call, leaves no test util method without shared usage, and puts no persistence read in the test util of another entity, so the validation passes.

Otherwise, a line without `candidate,` is a finding as printed. A line with it is a candidate, not a finding. Read the code it names, then classify it.

- **A call in a shape the scan cannot read.** Name the persistence and the method the call reads, then judge the call the way the scan judges one it can read. When the call sits in a test class, search the repository for the other calls to that persistence and method with `git grep`, and the candidate is a finding when another test class makes the same call. When the call sits in a test util method, count the classes that call that method, and the candidate is a finding when fewer than two do. A call that reads no persistence, such as one that runs a callable under a `TransactionConfig` or one in the tests of `TransactionTestUtil` itself, is not a finding.
- **A call outside a method the scan can name.** The call sits in a field initializer, an initializer block, or a nested class of a test util class. Count the test classes that reach the call, and the candidate is a finding when fewer than two do.
- **A test util method that its own class names again.** The class holds an overload of the method or calls it from another method. Count the test classes that reach the call site through any of them, and the candidate is a finding when fewer than two do.

When every line is a candidate and none is a finding, the validation passes.

When any line is a finding, the validation fails. Report the message below to the developer with one row for each finding and return it as the failure note.

```markdown
**A `TransactionTestUtil.invoke` call site is not kept in one shared place.** A test util method that wraps a persistence read must be called by two or more classes: remove one that no class calls, and move one that a single test class calls into that test, as a private method when the test calls it more than once. A persistence read that two or more test classes repeat must move into one test util method that all of them call. A test util method that reads `<Entity>Util.getPersistence()` belongs in `<Entity>TestUtil`. For additional questions, use #t-core-infrastructure on Slack.

| File | Line | Call | Fix |
| --- | --- | --- | --- |
| <file> | <line> | <persistence>.<method>, or <Entity>Util.getPersistence() for a read in the wrong test util | <remove the test util method, inline it into the named test, share one test util method with the named tests, or move the method into the test util of its entity> |
```

## Time Estimate

~5-20 sec for the command (static, no build), plus reading each candidate.