---

paths:
  - "**/layout-page-templates/**"
  - "**/layouts/**"

---

# Page Types

> **Before authoring:** Load `manage-pages` before authoring a page, and `scaffold-fragment` before authoring a fragment placed on one.

Reference for the `manage-pages` skill. Covers all page and template types available in Liferay DXP and which APIs handle each.

## Renderable Page Types

These types appear as pages that visitors can navigate to.

| Type | Key | Use Case | API |
| --- | --- | --- | --- |
| Content Page | `content` | Fragment based layout; the default for new pages | headless-admin-site: `POST /sites/{siteExternalReferenceCode}/site-pages` with `"type": "ContentPage"` |
| Widget Page | `portlet` | Legacy portlet layout; compatibility with old portlet apps | headless-admin-site: `POST /sites/{siteExternalReferenceCode}/site-pages` with `"type": "WidgetPage"` |
| Link to URL | `url` | Redirect node in the navigation hierarchy | headless-admin-site: `POST /sites/{siteExternalReferenceCode}/site-pages` with `"type": "LinkToURLPage"` |
| Embedded | `embedded` | Iframe pointing to an external URL | headless-admin-site: `POST /sites/{siteExternalReferenceCode}/site-pages` with `"type": "EmbeddedPage"` |
| Full Page Application | `full_page_application` | Single portlet occupying the entire page | headless-admin-site: `POST /sites/{siteExternalReferenceCode}/site-pages` (no distinct `type` enum value — verify; remaining enum values are `PageSetPage`, `LinkToPagePage`) |

> **Three distinct "type" vocabularies — do not mix them.** The `Key` column above is the internal layout-type key. The **`headless-admin-site` `SitePage.type` enum** is `ContentPage` / `WidgetPage` / `LinkToURLPage` / `EmbeddedPage` / `PageSetPage` / `LinkToPagePage` (shown in the API column). The **site-initializer `page.json` `type`** uses `Content` / `Portlet` / `URL` / `Embedded`. And **`headless-delivery`** uses a separate `pageType` field.

## Reusable Template Types

These are blueprints; they are not directly accessible as URLs.

| Type | Purpose | API |
| --- | --- | --- |
| Page Template | Reusable fragment layout for new pages | headless-admin-site: `POST /sites/{id}/page-templates` |
| Master Page Template | Persistent header/footer wrapping all assigned pages | headless-admin-site: `POST /sites/{id}/master-pages` |
| Display Page Template | Per entry landing page for a content type or object | headless-admin-site: `POST /sites/{id}/display-page-templates` |

## Content Page Structure

A Content Page `pageDefinition` is a tree of `pageElements`. Each element has a `type` and a `definition`.

| Element Type | Description |
| --- | --- |
| `Root` | Top level container; every page has exactly one |
| `Section` | Layout row, sets columns and spacing |
| `Row` | Grid row; `definition` carries `numberOfColumns`, `modulesPerRow`, `gutters`, `verticalAlignment` (+ optional `rowViewports` for responsive collapse) |
| `Column` | Column inside a `Row`; `definition.size` values sum to 12 (+ optional `columnViewports`, e.g. `size: 12` on `landscapeMobile` to stack) |
| `Fragment` | A deployed fragment, referenced by `fragment.key` (its directory name) + `fragment.siteKey` (`[$GROUP_KEY$]`); built in fragments use a combined `key` like `BASIC_COMPONENT-paragraph` with no `siteKey` |
| `Widget` | A portlet embedded in a Content Page |
| `Collection` | A server side collection display. `definition.collectionConfig` holds ONLY `collectionReference` + `collectionType`; for a Liferay Object set `collectionType` to `CollectionProvider` and `collectionReference.className` to `[$OBJECT_DEFINITION_CLASS_NAME:<Name>$]`. The display config — `numberOfItems`, `numberOfItemsPerPage`, `numberOfPages`, `numberOfColumns`, `displayAllItems`, `displayAllPages`, `showAllItems`, `paginationType`, `listStyle`, `listItemStyle` — are **siblings of `collectionConfig` at the `definition` level**, and the importer NPEs (silent template rollback) when they are missing. `gutters` is Row-only — invalid here (see `rules/site-initializer-format.md`) |
| `CollectionItem` | The per entry template nested inside a `Collection` (`definition.collectionItemConfig`). Its child `pageElements` are the fragments rendered once per record. Map a fragment field to an object field with `fragmentFields[].value.text.mapping.fieldKey = "ObjectField_<field>"` and `itemReference.contextSource = "CollectionItem"`. A link maps via `fragmentFields[].value.fragmentLink.value.href.mapping` with `fieldKey = "displayPageURL"` (**bare** — display page URL is not namespaced); content fields in CollectionItem mappings ARE namespaced by the item class simple name (e.g. `CPDefinition_name`) |

