# OAuth Scopes (CET Scaffolding Reference)

> **Before authoring:** Load `setup-oauth` when a CET needs a companion OAuth entry, or `scaffold-client-extension` when generating the whole project.

This card lists the `Liferay.*` scope strings used in OAuth companion entries in `client-extension.yaml`. Two OAuth CET types carry scopes, and which to use depends on the direction of the call:

- **`oAuthApplicationHeadlessServer`** — used by `siteInitializer` and `batch` CETs. On Liferay Cloud, a job uses its service account token to upload the CET: a site initializer through `PUT /o/headless-site/v1.0/sites/by-external-reference-code/<erc>`, a batch through `/o/headless-batch-engine`. On a local bundle the zip is processed inside the portal, and the scopes are not used. Verified on 2026.Q1.
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

**`Liferay.Headless.Object.everything` does not grant `/o/c/<plural>`.** Verified on 2026.Q1: a CET token that carried it got `403` with an empty body on `GET /o/c/events/<id>`, and the same call succeeded once `c_event.everything.read` was added. Write the scope in lowercase. How Liferay treats other casing depends on the OAuth CET type, and neither type fails the deploy. On `oAuthApplicationUserAgent`, `C_Event.everything` and `C_Event.everything.read` were **dropped from the issued token**, which still carried only the scopes Liferay recognized. On `oAuthApplicationHeadlessServer`, the same mixed case scope was lowercased in the token and granted access. Verified on 2026.Q1. Read the `scope` claim of a real token before trusting a scope list.

A microservice CET that reads or writes object entries needs the per object scope for every object it calls. Verified on 2026.Q1 for `objectAction`, `objectValidationRule`, `notificationType`, `workflowAction`, and `objectEntryManager`, each through a real CET call: the token Liferay passed got `403` on `GET` and `POST` with `Liferay.Headless.Object.everything`, and `200` with the object's own scope.

## Scope Table

| Module | Scope String | Grants Access To |
| --- | --- | --- |
| headless-admin-site | `Liferay.Headless.Admin.Site.everything` | Site pages, navigation menus, display page templates, master pages |
| headless-admin-content | `Liferay.Headless.Admin.Content.everything` | Structured contents, style books, fragment collections, web content |
| headless-delivery | `Liferay.Headless.Delivery.everything` | Blog posts, documents, structured content (delivery / nonadmin) |
| object-admin-rest | `Liferay.Object.Admin.REST.everything` | Object **definitions**, fields, relationships, actions, validations (admin) |
| object-rest (dynamic `/o/c/<plural>`) | `c_<name>.everything[.read\|.write]`, e.g. `c_event.everything.read` | Entries of **that one** object. Not covered by `Liferay.Headless.Object.everything` |
| headless-admin-list-type | `Liferay.Headless.Admin.List.Type.everything` | Picklist (list type) definitions and entries |
| headless-admin-user | `Liferay.Headless.Admin.User.everything` | Accounts, users, roles, organizations |
| headless-admin-workflow | `Liferay.Headless.Admin.Workflow.everything` | Workflow definitions, instances, tasks |
| batch-engine | `Liferay.Headless.Batch.Engine.everything` | Batch data import and export task execution |

## Scope Selection by CET Type

| CET Type | Minimum Scopes |
| --- | --- |
| `objectAction` | The per object scope (`c_<name>.everything`, or `.read` if it only reads) for each object the handler calls back into. Add `Liferay.Object.Admin.REST.everything` if the action mutates the definition |
| `objectValidationRule` | The per object scope (`c_<name>.everything`, or `.read` if it only reads) for each object the handler calls back into |
| `objectEntryManager` | The per object scope (`c_<name>.everything`, or `.read` if it only reads) for each object the handler calls back into |
| `notificationType` | The per object scope (`c_<name>.everything`, or `.read` if it only reads) for each object the handler calls back into |
| `workflowAction` | `Liferay.Headless.Admin.Workflow.everything` if the handler transitions the task through the payload's `transitionURL`, plus the per object scope for each object it calls back into |
| `siteInitializer` | `Liferay.Headless.Site.everything`. The upload got `403` with `Admin.Site`, `Admin.Content`, `Object.Admin.REST`, and `Admin.User`, and `200` with `Headless.Site` alone |
| Commerce CETs | Granular per Commerce subdomain — e.g. `Liferay.Headless.Commerce.Admin.Channel.everything`, `Liferay.Headless.Commerce.Admin.Order.everything`, `Liferay.Headless.Commerce.Admin.Catalog.everything`. Verify the exact subdomain against the relevant `headless-commerce-admin-*` module's `rest-config.yaml`. |

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

Each scope string is one list entry. Liferay does **not** reject an unknown scope string: the deploy succeeds and the string is silently left out of issued tokens (verified on 2026.Q1). Confirm a scope took effect by reading the `scope` claim of a real token.

## Verifying Scope Coverage

If an API call returns 403 (Forbidden), the token's scopes do not cover the endpoint:

1. Check the exact endpoint against the module table above.

1. Add the missing scope to the `oAuthApplicationHeadlessServer` entry in `client-extension.yaml`.

1. Redeploy via `deploy-and-verify`.

A 401 (Unauthorized) means the token itself is not valid — check the OAuth application registration and credentials, not the scopes.

## References

- OAuth 2 application management: Control Panel → OAuth 2 Administration
- `setup-oauth` skill: companion OAuth application generation
- CET type requirements: `rules/client-extension-types.md`