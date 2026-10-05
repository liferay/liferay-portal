# 205: Sort Comment Labelled Blocks

When a method body is divided into scenario blocks, each introduced by its own inline comment, order the blocks alphabetically by the comment's text. This extends rule 202 to statement blocks inside a method body: rule 202 covers method calls, parameters, sibling declarations, and literal lists, and does not reach comment labelled blocks. Reordering the blocks carries the first local variable declaration with them: the variable is declared in whichever block now comes first and reassigned in the rest, so a block that moves to the front takes the declaration and a block that moves down loses it, per rule 203.

When the blocks differ only by one literal argument, the comments are noise: drop them and sort the blocks by that literal instead.

**Rationale:** Scenario blocks in a test read as a list, and an arbitrary list order forces the reader to scan every block to find one scenario and drops new scenarios into random positions, which makes diffs noisier. Alphabetical order gives every scenario one predictable place.

A violation is a set of sibling comment labelled blocks left in an arbitrary order when nothing in the code forces one block to run before another. Do not flag blocks whose order a dependency already fixes — an "Add" block that creates an entity, followed by the "Update" and "Delete" blocks that act on it — even though the comments read out of alphabetical order.

**Example:** PR 49583 in `brianchandotcom/liferay-portal-ee` was closed because its comments were not sorted, and fixed in PR 49597 (`LPD-95940`, commit `56d343a`), which moved the "Tag only" block of `_testPostAgentInstanceWithTypeCategorizationIntent` from the top of the method to the bottom, after "Passthrough"; commit `d43795e` then dropped the comments from the same method and ordered its blocks by their `"message"` literal.

```diff
 private void _testFoo() throws Exception {

-	// Gamma
-
-	String data = _postFoo("gamma");
-
-	_assertContains(data, "gamma");
-
 	// Alpha

-	data = _postFoo("alpha");
+	String data = _postFoo("alpha");

 	_assertContains(data, "alpha");

 	// Beta

 	data = _postFoo("beta");

 	_assertContains(data, "beta");
+
+	// Gamma
+
+	data = _postFoo("gamma");
+
+	_assertContains(data, "gamma");
 }
```