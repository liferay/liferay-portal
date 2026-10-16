---

description: Create and configure the OAuth 2.0 application that a client extension needs to call Liferay Headless APIs, and the browser side client a fragment or widget needs to call a CET. Use when a CET of type objectAction, workflowAction, notificationType, siteInitializer, or any backend CET requires authenticated API access, or when browser code must call a client extension with a token. Called by scaffold-client-extension automatically when the CET type requires OAuth.
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
- Object action that reads and writes entries: `c_<name>.everything` for each object it calls
- Site initializer: `Liferay.Headless.Site.everything`
- Batch data import only: `Liferay.Headless.Batch.Engine.everything`

### Add the OAuth Application Entry to `client-extension.yaml`

The OAuth companion type depends on the consuming CET — check `rules/client-extension-types.md` for the correct type and ERC field name for the CET you are wiring.

Microservice handler CETs (`objectAction`, `workflowAction`, `notificationType`, `objectEntryManager`, `objectValidationRule`) use `oAuthApplicationUserAgent`. The **top level key** of the OAuth entry — **not** its `name` field — is what goes in `oAuth2ApplicationExternalReferenceCode` on the CET.

Deploy time CETs (`siteInitializer`, `batch`) use `oAuthApplicationHeadlessServer`, referenced via the `oAuthApplicationHeadlessServer` field on the CET entry.

### A Microservice With Its Own Identity

A microservice handler normally calls back into Liferay with the token Liferay sent it, which acts as whoever triggered the call. When that can be an anonymous visitor (an `objectAction` on an entry created by a public form), the token is Guest's and every callback returns `403` (`manage-object-logic` → "Calling Back Into Liferay From the CX"). Add an `oAuthApplicationHeadlessServer` entry to the same project and use it with `grant_type=client_credentials` for every callback. Keep the `oAuthApplicationUserAgent` entry: the handler CET still requires it as `oAuth2ApplicationExternalReferenceCode`.

```yaml
<workspace-id>-action-oauth-ua:
    .serviceAddress: localhost:3101
    .serviceScheme: http
    name: <WorkspaceId> Action OAuth (user agent)
    scopes:
        - Liferay.Headless.Object.everything
    type: oAuthApplicationUserAgent

<workspace-id>-action-oauth-hs:
    .serviceAddress: localhost:3101
    .serviceScheme: http
    name: <WorkspaceId> Action OAuth (headless server)
    scopes:
        - c_<objecta>.everything
        - c_<objectb>.everything
        - Liferay.Headless.Admin.User.everything.read
    type: oAuthApplicationHeadlessServer

<workspace-id>-action:
    name: <WorkspaceId> Action
    oAuth2ApplicationExternalReferenceCode: <workspace-id>-action-oauth-ua
    resourcePath: /object/action/<name>
    type: objectAction
```

Verified on 2026.Q3.2, gradle-plugins-workspace 17.1.5:

