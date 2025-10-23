/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.data.cleanup.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.PropsValues;

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

		boolean aliasNeeded = false;

		DB db = DBManagerUtil.getDB();

		DBType dbType = db.getDBType();

		if ((dbType == DBType.MYSQL) || (dbType == DBType.SQLSERVER) ||
			(dbType == DBType.MARIADB)) {

			aliasNeeded = true;
		}

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select ", _SOURCE_TABLE_ALIAS, StringPool.PERIOD,
					sourceColumnName, ", count(1) from ", sourceTableName,
					StringPool.SPACE, _SOURCE_TABLE_ALIAS,
					getWhereClause(
						connection, sourceAdditionalWhereClause,
						sourceColumnName, sourceTableName, targetColumnNames,
						targetTableName),
					" group by ", _SOURCE_TABLE_ALIAS, StringPool.PERIOD,
					sourceColumnName));
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				StringBundler.concat(
					"delete ",
					aliasNeeded ? (_SOURCE_TABLE_ALIAS + StringPool.SPACE) : "",
					"from ", sourceTableName, StringPool.SPACE,
					_SOURCE_TABLE_ALIAS,
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

	public static String getSourceTableAlias() {
		return _SOURCE_TABLE_ALIAS;
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
			additionalNullCheck = StringBundler.concat(
				" and ", _SOURCE_TABLE_ALIAS, StringPool.PERIOD,
				sourceColumnName, " != 0");
		}
		else if (db.getDBType() != DBType.ORACLE) {
			additionalNullCheck = StringBundler.concat(
				" and ", _SOURCE_TABLE_ALIAS, StringPool.PERIOD,
				sourceColumnName, " != ''");
		}

		String whereClause = null;

		if (db.getDBType() == DBType.MYSQL) {
			whereClause = _getMySQLWhereClause(
				dbInspector, sourceColumnName, sourceTableName,
				targetColumnNames, targetTableName);
		}
		else {
			whereClause = _getOtherDBsWhereClause(
				dbInspector, sourceColumnName, sourceTableName,
				targetColumnNames, targetTableName);
		}

		sourceAdditionalWhereClause = StringUtil.replace(
			sourceAdditionalWhereClause, "[$SOURCE_TABLE_ALIAS$]",
			_SOURCE_TABLE_ALIAS);

		return StringBundler.concat(
			whereClause, " and ",
			_SOURCE_TABLE_ALIAS + StringPool.PERIOD + sourceColumnName,
			" is not null", additionalNullCheck,
			(sourceAdditionalWhereClause != null) ?
				" and " + sourceAdditionalWhereClause : "");
	}

	private static String _getMySQLWhereClause(
		DBInspector dbInspector, String sourceColumnName,
		String sourceTableName, String[] targetColumnNames,
		String targetTableName) {

		StringBundler sb = new StringBundler(
			(15 * targetColumnNames.length) + 1);

		for (String targetColumnName : targetColumnNames) {
			String aliasTableName =
				targetTableName + StringPool.UNDERLINE + targetColumnName;

			sb.append(" left join ");
			sb.append(targetTableName);
			sb.append(StringPool.SPACE);
			sb.append(aliasTableName);
			sb.append(StringPool.SPACE);
			sb.append(" on ");
			sb.append(aliasTableName);
			sb.append(StringPool.PERIOD);
			sb.append(targetColumnName);
			sb.append(" = ");
			sb.append(_SOURCE_TABLE_ALIAS);
			sb.append(StringPool.PERIOD);
			sb.append(sourceColumnName);

			if (StringUtil.equalsIgnoreCase("Company", targetTableName) &&
				PropsValues.DATABASE_PARTITION_ENABLED &&
				!dbInspector.isControlTable(sourceTableName)) {

				sb.append(" and ");
				sb.append(aliasTableName);
				sb.append(".companyId = ");
				sb.append(CompanyThreadLocal.getCompanyId());
			}
		}

		sb.append(" where ");

		for (String targetColumnName : targetColumnNames) {
			String aliasTableName =
				targetTableName + StringPool.UNDERLINE + targetColumnName;

			sb.append(aliasTableName);

			sb.append(StringPool.PERIOD);
			sb.append(targetColumnName);
			sb.append(" is null");
			sb.append(" and ");
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private static String _getOtherDBsWhereClause(
		DBInspector dbInspector, String sourceColumnName,
		String sourceTableName, String[] targetColumnNames,
		String targetTableName) {

		StringBundler sb = new StringBundler(
			(8 * targetColumnNames.length) + 5);

		sb.append(" where not exists (select 1 from ");
		sb.append(targetTableName);
		sb.append(" where (");

		for (String targetColumnName : targetColumnNames) {
			sb.append(targetTableName);
			sb.append(StringPool.PERIOD);
			sb.append(targetColumnName);
			sb.append(" = ");
			sb.append(_SOURCE_TABLE_ALIAS);
			sb.append(StringPool.PERIOD);
			sb.append(sourceColumnName);
			sb.append(" or ");
		}

		sb.setIndex(sb.index() - 1);
		sb.append(")");

		if (StringUtil.equalsIgnoreCase("Company", targetTableName) &&
			PropsValues.DATABASE_PARTITION_ENABLED &&
			!dbInspector.isControlTable(sourceTableName)) {

			sb.append(" and companyId = ");
			sb.append(CompanyThreadLocal.getCompanyId());
		}

		sb.append(")");

		return sb.toString();
	}

	private static final String _SOURCE_TABLE_ALIAS = "s";

	private static final Log _log = LogFactoryUtil.getLog(
		OrphanReferencesDataCleanupUtil.class);

	private static final List<String> _excludedTableNames = new ArrayList<>(
		Arrays.asList(
			"Audit_AuditEvent", "CyrusUser", "CyrusVirtual", "SystemEvent"));
	private static final List<String> _normalizedExcludedTableNames =
		new ArrayList<>();

}