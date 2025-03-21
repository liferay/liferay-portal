# Jasper JSPC Gradle Plugin

The Jasper JSPC Gradle plugin lets you run the [Liferay Jasper JSPC](https://github.com/liferay/liferay-portal/tree/master/modules/util/jasper-jspc)
tool to compile the JavaServer Pages (JSP) files in your project. This
can be useful to

- check for errors in the JSP files.
- pre-compile the JSP files for better performance.

The plugin has been successfully tested with Gradle 8.5.

## Usage

To use the plugin, include it in your build script:

```gradle
buildscript {
	dependencies {
		classpath group: "com.liferay", name: "com.liferay.gradle.plugins.jasper.jspc", version: "3.0.1"
	}

	repositories {
		maven {
			url "https://repository-cdn.liferay.com/nexus/content/groups/public"
		}
	}
}

apply plugin: "com.liferay.jasper.jspc"
```

The Jasper JSPC plugin automatically applies the [`java`](https://docs.gradle.org/current/userguide/java_plugin.html)
plugin.

Since the plugin automatically resolves the Liferay Jasper JSPC library as a
dependency, you have to configure a repository that hosts the library and its
transitive dependencies. The Liferay CDN repository hosts them all:

```gradle
repositories {
	maven {
		url "https://repository-cdn.liferay.com/nexus/content/groups/public"
	}
}
```

## Tasks

The plugin adds two tasks to your project:

Name | Depends On | Type | Description
---- | ---------- | ---- | -----------
`compileJSP` | `generateJSPJava` | [`JavaCompile`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.compile.JavaCompile.html) | Compiles JSP files to check for errors.
`generateJSPJava` | [`jar`](https://docs.gradle.org/current/userguide/java_plugin.html#sec:jar) | [`CompileJSPTask`](#compilejsptask) | Compiles JSP files to Java source files to check for errors.

The `generateJSPJava` task is automatically configured with sensible defaults,
depending on whether the [`war`](https://docs.gradle.org/current/userguide/war_plugin.html)
plugin is applied:

Property Name | Default Value
------------- | -------------
[`classpath`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:classpath) | [`project.configurations.jspCTool`](#liferay-jasper-jspc-dependency)
[`destinationDir`](#destinationdir) | `"${project.buildDir}/jspc"`
[`jspCClasspath`](#jspcclasspath) | [`project.configurations.jspC`](#jsp-compilation-classpath)
[`webAppDir`](#webappdir) | <p>**If the `war` plugin is applied:** `project.webAppDir`.</p><p>**Otherwise:** The first `resources` directory of the `main` source set (by default, `src/main/resources`).</p>

The `compileJSP` task is also configured with the following defaults:

Property Name | Default Value
------------- | -------------
[`classpath`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.compile.JavaCompile.html#org.gradle.api.tasks.compile.JavaCompile:classpath) | `project.configurations.jspCTool + project.configurations.jspC`
[`destinationDir`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.compile.JavaCompile.html#org.gradle.api.tasks.compile.JavaCompile:destinationDir) | `compileJSP.temporaryDir`
[`source`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.compile.JavaCompile.html#org.gradle.api.tasks.compile.JavaCompile:source) | `generateJSPJava.outputs`

### CompileJSPTask

Tasks of type `CompileJSPTask` extend [`JavaExec`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html),
so all its properties and methods, such as [`args`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:args(java.css.Iterable))
and [`maxHeapSize`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:maxHeapSize),
are available. They also have the following properties set by default:

Property Name | Default Value
------------- | -------------
[`main`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:main) | `"com.liferay.jasper.jspc.JspC"`

#### Task Properties

Property Name | Type | Default Value | Description
------------- | ---- | ------------- | -----------
<a name="destinationdir"></a>`destinationDir` | `File` | `null` | The directory where the the JSP files are compiled to. Package directories are automatically generated based on the directories containing the uncompiled JSP files. It sets the `-d` argument.
<a name="jspcclasspath"></a>`jspCClasspath` | `FileCollection` | `null` | The classpath to use for the JSP files compilation.
<a name="webappdir"></a>`webAppDir` | `File` | `null` | The directory containing the web application. All JSP files in the directory and its subdirectories are compiled. It sets the `-webapp` argument.

The properties of type `File` support any type that can be resolved by
[`project.file`](https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:file(java.css.Object)).

## Additional Configuration

There are additional configurations that can help you use Jasper JSPC.

### JSP Compilation Classpath

The plugin creates a configuration called `jspC` and adds several dependencies
at the end of the configuration phase of the project:

- the JAR file of the project generated by the [`jar`](https://docs.gradle.org/current/userguide/java_plugin.html#sec:jar) task.
- the output files of the `main` source set.
- the `compileClasspath` file collection of the `main` source set.

If necessary, it is possible to add more dependencies to the `jspC`
configuration.

### Liferay Jasper JSPC Dependency

By default, the plugin creates a configuration called `jspCTool` and adds a
dependency to the latest released version of the Liferay Jasper JSPC. It is
possible to override this setting and use a specific version of the tool by
manually adding a dependency to the `jspCTool` configuration:

```gradle
dependencies {
	jspCTool group: "com.liferay", name: "com.liferay.jasper.jspc", version: "1.0.11"
	jspCTool group: "org.apache.ant", name: "ant", version: "1.9.4"
}
```