/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.jdbc.util;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.spring.hibernate.SpringHibernateThreadLocalUtil;

import jakarta.servlet.http.HttpSession;

import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * @author Dante Wang
 */
public class DynamicDataSource implements DataSource {

	public DynamicDataSource(
		DataSource readDataSource, DataSource writeDataSource) {

		_readDataSource = readDataSource;
		_writeDataSource = writeDataSource;
	}

	@Override
	public Connection getConnection() throws SQLException {
		DataSource dataSource = _getDataSource();

		return dataSource.getConnection();
	}

	@Override
	public Connection getConnection(String userName, String password)
		throws SQLException {

		DataSource dataSource = _getDataSource();

		return dataSource.getConnection(userName, password);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		DataSource dataSource = _getDataSource();

		return dataSource.getLoginTimeout();
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		DataSource dataSource = _getDataSource();

		return dataSource.getLogWriter();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		DataSource dataSource = _getDataSource();

		return dataSource.getParentLogger();
	}

	public DataSource getReadDataSource() {
		return _readDataSource;
	}

	public DataSource getWriteDataSource() {
		return _writeDataSource;
	}

	@Override
	public boolean isWrapperFor(Class<?> clazz) throws SQLException {
		DataSource dataSource = _getDataSource();

		return dataSource.isWrapperFor(clazz);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		DataSource dataSource = _getDataSource();

		dataSource.setLoginTimeout(seconds);
	}

	@Override
	public void setLogWriter(PrintWriter printWriter) throws SQLException {
		DataSource dataSource = _getDataSource();

		dataSource.setLogWriter(printWriter);
	}

	@Override
	public <T> T unwrap(Class<T> clazz) throws SQLException {
		DataSource dataSource = _getDataSource();

		return dataSource.unwrap(clazz);
	}

	private DataSource _getDataSource() {
		if (!_writeDynamicDataSource.get() &&
			SpringHibernateThreadLocalUtil.isCurrentTransactionReadOnly()) {

			if (PropsValues.JDBC_READ_DATA_SOURCE_UNAVAILABLE_TIMEOUT > 0) {
				HttpSession httpSession =
					PortalSessionThreadLocal.getHttpSession();

				if (httpSession == null) {
					if (_log.isTraceEnabled()) {
						_log.trace(
							"No context HTTP session exists, skip getting " +
								"the write data source's last used date");
					}
				}
				else {
					long lastUsedDate = GetterUtil.getLong(
						httpSession.getAttribute(
							_WRITE_DATA_SOURCE_LAST_USED_DATE));

					if (PropsValues.JDBC_READ_DATA_SOURCE_UNAVAILABLE_TIMEOUT >
							(System.currentTimeMillis() - lastUsedDate)) {

						if (_log.isTraceEnabled()) {
							_log.trace("Returning write data source");
						}

						return _writeDataSource;
					}
				}
			}

			if (_log.isTraceEnabled()) {
				_log.trace("Returning read data source");
			}

			return _readDataSource;
		}

		if ((PropsValues.JDBC_READ_DATA_SOURCE_UNAVAILABLE_TIMEOUT > 0) &&
			!SpringHibernateThreadLocalUtil.isCurrentTransactionReadOnly()) {

			HttpSession httpSession = PortalSessionThreadLocal.getHttpSession();

			if (httpSession == null) {
				if (_log.isTraceEnabled()) {
					_log.trace(
						"No context HTTP session exists, skip setting the " +
							"write data source's last used date");
				}
			}
			else {
				httpSession.setAttribute(
					_WRITE_DATA_SOURCE_LAST_USED_DATE,
					System.currentTimeMillis());
			}
		}

		if (_log.isTraceEnabled()) {
			_log.trace("Returning write data source");
		}

		_writeDynamicDataSource.set(true);

		return _writeDataSource;
	}

	private static final String _WRITE_DATA_SOURCE_LAST_USED_DATE =
		"WRITE_DATA_SOURCE_LAST_USED_DATE";

	private static final Log _log = LogFactoryUtil.getLog(
		DynamicDataSource.class);

	private static final ThreadLocal<Boolean> _writeDynamicDataSource =
		new CentralizedThreadLocal<>(
			DynamicDataSource.class + "._writeDynamicDataSource", () -> false);

	private final DataSource _readDataSource;
	private final DataSource _writeDataSource;

}