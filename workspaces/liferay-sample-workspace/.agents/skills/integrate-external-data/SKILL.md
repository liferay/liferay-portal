---

description: Back a Liferay Object with an external data source using the Object Entry Manager CET pattern. Use when the user wants to connect an object to an external REST API, database, or SaaS system rather than storing data in Liferay's own database.
name: integrate-external-data

---

# Integrate External Data

The Object Entry Manager pattern lets Liferay Objects delegate storage and retrieval to a microservice. Object CRUD calls proxy to the external service; Liferay UI, workflows, and permissions all work transparently.

## When to Invoke

- "Connect this object to our Salesforce data"
- "Back this object with an external REST API"
- "I want Liferay to display records from an external system"
- Called by `manage-objects` when an object needs external storage

## Architecture

```
Browser / Portal UI
        │
        ▼
Liferay Object (storageType: function#<cet-erc>)
        │  delegates CRUD
        ▼
objectEntryManager CET  ←→  External REST API / DB
```

The CET implements an HTTP server that handles the calls below. `<objectERC>` is the object definition's external reference code, and `<entryERC>` is the entry's:

| `/o/c/<plural>` Call | CET Receives |
| --- | --- |
| List entries | `GET <resourcePath>/<objectERC>?companyId=…&languageId=…&page=…&pageSize=…&scopeKey=…&userId=…` |
| Create entry | `POST <resourcePath>/<objectERC>` |
| Read entry by ERC | `GET <resourcePath>/<objectERC>/<entryERC>` |
| Update entry by ERC | `PUT <resourcePath>/<objectERC>/<entryERC>` |
| Patch entry by ERC | `GET`, then `PUT`, on the same path |
| Delete entry by ERC | `DELETE <resourcePath>/<objectERC>/<entryERC>` |

Entries are addressed by ERC only. A call by numeric ID, such as `GET /o/c/<plural>/<id>`, returns `400 UnsupportedOperationException` and never reaches the CET. Verified on 2026.Q1.

`POST` and `PUT` bodies are wrapped: `{"companyId": …, "languageId": …, "objectEntry": {…}, "scopeKey": …, "userId": …}`. Read the fields from `objectEntry`. Its `externalReferenceCode` is `null` on a `PUT`, so take the ERC from the path, and `null` on a `POST` without one, so generate it. Verified on 2026.Q1.

## Workflow

### Define the Object With External Storage

```bash
curl \
	--data '{
		"label": {"en_US": "<Label>"},
		"name": "<Name>",
		"pluralLabel": {"en_US": "<PluralLabel>"},
		"scope": "company",
		"storageType": "salesforce"
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions" \
	--user "test@liferay.com:test"
```

Use `"storageType": "salesforce"` for native Salesforce integration, or `"storageType": "function#<cet-erc>"` for the custom CET pattern, where `<cet-erc>` is the key of the `objectEntryManager` entry. Any `storageType` sent over REST needs feature flag `LPS-135430`, which is off by default. Without it the create returns `400 ObjectDefinitionStorageTypeException` for every value. For the CET pattern, proceed to **Scaffold the `objectEntryManager` CET**.

### Scaffold the `objectEntryManager` CET

Call `scaffold-client-extension` with type `objectEntryManager`. The CET is a microservice that Liferay calls inbound.

Minimum `client-extension.yaml` entry:

```yaml
<workspace-id>-entry-manager:
    name: <Name> Entry Manager
    oAuth2ApplicationExternalReferenceCode: <workspace-id>-oauth
    resourcePath: /object/entry/manager
    type: objectEntryManager
```

The entry takes only these keys. Liferay reaches the microservice at the OAuth entry's `.serviceAddress` plus `resourcePath`. `client-extensions/liferay-sample-etc-spring-boot` shows a working example.

### Implement the Microservice

The microservice must respond to the five endpoints above. Use any stack (Spring Boot, Node.js, Python). The request and response bodies follow the Liferay Headless delivery envelope:

```json
// GET / — list response

{
  "actions": {},
  "facets": [],
  "items": [{...}],
  "lastPage": 1,
  "page": 1,
  "pageSize": 20,
  "totalCount": 1
}

// POST / — single item response

{
  "id": 123,
  "<fieldName>": "<value>",
  ...
}
```

Liferay authenticates each call with an `Authorization: Bearer <JWT>` header, issued for the CET's OAuth application. Verify it as `client-extensions/liferay-sample-etc-spring-boot` does. Liferay sends no other token header. Verified on 2026.Q1.

### Wire OAuth

Call `setup-oauth` to add the companion `oAuthApplicationUserAgent` entry to `client-extension.yaml`. The entry manager needs scopes to call back into Liferay when it must resolve related objects or write audit entries.

Minimum scope: the per object scope (`c_<name>.everything`) for each object it calls back into; `Liferay.Headless.Object.everything` does not grant `/o/c` (see `rules/oauth-scopes.md`). Add `Liferay.Object.Admin.REST.everything` if the entry manager needs to inspect or modify the object definition itself.

### Deploy

Run `deploy-and-verify` from the client extension root. Then start the microservice separately on the port in the OAuth entry's `.serviceAddress`.

### Verify

```bash
# Create an entry — should proxy to the external system

curl \
	--data '{"<fieldName>": "test"}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/c/<pluralLabel>" \
	--user "test@liferay.com:test"

# List entries — should return data from the external system

curl \
	--silent \
	--url "http://localhost:${PORT}/o/c/<pluralLabel>" \
	--user "test@liferay.com:test"
```

Check the microservice logs to confirm Liferay forwarded the calls. If entries appear empty, the microservice's list response format may not match the expected envelope (see **Implement the Microservice**).

### Troubleshoot

| Symptom | Check |
| --- | --- |
| 500 on entry creation | Microservice unreachable at the OAuth entry's `.serviceAddress`; check network and port |
| 401 from microservice | Bearer JWT validation failing. The token is `RS256`, signed by the single key at `/o/oauth2/jwks`, and carries no `kid`, so do not require a `kid` match. `aud` and `client_id` match the `.oauth2.user.agent.audience` and `.oauth2.user.agent.client.id` routes. Verified on 2026.Q1 |
| `400 ObjectDefinitionStorageTypeException` on create | Enable `LPS-135430`, and set `storageType` to `function#<cet-erc>` |
| Empty list from Liferay | Microservice returns nonenvelope JSON; wrap in the Headless page envelope |

## Success Signal

TODO / inferred — verify against a running bundle. The `### Verify` step above (POST then GET on `/o/c/<pluralLabel>` return external system records; microservice logs show forwarded CRUD calls) is the observable completion check; confirm on a live bundle.