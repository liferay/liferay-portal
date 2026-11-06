## EmptySafeCloseableCheck

Do not write an empty lambda where a `SafeCloseable` is expected. Use the shared constant `SafeCloseable.EMPTY`, so every closeable that does nothing reads the same way.

### Example

Incorrect:

```java
public static SafeCloseable lock(long companyId) {
    if (isLocked()) {
        return () -> {
        };
    }

    ...
}
```

Correct:

```java
public static SafeCloseable lock(long companyId) {
    if (isLocked()) {
        return SafeCloseable.EMPTY;
    }

    ...
}
```

The check flags an empty `() -> {}` lambda that is returned from a method declared to return `SafeCloseable`, that initializes a field or local variable declared as `SafeCloseable`, or that is assigned to a variable declared as `SafeCloseable`, either directly or as a branch of a conditional expression.

An empty lambda passed as a method argument, returned from a lambda body, or used where the target type is another type, such as `Runnable`, `UnsafeRunnable`, `AutoCloseable` or a subinterface of `SafeCloseable`, is not flagged.