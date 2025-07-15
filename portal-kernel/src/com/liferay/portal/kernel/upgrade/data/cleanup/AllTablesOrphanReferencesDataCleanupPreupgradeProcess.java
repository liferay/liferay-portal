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

import java.util.List;

/**
 * @author Luis Ortiz
 */
public class AllTablesOrphanReferencesDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	public AllTablesOrphanReferencesDataCleanupPreupgradeProcess(
		String targetColumnName, String targetTableName) {

		_targetColumnName = targetColumnName;
		_targetTableName = targetTableName;
	}

	@Override
	protected void doUpgrade() throws Exception {
		DBInspector dbInspector = new DBInspector(connection);

		String targetTableName = dbInspector.normalizeName(_targetTableName);

		if (!dbInspector.hasTable(targetTableName)) {
			if (_log.isDebugEnabled()) {
				_log.debug("Table " + targetTableName + " does not exist");
			}

			return;
		}

		String targetColumnName = dbInspector.normalizeName(_targetColumnName);

		if (!dbInspector.hasColumn(targetTableName, targetColumnName)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Table ", targetTableName, " does not have column ",
						targetColumnName));
			}

			return;
		}

		List<String> tableNames = dbInspector.getTableNames(null);

		tableNames.remove(targetTableName);

		for (String sourceTableName : tableNames) {
			if (!dbInspector.hasColumn(sourceTableName, targetColumnName)) {
				continue;
			}

			OrphanReferencesDataCleanupUtil.cleanUpTable(
				connection, null, targetColumnName, sourceTableName,
				targetColumnName, targetTableName);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AllTablesOrphanReferencesDataCleanupPreupgradeProcess.class);

	private final String _targetColumnName;
	private final String _targetTableName;

}