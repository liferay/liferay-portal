---

paths:
  - "**/client-extension.yaml"

---

# OAuth Scopes (CET Scaffolding Reference)

> **Before authoring:** Load `setup-oauth` when a CET needs a companion OAuth entry, or `scaffold-client-extension` when generating the whole project.

This card lists the `Liferay.*` scope strings used in OAuth companion entries in `client-extension.yaml`. Two OAuth CET types carry scopes, and which to use depends on the direction of the call:

- **`oAuthApplicationHeadlessServer`** — used by `siteInitializer` and `batch` CETs. On Liferay Cloud, a job uses its service account token to upload the CET: a site initializer through `PUT /o/headless-site/v1.0/sites/by-external-reference-code/<erc>`, a batch through `/o/headless-batch-engine`. On a local bundle, the zip is processed inside the portal, and the scopes are not used. Verified on 2026.Q1. Also used by a microservice that needs its own identity, typically an `objectAction` triggered by an anonymous visitor, whose inbound token is Guest's (`setup-oauth` → "A Microservice With Its Own Identity").
- **`oAuthApplicationUserAgent`** — used by microservice CETs (`objectAction`, `objectValidationRule`, `objectEntryManager`, `notificationType`, `workflowAction`, etc.), where Liferay calls the microservice and passes a user delegated token that the microservice can use to call back into Liferay.

The scope strings in the table below apply to both types — only the companion CET type differs.

**Not needed for:**
- Curl examples using Basic auth (`test@liferay.com:test`). Basic auth as the test user has full admin perms; scopes are not evaluated.
- MCP server calls using the default `Basic` header. Same path.
- Custom elements using `Liferay.Util.fetch` (in browser, session cookie based).

Each `Liferay.*` scope in the table grants every operation in its module. Object entry scopes are the exception: each object has its own scope, and it splits reads from writes. `.read` allows `GET` only, `.write` allows `POST`, `PATCH`, and `DELETE` but not `GET`, and `.everything` allows both. Verified on 2026.Q1 with both OAuth CET types. See "Object Modules: Admin vs Entry" below.

## Object Modules: Admin vs Entry

Liferay's object work spans two distinct REST modules with distinct scopes:

- `Liferay.Object.Admin.REST.everything` — **admin surface**. Object **definition** CRUD: create/update/publish definitions, fields, relationships, validations, actions. Endpoints under `/o/object-admin/v1.0/`.
- `c_<name>.everything[.read|.write]` — **entry surface**, one scope per object. Object **entry** CRUD under `/o/c/<pluralLabel>`. The prefix is `c_` plus the object definition's `name` **lowercased**: `c_event.everything.read` for a custom object whose `name` is `Event`. Verified on 2026.Q1: `object-admin` returned `name` `ScopeProbe`, and `c_scopeprobe.everything` was the scope that granted `/o/c/scopeprobes`.

**`Liferay.Headless.Object.everything` does not grant `/o/c/<plural>`.** Verified on 2026.Q1: a CET token that carried it got `403` with an empty body on `GET /o/c/events/<id>`, and the same call succeeded once `c_event.everything.read` was added.

Write the scope in lowercase. How Liferay treats other casing depends on the OAuth CET type, and neither type fails the deploy. On `oAuthApplicationUserAgent`, `C_Event.everything` and `C_Event.everything.read` were **dropped from the issued token**, which still carried only the scopes Liferay recognized. On `oAuthApplicationHeadlessServer`, the same mixed case scope was lowercased in the token and granted access. Verified on 2026.Q1. Read the `scope` claim of a real token before trusting a scope list.

A microservice CET that reads or writes object entries needs the per object scope for every object it calls. Verified on 2026.Q1 for `objectAction`, `objectValidationRule`, `notificationType`, `workflowAction`, and `objectEntryManager`, each through a real CET call: the token Liferay passed got `403` on `GET` and `POST` with `Liferay.Headless.Object.everything`, and `200` with the object's own scope.

