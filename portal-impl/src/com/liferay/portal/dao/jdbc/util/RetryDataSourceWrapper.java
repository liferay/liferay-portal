/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.jdbc.util;

import com.liferay.portal.kernel.util.PropsValues;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * @author Matthew Tambara
 */
public class RetryDataSourceWrapper extends DataSourceWrapper {

	public RetryDataSourceWrapper(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public Connection getConnection() throws SQLException {
		int retries = PropsValues.RETRY_DATA_SOURCE_MAX_RETRIES;

		SQLException sqlException1 = null;

		while (retries-- >= 0) {
			try {
				return super.getConnection();
			}
			catch (SQLException sqlException2) {
				if (sqlException1 == null) {
					sqlException1 = sqlException2;
				}
			}
		}

		throw sqlException1;
	}

	@Override
	public Connection getConnection(String userName, String password)
		throws SQLException {

		int retries = PropsValues.RETRY_DATA_SOURCE_MAX_RETRIES;

		SQLException sqlException1 = null;

		while (retries-- >= 0) {
			try {
				return super.getConnection(userName, password);
			}
			catch (SQLException sqlException2) {
				if (sqlException1 == null) {
					sqlException1 = sqlException2;
				}
			}
		}

		throw sqlException1;
	}

}