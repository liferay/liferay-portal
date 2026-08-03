---

description: Create and configure site pages, navigation menus, display page templates, page templates, and SEO settings via the Headless Admin Site API. Use when the user asks to create a page, set up navigation, build a display page template for an object, or configure page SEO. Requires feature flag LPD-35443. Maps to "Mastering Liferay Pages and Navigation".
name: manage-pages

---

# Manage Pages

Create and wire site pages, navigation menus, and page templates. The reliable path is to **author pages in the site initializer** (`layouts/`) and provision the site from it; live page creation through the Headless Admin Site API is kept only as a fallback. See `rules/site-initializer-format.md` for the source of truth and reprovision model.

## When to Invoke

- "Create a page", "add a home page", "set up the site navigation"
- "Build a display page template for Books"
- "Set the page title, description, and URL"
- Called by `build-site` during the page composition phase

## Prerequisites

Feature flag `LPD-35443` must be on for the public layout API. Verify and enable via `feature-flags` skill.

## Page Types

Consult `rules/page-types.md` for the full table. Common types:

| Type | Use | API |
| --- | --- | --- |
| Content Page | Fragment based layout | headless-admin-site |
| Widget Page | Portlet based (legacy) | headless-admin-site |
| Display Page Template | Object/content type landing page | headless-admin-site |
| Page Template | Reusable page blueprint | headless-admin-site |

## Authoring Pages in the Site Initializer (Primary)

Pages live in the initializer tree and come into being when the initializer is triggered. This avoids the unreliable live page creation API and keeps the page definitions in source control.

### Write `page.json`

Create `site-initializer/layouts/<NN-page-name>/page.json`. The `NN` prefix controls creation order. Set the type, name, friendly URL, and any per role permissions (see the `page.json` format in `rules/site-initializer-format.md`):

```json
{
	"friendlyURL": "/<page-url-slug>",
	"hidden": false,
	"name": "<Page Name>",
	"name_i18n": {
		"en_US": "<Page Name>"
	},
	"private": false,
	"type": "Content"
}
```

### Write `page-definition.json`

Compose the layout in `site-initializer/layouts/<NN-page-name>/page-definition.json`. Reference each fragment by its `key` and `siteKey` (the fragments must exist under `site-initializer/fragments/group/<collection-key>/fragments/`):

```json
{
	"pageElement": {
		"pageElements": [
			{
				"definition": {
					"fragment": {
						"key": "<fragment-name>",
						"siteKey": "[$GROUP_KEY$]"
					}
				},
				"type": "Fragment"
			}
		],
		"type": "Root"
	},
	"version": "1.0"
}
```

The `key` is the fragment's directory name under the collection's `fragments/` folder (e.g. a folder `fragments/group/myco/fragments/hero/` → `"key": "hero"`). The `siteKey` token `[$GROUP_KEY$]` resolves to the current site at provision time and tells the importer the fragment lives in this site's collection (omit `siteKey` only for built in fragments, which use a combined key like `"key": "BASIC_COMPONENT-paragraph"`). **Do not** use `collectionExternalReferenceCode`/`fragmentEntryKey` here — the site-initializer importer reads `key`/`siteKey` and silently drops any fragment element it cannot resolve, leaving the page blank.

### Navigation and SEO

Set sitewide navigation and theme in `site-initializer/layout-set/public/metadata.json`. Per page SEO metadata lives alongside the page in `page.json`.

### Provision

Trigger (or, for `layouts/` changes on an existing site, reprovision) the site — delete and recreate it from the initializer. See `rules/site-initializer-format.md` for the commands. Because the source tree is current and object data is company scoped, runtime entries survive the reprovision.

## Display Object Data on a Page

To show a list of object entries on a page, use a **server side Collection Display**, not a client side `fetch`. A browser `fetch` to `/o/c/<pluralLabel>` carries the visitor's cookies, so the headless object API evaluates the request as the Guest user and typically returns **0 items** (Guest lacks entry level view permission).

> **The Collection element does not sidestep that permission** — it evaluates entry level permissions as the visiting user too, so an object backed listing renders **empty for anonymous visitors** while looking correct to you. Grant the object to Guest in `resource-permissions.json` before assuming the page works. See `rules/guest-access.md`.

Compose it in `page-definition.json` (see the `Collection` / `CollectionItem` element types in `rules/page-types.md`):

1. **`Collection` element** — bind it to the object definition:

	```json
	{
		"definition": {
			"collectionConfig": {
				"collectionReference": {
					"className": "[$OBJECT_DEFINITION_CLASS_NAME:<Name>$]"
				},
				"collectionType": "CollectionProvider"
			},
			"numberOfItems": 20,
			"numberOfColumns": 1,
			"paginationType": "Numeric"
		},
		"type": "Collection",
		"pageElements": [ /* one CollectionItem, below */ ]
	}
	```

