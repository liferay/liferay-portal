# REST Builder

Use these procedures whenever a task requires creating or modifying a Liferay REST Builder module bundle.

## Module Bundle Layout

A REST Builder feature lives in four sibling modules under `modules/apps/<area>`:

- `<name>-rest-api` — generated public API.
- `<name>-rest-impl` — implementation. Hand-written `rest-config.yaml` and `rest-openapi.yaml` drive everything else.
- `<name>-rest-client` — generated Java client.
- `<name>-rest-test` — integration tests.

## Files To Leave Alone

Every generated Java file is tagged `@Generated("")` — do not hand-edit anything carrying that annotation; `buildREST` rewrites it on each run. The same applies to the non-Java generated artifacts under `<name>-rest-impl/src/main/resources/OSGI-INF`.

## Creating a New API

Use this workflow to scaffold a brand-new REST Builder module bundle from scratch.

### Files To Author Before `buildREST`

```
<name>-rest-api/{.lfrbuild-portal, bnd.bnd, build.gradle}
<name>-rest-impl/{.lfrbuild-portal, bnd.bnd, build.gradle, rest-config.yaml, rest-openapi.yaml}
<name>-rest-client/{.lfrbuild-portal, bnd.bnd, build.gradle}
<name>-rest-test/{bnd.bnd, build.gradle}
```

`buildREST` discovers a module by finding `rest-config.yaml` next to `build.gradle` — a missing `build.gradle` silently skips the module. The `.lfrbuild-portal` files are empty marker files.

#### `<name>-rest-api/bnd.bnd`

```
Bundle-Name: Liferay <Title> REST API
Bundle-SymbolicName: com.liferay.<dotted-name>.rest.api
Bundle-Version: 1.0.0
Export-Package:\
	com.liferay.<dotted-name>.rest.dto.v1_0,\
	com.liferay.<dotted-name>.rest.resource.v1_0
```

#### `<name>-rest-api/build.gradle`

```
dependencies {
	compileOnly group: "com.fasterxml.jackson.core", name: "jackson-annotations", version: "2.18.9"
	compileOnly group: "com.liferay", name: "jakarta.ws.rs", version: "3.1.0.LIFERAY-PATCHED-1"
	compileOnly group: "com.liferay.portal", name: "com.liferay.portal.kernel", version: "default"
	compileOnly group: "io.swagger.core.v3", name: "swagger-annotations-jakarta", version: "2.2.28"
	compileOnly group: "jakarta.annotation", name: "jakarta.annotation-api", version: "2.1.1"
	compileOnly group: "jakarta.servlet", name: "jakarta.servlet-api", version: "6.0.0"
	compileOnly group: "jakarta.validation", name: "jakarta.validation-api", version: "3.1.0"
	compileOnly group: "jakarta.xml.bind", name: "jakarta.xml.bind-api", version: "4.0.2"
	compileOnly group: "org.osgi", name: "org.osgi.annotation.versioning", version: "1.1.0"
	compileOnly project(":apps:portal-odata:portal-odata-api")
	compileOnly project(":apps:portal-vulcan:portal-vulcan-api")
	compileOnly project(":core:petra:petra-function")
	compileOnly project(":core:petra:petra-string")
}
```

#### `<name>-rest-impl/bnd.bnd`

```
Bundle-Name: Liferay <Title> REST Implementation
Bundle-SymbolicName: com.liferay.<dotted-name>.rest.impl
Bundle-Version: 1.0.0
```

#### `<name>-rest-impl/build.gradle`

```
dependencies {
	compileOnly group: "com.liferay", name: "jakarta.ws.rs", version: "3.1.0.LIFERAY-PATCHED-1"
	compileOnly group: "com.liferay.portal", name: "com.liferay.portal.kernel", version: "default"
	compileOnly group: "io.swagger.core.v3", name: "swagger-annotations-jakarta", version: "2.2.28"
	compileOnly group: "jakarta.annotation", name: "jakarta.annotation-api", version: "2.1.1"
	compileOnly group: "jakarta.servlet", name: "jakarta.servlet-api", version: "6.0.0"
	compileOnly group: "org.osgi", name: "org.osgi.service.component", version: "1.4.0"
	compileOnly group: "org.osgi", name: "org.osgi.service.component.annotations", version: "1.4.0"
	compileOnly group: "org.osgi", name: "osgi.core", version: "6.0.0"
	compileOnly project(":apps:<area>:<name>-rest-api")
	compileOnly project(":apps:portal-odata:portal-odata-api")
	compileOnly project(":apps:portal-vulcan:portal-vulcan-api")
	compileOnly project(":core:petra:petra-function")
}
```

#### `<name>-rest-impl/rest-config.yaml`

```yaml
apiDir: "../<name>-rest-api/src/main/java"
apiPackagePath: "com.liferay.<dotted-name>.rest"
application:
    baseURI: "/<name>"
    className: "<Title>Application"
    name: "Liferay.<Title>.REST"
author: "<Your Name>"
clientDir: "../<name>-rest-client/src/main/java"
compatibilityVersion: 15
forcePredictableOperationId: true
javaEEPackage: "jakarta"
testDir: "../<name>-rest-test/src/testIntegration/java"
```

