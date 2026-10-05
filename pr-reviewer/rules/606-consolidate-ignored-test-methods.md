# 606: Consolidate Ignored Test Methods

A test class carries `@Ignore` on one `@Test` method per subject, not on each scenario. When sibling `@Test` methods that exercise the same subject are all annotated with `@Ignore`, keep one `@Test` method, convert the rest to `private void _test<SameName>()` helpers, and call them from the retained method. Place the calls at the end of the retained method, after its own assertions, set off by a blank line, and sort them (rule 202). The retained method keeps its `@Ignore`; consolidating does not turn the set on.

This is a different trigger from rule 601, which weighs consolidation by triviality and accepts a handful of well named scenario methods. Under this rule the repeated `@Ignore` is what forces the consolidation, so scenarios with substantial bodies and their own setup fold in too. Keep the helper naming of rule 601: the private method takes the name of the `@Test` method it replaces, prefixed with `_`.

**Rationale:** `@Ignore` on an integration test means it runs by hand only, and a person doing that has to find and remove the annotation from every method in the set, then remember to put the annotations back. One `@Test` makes it one switch, and the private helpers keep each scenario named and separately readable. Repeating the annotation also hides that the scenarios are one flow over one subject, which is the thing the reader most needs to know.

A violation is two or more `@Test` methods annotated with `@Ignore` that cover scenarios of the same subject in one class. A single ignored test method is not a violation, and sibling scenario methods that actually run are governed by rule 601, not by this rule.

**Example:** on https://github.com/brianchandotcom/liferay-portal-ee/pull/50028 (`LPD-101112`), Brian Chan commented "I went ahead and did the SF" and fixed it himself in commit `93ad072` ("LPD-101112 SF"), folding two ignored `@Test` methods of `MessageResourceTest` into `testPostChatByExternalReferenceCodeMessage` as private helpers called from the retained method.

```diff
 	@Ignore

 	@Test
 	public void testGetFoo() throws Exception {

 		// Assertions for the base scenario

+		_testGetFooWithBar();
+		_testGetFooWithBaz();
 	}

-	@Ignore
-
-	@Test
-	public void testGetFooWithBar() throws Exception {
+	private void _testGetFooWithBar() throws Exception {
```