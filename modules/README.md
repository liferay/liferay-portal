# Liferay Modules

This document explains how to use Liferay's build system and its recommended
best practices.

### Enable Java Compiler Warnings

In order to enable Java [compiler warnings](http://docs.oracle.com/javase/8/docs/technotes/tools/windows/javac.html#BHCJCABJ)
when building an OSGi module with Gradle, please set the `-D[task name].lint`
system property (where `[task name]` is the name of the [`JavaCompile`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.compile.JavaCompile.html)
task to configure) to a comma-separated list of warnings. For example:

```bash
./gradlew compileJava -DcompileJava.lint=deprecation,unchecked
./gradlew compileTestJava -DcompileJava.lint=deprecation,unchecked
./gradlew compileTestIntegrationJava -DcompileJava.lint=deprecation,unchecked
```

### Deploy Directory

The module's deploy directory is the `deploy.destinationDir` property (the
`destinationDir` property of the `deploy` task). This property is set to
`liferay.deployDir` (the `deployDir` property of the `liferay` extension object)
by default.

The logic that chooses the default deploy directory is as follows:

- For OSGi modules:

	1. If the project directory contains a `.lfrbuild-app-server-lib` marker
	file, the module is deployed to `${app.server.portal.dir}/WEB-INF/lib`.

	1. If the project directory contains a `.lfrbuild-tool` marker file, the
	module is deployed to `${liferay.home}/tools/${module.dir.name}`.

	1. If the project directory contains a `.lfrbuild-static` marker file, the
	module is deployed to `${liferay home}/osgi/static`.

	1. Otherwise, the module is deployed to `${liferay home}/osgi/portal`.
- For themes:

	1. If the project directory is in the main Liferay repository, the theme is
	deployed to `${liferay home}/osgi/portal-war`.

	1. If the `required-for-startup` property in the
  `src/WEB-INF/liferay-plugin-package.properties` file is `true`, the theme is
	deployed to `${liferay home}/osgi/war`.

	1. Otherwise, the theme is deployed to `${liferay home}/deploy`.

If possible, you should always use these marker files to specify the deploy
directory of your modules. If none of these cases apply to you, then add
something like this to your `build.gradle`:

```gradle
liferay {
   deployDir = file("${liferayHome}/osgi/test")
}
```

To know what paths (e.g., `liferayHome`) are available, examine the getter
methods in the `LiferayExtension` class.

It's fine to have both `.lfrbuild-portal` and one of these marker files in the
same project; the `.lfrbuild-portal` file tells the build system to build the
module with `ant all` and the other marker files choose the deploy directory.

## Marker Files

### Baseline

File Name | Description
--------- | -----------
`.lfrbuild-packageinfo` | Ignores specified baseline warnings: `EXCESSIVE-VERSION-INCREASE`, `PACKAGE-ADDED-MISSING-PACKAGEINFO`, `PACKAGE-REMOVED`, `PACKAGE-REMOVED-UNNECESSARY-PACKAGEINFO`, `VERSION-INCREASE-REQUIRED`, `VERSION-INCREASE-SUGGESTED`. Adding the suffix `-RECURSIVE` (i.e., `EXCESSIVE-VERSION-INCREASE-RECURSIVE`) will apply the ignored warning to the current directory and all child directories.

### Build

File Name | Description
--------- | -----------
`.lfrbuild-cms-standalone` | Deploys the module during the `ant all` execution when the `cms-standalone` build profile is selected (`-Dbuild.profile=cms-standalone`).
`.lfrbuild-portal-deprecated` | Marks the module as deprecated and skip deployment during the `ant all` execution. `-test` modules never have this file.
`.lfrbuild-portal-pre` | Builds the module during the `ant compile` execution in the `tmp/lib-pre` directory before building `portal-kernel`, `portal-impl`, etc.
`.lfrbuild-portal-private` | Deploys the module during the `ant all` execution in a private branch. `-test` modules never have this file.
`.lfrbuild-portal-public` | Deploys the module during the `ant all` execution in a public branch. `-test` modules never have this file.
`.lfrbuild-portal-skip-deploy` | Skip deploying the module during the `ant all` execution.
`.lfrbuild-portal` | Deploys the module during the `ant all` execution. `-test` modules never have this file.

### Continuous Integration

File Name | Description
--------- | -----------
`.lfrbuild-ci` | Deploys the module during the `ant all` execution, but only if running in Jenkins.
`.lfrbuild-ci-skip-test-integration-check` | When on Jenkins, prevent the `testIntegration` task from failing if a project defined in the `testIntegrationCompile` configuration should not be deployed and has this marker file.
`.lfrbuild-semantic-versioning` | Enables the semantic versioning check of the module on CI. `apps` and `core` modules are already checked, so they don't need this marker file.

### Deploy Directory

