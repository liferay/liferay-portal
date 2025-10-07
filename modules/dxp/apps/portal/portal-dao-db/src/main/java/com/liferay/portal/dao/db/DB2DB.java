/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.db;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Alexander Chow
 * @author Bruno Farache
 * @author Sandeep Soni
 * @author Ganesh Ram
 */
public class DB2DB extends BaseDB {

	public DB2DB(int majorVersion, int minorVersion) {
		super(DBType.DB2, majorVersion, minorVersion);
	}

	@Override
	public void alterColumnName(
			Connection connection, String tableName, String oldColumnName,
			String newColumnDefinition)
		throws Exception {

		List<IndexMetadata> indexMetadatas = dropIndexes(
			connection, tableName, oldColumnName);

		String[] primaryKeyColumnNames = getPrimaryKeyColumnNames(
			connection, tableName);

		DBInspector dbInspector = new DBInspector(connection);

		String normalizedOldColumnName = dbInspector.normalizeName(
			oldColumnName);

		boolean primaryKey = ArrayUtil.contains(
			primaryKeyColumnNames, normalizedOldColumnName);

		if (primaryKey) {
			removePrimaryKey(connection, tableName);
		}

		super.alterColumnName(
			connection, tableName, oldColumnName, newColumnDefinition);

		String normalizedNewColumnName = dbInspector.normalizeName(
			StringUtil.extractFirst(newColumnDefinition, StringPool.SPACE));

		if (primaryKey) {
			ArrayUtil.replace(
				primaryKeyColumnNames, normalizedOldColumnName,
				normalizedNewColumnName);

			addPrimaryKey(connection, tableName, primaryKeyColumnNames);
		}

		List<IndexMetadata> newIndexMetadatas = new ArrayList<>();

		for (IndexMetadata indexMetadata : indexMetadatas) {
			String[] columnNames = indexMetadata.getColumnNames();

			ArrayUtil.replace(
				columnNames, normalizedOldColumnName, normalizedNewColumnName);

			newIndexMetadatas.add(
				new IndexMetadata(
					indexMetadata.getIndexName(), indexMetadata.getTableName(),
					indexMetadata.isUnique(), columnNames));
		}

		if (!newIndexMetadatas.isEmpty()) {
			addIndexes(connection, newIndexMetadatas);
		}
	}

