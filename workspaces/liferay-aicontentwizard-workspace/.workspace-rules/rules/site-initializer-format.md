# Site Initializer Format

> **Before authoring:** Load the skill for whatever you are about to author into this tree — `manage-objects`, `manage-pages`, `scaffold-fragment`, `theme-and-design`, or `manage-roles-permissions`. This card is the file format; the skills hold the failure modes.

Reference for the `manage-environments` and `scaffold-client-extension` skills when capturing or building a `siteInitializer` CET.

A site initializer is a client extension of type `siteInitializer`. When deployed and triggered, it creates a fully configured site from the directory tree below.

## Provisioning and Iteration

The site-initializer CET tree is the **single source of truth** for the site. Build the site by triggering the initializer, then iterate by editing the source tree and applying each change by the cheapest reliable path. Always edit the source files first; never hand edit the live site as the authoritative copy.

| Change Type | How to Apply It | Reprovision the Site? |
| --- | --- | --- |
| Theme / design | Deploy the `themeCSS` CET (`blade gw deploy`) | No |
| Object definition or data | `object-admin` API, or edit `object-definitions/` and reprovision | No |
| Fragment content or new fragment | Edit the source tree, then reprovision. A live fragment API exists but does **not** propagate to pages already placing the fragment | Yes |
| New page, page composition change, or fragment placement on a page | Reprovision: delete the site, recreate from the initializer | Yes |

A page composition change — adding or rearranging fragments **on an existing page**, not just adding new pages — also requires a reprovision. Retriggering the initializer upserts pages but does not retrofit composition changes onto pages that already exist, so edited `page-definition.json` content only takes effect after the site is recreated.

The initializer runs **once, at site creation** — redeploying the CET does not update an existing site. To reapply `layouts/` changes, reprovision by **deleting the site, then redeploying the initializer CET**:

```bash
# Step 1. Delete the existing site — address it by its EXTERNAL REFERENCE CODE in the path,
#    not its numeric ID (a numeric ID in this path returns 404).

curl \
	--request DELETE \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-site/v1.0/sites/<site-erc>" \
	--user "test@liferay.com:test"

# Step 2. Redeploy the initializer CET. A siteInitializer CET that declares a
#    siteExternalReferenceCode autoprovisions the site on deploy.
#
#    Liferay retriggers the initializer when the file install watcher sees a CHANGED
#    zip in bundles/osgi/client-extensions — not when the Gradle task runs. Gradle's
#    up to date check is content based, so with no source edit `blade gw deploy`
#    prints BUILD SUCCESSFUL / "N up to date", rewrites nothing, and the site stays
#    deleted. Force a new artifact when reprovisioning without a source change:

rm -f bundles/osgi/client-extensions/<name>.zip

cd client-extensions/<name> && blade gw clean deploy
```

Confirm the initializer actually ran — the count must increase, and `BUILD SUCCESSFUL` alone does not prove it did:

```bash
grep --count "Initializing <Site Name>" bundles/tomcat/logs/catalina.out
```

### Do the Whole Cycle in One Command

Run the steps by hand and you will eventually wait on a counter that already advanced, or read the previous run's log as if it were this one. Both cost minutes and neither announces itself. Capture the count **before** deleting, then block until it exceeds that:

