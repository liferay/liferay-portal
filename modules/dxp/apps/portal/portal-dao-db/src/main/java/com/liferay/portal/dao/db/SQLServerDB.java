/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.db;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.db.Index;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alexander Chow
 * @author Sandeep Soni
 * @author Ganesh Ram
 */
public class SQLServerDB extends BaseDB {

	public SQLServerDB(int majorVersion, int minorVersion) {
		super(DBType.SQLSERVER, majorVersion, minorVersion);
	}

	@Override
	public void alterColumnType(
			Connection connection, String tableName, String columnName,
			String newColumnType)
		throws Exception {

		List<IndexMetadata> indexMetadatas = dropIndexes(
			connection, tableName, columnName);

		String[] primaryKeyColumnNames = getPrimaryKeyColumnNames(
			connection, tableName);

		DBInspector dbInspector = new DBInspector(connection);

		boolean primaryKey = ArrayUtil.contains(
			primaryKeyColumnNames, dbInspector.normalizeName(columnName));

		if (primaryKey) {
			removePrimaryKey(connection, tableName);
		}

		_dropDefaultConstraint(connection, tableName, columnName);

		super.alterColumnType(connection, tableName, columnName, newColumnType);

		if (primaryKey) {
			addPrimaryKey(connection, tableName, primaryKeyColumnNames);
		}

		if (!indexMetadatas.isEmpty()) {
			addIndexes(connection, indexMetadatas);
		}
	}

	@Override
	public void alterTableDropColumn(
			Connection connection, String tableName, String columnName)
		throws Exception {

		dropIndexes(connection, tableName, columnName);

		_dropDefaultConstraint(connection, tableName, columnName);

		super.alterTableDropColumn(connection, tableName, columnName);
	}

	@Override
	public String buildSQL(String template) throws IOException {
		template = replaceTemplate(template);

		template = reword(template);
		template = StringUtil.replace(template, "\ngo;\n", "\ngo\n");
		template = StringUtil.replace(
			template, new String[] {"\\\\", "\\'", "\\\"", "\\n", "\\r"},
			new String[] {"\\", "''", "\"", "\n", "\r"});

		return template;
	}