File Name | Description
--------- | -----------
`.lfrbuild-app-server-lib` | Deploys the module to `${app.server.portal.dir}/WEB-INF/lib`.
`.lfrbuild-static` | Deploys the module to `${liferay home}/osgi/static`.
`.lfrbuild-tool` | Deploys the module to `${liferay.home}/tools/${module.dir.name}`.

### Faro

File Name | Description
--------- | -----------
`.lfrbuild-faro-connector` | Deploys the module to the Faro client portal directory.
`.lfrbuild-faro-site` | Deploys the module to the Faro site portal directory.

### LCS

File Name | Description
--------- | -----------
`.lfrbuild-spark-job` | Configures the module as an Apache Spark job.
`.lfrbuild-spring-boot` | Configures the module as a Spring Boot application.

### Release

File Name | Description
--------- | -----------
`.lfrbuild-app-portal-build-ignore` | Prevents the `syncAppProperties` task from updating the `app.portal.build` property in the `app.properties` file.
`.lfrbuild-master-only` | Marks a module that should not be forked and deleted for release branches. If a `.lfrbuild-master-only` file is added to a parent directory, the whole subtree should not be forked.
`.lfrbuild-release-src` | Includes the app's source code in the DXP release, when added to the root of an app.
`.lfrbuild-releng-ignore` | Ignores checking the module for stale artifacts. An artifact is *stale* when the module has code that is different from the latest published release. This module can never be published. If a `.lfrbuild-releng-ignore` file is added to a parent directory, the whole subtree is ignored.
`.lfrbuild-releng-skip-source` | Prevents the artifact's source code from being published.
`.lfrbuild-releng-skip-update-file-versions` | Prevents the `updateFileVersions` task from converting project dependencies into module dependencies. If a `.lfrbuild-releng-skip-update-file-versions` file is added to a parent directory, the whole subtree is skipped.

### Themes

File Name | Description
--------- | -----------
`.lfrbuild-missing-resources-importer` | Prevents the theme from being published in case it doesn't contain the *Resources Importer* files. This is only added on the `master `branch.

## Source Formatting

### Gradle Files

The following source formatting rules should be followed for Gradle files.

* Always use double quotes, unless single quotes are necessary.
* Never define local variables with `def`; explicitly define the types, even for
closure arguments.
* Dependencies:
	* There is usually no need to declare `transitive: false` for
	`compileInclude` or `provided` dependencies; this is the default behavior.
	* If a module only includes unit tests, add all dependencies to the
	`testCompile` configuration. If a module only includes integration tests,
	add all dependencies to the `testIntegrationCompile` configuration.
	* Always sort dependencies alphabetically.
	* Separate dependencies of different configurations with an empty line.
* Ordering inside Gradle files:

	1. Class imports, sorted and separated in groups (same logic used in Java).

	1. `buildscript { ... }` block.

	1. `apply plugin` logic, sorted alphabetically.

	1. `ext { ... }` block.

	1. Initialization logic.

	1. Task creation: `task taskName(type: TaskType)` or simply `task taskName`
	for default tasks. Don't declare the task dependencies here.

	1. Project property assignments (e.g., `sourceCompatibility`).

	1. Variables used globally by the whole script, like a URL or a relative
	path.

	1. Blocks `{ ... }` to configure tasks, extension objects, etc.
* Inside a block `{ ... }`:
	* If variables are needed, declare them inside the block at the beginning.
	* If setting a property, use the `=` assignment, even if Gradle doesn't
	complain when it's not used.
	* If multiple assignments are necessary (for example, multiple `dependsOn`
	or multiple `excludes` declarations), write them on separate lines.
	* Order assignments alphabetically, leaving an empty line after multiple
	calls to the same method (e.g., after multiple `dependsOn` declarations) or if
	the assignment has a closure.

## Subrepositories

### `gradle.properties`

The following settings are available to you in the `gradle.properties` file of a
Liferay subrepository (e.g., [com-liferay-poshi-runner](https://github.com/liferay/com-liferay-poshi-runner)).

Property Name | Mandatory | Description
------------- | --------- | -----------
`com.liferay.source.formatter.version` | No | The version of Source Formatter to use in the subrepository. If the property is not set, the latest version is used.
`project.group` | No | The group ID of the artifacts that are published from the subrepository. If this property is not set, the default value `com.liferay` is used.
`project.path.prefix` | Yes | The project path of the Gradle prefix. It must start with a `':'` character and be equal to the relative path of the subrepository directory inside the main Liferay repository, with path components separated by `':'` characters instead of slashes.
`systemProp.repository.private.password` | No | The password used to access the private Maven repository. If set, this property must be equal to the `build.repository.private.password` property value in `build.properties`.
`systemProp.repository.private.url` | No | The URL of the private Maven repository. If set, this property must be equal to the `build.repository.private.url` property value in `build.properties`.
`systemProp.repository.private.username` | No | The username used to access the private Maven repository. If set, this property must be equal to the `build.repository.private.username` property value in `build.properties`.