# Guest Access

What an anonymous visitor can and cannot read. Every rule here fails **silently** — the page returns 200, the entry saves, nothing appears in the log — and every one of them looks correct to a signed in author, because the author's own session has the access the visitor lacks.

Consulted by `manage-pages`, `manage-objects`, and `manage-roles-permissions`; each holds the procedure, this card holds the fact.

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

## Public Forms Are Write Only

An object receiving public submissions holds whatever the visitor typed. Grant `ADD_OBJECT_ENTRY` and **not** `VIEW`: company scope VIEW publishes every name, email, and address to anonymous users.

The consequence is that no *query* over that object is available to the visitor, so never count it in the browser. Put the number on the public object instead.

Reach for an **`Aggregation` field** first: `businessType: Aggregation`, `function: COUNT`, over the relationship. It is computed server side and **ignores entry level permissions**, so a Guest who cannot read a single submission still gets the right count on the parent — verified with an unauthenticated `GET`. Nothing to maintain and no scripting to enable. It serialises as a **string**, so parse before arithmetic. See `skills/manage-objects/SKILL.md`.

A hand maintained counter kept in sync by an object action is the fallback for a value no aggregate function produces. Check first that object actions of the kind you need can actually be created — Groovy is disabled by default (`rules/object-actions-catalog.md`).

## Guest Cannot Read a Picklist Over REST

`GET /o/headless-admin-list-type/v1.0/list-type-definitions/by-external-reference-code/<ERC>/list-type-entries` returns **403** for Guest, and company scope `VIEW` on `com.liferay.list.type.model.ListTypeDefinition` does not lift it — the endpoint enforces more than that resource.

So a public form whose `<select>` is populated by fetching the picklist renders empty for the visitor, who submits with the field blank; the entry saves and looks successful. Ship the options in the fragment markup and let any fetch merely refresh them.

## Never Compute From Data the Visitor Cannot Read

Browser calls run as the visitor. A fetch that returns rows for you returns `0` for Guest — the API answers `200` with an empty list rather than failing — so arithmetic over it produces a confident wrong number (a full event rendering as "500 of 500") instead of an error. If a value cannot be derived from data the visitor may read, denormalize it or do not display it.

## Verify As the Visitor

`curl` with no `--user` and no cookie jar **is** a Guest request, and is the only cheap way to see what a visitor sees:

```bash
curl --silent --url "http://localhost:${PORT}/web/<site>/<page>" > /tmp/page.html
```

Assert on real values, and confirm placeholder strings are **absent** — placeholders still present mean the data did not resolve. Then open it signed out in a browser: `curl` executes no JavaScript, so anything client side is unverified until you do.
