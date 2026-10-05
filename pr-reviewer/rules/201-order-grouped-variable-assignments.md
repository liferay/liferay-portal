# 201: Order Grouped Variable Assignments

When several variable assignments are grouped together as a block — for example a FreeMarker `[#assign]` block, a shell `local` declaration that lists several names, or a cluster of constant or field declarations — put them in a deliberate order rather than the order in which they were written.

Two ordering principles apply:

- Sort independent assignments alphabetically by variable name.
- Place a derived assignment — one whose value is computed from earlier variables in the same block — after the variables it depends on. Separate it from the sorted block and put it next to its first use rather than wedging it into the alphabetical order.

**Rationale:** A block left in the order it was typed is effectively random, which raises the chaos factor of the codebase: a reader must scan the whole block to find a given variable, and cannot tell which values are independent inputs and which are computed from others. Sorting the independent assignments makes any one easy to locate and signals that they are peers with no ordering relationship between them. Pulling a derived assignment out and placing it after its dependencies, next to where it is used, makes the data flow visible — the reader can see at a glance that the value is computed rather than an input, and what it is computed from.

This rule applies only to assignments that are already grouped into a consecutive block. It does not require grouping assignments that are better declared at their first use, and it never overrides a necessary dependency order: when one assignment uses the value of another, the dependency wins over alphabetical order.

A grouped block of Java local declarations sorts the same way, and this is where the rule meets rule 203. Since rule 203 declares each local as it is used, locals form a grouped block only when they are used together, as when they all feed one call; inside such a block this rule's alphabetical order wins, and between blocks rule 203's order of use wins.

A violation is a block of consecutive, independent assignments left in an order that is not alphabetical, or a derived assignment sorted ahead of or interleaved with the variables it depends on instead of being grouped after them.

**Example:** https://github.com/brianchandotcom/liferay-portal/pull/175376 — in the `phone-number-input` fragment a block of `[#assign]` statements was in arbitrary order and was sorted alphabetically (`countryA2`, `countrySource`, `defaultLanguageId`, `disabled`, ...). The derived `fixed = countrySource == "fixed"` was then moved out of the sorted block and placed after it, next to the `[#if fixed ...]` that uses it, because it depends on `countrySource`. A grouped pair of Java locals that fed the same call was sorted the same way.

```diff
-Map<X, Y> beta = new HashMap<>();
-
 boolean alpha = check();
+Map<X, Y> beta = new HashMap<>();
```