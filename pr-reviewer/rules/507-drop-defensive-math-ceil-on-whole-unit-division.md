# 507: Drop Defensive Math Ceil on Whole Unit Division

When a whole unit value is converted to a coarser unit (milliseconds to seconds, bytes to kilobytes) and the realistic inputs are already multiples of the divisor, divide with integer arithmetic: `milliseconds / 1000`, not `(long)Math.ceil(milliseconds / 1000.0)`. The `Math.ceil` form promotes to `double`, rounds, and casts back, and for any input the code actually receives the three steps collapse to the plain division.

**Rationale:** Rounding up guards against a remainder that never occurs, so the promotion and the cast are dead work the reader still has to parse and reason about, including the precision question a `double` conversion raises for a large `long`. Integer division states the conversion in one term and cannot lose precision.

A violation is `(long)Math.ceil(x / 1000.0)`, or the same shape with another divisor, where `x` is a whole unit value (a timeout, an interval, a size) that the surrounding code produces as a multiple of the divisor. Do not flag a `Math.ceil` where the remainder is real and rounding up is the point, such as a page count computed from an item count and a page size, or a conversion of an arbitrary elapsed time that must not report zero for a partial unit.

**Example:** `long seconds = (long)Math.ceil(milliseconds / 1000.0);` became `long seconds = milliseconds / 1000;`.

```diff
-long seconds = (long)Math.ceil(milliseconds / 1000.0);
+long seconds = milliseconds / 1000;
```