```bash
#!/usr/bin/env bash

#
# reprovision.sh — delete the site, redeploy the initializer, wait, report.
#

set -o errexit
set -o nounset
set -o pipefail

SITE_ERC="${SITE_ERC:?set SITE_ERC}"
SITE_NAME="${SITE_NAME:?set SITE_NAME}"     # The "Initializing <SITE_NAME>" log string
CET="${CET:?set CET}"                       # The client-extensions/<CET> directory
PORT="${PORT:-8080}"
LOG=bundles/tomcat/logs/catalina.out

before=$(grep --count "Initializing ${SITE_NAME}" "${LOG}" || echo 0)

curl \
	--output /dev/null \
	--request DELETE \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-site/v1.0/sites/${SITE_ERC}" \
	--user "test@liferay.com:test"

rm -f "bundles/osgi/client-extensions/${CET}.zip"
(cd "client-extensions/${CET}" && blade gw clean deploy >/dev/null)

for _ in $(seq 1 100); do
	[[ $(grep --count "Initializing ${SITE_NAME}" "${LOG}" || echo 0) -gt ${before} ]] && break
	sleep 3
done

sleep 6   # Let the handlers finish after the "Initializing" line appears

awk -v n=$((before + 1)) '/Initializing '"${SITE_NAME}"'/{c++} c>=n' "${LOG}" \
	| grep --extended-regexp 'Invoking|Initialized|InitializationException|MustNotBeReserved'
```

Two details that are easy to get wrong by hand:

- **`before` must be read before the delete**, not after the deploy. Reading it late means comparing the new run against itself, and the loop spins until it times out on a build that actually succeeded.
- **The `Initializing` line is not the end of the run.** Handlers keep logging after it, so sampling immediately shows a partial list and a missing handler reads as a failure. The `sleep 6` covers this; for a large tree, assert on the `Initialized <Site> ... in N ms` line instead.

The final `awk` prints only the newest run, which is the comparison that matters — see "Verifying What the Initializer Actually Did" below for reading the timings.

**Sites are addressed by external reference code in the REST path** (`/sites/<erc>`) for these admin operations — passing the numeric site ID returns 404.

> Prefer delete then redeploy over recreating with `POST /sites` + `templateExternalReferenceCode` (or `templateKey`): resolving a site-initializer template through that POST is unreliable. The portable path relies on the CET's `siteExternalReferenceCode` to autoprovision on deploy.

Reprovisioning is always safe because the source tree is current. **Object definitions and entries are company scoped and survive site deletion**, so runtime data (for example, registration entries) persists across a reprovision. The exception is an object whose scope is explicitly set to the site — those entries are deleted with the site.

### Applying a Fragment Change

Fragments live in the initializer tree under `fragments/group/<collection-key>/fragments/<fragment-name>/`. To change a fragment, edit those source files and reprovision the site (delete then redeploy, above).

The `headless-admin-fragment` API **does** expose per fragment create/update/delete, and it is easy to mistake for a shortcut. It is not one: Liferay copies fragment code into each page's fragment instance at placement time, so a `PUT` updates the library entry and leaves every page already using it unchanged — verified on 2026.Q2. See `skills/scaffold-fragment/SKILL.md` → "The Live Fragment API" for what it is actually useful for.

## Directory Tree

```
client-extensions/<name>/
  client-extension.yaml
  site-initializer/
    documents/                    # Documents and media
      group/
        <folder-name>/
          <filename>
    fragments/                    # Page fragments
      group/
        <collection-key>/
          collection.json         # {"name": ..., "description": ...}
          fragments/              # required nesting level
            <fragment-name>/      # dir name becomes the fragment "key"
              fragment.json
              index.html
              index.css
              index.js
              configuration.json  # optional
    journal-articles/             # Web content (Journal) articles
      <article-name>.xml
      <article-name>.json
    layout-page-templates/        # Page templates and master pages
      display-page-templates/
        <template-name>/
          page-definition.json
          page-template.json
      master-pages/
        <master-name>/          # dir name is the master "key" (see settings.masterPage.key)
          page-definition.json
          master-page.json       # {"name": ...} — NOT page-template.json
          thumbnail.png          # optional
    layout-set/                   # Site wide navigation and theme settings
      public/
        metadata.json
    layouts/                      # Site pages
      <NN-page-name>/             # NN prefix controls creation order
        page.json                 # Page metadata (type, name, friendlyURL)
        page-definition.json      # Content Page fragment composition
        <NN-child-page>/          # Nested child pages
          page.json
          page-definition.json
    list-type-definitions/        # Picklists; one raw ListTypeDefinition DTO per file
      <name>.json
      <name>.list-type-entries.json   # optional; bare JSON array of entries
    object-definitions/           # One raw ObjectDefinition DTO per file
      <NN-name>.json
    object-relationships/         # One raw ObjectRelationship DTO per file
      <name>.json
    object-fields/                # Fields added to already existing objects
    object-folders/               # Object folders
    object-actions/               # Object actions
    object-entries/               # Seed object entry data
    resource-permissions.json     # Role grants on objects and other resources
    roles.json                    # Site roles
    site-navigation-menus.json    # Navigation menus (array of menus)
    style-books/                  # Style book entries
      <style-book-name>/
        style-book.json
    thumbnail.png                 # Site thumbnail (displayed in Site Admin)
```

