# Baseline Gradle Plugin

The Baseline Gradle plugin lets you verify that the OSGi [semantic versioning](http://semver.org/)
rules are obeyed by your OSGi bundle.

When you run the [`baseline`](#baseline) task, the plugin *baselines* the new
bundle against the latest released non-snapshot bundle (i.e., the *baseline*).
That is, it compares the public exported API of the new bundle with
the baseline. If there are any changes, it uses the OSGi semantic versioning
rules to calculate the minimum new version. If the new bundle has a lower
version, errors are thrown.

The plugin has been successfully tested with Gradle 8.5.

## Usage

To use the plugin, include it in your build script:

```gradle
buildscript {
	dependencies {
		classpath group: "com.liferay", name: "com.liferay.gradle.plugins.baseline", version: "7.0.7"
	}

	repositories {
		maven {
			url "https://repository-cdn.liferay.com/nexus/content/groups/public"
		}
	}
}

apply plugin: "com.liferay.baseline"
```

The Baseline plugin automatically applies the [`java`](https://docs.gradle.org/current/userguide/java_plugin.html)
and [`reporting-base`](https://docs.gradle.org/current/userguide/standard_plugins.html#sec:base_plugins) plugins.

Since the plugin needs to download the baseline, you have to configure a
[repository](https://docs.gradle.org/current/userguide/artifact_dependencies_tutorial.html#sec:repositories_tutorial)
that hosts it; for example, the central Maven 2 repository:

```gradle
repositories {
	mavenCentral()
}
```

## Project Extension

The Baseline plugin exposes the following properties through the
`baselineConfiguration` extension:

Property Name | Type | Default Value | Description
------------- | ---- | ------------- | -----------
`allowMavenLocal` | `boolean` | `false` | Whether to let the baseline come from the local Maven cache (by default: `${user.home}/.m2`). If the local Maven cache is not [configured](https://docs.gradle.org/current/userguide/dependency_management.html#sub:maven_local) as a project repository, this property has no effect.
`lowestBaselineVersion` | `String` | `"1.0.0"` | The greatest project version to ignore for the baseline check. If the [project version](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.bundling.Jar.html#org.gradle.api.tasks.bundling.Jar:version) is less than or equal to the value of this property, the [`baseline`](#baseline) task is skipped.

## Tasks

The plugin adds one task to your project:

Name | Depends On | Type | Description
---- | ---------- | ---- | -----------
<a name="baseline"></a>`baseline` | [`jar`]((https://docs.gradle.org/current/userguide/java_plugin.html#sec:jar)) | [`BaselineTask`](#baselinetask) | Compares the public API of this project with the public API of the previous released version, if found.

The `baseline` task is automatically configured with sensible defaults:

Property Name | Default Value
------------- | -------------
[`baselineConfiguration`](#baselineconfiguration) | [`configurations.baseline`](#baseline-dependency)
[`bndFile`](#bndfile) | `${project.projectDir}/bnd.bnd`
[`newJarFile`](#newjarfile) | [`project.tasks.jar.archivePath`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.bundling.Jar.html#org.gradle.api.tasks.bundling.Jar:archivePath)
[`sourceDir`](#sourcedir) | The first `resources` directory of the `main` source set (by default: `src/main/resources`).

### BaselineTask

#### Task Properties

Property Name | Type | Default Value | Description
------------- | ---- | ------------- | -----------
<a name="baselineconfiguration"></a>`baselineConfiguration` | `Configuration` | `null` | The configuration that contains exactly one dependency to the baseline bundle.
<a name="bndfile"></a>`bndFile` | `File` | `null` | The BND file of the project. If provided, the task will automatically update the [`Bundle-Version`](http://bnd.bndtools.org/heads/bundle_version.html) header.
`forceCalculatedVersion` | `boolean` | `false` | Whether to fail the baseline check if the `Bundle-Version` has been excessively increased.
<a name="ignoreexcessiveversionincreases"></a>`ignoreExcessiveVersionIncreases` | `boolean` | `false` | Whether to ignore excessive package version increase warnings.
<a name="ignorefailures"></a>`ignoreFailures` | `boolean` | `false` | Whether the build should not break when semantic versioning errors are found.
`logFile` | `File` | `null` | The file to which the results of the baseline check are written. *(Read-only)*
`logFileName` | `String` | `"baseline/${task.name}.log"` | The name of the file to which the results of the baseline check are written. If the `reporting-base` plugin is applied, the file name is relative to [`reporting.baseDir`](https://docs.gradle.org/current/dsl/org.gradle.api.reporting.ReportingExtension.html#org.gradle.api.reporting.ReportingExtension:baseDir); otherwise, it's relative to the project directory.
<a name="newjarfile"></a>`newJarFile` | `File` | `null` | The file of the new OSGi bundle.
`reportDiff` | `boolean` | `true` if the project property `baseline.jar.report.level` has either value `"diff"` or `"persist"`; `false` otherwise | Whether to show a granular, differential report of all changes that occurred in the exported packages of the OSGi bundle.
`reportOnlyDirtyPackages` | `boolean` | Value of the project property `baseline.jar.report.only.dirty.packages` if specified; `true` otherwise. | Whether to show only packages with API changes in the report.
<a name="sourcedir"></a>`sourceDir` | `File` | `null` | The directory to which the [`packageinfo`](http://bnd.bndtools.org/chapters/170-versioning.html#versioning-packages) files are generated or updated.

The properties of type `File` support any type that can be resolved by
[`project.file`](https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:file(java.css.Object)).
Moreover, it is possible to use Closures and Callables as values for the
`String` properties to defer evaluation until task execution.

### Helper Tasks

All tasks have the property [`ignoreExcessiveVersionIncreases`](#ignoreexcessiveversionincreases)
set to `true`.

## Additional Configuration

There are additional configurations that can help you baseline your OSGi bundle.

### Baseline Dependency

The plugin creates a configuration called `baseline` with a default dependency
to a released non-snapshot version of the bundle:

- version range `(,${project.version})`

It is possible to override this setting and use a different version of the
bundle as baseline.

### System Properties

It is possible to set the default values of the [`ignoreFailures`](#ignorefailures)
property for a `BaselineTask` task via system properties:

	-D${task.name}.ignoreFailures=true

For example, run the following Bash command to execute the baseline check
without breaking the build, in case of errors:

```bash
./gradlew baseline -Dbaseline.ignoreFailures=true
```