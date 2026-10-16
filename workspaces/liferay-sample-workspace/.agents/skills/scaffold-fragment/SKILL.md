---

description: Create a Liferay page fragment — an HTML/CSS/JS building block that marketers drop onto Content Pages. Use when the user asks to create a fragment, build a hero section, or make a reusable page component.
name: scaffold-fragment

---

# Scaffold Fragment

Generate the source files for a Liferay page fragment and deploy it to the running portal so it appears in the Content Page editor fragment palette.

## When to Invoke

- "Create a fragment", "make a hero section", "build a card component"
- "I need a reusable page widget that marketers can configure"
- Called by `build-site` or `manage-pages` when a page composition requires a custom fragment

## Fragment Types

| Type | Use Case | Key Feature |
| --- | --- | --- |
| Component | General purpose UI block | `data-lfr-editable` regions, configuration fields |
| Section | Full width layout block | Wraps other fragments or sets background |
| Form field | One field inside a Form Container, bound to an object field | `"type": "input"` plus `typeOptions.fieldTypes` — load `scaffold-form-fragment` |
| React / Custom Element | Complex interactive widget | Delivered as a custom element CET alongside the fragment |

For complex interactive widgets, use `scaffold-client-extension` with type `customElement` and reference it from the fragment's HTML with a `<custom-element-name>` tag. Its `<script>` and `<link>` tags go in the fragment's JS, since the HTML ones are stripped at render: `scaffold-client-extension` → "Placing It on a Page Through a Fragment".

## Workflow

### Choose Collection and Name

Fragments belong to a collection. The source of truth is the **site initializer tree** — author fragments there so they ship with the site and stay in version control:

```
client-extensions/<site-initializer>/site-initializer/fragments/group/<collection-key>/
  collection.json                       # {"name": ..., "description": ...}
  fragments/
    <fragment-name>/
      fragment.json
      index.html
      index.css
      index.js
```

The `collection.json` sits at the collection-key root, and the fragment folders live under a `fragments/` subdirectory beside it. This nesting is required — placing fragment folders directly under `<collection-key>/` (with no `fragments/` level) prevents the collection's fragments from importing, so any page that references them renders blank. The `<fragment-name>` directory name becomes the fragment's `key` used in `page-definition.json` (see `manage-pages`). Use an existing collection or create a new one with its own `collection.json`.

### Generate the Files

Create four files for each fragment:

**`fragment.json`** — metadata

```json
{
	"cssPath": "index.css",
	"htmlPath": "index.html",
	"jsPath": "index.js",
	"name": "<Fragment Display Name>",
	"type": "component"
}
```

**`index.html`** — markup with editable regions

```html
<div class="fragment-<name>">
  <div data-lfr-editable-id="image" data-lfr-editable-type="image">
    <img alt="" src="" />
  </div>
  <h2 data-lfr-editable-id="title" data-lfr-editable-type="text">Heading</h2>
  <div data-lfr-editable-id="body" data-lfr-editable-type="rich-text">
    <p>Body text here.</p>
  </div>
  <a data-lfr-editable-id="link" data-lfr-editable-type="link" href="#">
    Learn more
  </a>
</div>
```

Editable type values: `rich-text`, `text`, `image`, `link`, `html`, `background-image`.

**Make a heading the editable element itself, as a `text` editable.** Do not wrap an `<h1>`–`<h3>` inside a `rich-text` region. A page that sets the title through `fragmentFields` replaces the region's inner markup, so the wrapped heading renders as a plain `div` and the page loses its heading, its outline, and every heading style (`manage-pages` → "A Literal Override Replaces the Editable's Inner Markup"). With the heading tag as the editable, the override replaces only its text: verified on 2026.Q2, where `<h2 data-lfr-editable-type="text">` with a per page override still rendered as an `h2`.

**`index.css`** — scoped styles

```css
.fragment-<name> {
  padding: 2rem;
}

.fragment-<name> h2 {
  font-size: 2rem;
  margin-bottom: 1rem;
}
```

**`index.js`** — optional behavior (empty file if none)

```javascript
/* Fragment JS, runs once per fragment instance on the page */

// Liferay injects these before this code runs; do not redeclare them:
// fragmentElement (the root DOM element), fragmentElementId,
// fragmentEntryLinkNamespace, fragmentNamespace (a string), configuration
```

