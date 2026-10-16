---

paths:
  - "**/notification-templates/**"
  - "**/object-actions/**"

---

# Object Actions Catalog

> **Before authoring:** Load `manage-object-logic` before authoring an action, notification template, or workflow — this card is the catalog, that skill is the procedure.

Reference for the `manage-object-logic` skill. Covers triggers, conditions, and all action executor types.

## Triggers

Triggers fire automatically based on the lifecycle of an object entry. A `standalone` action fires only when invoked explicitly.

| Trigger Key | When It Fires | Typical Use |
| --- | --- | --- |
| `onAfterAdd` | After an entry is created and committed | Welcome notification, audit log entry |
| `onAfterUpdate` | After an entry is updated and committed | Change notification, recalculation |
| `onAfterDelete` | After an entry is deleted | Cascade cleanup, audit log |
| `standalone` | On demand via the Actions menu or API | Manual approval step, data export |

## Condition Expressions

A condition expression limits when the action runs. Leave the `conditionExpression` field empty to run on every trigger event.

Expression syntax mirrors object validation expressions. Use field references and comparison operators:

```
status == "approved"
amount > 1000
(priority == "high") AND (assigneeId != null)
```

Consult learn.liferay.com for the full expression builder reference (search `object validations expression builder`).

## Action Executor Types

### `notification`

Sends an in app or email notification using a notification template.

Required `parameters`:

```json
{
	"notificationTemplateId": "<template-id>"
}
```

Create the template first via `POST /o/notification/v1.0/notification-templates`. Template body supports field interpolation with `[%OBJECT_FIELD_NAME%]` tokens.

### `add-object-entry`

Creates a new entry in the target object definition.

Required `parameters`:

```json
{
	"objectDefinitionId": "<target-definition-id>",
	"predefinedValues": [
		{
			"name": "<fieldName>",
			"value": "<value>"
		}
	]
}
```

> **`predefinedValues` cannot set a `DateTime` field.** The action's value parser only handles `DBType=Date`, so a DateTime string is silently dropped — the entry is created with the field empty and no error is logged. To timestamp entries created by an action, put a `currentDate` **expression-builder default value** on the DateTime field itself: the service layer stamps it (minute precision) on every create path, and only when the field is absent, so explicit values from imports still win. Inferred, not verified against source; verify against a running bundle.

### `update-object-entry`

Updates an existing entry in the same or another object definition.

Required `parameters`:

```json
{
	"objectDefinitionId": "<target-definition-id>",
	"objectEntryId": "<expression-resolving-to-id>",
	"predefinedValues": [
		{
			"name": "<fieldName>",
			"value": "<new-value>"
		}
	]
}
```

### `webhook`

POSTs a JSON payload to an external URL.

Required `parameters`:

```json
{
	"secret": "<hmac-secret>",
	"url": "<https://endpoint.example.com/hook>"
}
```

Liferay signs each request with `HMAC-SHA256` using the secret. The `X-Liferay-Webhook-Signature` header carries the hex digest.

**Payload shape:** field values are NOT at the payload root — they sit under `objectEntry.values.<fieldName>` (including relationship FKs like `r_<rel>_c_<parent>Id`), and the acting user's details are under a per-object key `objectEntryDTO<ObjectName>.creator` (`givenName`, `familyName`, `email`). A receiver that reads fields off the root parses nothing and fails silently.

### `groovy`

Executes a Groovy script on the portal JVM.

Required `parameters`:

```json
{
	"script": "<groovy source>"
}
```

**Availability: self hosted is necessary but not sufficient.** Beyond the SaaS exclusion, creating a `groovy` action is gated at runtime by `ScriptManagementConfiguration.isAllowScriptContentToBeExecutedOrIncluded()`, which is **off by default on current DXP**. With it off the definition is rejected outright:

```text
400 ObjectActionExecutorKeyException
Groovy script based object actions are not allowed
```

