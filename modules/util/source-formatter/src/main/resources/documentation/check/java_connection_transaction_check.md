## JavaConnectionTransactionCheck

Do not call `commit`, `rollback`, or `setAutoCommit` on a `java.sql.Connection`.
Transaction boundaries are managed by the container's transaction manager. When
the connection is borrowed from the current transaction, for example through
`CurrentConnection`, an explicit `commit` flushes the caller's uncommitted work,
and restoring `setAutoCommit(true)` implicitly commits it. See LPD-98668.

Code that legitimately owns its connection, such as a counter that must persist
its sequence independently, is added to the
`source.check.JavaConnectionTransactionCheck.allowedFileNames` allowlist after
review.

### Example

Incorrect:

```java
Connection connection = _currentConnection.getConnection(
    InfrastructureUtil.getDataSource());

_executeUpdates(connection);

connection.commit();
```

Correct:

```java
Connection connection = _currentConnection.getConnection(
    InfrastructureUtil.getDataSource());

_executeUpdates(connection);
```