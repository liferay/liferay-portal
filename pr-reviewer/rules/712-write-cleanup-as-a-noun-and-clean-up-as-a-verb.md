# 712: Write Cleanup as a Noun and Clean Up as a Verb

Use `cleanup` as a noun, naming a thing such as a method, a phase, or a section heading, and `clean up` as a verb, describing an action. An identifier that names the thing uses the noun: `runCleanup`, `cleanupInterval`, a "Cleanup" heading. A comment, message, or sentence that tells the reader to perform the action uses the verb: "Clean up the cache after the test runs". The two are not interchangeable, and `CleanUp` inside an identifier is neither form.

**Rationale:** Treating `cleanup` and `clean up` as the same word produces noise in symbols and prose. The noun form names a thing and the verb form describes an action, so a reader who sees `runCleanup` knows the method runs a named phase, and one who reads "clean up the cache" knows what to do.

A violation is `cleanup` used where an action is meant, as in `// Cleanup the cache`, or `clean up` and `CleanUp` used as the noun in an identifier or heading, as in `runCleanUp`. An identifier that matches an existing API, such as an overridden method or a third party call, follows rule 001 and is not a violation.

**Example:** the verb form belongs in an imperative comment, and the noun form belongs in an identifier.

```diff
-// Cleanup the cache after the test runs
+// Clean up the cache after the test runs
```

```diff
-public void runCleanUp() {
+public void runCleanup() {
```