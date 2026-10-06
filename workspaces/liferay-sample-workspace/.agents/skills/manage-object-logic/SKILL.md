---

description: Add business logic to Liferay Objects via object actions, workflow definitions, and notification templates. Use when the user asks to send a notification on create, trigger an action on update, wire a workflow, or automate any response to object entry lifecycle events.
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
| Groovy Script | Script body | Self hosted or PaaS **and** script execution enabled — off by default, see `rules/object-actions-catalog.md`. Probe before designing around it |
| Client Extension | Executor key `function#<cet-erc>` of a deployed `objectAction` CET | Calls a deployed microservice |

### Deliver the Object Action as a Client Extension (Preferred)

Two different things share the name. The **`objectAction` CET** is the code to run; deploying it only registers an executor, `function#<cet-erc>`, and attaches it to nothing. Its `client-extension.yaml` names no object and no trigger. The **object action** is a record on the object definition — object, trigger, executor — and nothing fires until it exists.

Deliver that record as a client extension too, so a fresh environment gets it by deploying, not by replaying REST calls. The REST recipes below are for one off changes to a running instance.

A `batch` CET can create it in two ways:

| `className` | How | Use When |
| --- | --- | --- |
| `com.liferay.object.admin.rest.dto.v1_0.ObjectAction` | One file of actions, the definition named by ERC | The definition is owned elsewhere — another batch, the UI, a site initializer |
| `com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition` | `objectActions` inside the full definition | This batch already owns the whole definition |

#### Standalone `ObjectAction` Batch

```json
{
	"configuration": {
		"className": "com.liferay.object.admin.rest.dto.v1_0.ObjectAction",
		"parameters": {
			"containsHeaders": "true",
			"createStrategy": "INSERT",
			"externalReferenceCode": "<OBJECT_DEFINITION_ERC>",
			"importStrategy": "ON_ERROR_FAIL"
		},
		"taskItemDelegateName": "DEFAULT"
	},
	"items": [
		{
			"active": true,
			"conditionExpression": "",
			"externalReferenceCode": "<ACTION_ERC>",
			"label": {
				"en_US": "<Action Label>"
			},
			"name": "<actionName>",
			"objectActionExecutorKey": "function#<cet-erc>",
			"objectActionTriggerKey": "onAfterAdd",
			"parameters": {
			}
		}
	]
}
```

Verified on 2026.Q1 with a `function#` executor: the action was created on the named definition, fired, and read `success`, and the definition's fields were untouched.

- **`parameters.externalReferenceCode` is the object definition's ERC**, not the action's. `objectDefinitionId` works too, but it is a numeric ID and differs per environment. `objectDefinitionExternalReferenceCode` is rejected.
- **Use `INSERT`.** `UPSERT` returns `500` for `ObjectAction`. Give each item a fixed `externalReferenceCode`: a redeploy then leaves exactly one action instead of a duplicate.
- **Deploy the `objectAction` CET first.** The executor must exist when the batch runs.
- **Use a separate project.** `batch` and microservice CETs cannot share a project (`rules/client-extension-types.md`), so the action batch is a sibling of the microservice project.
- **The batch needs `Liferay.Headless.Batch.Engine.everything`** on its `oAuthApplicationHeadlessServer`. On a local bundle that scope alone imported both `ObjectField` and `ObjectAction` items. Verified on 2026.Q1. Liferay Cloud was not tested.

#### `objectActions` Inside an `ObjectDefinition` Batch

`client-extensions/liferay-sample-batch` shows this shape. An `ObjectDefinition` import **replaces the whole definition**: omit `objectFields` and the import tries to delete the existing fields. Verified on 2026.Q1, where it failed only because the field was the definition's last custom one. Use this form only in the batch that already carries the complete definition; never write a partial definition just to add an action.

### Object Action — Notification (Site Initializer, Preferred)

