# 909: Separate Logical Groups With Blank Lines

A blank line marks the boundary of a logical group; the statements inside a group read as one step. Statements that already form one step (parallel declarations, parallel assertions on parallel results, a run of setter calls on one receiver) sit on adjacent lines with no blank line between them, and within the group the order is deterministic: alphabetical, argument order, or call order, per rules 201 and 202. When statements cannot be ordered, split them into separate groups with a blank line so the reader knows the order is intentional rather than arbitrary. When one setup block finishes (configuring a context object, stubbing a service, building a fixture), a single blank line closes it so the next block reads as a new paragraph.

This rule and rule 203 divide the work: rule 203 sets a declaration off from the statement that consumes it with a blank line, so the declaration and its use form two short paragraphs, while this rule keeps sibling statements of the same kind together inside one paragraph. Two parallel declarations are therefore adjacent, and the blank line comes after the pair, before the first statement that uses either of them. Where the source formatter's own blank line checks disagree with either rule, the formatter wins.

**Rationale:** Blank lines are the only paragraph marks Java gives a method body. Used consistently they let a reader see the shape of the method (setup, action, assertions) before reading a word of it, and they encode which statements belong together. A blank line inside a pair that reads as one step breaks the pair into two unrelated thoughts; a missing blank line between two setup blocks runs two thoughts together.

A violation is a blank line between two statements that form one step (paired declarations, paired assertions on parallel values, consecutive setter calls on one object), the absence of a blank line where one logical setup block ends and the next begins, or a group of unrelated statements packed together in no order the reader can verify. Do not flag a blank line between a declaration and its first use, which rule 203 requires, or spacing the source formatter produced.

**Example:** `Foo first = create("first");` and `Foo second = create("second");` lost the blank line between them, two `Assert.assertEquals` calls on `alpha` and `beta` did the same, and a blank line was added between the last setter on `foo` and the `Bar` declaration that starts the next block, while the blank line between `Bar bar = new Bar();` and its first setter stays, per rule 203.

```diff
 Foo first = create("first");
-
 Foo second = create("second");
```

```diff
 Assert.assertEquals("alpha", alpha.getName());
-
 Assert.assertEquals("beta", beta.getName());
```

```diff
 foo.setAlpha(alpha);
 foo.setBeta(beta);
+
 Bar bar = new Bar();

 bar.setGamma(gamma);
```