Endpoints land at `/o/<baseURI>/v1.0/...` — do not collide with existing servlet patterns.

#### `<name>-rest-impl/rest-openapi.yaml`

The file is standard OpenAPI 3.0.1, with the following minimum shape:

- `info`: set `title`, `description`, `version: "v1.0"`. Title and description are not validator-required, but tooling (including the MCP `/discover` endpoint) relies on them.
- `paths`: each operation needs `operationId`, `description`, and `tags: [<Tag>]`. The first tag becomes the resource name (`<Tag>Resource`); use one tag per resource.
- `components.schemas`: each schema needs `description`, typed `properties`, and `required`.

Response shape drives the generated return type. Two cases are worth knowing:

- `application/json` with `{type: array, items: {$ref: ...}}` → `Page<DTO>`.
- `application/json` with `$ref` → the DTO.

For anything else, run REST Builder and read the signature on the generated `Base<Tag>ResourceImpl`.

#### `<name>-rest-client/bnd.bnd`

```
Bundle-Name: Liferay <Title> REST Client
Bundle-SymbolicName: com.liferay.<dotted-name>.rest.client
Bundle-Version: 1.0.0
Export-Package:\
	com.liferay.<dotted-name>.rest.client.dto.v1_0,\
	com.liferay.<dotted-name>.rest.client.function
```

#### `<name>-rest-client/build.gradle`

```
dependencies {
	compileOnly group: "jakarta.annotation", name: "jakarta.annotation-api", version: "2.1.1"
}
```

#### `<name>-rest-test/bnd.bnd`

```
Bundle-Name: Liferay <Title> REST Test
Bundle-SymbolicName: com.liferay.<dotted-name>.rest.test
Bundle-Version: 1.0.0
```

#### `<name>-rest-test/build.gradle`

```
dependencies {
	testIntegrationImplementation group: "com.fasterxml.jackson.core", name: "jackson-databind", version: "2.18.9"
	testIntegrationImplementation group: "com.liferay", name: "jakarta.ws.rs", version: "3.1.0.LIFERAY-PATCHED-1"
	testIntegrationImplementation group: "jakarta.annotation", name: "jakarta.annotation-api", version: "2.1.1"
	testIntegrationImplementation project(":apps:<area>:<name>-rest-api")
	testIntegrationImplementation project(":apps:<area>:<name>-rest-client")
	testIntegrationImplementation project(":apps:portal-odata:portal-odata-api")
	testIntegrationImplementation project(":apps:portal-vulcan:portal-vulcan-api")
	testIntegrationImplementation project(":test:arquillian-extension-junit-bridge")
}
```

### Files To Edit After `buildREST`

`buildREST` scaffolds `<name>-rest-impl/src/main/java/.../internal/resource/v1_0/<Tag>ResourceImpl.java` (one per OpenAPI tag) on first run and preserves it on every subsequent run. Implement the resource logic here by overriding the methods declared on the generated `Base<Tag>ResourceImpl`.

The matching `<Tag>ResourceTest.java` under `<name>-rest-test/src/testIntegration/java/.../resource/v1_0/test` follows the same scaffold-then-edit pattern.

### Workflow

Run every step without asking for confirmation, including the commits.

1. Commit the hand-written files.

1. Run REST Builder.

1. Commit the changes the tool produces or rewrites.

1. Continue with the work.

## Editing an Existing API

Use this workflow when changing `rest-config.yaml` or `rest-openapi.yaml` on a REST Builder module bundle that already exists. Every YAML edit must be regenerated and committed before any implementation work continues.

### Workflow

Run every step without asking for confirmation, including the commits.

1. Commit the hand-written YAML files.

1. Run REST Builder.

1. Commit the changes the tool produces or rewrites.

1. Continue with the work.

## Running REST Builder

Both entry points pick up the latest generator code automatically, so any change to the generator source under `modules/util/portal-tools-rest-builder` takes effect on the next run.

### A Single Module

Run `<gradlew> buildREST` from the impl module to regenerate that module alone.

### Every Module

To regenerate every REST Builder module in one pass, run `ant build-rests` from `portal-impl`:

```bash
(cd "${REPO_ROOT}/portal-impl" && ant build-rests)
```

A single JVM scans every module directly via `RESTBuilder`, which is faster than running `<gradlew> buildREST` per module.

## Editing REST Builder Itself

Use this workflow when editing the REST Builder generator itself. The generator's source lives under `modules/util/portal-tools-rest-builder`.

### Workflow

Run every step without asking for confirmation, including the commits.

1. If possible, commit a failing test to `modules/util/portal-tools-rest-builder-test-test`. The `modules/util/portal-tools-rest-builder-test-*` modules act as the generator's test bed. Run the test and make sure it fails.

1. Perform the change.

1. Regenerate `modules/util/portal-tools-rest-builder-test-impl` module as per the "Editing an Existing API" section. The test should now pass.

1. Run REST Builder for every module, as described in the "Every Module" section.

1. Commit the regenerated output.