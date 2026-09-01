/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.db;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Adolfo Pérez
 */
public class DBInspector {

	public static void disableCache() {
		_cacheEnabled = false;

		_columnTypes.clear();
		_tableNamesMap.clear();
	}

	public static void enableCache() {
		_cacheEnabled = true;
	}

	public DBInspector(Connection connection) {
		_connection = connection;
	}

	public String getCatalog() throws SQLException {
		return _connection.getCatalog();
	}

	public ResultSet getColumnsResultSet(String tableName) throws SQLException {
		return _getColumnsResultSet(tableName, null);
	}

	public List<String> getControlTableNames(Collection<String> tableNames)
		throws SQLException {

		List<String> controlTableNames = new ArrayList<>();

		for (String tableName : tableNames) {
			if (isControlTable(tableName)) {
				controlTableNames.add(normalizeName(tableName));
			}
		}

		return controlTableNames;
	}

	public String getSchema() {
		try {
			return _connection.getSchema();
		}
		catch (Throwable throwable) {
			if (_log.isDebugEnabled()) {
				_log.debug(throwable, throwable);
			}

			return null;
		}
	}

	public List<String> getTableNames(String tableNamePattern)
		throws SQLException {

		if (_cacheEnabled && (tableNamePattern == null)) {
			String cacheKey = _getSchemaCacheKey();

			Set<String> tableNames = _tableNamesMap.get(cacheKey);

			if (tableNames != null) {
				return new ArrayList<>(tableNames);
			}

			Set<String> schemaTableNames = new TreeSet<>(
				String.CASE_INSENSITIVE_ORDER);

			schemaTableNames.addAll(_getNames(null, "TABLE"));

			_tableNamesMap.putIfAbsent(cacheKey, schemaTableNames);

			return new ArrayList<>(schemaTableNames);
		}

		return _getNames(tableNamePattern, "TABLE");
	}

	public List<String> getViewNames(String viewNamePattern)
		throws SQLException {

		return _getNames(viewNamePattern, "VIEW");
	}

