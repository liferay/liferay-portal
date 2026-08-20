---

description: Orchestrate a complete Liferay site experience from a single prompt. Composes objects, business logic, fragments, pages, roles, and theme into a working site. Use when the user asks to "build a site", "create a site experience", or describes a multiobject, multipage scenario. Calls all other skills in sequence.
name: build-site

---

# Build Site

One command orchestrator. The user describes the site; this skill calls the right subskills in the right order.

## When to Invoke

- "Build a Bookstore site with an Author object, a Book object, and a home page"
- "Create a site experience for customer onboarding"
- "Scaffold the full job board site"
- Any multiobject, multipage request that spans data and presentation

## Workflow

The sequence below is the canonical order. Skip phases the user has not requested; do not add phases they have not asked for.

### Phase 0: Scope Confirmation

Before calling any subskill, confirm the scope with the user:

1. **Site name** — what to call the site

1. **Objects** — list of entity names with their key fields and relationships

1. **Pages** — list of pages and their purpose

1. **Roles** — named roles and their intended access (viewer, editor, admin)

1. **Theme** — any color, font, or visual requirement (optional)

Proceed only after the user confirms or corrects the scope list.

The canonical model is **site initializer first**: the `siteInitializer` CET tree is the single source of truth, and the site is created by triggering the initializer rather than by calling the live page API. After the initial build, iterate by editing the source tree and applying each change by the cheapest reliable path — see "Iterating on the Site" below and the spine in `rules/site-initializer-format.md`.

### Phase 1: Prerequisites

Call `feature-flags` for the full set of flags the workflow needs:

| Scenario | Required Flags |
| --- | --- |
| Site pages via API | `LPD-35443` |
| Fragment composition via API | `LPD-39244` |
| Object entry permissions | `LPD-17564` |
| MCP transport | `LPD-63311` |

Report the gap table. Enable flags only after explicit user confirmation. Bounce Tomcat if any flags are written.

### Phase 2: Transport Selection

Probe for the MCP server:

```bash
# Release 2026.Q1+ uses /o/mcp (Streamable HTTP transport); 2025.Q4 used /o/mcp/sse (SSE). See skills/mcp-server.

curl \
	--head \
	--silent \
	--url "http://localhost:${PORT}/o/mcp"
```

- **2xx**: MCP is available. Use the `call-http-endpoint` MCP tool for all subsequent API calls.
- **Otherwise**: Fall back to direct `curl` calls with Basic auth.

### Phase 3: Data Model

For each object in the confirmed scope, call `manage-objects`:

1. Create and publish the object definition.

1. Add all fields.

1. Add picklists (if any field references a picklist).

1. Add relationships between objects (parent → child).

1. Add validations.

For each business logic requirement, call `manage-object-logic`:

1. Choose the trigger and action type.

1. Create notification templates if needed.

1. Create the object action.

### Phase 4: Site Initializer Scaffold

Call `scaffold-client-extension` with type `siteInitializer` to create the CET that will provision the site. This tree is the source of truth for the site's fragments, pages, theme metadata, and roles. Populate it in Phases 5–8 per `rules/site-initializer-format.md`, then trigger it in Phase 9.

### Phase 5: Fragments

For each unique layout section needed by the page list, call `scaffold-fragment`:

1. Create the fragment source files **inside the initializer tree** at `site-initializer/fragments/group/<collection-key>/fragments/<fragment-name>/` (note the required `fragments/` nesting level under the collection). For the initial build, the content may be hardcoded (static text and images); later iterations bind it to objects.

1. Record each fragment's `key` (its `<fragment-name>` directory name) for Phase 6 — page definitions reference it as `fragment.key` with `siteKey: "[$GROUP_KEY$]"`.

### Phase 6: Pages

For each page in the confirmed scope, call `manage-pages` to author it in the initializer:

1. Write `site-initializer/layouts/<NN-page-name>/page.json` with the correct type (Content Page default).

1. Write `page-definition.json` composing the page with fragment elements (using keys from Phase 5).

1. Add the navigation menu and SEO metadata via the initializer's `layout-set/` and page metadata.

The pages come into being when the initializer is triggered in Phase 9.

### Phase 7: Theme and Design (Optional)