### Add Configuration Fields (Optional)

Create `configuration.json` to expose configurable options in the Content Page editor sidebar:

```json
{
	"fieldSets": [
		{
			"fields": [
				{
					"dataType": "string",
					"defaultValue": "primary",
					"label": "Button Style",
					"name": "buttonStyle",
					"type": "select",
					"typeOptions": {
						"validValues": [
							{
								"value": "primary"
							},
							{
								"value": "secondary"
							},
							{
								"value": "link"
							}
						]
					}
				}
			],
			"label": "Styling"
		}
	]
}
```

Access the value in `index.html` with `[configuration.buttonStyle]` or in `index.js` via `configuration.buttonStyle`.

### Deploy

**Initial build — with the site:**
The fragment lives in the `siteInitializer` tree and is created when the site is provisioned. Trigger the initializer (see `build-site` Phase 9 and `rules/site-initializer-format.md`); no separate fragment deploy is needed.

**Iterating on a fragment — re-fire the initializer from the tree:**
Edit the source files in the site initializer tree and re-fire the initializer (clean redeploy — the re-fire upserts the FragmentEntry and re-snapshots placed fragments; see `rules/site-initializer-format.md` → "Re-firing the initializer UPSERTS" for the deploy-order rule and the delete+redeploy fallback). Object data is company scoped and survives either path.

**A live fragment API does exist — it just will not save you the reprovision.** `headless-admin-fragment` is real and fully functional (see "The Live Fragment API" below). The reason you still reprovision is **propagation**, not a missing endpoint: Liferay copies a fragment's HTML, CSS and JS into each page's fragment *instance* when it is placed, so updating the library fragment leaves every page already using it untouched.

Verified on 2026.Q2 — a `PUT` that changed a fragment's CSS read back changed on the fragment entry and produced **zero** difference in the rendered page that placed it. Knowing this is worth a few minutes: the endpoint returns `200`, the read back confirms the edit, and it is entirely reasonable to conclude the change is live and then spend a while hunting a caching problem that does not exist.

**Alternative — standalone fragment collection CET:**

```bash
cd client-extensions/<fragment-collection-name>
blade gw deploy
```

### Verify

Open the Content Page editor at the target site. The fragment collection should appear in the left panel under Fragments. Drag the fragment onto the page and confirm editable regions are highlighted.

Check the browser console for JS errors from `index.js`. Check `bundles/logs/liferay.<date>.log` for import errors.

## Fragment Naming Conventions

- Collection key: `kebab-case`
- Fragment name: `kebab-case`
- CSS class prefix: `fragment-<name>` to avoid global collisions
- Editable ID: `camelCase`, unique within the fragment
- DOM `id` attributes: prefix with `${fragmentElementId}` (unique per fragment *instance*, and what the built in fragments use) — a bare id collides the moment the fragment is dropped twice on one page

## Patterns and Gotchas

### `fragment.json` Path Keys — Hallucination Warning

Do not use `html` or `css` keys in `fragment.json`. The valid keys are `htmlPath`, `cssPath`, `jsPath`, `configurationPath`, `thumbnailPath`. Using the short forms produces "HTML content must not be empty" or silent file not found errors.

### Configuration Field Types — Valid vs. Invalid

Valid types for `configuration.json` fields: `text`, `select`, `checkbox`, `colorPicker`, `colorPalette`, `length`, `url`, `itemSelector`, `videoSelector`, `collectionSelector` (DXP 7.3+), `navigationMenuSelector` (2024.Q1 / GA112+). The two selectors, like `itemSelector`, are read in FreeMarker through a `<name>Object` variable. `navigationMenuSelector` gives a custom fragment raw access to a menu's structure; for a rendered menu, use the built-in Menu Display fragment (`manage-pages`). Source: the Liferay fragment configuration reference.

Do **not** use `image`, `link`, or `rich-text` in `configuration.json` — these are not configuration types. They must be made editable via `index.html` using `data-lfr-editable-type` instead.

#### `dataType` Has No `boolean` — Omit It on a Checkbox

`dataType` is a closed enum and **`boolean` is not in it**:

```text
array, double, int, object, string
```

