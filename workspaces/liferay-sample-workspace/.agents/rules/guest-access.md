---

paths:
  - "**/fragments/**"
  - "**/layouts/**"
  - "**/resource-permissions.json"

---

# Guest Access

What an anonymous visitor can and cannot read. Every rule here fails **silently** — the page returns 200, the entry saves, nothing appears in the log — and every one of them looks correct to a signed in author, because the author's own session has the access the visitor lacks.

Consulted by `manage-pages`, `manage-objects`, and `manage-roles-permissions`; each holds the procedure, this card holds the fact.

## A Service Access Policy Gates Every Headless Call Before Permissions Are Consulted

Permissions are the second gate. The first is the **Service Access Policy** (SAP), and it decides whether the Headless endpoint is reachable at all. Every rule below this section is about what a reachable endpoint returns — none of it applies until a policy admits the call.

So a public fragment fetching web content fails here while every permission looks correctly granted, and the grant you are staring at is not the problem.

A policy is a named set of **allowed service signatures**, each the fully qualified implementation method the request would land on:

```text
com.liferay.headless.delivery.internal.resource.v1_0.StructuredContentResourceImpl#getStructuredContent
com.liferay.headless.delivery.internal.resource.v1_0.NavigationMenuResourceImpl#getNavigationMenu
```

Signatures name the `*ResourceImpl` class and method, not the REST path. Resolve one by finding the resource class behind the endpoint — the `internal.resource.v1_0` package of the module in `rules/headless-apis.md`.

Author it at Control Panel → Security → Service Access Policy, in **Advanced Mode** (the basic form does not expose raw signatures). Two independent booleans have to be right, and the entry silently admits nothing if either is wrong:

| Field | Set It To | Why |
| --- | --- | --- |
| Default | checked | A policy applies to unauthenticated requests only when it is marked default. Liferay collects the company's default entries on every request and unions them into the active set; a nondefault policy is only ever active when something names it explicitly. |
| Enabled | checked | A disabled entry is skipped entirely. |

**A policy is necessary but not sufficient.** It makes the endpoint reachable; the resource still has to be readable. Guest needs the VIEW grant covered in the rest of this card, and for an object that means `resource-permissions.json` at company scope. Both gates, or the visitor gets an empty result that looks like missing data rather than missing access.

Diagnostics and mechanics, verified against 2026.Q2.7 source and a live instance:

- **A SAP denial is a 403 with an empty body** (`{}` / `<Forbidden></Forbidden>`) — `access.control.sanitize.security.exception=true` (the default) strips the "Access denied to <class>#<method>" message. Reveal the exact denied signature by enabling DEBUG on `com.liferay.portal.security.access.control.AccessControlAdvisorImpl` (Server Administration → Log Levels, no restart), or temporarily setting the sanitize property false.
- **A 403 on a GET is never the permission layer** — permission failures on reads map to **404** (`PrincipalExceptionMapper`). 403 = SAP (empty body) or OAuth scope (see `rules/oauth-scopes.md` for telling those apart).
- **"Works with Basic auth / signed in" proves nothing about SAP**: password-authenticated requests get the `SYSTEM_USER_PASSWORD` policy (`*` — everything), guests get only `SYSTEM_DEFAULT` plus Default-flagged custom entries. The admin-vs-guest difference is often this, not permissions.
- **Only the entry JAX-RS resource method is checked** (service depth 1); nested backing-service calls are skipped. Whitelist the `*ResourceImpl#method`, never the kernel service.
- **In a plain (non-Vulcan) JAX-RS application, the first REMOTE service it calls is what gets checked.** A module's public endpoint (`auth.verifier.guest.allowed=true`) that reads object entries through Object REST's manager reaches `ObjectEntryService.getObjectEntry` per row, which Guest's default policy does not list: `SecurityException` from `AccessControlAdvisorImpl.accept`, a 500, **although the application sets `liferay.access.control.disable=true`** (2026.Q2.7). Admit that service signature in a Service Access Policy, knowing it opens the service on every endpoint.
- **All custom objects share one resource class** — `com.liferay.object.rest.internal.resource.v1_0.ObjectEntryResourceImpl`. Whitelisting e.g. `#getScopeScopeKeyPage` or `#getObjectEntry` opens that read shape for **every** custom object to guests (per-object permissions still apply). Note the by-scope list, the base get, and the relationship sub-resource (`/o/c/{obj}/{id}/{relationship}`) are **distinct methods** — one being open says nothing about the others. So is the approved-only list, `/o/c/{obj}/scopes/{key}/approved` → `#getScopeScopeKeyApprovedPage`: with only `#getScopeScopeKeyPage` admitted, Guest got `200` on the plain list and `403` (empty body) on `/approved` (2026.Q2.7).

