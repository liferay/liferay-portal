/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.data.cleanup.util;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.RetryAcceptor;
import com.liferay.portal.kernel.service.SQLStateAcceptor;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Luis Ortiz
 */
public class OrphanReferencesDataCleanupUtil {

	public static List<SafeCloseable> addTemporaryIndexes(
			String[] columnNames, Connection connection, DB db,
			String tableName)
		throws Exception {

		Set<String> firstIndexColumnNames = _getFirstIndexColumnNames(
			connection, db, tableName);

		List<SafeCloseable> safeCloseables = new ArrayList<>();

		if (firstIndexColumnNames != null) {
			for (String columnName : columnNames) {
				if (firstIndexColumnNames.contains(
						StringUtil.toLowerCase(columnName))) {

					continue;
				}

				safeCloseables.add(
					db.addTemporaryIndex(
						connection, tableName, false, columnName));
			}
		}

		return safeCloseables;
	}

	public static void cleanUpTable(
			Connection connection, String[] customJoinClauses, boolean readOnly,
			String sourceAdditionalWhereClause, String sourceColumnName,
			String sourceTableName, String[] targetColumnNames,
			String targetTableName)
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

		List<SafeCloseable> safeCloseables = addTemporaryIndexes(
			targetColumnNames, connection, db, targetTableName);

