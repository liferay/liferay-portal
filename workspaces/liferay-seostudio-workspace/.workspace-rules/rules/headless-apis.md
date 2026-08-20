# Headless APIs

Key Liferay REST modules, their base URIs, primary resources, and OAuth scopes. All paths resolve relative to `http://localhost:${PORT}`. Use Basic auth (`test@liferay.com:test`) for the curl examples on this page and in the skills. The OAuth scope strings noted per module are for `oAuthApplicationHeadlessServer` blocks when scaffolding microservice CETs — see `rules/oauth-scopes.md` for the full scaffolding reference.

The tables below list the common endpoints per module — they are not exhaustive. To confirm an exact path or find an endpoint not listed here, use the `get-openapi` MCP tool (see `skills/mcp-server/SKILL.md`), or fetch `GET /o/<module>/v1.0/openapi.json` directly. Base URIs, feature flag gates, and OAuth scopes are *not* discoverable from the specs — rely on this card for those.

## headless-admin-site

**Base URI:** `/o/headless-admin-site/v1.0`

> **Path identifier — this module keys subresources by external reference code, not numeric ID.** Use `{siteExternalReferenceCode}` (the site's ERC) in these paths; passing a numeric site ID returns 404. (By contrast, `headless-admin-content` and `headless-delivery` below use the numeric `{siteId}`.)

| Resource | Method | Path |
| --- | --- | --- |
| List sites | GET | `/sites` |
| Create site | POST | `/sites` |
| Get site | GET | `/sites/{siteExternalReferenceCode}` |
| Create page | POST | `/sites/{siteExternalReferenceCode}/site-pages` |
| List pages | GET | `/sites/{siteExternalReferenceCode}/site-pages` |
| Create navigation menu | POST | `/sites/{siteExternalReferenceCode}/navigation-menus` |
| Create display page template | POST | `/sites/{siteExternalReferenceCode}/display-page-templates` |
| Create master page | POST | `/sites/{siteExternalReferenceCode}/master-pages` |

**Required flag:** `LPD-35443` (off by default) for public layout API; `LPD-38869` (on by default) for private. Page-element / page-specification composition additionally requires `LPD-74328`.

**OAuth scope:** `Liferay.Headless.Admin.Site.everything`

## headless-admin-content

**Base URI:** `/o/headless-admin-content/v1.0`

| Resource | Method | Path |
| --- | --- | --- |
| Create web content | POST | `/sites/{siteId}/structured-contents` |
| List web content | GET | `/sites/{siteId}/structured-contents` |
| Create content structure | POST | `/sites/{siteId}/content-structures` |
| Create style book | POST | `/sites/{siteId}/style-books` |
| Import fragment collection | POST | `/sites/{siteId}/fragment-collections` |

**OAuth scope:** `Liferay.Headless.Admin.Content.everything`

## headless-delivery

**Base URI:** `/o/headless-delivery/v1.0`

| Resource | Method | Path |
| --- | --- | --- |
| List structured contents (site) | GET | `/sites/{siteId}/structured-contents` |
| Create blog post | POST | `/sites/{siteId}/blog-postings` |
| Get document | GET | `/documents/{documentId}` |
| Upload document | POST | `/sites/{siteId}/documents` |
| Get site page content | GET | `/sites/{siteId}/site-pages/{pageFriendlyUrl}/page-contents` |

**OAuth scope:** `Liferay.Headless.Delivery.everything`

## object-admin

**Base URI:** `/o/object-admin/v1.0`

| Resource | Method | Path |
| --- | --- | --- |
| Create object definition | POST | `/object-definitions` |
| List object definitions | GET | `/object-definitions` |
| Publish object definition | POST | `/object-definitions/{id}/publish` |
| Add field | POST | `/object-definitions/{id}/object-fields` |
| Add relationship | POST | `/object-definitions/{id}/object-relationships` |
| Add action | POST | `/object-definitions/{id}/object-actions` |
| Add validation | POST | `/object-definitions/{id}/object-validation-rules` |

Object entries (after publish): `/o/c/<pluralLabel>` — GET, POST, PUT, PATCH, DELETE by ID.

**OAuth scope:** `Liferay.Object.Admin.REST.everything` for the admin endpoints above (definitions, fields, etc.). `Liferay.Headless.Object.everything` for the dynamic `/o/c/<plural>` entry endpoints.

## headless-admin-list-type

**Base URI:** `/o/headless-admin-list-type/v1.0`

| Resource | Method | Path |
| --- | --- | --- |
| Create picklist | POST | `/list-type-definitions` |
| List picklists | GET | `/list-type-definitions` |
| Add picklist entry | POST | `/list-type-definitions/{id}/list-type-entries` |

**OAuth scope:** `Liferay.Headless.Admin.List.Type.everything`

## headless-admin-user

**Base URI:** `/o/headless-admin-user/v1.0`

| Resource | Method | Path |
| --- | --- | --- |
| List accounts | GET | `/accounts` |
| Create account | POST | `/accounts` |
| Create role | POST | `/roles` |
| List roles | GET | `/roles` |
| Assign role to user | POST | `/roles/{roleId}/association/user-account/{userId}` |
| List users | GET | `/user-accounts` |
| Create user | POST | `/user-accounts` |

**OAuth scope:** `Liferay.Headless.Admin.User.everything`

## headless-admin-workflow

**Base URI:** `/o/headless-admin-workflow/v1.0`

| Resource | Method | Path |
| --- | --- | --- |
| Create workflow definition | POST | `/workflow-definitions` |
| List workflow definitions | GET | `/workflow-definitions` |
| List workflow instances | GET | `/workflow-instances` |
| Transition workflow task | POST | `/workflow-tasks/{id}/change-transition` |

**OAuth scope:** `Liferay.Headless.Admin.Workflow.everything`

## Common Parameters

| Parameter | Values | Notes |
| --- | --- | --- |
| `page` | integer | 1 based page number |
| `pageSize` | integer | Max items per page (default 20, max 200) |
| `filter` | OData expression | e.g. `title eq 'Hello'` |
| `sort` | `fieldName:asc` or `fieldName:desc` | |
| `fields` | comma separated field names | Projection to reduce response size |
| `restrictFields` | comma separated field names | Exclude from response |

## Error Codes

| HTTP Status | Meaning |
| --- | --- |
| 400 | Validation error; read the `title` field in the problem detail response |
| 401 | Not authenticated; check credentials or OAuth token |
| 403 | Authenticated but forbidden; scope too narrow or permissions missing |
| 404 | Resource not found or feature flag off; check flag state |
| 409 | Conflict; typically duplicate name or ERC |
| 500 | Server error; check `bundles/logs/liferay.<date>.log` |