#set ($h1 = '#')
#set ($h2 = '##')
#set ($h3 = '###')
#set ($h4 = '####')
$h1 Getting Started with Liferay Workspace

Complete documentation for Liferay Workspace can be found
[here](https://learn.liferay.com/w/dxp/development/tooling/liferay-workspace).

$h2 Folder Structure

```
my-project
├── configs
│ ├── common
│ ├── dev
│ ├── docker
│ ├── local
│ ├── prod
│ └── uat
├── modules
│ ├── apis
│ ├── services
│ ├── java widgets
│ ├── js widgets
│ ├── java ee widgets (spring mvc, jsf, etc)
│ └── java themes
└── themes
    └── js themes
```

$h2 Running Liferay DXP locally

```
my-project $ blade gw initBundle
my-project $ blade gw deploy
my-project $ blade server run
```

$h2 Running Liferay DXP in Docker

```
my-project $ blade gw createDockerContainer
my-project $ blade gw startDockerContainer
```

$h2 Creating a Liferay DXP distribution

$h3 Creating a tar

```
my-project $ blade gw distBundleTar
```

$h3 Creating a zip

```
my-project $ blade gw distBundleZip
```

$h3 Creating multiple bundles

```
my-project $ blade gw distBundleTarAll
my-project $ blade gw distBundleZipAll
```

$h3 Creating a docker image

```
my-project $ blade gw buildDockerImage
```

$h2 Create a Liferay DXP module

$h3 Creating a new service

```
my-project $ blade create -t service-builder my-service
```

$h3 Creating a javascript theme

```
my-project $ blade create -t js-theme my-theme
```

$h3 Creating a Java Widget

```
my-project $ blade create -t mvc-portlet "my-java-widget"
```

$h3 Create a JS Widget

```
my-project $ blade create -t js-widget "my-js-widget"
```

$h2 Gradle Properties

Use these `gradle.properties` to customize default settings.

$h4 liferay.workspace.product
Set the `liferay.workspace.product` to set the `app.server.tomcat.version`,
`liferay.workspace.bundle.url`, `liferay.workspace.docker.image.liferay`, and
`liferay.workspace.target.platform.version` that matches your Liferay Product
Version. To override each of these settings, set them individually. The value
may be `null`, or one of the `releaseKey` values found in
https://releases.liferay.com/releases.json.

This file is refreshed once every 30 days. To force workspace to check for new
releases, use the system property `-Dliferay.workspace.refresh.liferay.releases`.

$h4 app.server.tomcat.version
Overrides the default setting from
`liferay.workspace.product`. Set the `app.server.tomcat.version` to match what's
inside the Liferay bundle. The `TestIntegrationPlugin` and
`LiferayExtPlugin` rely on this version to match the bundled Tomcat version. If
your configured bundle URL points to a bundle with a different Tomcat version,
set the property below to match that Tomcat version.

$h4 liferay.workspace.bundle.url
Overrides the default setting from
`liferay.workspace.product`. Set the URL pointing to the bundle Zip to
download.

$h4 liferay.workspace.docker.image.liferay
Overrides the default setting from
`liferay.workspace.product`. Set the Liferay Portal Docker image to create
your container from.

$h4 liferay.workspace.target.platform.version
Overrides the default setting from
`liferay.workspace.product`. Set the Liferay Portal or DXP version to
develop and test against. It enables target
platform features such as dependency management and OSGi resolve tasks. Use the
version that matches the Liferay Portal or DXP bundle version in this workspace.
See GETTING_STARTED#Overwrite-specific-dependency-in-one-project for overrides.

For a list of all available target platform versions, see
https://bit.ly/2IkAwwW for Liferay Portal and https://bit.ly/2GIyfZF for
Liferay DXP.

$h4 liferay.workspace.bundle.cache.dir
Set the directory where the downloaded bundle Zip files are stored. The
default value is `~/.liferay/bundles`.

$h4 liferay.workspace.default.repository.enabled
Set this to true to configure Liferay CDN as the default repository in the root
project. The default value is `true`.

$h4 liferay.workspace.environment
Set the environment with the settings appropriate for current development. The
`configs` folder is used to hold different environments in the same workspace.
You can organize environment settings and generate an environment installation
with those settings. There are five environments: common, dev, docker, local,
prod, and uat. The default value is `local`.

$h4 liferay.workspace.bundle.dist.include.metadata
Set this to true to append metadata for the current environment settings and
timestamp. The default value is `false`.

$h4 liferay.workspace.dir.excludes.globs
Set a list of glob patterns to exclude from the build life cycle. All glob
patterns start relative to the workspace root directory.

Examples:

```
liferay.workspace.dir.excludes.globs=\
	**/some-wip-project,\
	dependencies/**,\
	modules/**/*-test
```

$h4 liferay.workspace.docker.local.registry.address
Set this to the host and port of the local Docker registry. This enables the user to interact with a Docker registry other than DockerHub (e.g. myregistryaddress.org:5000).

$h4 liferay.workspace.docker.pull.policy
Set this to `false` to pull the user's local Docker cache first. The default value is `true`.

$h4 liferay.workspace.docker.username
Set this property to the registered user name on DockerHub to avoid conflicts with DockerHub.

$h4 liferay.workspace.docker.user.access.token
See https://docs.docker.com/docker-hub/access-tokens on how to generate a Docker access token.

$h4 liferay.workspace.ext.dir
Set the folder that contains all Ext OSGi modules and Ext plugins. The default
value is `ext`.

$h4 liferay.workspace.home.dir
Set the folder that contains the Liferay bundle downloaded from the
`liferay.workspace.bundle.url` property. The default value is `bundles`.

$h4 liferay.workspace.java.ee.use.jakarta
Set this to true to opt into Jakarta-compatible tooling and dependencies. Workspace
determines whether to use Jakarta dependencies based on the product version, but
setting this property forces the use of certain Jakarta dependencies regardless of the
product version.

Currently this affects

- JSP pre-compilation

The default value is `false`.

$h4 liferay.workspace.modules.default.repository.enabled
Set this to true to configure Liferay CDN as the default repository for
module/OSGi projects. The default value is `true`.

$h4 liferay.workspace.modules.dir
Set the folder that contains all module projects. Set to `*` to search all
subdirectories. The default value is `modules`.

$h4 liferay.workspace.modules.jsp.precompile.enabled
Set this to true to compile the JSP files in OSGi modules and have them added
to the distributable Zip/Tar. The default value is `false`.

$h4 liferay.workspace.node.lts.codename
Set this property to a Node.js LTS release codename to use the latest Node version and NPM version automatically for that LTS release. See https://github.com/nodejs/Release/blob/main/CODENAMES.md for the list of valid codenames.

This file is refreshed once every 30 days. To force workspace to check for new
releases, use the system property `-Dliferay.workspace.refresh.node.releases`.

$h4 liferay.workspace.node.package.manager
Set this property to `npm` to build Node.js-style projects using NPM. The
default value is `yarn`.

$h4 liferay.workspace.plugins.sdk.dir
Set the folder that contains the Plugins SDK environment. The default value is
`plugins-sdk`.

$h4 liferay.workspace.themes.dir
Set the folder that contains Node.js-style theme projects. The default value is
`themes`.

$h4 liferay.workspace.themes.java.build
Set this to `true` to build the theme projects using the Liferay Portal Tools
Theme Builder. The default value is `false`.

$h4 liferay.workspace.wars.dir
Set the folder that contains all legacy WAR projects. Set to `*` to search all
subdirectories. The default value is `modules`.

$h4 microsoft.translator.subscription.key
Set the subscription key for Microsoft Translation integration. This service
provides automatic translations for `buildLang`.

$h4 target.platform.index.sources
Set this to true if you have enabled the Target Platform plugin (i.e. you have
set the above property) and you want to apply the `TargetPlatformIDE` plugin to
the root workspace project. This causes all BOM artifact jars and
their Java sources to be indexed by your IDE. Setting this property to true can
slow down your IDE's project synchronization.

$h2 Build Customizations via `build.gradle`

$h3 Overwrite specific dependency in one project
Set `configuration.resolutionStrategy.force 'groupId:artifactId:version'` to
overwrite the version of a specific dependency. See
`https://docs.gradle.org/current/userguide/dependency_downgrade_and_exclude.html#forced_dependencies_vs_strict_dependencies`.

```
configurations {
    compileClasspath {
        resolutionStrategy.force 'groupId:artifactId:version`
    }
}
```

$h3 Overwrite dependency in multiple projects
Use the configuration below to overwrite the version of a dependency for the project.

```
subprojects {
	configurations.all {
		resolutionStrategy.force 'groupId:artifactId:version`
	}
}
```

$h2 platform.bndrun

This file resolves each module against the target version of
Liferay. Invoke the operation using the following command:
`./gradlew resolve`

SUCCESS: The successful result is a list of all the artifacts needed to run
without any resolution errors.
FAILURE: A failure will indicate missing a requirement. Correct the missing
requirement and rerun the task.