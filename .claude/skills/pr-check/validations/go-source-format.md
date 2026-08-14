# Go Source Format

## Trigger

Any hand-written Go file changed in the diff. Generated Go — a `zz_generated` name or a file carrying the `// Code generated ... DO NOT EDIT.` marker — is excluded, since the next `go generate` overwrites it.

The portal source formatter does not process `*.go`, so this validation covers Go the way Source Format covers the rest of the tree: `gofmt` plus the Go conventions in `.claude/rules/go-style.md`.

## Match

`\.go$ &! zz_generated`

## Command

Resolve the module root of each changed Go file by walking up to its nearest `go.mod`, and collect the distinct roots:

```bash
(cd "${REPO_ROOT}" && git diff --name-only "$(git merge-base HEAD master)...HEAD" | grep '\.go$' | grep --invert-match zz_generated | while read -r file; do dir=$(dirname "${file}"); while [ "${dir}" != "." ]; do if [ -f "${dir}/go.mod" ]; then echo "${dir}"; break; fi; dir=$(dirname "${dir}"); done; done | sort --unique)
```

Invoke the `format-source` skill scoped to the changed Go files. It runs `gofmt` and applies `.claude/rules/go-style.md`. After it finishes, confirm every resolved module is `gofmt`-clean:

```bash
(cd "${REPO_ROOT}/<go-module-root>" && gofmt -l .)
```

A nonempty `gofmt -l` listing is a FAIL; the Autocommit step applies the fix.

## Autocommit

Apply `gofmt` to each resolved Go module and stage the result along with any rule edits the `format-source` skill made:

```bash
(cd "${REPO_ROOT}/<go-module-root>" && gofmt -w .)
```

When `git status --porcelain` is nonempty afterward, stage all changes (`git add --all`) and create a commit titled `<TICKET> SF`.

When the commit fails, record the failure and continue to the next validation.

## Notes

Run alongside Source Format, after the drift validations, so the rules see the final tree.

## Time Estimate

~1 min. It scales with the number of changed Go files.