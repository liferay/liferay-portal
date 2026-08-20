---

description: Create and configure the OAuth 2.0 application that a client extension needs to call Liferay Headless APIs. Use when a CET of type objectAction, workflowAction, notificationType, batchEngineDataImportTaskExecutor, siteInitializer, or any backend CET requires authenticated API access. Called by scaffold-client-extension automatically when the CET type requires OAuth.
name: setup-oauth

---

# Setup OAuth

Generate the companion OAuth application entry inside `client-extension.yaml` and verify the deployed application is registered in Liferay.

## When to Invoke

- `scaffold-client-extension` identifies a CET type that requires OAuth (see `rules/client-extension-types.md`)
- A CET is deployed but returns 401 or 403 on Liferay API calls
- The user says "set up OAuth", "configure OAuth for this extension"

## Workflow

### Identify the Required Scopes

Consult `rules/oauth-scopes.md` for the full scope table. Pick the minimum set that covers what the CET calls.

Examples:
- Object action that reads and writes entries: `Liferay.Headless.Admin.User.everything`, `Liferay.Headless.Object.everything`
- Site initializer that creates pages and content: `Liferay.Headless.Admin.Site.everything`, `Liferay.Headless.Admin.Content.everything`
- Batch data import only: `Liferay.Headless.Batch.Engine.everything`

### Add the OAuth Application Entry to `client-extension.yaml`

The OAuth companion type depends on the consuming CET — check `rules/client-extension-types.md` for the correct type and ERC field name for the CET you are wiring.

Microservice handler CETs (`objectAction`, `workflowAction`, `notificationType`, `objectEntryManager`, `objectValidationRule`) use `oAuthApplicationUserAgent`. The **top level key** of the OAuth entry — **not** its `name` field — is what goes in `oAuth2ApplicationExternalReferenceCode` on the CET.

Deploy time CETs (`siteInitializer`, `batch`) use `oAuthApplicationHeadlessServer`, referenced via the `oAuthApplicationHeadlessServer` field on the CET entry.

Replace `<workspace-id>` with the value of `id` in `client-extension.yaml`.

### Deploy

Run `deploy-and-verify` from the client extension root. Blade copies both entries to Liferay on deploy.

### Verify Registration

After deployment, confirm the OAuth application is registered:

```bash
curl \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-user/v1.0/my-user-account" \
	--user "test@liferay.com:test" \
	| jq '{id, name}'
```

Then check the OAuth application list in Control Panel → OAuth 2 Administration. The application name `<WorkspaceId> OAuth Application` should appear with status Active.

To verify the credentials are wired, check the Gogo shell:

```bash
telnet localhost 11311
lb | grep <workspace-id>
```

Both the OAuth application bundle and the CET bundle should show `ACTIVE`.

### Token Acquisition (for Manual Testing)

The deployed CET retrieves its token automatically via the Liferay OAuth2 API. For manual testing:

```bash
curl \
	--data "grant_type=client_credentials" \
	--silent \
	--url "http://localhost:${PORT}/o/oauth2/token" \
	--user "<clientId>:<clientSecret>" \
	| jq '{access_token, token_type, expires_in}'
```

The client ID and secret are displayed once in Control Panel → OAuth 2 Administration when the application is created. Retrieve them from there when needed.

### Troubleshoot

| Symptom | Check |
| --- | --- |
| 401 on API call from CET | OAuth entry deployed and `ACTIVE`; token scope covers the endpoint |
| 403 on specific resource | Scope too narrow; add the resource's scope string from `rules/oauth-scopes.md` |
| Application not in Control Panel | Bundle not `ACTIVE`; run `diag <id>` in Gogo shell |
| ERC not resolved | ERC field in yaml must match the top level key of the OAuth entry, not its `name` field |