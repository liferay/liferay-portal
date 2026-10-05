# 110: Name a Map Entry Loop Variable Entry

When iterating a map's `entrySet()`, name the loop variable `entry` and name the parts pulled out of it after the data they hold — typically `key` or `name`, and `value` — rather than prefixing them with the surrounding domain, as in `argumentEntry`, `paramName`, and `paramValue`.

**Rationale:** Every map iteration then reads in one shape whatever the domain, so a reader who has seen one has seen them all, and the domain word a prefix would repeat is already in the map's own name on the line above.

A violation is a `Map.Entry` loop variable named anything other than `entry`, or a key or value local that carries a domain prefix the enclosing map's name already supplies. When two entry loops nest, the inner variables need a distinguishing name and are not a violation.

**Example:** a loop over an `arguments` map named its variable `argumentEntry` and its parts `paramName` and `paramValue`; the fix renamed them `entry`, `name`, and `value`.

```diff
-for (Map.Entry<String, Object> fooEntry : foos.entrySet()) {
-	String fooName = fooEntry.getKey();
-	Object fooValue = fooEntry.getValue();
+for (Map.Entry<String, Object> entry : foos.entrySet()) {
+	String name = entry.getKey();
+	Object value = entry.getValue();
```