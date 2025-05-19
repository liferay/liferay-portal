/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.db;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.db.Index;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alexander Chow
 * @author Sandeep Soni
 * @author Ganesh Ram
 */
public class PostgreSQLDB extends BaseDB {

	public static String getCreateRulesSQL(
		String tableName, String columnName) {

		return StringBundler.concat(
			"create or replace rule delete_",
			StringUtil.replace(tableName, '.', '_'), StringPool.UNDERLINE,
			columnName, " as on delete to ", tableName,
			" do also select case when exists(select 1 from ",
			"pg_catalog.pg_largeobject_metadata where (oid = old.", columnName,
			")) then lo_unlink(old.", columnName, ") end from ", tableName,
			" where ", tableName, StringPool.PERIOD, columnName, " = old.",
			columnName, ";\ncreate or replace rule update_",
			StringUtil.replace(tableName, '.', '_'), StringPool.UNDERLINE,
			columnName, " as on update to ", tableName, " where old.",
			columnName, " is distinct from new.", columnName, " and old.",
			columnName,
			" is not null do also select case when exists(select 1 from ",
			"pg_catalog.pg_largeobject_metadata where (oid = old.", columnName,
			")) then lo_unlink(old.", columnName, ") end from ", tableName,
			" where ", tableName, StringPool.PERIOD, columnName, " = old.",
			columnName, StringPool.SEMICOLON);
	}

	public PostgreSQLDB(int majorVersion, int minorVersion) {
		super(DBType.POSTGRESQL, majorVersion, minorVersion);

		if (majorVersion >= 13) {
			_supportsNewUuidFunction = true;
		}
		else {
			_supportsNewUuidFunction = false;
		}
	}

	@Override
	public String buildSQL(String template) throws IOException {
		template = replaceTemplate(template);

		template = reword(template);

		return template;
	}

