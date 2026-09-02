# Service Registration

## Trigger

A Java file changed. The defects this validation catches compile clean and surface only on a deployed portal, so nothing else at PR time reaches them.

Add a scan here only when a wrong registration is provable from the tree alone, with no build and no false positives to dismiss.

## Match

`\.java$`

## Command

Use `command grep` for any working tree scan, so it is filtered by the system grep rather than the shell's `grep` wrapper, which some environments route to another tool with its own defaults.

Take the changed Java files from the diff:

```bash
REPO_ROOT=$(git rev-parse --show-toplevel)
MERGE_BASE=$(git merge-base HEAD master)

git diff --name-only "${MERGE_BASE}...HEAD" -- ':/*.java'
```

### Selection

Run each scan whose inputs the diff touches:

| Diff Touches | Scan |
| --- | --- |
| Any changed `.java` file | Unsatisfied Reference |
| A `*ResourceImpl.java` under a REST Builder `resource/v<major>_<minor>` package | Redeclared Interface |

### Unsatisfied Reference

`@Component(service = {})` registers under no service type, so an `@Reference` whose field type is such a class can never be satisfied and the referencing component never activates.

Collect the risky class names from the diff:

- A changed `.java` file declaring `@Component` with `service = {}`. Its class name is the file name.

- A field the diff adds, declared `<visibility> <Type> _<name>;`, where `*/<Type>.java` declares `@Component` with `service = {}`.

An empty collection ends the scan.

List every `@Reference` in the repository and keep those whose declared field type is exactly a collected class, never a substring match:

```bash
(cd "${REPO_ROOT}" && git grep --after-context=30 --fixed-strings '@Reference' -- ':/*.java')
```

`git grep` searches from the current directory down, so run it from `${REPO_ROOT}` or the sweep silently narrows to a subtree.

Take the field declaration that follows each annotation rather than a fixed offset from it; an annotation carrying `policy`, `policyOption`, or `target` pushes its declaration down the window.

Report each hit as the unsatisfiable class with the file and field referencing it.

### Redeclared Interface

REST Builder generates `Base<Tag>ResourceImpl` with the interfaces the resource needs and scaffolds `<Tag>ResourceImpl` to extend it. Redeclaring one of them on the subclass changes how the service is registered.

For each changed `*ResourceImpl.java` in scope:

1. Skip it when the class name starts with `Base`, or when the file carries `@generated`.

1. Read its `implements` clause and that of `Base<ClassName>.java` in the same directory. Skip the file when no such base is there. Take the whole class declaration, from `public class` or `public abstract class` through the opening brace.

1. Compare raw type names, stripping type arguments.

Report any interface present in both, naming the class, the interface, and the base.

### Verdict

FAIL when any scan reports a finding, naming the scan alongside what it found. PASS when every scan prints nothing. Never take the verdict from an exit status: `grep` exits 1 precisely when it matches nothing.

## Checklist

Add one subitem per selected scan:

```
- [ ] <scan name>
```

## Time Estimate

~10-30 sec, mostly the `@Reference` sweep across the repository.