1. **`CollectionItem` element** — nest one inside the Collection; its child `pageElements` are the per entry template (typically a custom card fragment from `scaffold-fragment`).

1. **Map fragment fields to object fields** — on each editable fragment field, point the mapping at the object field and source it from the current collection item:

	```json
	"fragmentFields": [
		{
			"id": "<editable-id>",
			"value": {
				"text": {
					"mapping": {
						"fieldKey": "ObjectField_<field>",
						"itemReference": {"contextSource": "CollectionItem"}
					}
				}
			}
		}
	]
	```

### Setting Literal Text (Not Mapped)

The same `fragmentFields` array also sets a fixed value, using `value_i18n` where a mapped field would use `mapping`. Use it whenever one section fragment appears on several pages — without it every instance renders the fragment's default text, so a shared heading silently reads "Upcoming Events" on the register page:

```json
"fragmentFields": [
	{
		"id": "<editable-id>",
		"value": {
			"fragmentLink": {},
			"text": {
				"value_i18n": {
					"en_US": "<literal text>"
				}
			}
		}
	}
]
```

### Mapping Limits — Denormalize Into Display Fields

Field mapping renders the raw stored value and cannot transform it. In particular:

- **`DateTime` values cannot be formatted** through mapping (no date format option).
- **Related object fields cannot be mapped** — you cannot reach across a relationship to display a field from the related entry.
- **Aggregates over a relationship cannot be mapped** — a count of related entries (registrations for an event, items in an order) is not a field, so it cannot be mapped.
- **Per record presentation values** (e.g. a color that varies by entry) cannot be driven from mapping.

