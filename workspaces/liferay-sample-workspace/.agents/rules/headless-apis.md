# Headless APIs

> **Before authoring:** Load `mcp-server` first when MCP is available; prefer it over raw curl for content, page, and object work.

Key Liferay REST modules, their base URIs, primary resources, and OAuth scopes. All paths resolve relative to `http://localhost:${PORT}`. Use Basic auth (`test@liferay.com:test`) for the curl examples on this page and in the skills. The OAuth scope strings noted per module are for `oAuthApplicationHeadlessServer` blocks when scaffolding microservice CETs — see `rules/oauth-scopes.md` for the full scaffolding reference.

The tables below list the common endpoints per module — they are not exhaustive. To confirm an exact path or find an endpoint not listed here, use the `get-openapi` MCP tool (see `skills/mcp-server/SKILL.md`), or fetch `GET /o/<module>/v1.0/openapi.json` directly. Base URIs, feature flag gates, and OAuth scopes are *not* discoverable from the specs — rely on this card for those. The per module `Required flag` notes below map each module to its gating flag; see `rules/feature-flags-catalog.md` for each flag's default and status.

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

**Style books are behind a flag.** `StyleBookResourceImpl` (`/sites/{siteExternalReferenceCode}/style-books`) answers `400 UnsupportedOperationException` on every call unless its flag is on: `LPD-57283` (release, off by default) on 2026.Q3 and later, the hidden `LPD-56718` on 2026.Q1 and 2026.Q2. Without the flag, create style books in the site initializer tree (`style-books/`). A page's `settings.styleBookItemExternalReference` pointing at a missing style book is accepted with `200` and only a `WARN [LogUtil]` "Optional reference generated…", the same tolerance as for master pages (see `skills/manage-pages/SKILL.md`).

**Required flag:** `LPD-38869` (on by default) for the private layout API. Composing page elements and page specifications additionally requires `LPD-74328`.

**OAuth scope:** `Liferay.Headless.Admin.Site.everything`

## headless-admin-content

**Base URI:** `/o/headless-admin-content/v1.0`

| Resource | Method | Path |
| --- | --- | --- |
| Create web content | POST | `/sites/{siteId}/structured-contents` |
| List web content | GET | `/sites/{siteId}/structured-contents` |

