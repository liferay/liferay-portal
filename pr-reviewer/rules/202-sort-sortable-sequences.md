# 202: Sort Sortable Sequences Alphabetically

When a sequence of sibling items has no order imposed by dependencies or logic, sort it alphabetically. This applies to consecutive method calls on the same object, the parameters of a method, sibling declarations such as fields, methods, and functions, and lists of string or key literals. The Liferay source formatter sorts variable, field, and method names case insensitively in natural order (see `JavaTermComparator` in `modules/util/source-formatter`), so `_criterions` sorts before `_criterionType` because case is ignored. Literal strings sort case sensitively in ASCII order, with spaces and digits before letters, uppercase letters before lowercase, and `null` last. A builder chain sorts too: the calls between the chain's opening call and its terminal `build()` are alphabetical. A contiguous block of setter calls on one object sorts by method name and then by argument.

Several constraints override the default sort. Setter calls on a generated model mirror the defining schema rather than sort alphabetically: a ServiceBuilder entity follows the column order in its `service.xml`, and a REST DTO follows the field order in its `rest-openapi.yaml`; the source formatter's `JavaServiceObjectCheck` rewrites an entity setter block into that order, so alphabetizing it by hand is reverted on the next run. An entry that must come first or last — an initialization step, a default, an "all" option — is separated from the sorted block by a blank line, so the reader knows the leading or trailing position is intentional and the rest of the order carries no further meaning. In a constructor body, assignments from constructor parameters come first and follow the parameter order, with any derived assignments after them (see `JavaConstructorParametersCheck` in the same source formatter directory). A vararg parameter is forced to the end of a parameter list by the Java language. A group of functions or methods first orders public before private, then sorts alphabetically within each group; see rule 907.

**Rationale:** A sequence in arbitrary order forces the reader to scan all of it to find one item and scatters new items into random positions, which makes diffs noisier. One predictable order lets any item be found at a glance and gives every addition an obvious place. Sorting yields to a real constraint: when one item depends on, or must run before, another, that order wins over alphabetical order.

A violation is a sequence of sibling, order independent items left in an order that is not alphabetical — setter or getter calls on one object, a parameter list, sibling declarations, or the entries of a literal list.

**Example:** commit `a72be4fa` sorted a block of `themeDisplay.set*` calls and `172687f` ordered `setPortalDomain` before `setPortalURL`; `fb69012` reordered parameters to `(Account account, Long accountId)`; `19d4dbf` alphabetized shell functions; `d461c0b` fixed a string list to the case sensitive order `" 1 "`, `"0"`, `"0, 1"`, `"true"`, `null`; `f264e2c` sorted a test method into its alphabetical position among its siblings. The diffs below sort a builder chain, a parameter list, and a block of setter calls.

```diff
 Foo foo = builder.start(
 	arg
-).gamma(
-	gammaArg
 ).alpha(
 	alphaArg
 ).beta(
 	betaArg
+).gamma(
+	gammaArg
 ).build();
```

```diff
 private void process(
-	String charlie, long alpha, String beta,
+	long alpha, String beta, String charlie,
 	Object delta) {
```

```diff
 Foo foo = new Foo();

-foo.setGamma(gamma);
 foo.setAlpha(alpha);
 foo.setBeta(beta);
+foo.setGamma(gamma);
```