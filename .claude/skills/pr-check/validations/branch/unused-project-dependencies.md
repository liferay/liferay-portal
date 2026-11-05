# Unused Project Dependencies

Flags a `project(":...")` dependency in the `compileOnly`, `testCompileOnly`, `testIntegrationCompileOnly`, or `testIntegrationImplementation` configuration of a module's `build.gradle` that no source of its configuration uses, when the branch adds the line or removes the last source that used it. An unused project dependency still puts the provider in the module's build order and on its compile classpath, so every change to the provider rebuilds the module for nothing.

## Source Formatter Candidate

This validation stands in for a Source Formatter check. Source Formatter catches the same code at format time and in CI without a pr-check run, so the check is the intended home of this rule, and this validation is the guard until it lands. When the check lands in `modules/util/source-formatter`, delete this file and its entry in `SKILL.md`.

- Check: `GradleUnusedProjectDependenciesCheck`
- Source Formatter pull request: https://github.com/ling-alan-huang/liferay-portal/pull/4408
- Ticket: LPD-113244

## Match

`^modules/.+(/(bnd\.bnd|build\.gradle)|\.(groovy|java|jsp|jspf|jspx|kt|tag|tagf))$`

## Command

This check is static and needs no build. A changed `build.gradle` can add a dependency line, and a changed source file or `bnd.bnd` can remove the last use of one, so the scan walks each module that holds a changed path: the nearest directory above the path with a `build.gradle`, stopping at a directory with only a `bnd.bnd`. Like the Source Formatter check, it takes a module under `modules/apps`, `modules/core`, or `modules/dxp/apps`, or one under `modules/dxp`, `modules/test`, or `modules/util` with a `.lfrbuild-portal` marker, and skips a module whose `build.gradle` generates sources with `buildWSDL` or `buildXSD`. It uses `command grep` so the files are filtered by the system grep rather than the shell's `grep` wrapper, which some environments route to another tool with its own defaults.

The scan reads each line of the module's `build.gradle` that is exactly `<configuration> project("<project>")` in one of the four configurations. It skips a configuration that the file reads as `configurations.<name>`, a provider outside `modules/apps`, `modules/core`, `modules/dxp`, `modules/test`, and `modules/util`, and a provider with no class under `src/main/java`, `src/main/groovy`, or `src/main/kotlin` and no package in `Export-Package` or `-exportcontents`. It checks only a line the branch adds, one missing from the `build.gradle` of the merge base (followed through a rename), and a line whose provider was named by a line that the branch removes from the module, so a line that master already leaves unused never fails the branch.

A line is used when a file its configuration feeds names the provider. `compileOnly` feeds every file of the module, `testCompileOnly` feeds `src/test`, and the `testIntegration` configurations feed `src/testIntegration`, while a file under any other directory of `src` feeds none. A Java, Groovy, Kotlin, JSP, or resource file names the provider when it imports, names, or declares a package that the provider has a class in or exports, wildcards included, and a JSP also when it declares the URI of a TLD of the provider. The module's `bnd.bnd` names it for every configuration, with one of its packages in a package instruction, its name in a resource instruction such as `-includeresource`, its symbolic name in `Fragment-Host`, or one of its classes in any other instruction. A name in a comment also counts, which only makes the scan more lenient than the check.

The JSP compiler loads the tag handlers of each taglib a JSP declares, and the classes those handlers use, so a `compileOnly` line is also used when the sources that hold the TLD of such a taglib, such as `util-taglib`, name a package of the provider. The Java compiler can also need a provider that the module never names, such as a supertype of a class the module uses, or a type in the signature of a method the module calls or overrides, and such a class can only come from a dependency whose own sources name the provider. For each line that is still unused, the scan lists the module's other project dependencies, and the core projects such as `portal-kernel`, whose sources name a package of the provider. Each finding prints as `<path>:<line>: <configuration> project("<project>")`, followed by ` through <dependencies>` when any dependency names the provider, where `<path>` is the module's `build.gradle` and `<line>` is the line of the dependency.

