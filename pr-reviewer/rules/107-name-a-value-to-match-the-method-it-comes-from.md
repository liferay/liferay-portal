# 107: Name a Value to Match the Method It Comes From

Name a variable, and a test, to mirror the method it derives from or asserts on, taking that method's own wording even when a more literal term exists. The value returned by `getStatus()` is `status`, not `statusCode`, because the method is `getStatus`; a test that checks `getStatus()` returns not found is `testStatusNotFound`, not `testNotFoundResponse`. Matching the method keeps one vocabulary from call to value to test.

When the value then flows into a destination that uses a different conceptual name — a JSON key, a parameter, an output column — match the destination instead, with a type suffix per rule 104 if a class owns the plain name. For example, `String startDateString = jobStatus.getStartTime();` followed by `patchJSONObject.put("startDate", startDateString);` follows the JSON key `startDate` rather than the source method `getStartTime`, suffixed with `String` because `startDate` otherwise reads as a `Date`. Likewise `String errorMessage = jobCondition.getMessage();` followed by `patchJSONObject.put("errorMessage", errorMessage);` keeps the destination key `errorMessage` rather than the generic source `message`.

The same destination rule covers a method parameter: a local passed as `_portal.getPortalServerPort(secure)` is `secure`, not `httpsEnabled`, and a delegating method's own parameter takes the name of the parameter it hands the value to, `defaultValue` rather than `value` when the body passes it on as the default value. The source rule covers a `Page<T>` too: it takes the plural of the method that returned it, so `getBarFoosPage` yields `foosPage`, not `fooPage`. When you rename a local, propagate the new name to every call site, every parameter that passes it on, and every private helper that receives it.

**Rationale:** When the name of a value echoes the method that produced it, the reader connects the two without a second thought, and the whole path through the code uses one word for one thing. Switching to a different, even more precise, term for the same value forces the reader to bridge the two names and invites drift as the code grows.

A violation is a variable or test named with a synonym or a vaguer term than the method it comes from, such as `statusCode` for the result of `getStatus()` or `testNotFoundResponse` for a check on `getStatus()`, where the method's own word would match; a local named with a synonym of the parameter it is passed as; or a `Page<T>` named in the singular. A variable that matches a destination key with a type suffix per rule 104 is not a violation, even when the source method uses a different word.

**Example:** commit `92700dd` renamed `testNotFoundResponse` and its helper to `testStatusNotFound` to match `getStatus`, and named the value `status` rather than `statusCode` for the same reason. Commit `c75e33e` renamed local Strings from `getStartTime()` / `getCompletionTime()` / `getLastTransitionTime()` to `startDateString` and `endDateString` because the values flow into JSON keys `startDate` and `endDate`; the same commit kept `errorMessage` from `getMessage()` because the JSON key is `errorMessage`. The diffs below take the name from the assigned expression, from the parameter the value is passed as, and from the plural method that returns a `Page<T>`.

```diff
-long[] currentAndAncestorGroupIds = getReferencedGroupIds();
+long[] referencedGroupIds = getReferencedGroupIds();
```

```diff
-boolean httpsEnabled = _isHTTPSEnabled();
+boolean secure = _isSecure();

 String baseURL = _portal.getPortalURL(
 	company.getVirtualHostname(),
-	_portal.getPortalServerPort(httpsEnabled), httpsEnabled);
+	_portal.getPortalServerPort(secure), secure);
```

```diff
-Page<Foo> fooPage = service.getBarFoosPage(barId, pagination);
+Page<Foo> foosPage = service.getBarFoosPage(barId, pagination);

-for (Foo foo : fooPage.getItems()) {
+for (Foo foo : foosPage.getItems()) {
```