## ToSQLCallCheck

Do not discard the result of `toSQL(ASTNodeListener)`. That method builds the whole SQL text of the node in a `StringBundler` and returns it, so a statement that calls it only to walk the tree for its `ASTNodeListener` builds a string that nobody reads.

Call `toSQL(Consumer<String>, ASTNodeListener)` with a consumer that ignores the text instead. It walks the same nodes and notifies the same listener without building the string.

### Example

Incorrect:

```java
dslQuery.toSQL(astNodeListener);
```

Correct:

```java
dslQuery.toSQL(
    string -> {
    },
    astNodeListener);
```

The check only flags a statement that consists of a `toSQL` call with one argument. A call whose result is assigned, returned, passed as an argument, or used in an expression is not flagged.