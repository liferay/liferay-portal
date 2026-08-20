# Object Actions Catalog

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

### `groovy`

Executes a Groovy script on the portal JVM.

Required `parameters`:

```json
{
	"script": "<groovy source>"
}
```

**Availability:** self hosted and Liferay PaaS only. Not available on Liferay SaaS. The script runs in a sandbox; network calls and filesystem access are restricted.

The entry is available as `objectEntry` in the script binding, and the service locator is available via `PortalUtil`.

### `objectAction` (Client Extension)

Calls a deployed `objectAction` CET microservice.

Required `parameters`:

```json
{
	"clientExtensionEntryExternalReferenceCode": "<cet-erc>",
	"objectActionExecutorKey": "objectAction"
}
```

The microservice receives a POST with the object entry payload and a Bearer token. Implement via `scaffold-client-extension` with type `objectAction`.

## REST Payload Shape

Object action definition body:

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