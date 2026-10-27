# Whip Gradle Plugin

The Whip Gradle plugin lets you use the [Liferay Whip](https://github.com/liferay/liferay-portal/tree/master/modules/test/whip)
library to ensure that unit tests fully cover your project's code. See
[here](https://github.com/liferay/liferay-portal/tree/master/modules/sdk/gradle-plugins-whip/src/gradleTest/smoke)
for a usage sample.

The plugin has been successfully tested with Gradle 8.5.

## Usage

To use the plugin, include it in your build script:

```gradle
buildscript {
	dependencies {
		classpath group: "com.liferay", name: "com.liferay.gradle.plugins.whip", version: "1.0.12"
	}

	repositories {
		maven {
			url "https://repository-cdn.liferay.com/nexus/content/groups/public"
		}
	}
}

apply plugin: "com.liferay.whip"
```

Since the plugin automatically resolves the Liferay Whip library as a
dependency, you have to configure a repository that hosts the library and its
transitive dependencies. The Liferay CDN repository hosts them all:

```gradle
repositories {
	maven {
		url "https://repository-cdn.liferay.com/nexus/content/groups/public"
	}
}
```

By default, Whip is automatically applied to all tasks of type [`Test`](https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/testing/Test.html).
If a task has Whip applied and Whip is [enabled](#enabled), then Whip is
configured as a Java Agent.

## Project Extension

The Whip Gradle plugin exposes the following properties through the extension
named `whip`:

Property Name | Type | Default Value | Description
------------- | ---- | ------------- | -----------
<a name="version"></a>`version` | `String` | `latest.release` | The version of the Liferay Whip library to use.

The same extension exposes the following methods:

Method | Description
------ | -----------
`void applyTo(Task task)` | Applies Whip to a task. The task instance must implement the [`JavaForkOptions`](https://docs.gradle.org/current/javadoc/org/gradle/process/JavaForkOptions.html) interface.

## Task Extension

If Whip is applied, the following task properties are available through the
extension named `whip`:

Property Name | Type | Default Value | Description
------------- | ---- | ------------- | -----------
`dataFile` | `File` | `test-coverage/whip.dat` |
<a name="enabled"></a>`enabled` | `boolean` | `true` | Whether to configure Whip as a Java Agent.
`excludes` | `List<String>` | `[]` | The class name patterns to exclude when checking for unit test code coverage. For example, a value could be `['.*Test', '.*Test\\$.*', '.*\\$Proxy.*', 'com/liferay/whip/.*']`.
`includes` | `List<String>` | `[]` | The class name patterns to include when checking for unit test code coverage.
`instrumentDump` | `boolean` | `false` |
<a name="whipjarfile"></a>`whipJarFile` | `File` | The first file in the `whip` configuration whose name starts with `com.liferay.whip-`. | The Whip JAR file.

The same extension exposes the following methods:

Method | Description
------ | -----------
`WhipTaskExtension excludes(Iterable<Object> excludes)` | Adds class name patterns to exclude when checking for unit test coverage.
`WhipTaskExtension excludes(Object... excludes)` | Adds class name patterns to exclude when checking for unit test coverage.
`WhipTaskExtension includes(Iterable<Object> includes)` | Adds class name patterns to include when checking for unit test coverage.
`WhipTaskExtension includes(Object... includes)` | Adds class name patterns to include when checking for unit test coverage.

## Additional Configuration

There are additional configurations that can help you use Whip.

### Liferay Whip Dependency

By default, the Whip Gradle plugin creates a configuration called `whip` and
adds a dependency to the version of Liferay Whip configured in the [`whip.version`](#version)
extension property. It is possible to override this setting and use a specific
version of the library by manually adding a dependency to the `whip`
configuration:

```gradle
dependencies {
	whip group: "com.liferay", name: "com.liferay.whip", version: "1.0.6"
}
```

In order to leverage the sensible default of the [`whip.whipJarFile`](#whipjarfile)
task property, the name of the dependency must be `com.liferay.whip`. Otherwise,
it will be necessary to set the value of the `whip.whipJarFile` property
manually.