```bash
SCAN='
	function bnd(text,    count, entries, entry, i, key, token, value) {
		if (!match(text, /^[[:space:]]*[-[:alnum:]_.]+[[:space:]]*[:=]/)) {
			return
		}

		key = substr(text, RSTART, RLENGTH - 1)
		value = substr(text, RSTART + RLENGTH)

		gsub(/[[:space:]]/, "", key)

		if (key ~ /^(-conditionalpackage|-exportcontents|-includepackage|-privatepackage|Conditional-Package|DynamicImport-Package|Export-Package|Import-Package|Private-Package)$/) {
			gsub(/"[^"]*"/, "", value)

			count = split(value, entries, ",")

			for (i = 1; i <= count; i++) {
				entry = entries[i]

				sub(/;.*/, "", entry)
				gsub(/[[:space:]]/, "", entry)

				if (entry ~ /^[[:alnum:]_.]+$/) {
					reference(entry)
				}
				else if (entry ~ /^[[:alnum:]_.]+[.][*]$/) {
					emit("t:" substr(entry, 1, length(entry) - 2))
				}
			}
		}
		else if (key ~ /^(-classpath|-includeresource|Bundle-ClassPath|Include-Resource)$/) {
			while (match(value, /[[:alnum:]_.-]+/)) {
				token = substr(value, RSTART, RLENGTH)
				value = substr(value, RSTART + RLENGTH)

				if (value ~ /^[*[]/) {
					emit("n:" token)
				}

				if (token ~ /[.]jar$/) {
					emit("n:" substr(token, 1, length(token) - 4))
				}

				for (i = 2; i <= length(token); i++) {
					if (substr(token, i, 1) == "-") {
						emit("n:" substr(token, 1, i - 1))
					}
				}
			}
		}
		else if (key == "Fragment-Host") {
			sub(/;.*/, "", value)
			gsub(/[[:space:]]/, "", value)

			emit("n:" value)
		}
		else {
			names(value)
		}
	}

	function classify(path,    name, relative) {
		relative = substr(path, length(module) + 2)
		scope = "main"

		if (relative ~ /^src\/testIntegration\//) {
			scope = "testIntegration"
		}
		else if (relative ~ /^src\/test\//) {
			scope = "test"
		}
		else if ((relative ~ /^src\//) && (relative !~ /^src\/main\//)) {
			return ""
		}

		if (relative == "bnd.bnd") {
			scope = "bnd"

			return "bnd"
		}

		if (relative ~ /[.](groovy|java|kt)$/) {
			return "code"
		}

		if (relative ~ /[.](jsp|jspf|jspx|tag|tagf)$/) {
			return "jsp"
		}

		name = relative

		sub(/.*\//, "", name)

		if ((name !~ /^Language/) && ((relative ~ /\/META-INF\/services\//) || (relative ~ /[.](MF|cfg|config|ftl|properties|tld|tmpl|vm|xml|xsl|yaml|yml)$/))) {
			return "resource"
		}

		return ""
	}

	function consume(text,    name, rest) {
		if ((type == "code") && match(text, /^[[:space:]]*package[[:space:]]+[[:alnum:]_.]+/)) {
			name = substr(text, RSTART, RLENGTH)

			sub(/^[[:space:]]*package[[:space:]]+/, "", name)

			reference(name)
		}

		rest = text

		while ((type == "jsp") && match(rest, /uri[[:space:]]*=[[:space:]]*"[^"]*"/)) {
			name = substr(rest, RSTART, RLENGTH)
			rest = substr(rest, RSTART + RLENGTH)

			sub(/^[^"]*"/, "", name)

			emit("u:" substr(name, 1, length(name) - 1))
		}

		names(text)
	}

	function emit(key) {
		print scope "\t" key
	}

	function flush() {
		if (buffer != "") {
			bnd(buffer)
		}

		if (joined != "") {
			consume(joined)
		}

		buffer = ""
		joined = ""
	}

	function line(text) {
		if (type == "bnd") {
			if (text ~ /\\$/) {
				buffer = buffer substr(text, 1, length(text) - 1)
			}
			else {
				bnd(buffer text)

				buffer = ""
			}

			return
		}

		if (joined != "") {
			sub(/^[[:space:]]+/, "", text)

			text = joined text
			joined = ""
		}

		if (text ~ /[[:alnum:]_$][.][[:space:]]*$/) {
			sub(/[[:space:]]+$/, "", text)

			joined = text

			return
		}

		consume(text)
	}

	function names(text,    count, i, name, prefix, segments) {
		while (match(text, /[[:alpha:]_$][[:alnum:]_$]*([.]([[:alpha:]_$][[:alnum:]_$]*|[*]))+/)) {
			name = substr(text, RSTART, RLENGTH)
			text = substr(text, RSTART + RLENGTH)

			count = split(name, segments, ".")

			if (segments[1] !~ /^[a-z_]/) {
				continue
			}

			prefix = segments[1]

			for (i = 2; i <= count; i++) {
				if (segments[i] ~ /^[A-Z*]/) {
					reference(prefix)

					break
				}

				prefix = prefix "." segments[i]
			}
		}
	}

	function reference(name,    count, i, prefix, segments) {
		emit("p:" name)

		count = split(name, segments, ".")
		prefix = segments[1]

		for (i = 2; i <= count; i++) {
			emit("s:" prefix)

			prefix = prefix "." segments[i]
		}
	}

	diff && /^diff --git / {
		flush()

		header = 1
		type = ""

		next
	}

	diff && header && /^--- / {
		type = classify(substr($0, 7))

		next
	}

	diff && /^@@/ {
		flush()

		header = 0

		next
	}

	diff && !header && /^-/ && (type != "") {
		line(substr($0, 2))
	}

	!diff {
		paths[++size] = $0
	}

	END {
		flush()

		for (i = 1; i <= size; i++) {
			type = classify(paths[i])

			if (type == "") {
				continue
			}

			while ((getline text < paths[i]) > 0) {
				line(text)
			}

			close(paths[i])

			flush()
		}
	}
'

PROVIDE='
	function add(key) {
		keys[key] = 1
	}

	function bnd(text,    count, entries, entry, i, key, value) {
		if (!match(text, /^[[:space:]]*[-[:alnum:]_.]+[[:space:]]*[:=]/)) {
			return
		}

		key = substr(text, RSTART, RLENGTH - 1)
		value = substr(text, RSTART + RLENGTH)

		gsub(/[[:space:]]/, "", key)

		if (key == "Bundle-SymbolicName") {
			sub(/;.*/, "", value)
			gsub(/[[:space:]]/, "", value)

			add("n:" value)
		}

		if (key !~ /^(-exportcontents|Export-Package)$/) {
			return
		}

		gsub(/"[^"]*"/, "", value)

		count = split(value, entries, ",")

		for (i = 1; i <= count; i++) {
			entry = entries[i]

			sub(/;.*/, "", entry)
			gsub(/[[:space:]]/, "", entry)

			if (entry ~ /^[[:alnum:]_.]+$/) {
				provide(entry)
			}
			else if (entry ~ /^[[:alnum:]_.]+[.][*]$/) {
				entry = substr(entry, 1, length(entry) - 2)

				add("p:" entry)
				add("s:" entry)

				provided = 1
			}
		}
	}

	function provide(name,    count, i, prefix, segments) {
		add("p:" name)

		count = split(name, segments, ".")
		prefix = segments[1]

		add("t:" prefix)

		for (i = 2; i <= count; i++) {
			prefix = prefix "." segments[i]

			add("t:" prefix)
		}

		provided = 1
	}

	{
		relative = substr($0, length(provider) + 2)

		if (relative ~ /^src\/main\/(groovy|java|kotlin)\/.+\/[^\/]+[.](groovy|java|kt)$/) {
			sub(/^src\/main\/[a-z]+\//, "", relative)
			sub(/\/[^\/]+$/, "", relative)
			gsub(/\//, ".", relative)

			provide(relative)
		}
		else if ((relative == "bnd.bnd") || (relative ~ /[.]tld$/)) {
			buffer = ""

			while ((getline text < $0) > 0) {
				if (relative == "bnd.bnd") {
					if (text ~ /\\$/) {
						buffer = buffer substr(text, 1, length(text) - 1)
					}
					else {
						bnd(buffer text)

						buffer = ""
					}
				}

				while ((relative != "bnd.bnd") && match(text, /<uri>[^<]*<\/uri>/)) {
					value = substr(text, RSTART + 5, RLENGTH - 11)
					text = substr(text, RSTART + RLENGTH)

					gsub(/[[:space:]]/, "", value)

					add("u:" value)
				}
			}

			close($0)

			if (buffer != "") {
				bnd(buffer)
			}
		}
	}

	END {
		if (!provided) {
			exit
		}

		add("n:" substr(provider, match(provider, /[^\/]+$/)))

		for (key in keys) {
			print key
		}
	}
'

JOIN='
	NR == FNR {
		provided[$0] = 1

		next
	}

	($1 ~ scopes) && ($2 in provided) {
		found = 1

		exit
	}

	END {
		exit !found
	}
'

RENAMES=$(git diff --diff-filter=R --find-renames --name-status "${MERGE_BASE}...HEAD" -- "*build.gradle")

FINDINGS=$(
	bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}" | while IFS= read -r FILE
	do
		DIR=$(dirname "${FILE}")

		until [[ -f ${DIR}/bnd.bnd ]] ||
			[[ -f ${DIR}/build.gradle ]] ||
			[[ ${DIR} != modules/*/* ]]
		do
			DIR=$(dirname "${DIR}")
		done

		if [[ -f ${DIR}/build.gradle ]]
		then
			echo "${DIR}"
		fi
	done | sort --unique | while IFS= read -r MODULE
	do
		if [[ ! ${MODULE} =~ ^modules/(apps|core|dxp/apps)/ ]]
		then
			if [[ ! ${MODULE} =~ ^modules/(dxp|test|util)/ ]] ||
				! compgen -G "${MODULE}/.lfrbuild-portal*" > /dev/null
			then
				continue
			fi
		fi

		LINES=$(
			awk \
				'/^\t(compileOnly|testCompileOnly|testIntegrationCompileOnly|testIntegrationImplementation) project\(":[^"]+"\)$/ { print FNR, $0 }' \
				"${MODULE}/build.gradle"
		)

		if [[ -z ${LINES} ]] ||
			command grep \
				--extended-regexp \
				--quiet \
				--regexp='buildWSDL|buildXSD|wsdlBuilder|xsdBuilder' \
				--word-regexp \
				"${MODULE}/build.gradle"
		then
			continue
		fi

		ORIGIN=$(awk -v path="${MODULE}/build.gradle" '$3 == path { print $2 }' <<< "${RENAMES}")

		BASE=$(git show "${MERGE_BASE}:${ORIGIN:-${MODULE}/build.gradle}" 2> /dev/null)
		KEYS=""
		OWNERS=-
		REMOVED=$(git diff --no-renames "${MERGE_BASE}...HEAD" -- "${MODULE}" | awk -v diff=1 -v module="${MODULE}" "${SCAN}")

		while read -r NUMBER CONFIGURATION DEPENDENCY
		do
			if command grep \
				--extended-regexp \
				--quiet \
				--regexp="configurations\.${CONFIGURATION}([^[:alpha:]]|$)" \
				"${MODULE}/build.gradle"
			then
				continue
			fi

			PROJECT=${DEPENDENCY#project(\"}
			PROJECT=${PROJECT%\")}

			if [[ ! ${PROJECT} =~ ^:(apps|core|dxp|test|util): ]]
			then
				continue
			fi

			ADDED=true

			if command grep \
				--fixed-strings \
				--line-regexp \
				--quiet \
				--regexp=$'\t'"${CONFIGURATION} ${DEPENDENCY}" \
				<<< "${BASE}"
			then
				ADDED=false
			fi

			if [[ ${ADDED} == false ]] &&
				[[ -z ${REMOVED} ]]
			then
				continue
			fi

			PROVIDED=$(git ls-files -- "modules${PROJECT//://}" | awk -v provider="modules${PROJECT//://}" "${PROVIDE}")

			if [[ -z ${PROVIDED} ]]
			then
				continue
			fi

			if [[ ${CONFIGURATION} == compileOnly ]]
			then
				SCOPES="bnd|main|test|testIntegration"
			elif [[ ${CONFIGURATION} == testCompileOnly ]]
			then
				SCOPES="bnd|test"
			else
				SCOPES="bnd|testIntegration"
			fi

			if [[ ${ADDED} == false ]] &&
				! awk -v scopes="^(${SCOPES})$" "${JOIN}" <(echo "${PROVIDED}") <(echo "${REMOVED}")
			then
				continue
			fi

			if [[ -z ${KEYS} ]]
			then
				KEYS=$(git ls-files -- "${MODULE}" | awk -v module="${MODULE}" "${SCAN}" | sort --unique)
			fi

			if awk -v scopes="^(${SCOPES})$" "${JOIN}" <(echo "${PROVIDED}") <(echo "${KEYS}")
			then
				continue
			fi

			PACKAGES=$(sed -e 's/[.]/[.]/g' -e 's/^p://p' -n <<< "${PROVIDED}" | paste -d '|' -s -)
			PREFIXES=$(sed -e 's/[.]/[.]/g' -e 's/^s://p' -n <<< "${PROVIDED}" | paste -d '|' -s -)

			PATTERN="(^|[^[:alnum:]_.])((${PACKAGES})[.][A-Z*]${PREFIXES:+|(${PREFIXES})[.][[:alnum:]_]})"

			if [[ ${CONFIGURATION} == compileOnly ]] &&
				[[ ${OWNERS} == - ]]
			then
				OWNERS=""
				URIS=$(awk '$1 == "main" && $2 ~ /^u:/ { print "<uri>" substr($2, 3) "</uri>" }' <<< "${KEYS}")

				if [[ ! -z ${URIS} ]]
				then
					OWNERS=$(
						git grep --files-with-matches --fixed-strings -f - -- "*.tld" <<< "${URIS}" \
							| awk '
								!/^modules\/sdk\// && !/\/src\/test/ {
									sub(/\/src\/.*/, "/src")

									print
								}
							' \
							| sort --unique
					)
				fi
			fi

			THROUGH=$(
				{
					if [[ ${CONFIGURATION} == compileOnly ]] &&
						[[ ! -z ${OWNERS} ]]
					then
						sed -e 's/^/jsp /' <<< "${OWNERS}"
					fi

					awk '
						match($0, /project\(":[^"]+"\)/) {
							name = substr($0, RSTART + 9, RLENGTH - 11)
							path = name

							gsub(/:/, "/", path)

							print name, "modules" path "/src/main"
						}
					' "${MODULE}/build.gradle"

					printf '%s\n' portal-impl portal-kernel portal-test util-bridges util-java util-slf4j util-taglib | sed -e 's#.*#& &/src#'
				} | while read -r NAME SOURCE
				do
					if [[ ${NAME} != "${PROJECT}" ]] &&
						[[ -d ${SOURCE} ]] &&
						command grep \
							--extended-regexp \
							--include="*.groovy" \
							--include="*.java" \
							--include="*.kt" \
							--include="*.tld" \
							--quiet \
							--recursive \
							--regexp="${PATTERN}" \
							"${SOURCE}"
					then
						echo "${NAME}"
					fi
				done | sort --unique | paste -d ' ' -s -
			)

			if [[ " ${THROUGH} " == *" jsp "* ]]
			then
				continue
			fi

			echo "${MODULE}/build.gradle:${NUMBER}: ${CONFIGURATION} ${DEPENDENCY}${THROUGH:+ through ${THROUGH}}"
		done <<< "${LINES}"
	done
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind, so it reports on the last module it scanned rather than on the diff. Each iteration ends on a `continue` or on the loop over the dependency lines of its module, and every pass of that loop ends on a `continue` or an `echo`, so a clean branch and a branch with findings both exit 0.

Run the loop from `${REPO_ROOT}`. The changed paths are relative to the repository root, so from any directory below it, every file test fails and the check passes without scanning anything.

Use `while read` rather than a `for` over an unquoted substitution, so a path holding whitespace stays one file rather than splitting into several.

When `${FINDINGS}` is empty, the branch adds or leaves no unused project dependency, so the validation passes.

Otherwise, judge each finding that ends with `through`. This is the only judgment the validation leaves to you, because the scan cannot read class hierarchies or method signatures. In the sources of each listed dependency, find the classes that name a package of the provider, which are the packages of its classes under `src/main`. Drop the finding only when one of those classes is one the module uses or extends, or a supertype of one, and a class of the provider is among its supertypes or in the signature of a nonprivate method or constructor that the module calls, overrides, or must implement. Keep every finding without `through`, and every finding for which you find no such class.

When you drop every finding, the validation passes. Otherwise, the validation fails. Report the message below to the developer with one row for each finding you keep, giving the path, the line, and the dependency as the finding prints them, and return it as the failure note.

```markdown
**The branch adds or leaves project dependencies that no source of their configuration uses.** No file that the configuration of each line below feeds names its provider, and an unused project dependency still puts the provider in the module's build order and on its compile classpath. Remove each line below from its `build.gradle`, or move it to `testImplementation` when only unit tests need the provider at run time.

| File | Line | Dependency |
| --- | --- | --- |
| <file> | <line> | <dependency> |
```

## Time Estimate

~10-60 sec (static, no build).