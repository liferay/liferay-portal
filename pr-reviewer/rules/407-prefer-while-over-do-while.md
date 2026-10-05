# 407: Prefer While Over Do While

Write a loop as `while (condition) { ... }`, checking its guard before the body runs, rather than as `do { ... } while (condition);`. Reserve a `do` loop for the case where the body must run once before the guard can be evaluated at all, because the guard depends on a value that only the body computes.

**Rationale:** A `while` loop puts the exit condition where the reader meets the loop, so the guard is known before the body is read, and it matches the dominant form in the codebase. A `do` loop hides the guard at the bottom, forces the reader to hold the whole body in mind until the condition appears, and guarantees one execution whether or not that was intended, which is a bug the first time the input is empty or the first check would have failed.

A violation is a `do { ... } while (condition);` loop whose condition could be evaluated before the first iteration with the same result, so the body does not need to run to make the guard meaningful. Do not flag a `do` loop whose guard reads a value that only the body produces, such as a paging loop that must fetch the first page before it can ask whether a next page exists.

**Example:** a loop written as `do { advance(); } while (!done());` ran `advance()` once before it ever asked whether the work was done; `while (!done()) { advance(); }` asks first and reads the same way as every other loop in the file.

```diff
-do {
-	advance();
-}
-while (!done());
+while (!done()) {
+	advance();
+}
```