On 2026.Q3.2 this module holds only three resources — `StructuredContent`, `DisplayPageTemplate`, `PageDefinition` (verified against the bundle's `*ResourceImpl` classes). It has **no** content structure, style book, or fragment endpoint:

- **Style books** are declared in `headless-admin-site` but not usable over REST (above); author them in the tree.
- **Fragments** are in `headless-admin-fragment` (below).
- **Content structures** are read only in `headless-delivery` (`GET /sites/{siteId}/content-structures`). Create them through Data Engine: `POST /o/data-engine/v2.0/sites/{siteId}/data-definitions/by-content-type/{contentType}`.

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

**OAuth scope:** `Liferay.Object.Admin.REST.everything` for the admin endpoints above (definitions, fields, etc.). The dynamic `/o/c/<plural>` entry endpoints take one scope per object, `c_<name>.everything[.read|.write]`. `Liferay.Headless.Object.everything` does not grant them. See `rules/oauth-scopes.md`.

## headless-admin-fragment

**Base URI:** `/o/headless-admin-fragment/v1.0`

Its own module — **not** part of `headless-admin-site`, which is why fragment endpoints look absent when you search the page API's spec.

| Resource | Method | Path |
| --- | --- | --- |
| List / create fragment sets | GET, POST | `/sites/{siteExternalReferenceCode}/fragment-sets` |
| Get / update / delete a set | GET, PUT, DELETE | `/sites/{siteExternalReferenceCode}/fragment-sets/{setExternalReferenceCode}` |
| List / create fragments in a set | GET, POST | `/sites/{siteExternalReferenceCode}/fragment-sets/{setExternalReferenceCode}/fragments` |
| Get / replace / delete a fragment | GET, PUT, DELETE | `/sites/{siteExternalReferenceCode}/fragments/{fragmentExternalReferenceCode}` |

Fragment code is in `fragmentVersions[]` (`html`, `css`, `js`, `configuration`, `status`). Fragments are keyed by **ERC** — a generated UUID for fragments imported by an initializer, not the fragment key — so list the set to resolve it.

**Required flag:** `LPD-39244`.

**OAuth scope:** `Liferay.Headless.Admin.Fragment.everything` — verify against the running instance before relying on it in a CET.

> **Editing a fragment here does not change any page that already places it** — Liferay copies fragment code into each page's fragment instance. Reprovision to apply fragment changes. See `skills/scaffold-fragment/SKILL.md` → "The Live Fragment API", and note that one fragment declaring `"type": "section"` makes the whole set's listing return `400`.

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

## headless-admin-taxonomy

**Base URI:** `/o/headless-admin-taxonomy/v1.0`

| Resource | Method | Path |
| --- | --- | --- |
| List vocabularies (site) | GET | `/sites/{siteId}/taxonomy-vocabularies` |
| List vocabularies (asset library) | GET | `/asset-libraries/{assetLibraryId}/taxonomy-vocabularies` |
| Upsert vocabulary by ERC | PUT | `/asset-libraries/{id}/taxonomy-vocabularies/by-external-reference-code/{erc}` |
| List categories | GET | `/taxonomy-vocabularies/{vocabularyId}/taxonomy-categories` |
| Upsert category by ERC | PUT | `/taxonomy-vocabularies/{vocabularyId}/taxonomy-categories/by-external-reference-code/{erc}` |

Two verified quirks: `?flatten=true` on the category list returns the **whole hierarchy** (all nesting levels) in one paginated list — far cheaper than recursive parent walks. And the standalone category **by-ERC GET 404s for asset-library-scoped categories** — resolve those by listing the vocabulary's categories and filtering client-side. A third: **`PUT …/taxonomy-vocabularies/by-external-reference-code/{erc}` needs `multiValued` on update.** The first PUT creates the vocabulary without it, but a repeat PUT on the existing one answers a bare `500` (`NullPointerException … TaxonomyVocabulary.getMultiValued()`); send `"multiValued": true` every time (verified on 2026.Q2.7). Sibling categories must differ by **name**, not just ERC: a second sibling with an existing name answers `409`.

**OAuth scope:** `Liferay.Headless.Admin.Taxonomy.everything`

## headless-asset-library and headless-object (CMS)

**Base URIs:** `/o/headless-asset-library/v1.0` — Spaces (`/asset-libraries`, `type: "Space"`) and their `/connected-sites/{siteERC}`, which is what lets a Site render a Space's content. `/o/headless-object/v1.0` — entry folders at `/scopes/{siteId | spaceERC}/object-entry-folders`. CMS entries are depot-scoped `/o/c/<plural>/scopes/{siteId | spaceERC}`, and the unscoped collection returns `409`. The CMS browser's search (`/o/search/v1.0/search`) needs `emptySearch=true` for a filter-only query. Needs `LPD-17564` on 2026.Q1 and 2026.Q2; no flag from 2026.Q3.

**A library's `friendlyURL` cannot be set over REST on 2026.Q3.5**, although the DTO has the field and the resource tries. `PUT` (create) and `PATCH` with `friendlyURL` both answer `500 StaleObjectStateException` on the library's `GroupImpl`, and nothing changes (the create rolls back): `_updateFriendlyURL` writes a group that `addDepotEntry`/`updateDepotEntry` has just modified. `PUT /o/headless-admin-site/v1.0/sites/{libraryERC}` does change it, but it rewrites the group's `type_` from `5` (depot) to a site type (`SiteResourceImpl._getType` never returns `TYPE_DEPOT`) and joins the caller to the group, so never use it on a real library. The friendly URL matters because file URLs are `/documents/d/<group friendly URL>/<friendlyUrlPath>`, and a default library's is `/asset-library-<depotEntryId>`, which changes on every rebuild. Once set (any value, including another instance's old `/asset-library-<id>`), the `/documents/d/…` URLs resolve by it and `updateDepotEntry` keeps it. Verified live. **On 2026.Q2.7 the DTO has no `friendlyURL` field**.

## data-set-admin (Data Sets)

**Base URI:** `/o/data-set-admin` — **no version segment** (`/v1.0/…` returns `404`), and an OpenAPI spec **per resource** (`/o/data-set-admin/data-sets/openapi.json`, …). Children are reachable only nested: `/data-sets/by-external-reference-code/{erc}/dataSetToDataSet<Child>`, while the root child collections return `409`. Flag `LPS-164563`. Procedure: `skills/manage-data-sets/SKILL.md`.

## headless-admin-workflow

**Base URI:** `/o/headless-admin-workflow/v1.0`

| Resource | Method | Path |
| --- | --- | --- |
| Create workflow definition | POST | `/workflow-definitions` |
| List workflow definitions | GET | `/workflow-definitions` |
| List workflow instances | GET | `/workflow-instances` |
| Transition workflow task | POST | `/workflow-tasks/{id}/change-transition` |

**OAuth scope:** `Liferay.Headless.Admin.Workflow.everything`

## search-experiences-rest (Blueprints and Elements)

**Base URI:** `/o/search-experiences-rest/v1.0`. No feature flag. Verified on 2026.Q3.5.

| Resource | Method | Path |
| --- | --- | --- |
| List / create | GET, POST | `/sxp-blueprints`, `/sxp-elements` |
| Get / upsert by ERC | GET, PUT | `/sxp-blueprints/by-external-reference-code/{erc}`, `/sxp-elements/by-external-reference-code/{erc}` |
| Delete | DELETE | `/sxp-blueprints/{id}`, `/sxp-elements/{id}` |

- **PUT by ERC creates or updates, and an identical PUT still bumps `version`** (1.0 → 1.1). Compare first.
- **Every `elementDefinition` is URL-decoded on write.** The resource passes an element's definition, and each blueprint instance's `sxpElement.elementDefinition`, through `URLDecoder.decode` (`DecodeSXPUtil`), because the UI sends it URL-encoded. A literal `+` is therefore stored as a space: a rescore script's `') + 1` was stored as `')   1`, which no longer parses. Send every string of a definition with `%` as `%25` and `+` as `%2B`. An instance's `configurationEntry`, the clause that actually runs, is not decoded. **An element's create is decoded twice:** `putSXPElementByExternalReferenceCode` decodes the definition, then hands an element that does not exist yet to `postSXPElement`, which decodes it again (`SXPElementResourceImpl.java:347`, `:238`), so encode a created element twice (`%2B` → `%252B`) and an updated one once. Measured on 2026.Q3.5: a create encoded once stored `')   1`, a create encoded twice and an update encoded once `') + 1`. A blueprint decodes once on both paths (`SXPBlueprintResourceImpl.java:226`, `:353`). **The same on 2026.Q2.7**: a create encoded once stored `')   1`, a create encoded twice and an update encoded once `') + 1`, an update encoded twice a literal `%2B`.
- **Read-back shapes:** a blueprint GET carries `title` (a string), not `title_i18n`; an element definition comes back with rendered `labelLocalized` and `helpTextLocalized` keys in its `uiConfiguration` fields. Leave those out of a comparison.
- A site initializer cannot create blueprints (`rules/site-initializer-format.md`), and DXP installs its own `rescore-by-text-embedding` element only in test mode, so ship a copy of the element too.

