---

description: Create and configure Liferay Data Sets (Frontend Data Sets) over Object entries through the data-set-admin API — table, list, and card views, columns and renderers, filters, default sort, row and creation actions, custom renderer and filter client extensions — then place the Data Set fragment on a page and move the definition between environments. Use when the user asks for a table, list, or card grid of object entries, a filterable listing, or a Data Set that renders nothing or crashes.
name: manage-data-sets

---

# Manage Data Sets

A **Data Set** is a configurable table, list, or card view over a REST collection, usually an object's `/o/c/<plural>`. It is built from the Data Set definition (the `data-set-admin` API) and the **Data Set fragment** placed on a page. The definition itself is stored as entries of the system object `L_DATA_SET`, with one related object per child type.

Everything below was verified live on 2026.Q3.2: writes read back over the API, the rendered page checked in a browser, and admin UI shapes captured for comparison.

## When to Invoke

- "Show the books in a table", "a filterable list of …", "cards of …", "add a column / filter / sort / row action"
- A Data Set fragment shows nothing, "No Results Found", or "An unexpected error occurred"
- Moving a Data Set to another environment

Page placement is `manage-pages`, grants are `manage-roles-permissions`, and renderer or filter CETs are `scaffold-client-extension`.

## Prerequisites

- **Feature flag `LPS-164563`** (note the `LPS` prefix). Without it, `data-set-admin` returns `404`. On 2026.Q1 and 2026.Q2 it also needs `LPD-34594` (Root Object Definitions), and stays off while that one is off. See `feature-flags`.
- **Verified on 2026.Q3 only.** On 2026.Q2 the API and the page fragment exist as below; on 2026.Q1 the page fragment differs (see "Place the Data Set Fragment").
- **The module has no version segment:** `/o/data-set-admin/data-sets`; `/o/data-set-admin/v1.0/…` returns `404`.
- **The OpenAPI spec is per resource**, not at the module root: `/o/data-set-admin/<resource>/openapi.json` for `data-sets`, `table-sections`, `sorts`, `actions`, `date-filters`, `selection-filters`, `client-extension-filters`, `list-sections`, `cards-sections`, `snapshots`. Read required fields there. `data-sets` also exposes `validate`, `batch`, `export-batch`, and `permissions`.

## Workflow

### Create the Data Set

```bash
curl \
	--data '{
		"defaultItemsPerPage": 20,
		"externalReferenceCode": "<PROJECT>_<PURPOSE>_DS",
		"label": "<Display Name>",
		"listOfItemsPerPage": "10, 20, 50",
		"restApplication": "/c/<plural>",
		"restEndpoint": "/",
		"restSchema": "<ObjectDefinitionName>"
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/data-set-admin/data-sets" \
	--user "test@liferay.com:test"
```

- **Always choose the ERC.** It is the only portable pointer, and the page fragment references the Data Set by it. The admin UI cannot set one: it generates a UUID, which a later `PATCH` of `externalReferenceCode` can rename.
- **The fetch URL is `"/o" + restApplication + restEndpoint`.** So `restApplication` starts with `/` (`/c/<plural>`), and `restEndpoint` is `/` for an object. `"object-admin"` produces `/oobject-admin/`, a `404`, and an empty fragment.
- **A versioned Headless module** (e.g. Accounts) splits the path as `restApplication: "/headless-admin-user"`, `restEndpoint: "/v1.0/accounts"`. Put the version in `restEndpoint`, or it is dropped.
- **`restSchema` is not part of the URL.** Set it to the object's `name` (`restSchema: "ObjectEntry"` still renders).
- **`label` and `listOfItemsPerPage` are plain strings.** A map or an array is silently stored as its Java `toString()` (`{en_US=…}`, `"[10, 20, 50]"`) and shown that way in the admin list. `label_i18n` and `objectDefinitionId` do not exist and are ignored.
- **Display switches:**
  - `showSearch` (adds `&search=`) ;
  - `hideManagementBarInEmptyState` ;
  - `defaultVisualizationMode` (`table` | `list` | `cards`, lowercase) ;
  - `snapshotsEnabled` (user-saved views, stored separately in `/o/data-set-admin/snapshots`) ;
  - **`active: false` hides the whole fragment**, with no request and no message.
