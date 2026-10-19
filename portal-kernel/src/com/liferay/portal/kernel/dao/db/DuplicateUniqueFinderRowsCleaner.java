/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.db;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DuplicateUniqueFinderRowsCleaner {

	public DuplicateUniqueFinderRowsCleaner(
		Connection connection, String tableName, String[] columnNames,
		String orderByClause) {

		_connection = connection;
		_tableName = tableName;
		_columnNames = columnNames;
		_orderByClause = orderByClause;

		_db = DBManagerUtil.getDB();
	}

	public boolean deleteDuplicates() throws Exception {
		if (_hasUnpopulatedColumn()) {
			return false;
		}

		List<String[]> duplicateColumnValuesList =
			_getDuplicateColumnValuesList();

		if (duplicateColumnValuesList.isEmpty()) {
			return false;
		}

		String[] primaryKeyColumnNames = _db.getPrimaryKeyColumnNames(
			_connection, _tableName);

		if (primaryKeyColumnNames.length == 0) {
			_log.error(
				StringBundler.concat(
					"Unable to delete duplicate records in table ", _tableName,
					" because it does not have a primary key"));

			return false;
		}

		boolean duplicatesDeleted = false;

		for (String[] duplicateColumnValues : duplicateColumnValuesList) {
			List<Map<String, String>> duplicateRows = _getDuplicateRows(
				duplicateColumnValues);

			int duplicateRowsCount = duplicateRows.size();

			for (Map<String, String> duplicateRow : duplicateRows) {
				if (duplicateRowsCount == 1) {
					break;
				}

				StringBundler sb = new StringBundler();

				sb.append("delete from ");
				sb.append(_tableName);
				sb.append(" where ");

				for (String primaryKeyColumnName : primaryKeyColumnNames) {
					sb.append(primaryKeyColumnName);
					sb.append(" = ");
					sb.append(duplicateRow.get(primaryKeyColumnName));
					sb.append(" and ");
				}

				sb.setIndex(sb.index() - 1);

				try (PreparedStatement preparedStatement =
						_connection.prepareStatement(sb.toString())) {

					preparedStatement.execute();
					duplicatesDeleted = true;

					if (_log.isInfoEnabled()) {
						_log.info(
							StringBundler.concat(
								"Deleted row from table ", _tableName,
								" due to duplicate values in finder columns ",
								StringUtil.merge(_columnNames, ", "), ": ",
								duplicateRow));
					}
				}
				catch (SQLException sqlException) {
					_log.error(
						"Unable to delete row from table " + _tableName +
							duplicateRow.toString(),
						sqlException);
				}
				finally {
					duplicateRowsCount--;
				}
			}
		}

		return duplicatesDeleted;
	}

	private List<String[]> _getDuplicateColumnValuesList() throws Exception {
		List<String[]> duplicateColumnValuesList = new ArrayList<>();

		StringBundler sb = new StringBundler(7);

		sb.append("select ");
		sb.append(StringUtil.merge(_columnNames, ", "));
		sb.append(" from ");
		sb.append(_tableName);
		sb.append(" group by ");
		sb.append(StringUtil.merge(_columnNames, ", "));
		sb.append(" having count(*) > 1");

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				sb.toString());

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String[] duplicateColumnValues =
					new String[_columnNames.length];

				for (int i = 0; i < duplicateColumnValues.length; i++) {
					duplicateColumnValues[i] = resultSet.getString(i + 1);
				}

				duplicateColumnValuesList.add(duplicateColumnValues);
			}
		}

		return duplicateColumnValuesList;
	}

	private List<Map<String, String>> _getDuplicateRows(
			String[] duplicateColumnValues)
		throws Exception {

		List<Map<String, String>> duplicateRows = new ArrayList<>();

		StringBundler sb = new StringBundler();

		sb.append("select * from ");
		sb.append(_tableName);
		sb.append(" where ");

		for (int i = 0; i < _columnNames.length; i++) {
			sb.append(_columnNames[i]);

			if (duplicateColumnValues[i] == null) {
				sb.append(" is null ");
			}
			else {
				sb.append(" = ? ");
			}

			sb.append("and ");
		}

		sb.setIndex(sb.index() - 1);

		sb.append("order by ");
		sb.append(_orderByClause);

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				sb.toString())) {

			DatabaseMetaData databaseMetaData = _connection.getMetaData();
			DBInspector dbInspector = new DBInspector(_connection);
			int parameterIndex = 1;

			for (int i = 0; i < _columnNames.length; i++) {
				if (duplicateColumnValues[i] == null) {
					continue;
				}

				try (ResultSet resultSet = databaseMetaData.getColumns(
						dbInspector.getCatalog(), dbInspector.getSchema(),
						dbInspector.normalizeName(_tableName, databaseMetaData),
						dbInspector.normalizeName(
							_columnNames[i], databaseMetaData))) {

					resultSet.next();

					preparedStatement.setObject(
						parameterIndex, duplicateColumnValues[i],
						resultSet.getInt("DATA_TYPE"));

					parameterIndex++;
				}
			}

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

				String[] columnNames =
					new String[resultSetMetaData.getColumnCount()];

				for (int i = 0; i < columnNames.length; i++) {
					columnNames[i] = resultSetMetaData.getColumnName(i + 1);
				}

				while (resultSet.next()) {
					Map<String, String> duplicateRow = new LinkedHashMap<>();

					for (String columnName : columnNames) {
						duplicateRow.put(
							dbInspector.normalizeName(columnName),
							resultSet.getString(columnName));
					}

					duplicateRows.add(duplicateRow);
				}
			}
		}

		return duplicateRows;
	}

	private boolean _hasUnpopulatedColumn() throws Exception {
		Map<String, Integer> columnDataTypes = new HashMap<>();

		DatabaseMetaData databaseMetaData = _connection.getMetaData();

		DBInspector dbInspector = new DBInspector(_connection);

		try (ResultSet resultSet = databaseMetaData.getColumns(
				dbInspector.getCatalog(), dbInspector.getSchema(),
				dbInspector.normalizeName(_tableName, databaseMetaData),
				null)) {

			while (resultSet.next()) {
				columnDataTypes.put(
					StringUtil.toLowerCase(resultSet.getString("COLUMN_NAME")),
					resultSet.getInt("DATA_TYPE"));
			}
		}

		StringBundler sb = new StringBundler();

		sb.append("select count(*) as count");

		for (String columnName : _columnNames) {
			Integer dataType = columnDataTypes.get(
				StringUtil.toLowerCase(columnName));

			if ((dataType != null) && _isStringType(dataType)) {
				sb.append(", count(nullif(");
				sb.append(columnName);
				sb.append(", '')) as count_");
			}
			else {
				sb.append(", count(");
				sb.append(columnName);
				sb.append(") as count_");
			}

			sb.append(columnName);
		}

		sb.append(" from ");
		sb.append(_tableName);

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				sb.toString());

			ResultSet resultSet = preparedStatement.executeQuery()) {

			if (!resultSet.next()) {
				return false;
			}

			long totalCount = resultSet.getLong("count");

			if (totalCount == 0) {
				return false;
			}

			for (String columnName : _columnNames) {
				if (resultSet.getLong("count_" + columnName) == 0) {
					_log.error(
						StringBundler.concat(
							"Unable to delete duplicate records in table ",
							_tableName, " because all values in column ",
							columnName, " are null or empty"));

					return true;
				}
			}
		}

		return false;
	}

	private boolean _isStringType(int dataType) {
		if ((dataType == Types.CHAR) || (dataType == Types.LONGNVARCHAR) ||
			(dataType == Types.LONGVARCHAR) || (dataType == Types.NCHAR) ||
			(dataType == Types.NVARCHAR) || (dataType == Types.VARCHAR)) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DuplicateUniqueFinderRowsCleaner.class);

	private final String[] _columnNames;
	private final Connection _connection;
	private final DB _db;
	private final String _orderByClause;
	private final String _tableName;

}