Verified on a self hosted 2026.Q2 bundle. Treat Groovy as unavailable until proven otherwise: probe by creating a throwaway action before designing a solution around one, because turning the flag on is a portal wide decision to permit arbitrary script execution — surface it to the user rather than enabling it silently. Prefer a scriptless alternative where one exists (an `Aggregation` field replaces a hand maintained counter — see `skills/manage-objects/SKILL.md`). Source: `ObjectActionLocalServiceImpl`.

**Script bindings are the entry's fields as top level variables — there is no `objectEntry` object.** `GroovyObjectActionExecutorImpl` passes `ObjectEntryVariablesUtil.getVariables(...).get("baseModel")` as the binding, which spreads `objectEntry.values` directly. So a `Registration` field is referenced as bare `attendeeName`, and a relationship foreign key as bare `r_eventRegistrations_c_eventId`.

Also bound: `creator`, `currentDate`, `currentUserId`, `currentUserExternalReferenceCode`, `groupId`, `id` (the entry ID), and `entryDTO`. Guard optional fields with `binding.hasVariable("<name>")` — referencing an absent one throws `MissingPropertyException`. Source: `ObjectEntryVariablesUtil`.

### `function#<cet-erc>` (Client Extension)

Calls a deployed `objectAction` CET microservice. The executor key is `function#` plus the CET entry's ERC, which is its top level key in `client-extension.yaml` with no `LXC:` prefix, and `parameters` is empty:

```json
{
	"objectActionExecutorKey": "function#<cet-erc>",
	"parameters": {
	}
}
```

A bare `"objectAction"` key with the ERC in `parameters`, or `function#LXC:<cet-erc>`, also saves with a `200` but never fires: every run fails with `No object action executor found with company ID … and key …`. Each deployed CET registers its own executor (`FunctionObjectActionExecutorImpl.java:80-83`), so deploy the CET before creating the action. See `skills/manage-object-logic/SKILL.md` → "Object Action — Client Extension".

The microservice receives a POST with the object entry payload and a Bearer token. Implement via `scaffold-client-extension` with type `objectAction`. Facts about that payload (from source, `ObjectEntryVariablesUtil.java:195-215`, and verified on 2026.Q3):

- **The entry ID is the top level `classPK`.** `id` appears only nested.
- **Field values are under `objectEntryDTO<ObjectName>.properties`**, a key named per object (`objectEntryDTOBook`), mirrored raw in `objectEntry.values`. Nothing is flattened to the root.
- **A system object sends a different payload.** `AccountEntry` sends `modelDTOAccount` and `modelAccountEntry` (and their `original*` twins) plus `extendedProperties`, with no `objectEntryDTO*` key. Route by the top level `objectDefinitionId`.
- **The inbound Bearer token is for the user who triggered the entry**, and can call back into Liferay with the object's per object scope (`rules/oauth-scopes.md`). When the trigger is anonymous, the token is Guest's and every callback gets `403`, whatever the scopes. The microservice then needs its own `oAuthApplicationHeadlessServer` identity: `setup-oauth` → "A Microservice With Its Own Identity", and `manage-object-logic` → "Calling Back Into Liferay From the CX".

## REST Payload Shape

This is the object action definition body. The same shape is an item in a `batch` CET file with `className` `com.liferay.object.admin.rest.dto.v1_0.ObjectAction`, the preferred way to ship an action — see `skills/manage-object-logic/SKILL.md` → "Deliver the Object Action as a Client Extension":

```json
{
	"active": true,
	"conditionExpression": "<expression or empty string>",
	"description": "<optional description>",
	"label": {"en_US": "<Display Label>"},
	"name": "<camelCaseName>",
	"objectActionExecutorKey": "<executor-type>",
	"objectActionTriggerKey": "<trigger>",
	"parameters": { ... }
}
```

## References

- `skills/manage-object-logic/SKILL.md` — the primary consumer of this card.
- `rules/client-extension-types.md` — the `objectAction` CET type.
- `rules/oauth-scopes.md` — scopes for CET backed actions.
- Objects: `https://learn.liferay.com/w/dxp/low-code/objects`