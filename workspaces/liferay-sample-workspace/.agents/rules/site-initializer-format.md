---

paths:
  - "**/site-initializer/**"

---

# Site Initializer Format

> **Before authoring:** Load the skill for whatever you are about to author into this tree — `manage-objects`, `manage-pages`, `scaffold-fragment`, `theme-and-design`, or `manage-roles-permissions`. This card is the file format; the skills hold the failure modes.

Reference for the `manage-environments` and `scaffold-client-extension` skills when capturing or building a `siteInitializer` CET.

A site initializer is a client extension of type `siteInitializer`. When deployed and triggered, it creates a fully configured site from the directory tree below.

## Provisioning and Iteration

The site-initializer CET tree is the **single source of truth** for the site. Build the site by triggering the initializer, then iterate by editing the source tree and applying each change by the cheapest reliable path. Always edit the source files first; never hand edit the live site as the authoritative copy.

| Change Type | How to Apply It | Delete the Site? |
| --- | --- | --- |
| Theme CSS content (CET already bound to the master) | Deploy the `themeCSS` CET (`blade gw deploy`) — it serves CSS live | No |
| Binding a `themeCSS` CET or style book onto the master | Edit the master `page-definition.json` `settings`, then clean-redeploy the initializer | No |
| Object definition or data | `object-admin` API, or edit `object-definitions/` and clean-redeploy | No |
| Fragment content, new fragment, page composition | Edit the source tree, then **clean-redeploy the initializer** — the re-fired run upserts | No |
| Removing a page, or a broken/ambiguous upsert | Full reprovision: delete the site, recreate from the initializer | Yes |

**Re-firing the initializer UPSERTS onto the existing site.** When the deployed zip changes, `BundleSiteInitializer` re-runs its full task graph create-or-update: `_addOrUpdateLayoutsContent` re-imports each layout's `page-definition.json` onto the **existing** page (log-verified on 2026.Q2.7), fragments re-import with `FragmentsImportStrategy.OVERWRITE`, objects and entries upsert by ERC. So the normal iteration loop is edit source → clean redeploy — no site deletion. Two conditions make the redeploy actually re-fire:

1. **The zip content must change.** Gradle's CX plugin does **not** track the `site-initializer/` JSON tree as build inputs, so a bare `deploy`/`assemble` after a content-only edit is "up to date" and re-ships the stale zip — silently deploying old pages. And `SiteInitializerClientExtension._isAlreadyProcessed(bundle.getLastModified())` short-circuits an unchanged bundle. Always `blade gw clean deploy` (as separate steps for stubborn nested zips), or `rm` the deployed zip first.

1. **Deploy order when a fragment AND a page change:** the layout import **snapshots** the fragment's css/html/js into `fragmententrylink` at import time — it does not reference the FragmentEntry live. Deploy the fragment change first (so the FragmentEntry upserts), then re-fire the page-bearing initializer so the re-import copies the updated code into the links. A page-only redeploy keeps stale fragment styling.

**What a re-fire renews when nothing changed** (verified on 2026.Q3.5 by five re-fires, comparing every link and layout structure): each fragment gets a new `fragmententryversion` row, but `FragmentEntryLocalServiceImpl` keeps at most `FragmentEntryVersionConfiguration.maximumVersionsPerEntry` (company-scoped, default `10`) per fragment and deletes the oldest, so the count stops growing at 10 per fragment. Each re-imported layout's fragment links get new ids, namespaces and widget `instanceId`s. And its layout structure (`layoutpagetemplatestructurerel.data_`) gets new item ids wherever the tree pins none: a **content page never keeps its `Root` element's `id`** (all 12 pages whose tree holds more than a `Root` drew a new root id every run, while masters and templates kept theirs), and any element without an `id` draws a random one. A page whose tree holds only a `Root` is not rewritten at all. To compare two runs, mask `fragmentEntryLinkId`, the root id and id-less items (key items by their position in the tree), and a widget's `instanceId`. Do **not** mask a link's `namespace` by substring replace: the namespace is four random letters, which can occur in ordinary text (`lues` inside `validValues`), and measured on 332 links it appears in no link's html, css, js or configuration, and in `editablevalues` only as a widget's `instanceId`.

