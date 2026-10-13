---

description: Set up the Liferay MCP server, manage CLI session restart ordering, and diagnose MCP specific failure modes (transport version switch, the 2026.Q3 enable switch and tool sets, connection lifecycle, auth split, 204 false alarms, unencoded query strings, token limit exceeded). Use when the user asks to enable or set up MCP, when an MCP tool call returns errors, or before performing content/page/object operations where MCP is preferred over raw REST.
name: mcp-server

---

# MCP Server

**MCP (Model Context Protocol)** is the AI agent integration standard that lets AI tools call Liferay APIs directly without manual REST auth setup.

> Available on recent DXP quarterly releases (gated by feature flag `LPD-63311`, marked `beta`). Verify against your DXP version before relying on it.

The server changed shape twice: at 2026.Q1 and at 2026.Q3. Find the row for your release, read from `liferay.workspace.product` in `gradle.properties`, before following anything below:

| DXP | Endpoint | Transport | Enabled By | Tools |
| --- | --- | --- | --- | --- |
| 2025.Q4 | `/o/mcp/sse` | SSE | `LPD-63311` | `call-http-endpoint`, `get-openapi`, `get-openapis` |
| 2026.Q1, 2026.Q2 | `/o/mcp` | Streamable HTTP | `LPD-63311` | `call-http-endpoint`, `get-openapi`, `get-openapis` |
| 2026.Q3 | `/o/mcp` | Streamable HTTP | `LPD-63311` **and** `MCPServerConfiguration` `enabled` | Tool sets: `getToolSetsPage`, `getToolSetToolSetNameToolSummariesPage`, `getToolSetToolSetNameTool`, `postToolSetToolSetNameToolInvoke` |

2026.Q2 has the 2026.Q1 shape. Verified on 2026.Q2.12: `/o/mcp` answered with the flag alone, the tools were `call-http-endpoint`, `get-openapi`, and `get-openapis`, and the credential, `204`, and query string encoding behavior below all matched 2026.Q1. Sections that apply to one shape only say so in their heading.

## When to Invoke

- "Enable MCP", "set up the MCP server"
- An MCP tool call returns errors
- Before content, page, or object operations where MCP is preferred over raw REST

## Setup

### Audit the Current State First

MCP needs three independent pieces, four on 2026.Q3, and having one says nothing about the others. An MCP entry in the agent's server list is only the client half — the portal does not serve `/o/mcp` until `LPD-63311` is enabled on its side. Check every row before deciding what to do, and never mark MCP as set up from any single row:

| Piece | How to Check | If Missing |
| --- | --- | --- |
| Portal feature flag | Look for `feature.flag.LPD-63311=true` in the sources listed under "Verifying State" in `rules/feature-flags-catalog.md` — for Tomcat that includes `bundles/portal-ext.properties`, the copy the running portal actually reads. For Docker, check the env var or `configs/docker` per `skills/feature-flags/SKILL.md`. When the server is up, confirm with the probe below. | Enable the Feature Flag (below) |
| MCP server enabled (2026.Q3 and later) | Look for `bundles/osgi/configs/com.liferay.mcp.server.rest.internal.configuration.MCPServerConfiguration.config` containing `enabled=B"true"`. When the server is up, confirm with the probe below. | Enable the MCP Server Configuration (below) |
| Client configuration | Read the agent's MCP config for a Liferay entry. Confirm the URL matches the DXP version (see Endpoint URL by DXP Version) and the transport is Streamable HTTP on 2026.Q1 and later. | Add or fix the entry, then Restart the CLI Session |
| Loaded into the session | The entry appears in the agent's MCP server list. Before the portal is running, a disconnected or failed status is expected. | Restart the CLI Session |

Report the state of each row to the user, then do only the steps for rows that are missing. When the client half is already correct and loaded, no CLI restart is needed — only the flag and a portal restart.

When the portal is running, probe the endpoint directly. This is the only check that proves the flag is in effect, because a flag in a file the portal never read looks identical to one that took. The probe below targets the Streamable HTTP endpoint of 2026.Q1 and later. On 2025.Q4 the endpoint is `/o/mcp/sse` (see Endpoint URL by DXP Version), which this probe does not cover; rely on the client's connection test there.