	@Override
	public String getCharacterSet(Connection connection) throws SQLException {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select serverproperty('collation')")) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getString(1);
				}
			}
		}

		return StringPool.BLANK;
	}

	@Override
	public String getDefaultValue(String columnDef) {
		Matcher matcher = _defaultValuePattern.matcher(columnDef);

		if (matcher.find()) {
			if (matcher.group(1) == null) {
				return matcher.group(2);
			}

			return matcher.group(1);
		}

		return columnDef;
	}

	@Override
	public List<Index> getIndexes(Connection connection) throws SQLException {
		List<Index> indexes = new ArrayList<>();

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		if (databaseMetaData.getDatabaseMajorVersion() <= _SQL_SERVER_2000) {
			return indexes;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select sys.tables.name as table_name, sys.indexes.name ",
					"as index_name, is_unique from sys.indexes inner join ",
					"sys.tables on sys.tables.object_id = ",
					"sys.indexes.object_id where sys.indexes.name like ",
					"'LIFERAY_%' or sys.indexes.name like 'IX_%'"));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String indexName = resultSet.getString("index_name");
				String tableName = resultSet.getString("table_name");
				boolean unique = resultSet.getBoolean("is_unique");

				indexes.add(new Index(indexName, tableName, unique));
			}
		}

		return indexes;
	}

	@Override
	public String getNewUuidFunctionName() {
		return "lower(NEWID())";
	}

	@Override
	public String getPopulateSQL(String databaseName, String sqlContent) {
		return StringBundler.concat("use ", databaseName, ";\n\n", sqlContent);
	}

	@Override
	public String getRecreateSQL(String databaseName) {
		return StringBundler.concat(
			"drop database ", databaseName, ";\ncreate database ", databaseName,
			";\n\ngo\n\n");
	}

	@Override
	public boolean isSupportsCharacterSet(Connection connection) {
		return true;
	}

	@Override
	public boolean isSupportsNewUuidFunction() {
		return true;
	}

	@Override
	public void removePrimaryKey(Connection connection, String tableName)
		throws Exception {

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		DBInspector dbInspector = new DBInspector(connection);

		String normalizedTableName = dbInspector.normalizeName(
			tableName, databaseMetaData);

		if (!dbInspector.hasTable(normalizedTableName)) {
			throw new SQLException(
				StringBundler.concat(
					"Table ", normalizedTableName, " does not exist"));
		}

		String primaryKeyConstraintName = null;

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select name from sys.key_constraints where type = 'PK' and " +
					"OBJECT_NAME(parent_object_id) = ?")) {

			preparedStatement.setString(1, normalizedTableName);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					primaryKeyConstraintName = resultSet.getString("name");
				}
			}
		}

		if (primaryKeyConstraintName == null) {
			throw new SQLException(
				"No primary key constraint found for " + normalizedTableName);
		}

		if (dbInspector.hasIndex(
				normalizedTableName, primaryKeyConstraintName)) {

			runSQL(
				StringBundler.concat(
					"alter table ", normalizedTableName, " drop constraint ",
					primaryKeyConstraintName));
		}
		else {
			throw new SQLException(
				StringBundler.concat(
					"Primary key with name ", primaryKeyConstraintName,
					" does not exist"));
		}
	}

	@Override
	protected void createSyncDeleteTrigger(
			Connection connection, String sourceTableName,
			String targetTableName, String triggerName,
			String[] sourcePrimaryKeyColumnNames,
			String[] targetPrimaryKeyColumnNames)
		throws Exception {

		StringBundler sb = new StringBundler();

		sb.append("create trigger ");
		sb.append(triggerName);
		sb.append(" on ");
		sb.append(sourceTableName);
		sb.append(" after delete as delete ");
		sb.append(targetTableName);
		sb.append(" from ");
		sb.append(targetTableName);
		sb.append(" inner join deleted on ");

		for (int i = 0; i < sourcePrimaryKeyColumnNames.length; i++) {
			if (i > 0) {
				sb.append(" and ");
			}

			sb.append(targetTableName);
			sb.append(StringPool.PERIOD);
			sb.append(targetPrimaryKeyColumnNames[i]);
			sb.append(" = deleted.");
			sb.append(sourcePrimaryKeyColumnNames[i]);
		}

		runSQL(connection, sb.toString());
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

		sb.append("create trigger ");
		sb.append(triggerName);
		sb.append(" on ");
		sb.append(sourceTableName);
		sb.append(" after insert as insert into ");
		sb.append(targetTableName);
		sb.append(" (");
		sb.append(StringUtil.merge(targetColumnNames, ", "));
		sb.append(") select ");

		for (int i = 0; i < sourceColumnNames.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}

			String defaultValue = defaultValuesMap.get(targetColumnNames[i]);

			if (defaultValue != null) {
				sb.append("COALESCE(");
			}

			sb.append(sourceTableName);
			sb.append(StringPool.PERIOD);
			sb.append(sourceColumnNames[i]);

			if (defaultValue != null) {
				sb.append(", ");
				sb.append(defaultValue);
				sb.append(")");
			}
		}

		sb.append(" from ");
		sb.append(sourceTableName);
		sb.append(" inner join inserted on ");

		for (int i = 0; i < sourcePrimaryKeyColumnNames.length; i++) {
			if (i > 0) {
				sb.append(" and ");
			}

			sb.append(sourceTableName);
			sb.append(StringPool.PERIOD);
			sb.append(sourcePrimaryKeyColumnNames[i]);
			sb.append(" = inserted.");
			sb.append(sourcePrimaryKeyColumnNames[i]);
		}

		runSQL(connection, sb.toString());
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

		sb.append("create trigger ");
		sb.append(triggerName);
		sb.append(" on ");
		sb.append(sourceTableName);
		sb.append(" after update as update ");
		sb.append(targetTableName);
		sb.append(" set ");

		for (int i = 0; i < sourceColumnNames.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}

			sb.append(targetTableName);
			sb.append(StringPool.PERIOD);
			sb.append(targetColumnNames[i]);
			sb.append(" = ");

			String defaultValue = defaultValuesMap.get(targetColumnNames[i]);

			if (defaultValue != null) {
				sb.append("COALESCE(");
			}

			sb.append("res.");
			sb.append(sourceColumnNames[i]);

			if (defaultValue != null) {
				sb.append(", ");
				sb.append(defaultValue);
				sb.append(")");
			}
		}

		sb.append(" from (select ");

		for (int i = 0; i < sourceColumnNames.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}

			sb.append(sourceTableName);
			sb.append(StringPool.PERIOD);
			sb.append(sourceColumnNames[i]);
		}

		sb.append(" from ");
		sb.append(sourceTableName);
		sb.append(" inner join inserted on ");

		for (int i = 0; i < sourcePrimaryKeyColumnNames.length; i++) {
			if (i > 0) {
				sb.append(" and ");
			}

			sb.append(sourceTableName);
			sb.append(StringPool.PERIOD);
			sb.append(sourcePrimaryKeyColumnNames[i]);
			sb.append(" = inserted.");
			sb.append(sourcePrimaryKeyColumnNames[i]);
		}

		sb.append(") as res where ");

		for (int i = 0; i < sourcePrimaryKeyColumnNames.length; i++) {
			if (i > 0) {
				sb.append(" and ");
			}

			sb.append("res.");
			sb.append(sourcePrimaryKeyColumnNames[i]);
			sb.append(" = ");
			sb.append(targetTableName);
			sb.append(StringPool.PERIOD);
			sb.append(targetPrimaryKeyColumnNames[i]);
		}

		runSQL(connection, sb.toString());
	}

	@Override
	protected String getCopyTableStructureSQL(
		String tableName, String newTableName) {

		return StringBundler.concat(
			"select * into ", newTableName, " from ", tableName,
			" where 1 = 0");
	}

	@Override
	protected String getRenameTableSQL(
		String oldTableName, String newTableName) {

		return StringBundler.concat(
			"exec sp_rename ", oldTableName, ", ", newTableName);
	}

	@Override
	protected int[] getSQLTypes() {
		return _SQL_TYPES;
	}

	@Override
	protected Map<String, Integer> getSQLVarcharSizes() {
		return HashMapBuilder.put(
			"STRING", _SQL_STRING_SIZE
		).put(
			"TEXT", SQL_VARCHAR_MAX_SIZE
		).build();
	}

	@Override
	protected String[] getTemplate() {
		return _SQL_SERVER;
	}

	@Override
	protected String reword(String data) throws IOException {
		if (Validator.isNull(data)) {
			return null;
		}

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(data))) {

			StringBundler sb = new StringBundler();

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				if (line.startsWith(ALTER_COLUMN_NAME)) {
					String[] template = buildColumnNameTokens(line);

					line = StringUtil.replace(
						"exec sp_rename '@table@.@old-column@', " +
							"'@new-column@', 'column';",
						REWORD_TEMPLATE, template);
				}
				else if (line.startsWith(ALTER_COLUMN_TYPE)) {
					String[] template = buildColumnTypeTokens(line);

					line = StringUtil.replace(
						"alter table @table@ alter column @old-column@ " +
							"@type@ @nullable@;",
						REWORD_TEMPLATE, template);

					line = StringUtil.replace(line, " ;", ";");

					String defaultValue = template[template.length - 2];

					if (!Validator.isBlank(defaultValue)) {
						line = line.concat(
							StringUtil.replace(
								"alter table @table@ add constraint " +
									"@old-column@_default default @default@ " +
										"for @old-column@;",
								REWORD_TEMPLATE, template));
					}
				}
				else if (line.startsWith(ALTER_TABLE_NAME)) {
					String[] template = buildTableNameTokens(line);

					line = StringUtil.replace(
						"exec sp_rename '@old-table@', '@new-table@';",
						RENAME_TABLE_TEMPLATE, template);
				}
				else if (line.contains(DROP_INDEX)) {
					String[] tokens = StringUtil.split(line, ' ');

					String tableName = tokens[4];

					if (tableName.endsWith(StringPool.SEMICOLON)) {
						tableName = tableName.substring(
							0, tableName.length() - 1);
					}

					line = StringUtil.replace(
						"drop index @table@.@index@;", "@table@", tableName);
					line = StringUtil.replace(line, "@index@", tokens[2]);
				}

				sb.append(line);
				sb.append("\n");
			}

			return sb.toString();
		}
	}

	private void _dropDefaultConstraint(
			Connection connection, String tableName, String columnName)
		throws Exception {

		String defaultConstraintName = null;

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select default_constraints.name from sys.",
					"default_constraints inner join sys.tables on ",
					"default_constraints.parent_object_id = tables.object_id ",
					"inner join sys.columns on default_constraints.",
					"parent_object_id = columns.object_id and ",
					"default_constraints.parent_column_id = columns.column_id ",
					"where tables.name = ? and columns.name = ?"))) {

			preparedStatement.setString(1, tableName);
			preparedStatement.setString(2, columnName);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					defaultConstraintName = resultSet.getString("name");
				}
			}
		}

		if (Validator.isNull(defaultConstraintName)) {
			return;
		}

		runSQL(
			connection,
			StringBundler.concat(
				"alter table ", tableName, " drop constraint ",
				defaultConstraintName));
	}

	private static final String[] _SQL_SERVER = {
		"--", "1", "0", "'19700101'", "GetDate()", " image", " image",
		" decimal(30, 16)", " bit", " datetime2(6)", " float", " int",
		" bigint", " nvarchar(4000)", " nvarchar(max)", " nvarchar",
		"  identity(1,1)", "go"
	};

	private static final int _SQL_SERVER_2000 = 8;

	private static final int _SQL_STRING_SIZE = 4000;

	private static final int[] _SQL_TYPES = {
		Types.LONGVARBINARY, Types.LONGVARBINARY, Types.DECIMAL, Types.BIT,
		Types.TIMESTAMP, Types.DOUBLE, Types.INTEGER, Types.BIGINT,
		Types.NVARCHAR, Types.NVARCHAR, Types.NVARCHAR
	};

	private static final Pattern _defaultValuePattern = Pattern.compile(
		"^\\('(.*)'\\)|\\(\\((\\d*)\\)\\)", Pattern.CASE_INSENSITIVE);

}