Source: `SAPAccessControlPolicy` and `SAPEntryLocalServiceImpl` in `modules/apps/portal-security/portal-security-service-access-policy-service`.

## Object Entries Are Invisible Until Granted

Publishing an object does not make its entries readable. Page level VIEW does not confer entry level VIEW, and a server side **Collection element does not sidestep this** — it evaluates entry permissions as the visiting user, so an object backed listing renders **empty** for Guest while looking right to you.

Grant it in the tree, never by hand — `resource-permissions.json`, `scope` `"1"` for company wide. Procedure and the scope table: `rules/site-initializer-format.md`.

## An Object Has Two Resources

The action decides which resource name to grant on. Getting this wrong applies cleanly and grants nothing.

| Resource | Actions | Guest may hold |
| --- | --- | --- |
| `com.liferay.object.model.ObjectEntry#<id>` — what `[$OBJECT_DEFINITION_CLASS_NAME:<Name>$]` resolves to | `VIEW`, `UPDATE`, `DELETE`, `PERMISSIONS` | `VIEW` |
| `com.liferay.object#<id>` — write as `com.liferay.object#[$OBJECT_DEFINITION_ID:<Name>$]` | `ADD_OBJECT_ENTRY`, `PERMISSIONS` | `ADD_OBJECT_ENTRY` |

`UPDATE`, `DELETE`, and `PERMISSIONS` are `guest-unsupported` — ungrantable to Guest at any scope.

## File-Type Entries Need `DOWNLOAD_FILE`, and Folders Need Their Own VIEW

Two grants beyond entry VIEW that public file rendering depends on (verified 2026.Q2.7):

- **`file.link.href` is `DOWNLOAD_FILE`-gated.** A Guest holding only VIEW on a file-type object entry (Image, Video, CMSBasicDocument, …) gets the entry with an **empty** `file.link.href` (and `file.fileURL` reduced to the bare origin) — a client fragment then feeds garbage to its renderer, which reads as a parsing bug, not a permission gap. Grant Guest `DOWNLOAD_FILE` alongside VIEW — but only on objects whose action set actually includes it (sending an invalid action id fails the whole permissions PUT). A page that **maps** an image attachment server-side (`ObjectField_<id>#previewURL`) needs no download action: the inline `/documents/…` URL answered a Guest holding only VIEW on the entry (2026.Q3.5).
- **Object entry folders have their own ACL and default to authenticated-User VIEW, never Guest.** Entries granted to Guest can still vanish from guest search while visible to admins because the *folder* isn't viewable. The folder permissions endpoint is **scope-less**, unlike entry ACLs: `GET/PUT /o/headless-object/v1.0/object-entry-folders/{id}/permissions`. A reindex may be needed for search to pick up the new grant.

## Public Forms Are Write Only

An object receiving public submissions holds whatever the visitor typed. Grant `ADD_OBJECT_ENTRY` and **not** `VIEW`: company scope VIEW publishes every name, email, and address to anonymous users. Without `ADD_OBJECT_ENTRY`, a Form Container bound to the object renders **nothing** for the visitor — no form, no fields, no wrapper, no log line (verified 2026.Q3.2; `skills/manage-form-containers/SKILL.md`).

The consequence is that no *query* over that object is available to the visitor, so never count it in the browser. Put the number on the public object instead.

Reach for an **`Aggregation` field** first: `businessType: Aggregation`, `function: COUNT`, over the relationship. It is computed server side and **ignores entry level permissions**, so a Guest who cannot read a single submission still gets the right count on the parent — verified with an unauthenticated `GET`. Nothing to maintain and no scripting to enable. It serializes as a **string**, so parse before arithmetic. See `skills/manage-objects/SKILL.md`.

