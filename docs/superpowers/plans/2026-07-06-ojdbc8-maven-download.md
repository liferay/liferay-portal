# OJDBC8 Maven Download Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remove tracked `lib/development/ojdbc8.jar` and make Ant download Oracle JDBC driver `com.oracle.database.jdbc:ojdbc8:23.26.2.0.0` from Maven Central when needed.

**Architecture:** Keep the legacy runtime contract: Ant still copies `lib/development/ojdbc8.jar` during `deploy-additional-jars`. Add a small prerequisite target that materializes that file from Maven Central only when missing. Keep generated binary untracked with `.gitignore`.

**Tech Stack:** Ant build, Maven Central URL layout, Git ignore rules, existing shell dependency-cache helper.

## Global Constraints

- Use Oracle JDBC Maven coordinate `com.oracle.database.jdbc:ojdbc8:23.26.2.0.0`.
- Generated file path must remain `lib/development/ojdbc8.jar` for existing Ant copy logic.
- Remove `lib/development/ojdbc8.jar` from Git tracking.
- Do not edit `build.wos.properties` or `release.wos.properties`.
- Do not create commits unless explicitly requested; stage only intended non-generated files if staging is requested.
- Do not add a new dependency manager or wrapper.

---

## File Structure

- Modify `.gitignore`: ignore the generated Oracle JDBC jar at `/lib/development/ojdbc8.jar`.
- Modify `build.xml`: add Oracle JDBC download properties and a target that downloads the jar only when missing; make `deploy-additional-jars` depend on that target.
- Modify `scripts/sweep-deps.sh`: update old Oracle JDBC cache alias from `com.oracle.jdbc:ojdbc8:12.2.0.1` to the Central coordinate `com.oracle.database.jdbc:ojdbc8:23.26.2.0.0`.
- Delete `lib/development/ojdbc8.jar`: remove binary from repository tracking and working tree.

### Task 1: Ignore Generated Oracle JDBC Jar

**Files:**
- Modify: `.gitignore:36-43`

**Interfaces:**
- Consumes: final generated jar path `/lib/development/ojdbc8.jar`.
- Produces: Git ignore rule preventing downloaded jar from being re-added.

- [ ] **Step 1: Add ignore rule**

Edit `.gitignore` near the other root-level generated paths and add this exact line:

```gitignore
/lib/development/ojdbc8.jar
```

Suggested placement after `/jacoco`:

```gitignore
/jacoco
/lib/development/ojdbc8.jar
/modules/.releng/private
```

- [ ] **Step 2: Verify ignore rule matches jar path**

Run:

```bash
git check-ignore -v lib/development/ojdbc8.jar
```

Expected: output includes `.gitignore` and `/lib/development/ojdbc8.jar`. If file is still tracked, `git check-ignore` may be silent unless run with `--no-index`; use this fallback:

```bash
git check-ignore -v --no-index lib/development/ojdbc8.jar
```

Expected fallback output includes `.gitignore` and `/lib/development/ojdbc8.jar`.

### Task 2: Add Ant Download Target

**Files:**
- Modify: `build.xml:3-6`
- Modify: `build.xml:534-541`

**Interfaces:**
- Consumes: Maven Central artifact URL `https://repo1.maven.org/maven2/com/oracle/database/jdbc/ojdbc8/23.26.2.0.0/ojdbc8-23.26.2.0.0.jar`.
- Produces: Ant target `download-oracle-jdbc-driver` that creates `lib/development/ojdbc8.jar` when missing.
- Produces: `deploy-additional-jars` depends on `download-oracle-jdbc-driver`.

- [ ] **Step 1: Add Oracle JDBC properties**

In `build.xml`, immediately after `<import file="build-common.xml" />`, add:

```xml
	<property name="oracle.jdbc.driver.file" value="lib/development/ojdbc8.jar" />
	<property name="oracle.jdbc.driver.version" value="23.26.2.0.0" />
	<property name="oracle.jdbc.driver.url" value="https://repo1.maven.org/maven2/com/oracle/database/jdbc/ojdbc8/${oracle.jdbc.driver.version}/ojdbc8-${oracle.jdbc.driver.version}.jar" />
```

Result should look like:

```xml
<project basedir="." default="compile" name="portal" xmlns:antelope="antlib:ise.antelope.tasks" xmlns:artifact="antlib:org.apache.maven.artifact.ant">
	<import file="build-common.xml" />

	<property name="oracle.jdbc.driver.file" value="lib/development/ojdbc8.jar" />
	<property name="oracle.jdbc.driver.version" value="23.26.2.0.0" />
	<property name="oracle.jdbc.driver.url" value="https://repo1.maven.org/maven2/com/oracle/database/jdbc/ojdbc8/${oracle.jdbc.driver.version}/ojdbc8-${oracle.jdbc.driver.version}.jar" />

	<macrodef name="clean-liferay-home">
```

- [ ] **Step 2: Add conditional download target**

In `build.xml`, immediately before `<target name="deploy-additional-jars">`, add:

```xml
	<target name="download-oracle-jdbc-driver">
		<if>
			<not>
				<available file="${oracle.jdbc.driver.file}" />
			</not>
			<then>
				<mkdir dir="lib/development" />

				<get
					dest="${oracle.jdbc.driver.file}"
					src="${oracle.jdbc.driver.url}"
					usetimestamp="true"
				/>
			</then>
		</if>
	</target>
```

- [ ] **Step 3: Make deploy copy depend on download**

Change the target declaration from:

```xml
	<target name="deploy-additional-jars">
```

to:

```xml
	<target depends="download-oracle-jdbc-driver" name="deploy-additional-jars">
```