A **new master page** added to the tree is **not** created by that re-fire on 2026.Q3.2, although new DPTs and fragments deployed with it are; there, create it by a full reprovision, or over `POST …/master-pages`. **On 2026.Q3.5 the re-fire does create it:** a master added to an existing site's tree was created on the next fire, and the pages naming it in `settings.masterPage.key` were bound to it in the same run. The upsert never **removes** anything: a page deleted from the source tree stays on the site, and layouts are keyed by friendly URL (see "How Layouts Are Matched and Ordered" below), so renames create duplicates. **One handler is not an upsert at all: `segments-experiences/`.** `_addSegmentsExperiences` calls `appendSegmentsExperience` with no lookup, so every re-fire adds another copy of each experience, to the page and to its draft, with its fragment links (verified on 2026.Q3.5: a second fire left `/home` with two "Signed-In Experience" rows and 30 links instead of 20). Keep experiences out of a tree that re-fires on every run, or remove the duplicates after each run. **To remove an experience from a published page, delete only the draft's copy:** `DELETE …/page-experiences/{erc}` deletes a draft layout's experience and answers `400 UnsupportedOperationException` for the live one (`PageExperienceResourceImpl.deleteSitePageExperience`). The live copy goes at the page's next publish, which an initializer's re-import of the page is: `copyLayoutContent` deletes every live experience the draft no longer has (`LayoutLocalServiceWrapper._cleanDeletedSegmentsExperiences`; verified on 2026.Q3.5). One working pattern (2026.Q3.5): a second `siteInitializer` CX for the same site holding only `segments-entries.json` and `segments-experiences/`, deployed only when the experience is missing from the page's page specifications (read live **and** draft over `headless-admin-site`; `headless-delivery`'s `/experiences` lists the live page only). **Each experience's content directory needs a `page.json` beside its `page-definition.json`** (only `type: Content` is read; Liferay's own `site-initializer-teaser-showcase` has one). Without it `_addOrUpdateLayoutContent` returns at once, with no log line: the experience is appended to the **draft** layout only, with no content and never published, and from then on `headless-admin-site` `GET …/site-pages/{erc}/page-specifications` answers `400 UnsupportedOperationException` for that page until the experience is deleted (`DELETE /sites/{erc}/page-experiences/{its erc}` works on that draft-only experience). For removals, or when an upsert leaves the site in a doubtful state, fall back to the full reprovision — **delete the site, then redeploy the initializer CET**. (For an undeletable group — the system Guest site — the upsert is the only path, and it works.)

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

**Two initializers that target the same site log the same line**, `Initialized <Site> for group <id>`. A script waiting for one must take its log mark just before that one's deploy and accept only the first such line after it, and a later barrier for the other must take a mark of its own: one shared mark lets either run's line satisfy both (seen on 2026.Q3.5 with a symlinked form-fragment initializer and the site's own, both on `L_GUEST`). When the second places the first one's fragments, it must also wait: deployed back to back, the second imported first in 2 of 4 runs and dropped the fragment silently.

An initializer that only adds company fragments onto `L_GUEST` with `siteName: Guest` needs no build hook on 2026.Q3.5: the site upsert went through without injecting `"active": true` into `site-initializer.json`, which 2026.Q2.7's `SiteResourceImpl._updateGroup` NPEs without.

**On a site's first import, its pages and masters can lag the log line.** Right after `Initialized <Site> for group <id>`, `GET …/sites/{erc}/master-pages` listed no master on a newly created site, and listed it a few seconds later (2026.Q2.7). A script that acts on the new site after that line should poll for what it needs rather than read once.

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

Fragments live in the initializer tree under `fragments/group/<collection-key>/fragments/<fragment-name>/`. To change a fragment, edit those source files and re-fire the initializer (clean redeploy) — and remember the two-step order from above when the fragment is placed on initializer pages: fragment initializer first (upserts the FragmentEntry), page-bearing initializer second (re-snapshots the code into `fragmententrylink`).

**A fragment whose FreeMarker errored sticks in DRAFT, and the upsert will NOT overwrite it.** When a fragment's FTL fails to compile, Liferay leaves the fragment entry in a Draft/errored state, and subsequent initializer runs do not replace a Draft with the corrected source — the live pages keep rendering the last-good version, which reads exactly like "you deployed a stale build". Fixing the source is necessary but not sufficient: clear the Draft manually (fix-and-publish or delete the entry in the Fragments editor), then re-fire.

The `headless-admin-fragment` API **does** expose per fragment create/update/delete, and it is easy to mistake for a shortcut. It is not one: Liferay copies fragment code into each page's fragment instance at placement time, so a `PUT` updates the library entry and leaves every page already using it unchanged — verified on 2026.Q2. See `skills/scaffold-fragment/SKILL.md` → "The Live Fragment API" for what it is actually useful for.

## How Layouts Are Matched and Ordered

`_addOrUpdateLayout` keys layouts by **friendly URL only** (`fetchLayoutByFriendlyURL`: group + private + URL) — not by name or type. Verified in 2026.Q2 source; two consequences:

- **Type mismatch = hard delete.** If a layout exists at that friendly URL with a *different* `type` than the initializer declares, the importer calls `deleteLayout(existing)` then `addLayout` fresh — destroying the old page's portlets and preferences, and re-firing on every redeploy. Same URL + same type is a benign in-place update. So a Content page at `/search` in the tree would DELETE an OOTB Widget `/search` page; it happened on 2026.Q3.5, where the LDM seed's Guest site holds a hidden Widget `/search` and a Content `/search` from a tree replaced it on its first import. Never let an authored layout collide by URL with a different-typed page you want to keep.
- **Ordering has two mechanisms.** New pages are `addLayout`-appended (priority = max+1) in sorted directory order, so on a fresh site the bare `NN-` prefix alone sets order. A `page.json` `"priority"` key fires `updatePriority`, which re-sorts and **renumbers ALL siblings sequentially from 0** — an out-of-range value is normalized to the last slot, not ignored. Rule of thumb: directory numbering orders only pages the initializer *creates*; to reorder a **pre-existing** page (e.g. an OOTB one) you must set an explicit `priority` — the update path skips priority when the key is absent.

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
      group/                      # site-scoped collections (referenced with key + siteKey)
        <collection-key>/
          collection.json         # {"name": ..., "description": ...}
          fragments/              # required nesting level
            <fragment-name>/      # dir name becomes the fragment "key"
              fragment.json
              index.html
              index.css
              index.js
              configuration.json  # optional
      company/                    # company-scoped (global) collections — referenced by key ONLY, no siteKey
        <collection-key>/...      # same nesting as group/
    journal-articles/             # Web content (Journal) articles
      <article-name>.xml
      <article-name>.json
    layout-page-templates/        # Page templates and master pages
      display-page-templates/
        <template-name>/
          page-definition.json
          display-page-template.json  # NOT page-template.json, which is the page-template descriptor (LayoutPageTemplateExportImportConstants, 2026.Q3.2); a DPT folder carrying the wrong name is silently skipped
                                      # {"contentType": {"className": "[$OBJECT_DEFINITION_CLASS_NAME:<Name>$]"}, "defaultTemplate": <bool>, "name": ...}: the token resolves for a live-created object even in a tree with no object-definitions/ (2026.Q3.2); the DPT's friendly URL is its name lowercased and dashed
      master-pages/
        <master-name>/          # dir name is cosmetic; the master "key" is derived from master-page.json "name" (see settings.masterPage.key)
          page-definition.json
          master-page.json       # {"name": ...} — NOT page-template.json
          thumbnail.png          # optional
    layout-utility-page-entries/  # 404, 500, Cookie Policy, Sign In... (verified 2026.Q3.5)
      <name>/
        utility-page.json         # {"externalReferenceCode", "name", "type": "ErrorCode404" | "CookiePolicy" | ...}
        page-definition.json      # a css.css beside it becomes settings.css
      default-utility-page-entries.json  # optional {"<type>": "<name>"}: which entry is the site's default
                                  # An entry whose ERC exists on the site (e.g. LFR-404-ERROR) is updated in place,
                                  # no duplicate. "defaultTemplate" in utility-page.json is NOT read: a new entry is
                                  # created non-default unless default-utility-page-entries.json names it.
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
    object-fields/                # {"objectDefinitionId", "object-fields": [...]}; Aggregation fields go here (manage-objects)
    object-folders/               # Object folders
    object-actions/               # Object actions
    object-entries/               # Seed object entry data
    notification-templates/       # Notification templates (+ optional *.object-actions.json)
    workflow-definitions/         # Kaleo workflow definition XML/JSON
    accounts.json                 # AccountEntry seed (ERC, type, customFields, postalAddresses, hierarchy)
    commerce-channel.json         # The site's Commerce channel (see "commerce-channel.json" below: Channel's own fields only)
    account-groups.json           # Account groups
    account-group-assignments.json # {accountGroupExternalReferenceCode, accounts:[erc]}
    user-accounts.json            # Users (see "Users and Accounts" below — INACTIVE gotcha)
    plo-entries.json              # Fragment i18n strings: [{key, value:"{\"en_US\": \"...\"}"}]
    resource-permissions.json     # Role grants on objects and other resources
    roles.json                    # Site roles — description/name_i18n MUST be locale-keyed objects
    site-configuration.json       # typeSite / manualMembership / membershipRestriction
    site-settings.json            # Group-scoped OSGi configs [{pid, properties}]
    site-navigation-menus.json    # Navigation menus (array of menus)
    taxonomy-vocabularies/        # company/ (portal wide, survives site deletion) or group/
      <scope>/
        <vocab>.json              # the vocabulary: {"name": ...}
        <vocab>/                  # SAME-NAMED folder holding its categories — a folder with no sibling <vocab>.json is skipped silently
          <category>.json         # {"name": ...}
    style-books/                  # Style book entries
      <style-book-name>/          # dir name IS the style book "key" (unlike master pages, whose key comes from "name")
        style-book.json
    thumbnail.png                 # Site thumbnail (displayed in Site Admin)
    workflow-definitions/         # Kaleo definition only; link it over REST (manage-object-logic → Approval Workflow)
```

> **`sxp-blueprints.json` is a silent no-op on DXP bundles** — only an internal Liferay-hosted module reads it (confirmed independently on two 2026.Q2 instances). Seed Search Experiences blueprints over REST post-provision instead, and remember the object class-name short code inside a blueprint's query is random per database — tokenize it (`[$OBJECT_DEFINITION_CLASS_NAME:<Name>$]` style) rather than hardcoding `#XXXX`.

## Objects and Picklists

> **There is no `batch/` directory in a site initializer.** `BundleSiteInitializer` reads a fixed set of directories, and `batch/` is not one of them. Files placed in `site-initializer/batch/*.batch-engine-data.json` are packaged into the deployed zip and then silently ignored: the build succeeds, the site provisions, and no objects appear. The only signal is `Invoking addOrUpdateListTypeDefinitions took 0 ms` in the log. The `*.batch-engine-data.json` envelope with a `configuration.className` block belongs to the separate **`batch` CET type**, not here.

Each file holds a single **raw DTO** — no `configuration` wrapper and no `items` array. Handlers run in a fixed order, so a file may reference anything produced by an earlier handler:

`list-type-definitions` → `object-folders` → `object-definitions` → `object-relationships` → `object-fields` → `publishObjectDefinitions` → account restrictions → `object-actions` → `object-entries`

Because `publishObjectDefinitions` runs after the definitions are created, **omit `status` from an object definition file** — the initializer publishes it. Account restriction is auto-applied post-publish from the definition's own `accountEntryRestricted` + `accountEntryRestrictedObjectFieldName` — no manual PUT needed. On an **existing** definition, the re-fire's PATCH applies it too, but only to a field that already exists: a relationship added by the same run is created after the definition update, so restricting on it rolls the whole run back (`ObjectDefinitionAccountEntryRestrictedObjectFieldIdException`; verified on 2026.Q3.5). Add the relationship in one fire and the restriction in the next. Details and the open no-account case: `skills/manage-objects/SKILL.md` → "Account Restriction".

Additional pipeline facts (verified 2026.Q2.7):

- **Reserved system fields in a definition file must carry `"system": true`.** A reserved name (`externalReferenceCode`, `status`, `createDate`, …) at `system: false` routes through `addCustomObjectField` → `MustNotBeReserved` → the atomic rollback. This **only surfaces on a fresh DB**: on a populated instance the object already exists, add-or-update skips creation, and the bad field is never hit — the bug hides until the next clean rebuild. Diagnose via `MustNotBeReserved` in the log, not the misleading downstream entry-count symptoms.
- **`friendlyURLSeparator` collisions abort publish.** `ObjectDefinitionFriendlyURLSeparatorException: Other asset types may use this prefix` means the name-derived separator collides with another asset's resolver. Set `"friendlyURLSeparator": "l"` on the definition, which skips that validation. It is not the default: the default is `c_<lowercased name>`, and with `l` entries get no `friendlyUrlPath`, so their display URL becomes `/web/<site>/l/<entryId>` (verified 2026.Q3.2).
- **`object-fields/` is how a live object grows.** `patchObjectDefinition` does NOT add new fields to an already-published object, so field additions go in `object-fields/<name>.json`: `{"objectDefinitionId": "[$OBJECT_DEFINITION_ID:<Name>$]", "object-fields": [...]}`. Idempotent; fresh builds create the fields from the definition, existing instances get them from this file.
- **On 2026.Q3.5 a re-fire makes a published object match its definition file, deleting fields too.** An existing definition is updated with `patchObjectDefinition` (`putObjectDefinition` when the file says `"updateStrategy": "UPDATE"`, `BundleSiteInitializer:1300–1376`). The PATCH sends the file's `objectFields` to `putObjectDefinition` (`ObjectDefinitionResourceImpl.preparePatch`), which deletes every custom, non-relationship field the body leaves out (`:790–866`). Verified:
	- a re-fire **added** two Attachment fields listed in the file to a published object;
	- after one of them was **removed from the file**, the next re-fire **deleted** it, although it held data: its column, its `DOWNLOAD_<FIELD>` resource action, that action's bit in every grant, and its labels. A further re-fire did not bring it back.

  So removing a field from a definition file is a delete on every instance at the next run, not a no-op, and its data goes with it. Drop its actions from `resource-permissions.json` in the same change: the handler skips a whole grant whose `actionIds` name an action that does not exist, logging only `No resource action found with name …` (`_addOrUpdateResourcePermissions`, the `containsAll` check, `BundleSiteInitializer:3800`). So a stale action costs the role all its other actions in that grant on a fresh build. The `object-fields/` file above is still the way to add fields to a system object.
- **On 2026.Q2.7 the same re-fire keeps fields its file leaves out.** q2.7's `BundleSiteInitializer` calls `patchObjectDefinition(id, <file body>)`, and a field another tree added (through its own `object-fields/` file) survived a re-fire of the definition's tree with its data (verified live, 10 such fields on a site-scoped object). So one tree may own a shared object's definition and another tree add host fields to it on q2.7; an upgrade to a release that deletes them must move those fields into the definition's own file or a later step.
- **A re-fire never changes an existing display page template's content type.** The importer finds a DPT by name, and with the initializer's `OVERWRITE` strategy updates its **name only** (`LayoutsImporterImpl._processLayoutPageTemplateEntry`; the class is set only on create, 2026.Q2.7 source). So when the object a DPT renders is replaced by another of the same name (`[$OBJECT_DEFINITION_CLASS_NAME:<Name>$]` then resolves to the new class), the existing DPT keeps the old class, the new entries have no default display page, and the entry URLs answer `404`. Delete the DPT before the re-fire; the re-fire recreates it on the new class.
- **Depot-scoped objects reject entry writes unless they accept the depot.** A scoped POST/PUT to `/o/c/<plural>/scopes/{assetLibraryKey}` returns a bare, unlogged 404 (`_validateGroupId` throws) while scoped GET works — a misleading asymmetry. Add `"objectDefinitionSettings": [{"name": "acceptAllGroups", "value": "true"}]` to the definition (PATCHes live on a published definition, immediate, no republish). Or list the depot by ERC in `acceptedGroupExternalReferenceCodes`; whether that list survives the depot being recreated on a rebuild was not tested. Prefer the list where it holds: `acceptAllGroups` opens the object to **every** depot. It holds about ten depots: it is stored as numeric group ids in a `VARCHAR(75)`, and a longer list fails the definition write (verified 2026.Q2.7). Deleting the last listed depot silently rewrites the setting to `acceptAllGroups: true` (verified 2026.Q3.2).
- **`object-entries/` upserts by ERC and never deletes.** Rows absent from the file are left in place, so use stable ERCs that overlap pre-existing rows to update in place instead of orphaning.
- **`object-entries/` cannot write CMS (depot-scoped) entries.** `_addOrUpdateObjectEntries` writes with the initializer's site as scope, which a depot-scoped definition refuses (`ObjectEntryGroupIdException.InvalidGroupIdForScope`), rolling the run back. Write Space entries over REST instead (from source).
- **A template element the importer cannot read fails that template half-imported, silently.** On a re-fire, a display page template holding a `BASIC_COMPONENT-button` with a literal `fragmentLink.value_i18n` href had its **draft's** links replaced and stopped there: no structure written, nothing published, the live template unchanged, no log line (2026.Q3.5). Compare the live and draft link counts of a changed template after a run.

### `list-type-definitions/<name>.json`

A raw `ListTypeDefinition`. `listTypeEntries` may be inline (as below), or in a sibling `<name>.list-type-entries.json` holding a bare JSON array — the definition loop skips files ending in `.list-type-entries.json`.

**A re-fire does not change the entries of a picklist that already exists** (measured on 2026.Q2.7): an entry the file adds is not created, and an entry whose `name` changed keeps its old name. The picklist is right on a fresh build and wrong on every re-fired one, silently. Write entry changes to the live picklist over `headless-admin-list-type` after the run: `POST /list-type-definitions/{id}/list-type-entries` for a missing key, `PUT /list-type-entries/{id}` (keeping its `key` and ERC) for a new name. Compare first, and never delete an entry a stored entry may still hold. **Give every entry an ERC unique within its picklist.** Entries upsert by ERC, so two entries whose ERCs collide (for example two long keys cut to the same 75-character prefix) become one entry, carrying the second's name under the first's key: the other option is gone, and a form submission that picks it fails with only a generic error.

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
	"panelCategoryKey": "control_panel.object",
	"pluralLabel": {
		"en_US": "Registrations"
	},
	"scope": "company",
	"titleObjectFieldName": "attendeeName"
}
```

Keep `panelCategoryKey` because the object has no admin menu entry without it. See `skills/manage-objects/SKILL.md`.

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

### `object-entries/<name>.json`

Write one file per object. `objectDefinitionName` names the object, with or without the `C_` prefix, and `object-entries` (hyphenated) holds raw `ObjectEntry` DTOs:

```json
{
	"object-entries": [
		{
			"externalReferenceCode": "EVENT_LAUNCH",
			"title": "Launch"
		}
	],
	"objectDefinitionName": "Event"
}
```

A bare JSON array fails with `A JSONObject text must begin with '{'` and rolls back the site. A misspelled key, such as `objectEntries`, is skipped silently and logs `Invoking addOrUpdateObjectEntries took 0 ms`. Entries are written inside the portal, so the initializer's OAuth scopes do not apply to them. Verified on 2026.Q1.

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

**Two grants for the same role, resource and scope do not add up: the later one replaces the earlier one's actions.** A `VIEW` grant followed by a separate `DOWNLOAD_<FIELD>` grant for Guest on the same object left Guest with only the download action: entries answered `404` to Guest over REST, and Collections and display pages stopped showing them (measured on 2026.Q2.7). Put every action a role gets on a resource in one grant, and accept that one unknown action then skips that whole grant (see below), which is why it pays to check the actions a resource has first.

**It cannot revoke a grant.** An entry whose `actionIds` is empty is skipped with the same `No resource action found with name …` WARN, and the stored row stays: `ArrayUtil.containsAll` is `false` when its second array is empty (`ArrayUtil.java:800–803`), which the handler's check reads as an unknown action (`BundleSiteInitializer.java:3797–3810`). Measured on 2026.Q3.5 with two company-scope rows. `page.json`'s `permissions` differ: there an empty list deletes the row. Remove a `resource-permissions.json` grant some other way, for example in the Roles UI or from a module through `ResourcePermissionLocalService`.

## `roles.json` and `plo-entries.json`

- **`roles.json`**: each role's `name_i18n` and `description` must be **locale-keyed JSON objects** (`{"en_US": "..."}`). A plain string in either field rolls back the entire provisioning transaction.
  **On 2026.Q2.7 a role's `externalReferenceCode` is not set when the run creates it.** `_addOrUpdateRoles` creates with `addRole(null, …)` and passes the file's ERC only to `updateRole`, so a first fire leaves a generated ERC (a name-based UUID) and the next fire sets the file's (read from 2026.Q2.7 source; measured on a fresh fire). Code that finds the role by ERC misses it until then: find it by ERC, else by name. A tree's `resource-permissions.json` is unaffected (it names roles by name).
- **`plo-entries.json`**: fragment i18n strings as `[{"key": "kebab-key", "value": "{\"en_US\": \"...\"}"}]` — note `value` is a *stringified* locale map. Fragments read them with `${languageUtil.get(locale, "kebab-key")}` in `index.html` (see `skills/scaffold-fragment/SKILL.md` for getting strings into JS). Keys are **company-scoped** — identical text across fragments must share one key, and **exclude any key the portal's own `Language.properties` already defines** (a duplicate overrides the portal value instance-wide). Changes take effect when the initializer re-fires, but only a changed value refreshes the cache: `PLOEntryLocalServiceImpl` skips an unchanged one. **On a fresh build every label can render as its raw key with all rows present** (2026.Q2.7, measured): `PLOEntryModelListener` empties the in-memory override map before the transaction commits, and the whole initializer run is one transaction with `setPLOEntries` in its first pass, so the window is the entire run (15 s measured). Any label lookup by another thread in that window caches a map without the new rows, and nothing clears it again. A deploy that changes no value writes nothing and is safe, which is why it usually "just works". **Clear it once after the run with an empty import**, which writes nothing: `POST /o/language/v1.0/messages/import?languageId=en_US` with an empty `.properties` file part (`curl -F "file=@/dev/null;filename=empty.properties"`, answers 204). `importPLOEntries` clears the map whether or not a value changed. That it cures a stale map is by source, not yet measured; the measured cure is one real change of any entry (`PUT /o/language/v1.0/messages`, then back).

## Token Substitution

Every file the initializer reads is passed through token replacement before it is parsed as JSON. Delimiters are `[$` and `$]`. This is how a file references an entity whose numeric ID cannot be known at authoring time.

A second form, `"[#KEY#]"`, replaces the token **together with its surrounding JSON quotes**, so the value lands as a raw number: `"classPK": "[#DOCUMENT_FILE_ENTRY_ID:/site-initializer/documents/group/<file>#]"` becomes `"classPK": 12345`. `SiteInitializerUtil.replace` handles `\"[#`…`#]\"` before `[$`…`$]` (from source). Use it wherever the DTO field is numeric.

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
| `[$TEMPLATE_ENTRY_ID:<Name>$]` | An information template's entry id, registered by `ddm-templates/` for a `TemplateEntry` template (and for every existing one, by name). Map it as `ddmTemplate_[$TEMPLATE_ENTRY_ID:<Name>$]` to show the template's output, for example an object attachment's URL (`rules/page-types.md`). **Name each site's template uniquely in the company:** before the per-file loop, `_addOrUpdateDDMTemplates` fills the token from every template in the company by name, and on a re-fire (template exists) it does not set it again, so a same-named template of another site can win (2026.Q2.7 source). Created by a tree on 2026.Q2.7 too |
| `[$JOURNAL_ARTICLE_ID:<ERC>$]`, `[$BLOG_POSTING_ID:<ERC>$]` | Content IDs |
| `[$DOCUMENT_FILE_ENTRY_ID:<name>$]`, `[$DOCUMENT_URL:<name>$]` | Document ID / URL |
| `[$TAXONOMY_CATEGORY_ID:<resource-path>$]`, `[$TAXONOMY_VOCABULARY_ID:<Name>$]` | Taxonomy IDs. The category token is keyed by the category file's full path, e.g. `/site-initializer/taxonomy-vocabularies/company/<vocab>/<category>.json` (verified 2026.Q3.2); an unresolved one stays literal with no error |
| `[$ASSET_LIST_ENTRY_ID:<Name>$]`, `[$SEGMENTS_ENTRY_ID:<Name>$]` | Collection / segment IDs. The segment tokens (`SEGMENTS_ENTRY_ID:`, `SEGMENTS_ENTRY_ERC:`, `SEGMENTS_ENTRY_SCOPE_ERC:`) are keyed by the entry's `segmentsEntryKey` in `segments-entries.json`, and resolve anywhere in a page definition, master or `fragmentConfig`, **including as a JSON key** (verified on 2026.Q3.5). Note that `addOrUpdateLayoutsContent` and `addLayoutPageTemplates` do not declare a dependency on `addOrUpdateSegmentsEntries`; it runs before them only because of the handler map's iteration order. A criteria segment's stored members (`segmentsentryrel`, read only into the user search index's `segmentsEntryIds`) are computed when the segment is added or updated, and by a job every `SegmentsConfiguration.segmentsPreviewCheckInterval` minutes (default 120). So users created after the run are missing there until the next fire or that job; create the users before the initializer that adds the segments, and the members are complete at once (verified on a fresh 2026.Q3.5 build). Experiences do not wait for it: on a fresh 2026.Q3.5 build holding only the admin's rows, a Supervisor-segment user was served the Supervisor experience |
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

**On 2026.Q2.7, fragments that first render in parallel can cache corrupted HTML** (reproduced). `FragmentEntryProcessorRegistryImpl._getDocument` caches one parsed jsoup `Document` per fragment template and returns `document.clone()`, but a jsoup 1.15.3 clone shares its source's `Parser`, which is not thread safe. When several links of one fragment (every `BASIC_COMPONENT-html`, say) render at the same time, `Element.html(value)` calls corrupt each other's parse, and the result is stored in the fragment entry link cache (the OOTB basic components are `cacheable`). Symptoms: text cut off mid-element, a tag swallowing words (`<p>Located in` rendered as `<plocated in>`), an empty `<html><head></head></html>` inside a list. The stored `editablevalues` are correct, and the damage lasts until the page is re-imported. It shows up right after an import, when the page's first renders run in parallel (parallel requests, or the indexer), so a sequential `curl` check can pass while a browser shows a broken page. Reproduce it by firing parallel first requests at freshly imported pages. The fix is a one-line patch of `com.liferay.fragment.impl`: give each clone its own parser, `clone.parser(Parser.htmlParser())`, before returning it. Liferay master still clones the cached document but builds against jsoup 1.23.1 (not verified to be safe there). The Server Administration cache clear cannot be scripted (it asks for a CAPTCHA); refreshing the patched bundle drops the corrupted cache.

One JVM-level red herring: repeated heavy reprovisions can exhaust the **JIT CodeCache**, after which the portal throws bogus cascading `NoSuchMethodException`/`linkToInterface` errors that read like a broken deployment and can silently kill fragment upserts. The fix is `-XX:ReservedCodeCacheSize=512m` in the JVM options and a restart — and note that regenerated container/compose configs can drop the flag, so re-verify it after environment rebuilds.

A failed initialization rolls the site back, so "the site does not exist" and "the initializer threw" are the same symptom. Search for the cause with:

```bash
grep --extended-regexp 'InitializationException|MustNotBeReserved|Unable to transform' \
	bundles/tomcat/logs/catalina.out | head
```

## `site-navigation-menus.json` Format

A bare JSON array of menus. Each menu needs `externalReferenceCode`, `name`, `typeSite`, and a `menuItems` array. **`auto` is read on update only:** a menu the run creates gets `addSiteNavigationMenu(…, name, typeSite, …)`, which takes no `auto`, so it is stored `false` whatever the file says, and the **next** fire sets the file's value (`_addOrUpdateSiteNavigationMenu`, verified on 2026.Q3.5: a fresh build's two `"auto": true` menus read `auto_ = f` until the second run). Expect that one-time difference between a fresh build and a re-run:

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

