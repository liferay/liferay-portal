# BND Content

Flags content that a branch adds to a `.bnd` file and that builds nothing or the wrong thing: a `-donotcopy` instruction, a `package.json` include, a `test-classes/integration` include in a test module, an included path that does not exist or that `src/main/resources` already ships, a class unpacked by name that does not exist or that the module never references, and a class name in `Export-Package` or `-exportcontents`. A cleanup removed all of these from master, and nothing else stops them from coming back.

## Source Formatter Candidate

This validation stands in for a Source Formatter check. Source Formatter catches the same code at format time and in CI without a pr-check run, so the check is the intended home of this rule, and this validation is the guard until it lands. When the check lands in `modules/util/source-formatter`, delete this file and its entry in `SKILL.md`.

- Check: `BNDDefinitionKeysCheck`, `BNDImportsCheck`, `BNDIncludeResourceCheck`
- Source Formatter pull request: https://github.com/ling-alan-huang/liferay-portal/pull/4399
- Ticket: LPD-113241

## Match

`\.bnd$ &! (^|/)src/`

## Command

This check is static and needs no build. It walks each changed `.bnd` file and checks only the clauses that start on a line the branch diff adds, so content already on master never fails the branch. A clause is one comma separated entry of an instruction, split the way bnd splits it, outside quotes and braces, across continued lines. It applies the rules below and prints each finding as `<path>:<line>: <message>`, with the message Source Formatter prints for it, where `<line>` is the line the clause starts on, or the line of the `-donotcopy` instruction. It reads the module's other files from the working tree, as Source Formatter does, and the module is the directory of the `.bnd` file. It uses `command grep` so those files are searched by the system grep rather than the shell's `grep` wrapper, which some environments route to another tool with its own defaults.

- In a `bnd.bnd`, a `-donotcopy` instruction.
- In a `bnd.bnd`, a clause of `-includeresource`, `-includeresource.<name>`, or `Include-Resource` that includes `package.json`, or that includes `test-classes/integration` in a `*-test` module whose `build.gradle` does not enable its `jar` task again.
- In a `bnd.bnd`, an included path that does not exist, or that lies in a build output directory (`bin`, `build`, `classes`, `lib`, `node_modules`, `test-classes`, or `tmp` of a module), unless that directory is the module's own `build`, `classes`, or `tmp`, or its own `lib` holding a `-ws.jar` built from `wsdl`, a `-xbean.jar` built from `xsd`, or what its `build.gradle` copies `into "lib"`. An optional path, a path with a macro, a `test-classes/integration` path, a jar name or classpath pattern without a slash, and the jar of a core module are not checked. An existing path inside `src/main/resources` (`src/testIntegration/resources` in a `*-test` module) that lands at the same place in the jar is reported as included twice.
- In a `bnd.bnd` outside `modules/sdk` and `modules/util`, a class unpacked by name (`@<jar>!/<path>.class`) whose source is missing from the core module or project dependency that builds the jar, or that the module's sources (`src/main`, `src/testIntegration` in a `*-test` module, `src` in a core module) never name: no `.ftl`, `.java`, `.js`, `.jsp`, `.jspf`, `.properties`, `.tag`, `.tld`, or `.xml` file holds its class name or its path, and no Java file that is in its package or imports its package with `.*` holds its simple name as a word.
- In any `.bnd`, an entry of the first `Export-Package` or `-exportcontents` whose last segment starts with an uppercase letter, unless the module has a `wsdl` or `xsd` directory or a directory of that package under `src`, `src/main/java`, or `src/main/resources`.