## Objects and Picklists

> **There is no `batch/` directory in a site initializer.** `BundleSiteInitializer` reads a fixed set of directories, and `batch/` is not one of them. Files placed in `site-initializer/batch/*.batch-engine-data.json` are packaged into the deployed zip and then silently ignored: the build succeeds, the site provisions, and no objects appear. The only signal is `Invoking addOrUpdateListTypeDefinitions took 0 ms` in the log. The `*.batch-engine-data.json` envelope with a `configuration.className` block belongs to the separate **`batch` CET type**, not here.

Each file holds a single **raw DTO** — no `configuration` wrapper and no `items` array. Handlers run in a fixed order, so a file may reference anything produced by an earlier handler:

`list-type-definitions` → `object-folders` → `object-definitions` → `object-relationships` → `object-fields` → `publishObjectDefinitions` → `object-actions` → `object-entries`

Because `publishObjectDefinitions` runs after the definitions are created, **omit `status` from an object definition file** — the initializer publishes it.

### `list-type-definitions/<name>.json`

A raw `ListTypeDefinition`. `listTypeEntries` may be inline (as below), or in a sibling `<name>.list-type-entries.json` holding a bare JSON array — the definition loop skips files ending in `.list-type-entries.json`.

```json
{
	"externalReferenceCode": "REGISTRATION_STATUS",
	"listTypeEntries": [
		{
			"externalReferenceCode": "REGISTRATION_STATUS_PENDING",
			"key": "pending",
			"name": "Pending"
		}
	],
	"name": "Registration Status"
}
```

### `object-definitions/<NN-name>.json`

A raw `ObjectDefinition`. Reference a picklist by **ERC**, via `listTypeDefinitionExternalReferenceCode` on the field:

```json
{
	"externalReferenceCode": "REGISTRATION",
	"label": {
		"en_US": "Registration"
	},
	"name": "Registration",
	"objectFields": [
		{
			"businessType": "Picklist",
			"label": {
				"en_US": "Status"
			},
			"listTypeDefinitionExternalReferenceCode": "REGISTRATION_STATUS",
			"name": "registrationStatus",
			"required": true
		}
	],
	"pluralLabel": {
		"en_US": "Registrations"
	},
	"scope": "company",
	"titleObjectFieldName": "attendeeName"
}
```

Custom field names are validated against a reserved list. `name`, `email`, `location`, and `company` are fine; `status`, `id`, `creator`, `keywords`, and `userId` are rejected. A collision throws `ObjectFieldNameException$MustNotBeReserved`, which **aborts initialization and rolls back the entire site creation transaction**, leaving no site, no objects, and no picklists. See `skills/manage-objects/SKILL.md` for the full list.

A status field is the usual reason to reach for `"state": true`, and that flag makes `defaultValue` and `defaultValueType` **mandatory** on the same field. Omitting them throws `ObjectFieldSettingValueException.MissingRequiredValues` — which rolls the site back exactly like a reserved name does, and reads as "the CET never deployed":

```json
{
	"businessType": "Picklist",
	"label": {
		"en_US": "Status"
	},
	"listTypeDefinitionExternalReferenceCode": "REGISTRATION_STATUS",
	"name": "registrationStatus",
	"objectFieldSettings": [
		{
			"name": "defaultValue",
			"value": "pending"
		},
		{
			"name": "defaultValueType",
			"value": "inputAsValue"
		}
	],
	"required": true,
	"state": true
}
```

