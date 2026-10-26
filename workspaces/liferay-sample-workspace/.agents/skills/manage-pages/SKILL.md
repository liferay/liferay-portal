---

description: Create and configure site pages, navigation menus, display page templates, page templates, and SEO settings via the Headless Admin Site API. Use when the user asks to create a page, set up navigation, build a display page template for an object, or configure page SEO.
name: manage-pages

---

# Manage Pages

Create and wire site pages, navigation menus, and page templates. The reliable path is to **author pages in the site initializer** (`layouts/`) and provision the site from it; live page creation through the Headless Admin Site API is kept only as a fallback. See `rules/site-initializer-format.md` for the source of truth and reprovision model.

## When to Invoke

- "Create a page", "add a home page", "set up the site navigation"
- "Build a display page template for Books"
- "Set the page title, description, and URL"
- Called by `build-site` during the page composition phase

## Page Types

Consult `rules/page-types.md` for the full table. Common types:

| Type | Use | API |
| --- | --- | --- |
| Content Page | Fragment based layout | headless-admin-site |
| Widget Page | Portlet based (legacy) | headless-admin-site |
| Display Page Template | Object/content type landing page | headless-admin-site |
| Page Template | Reusable page blueprint | headless-admin-site |

## Workflow

### Author Pages in the Site Initializer (Primary)

Pages live in the initializer tree and come into being when the initializer is triggered. This avoids the unreliable live page creation API and keeps the page definitions in source control.

#### Write `page.json`

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

#### Write `page-definition.json`

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

#### Navigation and SEO

Set sitewide navigation and theme in `site-initializer/layout-set/public/metadata.json`. Per page SEO metadata lives alongside the page in `page.json`.

#### Provision

Trigger (or, for `layouts/` changes on an existing site, reprovision) the site — delete and recreate it from the initializer. See `rules/site-initializer-format.md` for the commands. Because the source tree is current and object data is company scoped, runtime entries survive the reprovision.

### Display Object Data on a Page

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

	**Give a Collection its columns per viewport.** On 2026.Q2.7 a `numberOfColumns: 1` Collection with no `collectionViewports` renders its items `col-lg-12 col-sm-1 col-1`: a twelfth of the width below 768px, one word per line. Set them in the tree: `"collectionViewports": [{"collectionViewportDefinition": {"numberOfColumns": 1}, "id": "landscapeMobile"}, …]` for `landscapeMobile`, `portraitMobile` and `tablet` gives `col-12` (verified by render).

	**A paginated Collection shows at most `numberOfPages × numberOfItemsPerPage` entries** unless `"displayAllPages": true`, which pages through all of them; `numberOfItems` caps only an unpaginated one (`displayAllItems` lifts that). Showing every page costs nothing extra per request: the Collection fetches one page either way and takes the total from the same result, and the numeric bar prints "Showing 1 to 10 of N entries." server-side (`CollectionPaginationUtil`, 2026.Q2.7).

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

#### Setting Literal Text (Not Mapped)

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

##### A Literal Override Replaces the Editable's Inner Markup

The value is written as the editable region's **content**, so any tag inside that region is gone. A `rich-text` region authored as

```html
<div class="section-heading__title" data-lfr-editable-id="title" data-lfr-editable-type="rich-text">
	<h2>Section title</h2>
</div>
```

renders on an overridden placement as `<div class="section-heading__title" …>Coming up next</div>` — verified on 2026.Q2. The `<h2>` is not preserved.

Two consequences, both quiet:

- **CSS targeting the inner tag stops matching.** A rule written `.section-heading__title h2` styles the fragment's default and nothing else, so the placements you actually ship lose their sizing. Style the wrapper class itself, or list both.
- **Site wide `h1`…`h4` rules skip these headings entirely** — there is no heading element left to match. This is the trap for the master page heading rule in `skills/theme-and-design/SKILL.md`: the selector must name the title wrapper classes alongside the tags, or the overridden headings — the ones on every real page — silently keep the body font.

It also means the page has no `<h2>` where the author intended one. Make the heading tag the editable element itself, `<h2 data-lfr-editable-id="title" data-lfr-editable-type="text">`, and the override replaces only its text, keeping the `h2`. Verified on 2026.Q2 (`scaffold-fragment` → `index.html`).

#### Per Instance Configuration — `fragmentConfig`

`fragmentFields` sets *editable regions*. A sibling key, `fragmentConfig`, sets the fragment's **`configuration.json` values** for that one placement, and it is what lets a single fragment serve several pages with different behavior instead of forking it:

```json
{
	"definition": {
		"fragment": {
			"key": "event-list",
			"siteKey": "[$GROUP_KEY$]"
		},
		"fragmentConfig": {
			"layout": "grid",
			"maxItems": "3",
			"showCapacity": false,
			"upcomingOnly": true
		}
	},
	"type": "Fragment"
}
```

`fragmentConfig` and `fragmentFields` are independent — use either, both, or neither on a placement.

Keys are the `name` values from `configuration.json`, and each value must match how that field is declared there — a `checkbox` takes a real JSON boolean, and a `select` whose `validValues` are strings takes the string (so `"3"`, not `3`). Verified in a site initializer `page-definition.json` on 2026.Q2: the same `event-list` fragment rendered `maxItems="3" showCapacity=false layout="grid"` on one page and `200 / true / rows` on another. Whether a numeric `select` also accepts a bare number was not tested — declaring `validValues` as strings and passing strings is the form known to work.

