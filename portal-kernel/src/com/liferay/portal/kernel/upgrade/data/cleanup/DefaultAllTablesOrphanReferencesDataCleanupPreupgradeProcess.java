/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.data.cleanup;

import com.liferay.portal.kernel.upgrade.data.cleanup.util.OrphanReferencesDataCleanupUtil;

/**
 * @author Luis Ortiz
 */
public class DefaultAllTablesOrphanReferencesDataCleanupPreupgradeProcess
	extends BaseAllTablesOrphanReferencesDataCleanupPreupgradeProcess {

	public DefaultAllTablesOrphanReferencesDataCleanupPreupgradeProcess(
		String targetColumnName, String targetTableName) {

		super(targetColumnName, targetTableName);
	}

	@Override
	protected void cleanUp(
			String sourceColumnName, String sourceTableName,
			String[] targetColumnNames, String targetTableName)
		throws Exception {

		OrphanReferencesDataCleanupUtil.cleanUpTable(
			connection, null, sourceColumnName, sourceTableName,
			targetColumnNames, targetTableName);
	}

}