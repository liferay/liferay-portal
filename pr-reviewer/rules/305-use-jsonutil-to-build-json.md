# 305: Use JSONUtil to Build JSON

Assemble JSON with the `JSONUtil` helpers (`JSONUtil.put`, `JSONUtil.toJSONArray`, `JSONUtil.toList`, and so on) rather than injecting a `JSONFactory` and building `JSONObject` and `JSONArray` instances by hand.

When the JSON is built only to be serialized, chain `toString()` onto the `JSONUtil` expression instead of assigning it to a local that the next statement only serializes: `return JSONUtil.put(...).toString();`, not `JSONObject jsonObject = JSONUtil.put(...);` followed by `return jsonObject.toString();`. Chaining here is allowed because `JSONObject` and `JSONArray` are fluent types under rule 402, and it is the form the codebase already uses, as in `FileEntryAMImageURLItemSelectorReturnTypeResolver`. This applies to JSON only: a local of another type that is used once is governed by rule 503.

**Rationale:** `JSONUtil` builds the same structures in a single fluent expression and removes the need to inject and reference a `JSONFactory`, so the construction reads as data rather than as a sequence of `put` calls on a mutable object. A local that exists only to be serialized on the next line adds a name and a hop to that expression for nothing.

A violation is JSON assembled by injecting `JSONFactory`, or by creating and populating `JSONObject` or `JSONArray` by hand, where the `JSONUtil` helpers apply; or a `JSONObject` or `JSONArray` built with `JSONUtil` into a local whose only use is a `toString()` call on the next statement. Do not flag a JSON local that is changed or read again before it is serialized, such as a `JSONArray` filled by `put` calls in a loop.

**Example:** commit `4705fc4` replaced `_jsonFactory.createJSONObject().put(...)` with `JSONUtil.put(...)` and dropped the `@Reference JSONFactory`; `c142075` replaced manual population with `JSONUtil.toList`; `20ad3779` replaced a hand built `JSONArray` with `JSONUtil.toJSONArray`. The diff below serializes a `JSONUtil.put` chain directly instead of through a local.

```diff
-JSONObject jsonObject = JSONUtil.put(
+return JSONUtil.put(
 	"id", foo.getId()
 ).put(
 	"name", foo.getName()
-);
-
-return jsonObject.toString();
+).toString();
```