When the user provided visual requirements, call `theme-and-design`:

1. Generate and deploy the `themeCSS` CET.

1. Create and assign the style book.

1. Create the master page with header and footer fragments.

### Phase 8: Roles and Permissions

For each role in the confirmed scope, call `manage-roles-permissions`:

1. Create the role.

1. Assign permissions on each object definition.

1. Assign permissions on each page (restrict visibility if needed).

### Phase 9: Provision the Site

Deploy the CET and trigger the initializer. This creates the site with its fragments, pages, and roles in one pass:

```bash
# Deploy the site initializer CET

cd client-extensions/<site-init-name> && blade gw deploy

# Trigger it to create the site

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

> **Caution:** resolving a site-initializer template through `POST /sites` is unreliable (the current `Site` DTO uses `templateKey`, not `templateExternalReferenceCode`). The portable path is to let the `siteInitializer` CET autoprovision on deploy, and to reprovision by **delete then redeploy** — see `rules/site-initializer-format.md`.

Save the site's `externalReferenceCode` as `<site-erc>` from the response.

### Phase 10: Verification

Confirm the site is functional:

```bash
# Site exists

curl \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-site/v1.0/sites/<site-erc>" \
	--user "test@liferay.com:test" \
	| jq '{externalReferenceCode, name}'

# Pages exist

curl \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-site/v1.0/sites/<site-erc>/site-pages" \
	--user "test@liferay.com:test" \
	| jq '[.items[] | {name, friendlyUrlPath, type}]'

# Object definitions published

curl \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions?filter=status%20eq%20%27approved%27" \
	--user "test@liferay.com:test" \
	| jq '[.items[] | {name, status}]'

# Probe site home page

curl \
	--head \
	--silent \
	--url "http://localhost:${PORT}/web/<site-friendly-url>"
```

Report the final state: objects created, pages created, roles created, site URL.

## Iterating on the Site

After the initial build, the user improves the site through natural language prompts. The initializer tree stays the source of truth: edit the source files first, then apply the change by the cheapest reliable path. The initializer runs only at site creation, so only page changes force a reprovision.

| Prompt Intent | Source Edit | How to Apply | Reprovision? |
| --- | --- | --- | --- |
| "Give me a unified theme" | `themeCSS` CET (+ style book, master page) | `theme-and-design` → `blade gw deploy` | No |
| "Replace the hardcoded list with an object" | object definition + `batch/` data | `manage-objects` → `object-admin` API / batch import | No |
| "Edit / restyle a fragment" | `fragments/group/<key>/...` | `scaffold-fragment` → reprovision (there is no portable live fragment import endpoint) | Yes |
| "Add a new fragment" | new `fragments/group/<key>/...` | `scaffold-fragment` → reprovision | Yes |
| "Add a page" / recompose a page | `layouts/<NN>/page*.json` | reprovision (delete + recreate from initializer) | Yes |

Object definitions and entries are company scoped, so a reprovision for a page change preserves runtime data. See `rules/site-initializer-format.md` for the reprovision commands.

## Example Prompt Interpretation

**User**: "Build a Bookstore site with an Author object (name, bio), a Book object (title, isbn, linked to Author), a home page listing Books, and a Reader role that can only view Books."

**Scope confirmation**:
- Objects: Author (name: Text, bio: LongText), Book (title: Text, isbn: Text, authorId: Relationship to Author)
- Pages: Home (Content Page, lists Books via a fragment)
- Roles: Reader (VIEW on Book object, VIEW on Home page)

**Execution order**: Phase 1 (flags) → Phase 2 (MCP probe) → Phase 3 (Author, Book, relationship) → Phase 4 (scaffold initializer) → Phase 5 (book-list fragment in tree) → Phase 6 (home page in `layouts/`) → Phase 8 (Reader role + page permissions) → Phase 9 (deploy + trigger initializer) → Phase 10 (verify)

## Handling Partial Failures

When a phase fails:

1. Surface the error and the raw API response to the user.

1. Diagnose the cause (missing flag, validation error, unreachable endpoint).

1. Ask the user whether to retry the failed phase, skip it, or abort.

1. Do not proceed to dependent phases when a prerequisite phase has failed.