When the object lives in a site initializer, author the template **and** its action in the tree so the whole thing survives delete and redeploy. The REST recipe further down is for one off changes to a running instance.

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
			"fromName": {
				"en_US": "<Sender>"
			},
			"singleRecipient": true,
			"to": {
				"en_US": "[%<OBJECTNAME>_<FIELDNAME>%]"
			}
		}
	],
	"subject": {
		"en_US": "<Subject>"
	},
	"type": "email"
}
```

`notification-template.object-actions.json` — a bare array. **Do not set `notificationTemplateId`**; the handler injects the ID of the template it sits beside, which is what makes the pair portable:

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

Only fields **on that object** resolve. A token reaching across a relationship stays in the output as literal `[%…%]` text.

**The fix is to denormalize.** Add a plain `Text` field to the object holding the related value, populate it when the entry is created, and token that field instead. A registration confirmation that must name the event needs an `eventName` copied onto `Registration` — `[%REGISTRATION_EVENTNAME%]` resolves, a reach through `eventRegistrations` does not. Same pattern as `manage-pages` → "Mapping Limits"; the relationship stays authoritative, the copy is for display.

Verify after sending:

```bash
curl \
	--silent \
	--url "http://localhost:${PORT}/o/notification/v1.0/notification-queue-entries?pageSize=50" \
	--user "test@liferay.com:test" \
	| jq '.items | sort_by(.id) | last
		| {id, recipientsSummary, status, unresolved: (.body | test("\\[%"))}'
```

> **Do not add `sort=` to that URL.** `notification-queue-entries` does not support the common `sort` parameter and **fails silently** — `?sort=id:desc` returns `{"totalCount": null, "items": []}` with a `200`, which reads exactly like "the action never fired". Fetch unsorted and sort in `jq`, as above.

`unresolved: false` with the right `recipientsSummary` means Liferay **built** a correctly addressed message. That is the part this endpoint can actually tell you.

#### `status: 1` Is `STATUS_SENT` and It Lies

The status codes are not a queue depth. From `NotificationQueueEntryConstants`:

| Value | Constant |
| --- | --- |
| `0` | `STATUS_FAILED` |
| `1` | `STATUS_SENT` |
| `2` | `STATUS_UNSENT` |

So `status: 1` is Liferay asserting the mail **was sent** — and it asserts that even when nothing was transmitted. Verified on 2026.Q2 with a local SMTP sink on `127.0.0.1:2525` and the mail session pointed at it: two entries came back `status: 1` with `sent: null`, and the sink received **nothing**. Not a delayed send, not a failure code — a claim of success with no traffic behind it.

Treat this field as worthless for delivery. `sent: null` alongside `status: 1` is the tell that the two are not wired to the same truth, but the only real check is at the receiving end.

#### Localize a Mail Failure Before Blaming the Notification

When the message builds but nothing arrives, the fault is either the notification framework or the mail transport, and the queue entry cannot distinguish them. **Trigger an unrelated portal email and see whether that arrives**, which needs no admin session:

```bash
curl \
	--data-urlencode "emailAddress=test@liferay.com" \
	--data-urlencode "step=2" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/c/portal/forgot_password"
