# Service Builder Gradle Plugin

The Service Builder Gradle plugin lets you generate a service layer defined in a
[Service Builder](https://learn.liferay.com/web/guest/w/dxp/building-applications/data-frameworks/service-builder)
`service.xml` file.

The plugin has been successfully tested with Gradle 8.5.

## Usage

To use the plugin, include it in your build script:

```gradle
buildscript {
	dependencies {
		classpath group: "com.liferay", name: "com.liferay.gradle.plugins.service.builder", version: "4.0.177"
	}

	repositories {
		maven {
			url "https://repository-cdn.liferay.com/nexus/content/groups/public"
		}
	}
}

apply plugin: "com.liferay.portal.tools.service.builder"
```

The Service Builder plugin automatically applies the [`java`](https://docs.gradle.org/current/userguide/java_plugin.html)
plugin.

Since the plugin automatically resolves the [Liferay Service Builder](https://github.com/liferay/liferay-portal/tree/master/modules/util/portal-tools-service-builder)
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
`buildService` | \- | [`BuildServiceTask`](#buildservicetask) | Runs the Liferay Service Builder.

The `buildService` task is automatically configured with sensible defaults,
depending on whether the [`war`](https://docs.gradle.org/current/userguide/war_plugin.html)
plugin is applied, or whether the [`osgiModule`](#osgimodule) property is `true`:

Property Name | Default Value
------------- | -------------
[`apiDir`](#apidir) | <p>**If the `war` plugin is applied:** `${project.webAppDir}/WEB-INF/service`</p><p>**Otherwise:** `null`</p>
[`hbmFile`](#hbmfile) | <p>**If `osgiModule` is `true`:** `${buildService.resourcesDir}/META-INF/module-hbm.xml`</p><p>**Otherwise:** `${buildService.resourcesDir}/META-INF/portlet-hbm.xml`</p>
[`implDir`](#impldir) | The first `java` directory of the `main` source set (by default: `src/main/java`).
[`inputFile`](#inputfile) | <p>**If the `war` plugin is applied:** `${project.webAppDir}/WEB-INF/service.xml`</p><p>**Otherwise:** `${project.projectDir}/service.xml`</p>
[`modelHintsFile`](#modelhintsfile) | The file `META-INF/portlet-model-hints.xml` in the first `resources` directory of the `main` source set (by default: `src/main/resources/META-INF/portlet-model-hints.xml`).
[`pluginName`](#pluginname) | <p>**If `osgiModule` is `true`:** `""`</p><p>**Otherwise:** `project.name`</p>
[`propsUtil`](#pluginname) | <p>**If `osgiModule` is `true`:** `"${bundleSymbolicName}.util.ServiceProps"`<br />The `bundleSymbolicName` of the project is inferred via the [`OsgiHelper`](https://github.com/gradle/gradle/blob/master/subprojects/osgi/src/main/java/org/gradle/api/internal/plugins/osgi/OsgiHelper.java) class.</p><p>**Otherwise:** `"com.liferay.util.service.ServiceProps"`</p>
[`resourcesDir`](#resourcesdir) | The first `resources` directory of the `main` source set (by default: `src/main/resources`).
[`springFile`](#springfile) | <p>**If `osgiModule` is `true`:** the file `META-INF/spring/module-spring.xml` in the first `resources` directory of the `main` source set (by default: `src/main/resources/META-INF/spring/module-spring.xml`)</p><p>**Otherwise:** the file `META-INF/portlet-spring.xml` in the first `resources` directory of the `main` source set (by default: `src/main/resources/META-INF/portlet-spring.xml`)</p>
[`sqlDir`](#sqldir) | <p>**If the `war` plugin is applied:** `${project.webAppDir}/WEB-INF/sql`</p><p>**Otherwise:** The directory `META-INF/sql` in the first `resources` directory of the `main` source set (by default: `src/main/resources/META-INF/sql`).</p>

In the [typical scenario](https://learn.liferay.com/web/guest/w/dxp/building-applications/data-frameworks/service-builder/defining-entities/defining-entity-columns)
of a data-driven Liferay OSGi application split in `myapp-app`, `myapp-service`
and `myapp-web` modules, the `service.xml` file is usually contained in the root
directory of `myapp-service`. In the `build.gradle` of the same module, it is
enough to apply the `com.liferay.service.builder` plugin [as described](#usage),
and then add the following snippet to enable the use of Liferay Service Builder:

```gradle
buildService {
	apiDir = "../myapp-api/src/main/java"
	testDir = "../myapp-test/src/testIntegration/java"
}
```

While `apiDir` is required, the `testDir` property assignment can be left out,
in which case Arquillian-based integration test classes are generated.

### BuildServiceTask

Tasks of type `BuildWSDDTask` extend [`JavaExec`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html),
so all its properties and methods, such as [`args`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:args(java.lang.Iterable))
and [`maxHeapSize`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:maxHeapSize)
are available. They also have the following properties set by default:

Property Name | Default Value
------------- | -------------
[`args`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:args) | Service Builder command line arguments
[`classpath`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:classpath) | [`project.configurations.serviceBuilder`](#liferay-service-builder-dependency)
[`main`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:main) | `"com.liferay.portal.tools.service.builder.ServiceBuilder"`
[`systemProperties`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html#org.gradle.api.tasks.JavaExec:systemProperties) | `["file.encoding": "UTF-8"]`

#### Task Properties

Property Name | Type | Default Value | Description
------------- | ---- | ------------- | -----------
<a name="apidir"></a>`apiDir` | `File` | `null` | A directory where the service API Java source files are generated. It sets the `service.api.dir` argument.
`autoImportDefaultReferences` | `boolean` | `true` | Whether to automatically add default references, like `com.liferay.portal.ClassName`, `com.liferay.portal.Resource` and `com.liferay.portal.User`, to the services. It sets the `service.auto.import.default.references` argument.
`autoNamespaceTables` | `boolean` | `true` | Whether to prefix table names by the namespace specified in the `service.xml` file. It sets the `service.auto.namespace.tables` argument.
`beanLocatorUtil` | `String` | `"com.liferay.util.bean.PortletBeanLocatorUtil"` | The fully qualified class name of a bean locator class to use in the generated service classes. It sets the `service.bean.locator.util` argument.
`buildNumber` | `long` | `1` | A specific value to assign the `build.number` property in the `service.properties` file. It sets the `service.build.number` argument.
`buildNumberIncrement` | `boolean` | `true` | Whether to automatically increment the `build.number` property in the `service.properties` file by one at every service generation. It sets the `service.build.number.increment` argument.
`databaseNameMaxLength` | `int` | `30` | The upper bound for database table and column name lengths to ensure it works on all databases. It sets the `service.database.name.max.length` argument.
<a name="hbmfile"></a>`hbmFile` | `File` | `null` | A Hibernate Mapping file to generate. It sets the `service.hbm.file` argument.
<a name="impldir"></a>`implDir` | `File` | `null` | A directory where the service Java source files are generated. It sets the `service.impl.dir` argument.
<a name="inputfile"></a>`inputFile` | `File` | `null` | The project's `service.xml` file. It sets the `service.input.file` argument.
`modelHintsConfigs` | `Set` | `["classpath*:META-INF/portal-model-hints.xml", "META-INF/portal-model-hints.xml", "classpath*:META-INF/ext-model-hints.xml", "classpath*:META-INF/portlet-model-hints.xml"]` | Paths to the [model hints](https://learn.liferay.com/web/guest/w/dxp/building-applications/data-frameworks/service-builder/defining-entities/adding-model-hints) files for Liferay Service Builder to use in generating the service layer. It sets the `service.model.hints.configs` argument.
<a name="modelhintsfile"></a>`modelHintsFile` | `File` | `null` | A model hints file for the project. It sets the `service.model.hints.file` argument.
<a name="osgimodule"></a>`osgiModule` | `boolean` | `false` | Whether to generate the service layer for OSGi modules. It sets the `service.osgi.module` argument.
<a name="pluginname"></a>`pluginName` | `String` | `null` | If specified, a plugin can enable additional generation features, such as `Clp` class generation, for non-OSGi modules. It sets the `service.plugin.name` argument.
<a name="propsutil"></a>`propsUtil` | `String` | `null` | The fully qualified class name of the service properties util class to generate. It sets the `service.props.util` argument.
`readOnlyPrefixes` | `Set` | `["fetch", "get", "has", "is", "load", "reindex", "search"]` | Prefixes of methods to consider read-only. It sets the `service.read.only.prefixes` argument.
`resourceActionsConfigs` | `Set` | `["META-INF/resource-actions/default.xml", "resource-actions/default.xml"]` | Paths to the [resource actions](https://learn.liferay.com/web/guest/w/dxp/building-applications/objects/understanding-object-integrations/permissions-framework-integration#resource-permissions) files for Liferay Service Builder to use in generating the service layer. It sets the `service.resource.actions.configs` argument.
<a name="resourcesdir"></a>`resourcesDir` | `File` | `null` | A directory where the service non-Java files are generated. It sets the `service.resources.dir` argument.
<a name="springfile"></a>`springFile` | `File` | `null` | A service Spring file to generate. It sets the `service.spring.file` argument.
`springNamespaces` | `Set` | `["beans"]` | Namespaces of Spring XML Schemas to add to the service Spring file. It sets the `service.spring.namespaces` argument.
<a name="sqldir"></a>`sqlDir` | `File` | `null` | A directory where the SQL files are generated. It sets the `service.sql.dir` argument.
`sqlFileName` | `String` | `"tables.sql"` | A name (relative to `sqlDir`) for the file in which the SQL table creation instructions are generated. It sets the `service.sql.file` argument.
`sqlIndexesFileName` | `String` | `"indexes.sql"` | A name (relative to `sqlDir`) for the file in which the SQL index creation instructions are generated. It sets the `service.sql.indexes.file` argument.
`sqlSequencesFileName` | `String` | `"sequences.sql"` | A name (relative to `sqlDir`) for the file in which the SQL sequence creation instructions are generated. It sets the `service.sql.sequences.file` argument.
`targetEntityName` | `String` | `null` | If specified, it's the name of the entity for which Liferay Service Builder should generate the service. It sets the `service.target.entity.name` argument.
`testDir` | `File` | `null` | If specified, it's a directory where integration test Java source files are generated. It sets the `service.test.dir` argument.
`uadDir` | `File` | `null` | A directory where the UAD (user-associated data) Java source files are generated. It sets the `service.uad.dir` argument.
`uadTestIntegrationDir` | `File` | `null` | A directory where integration test UAD (user-associated data) Java source files are generated. It sets the `service.uad.test.integration.dir` argument.

The properties of type `File` supports any type that can be resolved by [`project.file`](https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:file(java.lang.Object)).
Moreover, it is possible to use Closures and Callables as values for the
`String` properties, to defer evaluation until task execution.

## Additional Configuration

There are additional configurations that can help you use Service Builder.

### Liferay Service Builder Dependency

By default, the plugin creates a configuration called `serviceBuilder` and adds
a dependency to the latest released version of Liferay Service Builder. It is
possible to override this setting and use a specific version of the tool by
manually adding a dependency to the `serviceBuilder` configuration:

```gradle
dependencies {
	serviceBuilder group: "com.liferay", name: "com.liferay.portal.tools.service.builder", version: "1.0.504"
}
```

If you're applying the
[`com.liferay.gradle.plugins`](https://github.com/liferay/liferay-portal/tree/master/modules/sdk/gradle-plugins)
or
[`com.liferay.gradle.plugins.workspace`](https://github.com/liferay/liferay-portal/blob/master/modules/sdk/gradle-plugins-workspace)
plugins to your project, the Service Builder dependency is already added to the
`serviceBuilder` configuration. Therefore, if you try to apply a customized
version of Service Builder, it's not recognized; you must override the
configuration already applied.

To do this, you must customize the classpath of the `buildService` task. If
you're supplying the customized Service Builder plugin through a module named
`custom-sb-api`, you could modify the `buildService` task like this:

```gradle
buildService {
	apiDir = "../custom-sb-api/src/main/java"
	classpath = configurations.serviceBuilder.filter { file -> !file.name.contains("com.liferay.portal.tools.service.builder") }
}
```

If you do this in conjunction with the `serviceBuilder` dependency
configuration, the custom Service Builder version is used.