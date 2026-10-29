/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.test.util;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Adam Brandizzi
 */
public class UpgradeDatabaseTestHelperImpl
	extends UpgradeProcess implements UpgradeDatabaseTestHelper {

	public UpgradeDatabaseTestHelperImpl(Connection connection)
		throws SQLException {

		this.connection = connection;
	}

	@Override
	public void close() throws Exception {
		connection.close();

		connection = null;
	}

	@Override
	public void dropColumn(
			String tableClassName, String tableName, String columnName)
		throws Exception {

		alterTableDropColumn(tableName, columnName);
	}

	@Override
	public boolean hasColumn(String tableName, String columnName)
		throws Exception {

		return super.hasColumn(tableName, columnName);
	}

	@Override
	protected void doUpgrade() throws Exception {
		throw new UnsupportedOperationException();
	}

}