- **`POST /data-sets/validate` takes `{"values": {…}}`.** `{}` returns a `500` NPE, and it does not catch a map-valued `label`.

### Add Children — Only Through the Nested Path

Columns, sorts, filters, actions, and view sections are separate entries linked to the Data Set. **Only the nested path works:**

```text
POST   /o/data-set-admin/data-sets/by-external-reference-code/<DS-ERC>/dataSetToDataSet<Child>
GET    /o/data-set-admin/data-sets/by-external-reference-code/<DS-ERC>/dataSetToDataSet<Child>
PATCH  /o/data-set-admin/data-sets/by-external-reference-code/<DS-ERC>/dataSetToDataSet<Child>/<child-erc>
DELETE /o/data-set-admin/data-sets/by-external-reference-code/<DS-ERC>/dataSetToDataSet<Child>/<child-erc>   → 204
```

The child types are `TableSections`, `ListSections`, `CardsSections`, `Sorts`, `DateFilters`, `SelectionFilters`, `ClientExtensionFilters`, and `Actions`. `/data-sets/{id}/…` works too.

- **The root collections** (`/o/data-set-admin/table-sections`, …) answer `409 "Conflict with postObjectEntry"` / `getObjectEntriesPage` to every call.
- **The back-references** on a child are `r_dataSetToDataSet<Child>_l_dataSetERC` / `_l_dataSetId`, with an `_l_` infix.
- **A nested `PUT` returns `404`.** `…/disassociate` fails because the relationship is required. Change a child with `PATCH`, or remove it with `DELETE`.
- **Children can also go inline in the create body**, as arrays named `dataSetToDataSet<Child>`: one call builds the whole Data Set.
- **Ordering.** The `*Order` fields on the Data Set (`tableSectionsOrder`, `sortsOrder`, …) are empty when built from the UI, but **`tableSectionsOrder` is writable and sets column order**: a comma-separated list of child ERCs. Unknown ERCs are ignored, and unlisted columns come last.
- **`PUT /data-sets/by-external-reference-code/<DS-ERC>` with the child arrays inline is an idempotent upsert** (verified on 2026.Q3.5): it creates the set when the ERC is new, and otherwise keeps the set's id and **upserts each child by its ERC in place** (same child ids; a PATCHed label and order come back to the body's values). So one `PUT` per Data Set, re-runnable, is the scripted way to create or restore a definition. A `POST` of an existing ERC answers `400 "This external reference code is already in use"` and changes nothing.

### Columns (`dataSetToDataSetTableSections`)

```json
{
	"fieldName": "title",
	"label_i18n": {
		"en_US": "Title"
	},
	"renderer": "default",
	"rendererType": "internal",
	"sortable": true,
	"type": "string"
}
```

- **`type` is required but neither validated nor rendered.** The UI writes `string`, `number`, `integer`, or `boolean`; a Date field becomes `string`. The renderer decides the display.
- **Built-in `renderer` keys:** `default` (raw value; a picklist prints as JSON, a date as ISO), `label` (pill), `date`, `dateTime` (local time), `boolean` (Yes/No), `status` (workflow badge), `link`, `image`, `actionLink`, and `quantitySelector`. An unknown or missing key falls back to `default`. `rendererType` is `""` on UI-generated columns and `"internal"` once edited, and both render the same.
- **Dot notation:**
  - `genre.name` / `genre.key` read a picklist;
  - `<relationshipName>.<field>` (e.g. `authorToBooks.authorName`) reads the related entry, and the fragment adds `nestedFields` itself;
  - the raw FK field shows the numeric id.
- **Header sort** sends `sort=<field>:asc|desc` and works on direct fields. On a dotted field, the source answers `400 "Unable to sort by property: …"`, and the table silently keeps its order. So set `sortable: false` there, as the UI does. On a system object module (e.g. `/accounts`), only a fixed set of built-in properties sorts; source the Data Set from a child custom object instead, and show the parent through the reverse relationship's dot notation.
- **Never define two columns on the same `fieldName`.** The table renders one cell per distinct field, and every following column shifts under the wrong header.
- **A column's `active: false` is ignored at render.** Delete the column instead.
- **CSS hooks:** cells are `.cell-<fieldName>`, with dots becoming hyphens (`.cell-authorToBooks-authorName`), and headers are `th.cell-<fieldName>`. The markup itself is fixed.
- **The UI generates no column on create.** "Add Fields" → "Assign from Data Source" lists the fields, related ones included. Via the API, add every column yourself.

