---

description: Create, update, and publish Liferay Object definitions — fields, relationships, picklists, and validations. Use when the user asks to create an object, add a field, define a picklist, add a relationship, or set up an object validation. Maps to "Mastering Data Modeling with Liferay Objects".
name: manage-objects

---

# Manage Objects

CRUD for Liferay Object definitions and their child resources via the Headless Admin Object APIs.

When iterating on a site built from a site initializer, object definitions and data apply **live** via these APIs (and batch import) — no site reprovision needed. Object definitions and entries are company scoped, so they also survive a page reprovision (see `rules/site-initializer-format.md`).

## When to Invoke

- "Create an object", "define a data model", "make a custom entity"
- "Add a field", "add a relationship", "add a picklist"
- "Set up a validation", "publish the object"
- Called by `build-site` during the data model phase

## Prerequisites

Probe the following flags via `feature-flags` before any API call. Record the result for the session — do not reprobe on every call.

| Flag | Default | Required For |
| --- | --- | --- |
| `LPD-17564` | off | Object collaborators API (per entry permissions) |
| `LPD-52006` | off | Object entry folders (requires `LPD-17564`) |

Skip flags the user's workflow does not need. Do not enable flags without explicit user confirmation.

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

Do **not** send `"storageType": "default"` on the create call — it returns `400 ObjectDefinitionStorageTypeException`. Omit `storageType` entirely and Liferay assigns the default DB storage. (For external storage, see `integrate-external-data`.)

Save the returned `id` as `<definition-id>`.

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

`"required"` is **mandatory** on the standalone `POST .../object-fields` call — omitting it returns `500` with a `getRequired()` NullPointerException. Always include `"required": true` or `"required": false` explicitly.

Common `businessType` values: `Text`, `LongText`, `Integer`, `Decimal`, `Boolean`, `Date`, `DateTime`, `Attachment`, `Relationship`, `Picklist`.

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

Save the returned `id` as `<list-type-id>`. Then add a `Picklist` field referencing `"listTypeDefinitionId": <list-type-id>`.

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

Relationship `type` values: `oneToMany`, `manyToMany`, `oneToOne`.

### Add Validations

Expression builder and script based validations are both available — no feature flag required.

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

An unpublished object has no REST endpoint and no UI entry. Always publish after adding fields and relationships.

```bash
curl \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions/<definition-id>/publish" \
	--user "test@liferay.com:test"
```

After publishing, object entries are available at `/o/c/<pluralLabel>`.

### Verify

```bash
# List all published definitions

curl \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions?filter=status%20eq%20%27approved%27" \
	--user "test@liferay.com:test" \
	| jq '[.items[] | {id, name, status}]'
```

Confirm the definition name appears and `status` is `approved`.

## Object Entry CRUD (After Publishing)

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

### Creating a Related Child Entry (Live API)

To create a child entry already linked to its parent over a `oneToMany` relationship, **POST the child directly to its own endpoint** and set the foreign key field in the body. The FK field name is `r_<relationshipName>_c_<childObject>Id` (the related parent's numeric entry ID):

```bash
curl \
	--data '{
		"<childField>": "<value>",
		"r_<relationshipName>_c_<childObject>Id": <parent-entry-id>
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

## Querying Entries — OData Filters and Response Shapes

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

Always destructure before use: `const key = entry.status?.key || ''`. Rendering `entry.status` directly outputs `[object Object]`.

### Schema Discovery Before Write Operations

The OpenAPI spec for `object-admin` and the per object `/o/c/<pluralLabel>` endpoints is the source of truth — Liferay's hosted documentation lags. Before any POST or PATCH:

- Fetch the relevant OpenAPI spec via the `get-openapi` MCP tool, or via `/o/object-admin/v1.0/openapi.yaml`.
- GET response structure does NOT equal POST/PATCH request structure — do not infer the write shape from a read response.
- For settings whose `value` resolves to a generic string (`fileSource`, `acceptedFileExtensions`, etc.), see "Field Settings Gotchas" below — those values are not in the OpenAPI surface at all.

## Field Rules

- **Namespace safety**: NEVER use `userId` as a custom field name — it is a system column in `ObjectEntryTable` and will collide. Use `liferayUserId` instead.
- **Type storage**: every `DateTime` or `Date` field MUST have `timeStorage` set in `objectFieldSettings` (e.g., `"convertToUTC"`).
- **Indexed language**: `indexedLanguageId` is valid only on `String` and `Clob` field types. Never set it on `Date`/`DateTime` or other non-text fields.

## Field Settings Gotchas

`objectFieldSettings` entries that use generic string `value` fields (e.g., `fileSource`, `acceptedFileExtensions`, `maximumFileSize`) are **not documented as enums in the OpenAPI schema** and are not discoverable via GraphQL introspection — the `value` field resolves as a generic `Object` scalar. Guessing common values will produce `400 Bad Request` with no enum hint in the response.

When a `400 Bad Request` is returned for an unknown setting value, search the [Liferay Portal GitHub repository](https://github.com/liferay/liferay-portal) for the relevant constants or validation logic rather than guessing. Do not attempt further guesses without a source verified value.

## Permission Grants — Always Verify via Follow Up GET

After granting permissions via the Headless API, always verify with a follow up GET. Object permission APIs may return `200 OK` without persisting the change. If the follow up GET does not reflect the grant, use the Admin UI (Control Panel → Objects → [Object] → Permissions) as the reliable fallback.

## Batch Initialization via Client Extension

Use a Batch Client Extension (CX) to initialize Object Definitions, Folders, and seed data at deploy time. Do **not** mix Batch CX with Custom Element CX in the same project or `client-extension.yaml`.

**Permissions are not importable via batch.** The Batch Engine does not apply object or entry permissions from the JSON payload — do not put a `permissions` block in a `*.batch-engine-data.json` file expecting it to take effect. Grant permissions after deploy via Control Panel → Objects → [Object] → Permissions (or the Headless permissions API; see "Permission Grants" above).

### Project Structure

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

### `client-extension.yaml`

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

### Folder Definition (`01-00-...json`)

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

### Object Definition (`01-01-...json`)

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

### Data Entries (`03-00-...json`)

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

### Relationship Mapping

**Preferred (portable)** — use the relationship's camelCase name as the key:

```json
"relationshipName": {"externalReferenceCode": "TARGET-ERC-001"}
```

**Direct field mapping** (`r_...` syntax) — ERC is **not** supported here, only integer IDs:

```json
"r_accountToMyObject_accountEntryId": 38660
```

### Troubleshooting

- **NPE on deploy**: missing `.serviceAddress` or `.serviceScheme` in the OAuth server entry.
- **Object not created**: `className` in the `configuration` block must exactly match the REST DTO for your Liferay version.
- **Folder not found**: the `externalReferenceCode` in `objectFolderExternalReferenceCode` must match exactly — Batch Engine processes files alphabetically, so folders must have a lower prefix than the objects that reference them.