> **`ColumnDefinition` is NOT a valid element type — it aborts the whole import.** It is internal layout *export* vocabulary. The importer deserializes into `com.liferay.headless.delivery.dto.v1_0.PageElement$Type`, whose complete, source-verified enum (2026.Q2.7) is: `Collection`, `CollectionItem`, `Column`, `DropZone`, `Form`, `FormRelationship`, `FormStep`, `FormStepContainer`, `Fragment`, `FragmentDropZone`, `Root`, `Row`, `Section`, `Widget`. Any other value throws Jackson `ValueInstantiationException: Invalid enum value` in `LayoutsImporterImpl._importPageElement`, which aborts the **entire site initialization** (the CET still logs STARTED, but the site is not built). Multi-column layout is `Section → Row → Column → (inner Section) → Fragment`.

Minimal `pageDefinition` for an empty content page:

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

## Display Page Template Binding

The content type determines which entity type the template renders. (On the current `headless-admin-site` API these values are carried inside a `contentTypeReference` object on the `DisplayPageTemplate` DTO rather than as flat `contentType`/`contentSubtype` fields — verify against the OpenAPI spec (`get-openapi` MCP tool, or `GET /o/headless-admin-site/v1.0/openapi.json`). The class name and subtype values below are unchanged.)

| Entity | `contentType` | `contentSubtype` |
| --- | --- | --- |
| Blog Post | `com.liferay.blogs.model.BlogsEntry` | (empty) |
| Web Content | `com.liferay.journal.model.JournalArticle` | content structure ERC |
| Document | `com.liferay.portal.kernel.repository.model.FileEntry` | (empty) |
| Liferay Object entry | the definition's **suffixed** class name, `com.liferay.object.model.ObjectDefinition#<code>`, as `contentTypeReference.className` | (none) — this is what the site-initializer importer produces and what renders entries (verified 2026.Q3.2). `ObjectEntry` plus a `subTypeExternalReference` is stored verbatim by the API but was never shown to bind |

Only one display page template per `contentType` + `contentSubtype` combination can be marked default. The default template is used when a content item's URL is visited without an explicit template in the path.

A display page template created from a tree gets a **generated ERC** (`LayoutsImporterImpl._addLayoutPageTemplateEntry` passes none). It stays the same across re-fires, which match the template by name, but differs on every new database. No page-definition token resolves it (`LAYOUT_PAGE_TEMPLATE_ENTRY_ID:` exists only inside `resource-permissions.json`). Anything in a tree that names a specific DPT by ERC therefore only works once that database's ERC has been written into the tree. A Form's success result naming an unknown DPT ERC falls back to the default DPT without a warning (`skills/manage-form-containers/SKILL.md`; verified 2026.Q3.5).

A display page template's key and its layout's friendly URL come from its **name**: `"Event Session (Speaker)"` gives the key `event-session-(speaker)` and the URL `/event-session-speaker-`, with a trailing dash where the parentheses were (verified 2026.Q3.5). A specific template's URL, `/e/<that URL>/<classNameId>/<id or ERC>`, therefore cannot be typed from the name; read it from `GET /o/headless-admin-site/v1.0/sites/{siteERC}/display-page-templates` (`friendlyUrlPath_i18n`, keyed `en-US` with a hyphen in this module; that listing carries no `defaultTemplate` either, so read the default flag from the database or the UI).

Two scope facts (verified 2026.Q2.7):

- **OOTB `AccountEntry` has no display page support** — only custom Objects (and the content types above) can back a DPT. Build an account landing page as a regular Content Page reading `/o/headless-admin-user` from fragment JS instead.
- **A company-scoped Object CAN back a site DPT.** The entry page renders for authenticated users holding VIEW; an unauthenticated visitor gets 404. Note `curl --user` against `/web/...` friendly URLs still renders as guest — verify DPT visibility in a real browser session, not curl.

### Object Display Page URL Scheme

An object entry's display page URL is `/web/<site-friendly-url>/<separator>/[asset-library-<groupId>/]<entryFriendlyUrl>`:

