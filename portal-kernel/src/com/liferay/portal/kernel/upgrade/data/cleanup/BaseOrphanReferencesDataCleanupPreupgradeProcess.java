/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.data.cleanup;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

/**
 * @author Luis Ortiz
 */
public class BaseOrphanReferencesDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	public BaseOrphanReferencesDataCleanupPreupgradeProcess(
		String columnName, String tableName) {

		_columnName = columnName;
		_tableName = tableName;
	}

	@Override
	protected void doUpgrade() throws Exception {
		DBInspector dbInspector = new DBInspector(connection);

		String tableName = dbInspector.normalizeName(_tableName);

		List<String> tableNames = dbInspector.getTableNames(null);

		tableNames.remove(tableName);

		for (String currentTableName : tableNames) {
			if (!dbInspector.hasColumn(currentTableName, _columnName)) {
				continue;
			}

			String columnName = dbInspector.normalizeName(_columnName);

			try (PreparedStatement preparedStatement1 =
					connection.prepareStatement(
						StringBundler.concat(
							"select ", columnName, ", count(1) from ",
							currentTableName,
							_getWhereClause(
								columnName, tableName, currentTableName),
							" group by ", columnName));
				PreparedStatement preparedStatement2 =
					connection.prepareStatement(
						StringBundler.concat(
							"delete from ", currentTableName,
							_getWhereClause(
								columnName, tableName, currentTableName)));
				ResultSet resultSet = preparedStatement1.executeQuery()) {

				preparedStatement2.execute();

				while (resultSet.next()) {
					if (_log.isInfoEnabled()) {
						long columnValue = resultSet.getLong(1);
						long columnCount = resultSet.getLong(2);

						_log.info(
							StringBundler.concat(
								String.valueOf(columnCount),
								" orphan entries from table ", currentTableName,
								" have been deleted because value ",
								String.valueOf(columnValue),
								" cannot be found in the origin table ",
								tableName, " column ", columnName));
					}
				}
			}
		}
	}

	private String _getWhereClause(
		String columnName, String sourceTableName, String targetTableName) {

		return StringBundler.concat(
			" where not exists (select 1 from ", sourceTableName, " where ",
			sourceTableName, StringPool.PERIOD, columnName, " = ",
			targetTableName, StringPool.PERIOD, columnName, ") and ",
			columnName, " is not null and ", columnName, " != 0");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseOrphanReferencesDataCleanupPreupgradeProcess.class);

	private final String _columnName;
	private final String _tableName;

}