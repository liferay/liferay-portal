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
| `LPD-17564` | off | Object collaborators API (per entry permissions) |
| `LPD-52006` | off | Object entry folders (requires `LPD-17564`) |

Object definitions, fields, relationships, and validations need no flag. On a site built from a site initializer, object definitions and data apply **live** via these APIs (and batch import) with no reprovision, and — being company scoped — survive a page reprovision (see `rules/site-initializer-format.md`).

## Workflow

### Collect Object Definition Inputs

Gather from the user or infer from context:

- `name` — singular CamelCase label (e.g. `Book`)
- `label` — human readable singular (e.g. `Book`)
- `pluralLabel` — REST path safe plural (e.g. `books`)
- `scope` — `company` (default, global) or `site`
- `storageType` — where entries are stored: Liferay's own DB (the default) or an external source such as `salesforce` or `ext-Service` (see `integrate-external-data`). Do **not** send this on the create call for default DB storage — omit it and Liferay assigns the default (see **Create the Object Definition**).
- Fields list — each with `businessType`, `name`, `label`, `required`

### Create the Object Definition

```bash
curl \
	--data '{
		"label": {"en_US": "<Label>"},
		"name": "<Name>",
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

> **A public form cannot fetch this picklist.** Guest gets 403 from the list type REST endpoint, so a `<select>` populated by fetch renders empty and the entry saves blank looking like success. Ship the options in the fragment markup. See `rules/guest-access.md`.

Writing a picklist value accepts **either** the object form `{"key": "vegan"}` or the bare string `"vegan"`. Both persist identically and both read back as `{key, name}`. An unknown key is rejected with `400 Object field name "<field>" is not mapped to a valid list type entry`.

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

A plain (non `state`) Picklist needs none of this.

### Add Relationships

Relationships are defined on the parent object. The `objectDefinitionId2` is the child definition's ID.

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

Relationship `type` values: `oneToMany`, `manyToMany`, `oneToOne`. Add `"deletionType"` — `cascade`, `disassociate`, or `prevent` — to say what happens to children when the parent is deleted.

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

`function` accepts `COUNT`, `SUM`, `AVERAGE`, `MIN`, `MAX` (`ObjectFieldSettingConstants`); anything but `COUNT` also needs the child field to aggregate. Liferay adds a third `filters` setting itself.

Three behaviors that decide whether you can use it:

- **It ignores entry level permissions.** A Guest who cannot read a single child row still receives the correct count on the parent — verified with an unauthenticated `GET`. This is what lets a private, write only submissions object feed a public number, and it is a genuinely better option than the object action counter that `manage-pages` and `rules/guest-access.md` describe: nothing to maintain, nothing to drift, no scripting to enable.
- **It serializes as a string.** `"registrationCount": "2"`, not `2`. Parse before arithmetic — `attendeeCapacity - registrationCount` in JavaScript silently concatenates, and in `jq` it throws `number and string cannot be subtracted`.
- **It is computed, so it is read only.** Do not send it on a POST or PATCH.

Whether an Aggregation field can be *mapped* into a Collection fragment is a separate question — `manage-pages` → "Mapping Limits" says aggregates cannot be mapped, and that was not retested here. Reading it over REST from fragment JavaScript is verified.

### Add Validations

Expression and script validations are both available — no flag required. Consult learn.liferay.com for expression syntax (search `object validations expression builder`).

```bash
curl \
	--data '{
		"active": true,
		"engine": "function",
		"errorLabel": {"en_US": "<Error message>"},
		"name": "<validationName>",
		"script": "<expression>"
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions/<definition-id>/object-validation-rules" \
	--user "test@liferay.com:test"
