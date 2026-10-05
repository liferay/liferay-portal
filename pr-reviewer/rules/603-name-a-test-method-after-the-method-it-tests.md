# 603: Name a Test Method After the Method It Tests

Name a test method `test` followed by the exact name of the method it tests, including that method's own `is` or `has` prefix. The test for `isQuotaExceeded()` is `testIsQuotaExceeded`, not `testQuotaExceeded`; the test for `isValidConnection()` is `testIsValidConnection`. Append a scenario qualifier for a variant, as in `testIsValidConnectionWithNullAPIKey`.

When the qualifier names the state of a property, phrase it as `When<Subject><Property>Is<State>`: `testGetFooWhenBarBazIsNull`, not `testGetFooWhenBarHasNullBaz`, and `testGetFooWhenBarBazIsValid`, not `testGetFooWhenBarHasValidBaz`.

**Rationale:** A test name that matches the method under test lets a reader jump from a failing test straight to the method, and it keeps the suite's names parallel to the API. Dropping the `is`, writing `testQuotaExceeded` for `isQuotaExceeded`, breaks that mapping and hides which method the test covers. Putting the property before its state in the qualifier makes a sorted method list group the variants by the property under test, and the name reads as a subject, verb, complement sentence.

A violation is a test method whose name does not begin with `test` plus the method it tests, most commonly one that omits the `is` or `has` prefix of the method under test, or a scenario qualifier that puts the state before the property, as in `WhenBarHasNullBaz` for `WhenBarBazIsNull`.

**Example:** for a method `isQuotaExceeded()`, the test `testQuotaExceeded` should be `testIsQuotaExceeded`, mirroring how `testIsValidConnection` maps to `isValidConnection()`. A qualifier that names the state of a property puts the property first.

```diff
-public void testGetFooWhenBarHasNullBaz() throws Exception {
+public void testGetFooWhenBarBazIsNull() throws Exception {
```

```diff
-public void testGetFooWhenBarHasValidBaz() throws Exception {
+public void testGetFooWhenBarBazIsValid() throws Exception {
```