```bash
curl \
	--data '{"id": 1, "jsonrpc": "2.0", "method": "initialize", "params": {"capabilities": {}, "clientInfo": {"name": "probe", "version": "0"}, "protocolVersion": "2025-03-26"}}' \
	--header "Accept: application/json, text/event-stream" \
	--header "Content-Type: application/json" \
	--output /dev/null \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/mcp" \
	--user "test@liferay.com:test" \
	--write-out "%{http_code}\n"
```

A `200` means the portal is serving MCP. A `404` means nothing is registered at that path: the flag is not in effect, the 2026.Q3 configuration is off, or the URL does not match the DXP version. A `401` happens only on 2026.Q3 and later, and means the credentials were rejected.

On 2026.Q1 and 2026.Q2, the probe says nothing about credentials. Verified on 2026.Q1 and 2026.Q2 with valid credentials, wrong credentials, and none: every call returned `200` with the flag on and `404` with it off. Connecting and listing tools also succeed with any credentials. Only a tool call checks them: `call-http-endpoint` on `/headless-admin-user/v1.0/my-user-account` returns the user with valid credentials, and `Status code: 401` (wrong) or `403` (none) inside a `200` response otherwise. Verified on 2026.Q1 and 2026.Q2.

### Enable the Feature Flag

Enable `LPD-63311` in the active configuration. Use `skills/feature-flags/SKILL.md` for the per environment mechanism (Tomcat / Docker prebuilt / Docker custom) and the env var encoding scheme.

### DXP 2026.Q3+: Enable the MCP Server Configuration

On 2026.Q3, the flag is not enough. The server also reads `MCPServerConfiguration`, an instance scoped configuration whose `enabled` attribute defaults to `false`, and `/o/mcp` returns `404` until it is on. Nothing in the log says so. Verified on 2026.Q3 with `LPD-63311` already in effect: `404` before the file below, `200` within seconds after it.

Write it as an OSGi configuration file. File install picks it up while the portal runs, so no restart is needed:

```bash
printf \
	"enabled=B\"true\"\n" \
	> bundles/osgi/configs/com.liferay.mcp.server.rest.internal.configuration.MCPServerConfiguration.config
```

A file written only to `bundles/` is lost when the bundle is recreated. To keep it, put the same file in `configs/local/osgi/configs`. `blade server init` copies it into `bundles/osgi/configs`, verified on 2026.Q3. The workspace Docker image reads the same directory: the generated `build/docker/Dockerfile` sets `LIFERAY_WORKSPACE_ENVIRONMENT=local`, and its setup script copies `configs/local/*` into Liferay home. That was read from the files `blade gw createDockerfile` generates, not run in a container.

### Restart the CLI Session

Most CLI based agents load MCP configuration at startup only and cannot pick up new settings midsession. Prompt the user to exit and restart their agent session **before starting the Liferay server** — so the restart costs nothing while the server is still down.

The CLI session restart kills any running Liferay server. If `blade server start` runs before the restart, the restart will tear the server down — forcing a second cold boot cycle. Liferay cold boot is several minutes. Complete the CLI restart before running `blade server start`.

### Endpoint URL by DXP Version

- DXP 2025.Q4: `http://localhost:${PORT}/o/mcp/sse`
- DXP 2026.Q1 and later: `http://localhost:${PORT}/o/mcp`, unchanged on 2026.Q3

### Authentication

Basic auth with base64-encoded credentials. Default: `test@liferay.com` / `test`. Update in your MCP client config if credentials differ.

### Connection Check

Make one tool call. The client's built in connection test is not enough, because on 2026.Q1 and 2026.Q2 connecting and listing tools succeed with any credentials.

- **2026.Q1 and 2026.Q2**: `call-http-endpoint` with `GET /headless-admin-user/v1.0/my-user-account`. If the result reports `Status code: 401` or `403`, stop and ask the user for updated credentials — do not edit these rule files.
- **2026.Q3**: `postToolSetToolSetNameToolInvoke` with `toolSetName` `headless-admin-user-v1.0`, `toolName` `getMyUserAccount`, and `body` `{}`. It returns the user. Rejected credentials fail every request with HTTP `401`, including the connection itself, so a client that connected and listed tools is already past this check. Verified on 2026.Q3.

