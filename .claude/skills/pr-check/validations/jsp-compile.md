# JSP Compile

## Trigger

A `*.jsp` or `*.jspf` changed in an OSGi module.

`compileJSP` is autowired into every OSGi module by `LiferayOSGiPlugin` (via `JspCPlugin` / `JspCDefaultsPlugin`), but it is **not** in the `assemble`/`build`/`deploy` task graph — so `gradlew :path:deploy` bundles JSPs without compiling them, and scriptlet typos (e.g., `Validator.isURL` instead of `Validator.isUrl`) only fail at Tomcat-Jasper render time. Invoke `compileJSP` explicitly.

Only JSPs under `modules` are in scope. `LiferayOSGiPlugin` wires the task into OSGi modules alone, so the roughly 200 JSPs under `portal-impl` and `portal-web` have no `compileJSP` task to invoke. They also sit under `portal-impl/bnd.bnd`, so an unscoped run resolves them to a `portal-impl` project the modules build does not contain and fails on a path that never existed.

## Match

`^modules/.+\.(jsp|jspf)$`

## Command

Take the changed JSPs from the diff:

```bash
MERGE_BASE=$(git merge-base HEAD master)

git diff --name-only "${MERGE_BASE}...HEAD" -- ':/modules/*.jsp' ':/modules/*.jspf'
```

Group them by their owning module (the nearest ancestor with a `bnd.bnd`), and convert each module directory to a Gradle project path by stripping `modules/` and replacing `/` with `:`.

Per affected module:

```bash
"${REPO_ROOT}/gradlew" \
	--parallel \
	--project-dir "${REPO_ROOT}/modules" \
	:<path>:compileJSP
```

`compileJSP` runs in two stages — `generateJSPJava` (JSP → Java) then `JavaCompile` — and surfaces both syntax errors and unresolved method/class references.

FAIL when a module reports `BUILD FAILED`, and report the module and the compiler error. A run that produced neither `BUILD SUCCESSFUL` nor `BUILD FAILED` did not finish, which is also a FAIL — a JSP that was never compiled is not one that compiled cleanly. PASS when every selected module reports `BUILD SUCCESSFUL`.

## Checklist

```
- [ ] JSP compile: <module path>
```

## Time Estimate

~30 sec - 2 min per module.