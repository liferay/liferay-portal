/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.migration.importer;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.jdbc.postgresql.PostgreSQLJDBCUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.dao.db.IndexMetadataFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.framework.ThrowableCollector;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.db.migration.importer.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.tools.db.migration.importer.jdbc.ConnectionConfigUtil;

import com.mysql.cj.jdbc.JdbcStatement;

import java.math.BigDecimal;

import java.nio.charset.StandardCharsets;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;

import java.text.DateFormat;

import java.time.LocalTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DBCopyTablesProcess {

	public DBCopyTablesProcess(
		DataSource sourceDataSource, DataSource targetDataSource) {

		this(null, sourceDataSource, targetDataSource);
	}

	public DBCopyTablesProcess(
		String partitionName, DataSource sourceDataSource,
		DataSource targetDataSource) {

		_sourceDataSource = sourceDataSource;
		_targetDataSource = targetDataSource;

		if (partitionName == null) {
			_partitionMessage = " in the default schema";
		}
		else {
			_partitionMessage = " in partition " + partitionName;
		}
	}

	public void addCustomPrimaryKeysAndIndexes() throws Exception {
		_addCustomPrimaryKeys();

		_addCustomIndexes();
	}

	public List<String> getCustomTableAndColumnInfos() {
		List<String> customTableAndColumnInfos = new ArrayList<>();

		String customTableNamesString = "none";

		if (!_customTableNames.isEmpty()) {
			customTableNamesString = StringUtil.merge(
				_customTableNames, StringPool.COMMA_AND_SPACE);
		}

		customTableAndColumnInfos.add(
			"custom tables: " + customTableNamesString);

		customTableAndColumnInfos.addAll(_customColumnInfos);

		return customTableAndColumnInfos;
	}

	public void run() throws Exception {
		_loadColumnsMetadata();

		_addCustomColumnsAndTables();

		_copyTables();
	}

	private void _addCustomColumnsAndTables() throws Exception {
		try (Connection sourceConnection = _sourceDataSource.getConnection();

			Connection targetConnection = _targetDataSource.getConnection();

			Statement statement = targetConnection.createStatement()) {

			DatabaseMetaData databaseMetaData = sourceConnection.getMetaData();

			Map<String, List<String>> columnDefinitionsMap = new TreeMap<>(
				String.CASE_INSENSITIVE_ORDER);
			Map<String, List<String>> customColumnInfosMap = new TreeMap<>(
				String.CASE_INSENSITIVE_ORDER);
			Set<String> longNameTableNames = new TreeSet<>(
				String.CASE_INSENSITIVE_ORDER);

			try (ResultSet resultSet = databaseMetaData.getColumns(
					sourceConnection.getCatalog(), sourceConnection.getSchema(),
					null, null)) {

				while (resultSet.next()) {
					String tableName = resultSet.getString("TABLE_NAME");

					if (!_sourceColumnNamesMap.containsKey(tableName)) {
						continue;
					}

					String columnName = resultSet.getString("COLUMN_NAME");

					List<String> targetColumnNames = _targetColumnNamesMap.get(
						tableName);

					if (targetColumnNames == null) {
						if (_isLongName(columnName) || _isLongName(tableName)) {
							longNameTableNames.add(tableName);

							continue;
						}

						String columnType = _getPostgreSQLColumnType(resultSet);

						String columnDefinition = _getColumnDefinition(
							columnName, columnType);

						if (resultSet.getInt("NULLABLE") ==
								DatabaseMetaData.columnNoNulls) {

							columnDefinition += " not null";
						}

						List<String> columnDefinitions =
							columnDefinitionsMap.computeIfAbsent(
								tableName, newTableName -> new ArrayList<>());

						columnDefinitions.add(columnDefinition);

						List<String> customColumnInfos =
							customColumnInfosMap.computeIfAbsent(
								tableName, newTableName -> new ArrayList<>());

						customColumnInfos.add(
							_getCustomColumnInfo(
								columnName, columnType, resultSet));

						continue;
					}

					if (ListUtil.exists(
							targetColumnNames,
							targetColumnName -> StringUtil.equalsIgnoreCase(
								targetColumnName, columnName))) {

						continue;
					}

					if (_isLongName(columnName)) {
						System.out.println(
							StringBundler.concat(
								"Unable to add column ", columnName,
								" to table ", tableName, _partitionMessage,
								" because its name is longer than ",
								_POSTGRESQL_NAME_MAX_LENGTH, " bytes"));

						continue;
					}

					String columnType = _getPostgreSQLColumnType(resultSet);

					statement.executeUpdate(
						StringBundler.concat(
							"alter table ",
							_getQuotedName(StringUtil.toLowerCase(tableName)),
							" add ",
							_getColumnDefinition(columnName, columnType)));

					_customColumnInfos.add(
						_getCustomColumnInfo(
							columnName, columnType, resultSet));
				}
			}

			for (String longNameTableName : longNameTableNames) {
				columnDefinitionsMap.remove(longNameTableName);

				_customColumnInfos.add(
					StringBundler.concat(
						"custom table ", longNameTableName,
						": not migrated because its name or the name of one ",
						"of its columns is longer than ",
						_POSTGRESQL_NAME_MAX_LENGTH, " bytes"));

				System.out.println(
					StringBundler.concat(
						"Unable to create table ", longNameTableName,
						_partitionMessage, " because its name or the name of ",
						"one of its columns is longer than ",
						_POSTGRESQL_NAME_MAX_LENGTH, " bytes"));
			}

			for (Map.Entry<String, List<String>> entry :
					columnDefinitionsMap.entrySet()) {

				statement.executeUpdate(
					StringBundler.concat(
						"create table ",
						_getQuotedName(StringUtil.toLowerCase(entry.getKey())),
						" (", StringUtil.merge(entry.getValue()), ")"));

				_customColumnInfos.addAll(
					customColumnInfosMap.get(entry.getKey()));
				_customTableNames.add(entry.getKey());
			}
		}

		_targetColumnNamesMap.clear();
		_targetColumnsType.clear();

		_loadColumnNamesMap(
			_targetColumnNamesMap, _targetColumnsType, _targetDataSource);
	}

	private void _addCustomIndexes() throws Exception {
		try (Connection sourceConnection = _sourceDataSource.getConnection();

			Connection targetConnection = _targetDataSource.getConnection();

			Statement statement = targetConnection.createStatement()) {

			DatabaseMetaData databaseMetaData = targetConnection.getMetaData();

			Set<String> longColumnNames = new HashSet<>();

			try (ResultSet resultSet = databaseMetaData.getColumns(
					targetConnection.getCatalog(), targetConnection.getSchema(),
					null, null)) {

				while (resultSet.next()) {
					if ((resultSet.getInt("DATA_TYPE") != Types.VARCHAR) ||
						(resultSet.getInt("COLUMN_SIZE") <=
							_POSTGRESQL_STRING_INDEX_MAX_LENGTH)) {

						continue;
					}

					longColumnNames.add(
						resultSet.getString("TABLE_NAME") + "." +
							resultSet.getString("COLUMN_NAME"));
				}
			}

			Set<String> targetNames = new HashSet<>();

			try (ResultSet resultSet = databaseMetaData.getTables(
					targetConnection.getCatalog(), targetConnection.getSchema(),
					null, null)) {

				while (resultSet.next()) {
					targetNames.add(resultSet.getString("TABLE_NAME"));
				}
			}

			Map<String, String> indexTypes = _getIndexTypes(sourceConnection);
			List<IndexMetadata> targetIndexMetadatas = _getIndexMetadatas(
				targetConnection);

			for (IndexMetadata indexMetadata :
					_getIndexMetadatas(sourceConnection)) {

				String tableName = indexMetadata.getTableName();

				List<String> sourceColumnNames = _sourceColumnNamesMap.get(
					tableName);

				if (sourceColumnNames == null) {
					continue;
				}

				String indexName = indexMetadata.getIndexName();

				IndexMetadata matchingIndexMetadata = null;

				for (IndexMetadata targetIndexMetadata : targetIndexMetadatas) {
					if (targetIndexMetadata.equals(indexMetadata) ||
						(Objects.equals(
							targetIndexMetadata.getTableName(), tableName) &&
						 StringUtil.equalsIgnoreCase(
							 targetIndexMetadata.getIndexName(), indexName) &&
						 ArrayUtil.contains(
							 targetIndexMetadata.getColumnNames(),
							 StringPool.BLANK))) {

						matchingIndexMetadata = targetIndexMetadata;

						break;
					}
				}

				if (matchingIndexMetadata != null) {
					if (indexMetadata.isUnique() &&
						!matchingIndexMetadata.isUnique()) {

						System.out.println(
							StringBundler.concat(
								"Unable to create unique index ", indexName,
								" on table ", tableName, _partitionMessage,
								" because index ",
								matchingIndexMetadata.getIndexName(),
								" already exists on the same columns"));
					}

					continue;
				}

				List<String> indexColumnNames = ListUtil.fromArray(
					indexMetadata.getColumnNames());

				List<String> columnNames = ListUtil.toList(
					sourceColumnNames, StringUtil::toLowerCase);

				if (!columnNames.containsAll(indexColumnNames)) {
					System.out.println(
						StringBundler.concat(
							"Unable to create index ", indexName, " on table ",
							tableName, _partitionMessage,
							" because one of its columns is an expression"));

					continue;
				}

				String targetIndexName = StringUtil.toLowerCase(indexName);

				if (_isLongName(targetIndexName) ||
					targetNames.contains(targetIndexName)) {

					targetIndexName = StringUtil.toLowerCase(
						IndexMetadataFactoryUtil.createIndexName(
							tableName, indexMetadata.getColumnNames()));

					System.out.println(
						StringBundler.concat(
							"Renaming index ", indexName, " on table ",
							tableName, _partitionMessage, " to ",
							targetIndexName, " because its name is longer ",
							"than ", _POSTGRESQL_NAME_MAX_LENGTH,
							" bytes or a relation with the same name already ",
							"exists"));
				}

				List<String> indexColumnSQLs = new ArrayList<>();

				boolean wrapped = false;

				for (String indexColumnName : indexColumnNames) {
					String indexColumnSQL = _getQuotedName(indexColumnName);

					if (longColumnNames.contains(
							tableName + "." + indexColumnName)) {

						indexColumnSQL = StringBundler.concat(
							"left(", indexColumnSQL, ", ",
							_POSTGRESQL_STRING_INDEX_MAX_LENGTH, ")");

						wrapped = true;
					}

					indexColumnSQLs.add(indexColumnSQL);
				}

				String indexColumnsSQL = StringUtil.merge(
					indexColumnSQLs, ", ");

				String indexType = indexTypes.get(tableName + "." + indexName);

				String uniqueSQL = StringPool.BLANK;

				if (indexMetadata.isUnique() &&
					!Objects.equals(indexType, "filtered")) {

					uniqueSQL = "unique ";
				}

				try {
					statement.executeUpdate(
						StringBundler.concat(
							"create ", uniqueSQL, "index ",
							_getQuotedName(targetIndexName), " on ",
							_getQuotedName(tableName), " (", indexColumnsSQL,
							")"));
				}
				catch (SQLException sqlException) {
					System.out.println(
						StringBundler.concat(
							"Unable to create index ", indexName, " on table ",
							tableName, _partitionMessage, ": ",
							sqlException.getMessage()));

					continue;
				}

				if ((indexType != null) || wrapped) {
					System.out.println(
						StringBundler.concat(
							"Created ",
							(indexType == null) ? "" : indexType + " ",
							"index ", indexName, " on table ", tableName,
							_partitionMessage, " as a ", uniqueSQL,
							"btree index on (", indexColumnsSQL, ")"));
				}

				targetIndexMetadatas.add(indexMetadata);
				targetNames.add(targetIndexName);
			}
		}
	}

	private void _addCustomPrimaryKeys() throws Exception {
		try (Connection sourceConnection = _sourceDataSource.getConnection();

			Connection targetConnection = _targetDataSource.getConnection();

			Statement statement = targetConnection.createStatement()) {

			DatabaseMetaData databaseMetaData = sourceConnection.getMetaData();

			for (String customTableName : _customTableNames) {
				Map<Integer, String> primaryKeyColumnNamesMap = new TreeMap<>();

				try (ResultSet resultSet = databaseMetaData.getPrimaryKeys(
						sourceConnection.getCatalog(),
						sourceConnection.getSchema(), customTableName)) {

					while (resultSet.next()) {
						primaryKeyColumnNamesMap.put(
							resultSet.getInt("KEY_SEQ"),
							_getQuotedName(
								StringUtil.toLowerCase(
									resultSet.getString("COLUMN_NAME"))));
					}
				}

				if (primaryKeyColumnNamesMap.isEmpty()) {
					continue;
				}

				try {
					statement.executeUpdate(
						StringBundler.concat(
							"alter table ",
							_getQuotedName(
								StringUtil.toLowerCase(customTableName)),
							" add primary key (",
							StringUtil.merge(
								primaryKeyColumnNamesMap.values(), ", "),
							")"));
				}
				catch (SQLException sqlException) {
					System.out.println(
						StringBundler.concat(
							"Unable to add primary key to table ",
							customTableName, _partitionMessage, ": ",
							sqlException.getMessage()));
				}
			}
		}
	}

	private void _copyTable(String sourceTableName, String targetTableName)
		throws Exception {

		List<String> sourceColumnNames = _sourceColumnNamesMap.get(
			sourceTableName);
		List<String> targetColumnNames = _targetColumnNamesMap.get(
			targetTableName);

		Set<String> sourceColumnNamesSet = new TreeSet<String>(
			String.CASE_INSENSITIVE_ORDER) {

			{
				addAll(sourceColumnNames);
			}
		};

		targetColumnNames.removeIf(
			columnName -> !sourceColumnNamesSet.contains(columnName));

		String selectSQL = StringBundler.concat(
			"select ", StringUtil.merge(sourceColumnNames), " from ",
			sourceTableName);
		String insertSQL = StringBundler.concat(
			"insert into ", _getQuotedName(targetTableName), " (",
			StringUtil.merge(
				ListUtil.toList(targetColumnNames, this::_getQuotedName), ", "),
			") values (",
			StringUtil.merge(
				Collections.nCopies(targetColumnNames.size(), "?")),
			")");

		try (Connection sourceConnection = _sourceDataSource.getConnection();
			Connection targetConnection = _targetDataSource.getConnection();
			PreparedStatement preparedStatement1 =
				sourceConnection.prepareStatement(selectSQL);
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					targetConnection, insertSQL)) {

			preparedStatement1.setFetchSize(
				ConnectionConfigUtil.getFetchSize());

			if (preparedStatement1.isWrapperFor(JdbcStatement.class)) {
				JdbcStatement jdbcStatement = preparedStatement1.unwrap(
					JdbcStatement.class);

				jdbcStatement.enableStreamingResults();
			}

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					for (int i = 0; i < sourceColumnNames.size(); i++) {
						String columnName = sourceColumnNames.get(i);

						_getAndSetColumn(
							columnName, i + 1, preparedStatement2, resultSet,
							_sourceColumnsType.get(
								sourceTableName + "." + columnName),
							_targetColumnsType.get(
								targetTableName + "." +
									targetColumnNames.get(i)));
					}

					preparedStatement2.addBatch();
				}
			}

			preparedStatement2.executeBatch();
		}
	}

	private void _copyTables() throws Exception {
		List<Future<?>> futures = new ArrayList<>();

		ExecutorService executorService = Executors.newFixedThreadPool(5);

		Set<String> sourceTableNames = _sourceColumnNamesMap.keySet();
		Set<String> targetTableNames = _targetColumnNamesMap.keySet();

		sourceTableNames.retainAll(targetTableNames);

		targetTableNames.retainAll(sourceTableNames);

		_validateTables(sourceTableNames, targetTableNames);

		AtomicInteger count = new AtomicInteger();
		Iterator<String> sourceIterator = sourceTableNames.iterator();
		Iterator<String> targetIterator = targetTableNames.iterator();

		ThrowableCollector throwableCollector = new ThrowableCollector();

		while (sourceIterator.hasNext()) {
			String sourceTableName = sourceIterator.next();
			String targetTableName = targetIterator.next();

			futures.add(
				executorService.submit(
					() -> {
						try {
							_copyTable(sourceTableName, targetTableName);

							if ((count.incrementAndGet() % 10) == 0) {
								System.out.println(
									StringBundler.concat(
										"Copied ", count, " tables out of ",
										sourceTableNames.size(),
										_partitionMessage));
							}
						}
						catch (Exception exception) {
							throwableCollector.collect(exception);
						}
					}));
		}

		for (Future<?> future : futures) {
			future.get();
		}

		System.out.println("All tables copied" + _partitionMessage);

		Throwable throwable = throwableCollector.getThrowable();

		if (throwable != null) {
			ReflectionUtil.throwException(throwable);
		}
	}

	private void _getAndSetColumn(
			String columnName, int index, PreparedStatement preparedStatement,
			ResultSet resultSet, int sourceType, int targetType)
		throws Exception {

		String valueString = null;

		if (sourceType == Types.BIGINT) {
			long value = resultSet.getLong(columnName);

			if ((value == 0L) && resultSet.wasNull()) {
				preparedStatement.setNull(index, targetType);

				return;
			}

			if ((targetType == Types.BIGINT) || (targetType == Types.NUMERIC)) {
				preparedStatement.setLong(index, value);

				return;
			}

			valueString = String.valueOf(value);
		}
		else if ((sourceType == Types.BINARY) ||
				 (sourceType == Types.LONGVARBINARY) ||
				 (sourceType == Types.VARBINARY)) {

			byte[] value = resultSet.getBytes(columnName);

			if (value == null) {
				preparedStatement.setNull(index, targetType);

				return;
			}

			if ((targetType == Types.BINARY) ||
				(targetType == Types.LONGVARBINARY)) {

				preparedStatement.setBytes(index, value);

				return;
			}
			else if (targetType == Types.BIGINT) {
				PostgreSQLJDBCUtil.setLargeObject(
					preparedStatement, index, value);

				return;
			}

			valueString = new String(value);
		}
		else if (sourceType == Types.BLOB) {
			Blob value = resultSet.getBlob(columnName);

			if (value == null) {
				preparedStatement.setNull(index, targetType);

				return;
			}

			if (targetType == Types.BIGINT) {
				PostgreSQLJDBCUtil.setLargeObject(
					preparedStatement, index,
					value.getBytes(1, (int)value.length()));

				return;
			}
			else if (targetType == Types.BINARY) {
				preparedStatement.setBinaryStream(
					index, value.getBinaryStream());

				return;
			}

			valueString = new String(value.getBytes(1, (int)value.length()));
		}
		else if ((sourceType == Types.BOOLEAN) || (sourceType == Types.BIT)) {
			boolean value = resultSet.getBoolean(columnName);

			if (!value && resultSet.wasNull()) {
				preparedStatement.setNull(index, targetType);

				return;
			}

			if ((targetType == Types.BOOLEAN) || (targetType == Types.BIT)) {
				preparedStatement.setBoolean(index, value);

				return;
			}

			valueString = value ? "1" : "0";
		}
		else if ((sourceType == Types.CHAR) ||
				 (sourceType == Types.LONGNVARCHAR) ||
				 (sourceType == Types.LONGVARCHAR) ||
				 (sourceType == Types.NCHAR) || (sourceType == Types.NCLOB) ||
				 (sourceType == Types.NVARCHAR) || (sourceType == Types.TIME) ||
				 (sourceType == Types.VARCHAR)) {

			String value = resultSet.getString(columnName);

			if (value == null) {
				preparedStatement.setNull(index, targetType);

				return;
			}

			if ((targetType == Types.LONGNVARCHAR) ||
				(targetType == Types.VARCHAR)) {

				preparedStatement.setString(index, _getSanitizedString(value));

				return;
			}

			valueString = value;
		}
		else if (sourceType == Types.CLOB) {
			String value = resultSet.getString(columnName);

			if (value == null) {
				preparedStatement.setNull(index, targetType);

				return;
			}

			valueString = value;
		}
		else if ((sourceType == Types.DATE) ||
				 (sourceType == Types.TIMESTAMP) ||
				 (sourceType == Types.TIMESTAMP_WITH_TIMEZONE) ||
				 (sourceType ==
					 _SQL_TYPE_ORACLE_TIMESTAMP_WITH_LOCAL_TIME_ZONE) ||
				 (sourceType == _SQL_TYPE_ORACLE_TIMESTAMP_WITH_TIME_ZONE) ||
				 (sourceType == _SQL_TYPE_SQL_SERVER_DATETIMEOFFSET)) {

			Timestamp value = resultSet.getTimestamp(columnName);

			if (value == null) {
				preparedStatement.setNull(index, targetType);

				return;
			}

			if (targetType == Types.TIMESTAMP) {
				preparedStatement.setTimestamp(index, value);

				return;
			}

			valueString = value.toString();
		}
		else if ((sourceType == Types.DECIMAL) ||
				 (sourceType == Types.NUMERIC)) {

			BigDecimal value = resultSet.getBigDecimal(columnName);

			if (value == null) {
				preparedStatement.setNull(index, targetType);

				return;
			}

			if (targetType == Types.BIGINT) {
				preparedStatement.setLong(index, value.longValue());

				return;
			}

			if (targetType == Types.BIT) {
				preparedStatement.setBoolean(index, value.signum() != 0);

				return;
			}

			if (targetType == Types.INTEGER) {
				preparedStatement.setInt(index, value.intValue());

				return;
			}

			if (targetType == Types.NUMERIC) {
				preparedStatement.setBigDecimal(index, value);

				return;
			}

			valueString = value.toString();
		}
		else if ((sourceType == Types.DOUBLE) || (sourceType == Types.FLOAT) ||
				 (sourceType == _SQL_TYPE_ORACLE_BINARY_DOUBLE)) {

			double value = resultSet.getDouble(columnName);

			if ((value == 0.0) && resultSet.wasNull()) {
				preparedStatement.setNull(index, targetType);

				return;
			}

			if (targetType == Types.DOUBLE) {
				preparedStatement.setDouble(index, value);

				return;
			}

			valueString = String.valueOf(value);
		}
		else if (sourceType == Types.INTEGER) {
			int value = resultSet.getInt(columnName);

			if ((value == 0) && resultSet.wasNull()) {
				preparedStatement.setNull(index, targetType);

				return;
			}

			if (targetType == Types.INTEGER) {
				preparedStatement.setInt(index, value);

				return;
			}

			valueString = String.valueOf(value);
		}
		else if ((sourceType == Types.REAL) ||
				 (sourceType == _SQL_TYPE_ORACLE_BINARY_FLOAT)) {

			float value = resultSet.getFloat(columnName);

			if ((value == 0.0F) && resultSet.wasNull()) {
				preparedStatement.setNull(index, targetType);

				return;
			}

			valueString = String.valueOf(value);
		}
		else if ((sourceType == Types.TINYINT) ||
				 (sourceType == Types.SMALLINT)) {

			short value = resultSet.getShort(columnName);

			if ((value == 0) && resultSet.wasNull()) {
				preparedStatement.setNull(index, targetType);

				return;
			}

			valueString = String.valueOf(value);
		}
		else if ((targetType == Types.LONGVARCHAR) ||
				 (targetType == Types.VARCHAR)) {

			String value = resultSet.getString(columnName);

			if (value == null) {
				preparedStatement.setNull(index, targetType);

				return;
			}

			valueString = value;
		}
		else {
			throw new PortalException("Invalid type " + sourceType);
		}

		_setColumn(index, preparedStatement, targetType, valueString);
	}

	private String _getColumnDefinition(String columnName, String columnType) {
		return StringBundler.concat(
			_getQuotedName(StringUtil.toLowerCase(columnName)), " ",
			GetterUtil.getString(columnType, "text"));
	}

	private String _getCustomColumnInfo(
			String columnName, String columnType, ResultSet resultSet)
		throws SQLException {

		if (columnType == null) {
			columnType = "text (no equivalent PostgreSQL type)";
		}

		return StringBundler.concat(
			"custom column ", resultSet.getString("TABLE_NAME"), ".",
			columnName, ": source ", resultSet.getString("TYPE_NAME"),
			", created as ", columnType);
	}

	private List<IndexMetadata> _getIndexMetadatas(Connection connection)
		throws SQLException {

		List<IndexMetadata> indexMetadatas = new ArrayList<>();

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		String databaseProductName = databaseMetaData.getDatabaseProductName();

		String schemaName = connection.getSchema();
		String sql = null;

		if (databaseProductName.startsWith("DB2")) {
			sql = StringBundler.concat(
				"select syscat.indexes.tabname as TABLE_NAME, ",
				"syscat.indexes.indname as INDEX_NAME, case when ",
				"syscat.indexes.uniquerule = 'D' then 1 else 0 end as ",
				"NON_UNIQUE, syscat.indexcoluse.colname as COLUMN_NAME, ",
				"syscat.indexcoluse.colseq as ORDINAL_POSITION from ",
				"syscat.indexes inner join syscat.indexcoluse on ",
				"syscat.indexcoluse.indschema = syscat.indexes.indschema and ",
				"syscat.indexcoluse.indname = syscat.indexes.indname where ",
				"syscat.indexes.tabschema = ? and syscat.indexcoluse.colorder ",
				"<> 'I'");
		}
		else if (databaseProductName.equals("MariaDB") ||
				 databaseProductName.equals("MySQL")) {

			schemaName = connection.getCatalog();
			sql = StringBundler.concat(
				"select table_name as TABLE_NAME, index_name as INDEX_NAME, ",
				"non_unique as NON_UNIQUE, column_name as COLUMN_NAME, ",
				"seq_in_index as ORDINAL_POSITION from ",
				"information_schema.statistics where table_schema = ?");
		}
		else if (databaseProductName.equals("Microsoft SQL Server")) {
			sql = StringBundler.concat(
				"select object_name(sys.indexes.object_id) as TABLE_NAME, ",
				"sys.indexes.name as INDEX_NAME, case when ",
				"sys.indexes.is_unique = 1 then 0 else 1 end as NON_UNIQUE, ",
				"sys.columns.name as COLUMN_NAME, ",
				"sys.index_columns.key_ordinal as ORDINAL_POSITION from ",
				"sys.indexes inner join sys.index_columns on ",
				"sys.index_columns.object_id = sys.indexes.object_id and ",
				"sys.index_columns.index_id = sys.indexes.index_id inner join ",
				"sys.columns on sys.columns.object_id = ",
				"sys.index_columns.object_id and sys.columns.column_id = ",
				"sys.index_columns.column_id where ",
				"object_schema_name(sys.indexes.object_id) = ? and ",
				"sys.index_columns.key_ordinal > 0");
		}
		else if (databaseProductName.equals("Oracle")) {
			sql = StringBundler.concat(
				"select all_indexes.table_name as TABLE_NAME, ",
				"all_indexes.index_name as INDEX_NAME, ",
				"decode(all_indexes.uniqueness, 'UNIQUE', 0, 1) as ",
				"NON_UNIQUE, all_ind_columns.column_name as COLUMN_NAME, ",
				"all_ind_columns.column_position as ORDINAL_POSITION from ",
				"all_indexes inner join all_ind_columns on ",
				"all_ind_columns.index_owner = all_indexes.owner and ",
				"all_ind_columns.index_name = all_indexes.index_name where ",
				"all_indexes.table_owner = ?");
		}
		else {
			sql = StringBundler.concat(
				"select tables.relname as TABLE_NAME, indexes.relname as ",
				"INDEX_NAME, case when pg_index.indisunique then 0 else 1 end ",
				"as NON_UNIQUE, pg_attribute.attname as COLUMN_NAME, ",
				"index_keys.ordinal as ORDINAL_POSITION from pg_index inner ",
				"join pg_class indexes on indexes.oid = pg_index.indexrelid ",
				"inner join pg_class tables on tables.oid = pg_index.indrelid ",
				"inner join pg_namespace on pg_namespace.oid = ",
				"tables.relnamespace cross join unnest(pg_index.indkey) with ",
				"ordinality as index_keys(attnum, ordinal) left join ",
				"pg_attribute on pg_attribute.attrelid = pg_index.indrelid ",
				"and pg_attribute.attnum = index_keys.attnum where ",
				"pg_namespace.nspname = ? and index_keys.ordinal <= ",
				"pg_index.indnkeyatts");
		}

		Map<String, List<String>> columnNamesMap = new LinkedHashMap<>();
		Map<String, String> indexNames = new HashMap<>();
		Map<String, String> tableNames = new HashMap<>();
		Set<String> uniqueIndexKeys = new HashSet<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sql + " order by TABLE_NAME, NON_UNIQUE, INDEX_NAME, " +
					"ORDINAL_POSITION")) {

			preparedStatement.setString(1, schemaName);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String indexName = resultSet.getString("INDEX_NAME");
					String tableName = resultSet.getString("TABLE_NAME");

					String indexKey = tableName + "." + indexName;

					List<String> columnNames = columnNamesMap.computeIfAbsent(
						indexKey, newIndexKey -> new ArrayList<>());

					columnNames.add(
						StringUtil.toLowerCase(
							GetterUtil.getString(
								resultSet.getString("COLUMN_NAME"))));

					indexNames.put(indexKey, indexName);
					tableNames.put(indexKey, tableName);

					if (resultSet.getInt("NON_UNIQUE") != 0) {
						continue;
					}

					uniqueIndexKeys.add(indexKey);
				}
			}
		}

		for (Map.Entry<String, List<String>> entry :
				columnNamesMap.entrySet()) {

			String indexKey = entry.getKey();

			indexMetadatas.add(
				new IndexMetadata(
					indexNames.get(indexKey),
					StringUtil.toLowerCase(tableNames.get(indexKey)),
					uniqueIndexKeys.contains(indexKey),
					ArrayUtil.toStringArray(entry.getValue())));
		}

		return indexMetadatas;
	}

	private Map<String, String> _getIndexTypes(Connection connection)
		throws SQLException {

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		String databaseProductName = databaseMetaData.getDatabaseProductName();

		String schemaName = null;
		String sql = null;

		if (databaseProductName.equals("MariaDB") ||
			databaseProductName.equals("MySQL")) {

			schemaName = connection.getCatalog();
			sql =
				"select distinct table_name, index_name, index_type from " +
					"information_schema.statistics where table_schema = ? " +
						"and index_type not in ('BTREE', 'HASH')";
		}
		else if (databaseProductName.equals("Microsoft SQL Server")) {
			schemaName = connection.getSchema();
			sql =
				"select object_name(object_id), name, 'filtered' from " +
					"sys.indexes where has_filter = 1 and " +
						"object_schema_name(object_id) = ?";
		}
		else if (databaseProductName.equals("Oracle")) {
			schemaName = connection.getSchema();
			sql =
				"select table_name, index_name, ityp_owner || '.' || " +
					"ityp_name from all_indexes where owner = ? and " +
						"index_type = 'DOMAIN'";
		}
		else {
			return Collections.emptyMap();
		}

		Map<String, String> indexTypes = new TreeMap<>(
			String.CASE_INSENSITIVE_ORDER);

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			preparedStatement.setString(1, schemaName);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					indexTypes.put(
						resultSet.getString(1) + "." + resultSet.getString(2),
						resultSet.getString(3));
				}
			}
		}

		return indexTypes;
	}

	private String _getPostgreSQLColumnType(
		int columnSize, int dataType, int decimalDigits) {

		if (dataType == Types.BIGINT) {
			return "bigint";
		}

		if ((dataType == Types.BINARY) || (dataType == Types.LONGVARBINARY) ||
			(dataType == Types.VARBINARY)) {

			return "bytea";
		}

		if ((dataType == Types.BIT) || (dataType == Types.BOOLEAN)) {
			return "bool";
		}

		if (dataType == Types.BLOB) {
			return "oid";
		}

		if (((dataType == Types.CHAR) || (dataType == Types.NCHAR) ||
			 (dataType == Types.NVARCHAR) || (dataType == Types.VARCHAR)) &&
			(columnSize > 0) &&
			(columnSize < DB.SQL_VARCHAR_MAX_SIZE_THRESHOLD)) {

			return "varchar(" + columnSize + ")";
		}

		if ((dataType == Types.CHAR) || (dataType == Types.CLOB) ||
			(dataType == Types.LONGNVARCHAR) ||
			(dataType == Types.LONGVARCHAR) || (dataType == Types.NCHAR) ||
			(dataType == Types.NCLOB) || (dataType == Types.NVARCHAR) ||
			(dataType == Types.VARCHAR)) {

			return "text";
		}

		if ((dataType == Types.DATE) || (dataType == Types.TIMESTAMP)) {
			return "timestamp";
		}

		if ((dataType == Types.DECIMAL) || (dataType == Types.NUMERIC)) {
			if ((columnSize <= 0) || (decimalDigits < 0)) {
				return "numeric";
			}

			if ((decimalDigits > 0) && ((columnSize - decimalDigits) <= 14) &&
				(decimalDigits <= 16)) {

				return "decimal(30, 16)";
			}

			return StringBundler.concat(
				"numeric(", columnSize, ", ", decimalDigits, ")");
		}

		if ((dataType == Types.DOUBLE) || (dataType == Types.FLOAT) ||
			(dataType == Types.REAL) ||
			(dataType == _SQL_TYPE_ORACLE_BINARY_DOUBLE) ||
			(dataType == _SQL_TYPE_ORACLE_BINARY_FLOAT)) {

			return "double precision";
		}

		if ((dataType == Types.INTEGER) || (dataType == Types.SMALLINT) ||
			(dataType == Types.TINYINT)) {

			return "integer";
		}

		if (dataType == Types.TIME) {
			return "time";
		}

		if ((dataType == Types.TIMESTAMP_WITH_TIMEZONE) ||
			(dataType == _SQL_TYPE_ORACLE_TIMESTAMP_WITH_LOCAL_TIME_ZONE) ||
			(dataType == _SQL_TYPE_ORACLE_TIMESTAMP_WITH_TIME_ZONE) ||
			(dataType == _SQL_TYPE_SQL_SERVER_DATETIMEOFFSET)) {

			return "timestamp with time zone";
		}

		return null;
	}

	private String _getPostgreSQLColumnType(ResultSet resultSet)
		throws SQLException {

		return _getPostgreSQLColumnType(
			resultSet.getInt("COLUMN_SIZE"), resultSet.getInt("DATA_TYPE"),
			resultSet.getInt("DECIMAL_DIGITS"));
	}

	private String _getQuotedName(String name) {
		return StringBundler.concat(
			StringPool.QUOTE,
			StringUtil.replace(name, CharPool.QUOTE, StringPool.DOUBLE_QUOTE),
			StringPool.QUOTE);
	}

	private String _getSanitizedString(String value) {
		return StringUtil.removeSubstring(value, StringPool.NULL_CHAR);
	}

	private Set<String> _getViews(DataSource dataSource) throws Exception {
		Set<String> views = new HashSet<>();

		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData databaseMetaData = connection.getMetaData();

			try (ResultSet resultSet = databaseMetaData.getTables(
					connection.getCatalog(), connection.getSchema(), null,
					new String[] {"VIEW"})) {

				while (resultSet.next()) {
					views.add(resultSet.getString("TABLE_NAME"));
				}
			}
		}

		return views;
	}

	private boolean _isLongName(String name) {
		byte[] bytes = name.getBytes(StandardCharsets.UTF_8);

		if (bytes.length > _POSTGRESQL_NAME_MAX_LENGTH) {
			return true;
		}

		return false;
	}

	private void _loadColumnNamesMap(
			Map<String, List<String>> columnNamesMap,
			Map<String, Integer> columnTypes, DataSource dataSource)
		throws Exception {

		Set<String> views = _getViews(dataSource);

		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData databaseMetaData = connection.getMetaData();

			try (ResultSet resultSet = databaseMetaData.getColumns(
					connection.getCatalog(), connection.getSchema(), null,
					null)) {

				while (resultSet.next()) {
					String tableName = resultSet.getString("TABLE_NAME");

					if (views.contains(tableName)) {
						continue;
					}

					String columnName = resultSet.getString("COLUMN_NAME");

					List<String> columnNames = columnNamesMap.computeIfAbsent(
						tableName, key -> new ArrayList<>());

					columnNames.add(columnName);

					columnTypes.put(
						tableName + "." + columnName,
						resultSet.getInt("DATA_TYPE"));
				}
			}
		}

		for (List<String> columnNames : columnNamesMap.values()) {
			Collections.sort(columnNames, String.CASE_INSENSITIVE_ORDER);
		}
	}

	private void _loadColumnsMetadata() throws Exception {
		_loadColumnNamesMap(
			_sourceColumnNamesMap, _sourceColumnsType, _sourceDataSource);

		Set<String> toolTableNames = new HashSet<>();

		try (Connection connection = _sourceDataSource.getConnection()) {
			DatabaseMetaData databaseMetaData = connection.getMetaData();

			String databaseProductName =
				databaseMetaData.getDatabaseProductName();

			String sql = null;

			if (databaseProductName.startsWith("DB2")) {
				Collections.addAll(
					toolTableNames, "ADVISE_INDEX", "ADVISE_INSTANCE",
					"ADVISE_MQT", "ADVISE_PARTITION", "ADVISE_TABLE",
					"ADVISE_WORKLOAD", "EXPLAIN_ACTUALS", "EXPLAIN_ARGUMENT",
					"EXPLAIN_DIAGNOSTIC", "EXPLAIN_DIAGNOSTIC_DATA",
					"EXPLAIN_INSTANCE", "EXPLAIN_OBJECT", "EXPLAIN_OPERATOR",
					"EXPLAIN_PREDICATE", "EXPLAIN_STATEMENT", "EXPLAIN_STREAM",
					"OBJECT_METRICS");
			}
			else if (databaseProductName.equals("Microsoft SQL Server")) {
				sql = "select name from sys.tables where is_ms_shipped = 1";
			}
			else if (databaseProductName.equals("Oracle")) {
				sql = StringBundler.concat(
					"select job_name from user_datapump_jobs union select ",
					"log_table from user_mview_logs union select ",
					"user_tables.table_name from user_tables inner join ",
					"user_indexes on user_tables.table_name like 'DR$' || ",
					"user_indexes.index_name || '$%' where ",
					"user_indexes.index_type = 'DOMAIN'");
			}

			if (sql != null) {
				try (PreparedStatement preparedStatement =
						connection.prepareStatement(sql);

					ResultSet resultSet = preparedStatement.executeQuery()) {

					while (resultSet.next()) {
						toolTableNames.add(resultSet.getString(1));
					}
				}
			}
		}

		for (String toolTableName : toolTableNames) {
			if (_sourceColumnNamesMap.remove(toolTableName) == null) {
				continue;
			}

			System.out.println(
				StringBundler.concat(
					"Skipping table ", toolTableName, _partitionMessage,
					" because it belongs to a database tool"));
		}

		_loadColumnNamesMap(
			_targetColumnNamesMap, _targetColumnsType, _targetDataSource);
	}

	private void _setColumn(
			int index, PreparedStatement preparedStatement, int targetType,
			String value)
		throws Exception {

		if (targetType == Types.BIGINT) {
			preparedStatement.setLong(index, GetterUtil.getLong(value));
		}
		else if ((targetType == Types.BIT) || (targetType == Types.BOOLEAN)) {
			preparedStatement.setBoolean(index, GetterUtil.getBoolean(value));
		}
		else if ((targetType == Types.LONGVARBINARY) ||
				 (targetType == Types.BINARY)) {

			preparedStatement.setBytes(index, Base64.decode(value));
		}
		else if ((targetType == Types.LONGVARCHAR) ||
				 (targetType == Types.VARCHAR)) {

			preparedStatement.setString(index, _getSanitizedString(value));
		}
		else if (targetType == Types.DOUBLE) {
			preparedStatement.setDouble(index, GetterUtil.getDouble(value));
		}
		else if (targetType == Types.INTEGER) {
			preparedStatement.setInt(index, GetterUtil.getInteger(value));
		}
		else if (targetType == Types.NUMERIC) {
			preparedStatement.setBigDecimal(
				index, (BigDecimal)GetterUtil.get(value, BigDecimal.ZERO));
		}
		else if (targetType == Types.TIME) {
			preparedStatement.setObject(index, LocalTime.parse(value));
		}
		else if (targetType == Types.TIMESTAMP) {
			DateFormat dateFormat = DateUtil.getISOFormat();

			Date date = dateFormat.parse(value);

			preparedStatement.setTimestamp(
				index, new Timestamp(date.getTime()));
		}
		else {
			throw new PortalException("Invalid type: " + targetType);
		}
	}

	private void _validateTables(
		Set<String> sourceTableNames, Set<String> targetTableNames) {

		StringBundler sb = new StringBundler();

		Iterator<String> sourceIterator = sourceTableNames.iterator();
		Iterator<String> targetIterator = targetTableNames.iterator();

		while (sourceIterator.hasNext()) {
			String sourceTableName = sourceIterator.next();
			String targetTableName = targetIterator.next();

			Set<String> targetColumnNames = new TreeSet<>(
				String.CASE_INSENSITIVE_ORDER);

			targetColumnNames.addAll(
				_targetColumnNamesMap.get(targetTableName));

			for (String sourceColumnName :
					_sourceColumnNamesMap.get(sourceTableName)) {

				if (targetColumnNames.contains(sourceColumnName)) {
					continue;
				}

				sb.append("Column ");
				sb.append(sourceColumnName);
				sb.append(" of source table ");
				sb.append(sourceTableName);
				sb.append(" does not exist in target table ");
				sb.append(targetTableName);
				sb.append(_partitionMessage);
				sb.append(StringPool.NEW_LINE);
			}
		}

		if (sb.length() > 0) {
			throw new IllegalArgumentException(sb.toString());
		}
	}

	private static final int _POSTGRESQL_NAME_MAX_LENGTH = 63;

	private static final int _POSTGRESQL_STRING_INDEX_MAX_LENGTH = 676;

	private static final int _SQL_TYPE_ORACLE_BINARY_DOUBLE = 101;

	private static final int _SQL_TYPE_ORACLE_BINARY_FLOAT = 100;

	private static final int _SQL_TYPE_ORACLE_TIMESTAMP_WITH_LOCAL_TIME_ZONE =
		-102;

	private static final int _SQL_TYPE_ORACLE_TIMESTAMP_WITH_TIME_ZONE = -101;

	private static final int _SQL_TYPE_SQL_SERVER_DATETIMEOFFSET = -155;

	private final List<String> _customColumnInfos = new ArrayList<>();
	private final Set<String> _customTableNames = new TreeSet<>(
		String.CASE_INSENSITIVE_ORDER);
	private final String _partitionMessage;
	private final Map<String, List<String>> _sourceColumnNamesMap =
		new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private final Map<String, Integer> _sourceColumnsType = new HashMap<>();
	private final DataSource _sourceDataSource;
	private final Map<String, List<String>> _targetColumnNamesMap =
		new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private final Map<String, Integer> _targetColumnsType = new HashMap<>();
	private final DataSource _targetDataSource;

}