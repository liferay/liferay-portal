# CI Artifacts

Every case result in Testray links to the logs and screenshots of the shard that ran it. They live in the `testray-results` Google Cloud Storage bucket.

## Preconditions

Application default credentials for Google Cloud with read access to `testray-results`. Check them with:

```bash
gcloud auth application-default print-access-token > /dev/null && echo OK
```

When the check fails, ask the user to run `gcloud auth application-default login` themselves (it opens a browser).

## Find the Shard Prefix

Read the case result's `attachments` field, with `${ACCESS_TOKEN}` obtained as in [`testray.md`](testray.md). It is a JSON string holding a list of entries with `name`, `value`, and `url`, for example **Jenkins Console**, **Liferay Log**, **Poshi Report**, and **Docker Log (<instance>)**:

```bash
curl \
	--header "Accept: application/json" \
	--header "Authorization: Bearer ${ACCESS_TOKEN}" \
	--silent \
	--url "https://testray.liferay.com/o/c/caseresults/<caseResultId>"
```

Take an entry whose `value` is an object path inside the bucket: **Jenkins Console** for Playwright, **Liferay Log** for Poshi (Poshi case results have no **Jenkins Console** entry). Strip the query string and the file name to get the shard prefix, for example `2026-10/test-1-47/test-portal-testsuite-upstream(master)/1714/playwright-js-tomcat101-postgresql163/12/0`.

## List and Download

List the exact object names under the prefix rather than building them by hand. The command adds a slash after the prefix so that shard `1` does not also match shard `12`:

```bash
curl \
	--data-urlencode "fields=items(name),nextPageToken" \
	--data-urlencode "prefix=<shard prefix>/" \
	--get \
	--header "Authorization: Bearer $(gcloud auth application-default print-access-token)" \
	--silent \
	--url "https://storage.googleapis.com/storage/v1/b/testray-results/o"
```

Download an object by its URL encoded name. Objects whose names end in `.gz` are stored gzip compressed, but Cloud Storage decompresses them for a client that does not send `Accept-Encoding: gzip`, as the command below does not, so the file arrives as plain text or an image; do not run `gunzip` on it. `trace.zip` is an ordinary zip file:

```bash
curl \
	--header "Authorization: Bearer $(gcloud auth application-default print-access-token)" \
	--output "<local file>" \
	--silent \
	--url "https://storage.googleapis.com/storage/v1/b/testray-results/o/<URL encoded object name>?alt=media"
```

## What to Read

- **`jenkins-console.txt.gz`**: the full console of the shard. For Playwright it holds the run summary (`N passed`, `N failed`, `N flaky`) and the numbered list of failing specs; for Poshi it holds the real stack trace and any Ant errors.

- **`liferay-log.txt.gz`**: the portal log for the shard.

- **Playwright results**: under `test-results/<spec>`, with `test-failed-*.png.gz`, `error-context.md.gz`, and `trace.zip` (open with `npx playwright show-trace`). Spec folders start with the spec file's basename, then the title, cut short with a short hash inside it when long, then the project name and `-retry<N>` for retries. Match on the basename plus the start of the title. Files at the shard root may belong to any spec in the shard; use the per spec folder.

- **Poshi results**: under a folder named `LocalFile.<Class>_<Method>` inside the shard prefix. `summary.html.gz` and `index.html.gz` (the **Poshi Summary** and **Poshi Report**) show the failing step, and `screenshots` holds `before<N>.jpg.gz` and `after<N>.jpg.gz` (PNG data despite the name); the highest `after` image is the page at the failure.