---

description: Add business logic to Liferay Objects via object actions, workflow definitions, and notification templates. Use when the user asks to send a notification on create, trigger an action on update, wire a workflow, or automate any response to object entry lifecycle events. Maps to the Business Logic and Automation section of "Mastering Data Modeling with Liferay Objects".
name: manage-object-logic

---

# Manage Object Logic

Attach triggers, conditions, and actions to object definitions. The three extension points are object actions (immediate), Kaleo workflows (multistep approval), and notification templates (user facing messages).

## When to Invoke

- "Send a notification when a Book is created"
- "Trigger a webhook on order update"
- "Wire an approval workflow to this object"
- "Run a Groovy script after an entry is deleted"
- Called by `build-site` during the logic phase

## Prerequisites

Object definition must already exist and be published (see `manage-objects`). Client Extension actions require the CET to be deployed first (see `scaffold-client-extension`).

## Workflow

### Choose the Trigger

| Trigger | When It Fires |
| --- | --- |
| `onAfterAdd` | After an entry is created |
| `onAfterUpdate` | After an entry is updated |
| `onAfterDelete` | After an entry is deleted |
| `standalone` | On demand, invoked explicitly by a user or API call |

### Choose the Action Type

Consult `rules/object-actions-catalog.md` for the full catalog. Summary:

| Action Type | Payload | Notes |
| --- | --- | --- |
| Notification | Template ID | Sends in app or email notification |
| `addObjectEntry` | Definition name + field map | Creates an entry in another object |
| `updateObjectEntry` | Entry ID + field map | Updates an entry in the same or another object |
| Webhook | URL + secret | HTTP POST to external endpoint |
| Groovy Script | Script body | Only on self hosted or PaaS; not available on Liferay SaaS |
| Client Extension | CET `objectAction` or `workflowAction` externalReferenceCode | Calls a deployed microservice |

### Object Action — Notification (Site Initializer, Preferred)

When the object lives in a site initializer, author the template **and** its action in the tree so the whole thing survives delete-and-redeploy. The REST recipe further down is for one off changes to a running instance.

Three files in one directory, `site-initializer/notification-templates/<name>/`:

```
notification-templates/
  registration-confirmation/
    notification-template.json                  # metadata
    en-US.html                                  # body, one file per locale
    notification-template.object-actions.json   # the action(s) that fire it
```

`notification-template.json` — note there is **no `body` key**; the handler builds `body` from every `*.html` in the directory, keyed by filename (`en-US.html` → `en-US`):

```json
{
	"editorType": "richText",
	"externalReferenceCode": "<TEMPLATE_ERC>",
	"name": "<Template Name>",
	"recipientType": "email",
	"recipients": [
		{
			"from": "noreply@example.com",
			"fromName": {"en_US": "<Sender>"},
			"singleRecipient": true,
			"to": {"en_US": "[%<OBJECTNAME>_<FIELDNAME>%]"}
		}
	],
	"subject": {"en_US": "<Subject>"},
	"type": "email"
}
```

`notification-template.object-actions.json` — a bare array. **Do not set `notificationTemplateId`**; the handler injects the id of the template it sits beside, which is what makes the pair portable:

```json
[
	{
		"active": true,
		"externalReferenceCode": "<ACTION_ERC>",
		"label": {"en_US": "<Action Label>"},
		"name": "<actionName>",
		"objectActionExecutorKey": "notification",
		"objectActionTriggerKey": "onAfterAdd",
		"objectDefinitionId": "[$OBJECT_DEFINITION_ID:<ObjectName>$]"
	}
]
```

#### Field Tokens

A term is `[%` + the object's **short name** + `_` + the **field name**, all uppercased, + `%]`. For a `Registration` object with fields `attendeeName` and `email`: `[%REGISTRATION_ATTENDEENAME%]` and `[%REGISTRATION_EMAIL%]`. Camel case collapses — there is no separator inside the field name.

Tokens work in `subject`, in the body HTML, and in `recipients[].to`, which is how a confirmation is addressed to the address the visitor just typed.

Only fields **on that object** resolve. A token reaching across a relationship stays in the output as literal `[%…%]` text, so verify after sending:

```bash
curl --silent --user "test@liferay.com:test" \
	"http://localhost:${PORT}/o/notification/v1.0/notification-queue-entries?pageSize=5" \
	| jq '.items[] | {id, recipientsSummary, status, unresolved: (.body | test("\\[%"))}'
```

`status: 1` with `unresolved: false` and the right `recipientsSummary` is a delivered, correctly addressed notification. On a local bundle without SMTP the queue entry is the evidence — actual delivery is out of scope.

### Object Action — Notification (Live API)

Create a notification template first if one does not exist:

```bash
curl \
	--data '{
		"body": {"en_US": "A new [%OBJECT_FIELD_NAME%] was created."},
		"description": "",
		"editorType": "richText",
		"name": "<TemplateName>",
		"objectDefinitionExternalReferenceCode": "<objectERC>",
		"recipientType": "user",
		"subject": {"en_US": "New entry created"},
		"type": "email"
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/notification/v1.0/notification-templates" \
	--user "test@liferay.com:test"
```

Save the returned `id` as `<template-id>`. Then create the action:

```bash
curl \
	--data '{
		"active": true,
		"label": {"en_US": "<ActionLabel>"},
		"name": "<actionName>",
		"objectActionExecutorKey": "notification",
		"objectActionTriggerKey": "onAfterAdd",
		"parameters": {
			"notificationTemplateId": <template-id>
		}
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions/<definition-id>/object-actions" \
	--user "test@liferay.com:test"
```

### Object Action — Webhook

```bash
curl \
	--data '{
		"active": true,
		"label": {"en_US": "<ActionLabel>"},
		"name": "<actionName>",
		"objectActionExecutorKey": "webhook",
		"objectActionTriggerKey": "onAfterAdd",
		"parameters": {
			"secret": "<hmac-secret>",
			"url": "<https://endpoint.example.com/hook>"
		}
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions/<definition-id>/object-actions" \
	--user "test@liferay.com:test"
```

### Object Action — Client Extension

First deploy the `objectAction` CET via `scaffold-client-extension`. Then reference its `externalReferenceCode`:

```bash
curl \
	--data '{
		"active": true,
		"label": {"en_US": "<ActionLabel>"},
		"name": "<actionName>",
		"objectActionExecutorKey": "objectAction",
		"objectActionTriggerKey": "onAfterAdd",
		"parameters": {
			"clientExtensionEntryExternalReferenceCode": "<cet-erc>"
		}
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions/<definition-id>/object-actions" \
	--user "test@liferay.com:test"
```

The executor key for a Client Extension action is `"objectAction"` (not `"groovy"` — that key is only for the inline Groovy executor in §3a/§3b's sibling pattern). See `rules/object-actions-catalog.md`.

### Kaleo Workflow

Deploy a workflow definition when the object requires multistep review or approval.

```bash
curl \
	--data '{
		"active": true,
		"name": "<WorkflowName>",
		"title": {"en_US": "<Workflow Title>"},
		"content": "<escaped XML or JSON workflow definition>"
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-workflow/v1.0/workflow-definitions" \
	--user "test@liferay.com:test"
```

After creating, associate the workflow with the object definition. The reliable path is the Control Panel (Objects → \<Definition\> → Actions → Workflow). There is **no** `workflow-definitions/{id}/assign-to-object` endpoint; programmatic association is done through the `workflow-definition-links` resource (`POST /o/headless-admin-workflow/v1.0/workflow-definitions/<id>/workflow-definition-links`) — confirm the request body against the OpenAPI spec (`get-openapi` MCP tool, or `GET /o/headless-admin-workflow/v1.0/openapi.json`) before scripting it, as the link payload (workflow, class name, type pk) is version sensitive.

### Verify Object Actions

```bash
curl \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions/<definition-id>/object-actions" \
	--user "test@liferay.com:test" \
	| jq '[.items[] | {name, objectActionTriggerKey, objectActionExecutorKey, active}]'
```

Confirm each action is `"active": true`.

### Test the Trigger

Create a test entry and check the expected side effect (email received, webhook payload, other entry created):

```bash
curl \
	--data '{"<fieldName>": "test value"}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/c/<pluralLabel>" \
	--user "test@liferay.com:test"
```

## Common Gotchas

### Object Action Refire Loop

`onAfterUpdate` fires on **every** REST PATCH — including PATCHes made by the action itself. If your action writes back to the same record via REST, it will loop.

**Safe path**: call `ObjectEntryLocalServiceUtil.updateObjectEntry` directly from within the Object Action. Direct service layer calls do **not** retrigger the Object Action — only REST API calls do. Use this pattern for any script that needs to update the same record it is acting on.

### Groovy Output Binding

Object Action Groovy scripts have no `out` binding. Use bare `println` to write to `catalina.out`. Using `out.println` throws `No such property: out`.

### Diagnostic Action Hygiene

Stale diagnostic Object Actions are a silent data hazard: they continue firing on every matching event after a session ends and can revert data changes at unexpected times — often with no error, just a wrong value in the database. Secondary concern: they accumulate output in `catalina.out` and interleave with real logs.

Rules:

- Prefix all diagnostic actions with `diag-` (e.g., `diag-check-balance`).
- Delete all `diag-` actions before shipping — they are not safe to leave running in any persistent environment.
- Bulk delete when done: filter by name prefix in Control Panel → Objects → [Object] → Actions, or via the Object Admin REST API.

### Type Safety in Groovy

Never pass interpolated strings (`"${var}"`) to Liferay Service APIs. Groovy `GStringImpl` causes cast exceptions. Always use explicit string concatenation: `"" + var`.