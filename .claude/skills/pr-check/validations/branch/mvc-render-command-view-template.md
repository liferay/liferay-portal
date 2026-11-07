# MVC Render Command View Template

Catches an `MVCRenderCommand` that only repeats the portlet's view template. When a render request names no command, `MVCPortlet` looks up the command registered for `/`, and when there is none, it renders the JSP of the portlet's `view-template` init parameter. A render command registered with `mvc.command.name=/` whose `render` method does nothing but return a constant path is therefore a component that stands in for `"jakarta.portlet.init-param.view-template=<path>"`: it still activates, registers, and gets maintained, and the names it registers next to `/` keep URLs pointing at it. The check flags a branch that adds such a command or changes one.

## Source Formatter Candidate

This validation stands in for a Source Formatter check. Source Formatter catches the same code at format time and in CI without a pr-check run, so the check is the intended home of this rule, and this validation is the guard until it lands. When the check lands in `modules/util/source-formatter`, delete this file and its entry in `SKILL.md`.

- Check: `MVCRenderCommandViewTemplateCheck`
- Source Formatter pull request: https://github.com/ling-alan-huang/liferay-portal/pull/4416
- Ticket: LPD-114418

## Match

`\.java$ &! -test/|-test-util/|/src/test/|/src/testIntegration/|^modules/test/|^portal-test`

## Command

This check is static and needs no build. It walks each changed Java file the match selects, skips a file the branch deletes and a file whose diff adds no line, and reads the rest from the working tree. A file is a candidate when it implements `MVCRenderCommand`, registers the quoted string `"mvc.command.name=/"`, and the body of its `render` method is a single `return` of a string literal. A registration that builds the name from a constant has no literal to search for and is not collected.

Liferay formats a method body two tabs deep and closes the method with a single tab and a brace, so the scan reads the `render` body as the lines between the `{` that ends the signature, which may span several lines and a `throws` clause, and the first line that is only `\t}`. Blank lines do not count.

Each candidate prints as `<file>:<line>: maps "/" to <path>, and its "render" method does nothing else`, where the line is the one that registers `/` and the path is the one that the command returns.

```bash
FINDINGS=$(
	bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}" | while IFS= read -r FILE
	do
		if [[ ! -f ${FILE} ]] ||
		   ! git diff --unified=0 "${MERGE_BASE}...HEAD" -- "${FILE}" | command grep --regexp='^+[^+]' > /dev/null ||
		   ! command grep --quiet --regexp='implements MVCRenderCommand' "${FILE}" ||
		   ! command grep --fixed-strings --quiet --regexp='"mvc.command.name=/"' "${FILE}"
		then
			continue
		fi

		BODY=$(
			awk '
				/^\tpublic String render\(/ {
					state = 1
				}

				(state == 1) && /\{$/ {
					state = 2

					next
				}

				(state == 2) && /^\t\}$/ {
					exit
				}

				(state == 2) && NF {
					print
				}
			' "${FILE}"
		)

		if [[ ${BODY} != *$'\n'* ]] &&
		   [[ ${BODY} =~ ^[[:space:]]*return\ (\"[^\"]*\")\;$ ]]
		then
			LINE=$(command grep --fixed-strings --line-number --max-count=1 --regexp='"mvc.command.name=/"' "${FILE}")

			echo "${FILE}:${LINE%%:*}: maps \"/\" to ${BASH_REMATCH[1]}, and its \"render\" method does nothing else"
		fi
	done | sort --unique
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction.

Run the command from `${REPO_ROOT}`. The changed paths are relative to the repository root, so from any directory below it, every file test fails and the check passes without scanning anything.

When `${FINDINGS}` is empty, the branch neither adds nor changes a render command that only repeats the view template, so the validation passes.

Otherwise, each printed line is a candidate, not a finding. Judge each one before calling it a finding:

1. **Check that the portlet is an `MVCPortlet`.** Find the portlet class whose `jakarta.portlet.name` matches the command's. The view template fallback belongs to `MVCPortlet`, so a candidate whose portlet does not extend `MVCPortlet` or one of its subclasses is not a finding.

1. **Check whether another name of the command is a peer view.** A command that registers a name next to `/` stays when that name is one of several views that callers choose by command name, such as navigation items that each name a command. Moving only `/` to the view template would keep the class and point two places at one JSP, so such a candidate is not a finding.

1. **Read the command and the code around the line.** A candidate is not a finding when the line only renames, reformats, or moves existing code.

Every candidate left after these steps is a finding. The fix is to set `jakarta.portlet.init-param.view-template` on the portlet to the path the command returns, replacing a view template that points somewhere else, to delete the command, and to let the URLs that use its other names render the default view: drop the `mvcRenderCommandName` they set, or use `/` where the code picks the name among several commands.

When any candidate is a finding, the validation fails. Report the message below to the developer with one row for each finding, giving the file, the line, and the path as the candidate prints them, and return it as the failure note.

```markdown
**An MVC render command only repeats the view template.** Each command below maps `/` to a JSP and does nothing else. Before requesting review, set `jakarta.portlet.init-param.view-template` on the portlet to that JSP, delete the command, and let the URLs that named it render the default view. For additional questions, use #t-core-infrastructure on Slack.

| File | Line | Path |
| --- | --- | --- |
| <file> | <line> | <path> |
```

When no candidate is a finding, the validation passes.

## Time Estimate

~5 sec (static, no build).