- [ ] **Step 4: Verify XML parses and target can run**

Run:

```bash
ant -projecthelp
```

Expected: Ant exits `BUILD SUCCESSFUL` and lists targets. No XML parse error.

- [ ] **Step 5: Verify download target creates jar when missing**

If `lib/development/ojdbc8.jar` exists, remove only that generated file from the working tree before running this target:

```bash
rm lib/development/ojdbc8.jar
ant download-oracle-jdbc-driver
```

Expected: Ant exits `BUILD SUCCESSFUL`; `lib/development/ojdbc8.jar` exists.

- [ ] **Step 6: Verify target is idempotent**

Run again:

```bash
ant download-oracle-jdbc-driver
```

Expected: Ant exits `BUILD SUCCESSFUL`; no duplicate file; target does not re-download when file already exists.

### Task 3: Update Dependency Cache Helper

**Files:**
- Modify: `scripts/sweep-deps.sh:175-181`

**Interfaces:**
- Consumes: helper functions already defined in `scripts/sweep-deps.sh`: `in_m2`, `install_url`, `CENTRAL`, `UNRESOLVED`.
- Produces: cache helper installs `com.oracle.database.jdbc:ojdbc8:23.26.2.0.0` from Central, matching Ant download version.

- [ ] **Step 1: Replace old Oracle JDBC block**

Replace lines 175-181:

```bash
# ojdbc8 — never published under com.oracle.jdbc; identical artifact exists on
# Central as com.oracle.database.jdbc:ojdbc8
if ! in_m2 com.oracle.jdbc ojdbc8 12.2.0.1; then
	install_url com.oracle.jdbc ojdbc8 12.2.0.1 \
		"$CENTRAL/com/oracle/database/jdbc/ojdbc8/12.2.0.1/ojdbc8-12.2.0.1.jar" ||
		{ echo "  UNRESOLVED com.oracle.jdbc:ojdbc8:12.2.0.1"; UNRESOLVED=$((UNRESOLVED + 1)); }
fi
```

with:

```bash
# ojdbc8 is published on Central under com.oracle.database.jdbc.
if ! in_m2 com.oracle.database.jdbc ojdbc8 23.26.2.0.0; then
	install_url com.oracle.database.jdbc ojdbc8 23.26.2.0.0 \
		"$CENTRAL/com/oracle/database/jdbc/ojdbc8/23.26.2.0.0/ojdbc8-23.26.2.0.0.jar" ||
		{ echo "  UNRESOLVED com.oracle.database.jdbc:ojdbc8:23.26.2.0.0"; UNRESOLVED=$((UNRESOLVED + 1)); }
fi
```

- [ ] **Step 2: Syntax-check shell script**

Run:

```bash
bash -n scripts/sweep-deps.sh
```

Expected: no output; exit code 0.

### Task 4: Remove Tracked Binary and Verify Final State

**Files:**
- Delete: `lib/development/ojdbc8.jar`
- Verify: `.gitignore`
- Verify: `build.xml`
- Verify: `scripts/sweep-deps.sh`

**Interfaces:**
- Consumes: ignore rule from Task 1 and download target from Task 2.
- Produces: repository has no tracked Oracle JDBC jar; build can recreate it.

- [ ] **Step 1: Remove jar from working tree**

Run:

```bash
rm lib/development/ojdbc8.jar
```

Expected: file is absent. If already absent, command may print `No such file or directory`; that is acceptable only if `git status --short lib/development/ojdbc8.jar` still shows deletion after the next step.

- [ ] **Step 2: Confirm Git sees binary deletion**

Run:

```bash
git status --short lib/development/ojdbc8.jar
```

Expected:

```text
 D lib/development/ojdbc8.jar
```

If output is empty, the jar was not tracked in this checkout; continue with verification.

- [ ] **Step 3: Verify no tracked jar remains after regeneration**

Run:

```bash
ant download-oracle-jdbc-driver
git status --short lib/development/ojdbc8.jar
```

Expected: Ant exits `BUILD SUCCESSFUL`; Git status still shows deletion only, not `?? lib/development/ojdbc8.jar`, because `.gitignore` hides regenerated jar.

- [ ] **Step 4: Verify deployment prerequisite path**

Run:

```bash
ant -Dapp.server.type=tomcat deploy-additional-jars
```

Expected: `download-oracle-jdbc-driver` runs before `deploy-additional-jars`; Ant copies `ojdbc8.jar` from `lib/development` to `${app.server.lib.global.dir}`; build exits `BUILD SUCCESSFUL` if local app server directories are configured.

If local app server directories are not configured, expected failure may mention missing app server path after the download target succeeds. In that case, verify the target ordering from Ant output and keep the narrower `ant download-oracle-jdbc-driver` result as functional proof.

- [ ] **Step 5: Review final diff**

Run:

```bash
git diff -- .gitignore build.xml scripts/sweep-deps.sh
git status --short
```

Expected changes:

```text
 M .gitignore
 M build.xml
 M scripts/sweep-deps.sh
 D lib/development/ojdbc8.jar
```

Generated `lib/development/ojdbc8.jar` must not appear as untracked.

## Self-Review

- Spec coverage: plan removes tracked jar, adds Maven Central download for version `23.26.2.0.0`, preserves `lib/development/ojdbc8.jar`, and updates related cache helper.
- Placeholder scan: no placeholder markers, vague implementation steps, or missing commands remain.
- Type/property consistency: `oracle.jdbc.driver.file`, `oracle.jdbc.driver.version`, and `oracle.jdbc.driver.url` names are used consistently in `build.xml` steps.
