# Go Source Format

## Trigger

Any hand written Go file changed under `cloud/operator/`, which is the scope `.claude/rules/go-style.md` declares. Generated Go — a `zz_generated` name, or a file carrying the `// Code generated ... DO NOT EDIT.` marker — is excluded, since the next `go generate` overwrites it. The name is what the Match below can filter on; skip a marked file the regex still admits.

The portal source formatter does not process `*.go`, so this validation covers Go the way Source Format covers the rest of the tree: `gofmt` plus the Go conventions in `.claude/rules/go-style.md`.

## Match

`^cloud/operator/.*\.go$ &! zz_generated`

## Command

`cloud/operator/resources` is the Go module root — the directory that holds `go.mod`.

Invoke the `format-source` skill scoped to the changed Go files. It runs `gofmt` and applies `.claude/rules/go-style.md`. After it finishes, confirm the module is clean under `gofmt`:

```bash
(cd "${REPO_ROOT}/cloud/operator/resources" && gofmt -l .)
```

A nonempty `gofmt -l` listing is a FAIL; the Autocommit step applies the fix.

## Autocommit

Apply `gofmt` to the module and stage the result along with any rule edits the `format-source` skill made:

```bash
(cd "${REPO_ROOT}/cloud/operator/resources" && gofmt -w .)
```

When `git status --porcelain` is nonempty afterward, stage all changes (`git add --all`) and create a commit titled `<TICKET> SF`.

When the commit fails, record the failure and continue to the next validation.

## Notes

Run alongside Source Format, after the drift validations, so the rules see the final tree.

`gofmt` settles formatting only. The manual conventions in `.claude/rules/go-style.md` — naming, declaration order, statement grouping, message form — have no checker behind them, so a PASS here means the module is formatted, not that those conventions were applied.

When a Go module appears outside `cloud/operator`, widen this Match and the `paths` list in `.claude/rules/go-style.md` together, so the two stay in agreement.

## Time Estimate

~1 min. It scales with the number of changed Go files.