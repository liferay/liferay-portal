# Database Partition Migration Validator Tool

This tool validates the compatibility between the source and target databases
for a partition migration.

## Requirements

- MySQL or PostgreSQL
- Database user with read access to all partitions

## Usage

```
./db_partition_migration_validator.sh <command> <parameters>
```

Commands:

- `export` Export validation file.
- `validate` Validate source and target validation files.

Export parameters:

- `--company-id <arg>` Set the company ID.
- `--jdbc-url <arg>` Set the JDBC URL.
- `--output-dir <arg>` Set the output directory.
- `--password <arg>` Set the database user password.
- `--schema-name <arg>` Set the schema name.
- `--user <arg>` Set the database user name.

Validate parameters:

- `--source-file <arg>` Set the path to the source validation file.
- `--target-file <arg>` Set the path to the target validation file.

## Examples

```
./db_partition_migration_validator.sh export --jdbc-url "jdbc:mysql://localhost:3306/defaultSchema" --company-id 1234 --password xyz123 --user xyz123 --schema-name lpartition_1234
./db_partition_migration_validator.sh export --jdbc-url "jdbc:mysql://localhost:3306/defaultSchema" --company-id 1234 --password xyz123 --user xyz123
./db_partition_migration_validator.sh validate --source-file source.json --target-file target.json
```