### Filters

Give every filter a label: **a date filter without `label_i18n` never appears**. Use one filter per field: the UI enforces it, the API does not, and two filters on one field share their value.

**Date:**

```json
{
	"fieldName": "publicationDate",
	"label_i18n": {
		"en_US": "Published"
	},
	"type": "date"
}
```

It emits `filter=(publicationDate ge …) and (publicationDate le …)`.

**Picklist** (`dataSetToDataSetSelectionFilters`):

```json
{
	"fieldName": "genre",
	"include": true,
	"label_i18n": {
		"en_US": "Genre"
	},
	"multiple": true,
	"preselectedValues": "[]",
	"source": "<LIST_TYPE_DEFINITION_ERC>",
	"sourceType": "OBJECT_PICKLIST"
}
```

It emits `filter=(genre in ('…'))`, and `multiple: false` gives radio buttons with `eq`.

**Relationship:**

```json
{
	"fieldName": "r_<rel>_c_<parent>ERC",
	"include": true,
	"itemKey": "externalReferenceCode",
	"itemLabel": "<labelField>",
	"label_i18n": {
		"en_US": "Author"
	},
	"multiple": true,
	"preselectedValues": "[]",
	"restApplication": "/c/<parent-plural>",
	"restEndpoint": "/",
	"restSchema": "<ParentName>",
	"source": "/o/c/<parent-plural>/",
	"sourceType": "API_REST_APPLICATION"
}
```

This is the UI's shape: filter on the `…ERC` twin with `itemKey: "externalReferenceCode"`. The numeric FK with `itemKey: "id"` also works, since the id is sent quoted.

- **`include: false` is not honored.** Visitors have a runtime Exclude toggle instead.
- **`preselectedValues`** had no clear effect in testing; one form made the Filter button disappear.

**Static parameters.** `additionalAPIURLParameters` (e.g. `filter=genre eq 'sf'`) is AND-merged with the visitor's own filters. On 2026.Q3.2 it was static: no native dynamic token mapping, so the known workaround was a `globalJS` CET that rewrites `fetch` for URLs carrying `liferaydds=true` (verified on 2026.Q2.11). **On 2026.Q3.5 the parameters take tokens, with `LPD-38564` off** (verified live): `FDSAPIURLBuilder._interpolateTokens` replaces, in order, the fragment's `apiURLTokenMappings` (behind `LPD-38564`: release, off by default, on 2026.Q3; hidden dev on 2026.Q1 and 2026.Q2, so only `feature.flag.LPD-38564=true` enables it there), then whatever an `FDSAPIURLResolver` registered for `restApplication + "/" + restSchema` returns, then the defaults `{scopeKey}`, `{siteId}`, `{userExternalReferenceCode}` and `{userId}`. An unresolved `{token}` is still sent literally and matches nothing.

- **A per-user filter on a custom object needs a resolver**, since no default token names the user's account or anything beyond the user. An OSGi component does it: `@Component(property = "fds.rest.application.key=/c/<plural>/<ObjectName>", service = FDSAPIURLResolver.class)`, `getSchema()` returning the object name, and `resolve(String baseURL, HttpServletRequest)` returning `StringUtil.replace(baseURL, "{myToken}", value)` (commerce's `AccountEntryFDSAPIURLResolver` is the pattern). Verified with `filter=r_<rel>_accountEntryERC eq '{agentAccountERC}'` filled with the signed-in user's person account ERC: each user's page fetched only their own rows.
- **Read the result off the rendered page:** the fragment's props carry `"apiURL"` (the endpoint plus `nestedFields`) and, as a separate key present only when non-empty, `"additionalAPIURLParameters"` with the tokens already replaced; the browser fetches `apiURL + "&" + additionalAPIURLParameters`. `curl` the page as the user and read both keys; the rows themselves are fetched client-side.

### Default Sort (`dataSetToDataSetSorts`)

```json
{
	"default": true,
	"fieldName": "publicationDate",
	"label_i18n": {
		"en_US": "Newest"
	},
	"orderType": "desc"
}
```

