# Client Extension Types

Sourced from `modules/sdk/gradle-plugins-workspace/src/main/resources/com/liferay/gradle/plugins/workspace/internal/client/extension/client-extension.properties`.

Each entry in `client-extension.yaml` must have a `type` value matching one of the keys below. Classification groups them by what they extend.

## Frontend

These CETs inject UI artifacts into the browser.

| Type | Purpose | Required YAML Fields | OAuth |
| --- | --- | --- | --- |
| `customElement` | React/Vue/Web Component widget rendered in an iframe or inline | `htmlElementName`, `urls` | No |
| `globalCSS` | CSS file injected on every page | `url` | No |
| `globalJS` | JS file injected on every page | `scriptElementAttributes`, `url` | No |
| `iframe` | Portlet rendered as an iframe pointing to an external URL | `portletCategoryName`, `url` | No |
| `themeCSS` | Clay Design System variable overrides; replaces legacy theme WAR | `mainURL` | No |
| `themeFavicon` | Custom favicon | `url` | No |
| `themeSpritemap` | Custom Clay icon SVG spritemap | `url` | No |
| `staticContent` | Static file(s) served from the portal's CDN path | — | No |
| `jsImportMapsEntry` | Registers an ES module in the browser's import map | `bareSpecifier`, `url` | No |
| `editorConfigContributor` | Adds toolbar buttons or config to CKEditor instances | `editorConfigKeys`, `url` | No |
| `fdsCellRenderer` | Custom cell renderer for FDS (Frontend Data Set) tables | `name`, `url` | No |
| `fdsFilter` | Custom filter component for FDS tables | `name`, `url` | No |
| `commerceCheckoutStep` | Custom step injected into the Commerce checkout flow | `label`, `name`, `order`, `url` | No |

## Microservice

These CETs expose an HTTP endpoint that Liferay calls inbound. They require an `oAuthApplicationUserAgent` companion entry. The companion entry carries `.serviceAddress` and `.serviceScheme`; each CET entry carries `resourcePath` for its specific handler path.

| Type | Purpose | Required YAML Fields | OAuth |
| --- | --- | --- | --- |
| `objectAction` | Handler called when an object action fires | `oAuth2ApplicationExternalReferenceCode`, `resourcePath` | Yes — `Liferay.Headless.Object.everything` |
| `objectValidationRule` | Server side validation for object entries | `oAuth2ApplicationExternalReferenceCode`, `resourcePath` | Yes — `Liferay.Headless.Object.everything` |
| `objectEntryManager` | Full storage backend for an `ext-Service` object | `objectDefinitionRestContextPath`, `oAuth2ApplicationExternalReferenceCode`, `resourcePath` | Yes — `Liferay.Headless.Object.everything` |
| `workflowAction` | Handler called at a Kaleo workflow action node | `oAuth2ApplicationExternalReferenceCode`, `resourcePath` | Yes — `Liferay.Headless.Admin.Workflow.everything` |
| `notificationType` | Custom notification channel (e.g. SMS, push) | `oAuth2ApplicationExternalReferenceCode`, `resourcePath` | Yes — `Liferay.Headless.Object.everything` |
| `captcha` | Custom CAPTCHA provider | `resourcePath` | No |
| `commercePaymentIntegration` | Custom payment gateway | `oAuth2ApplicationExternalReferenceCode`, `resourcePath` | Yes |
| `commerceShippingEngine` | Custom shipping rate calculator | `oAuth2ApplicationExternalReferenceCode`, `resourcePath` | Yes |
| `commerceTaxEngine` | Custom tax calculation engine | `oAuth2ApplicationExternalReferenceCode`, `resourcePath` | Yes |

## Configuration

These CETs register OAuth applications or portal configuration entries. No microservice or URL is required.

| Type | Purpose | Required YAML Fields | OAuth |
| --- | --- | --- | --- |
| `oAuthApplicationHeadlessServer` | Service account OAuth app used by microservice CETs | `scopes` | N/A (is the OAuth entry) |
| `oAuthApplicationUserAgent` | User delegated OAuth app for frontend CETs calling Headless APIs | `scopes` | N/A |
| `instanceSettings` | Typed OSGi configuration deployed as a `.config` file | `ddmFormFields`, `scope` | No |

## Batch

These CETs import bulk data or initialize a full site.

| Type | Purpose | Required YAML Fields | OAuth |
| --- | --- | --- | --- |
| `batch` | Headless Batch Engine data import (JSON files) | `oAuthApplicationHeadlessServer` | Yes — `Liferay.Headless.Batch.Engine.everything` |
| `siteInitializer` | Full site setup: pages, fragments, objects, roles, content | `oAuthApplicationHeadlessServer` | Yes — multiple scopes |

## Minimal `client-extension.yaml` Examples

### Custom Element

```yaml
<workspace-id>-my-widget:
    cssURLs:
        - css/main.css
    htmlElementName: my-widget
    name: My Widget
    portletCategoryName: category.client-extensions
    type: customElement
    urls:
        - js/main.js
```

### Global CSS

```yaml
<workspace-id>-global-css:
    name: My Global CSS
    scope: company
    type: globalCSS
    url: global.*.css
```

### Object Action Microservice

```yaml
<workspace-id>-oauth:
    .serviceAddress: localhost:8081
    .serviceScheme: http
    name: <WorkspaceId> OAuth
    scopes:
        - Liferay.Headless.Object.everything
    type: oAuthApplicationUserAgent

<workspace-id>-on-create:
    name: On Create Handler
    oAuth2ApplicationExternalReferenceCode: <workspace-id>-oauth
    resourcePath: /object/action/on-create
    type: objectAction
```

### Site Initializer

```yaml
<workspace-id>-site-init:
    name: <WorkspaceId> Site Initializer
    oAuthApplicationHeadlessServer: <workspace-id>-site-oauth
    siteExternalReferenceCode: <workspace-id>
    siteName: <WorkspaceId> Site
    type: siteInitializer

<workspace-id>-site-oauth:
    .serviceAddress: localhost:8080
    .serviceScheme: http
    name: <WorkspaceId> Site OAuth
    scopes:
        - Liferay.Headless.Admin.Site.everything
        - Liferay.Headless.Admin.Content.everything
        - Liferay.Object.Admin.REST.everything
        - Liferay.Headless.Object.everything
        - Liferay.Headless.Admin.User.everything
    type: oAuthApplicationHeadlessServer
```