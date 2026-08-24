---

description: Create and configure the OAuth 2.0 application that a client extension needs to call Liferay Headless APIs, and the browser side client a fragment or widget needs to call a CET. Use when a CET of type objectAction, workflowAction, notificationType, batchEngineDataImportTaskExecutor, siteInitializer, or any backend CET requires authenticated API access, or when browser code must call a client extension with a token. Called by scaffold-client-extension automatically when the CET type requires OAuth.
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

### Call the CET From Browser Code

The steps above authenticate Liferay calling **into** a CET, or a CET calling back into Liferay. Browser code calling **out** to a CET is a third case, and `Liferay.Util.fetch` does not cover it — that carries the user's session cookie, which the CET's own origin will not accept. Use `Liferay.OAuth2Client`, which exchanges the session for a token against the `oAuthApplicationUserAgent` entry and attaches it as a Bearer header.

The `oAuthApplicationUserAgent` entry must already exist; add it per the steps above. The name passed below is that entry's **top level key** in `client-extension.yaml`, the same value `oAuth2ApplicationExternalReferenceCode` takes.

```javascript
const client = Liferay.OAuth2Client.FromUserAgentApplication(
	'<workspace-id>-oauth'
);

const data = await client.fetch('/o/my-service/v1.0/things');
```

Three behaviors differ from `fetch` and each one fails in a way that does not name its cause:

- **There are two entry points with different signatures.** The global `Liferay.OAuth2Client.FromUserAgentApplication` shown above is **synchronous**. The identically named export from the `@liferay/oauth2-provider-web` module is **`async`** — it resolves the application profile over HTTP. Miss the `await` on the module form and the next line calls `.fetch()` on a Promise.

- **`fetch` returns parsed JSON, not a `Response`.** On success it returns `response.json()` when the content type is JSON, and the `Response` only for everything else. Calling `.json()` on the result throws `TypeError`.

- **A failed call rejects.** There is no `response.ok` to branch on — a non-2xx rejects with the `Response`. Wrap in `try`/`catch`, or the failure surfaces as an unhandled rejection.

```javascript
try {
	const data = await client.fetch('/o/my-service/v1.0/things');
}
catch (response) {
	console.error(`Request failed: ${response.status}`);
}
```

The client also refuses any URL outside the application's registered `homePageURL`, so the redirect URIs on the OAuth entry have to include the portal origin the page is served from.

Source: `js/client/OAuth2Client.ts` and `js/client/liferay.ts` in `modules/apps/oauth2-provider/oauth2-provider-web`.

## Success Signal

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

## Common Errors and Fixes

| Symptom | Check |
| --- | --- |
| 401 on API call from CET | OAuth entry deployed and `ACTIVE`; token scope covers the endpoint |
| 403 on specific resource | Scope too narrow; add the resource's scope string from `rules/oauth-scopes.md` |
| Application not in Control Panel | Bundle not `ACTIVE`; run `diag <id>` in Gogo shell |
| ERC not resolved | ERC field in yaml must match the top level key of the OAuth entry, not its `name` field |