	public boolean hasColumn(String tableName, String columnName)
		throws Exception {

		if ((columnName == null) || (tableName == null)) {
			return false;
		}

		if (_cacheEnabled) {
			if (_getColumnType(tableName, columnName) != _COLUMN_TYPE_NONE) {
				return true;
			}

			return false;
		}

		try (ResultSet resultSet = _getColumnsResultSet(
				tableName, columnName)) {

			return resultSet.next();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	public boolean hasColumnType(
			String tableName, String columnName, String columnType)
		throws Exception {

		if ((columnName == null) || (columnType == null) ||
			(tableName == null)) {

			return false;
		}

		try (ResultSet resultSet = _getColumnsResultSet(
				tableName, columnName)) {

			if (!resultSet.next()) {
				return false;
			}

			int expectedColumnSize = _getColumnSize(columnType);

			int actualColumnSize = resultSet.getInt("COLUMN_SIZE");

			if ((expectedColumnSize != DB.SQL_SIZE_NONE) &&
				(((expectedColumnSize != DB.SQL_VARCHAR_MAX_SIZE) &&
				  (expectedColumnSize != actualColumnSize)) ||
				 ((expectedColumnSize == DB.SQL_VARCHAR_MAX_SIZE) &&
				  (actualColumnSize < DB.SQL_VARCHAR_MAX_SIZE_THRESHOLD)))) {

				return false;
			}

			Integer expectedColumnDataType = _getByColumnType(
				columnType, DB::getSQLType);

			int actualColumnDataType = resultSet.getInt("DATA_TYPE");

			if ((expectedColumnDataType == null) ||
				(expectedColumnDataType != actualColumnDataType)) {

				return false;
			}

			Integer expectedColumnDecimalDigits = _getByColumnType(
				columnType, DB::getSQLTypeDecimalDigits);

			if (expectedColumnDecimalDigits != DB.SQL_SIZE_NONE) {
				int actualColumnDecimalDigits = resultSet.getInt(
					"DECIMAL_DIGITS");

				if (expectedColumnDecimalDigits != actualColumnDecimalDigits) {
					return false;
				}
			}

			boolean expectedColumnNullable = _isColumnNullable(columnType);

			int actualColumnNullable = resultSet.getInt("NULLABLE");

			if ((expectedColumnNullable &&
				 (actualColumnNullable != DatabaseMetaData.columnNullable)) ||
				(!expectedColumnNullable &&
				 (actualColumnNullable != DatabaseMetaData.columnNoNulls))) {

				return false;
			}

			if (!expectedColumnNullable) {
				String expectedColumnDefaultValue = _getColumnDefaultValue(
					columnType);
				String actualColumnDefaultValue = _getColumnDefaultValue(
					resultSet.getString("COLUMN_DEF"), DB::getDefaultValue);

				if (Validator.isNull(expectedColumnDefaultValue) &&
					Validator.isNull(actualColumnDefaultValue)) {

					return true;
				}

				return StringUtil.equals(
					expectedColumnDefaultValue, actualColumnDefaultValue);
			}

			return true;
		}
	}

	public boolean hasIndex(String tableName, String indexName)
		throws Exception {

		if ((indexName == null) || (tableName == null)) {
			return false;
		}

		DB db = DBManagerUtil.getDB();
		DatabaseMetaData databaseMetaData = _connection.getMetaData();

		try (ResultSet resultSet = db.getIndexResultSet(
				_connection, normalizeName(tableName, databaseMetaData),
				false)) {

			while (resultSet.next()) {
				if (StringUtil.equalsIgnoreCase(
						indexName, resultSet.getString("index_name"))) {

					return true;
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	public boolean hasRows(String tableName) {
		if (tableName == null) {
			return false;
		}

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"select count(*) as count from " + tableName);

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				if (resultSet.getLong("count") > 0) {
					return true;
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	public boolean hasTable(String tableName) throws Exception {
		if (_cacheEnabled) {
			String cacheKey = _getSchemaCacheKey();

			Set<String> tableNames = _tableNamesMap.get(cacheKey);

			if (tableNames == null) {
				getTableNames(null);

				tableNames = _tableNamesMap.get(cacheKey);
			}

			if (tableNames != null) {
				return tableNames.contains(normalizeName(tableName));
			}
		}

		return _hasElement(tableName, "TABLE");
	}

	public boolean hasView(String viewName) throws Exception {
		return _hasElement(viewName, "VIEW");
	}

	public boolean isControlTable(String tableName) {
		if (_controlTableNames.contains(StringUtil.toLowerCase(tableName)) ||
			StringUtil.toLowerCase(
				tableName
			).startsWith(
				"quartz"
			)) {

			return true;
		}

		return false;
	}

	public boolean isNullable(String tableName, String columnName)
		throws SQLException {

		if ((columnName == null) || (tableName == null)) {
			return false;
		}

		try (ResultSet resultSet = _getColumnsResultSet(
				tableName, columnName)) {

			if (!resultSet.next()) {
				throw new SQLException(
					StringBundler.concat(
						"Column ", tableName, StringPool.PERIOD, columnName,
						" does not exist"));
			}

			if (resultSet.getInt("NULLABLE") ==
					DatabaseMetaData.columnNullable) {

				return true;
			}

			return false;
		}
	}

	public boolean isNumeric(String tableName, String columnName)
		throws Exception {

		if ((columnName == null) || (tableName == null)) {
			return false;
		}

		if (_cacheEnabled) {
			return _isNumericType(_getColumnType(tableName, columnName));
		}

		try (ResultSet resultSet = _getColumnsResultSet(
				tableName, columnName)) {

			if (!resultSet.next()) {
				return false;
			}

			return _isNumericType(resultSet.getInt("DATA_TYPE"));
		}
	}

	public boolean isObjectTable(List<Long> companyIds, String tableName) {
		if (tableName == null) {
			return false;
		}

		String lowerCaseTableName = StringUtil.toLowerCase(tableName);

		for (long companyId : companyIds) {

			// See ObjectDefinitionImpl#getExtensionDBTableName and
			// ObjectDefinitionLocalServiceImpl#_getDBTableName

			if (lowerCaseTableName.endsWith("_x_" + companyId) ||
				lowerCaseTableName.startsWith("l_" + companyId + "_") ||
				lowerCaseTableName.startsWith("o_" + companyId + "_") ||
				lowerCaseTableName.startsWith("r_")) {

				return true;
			}
		}

		return false;
	}

	public boolean isObjectTable(String tableName) {
		return isObjectTable(
			ListUtil.fromArray(PortalInstancePool.getCompanyIds()), tableName);
	}

	public boolean isPartitionedControlTable(String tableName) {
		return _partitionedControlTableNames.contains(
			StringUtil.toLowerCase(tableName));
	}

	public String normalizeName(String name) throws SQLException {
		return normalizeName(name, _connection.getMetaData());
	}

	public String normalizeName(String name, DatabaseMetaData databaseMetaData)
		throws SQLException {

		if (databaseMetaData.storesLowerCaseIdentifiers()) {
			return StringUtil.toLowerCase(name);
		}

		if (databaseMetaData.storesUpperCaseIdentifiers()) {
			return StringUtil.toUpperCase(name);
		}

		return name;
	}

	private Integer _getByColumnType(
		String columnType, BiFunction<DB, String, Integer> biFunction) {

		Matcher matcher = _columnTypePattern.matcher(columnType);

		if (!matcher.lookingAt()) {
			return null;
		}

		return biFunction.apply(DBManagerUtil.getDB(), matcher.group(1));
	}

	private String _getColumnDefaultValue(String columnType) {
		Matcher matcher = _columnDefaultClausePattern.matcher(columnType);

		if (matcher.find()) {
			return StringUtil.unquote(matcher.group(1));
		}

		return null;
	}

	private String _getColumnDefaultValue(
		String columnDef, BiFunction<DB, String, String> biFunction) {

		if (Validator.isNull(columnDef)) {
			return columnDef;
		}

		return biFunction.apply(DBManagerUtil.getDB(), columnDef);
	}

	private int _getColumnSize(String columnType) throws Exception {
		Matcher matcher = _columnSizePattern.matcher(columnType);

		if (!matcher.matches()) {
			return DB.SQL_SIZE_NONE;
		}

		String columnSize = matcher.group(1);

		if (Validator.isNotNull(columnSize)) {
			try {
				return Integer.parseInt(columnSize);
			}
			catch (NumberFormatException numberFormatException) {
				throw new Exception(
					StringBundler.concat(
						"Column type ", columnType,
						" has an invalid column size ", columnSize),
					numberFormatException);
			}
		}

		Integer dataTypeSize = _getByColumnType(columnType, DB::getSQLTypeSize);

		if (dataTypeSize != null) {
			return dataTypeSize;
		}

		return DB.SQL_SIZE_NONE;
	}

	private ResultSet _getColumnsResultSet(String tableName, String columnName)
		throws SQLException {

		DatabaseMetaData databaseMetaData = _connection.getMetaData();

		if (columnName != null) {
			columnName = normalizeName(columnName, databaseMetaData);
		}

		return databaseMetaData.getColumns(
			getCatalog(), getSchema(),
			normalizeName(tableName, databaseMetaData), columnName);
	}

	private int _getColumnType(String tableName, String columnName)
		throws Exception {

		DatabaseMetaData databaseMetaData = _connection.getMetaData();

		String normalizedColumnName = normalizeName(
			columnName, databaseMetaData);
		String normalizedTableName = normalizeName(tableName, databaseMetaData);

		String cacheKey = StringBundler.concat(
			_getSchemaCacheKey(), ".", normalizedTableName, ".",
			normalizedColumnName);

		Integer columnType = _columnTypes.get(cacheKey);

		if (columnType != null) {
			return columnType;
		}

		columnType = _COLUMN_TYPE_NONE;

		try (ResultSet resultSet = _getColumnsResultSet(
				normalizedTableName, normalizedColumnName)) {

			if (resultSet.next()) {
				columnType = resultSet.getInt("DATA_TYPE");
			}
		}

		_columnTypes.put(cacheKey, columnType);

		return columnType;
	}

	private List<String> _getNames(String namePattern, String elementType)
		throws SQLException {

		List<String> names = new ArrayList<>();

		DatabaseMetaData databaseMetaData = _connection.getMetaData();

		try (ResultSet resultSet = databaseMetaData.getTables(
				_connection.getCatalog(), _connection.getSchema(), namePattern,
				new String[] {elementType})) {

			while (resultSet.next()) {
				names.add(resultSet.getString("TABLE_NAME"));
			}
		}

		return names;
	}

	private String _getSchemaCacheKey() throws SQLException {
		return Objects.toString(getCatalog(), StringPool.BLANK) + "." +
			Objects.toString(getSchema(), StringPool.BLANK);
	}

	private boolean _hasElement(String elementName, String elementType)
		throws Exception {

		if (elementName == null) {
			return false;
		}

		DatabaseMetaData databaseMetaData = _connection.getMetaData();

		elementName = normalizeName(elementName, databaseMetaData);

		try (ResultSet resultSet = databaseMetaData.getTables(
				getCatalog(), getSchema(), elementName,
				new String[] {elementType})) {

			if (resultSet.next()) {
				return true;
			}
		}

		return false;
	}

	private boolean _isColumnNullable(String typeName) {
		typeName = typeName.trim();

		typeName = StringUtil.toLowerCase(typeName);

		return !typeName.endsWith("not null");
	}

	private boolean _isNumericType(int columnType) {
		if ((columnType == Types.BIGINT) || (columnType == Types.DECIMAL) ||
			(columnType == Types.DOUBLE) || (columnType == Types.FLOAT) ||
			(columnType == Types.INTEGER) || (columnType == Types.NUMERIC) ||
			(columnType == Types.REAL) || (columnType == Types.SMALLINT) ||
			(columnType == Types.TINYINT)) {

			return true;
		}

		return false;
	}

	private static final int _COLUMN_TYPE_NONE = Integer.MIN_VALUE;

	private static final Log _log = LogFactoryUtil.getLog(DBInspector.class);

	private static volatile boolean _cacheEnabled;
	private static final Pattern _columnDefaultClausePattern = Pattern.compile(
		".*DEFAULT ((?:'[^']+')|(?:\\S+)) NOT NULL", Pattern.CASE_INSENSITIVE);
	private static final Pattern _columnSizePattern = Pattern.compile(
		"^\\w+(?:\\((\\d+)\\))?.*", Pattern.CASE_INSENSITIVE);
	private static final Pattern _columnTypePattern = Pattern.compile(
		"(^\\w+)", Pattern.CASE_INSENSITIVE);
	private static final Map<String, Integer> _columnTypes =
		new ConcurrentHashMap<>();
	private static final Set<String> _controlTableNames = new HashSet<>(
		Arrays.asList(
			"company", "release_", "servicecomponent", "virtualhost"));
	private static final Set<String> _partitionedControlTableNames =
		new HashSet<>(Arrays.asList("classname_", "counter", "resourceaction"));
	private static final Map<String, Set<String>> _tableNamesMap =
		new ConcurrentHashMap<>();

	private final Connection _connection;

}