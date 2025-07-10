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

/**
 * @author Luis Ortiz
 */
public class BaseOrphanReferencesDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	public BaseOrphanReferencesDataCleanupPreupgradeProcess(
		String sourceAdditionalWhereClause, String sourceColumnName,
		String sourceTableName, String targetColumnName,
		String targetTableName) {

		_sourceAdditionalWhereClause = sourceAdditionalWhereClause;
		_sourceColumnName = sourceColumnName;
		_sourceTableName = sourceTableName;
		_targetColumnName = targetColumnName;
		_targetTableName = targetTableName;
	}

	@Override
	protected void doUpgrade() throws Exception {
		DBInspector dbInspector = new DBInspector(connection);

		String sourceTableName = dbInspector.normalizeName(_sourceTableName);

		if (!dbInspector.hasTable(sourceTableName)) {
			if (_log.isDebugEnabled()) {
				_log.debug("Table " + sourceTableName + " does not exist");
			}

			return;
		}

		String sourceColumnName = dbInspector.normalizeName(_sourceColumnName);

		if (!dbInspector.hasColumn(sourceTableName, sourceColumnName)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Table ", sourceTableName, " does not have column ",
						sourceColumnName));
			}

			return;
		}

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

		OrphanReferencesDataCleanupUtil.cleanUpTable(
			connection, _sourceAdditionalWhereClause, sourceColumnName,
			sourceTableName, targetColumnName, targetTableName);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseOrphanReferencesDataCleanupPreupgradeProcess.class);

	private final String _sourceAdditionalWhereClause;
	private final String _sourceColumnName;
	private final String _sourceTableName;
	private final String _targetColumnName;
	private final String _targetTableName;

}