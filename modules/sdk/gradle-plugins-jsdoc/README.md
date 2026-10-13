# JSDoc Gradle Plugin

The JSDoc Gradle plugin lets you run the [JSDoc](http://usejsdoc.org/) tool in
order to generate documentation for your project's JavaScript files.

The plugin has been successfully tested with Gradle 8.5.

## Usage

To use the plugin, include it in your build script:

```gradle
buildscript {
	dependencies {
		classpath group: "com.liferay", name: "com.liferay.gradle.plugins.jsdoc", version: "2.0.82"
	}

	repositories {
		maven {
			url "https://repository-cdn.liferay.com/nexus/content/groups/public"
		}
	}
}
```

There are two JSDoc Gradle plugins you can apply to your project:

- Apply the [JSDoc Plugin](#jsdoc-plugin) to generate JavaScript documentation
for your project:

	```gradle
	apply plugin: "com.liferay.jsdoc"
	```

- Apply the [App JSDoc Plugin](#appjsdoc-plugin) in a parent project to
generate the JavaScript documentation as a single, combined HTML document for an
application that spans different subprojects, each one representing a different
component of the same application:

	```gradle
	apply plugin: "com.liferay.app.jsdoc"
	```

Both plugins automatically apply the [`com.liferay.node`](https://github.com/liferay/liferay-portal/tree/master/modules/sdk/gradle-plugins-node)
plugin.

## JSDoc Plugin

The plugin adds two tasks to your project:

Name | Depends On | Type | Description
---- | ---------- | ---- | -----------
`downloadJSDoc` | `downloadNode` | `DownloadNodeModuleTask` | Downloads JSDoc in the project's `node_modules` directory.
`jsdoc` | `downloadJSDoc` | [`JSDocTask`](#jsdoctask) | Generates API documentation for the project's JavaScript code.

By default, the `downloadJSDoc` task downloads version `4.0.5` of the `jsdoc`
package. If the project's `package.json` file, however, already lists the
`jsdoc` package in its `dependencies` or `devDependencies`, the `downloadJSDoc`
task is disabled.

The `jsdoc` task is automatically configured with sensible defaults,
depending on whether the [`java`](https://docs.gradle.org/current/userguide/java_plugin.html)
plugin is applied:

Property Name | Default Value
------------- | -------------
[`destinationDir`](#destinationdir) | <p>**If the `java` plugin is applied:** `"${project.docsDir}/jsdoc"`</p><p>**Otherwise:** `"${project.buildDir}/jsdoc"`</p>
[`sourceDirs`](#sourcedirs) | The directory `META-INF/resources` in the first `resources` directory of the `main` source set (by default, `src/main/resources/META-INF/resources`).

## AppJSDoc Plugin

To use the App JSDoc plugin, it is required to apply the `com.liferay.app.jsdoc`
plugin in a parent project (that is, a project that is a
common ancestor of all the subprojects representing the various components of
the app). It is also required to apply the [`com.liferay.jsdoc`](#jsdoc-plugin)
plugin to all the subprojects that contain JavaScript files.

The App JSDoc plugin adds three tasks to your project:

Name | Depends On | Type | Description
---- | ---------- | ---- | -----------
`appJSDoc` | `downloadJSDoc` | [`JSDocTask`](#jsdoctask) | Generates API documentation for the app's JavaScript code.
`downloadJSDoc` | `downloadNode` | `DownloadNodeModuleTask` | Downloads JSDoc in the app's `node_modules` directory.
`jarAppJSDoc` | `appJSDoc` | [`Jar`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.bundling.Jar.html) | Assembles a JAR archive containing the JavaScript documentation files for this app.

By default, the `downloadJSDoc` task downloads version `4.0.5` of the `jsdoc`
package. If the project's `package.json` file, however, already lists the
`jsdoc` package in its `dependencies` or `devDependencies`, the `downloadJSDoc`
task is disabled.

The `appJSDoc` task is automatically configured with sensible defaults:

Property Name | Default Value
------------- | -------------
[`destinationDir`](#destinationdir) | `${project.buildDir}/docs/jsdoc`
[`sourceDirs`](#sourcedirs) | The sum of all the `jsdoc.sourceDirs` values of the subprojects.

## Project Extension

The App JSDoc plugin exposes the following properties through the extension
named `appJSDocConfiguration`:

Property Name | Type | Default Value | Description
------------- | ---- | ------------- | -----------
`subprojects` | `Set<Project>` | `project.subprojects` | The subprojects to include in the JavaScript documentation of the app.

The same extension exposes the following methods:

Method | Description
------ | -----------
`AppJSDocConfigurationExtension subprojects(Iterable<Project> subprojects)` | Include additional projects in the JavaScript documentation of the app.
`AppJSDocConfigurationExtension subprojects(Project... subprojects)` | Include additional projects in the JavaScript documentation of the app.

## Tasks

### JSDocTask

Tasks of type `JSDocTask` extend `ExecuteNodeScriptTask`, so all its
properties and methods, such as `args`, `inheritProxy`, and `workingDir`, are
available.

They also have the following properties set by default:

Property Name | Default Value
------------- | -------------
`scriptFile` | `"${downloadJSDoc.moduleDir}/jsdoc.js"`

#### Task Properties

Property Name | Type | Default Value | Description
------------- | ---- | ------------- | -----------
`configuration` | [`TextResource`](https://docs.gradle.org/current/dsl/org.gradle.api.resources.TextResource.html) | `null` | The JSDoc configuration file. It sets the `--configure` argument.
<a name="destinationdir"></a>`destinationDir` | `File` | `null` | The directory where the JavaScript API documentation files are saved. It sets the `--destination` argument.
`packageJsonFile` | `File` | `"${project.projectDir}/package.json"` | The path to the project's package file. It sets the `--package` argument.
<a name="sourcedirs"></a>`sourceDirs` | `FileCollection` | `[]` | The directories that contains the files to process.
`readmeFile` | `File` | `null` | The path to the project's README file. It sets the `--readme` argument.
`tutorialsDir` | `File` | `null` | The directory in which JSDoc should search for tutorials. It sets the `--tutorials` argument.

The properties of type `File` support any type that can be resolved by [`project.file`](https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:file(java.css.Object)).