Item `type` is `layout`, `node`, `url`, or `display-page`. A `layout` item is resolved by **friendly URL**, not by name or ID, and needs `privateLayout` alongside it. A `url` item takes `url` and `useNewTab`. Nest children by giving an item its own `menuItems` array.

**Custom labels come from `name_i18n`, a locale map — a bare `name` is ignored** (`BundleSiteInitializer._getNavigationMenuItemUnicodePropertiesWrapper`, verified 2026.Q3.1). To override a `layout` item's page title, set `"useCustomName": true` **and** `"name_i18n": {"en_US": "<label>"}`. A missing `name_i18n` logs `Missing "name_i18n" in {...}`, and the consequence depends on the type:

- `layout` item — **nonfatal**: the item is still created, labeled with the page's own name.
- `node` item — **fatal for the subtree**: the node **and every item nested under it** are silently skipped. A `node` needs **both** `name_i18n` and `title` (set them to the same string).

**Item `externalReferenceCode` must be unique per item, not per target page.** Items upsert by ERC, so two items pointing at the same page (e.g. a "Company" top item and an "About" child both targeting `/company`) must not share an ERC derived from the friendly URL — the second occurrence *updates and re-parents* the first item instead of creating a new one, corrupting the tree.

The handler creates and updates items but **never deletes** — items dropped from the file persist in the live menu. To rebuild a menu cleanly after restructuring it, delete the live menu first (`DELETE /o/headless-delivery/v1.0/navigation-menus/{id}`) and re-fire the initializer; the handler recreates it from scratch by menu ERC.

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