## MCP First Workflow

When MCP is available, always attempt operations via MCP before reaching for headless REST. MCP bypasses the complex auth dance (session cookies, `p_auth`, SAP policies, Basic Auth verifier).

**Fallback policy**: only fall back to REST APIs if MCP has been configured correctly and is still returning errors. "Not yet configured" is not a valid fallback condition — configure it first. When falling back, document why before pivoting to REST — this prevents silently sliding into REST auth debugging.

## Patterns and Gotchas

### DXP 2026.Q3+: Tool Sets Replace `call-http-endpoint`

2026.Q3 drops `call-http-endpoint`, `get-openapi`, and `get-openapis`. A call to one fails with `Unknown tool: invalid_tool_name` and `Tool not found: call-http-endpoint`. Every headless API is instead a **tool set**, and each endpoint is a tool inside it. Four meta tools walk that tree, and each one's output feeds the next:

| Step | Tool | Arguments | Returns |
| --- | --- | --- | --- |
| 1 | `getToolSetsPage` | none | Every tool set, with `name` and `description` |
| 2 | `getToolSetToolSetNameToolSummariesPage` | `toolSetName` | Every tool in the set, with `name` and `description` |
| 3 | `getToolSetToolSetNameTool` | `toolSetName`, `toolName` | The tool's `inputSchema` |
| 4 | `postToolSetToolSetNameToolInvoke` | `toolSetName`, `toolName`, `body` | The endpoint's response body |

Tool set names are the REST module name plus its version: `headless-admin-user-v1.0`, `object-admin-v1.0`, `headless-admin-site-v1.0`, `headless-delivery-v1.0`. The guess `admin-user-v1.0` fails with `No tool-set was found with name "admin-user-v1.0"`, so list the sets once rather than guess. Tool names are the OpenAPI operation IDs, such as `getMyUserAccount`.

Step 3 replaces `get-openapi` for one operation: it returns that operation's parameters, and lists every legal value for `fields`. Do not skip it before step 4.

The `body` argument of step 4 is the target tool's whole input map, not the request payload. Path and query parameters sit at its top level. When the target's `inputSchema` has its own `body` property, the request payload goes there, nested one level down. Adding a field to an object definition:

```json
{
	"body": {
		"body": {
			"DBType": "String",
			"businessType": "Text",
			"label": {
				"en_US": "Note"
			},
			"name": "note",
			"required": false
		},
		"objectDefinitionId": "37911"
	},
	"toolName": "postObjectDefinitionObjectField",
	"toolSetName": "object-admin-v1.0"
}
```

Flattening the payload into the outer `body` is rejected before the call is made:

```text
The "postObjectDefinitionObjectField" tool requires the request payload nested under a "body" property.
```

A tool with no parameters still takes `"body": {}`; the argument is required. Query parameters are plain values: `{"fields": ["id", "name"], "filter": "name eq 'McpProbe'"}` works as written, with no URL encoding. An endpoint error comes back as text inside a successful result, such as `Status code: 500, Content: {...}`, and is logged at `ERROR` from `MCPServerServlet`.

So the 2026.Q1 and 2026.Q2 gotchas about encoding the query string and `204` errors do not apply. A `DELETE` returns the text `Status code: 204` with `isError: false`.

Tool set names come in three kinds:

| Kind | Example | Tool Names |
| --- | --- | --- |
| Headless REST module | `headless-admin-user-v1.0`, `object-admin-v1.0` | The OpenAPI operation ID, such as `getMyUserAccount` |
| Liferay's own objects | `data-masks`, `mcp-server-profiles` | The object entry operations, such as `getDataMasksPage` |
| `openapi` | `openapi` | Every operation in one set, prefixed with the module: `HeadlessCommerceAdminCatalog.v1.0.getProductGroupByExternalReferenceCode` |

The `openapi` set held 4,761 tools on 2026.Q3. Use a module's own set instead.

Verified on 2026.Q3: steps 1 to 4 for `getMyUserAccount`, and a draft object definition created, given a field, found by `filter`, and deleted, all through `postToolSetToolSetNameToolInvoke`.