A `checkbox` field is the trap, because `"dataType": "boolean"` is the natural guess and the failure is disproportionate. Portal's own checkbox fields carry **no `dataType` at all** — just `defaultValue`, `label`, `name`, `type`:

```json
{
	"defaultValue": true,
	"label": "Only show upcoming events",
	"name": "upcomingOnly",
	"type": "checkbox"
}
```

An invalid value fails schema validation on import:

```text
FragmentEntryConfigurationException: Fragment configuration is invalid.
/fieldSets/0/fields/1/dataType: boolean is not a valid enum value
```

Inside a site initializer that exception is an `InitializationException` — it **rolls back the entire site creation transaction**, so the symptom is "no site was created", not "one fragment is wrong". Source: `configuration-json-schema.json` in `modules/apps/fragment/fragment-service`.

#### Per-Type Configuration Quirks

- `select` options go in `typeOptions.validValues` (`[{"value": "x", "label": "X"}]`) — a legacy `options` key or a bare array fails validation with `expected type: JSONObject, found: JSONArray`.
- A `url` field's `defaultValue` must be a non-empty absolute value (use `"#"` when there is no sensible default) — null/empty throws an NPE during configuration parsing.
- `videoSelector` hands JS a **JSON string, not a URL**: `JSON.parse(configuration.<field>)` and inject its `html` property (the provider's embed markup) into the DOM.
- `itemSelector` requires `typeOptions.itemType` set to the fully qualified class of the selectable entity (e.g. `com.liferay.journal.model.JournalArticle`).
- `colorPalette` takes `typeOptions.colors` — a flat array of hex strings, not `validValues`.

#### Detect Edit Mode Before Running View Behavior

The page editor adds `has-edit-mode-menu` to `<body>`. Gate carousels, auto-rotation, overlays, and other view-only behavior on `!document.body.classList.contains('has-edit-mode-menu')` (or the CSS form `body.has-edit-mode-menu .my-fragment-wrapper { ... }`) so all items stay visible and clickable while an author composes the page.

### Fragment Scoping — Prevent Cascade Conflicts

Every fragment must wrap its content in a named container div. Prefix **all** CSS rules with `#wrapper .<wrapper-class>` to prevent cascade conflicts with other fragments on the same page:

```html
<!-- index.html -->
<div class="my-fragment-wrapper">
  <h1 data-lfr-editable-id="title" data-lfr-editable-type="text">Title</h1>
</div>
```

```css
/* index.css */
#wrapper .my-fragment-wrapper h1 {
  color: var(--primary);
}
```

Without this prefix, styles leak across fragments and produce unpredictable cascade conflicts that are difficult to reproduce outside the full page context.

### A Navigation Menu Macro Is Configured Outside the Fragment

`[@liferay.navigation_menu /]` in a fragment renders the site's **non-instanced** navigation portlet, which keeps its settings (which menu, which template) in one site-wide preference row that the theme's own header also uses and creates first. Nothing in a site initializer tree reaches that row, so on a new site the fragment lists every page. To show a named menu, use the Menu Display renderer with `fragmentConfig.source` (`skills/manage-pages/SKILL.md`), or write the row after the import (`rules/site-initializer-format.md` → "A fragment's `[@liferay.navigation_menu /]`"; verified on 2026.Q3.5).

### Drop Zones

To create a container fragment that accepts nested content, use the `<lfr-drop-zone>` tag — exact spelling, not `lfr-dropzone` or any other variant.

- Give each drop zone a unique `data-lfr-drop-zone-id` attribute within the fragment: that is the attribute the drop zone processor reads (`DropZoneDocumentFragmentEntryProcessor`, as Liferay's own Tabs and Accordion use it, 2026.Q2.7).
- In edit mode, drop zones are visible as highlighted regions; they render invisibly in view mode.

```html
<div class="my-layout-wrapper">
  <div class="col-left">
    <lfr-drop-zone data-lfr-drop-zone-id="left"></lfr-drop-zone>
  </div>
  <div class="col-right">
    <lfr-drop-zone data-lfr-drop-zone-id="right"></lfr-drop-zone>
  </div>
</div>
```

From a site-initializer tree, fill each zone with a `FragmentDropZone` child of the Fragment element whose `definition.fragmentDropZoneId` names it: `{"definition": {"fragmentDropZoneId": "left"}, "pageElements": [...], "type": "FragmentDropZone"}`. Verified on 2026.Q2.7 with a two-zone header fragment in a master (its zones held Menu Display renderers, another fragment with its own zone, and Sections), rendered for Guest.

### Edit Mode Awareness

Liferay's Page Editor adds the class `has-edit-mode-menu` to `<body>` when a page is open for editing. Use this to reveal elements or disable behaviors that should only be visible to authors.

**CSS — show a hidden element only while editing:**

```css
#wrapper .my-fragment-wrapper .drop-zone-hint {
  display: none;
}
body.has-edit-mode-menu #wrapper .my-fragment-wrapper .drop-zone-hint {
  display: block;
  border: 2px dashed var(--warning);
}
```

**JS — disable animations in edit mode to avoid interfering with drag and drop:**

```javascript
if (!document.body.classList.contains('has-edit-mode-menu')) {

  // Run animation logic only in view mode

  initAnimations();
}
```

### Stylebook Tokens

Always prefer Liferay's CSS variables over hardcoded hex values to ensure sitewide brand consistency.

- **Core colors**: `var(--primary)`, `var(--secondary)`, `var(--brand-color-1)`, `var(--white)`, `var(--black)`.
- **Status colors**: `var(--success)`, `var(--info)`, `var(--warning)`, `var(--danger)`.
- **Gray scale**: `var(--gray-100)` to `var(--gray-900)`.
- **Spacing**: use `var(--spacer-1)` through `var(--spacer-10)`.

### FreeMarker Defaults

When referencing configuration or editable values in `index.html`, always provide a FreeMarker default to avoid Null Pointer errors:

```html
${configuration.myVar!'Default'}
```

**`!` only covers a missing value, and a never-configured field is not missing.** It is present as `""`, so `!'Default'` never fires and the fragment prints an empty string (verified 2026.Q3.2: `A=[${configuration.x!'Default'}]` rendered `A=[]`). When the fallback matters, test for content instead:

```html
[#if configuration.myVar?has_content]${configuration.myVar}[#else]Default[/#if]
```

#### A Checkbox Value Will Not Print Without `?c`

FreeMarker refuses to convert a boolean to a string implicitly, so printing a `checkbox` configuration value the same way as a text one fails:

```text
FragmentEntryContentException: FreeMarker syntax is invalid.
Can't convert boolean to string automatically, because the "boolean_format" setting
was "true,false", which is the legacy deprecated default…
Failed at: ${configuration.upcomingOnly!true}
```

Append `?c` (the computer readable format, giving `true` / `false`), and keep the default **inside** the parentheses so it is applied before the conversion:

```html
<div data-upcoming-only="${(configuration.upcomingOnly!true)?c}">
```

Parenthesizing the default is the form verified here; bind it to the variable rather than to the formatted result. For human facing output the error message itself suggests `?string('Yes', 'No')` instead of `?c`.

This is the same rollback trap as an invalid `dataType`: inside a site initializer it aborts the whole site creation, so a fragment that reads a checkbox is worth rendering once before shipping the tree.

#### Fragment FreeMarker Is SQUARE-BRACKET Syntax — Angle-Bracket Directives Are Emitted as Text

Fragment `index.html` is processed with FreeMarker's **square-bracket tag syntax**: `[#if]…[/#if]`, `[#assign]`, `[#list]`, and macro calls like `[@clay["icon"] symbol="times" /]` all evaluate (production-verified — server-side Clay icons and conditional markup in fragments work through this syntax). The familiar **angle-bracket** form does NOT run: it is HTML escaped and emitted into the page as visible text. Verified on a self hosted 2026.Q2 bundle, where

```html
<#if (configuration.showCta!true)>
	<div class="hero__actions">…</div>
</#if>
```

rendered the literal string `<#if (configuration.showCta!true)>` on the live page, directly above a fully rendered `hero__actions`.

Two things fail at once with the angle-bracket form, and only the first is visible:

- The opening tag appears as page text. The closing `</#if>` is swallowed, so there is no matching junk further down to grep for.
- **The wrapped content renders unconditionally**, so the toggle silently does nothing — `showCta: false` still shows the button. This half survives any amount of visual checking, because a fragment authored with the toggle *on* looks correct either way.

The syntax rule extends to comments and assignments:

- `<#-- ... -->` is NOT a comment — it renders **visibly on the page**, and a `${...}` inside it is still evaluated (a broken one throws). Use `[#-- ... --]`.
- `<#assign x = ...>` silently does nothing (it's literal text), so the later `${x}` fails "null or missing". Use `[#assign ...]` — or better, skip assignment and chain inline `${...}` interpolations directly in the attribute (`action="${themeDisplay.getPathFriendlyURLPublic()}${themeDisplay.getSiteGroup().getFriendlyURL()}/search"`), letting JS read the value back off the attribute.

Square-bracket directives that DO run bring their own failure modes — each aborts the fragment import or the whole display-page/site import, usually silently:

- **A bare `>` or `>=` inside `[#if …]` is an FTL syntax error** that silently aborts the entire template import (`WARN LayoutsImporterImpl` and a display page with no master, no fragments, no default). Parenthesize the comparison — `[#if (a >= b)]` — or use the `gt`/`gte` operators.
- **`${slug?url}` throws at RENDER time** ("url_escaping_charset must be set") — the fragment context sets no escaping charset, and because the failure is runtime + branch-gated, an offline parse check passes it. Always write `?url('UTF-8')`.
- **HTML comments are parsed too.** FreeMarker macro-call syntax inside `<!-- … -->` still parses, so a commented-out `[@…]` with a typo breaks provisioning with "unclosed @ at EOF". Delete dead directive code; do not comment it out.
- Validate fragment FTL offline with `jshell` + FreeMarker configured for `SQUARE_BRACKET` tag syntax before a reprovision — but remember runtime-only failures (`?url`) still get through.

For a simple show/hide toggle, still prefer no conditional markup at all. Emit every element, publish the flag as a data attribute on the wrapper (this is what `?c` above is for), and branch in CSS:

```html
<div class="my-fragment-wrapper" data-show-cta="${(configuration.showCta!true)?c}">
```

```css
#wrapper .my-fragment-wrapper[data-show-cta='false'] .my-fragment__actions {
	display: none;
}
```

CSS alone is wrong for a **form field** — `display: none` still submits the input. Remove those from the DOM in `index.js` so a switched off field cannot post a value:

```javascript
if (root.getAttribute('data-show-company') === 'false') {
	var field = root.querySelector('[data-optional-field="company"]');

	if (field) {
		field.remove();
	}
}
```

Prefer the data attribute plus CSS form regardless of the directive limitation: it reevaluates live in the page editor as the author flips the checkbox, whereas server side branching would only apply when the page is rendered again.

`index.js` is **not** passed through FreeMarker — only `index.html` is. Verified by shipping `/* ${configuration.layout!'grid'} */` in an `index.js` and reading it back from the served page still literal, on a fragment whose `layout` was set to `rows`. So read configuration in JS from the data attributes; a `${...}` written there is inert rather than interpolated (which also means template literals in `index.js` are safe).

**But an inline `<script>` inside `index.html` IS FreeMarker territory** — the processor runs over the whole file, script blocks included. Two consequences (each throws `FragmentEntryContentException` at site-initializer import and rolls back the **entire atomic site import**, one failure surfacing per rebuild):

- A JS template literal `` `${jsVar}` `` in an inline script: FreeMarker tries to resolve `jsVar` in its own context and fails with "null or missing". Use `+` string concatenation in inline scripts, or move the code to `index.js`. Before a rebuild, sweep for `${` inside every `<script>` block across the fragments — the count should be 0 (server-side FreeMarker outside script blocks is fine).
- A raw boolean printed in an inline script needs `?c`, same as anywhere in `index.html`.

**`<script type="module">` embedded in `index.html` does not get `fragmentElement` injected** — Liferay injects it only into `index.js`. A module script referencing it throws `ReferenceError: fragmentElement is not defined`; get the root with `document.querySelector('.<wrapper-class>')` instead (assumes one instance per page).

To pass localized or configured strings to JS, put them on the wrapper as data attributes in `index.html` (`data-msg-save="${languageUtil.get(locale, 'save')}"`) and read them with a tiny helper (`const MSG = k => root.getAttribute('data-msg-' + k) || ''`) — never `${}` in the JS itself.

### Common Errors and Fixes

- **"HTML content must not be empty"**: check `fragment.json` for incorrect path keys (must be `htmlPath`/`cssPath`/`jsPath`, not `html`/`css`/`js`).
- **"required key [fieldSets] not found"**: check `configuration.json` — fields must be nested inside a `fieldSets` array.
- **FreeMarker Null Pointer**: always provide defaults in HTML — `${configuration.myVar!'Default'}`, or `?has_content` when an unconfigured `""` must fall back too (see "FreeMarker Defaults").
- **"boolean is not a valid enum value"**: a `configuration.json` field declares `"dataType": "boolean"` — checkbox fields take no `dataType`.
- **"Can't convert boolean to string automatically"**: a checkbox value is printed without `?c` — use `${(configuration.myFlag!true)?c}`.
- **A literal `<#if …>` appears as text on the page**: angle-bracket directives are not evaluated in fragment HTML — fragment FreeMarker is square-bracket syntax (`[#if]…[/#if]`). Either convert to square brackets or emit the markup unconditionally and hide it via a data attribute plus CSS. Until then, assume the toggle is also doing nothing.
- **Fragment JS breaks with "Illegal return statement"**: `javaScriptModuleEnabled` defaults to true, so `index.js` is emitted as an **unwrapped `<script type="module">`** — a top-level `return` is a syntax error that kills the whole file. Wrap fragment JS in an IIFE.
- **A download link renders binary garbage inline**: SennaJS SPA-hijacks any `<a>` without `data-senna-off` or `target="_blank"` — a `?download=true` link gets XHR-fetched and rendered as text. The attribute must be on the anchor itself (ancestors don't count); when the anchor comes from a built-in fragment that takes no attributes, set it in fragment JS: `anchor.setAttribute('data-senna-off', '')`.
- **A fragment edit "won't deploy" after an earlier FTL error**: the errored fragment is stuck in Draft, which the initializer upsert never overwrites — clear/publish the Draft in the Fragments editor, then re-fire. See `rules/site-initializer-format.md` → "Applying a Fragment Change".
- **A new fragment imports as a Draft with no published version, and later edits never land**: check for an editable id that appears twice in `index.html`, for example the same `data-lfr-editable-id` in both branches of an `[#if]…[#else]…[/#if]`. The import validates the raw HTML, branches included, and the duplicate silently leaves the fragment a Draft (`fragmententry.status` 2, `head` false, no published row), with nothing in the log. Pages placed in that first run still render its first markup, so it looks deployed, but every later re-fire keeps that stale copy. Keep one element per editable id and vary its attributes in FreeMarker instead (`aria-level="[#if …]2[#else]3[/#if]"`). Once the HTML was valid, the next re-fire replaced the Draft with a published version (verified on 2026.Q2.7).

### Sharing Code Between Fragments: an Import-Map Module

Two fragments that need the same few hundred lines of JS can import them from one `jsImportMapsEntry` client extension instead of each carrying a copy:

```yaml
<project>:
    bareSpecifier: "@<org>/<module>"
    scope: company
    type: jsImportMapsEntry
    url: <module>.*.js
```

With an `assemble` block that `hashify`s the file into `static`. Each fragment's `index.js` (a module script) then does `import('@<org>/<module>').then(({mount}) => mount({fragmentElement, fragmentElementId, input, …}))`, passing the injected variables the module cannot see. Verified on 2026.Q3.5 with two input fragments:

- The specifier is in every page's import map once the CX is `STARTED`, and the file downloads only on a page whose fragment imports it, unlike a `globalJS`.
- A `jsImportMapsEntry` is a frontend type, so it cannot share a project with a `siteInitializer` (`rules/client-extension-types.md`); deploy it as its own project. The order between the two does not matter: the import resolves when the page runs.
- A plain ES module needs no build. Inject its CSS once from the module (a `<style>` with an id), so the fragments carry none.
- Catch the import's failure in the fragment and say so on the page: without the CX the field is otherwise dead.

### Server-Side Reads With `restClient`

`index.html` can read Headless APIs while the page renders, through the global `restClient` (`RESTClientTemplateContextContributor`). Paths omit `/o`: `restClient.get("/c/<plural>/" + id?c + "/<relationshipName>?nestedFields=…")`. The result is a FreeMarker hash, so read `(resp.items)![]`, `(m.actions.update)??` and `(resp.actions.create)??` exactly like the JSON.

**On stock 2026.Q3.5 it runs as the signed-in user**, through `VulcanRequestForwarder`, and as the Guest user for anonymous visitors. Verified against an owner-only object: each user's seed held exactly their own rows, and `my-user-account` named them. Each row's `actions` and the collection's `create` were those of that user, and Guest got 0 rows and no `create`. So a fragment can seed a list's first page and decide **Add, Edit and Delete by permission**, with no client fetch. On 2026.Q2.7 the helper forwarded as Guest, and protected rows rendered empty (LPD-103304, which the CRM workspace patches).

Wrap each call in `[#attempt]…[#recover][/#attempt]`, because a denied or failing read throws. During a site-initializer import, the fragment validator swaps in a `DummyRESTClient` (`FreeMarkerFragmentEntryValidator:136–137`), so a seeding fragment imports cleanly.

### The Live Fragment API

`headless-admin-fragment` exists and works. Base URI `/o/headless-admin-fragment/v1.0`, gated by `LPD-39244` (beta on 2026.Q3, hidden dev on 2026.Q2). **It does not exist on 2026.Q1**, so reprovision there. Verified end to end on a self hosted 2026.Q2 bundle — create, update and delete all returned `200`/`204` and round tripped correctly.

| Resource | Method | Path |
| --- | --- | --- |
| List fragment sets | GET | `/sites/{siteERC}/fragment-sets` |
| List fragments in a set | GET | `/sites/{siteERC}/fragment-sets/{setERC}/fragments` |
| Create a fragment | POST | `/sites/{siteERC}/fragment-sets/{setERC}/fragments` |
| Get / replace / delete a fragment | GET, PUT, DELETE | `/sites/{siteERC}/fragments/{fragmentERC}` |

The code lives in `fragmentVersions[]`, which carries `html`, `css`, `js`, `configuration` and `status`. Fragments are addressed by **external reference code** — a generated UUID for anything the initializer imported, *not* the fragment key. `GET /sites/{siteERC}/fragments/<key>` returns `404`; list the set first to resolve the ERC.

**Do not reach for this to iterate on a placed fragment** — see "Iterating on a fragment" above. Page instances hold their own copy, so a `PUT` changes the library entry and nothing the visitor sees. What it is genuinely good for:

- **Inspecting what actually imported.** Reading back the `html`/`css` the initializer produced is the fastest way to confirm a tree change landed, and it beats grepping rendered page source.
- **Authoring a fragment that no page places yet**, so an author can drop it in the page editor.
- **Auditing** — enumerating what exists in a site's sets.

#### One Bad `type` Makes the Whole Set Unlistable

`Fragment.type` is an enum whose **only** accepted value is `Component`. A fragment whose `fragment.json` declares `"type": "section"` (or `input`, or `react`) imports fine and renders fine, but poisons the listing endpoint for **every fragment in its set**:

```text
400 BAD_REQUEST — Invalid enum value: Section
```

The failure is at serialization, so it is not per item and a `filter` does not route around it — one bad fragment and no ERC in that set can be discovered at all. Always write `"type": "component"` in `fragment.json`, whatever role the fragment plays on the page; the "Fragment Types" table at the top of this skill describes *intent*, not this field's value.

### Programmatic Placement on Pages

For inserting a fragment into a Content Page via the headless API (rather than the UI or a site initializer), follow the placement gotchas in `manage-pages` → "Custom Fragment Placement via the Headless API". Two points that affect authoring:

- **Custom fragments must inline `html` and `css` on the `fragmentInstance`** at placement time. The source files generated here ARE the content that gets inlined — keep them selfcontained so the placement does not pull in fragile external dependencies.
- The fragment's `fragmentEntryKey` IS its external reference code in placement payloads — choose a stable, kebab-case key.

## Success Signal

TODO / inferred — verify against a running bundle. The `### Verify` step above (fragment appears in the editor's Fragments panel, editable regions highlight, no console or import errors) is the observable completion check; confirm on a live bundle.