		String whereClause = getWhereClause(
			connection, customJoinClauses, sourceAdditionalWhereClause,
			sourceColumnName, sourceTableName, targetColumnNames,
			targetTableName);

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select ", _SOURCE_TABLE_ALIAS, StringPool.PERIOD,
					sourceColumnName, ", count(1) as count from ",
					sourceTableName, StringPool.SPACE, _SOURCE_TABLE_ALIAS,
					whereClause, " group by ", _SOURCE_TABLE_ALIAS,
					StringPool.PERIOD, sourceColumnName));

			ResultSet resultSet = preparedStatement1.executeQuery()) {

			if (!readOnly) {
				_executeDelete(
					connection,
					StringBundler.concat(
						"delete ",
						aliasNeeded ? (_SOURCE_TABLE_ALIAS + StringPool.SPACE) :
							"",
						"from ", sourceTableName, StringPool.SPACE,
						_SOURCE_TABLE_ALIAS, whereClause));
			}

			if (!_log.isInfoEnabled()) {
				return;
			}

			while (resultSet.next()) {
				DataCleanupLoggingUtil.logDelete(
					_log, resultSet.getLong("count"), readOnly, sourceTableName,
					StringBundler.concat(
						sourceColumnName, StringPool.SPACE,
						resultSet.getObject(sourceColumnName),
						" was not found in column",
						(targetColumnNames.length > 1) ? "s " : " ",
						String.join(", ", targetColumnNames), " from table ",
						targetTableName));
			}
		}
		finally {
			for (SafeCloseable safeCloseable : safeCloseables) {
				safeCloseable.close();
			}
		}
	}

	public static void cleanUpTable(
			Connection connection, String[] customJoinClauses,
			String sourceAdditionalWhereClause, String sourceColumnName,
			String sourceTableName, String[] targetColumnNames,
			String targetTableName)
		throws Exception {

		cleanUpTable(
			connection, customJoinClauses, false, sourceAdditionalWhereClause,
			sourceColumnName, sourceTableName, targetColumnNames,
			targetTableName);
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
			Connection connection, String[] customJoinClauses,
			String sourceAdditionalWhereClause, String sourceColumnName,
			String sourceTableName, String[] targetColumnNames,
			String targetTableName)
		throws Exception {

		String whereClause = null;

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

		String additionalWhereClause = StringBundler.concat(
			_SOURCE_TABLE_ALIAS + StringPool.PERIOD + sourceColumnName,
			" is not null", additionalNullCheck,
			(sourceAdditionalWhereClause != null) ?
				" and " + sourceAdditionalWhereClause : "");

		if ((db.getDBType() == DBType.MARIADB) ||
			(db.getDBType() == DBType.MYSQL)) {

			whereClause = _getMySQLWhereClause(
				additionalWhereClause, customJoinClauses, dbInspector,
				sourceColumnName, sourceTableName, targetColumnNames,
				targetTableName);
		}
		else {
			whereClause = _getOtherDBsWhereClause(
				additionalWhereClause, customJoinClauses, dbInspector,
				sourceColumnName, sourceTableName, targetColumnNames,
				targetTableName);
		}

		return StringUtil.replace(
			whereClause, "[$SOURCE_TABLE_ALIAS$]", _SOURCE_TABLE_ALIAS);
	}

	private static void _executeDelete(Connection connection, String deleteSQL)
		throws Exception {

		int retryCount = 0;

		while (true) {
			try (PreparedStatement preparedStatement =
					connection.prepareStatement(deleteSQL)) {

				preparedStatement.executeUpdate();

				return;
			}
			catch (SQLException sqlException) {
				if ((retryCount >= _DELETE_DEADLOCK_MAX_RETRIES) ||
					!_retryAcceptor.acceptException(
						sqlException,
						Collections.singletonMap(
							SQLStateAcceptor.SQLSTATE,
							SQLStateAcceptor.SQLSTATE_TRANSACTION_ROLLBACK))) {

					throw sqlException;
				}

				retryCount++;

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Retrying delete after a database deadlock: " +
							deleteSQL,
						sqlException);
				}
				else if (_log.isInfoEnabled()) {
					_log.info(
						"Retrying delete after a database deadlock: " +
							deleteSQL);
				}

				try {
					Thread.sleep(_DELETE_DEADLOCK_RETRY_WAIT * retryCount);
				}
				catch (InterruptedException interruptedException) {
					Thread currentThread = Thread.currentThread();

					currentThread.interrupt();

					throw interruptedException;
				}
			}
		}
	}

	private static Set<String> _getFirstIndexColumnNames(
			Connection connection, DB db, String tableName)
		throws Exception {

		DBInspector dbInspector = new DBInspector(connection);

		if (!dbInspector.hasTable(tableName)) {
			return null;
		}

		Set<String> firstIndexColumnNames = new HashSet<>();

		try (ResultSet resultSet = db.getIndexResultSet(
				connection, tableName, false)) {

			while (resultSet.next()) {
				String indexName = resultSet.getString("INDEX_NAME");

				if ((indexName == null) ||
					(resultSet.getShort("ORDINAL_POSITION") != 1)) {

					continue;
				}

				String columnName = resultSet.getString("COLUMN_NAME");

				if (columnName == null) {
					continue;
				}

				firstIndexColumnNames.add(StringUtil.lowerCase(columnName));
			}
		}

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		try (ResultSet resultSet = databaseMetaData.getPrimaryKeys(
				dbInspector.getCatalog(), dbInspector.getSchema(),
				dbInspector.normalizeName(tableName, databaseMetaData))) {

			while (resultSet.next()) {
				if (resultSet.getShort("KEY_SEQ") != 1) {
					continue;
				}

				String columnName = resultSet.getString("COLUMN_NAME");

				firstIndexColumnNames.add(StringUtil.toLowerCase(columnName));

				break;
			}
		}

		return firstIndexColumnNames;
	}

	private static String _getMySQLWhereClause(
		String additionalWhereClause, String[] customJoinClauses,
		DBInspector dbInspector, String sourceColumnName,
		String sourceTableName, String[] targetColumnNames,
		String targetTableName) {

		int index = 0;
		StringBundler sb = new StringBundler(
			(17 * targetColumnNames.length) + 3);

		for (String targetColumnName : targetColumnNames) {
			String aliasTableName =
				targetTableName + StringPool.UNDERLINE + targetColumnName;

			sb.append(" left join ");
			sb.append(targetTableName);
			sb.append(StringPool.SPACE);
			sb.append(aliasTableName);
			sb.append(" on ");

			if ((customJoinClauses == null) ||
				(customJoinClauses[index] == null) ||
				!customJoinClauses[index].startsWith(
					"[$TARGET_TABLE_ALIAS$]")) {

				sb.append(aliasTableName);
				sb.append(StringPool.PERIOD);
				sb.append(targetColumnName);
				sb.append(" = ");
			}

			if ((customJoinClauses != null) &&
				(customJoinClauses[index] != null)) {

				sb.append(
					StringUtil.replace(
						customJoinClauses[index], "[$TARGET_TABLE_ALIAS$]",
						aliasTableName));
			}
			else {
				sb.append(_SOURCE_TABLE_ALIAS);
				sb.append(StringPool.PERIOD);
				sb.append(sourceColumnName);
			}

			if (StringUtil.equalsIgnoreCase("Company", targetTableName) &&
				PropsValues.DATABASE_PARTITION_ENABLED &&
				!dbInspector.isControlTable(sourceTableName)) {

				sb.append(" and ");
				sb.append(aliasTableName);
				sb.append(".companyId = ");
				sb.append(CompanyThreadLocal.getCompanyId());
			}

			index++;
		}

		sb.append(" where ");
		sb.append(additionalWhereClause);
		sb.append(" and ");

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
		String additionalWhereClause, String[] customJoinClauses,
		DBInspector dbInspector, String sourceColumnName,
		String sourceTableName, String[] targetColumnNames,
		String targetTableName) {

		StringBundler sb = new StringBundler(
			(8 * targetColumnNames.length) + 10);

		sb.append(" where ");
		sb.append(additionalWhereClause);
		sb.append(" and not exists (select 1 from ");
		sb.append(targetTableName);
		sb.append(" where (");

		for (int i = 0; i < targetColumnNames.length; i++) {
			if ((customJoinClauses == null) || (customJoinClauses[i] == null) ||
				(!customJoinClauses[i].startsWith("CAST") &&
				 !customJoinClauses[i].startsWith("CONVERT") &&
				 !customJoinClauses[i].startsWith("[$TARGET_TABLE_ALIAS$]"))) {

				sb.append(targetTableName);
				sb.append(StringPool.PERIOD);
				sb.append(targetColumnNames[i]);
				sb.append(" = ");
			}

			if ((customJoinClauses != null) && (customJoinClauses[i] != null)) {
				sb.append(
					StringUtil.replace(
						customJoinClauses[i], "[$TARGET_TABLE_ALIAS$]",
						targetTableName));
			}
			else {
				sb.append(_SOURCE_TABLE_ALIAS);
				sb.append(StringPool.PERIOD);
				sb.append(sourceColumnName);
			}

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

	private static final int _DELETE_DEADLOCK_MAX_RETRIES = 3;

	private static final int _DELETE_DEADLOCK_RETRY_WAIT = 500;

	private static final String _SOURCE_TABLE_ALIAS = "s";

	private static final Log _log = LogFactoryUtil.getLog(
		OrphanReferencesDataCleanupUtil.class);

	private static final List<String> _excludedTableNames = new ArrayList<>(
		Arrays.asList("Audit_AuditEvent", "SystemEvent"));
	private static final List<String> _normalizedExcludedTableNames =
		new ArrayList<>();
	private static final RetryAcceptor _retryAcceptor = new SQLStateAcceptor();

}