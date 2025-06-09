/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.portal.db.DBResourceUtil;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.upgrade.PortalUpgradeProcess;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Jorge Avalos
 */
public class PreupgradeVerifyDatabaseState extends PreupgradeVerifyProcess {

	@Override
	protected void doVerify() throws Exception {
		_checkDatabaseState();
	}

	private void _checkDatabaseState() throws Exception {
		if (StartupHelperUtil.isDBNew() ||
			PortalUpgradeProcess.isInLatestSchemaVersion(connection)) {

			return;
		}

		Set<String> preupgradedServiceComponentTables =
			DBResourceUtil.getServiceComponentModuleTableNames(connection);

		if (preupgradedServiceComponentTables.isEmpty()) {
			return;
		}

		DBInspector dbInspector = new DBInspector(connection);

		Set<String> databaseTables = new HashSet<>(
			dbInspector.getTableNames(null));

		if (!databaseTables.containsAll(preupgradedServiceComponentTables)) {
			Set<String> missingTables = new HashSet<>(
				preupgradedServiceComponentTables);

			missingTables.removeAll(databaseTables);

			throw new Exception(
				"Missing tables detected:\n" + missingTables +
					"\nPlease fix these tables to continue the upgrade");
		}
	}

}