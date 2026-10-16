# Fetch Failure Data From Testray

Pull a single Testray case result through the REST API at `https://testray.liferay.com`. The caller supplies a positive integer case result ID, a test name, or a Testray build URL as the failure identifier (see **Input** in the skill).

## Preconditions

`${TESTRAY_CLIENT_ID}` and `${TESTRAY_CLIENT_SECRET}` must be set in the environment. Without them, abort and surface the reason.

## Authentication

Obtain a bearer token once per run through the OAuth2 client credentials grant at `https://testray.liferay.com/o/oauth2/token`, authenticating with `${TESTRAY_CLIENT_ID}` and `${TESTRAY_CLIENT_SECRET}` as HTTP Basic credentials. Read the `access_token` from the JSON response, store it in `${ACCESS_TOKEN}`, and present it as a `Bearer` token on every subsequent request.

## Resolve a Test Name to a Case Result ID

Skip when the input is not a test name. Otherwise, resolve the name through the master project's team routine. Every step that fails aborts the resolution — surface the reason and ask the user to retry with a case result ID directly. Do not silently fall back to a different project or routine.

1. Use the canonical master project ID `35392` as `<masterProjectId>`. This is the project that hosts every team routine, browsable at `https://testray.liferay.com/#/project/35392/routines`. Do not derive it from the most recent `[master]` build — fork and sandbox projects also publish `[master]` builds, and routine names like `[master] ci:test:stable` exist on both, so the most recent build may belong to a fork.

1. Fetch every Case with that exact name and keep the single one whose `r_projectToCases_c_projectId` equals `<masterProjectId>`. When zero or more than one match, abort.

	```bash
	curl \
		--data-urlencode "filter=name eq '<name>'" \
		--data-urlencode "pageSize=20" \
		--get \
		--header "Accept: application/json" \
		--header "Authorization: Bearer ${ACCESS_TOKEN}" \
		--silent \
		--url "https://testray.liferay.com/o/c/cases"
	```

1. Derive the team routine from `.github/CODEOWNERS`. Each Liferay team owns a routine on the master project named **`[master] ci:test:<team>`**, where `<team>` is the suffix of the GitHub team handle (`@liferay-page-management` maps to `page-management`). Other routines (`ci:test:relevant`, `EE Development Acceptance`, …) are not team routines and must not substitute. Resolve its ID:

	```bash
	curl \
		--data-urlencode "filter=name eq '[master] ci:test:<team>' and r_routineToProjects_c_projectId eq '<masterProjectId>'" \
		--data-urlencode "pageSize=1" \
		--get \
		--header "Accept: application/json" \
		--header "Authorization: Bearer ${ACCESS_TOKEN}" \
		--silent \
		--url "https://testray.liferay.com/o/c/routines"
	```

	When CODEOWNERS does not point to a single team, or the routine does not exist, abort.

1. List the most recent case results for the case:

	```bash
	curl \
		--data-urlencode "filter=r_caseToCaseResult_c_caseId eq '<caseId>'" \
		--data-urlencode "pageSize=50" \
		--data-urlencode "sort=dateCreated:desc" \
		--get \
		--header "Accept: application/json" \
		--header "Authorization: Bearer ${ACCESS_TOKEN}" \
		--silent \
		--url "https://testray.liferay.com/o/c/caseresults"
	```

	For each item in order, fetch its build (`/o/c/builds/<buildId>`) and read `r_routineToBuilds_c_routineId`. Return the `id` of the first case result whose build routine matches the team routine ID and whose `dueStatus.key` is not `UNTESTED`. The result may be `PASSED` — that is correct when the test currently passes on the team routine. When the loop ends without a match, abort.

## Resolve a Build URL to a Case Result ID

Skip when the input is not a Testray build URL. Otherwise, pick the first unclaimed failure automatically from the build.

Parse `<buildId>` from the URL path and read `<teamIds>` from `filter.testrayTeamIds` when present in the query string. Print the resulting `<buildId>` and `<teamIds>` before running any query, so the parse is auditable rather than asserted.

