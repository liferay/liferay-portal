# Page Types

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
| `ColumnDefinition` | Column within a section (width, offset) |
| `Fragment` | A deployed fragment, referenced by `fragment.key` (its directory name) + `fragment.siteKey` (`[$GROUP_KEY$]`); built in fragments use a combined `key` like `BASIC_COMPONENT-paragraph` with no `siteKey` |
| `Widget` | A portlet embedded in a Content Page |
| `Row` | Grid row inside a section |
| `Collection` | A server side collection display bound to a data source via `definition.collectionConfig`. For a Liferay Object, set `collectionConfig.collectionType` to `CollectionProvider` and `collectionConfig.collectionReference.className` to the token `[$OBJECT_DEFINITION_CLASS_NAME:<Name>$]` (`<Name>` = the object definition name). Controls paging/columns via `numberOfItems`, `numberOfColumns`, `paginationType`, etc. |
| `CollectionItem` | The per entry template nested inside a `Collection` (`definition.collectionItemConfig`). Its child `pageElements` are the fragments rendered once per record. Map a fragment field to an object field with `fragmentFields[].value.text.mapping.fieldKey = "ObjectField_<field>"` and `itemReference.contextSource = "CollectionItem"` |

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
| Liferay Object entry | `com.liferay.object.model.ObjectEntry` | object definition ERC |

Only one display page template per `contentType` + `contentSubtype` combination can be marked default. The default template is used when a content item's URL is visited without an explicit template in the path.

## Feature Flags for Page APIs

| Flag | Default | Unlocks |
| --- | --- | --- |
| `LPD-35443` | off | Public layout (page) REST API via headless-admin-site |
| `LPD-38869` | on | Private layout REST access |
| `LPD-39244` | off | Fragment and page composition REST API |
| `LPD-74328` | off | Page element / page-specification creation and update |

Enable required flags via `feature-flags` skill before calling the page APIs.

## Navigation Menu Item Types

| `type` | What It Links To |
| --- | --- |
| `layout` | A page in the current site (by UUID or friendly URL) |
| `url` | An arbitrary absolute URL |
| `node` | A nonclickable label grouping child items |
| `asset-publisher` | Dynamic list from Asset Publisher portlet |