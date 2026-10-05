# 608: Avoid Unboxing in Assertion Comparisons

When the actual value in an assertion is a boxed type such as `Long` or `Integer`, compare it against a boxed expected value, `Long.valueOf(0)` or `Integer.valueOf(1)`, rather than unboxing the actual value with `.longValue()` or `.intValue()` to compare it against a primitive literal.

**Rationale:** Unboxing the actual value chains a second call onto the line being asserted, which rule 402 forbids and which the formatter then splits across several lines. Matching the boxed type on the expected side keeps the comparison to one readable call. A boxed comparison also fails as an assertion when the actual value is `null`, where `.longValue()` would throw a `NullPointerException` and hide the real failure.

A violation is an `Assert.assertEquals` or similar assertion whose actual argument ends in `.longValue()`, `.intValue()`, `.booleanValue()`, or another unboxing call made only to match a primitive expected value. An assertion whose actual value is already a primitive is not a violation.

**Example:** an assertion that unboxes the value held by a `ThreadLocal<Long>` to compare it with `0L` should instead compare the boxed value with `Long.valueOf(0)`.

```diff
-Assert.assertEquals(
-	0L,
-	threadLocal.getValue(
-	).longValue());
+Assert.assertEquals(Long.valueOf(0), threadLocal.getValue());
```