`defaultValue` is the picklist **entry key**, and it must match an entry the `list-type-definitions` handler created earlier — a typo here is a rollback, not a warning. Liferay then generates a fully connected `stateFlow` allowing every transition; constrain it explicitly if a state should be terminal. See `skills/manage-objects/SKILL.md` for the live API equivalent.

### `object-relationships/<name>.json`

A raw `ObjectRelationship`. This handler resolves the parent by **numeric ID** (`getObjectDefinitionId1()`), so ERC fields alone do not work here — use `[$OBJECT_DEFINITION_ID:<Name>$]` tokens, keyed on the object definition `name`:

```json
{
	"deletionType": "cascade",
	"externalReferenceCode": "EVENT_REGISTRATIONS",
	"label": {
		"en_US": "Registrations"
	},
	"name": "eventRegistrations",
	"objectDefinitionId1": "[$OBJECT_DEFINITION_ID:Event$]",
	"objectDefinitionId2": "[$OBJECT_DEFINITION_ID:Registration$]",
	"objectDefinitionName2": "Registration",
	"type": "oneToMany"
}
```

`deletionType` is `cascade`, `disassociate`, or `prevent`. Creating the relationship adds an `r_<relationshipName>_c_<parent>Id` field to the child object; set that field when creating a child entry over REST.

The field sits on the child but is named for the **parent**, first letter lowercased — the relationship above puts `r_eventRegistrations_c_eventId` on `Registration`, not `..._c_registrationId`. Getting it wrong is silent: the unknown key is ignored, the child is created with the FK left at `0`, and the POST still returns `200`. There is an ERC twin, `r_eventRegistrations_c_eventERC`, which is what OData relationship filters require (see `skills/manage-objects/SKILL.md`).

Do **not** copy `"system": true` from portal internal initializers (seo-studio, ai-hub) — it makes the object or picklist nonmodifiable.

## `resource-permissions.json`

A flat array of grants, applied by the `addOrUpdateResourcePermissions` handler. This is how an object becomes visible to Guest — required before an object backed Collection renders anything on a public page.

```json
[
	{
		"actionIds": [
			"VIEW"
		],
		"primKey": "0",
		"resourceName": "[$OBJECT_DEFINITION_CLASS_NAME:<Name>$]",
		"roleName": "Guest",
		"scope": "1"
	}
]
```

**`scope` is the trap.** It is a `ResourceConstants` integer, and the wrong value fails silently — the handler reports no error and grants nothing useful:

| `scope` | Constant | Grants |
| --- | --- | --- |
| `"1"` | `SCOPE_COMPANY` | The role may act on **all existing and future** entries of that resource. This is the one that makes a public listing work. |
| `"2"` | `SCOPE_GROUP` | The same, limited to the current site. |
| `"3"` | `SCOPE_GROUP_TEMPLATE` | Only the **default permissions applied to newly created** entries. Does nothing for entries that already exist. |
| `"4"` | `SCOPE_INDIVIDUAL` | One specific entry, named by `primKey`. Used by `page.json` permissions. |

Several portal initializers (seo-studio, ai-hub) use `"3"`, so copying from them without checking produces a grant that appears to apply and changes nothing. For scopes `1` and `2` the handler overwrites `primKey` with the company or group ID, so `"0"` is a fine placeholder.

The handler **warns rather than fails** on a bad `resourceName` or an unknown `roleName` — `No resource action found` / `No role found` in the log. A silent 2 ms run with no warning means the grant was applied as written; if behavior did not change, suspect `scope`.

## Token Substitution

Every file the initializer reads is passed through token replacement before it is parsed as JSON. Delimiters are `[$` and `$]`. This is how a file references an entity whose numeric ID cannot be known at authoring time.

