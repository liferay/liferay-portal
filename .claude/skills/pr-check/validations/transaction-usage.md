# Transaction Usage

## Trigger

The diff adds new transaction usage in a hand-written Java file (generated files are excluded), such as the `@Transactional` annotation, `Propagation.REQUIRES_NEW`, a `REQUIRES_NEW_TRANSACTION` config, `TransactionCallbackUtil`, `TransactionCommitCallbackUtil`, or `TransactionInvokerUtil`. Improper transaction usage has heavy performance implications, so any new case must be reviewed and approved by Shuyang Zhou and the Core Infrastructure team first.

## Match

`\.java$`

## Command

This check is static and needs no build. It walks each changed Java file, skips generated files, and reports any newly added transaction usage in the rest. It uses `command grep` so the diff is filtered by the system grep rather than the shell's `grep` wrapper, which some environments route to another tool with its own defaults.

A generated file carries the `@generated` Javadoc marker that Service Builder and the other code generators stamp on every file they produce (for example every `*LocalService` interface). The scan matches it case-insensitively, so the `@Generated` annotation on REST Builder output is covered too. That annotation is machine-written boilerplate, not hand-written transaction code, so any file carrying it is excluded before the transaction scan.

Anchor that match to a Javadoc tag or an annotation rather than searching the whole file for the bare word, or a class that only mentions `@generated` in prose drops out of the scan entirely.

```bash
MERGE_BASE=$(git merge-base HEAD master)

FINDINGS=$(
	git diff --name-only "${MERGE_BASE}...HEAD" -- ':/*.java' \
		| while IFS= read -r FILE
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

		git diff "${MERGE_BASE}...HEAD" -- "${FILE}" \
			| command grep --extended-regexp '^\+' \
			| command grep --extended-regexp --invert-match '^\+\+\+' \
			| command grep \
				--fixed-strings \
				--regexp='@Transactional' \
				--regexp='Propagation.REQUIRES_NEW' \
				--regexp='REQUIRES_NEW_TRANSACTION' \
				--regexp='TransactionCallbackUtil' \
				--regexp='TransactionCommitCallbackUtil' \
				--regexp='TransactionInvokerUtil'
	done
)

printf '%s' "${FINDINGS}"
```

**Judge this from `${FINDINGS}` and never from the exit status**, which carries no verdict in either direction. The loop's status is whatever its last iteration happened to leave behind, so it reports on the diff's last Java file rather than on the diff. A clean branch ends on a `command grep` that matched nothing and exits 1. A branch that adds transaction usage in an early file and ends on a clean one also exits 1. `continue` returns 0, so a diff whose last Java file is generated exits 0 whatever preceded it. Both statuses occur on both verdicts.

The `':/*.java'` pathspec is anchored to the repository root on purpose. Git resolves a bare `'*.java'` against the current directory, so running this from anywhere below the root selects no files at all and the check passes having scanned nothing.

Use `while read` rather than a `for` over an unquoted substitution, so a path holding whitespace stays one file rather than splitting into several.

When `${FINDINGS}` is empty, the branch adds no new transaction usage, so the validation passes.

Otherwise, resolve the GitHub login of the person running pr-check with `gh api user --jq '.login'` and check it against the whitelist below. The validation passes only when that login is on the whitelist, and fails for everyone else. It also fails when the login cannot be resolved, for example when `gh` is not authenticated, so an unknown initiator never passes. Add one login per line as the roster changes.

```
shuyangzhou
```

When the validation fails, report the message below to the developer and return it as the failure note.

```markdown
**Transaction usage requires Core Infrastructure review.** New transaction code (`@Transactional`, `TransactionInvokerUtil`, etc.) has heavy performance implications and requires additional review. After team review is complete, please send this pull request to @liferay-core-infra and request review from @shuyangzhou. For additional questions, use #t-core-infrastructure on Slack.
```

## Time Estimate

~10-20 sec (static, no build).