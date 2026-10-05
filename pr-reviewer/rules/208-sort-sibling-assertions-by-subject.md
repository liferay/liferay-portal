# 208: Sort Sibling Assertions by Subject

When consecutive assertions check different keys, fields, or values of one subject, sort them by the argument that names what is being checked — the string literal, key, or field a reader scans for — and ignore the assertion verb (`assertTrue`, `assertFalse`, `assertEquals`, `assertNull`) and the accessor (`get`, `contains`, `has`) when those vary from line to line. When every line shares the same verb and accessor, the same key still decides: `map.get("apple")`, `map.get("banana")`, `map.get("cherry")`, not the expected values `1L`, `3L`, `2L` in the order they were typed.

This is the boundary of rule 207. In a run of same helper calls every difference sits in the arguments, and the left to right order of those arguments is the identity of the case, so the flattened text is the right key. In a run of assertions the verb states the expected outcome and the accessor the mechanism; neither identifies the case. The subject does, and it sits to the right of both, so a text sort would let the verb dominate and give the wrong order. Brian Chan's framing: sort as if a human were reading the list.

**Rationale:** A reader hunting for the assertion about one key scans the keys, and a list in key order shows at a glance which key is checked, which is missing, and where a new one goes. Grouping by verb splits one subject list into two runs and hides a missing key. It also churns: when an expectation flips from `assertFalse` to `assertTrue`, verb order moves the line while subject order leaves it in place, so the diff shows the one changed character instead of a relocation.

A violation is such a block ordered by verb or accessor — every `assertFalse` before the lone `assertTrue`, or `contains` before `get` — or left in the order it was typed, so that the subjects fall out of order. Do not flag `assertTrue` following `assertFalse`, or `contains` between two `get` calls, when the subjects are in order; that is the correct state. Do not reorder assertions that guard one another, such as `Assert.assertEquals(1, items.size());` before `Assert.assertEquals("alpha", items.get(0));`; that dependency wins.

**Example:** Brian Chan's own sketch, on a call following his review of PR 50573 (`LPD-104430`) in `brianchandotcom/liferay-portal-ee`, of how he reads such a block: sorted on `a`, `b`, `c`, `d`, `e`, with the odd verb and the odd accessor staying where their keys put them. There is no fixing commit, since the sketch clarifies the sort key rather than correcting a specific PR. Sorted by verb and accessor, the way a plain line sort leaves it, the keys read `c`, `b`, `d`, `e`, `a`; sorted by subject they read `a` to `e`. The second block shows the same key order with one verb throughout.

```diff
-Assert.assertFalse(foo.contains("c"));
-Assert.assertFalse(foo.get("b"));
-Assert.assertFalse(foo.get("d"));
-Assert.assertFalse(foo.get("e"));
-Assert.assertTrue(foo.get("a"));
+Assert.assertTrue(foo.get("a"));
+Assert.assertFalse(foo.get("b"));
+Assert.assertFalse(foo.contains("c"));
+Assert.assertFalse(foo.get("d"));
+Assert.assertFalse(foo.get("e"));
```

```diff
 Assert.assertEquals(1L, map.get("apple"));
-Assert.assertEquals(3L, map.get("cherry"));
 Assert.assertEquals(2L, map.get("banana"));
+Assert.assertEquals(3L, map.get("cherry"));
```