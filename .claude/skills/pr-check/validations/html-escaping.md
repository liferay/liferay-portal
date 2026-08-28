# HTML Escaping

## Trigger

The diff adds a line that renders a value as HTML in a hand written `.js`, `.jsx`, `.ts`, or `.tsx` file. When nothing escapes that value first, the result is a cross site scripting defect.

## Match

`\.(js|jsx|ts|tsx)$`

## Command

The scope is the whole branch diff, so a line added in one commit and escaped in a later one is not a finding.

List the changed files:

```bash
git diff --diff-filter=d --name-only "$(git merge-base HEAD master)...HEAD" -- '*.js' '*.jsx' '*.ts' '*.tsx'
```

Skip any file containing `@generated`, matched case insensitively. For each file that remains, list the added lines that render a value as HTML:

```bash
git diff "$(git merge-base HEAD master)...HEAD" -- <file> \
	| command grep '^+[^+]' \
	| command grep \
		--extended-regexp \
		--regexp='(bodyHTML|headerHTML)[[:space:]]*:' \
		--regexp='(inner|outer)HTML[[:space:]]*=' \
		--regexp='createContextualFragment\(' \
		--regexp='dangerouslySetInnerHTML' \
		--regexp='document\.write\(' \
		--regexp='insertAdjacentHTML\(' \
		--regexp='new Function\(' \
		--regexp='open(Toast|Modal)\(' \
		--regexp='srcdoc'
```

When no file remains to scan, because the Match fired on a path this diff only deletes or on a generated file, nothing was examined. Report **NOT VERIFIED** naming what was left out, since a pass over an empty set reads as a branch that was checked. When files remain and none produces output, the validation passes.

Otherwise, each printed line is a candidate, not a finding. Read the file to classify it and find the line number.

A candidate is not a finding when:

- A `// XSS:` waiver comment appears above the line.
- The call is `openToast` or `openModal` and passes no `bodyHTML`, `headerHTML`, `message`, or `title` argument.
- The line only renames, reformats, or moves existing code.
- The value comes from `Liferay.Language.get` or `sub` on a literal key.
- The value is a static literal, including a template literal whose every interpolation is a literal.
- The value is already escaped by `escapeHTML` from `frontend-js-web` or by `Liferay.Util.escapeHTML`.

Everything else is a finding, including any value that cannot be traced.

Report every finding as a FAIL and return the block below as the failure note.

```markdown
**A value is rendered as HTML without being escaped.** Escape each value below with `escapeHTML` from `frontend-js-web` where it is rendered. When it is already escaped earlier, keep the code as it is and add a `// XSS: <value> is escaped by <where>` comment above the line. Do not add a second escape.

| File | Line | Rendered by | Value |
| --- | --- | --- | --- |
| <file> | <line> | <call> | <expression> |
```

## Time Estimate

~20-40 sec (static, no build).