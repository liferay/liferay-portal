# 506: Drop a Redundant Long Literal Suffix

Write an integer literal that lands in a slot typed `long` (a `long` field or local initializer, a `long` parameter, a `long` return) without the `L` suffix when the value fits in an `int`. Java widens the `int` literal to `long` on assignment, so `300000` and `300000L` produce the same value in a `long` slot, and the shorter form is the house form.

**Rationale:** The suffix restates a type the declaration already shows, so it is visual noise at every use, and a mixed file where some `long` literals carry `L` and others do not leaves the reader wondering whether the difference means something. Dropping it where the slot is already `long` keeps one form.

A violation is an `L` (or `l`) suffix on an integer literal assigned to, returned from, or passed into a slot whose static type is the primitive `long`, where the literal is within the `int` range. Do not flag a literal larger than `Integer.MAX_VALUE`, which needs the suffix to compile; a literal in an arithmetic expression such as `1000L * 60 * 60 * 24 * 365`, where the `int` multiplication would overflow before widening; or a literal whose slot is `Object`, `Long`, or a generic type parameter, since `Assert.assertEquals(1L, map.get("apple"))` boxes to `Long` and dropping the suffix boxes to `Integer` and fails the assertion.

**Example:** a `public static final long TIMEOUT = 300000L;` became `public static final long TIMEOUT = 300000;`.

```diff
-public static final long TIMEOUT = 300000L;
+public static final long TIMEOUT = 300000;
```