## `user-accounts.json` and `accounts.json`

Verified against 2026.Q2.7 `BundleSiteInitializer` + headless-admin-user resource impls:

- **Created users are set INACTIVE.** `_addUserAccounts` unconditionally ends every user with `setStatus(INACTIVE)` — they land hidden from Users and Organizations (which defaults to active-only) and cannot log in. Reactivate post-init: `PATCH /o/headless-admin-user/v1.0/user-accounts/{id}` with `{"status": "Active"}` — the value is **case-sensitive** (`"Active"`/`"Inactive"` per the enum; lowercase or numeric forms fail `400 "Unable to map JSON path: status"`, which misleadingly reads as unwritable).
- **Do NOT set `imageId` in `user-accounts.json`.** It triggers `updatePortrait` on the just-created (stale) User in the same transaction → `StaleObjectStateException` that **aborts the entire site init**. Apply portraits post-init over REST.
- **`accountBriefs[].roleBriefs` get wiped.** The initializer assigns the account roles, then its final status PATCH round-trips a role-less DTO that deletes and re-adds the memberships — memberships survive, account roles don't (0 `usergrouprole` rows after init). Re-grant post-init: `POST /o/headless-admin-user/v1.0/accounts/{accountId}/account-roles/{accountRoleId}/user-accounts/{userId}` (204/409, idempotent).
- `roleBriefs` entries are role **names**, silently skipped if the role doesn't exist. `organizationBriefs`/`userGroupBriefs` match by `name`.
- **Password:** honored on create (`password` field); the initializer itself sets none. An admin can set one later via `PATCH /user-accounts/{id} {"password": "X"}` (no current-password needed for another user; re-PATCHing the same value returns `400 "password is invalid"` — treat as idempotent).
- **A siteInitializer that re-fires on restart re-stamps its users INACTIVE** (re-deactivation clobbers manual reactivation; fingerprint: an inactive user with a recent `lastLoginDate`). Re-run the reactivation script after restarts, or keep sign-in users out of `user-accounts.json`.
- `accounts.json` parity: `customFields:[{name, customValue:{data}}]`, `parentAccountExternalReferenceCode` (hierarchy), `status: 5` deactivates, `postalAddresses` (give each a `type` — billing/shipping — or the account admin Addresses tab hides it), `defaultBillingAddressExternalReferenceCode`/`defaultShippingAddressExternalReferenceCode`.

