/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.jdbc.util;

import java.io.InputStream;
import java.io.Reader;

import java.math.BigDecimal;

import java.net.URL;

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;

import java.util.Calendar;

/**
 * @author István András Dézsi
 */
public class PreparedStatementWrapper
	extends StatementWrapper implements PreparedStatement {

	public PreparedStatementWrapper(PreparedStatement preparedStatement) {
		super(preparedStatement);

		_preparedStatement = preparedStatement;
	}

	@Override
	public void addBatch() throws SQLException {
		_preparedStatement.addBatch();
	}

	@Override
	public void clearParameters() throws SQLException {
		_preparedStatement.clearParameters();
	}

	@Override
	public boolean execute() throws SQLException {
		return _preparedStatement.execute();
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		return _preparedStatement.executeQuery();
	}

	@Override
	public int executeUpdate() throws SQLException {
		return _preparedStatement.executeUpdate();
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return _preparedStatement.getMetaData();
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return _preparedStatement.getParameterMetaData();
	}

	@Override
	public void setArray(int parameterIndex, Array array) throws SQLException {
		_preparedStatement.setArray(parameterIndex, array);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream inputStream)
		throws SQLException {

		_preparedStatement.setAsciiStream(parameterIndex, inputStream);
	}

	@Override
	public void setAsciiStream(
			int parameterIndex, InputStream inputStream, int length)
		throws SQLException {

		_preparedStatement.setAsciiStream(parameterIndex, inputStream, length);
	}

	@Override
	public void setAsciiStream(
			int parameterIndex, InputStream inputStream, long length)
		throws SQLException {

		_preparedStatement.setAsciiStream(parameterIndex, inputStream, length);
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal bigDecimal)
		throws SQLException {

		_preparedStatement.setBigDecimal(parameterIndex, bigDecimal);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream inputStream)
		throws SQLException {

		_preparedStatement.setBinaryStream(parameterIndex, inputStream);
	}

	@Override
	public void setBinaryStream(
			int parameterIndex, InputStream inputStream, int length)
		throws SQLException {

		_preparedStatement.setBinaryStream(parameterIndex, inputStream, length);
	}

	@Override
	public void setBinaryStream(
			int parameterIndex, InputStream inputStream, long length)
		throws SQLException {

		_preparedStatement.setBinaryStream(parameterIndex, inputStream, length);
	}

	@Override
	public void setBlob(int parameterIndex, Blob blob) throws SQLException {
		_preparedStatement.setBlob(parameterIndex, blob);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream)
		throws SQLException {

		_preparedStatement.setBlob(parameterIndex, inputStream);
	}

	@Override
	public void setBlob(
			int parameterIndex, InputStream inputStream, long length)
		throws SQLException {

		_preparedStatement.setBlob(parameterIndex, inputStream, length);
	}

	@Override
	public void setBoolean(int parameterIndex, boolean booleanValue)
		throws SQLException {

		_preparedStatement.setBoolean(parameterIndex, booleanValue);
	}

	@Override
	public void setByte(int parameterIndex, byte byteValue)
		throws SQLException {

		_preparedStatement.setByte(parameterIndex, byteValue);
	}

	@Override
	public void setBytes(int parameterIndex, byte[] bytes) throws SQLException {
		_preparedStatement.setBytes(parameterIndex, bytes);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader)
		throws SQLException {

		_preparedStatement.setCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setCharacterStream(
			int parameterIndex, Reader reader, int length)
		throws SQLException {

		_preparedStatement.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setCharacterStream(
			int parameterIndex, Reader reader, long length)
		throws SQLException {

		_preparedStatement.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setClob(int parameterIndex, Clob clob) throws SQLException {
		_preparedStatement.setClob(parameterIndex, clob);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		_preparedStatement.setClob(parameterIndex, reader);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length)
		throws SQLException {

		_preparedStatement.setClob(parameterIndex, reader, length);
	}

	@Override
	public void setDate(int parameterIndex, Date date) throws SQLException {
		_preparedStatement.setDate(parameterIndex, date);
	}

	@Override
	public void setDate(int parameterIndex, Date date, Calendar calendar)
		throws SQLException {

		_preparedStatement.setDate(parameterIndex, date, calendar);
	}

	@Override
	public void setDouble(int parameterIndex, double doubleValue)
		throws SQLException {

		_preparedStatement.setDouble(parameterIndex, doubleValue);
	}

	@Override
	public void setFloat(int parameterIndex, float floatValue)
		throws SQLException {

		_preparedStatement.setFloat(parameterIndex, floatValue);
	}

	@Override
	public void setInt(int parameterIndex, int intValue) throws SQLException {
		_preparedStatement.setInt(parameterIndex, intValue);
	}

	@Override
	public void setLong(int parameterIndex, long longValue)
		throws SQLException {

		_preparedStatement.setLong(parameterIndex, longValue);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader reader)
		throws SQLException {

		_preparedStatement.setNCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setNCharacterStream(
			int parameterIndex, Reader reader, long length)
		throws SQLException {

		_preparedStatement.setNCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setNClob(int parameterIndex, NClob nClob) throws SQLException {
		_preparedStatement.setNClob(parameterIndex, nClob);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader)
		throws SQLException {

		_preparedStatement.setNClob(parameterIndex, reader);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length)
		throws SQLException {

		_preparedStatement.setNClob(parameterIndex, reader, length);
	}

	@Override
	public void setNString(int parameterIndex, String stringValue)
		throws SQLException {

		_preparedStatement.setNString(parameterIndex, stringValue);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		_preparedStatement.setNull(parameterIndex, sqlType);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName)
		throws SQLException {

		_preparedStatement.setNull(parameterIndex, sqlType, typeName);
	}

	@Override
	public void setObject(int parameterIndex, Object object)
		throws SQLException {

		_preparedStatement.setObject(parameterIndex, object);
	}

	@Override
	public void setObject(int parameterIndex, Object object, int targetSQLType)
		throws SQLException {

		_preparedStatement.setObject(parameterIndex, object, targetSQLType);
	}

	@Override
	public void setObject(
			int parameterIndex, Object object, int targetSQLType,
			int scaleOrLength)
		throws SQLException {

		_preparedStatement.setObject(
			parameterIndex, object, targetSQLType, scaleOrLength);
	}

	@Override
	public void setRef(int parameterIndex, Ref ref) throws SQLException {
		_preparedStatement.setRef(parameterIndex, ref);
	}

	@Override
	public void setRowId(int parameterIndex, RowId rowId) throws SQLException {
		_preparedStatement.setRowId(parameterIndex, rowId);
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML sqlXML)
		throws SQLException {

		_preparedStatement.setSQLXML(parameterIndex, sqlXML);
	}

	@Override
	public void setShort(int parameterIndex, short shortValue)
		throws SQLException {

		_preparedStatement.setShort(parameterIndex, shortValue);
	}

	@Override
	public void setString(int parameterIndex, String stringValue)
		throws SQLException {

		_preparedStatement.setString(parameterIndex, stringValue);
	}

	@Override
	public void setTime(int parameterIndex, Time time) throws SQLException {
		_preparedStatement.setTime(parameterIndex, time);
	}

	@Override
	public void setTime(int parameterIndex, Time time, Calendar calendar)
		throws SQLException {

		_preparedStatement.setTime(parameterIndex, time, calendar);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp timestamp)
		throws SQLException {

		_preparedStatement.setTimestamp(parameterIndex, timestamp);
	}

	@Override
	public void setTimestamp(
			int parameterIndex, Timestamp timestamp, Calendar calendar)
		throws SQLException {

		_preparedStatement.setTimestamp(parameterIndex, timestamp, calendar);
	}

	@Override
	public void setURL(int parameterIndex, URL url) throws SQLException {
		_preparedStatement.setURL(parameterIndex, url);
	}

	@Override
	public void setUnicodeStream(
			int parameterIndex, InputStream inputStream, int length)
		throws SQLException {

		_preparedStatement.setUnicodeStream(
			parameterIndex, inputStream, length);
	}

	@Override
	public <T> T unwrap(Class<T> clazz) throws SQLException {
		if (!PreparedStatement.class.equals(clazz)) {
			throw new SQLException("Invalid class " + clazz);
		}

		return (T)this;
	}

	private volatile PreparedStatement _preparedStatement;

}