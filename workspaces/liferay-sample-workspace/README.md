# Liferay Sample Workspace

The Liferay Sample workspace contains an example of every client extension and is meant to be the ***primary*** source of truth of how client extensions work by documenting via code.

Feel free to initiate your project by copying the samples found here. But make sure to rename your client extensions according to our established [naming conventions](#naming-conventions).

## Client Extensions

Client extensions are the recommended way of customizing Liferay. Modules and themes are supported for backwards compatibility.

To deploy all client extensions, go to `liferay-sample-workspace` and type `./gradlew deploy`.

To deploy a specific client extension (e.g. liferay-sample-custom-element-1), go to `liferay-sample-workspace` and type `./gradlew :client-extensions:liferay-sample-custom-element-1:deploy`.

### Naming Conventions

The standard directory name of a client extension is broken up into several parts. The first two parts, the owner and project, are separated by `-`.

For `liferay-sample-batch`, the owner is `liferay` and the project is `sample`. The owner and the project must not contain `-` since we use `-` to differentiate the owner from the project.

The third part is usually one of the available client extension types: batch, custom-element, fds-cell-renderer, global-css, global-js, iframe, notification-type, oahs, oaua, object-action, site-initializer, static-content, theme-css, theme-favicon, theme-spritemap, or workflow-action.

For `liferay-sample-batch`, the third part is the client extension type `batch`.

For `liferay-sample-custom-element-1` and `liferay-sample-custom-element-2`, the third part, `custom-element`, means this is a custom element. The fourth parts, `1` and `2`, are a general description that can be anything.

If the third part it is not a client extension type, then the third part must be the special keyword `etc`.

For `liferay-sample-etc-cron` and `liferay-sample-etc-spring-boot` the third type is `etc`. The fourth parts, `cron` and `spring-boot` are general descriptions that can be anything.

### List of Client Extensions

- *liferay-sample-batch*

	...

- *liferay-sample-commerce-payment-integration*

	TODO

- *liferay-sample-commerce-shipping-engine*

	Use Spring Boot to provide a new commerce shipping engine.

- *liferay-sample-commerce-tax-engine*

	Use Spring Boot to provide a new commerce tax engine.

- *liferay-sample-custom-element-1*

	A custom element can be self contained (i.e. does not depend on any external packages).

- *liferay-sample-custom-element-2*

	Build a custom element with `react-scripts`.

- *liferay-sample-custom-element-3*

	Build a custom element with `@angular/cli`.

- *liferay-sample-custom-element-4*

	Build a custom element that uses `react` and `react-dom` packages that Liferay makes publicly available through import maps.

- *liferay-sample-custom-element-5*

	Build a custom element that uses `@clayui/badge`, `react`, and `react-dom` packages that Liferay makes publicly available through import maps.

- *liferay-sample-custom-element-6*

	Build a custom element that uses `react`, `react-dom`, and `vite`.

- *liferay-sample-editor-config-contributor*

	Build a JavaScript function to configure WYSIWYG editors.

- *liferay-sample-etc-cron*

	Use Spring Boot and OAuth (server to server) to read from and write to Liferay in timed intervals.

	To see this in action on your local machine:

	1. Go to `liferay-sample-workspace` and type `./gradlew startDockerContainer logsDockerContainer` to start Liferay.

	1. Go to `liferay-sample-workspace` and type `./gradlew :client-extensions:liferay-sample-etc-cron:deployDev`.

	1. Login to Liferay and go to Control Panel > Configuration > OAuth 2 Administration. Select `Liferay Sample Etc Cron`.

	1. Copy the client secret. Open `liferay-sample-workspace/client-extensions/liferay-sample-etc-cron/src/main/resources/application-default.properties` and replace `liferay-sample-etc-cron.oauth2.headless.server.client.secret=myfancypassword` with the real client secret.

	1. Go to `liferay-sample-workspace/client-extensions/liferay-sample-etc-cron` and type `./gradlew bootRun` to start Spring Boot.

- *liferay-sample-etc-frontend*

	Share code via JavaScript import maps to a custom element.

- *liferay-sample-etc-node*

	TODO

- *liferay-sample-etc-spring-boot*

	Use Spring Boot and OAuth (human to server) to interact with Liferay.

- *liferay-sample-fds-cell-renderer*

	Build a custom cell display in a frontend data set configured for table mode.

- *liferay-sample-fds-filter*

	Build a custom filter in a frontend data set.

- *liferay-sample-global-css-1*

	Add a global CSS to a company.

- *liferay-sample-global-css-2*

	Add a global CSS to a page.

- *liferay-sample-global-js-1*

	Add a global script element to a page.

- *liferay-sample-global-js-2*

	Add a global script element with attributes to a page.

- *liferay-sample-global-js-3*

	Add a global script element to a company.

- *liferay-sample-iframe-1*

	Add an IFrame widget with an interactive counter app.

- *liferay-sample-iframe-2*

	Add three IFrame widgets that render different Wikipedia topics.

- *liferay-sample-instance-settings*

	Customize instance settings with YAML by overridding OSGi configurations.

- *liferay-sample-js-import-maps-entry*

	Share jQuery via JavaScript import maps.

- *liferay-sample-site-initializer*

	TODO

- *liferay-sample-static-content*

	Deploy static content that is only accessible if you know the URL.

- *liferay-sample-theme-css-1*

	Extend the CSS of the `styled` theme.

- *liferay-sample-theme-css-2*

	Extend the CSS of the `styled` theme and apply it to admin pages.

- *liferay-sample-theme-css-3*

	Extend the CSS of the `unstyled` theme.

- *liferay-sample-theme-css-4*

	Extend the CSS of the `unstyled` theme and provide frontend token definitions.

- *liferay-sample-theme-favicon*

	Replace a theme's favicon.

- *liferay-sample-theme-spritemap-1*

	Replace a theme's spritemap with a single SVG file.

- *liferay-sample-theme-spritemap-2*

	Replace a theme's spritemap with a single SVG file that is built from multiple smaller SVG files.