The workaround is to **denormalize**: add a plain `Text` display field on the object and populate it with the presentation ready value, then map that field. For example add `timeLabel` (a preformatted time string instead of the raw `DateTime`) or `speakerName` (the related person's name copied onto the entry), and map `ObjectField_timeLabel` / `ObjectField_speakerName`.

Denormalizing is also the way to publish a number derived from private records. To show remaining capacity, put the count on the public object — preferably an `Aggregation` field (`function: COUNT` over the relationship), which is computed server side and ignores entry level permissions, so the visitor reads the count without the underlying rows ever being granted to Guest. A counter maintained by an object action is the fallback where no aggregate function fits; note Groovy actions are disabled by default (`rules/object-actions-catalog.md`). See `skills/manage-objects/SKILL.md` for both.

An `Aggregation` field is still a computed field, so the "cannot be mapped" limit above is the reason it may not reach a Collection fragment through mapping — reading it over REST from fragment JavaScript is the verified path.

**Do not compute these in fragment JavaScript instead** — the browser call runs as the visitor and silently returns 0 rows, so the number is confidently wrong rather than absent. See `rules/guest-access.md`.

## Verify

Applies to **both** paths — a page authored in the initializer tree and one created over the live API.

```bash
curl --head --silent --url "http://localhost:${PORT}/web/<site-friendly-url>/<page-url-slug>"
```

Expect `200 OK`. Listing pages over REST (`GET /sites/<site-erc>/site-pages`) needs flag `LPD-35443`; without it the call returns `400 UnsupportedOperationException`, which says nothing about whether the pages exist.

**`200 OK` is not evidence the page works.** A page whose Collection returned no rows, whose fragment reused another page's placeholder text, or whose mapping silently failed all return 200 with fragments present. Verify content, and verify it as the audience:

1. **Probe without credentials.** `curl` with no `--user` and no cookie jar is exactly a Guest request, and it is the only cheap way to see what a visitor sees. Signed in verification hides every permission gap in this skill — see `rules/guest-access.md`.

	```bash
	curl --silent --url "http://localhost:${PORT}/web/<site>/<page>" > /tmp/page.html
	```

1. **Assert real data, not markup presence.** Grep for a value that only exists in the database (an actual event name) *and* confirm the fragment's placeholder strings are absent — placeholders still present means the mapping did not resolve.

1. **Check each page's own text.** Reusing a section fragment across pages carries its default text with it; a heading that reads "Upcoming Events" on the register page is a 200 with wrong content. Set per page values with a literal `fragmentFields` entry rather than relying on the fragment default.

1. **Open it in a browser before declaring success.** Anything driven by JavaScript — a computed value, a populated `select` — is invisible to `curl`, which executes none of it. Confirm signed out too: sign in state changes what client side calls return.

## Fallback: Live API

Use the Headless Admin Site API (`/o/headless-admin-site/v1.0`) only when reprovisioning is undesirable and the change is small. It is unreliable for page **creation** in particular. When the MCP server is available, prefer MCP tool calls over raw curl.

Everything below has a tree equivalent that is the verified path. Reach for the live API only for the last row:

| To do this | Author it here instead |
| --- | --- |
| Create a page | `layouts/<NN-name>/page.json` + `page-definition.json` |
| Place fragments on a page | `page-definition.json` — see "Authoring Pages" above |
| Navigation menu | `site-navigation-menus.json` |
| Display page template | `layout-page-templates/display-page-templates/<name>/` |
| SEO settings | No tree equivalent is documented — use the live API below |

### Corrections to Any Live API Example

Published examples for this module — including older ones in this pack's history — predate the current DTOs. Verify against the spec (`get-openapi` MCP tool, or `GET /o/headless-admin-site/v1.0/openapi.json`) and apply these:

- **Sites are addressed by external reference code**, not numeric ID, and subresources nest under `/sites/<site-erc>/…`. There is no top level `/site-pages/{id}`.
- **The DTOs use `pageSpecifications`**, not the initializer's `pageDefinition`.
- **Localized fields are `*_i18n` maps** (`name_i18n`, `friendlyUrlPath_i18n`). `SitePage` has no `title` field.
- **`DisplayPageTemplate` binds through `contentTypeReference`**, not flat `contentType`/`contentSubtype`. For a Liferay Object the class name is `com.liferay.object.model.ObjectEntry` and the subtype is the object definition's ERC.
- **A custom fragment reference uses `BasicFragment` + `fragmentReferenceType`** over the live API, and `key` + `siteKey` in the initializer tree. Neither accepts `collectionExternalReferenceCode`/`fragmentEntryKey` — that form is silently dropped and the section renders blank. See "Custom Fragment Placement via the Headless API" below.
- **Three distinct `type` vocabularies.** Live API: `ContentPage` / `WidgetPage` / `LinkToURLPage` / `EmbeddedPage` / `PageSetPage` / `LinkToPagePage`. Initializer `page.json`: `Content` / `Portlet` / `URL` / `Embedded`. `headless-delivery` uses a separate `pageType`.
- **Page element operations require flag `LPD-74328`**, and public layout access requires `LPD-35443`.

### Ensure the Site Exists

```bash
curl \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-site/v1.0/sites" \
	--user "test@liferay.com:test" \
	| jq '[.items[] | {externalReferenceCode, name, friendlyUrlPath}]'
```

Save the `externalReferenceCode` as `<site-erc>`. A `siteInitializer` CET that declares `siteExternalReferenceCode` provisions its own site on deploy, so creating one by hand is usually a sign the initializer is not wired up.

### Configure SEO Settings

The one operation with no documented initializer equivalent — no `page.json` in the portal source sets these fields. Patch the page after it exists:

```bash
curl \
	--data '{
		"customMetaTags": [
			{"key": "description", "value": "<meta description>"}
		],
		"htmlTitle": {"en_US": "<SEO Title>"},
		"seoSettings": {
			"canonicalURL": {"en_US": "<canonical-url>"},
			"description_i18n": {"en_US": "<meta description>"},
			"robots": "index,follow",
			"title_i18n": {"en_US": "<SEO Title>"}
		}
	}' \
	--header "Content-Type: application/json" \
	--request PATCH \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-site/v1.0/sites/<site-erc>/site-pages/<page-erc>" \
	--user "test@liferay.com:test"
```

Because this is applied live, it is **lost on reprovision**. Record it as a known manual step, or confirm whether `page.json` accepts SEO fields on your version and document the result here.

## Live API Patterns and Gotchas

### Schema Discovery Before Write Operations

Never guess field names or payload shapes from memory or GET responses. Before any POST or PATCH:

1. Read the write endpoint's schema from the OpenAPI spec (via the `get-openapi` MCP tool or by fetching `/o/headless-admin-site/v1.0/openapi.yaml`). The OpenAPI surface is the source of truth — Liferay's hosted documentation lags. GET response structure does NOT equal POST/PATCH request structure; do not infer the request shape from a GET.

1. Discover fragment keys from an existing page definition — there is no dedicated endpoint:

       GET /headless-delivery/v1.0/sites/{siteId}/site-pages/{friendlyURL}?nestedFields=pageDefinition

1. For built in fragment collection keys not present on any existing page, do NOT decompile JARs. Search the [liferay-portal GitHub repository](https://github.com/liferay/liferay-portal) for `fragment.json` under `modules/apps/fragment/` to extract `fragmentEntryKey` and collection keys.

The spec itself has gaps — several fields required at runtime are marked optional. See the gotchas below.

### Site Creation Can 500 With Ghost Success

`POST /headless-admin-site/v1.0/sites` may return `500 Internal Server Error` due to a background NPE or permission error in the site initializer — but the site is still created. Always verify via GET before retrying. Retrying a successful creation produces a duplicate site.

### Page Specification Workflow (Draft and Publish)

Modern Content Pages use a paired specification model: every page has a main spec and a linked draft spec (`draftContentPageSpecificationExternalReferenceCode`, suffixed `-draft`). The UI Publish button overwrites the main spec with whatever is in the draft.

- **Always write page-element changes to the draft spec**, never the main spec. Anything written directly to the main spec is silently overwritten when the next Publish runs.
- Resolve the draft ERC from a page GET, then target it for `page-elements` operations:

      GET /headless-admin-site/v1.0/sites/{siteId}/site-pages/{friendlyURL}
      → use draftContentPageSpecificationExternalReferenceCode for subsequent POST/PUT

- Publish with `PUT /o/headless-admin-site/v1.0/sites/{siteERC}/site-pages/{pageERC}`. The `POST /page-specifications/{specERC}/publish` endpoint is a no-op stub — it returns 204 and does nothing.
- `pageSpecifications[]` on the PUT must include both the published spec (`status: "Approved"`) and the draft spec; the published spec references the draft via `draftContentPageSpecificationExternalReferenceCode`.

Required fields missing from the OpenAPI spec (omitting any of these causes a server error):

- `pageSettings: {"type": "ContentPageSettings"}` at the top level
- `pageExperiences[]` with at least one entry per spec
- `name_i18n` on each `pageExperience`

ERC suffixes autogenerated on page creation: draft spec is `{pageERC}-draft`, published experience is `{pageERC}-default`, draft experience is `{pageERC}-draft-default`.

A successful PUT response code is not a reliable success signal — verify by issuing an HTTP GET on the page URL.

### `headless-admin-site` Schema Gotchas

**`type` vs `pageType` — field names differ by API.** `headless-delivery` uses `pageType` (plain string). `headless-admin-site` uses `type` (enum: `ContentPage`, `WidgetPage`, etc.). Using `type` against the delivery API returns `400 - The property "type" is not defined in SitePage`.

**`FragmentReference` uses a nonstandard discriminator.** Every other polymorphic schema in `headless-admin-site` uses `type`. `FragmentReference` is the exception — it uses `fragmentReferenceType`. Using `"type": "DefaultFragmentReference"` returns `400 InvalidTypeIdException: missing type id property 'fragmentReferenceType'`.

**`position` is required despite the spec marking it optional.** Omitting `position` from a page-element POST returns `500 NullPointerException`. Always include `"position": 0` (or the intended 0 based index).

**`FormContainerConfig.numberOfSteps` is required despite the spec marking it optional.** Same pattern — omitting it returns `500 NullPointerException`. Always include `"numberOfSteps": 1` for a single step form.

**FormFragment elements nest via `parentExternalReferenceCode`.** The page-elements POST endpoint is flat — all elements hit the same endpoint regardless of nesting depth. Parent child relationships are expressed through `parentExternalReferenceCode`, not nested objects. Key fields for `FormFragment` children:

- `fieldKey` — maps to an Object field name (e.g. `"email"`) or `"formButton"` for submit
- `fragmentInstance.fragmentReference.defaultFragmentKey` — the INPUTS fragment key
- `label_i18n` — localized label
- `markAsRequired` — boolean
- `showLabel` — boolean

### Custom Fragment Placement via the Headless API

When placing custom fragments via `PUT /sites/{siteERC}/site-pages/{pageERC}`, inline the fragment's `html` and `css` on the `fragmentInstance`. Both the draft and published specs must carry the same inlined content — otherwise the UI's next Publish reverts the affected sections to empty divs.

Discriminators for fragment placement:

- Page element type: `"type": "BasicFragment"` (not `"Fragment"`)
- Fragment reference: keyed by `"fragmentReferenceType"` (not `"type"`)
- Custom fragments: `"fragmentReferenceType": "FragmentItemExternalReference"` with `"externalReferenceCode": "<fragmentEntryKey>"` — the fragment entry key IS the ERC
- Out of the box fragments: `"fragmentReferenceType": "DefaultFragmentReference"` with `defaultFragmentKey` — inlining not required

### Fragment Management API Gap

Headless fragment CRUD endpoints are not consistently available across DXP versions — verify before assuming an endpoint exists (the `get-openapi` MCP tool, or fetch `GET /o/headless-admin-site/v1.0/openapi.json` with curl). If no fragment import endpoint appears in the live API surface, import via the portal UI instead. For programmatic placement on pages, use the discriminators above.

## Placing a Client Extension Widget on a Page

When the user wants to add a deployed Custom Element CET to a page:

1. Confirm the CET is verified active first (see `deploy-and-verify`).

1. Ask the user which page to add it to — do not assume.

1. If the widget is already on the page, ask whether to replace or add a new one.

1. Place the widget via MCP, the Headless Admin Site API, or the Liferay UI depending on what the environment supports. See `mcp-server` for MCP availability per DXP version.