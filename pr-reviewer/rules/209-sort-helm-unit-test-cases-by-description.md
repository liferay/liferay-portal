# 209: Sort Helm Unit Test Cases by Description

A `helm unittest` suite (a `*_test.yaml` file under a chart's `tests` directory) lists its cases under `tests`. The cases are independent of one another, so sort them alphabetically by their `it` description in case sensitive ASCII order — the order rule 202 applies to any other sortable sequence — rather than by the document a case selects or the assertion it makes.

**Rationale:** The `it` description is the line a reader scans for, while the `asserts` and `set` keys vary in shape and length from case to case. Sorting by the description gives every new case one obvious place and keeps a suite of twenty cases scannable.

A violation is a `tests` list in a `*_test.yaml` Helm unit test file whose cases are not in case sensitive ASCII order of their `it` descriptions. Do not flag the key order inside a single case, and do not flag a list whose descriptions are already in order even where the `set` values or the templates the cases select look unsorted.

**Example:** a suite listed a case that omits a resource before the case that names it; the fix moved the "Omits" case after the "Names" case. The indentation is spaces because YAML forbids tabs.

```diff
 tests:
-    -   asserts:
-            -   hasDocuments:
-                    count: 0
-        it: Omits the resource when the feature is disabled
-        set:
-            foo.enabled: false
     -   asserts:
             -   equal:
                     path: metadata.name
                     value: foo
         it: Names the resource after the release
         set:
             foo.enabled: true
+    -   asserts:
+            -   hasDocuments:
+                    count: 0
+        it: Omits the resource when the feature is disabled
+        set:
+            foo.enabled: false
```