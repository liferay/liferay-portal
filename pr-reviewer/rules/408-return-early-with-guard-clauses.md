# 408: Return Early With Guard Clauses

When a method or loop body does its real work only under a condition, invert the condition and leave early with `return`, `continue`, `break`, or `throw`, so the work that follows sits at the outer indentation level instead of inside a positive `if`. A guard clause names the cases the code does not handle and dismisses them at the top; the remaining body then reads straight down.

**Rationale:** Nesting the bulk of a method inside a positive condition pushes the code that matters one level to the right and makes the reader carry the condition all the way to the closing brace to know where it stops. Exiting early flattens the body into one column, lets every guard be read and forgotten in place, and keeps the main path from being buried under the exceptional cases. Adding a second condition later costs one more guard line rather than one more level of nesting.

A violation is an `if` with no `else` whose block holds most of the enclosing method or loop body, where inverting the condition and exiting early would flatten the block. Do not flag a short `if` whose body is a few lines, an `if` with an `else` branch of comparable weight, or a positive condition that wraps only part of the body with unconditional work after it, since none of those gains anything from inversion.

**Example:** a loop that checked `(foo != null) && foo.isEnabled()` and put thirty lines of work inside that `if` became a loop that opens with `if ((foo == null) || !foo.isEnabled()) { continue; }` and then does the work at the loop's own indentation.

```diff
 for (Foo foo : foos) {
-	if ((foo != null) && foo.isEnabled()) {
-		// Thirty lines of work
-	}
+	if ((foo == null) || !foo.isEnabled()) {
+		continue;
+	}
+
+	// Thirty lines of work
 }
```