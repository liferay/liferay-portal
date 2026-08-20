---

description: Set up the Liferay MCP server, manage CLI session restart ordering, and diagnose MCP specific failure modes (transport version switch, connection lifecycle, auth split, 204 false alarms, token limit exceeded). Use when the user asks to enable or set up MCP, when an MCP tool call returns errors, or before performing content/page/object operations where MCP is preferred over raw REST.
name: mcp-server

---

# Liferay MCP Server

**MCP (Model Context Protocol)** is the AI agent integration standard that lets AI tools call Liferay APIs directly without manual REST auth setup.

> Available on recent DXP quarterly releases (gated by feature flag `LPD-63311`, marked `beta`). Verify against your DXP version before relying on it.

## Setup

### Enable the Feature Flag

Enable `LPD-63311` in the active configuration. Use `skills/feature-flags/SKILL.md` for the per environment mechanism (Tomcat / Docker prebuilt / Docker custom) and the env var encoding scheme.

### Restart the CLI Session

Most CLI based agents load MCP configuration at startup only and cannot pick up new settings midsession. Prompt the user to exit and restart their agent session **before starting the Liferay server** — so the restart costs nothing while the server is still down.

The CLI session restart kills any running Liferay server. If `blade server start` runs before the restart, the restart will tear the server down — forcing a second cold boot cycle. Liferay cold boot is several minutes. Complete the CLI restart before running `blade server start`.

### Endpoint URL by DXP Version

- DXP 2025.Q4: `http://localhost:${PORT}/o/mcp/sse`
- DXP 2026.Q1 and later: `http://localhost:${PORT}/o/mcp`

### Authentication

Basic auth with base64-encoded credentials. Default: `test@liferay.com` / `test`. Update in your MCP client config if credentials differ.

### Connection Check

Use your MCP client's built in connection test. If it returns 401/403, stop and ask the user for updated credentials — do not edit these rule files.

## MCP First Workflow

When MCP is available, always attempt operations via MCP before reaching for headless REST. MCP bypasses the complex auth dance (session cookies, `p_auth`, SAP policies, Basic Auth verifier).

**Fallback policy**: only fall back to REST APIs if MCP has been configured correctly and is still returning errors. "Not yet configured" is not a valid fallback condition — configure it first. When falling back, document why before pivoting to REST — this prevents silently sliding into REST auth debugging.

## Quirks and Workarounds

### DXP 2026.Q1+: Streamable HTTP Transport Required

The Liferay MCP server on DXP 2026.Q1 and later requires the **Streamable HTTP** transport. MCP clients configured to use SSE will show "Disconnected" even when the server is running and the endpoint URL is correct. Check your MCP client configuration and ensure it is using Streamable HTTP, not SSE.

### Session Restart Required After Any Config Change

CLI sessions load MCP server config once at startup. Any change — including initial setup, endpoint URL updates, or auth credential changes — will not be visible until you exit and restart the session. This applies whether the change was made before or during the session.

After restarting, verify the MCP server entry appears in your client's server list before starting Liferay. A disconnected or failed status is expected and acceptable at this point — the server is not running yet. If the entry is absent entirely, recheck the MCP configuration before proceeding.

If the server shows as connected but lists zero tools, check whether your agent provides a reconnect or refresh option for MCP servers — this can recover a dropped midsession connection without a full restart. This does **not** apply to newly added or changed server config — those always require a full restart.

### 204 No Content Responses Are False Alarms

The MCP `call-http-endpoint` tool throws `MCP error -32603: text must not be null` when an endpoint returns 204 No Content. This is a tool limitation, not an API failure — the operation most likely succeeded.

**Affected operations include**: publishing a page specification, checking out a CT collection, and any other endpoint that intentionally returns no response body.

**Workaround**: after a 204 MCP error, follow up with a GET to confirm the operation succeeded before assuming failure or retrying.

### Endpoint Discovery With `get-openapi`

`get-openapi` enumerates Liferay's headless API surface from the live instance's OpenAPI specs. Use it before answering "is there an endpoint for X?" questions, before debugging a call whose expected behavior is uncertain, or before composing any POST/PATCH payload. The live specs are the source of truth — Liferay's hosted documentation is known to lag, and GET response structure does not equal POST/PATCH request structure.

The spec has known gaps: several fields required at runtime are marked optional (see `manage-pages` → "`headless-admin-site` Schema Gotchas" for the documented exceptions in that API). Treat the spec as the discovery surface, not the final word, and cross check against the per skill gotchas when working with `headless-admin-site`, `headless-delivery`, or `object-admin`.

### Large OpenAPI Specs Exceed the Token Limit

`get-openapi` on large APIs (e.g., `headless-delivery`, `headless-admin-site`) exceeds the MCP tool's token limit. The tool saves the output to a temp file rather than returning it inline.

**Workaround**: use a targeted `grep` on the saved temp file to extract only the relevant schema sections rather than attempting to read the full output.

### MCP Connection Lifecycle (Server Restart Drops the Connection)

The MCP client connection drops when the Liferay server stops, restarts, or crashes and does NOT autoreconnect. Recovery requires the user to manually trigger reconnect in their agent — the agent cannot trigger reconnection via Bash, MCP, or any other tool. After any Liferay restart cycle completes, prompt the user to reconnect the MCP server in their agent (each agent has its own reconnect command; e.g., a slash command, a UI button, or a settings refresh) and wait for their reply before making MCP tool calls.

### MCP/REST Auth Split — Handshake Succeeds Without BasicAuth Verifier

The MCP connection handshake succeeds without `BasicAuthHeaderAuthVerifier` configured. Tools appear in the agent's tool list and the connection looks healthy. But `call-http-endpoint` invokes the same `/o/*` endpoints as direct REST and is subject to the same verifier rules — without it, every tool call returns 403. If first login bootstrap is confirmed complete and 403s persist, add the BasicAuth verifier (dev only) per `skills/workspace-init/SKILL.md` → "Tomcat" setup.

### First Login 403 Trap (Fresh Liferay Instances)

If MCP connects and tools appear but every `call-http-endpoint` returns 403, the default admin has not completed first login bootstrap. On a fresh instance, `headless-delivery` endpoints return JSON 404 while admin APIs return XML 403 — both are auth failures, not routing or SAP issues. Complete first login bootstrap per `skills/workspace-init/SKILL.md` → "First Login Bootstrap" before retrying.