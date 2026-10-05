# 910: Separate Dependent Try Resources With a Blank Line

In a `try` with resources that declares several resources, put a blank line between the resources that are independent of one another and a resource that is built from them. The blank line splits the resource list into the same paragraphs the body would have: first the resources opened on their own, then the one that consumes them.

**Rationale:** A multiresource `try` reads as a flat list, so a dependency between two entries (a `ResultSet` produced by executing one of the `PreparedStatement`s above it) is invisible unless the reader traces the identifiers. The blank line surfaces the dependency at the declaration level, where the close order that matters is decided, since the dependent resource closes first, instead of forcing the reader to reconstruct it from the body.

A violation is a `try` with resources in which a resource whose initializer references an earlier resource in the same list follows the independent resources with no blank line before it. Do not flag a `try` with a single resource, a list whose resources are all independent of one another, or a list in which every resource depends on the previous one, since there is no independent group to set off.

**Example:** a `try` that opened two `PreparedStatement`s from one connection and then a `ResultSet` from `preparedStatement1.executeQuery()` gained a blank line between the second statement and the result set.

```diff
 try (
 	PreparedStatement preparedStatement1 = connection.prepareStatement(selectSQL);
 	PreparedStatement preparedStatement2 = connection.prepareStatement(updateSQL);
+
 	ResultSet resultSet = preparedStatement1.executeQuery()) {
```