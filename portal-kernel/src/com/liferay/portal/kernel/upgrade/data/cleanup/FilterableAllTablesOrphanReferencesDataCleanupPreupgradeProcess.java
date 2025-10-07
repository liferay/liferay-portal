/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.data.cleanup;

import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.OrphanReferencesDataCleanupUtil;

/**
 * @author Luis Ortiz
 */
public class FilterableAllTablesOrphanReferencesDataCleanupPreupgradeProcess
	extends BaseAllTablesOrphanReferencesDataCleanupPreupgradeProcess {

	public FilterableAllTablesOrphanReferencesDataCleanupPreupgradeProcess(
		String sourceAdditionalWhereClause,
		String[] sourceAdditionalColumnNamesCheck, String sourceColumnName,
		String[] targetColumnNames, String targetTableName) {

		super(sourceColumnName, targetColumnNames, targetTableName);

		_sourceAdditionalWhereClause = sourceAdditionalWhereClause;
		_sourceAdditionalColumnNamesCheck = sourceAdditionalColumnNamesCheck;
	}

	@Override
	protected void cleanUp(
			String sourceColumnName, String sourceTableName,
			String[] targetColumnNames, String targetTableName)
		throws Exception {

		DBInspector dbInspector = new DBInspector(connection);

		for (String sourceAdditionalColumnName :
				_sourceAdditionalColumnNamesCheck) {

			if (!dbInspector.hasColumn(
					sourceTableName, sourceAdditionalColumnName)) {

				return;
			}
		}

		OrphanReferencesDataCleanupUtil.cleanUpTable(
			connection, _sourceAdditionalWhereClause, sourceColumnName,
			sourceTableName, targetColumnNames, targetTableName);
	}

	private final String[] _sourceAdditionalColumnNamesCheck;
	private final String _sourceAdditionalWhereClause;

}