Context tokens: `[$COMPANY_ID$]`, `[$GROUP_ID$]`, `[$GROUP_KEY$]`, `[$GROUP_FRIENDLY_URL$]`, `[$PORTAL_URL$]`.

Entity tokens are keyed by the entity's name or ERC, and are registered by the handler that creates the entity — so they generally resolve only for entities created by an **earlier** handler in the order above. **Object definition tokens are the exception**: they are registered company wide for every published object, including ones created outside the tree — see "Object Definition Tokens Are the Exception" below before concluding a token will not resolve.

| Token | Resolves To |
| --- | --- |
| `[$OBJECT_DEFINITION_ID:<Name>$]` | Numeric object definition ID |
| `[$OBJECT_DEFINITION_CLASS_NAME:<Name>$]` | Fully qualified class name |
| `[$OBJECT_DEFINITION_PORTLET_ID:<Name>$]` | Portlet ID |
| `[$LIST_TYPE_DEFINITION_ID:<Name>$]` | Numeric picklist ID |
| `[$ROLE_ID:<Name>$]` | Numeric role ID |
| `[$LAYOUT_ID:<friendly-url>$]` | Numeric layout ID |
| `[$DDM_STRUCTURE_ID:<Name>$]`, `[$DDM_TEMPLATE_ID:<Name>$]` | Structure / template IDs |
| `[$JOURNAL_ARTICLE_ID:<ERC>$]`, `[$BLOG_POSTING_ID:<ERC>$]` | Content IDs |
| `[$DOCUMENT_FILE_ENTRY_ID:<name>$]`, `[$DOCUMENT_URL:<name>$]` | Document ID / URL |
| `[$TAXONOMY_CATEGORY_ID:<Name>$]`, `[$TAXONOMY_VOCABULARY_ID:<Name>$]` | Taxonomy IDs |
| `[$ASSET_LIST_ENTRY_ID:<Name>$]`, `[$SEGMENTS_ENTRY_ID:<Name>$]` | Collection / segment IDs |
| `[$CLIENT_EXTENSION_ENTRY_ERC:<name>$]` | Client extension entry ERC |

The authoritative list is `stringUtilReplaceValues` in `BundleSiteInitializer`; the delimiters are in `SiteInitializerUtil.replace`.

### Object Definition Tokens Are the Exception — They See the Whole Company

The "registered by the handler that creates the entity" rule holds for most tokens, but **object definitions are exempt**, and assuming otherwise leads to needlessly abandoning the tree. `_addObjectDefinitions` runs a company wide pass *before* it reads a single file:

```java
getObjectDefinitions(companyId, true, WorkflowConstants.STATUS_APPROVED)
	-> _replaceObjectDefinitionValues(className, shortName, id, ...)
```

That registers `[$OBJECT_DEFINITION_ID:<Name>$]` and `[$OBJECT_DEFINITION_CLASS_NAME:<Name>$]` for **every published object definition in the company**, whoever created it, and only then registers them again for the ones it creates from `object-definitions/`.

So a tree may freely reference objects created live over `object-admin` — `resource-permissions.json`, `object-actions/`, `notification-templates/*.object-actions.json`, and `object-relationships/` all resolve against them. Two conditions:

- The object must be **published**. That pass filters on `STATUS_APPROVED`, so a draft definition registers no token.
- The token is keyed on the definition's **short name**, which equals `name` for a custom object.

**Do not generalize this to other entity types.** `[$LIST_TYPE_DEFINITION_ID:<Name>$]` is registered inside the per file loop in `_addOrUpdateListTypeDefinitions`, with no such pass, so it resolves only for picklists in this tree. When in doubt, find where the handler calls `stringUtilReplaceValues.put` — inside the loop means tree only, before the loop means company wide.

