---

description: Back a Liferay Object with an external data source using the Object Entry Manager CET pattern. Use when the user wants to connect an object to an external REST API, database, or SaaS system rather than storing data in Liferay's own database. Maps to "Integration with External Systems and Data" in "Mastering Data Modeling with Liferay Objects".
name: integrate-external-data

---

# Integrate External Data

The Object Entry Manager pattern lets Liferay Objects delegate storage and retrieval to a microservice. Object CRUD calls proxy to the external service; Liferay UI, workflows, and permissions all work transparently.

## When to Invoke

- "Connect this object to our Salesforce data"
- "Back this object with an external REST API"
- "I want Liferay to display records from an external system"
- Called by `manage-objects` when `storageType: ext-Service` is specified

## Architecture

```
Browser / Portal UI
        │
        ▼
Liferay Object (storageType: ext-Service)
        │  delegates CRUD
        ▼
objectEntryManager CET  ←→  External REST API / DB
```

The CET implements an HTTP server that handles five operations Liferay calls:

| Liferay Call | CET Endpoint |
| --- | --- |
| Create entry | `POST /` |
| Read entry | `GET /<id>` |
| Update entry | `PUT /<id>` |
| Delete entry | `DELETE /<id>` |
| List entries (paged) | `GET /` with `?page=&pageSize=&filter=` |

## Workflow

### Define the Object With `ext-Service` Storage

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

Use `"storageType": "salesforce"` for native Salesforce integration, or `"storageType": "ext-Service"` for the custom CET pattern. For `ext-Service`, proceed to **Scaffold the `objectEntryManager` CET**.

### Scaffold the `objectEntryManager` CET

Call `scaffold-client-extension` with type `objectEntryManager`. The CET is a microservice that Liferay calls inbound.

Minimum `client-extension.yaml` entry:

```yaml
<workspace-id>-entry-manager:
  baseURL: "http://host.docker.internal:<microservice-port>"
  name: <Name> Entry Manager
  oAuthApplicationHeadlessServerExternalReferenceCode: <workspace-id>-oauth
  objectDefinitionRestContextPath: "/o/c/<pluralLabel>"
  type: objectEntryManager
```

Where:
- `baseURL` is the address Liferay uses to reach the microservice (use `host.docker.internal` in Docker environments)
- `objectDefinitionRestContextPath` matches the published object's plural label

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

Liferay sends the `X-Liferay-Token` header with each call for the CET to verify authenticity.

### Wire OAuth

Call `setup-oauth` to add the companion `oAuthApplicationHeadlessServer` entry to `client-extension.yaml`. The entry manager needs scopes to call back into Liferay when it must resolve related objects or write audit entries.

Minimum scope: `Liferay.Headless.Object.everything`. Add `Liferay.Object.Admin.REST.everything` if the entry manager needs to inspect or modify the object definition itself.

### Deploy

Run `deploy-and-verify` from the client extension root. Then start the microservice separately on the port declared in `baseURL`.

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
| 500 on entry creation | Microservice unreachable at `baseURL`; check network and port |
| 401 from microservice | `X-Liferay-Token` validation failing; verify the token algorithm |
| CET not linked to object | `objectDefinitionRestContextPath` must match exactly; redeploy after fix |
| Empty list from Liferay | Microservice returns nonenvelope JSON; wrap in the Headless page envelope |