# Transaction Usage

## Trigger

The diff adds new transaction usage in a hand-written Java file (generated files are excluded), such as the `@Transactional` annotation, `Propagation.REQUIRES_NEW`, a `REQUIRES_NEW_TRANSACTION` config, `TransactionCallbackUtil`, `TransactionCommitCallbackUtil`, or `TransactionInvokerUtil`. Improper transaction usage has heavy performance implications, so any new case must be reviewed and approved by Shuyang Zhou and the Core Infrastructure team first.

## Match

`\.java$`

## Command

This check is static and needs no build. It walks each changed Java file, skips generated files, and reports any newly added transaction usage in the rest. It uses `command grep` because a bare `grep` resolves to a shell function that rejects these patterns.

A generated file carries the `@generated` Javadoc marker that Service Builder and the other code generators stamp on every file they produce (for example every `*LocalService` interface). The scan matches it case-insensitively, so the `@Generated` annotation on REST Builder output is covered too. That annotation is machine-written boilerplate, not hand-written transaction code, so any file carrying it is excluded before the transaction scan.

```bash
MERGE_BASE=$(git merge-base HEAD master)

for FILE in $(git diff --name-only "${MERGE_BASE}...HEAD" -- '*.java'); do
	if [ ! -f "${FILE}" ] || command grep -q -i -F -e '@generated' "${FILE}"; then
		continue
	fi

	git diff "${MERGE_BASE}...HEAD" -- "${FILE}" \
		| command grep -E '^\+' \
		| command grep -v -E '^\+\+\+' \
		| command grep -F -e '@Transactional' -e 'Propagation.REQUIRES_NEW' -e 'REQUIRES_NEW_TRANSACTION' -e 'TransactionCallbackUtil' -e 'TransactionCommitCallbackUtil' -e 'TransactionInvokerUtil'
done
```

When the command prints nothing, the branch adds no new transaction usage, so the validation passes.

Otherwise, resolve the GitHub login of the person running pr-check with `gh api user --jq '.login'` and check it against the whitelist below. The validation passes only when that login is on the whitelist, and fails for everyone else. It also fails when the login cannot be resolved, for example when `gh` is not authenticated, so an unknown initiator never passes. Add one login per line as the roster changes.

```
shuyangzhou
```

When the validation fails, report the message below to the developer and return it as the failure note.

```markdown
⚠️ **Transaction usage requires Core Infrastructure review.** New transaction code (`@Transactional`, `TransactionInvokerUtil`, etc.) has heavy performance implications and requires additional review. After team review is complete, please send this pull request to @liferay-core-infra and request review from @shuyangzhou. For additional questions, use #t-core-infrastructure on Slack.
```

## Time Estimate

~10-20 sec (static, no build).