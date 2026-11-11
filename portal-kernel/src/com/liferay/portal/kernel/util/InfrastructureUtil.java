/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.concurrent.DefaultNoticeableFuture;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;

import javax.sql.DataSource;

/**
 * @author Brian Wing Shun Chan
 * @author Michael Young
 */
@OSGiBeanProperties(service = InfrastructureUtil.class)
public class InfrastructureUtil {

	public static DataSource getDataSource() {
		return _dataSource;
	}

	public static Object getSessionFactory() {
		try {
			return _sessionFactoryDefaultNoticeableFuture.get();
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	public static Object getTransactionManager() {
		try {
			return _transactionManagerDefaultNoticeableFuture.get();
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	public static boolean isReadWriteDataSource() {
		return _readWriteDataSource;
	}

	public static void setDataSource(DataSource dataSource) {
		_dataSource = dataSource;
	}

	public static void setReadWriteDataSource(boolean readWriteDataSource) {
		_readWriteDataSource = readWriteDataSource;
	}

	public static void setSessionFactory(Object sessionFactory) {
		_sessionFactoryDefaultNoticeableFuture.set(sessionFactory);
	}

	public static void setTransactionManager(Object transactionManager) {
		_transactionManagerDefaultNoticeableFuture.set(transactionManager);
	}

	private static DataSource _dataSource;
	private static boolean _readWriteDataSource;
	private static final DefaultNoticeableFuture<Object>
		_sessionFactoryDefaultNoticeableFuture =
			new DefaultNoticeableFuture<>();
	private static final DefaultNoticeableFuture<Object>
		_transactionManagerDefaultNoticeableFuture =
			new DefaultNoticeableFuture<>();

}