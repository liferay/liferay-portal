# Database Schema Importer Tool

This tool imports database schemas between different databases in the same
network. This tool is a beta feature. It is experimental and not supported.

## Requirements

- Source database must be DB2, MariaDB, MySQL, Oracle, or SQL Server
- Target database must be PostgreSQL
- Database user with read access to all partitions
- Previous run of DBSchemaDefinitionExporter to generate SQL schema files

## Usage

```
./db_schema_importer.sh <parameters>
```

Import parameters:

- `--jdbc-batch-size <arg>` Set the JDBC batch size. The default value is 2500.
- `--jdbc-fetch-size <arg>` Set the JDBC result set fetch size. The default value is 2500.
- `--path <arg>` Set the path of the source SQL files.
- `--source-jdbc-url <arg>` Set the source JDBC URL.
- `--source-password <arg>` Set the source database user password.
- `--source-user <arg>` Set the source database user.
- `--target-jdbc-url <arg>` Set the target JDBC URL.
- `--target-password <arg>` Set the target database user password.
- `--target-user <arg>` Set the target database user.

## Examples

```
./db_schema_importer.sh --path "/directory/" --source-jdbc-url "jdbc:mysql://localhost:3306/schema" --source-password "xyz123" --source-user "xyz123" --target-jdbc-url "jdbc:postgresql://localhost:5432/schema" --target-password "xyz321" --target-user "xyz321"
./db_schema_importer.sh --jdbc-batch-size 600 --jdbc-fetch-size 1600 --path "/directory/" --source-jdbc-url "jdbc:mysql://localhost:3306/schema" --source-password "xyz123" --source-user "xyz123" --target-jdbc-url "jdbc:postgresql://localhost:5432/schema" --target-password "xyz321" --target-user "xyz321"
```