	@Override
	public String getCharacterSet(Connection connection) throws SQLException {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"show server_encoding")) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getString(1);
				}
			}
		}

		return StringPool.BLANK;
	}

	@Override
	public List<Index> getIndexes(Connection connection) throws SQLException {
		List<Index> indexes = new ArrayList<>();

		// https://issues.liferay.com/browse/LPS-136307
		// https://www.postgresql.org/docs/13/catalog-pg-index.html
		// https://www.postgresql.org/docs/13/catalog-pg-class.html
		// https://www.postgresql.org/docs/13/view-pg-indexes.html

		String sql = StringBundler.concat(
			"select pg_indexes.indexname, pg_indexes.tablename, ",
			"pg_index.indisunique from pg_indexes, pg_index, pg_class where ",
			"pg_indexes.schemaname = current_schema() and ",
			"(pg_indexes.indexname like 'liferay_%' or pg_indexes.indexname ",
			"like 'ix_%') and pg_class.relname = pg_indexes.indexname and ",
			"pg_index.indexrelid = pg_class.oid");

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sql);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String indexName = resultSet.getString("indexname");
				String tableName = resultSet.getString("tablename");
				boolean unique = resultSet.getBoolean("indisunique");

				indexes.add(new Index(indexName, tableName, unique));
			}
		}

		return indexes;
	}

	@Override
	public String getNewUuidFunctionName() {
		return "gen_random_uuid()";
	}

	@Override
	public String getPopulateSQL(String databaseName, String sqlContent) {
		return StringBundler.concat("\\c ", databaseName, ";\n\n", sqlContent);
	}

	@Override
	public String getRecreateSQL(String databaseName) {
		return StringBundler.concat(
			"drop database ", databaseName, ";\ncreate database ", databaseName,
			" encoding = 'UTF8';\n");
	}

	@Override
	public boolean isSupportsCharacterSet(Connection connection)
		throws SQLException {

		return Objects.equals(getCharacterSet(connection), "UTF8");
	}

	@Override
	public boolean isSupportsDBPartition() {
		return true;
	}

	@Override
	public boolean isSupportsNewUuidFunction() {
		return _supportsNewUuidFunction;
	}

	@Override
	public boolean isSupportsQueryingAfterException() {
		return false;
	}

	@Override
	protected void createSyncDeleteTrigger(
			Connection connection, String sourceTableName,
			String targetTableName, String triggerName,
			String[] sourcePrimaryKeyColumnNames,
			String[] targetPrimaryKeyColumnNames)
		throws Exception {

		StringBundler sb = new StringBundler();

		sb.append("delete from ");
		sb.append(targetTableName);
		sb.append(" where ");

		for (int i = 0; i < sourcePrimaryKeyColumnNames.length; i++) {
			if (i > 0) {
				sb.append(" and ");
			}

			sb.append(targetPrimaryKeyColumnNames[i]);
			sb.append(" = old.");
			sb.append(sourcePrimaryKeyColumnNames[i]);
		}

		_createTriggerFunction(connection, triggerName, sb.toString());

		_createTrigger(
			connection, sourceTableName, "after delete", triggerName);
	}

	@Override
	protected void createSyncInsertTrigger(
			Connection connection, String sourceTableName,
			String targetTableName, String triggerName,
			String[] sourceColumnNames, String[] targetColumnNames,
			String[] sourcePrimaryKeyColumnNames,
			String[] targetPrimaryKeyColumnNames,
			Map<String, String> defaultValuesMap)
		throws Exception {

		StringBundler sb = new StringBundler();

		sb.append("insert into ");
		sb.append(targetTableName);
		sb.append(" (");
		sb.append(StringUtil.merge(targetColumnNames, ", "));
		sb.append(") values (");

		for (int i = 0; i < sourceColumnNames.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}

			String defaultValue = defaultValuesMap.get(targetColumnNames[i]);

			if (defaultValue != null) {
				sb.append("COALESCE(");
			}

			sb.append("new.");
			sb.append(sourceColumnNames[i]);

			if (defaultValue != null) {
				sb.append(", ");
				sb.append(defaultValue);
				sb.append(")");
			}
		}

		sb.append(")");

		_createTriggerFunction(connection, triggerName, sb.toString());

		_createTrigger(
			connection, sourceTableName, "after insert", triggerName);
	}

	@Override
	protected void createSyncUpdateTrigger(
			Connection connection, String sourceTableName,
			String targetTableName, String triggerName,
			String[] sourceColumnNames, String[] targetColumnNames,
			String[] sourcePrimaryKeyColumnNames,
			String[] targetPrimaryKeyColumnNames,
			Map<String, String> defaultValuesMap)
		throws Exception {

		StringBundler sb = new StringBundler();

		sb.append("update ");
		sb.append(targetTableName);
		sb.append(" set ");

		for (int i = 0; i < sourceColumnNames.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}

			sb.append(targetColumnNames[i]);
			sb.append(" = ");

			String defaultValue = defaultValuesMap.get(targetColumnNames[i]);

			if (defaultValue != null) {
				sb.append("COALESCE(");
			}

			sb.append("new.");
			sb.append(sourceColumnNames[i]);

			if (defaultValue != null) {
				sb.append(", ");
				sb.append(defaultValue);
				sb.append(")");
			}
		}

		sb.append(" where ");

		for (int i = 0; i < sourcePrimaryKeyColumnNames.length; i++) {
			if (i > 0) {
				sb.append(" and ");
			}

			sb.append(targetPrimaryKeyColumnNames[i]);
			sb.append(" = old.");
			sb.append(sourcePrimaryKeyColumnNames[i]);
		}

		_createTriggerFunction(connection, triggerName, sb.toString());

		_createTrigger(
			connection, sourceTableName, "after update", triggerName);
	}

	@Override
	protected void dropTrigger(
			Connection connection, String tableName, String triggerName)
		throws Exception {

		runSQL(
			connection,
			StringBundler.concat(
				"drop trigger ", triggerName, " on ", tableName));

		runSQL(connection, "drop function " + triggerName);
	}

	@Override
	protected String getCopyTableStructureSQL(
		String tableName, String newTableName) {

		return StringBundler.concat(
			"create table ", newTableName, " (like ", tableName,
			" including all excluding indexes)");
	}

	@Override
	protected String getIndexColumnName(String indexColumnName) {
		return StringUtil.replaceFirst(indexColumnName, "left\"(", "left(");
	}

	@Override
	protected int[] getSQLTypes() {
		return _SQL_TYPES;
	}

	@Override
	protected Map<String, Integer> getSQLVarcharSizes() {
		return HashMapBuilder.put(
			"STRING", SQL_VARCHAR_MAX_SIZE
		).put(
			"TEXT", SQL_VARCHAR_MAX_SIZE
		).build();
	}

	@Override
	protected String[] getTemplate() {
		return _POSTGRESQL;
	}

	@Override
	protected boolean isSupportsDuplicatedIndexName() {
		return false;
	}

	@Override
	protected String limitColumnLength(String column, int length) {
		return StringBundler.concat("left(", column, ", ", length, ")");
	}

	@Override
	protected String reword(String data) throws IOException {
		if (Validator.isNull(data)) {
			return null;
		}

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(data))) {

			StringBundler sb = new StringBundler();

			StringBundler createRulesSQLSB = new StringBundler();
			String line = null;
			String tableName = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				if (line.startsWith(ALTER_COLUMN_NAME)) {
					String[] template = buildColumnNameTokens(line);

					line = StringUtil.replace(
						"alter table @table@ rename @old-column@ to " +
							"@new-column@;",
						REWORD_TEMPLATE, template);
				}
				else if (line.startsWith(ALTER_COLUMN_TYPE)) {
					String[] template = buildColumnTypeTokens(line);

					line = StringUtil.replace(
						"alter table @table@ alter @old-column@ type @type@ " +
							"using @old-column@::@type@;",
						REWORD_TEMPLATE, template);

					String defaultValue = template[template.length - 2];

					if (!Validator.isBlank(defaultValue)) {
						line = line.concat(
							StringUtil.replace(
								"alter table @table@ alter column " +
									"@old-column@ set default @default@;",
								REWORD_TEMPLATE, template));
					}
					else {
						line = line.concat(
							StringUtil.replace(
								"alter table @table@ alter column " +
									"@old-column@ drop default;",
								REWORD_TEMPLATE, template));
					}

					String nullable = template[template.length - 1];

					if (Objects.equals(nullable, "not null")) {
						line = line.concat(
							StringUtil.replace(
								"alter table @table@ alter column " +
									"@old-column@ set not null;",
								REWORD_TEMPLATE, template));
					}
					else {
						line = line.concat(
							StringUtil.replace(
								"alter table @table@ alter column " +
									"@old-column@ drop not null;",
								REWORD_TEMPLATE, template));
					}
				}
				else if (line.startsWith(ALTER_TABLE_NAME)) {
					String[] template = buildTableNameTokens(line);

					line = StringUtil.replace(
						StringBundler.concat(
							"alter table @old-table@ rename to @new-table@;",
							"alter table @new-table@ rename constraint ",
							"@old-table@_pkey to @new-table@_pkey;"),
						RENAME_TABLE_TEMPLATE, template);
				}
				else if (line.startsWith(CREATE_TABLE)) {
					String[] tokens = StringUtil.split(line, ' ');

					tableName = tokens[2];
				}
				else if (line.contains(DROP_INDEX)) {
					String[] tokens = StringUtil.split(line, ' ');

					line = StringUtil.replace(
						"drop index @index@;", "@index@", tokens[2]);
				}
				else if (line.contains(DROP_PRIMARY_KEY)) {
					String[] tokens = StringUtil.split(line, ' ');

					line = StringUtil.replace(
						"alter table @table@ drop constraint @table@_pkey;",
						"@table@", tokens[2]);
				}
				else if (line.contains(getTemplateBlob())) {
					Matcher matcher = _oidPattern.matcher(line);

					if (matcher.find()) {
						String[] tokens = StringUtil.split(line, ' ');

						createRulesSQLSB.append(StringPool.NEW_LINE);
						createRulesSQLSB.append(
							getCreateRulesSQL(tableName, tokens[0]));
						createRulesSQLSB.append(StringPool.NEW_LINE);
					}
				}
				else if (line.contains("\\\'")) {
					line = StringUtil.replace(line, "\\\'", "\'\'");
				}

				sb.append(line);
				sb.append("\n");
			}

			sb.append(createRulesSQLSB.toString());

			return sb.toString();
		}
	}

	private void _createTrigger(
			Connection connection, String tableName, String triggerEvent,
			String triggerName)
		throws Exception {

		runSQL(
			connection,
			StringBundler.concat(
				"create trigger ", triggerName, " ", triggerEvent, " on ",
				tableName, " for each row execute procedure ", triggerName,
				"()"));
	}

	private void _createTriggerFunction(
			Connection connection, String functionName,
			String functionStatement)
		throws Exception {

		StringBundler sb = new StringBundler(5);

		sb.append("create function ");
		sb.append(functionName);
		sb.append("() returns trigger language plpgsql as $$ begin ");
		sb.append(functionStatement);
		sb.append("; return null; end; $$");

		runSQL(connection, sb.toString());
	}

	private static final String[] _POSTGRESQL = {
		"--", "true", "false", "'01/01/1970'", "current_timestamp", " oid",
		" bytea", " decimal(30, 16)", " bool", " timestamp",
		" double precision", " integer", " bigint", " text", " text",
		" varchar", "", "commit"
	};

	private static final int[] _SQL_TYPES = {
		Types.BIGINT, Types.BINARY, Types.NUMERIC, Types.BIT, Types.TIMESTAMP,
		Types.DOUBLE, Types.INTEGER, Types.BIGINT, Types.VARCHAR, Types.VARCHAR,
		Types.VARCHAR
	};

	private static final Pattern _oidPattern = Pattern.compile(
		" oid(\\W|$)", Pattern.CASE_INSENSITIVE);

	private final boolean _supportsNewUuidFunction;

}