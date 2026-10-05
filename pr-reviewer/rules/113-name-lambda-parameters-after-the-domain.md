# 113: Name Lambda Parameters After the Domain

Name a lambda parameter after the value it stands for, as you would a local per rule 103, rather than a single letter such as `k`, `v`, or `e`. In `fooMap.computeIfAbsent(name, k -> new HashSet<>())`, the parameter is a foo name, so it is `fooName`.

**Rationale:** A bare letter forces the reader to scroll up to the enclosing call to learn what it represents, while a domain name reads on its own line. A lambda parameter is a variable like any other, and the naming rules that make a local readable apply to it unchanged.

A violation is a lambda parameter introduced in the diff that is a single letter or an abbreviation with no meaning outside the enclosing call. Do not flag a parameter whose name already follows another naming rule, such as `entry` for a `Map.Entry` per rule 110 or the plain type name when one instance is in scope per rule 103. The domain name replaces a meaningless letter; it is not a prefix to add to a name that is already plain, so the `key` and `value` pulled out of an entry stay plain per rule 110.

**Example:** a `computeIfAbsent` call named its key parameter `k`; the fix renamed it after the value the key holds.

```diff
-fooMap.computeIfAbsent(name, k -> new HashSet<>());
+fooMap.computeIfAbsent(name, fooName -> new HashSet<>());
```