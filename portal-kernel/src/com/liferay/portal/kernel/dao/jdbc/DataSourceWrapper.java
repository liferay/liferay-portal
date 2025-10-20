/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.jdbc;

import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * @author Shuyang Zhou
 */
public class DataSourceWrapper implements DataSource {

	public DataSourceWrapper(DataSource dataSource) {
		_dataSource = dataSource;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return _dataSource.getConnection();
	}

	@Override
	public Connection getConnection(String userName, String password)
		throws SQLException {

		return _dataSource.getConnection(userName, password);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return _dataSource.getLoginTimeout();
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return _dataSource.getLogWriter();
	}

	@Override
	public Logger getParentLogger() {
		throw new UnsupportedOperationException();
	}

	public DataSource getWrappedDataSource() {
		return _dataSource;
	}

	@Override
	public boolean isWrapperFor(Class<?> clazz) {

		// JDK 6

		return DataSource.class.equals(clazz);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		_dataSource.setLoginTimeout(seconds);
	}

	@Override
	public void setLogWriter(PrintWriter printWriter) throws SQLException {
		_dataSource.setLogWriter(printWriter);
	}

	public void setWrappedDataSource(DataSource wrappedDataSource) {
		_dataSource = wrappedDataSource;
	}

	@Override
	public <T> T unwrap(Class<T> clazz) throws SQLException {

		// JDK 6

		if (!DataSource.class.equals(clazz)) {
			throw new SQLException("Invalid class " + clazz);
		}

		return (T)this;
	}

	private volatile DataSource _dataSource;

}