```bash
CLAUSES='
	function export(line, name,    last) {
		sub(/;.*/, "", name)

		name = trim(name)

		if ((name ~ /^!/) || index(name, "${")) {
			return
		}

		sub(/\*$/, "", name)
		sub(/\.$/, "", name)

		last = name

		sub(/.*\./, "", last)

		if (last ~ /^[[:upper:]]/) {
			print line "\tE\t" key "\t" name
		}
	}

	function flush() {
		if ((start in added) && (trim(clause) != "")) {
			if (key == "include") {
				include(start, trim(clause))
			}
			else {
				export(start, trim(clause))
			}
		}

		clause = ""
		start = 0
	}

	function include(line, name,    entry, jar, optional, path, position, source, target) {
		if ((name ~ /^\{/) && match(name, /\}[^}]*$/)) {
			name = substr(name, 2, RSTART - 2) substr(name, RSTART + 1)
		}

		sub(/;.*/, "", name)

		name = trim(name)
		source = name
		target = ""

		position = index(name, "=")

		if (position > 0) {
			source = trim(substr(name, position + 1))
			target = trim(substr(name, 1, position - 1))
		}

		optional = (source ~ /^-/)

		if (optional) {
			source = substr(source, 2)
		}

		if ((source !~ /^@/) && (source ~ /(^|\/)package\.json$/)) {
			print line "\tJ"
		}

		path = source

		if (path ~ /^@/) {
			path = substr(path, 2)

			sub(/!\/.*/, "", path)
		}

		if (!optional && (path != "") && ((path ~ /\//) || ((source !~ /^@/) && (path !~ /[*[]|\.(bin|jar|mmdb|zip)$/))) && !index(path, "${") && !index(path, "test-classes/integration")) {
			gsub(/^\/+|\/+$/, "", target)

			print line "\tP\t" path "\t" normalize(module "/" path) "\t" target
		}

		if (index(source, "test-classes/integration") && (file ~ /-test\/bnd\.bnd$/)) {
			print line "\tT"
		}

		position = index(source, "!/")
		entry = substr(source, position + 2)

		if ((source ~ /^@/) && (position > 0) && (file !~ /(^|\/)modules\/(sdk|util)\//) && (entry ~ /^[[:alnum:]_\/]+(\$[[:alnum:]_$]+)?\.class$/)) {
			sub(/(\$[[:alnum:]_$]+)?\.class$/, "", entry)

			if (path ~ /\//) {
				jar = normalize(module "/" path)
			}
			else {
				jar = path

				sub(/\.jar$/, "", jar)
				sub(/-[*[0-9].*$/, "", jar)
			}

			print line "\tU\t" jar "\t" entry
		}
	}

	function normalize(path,    count, i, parts, result, size) {
		count = split(path, parts, "/")
		size = 0

		for (i = 1; i <= count; i++) {
			if ((parts[i] == "..") && (size > 0) && (result[size] != "..")) {
				size--
			}
			else if ((parts[i] != "") && (parts[i] != ".")) {
				result[++size] = parts[i]
			}
		}

		path = "."

		for (i = 1; i <= size; i++) {
			path = (i == 1) ? result[i] : path "/" result[i]
		}

		return path
	}

	function trim(text) {
		gsub(/^[[:space:]]+|[[:space:]]+$/, "", text)

		return text
	}

	BEGIN {
		count = split(lines, list, " ")

		for (i = 1; i <= count; i++) {
			added[list[i]] = 1
		}

		bnd = (file ~ /(^|\/)bnd\.bnd$/)
		module = file

		if (!sub(/\/[^\/]+$/, "", module)) {
			module = "."
		}
	}

	bnd && (NR in added) && /^-donotcopy(\.[A-Za-z.-]*)?[:=]/ {
		print NR "\tD"
	}

	{
		text = $0

		if (continued) {
			sub(/^[[:space:]]+/, "", text)
		}
		else {
			key = ""

			if (bnd && match(text, /^(-includeresource(\.[^[:space:]:=]+)?|Include-Resource)[:=]/)) {
				key = "include"
			}
			else if (match(text, /^(-exportcontents|Export-Package):/) && !(substr(text, 1, RLENGTH - 1) in seen)) {
				key = substr(text, 1, RLENGTH - 1)
				seen[key] = 1
			}

			if (key != "") {
				text = substr(text, RLENGTH + 1)
			}

			clause = ""
			depth = 0
			quote = ""
			start = 0
		}

		continued = (text ~ /\\$/)

		if (continued) {
			text = substr(text, 1, length(text) - 1)
		}

		if (key == "") {
			next
		}

		for (i = 1; i <= length(text); i++) {
			c = substr(text, i, 1)

			if ((c == ",") && (depth == 0) && (quote == "")) {
				flush()

				continue
			}

			if (quote != "") {
				if (c == quote) {
					quote = ""
				}
			}
			else if ((c == "\"") || (c == "\047")) {
				quote = c
			}
			else if (c == "{") {
				depth++
			}
			else if (c == "}") {
				depth--
			}

			if (!start && (c !~ /[[:space:]]/)) {
				start = NR
			}

			clause = clause c
		}

		if (!continued) {
			flush()
		}
	}

	END {
		flush()
	}
'

JAR_ENABLED='
	/^jar \{/ {
		inside = 1

		$0 = substr($0, index($0, "{") + 1)
	}

	inside {
		text = $0

		if (match(text, /[{}]/)) {
			inside = 0
			text = substr(text, 1, RSTART - 1)
		}

		if (text ~ /(^|[^[:alnum:]_])enabled = true/) {
			found = 1

			exit
		}
	}

	END {
		exit !found
	}
'

PROJECTS='
	{
		path = $2

		gsub(/:/, "/", path)

		file = root path "/bnd.bnd"

		while ((getline text < file) > 0) {
			if (sub(/^Bundle-SymbolicName:[[:space:]]*/, "", text)) {
				sub(/[[:space:]]*(;.*)?$/, "", text)

				if (text == name) {
					print root path "/src/main/java"
				}

				break
			}
		}

		close(file)
	}
'

FINDINGS=$(
	bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}" | while IFS= read -r FILE
	do
		[[ -f ${FILE} ]] || continue

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
				' \
				| paste -d ' ' -s -
		)

		MODULE_DIR=$(dirname "${FILE}")

		awk -v file="${FILE}" -v lines="${ADDED}" "${CLAUSES}" "${FILE}" | while IFS=$'\t' read -r LINE KIND ARG1 ARG2 ARG3
		do
			PREFIX="${FILE}:${LINE}:"

			if [[ ${KIND} == D ]]
			then
				echo "${PREFIX} Do not use \"-donotcopy\" in bnd.bnd"
			elif [[ ${KIND} == E ]]
			then
				for DIR in "src/${ARG2//.//}" "src/main/java/${ARG2//.//}" "src/main/resources/${ARG2//.//}" wsdl xsd
				do
					[[ -d ${MODULE_DIR}/${DIR} ]] && continue 2
				done

				echo "${PREFIX} \"${ARG2}\" in ${ARG1} is a class name, use its package name"
			elif [[ ${KIND} == J ]]
			then
				echo "${PREFIX} Do not include \"package.json\", Gradle already includes it"
			elif [[ ${KIND} == P ]]
			then
				OUTPUT=""
				PARENT=$(dirname "${ARG2}")

				if [[ ${ARG2##*/} == "${PARENT##*/}.jar" ]] &&
					[[ -e ${PARENT}/build.xml ]]
				then
					continue
				fi

				DIR=${ARG2}

				while [[ -z ${OUTPUT} ]] &&
					[[ ${DIR} != . ]] &&
					[[ ${DIR} != .. ]] &&
					[[ ${MODULE_DIR}/ != "${DIR}"/* ]]
				do
					PARENT=$(dirname "${DIR}")

					if [[ ${DIR##*/} =~ ^(bin|build|classes|lib|node_modules|test-classes|tmp)$ ]] &&
						[[ -e ${PARENT}/bnd.bnd ||
							-e ${PARENT}/build.gradle ||
							-e ${PARENT}/build.xml ]]
					then
						OUTPUT=${DIR}
					fi

					DIR=${PARENT}
				done

				if [[ -z ${OUTPUT} ]] &&
					[[ -e ${ARG2} ]]
				then
					RESOURCES=src/main/resources

					if [[ ${FILE} == *-test/bnd.bnd ]]
					then
						RESOURCES=src/testIntegration/resources
					fi

					TARGET=${ARG3}

					if [[ -z ${TARGET} ]] &&
						[[ ! -d ${ARG2} ]]
					then
						TARGET=${ARG2##*/}
					fi

					if [[ ${ARG2} == "${MODULE_DIR}/${RESOURCES}" &&
							-z ${TARGET} ]] ||
						[[ ${ARG2} == "${MODULE_DIR}/${RESOURCES}/${TARGET}" ]]
					then
						echo "${PREFIX} Do not include \"${ARG1}\", Gradle already includes it from \"${RESOURCES}\""
					fi

					continue
				fi

				if [[ ${OUTPUT%/*} == "${MODULE_DIR}" ]]
				then
					if [[ ${OUTPUT##*/} =~ ^(build|classes|tmp)$ ]]
					then
						continue
					elif [[ ${OUTPUT##*/} == lib ]]
					then
						if [[ ${ARG2} == *-ws.jar &&
								-e ${MODULE_DIR}/wsdl/$(basename "${ARG2}" -ws.jar).wsdl ]] ||
							[[ ${ARG2} == *-xbean.jar &&
								-d ${MODULE_DIR}/xsd ]] ||
							command grep --extended-regexp --quiet '^[[:space:]]*into "lib"$' "${MODULE_DIR}/build.gradle" 2> /dev/null
						then
							continue
						fi
					fi
				fi

				echo "${PREFIX} Included path \"${ARG1}\" does not exist in the repository"
			elif [[ ${KIND} == T ]]
			then
				if ! awk "${JAR_ENABLED}" "${MODULE_DIR}/build.gradle" 2> /dev/null
				then
					echo "${PREFIX} Do not use \"test-classes/integration\" in bnd.bnd in test modules"
				fi
			elif [[ ${KIND} == U ]]
			then
				CLASS=${ARG2//\//.}
				CORE_DIR=${ARG1#com.liferay.}
				CORE_DIR=${CORE_DIR//./-}
				PARENT=$(dirname "${ARG1}")
				ROOT=${MODULE_DIR}
				SOURCE_DIR=""

				while [[ ! -e ${ROOT}/settings.gradle && ${ROOT} != . ]]
				do
					ROOT=$(dirname "${ROOT}")
				done

				if [[ ${ARG1} == */* ]]
				then
					if [[ ${ARG1##*/} == "${PARENT##*/}.jar" ]] &&
						[[ -e ${PARENT}/build.xml ]]
					then
						SOURCE_DIR=${PARENT}/src
					fi
				elif [[ ${ARG1} == com.liferay.* ]] &&
					[[ -e ${CORE_DIR}/build.xml ]]
				then
					SOURCE_DIR=${CORE_DIR}/src
				else
					SOURCE_DIR=$(
						command grep --extended-regexp --only-matching 'project\((path: )?"[^"]+"' "${MODULE_DIR}/build.gradle" 2> /dev/null \
							| awk -F '"' -v name="${ARG1}" -v root="${ROOT}" "${PROJECTS}"
					)
				fi

				if [[ ! -z ${SOURCE_DIR} ]] &&
					[[ ! -e ${SOURCE_DIR}/${ARG2}.java ]]
				then
					echo "${PREFIX} Unpacked class \"${CLASS}\" does not exist in its jar"

					continue
				fi

				SOURCE_DIRS=("${MODULE_DIR}/src/main")

				if [[ -e ${MODULE_DIR}/build.xml ]]
				then
					SOURCE_DIRS=("${MODULE_DIR}/src")
				elif [[ ${FILE} == *-test/bnd.bnd ]]
				then
					SOURCE_DIRS+=("${MODULE_DIR}/src/testIntegration")
				fi

				if command grep \
					--fixed-strings \
					--include='*.ftl' \
					--include='*.java' \
					--include='*.js' \
					--include='*.jsp' \
					--include='*.jspf' \
					--include='*.properties' \
					--include='*.tag' \
					--include='*.tld' \
					--include='*.xml' \
					--quiet \
					--recursive \
					--regexp="${CLASS}" \
					--regexp="${ARG2}" \
					"${SOURCE_DIRS[@]}" 2> /dev/null
				then
					continue
				fi

				while IFS= read -r JAVA_FILE
				do
					if command grep --fixed-strings --quiet --regexp="${CLASS##*.}" --word-regexp "${JAVA_FILE}"
					then
						continue 2
					fi
				done < <(command grep --files-with-matches --fixed-strings --include='*.java' --recursive --regexp="package ${CLASS%.*};" --regexp="import ${CLASS%.*}.*;" "${SOURCE_DIRS[@]}" 2> /dev/null)

				echo "${PREFIX} Unpacked class \"${CLASS}\" is not referenced by the module"
			fi
		done
	done
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind, so it reports on the diff's last `.bnd` file rather than on the diff. Each iteration ends on a `continue` or on the loop over the clauses of its file, and every path through that loop ends on an `echo`, a `continue`, or an `if` that exits 0, so a clean branch and a branch with findings both exit 0.

Run the loop from `${REPO_ROOT}`. The changed paths are relative to the repository root, so from any directory below it, every file test fails and the check passes without scanning anything.

Use `while read` rather than a `for` over an unquoted substitution, so a path holding whitespace stays one file rather than splitting into several.

When `${FINDINGS}` is empty, the branch adds no such content, so the validation passes.

Otherwise, the validation fails. Report the message below to the developer with one row for each finding, giving the path, the line, and the message as the finding prints them, and return it as the failure note.

```markdown
**`.bnd` content builds nothing or the wrong thing.** Each line below adds an instruction or a clause that either changes nothing in the bundle or includes, unpacks, or exports something other than what it names. Remove it, or point it at what the module needs.

| File | Line | Problem |
| --- | --- | --- |
| <file> | <line> | <message> |
```

## Time Estimate

~10-30 sec (static, no build).