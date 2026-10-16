# Reading Failures

Patterns in which the error text points somewhere other than the cause. The examples come from `ci:test:content-management`.

## Screenshots and Artifacts

- **Poshi step reports list step source, not resolved values.** Use the screenshots to learn which page or URL a step was on.

- **Read the screenshot before the error.** A region that never rendered (a blank portlet, an empty editor, a widget page where a content page was expected) fails a locator that names the wrong thing. The screenshot settles these quickly; the error text alone points the wrong way.

- **Read the server log for the test's time window.** It carries exceptions the test framework never reports, and often names the real failure.

- **Shard screenshots overwrite each other.** Files named `test-failed-1.png.gz` at the shard level may belong to any spec in the shard. Use the per test `test-results/<spec>` directory, its `error-context.md.gz`, and its `trace.zip`.

## Clusters

- **A shared cause can hide behind different errors.** One defect can fail many tests, each on its own step. For example, a null key in the generic error tag stopped the "Your request failed to complete." alert from rendering and failed more than twenty tests.

- **A shared locator is not a shared cause.** A widely used locator collects failures from unrelated pages. For example, nine failures on one modal iframe locator were three unrelated modals, and seven failures in one test class had six different causes.

- **Pass rate discriminates.** Within a cluster, a test that fails every build and one that fails a third of the time usually have different causes.

## Run Context

- **A build where every test is `UNTESTED`** means the job never ran. A green build with whole projects missing means test grouping broke. Neither is a test result.

- **Company settings beat `portal-ext.properties`.** Deprecation type feature flags and editor choices are stored per company. Changing the property does not change an existing company; check the company's creation date and the stored value.

- **Downloads from nonlocalhost origins.** Chrome holds downloads from insecure nonlocalhost origins, so download steps stall on virtual instance hostnames.

- **Virtual instances.** Many Poshi classes run inside a fresh virtual instance on CI. A local run on the default instance can pass, or fail differently, for that reason alone. Reproduce on a virtual instance before calling a failure CI only.

## Poshi Semantics

- **A typing step that seems to merge text instead of replacing it** is a known macOS clear artifact in the Poshi runner; CI on Linux is unaffected.

- **`IsElementNotPresent` means absent or hidden.** Use `!(IsElementPresent(...))` when "not in the DOM" is what matters.

- **`tearDown` failures are warnings.** A teardown that failed to delete its data does not fail the test, and the leftover data breaks the next run.

- **`WaitFor*` is soft.** The whole family times out silently. A wait that "passed" may have proved nothing. Pair it with an assertion that fails.

## Product or Test

- **Expected values are the specification** unless the commit that changed the behaviour documents the contract change.

- **Failing against product code does not make the product wrong.** Check what moved on the test side first (`git log -S` on the locator, macro, or page object). A helper that changed under the test looks exactly like a product regression.

- **Fixing step N often exposes step N+1.** A fix that gets the test one step further is progress, not a pass. Rerun to the end.