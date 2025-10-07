/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.data.cleanup.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Luis Ortiz
 */
public class OrphanReferencesDataCleanupUtil {

	public static void cleanUpTable(
			Connection connection, String sourceAdditionalWhereClause,
			String sourceColumnName, String sourceTableName,
			String[] targetColumnNames, String targetTableName)
		throws Exception {

		List<String> excludedTableNames = getNormalizedExcludedTableNames(
			connection);

		if (excludedTableNames.contains(sourceTableName)) {
			return;
		}

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select ", sourceColumnName, ", count(1) from ",
					sourceTableName,
					getWhereClause(
						connection, sourceAdditionalWhereClause,
						sourceColumnName, sourceTableName, targetColumnNames,
						targetTableName),
					" group by ", sourceColumnName));
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				StringBundler.concat(
					"delete from ", sourceTableName,
					getWhereClause(
						connection, sourceAdditionalWhereClause,
						sourceColumnName, sourceTableName, targetColumnNames,
						targetTableName)));
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			preparedStatement2.execute();

			if (!_log.isInfoEnabled()) {
				return;
			}

			while (resultSet.next()) {
				DataCleanupLoggingUtil.logDelete(
					_log, resultSet.getLong(2), sourceTableName,
					StringBundler.concat(
						sourceColumnName, StringPool.SPACE,
						resultSet.getObject(1), " was not found in column",
						(targetColumnNames.length > 1) ? "s " : " ",
						String.join(", ", targetColumnNames), " from table ",
						targetTableName));
			}
		}
	}

	public static List<String> getNormalizedExcludedTableNames(
			Connection connection)
		throws Exception {

		if (_normalizedExcludedTableNames.isEmpty()) {
			DBInspector dbInspector = new DBInspector(connection);

			for (String excludedTableName : _excludedTableNames) {
				_normalizedExcludedTableNames.add(
					dbInspector.normalizeName(excludedTableName));
			}
		}

		return _normalizedExcludedTableNames;
	}

	public static String getWhereClause(
			Connection connection, String sourceAdditionalWhereClause,
			String sourceColumnName, String sourceTableName,
			String[] targetColumnNames, String targetTableName)
		throws Exception {

		String additionalNullCheck = "";

		DB db = DBManagerUtil.getDB();
		DBInspector dbInspector = new DBInspector(connection);

		if (dbInspector.isNumeric(sourceTableName, sourceColumnName)) {
			additionalNullCheck = " and " + sourceColumnName + " != 0";
		}
		else if (db.getDBType() != DBType.ORACLE) {
			additionalNullCheck = " and " + sourceColumnName + " != ''";
		}

		StringBundler sb = new StringBundler(
			(8 * targetColumnNames.length) + 4);

		sb.append("not exists (select 1 from ");
		sb.append(targetTableName);
		sb.append(" where ");

		for (String targetColumnName : targetColumnNames) {
			sb.append(targetTableName);
			sb.append(StringPool.PERIOD);
			sb.append(targetColumnName);
			sb.append(" = ");
			sb.append(sourceTableName);
			sb.append(StringPool.PERIOD);
			sb.append(sourceColumnName);
			sb.append(" or ");
		}

		sb.setIndex(sb.index() - 1);

		sb.append(")");

		return StringBundler.concat(
			" where ", sb, " and ", sourceColumnName, " is not null",
			additionalNullCheck,
			(sourceAdditionalWhereClause != null) ?
				" and " + sourceAdditionalWhereClause : "");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OrphanReferencesDataCleanupUtil.class);

	private static final List<String> _excludedTableNames = new ArrayList<>(
		Arrays.asList(
			"Audit_AuditEvent", "CyrusUser", "CyrusVirtual", "SystemEvent"));
	private static final List<String> _normalizedExcludedTableNames =
		new ArrayList<>();

}