**This is cheap to verify without a browser.** Have the fragment publish each flag as a data attribute (`scaffold-fragment` requires this anyway, since block directives do not run) and the values land in the server rendered HTML, where `curl` can assert them:

```bash
grep --extended-regexp --only-matching '<div[^>]*event-list-wrapper[^>]*' /tmp/page.html | tr ' ' '\n' | grep '^data-'
```

Match the class **inside** the tag as above rather than anchoring on `class="…"`. A wrapper that also carries style classes (`class="conference-hero-wrapper conference-hero--dark …"`) does not match `class="conference-hero-wrapper"`, and the grep then returns nothing — indistinguishable from a config that never applied.

Assert on the page whose values **differ** from the `configuration.json` defaults. Those defaults are what a placement renders with no override, so a page configured to match them proves nothing about whether `fragmentConfig` was read at all — and defaults are usually chosen to suit the most common placement, which is exactly the page you would reach for first.

A **Data Set** is placed as a `Fragment` whose `key` is the renderer class `com.liferay.frontend.data.set.fragment.web.internal.fragment.renderer.FDSFragmentRenderer` (2026.Q2 and later; on 2026.Q1 the class is `com.liferay.frontend.data.set.admin.web.internal.fragment.renderer.FDSAdminFragmentRenderer`), with `fragmentConfig.itemSelector` pointing at the Data Set's ERC. It needs the `LPD-102157` fix (2026.Q3.2). Shape and verification: `skills/manage-data-sets/SKILL.md` → "Place the Data Set Fragment".

#### Links, Forms, and Menus in the Tree

Tree keys that the live DTO names do not predict:

- **Link to an entry's display page:** map the link's `href` with `"fieldKey": "displayPageURL"` and `"itemReference": {"contextSource": "CollectionItem"}`. It resolves to the **default** DPT for the type (see `rules/page-types.md`). A link to a specific *non-default* DPT (`LayoutPageTemplateEntry__ERC__<dpt-erc>`) works through the live API, but the tree importer drops it silently in every shape tried on q3.2. Make the target the default DPT, or build that URL in the fragment's JS.
- **A `Form` bound to another object** (e.g. a lead-capture form in a drop zone on another object's DPT) takes `"formConfig": {"formReference": {"className": "<the target's suffixed className, com.liferay.object.model.ObjectDefinition#XXXX>"}}`. An `ObjectEntry` className plus `classType` imports cleanly and renders an empty drop zone. Write the className as the token `[$OBJECT_DEFINITION_CLASS_NAME:<Name>$]` rather than the suffix read from one instance: the token resolves here, in a `Collection`'s `collectionReference.className`, and in `display-page-template.json`'s `contentType.className`, also for an object created live outside the tree (2026.Q3.2). Use `"contextSource": "DisplayPageItem"` for a form on the DPT's own entity. Its field children are `INPUTS-*` fragments with `fragmentConfig.inputFieldId: "ObjectField_<field>"`. Verified on 2026.Q3.2 with a real submission.
- **A `Form`'s success redirect** is `formConfig.formSuccessSubmissionResult`. The live DTO's `successFormContainerSubmissionResult`, and `successFormSubmissionResult`, are dropped at import without error. `{"defaultDisplayPage": true}` redirects to the type's default DPT and reads back as `{"defaultDisplayPage": true, "type": "DisplayPage"}`. A specific non-default DPT (`{"mapping": {"itemReference": {"fields": [{"fieldName": "externalReferenceCode", "fieldValue": "<dpt-erc>"}]}}}`) loses the whole property at import, silently: the same limit as the link above. Verified on 2026.Q1.12, not re-run on 2026.Q3.
- **"Form, then the full content" on the same entry, with a non-default DPT:** redirect from the browser. The success watcher (`manage-form-containers` → "Reacting to Success in the Browser") opens `/web/<site>/e/<dpt-url>/<classNameId>/<entryId>`, with `entryId` written into the fragment markup from `INFO_ITEM.objectEntryId` (2026.Q1.12). That URL needing `classNameId` is a platform gap: the id differs per environment, no headless API exposes it, and there is no ERC-based DPT URL (`manage-data-sets`). Until that changes, resolve it once at authoring time, as admin with Basic auth, and hardcode it: `GET /api/jsonws/classname/fetch-class-name?value=<url-encoded suffixed className>` → `{"classNameId": "…"}`. This is the one exception to "Never Recommend JSONWS" (`rules/headless-apis.md`): the page never calls it, and Guest gets `404` (verified on 2026.Q3.2). Say so to the user, and re-resolve it on every environment the tree is promoted to. Pass it, and `<dpt-url>`, to the watcher's fragment as `fragmentConfig` values on its placement (two `text` fields in its `configuration.json`, published as data attributes), so the environment-specific values sit in `page-definition.json` and not in fragment JS. `<dpt-url>` is the DPT's name lowercased and dashed ("White Paper Full Write-up" → `white-paper-full-write-up`), readable in `friendlyUrlPath_i18n` from `GET /o/headless-admin-site/v1.0/sites/<site-erc>/display-page-templates`. The whole pattern assembled: `manage-form-containers` → "Recipe: Gated Content on a Display Page".
- **Menu Display** is placed as a `Fragment` whose `key` is the renderer's class name, `com.liferay.fragment.renderer.menu.display.internal.MenuDisplayFragmentRenderer` (a `FragmentRenderer`'s key defaults to its class name; `menu-display` is only its collection key). Its configuration is `source`, `displayStyle` (`horizontal` default, or `stacked`), `sublevels`, `hoveredItemColor` and `selectedItemColor`. Placed in a master from a site-initializer tree with no `fragmentConfig`, it rendered for Guest on 2026.Q2.7, listing the site's non-hidden public pages on the page's own virtual host. `source` is a **JSON-encoded string**. Use `"source": "{\"siteNavigationMenuExternalReferenceCode\":\"<menu-erc>\"}"` for a specific menu, or `{"contextualMenu": "children"}` (also `parent-and-its-siblings`, `self-and-siblings`) for relative navigation. With `source` absent, it renders the site's own pages. The ERC resolves at render time, so the menu may be created after the page. Read from 2026.Q3 source, not run.
- **Menu Display writes its link color with an ID** (2026.Q2.7): each placement prints `<style>#fragment-<id> .lfr-nav-item > a { color: inherit; }` and a `:hover` rule with an empty color (`hoveredItemColor` unset). The ID outranks any class selector, so a theme's link and hover colors for menus drawn by it do nothing (the links inherit the parent's color, black in a white header). Write those rules under an ID the chrome sits in (`#wrapper .my-header .lfr-nav-item > a`), and set the hover there too.
- **Menu Display renders every level of a named menu server-side** (2026.Q2.7): `sublevels` (`"-1"` for all, the default) becomes the navigation tag's display depth, and the horizontal style draws nested items as `ul.child-menu` inside `ul.navbar-nav.navbar-site` (`NAVBAR-BLANK-FTL`); `stacked` uses `LIST-MENU-FTL`. So a fragment script can rebuild it (a mega menu) when the Menu Display sits in that fragment's drop zone (a `FragmentDropZone` child in the tree). A `node` item renders with no `href`, and a `layout` item to a **hidden** page still renders (verified as Guest).

