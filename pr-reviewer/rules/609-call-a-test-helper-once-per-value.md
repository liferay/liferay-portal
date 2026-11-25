# 609: Call a Test Helper Once per Value

When a test checks the same behavior for several values, call the assertion or test helper once per value, one statement per call. Do not collect the values in an array, `List.of`, `Arrays.asList`, or another literal built in place only to iterate over it with a `for` loop or `forEach`.

**Rationale:** Explicit calls turn the cases into a list a reader scans like a table, a run that rule 207 can sort, and a failure whose line number names the case that broke. A loop hides the cases inside one statement, reports every failure on the same line, and invites a nested loop that multiplies cases nobody chose on purpose.

A violation is a `for` loop or `forEach` in a test whose iterable is an array or collection literal written in place, such as `new String[] {...}`, `List.of(...)`, or `Arrays.asList(...)`, and whose body only exercises the code under test with each element. Replace it with one call per value, extracting a private helper that takes the varying value when the body is more than one statement (rule 601). Do not flag a loop over data the test did not write as a literal, such as the items of a page or a list returned by the code under test, a loop that builds fixtures (for example adding several entries before checking a count), or a loop over a range whose size is itself what the test checks.

**Example:** PR 53037 (`LPD-116168`) in `brianchandotcom/liferay-portal-ee`, where Brian Chan asked to "call multiple methods" and "don't use an array" for a listener test that looped over the names it expected to be refused. The fix replaced every such loop in the class with two private assertion helpers, each called once per value, and turned a nested loop over three original names and three blank names into five explicit calls.

```diff
-	for (String bar : new String[] {"", " ", null}) {
-		_assertFoo(originalBaz, bar);
-	}
+	_assertFoo(originalBaz, " ");
+	_assertFoo(originalBaz, "");
+	_assertFoo(originalBaz, null);
```