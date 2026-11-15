# Required Configuration Policy

Catches a component that activates out of the box only to do nothing. A component bound through `configurationPid` to a system scoped configuration, whose behavior is entirely gated by a switch in that configuration that is off by default, must declare `configurationPolicy = ConfigurationPolicy.REQUIRE`. Out of the box no configuration is deployed, so with the default optional policy Service Component Runtime activates the component, which finds the switch off and backs off. With a required policy the component stays inactive until an administrator deploys the configuration.

Company and group scoped configurations, factory configurations, and configurations read at use time through `ConfigurationProvider` cannot use a required policy and are out of scope.

## Source Formatter Candidate

This validation stands in for a Source Formatter check. Source Formatter catches the same code at format time and in CI without a pr-check run, so the check is the intended home of this rule, and this validation is the guard until it lands. When the check lands in `modules/util/source-formatter`, delete this file and its entry in `SKILL.md`.

- Check: `JavaComponentAnnotationsCheck`, enabled through `checkRequireConfigurationPolicy`, with its exceptions in `allowedOptionalConfigurationPolicyClassNames`
- Source Formatter pull request: https://github.com/ling-alan-huang/liferay-portal/pull/4422
- Ticket: LPD-115796

The check matches only the gate shapes it can recognize mechanically. This validation applies the rule by judgment, so it also covers a gate the check cannot see, such as a switch passed into a context object that every public method consults.

## Match

`\.java$ &! ^modules/third-party/`

## Command

This check is static and needs no build. It lists two kinds of candidates in the changed Java files, skipping deleted files, generated files, and test code. It uses `command grep` so each file is searched by the system grep rather than the shell's `grep` wrapper, which some environments route to another tool with its own defaults.

- A changed `@Component` class that declares a `configurationPid` and no `ConfigurationPolicy.REQUIRE`. The whole file is a candidate, not only its added lines, because the switch, the gate, and the annotation rarely share a diff hunk. Each prints as `<path>: component`.
- A changed `@Meta.OCD` interface where the branch adds a line with `deflt = "false"`, which can add a switch or turn one off by default. Every component in the repository that binds the interface's `id` through `configurationPid` without `ConfigurationPolicy.REQUIRE` is a candidate. Each prints as `<path>: binds <id>, changed in <configuration path>`.

A generated file carries the `@generated` Javadoc marker, and the match ignores case so the `@Generated` annotation on REST Builder output is covered too. Test code is any path under a `-test` or `-test-util` module, `src/test`, or `testIntegration`.

```bash
FINDINGS=$(
	bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}" | while read -r FILE
	do
		case "${FILE}" in
			*-test/*|*-test-util/*|*/src/test/*|*/testIntegration/*)
				continue
				;;
		esac

		if [ ! -f "${FILE}" ] || command grep --ignore-case --quiet --regexp='^[[:space:]]*\*[[:space:]]*@generated' --regexp='^[[:space:]]*@Generated' "${FILE}"
		then
			continue
		fi

		if command grep --quiet --regexp='configurationPid' "${FILE}" && ! command grep --quiet --regexp='ConfigurationPolicy\.REQUIRE' "${FILE}"
		then
			printf '%s: component\n' "${FILE}"
		fi

		if ! command grep --quiet --regexp='@Meta\.OCD' "${FILE}" || ! git diff "${MERGE_BASE}...HEAD" -- "${FILE}" | command grep --quiet --regexp='^+.*deflt = "false"'
		then
			continue
		fi

		ID=$(tr '\n' ' ' < "${FILE}" | sed -e 's/.*@Meta\.OCD([^)]*[^[:alnum:]_]id = "\([^"]*\)".*/\1/' -e 't' -e 'd')

		if [ -z "${ID}" ]
		then
			continue
		fi

		git grep --files-with-matches --fixed-strings "\"${ID}\"" -- '*.java' | while read -r COMPONENT
		do
			if [ "${COMPONENT}" != "${FILE}" ] && command grep --quiet --regexp='configurationPid' "${COMPONENT}" && ! command grep --quiet --regexp='ConfigurationPolicy\.REQUIRE' "${COMPONENT}"
			then
				printf '%s: binds %s, changed in %s\n' "${COMPONENT}" "${ID}" "${FILE}"
			fi
		done
	done | sort -u
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind.

Run the command from `${REPO_ROOT}`. The changed paths are relative to the repository root, so from any directory below it, every file test fails and the check passes without scanning anything.

When `${FINDINGS}` is empty, no changed component can need a required configuration policy, so the validation passes.

Otherwise, each printed line is a candidate, not a finding. Read the component, the `@Meta.OCD` interface its `configurationPid` names, and any superclass the component extends, then classify it. A candidate is a finding only when all of the following hold:

- The `configurationPid` names a single configuration whose interface is not a factory configuration (`factory = true` in `@Meta.OCD`) and whose `@ExtendedObjectClassDefinition` scope, if any, is `SYSTEM`. A `.scoped` PID is company scoped.
- The interface has a `boolean` switch that defaults to `false`, such as `enabled`, `debugEnabled`, or `enableFoo`.
- Every way the component affects the portal is gated by that switch. Either every public service method returns early or does nothing while the switch is off, or the component exposes the switch through a method such as `isEnabled()` or `isAvailable()` that its framework or callers consult before using it, and the rest of its methods only do work that those callers skip while it returns `false`. The `@Activate` and `@Modified` methods may build objects from the configuration, but they must have no effect outside the component, such as registering a service, starting a thread, or writing a configuration.

A candidate is not a finding when:

- Its path contains an entry of `source.check.JavaComponentAnnotationsCheck.allowedOptionalConfigurationPolicyClassNames` in `source-formatter.properties`.
- The switch gates only part of the behavior, for example one branch of a method, one value in the output, or some methods while others serve regardless.
- The component is a configuration facade: besides returning the switch, it serves other values of the configuration, so callers need it while the switch is off.
- Another component holds a mandatory static `@Reference` to the component's service, so a required policy would leave that consumer unsatisfied out of the box. Search the repository for references to the service type to tell.

Everything else is a finding.

When any candidate is a finding, the validation fails. Report the message below to the developer with one row for each finding and return it as the failure note.

```markdown
**A component activates out of the box only to do nothing.** Its behavior is entirely gated by a switch in its system scoped configuration that is off by default. Add `configurationPolicy = ConfigurationPolicy.REQUIRE` to its `@Component`, so that it stays inactive until the configuration is deployed, and give any test that injects it the configuration it now requires. When a consumer cannot work without the component, keep the optional policy and add the component to `source.check.JavaComponentAnnotationsCheck.allowedOptionalConfigurationPolicyClassNames` with the reason in the commit message. For additional questions, use #t-core-infrastructure on Slack.

| File | Configuration | Switch | Gate |
| --- | --- | --- | --- |
| <file> | <configuration pid> | <switch> | <method and line that returns early or returns the switch> |
```

## Time Estimate

~5-20 sec for the command (static, no build), plus reading each candidate.