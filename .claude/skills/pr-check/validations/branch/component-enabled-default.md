# Component Enabled Default

Flags an `enabled = true` that the branch adds to an OSGi Declarative Services `@Component` annotation. A component is enabled by default, so the attribute changes nothing in the component description, and it sends the reader looking for an enabler that does not exist. It usually survives from a component that was first added with `enabled = false` and later switched on by flipping the value, and it spreads through copied classes. Enterprise app modules are the exception. Source Formatter's `JavaComponentAnnotationsCheck` requires every component outside their test modules to state `enabled`, and `BaseEnterpriseDSModulePortalProfile` enables their `enabled = false` components only when the license allows it, so in those modules `enabled = true` marks a component that must run whatever the license.

## Source Formatter Candidate

This validation stands in for a Source Formatter check. Source Formatter catches the same code at format time and in CI without a pr-check run, so the check is the intended home of this rule, and this validation is the guard until it lands. When the check lands in `modules/util/source-formatter`, delete this file and its entry in `SKILL.md`.

- Check: `JavaComponentAnnotationsCheck`
- Source Formatter pull request: https://github.com/ling-alan-huang/liferay-portal/pull/4430
- Ticket: LPD-116900

## Match

`\.java$`

## Command

This check is static and needs no build. It walks each changed Java file that imports `org.osgi.service.component.annotations.Component` and reports each `enabled = true` attribute of a `@Component` annotation that sits on a line the branch adds, so an attribute already on master never fails the branch. It skips a file whose path contains one of the enterprise app module paths that `source.check.JavaComponentAnnotationsCheck.enterpriseAppModulePathNames` lists in `source-formatter.properties`, unless the path contains `-test/` or `-test-util/`, which leaves exactly the paths where Source Formatter requires the attribute. An annotation runs from the line that opens `@Component(` to the line that closes it, which is the same line or the first line that holds only `)`. It uses `command grep` so each file and its added lines are searched by the system grep rather than the shell's `grep` wrapper, which some environments route to another tool with its own defaults.

Each finding prints as `<path>:<line>: "enabled = true" is the default`, where `<line>` is the line of the attribute.

```bash
ENTERPRISE_APP_MODULE_PATH_NAMES=$(
	awk '
		/^[[:space:]]*source\.check\.JavaComponentAnnotationsCheck\.enterpriseAppModulePathNames=/ {
			listed = 1

			sub(/^[^=]*=/, "")
		}

		listed {
			line = $0

			continued = sub(/\\[[:space:]]*$/, "", line)

			gsub(/[[:space:]]/, "", line)

			count = split(line, names, ",")

			for (i = 1; i <= count; i++) {
				if (names[i] != "") {
					print names[i]
				}
			}

			if (!continued) {
				exit
			}
		}
	' source-formatter.properties
)

FINDINGS=$(
	bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}" | while IFS= read -r FILE
	do
		[[ -f ${FILE} ]] || continue

		if ! command grep --fixed-strings --quiet --regexp='import org.osgi.service.component.annotations.Component;' "${FILE}"
		then
			continue
		fi

		if [[ ! -z ${ENTERPRISE_APP_MODULE_PATH_NAMES} ]] &&
			[[ ! ${FILE} =~ -test(-util)?/ ]] &&
			command grep --fixed-strings --quiet --regexp="${ENTERPRISE_APP_MODULE_PATH_NAMES}" <<< "${FILE}"
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

		awk '
			/^[[:space:]]*@Component\(/ {
				annotation = 1
			}

			annotation && /(^|[(,])[[:space:]]*enabled = true[[:space:]]*([,)]|$)/ {
				print NR
			}

			annotation && (/^[[:space:]]*@Component\(.*\)$/ || /^[[:space:]]*\)$/) {
				annotation = 0
			}
		' "${FILE}" | while IFS= read -r LINE
		do
			if command grep --fixed-strings --line-regexp --quiet "${LINE}" <<< "${ADDED}"
			then
				echo "${FILE}:${LINE}: \"enabled = true\" is the default"
			fi
		done
	done
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind, so it reports on the diff's last Java file rather than on the diff. Each iteration ends on a `continue` or on the loop over the lines the `awk` scan prints, and every pass of that loop ends on an `if` that prints a finding or not and exits 0 either way, so a clean branch and a branch with findings both exit 0.

Run the loop from `${REPO_ROOT}`. The changed paths are relative to the repository root, so from any directory below it, every file test fails and the check passes without scanning anything.

Use `while read` rather than a `for` over an unquoted substitution, so a path holding whitespace stays one file rather than splitting into several.

When `${FINDINGS}` is empty, the branch adds no `enabled = true` to a `@Component` annotation outside the enterprise app modules, so the validation passes.

Otherwise, the validation fails. Report the message below to the developer with one row for each finding, giving the path and the line as the finding prints them, and return it as the failure note.

```markdown
**`enabled = true` restates the default of `@Component`.** A component is enabled unless its annotation says `enabled = false`, so each attribute below changes nothing. Remove each one.

| File | Line |
| --- | --- |
| <file> | <line> |
```

## Time Estimate

~10-30 sec (static, no build).