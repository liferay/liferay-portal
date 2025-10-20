/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

/**
 * @author Amos Fong
 * @author Brian Wing Shun Chan
 */
public abstract class BaseUuidUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		String[] tableNames = getTableNames();

		for (String tableName : tableNames) {
			upgradeUuid(tableName);
		}
	}

	protected String getPrimaryKeyColumnName(String tableName)
		throws Exception {

		String[] primaryKeyColumnNames = getPrimaryKeyColumnNames(
			connection, tableName);

		if (primaryKeyColumnNames.length == 0) {
			throw new Exception("Table " + tableName + " has no primary key");
		}

		if (primaryKeyColumnNames.length > 1) {
			primaryKeyColumnNames = ArrayUtil.filter(
				primaryKeyColumnNames,
				name -> !StringUtil.equalsIgnoreCase(name, "ctCollectionId"));
		}

		if (primaryKeyColumnNames.length > 1) {
			throw new Exception(
				"Table " + tableName + " has too many primary key columns");
		}

		return primaryKeyColumnNames[0];
	}

	protected abstract String[] getTableNames();

	protected void upgradeUuid(String tableName) throws Exception {
		if (!hasTable(tableName)) {
			_log.error("Skip nonexistent table " + tableName);

			return;
		}

		if (_log.isInfoEnabled()) {
			_log.info("Upgrade table " + tableName);
		}

		if (!hasColumn(tableName, "uuid_")) {
			alterTableAddColumn(tableName, "uuid_", "VARCHAR(75) null");
		}

		String primaryKeyColumnName = getPrimaryKeyColumnName(tableName);

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			processConcurrently(
				StringBundler.concat(
					"select distinct ", primaryKeyColumnName, " from ",
					tableName, " where uuid_ is null or uuid_ = ''"),
				StringBundler.concat(
					"update ", tableName, " set uuid_ = ? where ",
					primaryKeyColumnName, " = ?"),
				resultSet -> new Object[] {
					resultSet.getLong(primaryKeyColumnName)
				},
				(values, preparedStatement) -> {
					preparedStatement.setString(1, PortalUUIDUtil.generate());
					preparedStatement.setLong(2, (long)values[0]);

					preparedStatement.addBatch();
				},
				null);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseUuidUpgradeProcess.class);

}