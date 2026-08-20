---

description: Create a Liferay page fragment — an HTML/CSS/JS building block that marketers drop onto Content Pages. Use when the user asks to create a fragment, build a hero section, or make a reusable page component. Maps to the Frontend Developer Learning Path and "Mastering Liferay Pages and Navigation".
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
| Form | Entry form tied to an object | `data-lfr-form-*` attributes |
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
There is **no stable headless endpoint for importing a fragment collection** into a running site — do not assume a live collection import call exists; verify the live API surface before relying on any such endpoint (the `get-openapi` MCP tool, or fetch the relevant module's `GET /o/<module>/v1.0/openapi.json` with curl). The portable way to apply a fragment change is to edit the source files in the site-initializer tree and reprovision the site — delete the site, then redeploy the initializer CET so it recreates the site from the current tree. See `rules/site-initializer-format.md` for the reprovision recipe. Object data is company scoped and survives the reprovision.

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

### Common Errors and Fixes

- **"HTML content must not be empty"**: check `fragment.json` for incorrect path keys (must be `htmlPath`/`cssPath`/`jsPath`, not `html`/`css`/`js`).
- **"required key [fieldSets] not found"**: check `configuration.json` — fields must be nested inside a `fieldSets` array.
- **FreeMarker Null Pointer**: always provide defaults in HTML — `${configuration.myVar!'Default'}`.

### Headless Fragment CRUD Is Inconsistent

Headless fragment endpoints are not consistent across DXP versions: there is no portable fragment collection import endpoint, and per fragment CRUD (create, update, delete an individual fragment outside a collection) is not consistently available. Verify the live API surface before scripting any fragment workflow (the `get-openapi` MCP tool, or fetch the relevant module's `GET /o/<module>/v1.0/openapi.json` with curl); otherwise use the site initializer tree (reprovision to apply changes) or the portal UI.

### Programmatic Placement on Pages

For inserting a fragment into a Content Page via the headless API (rather than the UI or a site initializer), follow the placement gotchas in `manage-pages` → "Custom Fragment Placement via the Headless API". Two points that affect authoring:

- **Custom fragments must inline `html` and `css` on the `fragmentInstance`** at placement time. The source files generated here ARE the content that gets inlined — keep them selfcontained so the placement does not pull in fragile external dependencies.
- The fragment's `fragmentEntryKey` IS its external reference code in placement payloads — choose a stable, kebab-case key.