**Tell the scope filter from the permission layer by the response.** A missing scope answers a bare `403`, with an empty body and no `WWW-Authenticate` header, on every operation, including reads the user's role may VIEW; it fires before any permission check. A permission failure answers `404` on a `GET` and a `403` with a JSON `{"status": "FORBIDDEN"}` body on other methods (`PrincipalExceptionMapper`). Verified on 2026.Q2.

## Scope Table

| Module | Scope String | Grants Access To |
| --- | --- | --- |
| headless-admin-site | `Liferay.Headless.Admin.Site.everything` | Site pages, navigation menus, display page templates, master pages, style books |
| headless-admin-content | `Liferay.Headless.Admin.Content.everything` | Structured contents (web content), display page templates, page definitions |
| headless-delivery | `Liferay.Headless.Delivery.everything` | Blog posts, documents, structured content (delivery / nonadmin) |
| object-admin-rest | `Liferay.Object.Admin.REST.everything` | Object **definitions**, fields, relationships, actions, validations (admin) |
| object-rest (dynamic `/o/c/<plural>`) | `c_<name>.everything[.read\|.write]`, e.g. `c_event.everything.read` | Entries of **that one** object; not covered by `Liferay.Headless.Object.everything` |
| headless-admin-list-type | `Liferay.Headless.Admin.List.Type.everything` | Picklist (list type) definitions and entries |
| headless-admin-user | `Liferay.Headless.Admin.User.everything` | Accounts, users, roles, organizations |
| headless-admin-workflow | `Liferay.Headless.Admin.Workflow.everything` | Workflow definitions, instances, tasks |
| batch-engine | `Liferay.Headless.Batch.Engine.everything` | Batch data import and export task execution |

## Scope Selection by CET Type

| CET Type | Minimum Scopes |
| --- | --- |
| `objectAction` | The per object scope (`c_<name>.everything`, or `.read` if it only reads) for each object the handler calls back into, plus `Liferay.Object.Admin.REST.everything` if the action mutates the definition. If the trigger can be anonymous, put these on a separate `oAuthApplicationHeadlessServer` entry (`setup-oauth` → "A Microservice With Its Own Identity") |
| `objectValidationRule` | The per object scope (`c_<name>.everything`, or `.read` if it only reads) for each object the handler calls back into |
| `objectEntryManager` | The per object scope (`c_<name>.everything`, or `.read` if it only reads) for each object the handler calls back into |
| `notificationType` | The per object scope (`c_<name>.everything`, or `.read` if it only reads) for each object the handler calls back into |
| `workflowAction` | `Liferay.Headless.Admin.Workflow.everything` if the handler transitions the task through the payload's `transitionURL`, plus the per object scope for each object it calls back into |
| `batch` | `Liferay.Headless.Batch.Engine.everything` |
| `siteInitializer` | `Liferay.Headless.Site.everything` alone; see the note below the table |
| Commerce CETs | Granular per Commerce subdomain — e.g. `Liferay.Headless.Commerce.Admin.Channel.everything`, `Liferay.Headless.Commerce.Admin.Order.everything`, `Liferay.Headless.Commerce.Admin.Catalog.everything`. Verify the exact subdomain against the relevant `headless-commerce-admin-*` module's `rest-config.yaml`. |

The `siteInitializer` upload got `403` with `Liferay.Headless.Admin.Site.everything`, `Liferay.Headless.Admin.Content.everything`, `Liferay.Object.Admin.REST.everything`, and `Liferay.Headless.Admin.User.everything`, and `200` with `Liferay.Headless.Site.everything` alone.

## How Scopes Appear in `client-extension.yaml`