- **The token acts as `default-service-account`, which has the Administrator role.** Its scopes are the only thing that limits it, so list exactly what the microservice touches.
- **`Liferay.Headless.Object.everything` alone returns `403` on everything**, `/o/c/*` included. Grant `c_<objectname>.everything` for each object the microservice reads or writes (`<objectname>` is the definition's `name`, lowercased), plus `Liferay.Headless.Admin.User.everything.read` to read Accounts. With those, the generic object scope is not needed.
- **The token lasts 600 s** and carries exactly the YAML scopes.
- **The microservice reads its client ID and secret from files DXP writes on deploy**, and both change on every redeploy: see "Token Acquisition" below.
- **Verifying the inbound token: its JWT header has no `kid`** (`{"typ":"at+jwt","alg":"RS256"}`), while `/o/oauth2/jwks` publishes one key with `kid: "authServer"`. A verifier that looks the key up by `kid` finds nothing and rejects every call. With no `kid`, try each published key, then check `client_id` against `<ua-erc>.oauth2.user.agent.client.id` in the routes, re-read per request because it rotates too. The object action payload's `objectDefinitionId` is a **string** (`"38575"`). Verified on 2026.Q3.2.

Replace `<workspace-id>` with the value of `id` in `client-extension.yaml`.

### Deploy

Run `deploy-and-verify` from the client extension root. Blade copies both entries to Liferay on deploy.

### Token Acquisition (for Manual Testing)

Verified on 2026.Q3.2 for an `oAuthApplicationHeadlessServer` entry, whether it serves a `siteInitializer`/`batch` or a microservice that needs its own `client_credentials` identity:

- **Every deploy rotates both the client ID and the secret**, even one that changes nothing, and a token issued before it then gets `401`. Never copy them into source control, a committed `.env`, or a log line.
- **Self-hosted, they are files under `${liferay.home}/routes/default/<project-name>/`**, one per key, prefixed `<erc>.`: `oauth2.headless.server.client.id`, `oauth2.headless.server.client.secret`, `oauth2.token.uri` (`/o/oauth2/token`), plus `.audience`, `.scopes`, `.authorization.uri`, `.introspection.uri`, `.jwks.uri`, `.redirect.uris`, `.home.page.uri`. A user-agent entry gets `oauth2.user.agent.client.id`, `.audience`, `.scopes`, and no secret. The DXP domain and protocol are in `routes/default/dxp/` (`com.liferay.lxc.dxp.mainDomain`, `com.liferay.lxc.dxp.server.protocol`).
- **Nothing else exposes the secret.** The deployed `*.client-extension-config.json` holds none, and `GET /o/oauth2/application?externalReferenceCode=<erc>` returns only `client_id`, `homePageURL`, and `redirectURIs`.
- **In the microservice,** point `LIFERAY_ROUTES_CLIENT_EXTENSION` at `routes/default/<project-name>` and `LIFERAY_ROUTES_DXP` at `routes/default/dxp`. Read the files at each token request (`client_secret_post` to the token URI), not once at startup, and on a `401` re-read them and retry once. On LXC the same code works with `/etc/liferay/lxc/ext-init-metadata` and `/etc/liferay/lxc/dxp-metadata` (from source and docs, not tested live); Liferay PaaS documents an `[ERC]_OAUTH2_HEADLESS_SERVER_CLIENT_SECRET` environment variable instead.

For manual testing, read the current values without printing them:

```bash
ROUTES="bundles/routes/default/<project-name>"

curl \
	--data "grant_type=client_credentials" \
	--data-urlencode "client_id=$(cat "${ROUTES}/<erc>.oauth2.headless.server.client.id")" \
	--data-urlencode "client_secret=$(cat "${ROUTES}/<erc>.oauth2.headless.server.client.secret")" \
	--silent \
	--url "http://localhost:${PORT}/o/oauth2/token" \
	| jq '{token_type, expires_in, scope}'
```

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

The client also refuses any URL outside the application's registered `homePageURL`. And it mints a token only on an origin that one of the application's redirect URIs starts with; otherwise it rejects with `No redirectURI in … matching origin …` (`OAuth2Client.ts:83–90`). **Those URIs are not in the yaml.** A user-agent application gets one per virtual host of the instance, `<protocol>://<host>:<port>/o/oauth2/redirect`, computed when it is provisioned (`OAuth2ProviderApplicationUserAgentConfigurationFactory.java:66–73`). So a page served from a host the instance does not list gets no token, for example `127.0.0.1` on an instance whose only virtual host is `localhost`. To fix it, add the host as a company virtual host, then redeploy the CX:

- `VirtualHostLocalService.updateVirtualHosts(companyId, 0, …)`, keeping every existing host and the default.
- The redeployed application then listed both `http://127.0.0.1:8680/o/oauth2/redirect` and `http://localhost:8680/o/oauth2/redirect`, and minted tokens from both origins.

Verified on 2026.Q3.5.

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
| 401 right after a redeploy, with a token or credentials that worked before | The deploy rotated the client ID and secret: re-read the `routes` files (see "Token Acquisition") |
| 403 on specific resource | Scope too narrow; add the resource's scope string from `rules/oauth-scopes.md` |
| Application not in Control Panel | Bundle not `ACTIVE`; run `diag <id>` in Gogo shell |
| ERC not resolved | ERC field in yaml must match the top level key of the OAuth entry, not its `name` field |