# Transaction Usage

## Trigger

The diff adds new transaction usage in a Java file, such as the `@Transactional` annotation, `Propagation.REQUIRES_NEW`, a `REQUIRES_NEW_TRANSACTION` config, `TransactionCallbackUtil`, `TransactionCommitCallbackUtil`, or `TransactionInvokerUtil`. Improper transaction usage has heavy performance implications, so any new case must be reviewed and approved by Shuyang Zhou and the Core Infrastructure team first.

## Match

`\.java$`

## Command

This check is static and needs no build. It uses `command grep` because a bare `grep` resolves to a shell function that rejects these patterns.

```bash
git diff "$(git merge-base HEAD master)...HEAD" -- '*.java' \
	| command grep -E '^\+' \
	| command grep -v -E '^\+\+\+' \
	| command grep -F -e '@Transactional' -e 'Propagation.REQUIRES_NEW' -e 'REQUIRES_NEW_TRANSACTION' -e 'TransactionCallbackUtil' -e 'TransactionCommitCallbackUtil' -e 'TransactionInvokerUtil'
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