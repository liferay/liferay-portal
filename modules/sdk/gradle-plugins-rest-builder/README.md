# REST Builder Gradle Plugin

The REST Builder Gradle plugin lets you generate a REST layer defined in the
REST Builder `rest-config.yaml` and `rest-openapi.yaml` files.

The plugin has been successfully tested with Gradle 8.5.

## Usage

To use the plugin, include it in your build script:

```gradle
buildscript {
	dependencies {
		classpath group: "com.liferay", name: "com.liferay.gradle.plugins.rest.builder", version: "1.1.296"
	}

	repositories {
		maven {
			url "https://repository-cdn.liferay.com/nexus/content/groups/public"
		}
	}
}

apply plugin: "com.liferay.portal.tools.rest.builder"
```

The REST Builder plugin automatically applies the [`java`](https://docs.gradle.org/current/userguide/java_plugin.html)
plugin.

Since the plugin automatically resolves the [Liferay REST Builder](https://github.com/liferay/liferay-portal/tree/master/modules/util/portal-tools-rest-builder)
library as a dependency, you have to configure a repository that hosts the
library and its transitive dependencies. The Liferay CDN repository hosts them
all:

```gradle
repositories {
	maven {
		url "https://repository-cdn.liferay.com/nexus/content/groups/public"
	}
}
```

## Tasks

The plugin adds one task to your project:

Name | Depends On | Type | Description
---- | ---------- | ---- | -----------
`buildREST` | \- | [`BuildRESTTask`](#buildresttask) | Runs the Liferay REST Builder.

### BuildRESTTask

Tasks of type `BuildRESTTask` extend [`JavaExec`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html),
so all its properties and methods, such as [`args`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:args(java.lang.Iterable))
and [`maxHeapSize`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:maxHeapSize)
are available. They also have the following properties set by default:

Property Name | Default Value
------------- | -------------
[`args`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:args) | REST Builder command line arguments
[`classpath`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:classpath) | [`project.configurations.restBuilder`](#liferay-rest-builder-dependency)
[`main`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:main) | `"com.liferay.portal.tools.rest.builder.RESTBuilder"`
[`systemProperties`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:systemProperties) | `[]`

#### Task Properties

Property Name | Type | Default Value | Description
------------- | ---- | ------------- | -----------
`copyrightFile` | `File` | `null` | The file that contains the copyright header.
`restConfigDir` | `File` |`${project.projectDir}` | The directory that contains the `rest-config.yaml` and `rest-openapi.yaml` files.

In the typical scenario, the `rest-config.yaml` and `rest-openapi.yaml` files
are contained in the project directory of `my-rest-app-impl`. In the
`build.gradle` of the same module, apply the `com.liferay.rest.builder` plugin.

The properties of type `File` supports any type that can be resolved by
[`project.file`](https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:file(java.lang.Object)).
Moreover, it is possible to use Closures and Callables as values for the
`String` properties, to defer evaluation until task execution.

## Additional Configuration

There are additional configurations added to use REST Builder.

### Liferay REST Builder Dependency

By default, the plugin creates a configuration called `restBuilder` and adds
a dependency to the latest released version of Liferay REST Builder.

```gradle
dependencies {
	restBuilder group: "com.liferay", name: "com.liferay.portal.tools.rest.builder", version: "1.0.418"
}
```