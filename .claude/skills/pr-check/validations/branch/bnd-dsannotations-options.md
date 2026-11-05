# BND DS Annotations Options

Flags a `-dsannotations-options: inherit` that a branch adds to a `bnd.bnd` of a module where no `@Component` class inherits a field or method annotated with `@Activate`, `@Deactivate`, `@Modified`, or `@Reference`. bnd reads inherited members into a component description only when the option is set, so in such a module it changes nothing in the bundle, and it keeps coming back through copied `bnd.bnd` files and project templates.

## Source Formatter Candidate

This validation stands in for a Source Formatter check. Source Formatter catches the same code at format time and in CI without a pr-check run, so the check is the intended home of this rule, and this validation is the guard until it lands. When the check lands in `modules/util/source-formatter`, delete this file and its entry in `SKILL.md`.

- Check: `BNDDSAnnotationsOptionsCheck`
- Source Formatter pull request: https://github.com/ling-alan-huang/liferay-portal/pull/4400
- Ticket: LPD-113242

## Match

`(^|/)bnd\.bnd$ &! (^|/)src/`

## Command

This check is static and needs no build. It walks each changed `bnd.bnd` file and looks only at its first `-dsannotations-options` line, and only when the branch diff adds that line and its comma separated values include `inherit`, so an option already on master never fails the branch. The module is the directory of the `bnd.bnd` file, and the check reads its Java files from the working tree, as Source Formatter does. It uses `command grep` so the Java files and the paths git tracks are searched by the system grep rather than the shell's `grep` wrapper, which some environments route to another tool with its own defaults.

For each `@Component` class of the module, a file under `src/main/java` or `src/testIntegration/java` that carries `@org.osgi.service.component.annotations.Component`, or `@Component` and its import, the check follows the superclass chain. It resolves each superclass name through the imports and the package of the class that extends it, and looks for its source first in the module of that class and then where Source Formatter looks: the `src/main/java` and `src/testIntegration/java` directories under `modules`, and the source directories of `portal-impl`, `portal-kernel`, `portal-test`, `portal-test-integration`, and `util-taglib`. A superclass declares a DS member when a field or method of its body, indented by one tab, carries `@Activate`, `@Deactivate`, `@Modified`, or `@Reference` from `org.osgi.service.component.annotations`. The module needs the option when any chain reaches such a superclass, a superclass whose source is not found and that is not a `jakarta`, `java`, or `javax` class, or a 32nd superclass, so a module that may need it is never reported. A chain ends without need at a class that extends nothing or extends a `jakarta`, `java`, or `javax` class.

When no chain needs the option, the check prints `<path>:<line>: <message>`, with the message Source Formatter prints for it, where `<line>` is the `-dsannotations-options` line.