	@Override
	public void alterColumnType(
			Connection connection, String tableName, String columnName,
			String newColumnType)
		throws Exception {

		DBInspector dbInspector = new DBInspector(connection);

		if (!dbInspector.hasColumn(tableName, columnName)) {
			throw new SQLException(
				StringBundler.concat(
					"Unknown column ", columnName, " in table ", tableName));
		}

		try {
			super.alterColumnType(
				connection, tableName, columnName, newColumnType);
		}
		catch (SQLException sqlException) {
			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Attempting to upgrade table ", tableName,
						" by adding a temporary column due to: ",
						sqlException.getMessage()));
			}

			String tempColumnName = "temp" + columnName;

			if (StringUtil.endsWith(newColumnType, "not null") &&
				!StringUtil.containsIgnoreCase(
					newColumnType, "default", StringPool.SPACE)) {

				runSQL(
					StringBundler.concat(
						"alter table ", tableName, " add ", tempColumnName,
						StringPool.SPACE, newColumnType, " default 0"));

				runSQL(
					StringBundler.concat(
						"alter table ", tableName, " alter column ",
						tempColumnName, " drop default"));
			}
			else {
				alterTableAddColumn(
					connection, tableName, tempColumnName, newColumnType);
			}

			runSQL(
				StringBundler.concat(
					"update ", tableName, " set ", tempColumnName, " = ",
					columnName));

			List<IndexMetadata> indexMetadatas = dropIndexes(
				connection, tableName, columnName);

			String[] primaryKeyColumnNames = getPrimaryKeyColumnNames(
				connection, tableName);

			boolean primaryKey = ArrayUtil.contains(
				primaryKeyColumnNames, columnName);

			if (primaryKey) {
				removePrimaryKey(connection, tableName);
			}

			alterColumnName(
				connection, tableName, columnName,
				tempColumnName + "2 " + newColumnType);

			alterColumnName(
				connection, tableName, tempColumnName,
				columnName + StringPool.SPACE + newColumnType);

			if (!indexMetadatas.isEmpty()) {
				addIndexes(connection, indexMetadatas);
			}

			if (primaryKey) {
				addPrimaryKey(connection, tableName, primaryKeyColumnNames);
			}

			alterTableDropColumn(connection, tableName, tempColumnName + "2");

			if (_log.isInfoEnabled()) {
				_log.info("Successfully upgraded table " + tableName);
			}
		}
	}

	@Override
	public String buildSQL(String template) throws IOException, SQLException {
		template = replaceTemplate(template);

		template = reword(template);
		template = _removeNull(template);
		template = StringUtil.replace(template, "\\'", "''");
		template = StringUtil.replace(template, "\\n", "'||CHR(10)||'");

		return template;
	}

	@Override
	public String getCharacterSet(Connection connection) throws SQLException {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select value from sysibmadm.dbcfg where name = 'codeset'")) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getString(1);
				}
			}
		}

		return StringPool.BLANK;
	}

	@Override
	public String getPopulateSQL(String databaseName, String sqlContent) {
		return StringBundler.concat(
			"connect to ", databaseName, ";\n", sqlContent);
	}

	@Override
	public String getRecreateSQL(String databaseName) {
		return StringBundler.concat(
			"drop database ", databaseName, ";\ncreate database ", databaseName,
			" pagesize 32768 temporary tablespace managed by automatic ",
			"storage;\n");
	}

	@Override
	public boolean isSupportsCharacterSet(Connection connection)
		throws SQLException {

		return Objects.equals(getCharacterSet(connection), "UTF-8");
	}

	@Override
	public boolean isSupportsInlineDistinct() {
		return false;
	}

	@Override
	public boolean isSupportsScrollableResults() {
		return false;
	}

	@Override
	public void runSQL(Connection connection, String[] templates)
		throws IOException, SQLException {

		reorgTables(connection, templates);

		super.runSQL(connection, templates);

		reorgTables(connection, templates);
	}

	@Override
	public void runSQL(String template) throws IOException, SQLException {
		template = StringUtil.trim(template);

		if (template.startsWith(ALTER_COLUMN_NAME)) {
			String sql = buildSQL(template);

			String[] alterSqls = StringUtil.split(sql, CharPool.SEMICOLON);

			for (String alterSql : alterSqls) {
				alterSql = StringUtil.trim(alterSql);

				runSQL(alterSql);
			}
		}
		else {
			super.runSQL(template);
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
		sb.append(" after delete on ");
		sb.append(sourceTableName);
		sb.append(" referencing old as old for each row delete from ");
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
		sb.append(" after insert on ");
		sb.append(sourceTableName);
		sb.append(" referencing new as new for each row insert into ");
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
		sb.append(" after update on ");
		sb.append(sourceTableName);
		sb.append(" referencing old as old new as new for each row update ");
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

		runSQL(connection, sb.toString());
	}

	@Override
	protected String getCopyTableStructureSQL(
		String tableName, String newTableName) {

		return StringBundler.concat(
			"create table ", newTableName, " as (select * from ", tableName,
			") with no data");
	}

	@Override
	protected String getRenameTableSQL(
		String oldTableName, String newTableName) {

		return StringBundler.concat(
			"rename table ", oldTableName, " to ", newTableName);
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
			"TEXT", SQL_SIZE_NONE
		).build();
	}

	@Override
	protected String[] getTemplate() {
		return _DB2;
	}

	protected boolean isNullable(String tableName, String columnName)
		throws SQLException {

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			return dbInspector.isNullable(tableName, columnName);
		}
	}

	protected boolean isRequiresReorgTable(
			Connection connection, String tableName)
		throws SQLException {

		boolean reorgTableRequired = false;

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select num_reorg_rec_alters from table(",
					"sysproc.admin_get_tab_info(current_schema, '",
					StringUtil.toUpperCase(tableName),
					"')) where reorg_pending = 'Y'"));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			if (resultSet.next()) {
				int numReorgRecAlters = resultSet.getInt(1);

				if (numReorgRecAlters >= 1) {
					reorgTableRequired = true;
				}
			}
		}

		return reorgTableRequired;
	}

	@Override
	protected boolean isSupportsDuplicatedIndexName() {
		return false;
	}

	protected void reorgTable(Connection connection, String tableName)
		throws SQLException {

		if (!isRequiresReorgTable(connection, tableName)) {
			return;
		}

		try (CallableStatement callableStatement = connection.prepareCall(
				"call sysproc.admin_cmd(?)")) {

			callableStatement.setString(1, "reorg table " + tableName);

			callableStatement.execute();
		}
	}

	protected void reorgTables(Connection connection, String[] templates)
		throws SQLException {

		Set<String> tableNames = new HashSet<>();

		for (String template : templates) {
			template = StringUtil.trim(template);

			String lowerCaseTemplate = StringUtil.toLowerCase(template);

			if (lowerCaseTemplate.startsWith("alter table") ||
				lowerCaseTemplate.startsWith("delete from")) {

				tableNames.add(template.split(" ")[2]);
			}
			else if (lowerCaseTemplate.startsWith(ALTER_COLUMN_TYPE)) {
				tableNames.add(template.split(" ")[1]);
			}
		}

		if (tableNames.isEmpty()) {
			return;
		}

		for (String tableName : tableNames) {
			reorgTable(connection, tableName);
		}
	}

	@Override
	protected String reword(String data) throws IOException, SQLException {
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
						"alter table @table@ rename column @old-column@ to " +
							"@new-column@;",
						REWORD_TEMPLATE, template);
				}
				else if (line.startsWith(ALTER_COLUMN_TYPE)) {
					String[] template = buildColumnTypeTokens(line);

					line = StringUtil.replace(
						"alter table @table@ alter column @old-column@ set " +
							"data type @type@;",
						REWORD_TEMPLATE, template);

					String defaultValue = template[template.length - 2];

					if (Validator.isBlank(defaultValue)) {
						runSQL(
							StringUtil.replace(
								"alter table @table@ alter column " +
									"@old-column@ set default 0;",
								REWORD_TEMPLATE, template));

						runSQL(
							StringUtil.replace(
								"alter table @table@ alter column " +
									"@old-column@ drop default;",
								REWORD_TEMPLATE, template));
					}
					else {
						runSQL(
							StringUtil.replace(
								"alter table @table@ alter column " +
									"@old-column@ set default @default@;",
								REWORD_TEMPLATE, template));
					}

					String nullable = template[template.length - 1];

					if (Objects.equals(nullable, "not null") &&
						isNullable(template[0], template[1])) {

						runSQL(
							StringUtil.replace(
								"alter table @table@ alter column " +
									"@old-column@ set not null;",
								REWORD_TEMPLATE, template));
					}
					else if (!Objects.equals(nullable, "not null") &&
							 !isNullable(template[0], template[1])) {

						runSQL(
							StringUtil.replace(
								"alter table @table@ alter column " +
									"@old-column@ drop not null;",
								REWORD_TEMPLATE, template));
					}
				}
				else if (line.startsWith(ALTER_TABLE_NAME)) {
					String[] template = buildTableNameTokens(line);

					line = StringUtil.replace(
						"rename table @old-table@ to @new-table@;",
						RENAME_TABLE_TEMPLATE, template);
				}
				else if (line.contains(DROP_INDEX)) {
					String[] tokens = StringUtil.split(line, ' ');

					line = StringUtil.replace(
						"drop index @index@;", "@index@", tokens[2]);
				}

				sb.append(line);
				sb.append("\n");
			}

			return sb.toString();
		}
	}

	private String _removeNull(String content) {
		content = StringUtil.replace(content, " = null", " = NULL");
		content = StringUtil.replace(content, " is null", " IS NULL");
		content = StringUtil.replace(content, " not null", " not_null");
		content = StringUtil.removeSubstring(content, " null");
		content = StringUtil.replace(content, " not_null", " not null");

		return content;
	}

	private static final String[] _DB2 = {
		"--", "1", "0", "'1970-01-01-00.00.00.000000'", "current timestamp",
		" blob", " blob", " decimal(30, 16)", " smallint", " timestamp",
		" double", " integer", " bigint", " varchar(4000)", " clob(2G)",
		" varchar", " generated always as identity", "commit"
	};

	private static final int _SQL_STRING_SIZE = 4000;

	private static final int[] _SQL_TYPES = {
		Types.BLOB, Types.BLOB, Types.DECIMAL, Types.SMALLINT, Types.TIMESTAMP,
		Types.DOUBLE, Types.INTEGER, Types.BIGINT, Types.VARCHAR, Types.CLOB,
		Types.VARCHAR
	};

	private static final Log _log = LogFactoryUtil.getLog(DB2DB.class);

}