**A `Sorts` entry without `label` or `label_i18n` breaks the whole fragment** ("An unexpected error occurred."), with `NullPointerException: … "labelI18n" is null` at `CustomFDSSerializer.serializeSorts` in the log. Either field is enough, since the other is filled in. The initial request then carries `sort=<field>:<orderType>`. The UI always writes `orderType: "asc"`; set `desc` over the API.

### Actions (`dataSetToDataSetActions`)

```json
{
	"label_i18n": {
		"en_US": "View"
	},
	"permissionKey": "",
	"target": "link",
	"type": "item",
	"url": "/web/<site>/c_<objectname>/{friendlyUrlPath}"
}
```

- **`type`:** `item` (per row; a lone row action shows as a button) or `creation` (a primary button in the management bar).
- **`target`:** `link`, `modal` (an iframe modal), `sidePanel`, and the untested `async` and `headless`.
- **`url` substitutes top-level row fields:** `{id}`, `{friendlyUrlPath}`, `{externalReferenceCode}`, `{title}`.
- **Link to an entry's default Display Page Template with `/web/<site>/c_<objectname>/{friendlyUrlPath}`.** `<objectname>` is the object `name`, lowercased and singular, which is the default `friendlyURLSeparator`. It needs no environment-specific id. The alternative `/web/<site>/e/<dpt-url>/<classNameId>/{id}` also works, but `classNameId` differs per environment. There is no ERC-based DPT URL on 2026.Q3.2.
- **`permissionKey`** (e.g. `"UPDATE"`) hides the action from users lacking that permission on the entry (verified with a VIEW-only user).
- **`confirmationMessage_i18n`** shows a native `window.confirm`.
- **Order fields.** `itemActionsOrder` / `creationActionsOrder` stay empty.

### List and Card Views

`dataSetToDataSetListSections` / `dataSetToDataSetCardsSections` entries are `{"fieldName": "<field>", "name": "title" | "description" | "image" | "symbol"}`, and dot notation works. With two or more views the fragment shows a view switcher. `defaultVisualizationMode` chooses the first view; empty or unknown falls back to Cards when a Cards view exists. `rendererName` on a list section does **not** apply a custom renderer.

### Custom Renderers and Filters (CETs)

- **Cell renderer** (`fdsCellRenderer` CET): the column takes `"renderer": "LXC:<cet-key>"`, `"rendererType": "clientExtension"`. The module's default export is `({value, itemData}) => HTMLElement`, where `itemData` is the whole row, so a cell can depend on sibling fields.
- **Filter** (`fdsFilter` CET): a `dataSetToDataSetClientExtensionFilters` entry `{"clientExtensionEntryERC": "LXC:<cet-key>", "fieldName": "<field no other filter uses>", "label_i18n": {…}}`. The default export is an object: `descriptionBuilder(selectedData)` builds the chip, `htmlElementBuilder({filter, setFilter})` builds the panel, and `oDataQueryBuilder(selectedData)` returns the OData, e.g. an `OR` across fields that built-in filters cannot express. **The `LXC:` prefix is mandatory.** Without it the entry saves, and the render logs `No frontend data set filter client extension exists with the external reference code …`.
- **Escaping.** Escape user values in hand-built OData (`replace(/'/g, "''")`). Fetch option data with `Liferay.Util.fetch`.

### Place the Data Set Fragment

The fragment references the Data Set by ERC. On a release carrying the **`LPD-102157`** fix (on master, and in a 2026.Q3 hotfix; check the release notes for your patch), the selection is a real item reference that the site-initializer tree and the live API can author, and that survives export. Without the fix, only the Page Editor can select the Data Set.

In the tree (`layouts/<page>/page-definition.json`):

```json
{
	"definition": {
		"fragment": {
			"key": "com.liferay.frontend.data.set.fragment.web.internal.fragment.renderer.FDSFragmentRenderer"
		},
		"fragmentConfig": {
			"itemSelector": {
				"className": "[$OBJECT_DEFINITION_CLASS_NAME:DataSet$]",
				"externalReferenceCode": "<DS-ERC>"
			}
		}
	},
	"type": "Fragment"
}
```

