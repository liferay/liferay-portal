/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.migration.importer;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.jdbc.postgresql.PostgreSQLJDBCUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.module.framework.ThrowableCollector;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.db.migration.importer.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.tools.db.migration.importer.jdbc.ConnectionConfigUtil;

import java.io.Reader;

import java.math.BigDecimal;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.sql.DataSource;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DBCopyTablesProcess {

	public DBCopyTablesProcess(
		DataSource sourceDataSource, DataSource targetDataSource) {

		_sourceDataSource = sourceDataSource;
		_targetDataSource = targetDataSource;
	}

	public void run() throws Exception {
		_loadColumnsMetadata();

		_copyTables();
	}

	private void _copyTable(String sourceTableName, String targetTableName)
		throws Exception {

		List<String> sourceColumnNames = _sourceColumnNamesMap.get(
			sourceTableName);
		List<String> targetColumnNames = _targetColumnNamesMap.get(
			targetTableName);

		if (sourceColumnNames.size() > targetColumnNames.size()) {
			throw new IllegalStateException(
				StringBundler.concat(
					"Source table ", targetTableName, " has ",
					sourceColumnNames.size(), " but target table name has ",
					targetColumnNames.size(), " columns"));
		}
		else if (sourceColumnNames.size() < targetColumnNames.size()) {
			Set<String> sourceColumnNamesSet = new TreeSet<String>(
				String.CASE_INSENSITIVE_ORDER) {

				{
					addAll(sourceColumnNames);
				}
			};

			targetColumnNames.removeIf(
				columnName -> !sourceColumnNamesSet.contains(columnName));
		}

		String selectSQL = StringBundler.concat(
			"select ", StringUtil.merge(sourceColumnNames), " from ",
			sourceTableName);
		String insertSQL = StringBundler.concat(
			"insert into ", targetTableName, "(",
			StringUtil.merge(targetColumnNames), ") values (",
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
						}
						catch (Exception exception) {
							throwableCollector.collect(exception);
						}
					}));
		}

		for (Future<?> future : futures) {
			future.get();
		}

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

		if ((sourceType == Types.BIGINT) || (sourceType == Types.NUMERIC)) {
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
				 (sourceType == Types.LONGVARBINARY)) {

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
		else if (sourceType == Types.CLOB) {
			Clob value = resultSet.getClob(columnName);

			if (value == null) {
				preparedStatement.setNull(index, targetType);

				return;
			}

			try (Reader reader = value.getCharacterStream();
				UnsyncBufferedReader unsyncBufferedReader =
					new UnsyncBufferedReader(reader)) {

				StringBundler sb = new StringBundler();

				String line = null;

				while ((line = unsyncBufferedReader.readLine()) != null) {
					if (sb.length() != 0) {
						sb.append("\n");
					}

					sb.append(line);
				}

				valueString = sb.toString();
			}
		}
		else if (sourceType == Types.DECIMAL) {
			BigDecimal value = resultSet.getBigDecimal(columnName);

			if (value == null) {
				preparedStatement.setNull(index, targetType);

				return;
			}

			if (targetType == Types.NUMERIC) {
				preparedStatement.setBigDecimal(index, value);

				return;
			}

			valueString = value.toString();
		}
		else if ((sourceType == Types.DOUBLE) ||
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
		else if (sourceType == Types.FLOAT) {
			float value = resultSet.getFloat(columnName);

			if ((value == 0.0F) && resultSet.wasNull()) {
				preparedStatement.setNull(index, targetType);

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
		else if ((sourceType == Types.LONGVARCHAR) ||
				 (sourceType == Types.NVARCHAR) ||
				 (sourceType == Types.VARCHAR)) {

			String value = resultSet.getString(columnName);

			if (value == null) {
				preparedStatement.setNull(index, targetType);

				return;
			}

			if ((targetType == Types.LONGNVARCHAR) ||
				(targetType == Types.VARCHAR)) {

				preparedStatement.setString(index, value);

				return;
			}

			valueString = value;
		}
		else if (sourceType == Types.TIMESTAMP) {
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
		else if ((sourceType == Types.TINYINT) ||
				 (sourceType == Types.SMALLINT)) {

			short value = resultSet.getShort(columnName);

			if ((value == 0) && resultSet.wasNull()) {
				preparedStatement.setNull(index, targetType);

				return;
			}

			valueString = String.valueOf(value);
		}
		else {
			throw new PortalException("Invalid type " + sourceType);
		}

		_setColumn(index, preparedStatement, targetType, valueString);
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

			preparedStatement.setString(index, value);
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

	private static final int _SQL_TYPE_ORACLE_BINARY_DOUBLE = 101;

	private final Map<String, List<String>> _sourceColumnNamesMap =
		new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private final Map<String, Integer> _sourceColumnsType = new HashMap<>();
	private final DataSource _sourceDataSource;
	private final Map<String, List<String>> _targetColumnNamesMap =
		new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private final Map<String, Integer> _targetColumnsType = new HashMap<>();
	private final DataSource _targetDataSource;

}