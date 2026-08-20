---

description: Generate the directory structure and client-extension.yaml for any Liferay client extension type. Use when the user asks to create a client extension, scaffold a custom element, add an object action microservice, create a site initializer, or build any other CET. After scaffolding, calls deploy-and-verify to confirm startup.
name: scaffold-client-extension

---

# Scaffold Client Extension

Generate a ready to deploy client extension under `client-extensions/<name>`. The CET type drives the file layout and the companion OAuth entry requirement.

## When to Invoke

- "Create a client extension", "scaffold a custom element", "build a React widget"
- "Add an object action microservice"
- "Create a site initializer"
- "I need a global CSS override" or "inject a JS library on every page"

## Workflow

### Identify the CET Type

If the user did not specify a type, ask. Refer to `rules/client-extension-types.md` for the full list of types grouped by classification:

- **Frontend**: `customElement`, `globalCSS`, `globalJS`, `iframe`, `themeCSS`, `themeFavicon`, `themeSpritemap`, `staticContent`, `jsImportMapsEntry`, `editorConfigContributor`, `fdsCellRenderer`, `fdsFilter`, `commerceCheckoutStep`
- **Microservice**: `objectAction`, `objectValidationRule`, `objectEntryManager`, `workflowAction`, `notificationType`, `captcha`, `commercePaymentIntegration`, `commerceShippingEngine`, `commerceTaxEngine`
- **Configuration**: `oAuthApplicationHeadlessServer`, `oAuthApplicationUserAgent`, `instanceSettings`
- **Batch**: `batch`, `siteInitializer`

### Collect Inputs

| Input | How to Obtain |
| --- | --- |
| Extension name | From user; convert to `kebab-case` for the directory |
| Workspace ID | Read from the `id` field at the root of `client-extension.yaml` in the workspace root |
| Microservice port | For microservice types; default `8081`, or next unused port |
| OAuth scopes | Look up in `rules/oauth-scopes.md` for the CET type |

### Create the Directory

```
client-extensions/<extension-name>/
  client-extension.yaml
```

Additional source files depend on type (see templates below).

### Type Specific Templates

#### `customElement` (React example)

```yaml
<workspace-id>-<name>:
  cssURLs:
    - "css/main.css"
  friendlyURLMapping: <name>
  htmlElementName: <name>-element
  name: <Display Name>
  portletCategoryName: category.client-extensions
  type: customElement
  urls:
    - "js/main.js"
  useESM: true
```

Source layout:

```
client-extensions/<name>/
  client-extension.yaml
  package.json
  src/
    index.jsx       # React entry, exports a web component
  build.gradle      # optional; Blade transpiles via Liferay's frontend Gradle tooling
```

Minimal `src/index.jsx`:

```jsx
import React from 'react';
import {createRoot} from 'react-dom/client';

class MyElement extends HTMLElement {
  connectedCallback() {
    const root = createRoot(this);
    root.render(<App />);
  }
}
customElements.define('<name>-element', MyElement);

function App() {
  return <div className="<name>">Hello from <Display Name></div>;
}
```

#### `globalCSS`

```yaml
<workspace-id>-global-css:
  name: <Display Name> Global CSS
  type: globalCSS
  url: "css/main.css"
```

```
client-extensions/<name>/
  client-extension.yaml
  css/
    main.css
```

#### `globalJS`

```yaml
<workspace-id>-global-js:
  name: <Display Name> Global JS
  scriptElementAttributes:
    async: true
  type: globalJS
  url: "js/main.js"
```

#### `objectAction` (Spring Boot example)

```yaml
<workspace-id>-oauth:
    .serviceAddress: localhost:8081
    .serviceScheme: http
    name: <WorkspaceId> OAuth
    scopes:
        - Liferay.Headless.Object.everything
    type: oAuthApplicationUserAgent

<workspace-id>-<name>:
    name: <Display Name> Action
    oAuth2ApplicationExternalReferenceCode: <workspace-id>-oauth
    resourcePath: /object/action/<name>
    type: objectAction
```

```
client-extensions/<name>/
  client-extension.yaml
  src/main/java/<package>/
    Application.java
    ObjectActionRestController.java
  src/main/resources/
    application.properties
```

`ObjectActionRestController.java` minimal shape:

```java
@RestController
public class ObjectActionRestController {

    @PostMapping("/")
    public ResponseEntity<String> post(@RequestBody Map<String, Object> body) {

        // Body contains the object entry fields

        return ResponseEntity.ok("{}");
    }

}
```

`application.properties`:

```properties
server.port=8081
liferay.oauth.application.external.reference.code=<workspace-id>-oauth
```

#### `siteInitializer`

```yaml
<workspace-id>-site-init:
  name: <Site Name> Initializer
  oAuthApplicationHeadlessServer: <workspace-id>-site-oauth
  siteExternalReferenceCode: <site-erc>
  siteName: <Site Name>
  type: siteInitializer

<workspace-id>-site-oauth:
  name: <Site Name> OAuth
  scopes:
    - Liferay.Headless.Admin.Site.everything
    - Liferay.Headless.Admin.Content.everything
    - Liferay.Object.Admin.REST.everything
    - Liferay.Headless.Object.everything
    - Liferay.Headless.Admin.User.everything
  type: oAuthApplicationHeadlessServer
```

Populate `site-initializer` per `rules/site-initializer-format.md`.

`siteExternalReferenceCode` uniquely identifies the site so rerunning the initializer updates it rather than creating a duplicate. Derive it from the site name in kebab-case.

### Wire OAuth When Required

If `rules/client-extension-types.md` shows "Yes" in the OAuth column, call `setup-oauth` to add the companion `oAuthApplicationHeadlessServer` entry. The OAuth entry must appear in the same `client-extension.yaml` file.

### Deploy and Verify

Call `deploy-and-verify`. For microservice CETs, also start the microservice process on the declared port before the smoke check.

### Troubleshoot

| Symptom | Check |
| --- | --- |
| Bundle not STARTED | Read `bundles/logs/liferay.<date>.log` for the error; run `diag <id>` in Gogo shell |
| Custom element not in Fragments panel | `htmlElementName` must be a valid custom element name (contains a hyphen) |
| 401 from microservice CET | OAuth companion not deployed or scopes insufficient |
| Site initializer not in template list | CET not `ACTIVE`; redeploy and recheck log |

## CSS Architecture for Custom Elements

CSS rules in Custom Element CETs run alongside theme styles and other CETs. A few rules keep the cascade predictable:

- **`global.css` is additive only.** Never redefine a class that already exists in the theme SCSS — the later stylesheet wins at equal specificity, producing silent cascade conflicts. Only add new classes.
- **Each component's `style.css` must be selfcontained.** The `globalCSS` client extension is not injected on every site; never rely on it from inside a Custom Element. Every rule the component needs has to be present in its own stylesheet.
- **Prefix scoped selectors with the custom element tag name.** E.g. `my-widget .btn { ... }`. This raises specificity above theme defaults regardless of stylesheet load order.
- **Include all required properties on each scoped rule.** A scoped rule does not inherit sibling properties from a lower specificity theme rule — declare every property the component depends on.

## Asset URLs

After deploy, CET assets are served at `/o/<webContextPath>/<filename>.<hash>.<ext>`. The exact URL — including the content hash — is regenerated on each build, so read the deployed `*.client-extension-config.json` (under `bundles/osgi/client-extensions`) to resolve it at runtime. Never hardcode asset paths.

## Runtime Patterns When Calling Liferay APIs From a Custom Element

### Always Use `Liferay.Util.fetch`

Use `Liferay.Util.fetch` for every API call inside a Custom Element — including GET requests:

```javascript
Liferay.Util.fetch(endpoint, options)
```

Native `fetch()` fails in two distinct ways:

- For write calls (`POST`, `PUT`, `DELETE`), native `fetch()` omits the CSRF token and the request is rejected.
- For GET calls on nonpublic Object APIs, native `fetch()` sends an anonymous request — Liferay responds `200 OK` with `items: []` rather than a 401, silently hiding every record the Guest role cannot see.

`Liferay.Util.fetch` propagates the user's session context for both cases.

### Resolve the Current User via `my-user-account`, Not `ThemeDisplay`

Never use `Liferay.ThemeDisplay.getUserId()` in client side Custom Element code. It can return `0` or a stale ID when the page is partially cached.

Resolve the current user via the session fresh API instead:

```text
GET /o/headless-admin-user/v1.0/my-user-account
```

Call this through `Liferay.Util.fetch` so the CSRF token is attached automatically.

### Never Use `roleBriefs` for Access Control

`my-user-account.roleBriefs` is not invalidated immediately after a role is assigned via API — the permission cache may not reflect the new role until the session is refreshed or the cache expires.

Do not gate Custom Element UI on `roleBriefs`. Use a functional data check against your own object model instead — to confirm a user has a role, check whether they have records that prove it (a direct report, an account membership, an assignment). This is authoritative, always current, and degrades gracefully if the data model changes.