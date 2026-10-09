/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.live;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kevin Lee
 */
public class LiveUpgradeSchemaDiff {

	public LiveUpgradeSchemaDiff(Connection connection, String tableName)
		throws Exception {

		_dbInspector = new DBInspector(connection);

		try (ResultSet resultSet = _dbInspector.getColumnsResultSet(
				tableName)) {

			while (resultSet.next()) {
				String columnName = resultSet.getString("COLUMN_NAME");

				_resultColumnsMap.put(
					columnName, new Column(columnName, false));
			}
		}
	}

	public Map<String, String> getResultColumnNamesMap() {
		Map<String, String> resultColumnNamesMap = new HashMap<>();

		for (Column column : _resultColumnsMap.values()) {
			if (column.isAdded()) {
				continue;
			}

			resultColumnNamesMap.put(
				column.getOldColumnName(), column.getNewColumnName());
		}

		return resultColumnNamesMap;
	}

	public Map<String, String> getResultDefaultValuesMap() {
		Map<String, String> defaultValuesMap = new HashMap<>();

		for (Column column : _resultColumnsMap.values()) {
			if (column.getDefaultValue() == null) {
				continue;
			}

			defaultValuesMap.put(
				column.getNewColumnName(), column.getDefaultValue());
		}

		return defaultValuesMap;
	}

	public void recordAddColumns(String... columnDefinitions) throws Exception {
		for (String columnDefinition : columnDefinitions) {
			String columnName = _dbInspector.normalizeName(
				StringUtil.extractFirst(columnDefinition, StringPool.SPACE));

			if (_resultColumnsMap.containsKey(columnName)) {
				throw new IllegalArgumentException(
					"Column " + columnName + " already exists");
			}

			String defaultValue = _getColumnDefaultValue(
				columnDefinition.substring(
					columnDefinition.indexOf(StringPool.SPACE) + 1));

			if (Validator.isNull(defaultValue)) {
				defaultValue = null;
			}

			_resultColumnsMap.put(
				columnName, new Column(columnName, defaultValue, true));
		}
	}

	public void recordAlterColumnName(
			String oldColumnName, String newColumnDefinition)
		throws Exception {

		oldColumnName = _dbInspector.normalizeName(oldColumnName);

		String newColumnName = _dbInspector.normalizeName(
			StringUtil.extractFirst(newColumnDefinition, StringPool.SPACE));

		Column column = _resultColumnsMap.remove(oldColumnName);

		if (column == null) {
			throw new IllegalArgumentException(
				"Column " + oldColumnName + " does not exist");
		}
		else if (_resultColumnsMap.containsKey(newColumnName)) {
			throw new IllegalArgumentException(
				"Column " + newColumnName + " already exists");
		}

		column.setNewColumnName(newColumnName);

		_resultColumnsMap.put(newColumnName, column);
	}

	public void recordAlterColumnType(String columnName, String newColumnType)
		throws Exception {

		columnName = _dbInspector.normalizeName(columnName);

		Column column = _resultColumnsMap.get(columnName);

		if (column == null) {
			throw new IllegalArgumentException(
				"Column " + columnName + " does not exist");
		}

		String defaultValue = _getColumnDefaultValue(newColumnType);

		if (Validator.isNotNull(defaultValue)) {
			column.setDefaultValue(defaultValue);
		}
	}

	public void recordDropColumns(String... columnNames) throws SQLException {
		for (String columnName : columnNames) {
			columnName = _dbInspector.normalizeName(columnName);

			Column column = _resultColumnsMap.remove(columnName);

			if (column == null) {
				throw new IllegalArgumentException(
					"Column " + columnName + " does not exist");
			}
		}
	}

	private String _getColumnDefaultValue(String columnType) {
		Matcher matcher = _columnDefaultClausePattern.matcher(columnType);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	private static final Pattern _columnDefaultClausePattern = Pattern.compile(
		".*DEFAULT ((?:'[^']+')|(?:\\S+)) NOT NULL", Pattern.CASE_INSENSITIVE);

	private final DBInspector _dbInspector;
	private final Map<String, Column> _resultColumnsMap = new HashMap<>();

	private static class Column {

		public Column(String columnName, boolean added) {
			this(columnName, null, added);
		}

		public Column(String columnName, String defaultValue, boolean added) {
			_defaultValue = defaultValue;
			_added = added;

			_newColumnName = columnName;
			_oldColumnName = columnName;
		}

		public String getDefaultValue() {
			return _defaultValue;
		}

		public String getNewColumnName() {
			return _newColumnName;
		}

		public String getOldColumnName() {
			return _oldColumnName;
		}

		public boolean isAdded() {
			return _added;
		}

		public void setDefaultValue(String defaultValue) {
			_defaultValue = defaultValue;
		}

		public void setNewColumnName(String newColumnName) {
			_newColumnName = newColumnName;
		}

		private final boolean _added;
		private String _defaultValue;
		private String _newColumnName;
		private final String _oldColumnName;

	}

}