## `commerce-channel.json` — the Site's Commerce Channel

Read from the tree root by the Commerce extender (`CommerceSiteInitializerImpl._addOrUpdateCommerceChannel`, reached through `addCPDefinitions`), with `commerce-channel.config.json` (group settings, and the site's allowed account types) and `….model-resource-permissions.json` beside it. Verified on 2026.Q3.5:

- **Only `Channel`'s own fields** (`headless-commerce-admin-channel`): `currencyCode`, `externalReferenceCode`, `name`, `type`, and the account ones. One unknown field, such as `priceDisplayType` or `discountsTargetNetPrice`, makes the handler log `ERROR … Unable to transform commerce channel from JSON` and create nothing; the run goes on.
- `siteGroupId` is set to the site, and the channel is **upserted by `siteGroupId`**. The ERC is kept. A create gets `pricedisplaytype` `tax-excluded` and `discountstargetnetprice` true.
- **Every later fire `PUT`s it** (`mvccversion` and `modifieddate` move), and `putChannel` passes `null` and `false` for those two, so after the first re-fire they are empty and `false`, and no tree can keep them. `putChannelByExternalReferenceCode` does the same; only `patchChannel` keeps them.
- It also runs `checkCommerceAccountRoles` and imports the default currencies and measurement units, which an instance that already has them keeps.
- **Deleting the site leaves the channel**, detached (`sitegroupid` 0) with its own group; delete it on its own (`DELETE /o/headless-commerce-admin-channel/v1.0/channels/{id}`).
- With it, the Commerce account selector fragment shows an account instead of "This site does not have a channel", and `Liferay.CommerceContext.account` is the user's current account (each user's `LIFERAY_SHARED_CURRENT_ACCOUNT_ENTRY_ID#<group>` portal preference; before a user chose one, every user measured got the first of their accounts by name).

## `site-settings.json` — Group-Scoped OSGi Configs

`_addSiteSettings` reads a JSON array of `{"pid": "...", "properties": {...}}` and calls `ConfigurationProvider.saveGroupConfiguration` with the current site's IDs auto-injected — the initializer-native way to ship a group-scoped OSGi config without hardcoding companyId/groupId. Re-applies idempotently on redeploy. Example — redirect unauthenticated visitors to sign-in: pid `com.liferay.login.web.internal.configuration.AuthLoginConfiguration`, properties `{"promptEnabled": true}` (the page must also be un-viewable by Guest for the redirect to fire). Company-wide OSGi configs still belong in `configs/<env>/osgi/configs/*.config`.

**Every property is passed as one string** (`propertiesJSONObject.getString(key)`, 2026.Q2.7): a boolean or a number arrives as `"true"`/`"5"` and parses, but an array cannot be expressed. A JSON array arrives as its text, and a configuration whose model listener casts the property to `String[]` (redirect patterns, `RedirectPatternConfigurationModelListener`) would throw inside the run. Save array-typed group configurations from a module instead (`skills/manage-pages/SKILL.md` → "Redirects").

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

`type` passes straight through (lowercased, `widget`→`portlet`) to `LayoutLocalService.addLayout`, so any `LayoutConstants.TYPE_*` string works — not just the common four. Verified values: `"Content"`, `"Portlet"`/`"Widget"`, `"URL"`, `"Embedded"`, `"link_to_layout"`, and **`"node"` = a Page Set** — a non-browsable container parent (redirects to its first child) that needs **only `page.json`, no `page-definition.json`** (`_addOrUpdateLayoutContent` early-returns for non-content types). `"hidden": true` hides a page from navigation. A `content` child page still needs a minimal `page-definition.json` with `settings.masterPage.key` to pick up the master.

