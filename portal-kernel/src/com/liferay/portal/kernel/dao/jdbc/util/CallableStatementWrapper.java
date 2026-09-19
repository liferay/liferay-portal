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
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;

import java.util.Calendar;
import java.util.Map;

/**
 * @author István András Dézsi
 */
public class CallableStatementWrapper
	extends StatementWrapper implements CallableStatement {

	public CallableStatementWrapper(CallableStatement callableStatement) {
		super(callableStatement);

		_callableStatement = callableStatement;
	}

	@Override
	public void addBatch() throws SQLException {
		_callableStatement.addBatch();
	}

	@Override
	public void clearParameters() throws SQLException {
		_callableStatement.clearParameters();
	}

	@Override
	public boolean execute() throws SQLException {
		return _callableStatement.execute();
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		return _callableStatement.executeQuery();
	}

	@Override
	public int executeUpdate() throws SQLException {
		return _callableStatement.executeUpdate();
	}

	@Override
	public Array getArray(int parameterIndex) throws SQLException {
		return _callableStatement.getArray(parameterIndex);
	}

	@Override
	public Array getArray(String parameterName) throws SQLException {
		return _callableStatement.getArray(parameterName);
	}

	@Override
	public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		return _callableStatement.getBigDecimal(parameterIndex);
	}

	@Override
	public BigDecimal getBigDecimal(int parameterIndex, int scale)
		throws SQLException {

		return _callableStatement.getBigDecimal(parameterIndex, scale);
	}

	@Override
	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		return _callableStatement.getBigDecimal(parameterName);
	}

	@Override
	public Blob getBlob(int parameterIndex) throws SQLException {
		return _callableStatement.getBlob(parameterIndex);
	}

	@Override
	public Blob getBlob(String parameterName) throws SQLException {
		return _callableStatement.getBlob(parameterName);
	}

	@Override
	public boolean getBoolean(int parameterIndex) throws SQLException {
		return _callableStatement.getBoolean(parameterIndex);
	}

	@Override
	public boolean getBoolean(String parameterName) throws SQLException {
		return _callableStatement.getBoolean(parameterName);
	}

	@Override
	public byte getByte(int parameterIndex) throws SQLException {
		return _callableStatement.getByte(parameterIndex);
	}

	@Override
	public byte getByte(String parameterName) throws SQLException {
		return _callableStatement.getByte(parameterName);
	}

	@Override
	public byte[] getBytes(int parameterIndex) throws SQLException {
		return _callableStatement.getBytes(parameterIndex);
	}

	@Override
	public byte[] getBytes(String parameterName) throws SQLException {
		return _callableStatement.getBytes(parameterName);
	}

	@Override
	public Reader getCharacterStream(int parameterIndex) throws SQLException {
		return _callableStatement.getCharacterStream(parameterIndex);
	}

	@Override
	public Reader getCharacterStream(String parameterName) throws SQLException {
		return _callableStatement.getCharacterStream(parameterName);
	}

	@Override
	public Clob getClob(int parameterIndex) throws SQLException {
		return _callableStatement.getClob(parameterIndex);
	}

	@Override
	public Clob getClob(String parameterName) throws SQLException {
		return _callableStatement.getClob(parameterName);
	}

	@Override
	public Date getDate(int parameterIndex) throws SQLException {
		return _callableStatement.getDate(parameterIndex);
	}

	@Override
	public Date getDate(int parameterIndex, Calendar calendar)
		throws SQLException {

		return _callableStatement.getDate(parameterIndex, calendar);
	}

	@Override
	public Date getDate(String parameterName) throws SQLException {
		return _callableStatement.getDate(parameterName);
	}

	@Override
	public Date getDate(String parameterName, Calendar calendar)
		throws SQLException {

		return _callableStatement.getDate(parameterName, calendar);
	}

	@Override
	public double getDouble(int parameterIndex) throws SQLException {
		return _callableStatement.getDouble(parameterIndex);
	}

	@Override
	public double getDouble(String parameterName) throws SQLException {
		return _callableStatement.getDouble(parameterName);
	}

	@Override
	public float getFloat(int parameterIndex) throws SQLException {
		return _callableStatement.getFloat(parameterIndex);
	}

	@Override
	public float getFloat(String parameterName) throws SQLException {
		return _callableStatement.getFloat(parameterName);
	}

	@Override
	public int getInt(int parameterIndex) throws SQLException {
		return _callableStatement.getInt(parameterIndex);
	}

	@Override
	public int getInt(String parameterName) throws SQLException {
		return _callableStatement.getInt(parameterName);
	}

	@Override
	public long getLong(int parameterIndex) throws SQLException {
		return _callableStatement.getLong(parameterIndex);
	}

	@Override
	public long getLong(String parameterName) throws SQLException {
		return _callableStatement.getLong(parameterName);
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return _callableStatement.getMetaData();
	}

	@Override
	public Reader getNCharacterStream(int parameterIndex) throws SQLException {
		return _callableStatement.getNCharacterStream(parameterIndex);
	}

	@Override
	public Reader getNCharacterStream(String parameterName)
		throws SQLException {

		return _callableStatement.getNCharacterStream(parameterName);
	}

	@Override
	public NClob getNClob(int parameterIndex) throws SQLException {
		return _callableStatement.getNClob(parameterIndex);
	}

	@Override
	public NClob getNClob(String parameterName) throws SQLException {
		return _callableStatement.getNClob(parameterName);
	}

	@Override
	public String getNString(int parameterIndex) throws SQLException {
		return _callableStatement.getNString(parameterIndex);
	}

	@Override
	public String getNString(String parameterName) throws SQLException {
		return _callableStatement.getNString(parameterName);
	}

	@Override
	public Object getObject(int parameterIndex) throws SQLException {
		return _callableStatement.getObject(parameterIndex);
	}

	@Override
	public <T> T getObject(int parameterIndex, Class<T> clazz)
		throws SQLException {

		return _callableStatement.getObject(parameterIndex, clazz);
	}

	@Override
	public Object getObject(int parameterIndex, Map<String, Class<?>> map)
		throws SQLException {

		return _callableStatement.getObject(parameterIndex, map);
	}

	@Override
	public Object getObject(String parameterName) throws SQLException {
		return _callableStatement.getObject(parameterName);
	}

	@Override
	public <T> T getObject(String parameterName, Class<T> clazz)
		throws SQLException {

		return _callableStatement.getObject(parameterName, clazz);
	}

	@Override
	public Object getObject(String parameterName, Map<String, Class<?>> map)
		throws SQLException {

		return _callableStatement.getObject(parameterName, map);
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return _callableStatement.getParameterMetaData();
	}

	@Override
	public Ref getRef(int parameterIndex) throws SQLException {
		return _callableStatement.getRef(parameterIndex);
	}

	@Override
	public Ref getRef(String parameterName) throws SQLException {
		return _callableStatement.getRef(parameterName);
	}

	@Override
	public RowId getRowId(int parameterIndex) throws SQLException {
		return _callableStatement.getRowId(parameterIndex);
	}

	@Override
	public RowId getRowId(String parameterName) throws SQLException {
		return _callableStatement.getRowId(parameterName);
	}

	@Override
	public SQLXML getSQLXML(int parameterIndex) throws SQLException {
		return _callableStatement.getSQLXML(parameterIndex);
	}

	@Override
	public SQLXML getSQLXML(String parameterName) throws SQLException {
		return _callableStatement.getSQLXML(parameterName);
	}

	@Override
	public short getShort(int parameterIndex) throws SQLException {
		return _callableStatement.getShort(parameterIndex);
	}

	@Override
	public short getShort(String parameterName) throws SQLException {
		return _callableStatement.getShort(parameterName);
	}

	@Override
	public String getString(int parameterIndex) throws SQLException {
		return _callableStatement.getString(parameterIndex);
	}

	@Override
	public String getString(String parameterName) throws SQLException {
		return _callableStatement.getString(parameterName);
	}

	@Override
	public Time getTime(int parameterIndex) throws SQLException {
		return _callableStatement.getTime(parameterIndex);
	}

	@Override
	public Time getTime(int parameterIndex, Calendar calendar)
		throws SQLException {

		return _callableStatement.getTime(parameterIndex, calendar);
	}

	@Override
	public Time getTime(String parameterName) throws SQLException {
		return _callableStatement.getTime(parameterName);
	}

	@Override
	public Time getTime(String parameterName, Calendar calendar)
		throws SQLException {

		return _callableStatement.getTime(parameterName, calendar);
	}

	@Override
	public Timestamp getTimestamp(int parameterIndex) throws SQLException {
		return _callableStatement.getTimestamp(parameterIndex);
	}

	@Override
	public Timestamp getTimestamp(int parameterIndex, Calendar calendar)
		throws SQLException {

		return _callableStatement.getTimestamp(parameterIndex, calendar);
	}

	@Override
	public Timestamp getTimestamp(String parameterName) throws SQLException {
		return _callableStatement.getTimestamp(parameterName);
	}

	@Override
	public Timestamp getTimestamp(String parameterName, Calendar calendar)
		throws SQLException {

		return _callableStatement.getTimestamp(parameterName, calendar);
	}

	@Override
	public URL getURL(int parameterIndex) throws SQLException {
		return _callableStatement.getURL(parameterIndex);
	}

	@Override
	public URL getURL(String parameterName) throws SQLException {
		return _callableStatement.getURL(parameterName);
	}

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType)
		throws SQLException {

		_callableStatement.registerOutParameter(parameterIndex, sqlType);
	}

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType, int scale)
		throws SQLException {

		_callableStatement.registerOutParameter(parameterIndex, sqlType, scale);
	}

	@Override
	public void registerOutParameter(
			int parameterIndex, int sqlType, String typeName)
		throws SQLException {

		_callableStatement.registerOutParameter(
			parameterIndex, sqlType, typeName);
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType)
		throws SQLException {

		_callableStatement.registerOutParameter(parameterName, sqlType);
	}

	@Override
	public void registerOutParameter(
			String parameterName, int sqlType, int scale)
		throws SQLException {

		_callableStatement.registerOutParameter(parameterName, sqlType, scale);
	}

	@Override
	public void registerOutParameter(
			String parameterName, int sqlType, String typeName)
		throws SQLException {

		_callableStatement.registerOutParameter(
			parameterName, sqlType, typeName);
	}

	@Override
	public void setArray(int parameterIndex, Array array) throws SQLException {
		_callableStatement.setArray(parameterIndex, array);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream inputStream)
		throws SQLException {

		_callableStatement.setAsciiStream(parameterIndex, inputStream);
	}

	@Override
	public void setAsciiStream(
			int parameterIndex, InputStream inputStream, int length)
		throws SQLException {

		_callableStatement.setAsciiStream(parameterIndex, inputStream, length);
	}

	@Override
	public void setAsciiStream(
			int parameterIndex, InputStream inputStream, long length)
		throws SQLException {

		_callableStatement.setAsciiStream(parameterIndex, inputStream, length);
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream inputStream)
		throws SQLException {

		_callableStatement.setAsciiStream(parameterName, inputStream);
	}

	@Override
	public void setAsciiStream(
			String parameterName, InputStream inputStream, int length)
		throws SQLException {

		_callableStatement.setAsciiStream(parameterName, inputStream, length);
	}

	@Override
	public void setAsciiStream(
			String parameterName, InputStream inputStream, long length)
		throws SQLException {

		_callableStatement.setAsciiStream(parameterName, inputStream, length);
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal bigDecimal)
		throws SQLException {

		_callableStatement.setBigDecimal(parameterIndex, bigDecimal);
	}

	@Override
	public void setBigDecimal(String parameterName, BigDecimal bigDecimal)
		throws SQLException {

		_callableStatement.setBigDecimal(parameterName, bigDecimal);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream inputStream)
		throws SQLException {

		_callableStatement.setBinaryStream(parameterIndex, inputStream);
	}

	@Override
	public void setBinaryStream(
			int parameterIndex, InputStream inputStream, int length)
		throws SQLException {

		_callableStatement.setBinaryStream(parameterIndex, inputStream, length);
	}

	@Override
	public void setBinaryStream(
			int parameterIndex, InputStream inputStream, long length)
		throws SQLException {

		_callableStatement.setBinaryStream(parameterIndex, inputStream, length);
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream inputStream)
		throws SQLException {

		_callableStatement.setBinaryStream(parameterName, inputStream);
	}

	@Override
	public void setBinaryStream(
			String parameterName, InputStream inputStream, int length)
		throws SQLException {

		_callableStatement.setBinaryStream(parameterName, inputStream, length);
	}

	@Override
	public void setBinaryStream(
			String parameterName, InputStream inputStream, long length)
		throws SQLException {

		_callableStatement.setBinaryStream(parameterName, inputStream, length);
	}

	@Override
	public void setBlob(int parameterIndex, Blob blob) throws SQLException {
		_callableStatement.setBlob(parameterIndex, blob);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream)
		throws SQLException {

		_callableStatement.setBlob(parameterIndex, inputStream);
	}

	@Override
	public void setBlob(
			int parameterIndex, InputStream inputStream, long length)
		throws SQLException {

		_callableStatement.setBlob(parameterIndex, inputStream, length);
	}

	@Override
	public void setBlob(String parameterName, Blob blob) throws SQLException {
		_callableStatement.setBlob(parameterName, blob);
	}

	@Override
	public void setBlob(String parameterName, InputStream inputStream)
		throws SQLException {

		_callableStatement.setBlob(parameterName, inputStream);
	}

	@Override
	public void setBlob(
			String parameterName, InputStream inputStream, long length)
		throws SQLException {

		_callableStatement.setBlob(parameterName, inputStream, length);
	}

	@Override
	public void setBoolean(int parameterIndex, boolean booleanValue)
		throws SQLException {

		_callableStatement.setBoolean(parameterIndex, booleanValue);
	}

	@Override
	public void setBoolean(String parameterName, boolean booleanValue)
		throws SQLException {

		_callableStatement.setBoolean(parameterName, booleanValue);
	}

	@Override
	public void setByte(int parameterIndex, byte byteValue)
		throws SQLException {

		_callableStatement.setByte(parameterIndex, byteValue);
	}

	@Override
	public void setByte(String parameterName, byte byteValue)
		throws SQLException {

		_callableStatement.setByte(parameterName, byteValue);
	}

	@Override
	public void setBytes(int parameterIndex, byte[] bytes) throws SQLException {
		_callableStatement.setBytes(parameterIndex, bytes);
	}

	@Override
	public void setBytes(String parameterName, byte[] bytes)
		throws SQLException {

		_callableStatement.setBytes(parameterName, bytes);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader)
		throws SQLException {

		_callableStatement.setCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setCharacterStream(
			int parameterIndex, Reader reader, int length)
		throws SQLException {

		_callableStatement.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setCharacterStream(
			int parameterIndex, Reader reader, long length)
		throws SQLException {

		_callableStatement.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader)
		throws SQLException {

		_callableStatement.setCharacterStream(parameterName, reader);
	}

	@Override
	public void setCharacterStream(
			String parameterName, Reader reader, int length)
		throws SQLException {

		_callableStatement.setCharacterStream(parameterName, reader, length);
	}

	@Override
	public void setCharacterStream(
			String parameterName, Reader reader, long length)
		throws SQLException {

		_callableStatement.setCharacterStream(parameterName, reader, length);
	}

	@Override
	public void setClob(int parameterIndex, Clob clob) throws SQLException {
		_callableStatement.setClob(parameterIndex, clob);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		_callableStatement.setClob(parameterIndex, reader);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length)
		throws SQLException {

		_callableStatement.setClob(parameterIndex, reader, length);
	}

	@Override
	public void setClob(String parameterName, Clob clob) throws SQLException {
		_callableStatement.setClob(parameterName, clob);
	}

	@Override
	public void setClob(String parameterName, Reader reader)
		throws SQLException {

		_callableStatement.setClob(parameterName, reader);
	}

	@Override
	public void setClob(String parameterName, Reader reader, long length)
		throws SQLException {

		_callableStatement.setClob(parameterName, reader, length);
	}

	@Override
	public void setDate(int parameterIndex, Date date) throws SQLException {
		_callableStatement.setDate(parameterIndex, date);
	}

	@Override
	public void setDate(int parameterIndex, Date date, Calendar calendar)
		throws SQLException {

		_callableStatement.setDate(parameterIndex, date, calendar);
	}

	@Override
	public void setDate(String parameterName, Date date) throws SQLException {
		_callableStatement.setDate(parameterName, date);
	}

	@Override
	public void setDate(String parameterName, Date date, Calendar calendar)
		throws SQLException {

		_callableStatement.setDate(parameterName, date, calendar);
	}

	@Override
	public void setDouble(int parameterIndex, double doubleValue)
		throws SQLException {

		_callableStatement.setDouble(parameterIndex, doubleValue);
	}

	@Override
	public void setDouble(String parameterName, double doubleValue)
		throws SQLException {

		_callableStatement.setDouble(parameterName, doubleValue);
	}

	@Override
	public void setFloat(int parameterIndex, float floatValue)
		throws SQLException {

		_callableStatement.setFloat(parameterIndex, floatValue);
	}

	@Override
	public void setFloat(String parameterName, float floatValue)
		throws SQLException {

		_callableStatement.setFloat(parameterName, floatValue);
	}

	@Override
	public void setInt(int parameterIndex, int intValue) throws SQLException {
		_callableStatement.setInt(parameterIndex, intValue);
	}

	@Override
	public void setInt(String parameterName, int intValue) throws SQLException {
		_callableStatement.setInt(parameterName, intValue);
	}

	@Override
	public void setLong(int parameterIndex, long longValue)
		throws SQLException {

		_callableStatement.setLong(parameterIndex, longValue);
	}

	@Override
	public void setLong(String parameterName, long longValue)
		throws SQLException {

		_callableStatement.setLong(parameterName, longValue);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader reader)
		throws SQLException {

		_callableStatement.setNCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setNCharacterStream(
			int parameterIndex, Reader reader, long length)
		throws SQLException {

		_callableStatement.setNCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setNCharacterStream(String parameterName, Reader reader)
		throws SQLException {

		_callableStatement.setNCharacterStream(parameterName, reader);
	}

	@Override
	public void setNCharacterStream(
			String parameterName, Reader reader, long length)
		throws SQLException {

		_callableStatement.setNCharacterStream(parameterName, reader, length);
	}

	@Override
	public void setNClob(int parameterIndex, NClob nClob) throws SQLException {
		_callableStatement.setNClob(parameterIndex, nClob);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader)
		throws SQLException {

		_callableStatement.setNClob(parameterIndex, reader);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length)
		throws SQLException {

		_callableStatement.setNClob(parameterIndex, reader, length);
	}

	@Override
	public void setNClob(String parameterName, NClob nClob)
		throws SQLException {

		_callableStatement.setNClob(parameterName, nClob);
	}

	@Override
	public void setNClob(String parameterName, Reader reader)
		throws SQLException {

		_callableStatement.setNClob(parameterName, reader);
	}

	@Override
	public void setNClob(String parameterName, Reader reader, long length)
		throws SQLException {

		_callableStatement.setNClob(parameterName, reader, length);
	}

	@Override
	public void setNString(int parameterIndex, String stringValue)
		throws SQLException {

		_callableStatement.setNString(parameterIndex, stringValue);
	}

	@Override
	public void setNString(String parameterName, String stringValue)
		throws SQLException {

		_callableStatement.setNString(parameterName, stringValue);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		_callableStatement.setNull(parameterIndex, sqlType);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName)
		throws SQLException {

		_callableStatement.setNull(parameterIndex, sqlType, typeName);
	}

	@Override
	public void setNull(String parameterName, int sqlType) throws SQLException {
		_callableStatement.setNull(parameterName, sqlType);
	}

	@Override
	public void setNull(String parameterName, int sqlType, String typeName)
		throws SQLException {

		_callableStatement.setNull(parameterName, sqlType, typeName);
	}

	@Override
	public void setObject(int parameterIndex, Object object)
		throws SQLException {

		_callableStatement.setObject(parameterIndex, object);
	}

	@Override
	public void setObject(int parameterIndex, Object object, int targetSQLType)
		throws SQLException {

		_callableStatement.setObject(parameterIndex, object, targetSQLType);
	}

	@Override
	public void setObject(
			int parameterIndex, Object object, int targetSQLType,
			int scaleOrLength)
		throws SQLException {

		_callableStatement.setObject(
			parameterIndex, object, targetSQLType, scaleOrLength);
	}

	@Override
	public void setObject(String parameterName, Object object)
		throws SQLException {

		_callableStatement.setObject(parameterName, object);
	}

	@Override
	public void setObject(
			String parameterName, Object object, int targetSQLType)
		throws SQLException {

		_callableStatement.setObject(parameterName, object, targetSQLType);
	}

	@Override
	public void setObject(
			String parameterName, Object object, int targetSQLType, int scale)
		throws SQLException {

		_callableStatement.setObject(
			parameterName, object, targetSQLType, scale);
	}

	@Override
	public void setRef(int parameterIndex, Ref ref) throws SQLException {
		_callableStatement.setRef(parameterIndex, ref);
	}

	@Override
	public void setRowId(int parameterIndex, RowId rowId) throws SQLException {
		_callableStatement.setRowId(parameterIndex, rowId);
	}

	@Override
	public void setRowId(String parameterName, RowId rowId)
		throws SQLException {

		_callableStatement.setRowId(parameterName, rowId);
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML sqlXML)
		throws SQLException {

		_callableStatement.setSQLXML(parameterIndex, sqlXML);
	}

	@Override
	public void setSQLXML(String parameterName, SQLXML sqlXML)
		throws SQLException {

		_callableStatement.setSQLXML(parameterName, sqlXML);
	}

	@Override
	public void setShort(int parameterIndex, short shortValue)
		throws SQLException {

		_callableStatement.setShort(parameterIndex, shortValue);
	}

	@Override
	public void setShort(String parameterName, short shortValue)
		throws SQLException {

		_callableStatement.setShort(parameterName, shortValue);
	}

	@Override
	public void setString(int parameterIndex, String stringValue)
		throws SQLException {

		_callableStatement.setString(parameterIndex, stringValue);
	}

	@Override
	public void setString(String parameterName, String stringValue)
		throws SQLException {

		_callableStatement.setString(parameterName, stringValue);
	}

	@Override
	public void setTime(int parameterIndex, Time time) throws SQLException {
		_callableStatement.setTime(parameterIndex, time);
	}

	@Override
	public void setTime(int parameterIndex, Time time, Calendar calendar)
		throws SQLException {

		_callableStatement.setTime(parameterIndex, time, calendar);
	}

	@Override
	public void setTime(String parameterName, Time time) throws SQLException {
		_callableStatement.setTime(parameterName, time);
	}

	@Override
	public void setTime(String parameterName, Time time, Calendar calendar)
		throws SQLException {

		_callableStatement.setTime(parameterName, time, calendar);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp timestamp)
		throws SQLException {

		_callableStatement.setTimestamp(parameterIndex, timestamp);
	}

	@Override
	public void setTimestamp(
			int parameterIndex, Timestamp timestamp, Calendar calendar)
		throws SQLException {

		_callableStatement.setTimestamp(parameterIndex, timestamp, calendar);
	}

	@Override
	public void setTimestamp(String parameterName, Timestamp timestamp)
		throws SQLException {

		_callableStatement.setTimestamp(parameterName, timestamp);
	}

	@Override
	public void setTimestamp(
			String parameterName, Timestamp timestamp, Calendar calendar)
		throws SQLException {

		_callableStatement.setTimestamp(parameterName, timestamp, calendar);
	}

	@Override
	public void setURL(int parameterIndex, URL url) throws SQLException {
		_callableStatement.setURL(parameterIndex, url);
	}

	@Override
	public void setURL(String parameterName, URL url) throws SQLException {
		_callableStatement.setURL(parameterName, url);
	}

	@Override
	public void setUnicodeStream(
			int parameterIndex, InputStream inputStream, int length)
		throws SQLException {

		_callableStatement.setUnicodeStream(
			parameterIndex, inputStream, length);
	}

	@Override
	public <T> T unwrap(Class<T> clazz) throws SQLException {
		if (!CallableStatement.class.equals(clazz)) {
			throw new SQLException("Invalid class " + clazz);
		}

		return (T)this;
	}

	@Override
	public boolean wasNull() throws SQLException {
		return _callableStatement.wasNull();
	}

	private volatile CallableStatement _callableStatement;

}