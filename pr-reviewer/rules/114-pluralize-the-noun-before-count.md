# 114: Pluralize the Noun Before Count

When a name ends in `Count` (or `_COUNT`) and counts how many members a collection has, the noun before `Count` is plural: `maxFoosCount`, `itemsCount`, `getEntriesCount()`, `MAX_FOOS_COUNT`. This applies to variables, fields, constructor and method parameters, getters, constants, and object field names alike, and it reaches resource files: an Objects field's `name`, `externalReferenceCode`, and `label` all carry the same noun, so all three change together.

A name whose noun is not a countable thing being counted is unaffected: `totalCount`, `maxCharacterCount`, and `dynamicQueryCount` are correct as written.

**Rationale:** Liferay names a count after the collection it measures, which is why the service layer is full of `getUsersCount`, `getEntriesCount`, `getCategoriesCount`, and `getItemsCount`. A singular noun reads as "the count belonging to one document" rather than "how many documents", and it makes the name inconsistent with every sibling count in the codebase.

A violation is a `Count` or `_COUNT` suffixed name introduced in the diff whose preceding noun is singular although it names the members being counted, such as `maxDocumentCount` or `entryCount`. Do not flag a count of an uncountable quantity or of a total (`maxCharacterCount`, `totalCount`), or a name whose noun qualifies the count rather than naming what is counted (`dynamicQueryCount`).

**Example:** PR 49961 (`LPD-101110`) in `brianchandotcom/liferay-portal-ee` was closed with "Can you make it maxDocumentsCount and resend"; the fix renamed `maxDocumentCount` to `maxDocumentsCount` in the parameter, the field, and the Objects field definition together.

```diff
-		int maxFooCount, int maxBarCountPerFoo,
+		int maxFoosCount, int maxBarsCountPerFoo,
```

```diff
-	private final int _maxFooCount;
+	private final int _maxFoosCount;
```

```diff
-			"externalReferenceCode": "MAX_FOO_COUNT",
+			"externalReferenceCode": "MAX_FOOS_COUNT",
 			"label": {
-				"en_US": "Max Foo Count"
+				"en_US": "Max Foos Count"
 			},
-			"name": "maxFooCount",
+			"name": "maxFoosCount",
```