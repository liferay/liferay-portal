/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.db;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.db.Index;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PropsValues;
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
import java.util.regex.Matcher;

/**
 * @author Alexander Chow
 * @author Sandeep Soni
 * @author Ganesh Ram
 */
public class MySQLDB extends BaseDB {

	public MySQLDB(int majorVersion, int minorVersion) {
		super(DBType.MYSQL, majorVersion, minorVersion);
	}

	@Override
	public void alterColumnType(
			Connection connection, String tableName, String columnName,
			String newColumnType)
		throws Exception {

		List<IndexMetadata> indexMetadatas = new ArrayList<>();

		Matcher matcher = columnTypePattern.matcher(newColumnType);

		if (matcher.lookingAt() &&
			ArrayUtil.contains(
				SQL_VARCHAR_TYPES, getSQLType(matcher.group(1)))) {

			indexMetadatas = dropIndexes(connection, tableName, columnName);
		}

		super.alterColumnType(connection, tableName, columnName, newColumnType);

		if (!indexMetadatas.isEmpty()) {
			addIndexes(connection, indexMetadatas);
		}
	}

	@Override
	public void alterTableDropColumn(
			Connection connection, String tableName, String columnName)
		throws Exception {

		String[] primaryKeyColumnNames = getPrimaryKeyColumnNames(
			connection, tableName);

		boolean primaryKey = ArrayUtil.contains(
			primaryKeyColumnNames, columnName);

		if (primaryKey && (primaryKeyColumnNames.length > 1)) {
			removePrimaryKey(connection, tableName);

			addPrimaryKey(
				connection, tableName,
				ArrayUtil.remove(primaryKeyColumnNames, columnName));
		}

		List<IndexMetadata> indexMetadatas = getIndexMetadatas(
			connection, tableName, columnName, false);

		for (IndexMetadata indexMetadata : indexMetadatas) {
			String[] columnNames = indexMetadata.getColumnNames();

			if (columnNames.length > 1) {
				runSQL(connection, indexMetadata.getDropSQL());
			}
		}

		super.alterTableDropColumn(connection, tableName, columnName);
	}

	@Override
	public String buildSQL(String template) throws IOException {
		template = replaceTemplate(template);

		template = reword(template);
		template = StringUtil.replace(template, "\\'", "''");

		return template;
	}

	@Override
	public String getCharacterSet(Connection connection) throws SQLException {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select @@character_set_database")) {

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

		String sql = StringBundler.concat(
			"select distinct(index_name), table_name, non_unique from ",
			"information_schema.statistics where index_schema = database() ",
			"and (index_name like 'LIFERAY_%' or index_name like 'IX_%')");

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sql);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String indexName = resultSet.getString("index_name");
				String tableName = resultSet.getString("table_name");
				boolean unique = !resultSet.getBoolean("non_unique");

				indexes.add(new Index(indexName, tableName, unique));
			}
		}

		return indexes;
	}

	@Override
	public String getNewUuidFunctionName() {
		return "UUID()";
	}

	@Override
	public String getPopulateSQL(String databaseName, String sqlContent) {
		return StringBundler.concat("use ", databaseName, ";\n\n", sqlContent);
	}

	@Override
	public String getRecreateSQL(String databaseName) {
		return StringBundler.concat(
			"drop database if exists ", databaseName, ";\ncreate database ",
			databaseName, " character set utf8mb4;\n");
	}

	@Override
	public boolean isSupportsCharacterSet(Connection connection)
		throws SQLException {

		String characterSet = getCharacterSet(connection);

		return characterSet.startsWith("utf8");
	}

	@Override
	public boolean isSupportsDBPartition() {
		return true;
	}

	@Override
	public boolean isSupportsNewUuidFunction() {
		return true;
	}

	@Override
	public boolean isSupportsUpdateWithInnerJoin() {
		return true;
	}

	protected MySQLDB(DBType dbType, int majorVersion, int minorVersion) {
		super(dbType, majorVersion, minorVersion);
	}

	@Override
	protected final void doRenameTables(
			Connection connection,
			ObjectValuePair<String, String>... tableNameObjectValuePairs)
		throws Exception {

		StringBundler sb = new StringBundler(
			(tableNameObjectValuePairs.length * 4) + 1);

		sb.append("rename table ");

		for (int i = 0; i < tableNameObjectValuePairs.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}

			sb.append(tableNameObjectValuePairs[i].getKey());
			sb.append(" to ");
			sb.append(tableNameObjectValuePairs[i].getValue());
		}

		runSQL(connection, sb.toString());
	}

	@Override
	protected int[] getSQLTypes() {
		return _SQL_TYPES;
	}

	@Override
	protected String[] getTemplate() {
		return _MYSQL;
	}

	@Override
	protected String reword(String data) throws IOException {
		if (Validator.isNull(data)) {
			return null;
		}

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(data))) {

			StringBundler sb = new StringBundler();

			boolean createTable = false;

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				if (StringUtil.startsWith(line, "create table")) {
					createTable = true;
				}
				else if (line.startsWith(ALTER_COLUMN_NAME)) {
					String[] template = buildColumnNameTokens(line);

					String defaultValue = template[template.length - 2];

					if (!Validator.isBlank(defaultValue)) {
						line = StringUtil.replace(
							"alter table @table@ change column @old-column@ " +
								"@new-column@ @type@ default @default@ " +
									"@nullable@;",
							REWORD_TEMPLATE, template);
					}
					else {
						line = StringUtil.replace(
							"alter table @table@ change column @old-column@ " +
								"@new-column@ @type@ @nullable@;",
							REWORD_TEMPLATE, template);

						line = StringUtil.replace(line, " ;", ";");
					}
				}
				else if (line.startsWith(ALTER_COLUMN_TYPE)) {
					String[] template = buildColumnTypeTokens(line);

					String defaultValue = template[template.length - 2];

					if (!Validator.isBlank(defaultValue)) {
						line = StringUtil.replace(
							"alter table @table@ modify @old-column@ @type@ " +
								"default @default@ @nullable@;",
							REWORD_TEMPLATE, template);
					}
					else {
						line = StringUtil.replace(
							"alter table @table@ modify @old-column@ @type@ " +
								"@nullable@;",
							REWORD_TEMPLATE, template);

						line = StringUtil.replace(line, " ;", ";");
					}
				}
				else if (line.startsWith(ALTER_TABLE_NAME)) {
					String[] template = buildTableNameTokens(line);

					line = StringUtil.replace(
						"rename table @old-table@ to @new-table@;",
						RENAME_TABLE_TEMPLATE, template);
				}

				int pos = line.indexOf(CharPool.SEMICOLON);

				if (createTable && (pos != -1)) {
					createTable = false;

					line = StringBundler.concat(
						line.substring(0, pos), " engine ",
						PropsValues.DATABASE_MYSQL_ENGINE, line.substring(pos));
				}

				sb.append(line);
				sb.append("\n");
			}

			return sb.toString();
		}
	}

	private static final String[] _MYSQL = {
		"##", "1", "0", "'1970-01-01'", "now()", " longblob", " longblob",
		" decimal(30, 16)", " tinyint", " datetime(6)", " double", " integer",
		" bigint", " longtext", " longtext", " varchar", "  auto_increment",
		"commit"
	};

	private static final int[] _SQL_TYPES = {
		Types.LONGVARBINARY, Types.LONGVARBINARY, Types.DECIMAL, Types.TINYINT,
		Types.TIMESTAMP, Types.DOUBLE, Types.INTEGER, Types.BIGINT,
		Types.LONGVARCHAR, Types.LONGVARCHAR, Types.VARCHAR
	};

}