The practical upshot is that a **mixed layout is workable**: objects managed live over `object-admin`, their logic and permissions still authored in the tree and version controlled. Object definitions, fields, actions, notification templates, and entries are all company scoped and survive site deletion, so they persist across the reprovisions that page and fragment work require. What is *not* reproducible is the objects themselves — a fresh bundle or a different environment starts without them, so say plainly which half of the data layer the tree actually rebuilds.

## Verifying What the Initializer Actually Did

The handler log lines are the fastest diagnosis — a step reporting `took 0 ms` (or `1 ms`) found no files, which is how a wrong directory name presents. Grep the handler for whatever you just added, not only the object ones:

```bash
grep --extended-regexp 'Initializing|Invoking (addOrUpdateListTypeDefinitions|addObjectDefinitions|addOrUpdateObjectRelationships|addOrUpdateLayouts|addOrUpdateLayoutsContent|addLayoutPageTemplates|addStyleBookEntries|addFragmentEntries|addOrUpdateResourcePermissions)' \
	bundles/tomcat/logs/catalina.out | tail -20
```

**Fragments are `addFragmentEntries`, with no `addOrUpdate` prefix** — verified on 2026.Q2, where the run logged `Invoking addFragmentEntries took 117 ms`. Grepping for `addOrUpdateFragmentEntries` matches nothing, which reads exactly like the fragments directory was never found. There is likewise **no `addOrUpdateSiteNavigationMenus` line at all**, because that handler runs inside `addOrUpdateLayoutsContent` (see the navigation section above) — its absence is normal and proves nothing either way. Verify menus by counting items over REST instead.

When a handler you expected is simply missing from the output, dump every line for the run before concluding it did not fire:

```bash
awk '/Initializing <Site Name>/{n++} n>=1' bundles/tomcat/logs/catalina.out \
	| grep --extended-regexp 'Invoking|Initializing'
```

`n>=1` prints from the first run onward, so raise the threshold to skip earlier runs — `n>=2` means "the second run and everything after it", not the second run alone. Set it to the total number of `Initializing` lines to see only the latest.

The signal is the **jump between runs**, not the absolute number. Observed on a run that added a style book and a master page to an existing tree:

```text
Invoking addStyleBookEntries took 1 ms      <- before: directory absent
Invoking addStyleBookEntries took 21 ms     <- after: files found
Invoking addLayoutPageTemplates took 0 ms   <- before: no master page
Invoking addLayoutPageTemplates took 134 ms <- after: master page imported
```

So keep the previous run's numbers to compare against. A handler that stays at `0`/`1 ms` after you added its directory is a **hint** that the path is wrong — the build still succeeds and the site still provisions.

**Treat a flat timing as a prompt to check the effect, not as proof of failure.** The signal scales with how much work the handler does, so a handler reading one small file can apply it correctly and still round to `1 ms`. Verified on 2026.Q2: adding `layout-set/public/metadata.json` left `updateLayoutSets` at `1 ms` across the runs before and after, yet the settings had plainly taken — Classic's header and "Powered by Liferay" footer were both absent from the rendered page. Reading that `1 ms` as "the path is wrong" would have sent you chasing a bug that did not exist.

The timings are a triage tool for the file heavy handlers (fragments, layouts, objects, style books, page templates), where real work shows up as tens or hundreds of milliseconds. For a single file handler, assert the outcome instead:

| Handler | What to assert rather than the timing |
| --- | --- |
| `updateLayoutSets` | Fetch a page; confirm the chrome you switched off is gone from the HTML |
| `addOrUpdateResourcePermissions` | Make the request as the target role (unauthenticated `curl` for Guest) |
| `addOrUpdateSiteNavigationMenus` | Count `navigationMenuItems` over REST — this one logs no line at all |

`catalina.out` timestamps are UTC while a local shell is typically not, so a line that looks hours ahead may be the current run. When several runs are in the file, the earlier failures stay there — match on timestamp before concluding the newest run failed.

A failed initialization rolls the site back, so "the site does not exist" and "the initializer threw" are the same symptom. Search for the cause with:

