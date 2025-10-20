/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Amos Fong
 * @author Brian Wing Shun Chan
 */
public abstract class BaseExternalReferenceCodeUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		String[] tableNames = getTableNames();

		for (String tableName : tableNames) {
			upgradeExternalReferenceCode(tableName);
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

	protected boolean isUseUUID(String tableName) throws Exception {
		return hasColumn(tableName, "uuid_");
	}

	protected void upgradeExternalReferenceCode(String tableName)
		throws Exception {

		if (!hasTable(tableName)) {
			_log.error("Skip nonexistent table " + tableName);

			return;
		}

		if (_log.isInfoEnabled()) {
			_log.info("Upgrade table " + tableName);
		}

		if (!hasColumn(tableName, "externalReferenceCode")) {
			alterTableAddColumn(
				tableName, "externalReferenceCode", "VARCHAR(75)");
		}

		String primaryKeyColumnName = getPrimaryKeyColumnName(tableName);

		boolean useUUID = isUseUUID(tableName);

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			processConcurrently(
				StringBundler.concat(
					"select distinct ", useUUID ? "uuid_, " : StringPool.BLANK,
					primaryKeyColumnName, " from ", tableName,
					" where externalReferenceCode is null or ",
					"externalReferenceCode = ''"),
				StringBundler.concat(
					"update ", tableName,
					" set externalReferenceCode = ? where ",
					primaryKeyColumnName, " = ?"),
				resultSet -> new Object[] {
					useUUID ? resultSet.getString("uuid_") : null,
					resultSet.getLong(primaryKeyColumnName)
				},
				(values, preparedStatement) -> {
					if (useUUID) {
						preparedStatement.setString(1, (String)values[0]);
					}
					else {
						preparedStatement.setString(
							1, String.valueOf(values[1]));
					}

					preparedStatement.setLong(2, (long)values[1]);

					preparedStatement.addBatch();
				},
				null);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseExternalReferenceCodeUpgradeProcess.class);

}