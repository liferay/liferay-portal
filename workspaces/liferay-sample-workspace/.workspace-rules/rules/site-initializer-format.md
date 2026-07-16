# Site Initializer Format

Reference for the `manage-environments` and `scaffold-client-extension` skills when capturing or building a `siteInitializer` CET.

A site initializer is a client extension of type `siteInitializer`. When deployed and triggered, it creates a fully configured site from the directory tree below.

## Provisioning and Iteration

The site-initializer CET tree is the **single source of truth** for the site. Build the site by triggering the initializer, then iterate by editing the source tree and applying each change by the cheapest reliable path. Always edit the source files first; never hand edit the live site as the authoritative copy.

| Change Type | How to Apply It | Reprovision the Site? |
| --- | --- | --- |
| Theme / design | Deploy the `themeCSS` CET (`blade gw deploy`) | No |
| Object definition or data | `object-admin` API + batch import | No |
| Fragment content or new fragment | Edit the source tree, then reprovision — there is no portable live fragment import endpoint | Yes |
| New page, page composition change, or fragment placement on a page | Reprovision: delete the site, recreate from the initializer | Yes |

A page composition change — adding or rearranging fragments **on an existing page**, not just adding new pages — also requires a reprovision. Retriggering the initializer upserts pages but does not retrofit composition changes onto pages that already exist, so edited `page-definition.json` content only takes effect after the site is recreated.

The initializer runs **once, at site creation** — redeploying the CET does not update an existing site. To reapply `layouts/` changes, reprovision by **deleting the site, then redeploying the initializer CET**:

```bash
# Step 1. Delete the existing site — address it by its EXTERNAL REFERENCE CODE in the path,
#    not its numeric ID (a numeric ID in this path returns 404).

curl \
	--request DELETE \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-site/v1.0/sites/<site-erc>" \
	--user "test@liferay.com:test"

# Step 2. Redeploy the initializer CET. A siteInitializer CET that declares a
#    siteExternalReferenceCode autoprovisions the site on deploy.

blade gw deploy
```

**Sites are addressed by external reference code in the REST path** (`/sites/<erc>`) for these admin operations — passing the numeric site ID returns 404.

> Prefer delete then redeploy over recreating with `POST /sites` + `templateExternalReferenceCode` (or `templateKey`): resolving a site-initializer template through that POST is unreliable. The portable path relies on the CET's `siteExternalReferenceCode` to autoprovision on deploy.

Reprovisioning is always safe because the source tree is current. **Object definitions and entries are company scoped and survive site deletion**, so runtime data (for example, registration entries) persists across a reprovision. The exception is an object whose scope is explicitly set to the site — those entries are deleted with the site.

### Applying a Fragment Change

