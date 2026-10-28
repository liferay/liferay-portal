---

description: Create, update, and publish Liferay Object definitions — fields, relationships, picklists, and validations. Use when the user asks to create an object, add a field, define a picklist, add a relationship, or set up an object validation.
name: manage-objects

---

# Manage Objects

Create, publish, and query Liferay Object definitions and entries via the Headless Admin Object API.

## When to Invoke

- "Create an object", "define a data model", "make a custom entity"
- "Add a field", "add a relationship", "add a picklist"
- "Set up a validation", "publish the object"
- Called by `build-site` during the data model phase

## Prerequisites

Probe these flags via `feature-flags` before the calls that need them; record the result for the session. Do not enable a flag without explicit user confirmation. Flag defaults are `inferred — verify`.

| Flag | Default | Required For |
| --- | --- | --- |
| `LPD-17564` | off | CMS, including the object collaborators API (sharing an entry with users), on 2026.Q1 and 2026.Q2. Not needed on 2026.Q3, where the flag no longer exists (`manage-roles-permissions` → "Share an Entry With One User") |

Object definitions, fields, relationships, and validations need no flag. On a site built from a site initializer, object definitions and data apply **live** via these APIs (and batch import) with no reprovision, and — being company scoped — survive a page reprovision (see `rules/site-initializer-format.md`).

## Workflow

> **Ship the objects in a client extension.** Object definitions, fields, picklists, relationships, validations, and seed entries belong in the site initializer tree (`object-definitions/`, `object-fields/`, `list-type-definitions/`, `object-relationships/`, `object-entries/`; see `rules/site-initializer-format.md` → "Objects and Picklists"), or in a `batch` CET when there is no site. The REST calls in this skill show each payload's shape and serve one-off changes to a running instance; do not build the deliverable with them. See `liferay-rules.md` → "Deliver Through Client Extensions".

### Collect Object Definition Inputs

Gather from the user or infer from context:

- `name` — singular CamelCase label (e.g. `Book`)
- `label` — human readable singular (e.g. `Book`)
- `pluralLabel` — REST path safe plural (e.g. `books`)
- `scope` — `company` (default, global) or `site`
- `externalReferenceCode` — must NOT start with `L_` (reserved for Liferay-shipped definitions; a `C_` prefix is conventional but not required)
- `storageType` — where entries are stored: Liferay's own DB (the default) or an external source such as `salesforce` or `function#<cet-erc>` (see `integrate-external-data`). Any `storageType` sent over REST needs feature flag `LPS-135430`, which is off by default. Do **not** send this on the create call for default DB storage — omit it and Liferay assigns the default (see **Create the Object Definition**).
- Fields list — each with `businessType`, `name`, `label`, `required`

### Create the Object Definition

```bash
curl \
	--data '{
		"label": {"en_US": "<Label>"},
		"name": "<Name>",
		"panelCategoryKey": "control_panel.object",
		"pluralLabel": {"en_US": "<PluralLabel>"},
		"scope": "company"
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions" \
	--user "test@liferay.com:test"
```

**Do not send `"storageType": "default"` on create** — it returns `400 ObjectDefinitionStorageTypeException`. Omit `storageType` and Liferay assigns default DB storage (for external storage, see `integrate-external-data`).

Save the returned `id` as `<definition-id>`.

**Set `panelCategoryKey`, or administrators cannot find the object.** The object still publishes and its REST endpoint works, but it has no entry in the admin menus, so nobody can browse its entries in the UI. `control_panel.object` lists it under Control Panel → Object. For an object that already exists, `PATCH /o/object-admin/v1.0/object-definitions/<definition-id>` with `{"panelCategoryKey": "control_panel.object"}` adds it without republishing.

#### Prefer Creating Fields Inline

The create call accepts a full `objectFields` array — the same raw DTO shape a site initializer uses. **Prefer this over creating the definition and then adding fields.** One call instead of N+1, the definition never exists in a half built state, and it sidesteps the standalone field call's `required` trap below:

```bash
curl \
	--data '{
		"externalReferenceCode": "<ERC>",
		"label": {"en_US": "<Label>"},
		"name": "<Name>",
		"objectFields": [
			{
				"businessType": "Text",
				"indexed": true,
				"label": {"en_US": "<FieldLabel>"},
				"name": "<fieldName>",
				"required": true
			}
		],
		"panelCategoryKey": "control_panel.object",
		"pluralLabel": {"en_US": "<PluralLabel>"},
		"scope": "company",
		"titleObjectFieldName": "<fieldName>"
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions" \
	--user "test@liferay.com:test"
```

Set `titleObjectFieldName` here too — it names the field used as the entry's display title in the UI and in relationship pickers. Omitting it leaves entries labeled by ID.

Use the standalone `POST .../object-fields` call below only to add a field to an object that already exists.

### Add Fields

For each field in the user's list:

```bash
curl \
	--data '{
		"businessType": "<businessType>",
		"label": {"en_US": "<FieldLabel>"},
		"name": "<fieldName>",
		"required": false
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions/<definition-id>/object-fields" \
	--user "test@liferay.com:test"
```

**`required` is mandatory on the standalone `POST .../object-fields` call** — omitting it returns `500` with a `getRequired()` NullPointerException. Always send `true` or `false` explicitly.

**Give every field its own `externalReferenceCode` when the write will be repeated** (a batch CET, a setup script). A field created without one cannot be matched by a later `UPSERT`, so re-running against an environment where the definition already exists tries to add the field again and fails with `Duplicate name <fieldName>` (`ObjectFieldNameException$MustNotBeDuplicate`; in a batch CET the task ends `FAILED`). The field created without one got a random ERC. Verified on 2026.Q3.2.

`businessType` values: `Text`, `LongText`, `Integer`, `Decimal`, `Boolean`, `Date`, `DateTime`, `Attachment`, `Relationship`, `Picklist`, `Aggregation`.

### Add Picklists (When Needed)

Create the picklist first, then reference it in the field:

```bash
# Create list type definition

curl \
	--data '{
		"name": "<PicklistName>",
		"listTypeEntries": [
			{"key": "value1", "name": "Value One", "type": ""},
			{"key": "value2", "name": "Value Two", "type": ""}
		]
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-list-type/v1.0/list-type-definitions" \
	--user "test@liferay.com:test"
```

Save the returned `id` as `<list-type-id>`. Then add a `Picklist` field referencing `"listTypeDefinitionId": <list-type-id>`. In a site initializer, reference the picklist by ERC instead — `"listTypeDefinitionExternalReferenceCode": "<ERC>"` — so no numeric ID is baked into the tree.

**Entry `key` values must be letters and digits only** — hyphens, underscores, and spaces all throw `ListTypeEntryKeyException: Key must only contain letters and digits`. Use camelCase (`contractorInstaller`), keep the human label in `name`/`name_i18n`, and slugify source-system values that contain separators. The constraint is on entry keys specifically; the definition's ERC may contain underscores.

> **A public form cannot fetch this picklist.** Guest gets 403 from the list type REST endpoint, so a `<select>` populated by fetch renders empty and the entry saves blank looking like success. Ship the options in the fragment markup. See `rules/guest-access.md`.

Writing a picklist value accepts **either** the object form `{"key": "vegan"}` or the bare string `"vegan"`. Both persist identically and both read back as `{key, name}`. An unknown key is rejected with `400 Object field name "<field>" is not mapped to a valid list type entry`.

#### A List Type Entry `key` With an Underscore or a Hyphen Returns 500

`POST .../list-type-definitions/{id}/list-type-entries` fails with a bare `500 Internal Server Error` when `key` contains an underscore or a hyphen, and so does an entry in the inline `listTypeEntries` array of the definition create call. `gluten_free`, `dairy_free`, and `test-hyphen` all failed this way, while `glutenfree` and `testCamel` succeeded. Verified on a self hosted 2026.Q1 bundle.

The response carries no validation message, so it reads like a transient server fault rather than a rejected key. The **inline** array is worse: in some orderings the create call returns `200` and silently drops the *good* entries along with the bad one. Compare the length of the `listTypeEntries` you read back with what you sent rather than trusting the `200`.

Use camelCase for every list type entry `key`: `glutenFree`, not `gluten_free` or `gluten-free`. Object field names permit digits after the first letter, but they were not tested with an underscore or a hyphen, so do not assume either limit carries over.

