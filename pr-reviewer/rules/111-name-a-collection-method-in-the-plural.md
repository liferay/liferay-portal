# 111: Name a Collection Method in the Plural

When a method returns a collection — a `List`, `Set`, array, or `Page` — name it with the plural of what it returns, `getFoos`, so the name itself signals the return type. A singular or collective noun on a collection return, such as `getFooOverview` for a `List<Foo>`, hides the plurality.

**Rationale:** A reader predicts the return type from the name without checking the signature, and a singular sounding name on a collection return forces a second look. The plural also matches the service layer, where `getUsers`, `getEntries`, and `getCategories` return collections and the singular form returns one element.

A violation is a method introduced in the diff whose return type is a collection and whose name is singular or uses a collective noun in place of the plural of the element type. A method that returns a single element by lookup (`getFoo`), a count (`getFoosCount`, per rule 114), or a plural that already matches its return is not a violation.

This is the method side of rule 103, which names a variable after what it holds, and of rule 107, which names a `Page<T>` local in the plural of the method that returns it. Commit `84d9629`, cited in rule 103, applied the same reasoning to a method returning a `Page`, renaming `getAgentDefinitions` to `getAgentDefinitionsPage`.

**Example:** a service method returning `List<Foo>` was named `getFooOverview`; the fix renamed it `getFoos`.

```diff
-public List<Foo> getFooOverview() {
+public List<Foo> getFoos() {
 	return _fooLocalService.getFoos();
 }
```