Fragments live in the initializer tree under `fragments/group/<collection-key>/fragments/<fragment-name>/`. To change a fragment, edit those source files and reprovision the site (delete then redeploy, above). Do **not** assume a live fragment collection import endpoint exists — there is no portable headless endpoint for importing a fragment collection into a running site; verify the live API surface before relying on any such call (the `get-openapi` MCP tool, or fetch the relevant module's `GET /o/<module>/v1.0/openapi.json` with curl).

## Directory Tree

```
client-extensions/<name>/
  client-extension.yaml
  site-initializer/
    batch/                        # Bulk data import via Headless Batch Engine
      <NN-entity-name>.batch-engine-data.json
    documents/                    # Documents and media
      group/
        <folder-name>/
          <filename>
    fragments/                    # Page fragments
      group/
        <collection-key>/
          collection.json         # {"name": ..., "description": ...}
          fragments/              # required nesting level
            <fragment-name>/      # dir name becomes the fragment "key"
              fragment.json
              index.html
              index.css
              index.js
              configuration.json  # optional
    journal-articles/             # Web content (Journal) articles
      <article-name>.xml
      <article-name>.json
    layout-page-templates/        # Page templates and master pages
      display-page-templates/
        <template-name>/
          page-definition.json
          page-template.json
      master-pages/
        <master-name>/          # dir name is the master "key" (see settings.masterPage.key)
          page-definition.json
          master-page.json       # {"name": ...} — NOT page-template.json
          thumbnail.png          # optional
    layout-set/                   # Site wide navigation and theme settings
      public/
        metadata.json
    layouts/                      # Site pages
      <NN-page-name>/             # NN prefix controls creation order
        page.json                 # Page metadata (type, name, friendlyURL)
        page-definition.json      # Content Page fragment composition
        <NN-child-page>/          # Nested child pages
          page.json
          page-definition.json
    roles.json                    # Site roles
    style-books/                  # Style book entries
      <style-book-name>/
        style-book.json
    thumbnail.png                 # Site thumbnail (displayed in Site Admin)
```

## Batch Engine Data Format

Files under `batch/` are named `<NN-entity-name>.batch-engine-data.json`. The `NN` prefix controls import order (lower numbers first).

```json
{
	"configuration": {
		"className": "com.liferay.headless.admin.list.type.dto.v1_0.ListTypeDefinition",
		"multiCompany": true,
		"parameters": {
			"containsHeaders": "true",
			"createStrategy": "UPSERT",
			"importStrategy": "ON_ERROR_FAIL",
			"updateStrategy": "UPDATE"
		},
		"taskItemDelegateName": "DEFAULT"
	},
	"items": [
		{
			"externalReferenceCode": "<ERC>",
			...
		}
	]
}
```

| Field | Purpose |
| --- | --- |
| `configuration.className` | Fully qualified DTO class name; determines which Headless endpoint is called |
| `configuration.multiCompany` | `true` to import on all virtual instances |
| `parameters.createStrategy` | `INSERT` (fail on duplicate) or `UPSERT` (update if exists) |
| `parameters.importStrategy` | `ON_ERROR_FAIL` (halt on first error) or `ON_ERROR_CONTINUE` |
| `items` | Array of entity objects matching the DTO schema |

Common `className` values:

| Entity | `className` |
| --- | --- |
| Picklist | `com.liferay.headless.admin.list.type.dto.v1_0.ListTypeDefinition` |
| Object Definition | `com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition` |
| Object Folder | `com.liferay.object.admin.rest.dto.v1_0.ObjectFolder` |
| Role | `com.liferay.headless.admin.user.dto.v1_0.Role` |
| User | `com.liferay.headless.admin.user.dto.v1_0.UserAccount` |

## `page.json` Format

```json
{
	"friendlyURL": "/home",
	"hidden": false,
	"name": "Home",
	"name_i18n": {
		"en_US": "Home"
	},
	"permissions": [
		{
			"actionIds": [
				"VIEW"
			],
			"roleName": "Guest",
			"scope": 4
		}
	],
	"private": false,
	"system": false,
	"type": "Content"
}
```

`type` values: `"Content"`, `"Portlet"`, `"URL"`, `"Embedded"`.

## `page-definition.json` Format

Mirrors the `pageDefinition` field of the Headless Admin Site page API. Minimum shape for an empty content page:

```json
{
	"pageElement": {
		"pageElements": [
		],
		"type": "Root"
	},
	"version": "1.0"
}
```

Add fragment elements under `pageElements` to compose the layout. Each fragment element references its fragment by `key` (the fragment's directory name under `fragments/group/<collection-key>/fragments/`) and `siteKey` (the `[$GROUP_KEY$]` token, which resolves to the current site):

```json
{
	"definition": {
		"fragment": {
			"key": "<fragment-name>",
			"siteKey": "[$GROUP_KEY$]"
		}
	},
	"type": "Fragment"
}
```

Built in fragments instead use a combined `key` with no `siteKey` (e.g. `"key": "BASIC_COMPONENT-paragraph"`). The importer reads `key`/`siteKey` only — a fragment element written with `collectionExternalReferenceCode`/`fragmentEntryKey` is silently dropped and the page renders blank.

## Master Pages

A master page lives under `layout-page-templates/master-pages/<master-name>/` with two files: `master-page.json` (just `{"name": "<Display Name>"}`) and `page-definition.json`. The master's **key is its directory name** (`<master-name>`).

The master `page-definition.json` holds the persistent header/footer fragments plus a **DropZone** marking where each page's own content is injected. The DropZone must carry a `definition.fragmentSettings` block — a bare `{"type": "DropZone"}` does not render. Use the numeric `version` `1.1`:

```json
{
	"pageElement": {
		"pageElements": [
			{
				"definition": {
					"fragment": {
						"key": "<header-fragment-name>",
						"siteKey": "[$GROUP_KEY$]"
					}
				},
				"type": "Fragment"
			},
			{
				"definition": {
					"fragmentSettings": {
						"unallowedFragments": [
						]
					}
				},
				"type": "DropZone"
			}
		],
		"type": "Root"
	},
	"version": 1.1
}
```

A page selects its master through its **own** `page-definition.json`, in a top level `settings.masterPage.key` whose value is the master's directory name:

```json
{
	"pageElement": {
		"pageElements": [
		],
		"type": "Root"
	},
	"settings": {
		"masterPage": {
			"key": "<master-name>"
		}
	},
	"version": 1.1
}
```

## `layout-set/public/metadata.json`

Controls navigation menu visibility and theme assignment for the public (nonprivate) layout set:

```json
{
	"colorSchemeId": "01",
	"themeId": "classic_WAR_classictheme"
}
```

For themeCSS CETs, leave `themeId` as `"classic_WAR_classictheme"` and control appearance entirely from the CET.

## `client-extension.yaml` for the Initializer

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
        - Liferay.Headless.Batch.Engine.everything
    type: oAuthApplicationHeadlessServer
```

## Triggering the Initializer

After deploying, open Control Panel → Sites → Add and select the site initializer from the template list. The name matches the `name` field in `client-extension.yaml`.

Alternatively, via the REST API (the **discouraged** path — see the Provisioning warning above; the CET's `siteExternalReferenceCode` autoprovision + delete then redeploy is the portable route):

```bash
curl \
	--data '{
		"membershipType": "open",
		"name": "<Site Name>",
		"templateType": "site-initializer",
		"templateKey": "<workspace-id>-site-init"
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-site/v1.0/sites" \
	--user "test@liferay.com:test"
```

The current `Site` DTO uses `templateKey` (not `templateExternalReferenceCode`); resolving a site-initializer template through this POST is unreliable regardless.

## References

- Sample site initializer: `workspaces/liferay-sample-workspace/client-extensions/liferay-sample-site-initializer`
- Production site initializer: `modules/apps/site-initializer/site-initializer-cms`
- CET type details: `rules/client-extension-types.md`
- OAuth scopes: `rules/oauth-scopes.md`