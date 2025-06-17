# Workspace Gradle Plugin

A Liferay Workspace is a generated environment that is built to hold and manage
your Liferay projects. This workspace is intended to aid in the management of
Liferay projects by providing various build scripts and configured properties.

The plugin has been successfully tested with Gradle 8.5.

## Usage

To use the plugin, include it in your `settings.gradle`:

```gradle
buildscript {
	dependencies {
		classpath group: "com.liferay", name: "com.liferay.gradle.plugins.workspace", version: "13.0.9"
	}

	repositories {
		maven {
			url "https://repository-cdn.liferay.com/nexus/content/groups/public"
		}
	}
}

apply plugin: "com.liferay.workspace"
```

The Liferay Workspace plugin automatically applies the [`com.liferay.gradle.plugins`](https://github.com/liferay/liferay-portal/tree/master/modules/sdk/gradle-plugins)
plugin.

## Tasks

The plugin adds three tasks to your project:

Name | Depends On | Type | Description
---- | ---------- | ---- | -----------
`createToken` | \- | [`CreateTokenTask`](#createtokentask) | Runs the Liferay Create Token Task.
`initBundle` | \- | [`InitBundleTask`](#initbundletask) | Runs the Liferay Init Bundle Task.

### CreateTokenTask

Tasks of type `CreateTokenTask` extend [`DefaultTask`](https://docs.gradle.org/current/dsl/org.gradle.api.DefaultTask.html)

#### Task Properties

Property Name | Type | Default Value | Description
------------- | ---- | ------------- | -----------
`emailAddress` | `String` | `null` | The email address of the user's account.
`force` | `Boolean` |`false` | Whether or not to always run the task.
`password` | `String` | `null` | The password of the user's account.
`passwordFile` | `File` | `null` | The file that contains the user's password.
`tokenFile` | `File` | `null` | The file that contains the user's generated token by task.

##### Additional Details

There is an issue with the usage of gradle daemon's and the prompting for the
user's password. In order to hide the user's password from plain text when
running `createTokenTask`, the user must run the task with `--no-daemon`. For
more information please refer to:
[`GRADLE-2357`](https://issues.gradle.org/browse/GRADLE-2357)
[`GRADLE-2386`](https://issues.gradle.org/browse/GRADLE-2386)

### InitBundleTask

Tasks of type `InitBundleTask` extend [`JavaExec`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html),
so all its properties and methods, such as [`args`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:args(java.lang.Iterable))
and [`maxHeapSize`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:maxHeapSize)
are available. They also have the following properties set by default:

Property Name | Default Value
------------- | -------------
[`args`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:args) | InitBundle command line arguments
[`systemProperties`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:systemProperties) | `[]`

#### Task Properties

Property Name | Type | Default Value | Description
------------- | ---- | ------------- | -----------
`configEnvironment` | `String` | `local` | The configuration environment of the workspace, `dev`, `prod`, `uat` are the other options.