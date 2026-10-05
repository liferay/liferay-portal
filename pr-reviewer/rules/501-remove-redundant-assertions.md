# 501: Remove Redundant Assertions

Do not assert a precondition that a later line already guarantees or asserts more strongly. The common case is `assertNotNull(x)` immediately before code that dereferences `x`, which would throw and fail the test on its own, or before a stronger assertion about `x` that could not pass if `x` were null.

**Rationale:** An assertion that cannot change the outcome of the test is noise. If the value were null, the dereference or the stronger assertion on the next line already fails the test, so the `assertNotNull` only adds a line to read and maintain. Removing it leaves the single assertion that states the real expectation.

A violation is an `assertNotNull`, or a similar guard, immediately followed by code that dereferences the same value or asserts a stronger property of it.

**Example:** commits `fa6b60e`, `30028a0`, and `6213899` removed `assertNotNull` calls whose next line already dereferenced the value or made a stronger assertion (for example reading `configurations.length`). The shape is always the same: a lookup, an `assertNotNull` on its result, and a specific assertion that dereferences the same result on the next line.

```diff
 Foo foo = service.findFoo();

-Assert.assertNotNull(foo);
 Assert.assertEquals("expected", foo.getName());
 Assert.assertEquals("description", foo.getDescription());
```