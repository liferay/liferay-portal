# Cross-Module Compile

Compiles two kinds of consumer of a changed module. No other validation compiles them completely, because **Java Unit Tests** compiles only the modules it runs. The first kind is the test code that uses the changed module, in `src/test` and `src/testIntegration`. The second kind is a module carrying `.lfrbuild-portal-deprecated`, which the default profile leaves out. `ant all` compiles neither, so this runs whatever **Full Portal Build** returns. A changed file under `src/test` selects nothing, since that source set's output is on no consumer's classpath.

## Match

`^modules/.+\.java$|^portal-impl/src/.+\.java$|^portal-kernel/src/.+\.java$ &! /src/test/`

## Preconditions

- Portal Classpath
- Portal Snapshots

## Command

Run every command below from `${REPO_ROOT}`. `git grep` searches from the current directory down, so from anywhere else the sweeps silently narrow to a subtree.

### Consumers

Find consumers by two routes. Compile a consumer only once, even when both routes find it.

**By module.** Take the changed modules:

```bash
bash "${SKILL_DIR}/select_paths.sh" "${MERGE_BASE}" "${VALIDATION_FILE}" \
	| bash "${SKILL_DIR}/find_modules.sh" "${MERGE_BASE}" \
	| cut -d " " -f1 \
	| command grep '^modules/' \
	| sort --unique
```

For each changed module, take every module under the same parent directory whose name ends in `-test` and which has a `src/testIntegration` tree, so `apps:blogs:blogs-api` brings in `apps:blogs:blogs-test`. Add every `-test` module whose `build.gradle` declares the changed module, where `<path>` is the changed module's Gradle project path:

```bash
git grep --cached --files-with-matches --fixed-strings 'project(":<path>")' -- '*.gradle' \
	| bash "${SKILL_DIR}/find_modules.sh" "${MERGE_BASE}" \
	| cut -d " " -f1
```

**By type.** Take each changed `.java` file under `portal-impl/src` or `portal-kernel/src`, under the `src/main` of a module whose name ends in `-api`, or under any `src/main` and named `*Constants.java`, `*Service.java`, or `*Util.java`. Take its fully qualified type name, the package from its `package` line followed by the file name without `.java`, and list the files that reference it. A file outside that package has to import the type or spell out its fully qualified name, since the repository has no `com.liferay` wildcard imports, so search for that name rather than the simple one. A simple name collides with every unrelated type of the same name: `Test` matches each `import org.junit.Test`, which once put 473 modules in a consumer set whose true size was zero. A file in the same package needs no import, so search those for the simple name:

```bash
git grep --cached --files-with-matches --fixed-strings --word-regexp '<FullyQualifiedName>' -- 'modules/*.java'
git grep --all-match --cached --files-with-matches --fixed-strings --word-regexp -e 'package <package>;' -e '<TypeName>' -- 'modules/*.java'
```

Every file either search lists, other than the changed file itself, is a consumer file. For each consumer file under `src/test`, take its module. This includes the changed module itself. For each consumer file under `src/testIntegration`, take its module when the module's name ends in `-test`:

```bash
printf '%s\n' <consumer file>... \
	| command grep '/src/test/' \
	| bash "${SKILL_DIR}/find_modules.sh" "${MERGE_BASE}" \
	| cut -d " " -f1 \
	| command grep '^modules/' \
	| sort --unique

printf '%s\n' <consumer file>... \
	| command grep '/src/testIntegration/' \
	| bash "${SKILL_DIR}/find_modules.sh" "${MERGE_BASE}" \
	| cut -d " " -f1 \
	| command grep --regexp='-test$' \
	| sort --unique
```

Take the modules carrying `.lfrbuild-portal-deprecated` whose `src/main` holds one:

```bash
printf '%s\n' <consumer file>... \
	| command grep '/src/main/' \
	| bash "${SKILL_DIR}/find_modules.sh" "${MERGE_BASE}" \
	| cut -d " " -f1 \
	| sort --unique \
	| while IFS= read -r module
do
	if [[ -e ${REPO_ROOT}/${module}/.lfrbuild-portal-deprecated ]]
	then
		echo "${module}"
	fi
done
```

