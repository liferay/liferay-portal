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
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alexander Chow
 * @author Sandeep Soni
 * @author Ganesh Ram
 */
public class OracleDB extends BaseDB {

	public OracleDB(int majorVersion, int minorVersion) {
		super(DBType.ORACLE, majorVersion, minorVersion);
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

			alterTableAddColumn(
				connection, tableName, tempColumnName, newColumnType);

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
		template = StringUtil.replace(
			template, new String[] {"\\\\", "\\'", "\\\""},
			new String[] {"\\", "''", "\""});

		return StringUtil.replace(template, "\\n", "'||CHR(10)||'");
	}

	@Override
	public String getCharacterSet(Connection connection) throws SQLException {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select value from nls_database_parameters where parameter " +
					"in ('NLS_CHARACTERSET')")) {

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

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select index_name, table_name, uniqueness from ",
					"user_indexes where index_name like 'LIFERAY_%' or ",
					"index_name like 'IX_%'"));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String indexName = resultSet.getString("index_name");
				String tableName = resultSet.getString("table_name");
				String uniqueness = resultSet.getString("uniqueness");

				boolean unique = true;

				if (StringUtil.equalsIgnoreCase(uniqueness, "NONUNIQUE")) {
					unique = false;
				}

				indexes.add(new Index(indexName, tableName, unique));
			}
		}

		return indexes;
	}

	@Override
	public ResultSet getIndexResultSet(
			Connection connection, String tableName, boolean onlyUnique)
		throws SQLException {

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		DBInspector dbInspector = new DBInspector(connection);

		return databaseMetaData.getIndexInfo(
			dbInspector.getCatalog(), dbInspector.getSchema(), tableName,
			onlyUnique, true);
	}

	@Override
	public String getPopulateSQL(String databaseName, String sqlContent) {
		return "connect &1/&2;\nset define off;\n\n" + sqlContent + "quit";
	}

	@Override
	public String getRecreateSQL(String databaseName) {
		return "drop user &1 cascade;\ncreate user &1 identified by &2;\n" +
			"grant connect,resource to &1;\nquit";
	}

	@Override
	public boolean isSupportsCharacterSet(Connection connection)
		throws SQLException {

		return Objects.equals(getCharacterSet(connection), "AL32UTF8");
	}

	@Override
	public boolean isSupportsInlineDistinct() {
		return false;
	}

	@Override
	protected String[] buildColumnTypeTokens(String line) {
		Matcher matcher = _varchar2CharPattern.matcher(line);

		StringBuffer sb = new StringBuffer();

		while (matcher.find()) {
			matcher.appendReplacement(
				sb, "VARCHAR2(" + matcher.group(1) + "%20CHAR)");
		}

		matcher.appendTail(sb);

		String[] template = super.buildColumnTypeTokens(sb.toString());

		template[3] = StringUtil.replace(template[3], "%20", StringPool.SPACE);

		return template;
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
		sb.append(" for each row delete from ");
		sb.append(targetTableName);
		sb.append(" where ");

		for (int i = 0; i < sourcePrimaryKeyColumnNames.length; i++) {
			if (i > 0) {
				sb.append(" and ");
			}

			sb.append(targetPrimaryKeyColumnNames[i]);
			sb.append(" = :old.");
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
		sb.append(" for each row insert into ");
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

			sb.append(":new.");
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
		sb.append(" for each row update ");
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

			sb.append(":new.");
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
			sb.append(" = :old.");
			sb.append(sourcePrimaryKeyColumnNames[i]);
		}

		runSQL(connection, sb.toString());
	}

	@Override
	protected String getRenameTableSQL(
		String oldTableName, String newTableName) {

		return StringBundler.concat(
			"rename ", oldTableName, " to ", newTableName);
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
		return _ORACLE;
	}

	protected boolean isNullable(String tableName, String columnName)
		throws SQLException {

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			return dbInspector.isNullable(tableName, columnName);
		}
	}

	@Override
	protected boolean isSupportsDDLRollback() {
		return false;
	}

	@Override
	protected boolean isSupportsDuplicatedIndexName() {
		return false;
	}

	@Override
	protected String limitColumnLength(String column, int length) {
		return StringBundler.concat("substr(", column, ", 1, ", length, ")");
	}

	@Override
	protected String replaceTemplate(String template) {

		// LPS-12048

		Matcher matcher = _varcharPattern.matcher(template);

		StringBuffer sb = new StringBuffer();

		while (matcher.find()) {
			int size = GetterUtil.getInteger(matcher.group(1));

			if (size > 4000) {
				size = 4000;
			}

			matcher.appendReplacement(sb, "VARCHAR2(" + size + " CHAR)");
		}

		matcher.appendTail(sb);

		template = sb.toString();

		return super.replaceTemplate(template);
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

					String nullable = template[template.length - 1];

					boolean currentNullable = isNullable(
						template[0], template[1]);

					if (!Validator.isBlank(nullable)) {
						if ((nullable.equals("null") && currentNullable) ||
							(nullable.equals("not null") && !currentNullable)) {

							nullable = StringPool.BLANK;
						}
					}
					else if (!currentNullable) {
						nullable = "null";
					}

					String defaultValue = template[template.length - 2];

					if (!Validator.isBlank(defaultValue)) {
						line = StringUtil.replace(
							StringBundler.concat(
								"alter table @table@ modify @old-column@ ",
								"@type@ default @default@ ", nullable, ";"),
							REWORD_TEMPLATE, template);
					}
					else {
						line = StringUtil.replace(
							StringBundler.concat(
								"alter table @table@ modify @old-column@ ",
								"@type@ default null ", nullable, ";"),
							REWORD_TEMPLATE, template);
					}

					line = StringUtil.replace(line, " ;", ";");
				}
				else if (line.startsWith(ALTER_TABLE_NAME)) {
					String[] template = buildTableNameTokens(line);

					line = StringUtil.replace(
						"alter table @old-table@ rename to @new-table@;",
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

	private static final String[] _ORACLE = {
		"--", "1", "0",
		"to_date('1970-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS')", "sysdate",
		" blob", " blob", " decimal(30, 16)", " number(1, 0)", " timestamp",
		" binary_double", " number(30,0)", " number(30,0)",
		" varchar2(4000 char)", " clob", " varchar2", "", "commit"
	};

	private static final int _SQL_STRING_SIZE = 4000;

	private static final int _SQL_TYPE_BINARY_DOUBLE = 101;

	private static final int[] _SQL_TYPES = {
		Types.BLOB, Types.BLOB, Types.NUMERIC, Types.NUMERIC, Types.TIMESTAMP,
		_SQL_TYPE_BINARY_DOUBLE, Types.NUMERIC, Types.NUMERIC, Types.VARCHAR,
		Types.CLOB, Types.VARCHAR
	};

	private static final Log _log = LogFactoryUtil.getLog(OracleDB.class);

	private static final Pattern _varchar2CharPattern = Pattern.compile(
		"VARCHAR2\\((\\d+) CHAR\\)", Pattern.CASE_INSENSITIVE);
	private static final Pattern _varcharPattern = Pattern.compile(
		"VARCHAR\\((\\d+)\\)", Pattern.CASE_INSENSITIVE);

}