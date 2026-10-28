---

description: Add business logic to Liferay Objects via object actions, workflow definitions, and notification templates. Use when the user asks to send a notification on create, trigger an action on update, wire a workflow, or automate any response to object entry lifecycle events.
name: manage-object-logic

---

# Manage Object Logic

Attach triggers, conditions, and actions to object definitions. The three extension points are object actions (immediate), Kaleo workflows (approval; see "Approval Workflow"), and notification templates (user facing messages).

## When to Invoke

- "Send a notification when a Book is created"
- "Trigger a webhook on order update"
- "Wire an approval workflow to this object"
- "Run a Groovy script after an entry is deleted"
- Called by `build-site` during the logic phase

## Prerequisites

Object definition must already exist and be published (see `manage-objects`). Client Extension actions require the CET to be deployed first (see `scaffold-client-extension`).

## Workflow

> **Ship the logic in a client extension.** Deliver object actions as a `batch` CET ("Deliver the Object Action as a Client Extension" below), and notification templates and workflow definitions in the site initializer tree (`notification-templates/`, `workflow-definitions/`; see `rules/site-initializer-format.md`). The REST calls in this skill show each payload's shape and serve one-off changes to a running instance, plus the gaps the tree cannot cover, which "Approval Workflow" → "In a Site Initializer" names. See `liferay-rules.md` → "Deliver Through Client Extensions".

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
- **Use `INSERT`.** `UPSERT` is rejected for `ObjectAction`, so do not copy it from the `ObjectDefinition` batch in [liferay-sample-batch](https://github.com/liferay/liferay-portal/tree/master/workspaces/liferay-sample-workspace/client-extensions/liferay-sample-batch). In a `batch` CET the rejection is silent: the log shows `Illegal create strategy UPSERT` at `WARN`, no import task starts, and the bundle still reaches `STARTED`. Give each item a fixed `externalReferenceCode`: a redeploy then leaves exactly one action instead of a duplicate. That redeploy logs `Unable to update batch engine import task` at `ERROR` with `DuplicateObjectActionExternalReferenceCodeException`, or `DuplicateObjectFieldExternalReferenceCodeException` for an `ObjectField` batch — the expected outcome, not a failure. Verified on 2026.Q1.
- **Deploy the `objectAction` CET first.** The executor must exist when the batch runs.
- **Use a separate project.** `batch` and microservice CETs cannot share a project (`rules/client-extension-types.md`), so the action batch is a sibling of the microservice project.
- **The batch needs `Liferay.Headless.Batch.Engine.everything`** on its `oAuthApplicationHeadlessServer`. On a local bundle, that scope alone imported both `ObjectField` and `ObjectAction` items. Verified on 2026.Q1. Liferay Cloud was not tested.

#### Adding a Field the Action Needs

The same standalone form adds a field to a definition owned elsewhere, without replacing it: `className` `com.liferay.object.admin.rest.dto.v1_0.ObjectField`, the same `externalReferenceCode` parameter naming the definition, and one raw `ObjectField` per item (`DBType`, `businessType`, `externalReferenceCode`, `label`, `name`, `required`). Verified on 2026.Q1: the field was added and the existing fields were untouched. Number the files so the field batch sorts before the action batch.

#### `objectActions` Inside an `ObjectDefinition` Batch

[liferay-sample-batch](https://github.com/liferay/liferay-portal/tree/master/workspaces/liferay-sample-workspace/client-extensions/liferay-sample-batch) shows this shape. An `ObjectDefinition` import **replaces the whole definition**: omit `objectFields` and the import tries to delete the existing fields. Verified on 2026.Q1, where it failed only because the field was the definition's last custom one. Use this form only in the batch that already carries the complete definition; never write a partial definition just to add an action.

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

**The queue keeps the body.** Every queued notification's rendered `body` is a column of `NotificationQueueEntry`, and this endpoint serves it back. So a template must never carry a secret: a one-time link, an invitation token or a reset code would sit in the database in clear. Mail such a message through `MailService` from a module instead, which persists nothing.

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

**What delivered:** an outgoing mail server saved in **System Settings → Email → Mail Settings** (`com.liferay.mail.settings.configuration.MailSettingCompanyConfiguration`, stored in the database) with STARTTLS and an account sign-in, applied with no restart, and an object action's email reached the recipient's inbox. Verified on 2026.Q2. A provider can replace the template's `from` with the signed-in account; `fromName` stays. Keep the server settings out of any committed config, since they hold the password, and expect a new database to need them set again.

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

To send through a `notificationType` CET instead of email, set `"type"` to `function#<cet-erc>`. The bare ERC is rejected with a `500`. A `function#` template also needs a `recipients` array; without one the save fails with a `500` (`createNotificationRecipientSettings`). Every template needs `editorType`, whatever its type. Delivery leaves no notification queue entry, so confirm it in the CET's log. Verified on 2026.Q1.

That shape mails a **portal user**. A public form has no user — the address was typed into a field — so `recipientType: "user"` is wrong there and there is no `recipients` block to carry the address.

For an in app (bell) notification to a fixed user, the verified shape on 2026.Q3.2 is `"type": "userNotification"`, `"recipientType": "user"`, `"recipients": [{"userScreenName": "<screenName>"}]`; confirm delivery with `GET /o/headless-user-notification/v1.0/my-user-notifications` as the recipient. The bell shows the template's **`body`**, as plain text (`message`). A Picklist token such as `[%LEAD_QUALIFICATIONSTATUS%]` renders the entry's **label** ("Qualified"), not its key. A condition on a Picklist compares the **key**: `qualificationStatus == "qualified"`. It can be set or cleared later with `PATCH /o/object-admin/v1.0/object-actions/{id}` `{"conditionExpression": "..."}`, which keeps `parameters`. Verified on 2026.Q3.2.

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

`recipientType` `"email"` and `"emailAddress"` behaved identically for a fixed address on 2026.Q3.2: both resolve `to` and queue the message with `toType: "email"`. The action's own status (`never-ran` for one, `success` for the other) was no guide, so judge by the queue entry.

**On a local HSQLDB bundle, keep one action per trigger while testing.** Two `notification` actions on the same `onAfterAdd` deadlocked the portal on the first entry created (2026.Q3.2). The action-status update takes a lock in a new transaction that waits on the original one, page rendering blocked behind it, and only a Tomcat restart recovered. With one action per trigger there was no hang. The link to the number of actions is inferred from that contrast alone.

**`from` persists on the template; it is the queue entry that reports `null`.** Verified on 2026.Q2 — a fresh `GET /notification-templates/<id>` read back `"from": "noreply@devcon.example"` exactly as posted, alongside `fromName`. The `null` turns up one layer down, on the `notification-queue-entries` record, so a check there is what makes `from` look dropped. Set it on the template as documented, and confirm the actual sender on the received message rather than on the queue entry.

The `NotificationTemplate` schema exposes no enums for `type` or `recipientType` and publishes no nested `Recipient` schema, so the OpenAPI spec will not confirm this shape — it is verified by creating one and reading the queue entry.

**On 2026.Q2.7 the create needs the numeric `objectDefinitionId`.** With only `objectDefinitionExternalReferenceCode`, `POST /notification-templates` answers a bare `500` (`NullPointerException` on `NotificationTemplate.getObjectDefinitionId()` in the log); send both. `attachmentObjectFieldExternalReferenceCodes` (an Attachment field's ERC) attaches the entry's uploaded file to the email. **A missing template answers `204` with an empty body, not `404`,** on `GET /notification-templates/by-external-reference-code/{erc}`, so treat anything but `200` as absent. Verified on 2026.Q2.7.

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

**The executor key is `function#` plus the `objectAction` CET entry's ERC**, the key under which `FunctionObjectActionExecutorImpl` registers each deployed CET. Verified end to end on 2026.Q1: the action ran, the CET received the POST, and the action's status read `success`. A bare `"objectAction"` key with the ERC in `parameters`, or `function#LXC:<cet-erc>`, also saves with a `200` and then fails at every run with `No object action executor found`, so a successful save proves nothing. Confirm that the action fired from the CET's own log. `status.label` is not reliable: one action reached the CET four times and still read `never-ran`.

The CET receives the entry twice: as the persistence model under `objectEntry` (custom fields in `values`, `createDate` as a date string), and as the REST DTO under `objectEntryDTO<ObjectName>` (custom fields in `properties`, `dateCreated` as **epoch milliseconds**, not ISO 8601). There is no `modelDTO<ObjectName>` key for a custom object.

**The payload does not say which site the entry came from.** For a company scoped object, `objectEntry.groupId` is `0` and the DTO carries no site, and there is no HTTP request context to read a referrer from. Verified on 2026.Q1 with an entry submitted from a site page. When the action needs the originating site, capture it at submit time: add a field to the object (see "Adding a Field the Action Needs" above) and have the form send it. `Liferay.ThemeDisplay` exposes no site name, only IDs and URLs — `getPortalURL()` plus the `/web/<site>` prefix of `getLayoutRelativeURL()` gives a readable site URL.

A related value the payload carries only as a foreign key, such as the parent entry's name, can be fetched with the bearer token the CET receives, provided the OAuth application has that object's scope (`rules/oauth-scopes.md`). The token is the triggering user's, so a Guest submission fetches as Guest.

A callback to `/o/c/<plural>` needs the **object's own scope** on the CET's OAuth application, not `Liferay.Headless.Object.everything`. See `rules/oauth-scopes.md`.

**Respond before calling back.** The executor runs as a commit callback, so by the time the CET is called the entry is committed (`BaseObjectActionExecutor`, every release from 2026.Q1), and a `GET` with a separate token finds it. Verified on 2026.Q3. The request that added the entry still waits for the CET to answer. On Hypersonic, a callback to `/o/c` made before the CET responded hung that request. Verified on 2026.Q1. Answer Liferay first, then make the callback.

The same hang was seen once without any callback, on the first add after creating two actions on Hypersonic. Liferay never called the CET; the add waited on its own action status update. A portal restart cleared it, and it did not recur. If an add hangs with nothing in the CET log, restart the portal.

Create the object before deploying the CET, or redeploy the CET afterwards, so its `c_<object>.everything` scope is registered. See `rules/object-actions-catalog.md`.

#### Calling Back Into Liferay From the CX

Verified on 2026.Q3:

- **The inbound token acts as whoever triggered the action.** For an entry created by an anonymous visitor (a public form), that is **Guest** (`default@liferay.com`, `grant_type` `AUTHORIZATION_CODE_PKCE`, 600 s, the user-agent app's scopes). With it, reading the triggering entry, reading Accounts and PATCHing anything all return `403`. Propagating the inbound token only works when the trigger comes from a user who has the rights.
- **When the trigger can be anonymous, give the CX its own identity:** an `oAuthApplicationHeadlessServer` entry used with `grant_type=client_credentials`, next to the `oAuthApplicationUserAgent` that the `objectAction` entry still requires. Credentials, rotation on redeploy and scopes: `setup-oauth` → "A Microservice With Its Own Identity".
- **One CX can serve several objects.** The same `function#<cet-key>` can be attached to actions on several custom objects and on a system object such as `AccountEntry`, with no extra YAML. Tell the callers apart by the payload's top-level `objectDefinitionId`, not by the DTO key: a custom object sends `objectEntryDTO<ShortName>`, but `AccountEntry` sends `modelDTOAccount`, `originalDTOAccount`, `modelAccountEntry`, `originalAccountEntry` and `extendedProperties`, with no `objectEntryDTO*` key.
- **`allowedObjectDefinitionNames` (optional, on the `objectAction` entry) takes internal names only:** `C_<ShortName>` for a custom object, the system name (`AccountEntry`) for a system object. A short name (`Lead`) blocks every object, including the listed one. The restriction is checked only when the action runs: creating an action on an object outside the list still returns `200`, the run fails with `ObjectActionExecutorKeyException ... is not allowed for object definition C_<Name>` in `liferay.<date>.log`, and the action's status can still read "success". Leave the key out unless you need it, and check the log rather than the status.
- **Setting a child's relationship field fires the child's `onAfterUpdate`**, but nothing on the parent (even though the parent's Aggregation count changes) nor on the parent's other children. Recompute derived values from the child's own action.

### Approval Workflow

Use this when a reviewer approves or rejects each entry. Everything in this section was verified end to end on a 2026.Q2 bundle.

**Do not link the stock Single Approver.** Its `reject` transition goes to an `update` task that sets the entry back to Pending for the author to revise, so a rejected entry never leaves Pending, and a Guest submitter has no account to act on `update` with. **Do not write a Groovy script action either.** Deploying one fails with `KaleoDefinitionValidationException.NotAllowedScriptLanguage` (`Groovy is not allowed`).

Set the status with declarative `<status>` actions on two terminal states instead:

```xml
<?xml version="1.0"?>
<workflow-definition xmlns="urn:liferay.com:liferay-workflow_7.4.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="urn:liferay.com:liferay-workflow_7.4.0 http://www.liferay.com/dtd/liferay-workflow-definition_7_4_0.xsd">
	<name><Workflow Name></name>
	<version>1</version>
	<state>
		<name>created</name>
		<initial>true</initial>
		<transitions>
			<transition>
				<name>review</name>
				<target>review</target>
				<default>true</default>
			</transition>
		</transitions>
	</state>
	<task>
		<name>review</name>
		<assignments>
			<roles>
				<role>
					<role-type>regular</role-type>
					<name>Administrator</name>
				</role>
			</roles>
		</assignments>
		<transitions>
			<transition>
				<name>approve</name>
				<target>approved</target>
				<default>true</default>
			</transition>
			<transition>
				<name>reject</name>
				<target>rejected</target>
				<default>false</default>
			</transition>
		</transitions>
	</task>
	<state>
		<name>approved</name>
		<actions>
			<action>
				<name>approve</name>
				<status>0</status>
				<execution-type>onEntry</execution-type>
			</action>
		</actions>
	</state>
	<state>
		<name>rejected</name>
		<actions>
			<action>
				<name>reject</name>
				<status>4</status>
				<execution-type>onEntry</execution-type>
			</action>
		</actions>
	</state>
</workflow-definition>
```

The entry's system `status` follows the workflow: `1` pending on submission, `0` approved, `4` denied. There is no separate "rejected" code. A rejected entry reads `{"code": 4, "label": "denied"}`.

#### Deploy and Link Over REST

Deploy the definition, building the body with `jq` so the XML is escaped:

```bash
jq \
	--arg content "$(cat <workflow>.xml)" \
	--null-input \
	'{active: true, content: $content, name: "<Workflow Name>", title: "<Workflow Name>"}' \
	| curl \
		--data @- \
		--header "Content-Type: application/json" \
		--request POST \
		--silent \
		--url "http://localhost:${PORT}/o/headless-admin-workflow/v1.0/workflow-definitions/deploy" \
		--user "test@liferay.com:test"
```

Link it to the object with the returned `id`:

```bash
curl \
	--data '{
		"className": "<object definition className>",
		"groupId": 0,
		"workflowDefinitionName": "<Workflow Name>"
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-workflow/v1.0/workflow-definitions/<workflow-definition-id>/workflow-definition-links" \
	--user "test@liferay.com:test"
```

- `className` is the object definition's `className` field (`com.liferay.object.model.ObjectDefinition#<code>`), not its `name`. Read it with `GET /o/object-admin/v1.0/object-definitions/<id>`.
- `groupId` is `0` for a company scoped object. A link at the site's group ID is accepted and then never applies to entries posted to `/o/c/<plural>`.
- A link cannot be deleted (`405`), and there is no top level `GET /workflow-definition-links` (`404`). List a definition's links with `GET /workflow-definitions/<id>/workflow-definition-links`, and repoint one with `PUT /workflow-definition-links/by-external-reference-code/<erc>`.

Confirm by posting an entry: the response `status.label` is `pending`.

**Or set the link on the object definition itself.** A full `PUT` of `/o/object-admin/v1.0/object-definitions/by-external-reference-code/<ERC>` with `"workflowDefinitionLinks": [{"workflowDefinitionName": "<Workflow Name>"}]` writes the same link: a link made through `workflow-definition-links` reads back in that array. A `PATCH` with the array returns `200` and changes nothing. On 2026.Q1 and 2026.Q2 the array exists only while `LPD-17564` is on; without the flag the DTO has no such field and the `PUT` drops it silently. From 2026.Q3 it needs no flag. Verified on 2026.Q2.12 and 2026.Q3.8.

#### In a Site Initializer

`site-initializer/workflow-definitions/<name>/` holds `workflow-definition.json` (the DTO without `content`) and `workflow-definition.xml`, and deploying the definition from the tree works. **Linking it from the tree does not.** A `workflow-definition.properties.json` naming the object creates a link whose `className` is `ObjectDefinition#<numeric id>`, while the object's real class name is a generated code such as `ObjectDefinition#A1J1` (`_getUniqueClassName` in `ObjectDefinitionLocalServiceImpl`). The link exists, matches nothing, and entries never enter the workflow. On 2026.Q2, the same object carried a `#34825` link from the tree and the working `#A1J1` link from REST.

**From a site initializer, the object definition's own link works on a create only.** Ship the definition as `workflow-definitions/<dir>/workflow-definition.json` (`name`, `title`, `title_i18n`, `active`) beside `workflow-definition.xml`, and put the `workflowDefinitionLinks` array in the object's `object-definitions/<Name>.json`. A fresh install creates the object already bound. On an object that **already exists**, the initializer's update copies only scalar properties onto the stored definition, so the array is silently dropped and the object stays unbound, with no log line: the same limitation as new fields. Verified on 2026.Q2.7, both ways. Two more conditions:

- On 2026.Q1 and 2026.Q2, the array is honoured only while `LPD-17564` is on (`ObjectDefinitionLocalServiceImpl._addOrUpdateWorkflowDefinitionLinks` returns early otherwise). From 2026.Q3 the flag and the check are gone.
- A link written any other way should survive later initializer runs, because the update starts from the stored definition, links included. This was read from source, not re-measured.

So always link over REST after every provision, with the object's actual `className`, as in "Deploy and Link Over REST" above. Do not take an existing link as proof: compare its `className` with the object definition's `className`.

#### Approve or Reject as the Administrator

```bash
WORKFLOW="http://localhost:${PORT}/o/headless-admin-workflow/v1.0"

TASK_ID=$(curl \
	--silent \
	--url "${WORKFLOW}/workflow-tasks/assigned-to-my-roles?pageSize=200" \
	--user "test@liferay.com:test" \
	| jq --argjson id <entry-id> '.items[] | select(.objectReviewed.id == $id) | .id')

curl \
	--data '{}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "${WORKFLOW}/workflow-tasks/${TASK_ID}/assign-to-me" \
	--user "test@liferay.com:test"

curl \
	--data '{"transitionName": "approve"}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "${WORKFLOW}/workflow-tasks/${TASK_ID}/change-transition" \
	--user "test@liferay.com:test"
```

- Find the task in `assigned-to-my-roles`, not through `/workflow-instances`. An instance started by a Guest submission is missing from `GET /workflow-instances`, even though its task is in the Administrator queue.
- `assign-to-me` needs the JSON `{}` body. The call returns `415` without it, and transitioning an unclaimed task returns `403`.
- The body field is `transitionName`. `{"name": ...}` returns `400`.
- Use `"reject"` for the other outcome. The entry's status changes within a second.

Whether an entry ends `approved` or `denied` is the definition's choice, not the engine's. A terminal state with an `onEntry` `<status>` action (`0` approved, `4` denied) sets the entry's system `status`. This was measured on 2026.Q2.7, so give every terminal state such an action. Single Approver's own reject path does **not** end denied: it goes to an `update` task assigned back to the submitter, which sets `4` and then `1`, so the entry ends **pending**.

**Copying Single Approver: four traps.**

- **The review notice fires twice.** Its `execution-type` is `onAssignment`, which also fires when a reviewer claims a pooled task (`assign-to-me`) just before deciding, so every decision re-sends "awaiting your review". Use `onEntry`: one notice, on entering the task (measured).
- **A guest submitter breaks the copy.** The `update` task and the completion notice go to `<user />`, the submitter, and for entries added as the guest user that is nobody. Drop them, and end reject in a denied state.
- **A guest's entry starts no workflow, silently.** DXP starts it in a transaction-commit callback, after the add's service call has unwound. The Service Access Policy, which only checks service depth 1, then judges `KaleoInstanceLocalService#addKaleoInstance` as the request's first call and refuses it for a guest. The log shows `WorkflowException: java.lang.SecurityException` from `AccessControlAdvisorImpl`, and the entry stays `pending` with no instance and no task. This hits a guest `POST /o/c/...` and any guest JAX-RS endpoint that adds entries alike; a signed-in caller is admitted by `SYSTEM_USER_PASSWORD`, so an admin test passes. Measured on 2026.Q2. There is no supported workaround yet: do not attach a workflow to an object that guests submit to.
- **An account role resolves to nobody on a company-scoped entry.** Its workflow instance belongs to the Global group, and `AccountRoleGroupAwareRoleValidator` drops an account role outside the account's own group, so neither the definition nor a selector can hand a task to "the reviewers of this account" as a role. A selector returns the holders as **users** instead: `UserGroupRoleLocalService.getUserGroupRolesByGroupAndRole(accountEntry.getAccountEntryGroupId(), accountRole.getRoleId())`, filtered to active users who are still members of the account (verified on 2026.Q2.7 with a shared account role). Account restriction then gives those users their read of the entry with no further grant.
- **Per-entry routing needs code, and the script gate refuses `java`.** A definition whose `<scripted-assignment>` (or any script) uses `java` or `groovy` is refused at deploy (`400 NotAllowedScriptLanguage`) unless script management is on, and that setting is off by default and system-wide. A language name in the `function#<name>` form passes the validator and carries no script. Register a `com.liferay.portal.workflow.kaleo.runtime.assignment.ScriptingAssigneeSelector` with `scripting.language=function#<name>`. `getAssignees` returns `{"users": List<User>}` or `{"roles": List<Role>}`; roles win when both are present, and a map with neither key NPEs. The entry is `workflowContext.get("entryClassPK")`. Measured on 2026.Q2.7 at deploy and at runtime. It is not a documented extension point, so re-check it on an upgrade. A company-scoped entry's instance belongs to the Global group, where a site role resolves to nobody: route to regular roles or users.

**Finding a guest's task over REST.** `GET /o/headless-admin-workflow/v1.0/workflow-instances?assetClassName=&assetPrimaryKey=` lists only the instances **the caller started**, so an administrator finds their own entries and never a guest's. Find the task through `/workflow-tasks/assigned-to-my-roles`, `/workflow-tasks/assigned-to-user?assigneeId=` or `/workflow-tasks/assigned-to-me`, matching `objectReviewed.id`. A user assignee needs no `VIEW` on the entry to see their task, but the task shows only the entry's title, never its fields.

**Deciding tasks from a non-admin page.** headless-admin-workflow works from a signed-in user's own session (`Liferay.Util.fetch`), with the same answers as basic auth, and its own checks are enough of a gate (measured on 2026.Q2.7):

- **Outsiders are refused, with `400`, not `403`.** A user outside a task's pool gets `400 MustHavePermission` on `GET /workflow-tasks/{id}`, `assign-to-me` and `change-transition` alike. Do not reason from `WorkflowTaskManagerImpl` alone: `assignWorkflowTaskToUser` checks only that the caller claims for themself and `completeWorkflowTask` only the first assignee, which reads like "anyone can claim any task". The gate is the workflow-context read each resource method makes first (`getWorkflowTask` → `KaleoTaskInstanceTokenService` → `WorkflowTaskPermission`: a notifiable or assignable user or role; administrators pass).
- **List with both endpoints and filter.** `assigned-to-me` holds the tasks assigned to the user, alone or pooled with other users, and **completed ones too** (`completed: true`); `assigned-to-my-roles` holds role-assigned ones. Filter on `completed == false` and `workflowDefinitionName`.
- **A pooled task needs a claim, and the claim is exclusive.** With several user assignees (a selector returning two users), nobody can `change-transition` until they `assign-to-me` (`403`/`400`), and once one claims it the others no longer list, read or claim it. A task assigned to one user is decided without a claim. So claim only at the moment of deciding, never as a separate "take it" step that can strand the task.
- **A claim does not re-send an `onEntry` notice.**
- **A task shows only `objectReviewed.id` and the entry's title.** Read the entry itself through its own API with the same session; account restriction or entry permissions decide what each reviewer sees.

**A link in a Kaleo notice: mail only.** `EmailNotificationSender` sends the rendered template as HTML (`MailMessage(…, true)`), and `WorkflowTaskUserNotificationHandler` HTML-escapes it for the bell, so one notice with an `<a>` shows its tags as text in the portal. Use two `<notification>` elements, an `email` one with the link and a `user-notification` one without. The template sees the workflow context, whose `serviceContext` is the one the entry was added with: a module's bare `new ServiceContext()` and Object REST's (`ServiceContextUtil.createServiceContext`) carry no portal URL, so set `serviceContext.setPortalURL(PortalUtil.getPortalURL(request))` in the module that adds the entry and guard the link with `<#if serviceContext?? && serviceContext.getPortalURL()?has_content>`. A changed definition re-fired from a site initializer deploys as a new version, and an object linked by name follows it.

**Running module code on a decision, without Groovy.** Verified on 2026.Q2.7:

- **Hook the target state, not the transition.** A 7.4 `<transition>` holds only `name`, `target` and `default`; put an `onEntry` `<action>` in the state the transition leads to.
- **The action takes the selector's `function#` route.** Give it `<script-language>function#<name></script-language>` (the `<script>` element may just name the class) and register a `com.liferay.portal.workflow.kaleo.runtime.action.executor.ActionExecutor` whose `getActionExecutorKey()` returns `function#<name>`. `ActionExecutorManagerImpl` finds an action's executor by its script language, and the validator refuses only `groovy` and `java`, so the definition deploys with script management off and nothing is scripted. Keys starting with `function` also appear in the workflow designer's function list.
- **A status listener cannot tell two end states apart.** `ObjectEntryWorkflowHandler.updateStatus` receives only the status, and reject and discard both end at `4`. When the outcomes differ (mail the rejected, not the discarded), only a per-state action knows which one ran.
- **It runs after the decision commits, not inside it.** `change-transition` completes the task and registers the walk as a commit callback; `GraphWalkerPortalExecutor` then runs it on a single executor thread, each step in its own `REQUIRES_NEW` transaction, as the ServiceContext user (the reviewer), with remote access off. Local services work there without passing the access policy.
- **A failing action never undoes the decision.** `KaleoActionExecutorImpl` catches and logs whatever an executor throws, and whatever it wrote before throwing commits with the step. Write only what must commit with the state, and send mail, or delete the entry, from `TransactionCommitCallbackUtil.registerCallback` inside the action, which runs when that step commits. Deleting the entry also deletes its workflow instance and its tasks, so never do it while the walk is still on it, and expect the decided task to leave the decider's `assigned-to-me`.
- **The workflow context's `serviceContext` lasts to the end state.** A portal URL set on the add's ServiceContext (above) is still there when the end state's action runs, so a link mailed then can use it. An entry added over Object REST carries none, and the change-transition's own ServiceContext has none either: keep a fallback.

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

### Seeding Without Firing an Action

To load data without firing an `onAfterAdd` action (setup data that should send no notifications), switch the action off around the creates and back on in a `finally`: `PATCH /o/object-admin/v1.0/object-actions/{id}` with `{"active": false}`, then `{"active": true}`. The PATCH keeps `parameters` (a notification action's `notificationTemplateId` survived both, verified on 2026.Q3.5). Two cautions:

- The response's `actions` hrefs carry the object **definition** id, not the action id (`/object-actions/<definitionId>`). Address the action by the `id` you read from `GET …/object-definitions/{id}/object-actions`, never by those hrefs.
- An update fires no `onAfterAdd`, so toggle only when there is something to create. If the script dies between the two PATCHes, the action stays off: have a check (or the site initializer's next run, which re-applies `"active": true` from the tree) catch it.

A user-notification (bell) action sends each created entry's bell within about a second. The recipient sees it in `GET /o/headless-user-notification/v1.0/my-user-notifications`, and that API has no DELETE, so a test entry's bell stays in the recipient's list.

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

**From a Client Extension**, which can only write over REST, guard the write-back instead: compute the target value, compare it with the entry's current value, and PATCH only if they differ. The re-triggered call then sees the value unchanged and stops. Verified on 2026.Q3.2: one write-back, then the loop ended.

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