#### Mapping Limits — Denormalize Into Display Fields

Field mapping renders the raw stored value and cannot transform it. In particular:

- **`DateTime` values cannot be formatted** through mapping (no date format option).
- **Related object fields cannot be mapped** — you cannot reach across a relationship to display a field from the related entry. (A nested-relationship mapping picker exists behind the Deprecation flag `LPD-60546` (stored off on a new database; see `rules/feature-flags-catalog.md`), but it drills into **custom-object** relationships only — unmodifiable system objects like `User` are excluded, so an owner's full name is still unreachable; hand-authored nested keys import as Unmapped.)
- **Aggregates over a relationship cannot be mapped** — a count of related entries (registrations for an event, items in an order) is not a field, so it cannot be mapped.
- **Per record presentation values** (e.g. a color that varies by entry) cannot be driven from mapping.
- **`BASIC_COMPONENT-date` cannot format an object entry's date.** The fragment formats a mapped value with its `dateFormat` only when the value is a `java.util.Date` (`FragmentEntryProcessorHelperImpl`, `instanceof Date`, in the viewer's time zone), and object entries hand their dates over as `LocalDateTime` (`ObjectEntryInfoItemFieldValuesProvider._getLocalDateTime`, in the JVM default zone, UTC). What you see then depends on the release:
  - **2026.Q3.5:** mapped to `ObjectEntry_displayDate`, `_publishDate`, `_createDate` or `_modifiedDate`, it shows its own placeholder ("02/03/11 00:00 AM"). The same `ObjectEntry_displayDate` in a **text** editable (`BASIC_COMPONENT-paragraph`) renders "10/6/25, 5:04 PM".
  - **2026.Q2.7:** it renders the raw value, `2026-10-08T20:11` (UTC, no zone), with the `config.dateFormat` stored but ignored (verified, editable values read from `fragmententrylink`).

  A **Journal article's** display date is a `java.util.Date`, so the same fragment does format that one; that is the case people remember working. For an object entry, map the date into a text editable and format it in the browser as **UTC**, in the site's time zone: `new Intl.DateTimeFormat(lang, {dateStyle: 'long', timeZone: siteTimeZone}).format(new Date(raw + 'Z'))`, where `siteTimeZone` is the site's IANA zone (a fragment configuration value, for example). Parsing the zone-less value as local time, as liferay-base's `formatted-date` fragment does, shifts it by the visitor's offset and can change the day near midnight. A report that the date fragment formatted `ObjectEntry_displayDate` on 2026.Q3.1 was not reproduced on q2.7 or q3.5.
- **An `Attachment` field maps only by its numeric field id:** `ObjectField_<objectFieldId>#previewURL` or `#fileURL` (the two render the same URL) in an image editable, `#downloadURL` in a link. `ObjectField_<name>` renders the file's **id** as the image `src`. The id differs per database, OOTB definitions included, so for a tree map an **information template** that prints the URL instead (`rules/page-types.md`, "An Attachment field's own mapping keys are not portable").

Two import behaviors that change what a migrated layout shows (verified on 2026.Q3.5):

- **A `tel:` link loses its href in a rich-text editable.** The import sanitizes `BASIC_COMPONENT-paragraph` and heading values with AntiSamy, whose fragment policy (`fragment-sanitizer-configuration.xml`, `offsiteURL`) accepts only `http(s)`, `ftp` and `mailto:` (`WARN [AntiSamySanitizerImpl:129] … tel&#58;…`). The same markup in a `BASIC_COMPONENT-html` keeps it.
- **Demo Categorization's `tags` fragment works on an object entry's display page:** `LIFERAY_SHARED_LAYOUT_ASSET_ENTRY` is set, and it lists the entry's keywords.

Converting another page builder's layout into a tree (verified on 2026.Q2.7 by import and Guest render):

