# 112: Give Paired Methods the Same Suffix

When two methods form a pair — a fetch and its count, or a lookup by key and its count — their names differ only by the operation noun and share every qualifier. `getFoosByGroupIds` pairs with `getFoosCountByGroupIds`, not with `getFoosCount`, which reads as the count of a different, unqualified fetch.

**Rationale:** A mismatched suffix hides the pairing from `git grep` and from the reader: a search for `ByGroupIds` finds one half of the pair, and the reader of the count method cannot tell which fetch it counts without comparing parameter lists.

A violation is a method whose name pairs with a sibling, such as a count beside its fetch, but drops or reorders a qualifier the sibling carries. Two methods that take different inputs and only happen to share a stem are not a pair and are not a violation.

**Example:** a count method was named `getFoosCount` beside the `getFoosByGroupIds` fetch it counted; the fix renamed it `getFoosCountByGroupIds`.

```diff
 public List<Foo> getFoosByGroupIds(long[] groupIds);
-public int getFoosCount(long[] groupIds);
+public int getFoosCountByGroupIds(long[] groupIds);
```