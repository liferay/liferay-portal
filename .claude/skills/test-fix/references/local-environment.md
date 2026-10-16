# Local Environment

A local run is only evidence when the bundle matches what CI ran. Each item below has produced a local result that was the opposite of CI.

## The Bundle

- **Do not deploy single modules into an old bundle.** A targeted `deploy` into a bundle that is days behind can break OSGi resolution without any error. Rebuild instead.

- **One Liferay home per checkout.** Sibling checkouts resolve the stock `app.server.parent.dir` to the same `bundles` directory, so they share `osgi`, `data`, `deploy`, and `portal-ext.properties` even when their Tomcat folders differ. Point each checkout or worktree at its own directory in its gitignored `app.server.${USER}.properties`, then delete the cached `.gradle/gradle.properties` so Gradle stops using the old path. The `worktree-status` skill shows each worktree's ports and database.

- **Product changes get their own bundle.** While a product fix is being tried, test side runs must not share that bundle. Use a separate worktree and bundle, or schedule the two so they never overlap.

- **The bundle must contain the commit under test.** A bundle older than the commit that broke the test reports the opposite of CI. Compare jar dates with the commit date, and after a deploy confirm the change is in the deployed artifact (for example `javap -c` on the class inside the jar under `webapps/ROOT/WEB-INF/shielded-container-lib` or `osgi`).

- **Use JDK 21 for master.** Export `JAVA_HOME` for Ant, Gradle, and `shutdown.sh`. Under an older default JDK the source formatter fails with what looks like a violation, and `shutdown.sh` does nothing.

## Matching CI

- **Mirror CI's portal properties.** The `prepare-portal-ext-properties-default` macro in `build-test.xml` writes the values CI uses into `portal-impl/src/portal-ext.properties` (for example `company.security.strangers.verify=false`). Diff that block against the bundle's `portal-ext.properties`; a missing value fails login or setup for reasons CI never sees.

- **Pin the browser.** The test properties name the Chrome version CI uses (`browser.chrome.version`). Point `browser.chrome.bin.file[<version>]` in `test.${USER}.properties` at a Chrome for Testing binary of that version (for example `browser.chrome.bin.file[139.0]=/path/to/chrome`); a different Chrome can hang Poshi in its first `open`.

- **Run on a virtual instance** when the class does. Set `test.liferay.virtual.instance=true` in `test.${USER}.properties`; a `-D` flag on the command line is ignored, because the `get-testcase-property` macro in `build-test.xml` resets the property from the generated test properties. Run with `HOSTNAME=$(hostname)` so the test URL uses a name the virtual instance answers to, and restore both afterwards.

- **Use the database CI used** when the failure could depend on it.

## State Left Behind

CI starts every run from a fresh instance. A local bundle keeps whatever earlier runs left, and a failing run leaves the most.

- **Per user UI state.** A collapsed editor panel or a pinned product menu persists for the admin user and changes what later tests can see.

- **Publications.** A failed publications run can leave the admin working in a draft publication, which changes every later run.

- **Sites, pages, and taxonomy.** Leftover Guest pages make later runs navigate to stale copies. A test that adds a vocabulary to Guest and only cleans its own entities leaves the vocabulary behind, and the next run finds two with the same name.

Before rerunning, list what earlier runs created (for example with the headless APIs: sites, pages, vocabularies) and remove only those items, through the portal's own APIs. Ask the user before deleting anything a test run did not create.

## Reading Local Results

- **A Gradle or Ant run that ends `BUILD SUCCESSFUL` can still contain a failed test.** Read the test result line, not the outer build status.

- **Memory pressure looks like a page bug.** Every test dying in setup on its first `open` with a renderer timeout means the machine is swapping. Check swap before blaming the page.

- **Poshi's log check reads every `liferay.*.xml` in the logs directory.** Old logs fail new runs with `Liferay Exceptions were thrown`. Move them aside before a run.