### Applying a Blueprint to a Headless Search

Verified on 2026.Q2.7, from source and live as Guest:

- **`POST /o/search/v1.0/search` takes a Blueprint as the body attribute `search.experiences.blueprint.external.reference.code`.** Only attributes named `search.experiences.*`, `search.empty.search` or `status` reach the search context (`SearchResultResourceImpl._isAllowedSearchContextAttribute`); any other is dropped.
- **An ERC that names no Blueprint is ignored without an error.** The search runs as if no Blueprint were given: unscoped across the company, every searchable type. A client configured with an ERC the instance lacks therefore looks like it works, with the wrong scope.
- **A REST search has no page, so `${context.scope_group_id}` and `${context.scope_group_external_reference_code}` stay unresolved** unless the request sends the site as `search.experiences.scope.group.id` (a number). `ContextSXPParameterContributor` reads that attribute; the Search portlets set it from the page. With it, a `terms` filter on `groupExternalReferenceCode` keeps one site, and its Space too when the Space's ERC is derived from the site's by a naming convention of your own (for example `["${context.scope_group_external_reference_code}", "${context.scope_group_external_reference_code}_SPACE"]`; a Blueprint cannot look up which Spaces are connected to a site).
- **A query entry's `condition` picks clauses per request.** `{"exists": {"parameterName": "context.scope_group_external_reference_code"}}` on one entry and `{"not": {"exists": {...}}}` on another give "this site" when the caller names one and a wider fallback when it does not. `parameterName` takes the bare name, no `${}`.

### Searching the Text Inside an Object Entry's File

Verified on 2026.Q3.5, by source and measured with CMS Basic Documents and a custom file type:

- **An object entry's search document holds its file's name, not its text.** `ObjectEntryModelDocumentContributor` stores an Attachment field's file name (`_getFileName`) and an `extension` keyword, and extracts nothing. The text is on the file's own document, written by `DLFileEntryModelDocumentContributor`: `entryClassName` `DLFileEntry`, `content_<locale>`, `hidden=true` (an object attachment is in a hidden folder), the entry as its `classNameId`/`classPK`, and `relatedEntry=true`.
- **So a blueprint over the object's class alone never matches a word inside the file**, only titles, tags, file names and other fields. Adding `DLFileEntry` to its types is not enough: `DLFileEntryModelPreFilterContributor.addHiddenFilter` adds `hidden=false` unless `includeAttachments` is set, and REST search never sets it (only the Search Bar widget's "Include Attachments", the suggestion contributors and the Wiki and Message Boards screens do). `applyIndexerClauses: false` drops the keyword query as well: every query matched everything.
- **What works over REST:** `DLFileEntry` in `searchableAssetTypes`, and `"clauseContributorsExcludes": ["com.liferay.document.library.internal.search.spi.model.query.contributor.DLFileEntryModelPreFilterContributor"]` in the blueprint's `generalConfiguration` (that contributor is not mandatory; DL's MIME-type and workflow-status filters go with it). A word inside a PDF or an `.srt` then finds the file **for the entry's readers only**: the entry's grants and shares decided, although the file's own document listed the User role (measured with a Coach-only transcript shared with one agent).
- **The entry and its file are separate hits**, so one document can be listed twice. Both name the same stored file (the entry's `embedded.file.link.href` and the file's `embedded.contentUrl` share `/documents/{groupId}/{folderId}/{name}/{uuid}`), which a client can fold on. The total counts both.

### Semantic Search (Text Embeddings)

Verified on 2026.Q3.5 with OpenAI `text-embedding-3-small`, 768 dimensions:

- **Configuration:** company-scoped `com.liferay.portal.search.configuration.SemanticSearchConfiguration`. `openai` needs no feature flag (`TextEmbeddingRetrieverImpl._supportedProviders`); the screen is hidden only on a free-tier license. There is no REST write (`portal-search-rest` only validates a provider configuration).
- **A `.config` file applies it live:** `<pid>.scoped~<name>.config` with `companyWebId="<webId>"`, `textEmbeddingsEnabled=B"true"`, `textEmbeddingCacheTimeout=I"604800"` and `textEmbeddingProviderConfigurationJSONs=[ "<escaped JSON>" ]`, the JSON holding `attributes` (`apiKey`, `model`, `dimensions`, `maxCharacterCount`, `modelTimeout`, `textTruncationStrategy`), `embeddingVectorDimensions`, `languageIds`, `modelClassNames` and `providerName`. No restart: the `text_embedding_<dims>_*` fields are dynamic templates already. **FileInstall rewrites the file once applied**, replacing `companyWebId` with the resolved `companyId=L"…"`, so compare it ignoring that line.
- **What embeds:** an object entry its indexed fields as `name: value` text (for a file type, its file name and title); a DL file `title. text`, only when text was extracted (an image embeds nothing). `modelClassNames` takes an object's `com.liferay.object.model.ObjectDefinition#XXXX` class name, which differs per database for a custom object.
- **Cost:** one provider call per document indexed, and **every blueprint search embeds its keywords** (`MLSXPParameterContributor`), with or without a rescore element: one call per distinct keyword string, cached in memory for `textEmbeddingCacheTimeout`; a restart clears the cache.
- **Reindexing one class** is Search Admin's portlet action: POST, signed in, to the `reindexURL` its Index Actions page carries, with `_<portletId>_cmd=reindex`, `className`, `companyIds`, `executionMode=regular` and `blocking=true`.

Verified on 2026.Q2.7 with a self-hosted txtai (`BAAI/bge-small-en-v1.5`, 384 dimensions):