```bash
grep --extended-regexp 'InitializationException|MustNotBeReserved|Unable to transform' \
	bundles/tomcat/logs/catalina.out | head
```

## `site-navigation-menus.json` Format

A bare JSON array of menus. Each menu needs `externalReferenceCode`, `name`, `typeSite`, and a `menuItems` array; `auto` is read on update:

```json
[
	{
		"auto": false,
		"externalReferenceCode": "EVENT_SITE_MAIN_MENU",
		"menuItems": [
			{
				"externalReferenceCode": "EVENT_SITE_NAV_HOME",
				"friendlyURL": "/home",
				"privateLayout": false,
				"type": "layout"
			}
		],
		"name": "Main Menu",
		"typeSite": 1
	}
]
```

`typeSite` is a `SiteNavigationConstants` integer: `1` primary, `2` secondary, `3` social.

Item `type` is `layout`, `node`, `url`, or `display-page`. A `layout` item is resolved by **friendly URL**, not by name or ID, and needs `privateLayout` alongside it. Add `"useCustomName": true` plus a name to override the page's own title. A `url` item takes `url` and `useNewTab`; a `node` item takes `title`. Nest children by giving an item its own `menuItems` array.

The handler runs at the **end of** `addOrUpdateLayoutsContent`, so pages already exist by then. A friendly URL that does not match any page is skipped with a warning rather than failing the build:

```text
No layout found with friendly URL /whatever
```

That is a silent gap — the menu is created with fewer items than authored, and the site still provisions. Confirm the item count after provisioning:

```bash
curl \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-site/v1.0/sites/<site-erc>/navigation-menus" \
	--user "test@liferay.com:test" \
	| jq '[.items[] | {name, items: (.navigationMenuItems | length)}]'
```

Note that the Classic theme's site navbar renders the **public page hierarchy** on its own, so non hidden pages appear in navigation whether or not this file exists. There is no key in `layout-set/*/metadata.json` that selects a named menu for the theme — the same limitation the themeCSS note above describes.

Source: `_addOrUpdateSiteNavigationMenus` in `BundleSiteInitializer`.

## `page.json` Format

```json
{
	"friendlyURL": "/home",
	"hidden": false,
	"name": "Home",
	"name_i18n": {
		"en_US": "Home"
	},
	"permissions": [
		{
			"actionIds": [
				"VIEW"
			],
			"roleName": "Guest",
			"scope": 4
		}
	],
	"private": false,
	"system": false,
	"type": "Content"
}
```

`type` values: `"Content"`, `"Portlet"`, `"URL"`, `"Embedded"`.

## `page-definition.json` Format

Mirrors the `pageDefinition` field of the Headless Admin Site page API. Minimum shape for an empty content page:

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