#### A `state` Picklist Requires `defaultValue` and `defaultValueType`

Setting `"state": true` on a Picklist field turns it into a status field with a transition graph. It also makes two field settings **mandatory**, and omitting them fails the whole definition create:

```text
400 ObjectFieldSettingValueException.MissingRequiredValues
The settings "defaultValue, defaultValueType" are required for object field "<fieldName>"
```

Supply both. `defaultValue` is the entry **key** of the starting state; `defaultValueType` is `inputAsValue` for a literal key (verified — an expression variant exists but is not confirmed here):

```bash
{
	"businessType": "Picklist",
	"label": {"en_US": "Status"},
	"listTypeDefinitionId": <list-type-id>,
	"name": "<entity>Status",
	"objectFieldSettings": [
		{"name": "defaultValue", "value": "pending"},
		{"name": "defaultValueType", "value": "inputAsValue"}
	],
	"required": true,
	"state": true
}
```

Two consequences worth planning around:

- **The default actually fires.** A POST that omits the field entirely still succeeds and lands on the default state, despite `"required": true`. This is what you want for a public form — the visitor never submits a status.
- **Liferay generates a fully connected `stateFlow` automatically.** Every state gets a transition to every other state, returned as a third `stateFlow` setting you did not send. If a state should be terminal (a canceled registration that cannot go back to pending), you must constrain the flow explicitly — the default permits it.
- **A constrained `stateFlow` also polices UPDATEs, which bites bulk reloads.** Re-importing an existing entry whose state jumps a non-adjacent transition is rejected — while a fresh INSERT may set **any** initial state. So an in-place data reload can strand entries in an unreachable state; fix by deleting and recreating the stuck entries (or reprovisioning the data) rather than PATCHing through intermediate states.

- **`state` cannot be turned on later.** On a published Picklist field, `PATCH {"state": true}` fails with a bare `500` (`ObjectFieldStateException: Object field must be required when the state is true`), and `required` itself cannot be switched on after publication (`ObjectFieldRequiredException`, also a bare `500`), with or without `state` in the same body. Create the field with `state: true` and `required: true` from the start. To convert, delete it and create a new field under another name. Read every entry's value first, because the delete drops the data, and repoint anything keyed on the old name (Data Set columns, form `fieldKey`s). Verified on 2026.Q3.2.

A plain (non `state`) Picklist needs none of this.

### Add Relationships

Relationships are defined on the parent object. The `objectDefinitionId2` is the child definition's ID.

**A depot-scoped object (a CMS structure) can be related only to another depot-scoped object.** A `manyToMany` from a site-scoped object to a depot-scoped one answered `400 ObjectDefinitionScopeException "An object definition scoped by depot can only be related to object definitions of the same scope"` (2026.Q2.7, throwaway objects). So moving one side of a relationship out of a Space means dropping the relationship or moving both sides; denormalize what the pages show onto the remaining side first.

```bash
curl \
	--data '{
		"label": {"en_US": "<RelationshipLabel>"},
		"name": "<relationshipName>",
		"objectDefinitionId2": <child-definition-id>,
		"type": "oneToMany"
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions/<parent-definition-id>/object-relationships" \
	--user "test@liferay.com:test"
```

Relationship `type` values: `oneToMany` and `manyToMany`. The OpenAPI enum also lists `oneToOne`, but the server rejects it with a bare `500` (`ObjectRelationshipTypeException: Invalid type oneToOne` in the log only): on 2026.Q3.2, `ObjectRelationshipUtil`'s accepted set holds just the other two, for every pair of objects (from source, and reproduced live). Model a 1:1 as `oneToMany` and enforce single-child cardinality in your own logic. Add `"deletionType"` — `cascade`, `disassociate`, or `prevent` — to say what happens to children when the parent is deleted.

Create relationships **before** publishing, while both definitions are still drafts; this matches the site initializer's handler order and avoids a second publish cycle.

The response carries the generated foreign key field — **read it instead of deriving the name**. `objectField.name` is the FK, and `objectFieldSettings.objectRelationshipERCObjectFieldName` is its ERC twin:

```json
{
	"objectField": {
		"name": "r_eventRegistrations_c_eventId",
		"objectFieldSettings": [
			{
				"name": "objectDefinition1ShortName",
				"value": "Event"
			},
			{
				"name": "objectRelationshipERCObjectFieldName",
				"value": "r_eventRegistrations_c_eventERC"
			}
		]
	}
}
```

### Extending a System Object (Account, User, …)

Verified on 2026.Q3.2 against `AccountEntry`:

