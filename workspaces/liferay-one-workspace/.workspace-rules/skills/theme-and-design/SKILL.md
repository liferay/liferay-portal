---

description: Customize the visual appearance of a Liferay site using themeCSS client extensions, master pages, and style books. Use when the user asks to change colors or fonts, create a theme, set up a style book, define a master page header/footer, or apply WCAG accessibility guidelines.
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

## Workflow

### Layer 1: themeCSS Client Extension

A `themeCSS` CET injects custom CSS that overrides Clay Design System variables. This replaces the legacy Liferay theme WAR.

> **Two constraints to settle before writing any CSS.**
>
> 1. **It needs its own project.** `themeCSS` is classified `frontend`; a `siteInitializer` is `batch`, and the workspace plugin forbids that combination. The theme is always a sibling project — see `rules/client-extension-types.md` → "Which Types May Share a Project".
>
> 1. **It cannot be applied from the initializer tree.** Liferay attaches a themeCSS CET via a `ClientExtensionEntryRel` on the layout, master layout, or layout set. `BundleSiteInitializer` has no handler for that relation and `metadata.json` has no key for it, so a deployed CET is available but inert until someone selects it in Site Administration → Design → Theme — and that selection does not survive a reprovision.
>
> So decide up front where each part of the look lives. Anything that must survive delete and redeploy belongs in the style book, the master page, fragment CSS, or layout set settings. Use the themeCSS CET for Clay level overrides that have no style book token (Classic exposes no `headings*` tokens), and say plainly that it needs the manual selection step.

#### Scaffold

```
client-extensions/<name>/
  client-extension.yaml
  package.json         # drives the theme build
  src/
    css/
      _custom.scss     # Clay variable overrides
```

`package.json` is what compiles the SCSS — without it there is no `build/buildTheme` output for the URLs below to point at:

```json
{
	"liferayDesignPack": {
		"baseTheme": "styled"
	},
	"main": "package.json",
	"name": "@liferay/<name>",
	"version": "0.0.0"
}
```

**`client-extension.yaml`:**

```yaml
<workspace-id>-theme-css:
  clayRTLURL: css/clay_rtl.css
  clayURL: css/clay.css
  mainRTLURL: css/main_rtl.css
  mainURL: css/main.css
  name: <WorkspaceId> Theme CSS
  type: themeCSS
```

The keys are `mainURL` and `clayURL` — capital `URL`. All four paths are outputs of the theme build (`build/buildTheme/css/`), not files you author.

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

#### Apply to Site

After deployment, go to Site Administration → Design → Theme → Configure and select the deployed theme CSS client extension.

### Layer 2: Style Book

A style book maps Clay token names to site specific values. It overrides the themeCSS tokens without touching the code. Unlike a themeCSS CET, a style book **can** be applied from the initializer tree, which makes it the reproducible half of the theme layer.

#### Author It in the Tree

`site-initializer/style-books/<name>/style-book.json` — `defaultStyleBookEntry` is what applies it to the site with no manual step:

```json
{
	"defaultStyleBookEntry": true,
	"frontendTokensValuesPath": "frontend-tokens-values.json",
	"name": "<Style Book Name>",
	"themeId": "classic_WAR_classictheme"
}
```

`frontend-tokens-values.json` beside it, one entry per token:

```json
{
	"primaryColor": {
		"cssVariableMapping": "primary",
		"value": "#14284b"
	}
}
```

#### Token Names Must Exist in the Theme — Unknown Ones Are Dropped Silently

A token only takes effect if its name is declared in the active theme's `frontend-token-definition.json`. An invented name is discarded with **no error**: the initializer logs `addStyleBookEntries took N ms`, nothing warns, and the page keeps the default value. Do not guess — enumerate first:

Each entry needs **two** things — the token `name` (the JSON key) and its `cssVariableMapping`. The mapping is not derivable from the name (`primaryD1Color` → `primary-d1`, `btnPrimaryBackgroundColor` → `btn-primary-background-color`, `fontFamilyBase` → `font-family-base`), so dump both at once rather than guessing the kebab-case form:

Read it from the **deployed theme in the bundle**, not from portal source. A Liferay Workspace is not a portal checkout — `modules/apps/frontend-theme/...` does not exist there, and reaching for it sends you looking for a repository you may not have. The definition ships inside the theme WAR:

```bash
unzip -p bundles/osgi/portal-war/classic-theme.war WEB-INF/frontend-token-definition.json > /tmp/tokens.json

python3 -c "
import json
d = json.load(open('/tmp/tokens.json'))
for c in d['frontendTokenCategories']:
    for s in c.get('frontendTokenSets', []):
        for ft in s.get('frontendTokens', []):
            for m in ft.get('mappings', []):
                print(f\"{ft['name']:36s} -> {m.get('value')}\")" | sort -u
```

This has the added benefit of describing the theme **actually running**, so the list matches the release under test rather than whatever branch is checked out. Verified on a 2026.Q2 bundle: 252 tokens, and no `headings*` among them.

Filter that list to what you need (`grep --extended-regexp '^(primary|btnPrimary|body|font|h[1-6])'`) rather than reading all 252. For a non Classic theme, substitute its WAR under `bundles/osgi/portal-war/` (or `bundles/osgi/war/` for a deployed custom theme).

Classic declares 252 tokens. Useful ones: `bodyBgColor`, `bodyColor`, `primaryColor`, `primaryD1Color`, `primaryD2Color`, `primaryL1Color`…`primaryL3Color`, `secondaryColor`, `warningColor`, `warningD1Color`, `fontFamilyBase`, `fontSizeBase`, `fontWeightBold`, `fontWeightBolder`, `h1FontSize`…`h6FontSize`, and the full `btnPrimary*` / `btnSecondary*` sets (`BackgroundColor`, `BorderColor`, `Color`, and their `Hover` variants).

Recoloring buttons takes the whole `btnPrimary*` set, not just the background — leaving `btnPrimaryColor` alone gives white label text on a light accent.

**Classic has no `headings*` tokens at all** — `headingsColor`, `headingsFontFamily`, and `headingsFontWeight` are all plausible and all ignored. Only the *sizes* are tokenized (`h1FontSize`…`h6FontSize`); heading **family** and **weight** have no token.

##### Site Wide CSS With No Token — Put It in a Master Page Fragment

This is the gap the two constraints at the top of this skill create: the value has no style book token, and a themeCSS CET reverts to unselected on every reprovision. The reproducible answer is the **master page's header fragment**.

A fragment's `index.css` is a stylesheet on the page, so its rules can target anything — they are not confined to the fragment's markup. A fragment placed in the master page loads on **every page using that master**, which makes it the one place site wide CSS can live and still survive delete and redeploy:

```css
/* site-header/index.css — deliberately NOT scoped to the fragment wrapper */

#wrapper h1,
#wrapper h2,
#wrapper h3 {
	font-family: 'Your Face', -apple-system, 'Segoe UI', Roboto, Arial, sans-serif;
	font-weight: 800;
	letter-spacing: -0.022em;
}
```

This is a deliberate exception to "Fragment Scoping" in `scaffold-fragment` — that rule exists to stop *accidental* cascade leakage between sibling fragments on one page. Keep the exception narrow (headings and brand chrome), keep it in the master's fragment rather than a content fragment, and comment why, or the next reader will "fix" it back into scope.

##### Tag Selectors Alone Miss Every Overridden Heading

A `fragmentFields` literal in `page-definition.json` replaces the editable region's **inner markup**, so a title authored as `<div class="…__title"><h2>…</h2></div>` renders as that `div` with bare text — no `<h2>` survives (see `skills/manage-pages/SKILL.md` → "A Literal Override Replaces the Editable's Inner Markup").

The rule above therefore matches the fragment's *default* heading and skips every placement that sets its own text — which, on a real site, is most of them. Name the title wrapper classes alongside the tags:

```css
#wrapper h1,
#wrapper h2,
#wrapper h3,
#wrapper .conference-hero__title,
#wrapper .section-heading__title {
	/* family, weight, tracking */
}
```

Verified on 2026.Q2: with tags only, `getComputedStyle` on an overridden hero title returned the body font at weight `700`, while the untouched default beside it rendered correctly — so the page looked *mostly* branded and the failure read as a font loading problem rather than a selector one.

##### Every Content Fragment Outranks This Rule — Strip Their Heading Weights

The selector above is one ID plus a **type**, so any fragment styling its own heading through a class beats it. `scaffold-fragment` requires exactly that form (`#wrapper .<wrapper-class>`), so the collision is the default outcome, not an edge case:

```css
#wrapper h1                        /* master rule   — 1 id, 0 classes, 1 type */
#wrapper .conference-hero__title   /* fragment rule — 1 id, 1 class          */
```

The fragment wins. The failure is quiet because it is partial: family and tracking still apply (the fragment did not set those), so headings look *almost* right while `font-weight` silently stays at whatever the fragment declared. Verified on 2026.Q2 — a site wide rule asking for `800` computed to `700` on every heading whose fragment set `font-weight: 700`, and reads as "the heavy font did not take".

Fix it at the fragments, not by escalating the master selector. **Delete `font-weight` from every heading rule in every content fragment** so the master rule is the single source of truth:

```bash
grep --before-context=3 --line-number 'font-weight' */index.css | grep --extended-regexp '__title|__heading'
```

Then confirm the computed value in the browser rather than trusting the stylesheet, because the cascade is the whole question:

```javascript
getComputedStyle(document.querySelector('.my-fragment__title')).fontWeight;
```

Leave explicit weights on non headings (`<p>` date stamps, buttons, uppercase micro labels) — those are not governed by the rule and often want a different weight on purpose.

**Set family, weight, and tracking here — never `color`.** Headings must inherit color from their section, or the same rule that styles dark text on the light content areas also paints the hero `h1` and footer `h2` in that color, on their own dark background. The snippet above omits `color` for this reason; adding it is invisible on a light only site and renders headings unreadable the moment a navy hero or footer exists.

Weigh it against the alternatives before reaching for it:

| Need | Put it in |
| --- | --- |
| Value has a style book token | Style book — always first choice |
| Site wide, no token, must survive reprovision | Master page fragment CSS (above) |
| Site wide, no token, reprovision not a concern | `themeCSS` CET + manual Design → Theme selection |
| Truly instance wide, every site | `globalCSS` CET (no per site scoping available) |
| One section's look | That fragment's own scoped CSS |

A webfont still needs a file served from a CET; a system font stack at weight 800/900 gets a heavy sans with no external dependency.

Verify the book landed by fetching a page and checking the override lands *after* the theme default — two declarations, e.g. `--primary: #0b5fff;` from Classic then `--primary: #14284b;` from the book. One declaration means the tokens were dropped.

> **Verify the StyleBook write shape against the OpenAPI spec** (`get-openapi` MCP tool, or `GET /o/headless-admin-content/v1.0/openapi.json`). On the current API the `StyleBook` DTO exposes only `key`/`name` over `headless-admin-content`; `tokenValues`/`styleBookEntryId` shown below may not be accepted (the book would be created without tokens). If so, author the style book in the site initializer tree (`style-books/<name>/style-book.json`) or set tokens in Site Administration → Design → Style Book.

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

### Layer 3: Master Page

Master pages define the persistent header and footer that surround all Content Pages assigned to that master.

#### Create via API

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

## Success Signal

After deploying and assigning:

1. Open the site home page in the browser.

1. Inspect the `<head>` for `<link>` tags referencing the `main.css` from the themeCSS CET.

1. Confirm brand colors appear in primary buttons and headings.

1. Run a browser contrast audit (DevTools → Accessibility) to validate WCAG compliance.