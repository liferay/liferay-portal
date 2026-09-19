/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.xml.Element;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/**
 * @author Shuyang Zhou
 */
public class MVCCVersionUpgradeProcess extends UpgradeProcess {

	public void upgradeMVCCVersion(
			DatabaseMetaData databaseMetaData, String tableName)
		throws Exception {

		DBInspector dbInspector = new DBInspector(connection);

		tableName = dbInspector.normalizeName(tableName, databaseMetaData);

		ensureTableExists(databaseMetaData, dbInspector, tableName);

		try (ResultSet columnResultSet = databaseMetaData.getColumns(
				dbInspector.getCatalog(), dbInspector.getSchema(), tableName,
				dbInspector.normalizeName("mvccVersion", databaseMetaData))) {

			if (columnResultSet.next()) {
				return;
			}

			alterTableAddColumn(
				tableName, "mvccVersion", "LONG default 0 not null");

			if (_log.isDebugEnabled()) {
				_log.debug("Added column mvccVersion to table " + tableName);
			}
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		upgradeModuleTableMVCCVersions();
	}

	protected String[] getTableNames() {
		return new String[] {"BackgroundTask", "Lock_"};
	}

	protected void upgradeMVCCVersion(
			DatabaseMetaData databaseMetaData, Element classElement)
		throws Exception {

		String tableName = classElement.attributeValue("table");

		upgradeMVCCVersion(databaseMetaData, tableName);
	}

	protected void upgradeModuleTableMVCCVersions() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			DatabaseMetaData databaseMetaData = connection.getMetaData();

			String[] tableNames = getTableNames();

			for (String tableName : tableNames) {
				upgradeMVCCVersion(databaseMetaData, tableName);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MVCCVersionUpgradeProcess.class);

}