- **Resolve the real name first.** Account's definition is named `AccountEntry`, so `filter=name eq 'Account'` returns nothing — use `contains(name,'Account')`. The fixed ERCs are `L_ACCOUNT` for Account and `L_USER` for User, while the numeric ids vary by environment.
- **Add fields with the same `POST .../object-fields` call. There is no publish step.** The definition is already approved and `modifiable: false`, and a new field is live at once.
- **The field is served by the system object's own module, not `/o/c`** — e.g. `/o/headless-admin-user/v1.0/accounts`. It appears there as a **top level property** of the DTO, not inside `customFields`. Check that module's `openapi.json` for the new property.
- **Picklist values there are bare strings.** Send and expect `"sector": "manufacturing"`; the `{key, name}` object form returns `400 "The property name \"<field>\" is invalid for property type TEXT"`. This is unlike custom objects (see "Picklist Fields Return `{key, name}` Objects").
- **An Integer value above 2^31 − 1 reads as a type error there.** `PATCH /o/headless-admin-user/v1.0/accounts/{id}` with `"size": 240024502400` answers `400 "The property name \"size\" is invalid for property type INTEGER"`, which says nothing about range. Bound integer inputs client side. Verified on 2026.Q3.2.
- **An unknown picklist key is silently dropped there, not rejected.** `POST /o/headless-admin-user/v1.0/accounts` with `"sector": "energy"` (not an entry) returns `200`, creates the account, and stores `""`. Custom objects return `400` for the same mistake. A data load into Account must check each key against the picklist itself, then read back the stored values. Verified on 2026.Q3.2.
- **Custom fields on Account cannot be sorted or filtered** through `/o/headless-admin-user/v1.0/accounts`; `sort=` accepts only a small, fixed set of built-in fields (`name`, `dateCreated`, `dateModified`, `externalReferenceCode`, `status`, `type`). To rank accounts by a custom value, store it on a plain custom object related to the Account, which `/o/c` can sort.
- **The built-in `name` is filterable.** `filter=name eq 'Acme'` is an exact match, case-insensitive, with no partial match; double any single quote in the value. Verified on 2026.Q3.2.
- **An Aggregation field works on a system object too** (verified on Account: `COUNT` over Account → Lead). It appears as a top-level string property (`"numberOfLeads": "1"`), also returned with `?fields=numberOfLeads`, and it inherits the restriction above: no sort, no filter. It is computed when read, so a child re-linked by PATCH counts on the very next GET.
- **A child links to an Account through `r_<relationshipName>_accountEntryId`** (number) or `r_<relationshipName>_accountEntryERC` (the Account's ERC), not the `_c_<object>Id` form used between custom objects. Both work on PATCH.
- **A Decimal field there refuses any JSON value without a fraction.** On an `AccountEntry` extension field, `null`, `0`, `28` and `"28"` each answer `400 "The property name \"<field>\" is invalid for property type DOUBLE"`, while `0.0`, `28.0` and `28.5` pass (2026.Q2.7, measured). An unset one reads back as `0.0`. Browser code is the trap: `JSON.stringify(28.0)` writes `28`, so a form round-tripping a stored value fails whenever it is whole, `0` included. Send the field only when it is set and not a whole number, or build that part of the body as text.
- **An account cannot be categorized over REST.** The `Account` DTO declares `taxonomyCategoryBriefs` as `@JsonProperty(access = READ_ONLY)`, so a `POST`/`PATCH`/`PUT` carrying it answers `200` and assigns nothing (`AccountResourceImpl._getAssetCategoryIds` is unreachable from a request), and the read side returns `null` for an account that has categories. A site initializer's `accounts.json` bypasses Jackson, but its `taxonomyCategoryReference.siteKey` would have to name the company group by its **company id**, which is instance-specific. Carry a category on an account as a Text field holding the category's ERC and resolve it in code. Verified on 2026.Q2.7.
- **Addresses are served only with `nestedFields=postalAddresses`.** Without it `postalAddresses` and `accountContactInformation.postalAddresses` are both `[]` on an account that has addresses; with it they come back with `addressRegion` and `addressCountry` as **names** (`Florida`, `United States`), which the write path accepts back (it resolves a region by code or name, a country by A2, A3 or name). The list endpoint takes it too, beside `filter=type eq 'business'` (verified 2026.Q2.7).
- **A system object's extension fields do not reach a site initializer's `accounts.json`.** `_addAccounts` calls the account resource directly, past Vulcan's entity-extension reader, so a custom field in that file is dropped silently; set the values over REST after provisioning (a `PATCH` by ERC applies them). Fields added through `object-fields/` with `[$OBJECT_DEFINITION_ID:AccountEntry$]` do land: the token pass covers system objects.

### Aggregation Fields — Publish a Count Without Exposing the Rows

An `Aggregation` field computes a value **over a relationship** and stores it on the parent. It answers "how many children does this record have" with no scripting at all, and it is what makes a remaining capacity or item count figure publishable on a public page.

Add it to the parent **after** the relationship exists, naming the relationship it walks:

```bash
curl \
	--data '{
		"businessType": "Aggregation",
		"label": {"en_US": "Registrations"},
		"name": "registrationCount",
		"objectFieldSettings": [
			{"name": "function", "value": "COUNT"},
			{"name": "objectRelationshipName", "value": "eventRegistrations"}
		],
		"required": false
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions/<parent-definition-id>/object-fields" \
	--user "test@liferay.com:test"
```

`function` accepts `COUNT`, `SUM`, `AVERAGE`, `MIN`, `MAX` (`ObjectFieldSettingConstants`); anything but `COUNT` also needs the child field to aggregate. Omitting `"required"` returns a bare `500` (a `NullPointerException` in `ObjectFieldResourceImpl`), not a validation message, so always send it. Liferay adds a third `filters` setting itself (`[]` when you send none). A filter is accepted with `200` whatever field it names, but not every filter works when the parent is read: `status`, a picklist `includes` and a Boolean `eq` count correctly, while a Boolean `includes` (`{"in": [true]}`) or any filter on the relationship FK (`r_<relationshipName>_<parentPK>Id`, quoted or not) makes every read of the parent's entries drop all their custom fields, with `InvalidFilterException: Incompatible types` in the log. Verified on 2026.Q3.8.

In a site initializer tree, provision an Aggregation field via `object-fields/` (it must land **after** the relationship exists and before publish; the file upserts on redeploy). When reading related data over REST, note that `nestedFields` requested on the **many** side of a relationship returns the PARENT entry, not the children — fetch children from the parent side or filter the child collection by FK.

Three behaviors that decide whether you can use it:

- **It ignores entry level permissions.** A Guest who cannot read a single child row still receives the correct count on the parent — verified with an unauthenticated `GET`. This is what lets a private, write only submissions object feed a public number, and it is a genuinely better option than the object action counter that `manage-pages` and `rules/guest-access.md` describe: nothing to maintain, nothing to drift, no scripting to enable.
- **It serializes as a string.** `"registrationCount": "2"`, not `2`. Parse before arithmetic — `attendeeCapacity - registrationCount` in JavaScript silently concatenates, and in `jq` it throws `number and string cannot be subtracted`.
- **It is computed, so it is read only.** Do not send it on a POST or PATCH.
- **A change in the count fires no object action** on the parent or on its other children (verified on 2026.Q3.2). Anything derived from the count, such as a score on a related profile, must be recomputed from an action on the **child** (`onAfterAdd` on the counted object); see `manage-object-logic` → "Calling Back Into Liferay From the CX".

Whether an Aggregation field can be *mapped* into a Collection fragment is a separate question — `manage-pages` → "Mapping Limits" says aggregates cannot be mapped, and that was not retested here. Reading it over REST from fragment JavaScript is verified.

#### Count Only Some Children

Add a `filters` setting. Each filter carries `filterBy`, `filterType`, and a `json` value. `filterBy` and `filterType` sent as top level settings are rejected. To leave rejected entries out of a count (workflow status `4`, denied):

```json
{
	"name": "filters",
	"value": [
		{
			"filterBy": "status",
			"filterType": "excludes",
			"json": {
				"not": {
					"in": [
						4
					]
				}
			}
		}
	]
}
```

Verified on 2026.Q2: two pending children counted `2`, and rejecting one through the workflow dropped it to `1`. `includes` takes `{"in": [...]}` instead. For a custom field, `filterBy` is the field name and the values are its stored values, such as picklist keys. `eq` and `ne` take `{"eq": <value>}` and `{"ne": <value>}`. Source: `InclusionOperatorsObjectFilterParser` and `EqualityOperatorsObjectFilterParser`.

#### In a Site Initializer

An Aggregation field names a relationship, so it cannot go in `object-definitions/`, which runs before `object-relationships/`. Put it in `object-fields/<name>.json`, which runs after them. The file wraps the fields:

```json
{
	"object-fields": [
		{
			"businessType": "Aggregation",
			"label": {
				"en_US": "Registrations"
			},
			"name": "registrationCount",
			"objectFieldSettings": [
				{
					"name": "function",
					"value": "COUNT"
				},
				{
					"name": "objectRelationshipName",
					"value": "eventRegistrations"
				}
			],
			"required": false
		}
	],
	"objectDefinitionId": "[$OBJECT_DEFINITION_ID:Event$]"
}
```

A bare field DTO at the top level is skipped without a log line, because the handler reads only the `object-fields` array. Source: `_addOrUpdateObjectFields` in `BundleSiteInitializer`.

### Add Validations

Expression and script validations are both available — no flag required. Consult learn.liferay.com for expression syntax (search `object validations expression builder`).

Verified expression gotchas: **`between()` throws at evaluation** — write the range as `field >= x AND field <= y` instead. `oldValue("<fieldName>")` and `currentDate` are available, which enables rules like "validate only when the field actually changed" — useful to spare already-migrated data from a new rule.

```bash
curl \
	--data '{
		"active": true,
		"engine": "ddm",
		"errorLabel": {"en_US": "<Error message>"},
		"name": {"en_US": "<Validation Name>"},
		"outputType": "fullValidation",
		"script": "<expression>"
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions/<definition-id>/object-validation-rules" \
	--user "test@liferay.com:test"
```

The expression engine is **`ddm`**, and `name` is a **locale map**. Verified on 2026.Q3.2 with `fitScore >= 0 AND fitScore <= 100 AND valueScore >= 0 AND valueScore <= 100`. A rejected write is a `400` with `type: ObjectValidationRuleEngineException`, no `title`, and the `errorLabel` inside the JSON-encoded `detail` string (`[{"errorMessage": "..."}]`). Code that shows `body.title` therefore shows nothing; parse `detail`.

Consult learn.liferay.com for expression builder syntax (search `object validations expression builder`).

### Publish the Object Definition

An unpublished object has no REST endpoint and no UI entry. Publish after adding fields and relationships; entries are then available at `/o/c/<pluralLabel>`.

```bash
curl \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions/<definition-id>/publish" \
	--user "test@liferay.com:test"
```

After publishing, object entries are available at `/o/c/<pluralLabel>`.

### Create and Query Object Entries

```bash
# Create entry

curl \
	--data '{"<fieldName>": "<value>"}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/c/<pluralLabel>" \
	--user "test@liferay.com:test"

# List entries

curl \
	--silent \
	--url "http://localhost:${PORT}/o/c/<pluralLabel>" \
	--user "test@liferay.com:test"
```

**A 409 "Conflict with <operation>" on `/o/c/<pluralLabel>` for a SITE-scoped object is normal, not breakage** — the unscoped endpoint is excluded for site-scoped objects by stock configuration. Use the scoped path instead: `/o/c/<pluralLabel>/scopes/{siteId}` (a site ERC also works as the scope key). Company-scoped objects use the bare path. The same applies to depot-scoped objects, whose scope key can be the asset library's group id or its name. Two params the **scoped** list endpoints reject: `aggregationTerms` → 404, and `filter=scopeGroupId eq ...` → 400 (scoping is the path segment, not a filter) — both return an error body with no `items`, so `(result.items || [])` silently renders empty. GET/PATCH **by id** need no scope segment.

**The entry `actions` map uses per-object action names.** A custom object's HATEOAS `actions` keys are generated from the object name (`get-<objectname>`, `replace-<objectname>`, …), not the generic `get`/`replace` — code that looks up a hardcoded action name works for one object and silently misses others. To derive an object's REST context path generically, iterate all `actions` values and match the href against `/o/c/([^/]+)`.

**A localized field ignores a plain PATCH — write the `_i18n` map.** For a field with `localized: true`, `PATCH {"<field>": "..."}` returns **200 and silently no-ops** (read-back unchanged). Write `{"<field>_i18n": {"en_US": "...", ...}}` covering every locale you carry. Verified on 2026.Q3.2 on fields named `title` and `summary` alike — it is not about the field name — while a full `PUT` with flat keys does apply. Check `localized` in the definition before scripting any entry-text migration. (Commerce product name/description are the opposite convention — a plain locale-map without the `_i18n` suffix.)

**A `PUT` on an existing entry is a full replace; a `PATCH` merges.** `PUT /o/c/<plural>/by-external-reference-code/{erc}` creates the entry when the ERC is unknown, with the caller as creator and owner. On an existing entry it **clears every field the body leaves out**: verified on 2026.Q3.5, where a PUT carrying only `coachTitle` reset a Boolean to `false` and a RichText to `""`. A `PATCH` by ERC changes only the fields it carries, and `null` clears a picklist (it reads back as `{"key": ""}`). Neither moves `creator`, even when an admin PATCHes another user's entry. So a seeding script creates with the PUT and updates with a PATCH of the fields that differ.

**REST cannot back-date an entry.** `dateCreated` and `dateModified` in a `PUT` or `POST` body are accepted with a `200` and ignored: both read the write's time, since `ObjectEntryLocalServiceImpl:492` stamps `new Date()`. The system `displayDate` is written, to the minute (`18:49:05.283` reads back `18:49:00`). So a migration keeps an old record's creator (write as that user) and ERC (`PUT` by ERC), and can carry its old time in `displayDate`, not in `dateCreated`. Verified on 2026.Q3.5.

#### Creating a Related Child Entry (Live API)

To create a child entry already linked to its parent over a `oneToMany` relationship, **POST the child directly to its own endpoint** and set the foreign key field in the body.

The FK field lives on the **child** but is named after the **parent**: `r_<relationshipName>_c_<parentObject>Id`, lowercase first letter, holding the parent's numeric entry ID. A relationship named `eventRegistrations` from `Event` to `Registration` puts `r_eventRegistrations_c_eventId` on `Registration` — `event`, not `registration`. Guessing this wrong is silent: the unknown key is ignored, the child saves unlinked, and the parent's nested list comes back empty.

When the parent is a **system object** the PK segment is unprefixed: a `oneToMany` from the OOTB `User` object produces `r_<relationshipName>_userId` (no `c_`). That User relationship is also the standard **reassignable owner** pattern — model "owned by" as a relationship to `User` rather than a plain userId Text field, so the UI gets a picker and the FK stays referentially valid. (There is no server-side role filter on `/o/headless-admin-user/v1.0/user-accounts`, so filter candidate assignees by `roleBriefs` client-side.)

**For a system parent, the ERC twin works on write, and an unknown ERC creates the parent.** On a child of `AccountEntry`, `{"r_<rel>_accountEntryERC": "<account ERC>"}` with no id links the entry, the same as the numeric `r_<rel>_accountEntryId` (verified on 2026.Q3.5). Both go through `RelationshipObjectFieldBusinessType._getPrimaryKeyObj` → `SystemObjectDefinitionManager.getOrAddEmptyBaseModel`, which for accounts is `AccountEntryService.getOrAddEmptyAccountEntry`. Two consequences:

- **An unknown or mistyped ERC creates an empty *business* account** in `STATUS_EMPTY`, named after the ERC, when the writer holds `ADD_ACCOUNT_ENTRY`, as an administrator does (`AccountEntryServiceImpl:237–256`, `AccountEntryLocalServiceImpl:486–503`). A writer without it gets a `403`. Look each account up with `GET /o/headless-admin-user/v1.0/accounts/by-external-reference-code/{erc}` before writing the twin, and stop on a `404`.
- **The writer must be able to VIEW the parent.** Otherwise the write is a bare `403`, by twin or by id. An account member can view their own account, and an org role such as Account Manager covers the org's accounts. So a user who is not yet a member of their own person account cannot link it. Verified on 2026.Q3.5, and again on 2026.Q2.7, where the check is `AccountEntryServiceImpl.fetchAccountEntryByExternalReferenceCode` inside `getOrAddEmptyAccountEntry`: an id is turned into the account's ERC and fetched through the checked service. **Only Object REST runs this resolution.** A module that adds or updates the entry through `ObjectEntryLocalService` never calls `RelationshipObjectFieldBusinessType`, so it can set the FK for a user, or a guest, who holds nothing on the account — which is how a guest's application lands restricted to a church's account, and how a member names their church when granting every user `VIEW` on every account is not wanted. Validate the id yourself on that path: the local service checks nothing about it either, a Person account and a deactivated one included.

```bash
curl \
	--data '{
		"<childField>": "<value>",
		"r_<relationshipName>_c_<parentObject>Id": <parent-entry-id>
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/c/<childPlural>" \
	--user "test@liferay.com:test"
```

Two paths that do **not** work the way the nesting suggests:

- **Nested create is rejected.** `POST /o/c/<parentPlural>/{parentId}/<relationshipName>` returns `400 UnsupportedOperationException` — there is no nested create endpoint. Use the direct child POST with the FK field above.
- **`PUT` only attaches an existing entry.** `PUT /o/c/<parentPlural>/{parentId}/<relationshipName>/{relatedId}` links an already created child to the parent; it does not create one.

Nested **read** does work, and is the cheapest way to confirm a link actually took:

```bash
curl \
	--silent \
	--url "http://localhost:${PORT}/o/c/<parentPlural>/<parentId>/<relationshipName>" \
	--user "test@liferay.com:test"
```

Always verify with this after creating children. A mistyped FK field name is accepted silently — `200`, entry created, FK left at `0` — so a successful POST is not evidence the child is attached. Assert on `totalCount`.

### Initialize in Bulk via a Batch Client Extension

Use a Batch Client Extension (CX) to initialize Object Definitions, Folders, and seed data at deploy time. Do **not** mix Batch CX with Custom Element CX in the same project or `client-extension.yaml`.

> **This applies to the `batch` CET type only — not to `siteInitializer`.** A site initializer does not read a `batch/` directory and silently ignores `*.batch-engine-data.json` files placed in its tree. To define objects inside a site initializer, use its own `object-definitions/`, `list-type-definitions/`, and `object-relationships/` directories, which take raw DTOs with no `configuration` envelope. See `rules/site-initializer-format.md` → "Objects and Picklists".

**Permissions are not importable via batch.** The Batch Engine does not apply object or entry permissions from the JSON payload — do not put a `permissions` block in a `*.batch-engine-data.json` file expecting it to take effect. Grant permissions after deploy via Control Panel → Objects → [Object] → Permissions (or the Headless permissions API; see "Permission Grants" above).

#### Project Structure

Files inside `batch` are processed alphabetically — use numeric prefixes to enforce dependency order:

```text
client-extensions/my-batch-init/
├── client-extension.yaml
├── bnd.bnd
└── batch/
    ├── 01-00-folder-definition.batch-engine-data.json
    ├── 01-01-object-definition.batch-engine-data.json
    ├── 02-00-relationship.batch-engine-data.json
    └── 03-00-entries.batch-engine-data.json
```

Prefix guide: `01-00` = Folders → `01-01` = Object Definitions → `02-xx` = Relationships → `03-xx` = Entries/Data.

#### `client-extension.yaml`

`assemble` is a **top level** key here, a sibling of the CET entry. Nested under the entry (the `customElement` habit), `assembleClientExtension` reports `NO-SOURCE`, `batch/` never reaches the zip, and `deploy` still reports `BUILD SUCCESSFUL` — `unzip -l` the deployed zip to check (verified 2026.Q3.2, and the shape used by `workspaces/liferay-ticket-workspace` in liferay-portal).

```yaml
assemble:
    - from: batch
      into: batch

my-batch-init:
    name: My Batch Initialization
    oAuthApplicationHeadlessServer: my-batch-oauth-server
    type: batch

my-batch-oauth-server:
    .serviceAddress: <host>:<port>
    .serviceScheme: http
    name: My Batch OAuth Server
    scopes:
        - Liferay.Headless.Batch.Engine.everything
        - Liferay.Object.Admin.REST.everything
    type: oAuthApplicationHeadlessServer
```

**Critical**: use `oAuthApplicationHeadlessServer` (not `oAuthApplicationUserAgent`) — the Batch Engine requires server to server OAuth, not a user delegated token.

#### Folder Definition (`01-00-...json`)

```json
{
	"configuration": {
		"className": "com.liferay.object.admin.rest.dto.v1_0.ObjectFolder",
		"parameters": {
			"createStrategy": "UPSERT",
			"updateStrategy": "UPDATE"
		}
	},
	"items": [
		{
			"externalReferenceCode": "MY_FOLDER_ERC",
			"label": {
				"en_US": "My Custom Folder"
			},
			"name": "MyFolder"
		}
	]
}
```

#### Object Definition (`01-01-...json`)

```json
{
	"configuration": {
		"className": "com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition",
		"parameters": {
			"createStrategy": "UPSERT",
			"updateStrategy": "UPDATE"
		}
	},
	"items": [
		{
			"enableCategorization": true,
			"externalReferenceCode": "MY_OBJECT_ERC",
			"label": {
				"en_US": "My Object"
			},
			"name": "MyObject",
			"objectFields": [
				{
					"businessType": "Text",
					"indexed": true,
					"indexedAsKeyword": true,
					"label": {
						"en_US": "My Field"
					},
					"name": "myField",
					"required": false
				}
			],
			"objectFolderExternalReferenceCode": "MY_FOLDER_ERC",
			"scope": "company",
			"status": {
				"code": 0,
				"label": "approved"
			}
		}
	]
}
```

`"status": {"code": 0}` is required for the object to be immediately active. Without it the definition deploys in draft state and returns no entries.

#### Data Entries (`03-00-...json`)

```json
{
	"configuration": {
		"className": "com.liferay.object.rest.dto.v1_0.ObjectEntry",
		"parameters": {
			"createStrategy": "UPSERT"
		},
		"taskItemDelegateName": "C_MyObject"
	},
	"items": [
		{
			"assetCategoryIds": [
				12345
			],
			"externalReferenceCode": "ENTRY-001",
			"properties": {
				"myField": "value",
				"timestamp": "2024-03-27T10:00:00Z"
			}
		}
	]
}
```

- `taskItemDelegateName` is a **sibling** of `parameters` inside `configuration`, not a member of it: `BatchEngineUnitConfiguration` reads it as its own field (`BatchEngineUnitConfiguration`). Nested in `parameters` it deserializes to `null`, and the engine then waits forever for a nameless delegate — no error, no timeout, and no later file in the sequence runs either (reproduced live on 2026.Q3.1).
- `taskItemDelegateName` is the delegate the object registers: **`C_<Name>` for a custom object** (`C_MyObject` for an object named `MyObject`), but the **bare name for a Liferay-shipped one** — the CMS's built-in structures register `CMSBlog`, not `C_CMSBlog` (verified on 2026.Q3.1 and q2.7). A wrong name blocks on "Waiting for a service matching …" and silently skips that file and every later file.
- **Custom fields go under `properties`** (the `ObjectEntry` DTO field; not `values`). **System fields go at the item's top level**, beside `externalReferenceCode`: `displayDate`, `friendlyUrlPath`, `keywords`, `status`, `objectEntryFolderExternalReferenceCode`. Inside `properties` they are dropped without an error: `displayDate` is lost and `friendlyUrlPath` is generated from the title (verified on 2026.Q3.1 and q2.7; re-checked with `displayDate`, `friendlyUrlPath` and `keywords` on 2026.Q2.7). `assetCategoryIds` also belongs **outside** `properties`.
- Dates must use ISO 8601 with UTC `Z` suffix.
- **A site-scoped object's entries take the site's ERC as `scopeKey`** (`GroupUtil.getGroupId` resolves an ERC, group key or id). The site must exist when the file imports, so a batch for site-scoped entries belongs after the site initializers that create the sites, not in a content batch that runs before them. Measured on 2026.Q2.7: 2,835 entries in 6 files in about 20 s.
- **A duplicate `friendlyUrlPath` is not an error.** The later entry gets `<path>-1`, so which entry keeps the clean URL depends on import order. Resolve collisions in the generator (verified on 2026.Q2.7).
- **A `]` inside a text value can cut the file short.** The engine cuts the `items` array out of the file with `AdvancedJSONReader._transferBlock`, which counts `[` and `]` byte by byte and does not skip JSON strings. A string whose brackets do not balance (a stray `]` in prose, `[PDF` with no close) ends the array early, or runs past its end. The engine logs `Unable to get total items count`, writes the items before the cut, and the task ends `FAILED` with `Unexpected end-of-input in VALUE_STRING`. Measured on 2026.Q2.7: a file of 2,320 entries failed after 1,800, one of 2,402 after 107, and small files passed by luck. Write `[`, `]`, `{` and `}` inside strings as `\u005b`, `\u005d`, `\u007b` and `\u007d`; that is valid JSON and Jackson reads the same text. Then check each file by running that byte counter over it before packaging.
- **Tags are validated.** A `keywords` value holding any of ``& ' @ \ ] } : , = > / < [ { % | + # ` ? " ; * ~`` or a line break (`AssetHelper.INVALID_CHARACTERS`), or longer than 75 characters, fails its entry with `AssetTagException` (a bare `500` over REST). In a batch file, that fails the file. Clean tag names in the generator, for example `#supportsmallbusiness` → `supportsmallbusiness` (verified on 2026.Q2.7).

#### Relationship Mapping

**Preferred (portable)** — use the relationship's camelCase name as the key:

```json
"relationshipName": {"externalReferenceCode": "TARGET-ERC-001"}
```

**Direct field mapping** (`r_...` syntax) — ERC is **not** supported here, only integer IDs:

```json
"r_accountToMyObject_accountEntryId": 38660
```

**A `manyToMany` list cannot link existing entries by ERC.** `"<relationshipName>": [{"externalReferenceCode": "<erc>"}]`, in `properties` or at the top level, makes the engine write each listed item as a **nested entry**, so a bare ERC fails on the related object's required fields (`No value was provided for required object field "title"`), and a full body would overwrite the related entry. The task ends `FAILED 0/1`, **yet the parent entry is left created**. Link after the import instead: `PUT /o/c/<parentPlural>/{id}/<relationshipName>/{relatedId}` (`200`). A later REST `PUT` of the parent keeps the links, but **a batch re-UPSERT of the parent clears every one of them**, silently (all 27 links of 14 events were gone after a redeploy, while their categories stayed), so the linking step runs after every deploy of the batch. **Link one entry at a time.** Parallel `PUT …/<relationshipName>/{relatedId}` calls failed at random with `400 "The service parameter was not provided by this object"` (`IllegalArgumentException`, nothing logged), and the same call succeeded when sent alone. Serial is fast enough: 4,181 links took 26 s over a signed-in session (2026.Q2.7). To read the current links of many entries at once, list the parent's scope with `nestedFields=<relationshipName>` and `fields=id,externalReferenceCode,<relationshipName>.externalReferenceCode`, one paged call per Space. Verified on 2026.Q2.7 between two depot-scoped CMS structures; the inline relationship itself (`objectRelationships` in the definition item, `objectDefinitionExternalReferenceCode2`, `type: manyToMany`) creates fine.

#### Troubleshooting

- **NPE on deploy**: missing `.serviceAddress` or `.serviceScheme` in the OAuth server entry.
- **Object not created**: `className` in the `configuration` block must exactly match the REST DTO for your Liferay version.
- **Folder not found**: the `externalReferenceCode` in `objectFolderExternalReferenceCode` must match exactly — Batch Engine processes files alphabetically, so folders must have a lower prefix than the objects that reference them.

## Patterns and Gotchas

### OData Relationship Filters Use ERC Strings, Not Numeric IDs

An **unquoted** numeric id throws `HTTP 400 InvalidFilterException: Incompatible types` — the FK columns are string typed. Quote it (`r_<relationshipName>_c_<objectName>Id eq '123'`), or filter by the related entry's ERC, which is portable across environments:

```text
# Single value

r_<relationshipName>_c_<objectName>ERC eq 'ERC_VALUE'

# Multivalue

r_<relationshipName>_c_<objectName>ERC in ('ERC1','ERC2')
```

Applies to both `eq` and `in`. Both the quoted id and the ERC twin were verified on 2026.Q3.2; the `…ERC` field does not appear in `object-fields`, but it is in the entry payload and it filters.

### OData Date and DateTime Filters — Use a Full UTC Instant

An earlier report had every `Date`/`DateTime` OData filter (`eq`, `ge`, `le`) returning BAD_REQUEST, whatever the value's format. **On 2026.Q2.7 a range filter on custom `DateTime` fields works** when the value is a full ISO-8601 UTC instant: `startDate lt 2026-11-01T00:00:00.000Z and endDate ge 2026-10-01T00:00:00.000Z` with `sort=startDate:asc` returned the right 34 of 2,402 entries, for Guest too (verified, a CMS structure through `/o/c/<plural>/scopes/<site>`). `eq` and other value formats were not re-tested, so test your exact filter before you depend on it, and fall back to filtering client side if it answers `400`. A filter or sort that names a field the object does not have answers `400` (`"A property used in the filter criteria is not supported"`, `"Unable to sort by property"`).

### OData Filters on a Picklist: No `/key` Path on 2026.Q2.7

`filter=exceptionStatus/key ne 'cancelled'` answers `400 InvalidFilterException` ("Expected token 'QualifiedName' not found") on 2026.Q2.7, so a widget built with that path shows nothing. Compare the field itself, `exceptionStatus ne 'cancelled'` (an empty picklist was kept by `ne` in the one case measured), or drop the clause and filter on `keyOf(field) !== '<key>'` in the client, which reads the same on every release.

### Picklist Fields Return `{key, name}` Objects — Not Strings

Picklist values in API responses are objects:

```json
{
	"key": "DRAFT",
	"name": "Draft"
}
```

Always destructure before use: `const key = entry.registrationStatus?.key || ''`. Rendering the object directly outputs `[object Object]`.

This holds for custom objects over `/o/c`. A picklist field **added to a system object** is served as a bare string key by that object's own module (see "Extending a System Object").

Do **not** reach for `entry.status` here. Every object has a *system* `status` field — the workflow status — and it has a different shape, so `entry.status?.key` is always `undefined`:

```json
{
	"code": 0,
	"label": "approved",
	"label_i18n": "Approved"
}
```

`status` is also a reserved name, so a custom picklist can never be called `status` (see "Reserved Field Names").

### Schema Discovery Before Write Operations

The OpenAPI spec for `object-admin` and the per object `/o/c/<pluralLabel>` endpoints is the source of truth — Liferay's hosted documentation lags. Before any POST or PATCH:

- Fetch the relevant OpenAPI spec via the `get-openapi` MCP tool, or via `/o/object-admin/v1.0/openapi.yaml`.
- GET response structure does NOT equal POST/PATCH request structure — do not infer the write shape from a read response.
- For settings whose `value` resolves to a generic string (`fileSource`, `acceptedFileExtensions`, etc.), see "Field Settings Gotchas" below — those values are not in the OpenAPI surface at all.

### Field Rules

- **Namespace safety**: NEVER use `userId` as a custom field name — it is a system column in `ObjectEntryTable` and will collide. Use `liferayUserId` instead.
- **Type storage**: every `DateTime` field MUST have `timeStorage` set in `objectFieldSettings` (e.g., `"convertToUTC"`); it is required and cannot be changed later. A `Date` field takes **no** `timeStorage`: it allows only `defaultValue` and `defaultValueType`, and any other setting is rejected with `ObjectFieldSettingNameException.NotAllowedNames`, so a `Date` field with no settings at all is correct. Source (2026.Q3.5): `DateTimeObjectFieldBusinessType:213` (`getRequiredObjectFieldSettingsNames` → `timeStorage`), `DateObjectFieldBusinessType:51` (`getAllowedObjectFieldSettingsNames` → `defaultValue`, `defaultValueType`), and the not-allowed check in `ObjectFieldBusinessType.validateObjectFieldSettings`.
- **Indexed language**: `indexedLanguageId` is valid only on `String` and `Clob` field types. Never set it on `Date`/`DateTime` or other nontext fields.
  - **On create, a field that sets `indexedLanguageId` must also send `DBType`** (`"String"` for `Text` and `Picklist`, `"Clob"` for `RichText`). Otherwise the create fails with `ObjectFieldDBTypeException: Indexed language ID can only be applied with type "Clob" or "String" that is not indexed as a keyword`, although the business type is a text type. `_addObjectField` runs `_validateIndexed` on the DTO's `DBType`, which is null when omitted, **before** `_setBusinessTypeAndDBType` derives it from the business type. `Attachment` is the one exemption. Inside a site initializer this rolls back the whole run. So a definition file built from a read-back definition must keep `DBType` wherever it keeps `indexedLanguageId`, or drop both. Source: `ObjectFieldLocalServiceImpl._addObjectField` and `_validateIndexed`. Reproduced on 2026.Q3.5 with a definition file whose `Text` field carried `indexedLanguageId: en_US` and no `DBType`.
- **Facetable/filterable fields**: set `"indexed": true, "indexedAsKeyword": true` on fields that search facets or exact-match filters will target — without the keyword flag the field is analyzed text and exact filtering misbehaves.

#### Reserved Field Names

Liferay creates these as system fields on every object, so a custom field cannot claim the name. Rejected with `ObjectFieldNameException$MustNotBeReserved`, compared lowercased — `Status` fails exactly as `status` does. Check a new definition's field names against this list before deploying:

```
actions, companyid, createdate, creator, currentdate, datecreated, datemodified,
displaydate, expirationdate, externalreferencecode, groupid, id, keywords,
lastpublishdate, modifieddate, reviewdate, status, statusbyuserid,
statusbyusername, statusdate, taxonomycategoryids, userid, username
```

Also enforced: letters and digits only, must begin with a lowercase letter, under 41 characters, and must not equal the primary key field name.

The match is **exact**, not a prefix check, so near misses are safe: `company` is accepted even though `companyid` is reserved, and `name`, `email`, and `location` are all fine. Do not rename a field defensively just because it resembles an entry on the list — check it against the list literally.

**`status` is the trap** — the obvious name for any workflow like model, and always taken. Use `<entity>Status` (`registrationStatus`, `orderStatus`) with `"label": {"en_US": "Status"}` so the UI still reads "Status".

Inside a `siteInitializer` this failure is disproportionate: the exception aborts initialization and **rolls back the whole site creation transaction**, leaving no site, no objects, and no picklists. It looks like the CET never deployed. Check for `MustNotBeReserved` in the log before assuming that.

A reserved name **may** legitimately appear in an initializer object-definition file — but only with `"system": true`, which tells Liferay to skip re-adding the system field. At `"system": false` it routes through `addCustomObjectField` and throws. Because add-or-update skips creation when the object already exists, a `system: false` reserved field **hides on a populated instance and only detonates on the next fresh-DB rebuild**.

Source: `_reservedNames` in `modules/apps/object/object-service/src/main/java/com/liferay/object/service/impl/ObjectFieldLocalServiceImpl.java`.

#### A Published Field Cannot Be Renamed

`PUT /o/object-admin/v1.0/object-fields/{id}` with a new `name` on a published definition answers `200` and **keeps the old name**, with no error (verified on 2026.Q2.7 with a throwaway object). To move to another name, add the new field, rewrite the entries, then delete the old field and its data. Every query, sort, Blueprint and mapping that names the old field must change in the same step.

#### Read-Only Fields: Frozen Once Set

Measured on 2026.Q2.7 and 2026.Q3.5:

- **`readOnly: "true"` refuses a value on create** (POST, with or without an ERC; only a `PUT …/by-external-reference-code/{new}` slips one in). For a field set once by its writer, use `"readOnly": "conditional"` with `"readOnlyConditionExpression": "NOT(isEmpty(<field>))"`: it takes the value on create and refuses a change or a blank afterwards, **administrators included**.
- **The condition is evaluated over the stored values**, so it can name another field: a message's `messageSequence` (an Integer whose first value is `0`) is frozen with `<fk> > 0` on its relationship FK. An FK freezes itself the same way (`r_<rel>_c_<parent>Id > 0`).
- **On create there are no stored values, so a field frozen on another field's condition is open to whoever creates the row.** An FK frozen with `ownerUserId > 0` took any value on an administrator's `POST`, while every later change, the administrator's included, answered `400` (2026.Q2.7, UA's object access step 4a). Only the holders of `ADD_OBJECT_ENTRY` choose it, so keep that grant to the writer that sets the field (a service account) and accept that administrators hold it too.
- **A frozen Picklist answers `500`, not `400`, to a PATCH that carries it as a bare key** (`"reason": "other"`, or the same key resent unchanged). `ObjectFieldUtil._validateNewValue` parses a read-only Picklist's new value as `{"key": …}`, so a bare key throws `JSONException` (`A JSONObject text must begin with '{'`). The freeze still holds: nothing changes. A LongText freeze answers `400` as the others do. Never resend a frozen picklist in a PATCH (measured on 2026.Q2.7 for a service user and an administrator, UA's object access step 4b).
- **A field cannot be both `required` and read only.** The create fails with `ObjectFieldReadOnlyException: Required object field cannot be read only`, which inside a site initializer rolls back the whole run. Leave frozen fields not required, and have the writer set them.
- **A read-only FK is set in `object-fields/`**, after the relationship that creates it, and that entry must resend the FK's relationship settings, or the run fails with `ObjectFieldSettingValueException$MissingRequiredValues`: `"objectFieldSettings": [{"name": "objectDefinition1ShortName", "value": "<Parent>"}, {"name": "objectRelationshipERCObjectFieldName", "value": "r_<rel>_c_<parent>ERC"}]`, beside `"businessType": "Relationship"`, `"DBType": "Long"`, the `readOnly` pair and `"required": false`.
- **A `PUT` that omits a read-only field blanks it** (stock `object.rest.impl`), after which a conditional field can be set again. Write frozen rows with `PATCH` only, and grant no one UPDATE who should not rewrite them. Fixed locally in `com.liferay.object.rest.impl` 1.0.188 (UA's `jira/object-rest-put-blanks-read-only-fields.md`).
- A child's FK can be set on create through its **ERC twin** (`r_<rel>_c_<parent>ERC`), so a writer that knows only the parent's ERC needs no id lookup; a retried `PUT` by the child's ERC with the same body is accepted.

### Field Settings Gotchas

`objectFieldSettings` entries that use generic string `value` fields (e.g., `fileSource`, `acceptedFileExtensions`, `maximumFileSize`) are **not documented as enums in the OpenAPI schema** and are not discoverable via GraphQL introspection — the `value` field resolves as a generic `Object` scalar. Guessing common values will produce `400 Bad Request` with no enum hint in the response.

When a `400 Bad Request` is returned for an unknown setting value, search the [Liferay Portal GitHub repository](https://github.com/liferay/liferay-portal) for the relevant constants or validation logic rather than guessing. Do not attempt further guesses without a source verified value.

#### Attachment `fileSource` — Verified Matrix (2026.Q2.7)

The four values (`ObjectFieldSettingConstants`) are `documentsAndMedia`, `userComputerToDocumentsAndMedia`, `CMSBasicDocument`, `userComputerToCMSBasicDocument` — and the right one depends on the object's **scope** and **who uploads**:

| Object scope | Regular users upload files | Notes |
| --- | --- | --- |
| company / site | `userComputerToDocumentsAndMedia` (+ `showFilesInLibrary: false`) | `documentsAndMedia` means "pick an EXISTING D&M file" → needs D&M repo permissions a plain account member lacks → **403 on the attachment write only** (the base entry write still returns 200, so text-only submits work and mask the bug) |
| depot | `CMSBasicDocument` or `userComputerToCMSBasicDocument` | `_validateGroup` rejects the D&M sources on a depot group → every write fails a bare `400 "The value is invalid for object field"` with no stack trace |

On 2026.Q3.5 `userComputerToCMSBasicDocument` with `showFilesInLibrary: false` also works on a **company-scoped** object: a Form Container upload is stored hidden, owned by the entry, in a repository of the form page's site (`manage-form-containers` → the upload note under the field table).

Write payload for an attachment field over `/o/c`: `{"<field>": {"fileBase64": "<b64>", "name": "x.png"}}` (or `{"externalReferenceCode": "<doc-erc>"}` / `{"fileURL": "..."}`). A plain id or `{"id": n}` is rejected. `showFilesInLibrary: true` + `storageDLFolderPath` files the uploads in a named library folder but **NULLs `file.fileURL`** (and `previewURL` 404s) — keep it off if a client-side renderer reads `fileURL`. A running field's `fileSource` can be fixed live via `PUT /o/object-admin/v1.0/object-fields/{fieldId}` with the full field body — no restart.

**Linking an existing D&M file by ERC (`documentsAndMedia`) needs its site, and a miss creates a file.** Verified on 2026.Q3.5 with a company-scoped object and a file in the Guest site:

- **Name the file's site:** `{"<field>": {"externalReferenceCode": "<file ERC>", "scope": {"externalReferenceCode": "<site ERC>"}}}`. Without `scope`, the ERC is looked up in the object's own default group, not the file's site (`DefaultObjectEntryManagerImpl._fetchServiceBuilderFileEntry` → `_getFileEntryGroupId`), and the bare form answered `400 "File name is null"`.
- **A miss falls through to an upload.** When the lookup finds nothing, the write goes on to `_attachmentManager.getOrAddFileEntry` with the same ERC (`DefaultObjectEntryManagerImpl`, about `:3196–3320`). With a `name` in the body, that creates an empty file under that ERC rather than failing. So check that the file exists first (`GET /o/headless-delivery/v1.0/sites/{siteId}/documents/by-external-reference-code/{erc}`), and never send `name` when you mean to link a file.
- **The writer must be able to VIEW the file.** Otherwise the write is a bare `403`. Link as an administrator if the entry's author cannot see the file; a PATCH keeps the entry's creator.
- The first such write in a site makes the portal create a hidden `TempFileEntryUtil` repository and folder there, with three permission rows. That is portal infrastructure, not something your write left behind.

**Linking a CMS file (`CMSBasicDocument`) works from a company-scoped object too.** Verified on 2026.Q3.5, with the file of a Basic Document or of a custom file type in a Space:

- **The same body**, with the Space's ERC as `scope`: `{"<field>": {"externalReferenceCode": "<file ERC>", "scope": {"externalReferenceCode": "<space ERC>"}}}`. It links the existing file and reads back with `scope.type: AssetLibrary`.
- **The writer must VIEW the CMS entry that holds the file**, or the write is a `403`. A Guest grant on that entry counts for every signed-in user.
- **The group decides which source is valid**, on every create *and* update (`ObjectEntryLocalServiceImpl._validateGroup`, `:7609–7631`):
  - `CMSBasicDocument` requires the file's group to be a **depot**, a Space or a classic asset library;
  - `documentsAndMedia` requires it **not** to be. So `documentsAndMedia` cannot link a file in a classic library: `400 "The value is invalid for object field"`.
- **Changing a live field's `fileSource`** with `PUT /object-fields/{id}` keeps existing links on read. But **any later PATCH of an entry whose link no longer fits the new source fails with that `400`, even a PATCH of another field.** Clear or re-link those entries first.

**A usable download link needs two grants.** `file.link.href` is set only when the reader has the file's `DOWNLOAD` **and** the entry's per-field action `DOWNLOAD_<FIELD>` (`ObjectFieldUtil:231–236`). The action is named from the field: `documentCall1` gives `DOWNLOAD_DOCUMENT_CALL1`. Owner holds it per entry by default; any other role needs an explicit grant (`resource-permissions.json`). Granting both actions of an object with two Attachment fields at company scope (`"scope": "1"`, `[$OBJECT_DEFINITION_CLASS_NAME:<Name>$]`) to a role works: a member of that role who can download the linked file then gets a working `href` (verified on 2026.Q3.5). The grant only shows the link: the download itself is still checked against the entry that owns the file (`ObjectDLFileEntryModelResourcePermissionConfigurator._hasDownloadPermission`). Without it the `href` is `""`. Without the file's permission it is `/c/portal/login`. The `doc` metadata (name, size, id) is returned either way. 2025.Q3 had no `DOWNLOAD_<FIELD>` actions, so a migrated object can lose links it used to have.

**Deleting a linked entry or field never deletes a linked CMS file.** Verified on 2026.Q3.5 with rows linking a Transcript entry's file and a Basic Document's PDF:

- Deleting the entry, or its parent through a `cascade` relationship, removes only DL files owned by that entry (`ObjectEntryLocalServiceImpl:905`). `_deleteFileEntries` acts on `userComputerToDocumentsAndMedia` alone (`:3635`).
- Deleting a populated `CMSBasicDocument` field (`DELETE /o/object-admin/v1.0/object-fields/{id}`, `204`) drops its column, removes its `DOWNLOAD_<FIELD>` resource action and that action's bit from every grant, and leaves the files.
- Only a `userComputerToDocumentsAndMedia` field's deletion deletes its files (`ObjectFieldLocalServiceImpl:1333–1356`).
- Linking a CMS file copies nothing: `dlfileentry` keeps its count and the file's row is unchanged.

**An attachment's download-action labels are shared by every object with a same-named field.** Publishing an object writes language overrides (`ploentry`, one row per locale) under the key `action.DOWNLOAD_<FIELD>`, with no object in the key. Deleting an Attachment field deletes all rows under that key (`ObjectFieldLocalServiceImpl:1319`). So does deleting the object (`deleteObjectFieldByObjectDefinitionId`, `:390`). A throwaway object with a `documentCall1` field therefore takes the real object's "Download Document/Call #1" labels with it. Give probe fields unique names. To restore deleted labels, post each row back with `POST /o/language/v1.0/messages` `{"key", "languageId", "value"}` as an administrator, which calls `PLOEntryService.addOrUpdatePLOEntry`. Read the rows from `ploentry` before deleting. Verified on 2026.Q3.5: 13 rows restored with the same key, language, value and user.

**Deleting an object definition leaves two things behind** (2026.Q3.5):

- its REST app's `VulcanCompanyConfiguration` (`path="/c/<plural>"`). Remove it in the Gogo shell (`telnet localhost 11311` in the container) with `(cm:getConfiguration "<pid>" "?") delete`; `listConfigurations "(service.pid=<pid>)"` shows it first.
- its `portlet` table row, `…ObjectDefinitionsPortlet_<code>`, for which there is no API.

#### Future `displayDate` Hides the Entry

Object entries carry a system `displayDate` (publication date). A value in the **future relative to the server clock** marks the entry scheduled/unpublished — **hidden from everyone but the owner and administrators**, returning 404 even to users holding an explicit Sharing VIEW grant. Seeded data anchored to a "demo day" ahead of the real server clock silently vanishes for non-owners. Seed `displayDate` safely in the past relative to the SERVER date, or omit it (null = always visible).

#### Depot-Scoped Objects Reject Writes Outside Their Accepted Depots

A depot-scoped object's `/o/c/<plural>/scopes/{assetLibraryKey}` accepts GET but returns a bare, unlogged 404 on POST/PUT until the definition accepts that depot — `_validateGroupId` only runs on writes. The two settings are `acceptedGroupExternalReferenceCodes` (a comma-separated list of depot ERCs; a Site ERC returns `400`) and `acceptAllGroups: true`, which accepts every depot. With neither, every scoped write returns `404` (verified 2026.Q3.2). PATCHes live onto a published definition, effective immediately. **Deleting the last depot in the list silently switches the definition to `acceptAllGroups: true`** — re-restrict it afterwards.

#### Account Restriction: Each Entry for Its Account's Members (Verified on 2026.Q3.5)

Measured on throwaway copies of a company-scoped object with a `oneToMany` from `AccountEntry` to it (person accounts, one member each):

- **A re-fire turns it on for a published object that holds entries.** Put `"accountEntryRestricted": true` and `"accountEntryRestrictedObjectFieldName": "r_<rel>_accountEntryId"` in the definition file; the initializer's PATCH applies it, with no warning. **It cannot be turned off again while published** (`ObjectDefinitionLocalServiceImpl:3127–3151`, by source).
- **A company-scope role grant bypasses it.** `ObjectEntryModelResourcePermission` returns true on a role grant before it looks at the restriction (`:183–192`), and the list and search filters drop the restriction for a holder of company `VIEW`. So a role meant to see only its own account's entries must hold **no** company-scope `VIEW`. `resource-permissions.json` can narrow such a row but never delete it (`rules/site-initializer-format.md`).
- **Membership alone gives `VIEW`**: the list, by id, the relationship endpoint, display pages and search. `UPDATE` or `DELETE` need an account role grant: `{"actionIds": ["UPDATE"], "primKey": "0", "resourceName": "[$OBJECT_DEFINITION_CLASS_NAME:<Name>$]", "roleName": "Account Member", "scope": "3"}` let each member update their own account's entries, and no others.
- **An entry with no account is open to every signed-in user.** By id it was readable by anyone signed in, a user with no role at all included (not listed), and updatable by anyone who could see its related parent (`:224–226` by source). Enabling restriction does **not** make the field required (it read back `required: false`). Make it required in the relationship file's `objectField` (`"required": true`): a create without the account then answers `400`, and a PATCH of an existing entry without one is refused unless it sets it. A re-fire sets `required` on an existing field too, and so does `PUT /object-fields/{id}`.
- **A relationship and a restriction on it cannot arrive in one fire on an existing object.** The definition update runs before the relationships handler, so the field does not exist yet: `ObjectDefinitionAccountEntryRestrictedObjectFieldIdException`, and the whole run rolls back. Two fires (the relationship, then the restriction) work. A fresh build takes both in one fire.
- **Search:** entries indexed before the restriction are not found by members until reindexed (an update reindexes one). Before that, members find nothing, which is the safe direction.
- **A child object is not covered by its parent's restriction.** Give it an account relationship of its own, filled from the parent. A form on a display page can pass it as a URL parameter to a hidden relationship input, from `INFO_ITEM.getValues()["r_<rel>_accountEntryId"]`. The alternative, an edge relationship, has costs; see the next section.

#### Edge Relationships Make Children Unreachable by Id (Verified on 2026.Q3.5)

Measured on throwaway objects. Setting `"edge": true` in the relationship file re-fired fine on a live `oneToMany` with children, re-rooting them, and the children then inherited the parent's `VIEW`. But then, **for the administrator too**:

- the child's plain list (`/o/c/<childPlural>`) is empty;
- GET, PATCH and DELETE by id or by ERC answer `404`;
- a POST with the FK field **silently stores FK 0**, an orphan row (`200`); only the nested `POST /o/c/<parentPlural>/{id}/<relationship>` creates a linked child.

So every client that reads, edits or deletes a child by id or ERC breaks, and so does a Form Container that sets the FK. A holder of `UPDATE` on the parent may also add children. Edge could be turned off once the relationship had no entries (`PUT /o/object-admin/v1.0/object-relationships/{id}` with `"edge": false`, `200`). A definition with an edge relationship cannot be deleted until then ("To delete this object, you must first disable inheritance and delete its relationships.").

### Permission Grants — Always Verify via Follow Up GET

After granting permissions via the Headless API, always verify with a follow up GET. Object permission APIs may return `200 OK` without persisting the change. If the follow up GET does not reflect the grant, use the Admin UI (Control Panel → Objects → [Object] → Permissions) as the reliable fallback.

## Success Signal

```bash
# List all published definitions

curl \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions?filter=status%20eq%20%27approved%27" \
	--user "test@liferay.com:test" \
	| jq '[.items[] | {id, name, status}]'

# On instances with many/complex definitions this listing has been observed to 500 on
# serialization for some pages — restrictFields=... (or fields=id,name) narrows the
# payload past the failure and keeps pagination working.
```

Confirm the definition name appears and `status` is `approved`.