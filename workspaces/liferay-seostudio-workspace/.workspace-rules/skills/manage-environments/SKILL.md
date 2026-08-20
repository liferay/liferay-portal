---

description: Manage workspace environment configurations (local/dev/uat/prod), promote changes between environments, export object data models, and capture a running site as a reusable site initializer CET. Use when the user asks to promote to UAT, export this site, migrate object data, or set up environment specific properties. Maps to workspace multienvironment support and Data Model Migration in "Mastering Data Modeling with Liferay Objects".
name: manage-environments

---

# Manage Environments

Switch portal configurations per deployment target, promote property changes between environments, export data models, and capture a live site as a site initializer.

## When to Invoke

- "Promote to UAT", "add a UAT environment config"
- "Export the object definitions so we can import them to production"
- "Capture the current site as a site initializer for version control"
- "Set up a database connection for dev"
- Called by `build-site` after runtime iteration when the user wants to commit the site to source control

## Workspace Environment Model

`configs/` holds one directory per environment. Each directory may contain a `portal-ext.properties` that overrides the common config.

```
configs/
  common/          # Applied to every environment
  local/           # Developer workstation (default)
  dev/             # Shared dev server
  uat/             # User acceptance testing
  prod/            # Production
  docker/          # Docker / container deployments
```

The active environment is set by `liferay.workspace.environment` in `gradle.properties`. Default is `local`.

Blade merges properties in order: `common/portal-ext.properties` then `<env>/portal-ext.properties`. The merge result lands in `bundles/portal-ext.properties`.

## Workflow

### Declare the Target Environment

Check `gradle.properties`:

```bash
grep "liferay.workspace.environment" gradle.properties
```

To switch environments:

```bash
# Edit gradle.properties

liferay.workspace.environment=uat
```

Then run `blade gw initBundle` to merge and push configs (applies to remote/UAT server initialization only — do not use for local dev bundles; see `deploy-and-verify`). Alternatively, set the environment at deploy time:

```bash
blade gw deploy -Pliferay.workspace.environment=uat
```

### Add or Update Environment Properties

Edit `configs/<env>/portal-ext.properties` with the target environment's values. Common properties per environment:

```properties
# Database (dev example)

jdbc.default.driverClassName=org.mariadb.jdbc.Driver
jdbc.default.url=jdbc:mariadb://localhost:3306/liferay_dev
jdbc.default.username=liferay
jdbc.default.password=liferay

# Elasticsearch

com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration.operationMode=REMOTE
com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration.networkHostAddresses=["https://elasticsearch-dev:9200"]

# Mail (disable for dev)

mail.send.blacklist=*

# Feature flags

feature.flag.LPD-35443=true
```

### Export Object Definitions

There is **no** `/object-definitions/{id}/export` or `/object-definitions/import` endpoint. Migrate object **definitions** through the Batch Engine endpoints on `object-admin`:

```bash
# Export object definitions

curl \
	--data '{"callbackURL": "", "fieldNames": ["name", "label", "pluralLabel", "scope", "objectFields"], "taskItemDelegateName": "DEFAULT"}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions/export-batch" \
	--user "test@liferay.com:test"

# Import on the target environment (batch create/update)

curl \
	--data @object-definitions.json \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/object-admin/v1.0/object-definitions/batch" \
	--user "test@liferay.com:test"
```

Confirm the exact request bodies against the OpenAPI spec (`get-openapi` MCP tool, or fetch `GET /o/object-admin/v1.0/openapi.json` with curl). For source controlled migration, prefer a site-initializer `batch/` file (see `rules/site-initializer-format.md`).

### Export Object Data (Batch)

Export entries from a live environment for bulk import elsewhere:

```bash
# Request an export task

curl \
	--data '{
		"fieldNames": ["<field1>", "<field2>"],
		"taskItemDelegateName": "<pluralLabel>"
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/headless-batch-engine/v1.0/export-task/com.liferay.object.rest.dto.v1_0.ObjectEntry/JSON" \
	--user "test@liferay.com:test"
```

Poll the task until `"executeStatus": "COMPLETED"`, then download the content from the returned `contentURL`.

#### Batch Engine — Silent Field Skipping

BatchEngine silently skips unrecognized fields during import — it logs a `Cannot invoke` warning to `catalina.out` rather than failing the task. The import reports success and the task transitions to `COMPLETED` even when entire fields were dropped.

Ensure JSON field names exactly match the target Object Definition's field names before importing. Treat any `Cannot invoke` warning in `catalina.out` during an import as a hard error and cross check field names before retrying. After every import, GET a sample entry and confirm the imported fields actually persisted.

### Capture a Site as a Site Initializer

The `siteInitializer` CET pattern stores the site in source control so any environment can recreate it.

**Step 1 — Scaffold the CET structure** via `scaffold-client-extension` with type `siteInitializer`.

**Step 2 — Capture pages**: For each live page, call the Headless Admin Site API and write the result to `site-initializer/layouts/<NN-page-name>/page.json` and `page-definition.json`:

```bash
curl \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-site/v1.0/sites/<site-erc>/site-pages/<page-erc>?nestedFields=pageSpecifications" \
	--user "test@liferay.com:test" \
	| jq '{friendlyURL: .friendlyUrlPath_i18n.en_US, name: .name_i18n.en_US, type: .type}' \
	> site-initializer/layouts/01_home/page.json

# Note: there is no /site-pages/{id}/page-definition subresource. The page composition lives in
# the page's pageSpecifications (fetched via ?nestedFields=pageSpecifications above) and must be
# reshaped into the initializer's page-definition.json format. Confirm the shape via the OpenAPI
# spec (get-openapi MCP tool, or GET /o/headless-admin-site/v1.0/openapi.json).
```

**Step 3 — Capture object definitions**: Export definitions and place them under `site-initializer/batch/02-object-definition.batch-engine-data.json` using the batch engine data format (see `rules/site-initializer-format.md`).

**Step 4 — Capture fragments**: Copy the deployed fragment source directories into `site-initializer/fragments/group/`.

**Step 5 — Capture picklists**: Export list type definitions to `site-initializer/batch/00-list-type-definition.batch-engine-data.json`.

**Step 6 — Commit and deploy to the target environment** via `deploy-and-verify`.

### Promote a Config Change to UAT

1. Edit `configs/uat/portal-ext.properties` with the new value.

1. Commit the change.

1. Trigger the deployment pipeline (CI/CD), or push to the UAT server and run `blade gw initBundle`.

1. Bounce Tomcat on the UAT server to pick up the new properties.