Add fragment elements under `pageElements` to compose the layout. Each fragment element references its fragment by `key` (the fragment's directory name under `fragments/group/<collection-key>/fragments/`) and `siteKey` (the `[$GROUP_KEY$]` token, which resolves to the current site):

```json
{
	"definition": {
		"fragment": {
			"key": "<fragment-name>",
			"siteKey": "[$GROUP_KEY$]"
		}
	},
	"type": "Fragment"
}
```

Built in fragments instead use a combined `key` with no `siteKey` (e.g. `"key": "BASIC_COMPONENT-paragraph"`). The importer reads `key`/`siteKey` only — a fragment element written with `collectionExternalReferenceCode`/`fragmentEntryKey` is silently dropped and the page renders blank.

## Master Pages

A master page lives under `layout-page-templates/master-pages/<master-name>/` with two files: `master-page.json` (just `{"name": "<Display Name>"}`) and `page-definition.json`. The master's **key is its directory name** (`<master-name>`).

The master `page-definition.json` holds the persistent header/footer fragments plus a **DropZone** marking where each page's own content is injected. The DropZone must carry a `definition.fragmentSettings` block — a bare `{"type": "DropZone"}` does not render. Use the numeric `version` `1.1`:

```json
{
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
						"unallowedFragments": [
						]
					}
				},
				"type": "DropZone"
			}
		],
		"type": "Root"
	},
	"version": 1.1
}
```

A page selects its master through its **own** `page-definition.json`, in a top level `settings.masterPage.key` whose value is the master's directory name:

```json
{
	"pageElement": {
		"pageElements": [
		],
		"type": "Root"
	},
	"settings": {
		"masterPage": {
			"key": "<master-name>"
		}
	},
	"version": 1.1
}
```

## `layout-set/public/metadata.json`

Controls theme assignment and theme settings for the public (nonprivate) layout set. A theme is named by `themeId`, or by display name with `themeName`:

```json
{
	"colorSchemeId": "01",
	"settings": {
		"lfr-theme:regular:show-footer": false,
		"lfr-theme:regular:show-header": false,
		"lfr-theme:regular:show-header-search": false
	},
	"themeId": "classic_WAR_classictheme"
}
```

Use the `settings` block to switch off the stock theme chrome when a master page supplies a branded header and footer — otherwise both render, one above the other. Prefer this over hiding `#banner` / `#footer` with CSS: the setting is scoped to this layout set, whereas a `globalCSS` CET is injected instance wide and there is no site specific `body` class to scope such a rule to.

> **A `themeCSS` CET cannot be selected from the initializer tree.** Liferay attaches one through a `ClientExtensionEntryRel` on the layout, the master layout, or the layout set, and `BundleSiteInitializer` has **no handler** that creates that relation — there is no key in `metadata.json` for it either. A deployed themeCSS CET is therefore *available* but not *applied* until someone picks it in Site Administration → Design → Theme, and that selection is lost on every reprovision.
>
> Plan accordingly. Appearance that must survive delete and redeploy has to come from things the tree can express: a style book (`defaultStyleBookEntry: true`), the master page, fragment CSS, and these layout set settings. Reach for a themeCSS CET for Clay level overrides that have no token — Classic exposes no `headings*` style book tokens, for instance — and state plainly that applying it needs a manual step.

## `client-extension.yaml` for the Initializer

```yaml
<workspace-id>-site-init:
    name: <WorkspaceId> Site Initializer
    oAuthApplicationHeadlessServer: <workspace-id>-site-oauth
    siteExternalReferenceCode: <workspace-id>
    siteName: <WorkspaceId> Site
    type: siteInitializer

<workspace-id>-site-oauth:
    .serviceAddress: localhost:8080
    .serviceScheme: http
    name: <WorkspaceId> Site OAuth
    scopes:
        - Liferay.Headless.Admin.Site.everything
        - Liferay.Headless.Admin.Content.everything
        - Liferay.Object.Admin.REST.everything
        - Liferay.Headless.Object.everything
        - Liferay.Headless.Admin.User.everything
        - Liferay.Headless.Batch.Engine.everything
    type: oAuthApplicationHeadlessServer
```

## Triggering the Initializer

After deploying, open Control Panel → Sites → Add and select the site initializer from the template list. The name matches the `name` field in `client-extension.yaml`.

Alternatively, via the REST API (the **discouraged** path — see the Provisioning warning above; the CET's `siteExternalReferenceCode` autoprovision + delete then redeploy is the portable route):

```bash
curl \
	--data '{
		"membershipType": "open",
		"name": "<Site Name>",
		"templateType": "site-initializer",
		"templateKey": "<workspace-id>-site-init"
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-site/v1.0/sites" \
	--user "test@liferay.com:test"
```

The current `Site` DTO uses `templateKey` (not `templateExternalReferenceCode`); resolving a site-initializer template through this POST is unreliable regardless.

## References

- Sample site initializer: `workspaces/liferay-sample-workspace/client-extensions/liferay-sample-site-initializer`
- Production site initializer: `modules/apps/site-initializer/site-initializer-cms`
- CET type details: `rules/client-extension-types.md`
- OAuth scopes: `rules/oauth-scopes.md`