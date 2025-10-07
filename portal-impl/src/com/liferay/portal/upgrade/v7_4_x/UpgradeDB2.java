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

	protected void alterClobColumns() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select tabname, colname from syscat.columns where ",
					"generated != 'N' and hidden != 'N' and length = 1048576 ",
					"and typename = 'CLOB' and tabschema = '",
					connection.getSchema(), StringPool.APOSTROPHE));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String tableName = resultSet.getString(1);
				String columnName = resultSet.getString(2);

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Alter column ", tableName, StringPool.PERIOD,
							columnName, " type to clob(2G)"));
				}

				runSQL(
					StringBundler.concat(
						"alter table ", tableName, " alter column ", columnName,
						" set data type clob(2G)"));
			}
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		if (DBManagerUtil.getDBType() != DBType.DB2) {
			return;
		}

		alterClobColumns();
	}

	private static final Log _log = LogFactoryUtil.getLog(UpgradeDB2.class);

}