- **Per-viewport visibility imports and renders.** `"fragmentStyle": {"hidden": true}` hides an element on desktop, and `"fragmentViewports": [{"id": "tablet" | "landscapeMobile" | "portraitMobile", "fragmentViewportStyle": {"hidden": false}}]` shows it again below 992px, 768px and 576px. Both land in `/o/layout-common-styles/main.css` as `display: none` / `display: block` rules per media query, and the content stays in the HTML (so `curl` text checks still see it). It works on Sections and Fragments, so a source layout's desktop-only and mobile-only copies of a block keep their behavior. Keep it off Rows: the `display: block` override would replace the row's flex.
- **An element's `customCSS` carries what `fragmentStyle` cannot** (verified on 2026.Q2.7, Fragments and Sections, rendered for Guest): write it against `.[$FRAGMENT_CLASS$]`, which the page's style sheet (`/o/layout-common-styles/main.css`, `LayoutStructureCommonStylesCSSServlet`) replaces with the element's own class; the site initializer's `[$…$]` token substitution leaves an unknown token as written (`StringUtil.replaceToStringBundler`). Used for a source builder's color overlay (a `::before` layer: `.[$FRAGMENT_CLASS$] { isolation: isolate; position: relative; } .[$FRAGMENT_CLASS$]::before { background: …; content: ""; inset: 0; opacity: …; position: absolute; z-index: -1; }`, above the section's own background color or image, below its content), a button's own colors (`.[$FRAGMENT_CLASS$] .btn { … }`, which outranks Clay's `.btn-primary`), and per-element boxes (background, border, radius, padding). A migration that drops these renders light text written for a dark overlay on the bare background.
- **`fragmentStyle` values that are not tokens are written raw:** `"#00376c"`, `"rgba(178,210,232,0.5)"`, `"maxWidth": "640px"` and `"marginLeft": "auto"` all reach the style sheet as given. Spacing keys (`paddingTop`, ...) take Clay spacer indexes as strings.
- **The OOTB Accordion and Tabs take their content through drop zones.** `BASIC_COMPONENT-accordion` is one panel (title editable `accordion-title`, starts expanded) with one drop zone. `BASIC_COMPONENT-tabs` takes `fragmentConfig.numberOfTabs` as a string, titles in `title1`…`titleN` and drop zones `"1"`…`"N"`. Nest `{"definition": {"fragmentDropZoneId": "<n>"}, "pageElements": [...], "type": "FragmentDropZone"}` children on the Fragment element; the content renders for Guest. The Tabs' mobile toggle says "Tab 1" in the HTML until its script writes the active title there, so a placeholder grep must skip `.navbar-toggler`.
- **`BASIC_COMPONENT-image`'s "original size" (`imageSize: "w-0"`) has no CSS rule** in Classic, so a large image renders at its natural width and the fragment's `overflow-hidden` crops it. Use `w-100` and cap the fragment with `fragmentStyle.maxWidth` at the size the source showed.
- **A text editable's literal value is escaped once:** `"Boards &amp; Commissions"` renders as "Boards & Commissions", never `&amp;amp;`.

