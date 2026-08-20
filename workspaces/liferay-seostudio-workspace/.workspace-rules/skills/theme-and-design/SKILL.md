---

description: Customize the visual appearance of a Liferay site using themeCSS client extensions, master pages, and style books. Use when the user asks to change colors or fonts, create a theme, set up a style book, define a master page header/footer, or apply WCAG accessibility guidelines. Maps to "Mastering Liferay Design Elements".
name: theme-and-design

---

# Theme and Design

Three layers control a site's look and feel. Apply them in this order: themeCSS (base variables and overrides), style book (token values per site), master page (header/footer layout).

When iterating on a site built from a site initializer, theme changes apply **live** via `blade gw deploy` of the `themeCSS` CET — no site reprovision needed (see `rules/site-initializer-format.md`).

## When to Invoke

- "Change the site colors", "apply a brand theme", "create a custom theme"
- "Set up a style book with our design tokens"
- "Define a header and footer for all pages"
- "Create a dark variant of the site"
- Called by `build-site` when the user specifies visual design requirements

## Layer 1: themeCSS Client Extension

A `themeCSS` CET injects custom CSS that overrides Clay Design System variables. This replaces the legacy Liferay theme WAR.

### Scaffold

```
client-extensions/<name>/
  client-extension.yaml
  src/
    css/
      _custom.scss     # Clay variable overrides
      main.scss        # Entry point
```

**`client-extension.yaml`:**

```yaml
<workspace-id>-theme-css:
  clayVersion: "3"
  mainUrl: "css/main.css"
  name: <WorkspaceId> Theme CSS
  type: themeCSS
```

**`src/css/_custom.scss`** — Clay variable overrides:

```scss
// Brand colors

$primary: #0B5FFF;
$secondary: #6B7280;
$success: #287D3C;
$danger: #DA1414;

// Typography

$font-size-base: 1rem;
$font-family-base: "Inter", sans-serif;
$headings-font-weight: 700;

// Border radius

$border-radius: 0.5rem;
$border-radius-lg: 1rem;

// Shadows

$box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12);
```

**`src/css/main.scss`:**

```scss
@import "custom";
```

Build the SCSS to CSS: `blade gw buildClientExtension` or configure the Sass build in `build.gradle`. Then run `deploy-and-verify`.

### Apply to Site

After deployment, go to Site Administration → Design → Theme → Configure and select the deployed theme CSS client extension.

## Layer 2: Style Book

A style book maps Clay token names to site specific values. It overrides the themeCSS tokens without touching the code.

> **Verify the StyleBook write shape against the OpenAPI spec** (`get-openapi` MCP tool, or `GET /o/headless-admin-content/v1.0/openapi.json`). On the current API the `StyleBook` DTO exposes only `key`/`name` over `headless-admin-content`; `tokenValues`/`styleBookEntryId` shown below may not be accepted (the book would be created without tokens). If so, author the style book in the site-initializer tree (`style-books/<name>/style-book.json`) or set tokens in Site Administration → Design → Style Book.

```bash
curl \
	--data '{
		"name": "<Style Book Name>",
		"styleBookEntryId": 0,
		"tokenValues": {
			"bodyBg": "#FFFFFF",
			"primaryColor": "#0B5FFF",
			"borderRadius": "0.5rem"
		}
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-content/v1.0/sites/<site-id>/style-books" \
	--user "test@liferay.com:test"
```

Save the returned `id`. Apply the style book to the site via Site Administration → Design → Style Book → select.

Consult learn.liferay.com for the full style book token reference (search `style book tokens`).

## Layer 3: Master Page

Master pages define the persistent header and footer that surround all Content Pages assigned to that master.

### Create via API

```bash
curl \
	--data '{
		"name": "<Master Page Name>",
		"pageDefinition": {
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
								"unallowedFragments": []
							}
						},
						"type": "DropZone"
					}
				],
				"type": "Root"
			},
			"version": 1.1
		}
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-site/v1.0/sites/<site-erc>/master-pages" \
	--user "test@liferay.com:test"
```

The `DropZone` element must carry a `definition.fragmentSettings` block — a **bare** `{"type": "DropZone"}` does not render, and the master shows an empty body. Use `{"definition": {"fragmentSettings": {"unallowedFragments": []}}, "type": "DropZone"}` so page specific content can be placed inside the master. Note `version` is the **number** `1.1` (not the string `"1.0"`) for a master with a working drop zone. Custom header/footer fragments are referenced by `key` (the fragment's directory name) + `siteKey` (`[$GROUP_KEY$]`), exactly as in a page `page-definition.json` (see `manage-pages`); the content area is a `DropZone` element, **not** a `LAYOUT_DROP_ZONE` fragment. Master pages are typically authored in the initializer tree under `layout-page-templates/master-pages/<name>/page-definition.json` (where `pageDefinition`/`page-definition.json` is the correct importer format). Note: the **live** `headless-admin-site` `MasterPage` DTO uses `pageSpecifications`, not `pageDefinition` — prefer the initializer path, or verify the live write shape against the OpenAPI spec (`get-openapi` MCP tool, or `GET /o/headless-admin-site/v1.0/openapi.json`).

Consult learn.liferay.com for the full master page template reference (search `master page templates`).

## Accessibility

Follow WCAG 2.1 AA as the baseline:

- Color contrast: minimum 4.5:1 for normal text, 3:1 for large text (18pt+). Use a contrast checker before finalizing the `$primary` color.
- Focus indicators: ensure Clay's default focus ring is not overridden to `outline: none` without a replacement.
- Skip link: include a `<a href="#main-content" class="skip-link">Skip to main content</a>` in the master page header.
- Image alt text: enforce via `data-lfr-editable-type="image"` regions, which prompt editors to provide alt text.

## Favicon and Spritemap

To replace the Liferay favicon or icon spritemap, use the companion CET types:

```yaml
<workspace-id>-favicon:
  url: "images/favicon.ico"
  type: themeFavicon

<workspace-id>-spritemap:
  url: "images/icons.svg"
  type: themeSpritemap
```

Deploy alongside the `themeCSS` CET.

## globalCSS Versus themeCSS

A `themeCSS` CET overrides Clay Design System variables and is selected per site under Design → Theme. A `globalCSS` CET is different: it injects a plain CSS file on **every page** automatically as soon as it is deployed — there is no per site selection and **no Instance Settings or manual enablement step**. Scope is controlled on the CET itself (`scope: company` injects instance wide; omitting `scope` uses the default):

```yaml
<workspace-id>-global-css:
  assemble:
    - from: assets
      hashify: global.css
      into: static
  name: <WorkspaceId> Global CSS
  scope: company
  type: globalCSS
  url: global.*.css
```

Use `globalCSS` for site agnostic CSS that must always load; use `themeCSS` for brand tokens applied through the theme picker.

## Verify

After deploying and assigning:

1. Open the site home page in the browser.

1. Inspect the `<head>` for `<link>` tags referencing the `main.css` from the themeCSS CET.

1. Confirm brand colors appear in primary buttons and headings.

1. Run a browser contrast audit (DevTools → Accessibility) to validate WCAG compliance.