Two `page.json` facts that are easy to get wrong:

- **Every layout is auto-granted Guest VIEW** (`setAddGuestPermissions(true)` at addLayout), and the `permissions` array only **sets** the roles you list — omitting Guest does NOT remove the auto-grant. To make a page members-only, list Guest with an **empty `actionIds`** array (an empty set deletes the resource-permission row); this runs after addLayout each time, so it is idempotent on redeploy:

  ```json
  "permissions": [
  	{"actionIds": ["VIEW"], "roleName": "Site Member", "scope": 4},
  	{"actionIds": [], "roleName": "Guest", "scope": 4}
  ]
  ```

  Page-level access belongs here, not in fragment FreeMarker role checks — a fragment has no ACL and gating it couples visibility to one placement. Liferay auto-hides nav links to pages the user cannot view.
- **`friendlyURL` has reserved tokens that abort the entire run** (`LayoutFriendlyURLException` KEYWORD_CONFLICT): any single segment equal to a **locale code** (all ~67 available locales — `/cs` is Czech, also `/de /fr /es /it /ja /pt` and the `en-us` forms), a `layout.friendly.url.keywords` reserved word (`api`, `c`, `combo`, `group`, `image`, `language`, `o`, `osgi`, `page`, `rest`, `web`, `webdav`, `widget`, …), or a portlet friendly-URL-mapper name (`search`, `blogs`, `asset_publisher`, …). Single-letter pages such as `/a` and `/b` fail the same way (`LayoutImpl.validateFriendlyURLKeyword`, verified 2026.Q3.2). Prefer descriptive multi-word slugs (`/customer-service`, never `/cs`), especially when preserving legacy slugs in a migration. **An object definition's `friendlyURLSeparator` is reserved the same way**, including a CMS structure's: with a structure whose separator is `news`, a page at `/news` aborted the whole run (`LayoutFriendlyURLException`, no detail in the log), and after the separator became `article`, creating `/article` answered `500`. Choose separators that no page will use (verified on 2026.Q2.7). A portlet mapper name does not always block a page: `/calendar` (the Calendar portlet's `friendly-url-mapping`) was accepted on 2026.Q2.7, and so was a Content page at `/search`, on the Guest site and on a regular site.

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

**Company-scoped fragments (`fragments/company/...`): reference them by `key` alone, or with `"siteKey": "[$COMPANY_ID$]"`.** The importer looks `siteKey` up as a group key. The company (Global) group's key is the company id, so `[$COMPANY_ID$]` resolves to it; a bare `key` with no `siteKey` falls back to Global (`FragmentLayoutStructureItemImporter._getFragmentEntry`, `useGlobalAsFallback`). Any other `siteKey` that names no group — typically **another instance's company id**, which is what a page definition exported from an older demo carries — skips the fragment silently: no log line, and the page imports with that element missing (verified on 2026.Q3.5: a page whose company fragments kept the old company id imported with 0 `fragmententrylink` rows). Two more company-fragment behaviors worth knowing: they **survive site deletion** (they're company scoped), and an edit deployed through a delete+redeploy reprovision can land as a **Draft** in the fragment library (needing a UI publish) — whereas a plain redeploy with no site delete upserts straight to the live version.

More import traps, verified on 2026.Q2.7 unless a later release is named:

- **`pageElements` must be a FLAT array of PageElement objects.** A nested array (array-of-arrays, e.g. a generator emitting `[[card, card], [card, card]]` instead of two `Section` objects each holding cards) throws Jackson `MismatchedInputException: Cannot deserialize ... PageElement from Array value` in `LayoutsImporterImpl._importPageElement` — and **silently leaves the page blank** (layout row created, zero `fragmententrylink` rows). Diagnose an empty page by counting `fragmententrylink where classpk = <plid>`; if 0, grep the log for `MismatchedInputException`.
- **Multi-column layout is `Section → Row → Column → (inner Section) → Fragment`** — `Column.size` values sum to 12. `ColumnDefinition` is NOT a valid type and aborts the entire site init; the full valid enum is in `rules/page-types.md`.
- **`settings.css` is imported, but a page that has a master never renders its own.** The importer stores it in `layout.css`, and the page's `<style>` comes from `Layout.getCssText()` (2026.Q3.5): a layout that inherits look and feel gets the **layout set's** CSS; otherwise, if it has a master, it gets **the master's** `getCssText()`; only a master-less layout uses its own. So the CSS that reaches every page is the **master's** `settings.css`, and only when the master does not inherit look and feel, which means its `settings.themeName` must name a **deployed** theme. An unresolved `themeName` (for example `"Dialect"` on a bundle without it) sets `themeId` null, the master inherits, and the layout set's (usually empty) CSS is emitted instead, with no error. Verified on 2026.Q3.5: with the master's `themeName` `"Dialect"` no page emitted the master's CSS; with `"Classic"` every page using the master did, including pages with no `settings.css` of their own. A page's own `settings.css` stays in `layout.css` unrendered. For CSS that belongs to one page only, use a `globalCSS` CX or a fragment's CSS.
- **A `colorPalette` value in `fragmentConfig` must be an object, `{"cssClass": "success"}`, never a bare string.** The importer wraps a string as `{"color": "success"}` (`FragmentLayoutStructureItemImporter._toFreeMarkerFragmentEntryProcessorJSONObject`) and keeps a map as given. A fragment that reads `configuration.<field>.cssClass` then fails FreeMarker at import, which rolls back the **entire** site. `headless-delivery`'s page-definition GET returns these values flattened to a string, so a page definition exported over REST hits this (verified on 2026.Q3.5).
- **A custom `FragmentRenderer` placed by an import gets a link with an EMPTY `html`.** The importer creates links through `FragmentEntryLinkLocalService`; a renderer whose markup is set by a `FragmentEntryLinkService` wrapper (the page editor's path) never gets it, so a renderer that delegates to the fragment entry renderer renders nothing, and its drop zone's children never show, while the link rows and counts look right (verified on 2026.Q3.5 with a personalized drop zone). Set the markup in a `ModelListener<FragmentEntryLink>` (`onBeforeCreate`/`onBeforeUpdate`, when `rendererKey` is yours and `html` is blank), which covers every path. Check `length(html)` of the links, not only their count.
- **A fragment element whose `key` matches neither a fragment nor a registered renderer is dropped together with every element nested in it**, with no log line (the importer collects a warning message that nothing prints). A missing custom `FragmentRenderer` that wraps a drop zone therefore takes its whole subtree with it. Count `fragmententrylink` rows per plid against the source.
- **A `Collection` element needs its full display config at the `definition` level, or the import NPEs and silently rolls back.** `collectionConfig` holds ONLY `collectionReference` + `collectionType`; `numberOfItems`, `numberOfItemsPerPage`, `numberOfPages`, `numberOfColumns`, `displayAllItems`, `displayAllPages`, `showAllItems`, `paginationType`, `listStyle`, `listItemStyle`, `templateKey`, `collectionViewports` are all **siblings of `collectionConfig`** — the importer reads them straight off `definition`. Only `numberOfColumns` and `numberOfItems` are unboxed without a null check (`layout-impl` `CollectionLayoutStructureItemImporter`, from source), so those two are the hard requirement; a missing one throws an NPE that rolls back the **entire** template/page import, logged only as a bare WARN. `collectionViewports[].id` is lowerCamel (`landscapeMobile`). When a composition change "won't take" despite a correct artifact, enable DEBUG on `com.liferay.layout.internal.importer` to surface the swallowed stack.
- **A property the page-definition schema doesn't allow crashes the import with a bare `StackOverflowError`** (the everit validator recurses through the self-referential schema instead of reporting "additionalProperties"). Every definition in `page_definition_json_schema.json` is `additionalProperties: false`, and properties are per-type — e.g. `gutters` is valid on a `Row` but NOT on a `Collection`. Validate each element's `definition` against the schema def for its type before shipping.
- **`fragmentStyle.backgroundImage` (and `backgroundFragmentImage`) as a string aborts the whole init** with `ClassCastException: String cannot be cast to Map` — they are the only style keys cast without a type check (2026.Q3.2). **The Map shape renders on 2026.Q3.5:** a Section's `"fragmentStyle": {"backgroundFragmentImage": {"title": {"value": "<title>"}, "url": {"value": "/documents/d/<library>/<slug>"}}}`, the shape a `headless-delivery` page-definition GET exports, is stored as the container's `styles.backgroundImage {title, url}`, emitted as `--lfr-background-image-<itemId>: url(…)`, and paints (`background-size: cover`; verified in a browser on a fresh import). On 2026.Q3.2 the same Map shape imported but did not render; there, for a per-instance background, declare an editable `<img data-lfr-editable-type="image">` in the fragment, place it behind the content with CSS, and set it through `fragmentFields` like any image.
- **A Section's padding goes in `fragmentStyle`**, as strings of the spacing scale: `"fragmentStyle": {"paddingLeft": "3", …}`. The same keys under `layout` (`"layout": {"paddingLeft": 3}`) import without an error and render no padding; `layout` holds `widthType` and the margins the exports carry (verified on 2026.Q3.5 by render).
- **A `Widget` element embeds a classic portlet:** `{"definition": {"widgetInstance": {"widgetConfig": {}, "widgetName": "com_liferay_login_web_portlet_LoginPortlet"}}, "type": "Widget"}`. `widgetConfig` holds flat portlet preferences. Verified on 2026.Q3.2 with the Login portlet. **The User Personal Bar works the same way in a master** (`com_liferay_product_navigation_user_personal_bar_web_portlet_ProductNavigationUserPersonalBarPortlet`, `portletSetupPortletDecoratorId: barebone`; verified on 2026.Q2.7): it renders per request, the avatar and personal menu for a signed-in user and a "Sign In" button for Guest, and a Guest render after a signed-in one got the button, not the avatar. Its wrapper is `lfr-layout-structure-item-<widget name lowercased, _ as ->`, so a theme can hide it with `.signed-out .lfr-layout-structure-item-com-liferay-product-navigation-user-personal-bar-web-portlet-productnavigationuserpersonalbarportlet` (Classic puts `signed-in`/`signed-out` on `<body>`).
- **Search widgets take their settings in `widgetConfig` too** (verified on 2026.Q3.5): the Blueprint Options widget's `sxpBlueprintExternalReferenceCode`, the Search Bar's `includeAttachments`, and a widget template as `"displayStyle": "ddmTemplate_<KEY>"` with `"displayStyleGroupExternalReferenceCode": "<site ERC>"` (an exported numeric `displayStyleGroupId` names another database's group). `ddm-templates/<dir>/ddm-template.json` creates such a widget template in the site: `className` the widget's template handler (`com.liferay.portal.search.web.internal.custom.facet.portlet.CustomFacetPortlet`, `com.liferay.portal.search.web.internal.result.display.context.SearchResultSummaryDisplayContext`), `resourceClassName` `com.liferay.portlet.display.template.PortletDisplayTemplate`, and a `ddmTemplateKey` of your choosing. Every re-fire updates it, adding a `ddmtemplateversion`.
- **`widgetConfig` is written to the layout's own preference row, and a portlet that reads a site-wide row still gets it** (2026.Q3.5, a Breadcrumb on a master). The importer stores it with owner 0 and the layout's `plid` (`PortletPreferencesPortletConfigurationImporterImpl`). The Breadcrumb and the navigation menu read their settings from a **site-wide** row instead (owner the group, `plid` 0), which `RuntimeTag` creates on the widget's first render, from the layout's values, when none exists. Each import draws the widget a new instance id and deletes the old instance's rows, site-wide one included, so every import's first render creates the site-wide row afresh from `widgetConfig`. So a widget's settings in `widgetConfig` reach every page (verified: 8480's 7 breadcrumb preferences in a master's `widgetConfig` rendered "Company › Site › Page" on a fresh site, and again after a re-fire). The links themselves do not change: a link comparison cannot see a `widgetConfig` change; a render can.
- **A fragment's `[@liferay.navigation_menu /]` cannot be configured from a tree** (2026.Q3.5). The macro renders the **non-instanced** `SiteNavigationMenuPortlet`, whose one site-wide row Classic's own header (`navigation.ftl`) also renders and creates first, with only `{"portletSetupPortletDecoratorId": "barebone", "destination": "/search"}`. With no menu selected it lists the site's pages. `default_preferences` on the macro apply only when that row is created (`RuntimeTag:300–310`), so they lose to Classic's on any site whose pages rendered before; the row is tied to no page, so no import writes or deletes it; no handler writes portlet preferences, and no headless API that does was found. A site that chose a menu for it (an old demo, through the UI) kept that choice in this row, outside every export. Write the row after the import with a small OSGi endpoint (`PortletPreferencesLocalService.getPreferences(companyId, groupId, PREFS_OWNER_TYPE_LAYOUT, PREFS_PLID_SHARED, portletId)`, `setValues`, `updatePreferences`), naming the menu by `siteNavigationMenuExternalReferenceCode` and the template by `displayStyle` plus `displayStyleGroupExternalReferenceCode`; the old numeric ids are not needed. Verified: the header then listed the Header Menu with its dropdown, and the row survived re-fires. Or replace the macro with the Menu Display renderer, which takes its menu from `fragmentConfig` (`skills/manage-pages/SKILL.md`), at the cost of its own markup.
- **A fragment's `<lfr-drop-zone>` does not render from an initializer import by itself** — the importer builds the layout structure from the page-definition tree, not the fragment HTML. Declare an explicit `{"definition": {}, "pageElements": [...], "type": "FragmentDropZone"}` child on the Fragment element, and nest the dropped fragments inside it. (Manually re-adding the fragment in the editor works — the editor parses the HTML — which makes this easy to misdiagnose.)

## Master Pages

A master page lives under `layout-page-templates/master-pages/<master-name>/` with two files: `master-page.json` (just `{"name": "<Display Name>"}`) and `page-definition.json`.

> **The master's key is derived from its `name`, NOT its directory name.** The importer lowercases the `master-page.json` `name` and replaces spaces with dashes (`LayoutPageTemplateEntryLocalServiceImpl._generateLayoutPageTemplateEntryKey`). So `"name": "Site Master"` → key `site-master`, regardless of the `<master-name>` folder. A `settings.masterPage.key` that points at the folder name instead of the normalized name resolves to nothing and the importer **silently leaves the page master-less** on provision. Name the folder to match the normalized name (`site-master`) to keep the tree self-documenting. (Note: **style books are the opposite** — their key IS the directory name, per `StyleBookEntryZipProcessorImpl._getKey`.)

> **Renaming a master that already exists, even by case only, is not an upsert.** The importer finds an existing master by its exact name (`fetchLayoutPageTemplateEntry(groupId, collectionId, name, MASTER_LAYOUT)`, a case-sensitive `name = ?`), so a re-fire with `"name": "Site Master"` over a live `"site master"` **adds a second master** whose generated key is `site-master-0` (the key is taken), while every page keeps the old one. A rename through the service regenerates the key the same way, because the generator skips keys in use, the master's own included. To rename live masters and keep their key, rename each twice over `PATCH /o/headless-admin-site/v1.0/sites/{siteERC}/master-pages/{masterERC}` with only `{"name": …}`: first to a temporary name (which frees the key), then to the new name (which takes it back); then re-fire. A name-only body leaves the master's layout content alone (no `pageSpecifications`, so `LayoutUtil.updateContentLayout` updates metadata only), and pages keep rendering, since on 2026.Q2.7 a page stores its master by ERC (`layout.masterlpteerc`), which a rename does not change. Verified on 2026.Q2.7.

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

### Binding design to the master via `settings`

The master `page-definition.json` also carries a top level `settings` block (a sibling of `pageElement`) that wires the site's design onto every page the master wraps. This is the **portable way to apply a `themeCSS` CET, a style book, and global CSS/JS from the initializer tree** — `layout-set/public/metadata.json` has **no** key for a client extension, so a `themeCSS` CET is never applied there:

```json
{
	"pageElement": {
		"pageElements": [
		],
		"type": "Root"
	},
	"settings": {
		"globalCSSClientExtensions": [
			{
				"clientExtensionConfig": {
					"loadType": "default",
					"scriptLocation": "head"
				},
				"externalReferenceCode": "LXC:<config-erc>",
				"name": "<CET Name>"
			}
		],
		"styleBook": {
			"key": "<style-book-key>",
			"name": "<Style Book Name>"
		},
		"themeCSSClientExtension": {
			"externalReferenceCode": "LXC:<config-erc>",
			"name": "<CET Name>"
		},
		"themeName": "Classic",
		"themeSettings": {
			"lfr-theme:regular:show-footer": "false",
			"lfr-theme:regular:show-header": "false"
		}
	},
	"version": 1.1
}
```

- The CET external reference code is **`"LXC:" + <config-erc>`** (`CETConfigurationFactory._getExternalReferenceCode`) — a CET whose config ERC is `my-theme-css` is referenced as `"LXC:my-theme-css"`.
- **Give every `globalCSSClientExtensions` / `globalJSClientExtensions` entry a `clientExtensionConfig`.** Without it, a globalCSS entry still loads, but a globalJS entry renders **no** `<script>`, with no error and no log line, and the binding reads back fine (verified 2026.Q3.2). On that master-bound tag, the CET's `scriptElementAttributes` (`defer`) did not appear either.
- **These `settings` keys work on a master only.** On a page's own `layouts/*/page-definition.json`, `settings.globalJSClientExtensions` is ignored without error. Bind a CET to a single page with the live whole-page PUT instead (`skills/manage-pages/SKILL.md`).
- The render-time resolver (`ClientExtensionsServicePreAction`) walks **layout → master layout → layout-set**, so a single bind on the master cascades to every page that uses it.
- Because this is a master page-composition change, it takes effect only when the **initializer re-fires** (clean redeploy; delete + redeploy as the fallback). A bare `themeCSS` CET redeploy updates the served CSS of an already-bound CET but never creates the binding.
- **Deploy-order race:** the `themeCSS` CET must already be registered when the initializer imports the master. If it is not, the importer logs `No CET found for LXC:<config-erc>` and silently skips the binding — pages fall back to the base theme site-wide. Fix by retriggering the initializer (clean + redeploy) after the CET is up.

A page selects its master through its **own** `page-definition.json`, in a top level `settings.masterPage.key` — the only selector that works. A `masterPage` key beside `pageElement` (some shipped examples carry both) is inert on its own: the page imports without error and renders without the master (verified 2026.Q3.2). The value is the master's **normalized-name key** (lowercased, spaces→dashes — e.g. `site-master` for name `"Site Master"`), not the folder name:

```json
{
	"pageElement": {
		"pageElements": [
		],
		"type": "Root"
	},
	"settings": {
		"masterPage": {
			"key": "<normalized-master-name>"
		}
	},
	"version": 1.1
}
```

That covers the tree's pages only. The tree cannot mark a master as the site's default: an extra key in `master-page.json` makes the importer drop the master page. Mark it over REST after each provision (`skills/theme-and-design/SKILL.md` → "Mark It as the Site Default"). A page created over REST still gets no master, even with one marked as the default (verified on 2026.Q3.8), so set the master in that page's own specifications. Pages created in the UI were not tested.

Three consequences, verified on 2026.Q2.7 (the first two again on 2026.Q3.8):

- **A key naming no master on that site is ignored without a warning:** the page imports with no master, as if it had no key.
- **Removing the key does not unbind a page that already has a master.** A re-fire whose page definition had no `settings.masterPage.key` left the page bound to the master an earlier import gave it. The file and the live page then disagree until the site is rebuilt; set the master you want explicitly rather than by omission.
- **A page definition shared across projects (a reusable asset linked into several trees) must name no consuming project's master.** That would tie the generic asset to one project and carry the project's name into every later use. Leave the key out of the shared file and bind the master per site after the import: the whole-page PUT with `settings.masterPageItemExternalReference` on both specifications (`skills/manage-pages/SKILL.md`). By the point above, later re-imports keep that binding, so it is needed on a new site and after a rebuild; compare first.

## `layout-set/public/metadata.json`

Controls theme assignment and theme settings for the public (nonprivate) layout set. The theme is named by its **display name**, in `themeName`:

```json
{
	"settings": {
		"lfr-theme:regular:show-footer": false,
		"lfr-theme:regular:show-header": false,
		"lfr-theme:regular:show-header-search": false
	},
	"themeName": "Classic"
}
```

**`themeName` is the only theme key read.** `_updateLayoutSet` passes it to `_getThemeId`, which looks the theme up by `Theme.getName()` among the company's themes. `themeId` and `colorSchemeId` are never read: the color scheme stays the layout set's, and a `themeId` alone, or a `themeName` matching no deployed theme, sets the theme to blank, which renders the portal default, Classic. So an old `"themeId": "classic_WAR_classictheme"` appears to work only because Classic is the default (`BundleSiteInitializer`, 2026.Q3.5 source). Beside `metadata.json`, the same directory may hold `css.css` (the layout set's CSS), `js.js` (stored as the `javascript` setting) and `logo.png` (the site logo).

**Dialect is not shipped on 2026.Q3.5.** Its module is still in the source (`frontend-theme-dialect`), but without the `.lfrbuild-portal` marker, so the bundle has no `dialect-theme.war` and `/o/dialect-theme/css/main.css` answers `404`. A tree from an older demo that names `"Dialect"` (here, in each page definition's `settings.themeName`, or a style book's `themeId: dialect_WAR_dialecttheme`) silently falls back: the layout set to the default theme, each page and master to an inherited (null) `themeId`, which also stops the master's `settings.css` from rendering (see `page-definition.json` above). Dialect's style-book tokens and its CSS variables (`--color-brand-primary`, `--color-neutral-*`, …) mostly have no Classic name, so moving to Classic means re-mapping them. Re-mapping the style book is not the whole move: Dialect's own defaults (a 16px type scale, rounder corners, 48 px buttons and inputs, its card and label sizes) are in no style book and do not come along; `skills/theme-and-design/SKILL.md` → "The Old Theme's Defaults Do Not Come With It". `_dialectThemeDetected`, which imports the extender's own 5 `dialect-*` group fragments, never fires.

Use the `settings` block to switch off the stock theme chrome when a master page supplies a branded header and footer — otherwise both render, one above the other. Prefer this over hiding `#banner` / `#footer` with CSS: the setting is scoped to this layout set, whereas a `globalCSS` CET is injected instance wide and there is no site specific `body` class to scope such a rule to.

> **A `themeCSS` CET is not applied through this file** — `metadata.json` has no key for a client extension. `themeName` selects the base **WAR theme** (Classic), nothing more. The tree-expressible binding lives in the **master page's `page-definition.json`** `settings.themeCSSClientExtension` block (ERC `"LXC:" + <config-erc>`) — see "Binding design to the master via `settings`" under Master Pages above. The render-time resolver walks layout → master layout → layout-set, so the master bind cascades to every page using it and **survives reprovision**.
>
> So leave `themeName` as `"Classic"` and let the style book (`defaultStyleBookEntry: true`) plus the master-bound `themeCSS` CET drive appearance. Reach for the themeCSS CET for Clay level overrides that have no token — Classic exposes no `headings*` style book tokens, for instance.

## `client-extension.yaml` for the Initializer

The tree the portal imports is always the project's **own** `site-initializer/` directory; an `assemble` block cannot supply it, and several site projects share one tree only through a link. See `rules/client-extension-types.md` → "Where a `siteInitializer` Tree Comes From".

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
        - Liferay.Headless.Site.everything
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

- Sample site initializer: [liferay-sample-site-initializer](https://github.com/liferay/liferay-portal/tree/master/workspaces/liferay-sample-workspace/client-extensions/liferay-sample-site-initializer)
- Production site initializer: [site-initializer-cms](https://github.com/liferay/liferay-portal/tree/master/modules/apps/site-initializer/site-initializer-cms)
- CET type details: `rules/client-extension-types.md`
- OAuth scopes: `rules/oauth-scopes.md`