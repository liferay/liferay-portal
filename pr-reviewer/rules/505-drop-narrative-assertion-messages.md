# 505: Drop Narrative Assertion Messages

Do not pass an explanatory message to `Assert.assertTrue` or `Assert.assertFalse`. The predicate is the message: a failing `assertTrue(filterString.contains(...))` already names the line, the value under test, and the condition, so a sentence that restates them in prose, often built with a `StringBundler`, adds a wrapped argument list and nothing a reader learns from it.

This rule stops at `assertTrue` and `assertFalse`. The source formatter's `AssertEqualsCheck` goes the other way for `Assert.assertEquals`: when the actual value is a `Hits` length, an array `length`, or a collection `size()`, it demands a leading message argument (`hits.toString()`, `Arrays.toString(array)`, or `collection.toString()`) so the failure prints the contents that produced the wrong count. Leave those messages in place.

**Rationale:** A message on a boolean assertion can only repeat the predicate or paraphrase it, and a paraphrase drifts from the code it describes the first time the predicate changes. The failing line and its stack frame carry the diagnostic; the test reads shorter and the assertion fits on one line. `assertEquals` on a count is different because the count says nothing about which elements were wrong, which is why the formatter asks for the contents there and only there.

A violation is an `Assert.assertTrue` or `Assert.assertFalse` call with a message argument, whether a literal, a concatenation, or a `StringBundler.concat`. Do not flag a message on `Assert.assertEquals` or `Assert.assertNotNull`, or the message that is `Assert.fail`'s only argument, and never remove a message that `AssertEqualsCheck` requires.

**Example:** an `Assert.assertTrue` whose first argument was a `StringBundler.concat` sentence explaining that the lookup must use the entity's ID and quoting the actual filter was reduced to `Assert.assertTrue(filterString.contains("(id=" + id + ")"))`.

```diff
-Assert.assertTrue(
-	StringBundler.concat(
-		"Lookup must use the entity's ", id,
-		". Actual filter was: ", filterString),
-	filterString.contains("(id=" + id + ")"));
+Assert.assertTrue(filterString.contains("(id=" + id + ")"));
```