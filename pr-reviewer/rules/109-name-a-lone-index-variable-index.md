# 109: Name a Lone Index Variable Index

When a method scope holds a single `int` index — the result of `indexOf`, `lastIndexOf`, or a similar lookup — name it plainly `index` rather than qualifying it, as in `spaceIndex` or `slashIndex`. When a later lookup in the same scope supersedes the value, reassign `index` instead of introducing a parallel qualified name such as `firstSlashIndex` beside `secondSlashIndex`.

**Rationale:** A qualifier repeats what the lookup expression on the same line already says, and the bare `index` is the dominant convention in the codebase, so a reader recognizes it without a second thought. Reusing the one slot for sequential lookups keeps the scope to a single index and makes plain that each lookup replaces the last rather than adding a second value to track.

A violation is a lone `int` index variable in a scope that carries a qualifier, such as `spaceIndex`, or two index variables in one scope that hold successive lookups where reassigning `index` would do. Two indexes that are both live at once, such as a start and an end read together in one `substring` call, are not a violation and keep names that tell them apart.

**Example:** a helper that split a value on its first space named the index `spaceIndex` and read it in two `substring` calls; the fix renamed it `index`. A second lookup in the same scope reuses the slot.

```diff
-int spaceIndex = foo.indexOf(' ');
+int index = foo.indexOf(' ');

-String prefix = foo.substring(0, spaceIndex);
-String suffix = foo.substring(spaceIndex + 1);
+String prefix = foo.substring(0, index);
+String suffix = foo.substring(index + 1);
```

```diff
-int firstSlashIndex = foo.indexOf('/', 1);
-int secondSlashIndex = foo.indexOf('/', firstSlashIndex + 1);
+int index = foo.indexOf('/', 1);
+index = foo.indexOf('/', index + 1);
```