A hand maintained counter kept in sync by an object action is the fallback for a value no aggregate function produces. Check first that object actions of the kind you need can actually be created — Groovy is disabled by default (`rules/object-actions-catalog.md`).

### The Submission `POST` Still Returns the Created Entry

Write only does **not** mean write blind. A Guest holding `ADD_OBJECT_ENTRY` and no `VIEW` gets `200` with the **complete** entry body — `id`, every submitted field, the resolved picklist objects, the relationship FK and its ERC twin, and the workflow `status`. Verified with a fully unauthenticated `POST` on 2026.Q2, from the same visitor for whom `GET /o/c/<plural>` returned `totalCount: 0`.

So a form can safely `response.json()` and confirm back what was recorded. Do not code defensively around a body that is not there, and do not treat a parse failure as expected:

```javascript
const body = await response.json();

if (!response.ok) {
	throw new Error(body.title || body.detail || `HTTP ${response.status}`);
}
```

The read restriction applies to *querying the collection*, not to the response of the visitor's own write.

Guard the parse itself, though: this holds for the object endpoint's own `200` and `400` responses, both of which are JSON. A `500`, or anything returned by a proxy in front of Liferay, may be HTML — and parsing first means the visitor sees a JSON syntax error instead of the real failure. Read the body as text and parse inside a `try` when the form must report accurately.

## Vocabularies Created Over REST Need a Guest VIEW Grant

A vocabulary created with `PUT /o/headless-admin-taxonomy/v1.0/sites/{site}/taxonomy-vocabularies/by-external-reference-code/{erc}`, and its categories, grant VIEW only to Owner and the group's administrator role (CMS Administrator in the CMS site). Guest then gets an empty list from `…/sites/{site}/taxonomy-vocabularies` and `404` from `…/taxonomy-vocabularies/{id}/taxonomy-categories`, even with a Service Access Policy admitting both. A public facet built from them renders no panel, with no error.

Grant Guest VIEW on the vocabulary **and** on each category, through `PUT …/taxonomy-vocabularies/{id}/permissions` and `PUT …/taxonomy-categories/{id}/permissions`. Both are full replaces of a bare `Permission[]` (`skills/manage-roles-permissions/SKILL.md`), so send every existing grant back. With both granted, the vocabularies and their categories list for Guest. The `{site}` path segment accepts the group's ERC or key as well as its id (`L_CMS`, `CMS`, `20121` gave the same four vocabularies). Verified on 2026.Q2.7.

## Guest Cannot Read a Picklist Over REST

`GET /o/headless-admin-list-type/v1.0/list-type-definitions/by-external-reference-code/<ERC>/list-type-entries` returns **403** for Guest, and company scope `VIEW` on `com.liferay.list.type.model.ListTypeDefinition` does not lift it — the endpoint enforces more than that resource.

So a public form whose `<select>` is populated by fetching the picklist renders empty for the visitor, who submits with the field blank; the entry saves and looks successful. Ship the options in the fragment markup and let any fetch merely refresh them.

## Never Compute From Data the Visitor Cannot Read

Browser calls run as the visitor. A fetch that returns rows for you returns `0` for Guest — the API answers `200` with an empty list rather than failing — so arithmetic over it produces a confident wrong number (a full event rendering as "500 of 500") instead of an error. If a value cannot be derived from data the visitor may read, denormalize it or do not display it.

## Verify as the Visitor

`curl` with no `--user` and no cookie jar **is** a Guest request, and is the only cheap way to see what a visitor sees:

```bash
curl --silent --url "http://localhost:${PORT}/web/<site>/<page>" > /tmp/page.html
```

Assert on real values, and confirm placeholder strings are **absent** — placeholders still present mean the data did not resolve. Then open it signed out in a browser: `curl` executes no JavaScript, so anything client side is unverified until you do.

**With `web.server.host` set (a tunnel), pages fetched on `localhost` carry absolute links and form `action`s to `<public-host>:<local-port>`**, which go nowhere. Harmless for HTML assertions, but run any click-through over the public URL.