/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.data.cleanup;

import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.OrphanReferencesDataCleanupUtil;
import com.liferay.portal.kernel.util.StringBundler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Luis Ortiz
 */
public class CrossOrphanReferencesDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	public CrossOrphanReferencesDataCleanupPreupgradeProcess(
		String columnName, String tableName) {

		_columnName = columnName;
		_tableName = tableName;
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

		String columnName = dbInspector.normalizeName(_columnName);

		if (!dbInspector.hasColumn(tableName, columnName)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Table ", tableName, " does not have column ",
						columnName));
			}

			return;
		}

		List<String> tableNames = dbInspector.getTableNames(null);

		tableNames.remove(tableName);

		for (String excludedTableName : _excludedTableNames) {
			tableNames.remove(dbInspector.normalizeName(excludedTableName));
		}

		for (String currentTableName : tableNames) {
			if (!dbInspector.hasColumn(currentTableName, columnName)) {
				continue;
			}

			OrphanReferencesDataCleanupUtil.cleanUpTable(
				connection, null, columnName, currentTableName, columnName,
				tableName);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CrossOrphanReferencesDataCleanupPreupgradeProcess.class);

	private static final List<String> _excludedTableNames = new ArrayList<>(
		Arrays.asList("Audit_AuditEvent"));

	private final String _columnName;
	private final String _tableName;

}