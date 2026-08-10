/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Mariano Álvaro Sáiz
 */
public class UpgradeDB2 extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (DBManagerUtil.getDBType() != DBType.DB2) {
			return;
		}

		_alterColumnTypes("blob(2G)", "BLOB");
		_alterColumnTypes("clob(2G)", "CLOB");
	}

	private void _alterColumnTypes(String newColumnType, String typeName)
		throws Exception {

		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select columns.tabname, columns.colname from ",
					"syscat.columns columns inner join syscat.tables tables ",
					"on tables.tabschema = columns.tabschema and ",
					"tables.tabname = columns.tabname where tables.type = 'T' ",
					"and columns.length = 1048576 and columns.typename = ? ",
					"and columns.tabschema = ?"))) {

			preparedStatement.setString(1, typeName);
			preparedStatement.setString(2, connection.getSchema());

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String tableName = resultSet.getString("tabname");
					String columnName = resultSet.getString("colname");

					if (_log.isInfoEnabled()) {
						_log.info(
							StringBundler.concat(
								"Alter column ", tableName, StringPool.PERIOD,
								columnName, " type to ", newColumnType));
					}

					runSQL(
						StringBundler.concat(
							"alter table ", tableName, " alter column ",
							columnName, " set data type ", newColumnType));
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(UpgradeDB2.class);

}