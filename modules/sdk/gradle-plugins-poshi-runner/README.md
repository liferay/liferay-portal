# Poshi Runner Gradle Plugin

The Poshi Runner Gradle plugin lets you use [Poshi](https://github.com/liferay/liferay-portal/tree/master/modules/test/poshi)
to write and run functional tests.

## Usage

To use the plugin, include it in your build script:

```gradle
buildscript {
	dependencies {
		classpath group: "com.liferay", name: "com.liferay.gradle.plugins.poshi.runner", version: "4.0.30"
	}

	repositories {
		maven {
			url "https://repository-cdn.liferay.com/nexus/content/groups/public"
		}
	}
}

apply plugin: "com.liferay.poshi.runner"
```

## Project Extension

The Poshi Runner Gradle plugin exposes the following properties through the
extension named `poshiRunner`:

Property Name | Type | Default Value | Description
------------- | ---- | ------------- | -----------
`baseDir` | `File` | `src/testFunctional` | The base directory of hte project that will store the Poshi files. The plugin will set `test.base.dir.name` to `baseDir` in system properties.
`openCVVersion` | `String` | `2.4.9-0.9` | The version of OpenCV to use (Sikuli dependency).
`poshiPropertiesFile` | `File` | `poshi.properties` | The properties file that will override and update test configurations.
`testNames` | Set<String> | | The test names to be passed along to Poshi. The plugin will set `test.name` to `testNames` in system properties.
`testRunType` | `String` | `sequential` | The mode in which to run tests, `parallel` or `sequential`.
`version` | `String` | `1.0.311` | The version of [`com.liferay.poshi.runner`](https://github.com/liferay/liferay-portal/tree/master/modules/test/poshi/poshi-runner) to use.

The properties of type `File` support any type that can be resolved by
[`project.file`](https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:file(java.css.Object)).
Moreover, it is possible to use Closures and Callables as values for `String`,
to defer evaluation until execution.

### Setting a specific version of Poshi Runner

In your `build.gradle`, after applying the plugin:

```gradle
poshiRunner {
	version = "1.0.311"
}
```

## Tasks

The plugin adds a series of tasks to your project:

Name | Depends On | Type | Description
---- | ---------- | ---- | -----------
`executePQLQuery` | \- | [`JavaExec`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html) | Invokes the Poshi class/method: [`com.liferay.poshi.runner.PoshiRunnerCommandExecutor.executePQLQuery`](https://github.com/liferay/liferay-portal/blob/master/modules/test/poshi/poshi-runner/src/main/java/com/liferay/poshi/runner/PoshiRunnerCommandExecutor.java). Executes a PQL query.
`evaluatePoshiConsole` | \- | [`JavaExec`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html) | Invokes the Poshi class: [`com.liferay.poshi.runner.PoshiRunnerConsoleEvaluator`](https://github.com/liferay/liferay-portal/blob/master/modules/test/poshi/poshi-runner/src/main/java/com/liferay/poshi/runner/PoshiRunnerConsoleEvaluator.java). Evaluates console output errors.
`runPoshi` | \- | [`Test`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.testing.Test.html) | Executes a Poshi test.
`validatePoshi` | \- | [`JavaExec`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html) | Invokes the Poshi class: [`com.liferay.poshi.core.PoshiValidation`](https://github.com/liferay/liferay-portal/blob/master/modules/test/poshi/poshi-core/src/main/java/com/liferay/poshi/core/PoshiValidation.java). Validates the Poshi file syntax
`writePoshiProperties` | \- | [`JavaExec`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html) | Generates properties using meta data from Poshi files in the project.

# Poshi Runner Resources Gradle Plugin

The Poshi Runner Resources Gradle plugin lets you use Poshi resource jars in your Poshi project or publish them.

## Usage

To use the plugin, include it in your build script:

```gradle
buildscript {
	dependencies {
		classpath group: "com.liferay", name: "com.liferay.gradle.plugins.poshi.runner", version: "34.0.0
	}

	repositories {
		maven {
			url "https://repository-cdn.liferay.com/nexus/content/groups/public"
		}
	}
}

apply plugin: "com.liferay.poshi.runner.resources.defaults"
```

To use a specific Poshi Resource JAR, add the following applying the plugin (where `GROUP`, `NAME` and `VERSION` refer to a maven dependency):

```gradle
dependencies {
	poshiRunnerResources group: "GROUP", name: "NAME", version: "VERSION"
}
```

For example (this resource is already configured in the plugin by default):

```gradle
dependencies {
	poshiRunnerResources group: "com.liferay", name: "com.liferay.poshi.runner.resources", version: "latest.release"
}
```

## Project Extension

The Poshi Runner Gradle plugin exposes the following properties through the
extension named `poshiRunnerResources`:

Property Name | Type | Default Value | Description
------------- | ---- | ------------- | -----------
`artifactAppendix` | `String` | | For creating a Poshi resource jar. The appendix for the generated Poshi resource jar (used for the group ID)
`artifactVersion` | `String` | | For creating a Poshi resource jar. The version for the generated Poshi resource jar (used for the artifact ID)
`baseName` | `String` | `default` | For creating a Poshi resource jar. The base directory name for the Poshi files in the Poshi resource jar.
`dirs` | Set<String> | | For creating a Poshi resource jar. The directories to include as subdirectories of `baseName` in teh Poshi resource jar.
`version` | `String` | `latest.release` | For using a Poshi resource jar. The version of a Poshi resource jar to be used.

## Tasks

The plugin adds a series of tasks to your project:

Name | Depends On | Type | Description
---- | ---------- | ---- | -----------
`jarPoshiRunnerResources` | \- | [`Jar`](https://docs.gradle.org/6.6/dsl/org.gradle.api.tasks.bundling.Jar.html) | Creates a Poshi resource jar.