### DXP 2026.Q3+: Responses Are Masked

MCP responses pass through data masks, and personal data is masked by default. On 2026.Q3, `getMyUserAccount` returned `"emailAddress": "[EMAIL_ADDRESS]"` through MCP, while the same account over REST returned `test@liferay.com`. Verified on 2026.Q3.

So a masked value in an MCP response is not stored data. Do not write it back, compare against it, or report it to the user as the real value. When the real value matters, read it over REST.

A fresh 2026.Q3 bundle ships nine system masks, all attached to the only MCP server profile, `default`:

| Mask | Replaced With |
| --- | --- |
| Email Address | `[EMAIL_ADDRESS]` |
| Phone Number | `[PHONE_NUMBER]` |
| Credit Card Number | `[CREDIT_CARD_NUMBER]` |
| Bank Account Number (IBAN) | `[BANK_ACCOUNT_NUMBER]` |
| National ID (BSN), (DNI and NIF), (SSN) | `[NATIONAL_ID]` |
| IPv4 Address | The `/24` subnet |
| IPv6 Address | The `/48` subnet |

Masks, profiles, and the links between them are object entries, listed with `getDataMasksPage` in `data-masks`, `getMCPServerProfilesPage` in `mcp-server-profiles`, and `getMCPServerProfileDataMasksPage` in `mcp-server-profile-data-masks`. Turning a mask off was not tried; it changes what every MCP client sees, so leave it to the user.

### DXP 2026.Q1+: Streamable HTTP Transport Required

The Liferay MCP server on DXP 2026.Q1 and later requires the **Streamable HTTP** transport. MCP clients configured to use SSE will show "Disconnected" even when the server is running and the endpoint URL is correct. Check your MCP client configuration and ensure it is using Streamable HTTP, not SSE.

### Session Restart Required After Any Config Change

CLI sessions load MCP server config once at startup. Any change — including initial setup, endpoint URL updates, or auth credential changes — will not be visible until you exit and restart the session. This applies whether the change was made before or during the session.

After restarting, verify the MCP server entry appears in your client's server list before starting Liferay. A disconnected or failed status is expected and acceptable at this point — the server is not running yet. If the entry is absent entirely, recheck the MCP configuration before proceeding.

If the server shows as connected but lists zero tools, check whether your agent provides a reconnect or refresh option for MCP servers — this can recover a dropped midsession connection without a full restart. This does **not** apply to newly added or changed server config — those always require a full restart.

### DXP 2026.Q1 and 2026.Q2: 204 No Content Responses Are False Alarms

The MCP `call-http-endpoint` tool throws `MCP error -32603: text must not be null` when an endpoint returns 204 No Content. This is a tool limitation, not an API failure — the operation most likely succeeded.

**Affected operations include**: publishing a page specification, checking out a CT collection, and any other endpoint that intentionally returns no response body.

Verified on 2026.Q1 and 2026.Q2.

**Workaround**: after a 204 MCP error, follow up with a GET to confirm the operation succeeded before assuming failure or retrying.

### DXP 2026.Q1 and 2026.Q2: Encode the Query String Yourself

`call-http-endpoint` sends `path` as written and does not URL encode it. An OData `filter` with spaces or quotes fails before it reaches the API:

```text
Invalid URI: http://localhost:8080/o/object-admin/v1.0/object-definitions?filter=name eq 'Event'
```

Liferay also logs each failure at `ERROR` from `MCPServerServlet`, so these failures show up when you scan the log for deployment errors.

Encode spaces as `%20` and single quotes as `%27`: `?filter=name%20eq%20%27Event%27`. Commas need no encoding, so `?fields=id,name` works as written. Verified on 2026.Q1, and 2026.Q2 returns the same `Invalid URI` error.

### DXP 2026.Q1 and 2026.Q2: Endpoint Discovery With `get-openapi`

On 2026.Q3, use `getToolSetToolSetNameTool` instead (see "Tool Sets Replace `call-http-endpoint`" above). The reasons to check a schema first are the same.