The workaround is to **denormalize**: add a plain `Text` display field on the object and populate it with the presentation ready value, then map that field. For example add `timeLabel` (a preformatted time string instead of the raw `DateTime`) or `speakerName` (the related person's name copied onto the entry), and map `ObjectField_timeLabel` / `ObjectField_speakerName`.

Denormalizing is also the way to publish a number derived from private records. To show remaining capacity, put the count on the public object — preferably an `Aggregation` field (`function: COUNT` over the relationship), which is computed server side and ignores entry level permissions, so the visitor reads the count without the underlying rows ever being granted to Guest. A counter maintained by an object action is the fallback where no aggregate function fits; note Groovy actions are disabled by default (`rules/object-actions-catalog.md`). See `skills/manage-objects/SKILL.md` for both.

An `Aggregation` field is still a computed field, so the "cannot be mapped" limit above is the reason it may not reach a Collection fragment through mapping — reading it over REST from fragment JavaScript is the verified path.

**Never derive a number from records the visitor cannot read.** That browser call runs as the visitor and returns 0 rows with a `200`, so the arithmetic yields a confidently wrong figure rather than an error. See `rules/guest-access.md`.

The rule is about *what the fetch reads*, not about fetching. Read the line above as: never count the **private** object in the browser. A fetch of an object the visitor legitimately holds `VIEW` on is fine, and is sometimes the only option.

##### When a Collection Element Cannot Do the Job

Prefer the server side Collection. But it renders stored values only, so a listing needs fragment JavaScript once it requires any of these — and a realistic "upcoming events with remaining capacity" listing requires all three at once:

| Requirement | Why Collection mapping cannot serve it |
| --- | --- |
| Filter by date (`upcoming only`) | OData `Date`/`DateTime` filters return `BAD_REQUEST`, so the filtering has to happen client side anyway (`skills/manage-objects/SKILL.md`) |
| Format a date (`Oct 14–16, 2026`) | Mapping has no date format option — it prints the raw value |
| Show the *venue's* time | See "Read DateTime With the UTC Getters" below — the local getters shift it to the visitor's timezone |
| Show an aggregate (`498 of 500 seats`) | Aggregates cannot be mapped, per the limits above |

Denormalizing into `Text` display fields solves the first two, but each one then needs to be kept in sync, and it cannot solve a count that changes on every submission.

So the workable shape for a public listing is: grant Guest company scope `VIEW` on the **public** object, keep the private object write only, put the count on the public object as an `Aggregation` field, and let fragment JavaScript fetch the public object and do the filtering, formatting, and subtraction. Verified end to end on 2026.Q2 — an unauthenticated request returned every event with a correct `registrationCount` while `GET /o/c/registrations` returned `totalCount: 0` for the same visitor.

Two details that bite:

- **Use `Liferay.Util.fetch`, not native `fetch`** (`skills/scaffold-client-extension/SKILL.md`).
- **`Aggregation` serializes as a string** — an event with two registrations returns `"registrationCount": "2"`. `capacity - count` concatenates in JavaScript. Parse first.

##### Read DateTime With the UTC Getters

A `DateTime` field stored `convertToUTC` comes back as `2026-10-14T09:00:00.000Z`. `new Date(...)` parses that correctly, but `getHours()` / `getDate()` / `getDay()` then render it **in the browser's timezone** — so a 09:00 keynote displays as `02:00` to a visitor on US Pacific, and as a different time again to everyone else. Verified on 2026.Q2: the same listing read `09:00–10:30` server side and `02:00–03:30` in the browser.

This is worse than an ordinary bug because it is *plausible*. The listing renders, the layout is fine, the date is right, and only the hour is wrong — so it survives every check short of reading the number against the source data.

For an event at a fixed venue the stored value **is** the venue's wall clock time, so read it back the way it was written:

```javascript
pad(date.getUTCHours()) + ':' + pad(date.getUTCMinutes())
```

Use `getUTCDay()`, `getUTCDate()`, and `getUTCMonth()` for the date parts too, and compare `toUTCString().slice(0, 16)` rather than `toDateString()` when testing whether a start and end fall on the same day — the local variant flips near midnight. Only reach for the local getters when the time genuinely should follow the reader (a webinar), and say so in a comment, because the next person will assume it is this bug.

Verifying this is browser work, not `curl` work: `curl` executes no JavaScript, so an empty listing and a working one produce identical HTML. See "Success Signal" below.

### Create Pages via the Live API (Fallback)

Use the Headless Admin Site API (`/o/headless-admin-site/v1.0`) only when reprovisioning is undesirable and the change is small. It is unreliable for page **creation** in particular. When the MCP server is available, prefer MCP tool calls over raw curl.

Everything below has a tree equivalent that is the verified path. Reach for the live API only for the last row:

| To do this | Author it here instead |
| --- | --- |
| Create a page | `layouts/<NN-name>/page.json` + `page-definition.json` |
| Place fragments on a page | `page-definition.json` — see "Authoring Pages" above |
| Navigation menu | `site-navigation-menus.json` |
| Display page template | `layout-page-templates/display-page-templates/<name>/` |
| SEO settings | `page.json` (`title_i18n`, `description_i18n`, `keywords_i18n`, `robots_i18n`) and `site-settings.json` (the title suffix); canonical and Open Graph only over the live API below |

#### Corrections to Any Live API Example

Published examples for this module — including older ones in this pack's history — predate the current DTOs. Verify against the spec (`get-openapi` MCP tool, or `GET /o/headless-admin-site/v1.0/openapi.json`) and apply these:

- **Sites are addressed by external reference code**, not numeric ID, and subresources nest under `/sites/<site-erc>/…`. There is no top level `/site-pages/{id}`.
- **The DTOs use `pageSpecifications`**, not the initializer's `pageDefinition`.
- **Localized fields are `*_i18n` maps** (`name_i18n`, `friendlyUrlPath_i18n`). `SitePage` has no `title` field. **This module keys those maps `en-US`, with a hyphen** (`"friendlyUrlPath_i18n": {"en-US": "/event-session-speaker-"}` on a display page template, verified 2026.Q3.5), unlike the object and list-type APIs' `en_US`; a script reading `["en_US"]` gets nothing. Read both, then fall back to any locale.
- **`DisplayPageTemplate` binds through `contentTypeReference`**, not flat `contentType`/`contentSubtype`. For a Liferay Object the class name is `com.liferay.object.model.ObjectEntry` and the subtype is the object definition's ERC.
- **A custom fragment reference uses `BasicFragment` + `fragmentReferenceType`** over the live API, and `key` + `siteKey` in the initializer tree. Neither accepts `collectionExternalReferenceCode`/`fragmentEntryKey` — that form is silently dropped and the section renders blank. See "Custom Fragment Placement via the Headless API" below.
- **Three distinct `type` vocabularies.** Live API: `ContentPage` / `WidgetPage` / `LinkToURLPage` / `EmbeddedPage` / `PageSetPage` / `LinkToPagePage`. Initializer `page.json`: `Content` / `Portlet` / `URL` / `Embedded`. `headless-delivery` uses a separate `pageType`.
- **Every `site-pages` and `master-pages` call requires `LPD-35443`** ("Page Management API", off by default). Without it they answer `400 UnsupportedOperationException` ("Feature flag LPD-35443 is disabled for company …"). Verified on 2026.Q3.8.
- **Page element operations require the `LPD-74328` flag.**

#### Ensure the Site Exists

```bash
curl \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-site/v1.0/sites" \
	--user "test@liferay.com:test" \
	| jq '[.items[] | {externalReferenceCode, name, friendlyUrlPath}]'
```

Save the `externalReferenceCode` as `<site-erc>`. A `siteInitializer` CET that declares `siteExternalReferenceCode` provisions its own site on deploy, so creating one by hand is usually a sign the initializer is not wired up.

#### Configure SEO Settings

**Most of it goes in the tree** (read from 2026.Q2.7 source, `BundleSiteInitializer._addOrUpdateLayout`): `page.json` takes `title_i18n` (the HTML title), `description_i18n`, `keywords_i18n` and `robots_i18n`, locale maps, on create and on every re-fire (`addLayout`/`updateLayout`). Three more facts, verified on 2026.Q2.7:

- **Liferay already writes a canonical link** to the page's own URL on the host it is served on (`<link rel="canonical">`, `og:url`), so a migrated canonical that only pointed at the page itself needs nothing.
- **The title's suffix is a per-site setting:** `<title>` is "Page - Site - Instance name" by default. `com.liferay.layout.seo.internal.configuration.LayoutSEOGeneralGroupConfiguration` (`includeInstanceName`, `includeSiteName`) goes in `site-settings.json`; `includeInstanceName: "false"` gives "Page - Site" (`LayoutSEOLinkManagerImpl._getPageTitleSuffix`).
- **A display page template's SEO fields map entry fields through the layout's type settings** (`mapped-title`, `mapped-description`, `mapped-openGraphTitle`, `mapped-openGraphDescription`, `mapped-openGraphImage`), defaulting to `${title}` and `${description}`. For a type with no `description` field, every entry page prints `og:description` as the literal word "description" (`LayoutSEOTemplateProcessorImpl` prints an unknown name as itself). **Set them from the tree in the template's own `page-definition.json`**, `settings.themeSettings`: the importer copies every `themeSettings` key into the layout's type settings (`LayoutsImporterImpl._updateLayoutSettings`, which removes only `lfr-theme:` keys first). Name object fields `${ObjectField_<name>}`: `{"mapped-description": "${ObjectField_subtitle}", "mapped-openGraphDescription": "${ObjectField_subtitle}"}` gave both `<meta name="description">` and `og:description` the entry's text (verified as Guest on 2026.Q2.7). Map a plain-text field; a RichText one would print its markup.
- **`hreflang` alternates follow the site's languages.** A site that inherits the instance's languages lists an alternate per enabled locale on every page, translated or not. A site's own list is its group type settings `inheritLocales=false` and `locales=<languageIds>` (`LanguageImpl`, then `resetAvailableGroupLocales`), which no site-initializer file sets; with `locales=en_US` a page listed only `en-US` and `x-default` (2026.Q2.7). Set it from a module after the run that creates the site has committed, since that run updates the group too. **Every re-fire of the site's initializer resets it** (the run rewrites the group's type settings, and the site inherits every language again), so re-apply it from a `ModelListener<Group>` `onAfterUpdate`, after commit, which a re-fire then leaves English-only.

What stays live-only is the canonical override, Open Graph and custom meta tags (`LayoutSEOEntry`). Patch the page after it exists:

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

Because this is applied live, it is **lost on reprovision**. Record it as a known manual step.

### Redirects (Verified on 2026.Q2.7)

Liferay's Redirection (`RedirectEntry` exact entries and per-site regex patterns) is what keeps an old site's URLs working, and both are per site: a request on a site's virtual host is matched against that site's entries.

- **Redirects are consulted before Liferay resolves any page or entry URL** (`FriendlyURLServlet.getRedirect`, the redirect provider first). An exact entry whose source is a live page's URL hides the page, and a pattern matching a live URL hides every page it matches. Check every source against the site's live URLs before writing it.
- **There is no headless API and no site-initializer handler** for either. Write exact entries from a module through `RedirectEntryLocalService.addRedirectEntry(groupId, destinationURL, null, permanent, sourceURL, serviceContext)`; the source has no leading slash (one is refused) and is normalized (`FriendlyURLNormalizer.normalizeWithEncoding`: lowercased, encoded), so compare in that form.
- **A source whose first segment is reserved is refused** with a bare `LayoutFriendlyURLException`: the same keyword check as a page URL, so an object's `friendlyURLSeparator` (`venue/<slug>`) cannot be an exact source. A pattern is not checked that way.
- **Patterns** are the group configuration `com.liferay.redirect.internal.configuration.RedirectPatternConfiguration`, a `String[]` of `"<regex> <destination> <user agent>"` (re2j; the regex is anchored at the start and matched against the URL without its leading slash; `$1` works in the destination; user agent `all`, `bot` or `human`). A pattern redirect is always a `302`; an exact entry is `301` when permanent. Write them with `RedirectPatternConfigurationProvider.updatePatternStrings(groupId, entries)`; `site-settings.json` cannot carry them (`rules/site-initializer-format.md`).
- A destination may be root-relative (`/l/12345`); the browser resolves it on the host it asked.

A plain JAX-RS application that exposes such writes must return itself from `getSingletons()`. Without it the whiteboard registers the application with no resource methods (`jaxrs:check` lists none), and every request answers `404` "No service was found".

### Place a Client Extension Widget on a Page

When the user wants to add a deployed Custom Element CET to a page:

1. Confirm the CET is verified active first (see `deploy-and-verify`).

1. Ask the user which page to add it to — do not assume.

1. If the widget is already on the page, ask whether to replace or add a new one.

1. Place the widget via MCP, the Headless Admin Site API, or the Liferay UI depending on what the environment supports. See `mcp-server` for MCP availability per DXP version.

## Patterns and Gotchas

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
- **`PUT /sites/{siteERC}/site-pages/{pageERC}` is an upsert** — it creates the page when the ERC doesn't exist and updates it when it does, so scripted page provisioning is safely re-runnable.
- **The draft spec appears asynchronously after page creation.** Immediately after the create PUT, `GET .../page-specifications` can come back empty for a second or two — poll (a few retries with ~2s delays) for the draft spec before writing elements or publishing, or the follow-up call fails on a page that is actually fine.

**A content page's specification cannot be PATCHed** (verified on 2026.Q3.5): `PATCH /sites/{siteERC}/page-specifications/{specERC}` answers `400 "The page specification must be in draft status for content pages"`, the `-draft` specification of a published page included. To set a page's master or a placed fragment's configuration values, PUT the whole page: its GET plus `pageSpecifications` from `GET …/site-pages/{erc}/page-specifications`, changed only in `settings.masterPageItemExternalReference` and an element's `fragmentInstance.fragmentConfigurationFieldValues` (`{"<field>": {"type": "Text", "value": "…"}}`), on both specifications. Both took, and every fragment link was kept. A later site-initializer re-import of the page reset the configuration values and kept the master.

Required fields missing from the OpenAPI spec (omitting any of these causes a server error):

- `pageSettings: {"type": "ContentPageSettings"}` at the top level
- `pageExperiences[]` with at least one entry per spec
- `name_i18n` on each `pageExperience`

ERC suffixes autogenerated on page creation: draft spec is `{pageERC}-draft`, published experience is `{pageERC}-default`, draft experience is `{pageERC}-draft-default`.

A successful PUT response code is not a reliable success signal — verify by issuing an HTTP GET on the page URL.

Two more traps of the whole-page PUT:

- **It replaces the Master Page assignment along with everything else.** The master lives in each spec's `settings.masterPageItemExternalReference` (`{"externalReferenceCode": "<master-erc>"}`, the ERC from `GET .../master-pages`). A body without that field, or without the `settings` block, returns `200` and removes the master: the page renders with no header or footer. Verified live on 2026.Q3.2:
	- **To keep the master,** send the GET back unchanged. The PUT accepts the read-only fields as they come.
	- **To set or restore it,** put the reference on the **published** spec, which drives the render. On the `-draft` spec alone, no master renders, so set it on both. It can also be set at page creation.
	- **Leave `scope` out of the reference.** A reference carrying one returns `400 IllegalArgumentException` ("…do not belong to the same scope…").
	- **Validation depends on what the ERC names.** A display page template is rejected with `400` ("…does not point to a master page"), and the page stays unchanged; a page template takes the same code path (from source), though only the DPT case was run. An ERC that names **no** `LayoutPageTemplateEntry` is *accepted*: a typo, a page's ERC, or a utility page's ERC (`LFR-404-ERROR`). The response is `200`, the GET returns that ERC, and the page renders with **no** master. The only trace is a `WARN [LogUtil:34] Optional reference generated for missing LayoutPageTemplateEntry with external reference code <erc>`, logged once per spec. So check the ERC against `GET .../master-pages` before the PUT, and check the rendered page after it — neither the `200` nor the GET proves the master exists.
	- **Such a dangling reference resolves at render time.** Create a master with that exact ERC later (`POST .../master-pages` with a chosen `externalReferenceCode` returns `200`) and the page picks it up with no new PUT. So a page can reference a master that a later deploy step creates.
- **Scoping a `globalJS`/`globalCSS` CET to one page** goes in the spec's `settings.globalJSClientExtensions` / `globalCSSClientExtensions`. The shape is `{"clientExtensionConfig": {"loadType": "default", "scriptLocation": "head"}, "externalReferenceCode": "LXC:<cet-key>"}`. A bare `{"externalReferenceCode": "<cet-key>"}` returns `200`, round trips on GET, and loads nothing. Shape taken from a Page Editor write and diffed, and verified on 2026.Q3.2. This live PUT is the way to bind a CET to one page: in the initializer tree the same `settings` keys work on a master only (`rules/site-initializer-format.md`).

### `headless-admin-site` Schema Gotchas

**`type` vs `pageType` — field names differ by API.** `headless-delivery` uses `pageType` (plain string). `headless-admin-site` uses `type` (enum: `ContentPage`, `WidgetPage`, etc.). Using `type` against the delivery API returns `400 - The property "type" is not defined in SitePage`.

**`FragmentReference` uses a nonstandard discriminator.** Every other polymorphic schema in `headless-admin-site` uses `type`. `FragmentReference` is the exception — it uses `fragmentReferenceType`. Using `"type": "DefaultFragmentReference"` returns `400 InvalidTypeIdException: missing type id property 'fragmentReferenceType'`.

**`position` is required despite the spec marking it optional.** Omitting `position` from a page-element POST returns `500 NullPointerException`. Always include `"position": 0` (or the intended 0 based index).

> **Building a form?** `skills/manage-form-containers/SKILL.md` holds the full Form Container contract: field fragment keys per type, the submit button, multistep, success results, and the `ADD_OBJECT_ENTRY` grant without which the form does not render. The notes below are the page-API side of it.

**`FormContainerConfig.numberOfSteps` is required despite the spec marking it optional.** Same pattern — omitting it returns `500 NullPointerException` (`FormContainerConfig.getNumberOfSteps()` is null). The value itself is lenient: `0` and `1` both give a working single-step form, with `formContainerType` `Simple` or `Multistep`. What the platform itself produces is `"formContainerType": "Multistep", "numberOfSteps": 0` (verified 2026.Q3.2).

**FormFragment elements nest via `parentExternalReferenceCode`.** The page-elements POST endpoint is flat — all elements hit the same endpoint regardless of nesting depth. Parent child relationships are expressed through `parentExternalReferenceCode`, not nested objects. Key fields for `FormFragment` children:

- `fieldKey` — binds the input to an Object field. The platform writes `ObjectField_<name>`, and a bare `<name>` binds too. **A `FormFragment` without `fieldKey` still submits and shows the success message, but stores an empty value**, so an unbound field reads as a successful form. The submit button (`INPUTS-submit-button`) needs no `fieldKey`; `"formButton"` is harmless. Verified on 2026.Q3.2 with real submissions.
- `fragmentInstance.fragmentReference.defaultFragmentKey` — the INPUTS fragment key
- `label_i18n` — localized label
- `markAsRequired` — boolean
- `showLabel` — boolean

**Display page templates and master pages are writable the same way on 2026.Q3.2.** A whole-resource `PUT` of `/sites/{siteERC}/display-page-templates/{erc}` or `/master-pages/{erc}` carries both page specifications, like a site page. So does a `PATCH` of `pageSpecifications`. It returns `200`, and the change renders on the DPT. `POST …/page-specifications` still fails with `400 "The draft page specification is not in draft status"`. Inline text in a fragment goes in `htmlFragmentValue: {"type": "Inline", "fragmentInlineValue": {"value_i18n": {…}}}`; the only type ids are `Inline` and `Mapped`.

### Changing One Element in Place (Verified on 2026.Q3.5)

To change one placed fragment (an image URL, a text) without replacing the page, use the element endpoint `…/sites/{siteERC}/page-specifications/{specERC}/page-experiences/{experienceERC}/page-elements/{elementERC}` (flag `LPD-74328`):

- **`PATCH` is a silent no-op.** It answers `200` with the element, and moves the link's `modifieddate`, but the stored `editablevalues` do not change.
- **`PUT` works.** Send the element exactly as that spec's `GET …/page-specifications` returns it, with only the value changed, plus `parentExternalReferenceCode` and `position`. It updates the link in place: same link id, no new rows, and the page renders the new value.
- **Live and draft each need their own body.** Element ERCs are the same in both specs, but each element's `fragmentInstance.fragmentInstanceExternalReferenceCode` names that spec's own link. The live body sent to the draft answers `400 UnsupportedOperationException`.
- **A background-image editable does not round-trip.** An imported fragment with `data-lfr-background-image-id` stores the value under `BackgroundImageFragmentEntryProcessor` and an empty `{}` under `EditableFragmentEntryProcessor`. `FragmentEditableElementUtil` merges the two and the empty one wins, so the element comes back with no editable value. A `PUT` of that body cannot set it, and could clear it. A container's `fragmentStyle` background, by contrast, does come back.

### Custom Fragment Placement via the Headless API

When placing custom fragments via `PUT /sites/{siteERC}/site-pages/{pageERC}`, inline the fragment's `html` and `css` on the `fragmentInstance`. Both the draft and published specs must carry the same inlined content — otherwise the UI's next Publish reverts the affected sections to empty divs.

Discriminators for fragment placement:

- Page element type: `"type": "BasicFragment"` (not `"Fragment"`)
- Fragment reference: keyed by `"fragmentReferenceType"` (not `"type"`)
- Custom fragments: `"fragmentReferenceType": "FragmentItemExternalReference"` with `"externalReferenceCode": "<fragmentEntryKey>"` — the fragment entry key IS the ERC
- Out of the box fragments: `"fragmentReferenceType": "DefaultFragmentReference"` with `defaultFragmentKey` — inlining not required

### Fragment Management API

Fragment CRUD lives in its own module, `headless-admin-fragment` — not in `headless-admin-site`, which is why it looks absent when you check the page API's spec. It is verified working on 2026.Q2 and absent on 2026.Q1, but it does **not** propagate edits to fragments already placed on a page. See `skills/scaffold-fragment/SKILL.md` → "The Live Fragment API". For programmatic placement on pages, use the discriminators above.

## Success Signal

Applies to **both** paths — a page authored in the initializer tree and one created over the live API.

```bash
curl --head --silent --url "http://localhost:${PORT}/web/<site-friendly-url>/<page-url-slug>"
```

Expect `200 OK`.

**`200 OK` is not evidence the page works.** A page whose Collection returned no rows, whose fragment reused another page's placeholder text, or whose mapping silently failed all return 200 with fragments present. Verify content, and verify it as the audience:

1. **Probe without credentials.** `curl` with no `--user` and no cookie jar is exactly a Guest request, and it is the only cheap way to see what a visitor sees. Signed in verification hides every permission gap in this skill — see `rules/guest-access.md`.

	```bash
	curl --silent --url "http://localhost:${PORT}/web/<site>/<page>" > /tmp/page.html
	```

1. **Assert real data, not markup presence.** Grep for a value that only exists in the database (an actual event name) *and* confirm the fragment's placeholder strings are absent — placeholders still present means the mapping did not resolve.

1. **Check each page's own text.** Reusing a section fragment across pages carries its default text with it; a heading that reads "Upcoming Events" on the register page is a 200 with wrong content. Set per page values with a literal `fragmentFields` entry rather than relying on the fragment default.

1. **Check that every page has a heading.** Each page needs at least one `h1`–`h3`, with an `h1` on the home page, or it has no outline and the site's heading style has nothing to apply to. A count of `0` on a page whose title is overridden means the heading is wrapped inside a `rich-text` region (see "A Literal Override Replaces the Editable's Inner Markup").

	```bash
	curl --silent --url "http://localhost:${PORT}/web/<site>/<page>" | grep --count --extended-regexp '<h[1-3][ >]'
	```

1. **Open it in a browser before declaring success.** Anything driven by JavaScript — a computed value, a populated `select` — is invisible to `curl`, which executes none of it. Confirm signed out too: sign in state changes what client side calls return.