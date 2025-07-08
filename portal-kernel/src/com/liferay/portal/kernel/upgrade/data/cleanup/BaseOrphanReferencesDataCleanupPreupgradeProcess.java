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

import java.util.ArrayList;
import java.util.Arrays;
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

	public BaseOrphanReferencesDataCleanupPreupgradeProcess(
		String columnName, String tableName, List<String> targetColumnNames,
		List<String> targetTableNames,
		List<String> targetAdditionalWhereClauses) {

		if ((targetColumnNames.size() != targetTableNames.size()) ||
			(targetColumnNames.size() != targetAdditionalWhereClauses.size())) {

			throw new IllegalArgumentException(
				"target parameters must have the same size");
		}

		_columnName = columnName;
		_tableName = tableName;

		_targetColumnNames.addAll(targetColumnNames);
		_targetTableNames.addAll(targetTableNames);
		_targetAdditionalWhereClauses.addAll(targetAdditionalWhereClauses);
	}

	@Override
	protected void doUpgrade() throws Exception {
		DBInspector dbInspector = new DBInspector(connection);

		String tableName = dbInspector.normalizeName(_tableName);

		if (!dbInspector.hasTable(tableName)) {
			if (_log.isDebugEnabled()) {
				_log.debug("Table " + tableName + " does not exist");
			}

			return;
		}

		List<String> tableNames = dbInspector.getTableNames(null);

		tableNames.remove(tableName);

		for (String excludedTableName : _excludedTableNames) {
			tableNames.remove(dbInspector.normalizeName(excludedTableName));
		}

		String columnName = dbInspector.normalizeName(_columnName);

		for (String currentTableName : tableNames) {
			if (!dbInspector.hasColumn(currentTableName, columnName)) {
				continue;
			}

			_cleanUpTable(
				columnName, tableName, columnName, currentTableName, null);
		}

		for (int i = 0; i < _targetTableNames.size(); i++) {
			String targetColumnName = dbInspector.normalizeName(
				_targetColumnNames.get(i));

			String targetTableName = dbInspector.normalizeName(
				_targetTableNames.get(i));

			if (!dbInspector.hasTable(targetTableName)) {
				if (_log.isDebugEnabled()) {
					_log.debug("Table " + targetTableName + " does not exist");
				}

				continue;
			}

			if (!dbInspector.hasColumn(targetTableName, targetColumnName)) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Table ", targetTableName, " does not have column ",
							targetColumnName));
				}

				continue;
			}

			String targetAdditionalWhereClause =
				_targetAdditionalWhereClauses.get(i);

			_cleanUpTable(
				columnName, tableName, targetColumnName, targetTableName,
				targetAdditionalWhereClause);
		}
	}

	private void _cleanUpTable(
			String sourceColumnName, String sourceTableName,
			String targetColumnName, String targetTableName,
			String targetAdditionalWhereClause)
		throws Exception {

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select ", targetColumnName, ", count(1) from ",
					targetTableName,
					_getWhereClause(
						sourceColumnName, sourceTableName, targetColumnName,
						targetColumnName, targetAdditionalWhereClause),
					" group by ", targetColumnName));
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				StringBundler.concat(
					"delete from ", targetColumnName,
					_getWhereClause(
						sourceColumnName, sourceTableName, targetColumnName,
						targetTableName, targetAdditionalWhereClause)));
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			preparedStatement2.execute();

			if (!_log.isInfoEnabled()) {
				return;
			}

			while (resultSet.next()) {
				long columnCount = resultSet.getLong(2);
				long columnValue = resultSet.getLong(1);

				_log.info(
					StringBundler.concat(
						String.valueOf(columnCount),
						" orphan entries from table ", targetTableName,
						" have been deleted because value ",
						String.valueOf(columnValue),
						" cannot be found in the origin table ",
						sourceTableName, " column ", sourceColumnName));
			}
		}
	}

	private String _getWhereClause(
		String sourceColumnName, String sourceTableName,
		String targetColumnName, String targetTableName,
		String targetAdditionalWhereClause) {

		return StringBundler.concat(
			" where not exists (select 1 from ", sourceTableName, " where ",
			sourceTableName, StringPool.PERIOD, sourceColumnName, " = ",
			targetTableName, StringPool.PERIOD, targetColumnName, ") and ",
			targetColumnName, " is not null and ", targetColumnName, " != 0",
			(targetAdditionalWhereClause != null) ?
				" and " + targetAdditionalWhereClause : "");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseOrphanReferencesDataCleanupPreupgradeProcess.class);

	private static final List<String> _excludedTableNames = new ArrayList<>(
		Arrays.asList("Audit_AuditEvent"));

	private final String _columnName;
	private final String _tableName;
	private final List<String> _targetAdditionalWhereClauses =
		new ArrayList<>();
	private final List<String> _targetColumnNames = new ArrayList<>();
	private final List<String> _targetTableNames = new ArrayList<>();

}