- **On 2026.Q1 the renderer is still `FDSAdminFragmentRenderer`** (below), and the key above does not exist. Place the Data Set with the Page Editor there; this was not verified on 2026.Q1.
- **The key is the renderer class, with no `siteKey`.** `className` is the **`L_DATA_SET`** definition's class name (`DataSet` is its `name`), never the backing object's.
- **A 2025.Q3 export names the old renderer, `com.liferay.frontend.data.set.admin.web.internal.fragment.renderer.FDSAdminFragmentRenderer`, with an `itemSelector` carrying `classNameId`, `classPK`, `itemType` and `title`.** That renderer does not exist on 2026.Q3.5, and a fragment element whose renderer is missing is dropped with its children and no log line. Rewrite it to the key above with the two-key `itemSelector`; the token resolves to the instance's own `#XXXX` suffix in `editablevalues`. The Data Set is looked up by ERC on every render, so it may be created after the page is imported, and renaming its ERC takes the fragment off the page with no re-import.
- **Over the live whole-page PUT,** the same reference goes under `fragmentConfigurationFieldValues.itemSelector = {"type": "Item", "value": {"itemExternalReference": {"className": …, "externalReferenceCode": …}}}`, with `defaultFragmentKey` = the renderer class.
- **Verify on the rendered page**, not by a GET.

**Order of operations:** the definition applies live, so edits show on reload with no redeploy. Placement is page composition: a redeploy of the initializer re-applies the page, and live edits made on that page are overwritten. The Data Set only has to exist at render time: the fragment renders nothing until it exists and has at least one view.

## Visibility

**Only the source entries matter.** Guest needs `VIEW` on the object's entries (`rules/guest-access.md`) and **nothing** on the Data Set definition itself. Without it, the fragment shows the standard "No Results Found", and REST returns `200` with no items.

## Moving a Data Set Between Environments

`POST /data-sets/export-batch` exports the definition **without its children**. Instead:

1. `GET /o/data-set-admin/data-sets/by-external-reference-code/<DS-ERC>?nestedFields=dataSetToDataSetTableSections,dataSetToDataSetActions,dataSetToDataSetCardsSections,dataSetToDataSetClientExtensionFilters,dataSetToDataSetDateFilters,dataSetToDataSetListSections,dataSetToDataSetSelectionFilters,dataSetToDataSetSorts`.

1. Strip what is this instance's, not the definition's: `id`, `dateCreated`, `dateModified`, `creator`, the HATEOAS `actions`, `friendlyUrlPath` and `friendlyUrlPath_i18n`, `objectEntryFolderId` and `objectEntryFolderExternalReferenceCode`, `keywords`, `taxonomyCategoryBriefs`, `status`, `scopeId`, and on each child the `r_…_l_dataSetERC` / `r_…_l_dataSetId` back-references and the `dataSetToDataSet<Child>ERC` parent field. Keep every `externalReferenceCode` (and `label_i18n`, `defaultLanguageId`). All of these are in a 2025.Q3.2 GET body (verified against the database's `L_<companyId>_DataSet*` tables).
   - **An older instance's `tableSectionsOrder` holds numeric child ids**, not ERCs: a UI-built set on 2025.Q3.2 stored `47861,47857,…` (the `TableSections` entry ids). Map each id to its child's ERC before posting; the target takes ERCs there (§2, "Ordering").

1. `POST` it to `/data-sets` (or `/data-sets/batch`) as one nested item.

**Child ERCs are unique per instance.** Re-importing on the same instance with unchanged child ERCs fails as a whole (`No ObjectEntry exists with the key {r_dataSetToDataSet…_l_dataSetId=…}`), so rename them for a copy.

**Deleting a Data Set is permanent and cascades to its children** (no recycle bin). The same ERC can be re-created afterwards.

## Not Verified

- The `async` and `headless` action targets.
- `preselectedValues`.
- Using the `dataset.everything` OAuth scope for real calls: it is accepted at CET deploy time, but no call was made with it.
- The dynamic-parameter `fetch` workaround on 2026.Q3.

## Success Signal

- The page shows real rows for the intended audience, and each column shows the expected value.
- Filters emit the expected OData and narrow the rows.
- The default sort is applied on load, and actions open the right target.
- `GET …?nestedFields=…` lists every child under the chosen ERC.