- **Every provider but `openai` is ignored unless the beta flag `LPS-122920` is on** (`vertex-ai` needs `LPD-31789`), and saving a configuration naming it raises no error. Set the flag in `portal-ext.properties` and check it by effect (`Liferay.FeatureFlags` on a rendered page): the retriever keeps the value its `FeatureFlagListener` hears last, which at registration is the system company's.
- **Validate a provider from inside Liferay:** `POST /o/search/v1.0/embeddings/validate-provider-configuration` with the provider JSON answers `{"expectedDimensions": 384}`, or an `errorMessage`. It calls the provider the way indexing does, so it proves the flag and the network path together.
- **txtai:** `GET <hostAddress>/transform?text=<URL-encoded>`, which txtai's `/transform` answers with a flat array. Its vector for bge-small was identical to sentence-transformers' own (CLS pooling, normalized).
- **A failing provider is silent.** `TextEmbeddingProviderWebCacheItem` catches the exception and logs it at DEBUG, so a document indexed while the provider is down gets **no vector** and the reindex reports success (17 of 17 documents, no WARN, measured). Keyword search is unaffected; the keywords simply get no embedding. Check that the provider is healthy before a reindex, and compare vector counts in the index afterwards.
- **Embeddings are cached in memory by text** for `textEmbeddingCacheTimeout`, so a second reindex within that time made 0 provider calls. A restart clears the cache.
- **Lower `index.interval` before a reindex with embeddings.** A reindex writes `index.interval` documents per bulk request (`Indexer.DEFAULT_INTERVAL`, default 10,000). Each vector adds its floats written as strings, about 8 KB at 384 dimensions, and an 8,845-document bulk passed Elasticsearch's `http.max_content_length` (100 MB). Elasticsearch cut the connection (`IOException: Broken pipe` in `ElasticsearchIndexWriter`), the bulk was lost, and the **sync** reindex then deleted every document it had not rewritten, so the class left the index entirely. `index.interval=500` (a portal property, read at startup) avoided it. In sync mode, a restart before the reindex finishes keeps the old documents, because the stale-document delete runs last.
- **Guest gets no `DLFileEntry` hit for an object entry's attachment** even with `DLFileEntryModelPreFilterContributor` excluded (an administrator gets it). The file's document lists only the roles with individual VIEW on the file (User, Owner, the space administrator), and a Guest who reads the entries through a company-scope grant on the object has no grant on `DLFileEntry`, so the search permission filter drops the hit. A signed-in user holds User, which the file lists, so should pass (not measured here).
- **A file hit carries no site:** its `embedded` has `siteId` null, no `systemProperties`, and `assetLibraryKey` is the Space's **display name**, not its ERC.
- **Pages (`Layout`) never get a vector: no embedding contributor exists for them** (only Journal, Blogs, DLFileEntry, Wiki, Message Boards, Knowledge Base and object entries have one). So a rescore by text embedding sinks every page below every embedded hit. A module can add one:
  - write a `ModelDocumentContributor<Layout>` with `indexer.class.name=com.liferay.portal.kernel.model.Layout`;
  - have it call the public `TextEmbeddingDocumentContributor.contribute(document, languageId, layout, text)`;
  - add `com.liferay.portal.kernel.model.Layout` to the provider's `modelClassNames`. The helper checks that list, and that the layout's status is approved; `getLanguageIds(layout)` comes back empty otherwise, so check it before doing any work.

  **Do not embed `content_<languageId>`.** `LayoutModelDocumentContributor` renders the fragments in `HashMap` order of their `fragmentEntryLinkId` (`LayoutPageTemplateStructureRenderUtil`), not reading order, so the first `maxCharacterCount` characters are an arbitrary sample of the page. Walk the page's `LayoutStructure` depth first instead, and render each fragment with `FragmentRendererController` in `FragmentEntryLinkConstants.INDEX` mode, inside `LayoutServiceContextHelper.getServiceContextAutoCloseable(layout)` (which renders as Guest). That cost about 2 ms a page over 821 pages. Also:
  - the master is not part of the page's structure, and no `LayoutCrawler` is registered, so the text has no header or footer;
  - form fields render as "Text (Read Only)" placeholders;
  - Collections render nothing.

  Verified: a cold reindex of 2,183 pages, 772 of them embedded, took 44.5 s.
- **An indexer's `ModelDocumentContributor`s run by `service.ranking`, highest first, then in registration order.** `ServiceTrackerListFactory.open` gets no comparator, so `Collections.reverseOrder()` over `ServiceReference` applies. Verified live: a contributor at ranking +100 ran before Liferay's own and did not see its fields; one at ranking 0, registered later, did.
- **A component property with one value reaches `@Activate` as a `String`, not a `String[]`, and `GetterUtil.getStringValues(Object)` returns an empty array for it.** It converts only a `String[]`, so a single-valued list property silently reads as empty. Wrap a lone `String` in an array first.
- **Updating an existing `.config` file:** FileInstall writes it back without its final newline once Configuration Admin applies it. A script that compares the file with what it generates must ignore that newline, or it rewrites the file every run.
- **Counting txtai calls in uvicorn's access log:** uvicorn shortens a long request line to `"GET /transform... (984 more chars truncated) ..."`. So count lines matching `"GET /transform`, not `/transform?`, which misses every long text.