- `<separator>` comes from the object definition's `friendlyURLSeparator` — by default `c_<lowercased object name>` (`/web/<site>/c_<name>/<slug>`, verified 2026.Q3.2); set to `l`, entries carry no friendly URL path and resolve as `/web/<site>/l/<entryId>`. Resolve it at runtime from `GET /o/object-admin/v1.0/object-definitions` rather than hardcoding — likewise the site path from `GET /o/headless-admin-site/v1.0/sites/{id}` (`friendlyUrlPath`).
- The `asset-library-…` segment appears only for **depot-scoped** entries, and it is the library group's **friendly URL** without its slash, `asset-library-<depotEntryId>` by default (the library's `id` in `headless-asset-library`, not its `siteId`, which is the group id). Verified on 2026.Q3.5 with a CMS External Video: `/web/guest/cmsexternalvideo/asset-library-37161/<slug>` rendered and `asset-library-37162` (the group id) answered `404`. Once the library's friendly URL is pinned to another value, that value is the segment.
- Prefer the entry's friendly URL path (`entryFriendlyUrl`/`friendlyUrlPath`) and fall back to the numeric id only when it is empty — the id form works but is not SEO-stable.
- **The id form needs no library segment.** With a pinned library friendly URL, `/<separator>/<entryId>` and `/<separator>/<library friendly URL>/<entryId>` both answered `302` to the canonical `/<separator>/<library friendly URL>/<slug>`, while `/<separator>/asset-library-<depotEntryId>/<slug>` answered `404` (2026.Q2.7, as Guest on the site's virtual host). So a client that knows an entry's separator and id, but not its library's friendly URL, can still link it.
- On the instance's default site, a mapped `displayPageURL` renders without `/web/<site>` for a signed-in user (`/cmsblog/asset-library-<id>/<slug>`) and with it for a Guest; both open the page (2026.Q3.5).

### Rendering Related Child Entries on a DPT

A `Collection` element on a DPT can bind to the relationship-scoped provider, which auto-filters to the current entry (the DPT passes it as `relatedItem` — no manual filter). Set `collectionConfig.collectionReference.className` to the provider **key**:

```
com.liferay.object.internal.info.collection.provider.OneToManyObjectRelationshipRelatedInfoCollectionProvider_[$OBJECT_DEFINITION_CLASS_NAME:<ParentName>$]_<relationshipName>
```

- The middle segment is the PARENT object's className, which for a custom object is the **random** form `com.liferay.object.model.ObjectDefinition#<4charId>` — always use the `[$OBJECT_DEFINITION_CLASS_NAME:<Name>$]` token; a hardcoded `#XXXX` id breaks on the next install.
- `<relationshipName>` is the ObjectRelationship `name`, not its ERC. `collectionType` stays `"CollectionProvider"`.
- The provider auto-registers only when **neither side is an unmodifiable system object** — custom→custom works, custom→OOTB `User` gets no provider. However, mapping the FK scalar itself (`ObjectField_r_<rel>_<pk>`) renders the related entry's **title** server-side, including a `User`'s name.
- CollectionItem field-key prefixes: custom field → `ObjectField_<name>` (picklists render their label); built-in entry field → `ObjectEntry_<name>` (e.g. `ObjectEntry_createDate`).
- **An Attachment field's own mapping keys are not portable; map an information template instead.** A mapped `#downloadURL`/`#previewURL`/`#fileURL` hardcodes the numeric `objectFieldId` (`ObjectField_<id>#previewURL`), no site-initializer token resolves it, and `ObjectField_<name>` renders the file's id as an image's `src`. The ids differ per database even for OOTB definitions: the CMS's are created on the first boot, not carried by the LDM seed (`CMSBlog.coverImage` was 34707 on one 2026.Q3.5 build and 34807 on the next). **The declarative fix (verified on 2026.Q3.5, Collection and DPT, anonymous included):**
  - ship an information template in the tree, `ddm-templates/<dir>/ddm-template.json`: `{"className": "com.liferay.template.model.TemplateEntry", "ddmTemplateKey": "<KEY>", "infoItemClassName": "[$OBJECT_DEFINITION_CLASS_NAME:<Name>$]", "infoItemKey": "", "name": "<Template Name>", "resourceClassName": "com.liferay.template.model.TemplateEntry"}`, beside `ddm-template.ftl`;
  - make the FreeMarker print the URL by finding the field's key by pattern, since the template sees every field by name and unique id: `<#list .data_model?keys as key><#if key?starts_with("ObjectField_") && key?ends_with("#previewURL")>${.data_model[key].getData()}<#break></#if></#list>` (one line, no trailing newline, which would land in the `src`; the pattern needs the object to have one Attachment field);
  - map the image editable to `"fieldKey": "ddmTemplate_[$TEMPLATE_ENTRY_ID:<Template Name>$]"`. A template is an info field of the item (`ddmTemplate_<templateEntryId>`, `TemplateInfoItemFieldSetProviderImpl`), and an image editable takes its string output as the `src`.

## Feature Flags for Page APIs

| Flag | Default | Unlocks |
| --- | --- | --- |
| `LPD-35443` | off | Page Management API: every `site-pages` and `master-pages` call (`400` without it; beta on 2026.Q2, release on 2026.Q3) |
| `LPD-38869` | on | Private layout REST access |
| `LPD-39244` | off | Fragment and page composition REST API (absent on 2026.Q1, hidden dev on 2026.Q2, beta on 2026.Q3) |
| `LPD-74328` | off | Page element / page-specification creation and update |

This table is the page-API subset; `rules/feature-flags-catalog.md` is canonical for each flag's default and status. Enable required flags via `feature-flags` skill before calling the page APIs.

## Navigation Menu Item Types

| `type` | What It Links To |
| --- | --- |
| `layout` | A page in the current site (by UUID or friendly URL) |
| `url` | An arbitrary absolute URL |
| `node` | A nonclickable label grouping child items |
| `asset-publisher` | Dynamic list from Asset Publisher portlet |

## References

- `skills/manage-pages/SKILL.md` — the primary consumer of this card.
- `rules/headless-apis.md` — headless-admin-site paths for these page types.
- `rules/feature-flags-catalog.md` — defaults for the page-API flags.
- `rules/site-initializer-format.md` — `page.json` and `page-definition.json` formats.
- Site building: `https://learn.liferay.com/w/dxp/sites`