1. List failed case results on the build, newest first. Append the team predicate only when `<teamIds>` is nonempty, joining multiple IDs with `or`:

	```bash
	curl \
		--data-urlencode "filter=r_buildToCaseResult_c_buildId eq '<buildId>' and (r_teamToCaseResult_c_teamId eq '<teamId1>' or r_teamToCaseResult_c_teamId eq '<teamId2>') and dueStatus eq 'FAILED'" \
		--data-urlencode "pageSize=50" \
		--data-urlencode "sort=dateCreated:desc" \
		--get \
		--header "Accept: application/json" \
		--header "Authorization: Bearer ${ACCESS_TOKEN}" \
		--silent \
		--url "https://testray.liferay.com/o/c/caseresults"
	```

## Derive the Failure Data

The case result drives every other lookup:

```bash
curl \
	--header "Accept: application/json" \
	--header "Authorization: Bearer ${ACCESS_TOKEN}" \
	--silent \
	--url "https://testray.liferay.com/o/c/caseresults/<caseResultId>"
```

When `dueStatus.key` is `PASSED` or `BLOCKED`, return only **Name** and that `dueStatus`; the rest are skipped. Otherwise, return all fields.

### Name

Fetch the case using `r_caseToCaseResult_c_caseId` from the case result and read `name` from the first item:

```bash
curl \
	--data-urlencode "filter=id eq '<caseId>'" \
	--data-urlencode "pageSize=1" \
	--get \
	--header "Accept: application/json" \
	--header "Authorization: Bearer ${ACCESS_TOKEN}" \
	--silent \
	--url "https://testray.liferay.com/o/c/cases"
```

### Type

When `<name>` contains `PortalLogAssertor`, the type is `Java Log Assertor` and **no history is fetched**.

Otherwise, fetch the case type via `r_caseTypeToCases_c_caseTypeId` and map the matching item's `name`. Names not in the table pass through unchanged.

```bash
curl \
	--data-urlencode "filter=id eq '<caseTypeId>'" \
	--get \
	--header "Accept: application/json" \
	--header "Authorization: Bearer ${ACCESS_TOKEN}" \
	--silent \
	--url "https://testray.liferay.com/o/c/casetypes"
```

| Case Type Name | Label |
| --- | --- |
| Automated Functional Test | Poshi |
| JS Unit Test | JavaScript |
| Modules Integration Test | Java Integration |
| Modules Semantic Versioning Test | Java Semantic Versioning |
| Modules Unit Test | Java Unit |
| Playwright Test | Playwright |

### Error Trace

Return the `errors` field of the case result.

### Failure Date

Return the `dateCreated` field of the case result.

### Last Pass SHA and First Fail SHA

Both are `null` when the case name is `Top Level Build` or contains `PortalLogAssertor`. They are skipped entirely for `Java Log Assertor`. Otherwise, compute them from the case history filtered to the supplied case result's routine.

Resolve the routine ID by fetching the build at `/o/c/builds/<buildId>` (where `<buildId>` is `r_buildToCaseResult_c_buildId` from the case result) and reading `r_routineToBuilds_c_routineId` as `<routineId>`.

Fetch the case history:

```bash
curl \
	--data-urlencode "pageSize=300" \
	--data-urlencode "sort=executionDate:desc" \
	--get \
	--header "Accept: application/json" \
	--header "Authorization: Bearer ${ACCESS_TOKEN}" \
	--silent \
	--url "https://testray.liferay.com/o/testray-rest/v1.0/testray-case-result-history/<caseId>"
```

Filter to entries where `testrayRoutineId` equals `<routineId>` and walk newest-first. History entries expose the outcome on the top-level `status` field (not `dueStatus`, which only exists on case results):

- `lastPassSha` is the `gitHash` of the first entry whose `status` is `PASSED`, or `null` if none.

- `firstFailSha` is the `gitHash` of the oldest entry whose `status` is `FAILED` before that `PASSED`, or `null` if none.

### Build SHA

The `gitHash` of the build the case result belongs to — the commit the failing build was actually tested against, distinct from `firstFailSha` because later failing builds run against newer commits. Read it from the same `/o/c/builds/<buildId>` fetch used to resolve `<routineId>` above.