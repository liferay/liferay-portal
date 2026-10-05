# 713: Name Products Consistently in Language Values

In `Language.properties`, a value that references a named product, feature, or field uses its full official name, and uses it identically everywhere it appears, across sibling keys and within a single value. When the field's official name is "Google Model Armor Location", write "the Google Model Armor location" in the same sentence as "existing Google Model Armor templates"; do not shorten it to "the Model Armor location" on one mention.

Fixing the English value renames the key, which stays the kebab case of the value. Make the change following `.claude/rules/language.md`, which names the global file, its order, and how to regenerate the locale files. Update every code reference to the old key as well: `Meta.AD` name and description attributes, `Liferay.Language.get` calls, and JSP and TSX lookups, including the ones in the other repository of a `liferay-portal` and `liferay-portal-ee` pair.

**Rationale:** Product and field names are part of the UI contract; a shortened or drifting name reads as a different feature to the user. Because Liferay keys mirror their English values, a wording fix is never value only: a stale key that no longer matches its value is itself a defect, and a renamed key silently breaks every referencing lookup until those are updated too.

A violation is a value that shortens a product, feature, or field name on one mention while spelling it out on another, or that disagrees with the sibling key defining the name; and, once the value is fixed, a key left unrenamed or a code reference left pointing at the old key. A generic noun that is not part of an official name, such as `select-a-location=Select a Location`, needs no expansion and is not a violation.

**Example:** on https://github.com/brianchandotcom/liferay-portal/pull/180596, Brian Chan flagged that "Changing the Model Armor location..." must read "Changing the Google Model Armor location..."; the fix on the `LPD-98999` branches renamed the key in `liferay-portal` and updated the `VertexAIConfiguration` description key in `liferay-portal-ee`.

```diff
-changing-the-foo-location-can-impact-existing-bar-foo-templates=Changing the Foo location can impact existing Bar Foo templates.
+changing-the-bar-foo-location-can-impact-existing-bar-foo-templates=Changing the Bar Foo location can impact existing Bar Foo templates.
```

The referencing code follows the key rename.

```diff
 @Meta.AD(
 	deflt = "foo",
-	description = "changing-the-foo-location-can-impact-existing-bar-foo-templates",
+	description = "changing-the-bar-foo-location-can-impact-existing-bar-foo-templates",
 	name = "bar-foo-location", required = false
 )
```