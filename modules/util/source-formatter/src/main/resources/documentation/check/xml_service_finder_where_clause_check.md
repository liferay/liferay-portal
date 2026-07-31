## XMLServiceFinderWhereClauseCheck

A `finder` `where` clause in `service.xml` must reference the entity property
name instead of the database column name.

Service Builder rewrites only the property names it recognizes in a `where`
clause, keeping the property for the generated HQL and swapping to the column
for the generated native SQL. A database column name matches neither, so it is
passed unchanged into both. Hibernate 5 tolerated the unmapped path, but
Hibernate 7 rejects it with `SemanticException: Could not interpret path
expression`, which fails every finder carrying the clause on every database. See
LPD-98149.

A `finder-column` or an `order-column` that names a database column already
fails the build, because Service Builder resolves those against the entity
columns. A `where` clause is the only place where the mistake is silent.

An entity property name never contains an underscore. Service Builder appends
the underscore to a column name that is a reserved word, so the underscore only
ever belongs to the database side.

### Example

Incorrect:

```
<column name="type" type="int" />

<finder name="CompanyId" return-type="Collection" where="type_ = 1">
	<finder-column name="companyId" />
</finder>
```

Correct:

```
<column name="type" type="int" />

<finder name="CompanyId" return-type="Collection" where="type = 1">
	<finder-column name="companyId" />
</finder>
```

An underscore inside a quoted literal, such as the `LIKE` wildcard in
`where="name LIKE 'a_b'"`, is ignored.