```yaml
# The siteInitializer CET: on Liferay Cloud, a job uses this service account token to upload the site initializer

<workspace-id>-site-oauth:
    .serviceAddress: localhost:8080
    .serviceScheme: http
    name: <WorkspaceId> Site OAuth
    scopes:
        - Liferay.Headless.Site.everything
    type: oAuthApplicationHeadlessServer

# The objectAction / workflowAction / notificationType CETs — Liferay calls the microservice
# and passes a user delegated token; the microservice uses it to call back into Liferay

<workspace-id>-action-oauth:
    .serviceAddress: localhost:8081
    .serviceScheme: http
    name: <WorkspaceId> Action OAuth
    scopes:
        - c_<name>.everything
    type: oAuthApplicationUserAgent
```

Each scope string is one list entry. Liferay does **not** reject an unknown scope string: the deploy succeeds and the string is silently left out of issued tokens (verified on 2026.Q1). Confirm a scope took effect by reading the `scope` claim of a real token. A per object scope counts as unknown when the object does not exist yet at deploy, so deploy the CET again after creating the object. A scope list change takes effect only at the next deploy, and a deploy of a changed zip recreates the OAuth application with a new client ID and secret; read them again (`skills/setup-oauth/SKILL.md` → "Token Acquisition"). Verified on 2026.Q3.

## Verifying Scope Coverage

If an API call returns 403 (Forbidden), the token's scopes do not cover the endpoint:

1. Check the exact endpoint against the module table above. A bare `403` on `/o/c/<plural>` means the per object scope is missing; see "Object Modules: Admin vs Entry" above for telling it from a permission failure.

1. Add the missing scope to the `oAuthApplicationHeadlessServer` entry in `client-extension.yaml`.

1. Redeploy via `deploy-and-verify`.

A 401 (Unauthorized) means the token itself is not valid — check the OAuth application registration and credentials, not the scopes.

### CREATE_TOKEN on OAuth Applications

A CX provisioned OAuth 2 application gets **no role permissions** at creation. The token endpoint requires the requesting user to hold `CREATE_TOKEN` on that application, or to own it. For `oAuthApplicationHeadlessServer` (client credentials) the check runs on the application's **client credentials user** (`LiferayClientCredentialsAccessTokenGrantHandler.java:71`), and the owner is the Guest user (`OAuth2ProviderApplicationHeadlessServerConfigurationFactory.java:123`), not that user. The default, the company's service account, is an administrator, so it passes with no grant. **A least privilege user is refused** (`400 invalid_grant`) until a role it holds has `VIEW` and `CREATE_TOKEN` on the application. Choose that user with `userAccountScreenName` (or `userAccountEmailAddress`) on the `oAuthApplicationHeadlessServer` entry. It is read only when the application is first created (`OAuth2ProviderApplicationHeadlessServerConfigurationFactory.java:130-141`), so the user must exist before the CX's first deploy.

For `oAuthApplicationUserAgent` the **end user** requests the token, so without an explicit grant no signed in user can obtain one. Grant `VIEW` and `CREATE_TOKEN` on the application resource to a **regular (company scoped) role** the users hold; an organization or site role fails, because the token check runs with no group. A flow before sign in needs the grant on the built in `Guest` role.

A company scope grant (`resource-permissions.json`, `scope` `"1"`) works and covers every OAuth application. To grant one application only, use individual scope on its ID. A tree cannot name that ID, and **a deploy of a changed CX zip creates a new application**, with a new ID and none of the old grants. Reapply an individual grant whenever the application is created, for example from a module with a `ModelListener<OAuth2Application>` (`onAfterCreate`, `onAfterUpdate`) and a `PortalInstanceLifecycleListener`, writing each grant with `ResourcePermissionLocalService.setResourcePermissions(…, SCOPE_INDIVIDUAL, …)` by the application's ERC. Verified on 2026.Q3.

Where a page is served from matters too: see `skills/setup-oauth/SKILL.md` → "Call the CET From Browser Code" (redirect URIs come from the instance's virtual hosts).

## References

- OAuth 2 application management: Control Panel → OAuth 2 Administration
- `setup-oauth` skill: companion OAuth application generation
- CET type requirements: `rules/client-extension-types.md`