```

If the password reset mail is missing too, the notification wiring is fine and the mail session is the problem — stop debugging object actions. That one call saved a long detour here.

Then check **Control Panel → Server Administration → Mail** before trusting `portal-ext.properties`. Adding `mail.session.mail.smtp.*` to that file and restarting was not sufficient on a bundle in this run; the cause was not isolated (that page needs an admin session), so verify the effective host and port there rather than assuming the properties won.

Report queueing and delivery as separate facts. "Composed and addressed correctly, delivery unverified" is honest; "the confirmation email was sent" is not, no matter what `status` says.

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

That shape mails a **portal user**. A public form has no user — the address was typed into a field — so `recipientType: "user"` is wrong there and there is no `recipients` block to carry the address.

#### Mailing an Address Held in a Field (Public Forms)

Combine the live create with the tree format's `recipients` array. This is the shape a registration or enquiry confirmation needs:

```bash
curl \
	--data '{
		"body": {"en_US": "<p>Hi [%REGISTRATION_ATTENDEENAME%], we received your registration for [%REGISTRATION_EVENTNAME%].</p>"},
		"editorType": "richText",
		"externalReferenceCode": "REGISTRATION_CONFIRMATION",
		"name": "Registration Confirmation",
		"objectDefinitionExternalReferenceCode": "REGISTRATION",
		"recipientType": "email",
		"recipients": [
			{
				"from": "noreply@example.com",
				"fromName": {"en_US": "DevCon Registration"},
				"singleRecipient": true,
				"to": {"en_US": "[%REGISTRATION_EMAIL%]"}
			}
		],
		"subject": {"en_US": "We received your registration"},
		"type": "email"
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/notification/v1.0/notification-templates" \
	--user "test@liferay.com:test"
```

`recipients[].to` takes a token, which is what addresses the mail to whatever the visitor typed.

**`from` persists on the template; it is the queue entry that reports `null`.** Verified on 2026.Q2 — a fresh `GET /notification-templates/<id>` read back `"from": "noreply@devcon.example"` exactly as posted, alongside `fromName`. The `null` turns up one layer down, on the `notification-queue-entries` record, so a check there is what makes `from` look dropped. Set it on the template as documented, and confirm the actual sender on the received message rather than on the queue entry.

The `NotificationTemplate` schema exposes no enums for `type` or `recipientType` and publishes no nested `Recipient` schema, so the OpenAPI spec will not confirm this shape — it is verified by creating one and reading the queue entry.

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

This is the live recipe. To ship it, put the same body in a `batch` CET as above.

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

First deploy the `objectAction` CET via `scaffold-client-extension`. Then create the object action that references it — preferably as a `batch` CET (see "Deliver the Object Action as a Client Extension" above). For a one off change to a running instance:

```bash
curl \
	--data '{
		"active": true,
		"label": {"en_US": "<ActionLabel>"},
		"name": "<actionName>",
		"objectActionExecutorKey": "function#<cet-erc>",
		"objectActionTriggerKey": "onAfterAdd",
		"parameters": {
		}
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions/<definition-id>/object-actions" \
	--user "test@liferay.com:test"
```

**The executor key is `function#` plus the `objectAction` CET entry's ERC**, the key under which `FunctionObjectActionExecutorImpl` registers each deployed CET. Verified end to end on 2026.Q1: the action ran, the CET received the POST, and the action's status read `success`. A bare `"objectAction"` key with the ERC in `parameters` also saves with a `200`, so a successful save proves nothing. Read the action's `status.label` after triggering it: `never-ran` means it has not fired.

The CET receives the entry twice: as the persistence model under `objectEntry` (custom fields in `values`, `createDate` as a date string), and as the REST DTO under `objectEntryDTO<ObjectName>` (custom fields in `properties`, `dateCreated` as **epoch milliseconds**, not ISO 8601). There is no `modelDTO<ObjectName>` key for a custom object.

A callback to `/o/c/<plural>` needs the **object's own scope** on the CET's OAuth application, not `Liferay.Headless.Object.everything`. See `rules/oauth-scopes.md`.

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

A `workflowAction` CET receives a `transitionURL` in its payload, and it is relative (`/o/headless-admin-workflow/v1.0/workflow-tasks/<id>/change-transition`). Prefix the portal URL before posting `{"transitionName": "<name>"}`. The call needs `Liferay.Headless.Admin.Workflow.everything`. Verified on 2026.Q1.

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

## Patterns and Gotchas

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

## Success Signal

TODO / inferred — verify against a running bundle. The Verify Object Actions and Test the Trigger steps above (each action `"active": true`; a test entry fires the expected side effect) are the observable completion checks; confirm on a live bundle.