```bash
CLASS='
	function resolve(name,    first) {
		if (name ~ /^[[:lower:]][[:alnum:]_]*\.([[:lower:]][[:alnum:]_]*\.)+[[:upper:]]/) {
			return name
		}

		first = name

		sub(/\..*/, "", first)

		if (first in imports) {
			return imports[first] substr(name, length(first) + 1)
		}

		return package "." name
	}

	/^package / {
		package = $2

		sub(/;.*/, "", package)
	}

	/^import / && ($2 != "static") {
		name = $2

		sub(/;.*/, "", name)

		simple = name

		sub(/.*\./, "", simple)

		imports[simple] = name
	}

	!declared && /^((abstract|final|non-sealed|private|protected|public|sealed|static|strictfp)[[:space:]]+)*(class|enum|interface|record)[[:space:]]/ {
		declared = 1
	}

	declared && !body {
		declaration = declaration " " $0

		if (index($0, "{")) {
			body = 1

			sub(/\{.*/, "", declaration)

			while (gsub(/<[^<>]*>/, "", declaration)) {
			}

			if (match(declaration, /[[:space:]]extends[[:space:]]+[[:alnum:]_.]+/)) {
				superclass = substr(declaration, RSTART, RLENGTH)

				sub(/.*[[:space:]]/, "", superclass)
			}
		}

		next
	}

	body && members && match($0, /^\t@(org\.osgi\.service\.component\.annotations\.)?(Activate|Deactivate|Modified|Reference)([^[:alnum:]_]|$)/) {
		name = $0

		sub(/^\t@/, "", name)
		sub(/[^[:alnum:]_.].*$/, "", name)

		if (index(name, ".") || (imports[name] == "org.osgi.service.component.annotations." name)) {
			ds = 1

			exit
		}
	}

	END {
		if (ds) {
			print "ds"
		}
		else if (superclass != "") {
			print resolve(superclass)
		}
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

		LINE=$(
			awk -v lines="${ADDED}" '
				BEGIN {
					count = split(lines, list, " ")

					for (i = 1; i <= count; i++) {
						added[list[i]] = 1
					}
				}

				/^-dsannotations-options: / {
					value = $0

					sub(/^-dsannotations-options: /, "", value)

					count = split(value, options, ",")

					for (i = 1; i <= count; i++) {
						gsub(/^[[:space:]]+|[[:space:]]+$/, "", options[i])

						if ((options[i] == "inherit") && (NR in added)) {
							print NR
						}
					}

					exit
				}
			' "${FILE}"
		)

		[[ ! -z ${LINE} ]] || continue

		MODULE_DIR=$(dirname "${FILE}")

		NEEDED=$(
			command grep \
				--files-with-matches \
				--fixed-strings \
				--include='*.java' \
				--recursive \
				--regexp='@Component' \
				--regexp='@org.osgi.service.component.annotations.Component' \
				"${MODULE_DIR}/src/main/java" \
				"${MODULE_DIR}/src/testIntegration/java" 2> /dev/null \
				| while IFS= read -r JAVA_FILE
				do
					if ! command grep --fixed-strings --quiet --regexp='@org.osgi.service.component.annotations.Component' --regexp='import org.osgi.service.component.annotations.Component;' "${JAVA_FILE}"
					then
						continue
					fi

					CLASS_FILE=${JAVA_FILE}
					CLASS_MODULE_DIR=${MODULE_DIR}
					MEMBERS=0

					for (( DEPTH = 0; DEPTH < 32; ++DEPTH ))
					do
						SUPERCLASS=$(awk -v members="${MEMBERS}" "${CLASS}" "${CLASS_FILE}")

						if [[ -z ${SUPERCLASS} || ${SUPERCLASS} =~ ^(jakarta|java|javax)\. ]]
						then
							continue 2
						fi

						if [[ ${SUPERCLASS} == ds ]]
						then
							break
						fi

						CLASS_PATH=${SUPERCLASS//.//}.java
						CLASS_FILE=""

						for DIR in "${CLASS_MODULE_DIR}/src/main/java" "${CLASS_MODULE_DIR}/src/testIntegration/java"
						do
							if [[ ! -z ${CLASS_MODULE_DIR} ]] &&
								[[ -f ${DIR}/${CLASS_PATH} ]]
							then
								CLASS_FILE=${DIR}/${CLASS_PATH}

								break
							fi
						done

						if [[ -z ${CLASS_FILE} ]]
						then
							CLASS_FILE=$(
								git ls-files -- "*/${CLASS_PATH}" \
									| command grep \
										--extended-regexp \
										--max-count=1 \
										"^(modules/.+/src/(main|testIntegration)/java|portal-impl/src|portal-impl/test/(integration|unit)|portal-kernel/src|portal-test-integration/src|portal-test/src|util-taglib/src)/${CLASS_PATH}$"
							)
						fi

						if [[ -z ${CLASS_FILE} ]]
						then
							break
						fi

						CLASS_MODULE_DIR=$(sed -E -n 's#^(.+)/src/(main|testIntegration)/java/.*$#\1#p' <<< "${CLASS_FILE}")
						MEMBERS=1
					done

					echo "${JAVA_FILE}"

					break
				done
		)

		if [[ -z ${NEEDED} ]]
		then
			echo "${FILE}:${LINE}: Do not use \"inherit\" in \"-dsannotations-options\", no component inherits an @Activate, @Deactivate, @Modified or @Reference member"
		fi
	done
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind, so it reports on the diff's last `bnd.bnd` file rather than on the diff. Each iteration ends on a `continue` or on an `if` that prints a finding or not and exits 0 either way, so a clean branch and a branch with findings both exit 0.

Run the loop from `${REPO_ROOT}`. The changed paths are relative to the repository root, so from any directory below it, every file test fails and the check passes without scanning anything.

Use `while read` rather than a `for` over an unquoted substitution, so a path holding whitespace stays one file rather than splitting into several.

When `${FINDINGS}` is empty, the branch adds no `inherit` that its module does not need, so the validation passes.

Otherwise, the validation fails. Report the message below to the developer with one row for each finding, giving the path and the line as the finding prints them, and return it as the failure note.

```markdown
**`-dsannotations-options: inherit` changes nothing in the modules below.** No `@Component` class under `src/main/java` or `src/testIntegration/java` of each module inherits a field or method annotated with `@Activate`, `@Deactivate`, `@Modified`, or `@Reference`, so bnd builds the same bundle without the option. Remove `inherit` from each line below.

| File | Line |
| --- | --- |
| <file> | <line> |
```

## Time Estimate

~10-30 sec (static, no build).