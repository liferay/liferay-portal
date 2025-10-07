/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.data.cleanup;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.OrphanReferencesDataCleanupUtil;

import java.util.Collections;
import java.util.List;

/**
 * @author Luis Ortiz
 */
public abstract class BaseAllTablesOrphanReferencesDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	public BaseAllTablesOrphanReferencesDataCleanupPreupgradeProcess(
		String targetColumnName, String targetTableName) {

		_targetTableName = targetTableName;

		_sourceColumnName = targetColumnName;
		_targetColumnNames = new String[] {targetColumnName};
	}

	public BaseAllTablesOrphanReferencesDataCleanupPreupgradeProcess(
		String sourceColumnName, String[] targetColumnNames,
		String targetTableName) {

		_sourceColumnName = sourceColumnName;
		_targetColumnNames = targetColumnNames;
		_targetTableName = targetTableName;
	}

	protected abstract void cleanUp(
			String sourceColumnName, String sourceTableName,
			String[] targetColumnNames, String targetTableName)
		throws Exception;

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

		String[] targetColumnNames = new String[_targetColumnNames.length];

		for (int i = 0; i < _targetColumnNames.length; i++) {
			targetColumnNames[i] = dbInspector.normalizeName(
				_targetColumnNames[i]);

			if (!dbInspector.hasColumn(targetTableName, targetColumnNames[i])) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Table ", targetTableName, " does not have column ",
							targetColumnNames[i]));
				}

				return;
			}
		}

		List<String> excludedTableNames =
			OrphanReferencesDataCleanupUtil.getNormalizedExcludedTableNames(
				connection);

		List<String> tableNames = dbInspector.getTableNames(null);

		tableNames.remove(targetTableName);

		Collections.sort(tableNames);

		String sourceColumnName = dbInspector.normalizeName(_sourceColumnName);

		for (String sourceTableName : tableNames) {
			if (excludedTableNames.contains(sourceTableName) ||
				!dbInspector.hasColumn(sourceTableName, sourceColumnName)) {

				continue;
			}

			boolean compatibleTypes = true;

			boolean numericSourceColumn = dbInspector.isNumeric(
				sourceTableName, sourceColumnName);

			for (String targetColumnName : targetColumnNames) {
				boolean numericTargetColumn = dbInspector.isNumeric(
					targetTableName, targetColumnName);

				if (numericSourceColumn != numericTargetColumn) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Table ", sourceTableName, " and column ",
								sourceColumnName,
								" has an incompatible type with table ",
								targetTableName, " and column ",
								targetColumnName));
					}

					compatibleTypes = false;

					break;
				}
			}

			if (!compatibleTypes) {
				continue;
			}

			cleanUp(
				sourceColumnName, sourceTableName, targetColumnNames,
				targetTableName);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseAllTablesOrphanReferencesDataCleanupPreupgradeProcess.class);

	private final String _sourceColumnName;
	private final String[] _targetColumnNames;
	private final String _targetTableName;

}