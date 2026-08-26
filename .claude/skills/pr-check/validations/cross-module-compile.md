# Cross-Module Compile

## Trigger

A `*-api`, `portal-impl`, or `portal-kernel` signature changed (or a `*Constants`, `*Service`, or `*Util` class). Two kinds of consumer are not compiled by any other validation:

- Modules carrying `.lfrbuild-portal-deprecated`, which the default `portal`/`dxp` profile excludes.

- `testIntegration` sources in `-test` modules a producer did not change. Per-Module Compile skips `-test` modules; Integration Test Compile runs only for a module whose own `testIntegration` changed.

Both depend on the kernel as a binary, not a `project(...)` edge, so Per-Module Compile's expansion cannot reach them. Find them by source symbol.

## Match

`^portal-impl/.+\.java$|^portal-kernel/.+\.java$|^modules/.+-api/.+\.java$|^modules/.+/[^/]*(Constants|Service|Util)\.java$`

## Command

Take the changed files from the diff:

```bash
MERGE_BASE=$(git merge-base HEAD master)

git diff --name-only "${MERGE_BASE}...HEAD" -- '*.java'
```

For each changed `.java` file, take its simple type name and search the two surfaces no other validation compiles, modules carrying `.lfrbuild-portal-deprecated` and `testIntegration` sources in `-test` modules:

```bash
find "${REPO_ROOT}/modules" -name .lfrbuild-portal-deprecated | while read -r marker; do
	command grep --files-with-matches --include='*.java' --recursive --word-regexp "<TypeName>" "$(dirname "${marker}")/src/main" | sed "s#/src/main/.*##"
done | sort --unique
```

Pipe `find` into `while read -r` rather than looping over `$(find ...)`, which zsh does not word split, so that loop runs once over one joined string, greps a path that does not exist, and returns the same empty exit 1 as a clean scan.

```bash
command grep --files-with-matches --include='*.java' --recursive --word-regexp "<TypeName>" "${REPO_ROOT}/modules" \
	| command grep "/src/testIntegration/" | sed "s#/src/testIntegration/.*##" \
	| command grep --regexp='-test$' | sort --unique
```

Convert each module root to a Gradle project path by dropping everything up to and including the last `modules/`, then replacing `/` with `:`:

```bash
printf '%s\n' "${module_root#*"${REPO_ROOT}/modules/"}" | tr '/' ':'
```

The scans emit absolute paths, and a workspace directory can itself contain `modules`, so anchor the strip to `${REPO_ROOT}` rather than removing the bare substring.

Cap the combined set at 8. When it exceeds the cap, skip the expansion for **every** symbol rather than part of it, and recommend Full Portal Build plus Integration Test Compile instead, since a partial expansion reports on an arbitrary subset while reading as a whole result.

Deprecated consumer (the only profile that includes it):

```bash
("${REPO_ROOT}/gradlew" \
	--project-dir "${REPO_ROOT}/modules" \
	-Dbuild.profile=portal-deprecated \
	:<path>:compileJava \
	--rerun)
```

testIntegration consumer:

```bash
("${REPO_ROOT}/gradlew" \
	--project-dir "${REPO_ROOT}/modules" \
	:<path>:compileTestIntegrationJava \
	--rerun)
```

Keep `--rerun`, which is an option on the compile task and follows the task path. Without it a warm tree reports `UP-TO-DATE` and the build cache restores `FROM-CACHE`, either way a green log in which the compile under test never ran. It forces the one task alone, which is why it is safe where `--no-build-cache` below is not.

A consumer that declares the producer with `project(":...")` resolves it from the working tree, so it compiles against the branch's own change. One that resolves a published artifact instead sees only what is released, and pr-check never fetches a remote, so a producing change that is still in review is invisible to it.

FAIL when a consumer compile reports `BUILD FAILED` naming one of the type names searched for, and report the consumer and that symbol. A failure naming none of them is already broken on the merge base, since this validation compiles consumers the branch did not touch, so report it and do not fail the branch.

A compile that aborts before reaching the consumer of the changed symbol never checked it, so discounting the failure is not the same as clearing the symbol. Two things stop a compile short, and the error count detects neither, since a truncated run and a fatal abort both report a small number:

```bash
command grep --fixed-strings 'only showing the first' "${LOG}"
```

That line is `javac` truncating at its 100 error cap. A fatal abort prints no marker at all, so also treat any `error: cannot access`, annotation processor crash, or run whose output ends without a diagnostic for the file holding the searched symbol as having stopped short.

When either applies, report **NOT VERIFIED** naming the consumer. Otherwise confirm the symbol was actually compiled by recompiling on its own the one file that names it, against the same classpath. A PASS here has to mean the symbol was compiled and was fine, never that the compiler never got to it. Do not look for the consumer's class files as evidence: `javac` skips generation once any error exists, so a failing run emits none whether or not it reached your symbol.

Judge only the `compile` task, since the graph drags in the repository's node and yarn bootstrap and a failure there stops the compile from ever running. Do not pass `--no-build-cache`, which reruns that bootstrap and leaves the tree broken for the next validation.

An empty consumer set is a PASS only when the scans were able to look. Assert the search root before believing an empty result:

```bash
[ -d "${REPO_ROOT}/modules" ] || exit 1
```

`command grep --include` suppresses the missing directory diagnostic, so a scan of a path that does not exist returns empty stdout, empty stderr, and exit 1, which is byte for byte what a genuinely clean scan returns. With `${REPO_ROOT}` unset the scans read `/modules` and the validation passes having examined nothing.

## Checklist

```
- [ ] (One subitem per consumer, capped at 8:) Compile <module path> (deprecated | testIntegration)
```

## Time Estimate

~1 min per consumer compile.