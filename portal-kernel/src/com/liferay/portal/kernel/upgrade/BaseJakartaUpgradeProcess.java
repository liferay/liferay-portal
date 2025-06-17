/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.util.JakartaUpgradeProcessUtil;
import com.liferay.portal.kernel.util.StringBundler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Luis Ortiz
 */
public abstract class BaseJakartaUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		for (String[] tableAndColumnNames : getTableAndColumnNames()) {
			Queue<String> modifiedKeys = new ConcurrentLinkedQueue<>();

			DBInspector dbInspector = new DBInspector(connection);

			String columnName = dbInspector.normalizeName(
				tableAndColumnNames[1]);

			String tableName = dbInspector.normalizeName(
				tableAndColumnNames[0]);

			String[] primaryKeyColumnNames = getPrimaryKeyColumnNames(
				connection, tableName);

			processConcurrently(
				_getSelectSQL(columnName, primaryKeyColumnNames, tableName),
				_getUpdateSQL(columnName, primaryKeyColumnNames, tableName),
				resultSet -> _getValues(
					columnName, primaryKeyColumnNames, resultSet),
				(values, preparedStatement) -> {
					String modifiedKey = _updateValue(
						getCustomSeparators(), preparedStatement,
						primaryKeyColumnNames, values);

					if (modifiedKey == null) {
						return;
					}

					modifiedKeys.add(modifiedKey);
				},
				_getExceptionMessage(columnName, tableName));

			if (!_log.isInfoEnabled()) {
				continue;
			}

			int size =
				(modifiedKeys.size() * 2) + (primaryKeyColumnNames.length * 2);

			StringBundler sb = new StringBundler(
				DBPartition.isPartitionEnabled() ? size + 8 : size + 6);

			sb.append("Table ");
			sb.append(tableName);
			sb.append(" column ");
			sb.append(columnName);

			if (DBPartition.isPartitionEnabled()) {
				sb.append(" for company ");
				sb.append(CompanyThreadLocal.getCompanyId());
			}

			if (modifiedKeys.isEmpty()) {
				sb.append(" was not updated");
			}
			else {
				sb.append(" was updated for records with primary keys (");

				for (String primaryKeyColumnName : primaryKeyColumnNames) {
					sb.append(primaryKeyColumnName);
					sb.append(", ");
				}

				sb.setIndex(sb.index() - 1);

				sb.append("): ");

				for (String key : modifiedKeys) {
					sb.append(key);
					sb.append(", ");
				}

				sb.setIndex(sb.index() - 1);
			}

			_log.info(sb.toString());
		}
	}

	protected char[] getCustomSeparators() {
		return new char[0];
	}

	protected abstract String[][] getTableAndColumnNames();

	private String _getExceptionMessage(String columnName, String tableName) {
		StringBundler sb = new StringBundler(
			DBPartition.isPartitionEnabled() ? 6 : 4);

		sb.append("Unable to update Javax references in table ");
		sb.append(tableName);
		sb.append(" column ");
		sb.append(columnName);

		if (DBPartition.isPartitionEnabled()) {
			sb.append(" for company ");
			sb.append(CompanyThreadLocal.getCompanyId());
		}

		return sb.toString();
	}

	private String _getSelectSQL(
		String columnName, String[] primaryKeyColumnNames, String tableName) {

		StringBundler sb = new StringBundler(
			(primaryKeyColumnNames.length * 2) + 7);

		sb.append("select ");

		for (String primaryKeyColumnName : primaryKeyColumnNames) {
			sb.append(primaryKeyColumnName);
			sb.append(", ");
		}

		sb.append(columnName);
		sb.append(" from ");
		sb.append(tableName);
		sb.append(" where ");
		sb.append(columnName);
		sb.append(" is not null");

		return sb.toString();
	}

	private String _getUpdateSQL(
		String columnName, String[] primaryKeyColumnNames, String tableName) {

		StringBundler sb = new StringBundler(
			(primaryKeyColumnNames.length * 3) + 5);

		sb.append("update ");
		sb.append(tableName);
		sb.append(" set ");
		sb.append(columnName);
		sb.append(" = ? where ");

		for (String primaryKeyColumnName : primaryKeyColumnNames) {
			sb.append(primaryKeyColumnName);
			sb.append(" = ?");
			sb.append(" and ");
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private Object[] _getValues(
			String columnName, String[] primaryKeyColumnNames,
			ResultSet resultSet)
		throws SQLException {

		Object[] values = new Object[primaryKeyColumnNames.length + 1];

		int i = 0;

		for (String primaryKeyColumnName : primaryKeyColumnNames) {
			values[i] = resultSet.getObject(primaryKeyColumnName);
			i++;
		}

		values[i] = resultSet.getString(columnName);

		return values;
	}

	private String _updateValue(
			char[] customSeparators, PreparedStatement preparedStatement,
			String[] primaryKeyColumnNames, Object[] values)
		throws SQLException {

		String javaxValue = (String)values[values.length - 1];

		if (javaxValue == null) {
			return null;
		}

		String jakartaValue = null;

		if (customSeparators.length > 0) {
			jakartaValue = JakartaUpgradeProcessUtil.replace(
				javaxValue, customSeparators);
		}
		else {
			jakartaValue = JakartaUpgradeProcessUtil.replace(javaxValue);
		}

		if (jakartaValue.length() != javaxValue.length()) {
			preparedStatement.setString(1, jakartaValue);

			for (int i = 0; i < primaryKeyColumnNames.length; i++) {
				preparedStatement.setObject(i + 2, values[i]);
			}

			preparedStatement.addBatch();

			StringBundler sb = new StringBundler(
				(primaryKeyColumnNames.length * 2) + 1);

			sb.append("(");

			for (int i = 0; i < primaryKeyColumnNames.length; i++) {
				sb.append(values[i]);
				sb.append(", ");
			}

			sb.setIndex(sb.index() - 1);
			sb.append(")");

			return sb.toString();
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseJakartaUpgradeProcess.class);

}