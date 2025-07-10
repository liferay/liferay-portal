/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.data.cleanup.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Luis Ortiz
 */
public class OrphanReferencesDataCleanupUtil {

	public static void cleanUpTable(
			Connection connection, String sourceAdditionalWhereClause,
			String sourceColumnName, String sourceTableName,
			String targetColumnName, String targetTableName)
		throws Exception {

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select ", sourceColumnName, ", count(1) from ",
					sourceTableName,
					_getWhereClause(
						sourceAdditionalWhereClause, sourceColumnName,
						sourceTableName, targetColumnName, targetTableName),
					" group by ", sourceColumnName));
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				StringBundler.concat(
					"delete from ", sourceTableName,
					_getWhereClause(
						sourceAdditionalWhereClause, sourceColumnName,
						sourceTableName, targetColumnName, targetTableName)));
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			preparedStatement2.execute();

			if (!_log.isInfoEnabled()) {
				return;
			}

			while (resultSet.next()) {
				_log.info(
					StringBundler.concat(
						String.valueOf(resultSet.getLong(2)),
						" orphan entries from table ", sourceTableName,
						" have been deleted because value ",
						String.valueOf(resultSet.getLong(1)),
						" was not found in the origin table ", targetTableName,
						" column ", targetColumnName));
			}
		}
	}

	private static String _getWhereClause(
		String sourceAdditionalWhereClause, String sourceColumnName,
		String sourceTableName, String targetColumnName,
		String targetTableName) {

		return StringBundler.concat(
			" where not exists (select 1 from ", targetTableName, " where ",
			targetTableName, StringPool.PERIOD, targetColumnName, " = ",
			sourceTableName, StringPool.PERIOD, sourceColumnName, ") and ",
			sourceColumnName, " is not null and ", sourceColumnName, " != 0",
			(sourceAdditionalWhereClause != null) ?
				" and " + sourceAdditionalWhereClause : "");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OrphanReferencesDataCleanupUtil.class);

}