```

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

#### Creating a Related Child Entry (Live API)

To create a child entry already linked to its parent over a `oneToMany` relationship, **POST the child directly to its own endpoint** and set the foreign key field in the body.

The FK field lives on the **child** but is named after the **parent**: `r_<relationshipName>_c_<parentObject>Id`, lowercase first letter, holding the parent's numeric entry ID. A relationship named `eventRegistrations` from `Event` to `Registration` puts `r_eventRegistrations_c_eventId` on `Registration` — `event`, not `registration`. Guessing this wrong is silent: the unknown key is ignored, the child saves unlinked, and the parent's nested list comes back empty.

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
			"createStrategy": "UPSERT",
			"taskItemDelegateName": "C_MyObject"
		}
	},
	"items": [
		{
			"assetCategoryIds": [
				12345
			],
			"externalReferenceCode": "ENTRY-001",
			"values": {
				"myField": "value",
				"timestamp": "2024-03-27T10:00:00Z"
			}
		}
	]
}
```

- `taskItemDelegateName` must match the Object's **name** with a `C_` prefix (e.g., `C_MyObject` for an object named `MyObject`).
- `assetCategoryIds` belongs **outside** the `values` block.
- Dates must use ISO 8601 with UTC `Z` suffix.

#### Relationship Mapping

**Preferred (portable)** — use the relationship's camelCase name as the key:

```json
"relationshipName": {"externalReferenceCode": "TARGET-ERC-001"}
```

**Direct field mapping** (`r_...` syntax) — ERC is **not** supported here, only integer IDs:

```json
"r_accountToMyObject_accountEntryId": 38660
```

#### Troubleshooting

- **NPE on deploy**: missing `.serviceAddress` or `.serviceScheme` in the OAuth server entry.
- **Object not created**: `className` in the `configuration` block must exactly match the REST DTO for your Liferay version.
- **Folder not found**: the `externalReferenceCode` in `objectFolderExternalReferenceCode` must match exactly — Batch Engine processes files alphabetically, so folders must have a lower prefix than the objects that reference them.

## Patterns and Gotchas

### OData Relationship Filters Use ERC Strings, Not Numeric IDs

Filtering relationship fields by numeric ID throws `HTTP 400 InvalidFilterException: Incompatible types`. Always filter by the string ERC of the related entry instead:

```text
# Single value

r_<relationshipName>_c_<objectName>ERC eq 'ERC_VALUE'

# Multivalue

r_<relationshipName>_c_<objectName>ERC in ('ERC1','ERC2')
```

Applies to both `eq` and `in`. Numeric ID filters are broken for all relationship fields regardless of syntax.

### OData Date and DateTime Filters Are Broken — Filter Client Side

Filtering `Date` or `DateTime` fields via OData (`eq`, `ge`, `le`) consistently returns BAD_REQUEST regardless of value format. Fetch all records without a date filter and apply date logic in client code instead.

### Picklist Fields Return `{key, name}` Objects — Not Strings

Picklist values in API responses are objects:

```json
{
	"key": "DRAFT",
	"name": "Draft"
}
```

Always destructure before use: `const key = entry.registrationStatus?.key || ''`. Rendering the object directly outputs `[object Object]`.

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
- **Type storage**: every `DateTime` or `Date` field MUST have `timeStorage` set in `objectFieldSettings` (e.g., `"convertToUTC"`).
- **Indexed language**: `indexedLanguageId` is valid only on `String` and `Clob` field types. Never set it on `Date`/`DateTime` or other nontext fields.

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

Source: `_reservedNames` in `modules/apps/object/object-service/src/main/java/com/liferay/object/service/impl/ObjectFieldLocalServiceImpl.java`.

### Field Settings Gotchas

`objectFieldSettings` entries that use generic string `value` fields (e.g., `fileSource`, `acceptedFileExtensions`, `maximumFileSize`) are **not documented as enums in the OpenAPI schema** and are not discoverable via GraphQL introspection — the `value` field resolves as a generic `Object` scalar. Guessing common values will produce `400 Bad Request` with no enum hint in the response.

When a `400 Bad Request` is returned for an unknown setting value, search the [Liferay Portal GitHub repository](https://github.com/liferay/liferay-portal) for the relevant constants or validation logic rather than guessing. Do not attempt further guesses without a source verified value.

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
```

Confirm the definition name appears and `status` is `approved`.