## Common Parameters

| Parameter | Values | Notes |
| --- | --- | --- |
| `page` | integer | 1 based page number |
| `pageSize` | integer | Items per page (default 20). A larger value is **cut without an error**: `/o/c/<plural>` returned 500 items for `pageSize=1000` (2026.Q2.7). Page by `totalCount` |
| `filter` | OData expression | e.g. `title eq 'Hello'` |
| `sort` | `fieldName:asc` or `fieldName:desc` | |
| `fields` | comma separated field names | Projection to reduce response size |
| `restrictFields` | comma separated field names | Exclude from response |
| `aggregationTerms` | field name(s) | Facet bucket counts on `/o/c/<plural>`; see the bucket cap below |

**URL-encode ERC path segments.** `by-external-reference-code/{erc}` endpoints do not unquote leniently — an ERC containing anything beyond unreserved characters must be percent-encoded by the caller (in Python, `requests.utils.quote(erc, safe='')`), or the call 404s and reads as "entry missing".

### Pagination Clamps — Never Loop Until Empty

An out-of-range `page` is **clamped to the last valid page** — the response repeats the last page's items instead of returning an empty list (verified: 9 items at `pageSize=5` → `page=999` returns the same 4 items as `page=2`). A `while items: page += 1` loop that expects a terminating empty page **never terminates**. Page by `totalCount` instead: loop `ceil(totalCount / pageSize)` pages, or break when `page * pageSize >= totalCount` or when a page returns fewer than `pageSize` items.

### `aggregationTerms` Buckets Are Capped at `pageSize`

`GET /o/c/<plural>?aggregationTerms=<field>` returns **at most `pageSize` facet buckets** — the facet size is tied to the entry page size, not independent. `pageSize=1` silently yields exactly one bucket: the alphabetically first term (a chart built on it collapses to a single bar with no error; `totalCount` stays correct because it is DB backed while the facet is index backed). Request a generous `pageSize` (e.g. `100`) and add `&fields=id` to keep the entry payload tiny.

### OData Filters on Relationship FK Fields — Quote the Number, or Filter by ERC

Filtering `/o/c/<plural>` on a relationship FK (`r_<relationshipName>_<parentPK>Id`) with an unquoted integer throws `400 InvalidFilterException: Incompatible types` — the FK columns are string typed in the entity model. Single-quote the value (`r_account_accountEntryId eq '37057'`) or, better, filter on the ERC twin (`r_account_accountEntryERC eq '<erc>'`). In a microservice that hand-rolls the filter this surfaces as an unlogged 4xx/502 downstream, so audit any `eq " +` string concatenation on relationship id filters.

## Never Recommend JSONWS

The legacy `/api/jsonws` surface is deprecated and frequently disabled outright (404) on modern bundles — never reach for it, even as a "one-time" workaround. When a headless REST endpoint doesn't cover an operation, the acceptable answers are: a different headless module (check the OpenAPI specs), a documented manual UI step, or an OSGi component — and name the gap explicitly rather than papering over it with JSONWS. The single exception is an authoring-time `classNameId` lookup for display page URLs, which nothing else exposes: `skills/manage-pages/SKILL.md` → "Links, Forms, and Menus in the Tree".

## Error Codes

| HTTP Status | Meaning |
| --- | --- |
| 400 | Validation error; read the `title` field in the problem detail response. Can also signal an ERC collision ("This external reference code is already in use") — recover by GETting the existing entry by ERC instead of retrying the create |
| 401 | Not authenticated; check credentials or OAuth token |
| 403 | Authenticated but forbidden; scope too narrow or permissions missing |
| 404 | Resource not found or feature flag off; check flag state |
| 409 | Conflict; typically duplicate name or ERC |
| 500 | Server error; check `bundles/logs/liferay.<date>.log` |

## References

- `rules/oauth-scopes.md` — full scope string reference for these modules.
- `rules/feature-flags-catalog.md` — defaults for the flags gating these APIs.
- `skills/mcp-server/SKILL.md` — the `get-openapi` tool for confirming exact paths.
- Headless APIs: `https://learn.liferay.com/w/dxp/integration/headless-apis`