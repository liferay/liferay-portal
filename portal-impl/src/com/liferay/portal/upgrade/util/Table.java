/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.util;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncBufferedWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.jdbc.postgresql.PostgreSQLJDBCUtil;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.StagnantRowException;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;

import java.math.BigDecimal;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import java.text.DateFormat;

import java.util.Date;

import org.apache.commons.lang.time.StopWatch;

/**
 * @author Alexander Chow
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public class Table {

	public Table(String tableName) {
		_tableName = tableName;
	}

	public Table(String tableName, Object[][] columns) {
		_tableName = tableName;

		setColumns(columns);
	}

	public void appendColumn(StringBuilder sb, Object value, boolean last)
		throws Exception {

		if (value == null) {
			throw new UpgradeException(
				StringBundler.concat(
					"Nulls should never be inserted into the database. ",
					"Attempted to append column to ", sb, "."));
		}
		else if (value instanceof byte[]) {
			sb.append(Base64.encode((byte[])value));
		}
		else if (value instanceof Clob || value instanceof String) {
			value = StringUtil.replace(
				(String)value, _SAFE_TABLE_CHARS[0], _SAFE_TABLE_CHARS[1]);

			sb.append(value);
		}
		else if (value instanceof Date) {
			DateFormat df = DateUtil.getISOFormat();

			sb.append(df.format(value));
		}
		else {
			sb.append(value);
		}

		sb.append(StringPool.COMMA);

		if (last) {
			sb.append(StringPool.NEW_LINE);
		}
	}

	public void appendColumn(
			StringBuilder sb, ResultSet resultSet, String name, Integer type,
			boolean last)
		throws Exception {

		Object value = null;

		try {
			value = getValue(resultSet, name, type);
		}
		catch (SQLException sqlException) {
			if (_log.isDebugEnabled()) {
				_log.debug(sqlException);
			}

			if (name.equals("uuid_")) {
				sb.append(PortalUUIDUtil.generate());
			}

			sb.append(StringPool.COMMA);

			if (last) {
				sb.append(StringPool.NEW_LINE);
			}

			return;
		}

		appendColumn(sb, value, last);
	}

	public void generateTempFile() throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			generateTempFile(connection);
		}
	}

	public void generateTempFile(Connection connection) throws Exception {
		boolean empty = true;

		String tempFileName = String.valueOf(
			Files.createTempFile(
				Paths.get(SystemProperties.get(SystemProperties.TMP_DIR)),
				"temp-db-" + _tableName + "-", null));

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Starting backup of ", _tableName, " to ", tempFileName));
		}

		try (UnsyncBufferedWriter unsyncBufferedWriter =
				new UnsyncBufferedWriter(new FileWriter(tempFileName));

			PreparedStatement preparedStatement = getSelectPreparedStatement(
				connection);

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String data = null;

				try {
					data = getExportedData(resultSet);

					unsyncBufferedWriter.write(data);

					_totalRows++;

					empty = false;
				}
				catch (StagnantRowException stagnantRowException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Skipping stagnant data in ", _tableName, ": ",
								stagnantRowException.getMessage()));
					}
				}
			}

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Finished backup of ", _tableName, " to ", tempFileName,
						" in ", stopWatch.getTime(), " ms"));
			}
		}
		catch (Exception exception) {
			FileUtil.delete(tempFileName);

			throw exception;
		}

		if (!empty) {
			_tempFileName = tempFileName;

			return;
		}

		FileUtil.delete(tempFileName);
	}

	public Object[][] getColumns() {
		return _columns;
	}

	public String getCreateSQL() {
		return _createSQL;
	}

	public String getDeleteSQL() {
		return "DELETE FROM " + _tableName;
	}

	public String getExportedData(ResultSet resultSet) throws Exception {
		StringBuilder sb = new StringBuilder();

		Object[][] columns = getColumns();

		for (int i = 0; i < columns.length; i++) {
			boolean last = false;

			if ((i + 1) == columns.length) {
				last = true;
			}

			appendColumn(
				sb, resultSet, (String)columns[i][0], (Integer)columns[i][1],
				last);
		}

		return sb.toString();
	}

	public String getInsertSQL() {
		String sql = "INSERT INTO " + getInsertTableName() + " (";

		for (int i = 0; i < _order.length; i++) {
			int pos = _order[i];

			sql += _columns[pos][0];

			if ((i + 1) < _columns.length) {
				sql += ", ";
			}
			else {
				sql += ") VALUES (";
			}
		}

		for (int i = 0; i < _columns.length; i++) {
			sql += "?";

			if ((i + 1) < _columns.length) {
				sql += ", ";
			}
			else {
				sql += ")";
			}
		}

		return sql;
	}

	public String getInsertTableName() {
		String createSQL = getCreateSQL();

		if (Validator.isNotNull(createSQL)) {
			String createSQLLowerCase = StringUtil.toLowerCase(createSQL);

			int x = createSQLLowerCase.indexOf("create table ");

			if (x == -1) {
				return _tableName;
			}

			x += 13;

			int y = createSQL.indexOf(" ", x);

			return StringUtil.trim(createSQL.substring(x, y));
		}

		return _tableName;
	}

	public int[] getOrder() {
		return _order;
	}

	public PreparedStatement getSelectPreparedStatement(Connection connection)
		throws Exception {

		return connection.prepareStatement(getSelectSQL());
	}

	public String getSelectSQL() {
		if (_selectSQL == null) {
			/*String sql = "select ";

			for (int i = 0; i < _columns.length; i++) {
				sql += _columns[i][0];

				if ((i + 1) < _columns.length) {
					sql += ", ";
				}
				else {
					sql += " from " + _tableName;
				}
			}

			return sql;*/

			return "select * from " + _tableName;
		}

		return _selectSQL;
	}

	public String getTableName() {
		return _tableName;
	}

	public String getTempFileName() {
		return _tempFileName;
	}

	public long getTotalRows() {
		return _totalRows;
	}

	/**
	 * @see com.liferay.object.service.impl.ObjectEntryLocalServiceImpl#_getValue
	 */
	public Object getValue(ResultSet resultSet, String name, Integer type)
		throws Exception {

		Object value = null;

		int t = type.intValue();

		if (t == Types.BIGINT) {
			try {
				value = GetterUtil.getLong(resultSet.getLong(name));
			}
			catch (SQLException sqlException) {
				if (_log.isDebugEnabled()) {
					_log.debug(sqlException);
				}

				value = GetterUtil.getLong(resultSet.getString(name));
			}
		}
		else if (t == Types.BIT) {
			value = GetterUtil.getBoolean(resultSet.getBoolean(name));
		}
		else if ((t == Types.BLOB) || (t == Types.LONGVARBINARY)) {
			DBType dbType = DBManagerUtil.getDBType();

			if (dbType.equals(DBType.POSTGRESQL) &&
				PostgreSQLJDBCUtil.isPGStatement(resultSet.getStatement())) {

				value = PostgreSQLJDBCUtil.getLargeObject(resultSet, name);
			}
			else {
				value = resultSet.getBytes(name);
			}

			if (value == null) {
				value = new byte[0];
			}
		}
		else if (t == Types.BOOLEAN) {
			value = GetterUtil.getBoolean(resultSet.getBoolean(name));
		}
		else if (t == Types.CLOB) {
			try {
				Clob clob = resultSet.getClob(name);

				if (clob == null) {
					value = StringPool.BLANK;
				}
				else {
					try (Reader reader = clob.getCharacterStream();

						UnsyncBufferedReader unsyncBufferedReader =
							new UnsyncBufferedReader(reader)) {

						StringBundler sb = new StringBundler();

						String line = null;

						while ((line = unsyncBufferedReader.readLine()) !=
									null) {

							if (sb.length() != 0) {
								sb.append(_SAFE_TABLE_NEWLINE_CHARACTER);
							}

							sb.append(line);
						}

						value = sb.toString();
					}
				}
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				// If the database doesn't allow CLOB types for the column
				// value, then try retrieving it as a String

				value = GetterUtil.getString(resultSet.getString(name));
			}
		}
		else if (t == Types.DECIMAL) {
			try {
				value = resultSet.getBigDecimal(name);
			}
			catch (SQLException sqlException) {
				if (_log.isDebugEnabled()) {
					_log.debug(sqlException);
				}

				value = resultSet.getString(name);
			}

			value = GetterUtil.get(value, BigDecimal.ZERO);
		}
		else if (t == Types.DOUBLE) {
			value = GetterUtil.getDouble(resultSet.getDouble(name));
		}
		else if (t == Types.FLOAT) {
			value = GetterUtil.getFloat(resultSet.getFloat(name));
		}
		else if (t == Types.INTEGER) {
			value = GetterUtil.getInteger(resultSet.getInt(name));
		}
		else if (t == Types.LONGVARCHAR) {
			value = GetterUtil.getString(resultSet.getString(name));
		}
		else if (t == Types.NUMERIC) {
			value = GetterUtil.getLong(resultSet.getLong(name));
		}
		else if (t == Types.SMALLINT) {
			value = GetterUtil.getShort(resultSet.getShort(name));
		}
		else if (t == Types.TIMESTAMP) {
			try {
				value = resultSet.getTimestamp(name);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}

			if (value == null) {
				value = StringPool.NULL;
			}
		}
		else if (t == Types.TINYINT) {
			value = GetterUtil.getShort(resultSet.getShort(name));
		}
		else if (t == Types.VARCHAR) {
			value = GetterUtil.getString(resultSet.getString(name));
		}
		else {
			throw new UpgradeException(
				"Upgrade code using unsupported class type " + type);
		}

		return value;
	}

	public void populateTable() throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			populateTable(connection);
		}
	}

	public void populateTable(Connection connection) throws Exception {
		if (_tempFileName == null) {
			return;
		}

		try (PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection, getInsertSQL());
			UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new FileReader(_tempFileName))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				String[] values = StringUtil.split(line);

				Object[][] columns = getColumns();

				if (values.length != columns.length) {
					throw new UpgradeException(
						StringBundler.concat(
							"Column lengths differ between temp file and ",
							"schema. Attempted to insert row ", line, "."));
				}

				int[] order = getOrder();

				for (int i = 0; i < order.length; i++) {
					int pos = order[i];

					setColumn(
						preparedStatement, i, (Integer)columns[pos][1],
						values[pos]);
				}

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		}

		if (_log.isDebugEnabled()) {
			_log.debug(getTableName() + " table populated with data");
		}
	}

	public void populateTableRows(
			PreparedStatement preparedStatement, boolean batch)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Updating rows for " + getTableName());
		}

		if (batch) {
			preparedStatement.executeBatch();
		}
		else {
			preparedStatement.executeUpdate();
		}

		preparedStatement.close();
	}

	/**
	 * @see com.liferay.object.service.impl.ObjectEntryLocalServiceImpl#_setColumn
	 */
	public void setColumn(
			PreparedStatement preparedStatement, int index, Integer type,
			String value)
		throws Exception {

		index++;

		int t = type.intValue();

		if (t == Types.BIGINT) {
			preparedStatement.setLong(index, GetterUtil.getLong(value));
		}
		else if ((t == Types.BLOB) || (t == Types.LONGVARBINARY)) {
			byte[] valueBytes = Base64.decode(value);

			if (PostgreSQLJDBCUtil.isPGStatement(preparedStatement)) {
				PostgreSQLJDBCUtil.setLargeObject(
					preparedStatement, index, valueBytes);
			}
			else {
				preparedStatement.setBytes(index, valueBytes);
			}
		}
		else if (t == Types.BOOLEAN) {
			preparedStatement.setBoolean(index, GetterUtil.getBoolean(value));
		}
		else if ((t == Types.CLOB) || (t == Types.LONGVARCHAR) ||
				 (t == Types.VARCHAR)) {

			value = StringUtil.replace(
				value, _SAFE_TABLE_CHARS[1], _SAFE_TABLE_CHARS[0]);

			preparedStatement.setString(index, value);
		}
		else if (t == Types.DECIMAL) {
			preparedStatement.setBigDecimal(
				index, (BigDecimal)GetterUtil.get(value, BigDecimal.ZERO));
		}
		else if (t == Types.DOUBLE) {
			preparedStatement.setDouble(index, GetterUtil.getDouble(value));
		}
		else if (t == Types.FLOAT) {
			preparedStatement.setFloat(index, GetterUtil.getFloat(value));
		}
		else if (t == Types.INTEGER) {
			preparedStatement.setInt(index, GetterUtil.getInteger(value));
		}
		else if (t == Types.SMALLINT) {
			preparedStatement.setShort(index, GetterUtil.getShort(value));
		}
		else if (t == Types.TIMESTAMP) {
			if (StringPool.NULL.equals(value)) {
				preparedStatement.setTimestamp(index, null);
			}
			else {
				DateFormat dateFormat = DateUtil.getISOFormat();

				Date date = dateFormat.parse(value);

				preparedStatement.setTimestamp(
					index, new Timestamp(date.getTime()));
			}
		}
		else if (t == Types.TINYINT) {
			preparedStatement.setShort(index, GetterUtil.getShort(value));
		}
		else {
			throw new UpgradeException(
				"Upgrade code using unsupported class type " + type);
		}
	}

	public void setColumns(Object[][] columns) {
		_columns = columns;

		// LEP-7331

		_order = new int[_columns.length];

		int clobCount = 0;

		for (int i = 0; i < _columns.length; ++i) {
			Integer type = (Integer)columns[i][1];

			if (type.intValue() == Types.CLOB) {
				clobCount++;

				int pos = _columns.length - clobCount;

				_order[pos] = i;
			}
			else {
				int pos = i - clobCount;

				_order[pos] = i;
			}
		}
	}

	public void setCreateSQL(String createSQL) throws Exception {
		_createSQL = createSQL;
	}

	public void setSelectSQL(String selectSQL) {
		_selectSQL = selectSQL;
	}

	public void updateColumnValue(
		String columnName, String oldValue, String newValue) {

		String sql = StringBundler.concat(
			"update ", _tableName, " set ", columnName, " = ? where ",
			columnName, " = ?");

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			preparedStatement.setString(1, newValue);
			preparedStatement.setString(2, oldValue);

			preparedStatement.executeUpdate();
		}
		catch (SQLException sqlException) {
			_log.error(sqlException);

			throw new RuntimeException(
				"Unable to execute " + sql, sqlException);
		}
	}

	private static final String[][] _SAFE_TABLE_CHARS = {
		{StringPool.COMMA, StringPool.NEW_LINE, StringPool.RETURN},
		{
			Table._SAFE_TABLE_COMMA_CHARACTER,
			Table._SAFE_TABLE_NEWLINE_CHARACTER,
			Table._SAFE_TABLE_RETURN_CHARACTER
		}
	};

	private static final String _SAFE_TABLE_COMMA_CHARACTER =
		"_SAFE_TABLE_COMMA_CHARACTER_";

	private static final String _SAFE_TABLE_NEWLINE_CHARACTER =
		"_SAFE_TABLE_NEWLINE_CHARACTER_";

	private static final String _SAFE_TABLE_RETURN_CHARACTER =
		"_SAFE_TABLE_RETURN_CHARACTER_";

	private static final Log _log = LogFactoryUtil.getLog(Table.class);

	private Object[][] _columns;
	private String _createSQL;
	private int[] _order;
	private String _selectSQL;
	private final String _tableName;
	private String _tempFileName;
	private long _totalRows;

}