`get-openapi` enumerates Liferay's headless API surface from the live instance's OpenAPI specs. Use it before answering "is there an endpoint for X?" questions, before debugging a call whose expected behavior is uncertain, or before composing any POST/PATCH payload. The live specs are the source of truth — Liferay's hosted documentation is known to lag, and GET response structure does not equal POST/PATCH request structure.

The spec has known gaps: several fields required at runtime are marked optional (see `manage-pages` → "`headless-admin-site` Schema Gotchas" for the documented exceptions in that API). Treat the spec as the discovery surface, not the final word, and cross check against the per skill gotchas when working with `headless-admin-site`, `headless-delivery`, or `object-admin`.

### DXP 2026.Q1 and 2026.Q2: Large OpenAPI Specs Exceed the Token Limit

`get-openapi` on large APIs (e.g., `headless-delivery`, `headless-admin-site`) exceeds the MCP tool's token limit. The tool saves the output to a temp file rather than returning it inline.

**Workaround**: use a targeted `grep` on the saved temp file to extract only the relevant schema sections rather than attempting to read the full output.

### `object-admin` Write Responses Are Unfiltered and Very Large

Verified on 2026.Q1 with `call-http-endpoint`, and on 2026.Q3 with `postToolSetToolSetNameToolInvoke`: creating an empty draft definition echoed 6.5 KB.

`call-http-endpoint` returns the response verbatim — there is no `fields` projection and no way to trim it from the call. On `object-admin`, every definition create, update, and **publish** echoes the complete `ObjectDefinition`: all six system fields (`creator`, `createDate`, `externalReferenceCode`, `id`, `modifiedDate`, `status`), every custom field with its full settings, plus `actions`, `objectRelationships`, and the embedded relationship `objectField`. A single publish on a modest object runs several hundred lines, and a build of two related objects can spend more context on echoed responses than on the actual work.

The `fields` query parameter helps on **GET** (`?fields=id,name,status`) but is ignored on POST bodies.

**Prefer MCP for reads and discovery; drop to `curl | jq` for `object-admin` writes**, projecting just the confirmation you need:

```bash
curl \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions/<id>/publish" \
	--user "test@liferay.com:test" \
	| jq '{id, name, active, status: .status.label, restContextPath}'
```

This is a context budget concern, not a correctness one — the MCP path works. It matters most in long multiobject builds, where the flood arrives early and crowds out later steps.

### MCP Connection Lifecycle (Server Restart Drops the Connection)

The MCP client connection drops when the Liferay server stops, restarts, or crashes and does NOT autoreconnect. Recovery requires the user to manually trigger reconnect in their agent — the agent cannot trigger reconnection via Bash, MCP, or any other tool. After any Liferay restart cycle completes, prompt the user to reconnect the MCP server in their agent (each agent has its own reconnect command; e.g., a slash command, a UI button, or a settings refresh) and wait for their reply before making MCP tool calls.

### MCP/REST Auth Split — Handshake Succeeds Without BasicAuth Verifier

The MCP connection handshake succeeds without `BasicAuthHeaderAuthVerifier` configured. Tools appear in the agent's tool list and the connection looks healthy. But on 2026.Q1 and 2026.Q2, `call-http-endpoint` invokes the same `/o/*` endpoints as direct REST and is subject to the same verifier rules — without it, every tool call returns 403. If first login bootstrap is confirmed complete and 403s persist, add the BasicAuth verifier (dev only) per `skills/workspace-init/SKILL.md` → "Tomcat" setup.

### First Login 403 Trap (Fresh Liferay Instances)

If MCP connects and tools appear but every tool call (`call-http-endpoint` on 2026.Q1 and 2026.Q2) returns 403, the default admin has not completed first login bootstrap. On a fresh instance, `headless-delivery` endpoints return JSON 404 while admin APIs return XML 403 — both are auth failures, not routing or SAP issues. Complete first login bootstrap per `skills/workspace-init/SKILL.md` → "First Login Bootstrap" before retrying. If the bundle has already booted, syncing `portal-ext.properties` now will not help — the flags are in the database and only the manual login clears them.

## Success Signal

- The probe returns `200`.
- The MCP server appears in the client's server list and is connected.
- The Connection Check tool call for your release returns the signed in user.