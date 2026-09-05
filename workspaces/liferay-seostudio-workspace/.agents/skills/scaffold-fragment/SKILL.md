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

For complex interactive widgets, use `scaffold-client-extension` with type `customElement` and reference it from the fragment's HTML with a `<custom-element-name>` tag.

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
  <div data-lfr-editable-id="title" data-lfr-editable-type="rich-text">
    <h2>Heading</h2>
  </div>
  <div data-lfr-editable-id="body" data-lfr-editable-type="rich-text">
    <p>Body text here.</p>
  </div>
  <a data-lfr-editable-id="link" data-lfr-editable-type="link" href="#">
    Learn more
  </a>
</div>
```

Editable type values: `rich-text`, `text`, `image`, `link`, `html`, `background-image`.

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
/* Fragment JS — runs once per fragment instance on the page */
const fragmentElement = fragmentNamespace.element;

// FragmentElement is the fragment's root DOM element
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

**Iterating on a fragment — reprovision from the tree:**
Edit the source files in the site initializer tree and reprovision the site — delete the site, then redeploy the initializer CET so it recreates the site from the current tree. See `rules/site-initializer-format.md` for the reprovision recipe. Object data is company scoped and survives the reprovision.

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

## Patterns and Gotchas

### `fragment.json` Path Keys — Hallucination Warning

Do not use `html` or `css` keys in `fragment.json`. The valid keys are `htmlPath`, `cssPath`, `jsPath`, `configurationPath`, `thumbnailPath`. Using the short forms produces "HTML content must not be empty" or silent file not found errors.

### Configuration Field Types — Valid vs. Invalid

Valid types for `configuration.json` fields: `text`, `select`, `checkbox`, `colorPicker`, `length`, `url`, `itemSelector`, `videoSelector`.

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

### Drop Zones

To create a container fragment that accepts nested content, use the `<lfr-drop-zone>` tag — exact spelling, not `lfr-dropzone` or any other variant.

- Each drop zone must have a unique `id` attribute within the fragment.
- In edit mode, drop zones are visible as highlighted regions; they render invisibly in view mode.

```html
<div class="my-layout-wrapper">
  <div class="col-left">
    <lfr-drop-zone id="zone-left"></lfr-drop-zone>
  </div>
  <div class="col-right">
    <lfr-drop-zone id="zone-right"></lfr-drop-zone>
  </div>
</div>
```

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

#### Only `${...}` Interpolation Runs — Block Directives Do Not

`index.html` resolves `${...}` interpolation, and **nothing else**. A FreeMarker block directive is not evaluated: it is HTML escaped and emitted into the page as visible text. Verified on a self hosted 2026.Q2 bundle, where

```html
<#if (configuration.showCta!true)>
	<div class="hero__actions">…</div>
</#if>
```

rendered the literal string `<#if (configuration.showCta!true)>` on the live page, directly above a fully rendered `hero__actions`.

Two things fail at once, and only the first is visible:

- The opening tag appears as page text. The closing `</#if>` is swallowed, so there is no matching junk further down to grep for.
- **The wrapped content renders unconditionally**, so the toggle silently does nothing — `showCta: false` still shows the button. This half survives any amount of visual checking, because a fragment authored with the toggle *on* looks correct either way.

Write no conditional markup at all. Emit every element, publish the flag as a data attribute on the wrapper (this is what `?c` above is for), and branch in CSS:

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

### Common Errors and Fixes

- **"HTML content must not be empty"**: check `fragment.json` for incorrect path keys (must be `htmlPath`/`cssPath`/`jsPath`, not `html`/`css`/`js`).
- **"required key [fieldSets] not found"**: check `configuration.json` — fields must be nested inside a `fieldSets` array.
- **FreeMarker Null Pointer**: always provide defaults in HTML — `${configuration.myVar!'Default'}`.
- **"boolean is not a valid enum value"**: a `configuration.json` field declares `"dataType": "boolean"` — checkbox fields take no `dataType`.
- **"Can't convert boolean to string automatically"**: a checkbox value is printed without `?c` — use `${(configuration.myFlag!true)?c}`.
- **A literal `<#if …>` appears as text on the page**: block directives are not evaluated in fragment HTML. Emit the markup unconditionally and hide it via a data attribute plus CSS. Assume the toggle is also doing nothing.

### The Live Fragment API

`headless-admin-fragment` exists and works. Base URI `/o/headless-admin-fragment/v1.0`, gated by `LPD-39244`. Verified end to end on a self hosted 2026.Q2 bundle — create, update and delete all returned `200`/`204` and round tripped correctly.

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