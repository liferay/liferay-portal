## AutoBatchPreparedStatementUtilCheck

Use `AutoBatchPreparedStatementUtil.autoBatch` or
`AutoBatchPreparedStatementUtil.concurrentAutoBatch` to create a prepared
statement when using `preparedStatement.executeBatch()`.

### Example

Incorrect:

```java
try (PreparedStatement preparedStatement1 = connection.prepareStatement(
        "select configurationId, dictionary from Configuration_");
    PreparedStatement preparedStatement2 = connection.prepareStatement(
        "delete from Configuration_ where configurationId = ?");
    ResultSet resultSet = preparedStatement1.executeQuery()) {

    while (resultSet.next()) {
        preparedStatement2.setString(1, resultSet.getString("configurationId"));

        preparedStatement2.addBatch();
    }

    preparedStatement2.executeBatch();
}
```

Correct:

```java
try (PreparedStatement preparedStatement1 = connection.prepareStatement(
        "select configurationId, dictionary from Configuration_");
    PreparedStatement preparedStatement2 =
        AutoBatchPreparedStatementUtil.autoBatch(
            connection, "delete from Configuration_ where configurationId = ?");
    ResultSet resultSet = preparedStatement1.executeQuery()) {

    while (resultSet.next()) {
        preparedStatement2.setString(1, resultSet.getString("configurationId"));

        preparedStatement2.addBatch();
    }

    preparedStatement2.executeBatch();
}
```