Leave out `modules/dxp/apps/saml/saml-admin-rest-test` and every module under `modules/sdk`. Convert each module to its Gradle project path with `sed "s#^modules/##; s#/#:#g"`. Keep two sorted lists. The first holds the test compile tasks. A task is the project path followed by `:compileTestJava` for a `src/test` consumer, or by `:compileTestIntegrationJava` for a `src/testIntegration` consumer. An example is `apps:blogs:blogs-test:compileTestIntegrationJava`. The second list holds the deprecated consumers.

Assert the search root before believing an empty result. `git grep` exits 1 with no output both for a clean scan and for a pathspec that matches nothing, so a search from the wrong directory returns, byte for byte, what a genuinely clean scan returns:

```bash
[[ -d ${REPO_ROOT}/modules ]] || exit 1
```

When both lists are empty, compile nothing and report **NO COVERAGE**, naming the changed modules as having no test or deprecated consumer.

### Compile

Pass all the test compile tasks to one Gradle run, never one run per module. Gradle schedules the tasks in parallel itself, and every separate run pays the configuration cost again. Write the tasks to `${TASKS_FILE}` one per line, using a path such as `${LOG_DIR}/test-tasks.txt`:

```bash
TASKS=()

while IFS= read -r task_path
do
	TASKS+=(":${task_path}" --rerun)
done < "${TASKS_FILE}"

("${REPO_ROOT}/gradlew" \
	--continue \
	--parallel \
	--project-dir "${REPO_ROOT}/modules" \
	"${TASKS[@]}")
```

Compile the deprecated consumers in a second run, since they exist only under their own profile and that profile leaves out the `-test` modules. Build `${TASKS}` the same way from the deprecated consumers, with `:compileJava` after each project path. Then run:

```bash
("${REPO_ROOT}/gradlew" \
	--continue \
	--parallel \
	--project-dir "${REPO_ROOT}/modules" \
	-Dbuild.profile=portal-deprecated \
	"${TASKS[@]}")
```

Put `--rerun` after every task path, since it is an option on the task before it and forces only that one. Without it a warm tree prints `UP-TO-DATE` and the build cache restores `FROM-CACHE`, either way a green log in which the compile under test never ran. Do not pass `--no-build-cache`, which reruns the node and yarn bootstrap and leaves the tree broken for the next validation.

Count the executed compile lines and check the count against the list:

```bash
command grep --count --extended-regexp '^> Task :.*:compileTest(Integration)?Java$' "${LOG}"
```

A consumer whose task line is missing, or ends in `UP-TO-DATE` or `FROM-CACHE`, was not compiled, so name it and report **NOT VERIFIED** for it.

### Verdict

FAIL when a compile reports an error naming something the diff changed, either a changed type or a file in a changed module, and name the consumer and the error.

An error that names nothing the diff changed is in code the branch did not touch, so it either predates the branch or comes from the environment. Report **NOT VERIFIED** for that consumer and name the error. A common shape is `package com.liferay.portal.kernel.model does not exist` for a package that plainly exists in the tree, which is a broken snapshot rather than the branch.

A compile can stop short of checking every consumer in two ways. `javac` stops at 100 errors and prints the line below, while a fatal abort, such as `error: cannot access` or an annotation processor crash, prints nothing comparable:

```bash
command grep --fixed-strings 'only showing the first' "${LOG}"
```

When either happened in a consumer, report **NOT VERIFIED** naming it.

PASS when the build reported `BUILD SUCCESSFUL` and every compile task printed an executed line.

## Checklist

```
- [ ] Compile tests: <count> tasks in one run
- [ ] Compile deprecated: <count> consumers in one run
```

## Time Estimate

Under 1 min for a few dozen consumers on a warm tree, and about 4 min for every `testIntegration` module in the repository, or 11 min cold. Compiling the `src/test` consumers can take up to as long again. Add about 1 min when there are deprecated consumers.